package pt.iflow.api.notification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import pt.iflow.api.cluster.JobManager;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.ResourceModifiedListener;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iknow.utils.VelocityUtils;
import pt.iknow.utils.xml.NamedXMLElement;
import pt.iknow.utils.xml.SimpleXMLParser;

public class EmailManager extends Thread {

  public static final String sSEPARATOR = ",";
  
  // defualt timeout of 15 seconds
  private static final int nDEF_TIMEOUT = 15;
  // number of possible timeouts to occur before considering thread invalid
  private static final int nINVALID_TIMEOUT_COUNT = 5;
  // number of cycles to execute after first empty email list before stopping
  // execution
  private static final int nEMPTY_ALLOWED_COUNTER = 2;
  // timeout to move email to dirty table
  private static final long DIRTY_TIMESTAMP = (long) (1000 * 60 * 60 * 24 * 1); // 1
                                                                                // day

  // Queries
  private static final String EMAILS_NO_LIMIT = "EmailManager.EMAILS_NO_LIMIT";
  private static final String EMAILS_LIMIT = "EmailManager.EMAILS_LIMIT";
  private static final String EMAIL_DATA = "EmailManager.EMAIL_DATA";
  private static final String DELETE_EMAIL = "EmailManager.DELETE_EMAIL";
  private static final String INSERT_DIRTY_EMAIL = "EmailManager.INSERT_DIRTY_EMAIL";
  private static final String UPDATE_EMAIL_TRIES = "EmailManager.UPDATE_EMAIL_TRIES";
  private static final String INSERT_EMAIL = "EmailManager.INSERT_EMAIL";

  
  
  
  private static EmailManager _em = null;

  private boolean _bStop = false;
  private boolean _bRunning = true;
  private java.util.Date _dtLastCycle = null; // used to check if thread is
                                              // running
  private int _nEmptyCycleCount = 0;
  private int _nTimeout = Const.nEMAIL_MANAGER_TIMEOUT;

  // key: template name
  // value: EmailTemplate object
  private static HashMap<String, EmailTemplate> _hmTemplates = new HashMap<String, EmailTemplate>();


  private static final String sFROM_TAG = "emailfrom";
  private static final String sSUBJECT_TAG = "emailsubject";
  private static final String sBODY_TAG = "emailbody";
  private static final String sHTML_TAG = "emailhtml";
  
  // One minute
  private static final long TIME_INTERVAL_UNIT = 60000L;

  static {
    restartManager();
  }

  private synchronized static void restartManager() {
    if (_em != null) {
      _em._bStop = true;
      _em = null;
    }
    if(Const.bUSE_EMAIL) {
      _em = new EmailManager();
      _em.start();
    }
  }

  public EmailManager() {
    _bStop = false;
    _bRunning = true;

    if (_nTimeout <= 0) {
      _nTimeout = nDEF_TIMEOUT;
    }
    _nTimeout = _nTimeout * 1000;

    // reset template list
  }

  public synchronized void signalThreadCycle() {
    this._dtLastCycle = new java.util.Date();
  }

  public void checkManager() {
    if (!this._bRunning) {
      restartManager();
    }
    else {
      if (this._dtLastCycle != null) {
        long last = this._dtLastCycle.getTime();
        long now = new java.util.Date().getTime();

        long diff = now - last;
        long maxdiff = _nTimeout * nINVALID_TIMEOUT_COUNT;

        if (diff > maxdiff) {
          restartManager();
        }
      }
    }
  }

