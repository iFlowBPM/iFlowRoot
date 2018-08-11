package pt.iflow.blocks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.connectors.DMSConnectorUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.credentials.DMSCredential;
import pt.iflow.connector.dms.ContentResult;
import pt.iflow.connector.dms.DMSUtils;
import pt.iflow.connector.document.DMSDocument;
import pt.iflow.connector.document.Document;

public class BlockDocumentGet extends Block {
  public Port portIn, portSuccess, portError;

  private static final String ID = "id";
  private static final String VARIABLE = "variable";
  private static final String LOCK = "lock";

  private static final String OUTPUT_TITLE = "output.title";
  private static final String OUTPUT_DESCRIPTION = "output.description";
  private static final String OUTPUT_AUTHOR = "output.author";
  private static final String OUTPUT_ID = "output.id";
  private static final String OUTPUT_NAME = "output.name";
  private static final String OUTPUT_URL = "output.url";
  private static final String OUTPUT_PATH = "output.path";
  private static final String OUTPUT_CREATED = "output.created";
  private static final String OUTPUT_MODIFIED = "output.modified";

  private static final String PROP_NAME_PREFIX = "name_";
  private static final String PROP_VALUE_PREFIX = "value_";

  //AUTHENTICATION
  private static final String AUTHENTICATION = "o_AUTH";
  private static final String USER = "o_USER";
  private static final String CHAVE = "o_PASS";
  
  
  public BlockDocumentGet(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    saveFlowState = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getInPorts(pt.iflow.api.utils.UserInfoInterface)
   */
  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getEventPort()
   */
  public Port getEventPort() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getOutPorts(pt.iflow.api.utils.UserInfoInterface)
   */
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#before(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#after(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port retObj = portError;
    String login = userInfo.getUtilizador();

    
    
    try {
      String variable = this.getAttribute(VARIABLE);
      if (StringUtils.isNotBlank(variable)) {
        ProcessListVariable docsVar = procData.getList(variable);
        if (docsVar != null) {
          Documents docBean = BeanFactory.getDocumentsBean();
          DMSUtils dmsUtils = DMSUtils.getInstance();
          String dmsId = this.getParsedAttribute(userInfo, ID, procData);
          int docId;
          try {
            docId = Integer.parseInt(dmsId);
            Document doc = docBean.getDocument(userInfo, procData, docId);
            if (doc instanceof DMSDocument) {
              dmsId = StringUtils.isBlank(((DMSDocument) doc).getUuid()) ? dmsId : ((DMSDocument) doc).getUuid();
              if (Logger.isDebugEnabled()) {
                Logger.debug(login, this, "after", "Converted docId '" + docId + "' to dmsid '" + dmsId + "'");
              }
            }
          } catch (NumberFormatException ex) {
            docId = 0;
            if (Logger.isDebugEnabled()) {
              Logger.debug(login, this, "after", "Using dmsid '" + dmsId + "'");
            }
          }
          Map<String, String> properties = new HashMap<String, String>();
          properties.put(ID, dmsId);
          properties.put(LOCK, this.getAttribute(LOCK));
          
          //AUTHENTICATION
          DMSCredential credential = null;
          if(this.getParsedAttribute(userInfo, AUTHENTICATION, procData).equals("true")){
        	  credential =  DMSConnectorUtils.createCredentialAuth(this.getAttribute(USER), this.getAttribute(CHAVE));
          }else{
        	  credential =  DMSConnectorUtils.createCredential(userInfo, procData);  
          }
          
          DMSDocument document = dmsUtils.getDocument(credential, properties);
          if (docId > 0) {
            document.setDocId(docId);
          }
          Document doc = docBean.addDocument(userInfo, procData, document);
          extractProperties(userInfo, procData, dmsUtils.peek(credential, document), document.getComments());
          Logger.info(login, this, "after", "[" + procData.getFlowId() + "," + procData.getPid() + "," + procData.getSubPid()
              + "] file (" + doc.getFileName() + ") for var " + variable + " added.");
          this.addToLog("File '" + doc.getFileName() + "' for var '" + variable + "' added;");
          docsVar.parseAndAddNewItem(String.valueOf(doc.getDocId()));
          retObj = portSuccess;
        }
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "Exception caught: ", e);
    }

    this.addToLog("Using '" + retObj.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    return retObj;
  }

  private void extractProperties(UserInfoInterface userInfo, ProcessData procData, ContentResult content,
      Map<String, String> properties) {
    extractDefaultProperties(userInfo, procData, content);
    extractDynamicProperties(userInfo, procData, properties);
  }

  private void extractDynamicProperties(UserInfoInterface userInfo, ProcessData procData, Map<String, String> properties) {
    String login = userInfo.getUtilizador();
    Map<String, String> metadata = new HashMap<String, String>();
    String name = null;
    for (int i = 0; StringUtils.isNotEmpty(name = this.getParsedAttribute(userInfo, PROP_NAME_PREFIX + i, procData)); i++) {
      String value = this.getAttribute(PROP_VALUE_PREFIX + i);
      metadata.put(name, value);
    }
    for (String key : metadata.keySet()) {
      if (properties.containsKey(key)) {
        try {
          procData.parseAndSet(metadata.get(key), properties.get(key));
          Logger
              .info(login, this, "extractProperties", "Set variable '" + metadata.get(key) + "' as '" + properties.get(key) + "'");
          this.addToLog("Set variable '" + metadata.get(key) + "' as '" + properties.get(key) + "'");
        } catch (ParseException e) {
          Logger.error(login, this, "extractProperties", procData.getSignature() + "Unable to set variable '" + metadata.get(key)
              + "' as '" + properties.get(key) + "': " + e.toString());
        }
      }
    }
  }

  private void extractDefaultProperties(UserInfoInterface userInfo, ProcessData procData, ContentResult content) {
    SimpleDateFormat formatter = new SimpleDateFormat(Const.sDEF_DATE_FORMAT);
    extractProperty(userInfo, procData, this.getAttribute(OUTPUT_ID), content.getId());
    extractProperty(userInfo, procData, this.getAttribute(OUTPUT_TITLE), content.getTitle());
    extractProperty(userInfo, procData, this.getAttribute(OUTPUT_DESCRIPTION), content.getDescription());
    extractProperty(userInfo, procData, this.getAttribute(OUTPUT_AUTHOR), content.getAuthor());
    extractProperty(userInfo, procData, this.getAttribute(OUTPUT_NAME), content.getName());
    extractProperty(userInfo, procData, this.getAttribute(OUTPUT_URL), content.getUrl());
    extractProperty(userInfo, procData, this.getAttribute(OUTPUT_PATH), content.getPath());
    extractProperty(userInfo, procData, this.getAttribute(OUTPUT_CREATED), formatter.format(content.getCreateDate()));
    extractProperty(userInfo, procData, this.getAttribute(OUTPUT_MODIFIED), formatter.format(content.getModifiedDate()));
  }

  private void extractProperty(UserInfoInterface userInfo, ProcessData procData, String name, String value) {
    if (StringUtils.isNotBlank(name)) {
      procData.set(name, value);
      this.addToLog("Set '" + name + "' as '" + value + "';");
      if (Logger.isDebugEnabled()) {
        Logger
            .debug(userInfo.getUtilizador(), this, "extractProperty", procData.getSignature() + "Set '" + name + "' as '" + value);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#canProceed(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getDescription(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Document Download");
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getResult(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Document Download Efectuado");
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getUrl(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
