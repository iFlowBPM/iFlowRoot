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

import java.util.Map;

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.userdata.common.GuestUserData;

public class UserInfoMock extends UserInfo {

  /** <code>SerialVersionUID</code> */
  private static final long serialVersionUID = -6110477580055794768L;

  UserInfoMock(OrganizationData orgData) {
    super();
    GuestUserData guestData = new GuestUserData();
    Map<String,String> userData = guestData.getData();
    userData.put(UserData.ORG_ID, orgData.getOrganizationId());
    userData.put(UserData.ORG_NAME, orgData.getName());
    this._userData = guestData;
  }

  @Override
  public void sessionLogin(String asLogin, String asSessionId) {
    if (this._bLogged) {
      // already logged
      return;
    }

    this._sUtilizador = null;
    this._sIntranetSessionId = null;
    this._sAuthProfile = null;

    Logger.trace("USER " + asLogin + " requesting LOGIN");

    AuthProfile ap = BeanFactory.getAuthProfileBean();

    String sUsername = ap.fixUsername(asLogin);
    Logger.trace("Username fixed to: " + sUsername);
    try {
      if (asLogin.equals(Const.GUEST_NAME) && asSessionId.equals(Const.GUEST_SESSION)) {
        this._bLogged = true;
      } else {
        this._bLogged = ap.authenticateIntranetUser(sUsername, asSessionId);
      }
      if (!this._bLogged) {
        this._sError = "Sess&atilde;o inv&aacute;lida.";
        return;
      }
      this._sError = null;
      this._sUtilizador = sUsername;
      this._sIntranetSessionId = asSessionId;
    } catch (Throwable e) {
      this._sError = "Ocorreu um erro ao tentar fazer o loggin";
      this._bLogged = false;
      Logger.error(sUsername, this, "login", "Exception: "+ e.getMessage(), e);
    }
  }

  @Override
  public boolean isGuest() {
    return true;
  }
}
