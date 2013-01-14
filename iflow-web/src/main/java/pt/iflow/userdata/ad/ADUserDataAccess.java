/*
 *
 * Created on May 12, 2005 by iKnow
 *
 */

package pt.iflow.userdata.ad;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.Name;
import javax.naming.ldap.LdapName;

import org.apache.commons.lang.ArrayUtils;
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

public class ADUserDataAccess implements UserDataAccess {

  private static final String USERID = "userid";
  private String _SEARCH_BY_UID = "";
  private String _SEARCH_BY_FIELD = "";
  private String _LIST_PROFILES ="";
  private String _LIST_USER_PROFILES ="";
  private String _LIST_PROFILE_USERS = "";
  private String _ORG_ADM_PROFILE = "";
  private String _PROFILE_SEPARATOR = "";
  private String _SEARCH_BY_COMPLEX_ID = "";
  private String _LIST_USERS ="";
  
  private boolean stripDomain = false;

  protected Map<String,String> _mapping = null;
  protected Map<String,String> _mappingExtra = null;
  protected List<LDAPMapping>   _postProc = null;
  
  protected LdapName baseName = null;

  public void init(Properties parameters) {
    LDAPInterface.init(parameters);

    this._SEARCH_BY_UID = parameters.getProperty("SEARCH_BY_UID");
    this._SEARCH_BY_FIELD = parameters.getProperty("SEARCH_BY_FIELD");
    this._LIST_PROFILES = parameters.getProperty("LIST_PROFILES");
    this._LIST_USER_PROFILES = parameters.getProperty("LIST_USER_PROFILES");
    this._LIST_PROFILE_USERS = parameters.getProperty("LIST_PROFILE_USERS");
    this._ORG_ADM_PROFILE = parameters.getProperty("ORG_ADM_PROFILE");
    this._PROFILE_SEPARATOR = parameters.getProperty("PROFILE_SEPARATOR");
    this._SEARCH_BY_COMPLEX_ID = parameters.getProperty("SEARCH_BY_COMPLEX_ID");
    this._LIST_USERS = parameters.getProperty("LIST_USERS");
    String stripDomain = parameters.getProperty("STRIP_DOMAIN");
    this.stripDomain = ArrayUtils.contains(new String[]{"true","yes"}, stripDomain==null?null:stripDomain.toLowerCase());
    
    try {
      baseName = new LdapName(LDAPInterface.getBaseDN());
    } catch (Throwable t) {
      Logger.error(null, this, "init", "Could not parse Base Search DN", t);
    }

    Properties mappingFile = Setup.readPropertiesFile(parameters.getProperty("USER_MAPPING_FILE"));
    Map<String,String> map = new Hashtable<String,String>();
    List<LDAPMapping> postProcs = new ArrayList<LDAPMapping>(mappingFile.size()+2);

    postProcs.add(new LDAPMapping() { // USER_DN
      private static final long serialVersionUID = -3480053072348853399L;

      public void setupMapping(Properties parameters, Map<String, String> map) {
      }

      public void updateAttributes(Map<String, String> map) {
          map.put("USER_DN", map.get("distinguishedName"));
      }
    });
    
    postProcs.add(new LDAPMapping() {
      private static final long serialVersionUID = -3480053076108853399L;

      public void setupMapping(Properties parameters, Map<String, String> map) {
      }

      public void updateAttributes(Map<String, String> map) {
        String memberOf = map.get("memberOf");
        if(null != memberOf && memberOf.contains(ADUserDataAccess.this._ORG_ADM_PROFILE)) {
          map.put("ORGADM", "1");
        } else {
          map.put("ORGADM", "0");
        }
      }
    });
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
      if (StringUtils.isNotEmpty(userId)) {

        String search = MessageFormat.format(this._SEARCH_BY_UID,userId);
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
          
          // ja ca estava... nao me lembro onde eh usado.
          // String unitID = "";
          // String orgName = "";
          // 
          // user.put("unitID", unitID);
          // user.put("orgName", orgName);
          
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
        String search = MessageFormat.format(this._SEARCH_BY_FIELD, searchField, userId);
        Logger.debug(null, this, "getUserData", "Performing LDAP search " + search);
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
    String search = MessageFormat.format(this._LIST_PROFILE_USERS,this.formatGroupID(profile));
    Logger.debug("", this, "", "____________________________________");
    Logger.debug("", this, "", "SEARCH = " + search);
    Logger.debug("", this, "", "____________________________________");
    Collection<Map<String,String>> users = LDAPInterface.searchDeep(search);

    ArrayList<String> retObj = new ArrayList<String>(users!=null?users.size():0);

    if(null != users) {
      for(Map<String,String> userMap : users) {
        String user = userMap.get("userPrincipalName");
        String userSearch = MessageFormat.format(this._SEARCH_BY_COMPLEX_ID, user);

        Collection<Map<String,String>> colUserData = LDAPInterface.searchDeep(userSearch);
        
        if (colUserData != null && colUserData.size() > 0) {
          Map<String,String> userData = colUserData.iterator().next();
          String userID = (String)userData.get("sAMAccountName");
          retObj.add(userID);
        }
      }
    }

    return retObj;
  }

  public Collection<String> getUserProfiles(UserInfoInterface userInfo) {
    return getUserProfiles(userInfo.getUserId(), userInfo.getOrganization());
  }

  public Collection<String> getUserProfiles(String userName, String organization) {
    UserData tmpUser = getUserData(userName);
    String search = MessageFormat.format(this._LIST_USER_PROFILES, tmpUser.get("USER_DN"));

    Collection<Map<String,String>> profiles = LDAPInterface.searchDeep(search);

    ArrayList<String> retObj = new ArrayList<String>(profiles!=null?profiles.size():0);

    if(null != profiles) {
      for(Map<String,String> profMap : profiles) {
        String profile = profMap.get("distinguishedName");
        profile = parseGroupID(profile);
        Logger.debug("", this, "", "USER PROFILE = " + profile);
        retObj.add(profile);
      }
    }

    return retObj;
  }

  private String parseGroupID(String profile) {
    try {
      Name name = new LdapName(profile);
      if(name.startsWith(baseName))
        name = name.getSuffix(baseName.size());
      profile = name.toString();
    } catch (Throwable t) {}

    profile = profile.replace("CN=", "");
    profile = profile.replace("OU=", "");

    StringTokenizer tokenizer = new StringTokenizer(profile,",");
    String token = "";
    String newProfile = "";
    while(tokenizer.hasMoreTokens()) {
      token = tokenizer.nextToken();
      newProfile = token + _PROFILE_SEPARATOR + newProfile; 
    }
    if(newProfile.endsWith(_PROFILE_SEPARATOR))
      newProfile = newProfile.substring(0, newProfile.length()-1);

    return newProfile;
  }

  private String formatGroupID(String profile) {
      StringTokenizer tokenizer = new StringTokenizer(profile,_PROFILE_SEPARATOR);
      String token = "";
      String newProfile = "";
      boolean isFirst = true;
      while(tokenizer.hasMoreTokens()) {
    	  token = tokenizer.nextToken();
    	  if(!isFirst)
    		  newProfile = token + ",OU=" + newProfile;
    	  else
    		  newProfile = token;
    	  isFirst = false;
      }
      
      newProfile = "CN=" + newProfile; 

      newProfile =  newProfile + "," + baseName;
      
      return newProfile;
  }
  public Collection<String> getAllProfiles(String organization) {
    Collection<Map<String,String>> profiles = LDAPInterface.searchDeep(this._LIST_PROFILES);

    ArrayList<String> retObj = new ArrayList<String>(profiles!=null?profiles.size():0);

    if(null != profiles) {
      for(Map<String,String> profMap : profiles) {
        String profile = profMap.get("distinguishedName");
        retObj.add(parseGroupID(profile));
      }
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
        String search = MessageFormat.format(this._SEARCH_BY_UID, userId);
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
          if(name.size() > 0) {  // not at top base
            // remove first
            name = name.getPrefix(name.size()-1);  // 0 based size
          }
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
	  return getUsersInProfile(ADUserDataAccess.this._ORG_ADM_PROFILE, organization);
  }
  
  public String fixUsername(String username) {
    if(null == username) return null;
    if(!stripDomain) return username;
    String [] slashTokens = username.split("[/\\\\]");
    String [] atTokens = username.split("@");
    
    if(slashTokens.length == 2 && atTokens.length == 1) {
      // username in the form DOMAIN/USER
      return slashTokens[1];
    }
    else if(slashTokens.length == 1 && atTokens.length == 2) {
      // username in the form USER@DOMAIN
      return atTokens[0];
    }
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
