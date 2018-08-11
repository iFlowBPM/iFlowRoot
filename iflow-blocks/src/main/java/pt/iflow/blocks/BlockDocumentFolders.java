package pt.iflow.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.connectors.DMSConnectorUtils;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.credentials.DMSCredential;
import pt.iflow.connector.dms.ContentResult;
import pt.iflow.connector.dms.DMSUtils;

public class BlockDocumentFolders extends Block {

  public Port portIn, portSuccess, portError;

  private static final String PATTERN = "pattern";
  private static final String DEPTH = "depth";
  private static final String PATH = "path";
  private static final String PATHS = "paths";

  private static final String AUTHENTICATION = "f_AUTH";
  private static final String USER = "f_USER";
  private static final String CHAVE = "f_PASS";
  
  public BlockDocumentFolders(int anFlowId, int id, int subflowblockid, String filename) {
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
	  
    Port outPort = portError;
    String login = userInfo.getUtilizador();
    if (Logger.isDebugEnabled()) {
      Logger.debug(login, this, "after", procData.getSignature() + "Entered method!");
    }
    try {
      int depth;
      try {
        depth = Integer.parseInt(this.getParsedAttribute(userInfo, DEPTH, procData));
      } catch (NumberFormatException ex) {
        depth = 0;
      }
      
      //AUTHENTICATION
      DMSCredential credentialAux = null;
      if(this.getParsedAttribute(userInfo, AUTHENTICATION, procData).equals("true")){
    	  credentialAux =  DMSConnectorUtils.createCredentialAuth(this.getAttribute(USER), this.getAttribute(CHAVE));
      }else{
    	  credentialAux =  DMSConnectorUtils.createCredential(userInfo, procData);  
      }
      
      ContentResult content = DMSUtils.getInstance().getDescendents(
    		  credentialAux,
          this.getParsedAttribute(userInfo, PATH, procData), depth);
      String variable = this.getAttribute(PATHS);
      if (StringUtils.isNotBlank(variable)) {
        ProcessListVariable varList = procData.getList(variable);
        if (varList != null) {
          varList.clear();
          List<String> pathList = getPaths(content, this.getParsedAttribute(userInfo, PATTERN, procData));
          for (String value : pathList) {
            ProcessListItem item = varList.addNewItem(value);
            this.addToLog("Set '" + variable + "[" + item.getPosition() + "]" + "' as '" + item.getValue() + "';");
            if (Logger.isDebugEnabled()) {
              Logger.debug(userInfo.getUtilizador(), this, procData.getSignature() + "outputValues", "Set '" + variable + "["
                  + item.getPosition() + "]" + "' as '" + item.getValue());
            }
          }
          outPort = portSuccess;
        }
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "Exception caught: ", e);
    }
    this.addToLog("Using '" + outPort.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    if (Logger.isDebugEnabled()) {
      Logger.debug(login, this, "after", procData.getSignature() + "Leaving method!");
    }
    return outPort;
  }

  private List<String> getPaths(ContentResult content, String pattern) {
    List<String> retObj = new ArrayList<String>();
    for (ContentResult child : content.getChildren()) {
      if (!child.isLeaf()) {
        String path = child.getPath();
        path = path.replace("\\", "/");
        int beginIndex = 0;
        if (path.contains("/")) {
          beginIndex = path.lastIndexOf("/") + 1;
        }
        int endIndex = path.length();
        if (Pattern.matches(pattern, path.substring(beginIndex, endIndex))) {
          retObj.add(path);
        }
        retObj.addAll(getPaths(child, pattern));
      }
    }
    return retObj;
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
    return this.getDesc(userInfo, procData, true, "Document Folders");
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getResult(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Document Folders Efectuado");
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
