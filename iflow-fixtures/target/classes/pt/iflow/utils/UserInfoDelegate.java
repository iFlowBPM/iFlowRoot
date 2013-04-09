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