  public void run() {

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    String sId = null;
    Email email = null;

    String sError = null;
    String stmp = null;
    ArrayList<String> altmp = null;
    Timestamp tstmp = null;
    Timestamp lastSent = null;
    Timestamp nextSend = null;
    int tries = 0;
    long lCurrTimestamp = 0;
    boolean btmp = false;

    while (true) {
      if (_bStop) {
        return;
      }

      this.signalThreadCycle();

      if(JobManager.getInstance().isMyBeatValid())
	      try {
	        
	
	        String query = "";
	
	        if (Const.nEMAIL_MANAGER_LIMIT > 0) {
	        	query = DBQueryManager.processQuery(EmailManager.EMAILS_LIMIT, new Object[]{new Integer(Const.nEMAIL_MANAGER_LIMIT)});
	        } else {
	        	query = DBQueryManager.processQuery(EmailManager.EMAILS_NO_LIMIT);
	        }
	
	        Logger.adminDebug("EmailManager", "run", "EMAILMANAGER QUERY=" + query);
	
	        ds = Utils.getDataSource();
	        db = ds.getConnection();
	        db.setAutoCommit(false);
	        st = db.createStatement();
	        rs = st.executeQuery(query);
	
	        altmp = new ArrayList<String>();
	        while (rs != null && rs.next()) {
	          altmp.add(rs.getString("eid"));
	        }
	        if (rs != null && altmp.size() == 0) {
	          // no results
	          this._nEmptyCycleCount++;
	        }
	        else {
	          this._nEmptyCycleCount = 0;
	        }
	        rs.close();
	        rs = null;
	
	        lCurrTimestamp = (new java.util.Date()).getTime();
	
	        for (int i = 0; i < altmp.size(); i++) {
	          sId = altmp.get(i);
	
	          sError = null;
	          email = null;
	          
	          try {
	
	            query = DBQueryManager.processQuery(EmailManager.EMAIL_DATA, new Object[]{sId});
	
	            Logger.adminDebug("EmailManager", "run", "EMAILMANAGER QUERY=" + query);
	            sError = "email db fetch";
	
	            rs = st.executeQuery(query);
	            if (rs.next()) {
	              stmp = rs.getString("eserver");
	              int port = rs.getInt("eport");
	              email = new Email(stmp, port);
	
	              // ID
	              email.setId(sId);
	
	              // FROM
	              stmp = rs.getString("efrom");
	              email.setFrom(stmp);
	
	              sError = "TO set";
	
	              // TO
	              try (InputStream inStream = rs.getBinaryStream("eto");) 
	              {
	            	  stmp = parseBlobStream(inStream);
	            	  email.setTo(Utils.tokenize(stmp, sSEPARATOR));
	              }
	              catch (Exception e4) {}
	              
	              // CC
	              try (InputStream inStream = rs.getBinaryStream("ecc");)
	              {
	                stmp = parseBlobStream(inStream);
	                if (stmp != null && !stmp.equals("")) {
	                  email.setCc(Utils.tokenize(stmp, sSEPARATOR));
	                }
	              }
	              catch (Exception e3) {
	                Logger.adminError("EmailManager", "run", "Exception3: EMAIL " + sId + ": CC set FAILED: " + e3.getMessage(), e3);
	              }
	              
	              sError = "SUBJECT set";
	
	              // SUBJECT
	              stmp = rs.getString("esubject");
	              email.setSubject(stmp);
	
	              sError = "BODY set";
	
	              // BODY
	              try (InputStream inStream = rs.getBinaryStream("ebody");)
	              {
		              stmp = parseBlobStream(inStream);
		              email.setMsgText(stmp);
	              }
	              catch (Exception e4) {}
	
	              // HTML
	              if (rs.getInt("ehtml") == 1) {
	                email.setHtml(true);
	              }
	              else {
	                email.setHtml(false);
	              }
	
	              // CREATED
	              tstmp = rs.getTimestamp("ecreated");
	              if (tstmp != null) {
	                email.setCreatedTimestamp(tstmp.getTime());
	              }
	
	              // Number tries
	              tries = rs.getInt("etries");
	              
	              // Next send
	              nextSend = rs.getTimestamp("enext_send");
	              
	              // Last send
	              lastSent = rs.getTimestamp("elast_send");
	              
	              // Use TLS
	              email.setStartTls(rs.getInt("etls") == 1);
	              
	              // Use Auth
	              email.setAuth(rs.getInt("eauth") == 1);
	              
	              // SMTP username
	              email.setUser(rs.getString("euser"));
	              
	              // SMTP password
	              email.setPass(rs.getString("epass"));
	              
	            }
	            else {
	              email = null;
	            }
	            rs.close();
	            rs = null;
	
	            if (email != null) {
	
	              sError = "email send";
	
	              email.disableEmailManager();
	              btmp = false;
	              try {
	                if (email.sendMsg()) {
	                  sError = "email delete";
	                  btmp = true;
	                  query = DBQueryManager.processQuery(EmailManager.DELETE_EMAIL, new Object[]{sId});
	                  st.executeUpdate(query);
	                  sError += " commit";
	                  db.commit();
	                }
	                else {
	                  processErroneousEmail(sId, email, lCurrTimestamp, db, st, tries, lastSent, nextSend);
	                }
	              }
	              catch (Exception e4) {
	                Logger.adminError("EmailManager", "run", "Exception4: EMAIL " + sId + " FAILED (" + sError + "): " + e4.getMessage(), e4);
	                if (btmp) {
	                  db.rollback();
	                }
	                Logger.adminWarning("EmailManager", "run", "ADDING " + sId + " TO BLACK LIST");
	              }
	            }
	            else {
	              processErroneousEmail(sId, email, lCurrTimestamp, db, st, tries, lastSent, nextSend);
	            }
	          }
	          catch (Exception e2) {
	            Logger.adminError("EmailManager", "run", "Exception2: EMAIL " + sId + " FAILED (" + sError + "): " + e2.getMessage(), e2);
	            processErroneousEmail(sId, email, lCurrTimestamp, db, st, tries, lastSent, nextSend);
	          }
	          
	        }
	      }
	      catch (Exception e) {
	        e.printStackTrace();
	        Logger.adminError("EmailManager", "run", "RUN EXCEPTION: " + e.getMessage(), e);
	      }
	      finally {
	        DatabaseInterface.closeResources(db, st, rs);
	        ds = null;
	      }

      this.signalThreadCycle();

      if (this._nEmptyCycleCount >= nEMPTY_ALLOWED_COUNTER) {
        this._bRunning = false;
        return;
      }

      try {
        Thread.sleep(_nTimeout);
      }
      catch (InterruptedException e) {
        continue;
      }
    }
  }

