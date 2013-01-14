package pt.iflow.blocks;

import java.util.Iterator;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockAdminOperations extends Block {
  public Port portIn, portTrue, portFalse;

  private final static String OPERATION_CANCEL_PROCESS = "cancel_process";
  private final static String OPERATION_UNDO_HIST = "undo_hist";
  private final static String OPERATION_RESCHEDULE = "reschedule";
  
  protected int operationFlowId = -1;
  protected int operationPid = -1;
  protected int operationSubPid = -1;

  public BlockAdminOperations(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
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
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = null;

    String login = userInfo.getUtilizador();

    String flowidStr = null;
    String pidStr = null;
    String subpidStr = null;
    String final_stateStr = null;
    try {
      String operation = this.getAttribute("operation");

      flowidStr = this.getAttribute("flowid");
      pidStr = this.getAttribute("pid");
      subpidStr = this.getAttribute("subpid");

      int flowid = Integer.parseInt(flowidStr);
      int pid = Integer.parseInt(pidStr);
      int subpid = Integer.parseInt(subpidStr);

      ProcessManager pm = BeanFactory.getProcessManagerBean();

      if (OPERATION_CANCEL_PROCESS.equals(operation)) {
        Logger.debug(login, this, "after", procData.getSignature() + "Attempting to cancel process with flowid [" + flowidStr
            + "], pid [" + pidStr + "], subpid [" + subpidStr + "]");
        ProcessData processData = pm.getProcessDataToBlock(userInfo, flowid, pid, subpid);

        Flow flow = BeanFactory.getFlowBean();
        boolean endProcessResult = flow.endProccessInBlockAdministration(userInfo, processData);

        if (!endProcessResult){
          outPort = portFalse;
        }

      } else if (OPERATION_UNDO_HIST.equals(operation)) {
        final_stateStr = this.getAttribute("final_state");

        Logger.debug(login, this, "after", procData.getSignature() + "Attempting to cancel process with flowid [" + flowidStr
            + "], pid [" + pidStr + "], subpid [" + subpidStr + "], final state [" + final_stateStr + "]");

        boolean undoProcessResult = false;

        Flow flow = BeanFactory.getFlowBean();
        //undoProcessResult = flow.undoProcess(userInfo, flowid, pid, subpid, fi, mid, exit_flag);

        if (!undoProcessResult){
          outPort = portFalse;
        }

      } else if (OPERATION_RESCHEDULE.equals(operation)) {
        String user = this.getAttribute("user");

        if (user != null && !"".equals(user)){
          Logger.debug(login, this, "after", procData.getSignature() + "Attempting to cancel process with flowid [" + flowidStr
              + "], pid [" + pidStr + "], subpid [" + subpidStr + "], user [" + user + "]");

          user = user.trim();

          Iterator<Activity> it = pm.getProcessActivities(userInfo, flowid, pid, subpid);
          Activity activity = null;

        } else {
          Logger.error(login, this, "after", procData.getSignature() + "Invalid user for operation [" + flowidStr
              + "], pid [" + pidStr + "], subpid [" + subpidStr + "], user [" + user + "]");
        }

        
      }

      outPort = portTrue;
    } catch (NumberFormatException e) {
      Logger.error(login, this, "after", procData.getSignature() + "caught NumberFormatException: invalid entry parameter flowid ["
          + flowidStr + "], pid [" + pidStr + "], subpid [" + subpidStr + "], final state [" + final_stateStr + "]", e);
    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portFalse;
    }

    this.addToLog("Using '" + outPort.getName() + "';");
    this.saveLogs(userInfo, procData, this);

    return outPort;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "AdminOperations");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "AdminOperations Efectuada");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}