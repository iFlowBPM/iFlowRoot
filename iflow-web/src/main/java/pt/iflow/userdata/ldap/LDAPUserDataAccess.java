/*
 *
 * Created on May 12, 2005 by iKnow
 *
 */

package pt.iflow.userdata.ldap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Name;
import javax.naming.ldap.LdapName;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.userdata.UserData;
import pt.iflow.api.userdata.UserDataAccess;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.ldap.LDAPInterface;
import pt.iflow.ldap.LDAPMapping;
import pt.iflow.userdata.common.MappedData;
import pt.iflow.userdata.common.MappedUserData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class LDAPUserDataAccess implements UserDataAccess {

  private static final String USERID = "userid";
  private String _SEARCH_BY_UID = "";
  private String _SEARCH_BY_FIELD = "";
  private String _LIST_PROFILES ="";
  private String _LIST_USER_PROFILES ="";
  private String _LIST_PROFILE_USERS = "";
  private String _LIST_USERS ="";

  private Map<String,String> _mapping = null;
  protected Map<String,String> _mappingExtra = null;
  private List<LDAPMapping>   _postProc = null;
  
  private LdapName baseName = null;

  public void init(Properties parameters) {
    LDAPInterface.init(parameters);

    this._SEARCH_BY_UID = parameters.getProperty("SEARCH_BY_UID");
    this._SEARCH_BY_FIELD = parameters.getProperty("SEARCH_BY_FIELD");
    this._LIST_PROFILES = parameters.getProperty("LIST_PROFILES");
    this._LIST_USER_PROFILES = parameters.getProperty("LIST_USER_PROFILES");
    this._LIST_PROFILE_USERS = parameters.getProperty("LIST_PROFILE_USERS");
    this._LIST_USERS = parameters.getProperty("LIST_USERS");

    try {
      baseName = new LdapName(LDAPInterface.getBaseDN());
    } catch (Throwable t) {
      Logger.error(null, this, "init", "Could not parse Base Search DN", t);
    }

    Properties mappingFile = Setup.readPropertiesFile(parameters.getProperty("USER_MAPPING_FILE"));
    Map<String,String> map = new Hashtable<String,String>();
    List<LDAPMapping> postProcs = new ArrayList<LDAPMapping>(mappingFile.size());
    for(Object k : mappingFile.keySet()) {
      String key = (String) k;
      String val = mappingFile.getProperty(key);
      LDAPMapping m = LDAPMapping.getMapping(key, val);
      m.setupMapping(MappedData.cleanExtras(parameters), map);
      postProcs.add(m);
    }
    
    this._mapping = Collections.unmodifiableMap(map);
    this._mappingExtra = Collections.unmodifiableMap(Setup.getPropertiesAsStringMap(MappedData.getExtras(parameters)));
    this._postProc = Collections.unmodifiableList(postProcs);
  }

  public UserData getUserData(String userId) {
    UserData retObj = null;

    try {
      if (userId != null && !userId.equals("") ) {

        String search = MessageFormat.format(this._SEARCH_BY_UID,new Object[] {userId});
        Logger.debug(null,this,"getUserData","Performing LDAP search " + search);
        Collection<Map<String,String>> users = LDAPInterface.searchDeep(search);
        if (users.isEmpty() || users.size() == 0 || users.size() > 1) {
          Logger.debug(null,this,"checkUser","EMPTY USER LIST");
          retObj = null;
        }
        else {
          Map<String,String> user = users.iterator().next();
          for (LDAPMapping map : _postProc)
            map.updateAttributes(user);
          retObj = new MappedUserData(user,this._mapping);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return retObj;
  }

  public UserData getUserData(String userId, String searchField) {

    if (USERID.equalsIgnoreCase(searchField)) return getUserData(userId);

    UserData retObj = null;

    try {
      if (StringUtils.isNotEmpty(userId) && _mapping.containsKey(searchField)) {

        searchField = _mapping.get(searchField);
        String search = MessageFormat.format(this._SEARCH_BY_FIELD, new Object[] {searchField, userId});
        Logger.debug(null,this,"getUserData","Performing LDAP search " + search);
        Collection<Map<String,String>> users = LDAPInterface.searchDeep(search);
        if (users.isEmpty() || users.size() == 0 || users.size() > 1) {
          Logger.debug(null,this,"checkUser","EMPTY USER LIST");
          retObj = null;
        }
        else {
          Map<String,String> user = users.iterator().next();
          for (LDAPMapping map : _postProc)
            map.updateAttributes(user);
          retObj = new MappedUserData(user,this._mapping);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return retObj;
  }

  public Collection<String> getUsersInProfile(String profile, String organization) {
    ArrayList<String> retObj = new ArrayList<String>();
    String search = MessageFormat.format(this._LIST_PROFILE_USERS,new Object[] {profile});

    Iterator<Map<String,String>> users = LDAPInterface.searchDeep(search).iterator();

    while(users.hasNext()) {
      String user = users.next().get("member");
      retObj.add(pt.iflow.api.utils.Utils.replaceString(user, "uid=",""));
    }


    return retObj;
  }

  public Collection<String> getUserProfiles(UserInfoInterface userInfo) {
    return getUserProfiles(userInfo.getUserId(), userInfo.getOrganization());
  }

  public Collection<String> getUserProfiles(String userName, String organization) {
    ArrayList<String> retObj = new ArrayList<String>();
    String search = MessageFormat.format(this._LIST_USER_PROFILES,new Object[] {userName});

    Iterator<Map<String,String>> profiles = LDAPInterface.searchDeep(search).iterator();

    while(profiles.hasNext()) {
      String profile = profiles.next().get("cn");
      retObj.add(profile);
    }


    return retObj;
  }

  public Collection<String> getAllProfiles(String organization) {
    ArrayList<String> retObj = new ArrayList<String>();
    Iterator<Map<String,String>> profiles = LDAPInterface.searchDeep(this._LIST_PROFILES).iterator();

    while(profiles.hasNext()) {
      String profile = profiles.next().get("cn");
      retObj.add(profile);
    }

    return retObj;
  }

  public boolean canUserAdmin() {
    return false;
  }

  public boolean canModifyPassword() {
    return false;
  }

  public boolean canModifyUser() {
    return false;
  }

  public boolean canDeleteUser() {
    return false;
  }

  public String getOrganizationalUnitID(String userId) {

    String retObj = null;

    try {
      if (StringUtils.isNotEmpty(userId)) {

        String search = MessageFormat.format(this._SEARCH_BY_UID,new Object[] {userId});
        Logger.debug(null,this,"getOrganizationalUnitID","Performing LDAP search " + search);
        String dn = LDAPInterface.getDN(search);
        if (StringUtils.isEmpty(dn)) {
          Logger.debug(null,this,"checkUser","EMPTY DN");
          retObj = null;
        }
        else {
          // build parent DN without base DN
          Name name = new LdapName(dn);
          if(name.startsWith(baseName))
            name = name.getSuffix(baseName.size());
          // remove first
          name = name.getPrefix(name.size()-1);  // 0 based size 
          retObj = name.toString();
        }
      }
    }
    catch (Exception e) {
      Logger.error(null,this,"getOrganizationalUnitID","Error Performing LDAP search " + e.getMessage(), e);
    }

    return retObj;
  }

  public Collection<String> getOrganizationAdmins(String organization) {
	  // TODO Auto-generated method stub
	  return null;
  }
  
  public String fixUsername(String username) {
    return username;
  }

  public Collection<UserData> getAllUsers(String organization) {
    List<UserData> retObj = null;

    try {
      String search = this._LIST_USERS;
      Logger.debug(null,this,"getAllUsers","Performing LDAP search " + search);
      Collection<Map<String,String>> users = LDAPInterface.searchDeep(search);
      if (users == null) {
        Logger.debug(null,this,"getAllUsers","EMPTY USER LIST");
        retObj = null;
      }
      else {
        retObj = new ArrayList<UserData>(users.size());
        for(Map<String,String> userMap : users) {
          for (LDAPMapping map : _postProc) {
            map.updateAttributes(userMap);
          }
          retObj.add(new MappedUserData(userMap,this._mapping));
        }
      }
    }
    catch (Exception e) {
      Logger.error("ADMIN", this, "getAllUsers", "Error retrieving user list.", e);
    }

    return retObj;
  }
  
  public Collection<String> getUserProfilesForUser(UserInfoInterface userInfo,
      String username) {
    return getUserProfiles(username, userInfo.getOrganization());
  }

  public List<String> getListExtraProperties() {
    if (_mappingExtra == null) return null;
    List<String> retObj = new ArrayList<String>();
    for (String key : _mappingExtra.keySet()) {
      retObj.add(key);
    }
    return retObj;
  }

  public Map<String,String> getMappingExtra(){
    return _mappingExtra;
  }
}