  private static void processErroneousEmail(String sId, Email aeEmail, long alCurrTimestamp, Connection db, Statement st, int ntries, Timestamp lastTry, Timestamp nextTry) {

    long ltmp = 0;
    boolean btmp = false;
    String query = "";
    PreparedStatement pst = null;
    
    try {
      if (aeEmail != null) {
        ltmp = aeEmail.getCreatedTimestamp();
      }

      if((Const.iMAIL_MAXIMUM_RETRIES != 0 && ntries >= Const.iMAIL_MAXIMUM_RETRIES) || (ltmp > 0 && alCurrTimestamp > (ltmp + DIRTY_TIMESTAMP))) {
        // email is dirty (unable to send email during dirty_timestamp)
        // move it to dirty_email table
        btmp = true;
        query = DBQueryManager.processQuery(EmailManager.INSERT_DIRTY_EMAIL, new Object[]{sId});
        st.executeUpdate(query);
        query = DBQueryManager.processQuery(EmailManager.DELETE_EMAIL, new Object[]{sId});
        st.executeUpdate(query);
        
        db.commit();
      } else { // Number of tries not exceeded, rewrite email
        Logger.adminDebug("EmailManager", "processErroneousEmail", "Email " + sId + " to be rescheduled");
        btmp = true;
        ntries++;
        
        long timet;
        if(nextTry == null) {
          Date now = new Date();
          timet = now.getTime()+(TIME_INTERVAL_UNIT*Const.lMAIL_RESCHEDULE_INTERVAL);
          nextTry = new Timestamp(now.getTime());
        } else {
          timet = nextTry.getTime() + ((nextTry.getTime()-lastTry.getTime())*ntries);
        }
        query = DBQueryManager.getQuery(EmailManager.UPDATE_EMAIL_TRIES);
        
        pst = db.prepareStatement(query);
        pst.setInt(1, ntries);
        pst.setTimestamp(2, nextTry); // last is "now"
        pst.setTimestamp(3, new Timestamp(timet)); // next is now plus some minutes
        pst.setString(4, sId);
        pst.executeUpdate();
        pst.close();
        db.commit();
        
        Logger.adminWarning("EmailManager", "processErroneousEmail", "Rescheduling " + sId + " TO "+new Timestamp(timet)+ " ("+ntries+"/"+Const.iMAIL_MAXIMUM_RETRIES+" tries)");
      }
    }
    catch (Exception e) {
      Logger.adminError("EmailManager", "processErroneousEmail", "Exception: EMAIL " + sId + " FAILED: " + e.getMessage(), e);
      if (btmp) {
        try {
          if(pst != null) pst.close();
        }
        catch (SQLException e1) {
        }
        try {
          db.rollback();
        }
        catch (Exception ee) {
        }
      }
    }
  }

