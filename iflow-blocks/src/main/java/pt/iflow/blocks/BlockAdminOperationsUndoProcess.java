/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.blocks;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AdministrationProcessManager;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockAdminOperationsUndoProcess extends BlockAdminOperations {

  public BlockAdminOperationsUndoProcess(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
  }

  /**
   * Executes the block main action
   * 
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portTrue;

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

      String flow_stateStr = procData.getFormatted(this.getAttribute("process_flow_state"));
      if (flow_stateStr == null) {
        flow_stateStr = this.getAttribute("process_flow_state");
      }
      String midStr = procData.getFormatted(this.getAttribute("process_mid"));
      if (midStr == null) {
        midStr = this.getAttribute("process_mid");
      }

      if (flow_stateStr != null && midStr != null) {
        int flow_state = Integer.parseInt(flow_stateStr);
        int mid = Integer.parseInt(midStr);
        Logger.debug(login, this, "after", procData.getSignature() + "Attempting to undo process with flowid [" + flowidStr
            + "], pid [" + pidStr + "], subpid [" + subpidStr + "], to flowstate [" + flow_stateStr + "], and mid [" + mid
            + "]");

        AdministrationProcessManager admProcessManager = BeanFactory.getAdministrationProcessManagerBean();

        boolean registerTransaction = false;
        if (!admProcessManager.undoProcess(userInfo, flowid, pid, subpid, flow_state, mid, registerTransaction)) {
          outPort = portFalse;
        } else {
          outPort = portTrue;
        }
      } else {
        Logger.error(login, this, "after", procData.getSignature() + "Unable to find parameters flow_state and/or mid");
        outPort = portFalse;
      }

    } catch (NumberFormatException e) {
      Logger.error(login, this, "after", procData.getSignature() + "caught NumberFormatException: invalid entry parameter flowid ["
          + flowidStr + "], pid [" + pidStr + "], subpid [" + subpidStr + "], final state [" + final_stateStr + "]", e);
      outPort = portFalse;
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
