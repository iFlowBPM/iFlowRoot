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