  public static boolean setEmail(Email aeEmail) {// FIXME ISto eh grave!
    if(!Const.bUSE_EMAIL) return true;  // No email, no nothing...
    
    boolean retObj = false;

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    PreparedStatement pst = null;

    String query = null;
    byte[] baTo = null;
    byte[] baCc = null;
    byte[] baBody = null;
    Iterator<String> iter = null;

    _em.checkManager();

    try {

      // initialize baTo
      StringBuffer sbTo = null;
      iter = aeEmail.hsTo.iterator();
      while (iter.hasNext()) {
        if (sbTo == null) {
          sbTo = new StringBuffer();
        }
        else {
          sbTo.append(sSEPARATOR);
        }
        sbTo.append((String) iter.next());
      }
      if (sbTo != null) {
        baTo = sbTo.toString().getBytes("UTF-8");
      }      
      sbTo = null;  // request dispose

      // initialize baCc
      StringBuffer sbCc = null;
      iter = aeEmail.hsCc.iterator();
      while (iter.hasNext()) {
        if (sbCc == null) {
          sbCc = new StringBuffer();
        }
        else {
          sbCc.append(sSEPARATOR);
        }
        sbCc.append((String) iter.next());
      }
      if (sbCc != null) {
        baCc = sbCc.toString().getBytes("UTF-8");
      } else {
        baCc = new byte[0];
      }
      sbCc = null;  // request dispose

      // validations
      if (StringUtils.isEmpty(aeEmail.from)) {
        Logger.adminWarning("EmailManager", "setEmail", 
            aeEmail.getCallingProcessSignature() + "No FROM specified.. aborting...");
        return false;        
      }
      if (baTo == null) {
        Logger.adminWarning("EmailManager", "setEmail", 
            aeEmail.getCallingProcessSignature() + "No TO specified.. aborting...");
        return false;
      }
      

      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();


      query = DBQueryManager.getQuery(EmailManager.INSERT_EMAIL);


      pst = db.prepareStatement(query);

      int position = 1;
      pst.setString(position++, aeEmail.host);
      pst.setInt(position++, aeEmail.port);
      
      pst.setString(position++, aeEmail.from);
      
      ByteArrayInputStream isTo = new ByteArrayInputStream(baTo);
      pst.setBinaryStream(position++, isTo, baTo.length);

      ByteArrayInputStream isCc = new ByteArrayInputStream(baCc);
      pst.setBinaryStream(position++, isCc, baCc.length);

      pst.setString(position++, aeEmail.subject);
      
      baBody = aeEmail.msgText.getBytes("UTF-8");
      ByteArrayInputStream isBody = new ByteArrayInputStream(baBody);
      pst.setBinaryStream(position++, isBody, baBody.length);
      
      if (aeEmail.bHtml) pst.setString(position++, "1");
      else pst.setString(position++, "0");
      pst.setInt(position++, 0);
      pst.setInt(position++, aeEmail.isStartTls()?1:0);
      pst.setInt(position++, aeEmail.isAuth()?1:0);
      pst.setString(position++, aeEmail.getUser());
      pst.setString(position++, aeEmail.getPass());
      // TODO: set email process signature in database
      
      
      pst.execute();

      pst.close();
      pst = null;

      retObj = true;

      Logger.adminInfo("EmailManager", "setEmail", 
          aeEmail.getCallingProcessSignature() + "Email SET in DB: TO=" + new String(baTo));

    }
    catch (Exception e) {
      Logger.adminError("EmailManager", "setEmail", "EXCEPTION: " + e.getMessage(), e);
      retObj = false;
    }
    finally {
      try {
        if (pst != null) pst.close();
      }
      catch (Exception rse) {
      }
      DatabaseInterface.closeResources(db, st, rs);
    }

    return retObj;
  }

  private static String parseBlobStream(java.io.InputStream is) {
    String retObj = null;

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int STREAM_SIZE = 8096;
      byte[] r = new byte[STREAM_SIZE];
      int j = 0;

      while ((j = is.read(r, 0, STREAM_SIZE)) != -1) {
        baos.write(r, 0, j);
      }
      baos.flush();
      baos.close();

      retObj = baos.toString("UTF-8");
    }
    catch (Exception e) {
    }

