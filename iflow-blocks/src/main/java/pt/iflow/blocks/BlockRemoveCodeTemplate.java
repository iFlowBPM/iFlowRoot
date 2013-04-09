package pt.iflow.blocks;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.CodeTemplateManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.StringUtilities;
import pt.iflow.api.transition.ProfilesTO;
import pt.iflow.api.core.UserManager;
import pt.iflow.api.documents.CodeTemplate;
/**
 * <p>Title: BlockAddCodeTemplate</p>
 * <p>Description: This block removes a template, identified by name, from the internal template table</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Infosistema</p>
 * @author
 */

public class BlockRemoveCodeTemplate extends Block {
  public Port portIn, portSuccess, portError;

  private static final String NAME = "Name";

  public BlockRemoveCodeTemplate(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();

    try {
      String sNameVar = this.getAttribute(NAME);

      if (StringUtilities.isEmpty(sNameVar)) {
          Logger.error(login, this, "after", 
                  		procData.getSignature() + "empty value for Nome attribute");
          outPort = portError;
      }
      else {
        String sName = procData.transform(userInfo, sNameVar);

        Logger.debug(login, this, "after", "Nome=" + sName);

        if (sName != null) {
        	sName = sName.trim();
        }

        if (StringUtilities.isEmpty(sName)) {
            Logger.error(login, this, "after", 
                    	procData.getSignature() + "empty value for parsed Nome");
            outPort = portError;
        } 
        else {
        	CodeTemplateManager ap = BeanFactory.getCodeTemplateManagerBean();

        	ap.removeCodeTemplate(userInfo, procData, sName);                     
        	              
        	Logger.info(login,this,"after","Template with name" + sName + " was removed with success");
        	outPort = portSuccess;
        	}	
        }
    } catch (Exception e) {
      Logger.error(login, this, "after", 
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portError;
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }


  public String getDescription(UserInfoInterface userInfo, ProcessData process) {
    return this.getDesc(userInfo, process, true, "Remove Code Template");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Removed Code Template");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
