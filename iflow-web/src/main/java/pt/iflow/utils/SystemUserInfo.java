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
