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

import pt.iflow.api.blocks.Block;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;


public class JoinManager extends Thread {

  UserInfoInterface _userInfo = null;
  ProcessData _procData = null;

  private JoinManager(UserInfoInterface userInfo, ProcessData procData) {
    _userInfo = userInfo;
    _procData = procData;
  }

  public static boolean registerSubProc(UserInfoInterface userInfo, ProcessData procData, Block block) {

    String userid = userInfo.getUtilizador();

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      pm.createSubProcess(userInfo, procData, block.getId(), block.getDescription(userInfo, procData));

      JoinManager forkManager = new JoinManager(userInfo, procData);
      forkManager.start();
    } catch (Exception e) {
      Logger.error(userid, null, "registerSubProc", "caught exception: " + e.getMessage(), e);
    } 

    return true;
  }

  public void run() {

  }
}
