package pt.iflow.core;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.userdata.OrganizationDataAccess;
import pt.iflow.api.userdata.OrganizationalUnitData;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.userdata.UserDataAccess;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 *
 * <p>Title: AuthProfile</p>
 * <p>Description: Auheticates users and manage their profiles</p>
 * <p>Notes: Methods related with LDAP were made available by João C. Lopes
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author Paulo Flores
 * @version 1.0
 * 
 * 
 * $Id:AuthProfileBean.java 691 2007-06-22 14:56:24 +0000 (Fri, 22 Jun 2007) uid=mach,ou=Users,dc=iknow,dc=pt $
 */

public class AuthProfileBean implements AuthProfile {

  private static AuthProfileBean instance = null;

  static {
    //  SecurityWrapper.doIt();

    // make any dummy call to email manager to start it (static block does it)
    pt.iflow.api.notification.EmailManager.getEmailTemplate(null, null);
  }

  private AuthProfileBean() {
  }

  public static AuthProfileBean getInstance() {
    if(null == instance)
      instance = new AuthProfileBean();
    return instance;
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * verifica se um utilizador é válido, dado o par username-password
   * 
   * @param username String com o username do utilizador
   * @param password String com a password em plain-text
   * @return - valor lógico com o sucesso da autenticacao
   * @ejb.interface-method view-type = "remote"
   */
  public boolean checkUser(String username, String password) {
    return AccessControlManager.getAuthentication().checkUser(username, password);
  }

  /**
   * verifica se um utilizador de sistema é válido, dado o par username-password
   * 
   * @param username String com o username do utilizador
   * @param password String com a password em plain-text
   * @return - valor lógico com o sucesso da autenticacao
   * @ejb.interface-method view-type = "remote"
   */
  public boolean checkSystemUser(String username, String password) {
    return AccessControlManager.getSystemAuthentication().checkUser(username, password);
  }

  /**
   * Authenticates an user using his login information and the sessionId
   * generated when he logged on in the intranet using the LDAP framework
   * 
   * @param userId
   *          user login identification
   * @param sessionId
   *          a number that uniqlly identifies that validates the user
   * @return a boolean value indicating if user is autheticated sucessufull
   * @ejb.interface-method view-type = "remote"
   */
  public boolean authenticateIntranetUser(String username, String sessionId) {
    return AccessControlManager.getAuthentication().checkUserSession(username, sessionId);
  }

  /**
  /**
   * Gets user info from ldap.
   * 
   * @param asUser
   *          user to get information
   * @param searchField
   *          field to search
   * @return hashtable with user's ldap information
   * @ejb.interface-method view-type = "remote"
   */
  public UserData getUserInfo(String asUser, String searchField) {
    UserData temp = AccessControlManager.getUserDataAccess().getUserData(asUser, searchField);
    Logger.debug(null, this, "getUserInfo", "USER INFO : " + temp);
    return temp;
  }

  /**
   * Gets user info from ldap.
   * 
   * @param asUser
   *          user to get information
   * @return hashtable with user's ldap information
   * @ejb.interface-method view-type = "remote"
   */
  public UserData getUserInfo(String asUser) {
    UserData temp = AccessControlManager.getUserDataAccess().getUserData(asUser);
    Logger.debug(null, this, "getUserInfo", "USER INFO : " + temp);
    return temp;
  }

  /**
   * Get a list of users that belong to a given profile
   * 
   * @param profile
   *          the name of the profile
   * @return a value of type 'ListIterator' with the users ids belonging to the given profile
   * @exception NamingException
   *              if an error occurs
   * @exception SQLException
   *              if an error occurs
   * @ejb.interface-method view-type = "remote"
   */
  public Collection<String> getUsersInProfile(UserInfoInterface userInfo, String profile) {

    return AccessControlManager.getUserDataAccess().getUsersInProfile(profile, userInfo.getCompanyID());

  }

  /**
   * Get the logged user profile list
   * 
   * @param
   * @return a value of type 'Iterator' profiles for the logged user
   * @exception NamingException
   *              if an error occurs
   * @exception SQLException
   *              if an error occurs
   * @ejb.interface-method view-type = "remote"
   */

  public Collection<String> getUserProfiles(UserInfoInterface userInfo) {
    Collection<String> retObj = AccessControlManager.getUserDataAccess().getUserProfiles(userInfo);

    return retObj;
  }

  /**
   * Get a user's profile list
   * 
   * @param
   * @return a value of type 'Iterator' profiles for the logged user
   * @exception NamingException
   *              if an error occurs
   * @exception SQLException
   *              if an error occurs
   * @ejb.interface-method view-type = "remote"
   */

  public Collection<String> getUserProfilesForUser(UserInfoInterface userInfo, String username) {
    Collection<String> retObj = AccessControlManager.getUserDataAccess().getUserProfilesForUser(userInfo, username);

    return retObj;
  }

  /**
   * Get the logged user profile list
   * 
   * @param
   * @return a value of type 'Iterator' profiles for the logged user
   * @exception NamingException
   *              if an error occurs
   * @exception SQLException
   *              if an error occurs
   * @ejb.interface-method view-type = "remote"
   */

  public Collection<String> getUserProfiles(String userName, String organization) {
    Collection<String> retObj = AccessControlManager.getUserDataAccess().getUserProfiles(userName, organization);

    return retObj;
  }

  /**
   * Get the logged user profile list
   * 
   * @param
   * @return a value of type 'ListIterator' profiles for the logged user
   * @exception NamingException
   *              if an error occurs
   * @exception SQLException
   *              if an error occurs
   * @ejb.interface-method view-type = "remote"
   */

  public Collection<String> getUserProfiles(UserInfoInterface userInfo,
      String application) {
    return this.getUserProfiles(userInfo.getUtilizador(), userInfo.getOrganization(), application);
  }

  /**
   * 
   * List all profiles available
   * 
   * @return
   * @ejb.interface-method view-type = "remote"
   */
  public Collection<String> getAllProfiles(String organization) {
    Collection<String> retObj = AccessControlManager.getUserDataAccess().getAllProfiles(organization);

    return retObj;
  }

  /**
   * Get a user profile collection
   * 
   * @param
   * @return a value of type 'ListIterator' profiles for the given user
   * @exception NamingException
   *              if an error occurs
   * @exception SQLException
   *              if an error occurs
   *              
   * @ejb.interface-method view-type = "remote"
   */
  public Collection<String> getUserProfiles(String asUser, String organization, String application) {
    Vector<String> li = new Vector<String>();
    li.addAll(AccessControlManager.getUserDataAccess().getUserProfiles(asUser, organization));

    return li;
  }

  /**
   * 
   * Gets the manager for the given user
   *  - unit manager if user is not the given user 
   *  - parent's unit manager, if unit manager not found for user's unit or unit manager is user itself (recursively until found or reached top)
   * 
   * @param asUser
   * @return the unit manager or null if no unit manager found
   * @ejb.interface-method view-type = "remote"
   */
  public String getUpperNode(String asUser) {
    String retObj = null;

    // get unit from user
    UserData userData = this.getUserInfo(asUser);
    String sUserUnitId = userData.getUnitId();

    // get unit manager
    retObj = this.getUnitManager(asUser, sUserUnitId);

    return retObj;
  }

  /**
   * 
   * Gets the manager for the given user and unit id:
   *  - unit manager if user is not the given user 
   *  - parent's unit manager, if unit manager not found for user's unit or unit manager is user itself (recursively until found or reached top) 
   * 
   * @param asUser
   * @param asUnitId
   * @return the unit manager or null if no unit manager found
   */
  private String getUnitManager(String asUser, String asUnitId) {

    OrganizationalUnitData orgUnitData = this.getOrganicalUnitInfo(asUnitId);

    if (orgUnitData == null) return null;

    String sUnitManager = orgUnitData.get(OrganizationalUnitData.MANAGER);
    // unit manager valid if it exists and it's not the calling user  

    if (sUnitManager == null || sUnitManager.equals(asUser)) {
      // reset unit manager
      sUnitManager = null;

      // find parent unit manager
      OrganizationalUnitData oParentUnit = this.getOrganicalUnitParent(asUnitId);
      if (oParentUnit != null) {
        String sParentUnit = oParentUnit.get(OrganizationalUnitData.UNITCODE);
        sUnitManager = this.getUnitManager(asUser, sParentUnit);
      }
    }

    return sUnitManager;
  }

  /**
   * 
   * TODO Add comment for method getOrganicalUnitInfo on AuthProfileBean
   * 
   * @param asKey
   * @return
   * @ejb.interface-method view-type = "remote"
   */
  public OrganizationData getOrganizationInfo(String asKey) {
    return AccessControlManager.getOrganizationDataAccess().getOrganization(asKey);
  }

  /**
   * Gets organical unit info from ldap.
   * 
   * @param asKey
   *          key to get information
   * @return hashtable with oganical unit's ldap information
   * @ejb.interface-method view-type = "remote"
   */
  public OrganizationalUnitData getOrganicalUnitInfo(String asKey) {
    return AccessControlManager.getOrganizationDataAccess().getOrganizationalUnit(asKey);
  }

  /**
   * Gets organical unit parent info from ldap.
   * 
   * @param asKey
   *          key to get information
   * @return hashtable with oganical unit parent's ldap information
   * @ejb.interface-method view-type = "remote"
   */
  public OrganizationalUnitData getOrganicalUnitParent(String asKey) {
    return AccessControlManager.getOrganizationDataAccess().getOrganizationalUnitParent(asKey);
  }

  public UserData getSystemUserInfo(String asUser) {
    UserData temp = AccessControlManager.getSystemUserDataAccess().getUserData(asUser);
    Logger.debug(null, this, "getSystemUserInfo", "SYSTEM USER INFO : " + temp);
    return temp;
  }


  /**
   * Checks if a user is subordinate of another user.
   * @param userInfo the chiefe user
   * @param user the user to check
   * @return true if userInfo is the manager of user.
   */
  public boolean isSubordinate(UserInfoInterface userInfo, String user) {
    if(!userInfo.isUnitManager()) return false;
    Logger.debug(userInfo.getUtilizador(), this, "isSubordinate", "Checking if user "+user+" is my subordinate");
    String userUnit = null;
    String masterUnit = userInfo.getOrgUnitID();
    Logger.debug(userInfo.getUtilizador(), this, "isSubordinate", "My unit: "+masterUnit);

    OrganizationDataAccess oda = AccessControlManager.getOrganizationDataAccess();
    userUnit = getUserInfo(user).getUnitId();
    Logger.debug(userInfo.getUtilizador(), this, "isSubordinate", "User unit: "+userUnit);

    while(userUnit != null) {
      if(StringUtils.equals(masterUnit, userUnit)) return true;
      OrganizationalUnitData unitData = oda.getOrganizationalUnitParent(userUnit);
      if(null == unitData) break;
      userUnit = unitData.get(OrganizationalUnitData.UNITCODE);  // deduzo que seja este
      Logger.debug(userInfo.getUtilizador(), this, "isSubordinate", "Parent unit: "+userUnit);
    }

    Logger.debug(userInfo.getUtilizador(), this, "isSubordinate", "I am not your boss!");
    return false;
  }

  /**
   * Pre process username before login.
   * 
   * @param username
   * @return processed username
   */
  public String fixUsername(String username) {
    UserDataAccess uda = AccessControlManager.getUserDataAccess();
    return uda.fixUsername(username);
  }


  public Collection<UserData> getAllUsers(String orgId) {
    return AccessControlManager.getUserDataAccess().getAllUsers(orgId);
  }

}
