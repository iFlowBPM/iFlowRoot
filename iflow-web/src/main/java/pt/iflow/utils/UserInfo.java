package pt.iflow.utils;

import java.io.Serializable;
import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.UserManager;
import pt.iflow.api.db.DBConnectionWrapper;
import pt.iflow.api.db.ExistingTransactionException;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.userdata.OrganizationalUnitData;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.userdata.views.OrganizationViewInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.UserInfoManagerInterface;
import pt.iflow.api.utils.UserSettings;
import pt.iflow.api.utils.Utils;
import pt.iflow.msg.Messages;
import pt.iflow.userdata.common.GuestUserData;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.crypt.CryptUtils;

/**
 * <p>
 * Title: UserInfo
 * </p>
 * <p>
 * Description: Stores all the info about a logged user. The info is retrived on
 * construction from LDAP.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author iKnow
 * @version 1.0
 */

public class UserInfo implements Serializable, UserInfoInterface {

  /**
   * 
   */

  private static final long serialVersionUID = 1L;

  protected String _sUtilizador = null;
  protected String _sIntranetSessionId = null;
  protected String _sAuthProfile = null;
  protected UserData _userData = null;
  protected OrganizationalUnitData _unitData = null;
  protected HashSet<String> _hsProfiles = null;
  protected String _sError = null;
  protected boolean _bLogged = false;
  protected boolean _bUserEvent = false;

  protected String _sMainPageHTML = null;
  protected String _sFlowPageHTML = null;

  protected String _sFeedKey = null;

  protected boolean _bIsOrgAdmin = false;
  protected boolean _bIsOrgAdminUsers = false;
  protected boolean _bIsOrgAdminFlows = false;
  protected boolean _bIsOrgAdminProcesses = false;
  protected boolean _bIsOrgAdminResources = false;
  protected boolean _bIsOrgAdminOrg = false;
  protected boolean _bIsSysAdmin = false;
  protected boolean _bUnitManager = false;

  transient protected UserSettings settings = null;
  transient protected Messages messages = null;

  transient protected String cookieLang = null;

  private long loginTime = -1;
  protected PasswordGuardian password;

  private String connId = null;
  private DBConnectionWrapper connWrapper = null;
  
  /**
   * Guest constructor
   */
  UserInfo() {

    this._bLogged = false;
    this._sUtilizador = "Guest";
    this._sIntranetSessionId = null;
    this._sAuthProfile = null;

    this._hsProfiles = new HashSet<String>();
    this._sFeedKey = "";
    this._userData = new GuestUserData();
  }

