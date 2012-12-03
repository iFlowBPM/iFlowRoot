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
/*
 *
 * Created on May 12, 2005 by iKnow
 *
  */

package pt.iflow.authentication.ldap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.authentication.Authentication;
import pt.iflow.api.authentication.AuthenticationInfo;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;
import pt.iflow.ldap.LDAPInterface;
import pt.iflow.ldap.LDAPMapping;
import pt.iflow.userdata.common.MappedUserData;
import pt.iknow.utils.ldap.LDAPDirectory;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class LDAPAuthentication implements Authentication {

  private boolean _authUserBySearch = true;
  
  private static final String DEFAULT_SEARCH_BY_UID = "(&(uid={0})(objectClass=posixAccount))";
  private static String SEARCH_BY_UID = DEFAULT_SEARCH_BY_UID;
  private static String USER_BIND_DN = "uid={0},ou=Users,o=iKnow,dc=iknow,dc=pt";
  private static String LIST_PROFILE_USERS = "(&(ou=Profiles)(objectClass=groupOfNames)(cn={0}))";
  private static String LIST_USERS = "";
  private static String USERID = "";
  private static String LDAP_SESSION_ATTR = "sessionID";
  
  private static String generateSessionId(String username) {
    String sessionId = username + Long.toString((new Date()).getTime());
    sessionId = Utils.encrypt(sessionId);
    return sessionId;
  }

  public void init(Properties parameters) {
    LDAPInterface.init(parameters);
    
    // Get configured Search String
    String searchByUID = (String) parameters.get("SEARCH_BY_UID");
    if(!(searchByUID==null||"".equals(searchByUID))) {
      SEARCH_BY_UID = searchByUID;
    }

    // Get configured Profile Search String
    String listProfileUsers = (String) parameters.get("LIST_PROFILE_USERS");
    if(!(listProfileUsers==null||"".equals(listProfileUsers))) {
      LIST_PROFILE_USERS = listProfileUsers;
    }

    String listUsers = (String) parameters.get("LIST_USERS");
    if(!(listUsers==null||"".equals(listUsers))) {
      LIST_USERS = listUsers;
    }

    String userId = (String) parameters.get("USERID");
    if(!(userId==null||"".equals(userId))) {
      USERID = userId;
    }
    
    // if false, authenticate users using a bind dn mask. otherwise search for the user
    String doUserSearch = (String) parameters.get("AUTH_USER_BY_SEARCH");
    if(!(doUserSearch==null||"".equals(doUserSearch))) {
      _authUserBySearch="true".equalsIgnoreCase(doUserSearch);
    }

    // User bind dn
    String userBindDn = (String) parameters.get("USER_BIND_DN");
    if(!(userBindDn==null||"".equals(userBindDn))) {
      USER_BIND_DN=userBindDn;
    }

  }
  
  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#checkUser(java.lang.String, java.lang.String)
   */
  public boolean checkUser(String username, String password) {
    boolean retVal = false;
    
    try {
      if (username != null && !username.equals("") && password != null
          && !password.equals("")) {
        
        String bindDn = "";
        if(_authUserBySearch) {
          String query = MessageFormat.format(SEARCH_BY_UID,new Object[] {username});
          Logger.debug(null,this,"checkUser","Performing LDAP search " + query);
          bindDn = LDAPInterface.getDN(query);
        } else {
          bindDn = MessageFormat.format(USER_BIND_DN,new Object[] {username});
        }
        Logger.debug(null,this,"checkUser","user bind dn = " + bindDn);
        retVal = LDAPInterface.checkBindPassword(bindDn, password);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return retVal;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#getProfileUsers(java.lang.String)
   */
  public Collection<Map<String,String>> getProfileUsers(String profileID) {
    Collection<Map<String,String>> retObj = null;
    
    Logger.debug(null,this,"getProfileUsers","Performing LDAP search " + LIST_PROFILE_USERS);
    retObj = LDAPInterface.searchDeep(MessageFormat.format(LIST_PROFILE_USERS,new Object[] {profileID}));
    if (retObj.isEmpty() || retObj.size() == 0 || retObj.size() > 1) {
      Logger.debug(null,this,"getProfileUsers","EMPTY PROFILE LIST");
    }
    return retObj;
  }

  public AuthenticationInfo loginUser(String username, String password) {
    AuthenticationInfo retObj = null;
    
    if(checkUser(username, password)) {
      LDAPDirectory directory = LDAPInterface.getDirectory();
      Logger.debug(null,this,"loginUser","Performing LDAP search " + SEARCH_BY_UID);
      Collection<Map<String,String>> users = directory.searchDeep(MessageFormat.format(SEARCH_BY_UID,new Object[] {username}));
      if (users.isEmpty() || users.size() == 0 || users.size() > 1) {
        Logger.debug(null,this,"loginUser","EMPTY USER LIST");
      }
      else {
        Map<String,String> user = users.iterator().next();
        String sessionId = generateSessionId(username);
        directory.replaceAttribute((String)user.get(LDAPDirectory.DN), LDAP_SESSION_ATTR, sessionId);
        retObj = new LDAPAuthenticationInfo(user,sessionId);
      }
    }
    
    return retObj;
    
  }

  public boolean checkUserSession(String username, String sessionId) {

    try {
      if (username != null && !username.equals("") && sessionId != null
          && !sessionId.equals("")) {
        Logger.debug(null,this,"loginUser","Performing LDAP search " + SEARCH_BY_UID);
        Collection<Map<String,String>> users = LDAPInterface.searchDeep(MessageFormat.format(SEARCH_BY_UID,new Object[] {username}));
        if (users.isEmpty() || users.size() == 0 || users.size() > 1) {
          Logger.debug(null,this,"loginUser","EMPTY USER LIST");
        }
        else {
          Map<String,String> user =  users.iterator().next();
          String ldapSessionID = (String) user.get(LDAP_SESSION_ATTR);
          return (ldapSessionID!=null &&ldapSessionID.equals(sessionId));
        }
      }
    }
    catch (Exception e) {
    }

    return false;
  }

  public List<String> getAllUsersForSync(String orgId) {
    List<String> retObj = null;
    try {
      Logger.debug(null, this, "getAllUsersForSync", "Performing LDAP search " + LIST_USERS);
      Collection<Map<String,String>> users = LDAPInterface.searchDeep(LIST_USERS);
      if (users == null) {
        Logger.debug(null,this,"getAllUsersForSync","EMPTY USER LIST");
        retObj = new ArrayList<String>();
      }
      else {
        retObj = new ArrayList<String>(users.size());
        for(Map<String,String> userMap : users) {
          retObj.add(userMap.get(USERID));
        }
      }
    }
    catch (Exception e) {
      Logger.error("ADMIN", this, "getAllUsersForSync", "Error retrieving user list.", e);
    }
    return retObj;
  }

}
