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

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.events.AbstractEvent;
import pt.iflow.api.utils.Logger;

/**
 * <p>Title: UserInfoEvent</p>
 * <p>Description: Stores all the info about a logged user. The info is retrived on construction from LDAP.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author iKnow
 * @version 1.0
 */

class UserInfoEvent extends UserInfo {

  private static final long serialVersionUID = 1L;

  /**
   * Guest constructor
   */
  UserInfoEvent(Object cClass, String userId) {
    super();

    try {
      if (!(cClass instanceof AbstractEvent)) {
        throw new Exception("object is not of type event");
      }

      _bUserEvent = true;
      this._bLogged     = true;
      this._sUtilizador = userId;
      this._bIsOrgAdmin = true;
      AuthProfile ap = BeanFactory.getAuthProfileBean();
      loadUserInfo(userId, ap);
    } catch(Exception e) {
      Logger.warning("iFlow", this, "UserInfoEvent", "Ilegal Access");
      e.printStackTrace();
    }
  }

}
