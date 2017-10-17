package pt.iflow.blocks;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DMSDocumentIdentifier;
import pt.iflow.api.documents.DocumentIdentifier;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.DMSDocument;
import pt.iflow.connector.document.Document;

public class BlockDocumentDelete extends Block {
  public Port portIn, portSuccess, portError;

  private static final String FILES = "files";
  private static final String REMOTE = "remote";

  private static final String AUTHENTICATION = "d_AUTH";
  private static final String USER = "d_USER";
  private static final String PASSWORD = "d_PASS";
  
  public BlockDocumentDelete(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    saveFlowState = true;
  }

  @Override
  public Port after(UserInfoInterface userInfo, ProcessData procData) {  
    Port retObj = portError;
    String login = userInfo.getUtilizador();
    try {
      String file = this.getAttribute(FILES);
      boolean success = true;
      if (procData.isListVar(file)) {
        ProcessListVariable plv = procData.getList(file);
        for (int i = 0; i < plv.size(); i++) {
          ProcessListItem item = plv.getItem(i);
          success = this.removeFile(userInfo, procData, item.getRawValue());
        }
      } else {
        success = this.removeFile(userInfo, procData, file);
      }
      if (success) {
        retObj = portSuccess;
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "Exception caught: ", e);
    }
    this.addToLog("Using '" + retObj.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    return retObj;
  }

  private boolean removeFile(UserInfoInterface userInfo, ProcessData procData, String var) {
    Logger.debug("", this, "", "Delete AUTH "+this.getParsedAttribute(userInfo, AUTHENTICATION, procData));
    Logger.debug("", this, "", "Delete USER "+this.getAttribute(USER));
    Logger.debug("", this, "", "Delete PASS "+this.getAttribute(PASSWORD));  
	    
	  
    boolean retObj = false;
    Documents docBean = BeanFactory.getDocumentsBean();
    DocumentIdentifier docId = DocumentIdentifier.getInstance(var);
    
    //AUTHENTICATION
    Document doc =null;
    if(this.getParsedAttribute(userInfo, AUTHENTICATION, procData).equals("true")){
    	doc = docBean.getDocumentAuth(userInfo, procData, docId, this.getAttribute(USER),this.getAttribute(PASSWORD));
    }else{
    	doc = docBean.getDocument(userInfo, procData, docId);
    }
    
    if (doc != null) {
      if (docBean.removeDocument(userInfo, procData, doc)) {
        this.addToLog("Deleted local file with id '" + var + "';");
        retObj = true;
      }
      if (!Boolean.valueOf(this.getAttribute(REMOTE)) && doc instanceof DMSDocument) {
        DocumentIdentifier dmsDocId = DMSDocumentIdentifier.getInstance(((DMSDocument) doc).getUuid());
        
        //AUTHENTICATION
        if(this.getParsedAttribute(userInfo, AUTHENTICATION, procData).equals("true")){
            if (docBean.removeDocumentAuth(userInfo, procData, dmsDocId,this.getAttribute(USER),this.getAttribute(PASSWORD))) {
                this.addToLog("Deleted remote file with id '" + var + "';");
                retObj = true;
              }
        }else {
	        if (docBean.removeDocument(userInfo, procData, dmsDocId)) {
	          this.addToLog("Deleted remote file with id '" + var + "';");
	          retObj = true;
	        }
        }
      }
    }
    if (!retObj) {
      this.addToLog("Failed to completely delete file with id '" + var + "';");
    }
    return retObj;
  }

  @Override
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  @Override
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  @Override
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Document Deletion");
  }

  @Override
  public Port getEventPort() {
    return null;
  }

  @Override
  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  @Override
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  @Override
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Document Download Efectuado");
  }

  @Override
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
