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
package pt.iflow.services;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.events.EventManager;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * 
 * <p>
 * Title: EventManagerRemote
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author ptgm
 * @version 1.0
 */
public class EventManagerRemote {

  public boolean deRegisterEvent(String user, String password, int fid, int pid, int subpid, int blockid) throws Exception {
    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "deRegisterEvent", "Wrong user/password!");
      return false;
    }
    EventManager ev = EventManager.get();
    return ev.deRegisterEvent(ui, fid, pid, subpid, blockid);
  }

  public boolean registerEvent(String user, String password, int fid, int pid, int subpid, int blockid, String type,
      String properties) throws Exception {
    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "registerEvent", "Wrong user/password!");
      return false;
    }
    EventManager ev = EventManager.get();
    return ev.registerEvent(ui, fid, pid, subpid, blockid, type, properties);
  }

  public boolean setReadyToProcess(String user, String password, int fid, int pid, int subpid, int blockid) throws Exception {
    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "setReadyToProcess", "Wrong user/password!");
      return false;
    }
    EventManager ev = EventManager.get();
    return ev.setReadyToProcess(fid, pid, subpid, blockid);
  }
}