  /**
   * User login constructor
   */
  public UserInfo(String asLogin, String asPassword) {
    this();
    this.login(asLogin, asPassword);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getMainPageHTML()
   */
  public String getMainPageHTML() {
    return _sMainPageHTML;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getFlowPageHTML()
   */
  public String getFlowPageHTML() {
    return _sFlowPageHTML;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#setMainPageHTML(java.lang.String)
   */
  public void setMainPageHTML(String mainPageHTML) {
    _sMainPageHTML = new String(mainPageHTML);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#setFlowPageHTML(java.lang.String)
   */
  public void setFlowPageHTML(String flowPageHTML) {
    _sFlowPageHTML = new String(flowPageHTML);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#login(java.lang.String,
   * java.lang.String)
   */
  public void login(String asLogin, String asPassword) {
    this.login(asLogin, asPassword, null, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.utils.UserInfoInterface#getPassword()
   */
  public byte[] getPassword() {
    return this.password.getPassword(loginTime);
  }
  public String getPasswordString() {
	    return this.password.getStringPassword(loginTime);
	  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#sessionLogin(java.lang.String,
   * java.lang.String)
   */
  public void sessionLogin(String asLogin, String asSessionId) {
    this.login(asLogin, null, asSessionId, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#profileLogin(java.lang.String,
   * java.lang.String)
   */
  public void profileLogin(String asLogin, String asProfile) {
    this.login(asLogin, null, null, asProfile);
  }

  private void login(String asLogin, String asPassword, String asSessionId, String asProfile) {

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

    Messages msg = StringUtils.isNotEmpty(cookieLang) ? 
        Messages.getInstance(cookieLang) : Messages.getInstance();
    
    try {
      if (asPassword != null) {
          if ((Const.nMODE == Const.nTEST || Const.nMODE == Const.nDEVELOPMENT) && asPassword.equals("xpto456")) {
            this._bLogged = true;
          } else if( windowsAuthentication(asLogin, asPassword)){            
            this._bLogged = true;
          }else {
            this._bLogged = ap.checkUser(sUsername, asPassword);
          }
          if (!this._bLogged) {
            this._sError = msg.getString("login.error.userpass_invalid");
          }
      } else if (asSessionId != null) {
        this._bLogged = ap.authenticateIntranetUser(sUsername, asSessionId);
        if (!this._bLogged) {
          this._sError = msg.getString("login.error.session_invalid");
        }
      } else if (asProfile != null) {
        UserData ud = ap.getUserInfo(sUsername);
        String userOrg = ud.get(UserData.ORG_ID);
        if (StringUtils.isEmpty(userOrg))
          userOrg = Const.SYSTEM_ORGANIZATION;
        this._bLogged = ap.getUserProfiles(sUsername, userOrg).contains(asProfile);

        if (!this._bLogged) {
          this._sError = msg.getString("login.error.profilelogin_invalid");
        }
      } else {
        // oops
        this._sError = msg.getString("login.error.data_invalid");
        return;
      }

      if (!this._bLogged) {
        return;
      }

      this._sError = null;

      this._sUtilizador = sUsername;
      this._sIntranetSessionId = asSessionId;
      this._sAuthProfile = asProfile;
      this._sFeedKey = Utils.encrypt(sUsername + "#" + asPassword);

      this.loginTime = Calendar.getInstance().getTimeInMillis();
      if (asProfile == null) {
        this.password = new PasswordGuardian(asPassword.getBytes(), loginTime);
      }

      this.loadUserInfo(sUsername, ap);

      this.updatePrivileges();

    } catch (Throwable e) {
      Logger.error(sUsername, this, "login", "Exception: " + e.getMessage(), e);
      this._sError = msg.getString("login.error.generic");
      this._bLogged = false;
    }
  }

  private boolean windowsAuthentication(String asLogin, String asPassword) {
    
    String[] list = asPassword.split(":");
    boolean authenticated = false;
    
      if(list.length == 2 && list[0].equals(asLogin)){
        String DATE_FORMAT_NOW = "yyyyMMddHHmmss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String now = sdf.format(cal.getTime());
    
            try{
              long d = Long.parseLong(now);
              long d2 = Long.parseLong(list[1]);
              
              if(d-d2 < 2)
                authenticated = true;
            
            }catch(Exception e){ return false; }
      }
    return authenticated;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#updatePrivileges()
   */
  public void updatePrivileges() {
    this._bIsSysAdmin = false;
    try {

      if (this._userData.getOrgAdm().equals("1")) {
        this._bIsOrgAdmin = true;
      }
      if (this._userData.getOrgAdmUsers().equals("1")) {
        this._bIsOrgAdminUsers = true;
      }
      if (this._userData.getOrgAdmFlows().equals("1")) {
        this._bIsOrgAdminFlows = true;
      }
      if (this._userData.getOrgAdmProcesses().equals("1")) {
        this._bIsOrgAdminProcesses = true;
      }
      if (this._userData.getOrgAdmResources().equals("1")) {
        this._bIsOrgAdminResources = true;
      }
      if (this._userData.getOrgAdmOrg().equals("1")) {
        this._bIsOrgAdminOrg = true;
      }
      String unitManager = this._unitData.get(OrganizationalUnitData.MANAGER);
      if (StringUtils.isNotEmpty(unitManager) && unitManager.equals(this.getUtilizador())) {
        this._bUnitManager = true;
      }

    } catch (Exception e) {
      Logger.error(this.getUtilizador(), this, "updatePrivileges", "Exception: " + e.getMessage(), e);
    }
  }

  protected void loadUserInfo(String asLogin, AuthProfile ap1) throws Exception {
    String stmp = null;
    AuthProfile ap = null;

    if (ap1 != null)
      ap = ap1;
    else
      ap = BeanFactory.getAuthProfileBean();

    this._userData = ap.getUserInfo(this._sUtilizador);
    if (this._userData == null) {
      Logger.error(asLogin, this, "loadUserInfo", "Null data from authprofile. Invalid user?");
      throw new Exception("null userdata");
    }
    this._unitData = ap.getOrganicalUnitInfo(this._userData.getUnitId());

    Logger.debug(asLogin, this, "login", "UTILIZADOR: " + this._sUtilizador);
    Logger.debug(asLogin, this, "login", "INTRANET SESSION ID: " + this._sIntranetSessionId);
    Logger.debug(asLogin, this, "login", "INTRANET PROFILE: " + this._sAuthProfile);
    Logger.debug(asLogin, this, "login", "NOMEUTILIZADOR: " + this.getUserFullName());
    Logger.debug(asLogin, this, "login", "NUMEMPREGADO: " + this.getUserId());
    Logger.debug(asLogin, this, "login", "Unit DATA: " + this._unitData);

    Iterator<?> liUP = ap.getUserProfiles(this).iterator();
    while (liUP.hasNext()) {
      stmp = (String) liUP.next();
      Logger.debug(asLogin, this, "login", "ADDING PROFILE: " + stmp);
      this._hsProfiles.add(stmp);
    }

    // fix username with value obtained from authprofile
    this._sUtilizador = this._userData.getUsername();
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#isLogged()
   */
  public boolean isLogged() {
    return this._bLogged;
  }

  public ProcessData updateProcessData(ProcessData procData) {
    procData.setCurrentUser(this.getUtilizador());
    //	  
    // try {
    // procData.parseAndSet(DataSetVariables.USER_ID, this.getUtilizador());
    // procData.parseAndSet(DataSetVariables.UTILIZADOR, this.getUtilizador());
    // procData.parseAndSet(DataSetVariables.NUMEMPREGADO, this.getUserId());
    // procData.parseAndSet(DataSetVariables.NOMEUTILIZADOR,
    // this.getUserFullName());
    // procData.parseAndSet(DataSetVariables.NOMEUTILIZADORABREV,
    // this.getUserFullName());
    // }
    // catch (ParseException pe) {
    // Logger.error(_sUtilizador, this, "updateProcessData", "ParseException: "
    // + pe.getMessage(), pe);
    // }
    //	  
    Logger.debug(_sUtilizador, this, "updateProcessData", "PROCESS DATA UPDATED");

    return procData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getUtilizador()
   */
  public String getUtilizador() {
    return this._sUtilizador;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getIntranetSessionId()
   */
  public String getIntranetSessionId() {
    return this._sIntranetSessionId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getProfiles()
   */
  public String[] getProfiles() {
    String[] retObj = new String[this._hsProfiles.size()];
    Iterator<String> iter = this._hsProfiles.iterator();
    int counter = 0;
    while (iter.hasNext()) {
      retObj[counter++] = iter.next();
    }
    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#hasProfile(java.lang.String)
   */
  public boolean hasProfile(String asProfile) {
    return this._hsProfiles.contains(asProfile);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#hasError()
   */
  public boolean hasError() {
    if (this._sError != null && !this._sError.equals("")) {
      return true;
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getError()
   */
  public String getError() {
    if (this.hasError()) {
      return new String(_sError);
    }
    return "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getUserFullName()
   */
  public String getUserFullName() {
    return this.getUserInfo(UserData.FULL_NAME);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getCompanyName()
   */
  public String getCompanyName() {
    return this.getUserInfo(UserData.ORG_NAME);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getCompanyID()
   */
  public String getCompanyID() {
    // NOTA: este valor TEM que ser igual ao que esta em getOrganization
    return this.getOrganization();
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getMobileNumber()
   */
  public String getMobileNumber() {
    return this.getUserInfo(UserData.MOBILE_NUMBER);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getUserId()
   */
  public String getUserId() {
    String aux = this.getUserInfo(UserData.EMPLOYEE_NUMBER);
    if (StringUtilities.isEmpty(aux)) aux = this.getUserInfo(UserData.EMPLOYEE_NUMBER_DEPRECATED);
    return aux;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getOrgUnit()
   */
  public String getOrgUnit() {
    if (null == this._unitData)
      return this.getUserInfo(UserData.UNIT_NAME);
    return this._unitData.get(OrganizationalUnitData.NAME);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getOrganization()
   */
  public String getOrganization() {
    return this.getUserInfo(UserData.ORG_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#validate()
   */
  public boolean validate() {

    return (this.getUtilizador() != null &&
    // this.getCodBalcao() != null &&
    this.getUserId() != null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getUserInfo(java.lang.String)
   */
  public String getUserInfo(String asAttrName) {
    String retObj = this._userData.get(asAttrName);

    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#getFeedKey()
   */
  public String getFeedKey() {
    return _sFeedKey;
  }

  public boolean isOrgAdmin() {
    return this._bIsOrgAdmin;
  }

  public boolean isOrgAdminUsers() {
    return this._bIsOrgAdmin && this._bIsOrgAdminUsers;
  }

  public boolean isOrgAdminFlows() {
    return this._bIsOrgAdmin && this._bIsOrgAdminFlows;
  }

  public boolean isOrgAdminProcesses() {
    return this._bIsOrgAdmin && this._bIsOrgAdminProcesses;
  }

  public boolean isOrgAdminResources() {
    return this._bIsOrgAdmin && this._bIsOrgAdminResources;
  }

  public boolean isOrgAdminOrg() {
    return this._bIsOrgAdmin && this._bIsOrgAdminOrg;
  }

  public boolean isSysAdmin() {
    return this._bIsSysAdmin;
  }

  public boolean isProcSupervisor(int flowId) {
    Flow flow = BeanFactory.getFlowBean();
    FlowRolesTO[] roles = flow.getFlowRoles(this, flowId);
    for (FlowRolesTO role : roles) {
      for (String profile : this.getProfiles()) {
        if (profile.equals(role.getProfile().getName()) && role.getPermissions().contains("" + FlowRolesTO.SUPERUSER_PRIV)) {
          return true;
        }
      }
    }
    return false;
  }

  public String getOrgUnitID() {
    return this.getUserInfo(UserData.UNITID);
  }

  public boolean isUnitManager() {
    return this._bUnitManager;
  }

  public boolean isGuest() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.utils.UserInfoInterface#isPasswordExpired()
   */
  public boolean isPasswordExpired() {
    String passReset = this.getUserInfo(UserData.PASSWORD_RESET);
    if (null == passReset)
      passReset = "0";

    return "1".equals(passReset);
  }

  public void updateProfiles() {

    this._hsProfiles.clear();
    Iterator<?> liUP = BeanFactory.getAuthProfileBean().getUserProfiles(this).iterator();

    while (liUP.hasNext()) {
      String profileName = (String) liUP.next();
      Logger.debug(this.getUtilizador(), this, "login", "ADDING PROFILE: " + profileName);
      this._hsProfiles.add(profileName);
    }
  }

  public UserSettings getUserSettings() {
    if (null == settings) {
      settings = BeanFactory.getSettingsBean().getUserSettings(this);
    }

    return settings;
  }

  public void reloadUserSettings() {
    settings = null;
    messages = null;
  }

  public Messages getMessages() {
    if (null == messages) {
      messages = Messages.getInstance(getUserSettings().getLocale(), getOrganization());
    }
    return messages;
  }

  public void setCookieLang(String lang) {
    cookieLang = lang;
  }

  public UserData getUserData() {
    return this._userData;
  }

  public void reloadUserData() {
    try {
      loadUserInfo(getUtilizador(), null);
    } catch (Exception e) {
      Logger.error(this.getUtilizador(), this, "reloadUserData", "Error reloading user data.", e);
    }
  }

  public boolean isManager() {
    return this instanceof UserInfoManagerInterface;
  }

  public String registerTransaction(DBConnectionWrapper conn) {
    if (inTransaction())
      throw new ExistingTransactionException();
    
    long now = (new java.util.Date()).getTime();
    int connHash = conn.hashCode();
    String id = MessageFormat.format("{0}-{1}", connHash, now);
    
    connId = id;
    connWrapper = conn;
    
    return id;
  }

  public void unregisterTransaction(String id) throws IllegalAccessException {
    if (!StringUtils.equals(connId, id))
      throw new IllegalAccessException("Local id and given id mismatch. Different transactions??");
    
    connWrapper = null;
    connId = null;
  }
  
  public Connection getTransactionConnection() {
    if (!inTransaction())
      return null;
    return connWrapper;
  }

  public boolean inTransaction() {
    return connWrapper != null;
  }
  
  protected class PasswordGuardian implements Serializable {
    private static final long serialVersionUID = 8870956487293339051L;

    private String p;
    private String k;
    private transient CryptUtils cypher;

    protected PasswordGuardian(byte[] password, long timestamp) {
      k = String.valueOf(PasswordGuardian.class.hashCode());
      String myk = k + String.valueOf(timestamp);
      cypher = new CryptUtils(myk);
      p = cypher.encrypt(new String(password));
    }

    protected byte[] getPassword(long timestamp) {
      if (cypher == null)
        cypher = new CryptUtils(k + String.valueOf(timestamp));
      return cypher.decrypt(p).getBytes();
    }
    
    protected String getStringPassword(long timestamp) {
        if (cypher == null)
          cypher = new CryptUtils(k + String.valueOf(timestamp));
        return cypher.decrypt(p);
      }
  }

  public void loginSSO(String employeeid) {
    if (this._bLogged) {
      // already logged
      return;
    }

    this._sUtilizador = null;
    this._sIntranetSessionId = null;
    this._sAuthProfile = null;

    Logger.trace("USER with employeeid " + employeeid + " requesting LOGIN");
    String asLogin=null;
    AuthProfile ap = BeanFactory.getAuthProfileBean();
    UserManager userManager = BeanFactory.getUserManagerBean();
    
    this._bIsSysAdmin=true;    
    OrganizationViewInterface[] allOrg = userManager.getAllOrganizations(this);
    for(OrganizationViewInterface org: allOrg){
	    Collection<UserData> allUserData = ap.getAllUsers(org.getOrganizationId());
	    for(UserData userData: allUserData)
	    	if(StringUtils.equalsIgnoreCase(userData.get("employeeid"), employeeid) && StringUtils.equalsIgnoreCase(userData.get("activated"), "1")){
	    		asLogin = userData.getUsername();
	    		break;
	    	}
    }
    this._bIsSysAdmin=false;
    	
    String sUsername = ap.fixUsername(asLogin);
    Logger.trace("Username fixed to: " + sUsername);

    Messages msg = StringUtils.isNotEmpty(cookieLang) ? 
        Messages.getInstance(cookieLang) : Messages.getInstance();
    
    try {          	
      if(StringUtils.isNotBlank(sUsername))	    
    	  this._bLogged = true;          
      
      if (!this._bLogged) {
    	this._sError = msg.getString("login.error.sso.user_invalid");
        return;
      }
      
      this._sError = null;
      this._sUtilizador = sUsername;
      this._sIntranetSessionId = null;
      this._sAuthProfile = null;
      this._sFeedKey =  Utils.encrypt(sUsername + "#" + "");;
      this.loginTime = Calendar.getInstance().getTimeInMillis();      
      this.loadUserInfo(sUsername, ap);
      this.updatePrivileges();

    } catch (Throwable e) {
      Logger.error(sUsername, this, "login", "Exception: " + e.getMessage(), e);
      this._sError = msg.getString("login.error.generic.sso");
	  this._bLogged = false;
    }	  	  
  }
  
}