    return retObj;
  }

  public static EmailTemplate getEmailTemplate(UserInfoInterface userInfo, String asName) {
    return EmailManager.getEmailTemplate(userInfo, null, asName);
  }

  public static EmailTemplate getEmailTemplate(UserInfoInterface userInfo, String asTemplateDir, String asName) {

    EmailTemplate retObj = null;

    try {

      String sTemplateDir = asTemplateDir;
      if (sTemplateDir != null && !sTemplateDir.equals("")) {
        sTemplateDir = sTemplateDir + "/";
      }
      else if (sTemplateDir == null) {
        sTemplateDir = "";
      }

      String sName = asName;

      if (sName == null) return null;

      if (!sName.endsWith(".et")) {
        sName = sName + ".et";
      }
      
      retObj = buildEmailTemplate(userInfo, sTemplateDir, sName);
    }
    catch (Exception e) {
      Logger.adminError("EmailManager", "getEmailTemplate", "Caught exception: " + e.getMessage(), e);
    }

    return retObj;

  }

  public static Email buildEmail(Hashtable<?,?> ahtSubst, EmailTemplate aetTemplate) {
    Email retObj = new Email();  // use defaults configured in Const

    try {
      retObj.setFrom(VelocityUtils.processTemplate(ahtSubst, aetTemplate.getFrom()));

      retObj.setSubject(VelocityUtils.processTemplate(ahtSubst, aetTemplate.getSubject()));

      retObj.setMsgText(VelocityUtils.processTemplate(ahtSubst, aetTemplate.getBody()));

      retObj.setHtml(aetTemplate.getHtml());
    }
    catch (Exception e) {
      Logger.adminError("EmailManager", "buildEmail", "Caught exception: " + e.getMessage(), e);
      retObj = null;
    }

    return retObj;
  }

  public static EmailTemplate buildEmailTemplate(UserInfoInterface userInfo, String asTemplateDir, String asTemplate) {
    Logger.adminInfo("EmailManager", "buildEmailTemplate", "Getting template for: "+(asTemplate));
    
    EmailTemplate retObj = null;
    
    String cacheKey = getCacheKey(userInfo, asTemplate);

    if (_hmTemplates.containsKey(cacheKey)) {
      return _hmTemplates.get(cacheKey);
    }

    Repository rep = BeanFactory.getRepBean();
    try (InputStream input = rep.getEmailTemplate(userInfo, asTemplate).getResourceAsStream();){
   
      NamedXMLElement templateDOM = null; // this is a DOM abstraction...
      if (null != input) {
        templateDOM = SimpleXMLParser.parseXML(new InputSource(input));
      }
      else {
        templateDOM = null;
      }
      
      if (templateDOM == null) {
        throw new Exception("Couldn't get mail template " + asTemplate + " from repository");
      }

      
      String from = null;
      String subject = null;
      String html = null;
      String body = null;
      
      Node fromNode = templateDOM.getChildElement(sFROM_TAG);
      if(fromNode != null) from = fromNode.getTextContent();
      
      Node subjectNode = templateDOM.getChildElement(sSUBJECT_TAG);
      if(subjectNode != null) subject = subjectNode.getTextContent();

      Node htmlNode = templateDOM.getChildElement(sHTML_TAG);
      if(htmlNode != null) html = htmlNode.getTextContent();

      Node bodyNode = templateDOM.getChildElement(sBODY_TAG);
      if(bodyNode != null) body = bodyNode.getTextContent();

      
      // Assume HTML template by default.
      boolean isHTML = html != null && (StringUtils.isEmpty(html) || !ArrayUtils.contains(new String[]{"false","no","0"}, html.toLowerCase()));
      
      // Assume app email by default
      if(StringUtils.isEmpty(from)) from = Const.sAPP_EMAIL;
      
      retObj = new EmailTemplate(asTemplate, from, subject, body, isHTML);

      // store template in template cache
      _hmTemplates.put(cacheKey, retObj);

    }
    catch (Exception e) {}
    return retObj;
  }

  private static String getCacheKey(UserInfoInterface userInfo, String name) {
    String organization = Const.SYSTEM_ORGANIZATION;
    if(null != userInfo) organization = userInfo.getOrganization();
    return organization+"*"+name;
  }
  
  public static void clearCacheElement(UserInfoInterface userInfo, String name) {
    _hmTemplates.remove(getCacheKey(userInfo, name));
    Logger.adminInfo("EmailManager", "clearCacheElement", "Removing cache element for: "+name);
  }

  public static void resetEmailCache() {
    _hmTemplates.clear();
    Logger.adminInfo("EmailManager", "resetEmailCache", "Cache cleaned");
  }

  private static ResourceModifiedListener modificationEvent = null;
  
  public static ResourceModifiedListener getResourceModifiedListener() {
    if(null == modificationEvent) {
      modificationEvent = new ResourceModifiedListener() {
        public void resourceModified(UserInfoInterface userInfo, String resource, String fullname) {
          clearCacheElement(userInfo, resource);
        }
      };
    }
    
    return modificationEvent;
  }
  

}
