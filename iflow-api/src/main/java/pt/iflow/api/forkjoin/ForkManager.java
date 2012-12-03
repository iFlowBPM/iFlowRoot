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
package pt.iflow.api.forkjoin;

import java.util.ListIterator;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;


public class ForkManager extends Thread {

  UserInfoInterface _userInfo = null;
  ProcessData _procData = null;

  private ForkManager(UserInfoInterface userInfo, ProcessData procData) {
    _userInfo = userInfo;
    _procData = procData;
  }

  public static boolean registerSubProc(UserInfoInterface userInfo, ProcessData threadProcData, Block block) {
    boolean retObj = false;
    String userid = userInfo.getUtilizador();

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      pm.createSubProcess(userInfo, threadProcData, block.getId(), block.getDescription(userInfo, threadProcData));

      ForkManager forkManager = new ForkManager(userInfo, threadProcData);
      forkManager.start();

      retObj = true;
    } catch (Exception e) {
      Logger.error(userid, null, "registerSubProc", 
          "caught exception: " + e.getMessage());
      e.printStackTrace();
    } 

    return retObj;
  }

  public void run() {
    String userid = this._userInfo.getUtilizador();

    try {
      int flowid = this._procData.getFlowId();
      int pid    = this._procData.getPid();
      int subpid = this._procData.getSubPid();

      Flow flow = BeanFactory.getFlowBean();

      // follows the outPortSubProc path
      flow.nextBlock(this._userInfo, this._procData, true);

      // gets the current block
      Block block = flow.getBlock(this._userInfo, this._procData);
      if (block != null) {
        ProcessManager pm = BeanFactory.getProcessManagerBean();

        ListIterator<Activity> liActv = pm.getProcessActivities(this._userInfo, flowid, pid, subpid);

        if (liActv != null && liActv.hasNext()) {
          while (liActv.hasNext()) {
            Activity a = liActv.next();
            a.status = 1;
            a.description = block.getDescription(this._userInfo, this._procData);
            a.url = Block.getDefaultUrl(this._userInfo, this._procData);
            a.mid = this._procData.getMid();
            pm.updateActivity(this._userInfo, a);
          }

        } else {

          // build an activity in that block
          Activity a = new Activity(userid, 
              this._procData.getFlowId(),
              this._procData.getPid(),
              this._procData.getSubPid(),
              0,
              0,
              block.getDescription(this._userInfo, this._procData), 
              Block.getDefaultUrl(this._userInfo, this._procData));
          a.mid = this._procData.getMid();
          
          
          boolean createPriv = flow.checkUserSelfFlowRoles(this._userInfo, 
              this._procData.getFlowId(), "" + FlowRolesTO.CREATE_PRIV);

          try {
            // create the activity and notify the user
            pm.createActivity(this._userInfo, a, createPriv);
          } catch (Exception e) {
            Logger.error(userid, null, "registerSubProc", 
                _procData.getSignature() + "caught exception: " + e.getMessage(), e);
          }
        }
      }

    } catch (Exception e) {
      Logger.error(userid, null, "registerSubProc", 
          _procData.getSignature() + "caught exception: " + e.getMessage(), e);
    } 
    // TODO fazer a tabela maravilha!
    // informacao para a tabela e mail para o admin
  }
}
