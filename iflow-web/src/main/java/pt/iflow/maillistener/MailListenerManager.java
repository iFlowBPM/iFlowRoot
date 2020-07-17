package pt.iflow.maillistener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowDeployListener;
import pt.iflow.api.flows.FlowHolder;
import pt.iflow.api.flows.FlowSettings;
import pt.iflow.api.flows.FlowSettingsListener;
import pt.iflow.api.flows.FlowVersionListener;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.flows.MailStartSettings;
import pt.iflow.api.flows.NewFlowListener;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.UserInfoManagerInterface;
import pt.iflow.api.utils.mail.MailChecker;
import pt.iflow.api.utils.mail.MailClient;
import pt.iflow.api.utils.mail.MailConfig;
import pt.iflow.api.utils.mail.imap.IMAPMailPlainClient;
import pt.iflow.api.utils.mail.imap.IMAPMailSSLClient;
import pt.iflow.api.utils.mail.parsers.AbstractPropertiesMessageParser;
import pt.iflow.api.utils.mail.parsers.MessageParseException;
import pt.iflow.api.utils.mail.parsers.MessageParser;
import pt.iflow.utils.BuildPdf;

public class MailListenerManager extends Thread {
	
  private static final String sPDF_DOCUMENT_VARIABLE = Setup.getProperty("PDF_DOCUMENT_VARIABLE");

  private static MailListenerManager instance;

  private enum FlowStatus { CHECK_FLOW, ONLINE };

  private Map<Integer, MailChecker> checkers = Collections.synchronizedMap(new HashMap<Integer, MailChecker>());
  
  private UserInfoInterface userInfo;
  
  private MailListenerManager() {
    userInfo = BeanFactory.getUserInfoFactory().newClassManager(this.getClass().getName());
  }

  public static MailListenerManager getInstance() {
    if (instance == null)
      instance = new MailListenerManager();

    return instance;
  }

  public static void startManager() {
    Logger.adminInfo("MailListenerManager", "startManager", "Starting...");
    getInstance().start();
  }

  public static void stopManager() {
    Logger.adminInfo("MailListenerManager", "stopManager", "Stopping...");
    getInstance().shutdown();
  }

  private void prepareAndStartChecker(int flowid) throws Exception {
    prepareChecker(flowid);
    startChecker(flowid, FlowStatus.CHECK_FLOW);
  }
  
  public void run() {
    FlowHolder fholder = BeanFactory.getFlowHolderBean();
    Collection<Integer> flowids = fholder.listFlowIds(userInfo);
    for (int flowid : flowids) {
      try {
        prepareAndStartChecker(flowid);
        Logger.adminInfo("MailListenerManager", "run", "Flow " + flowid + " processed.");
      } 
      catch (Exception e) {
        Logger.adminError("MailListenerManager", "run", "Error preparing flow " + flowid, e);
      }
    }

    // add settings listener
    BeanFactory.getFlowSettingsBean().addFlowSettingsListener(this.getClass().getName(), new FlowSettingsListener() {
      public void settingsChanged(int flowid) {
        Logger.adminTrace("MailListenerManager", "settingsChanged", "entered for flow " + flowid);
        try {
          prepareAndStartChecker(flowid);
          Logger.adminInfo("MailListenerManager", "settingsChanged", 
              "Flow " + flowid + " re-processed.");
        } 
        catch (Exception e) {
          Logger.adminError("MailListenerManager", "settingsChanged", 
              "error preparing checker for flow " + flowid, e);
        }
      }
    });

    fholder.addNewFlowListener(this.getClass().getName(), new NewFlowListener() {
      public void flowAdded(int flowid) {
        Logger.adminTrace("MailListenerManager", "flowAdded", "entered for flow " + flowid);
        
        try {
          prepareAndStartChecker(flowid);
          Logger.adminInfo("MailListenerManager", "flowAdded", 
              "Flow " + flowid + " initialized.");
        } 
        catch (Exception e) {
          Logger.adminError("MailListenerManager", "flowAdded", 
              "error initializing checker for flow " + flowid, e);
        }        
      }
    });

    fholder.addFlowDeployListener(this.getClass().getName(), new FlowDeployListener() {
      public void goOffline(int flowid) {
        Logger.adminTrace("MailListenerManager", "goOffline", "entered for flow " + flowid);
        stopChecker(flowid);
      }

      public void goOnline(int flowid) {
        Logger.adminTrace("MailListenerManager", "goOnline", "entered for flow " + flowid);
        try {
          startChecker(flowid, FlowStatus.ONLINE);
        } catch (Exception e) {
          Logger.adminError("MailListenerManager", "goOnline", 
              "error starting checker for flow " + flowid, e);
        }
      }
    });

    fholder.addFlowVersionListener(this.getClass().getName(), new FlowVersionListener() {
      public void newVersion(int flowid) {
        Logger.adminTrace("MailListenerManager", "newVersion", "entered for flow " + flowid);
        try {
          prepareAndStartChecker(flowid);
          Logger.adminInfo("MailListenerManager", "newVersion", 
              "Flow " + flowid + " re-processed.");
        } 
        catch (Exception e) {
          Logger.adminError("MailListenerManager", "newVersion", 
              "error preparing checker for flow " + flowid, e);
        }        
      }
    });
    
  }

