package pt.iflow.blocks;

import java.sql.Timestamp;
import java.util.Calendar;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.transition.ReportTO;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: Block Start</p>
 * <p>Description: Implements the start block.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author João Valentim
 * @version 1.0
 */

public class BlockStart extends Block {

  public Port portOut;
  private static final String CONST_START = "const.start";

  public BlockStart(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
  }

  public boolean isStartBlock () {
    return true;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
      return null;
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
    // in this block, before is NEVER CALLED!!!
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
    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    Timestamp current = new Timestamp(Calendar.getInstance().getTimeInMillis());
    procData.storeReport(new ReportTO(flowid, pid, subpid, CONST_START, current));
    return portOut;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Processo Iniciado");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
