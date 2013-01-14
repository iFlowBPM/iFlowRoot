package pt.iflow.blocks;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AdministrationProcessManager;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockAdminOperationsTerminateProcess extends BlockAdminOperations {

  public BlockAdminOperationsTerminateProcess(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
  }

  /**
   * Executes the block main action
   * 
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portTrue;;

    String login = userInfo.getUtilizador();

    String flowidStr = null;
    String pidStr = null;
    String subpidStr = null;
    String final_stateStr = null;
    try {
      flowidStr = procData.getFormatted(this.getAttribute("flowid"));
      if (flowidStr == null) {
        flowidStr = this.getAttribute("flowid");
      }
      pidStr = procData.getFormatted(this.getAttribute("pid"));
      if (pidStr == null) {
        pidStr = this.getAttribute("pid");
      }
      subpidStr = procData.getFormatted(this.getAttribute("subpid"));
      if (subpidStr == null) {
        subpidStr = this.getAttribute("subpid");
      }

      int flowid = Integer.parseInt(flowidStr);
      int pid = Integer.parseInt(pidStr);
      int subpid = Integer.parseInt(subpidStr);

      Logger.debug(login, this, "after", procData.getSignature() + "Attempting to cancel process with flowid [" + flowidStr
          + "], pid [" + pidStr + "], subpid [" + subpidStr + "]");

      AdministrationProcessManager admProcessManager =  BeanFactory.getAdministrationProcessManagerBean();

      if (!admProcessManager.terminateProcess(userInfo, flowid, pid, subpid)){
        outPort = portFalse;
      }

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
    return this.getDesc(userInfo, procData, true, "AdminOperationsTerminateProcess");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "AdminOperationsTerminateProcess Efectuada");
  }
}