  private void prepareChecker(int flowid) throws Exception {

    stopChecker(flowid);

    FlowSettings settingsbean = BeanFactory.getFlowSettingsBean();
    MailConfig mconfig = MailConfig.parseAndValidate(settingsbean.getFlowSettings(flowid));

    if (mconfig == null) {
      checkers.put(flowid, null);
      Logger.adminInfo("MailListenerManager", "prepareChecker", 
          "no mail configuration for flow " + flowid);
      return;
    }
    MailClient mclient = mconfig.isSecure() ? new IMAPMailSSLClient(mconfig) : new IMAPMailPlainClient(mconfig);
    MailChecker mchecker = new MailChecker(flowid, mconfig.getCheckIntervalInSeconds(), mclient, getMessageParser(flowid));

    checkers.put(flowid, mchecker);
    Logger.adminInfo("MailListenerManager", "prepareChecker", 
        "mail checker prepared for flow " + flowid);

  }

  private MessageParser getMessageParser(final int flowid) {
    MessageParser parser = new AbstractPropertiesMessageParser() {

      public boolean parse(Message message) throws MessageParseException {

        
        Logger.adminTrace("MailListenerManager", "parse", "entered for flow " + flowid);

        ProcessData procData = null;

        ProcessManager pm = null;
        Flow flowBean = null;
        IFlowData flow = null;
        MailStartSettings mailsettings = null;

        try {
          pm = BeanFactory.getProcessManagerBean();
          flowBean = BeanFactory.getFlowBean();
          flow = flowBean.getFlow(userInfo, flowid);
          if (userInfo instanceof UserInfoManagerInterface
              && StringUtils.equals(userInfo.getOrganization(), Const.SYSTEM_ORGANIZATION)) {
            ((UserInfoManagerInterface) userInfo).setOrganizationId(flow.getOrganizationId());
          }
        } 
        catch (Exception e) {
          Logger.adminError("MailListenerManager", "parse", "unable to get beans", e);
          return false;
        }
        try {
          mailsettings = flow.getMailSettings(); 
        } catch (Exception e) {
          Logger.adminError("MailListenerManager", "parse", "unable to get flow mail settings", e);
          return false;
        }
        
        try {
          procData = pm.createProcess(userInfo, flowid);
        } 
        catch (Exception e) {
          Logger.adminError("MailListenerManager", "parse", "error creating process", e);
          return false;
        }

        Logger.adminInfo("MailListenerManager", "parse", "process created for flow " + flowid + "(pid: " + procData.getPid() + ")");

        if (mailsettings != null) {

          String fromEmail = "";
          String fromName = "";
          String subject = "";
          String text = "";
          Date sentDate = null;

          try {
            if (message.getFrom() != null && message.getFrom().length > 0) {
              Address a = message.getFrom()[0];
              if (a instanceof InternetAddress) {
                InternetAddress ia = (InternetAddress) (message.getFrom()[0]);
                fromEmail = ia.getAddress();
                fromName = ia.getPersonal();
              } else {
                fromEmail = a.toString(); // TODO: check...
              }
            }
          } catch (MessagingException e) {
            Logger.adminWarning("MailListenerManager", "parse", "error parsing from", e);
          }

          try {
            subject = message.getSubject();
          } catch (MessagingException e) {
            Logger.adminWarning("MailListenerManager", "parse", "error getting subject", e);
          }
          try {
            sentDate = message.getSentDate();
          } catch (MessagingException e) {
            Logger.adminWarning("MailListenerManager", "parse", "error getting sent date", e);
          }
          try {
        	  if (message.isMimeType("multipart/*")) {
        		ArrayList<String> auxAlreadyAdded = new ArrayList<String>();
		        Multipart mp = (Multipart)message.getContent();
		        for (int i = 0; i < mp.getCount(); i++) {
		          Part bp = mp.getBodyPart(i);
	
		          String disposition = bp.getDisposition();
		          if (!StringUtils.equalsIgnoreCase(disposition, Part.ATTACHMENT)) {
		        	  String tmpText = getText(message);		        	  
		        	  if(!auxAlreadyAdded.contains(tmpText)){
		        		  text += getText(message);
		        		  auxAlreadyAdded.add(tmpText);
		        	  }
		          }
		        }
		      }
		      else {
		        text = getText(message);
		      }        	      
          } catch (Exception e) {
            Logger.adminWarning("MailListenerManager", "parse", "error getting text", e);
          }

          Properties props = parseProperties(message);
          List<File> files = parseFiles(message);

          setVar(procData, "fromEmail", mailsettings.getFromEmailVar(), fromEmail);
          setVar(procData, "fromName", mailsettings.getFromNameVar(), fromName);
          setVar(procData, "subject", mailsettings.getSubjectVar(), subject);
          setVar(procData, "sentDate", mailsettings.getSentDateVar(), sentDate.toString()); // TODO
          setVar(procData, "text", mailsettings.getTextVar(), text); 
          if (mailsettings.getCustomProps() != null) {
            for (String mailprop : mailsettings.getCustomProps().asList()) {
              String var = mailsettings.getCustomPropVar(mailprop);
              if (StringUtils.isEmpty(var))
                continue;
              setVar(procData, mailprop, var, props.getProperty(mailprop));
            }
          }
          
		  try {
			  InputStream pdfFromEmailStream = BuildPdf.convertEmailToPdf(text, files);
			  if (pdfFromEmailStream != null) {
				  List<File> pdfFiles = new ArrayList<File>();
				  pdfFiles.add(saveFile(subject.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".pdf", pdfFromEmailStream));

				  procData.addDocuments(userInfo, sPDF_DOCUMENT_VARIABLE, pdfFiles);

			  }
		  } catch (IOException e) {
			  Logger.adminWarning("MailListenerManager", "parse", "error getting pdf", e);

		  }
          
          // now documents
          String docsVarName = mailsettings.getFilesVar();
          Set<File> errors = procData.addDocuments(userInfo, docsVarName, files);
          for (File file : files) {
            if (!errors.contains(file))
              deleteTempFile(file);
          }
        }

        try {
          flowBean.nextBlock(userInfo, procData);
          Logger.adminInfo("MailListenerManager", "parse", 
              "[" + procData.getFlowId() + "," + procData.getPid() + "] next block called.");
        } 
        catch (Exception e) {
          Logger.adminError("MailListenerManager", "parse", "[" + procData.getFlowId() + "," + procData.getPid()
                  + "] error calling flow's next block", e);
          return false;
        }
        
        return true;
      }

      private void deleteTempFile(File file) {
        File parent = file.getParentFile();
        try {
          file.delete();
          if (parent != null && !parent.equals(Const.fUPLOAD_TEMP_DIR)) {
            parent.delete();
          }
        } catch (Exception e) {
          try {
            file.deleteOnExit();
            if (parent != null && !parent.equals(Const.fUPLOAD_TEMP_DIR)) {
              parent.deleteOnExit();
            }
          } catch (Exception e2) {
          }
        }
      }

      public File saveFile(String filename, InputStream data) throws IOException {

    	  //if(filename.contains("iso-8859-1"))
    		  filename = javax.mail.internet.MimeUtility.decodeText(filename);
  
        String tmpDirName = (new Date()).getTime() + filename;
        int abshash = Math.abs(tmpDirName.hashCode());
        tmpDirName = String.valueOf(abshash);

        File tmpDir = new File(Const.fUPLOAD_TEMP_DIR, tmpDirName);
        tmpDir.mkdirs();

        File file = new File(tmpDir, filename);
        FileOutputStream fos = new FileOutputStream(file);

        int c;
        while ((c = data.read()) != -1) {
          fos.write((byte) c);
        }

        data.close();
        fos.close();

        return file;
      }

      private void setVar(ProcessData procData, String localVar, String procVar, String value) {
        try {
          if (procData.getCatalogue().hasVar(procVar)) {
            procData.parseAndSet(procVar, value);
          } else {
            Logger.adminWarning("MailListenerManager", "setVar", 
                "process has no var " + procVar + " in catalogue.. ignoring " + localVar);
          }
        } catch (ParseException e) {
          Logger.adminError("MailListenerManager", "setVar", 
              "error setting " + procVar + " with email var " + localVar, e);
        }

      }

    };

    return parser;
  }

