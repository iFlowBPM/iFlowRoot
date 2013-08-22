/*
 *
 * Created on May 12, 2005 by iKnow
 *
 */

package pt.iflow.authentication.db;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.authentication.Authentication;
import pt.iflow.api.authentication.AuthenticationInfo;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.userdata.UserDataAccess;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.userdata.common.MappedUserData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class DBSystemAuthentication implements Authentication,UserDataAccess {

  private Map<String,String> _mapping;

  private static final Collection<String> _emptyProfilesList = Collections.unmodifiableCollection(new ArrayList<String>(0));
  private static final Collection<Map<String,String>> _emptyProfileUsers = Collections.unmodifiableCollection(new ArrayList<Map<String,String>>(0));

  private static final String SQL_GET_USER_PASSWORD = "select USERPASSWORD from system_users where USERNAME=''{0}''";
  private static final String SQL_GET_USER_SESSION = "select SESSIONID from system_users where USERNAME=''{0}''";
  private static final String SQL_GET_USER_INFO = "select USERNAME,SESSIONID from system_users where USERNAME=''{0}''";

  private static final String SQL_SET_USER_SESSION = "update system_users set SESSIONID=''{0}'' where USERNAME=''{1}''";

  private static String generateSessionId(String username) {

    String sessionId = username + Long.toString((new Date()).getTime());

    sessionId = Utils.encrypt(sessionId);

    return sessionId;
  }

  /* (non-Javadoc)   Collection users = 
       DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USER_PASSWORD,new String[]{username}));

   * @see pt.iknow.authentication.Authentication#checkUser(java.lang.String, java.lang.String)
   */
  public boolean checkUser(String username, String password) {
    boolean result = false;

    Collection<Map<String,String>> users = DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USER_PASSWORD,new Object[]{username}));

    if(users.isEmpty()) {
      Logger.error(null,this,"checkUser","No user with username " + username);
      result = false;
    } else if (users.size() > 1) {
      Logger.error(null,this,"checkUser","More than one user with username " + username);
      result = false;
    } else {
      String checkPass = users.iterator().next().get("USERPASSWORD");

      checkPass = Utils.decrypt(checkPass);
      if(!checkPass.equals(password)){
        Logger.debug(null,this,"checkUser","Passwords do not match for username " + username);
        result = false;
      } else {
        result = true;
      }
    }
    return result;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#checkUserSession(java.lang.String, java.lang.String)
   */
  public boolean checkUserSession(String username, String sessionID) {
    boolean result = false;

    Collection<Map<String,String>> users = 
      DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USER_SESSION,new Object[]{username}));

    if(users.isEmpty()) { 
      Logger.error(null,this,"checkUser","No user with username " + username);
      result = false;
    } else if (users.size() > 1) {
      Logger.error(null,this,"checkUser","More than one user with username " + username);
      result = false;
    } else {
      String checkSession = users.iterator().next().get("SESSIONID");
      if(!checkSession.equals(sessionID)){
        Logger.debug(null,this,"checkUser","Session id's do not match for username " + username);
        result = false;
      } else {
        result = true;
      }
    }
    return result;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#loginUser(java.lang.String, java.lang.String)
   */
  public AuthenticationInfo loginUser(String username, String password) {
    AuthenticationInfo retObj = null;

    if(checkUser(username,password)) {
      DatabaseInterface.executeUpdate(MessageFormat.format(SQL_SET_USER_SESSION,new Object[]{generateSessionId(username),username}));
      Map<String,String> userInfo = DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USER_INFO,new Object[]{username})).iterator().next();
      retObj = new DBAuthenticationInfo(userInfo);
    }

    return retObj;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#getProfileUsers(java.lang.String)
   */
  public Collection<Map<String,String>> getProfileUsers(String profileID) {
    return _emptyProfileUsers;
  }


  public void init(Properties parameters) {
    this._mapping = Collections.unmodifiableMap(Setup.getPropertiesAsStringMap(parameters));

  }

  public Collection<String> getAllProfiles(String organization) {
    return _emptyProfilesList;
  }

  public UserData getUserData(String userId) {


    MappedUserData retObj = null;

    String sqlGetUserData = DBQueryManager.processQuery("DBSystemUserDataAccess.SQL_GET_USER_DATA", new Object[]{Const.SYSTEM_ORGANIZATION,userId});

    Collection<Map<String,String>> users = DatabaseInterface.executeQuery(sqlGetUserData);

    if(users.isEmpty()) {
      Logger.error(null,this,"getUserData","No user with id " + userId);
    } else if (users.size() > 1) {
      Logger.error(null,this,"getUserData","More than one user with id " + userId);
    } else {
      Map<String,String> userData = users.iterator().next();
      // SET DEFAULT ORGANIZATION
      userData.put(UserData.ORG_ID.toLowerCase(), Const.SYSTEM_ORGANIZATION);
      retObj = new MappedUserData(userData,this._mapping);
    }

    return retObj;	
  }

  public UserData getUserData(String userId, String searchField) {
	  //TODO EMAIL


    MappedUserData retObj = null;

    String sqlGetUserData = DBQueryManager.processQuery("DBSystemUserDataAccess.SQL_GET_USER_DATA", new Object[]{Const.SYSTEM_ORGANIZATION,userId});

    Collection<Map<String,String>> users = DatabaseInterface.executeQuery(sqlGetUserData);

    if(users.isEmpty()) {
      Logger.error(null,this,"getUserData","No user with id " + userId);
    } else if (users.size() > 1) {
      Logger.error(null,this,"getUserData","More than one user with id " + userId);
    } else {
      Map<String,String> userData = users.iterator().next();
      // SET DEFAULT ORGANIZATION
      userData.put(UserData.ORG_ID.toLowerCase(), Const.SYSTEM_ORGANIZATION);
      retObj = new MappedUserData(userData,this._mapping);
    }

    return retObj;	
  }

  public Collection<String> getUserProfiles(UserInfoInterface userInfo) {
    return  _emptyProfilesList;
  }

  public Collection<String> getUserProfiles(String user, String organization) {
    return  _emptyProfilesList;
  }
  public Collection<String> getUserProfilesForUser(UserInfoInterface userInfo, String username) {
    return  _emptyProfilesList;
  }

  public Collection<String> getUsersInProfile(String profile, String organizatio) {
    return  _emptyProfilesList;
  }

  public boolean canUserAdmin() {
    return false;
  }

  public boolean canModifyPassword() {
    return true;
  }

  public boolean canModifyUser() {
    return false;
  }

  public boolean canDeleteUser() {
    return false;
  }

  public String getOrganizationalUnitID(String user) {
    UserData data = getUserData(user);
    return data.getUnitId();
  }

  public Collection<String> getOrganizationAdmins(String organization) {
	  return null;
  }
  
  public String fixUsername(String username) {
    return username;
  }

  public Collection<UserData> getAllUsers(String organization) {
    return Collections.unmodifiableCollection(new ArrayList<UserData>());
  }

  public List<String> getListExtraProperties() {
    return null;
  }

  public Map<String,String> getMappingExtra(){
    return null;
  }

  //Não há nada para sincronizar neste caso
  public List<Map<String, String>> getUsersForSync() {
    return null;
  }
  
  public boolean hasMoreUserToProcess() {
    return false;
  }

  public boolean doSyncronizeUsers() {
    return false;
  }

  public String getSyncOrgId() {
    return "";
  }
  
  public String getSyncUnitId() {
    return "";
  }
  
  public int getSyncThreadCicle(){
    return 10000000;
  } 

  public boolean shouldUpdateUser() {
    return false;
  }

  public boolean shouldUpdateThisColumn(String prop) {
    return false;
  }
  
}
