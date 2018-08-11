/*
 *
 * Created on May 12, 2005 by iKnow
 *
 */

package pt.iflow.authentication.ad;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.authentication.Authentication;
import pt.iflow.api.authentication.AuthenticationInfo;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;
import pt.iflow.ldap.LDAPInterface;
import pt.iknow.utils.ldap.LDAPDirectory;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class ADAuthentication implements Authentication {

  private boolean _authUserBySearch = true;

  private static final String SEARCH_BY_UID = "SEARCH_BY_UID";
  private static final String USER_BIND_DN = "USER_BIND_DN";
  private static final String LIST_PROFILE_USERS = "LIST_PROFILE_USERS";
  private static final String LIST_USERS = "LIST_USERS";
  private static final String LIST_USERS_BY_PAGE = "LIST_USERS_BY_PAGE";
  private static final String LIST_USERS_BY_PAGE_OTHERS = "LIST_USERS_BY_PAGE_OTHERS";
  private static final String LIST_USERS_MAX_PAGE = "LIST_USERS_MAX_PAGE";
  private static final String SEARCH_BY_PAGE = "SEARCH_BY_PAGE";
  private static final String LDAP_SESSION_ATTR = "sessionID";
  private static final String USERSYNC_ON = "USERSYNC_ON";
  private static final String USERSYNC_ORGID = "USERSYNC_ORGID";
  private static final String USERSYNC_UNITID = "USERSYNC_UNITID";
  private static final String USERSYNC_THREAD_CICLE = "USERSYNC_THREAD_CICLE";
  private static final String PROPERTIES_TO_UPDATE = "PROPERTIES_TO_UPDATE";

  private String _searchByUserUid = "(&(uid={0})(objectClass=posixAccount))";
  private String _userBindDN = "uid={0},ou=Users,o=iKnow,dc=iknow,dc=pt";
  private String _listProfileUsers = "(&(ou=Profiles)(objectClass=groupOfNames)(cn={0}))";
  private String _listUsers = "(&(uid=*)(objectClass=sessionUser)(objectClass=posixAccount))";
  private String _listUsersByPages = "";
  private String _listUsersByPagesOthers = "";
  private int _listUsersMaxPage = 1500;
  private boolean _userSyncOn = false;
  private String _orgId = "1";
  private String _unitId = "1";
  private int _userSyncThreadCicle = 200;
  
  private List<Integer> charsIndexes = new ArrayList<Integer>();
  private static final String[] chars = new String[]{ "0", "1", "2", "3", "4", "5", "6", "7", "8",
                                                "9", "a", "b", "c", "d", "e", "f", "g", "h",
                                                "i", "j", "k", "l", "m", "n", "o", "p", "q",
                                                "r", "s", "t", "u", "v", "w", "x", "y", "z"};

  private List<String> _listPropertiesToImport = new ArrayList<String>();
  private List<String> _listPropertiesToUpdate = new ArrayList<String>();
  private Map<String, String> _propertiesVar = null;
  private boolean _searchByPage = true;
  private boolean _finishedSearch = false;

  private static String generateSessionId(String username) {
    String sessionId = username + Long.toString((new Date()).getTime());
    sessionId = Utils.encrypt(sessionId);
    return sessionId;
  }

  public void init(Properties parameters) {
    LDAPInterface.init(parameters);

    // Get configured Search String
    String searchByUID = (String) parameters.get(SEARCH_BY_UID);
    if(!(searchByUID==null||"".equals(searchByUID))) {
      _searchByUserUid = searchByUID;
    }

    // Get configured Profile Search String
    String listProfileUsers = (String) parameters.get(LIST_PROFILE_USERS);
    if(!(listProfileUsers==null||"".equals(listProfileUsers))) {
      _listProfileUsers = listProfileUsers;
    }

    String userSyncOn = (String) parameters.get(USERSYNC_ON);
    if(!(userSyncOn==null || "".equals(userSyncOn))) {
      try {
        _userSyncOn = Boolean.parseBoolean(userSyncOn.toLowerCase());
      } catch (Exception e) {
        Logger.error("ADMIN", this, "init", "Converting the property USERSYNC_ON = " + userSyncOn + " to boolean.", e);
      }
      
      if (_userSyncOn) {

        String orgId = (String) parameters.get(USERSYNC_ORGID);
        if(!(orgId==null || "".equals(orgId))) {
          _orgId = orgId;
        }

        String unitId = (String) parameters.get(USERSYNC_UNITID);
        if(!(unitId==null || "".equals(unitId))) {
          _unitId = unitId;
        }

        String userSyncThreadCicle = (String) parameters.get(USERSYNC_THREAD_CICLE);
        if(!(userSyncThreadCicle==null || "".equals(userSyncThreadCicle))) {
          try {
            _userSyncThreadCicle = Integer.parseInt(userSyncThreadCicle);
          } catch (Exception e) {
            Logger.error("ADMIN", this, "init", "Converting the property USERSYNC_THREAD_CICLE = " + userSyncThreadCicle + " to int.", e);
          }
        }
        
        String listPropertiesToUpdate = (String) parameters.get(PROPERTIES_TO_UPDATE);
        if(!(listPropertiesToUpdate==null || "".equals(listPropertiesToUpdate))) {
          _listPropertiesToUpdate = Arrays.asList(listPropertiesToUpdate.split(","));
        }
        
        getListPropertiesToImport(parameters);

        String searchByPage = (String) parameters.get(SEARCH_BY_PAGE);
        if(!(searchByPage==null || "".equals(searchByPage))) {
          try {
            _searchByPage = Boolean.parseBoolean(searchByPage.toLowerCase());
          } catch (Exception e) {
            Logger.error("ADMIN", this, "init", "Converting the property SEARCH_BY_PAGE = " + searchByPage + " to boolean.", e);
          }
          
          if (_searchByPage) {

            String listUsersByPages = (String) parameters.get(LIST_USERS_BY_PAGE);
            if(!(listUsersByPages==null || "".equals(listUsersByPages))) {
              _listUsersByPages = listUsersByPages;
            }

            String listUsersMaxPage = (String) parameters.get(LIST_USERS_MAX_PAGE);
            if(!(listUsersMaxPage==null || "".equals(listUsersMaxPage))) {
              try {
                _listUsersMaxPage = Integer.parseInt(listUsersMaxPage);
              } catch (Exception e) {
                Logger.error("ADMIN", this, "init", "Converting the property LIST_USERS_MAX_PAGE = " + listUsersByPages + " to int.", e);
              }
            }
              
            String listUsersByPagesOthers = (String) parameters.get(LIST_USERS_BY_PAGE_OTHERS);
            if(!(listUsersByPagesOthers==null || "".equals(listUsersByPagesOthers))) {
              _listUsersByPagesOthers = listUsersByPagesOthers;
            }
          } else {
            String listUsers = (String) parameters.get(LIST_USERS);
            if(!(listUsers==null||"".equals(listUsers))) {
              _listUsers = listUsers;
            }

          }
        }
      }
    }

    // if false, authenticate users using a bind dn mask. otherwise search for the user
    String authUserBySearch = (String) parameters.get("AUTH_USER_BY_SEARCH");
    if(!(authUserBySearch==null||"".equals(authUserBySearch))) {
      _authUserBySearch="true".equalsIgnoreCase(authUserBySearch);
    }

    // User bind dn
    String userBindDn = (String) parameters.get(USER_BIND_DN);
    if(!(userBindDn==null||"".equals(userBindDn))) {
      _userBindDN = userBindDn;
    }

  }

  private void getListPropertiesToImport(Properties parameters) {
    _propertiesVar = new HashMap<String, String>();
    String aux = null;
    
    aux = (String)parameters.get(UserData.ID);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.ID));
      _propertiesVar.put(UserData.ID, aux);
    }
    aux = (String)parameters.get(UserData.USERNAME);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.USERNAME));
      _propertiesVar.put(UserData.USERNAME, aux);
    }
    aux = (String)parameters.get(UserData.FULL_NAME);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.FULL_NAME));
      _propertiesVar.put(UserData.FULL_NAME, aux);
    }
    aux = (String)parameters.get(UserData.EMAIL_ADDRESS);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.EMAIL_ADDRESS));
      _propertiesVar.put(UserData.EMAIL_ADDRESS, aux);
    }
    aux = (String)parameters.get(UserData.EMPLOYEE_NUMBER);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.EMPLOYEE_NUMBER));
      _propertiesVar.put(UserData.EMPLOYEE_NUMBER, aux);
    }
    aux = (String)parameters.get(UserData.ORG_NAME);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.ORG_NAME));
      _propertiesVar.put(UserData.ORG_NAME, aux);
    }
    aux = (String)parameters.get(UserData.ORG_ID);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.ORG_ID));
      _propertiesVar.put(UserData.ORG_ID, aux);
    }
    aux = (String)parameters.get(UserData.UNIT_NAME);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.UNIT_NAME));
      _propertiesVar.put(UserData.UNIT_NAME, aux);
    }
    aux = (String)parameters.get(UserData.UNITID);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.UNITID));
      _propertiesVar.put(UserData.UNITID, aux);
    }
    aux = (String)parameters.get(UserData.MOBILE_NUMBER);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.MOBILE_NUMBER));
      _propertiesVar.put(UserData.MOBILE_NUMBER, aux);
    }
    aux = (String)parameters.get(UserData.GENDER);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.GENDER));
      _propertiesVar.put(UserData.GENDER, aux);
    }
    aux = (String)parameters.get(UserData.PHONE_NUMBER);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.PHONE_NUMBER));
      _propertiesVar.put(UserData.PHONE_NUMBER, aux);
    }
    aux = (String)parameters.get(UserData.FAX_NUMBER);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.FAX_NUMBER));
      _propertiesVar.put(UserData.FAX_NUMBER, aux);
    }
    aux = (String)parameters.get(UserData.COMPANY_PHONE);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.COMPANY_PHONE));
      _propertiesVar.put(UserData.COMPANY_PHONE, aux);
    }
    aux = (String)parameters.get(UserData.DEPARTMENT);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.DEPARTMENT));
      _propertiesVar.put(UserData.DEPARTMENT, aux);
    }
    aux = (String)parameters.get(UserData.MANAGER);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.MANAGER));
      _propertiesVar.put(UserData.MANAGER, aux);
    }
    aux = (String)parameters.get(UserData.TITLE);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.TITLE));
      _propertiesVar.put(UserData.TITLE, aux);
    }
    aux = (String)parameters.get(UserData.FIRST_NAME);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.FIRST_NAME));
      _propertiesVar.put(UserData.FIRST_NAME, aux);
    }
    aux = (String)parameters.get(UserData.LAST_NAME);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.LAST_NAME));
      _propertiesVar.put(UserData.LAST_NAME, aux);
    }
    aux = (String)parameters.get(UserData.ORGADM);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.ORGADM));
      _propertiesVar.put(UserData.ORGADM, aux);
    }
    aux = (String)parameters.get(UserData.CHAVE_RESET);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.CHAVE_RESET));
      _propertiesVar.put(UserData.CHAVE_RESET, aux);
    }
    aux = (String)parameters.get(UserData.ORGADM_USERS);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.ORGADM_USERS));
      _propertiesVar.put(UserData.ORGADM_USERS, aux);
    }
    aux = (String)parameters.get(UserData.ORGADM_FLOWS);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.ORGADM_FLOWS));
      _propertiesVar.put(UserData.ORGADM_FLOWS, aux);
    }
    aux = (String)parameters.get(UserData.ORGADM_PROCESSES);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.ORGADM_PROCESSES));
      _propertiesVar.put(UserData.ORGADM_PROCESSES, aux);
    }
    aux = (String)parameters.get(UserData.ORGADM_RESOURCES);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.ORGADM_RESOURCES));
      _propertiesVar.put(UserData.ORGADM_RESOURCES, aux);
    }
    aux = (String)parameters.get(UserData.ORGADM_ORG);
    if (aux != null && ! "".equals(aux)) {
      _listPropertiesToImport.add((UserData.ORGADM_ORG));
      _propertiesVar.put(UserData.ORGADM_ORG, aux);
    }
  }
  
  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#checkUser(java.lang.String, java.lang.String)
   */
  public boolean checkUser(String username, String password) {
    boolean retVal = false;
    Logger.debug(null,this,"checkUser","starting with username: " + username);
    try {
      if (username != null && !username.equals("") && password != null
          && !password.equals("")) {

        String bindDn = "";
        Logger.debug(null,this,"checkUser","Auth user by search: " + _authUserBySearch);
        if(_authUserBySearch) {
          Logger.debug(null,this,"checkUser","Search by user uid: " + _searchByUserUid);
          String query = MessageFormat.format(_searchByUserUid, new Object[] {username});
          Logger.debug(null,this,"checkUser","Performing LDAP search " + query);
          bindDn = LDAPInterface.getDN(query);
          Logger.debug(null,this,"checkUser","bindDn :" + bindDn);
        } else {
          try {
        	Logger.debug(null,this,"checkUser","Search by user uid: " + _searchByUserUid);
            String query = MessageFormat.format(_searchByUserUid, new Object[] {username});
            Logger.debug(null,this,"checkUser","Performing LDAP search " + query);
            bindDn = LDAPInterface.getDN(query);
            Logger.debug(null,this,"checkUser","bindDn :" + bindDn +", userBinDN: " +_userBindDN + ", BaseDN: " + LDAPInterface.getBaseDN());
            bindDn = MessageFormat.format(_userBindDN, new Object[] {bindDn, LDAPInterface.getBaseDN()});
          } catch (Exception e) {
        	Logger.debug(null,this,"checkUser","No anonymous acccess to LDAP, exception: " + e);
            Logger.debug(null,this,"checkUser","No anonymous acccess to LDAP. Using config bind dn");
            bindDn = MessageFormat.format(_userBindDN, new Object[] {username});
            Logger.debug(null,this,"checkUser","Using config bind dn, bindDn :" + bindDn +", userBinDN: " +_userBindDN);
          }
        }
        Logger.debug(null,this,"checkUser","user bind dn = " + bindDn);      
        retVal = LDAPInterface.checkBindPassword(bindDn, password) || LDAPInterface.checkBindPassword(username, password) || LDAPInterface.checkBindPassword("DN=" + username, password) || LDAPInterface.checkBindPassword("PN=" + username, password);
      }
    }
    catch (Exception e) {
      Logger.error(null,this,"checkUser","global exception = " + e);
      e.printStackTrace();
    }
    return retVal;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#getProfileUsers(java.lang.String)
   */
  public Collection<Map<String,String>> getProfileUsers(String profileID) {
    Collection<Map<String,String>> retObj = null;

    Logger.debug(null,this,"getProfileUsers","Performing LDAP search " + _listProfileUsers);
    retObj = LDAPInterface.searchDeep(MessageFormat.format(_listProfileUsers,new Object[] {profileID}));
    if (retObj.isEmpty() || retObj.size() == 0 || retObj.size() > 1) {
      Logger.debug(null,this,"getProfileUsers","EMPTY PROFILE LIST");
    }
    return retObj;
  }

  public AuthenticationInfo loginUser(String username, String password) {
    AuthenticationInfo retObj = null;

    if(checkUser(username, password)) {
      LDAPDirectory directory = LDAPInterface.getDirectory();
      Logger.debug(null,this,"loginUser","Performing LDAP search " + _searchByUserUid);
      Collection<Map<String,String>> users = directory.searchDeep(MessageFormat.format(_searchByUserUid, new Object[] {username}));
      if (users.isEmpty() || users.size() == 0 || users.size() > 1) {
        Logger.debug(null,this,"loginUser","EMPTY USER LIST");
      }
      else {
        Map<String,String> user = users.iterator().next();
        String sessionId = generateSessionId(username);
        directory.replaceAttribute((String)user.get(LDAPDirectory.DN), LDAP_SESSION_ATTR, sessionId);
        retObj = new ADAuthenticationInfo(user,sessionId);
      }
    }

    return retObj;
  }

  public boolean checkUserSession(String username, String sessionId) {

    try {
      if (username != null && !username.equals("") && sessionId != null
          && !sessionId.equals("")) {
        Logger.debug(null,this,"loginUser","Performing LDAP search " + _searchByUserUid);
        Collection<Map<String,String>> users = LDAPInterface.searchDeep(MessageFormat.format(_searchByUserUid, new Object[] {username}));
        if (users.isEmpty() || users.size() == 0 || users.size() > 1) {
          Logger.debug(null,this,"loginUser","EMPTY USER LIST");
        }
        else {
          Map<String,String> user = users.iterator().next();
          String ldapSessionID = (String) user.get(LDAP_SESSION_ATTR);
          return (ldapSessionID!=null &&ldapSessionID.equals(sessionId));
        }
      }
    }
    catch (Exception e) {
    }

    return false;
  }

  public List<Map<String, String>> getUsersForSync() {
    if (_searchByPage) 
      return getNextPageUsersForSync();
    else
      return getAllUsersForSync();
  }
  
  private List<Map<String, String>> getAllUsersForSync() {
    List<Map<String, String>> retObj = new ArrayList<Map<String, String>>();
    try {
      Logger.debug(null, this, "getAllUsersForSync", "Performing LDAP search " + _listUsers);
      Collection<Map<String,String>> users = LDAPInterface.searchDeep(_listUsers);
      if (users == null || users.isEmpty()) {
        Logger.debug(null,this,"getAllUsersForSync","EMPTY USER LIST");
      }
      else {
        retObj = transformResults(users);
      }
    }
    catch (Exception e) {
      Logger.error("ADMIN", this, "getAllUsersForSync", "Error retrieving user list.", e);
    }
    return retObj;
  }

  private List<Map<String, String>> getNextPageUsersForSync() {
    List<Map<String, String>> retObj = new ArrayList<Map<String, String>>();

    if (charsIndexes.isEmpty()) {
      try {
        Logger.debug(null, this, "getNextPageUsersForSync", "Performing LDAP search " + _listUsersByPagesOthers);
        Collection<Map<String,String>> users = LDAPInterface.searchDeep(_listUsersByPagesOthers);
        if (users == null || users.isEmpty()) {
          Logger.debug(null,this,"getNextPageUsersForSync","EMPTY USER LIST");
        } else {
          retObj = transformResults(users);
        }
      } catch (Exception e) {
        Logger.error("ADMIN", this, "getNextPageUsersForSync", "Error retrieving user list.", e);
      }
      charsIndexes.add(0);
      
    } else {
      
      StringBuffer sb = new StringBuffer();
      try {
        for (int i = 0; i < charsIndexes.size(); i++) {
          sb.append(chars[charsIndexes.get(i)]);
        }
        String sAux = MessageFormat.format(_listUsersByPages, sb.toString());
        Logger.debug(null, this, "getNextPageUsersForSync", "Performing LDAP search " + sAux);
        Collection<Map<String,String>> users = LDAPInterface.searchDeep(sAux);
        if (users == null || users.isEmpty()) {
          Logger.debug(null,this,"getNextPageUsersForSync","EMPTY USER LIST");
        } else if (users.size() >= _listUsersMaxPage) {
          charsIndexes.add(0);
          return getNextPageUsersForSync();
        } else {
          retObj = transformResults(users);
        }
      }
      catch (Exception e) {
        Logger.error("ADMIN", this, "getNextPageUsersForSync", "Error retrieving user list.", e);
      }
      updateSearchParameters();
    }
    return retObj;
  }
  
  private List<Map<String, String>> transformResults(Collection<Map<String,String>> users) {
    List<Map<String, String>> retObj = new ArrayList<Map<String, String>>();
    for(Map<String,String> userMap : users) {
      Map<String,String> retUserMap = new HashMap<String, String>();
      for (String prop : _listPropertiesToImport) {
        retUserMap.put(prop, userMap.get(_propertiesVar.get(prop)));
      }
      retObj.add(retUserMap);
    }
    return retObj;
  }

  private void updateSearchParameters() {
    for (int i = charsIndexes.size() - 1; i >= 0; i--) {
      Integer n = charsIndexes.get(i) + 1;
      if (n >= chars.length) {
        charsIndexes.set(i, 0);
      } else {
        charsIndexes.set(i, n);
        return;
      }
    }
    charsIndexes.clear();
    _finishedSearch = true;
  }

  public boolean hasMoreUserToProcess() {
    if (!_searchByPage) return false;
    if (_finishedSearch) {
      _finishedSearch = false;
      return false;
    }
    return true;
  }

  public boolean doSyncronizeUsers() {
    return _userSyncOn;
  }

  public String getSyncOrgId() {
    return _orgId;
  }
  
  public String getSyncUnitId() {
    return _unitId;
  }
  
  public int getSyncThreadCicle(){
    return _userSyncThreadCicle;
  } 

  public boolean shouldUpdateUser() {
    return !_listPropertiesToUpdate.isEmpty();
  }

  public boolean shouldUpdateThisColumn(String prop) {
    return _listPropertiesToUpdate.contains(prop);
  }
  
}
