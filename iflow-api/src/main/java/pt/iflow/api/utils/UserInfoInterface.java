package pt.iflow.api.utils;

import java.sql.Connection;

import pt.iflow.api.db.DBConnectionWrapper;
import pt.iflow.api.db.ExistingTransactionException;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.userdata.UserData;

public interface UserInfoInterface {

	public abstract String getMainPageHTML();

	public abstract String getFlowPageHTML();

	public abstract void setMainPageHTML(String mainPageHTML);

	public abstract void setFlowPageHTML(String flowPageHTML);

	public abstract void login(String asLogin, String asPassword);

	public abstract byte[] getPassword();
	
	public String getPasswordString(); 

	public abstract void sessionLogin(String asLogin, String asSessionId);

	public abstract void profileLogin(String asLogin, String asProfile);

	public abstract void updatePrivileges();
	
	public abstract void updateProfiles();

	public abstract boolean isLogged();

	public abstract boolean isOrgAdmin();
	
	public abstract boolean isSysAdmin();
	
	public abstract boolean isProcSupervisor(int flowId);
	
	public abstract boolean isUnitManager();
	
	public abstract boolean isGuest();

	public abstract ProcessData updateProcessData(ProcessData procData);
	
	/**
	 * Getter for utilizador
	 * @return _sUtilizador
	 */
	public abstract String getUtilizador();

	/**
	 * Getter for intranet session id
	 * @return _sIntranetSessionId
	 */
	public abstract String getIntranetSessionId();

	/**
	 * Getter for perfil
	 * @return _sPerfil
	 */
	public abstract String[] getProfiles();

	public abstract boolean hasProfile(String asProfile);

	/**
	 * Checks if there's errors in this user info
	 */
	public abstract boolean hasError();

	/**
	 * returns this user info's error
	 */
	public abstract String getError();

	/**
	 * Getter for nomeUtilizador
	 * @return NomeUtilizador
	 */
	public abstract String getUserFullName();

	public abstract String getCompanyName();

	public abstract String getCompanyID();

	public abstract String getMobileNumber();

	/**
	 * Getter for numEmpregado
	 * @return NumEmpregado
	 */
	public abstract String getUserId();

	public abstract String getOrgUnit();
	
	public abstract String getOrgUnitID();

	public abstract String getOrganization();

	/**
	 * Validate if all the user data needed to execute transactions exists.
	 * @return true if OK.
	 */
	public abstract boolean validate();

	public abstract String getUserInfo(String asAttrName);

	/**
	 *
	 * Getter method for _sFeedKey
	 *
	 * @return Returns the _sFeedKey.
	 */
	public abstract String getFeedKey();


	/**
	 * Test if the user password has expired and must be changed
	 * @return <code>true</code> if password is expired
	 */
	public abstract boolean isPasswordExpired();
	
	public abstract UserSettings getUserSettings();

  public abstract void reloadUserSettings();
  
  public abstract IMessages getMessages();
  
  public abstract void setCookieLang(String lang);
  
  public abstract UserData getUserData();
  
  public void reloadUserData();
  
  public boolean isManager();

  public String registerTransaction(DBConnectionWrapper conn) throws ExistingTransactionException;
  public void unregisterTransaction(String id) throws IllegalAccessException;
  public boolean inTransaction();
  public Connection getTransactionConnection();
}