package pt.iflow.utils;

import java.util.Calendar;

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

class SystemUserInfo extends UserInfo implements UserInfoInterface {

  @Override
	public void updateProfiles() {
	  // DO NOTHING !!!
	  // SYS ADMIN HAS NO PROFILES !!
	}

/**
  *
  */

 private static final long serialVersionUID = 2L;


	@Override
	public boolean isOrgAdmin() {
		return this._bIsOrgAdmin;
	}

	@Override
	public boolean isSysAdmin() {
		return this._bIsSysAdmin;
	}

	@Override
	protected void loadUserInfo(String asLogin, AuthProfile ap1)
			throws Exception {
	    AuthProfile ap = BeanFactory.getAuthProfileBean();
	    
	    if (ap1 != null)
	      ap = ap1;

	    this._userData = ap.getSystemUserInfo(this._sUtilizador);

	    Logger.debug(asLogin,this,"login","UTILIZADOR: "          + this._sUtilizador);
	    Logger.debug(asLogin,this,"login","INTRANET SESSION ID: " + this._sIntranetSessionId);
	    Logger.debug(asLogin,this,"login","INTRANET PROFILE: "    + this._sAuthProfile);
	    Logger.debug(asLogin,this,"login","NOMEUTILIZADOR: "      + this.getUserFullName());
	    Logger.debug(asLogin,this,"login","NUMEMPREGADO: "        + this.getUserId());
	    Logger.debug(asLogin,this,"login","ORG ID: "        + this.getOrganization());
	}

	  /* (non-Javadoc)
	 * @see pt.iknow.utils.UserInfoInterface#login(java.lang.String, java.lang.String)
	 */
	public void login(String asLogin, String asPassword) {
	    this.login(asLogin, asPassword, null);
	  }

	  /* (non-Javadoc)
	 * @see pt.iknow.utils.UserInfoInterface#sessionLogin(java.lang.String, java.lang.String)
	 */
	public void sessionLogin(String asLogin, String asSessionId) {
	    this.login(asLogin, null, asSessionId);
	  }

	  /* (non-Javadoc)
	 * @see pt.iknow.utils.UserInfoInterface#profileLogin(java.lang.String, java.lang.String)
	 */
	public void profileLogin(String asLogin, String asProfile) {
	    this.login(asLogin, null, null);
	  }

	
	  private void login(String asLogin,
		      String asPassword,
		      String asSessionId) {
	        this.password = new PasswordGuardian(asPassword.getBytes(), Calendar.getInstance().getTimeInMillis());
		    if (this._bLogged) {
		      // already logged
		      return;
		    }

		    this._sUtilizador          = null;
		    this._sIntranetSessionId   = null;
		    this._sAuthProfile         = null;


		    Logger.trace("SYSTEM USER " + asLogin + " requesting LOGIN");

		    AuthProfile ap = BeanFactory.getAuthProfileBean();

		    try {
		      if (asPassword != null) {
		        this._bLogged = ap.checkSystemUser(asLogin, asPassword);
		        if (!this._bLogged) {
		          this._sError = "User/Password inv&aacute;lidos.";
		        }
		      }
		      else if (asSessionId != null) {
		        this._bLogged = ap.authenticateIntranetUser(asLogin, asSessionId);
		        if (!this._bLogged) {
		          this._sError = "Sess&atilde;o inv&aacute;lida.";
		        }
		      }
		      else {
		        // oops
		        this._sError = "Dados insuficientes para efectuar o login.";
		        return;
		      }

		      if (!this._bLogged) {
		        return;
		      }

		      this._sError = null;

		      this._sUtilizador          = asLogin;
		      this._sIntranetSessionId   = asSessionId;
		      this._sAuthProfile         = "NONE";
		      this._sFeedKey = Utils.encrypt(asLogin + "#" + asPassword);

		      this.loadUserInfo(asLogin, ap);

		      this.updatePrivileges();

		    }
		    catch (Exception e) {
		      Logger.error(asLogin,this,"login","Exception: " + e.getMessage());
		      e.printStackTrace();
		      this._sError = "Ocorreu um erro ao tentar fazer o loggin";
		      this._bLogged = false;
		    }
		  }

	  /* (non-Javadoc)
	   * @see pt.iknow.utils.UserInfoInterface#updatePrivileges()
	   */
	  public void updatePrivileges() {

		  this._bIsOrgAdmin = false;
		  this._bIsSysAdmin = true;
		  
	    }

	  @Override
	  public boolean isPasswordExpired() {
	    return super.isPasswordExpired();
	  }

}
