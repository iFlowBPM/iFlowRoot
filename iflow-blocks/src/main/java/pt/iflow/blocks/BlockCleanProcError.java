package pt.iflow.blocks;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockCleanProcError</p>
 * <p>Description: cleans process(dataset) error.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class BlockCleanProcError extends Block {
  public Port portIn;
  public Port portOut;

  public BlockCleanProcError(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    saveFlowState = true;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portOut;
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
   * Executes the actions in this block before the block is executed.
   * @return true if ok.
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * Checks if all is right to procced.
   * @param dataSet
   * @return true if it can proceed, false otherwise.
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the actions after this block is executed.
   * @param dataSet
   * @return the outPort.
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    String stmp = procData.getError();
    procData.clearError();
	Logger.debug(userInfo.getUtilizador(), this, "after", "error (subpid="
            + procData.getSubPid() + ", pid=" 
		 + procData.getPid() + ") cleaned: \"" + stmp + "\"");
    return portOut;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Clean Proc Error");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Proc Error Cleaned");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
