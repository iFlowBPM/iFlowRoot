package pt.iflow.blocks;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: Block SaveToDB</p>
 * <p>Description: Implements the end block.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class BlockSaveToDB extends Block {

  public Port portIn;
  public Port portOut;

  public BlockSaveToDB(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    bProcInDBRequired = true;
    saveFlowState = false;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[1];
      retObj[0] = portIn;
      return retObj;
  }
  
  public Port getEventPort() {
      return null;
  }
  
  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portOut;
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
    return portOut;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Save to DB");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Saved to DB");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
