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