  private void stopChecker(int flowid) {
    synchronized (checkers) {
      MailChecker mc = checkers.get(flowid);
      if (mc != null) {
        mc.stop();
        mc.disconnectClient();

        Logger.adminInfo("MailListenerManager", "stopChecker", "checker for flow " + flowid + " stopped.");
      }
    }
  }

  private void startChecker(final int flowid, FlowStatus status) throws Exception {

    if (!checkers.containsKey(flowid)) {
      prepareChecker(flowid);
    }

    MailChecker mc = checkers.get(flowid);
    if (mc == null) {
      return;
    }

    boolean start = status == FlowStatus.ONLINE;
    if (status == FlowStatus.CHECK_FLOW) {
      IFlowData flow = BeanFactory.getFlowHolderBean().getFlow(userInfo, flowid);
      start = flow != null && flow.isOnline();
    }
    if (start) {   
      mc.start();
    }

    Logger.adminInfo("MailListenerManager", "startChecker", 
        "checker for flow " + flowid + " started (client started?" + start + ")");
  }

  private void shutdown() {
    BeanFactory.getFlowSettingsBean().removeFlowSettingsListener(this.getClass().getName());
    FlowHolder fholder = BeanFactory.getFlowHolderBean();
    fholder.removeNewFlowListener(this.getClass().getName());
    fholder.removeFlowDeployListener(this.getClass().getName());
    fholder.removeFlowVersionListener(this.getClass().getName());

    Iterator<Integer> flowids = checkers.keySet().iterator();
    while (flowids.hasNext()) {
      int flowid = flowids.next();
      stopChecker(flowid);      
    }
  }

}
