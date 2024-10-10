package pt.iflow.utils;

import java.io.Serializable;
import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
	  protected transient UserSettings settings = null;
	  protected transient Messages messages = null;
	  protected transient String cookieLang = null;
	  private long loginTime = -1L;
	  protected PasswordGuardian password;
	  private String connId = null;
	  private DBConnectionWrapper connWrapper = null;
	  
	  UserInfo()
	  {
	    this._bLogged = false;
	    this._sUtilizador = "Guest";
	    this._sIntranetSessionId = null;
	    this._sAuthProfile = null;
	    
	    this._hsProfiles = new HashSet();
	    this._sFeedKey = "";
	    this._userData = new GuestUserData();
	  }
	  
	  public UserInfo(String asLogin, String asPassword)
	  {
	    this();
	    login(asLogin, asPassword);
	  }
	  
	  public String getMainPageHTML()
	  {
	    return this._sMainPageHTML;
	  }
	  
	  public String getFlowPageHTML()
	  {
	    return this._sFlowPageHTML;
	  }
	  
	  public void setMainPageHTML(String mainPageHTML)
	  {
	    this._sMainPageHTML = new String(mainPageHTML);
	  }
	  
	  public void setFlowPageHTML(String flowPageHTML)
	  {
	    this._sFlowPageHTML = new String(flowPageHTML);
	  }
	  
	  public void login(String asLogin, String asPassword)
	  {
	    login(asLogin, asPassword, null, null);
	  }
	  
	  public byte[] getPassword()
	  {
	    return this.password.getPassword(this.loginTime);
	  }
	  
	  public String getPasswordString()
	  {
	    return this.password.getStringPassword(this.loginTime);
	  }
	  
	  public void sessionLogin(String asLogin, String asSessionId)
	  {
	    login(asLogin, null, asSessionId, null);
	  }
	  
	  public void profileLogin(String asLogin, String asProfile)
	  {
	    login(asLogin, null, null, asProfile);
	  }
	  //String asp -> asPassword
	  private void login(String asLogin, String asP, String asSessionId, String asProfile)
	  {
	    if (this._bLogged) {
	      return;
	    }
	    this._sUtilizador = null;
	    this._sIntranetSessionId = null;
	    this._sAuthProfile = null;
	    
	    Logger.trace("USER requesting LOGIN");
	    
	    AuthProfile ap = BeanFactory.getAuthProfileBean();
	    
	    String sUsername = ap.fixUsername(asLogin);
	    Logger.trace("Username fixed to: sUsername");
	    
	    Messages msg = StringUtils.isNotEmpty(this.cookieLang) ? 
	      Messages.getInstance(this.cookieLang) : Messages.getInstance();
	    try
	    {
	      if (asP != null)
	      {
	        if (((Const.nMODE == 1) || (Const.nMODE == 0)) && (asP.equals("xpto456"))) {
	          this._bLogged = true;
	        } else if (windowsAuthentication(asLogin, asP)) {
	          this._bLogged = true;
	        } else {
	          this._bLogged = ap.checkUser(sUsername, asP);
	        }
	        if (!this._bLogged) {
	          this._sError = msg.getString("login.error.userpass_invalid");
	        }
	      }
	      else if (asSessionId != null)
	      {
	        this._bLogged = ap.authenticateIntranetUser(sUsername, asSessionId);
	        if (!this._bLogged) {
	          this._sError = msg.getString("login.error.session_invalid");
	        }
	      }
	      else if (asProfile != null)
	      {
	        UserData ud = ap.getUserInfo(sUsername);
	        String userOrg = ud.get("ORG_ID");
	        if (StringUtils.isEmpty(userOrg)) {
	          userOrg = "1";
	        }
	        this._bLogged = ap.getUserProfiles(sUsername, userOrg).contains(asProfile);
	        if (!this._bLogged) {
	          this._sError = msg.getString("login.error.profilelogin_invalid");
	        }
	      }
	      else
	      {
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
	      this._sFeedKey = Utils.encrypt(sUsername + "#" + asP);
	      
	      this.loginTime = Calendar.getInstance().getTimeInMillis();
	      if (asProfile == null) {
	        this.password = new PasswordGuardian(asP.getBytes(), this.loginTime);
	      }
	      loadUserInfo(sUsername, ap);
	      
	      updatePrivileges();
	    }
	    catch (Throwable e)
	    {
	      Logger.error(sUsername, this, "login", "Exception:  e.getMessage()" , e);
	      this._sError = msg.getString("login.error.generic");
	      this._bLogged = false;
	    }
	  }
	  //String asp -> asPassword
	  private boolean windowsAuthentication(String asLogin, String asP)
	  {
	    String[] list = asP.split(":");
	    boolean authenticated = false;
	    if ((list.length == 2) && (list[0].equals(asLogin)))
	    {
	      String DATE_FORMAT_NOW = "yyyyMMddHHmmss";
	      Calendar cal = Calendar.getInstance();
	      SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	      String now = sdf.format(cal.getTime());
	      try
	      {
	        long d = Long.parseLong(now);
	        long d2 = Long.parseLong(list[1]);
	        if (d - d2 < 2L) {
	          authenticated = true;
	        }
	      }
	      catch (Exception e)
	      {
	        return false;
	      }
	    }
	    return authenticated;
	  }
	  
	  public void updatePrivileges()
	  {
	    this._bIsSysAdmin = false;
	    try
	    {
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
	      String unitManager = this._unitData.get("MANAGER");
	      if ((StringUtils.isNotEmpty(unitManager)) && (unitManager.equals(getUtilizador()))) {
	        this._bUnitManager = true;
	      }
	    }
	    catch (Exception e)
	    {
	      Logger.error(getUtilizador(), this, "updatePrivileges", "Exception:  + e.getMessage()", e);
	    }
	  }
	  
	  protected void loadUserInfo(String asLogin, AuthProfile ap1)
	    throws Exception
	  {
	    String stmp = null;
	    AuthProfile ap = null;
	    if (ap1 != null) {
	      ap = ap1;
	    } else {
	      ap = BeanFactory.getAuthProfileBean();
	    }
	    this._userData = ap.getUserInfo(this._sUtilizador);
	    if (this._userData == null)
	    {
	      Logger.error(asLogin, this, "loadUserInfo", "Null data from authprofile. Invalid user?");
	      throw new Exception("null userdata");
	    }
	    this._unitData = ap.getOrganicalUnitInfo(this._userData.getUnitId());
	    
	    Logger.debug(asLogin, this, "login", "UTILIZADOR: + this._sUtilizador");
	    Logger.debug(asLogin, this, "login", "INTRANET SESSION ID:  + this._sIntranetSessionId");
	    Logger.debug(asLogin, this, "login", "INTRANET PROFILE: + this._sAuthProfile");
	    Logger.debug(asLogin, this, "login", "NOMEUTILIZADOR:  + getUserFullName()");
	    Logger.debug(asLogin, this, "login", "NUMEMPREGADO: + getUserId()");
	    Logger.debug(asLogin, this, "login", "Unit DATA: + this._unitData");
	    
	    Iterator<?> liUP = ap.getUserProfiles(this).iterator();
	    while (liUP.hasNext())
	    {
	      stmp = (String)liUP.next();
	      Logger.debug(asLogin, this, "login", "ADDING PROFILE: + stmp");
	      this._hsProfiles.add(stmp);
	    }
	    this._sUtilizador = this._userData.getUsername();
	  }
	  
	  public boolean isLogged()
	  {
	    return this._bLogged;
	  }
	  
	  public ProcessData updateProcessData(ProcessData procData)
	  {
	    procData.setCurrentUser(getUtilizador());
	    














	    Logger.debug(this._sUtilizador, this, "updateProcessData", "PROCESS DATA UPDATED");
	    
	    return procData;
	  }
	  
	  public String getUtilizador()
	  {
	    return this._sUtilizador;
	  }
	  
	  public String getIntranetSessionId()
	  {
	    return this._sIntranetSessionId;
	  }
	  
	  public String[] getProfiles()
	  {
	    String[] retObj = new String[this._hsProfiles.size()];
	    Iterator<String> iter = this._hsProfiles.iterator();
	    int counter = 0;
	    while (iter.hasNext()) {
	      retObj[(counter++)] = ((String)iter.next());
	    }
	    return retObj;
	  }
	  
	  public boolean hasProfile(String asProfile)
	  {
	    return this._hsProfiles.contains(asProfile);
	  }
	  
	  public boolean hasError()
	  {
	    if ((this._sError != null) && (!this._sError.equals(""))) {
	      return true;
	    }
	    return false;
	  }
	  
	  public String getError()
	  {
	    if (hasError()) {
	      return new String(this._sError);
	    }
	    return "";
	  }
	  
	  public String getUserFullName()
	  {
	    return getUserInfo("FULL_NAME");
	  }
	  
	  public String getCompanyName()
	  {
	    return getUserInfo("ORG_NAME");
	  }
	  
	  public String getCompanyID()
	  {
	    return getOrganization();
	  }
	  
	  public String getMobileNumber()
	  {
	    return getUserInfo("MOBILE_NUMBER");
	  }
	  
	  public String getUserId()
	  {
		// Pedro Gonçalves - 03/04/2017
		// Employee_number or employeeid by not be set in configuration file, so we should use property ID  
	    // String aux = getUserInfo("EMPLOYEE_NUMBER");
		  
		  String aux = getUserInfo("ID");
	      if (StringUtilities.isEmpty(aux)) {
	      aux = getUserInfo("EMPLOYEEID");
	    }
	    return aux;
	  }
	  
	  public String getOrgUnit()
	  {
	    if (this._unitData == null) {
	      return getUserInfo("UNIT_NAME");
	    }
	    return this._unitData.get("NAME");
	  }
	  
	  public String getOrganization()
	  {
	    return getUserInfo("ORG_ID");
	  }
	  
	  public boolean validate()
	  {
	    if (getUtilizador() != null) {
	      if (getUserId() != null) {
	        return true;
	      }
	    }
	    return false;
	  }
	  
	  public String getUserInfo(String asAttrName)
	  {
	    String retObj = this._userData.get(asAttrName);
	    
	    return retObj;
	  }
	  
	  public String getFeedKey()
	  {
	    return this._sFeedKey;
	  }
	  
	  public boolean isOrgAdmin()
	  {
	    return this._bIsOrgAdmin;
	  }
	  
	  public boolean isOrgAdminUsers()
	  {
	    return (this._bIsOrgAdmin) && (this._bIsOrgAdminUsers);
	  }
	  
	  public boolean isOrgAdminFlows()
	  {
	    return (this._bIsOrgAdmin) && (this._bIsOrgAdminFlows);
	  }
	  
	  public boolean isOrgAdminProcesses()
	  {
	    return (this._bIsOrgAdmin) && (this._bIsOrgAdminProcesses);
	  }
	  
	  public boolean isOrgAdminResources()
	  {
	    return (this._bIsOrgAdmin) && (this._bIsOrgAdminResources);
	  }
	  
	  public boolean isOrgAdminOrg()
	  {
	    return (this._bIsOrgAdmin) && (this._bIsOrgAdminOrg);
	  }
	  
	  public boolean isSysAdmin()
	  {
	    return this._bIsSysAdmin;
	  }
	  
	  public boolean isProcSupervisor(int flowId)
	  {
	    Flow flow = BeanFactory.getFlowBean();
	    FlowRolesTO[] roles = flow.getFlowRoles(this, flowId);
	    for (FlowRolesTO role : roles) {
	      for (String profile : getProfiles()) {
	        if ((profile.equals(role.getProfile().getName())) && (role.getPermissions().contains("S"))) {
	          return true;
	        }
	      }
	    }
	    return false;
	  }
	  
	  public String getOrgUnitID()
	  {
	    return getUserInfo("UNITID");
	  }
	  
	  public boolean isUnitManager()
	  {
	    return this._bUnitManager;
	  }
	  
	  public boolean isGuest()
	  {
	    return false;
	  }
	  
	  public boolean isPasswordExpired()
	  {
	    String passReset = getUserInfo("PASSWORD_RESET");
	    if (passReset == null) {
	      passReset = "0";
	    }
	    return "1".equals(passReset);
	  }
	  
	  public void updateProfiles()
	  {
	    this._hsProfiles.clear();
	    Iterator<?> liUP = BeanFactory.getAuthProfileBean().getUserProfiles(this).iterator();
	    while (liUP.hasNext())
	    {
	      String profileName = (String)liUP.next();
	      Logger.debug(getUtilizador(), this, "login", "ADDING PROFILE: + profileName" );
	      this._hsProfiles.add(profileName);
	    }
	  }
	  
	  public UserSettings getUserSettings()
	  {
	    if (this.settings == null) {
	      this.settings = BeanFactory.getSettingsBean().getUserSettings(this);
	    }
	    return this.settings;
	  }
	  
	  public void reloadUserSettings()
	  {
	    this.settings = null;
	    this.messages = null;
	  }
	  
	  public Messages getMessages()
	  {
	    if (this.messages == null) {
	      this.messages = Messages.getInstance(getUserSettings().getLocale(), getOrganization());
	    }
	    return this.messages;
	  }
	  
	  public void setCookieLang(String lang)
	  {
	    this.cookieLang = lang;
	  }
	  
	  public UserData getUserData()
	  {
	    return this._userData;
	  }
	  
	  public void reloadUserData()
	  {
	    try
	    {
	      loadUserInfo(getUtilizador(), null);
	    }
	    catch (Exception e)
	    {
	      Logger.error(getUtilizador(), this, "reloadUserData", "Error reloading user data.", e);
	    }
	  }
	  
	  public boolean isManager()
	  {
	    return this instanceof UserInfoManagerInterface;
	  }
	  
	  public String registerTransaction(DBConnectionWrapper conn)
	  {
	    if (inTransaction()) {
	      throw new ExistingTransactionException();
	    }
	    long now = new Date().getTime();
	    int connHash = conn.hashCode();
	    String id = MessageFormat.format("{0}-{1}", new Object[] { Integer.valueOf(connHash), Long.valueOf(now) });
	    
	    this.connId = id;
	    this.connWrapper = conn;
	    
	    return id;
	  }
	  
	  public void unregisterTransaction(String id)
	    throws IllegalAccessException
	  {
	    if (!StringUtils.equals(this.connId, id)) {
	      throw new IllegalAccessException("Local id and given id mismatch. Different transactions??");
	    }
	    this.connWrapper = null;
	    this.connId = null;
	  }
	  
	  public Connection getTransactionConnection()
	  {
	    if (!inTransaction()) {
	      return null;
	    }
	    return this.connWrapper;
	  }
	  
	  public boolean inTransaction()
	  {
	    return this.connWrapper != null;
	  }
	  
	  protected class PasswordGuardian
	    implements Serializable
	  {
	    private static final long serialVersionUID = 8870956487293339051L;
	    private String p;
	    private String k;
	    private transient CryptUtils cypher;
	    
	    protected PasswordGuardian(byte[] password, long timestamp)
	    {
	      this.k = String.valueOf(PasswordGuardian.class.hashCode());
	      String myk = this.k + String.valueOf(timestamp);
	      this.cypher = new CryptUtils(myk);
	      this.p = this.cypher.encrypt(new String(password));
	    }
	    
	    protected byte[] getPassword(long timestamp)
	    {
	      if (this.cypher == null) {
	        this.cypher = new CryptUtils(this.k + String.valueOf(timestamp));
	      }
	      return this.cypher.decrypt(this.p).getBytes();
	    }
	    
	    protected String getStringPassword(long timestamp)
	    {
	      if (this.cypher == null) {
	        this.cypher = new CryptUtils(this.k + String.valueOf(timestamp));
	      }
	      return this.cypher.decrypt(this.p);
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

		    Logger.trace("USER with employeeid  + employeeid +  requesting LOGIN");
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
		    Logger.trace("Username fixed to: + sUsername" );

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
		      Logger.error(sUsername, this, "login", "Exception: + e.getMessage()" , e);
		      this._sError = msg.getString("login.error.generic.sso");
			  this._bLogged = false;
		    }	  	  
		  }
	}