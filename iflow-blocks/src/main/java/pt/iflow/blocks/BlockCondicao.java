package pt.iflow.blocks;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockValidacoes</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockCondicao extends Block {
  public Port portIn, portTrue, portFalse;

  public BlockCondicao(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
    saveFlowState = false;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portTrue;
    retObj[1] = portFalse;
    return retObj;
  }
  
  public Port getEventPort() {
      return null;
  }
  
  public Port[] getInPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[1];
      retObj[0] = portIn;
      return retObj;
  }

  /**
   * No action in this block
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort;

    String login = userInfo.getUtilizador();

    try {
      String sCondition = null;
      boolean bEvalResult = false;

      sCondition = this.getAttribute("condition");

      if (StringUtils.isEmpty(sCondition)) {
        Logger.warning(login,this,"after", 
            procData.getSignature() + "empty condition to evaluate!! Assuming false");
        bEvalResult = false;
      }
      else {
        try {
          bEvalResult = procData.query(userInfo, sCondition);
          Logger.debug(login,this,"after", sCondition + " evaluated " + bEvalResult);
        } catch (Exception ei) {
          bEvalResult = false;
          Logger.error(login,this,"after", 
              procData.getSignature() + "caught exception evaluation condition (assuming false): " + sCondition, ei);
        }
      }
      
      if (bEvalResult) {
        outPort = portTrue;
      } else {
        outPort = portFalse;
      }
      
      this.addToLog("Evaluated '" + sCondition + "' to '" + bEvalResult + "', using " + outPort.getName());
    }
    catch (Exception e) {
      Logger.error(login,this,"after",
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portFalse;
    }
    
    this.addToLog("Using '" + outPort.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Condição");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Condição Efectuada");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
