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
package pt.iflow.utils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: UserInfoDelegate</p>
 * <p>Description: Stores all the info about a logged user. The info is retrived on construction from LDAP.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author iKnow
 * @version 1.0
 */

class UserInfoDelegate extends UserInfo {

  private static final long serialVersionUID = 1L;

  /**
   * Guest constructor
   */
  UserInfoDelegate(Object object, String user) {
    super();

    try {
      if (object instanceof Block) {
        BlockLogin(user);
      }
      else if (object instanceof UserInfoInterface) {
        SupervisorLogin((UserInfoInterface)object, user);
      }
      else {
        throw new Exception("object type does not allow delegation user");
      }
    } 
    catch(Exception e) {
      Logger.warning(user, this, "UserInfoDelegate", "Illegal Access (" + e.getMessage() + ")");
      e.printStackTrace();
      this._bLogged = false;
      this._sUtilizador = null;
      this._userData = null;
      this._hsProfiles = null;
      this._bLogged = false;			 
    }
  }

  private void BlockLogin(String user) throws Exception {
    this._bLogged = true;
    this._sUtilizador = user;
    this.loadUserInfo(user, BeanFactory.getAuthProfileBean());
  }

  private void SupervisorLogin(UserInfoInterface supervisor, String user) throws Exception {
    this._bLogged = true;
    this._sUtilizador = user;
    this.loadUserInfo(user, BeanFactory.getAuthProfileBean());

    if (!supervisor.isSysAdmin() && !supervisor.getOrganization().equals(this.getOrganization())) {
      throw new Exception("user belongs to another organization and is not sysadmin");
    }
  }

}
