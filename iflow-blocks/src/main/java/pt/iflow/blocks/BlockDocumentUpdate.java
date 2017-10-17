package pt.iflow.blocks;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.connectors.DMSConnectorUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.credentials.DMSCredential;
import pt.iflow.connector.dms.DMSUtils;
import pt.iflow.connector.document.DMSDocument;

public class BlockDocumentUpdate extends Block {
  public Port portIn, portSuccess, portError;

  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String AUTHOR = "author";
  private static final String VARIABLE = "variable";
  private static final String LOCK = "lock";
  private static final String VERSIONABLE = "versionable";
  private static final String PATH = "path";
  private static final String ID = "id";

  private static final String PROP_NAME_PREFIX = "name_";
  private static final String PROP_VALUE_PREFIX = "value_";

  
  private static final String AUTHENTICATION = "u_AUTH";
  private static final String USER = "u_USER";
  private static final String PASSWORD = "u_PASS";
  
  
  public BlockDocumentUpdate(int anFlowId, int id, int subflowblockid, String filename) {
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
      // prepare document properties
      Map<String, String> properties = new HashMap<String, String>();

      String name = null;
      for (int i = 0; StringUtils.isNotEmpty(name = this.getParsedAttribute(userInfo, PROP_NAME_PREFIX + i, procData)); i++) {
        String value = this.getParsedAttribute(userInfo, PROP_VALUE_PREFIX + i, procData);
        properties.put(name, value);
      }

      String path = this.getParsedAttribute(userInfo, PATH, procData);
      String variable = this.getAttribute(VARIABLE);
      if (StringUtils.isNotBlank(variable) && StringUtils.isNotBlank(path)) {
        properties.put(TITLE, this.getParsedAttribute(userInfo, TITLE, procData));
        properties.put(DESCRIPTION, this.getParsedAttribute(userInfo, DESCRIPTION, procData));
        properties.put(AUTHOR, this.getParsedAttribute(userInfo, AUTHOR, procData));
        properties.put(LOCK, this.getAttribute(LOCK));
        properties.put(VERSIONABLE, this.getAttribute(VERSIONABLE));
        properties.put(PATH, path);

        try {
          // check if variable itself is the document ID
          int docId = Integer.parseInt(variable);
          if (updateDocument(userInfo, procData, docId, properties)) {
            retObj = portSuccess;
          }
        } catch (Exception e) {
          // if the variable is not the ID, assume it is a list of ID's
          ProcessListVariable docsVar = procData.getList(variable);
          if (docsVar == null || docsVar.size() == 0) {
            retObj = portSuccess;
            if (Logger.isDebugEnabled()) {
              Logger.debug(userInfo.getUtilizador(), this, "after", "No file to update on DMS.");
            }
          } else {
            for (int i = 0, lim = docsVar.size(); i < lim; i++) {
              ProcessListItem item = docsVar.getItem(i);
              int docId = -1;
              try {
                docId = Integer.parseInt(item.format());
                if (updateDocument(userInfo, procData, docId, properties)) {
                  retObj = portSuccess;
                }
              } catch (NumberFormatException ex) {
                Logger.warning(userInfo.getUtilizador(), this, "after", "ID do documento é uma variável obrigatória (docId="
                    + docId + ")");
              } catch (Exception ex) {
                Logger.warning(userInfo.getUtilizador(), this, "after", "Document not updated/inserted, exception caught: ", ex);
              }
            }
          }
        }
      } else {
        Logger.warning(login, this, "after", "Required variable is invalid (" + VARIABLE + "=" + variable + "; " + PATH + "="
            + path + ")");
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "Exception caught: ", e);
    }
    this.addToLog("Using '" + retObj.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    return retObj;
  }

  private boolean updateDocument(UserInfoInterface userInfo, ProcessData procData, int docId, Map<String, String> properties)
      throws Exception { 
	  
	if (docId < 0) {
      Logger.warning(userInfo.getUtilizador(), this, "updateDocument",
          "Unable to update document due to invalid local reference (docId=" + docId + ")");
      return false;
    }
    Documents docBean = BeanFactory.getDocumentsBean();
    DMSUtils dmsUtils = DMSUtils.getInstance();
    
    //AUTHENTICATION
    DMSCredential credential = null;
    if(this.getParsedAttribute(userInfo, AUTHENTICATION, procData).equals("true")){
  	  credential =  DMSConnectorUtils.createCredentialAuth(this.getAttribute(USER), this.getAttribute(PASSWORD));
    }else{
  	  credential =  DMSConnectorUtils.createCredential(userInfo, procData);  
    }
    
    DMSDocument dmsDoc = dmsUtils.updateDocument(credential, docBean.getDocument(userInfo, procData, docId), properties);

    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "updateDocument", "Document '" + dmsDoc.getFileName()
          + "' updated to DMS service, attempting to update local document reference...");
    }
    if (dmsDoc != null && StringUtils.isNotBlank(dmsDoc.getUuid())) {
      properties.put(ID, dmsDoc.getUuid());
      dmsDoc = dmsUtils.getDocument(credential, properties);
      docBean.addDocument(userInfo, procData, dmsDoc);
      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "updateDocument", "Document '" + dmsDoc.getFileName()
            + "' updated successfully.");
      }
      return true;
    }
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "updateDocument", "Unable to complete update procedure on document '"
          + dmsDoc.getFileName() + "', local document reference is out of synch.");
    }
    return false;
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
    return this.getDesc(userInfo, procData, true, "Document Upload");
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getResult(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Document Upload Efectuado");
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
