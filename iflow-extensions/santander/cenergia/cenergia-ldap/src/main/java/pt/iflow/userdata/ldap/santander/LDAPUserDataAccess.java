package pt.iflow.userdata.ldap.santander;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import pt.iflow.api.userdata.UserData;
import pt.iflow.api.userdata.UserDataAccess;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.common.properties.santander.Constants;
import pt.iflow.userdata.common.santander.MappedData;
import pt.iflow.userdata.common.santander.MappedUserData;
import pt.totta.ldap.api.GetApplicationProfiles;
import pt.totta.ldap.api.GetApplications;
import pt.totta.ldap.api.GetUserInfo;
import pt.totta.ldap.api.GetUserProfiles;
import pt.totta.ldap.api.GetUsersInProfile;

public class LDAPUserDataAccess implements UserDataAccess {

  private Map<String,String> _mapping = null;
  protected Map<String,String> _mappingExtra = null;
  
  public void init(Properties parameters) {
    this._mapping = Collections.unmodifiableMap(Setup.getPropertiesAsStringMap(MappedData.cleanExtras(parameters)));
    this._mappingExtra = Collections.unmodifiableMap(Setup.getPropertiesAsStringMap(MappedData.getExtras(parameters)));
  }

  public UserData getUserData(String userId) {
    MappedUserData retObj = null;

    try {
      if (userId != null && !userId.equals("") ) {

        GetUserInfo gui = new GetUserInfo();
        Map<String,String> userData = gui.business(userId);
        if (userData==null || userData.isEmpty() || userData.size() == 0) {
          Logger.debug(null, this, "getUserData", "EMPTY USER LIST");
          retObj = null;
        }
        else {
          if (userId.equals(Constants.getOrganizationAdminUser()))
            userData.put(UserData.ORGADM, "1");
          else
            userData.put(UserData.ORGADM, "0");
          userData.put(Constants.getMap_OrganizationDescription(), Constants.getOrganizationDescription());
          userData.put(Constants.getMap_OrganizationId(), Constants.getOrganizationId());
          userData.put(Constants.getMap_OrganizationName(), Constants.getOrganizationName());
          retObj = new MappedUserData(userData, this._mapping);
          
        }
      }
    }
    catch (Exception e) {
      Logger.error(userId, this, "getUserData", e.getMessage());
    }

    return retObj;
  }

  public Collection<String> getUsersInProfile(String profile, String organization) {
    List<String> lstAppNames = Constants.getAppNames();
    for (String appName : lstAppNames) {
      if (profile.startsWith(appName + Constants.getAppProfileSeparator())) {
        profile = profile.replace(appName + Constants.getAppProfileSeparator(), "");
        return new GetUsersInProfile().business(appName, profile);
      }
    }
    return null;
  }

  public Collection<String> getUserProfiles(UserInfoInterface userInfo) {
    return getUserProfiles(userInfo.getUserId(), userInfo.getOrganization());
  }

  public Collection<String> getUserProfiles(String userName, String organization) {
    ArrayList<String> retObj =  new ArrayList<String>();
    for (String appName : Constants.getAppNames()) {
      Collection<Hashtable> col = new GetUserProfiles().business(appName, userName);
      if (col != null) {
        
        Logger.error("", "", "", "###NOVA CLASSE###");
        Iterator<Hashtable> s= col.iterator();
        while (s.hasNext()) {
          Hashtable hPerf = s.next();
          String perf = (String)(hPerf.get("pf"));
          Logger.error("", "", "", "###5 - Prefil: " + appName + Constants.getAppProfileSeparator() + perf);
          retObj.add(appName + Constants.getAppProfileSeparator() + perf);
        }
      }
    }

    Collections.sort(retObj);
    return retObj;
  }

  public Collection<String> getAllProfiles(String organization) {
    ArrayList<String> retObj = new ArrayList<String>();
    
    for (String appName : Constants.getAppNames()) {
      Vector v = new GetApplicationProfiles().business(appName);
      Iterator s= v.iterator();
      while (s.hasNext()) {
        Hashtable h = (Hashtable)s.next();
        retObj.add(appName + Constants.getAppProfileSeparator() + (String)(h.get("pf")));
        Collections.sort(retObj);
      }
    }

    Collections.sort(retObj);
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
    UserData data = getUserData(userId);
    return data.getUnitId();
  }

  public Collection<String> getOrganizationAdmins(String organization) {
    ArrayList<String> retObj = new ArrayList<String>();
    retObj.add(Constants.getOrganizationAdminUser());
    return retObj;
  }
  
  public String fixUsername(String username) {
    return username;
  }

  public Collection<UserData> getAllUsers(String organization) {
    //a api não dá isto
    return null;
  }
  
  public Collection<String> getUserProfilesForUser(UserInfoInterface userInfo, String username) {
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
