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

package pt.iflow.userdata.db;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.userdata.UserDataAccess;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
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

public class DBUserDataAccess implements UserDataAccess {

  private static final String USERID = "userid";
  private Map<String,String> _mapping = null;
  protected Map<String,String> _mappingExtra = null;

  private static final String SQL_GET_USERS_IN_PROFILE =
    "select a.username as USERNAME from users a,profiles b,userprofiles c where c.profileid=b.profileid and c.userid=a.userid and b.name=''{0}'' and b.organizationid=''{1}''";

  private static final String SQL_GET_USER_PROFILES =
    "select a.name as PROFILE from profiles a,userprofiles b, users c where a.profileid=b.profileid and b.userid=c.userid and c.username=''{0}'' and a.organizationid=''{1}''";

  private static final String SQL_GET_ALL_PROFILES =
    "select name as PROFILE from profiles where organizationid=''{0}''";

  private static final String SQL_GET_ORGANIZATION_ADMINS =
	    "select a.username from users a, organizational_units ou where a.orgadm=1 and a.unitid=ou.unitid and ou.organizationid={0}";

  /* (non-Javadoc)
   * @see pt.iknow.userdata.UserDataAccess#getUserData(java.lang.String)
   */
  public UserData getUserData(String userId) {
    MappedUserData retObj = null;

    String sqlGetUserData = DBQueryManager.processQuery("DBUserDataAccess.SQL_GET_USER_DATA", new Object[]{userId});

    Collection<Map<String,String>> users =
      DatabaseInterface.executeQuery(sqlGetUserData);

    if(users.isEmpty()) {
      Logger.error(null,this,"getUserData","No user with id " + userId);
    } else if (users.size() > 1) {
      Logger.error(null,this,"getUserData","More than one user with id " + userId);
    } else {
      Map<String,String> userData = users.iterator().next();
      retObj = new MappedUserData(userData,this._mapping);
    }

    return retObj;
  }

  public UserData getUserData(String userId, String searchField) {
    
    if (USERID.equalsIgnoreCase(searchField)) return getUserData(userId);
    
    MappedUserData retObj = null;

    if (_mapping.containsKey(searchField)) {

      searchField = _mapping.get(searchField);

      String sqlGetUserData = DBQueryManager.processQuery("DBUserDataAccess.SQL_GET_USER_DATA_BY_FIELD", new Object[]{searchField, userId});

      Collection<Map<String,String>> users =
        DatabaseInterface.executeQuery(sqlGetUserData);

      if(users.isEmpty()) {
        Logger.error(null,this,"getUserData","No user with id " + userId);
      } else if (users.size() > 1) {
        Logger.error(null,this,"getUserData","More than one user with id " + userId);
      } else {
        Map<String,String> userData = users.iterator().next();
        retObj = new MappedUserData(userData,this._mapping);
      }
    }

    return retObj;
  }
  
  public Collection<String> getUsersInProfile(String profile, String organization) {

    Collection<Map<String,String>> users =
      DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USERS_IN_PROFILE,new Object[]{profile, organization}));
    ArrayList<String> retObj = new ArrayList<String>(null!=users?users.size():0);

    Iterator<Map<String,String>> iter = users.iterator();
    while(iter.hasNext()) {
      String val = iter.next().get("USERNAME");
      retObj.add(val);
    }

    return retObj;
  }

  public Collection<String> getUserProfiles(UserInfoInterface userInfo) {
    return getUserProfiles(userInfo.getUtilizador(), userInfo.getCompanyID());
  }

  public Collection<String> getUserProfiles(String userName, String organization) {

    Collection<Map<String,String>> users =
      DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USER_PROFILES,new Object[]{userName, organization}));

    ArrayList<String> retObj = new ArrayList<String>(null!=users?users.size():0);


    Iterator<Map<String,String>> iter = users.iterator();
    while(iter.hasNext()) {
      String val = iter.next().get("PROFILE");
      retObj.add(val);
    }

    return retObj;
  }

  public Collection<String> getAllProfiles(String organization) {
    Collection<Map<String,String>> users = DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_ALL_PROFILES, new Object[]{organization}));

    ArrayList<String> retObj = new ArrayList<String>(null!=users?users.size():0);

    Iterator<Map<String,String>> iter = users.iterator();
    while(iter.hasNext()) {
      String val = iter.next().get("PROFILE");
      retObj.add(val);
    }

    return retObj;
  }

  public void init(Properties parameters) {
    this._mapping = Collections.unmodifiableMap(Setup.getPropertiesAsStringMap(MappedData.cleanExtras(parameters)));
    this._mappingExtra = Collections.unmodifiableMap(Setup.getPropertiesAsStringMap(MappedData.getExtras(parameters)));
  }
  
  public boolean canUserAdmin() {
    return true;
  }

  public boolean canModifyPassword() {
    return true;
  }

  public boolean canModifyUser() {
    return true;
  }

  public boolean canDeleteUser() {
    return true;
  }

  public String getOrganizationalUnitID(String user) {
    UserData data = getUserData(user);
    return data.getUnitId();
  }

  public Collection<String> getOrganizationAdmins(String organization) {
	    Collection<Map<String,String>> users = DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_ORGANIZATION_ADMINS, new Object[]{organization}));

	    ArrayList<String> retObj = new ArrayList<String>(null!=users?users.size():0);

	    Iterator<Map<String,String>> iter = users.iterator();
	    while(iter.hasNext()) {
	      String val = iter.next().get("USERNAME");
	      retObj.add(val);
	    }

	    return retObj;
  }
  
  public String fixUsername(String username) {
    return username;
  }

  public Collection<UserData> getAllUsers(String orgId) {
    List<UserData> retObj = null;

    String sqlGetUserData = DBQueryManager.processQuery("DBUserDataAccess.SQL_GET_USERS", new Object[]{orgId});

    Collection<Map<String,String>> users =
      DatabaseInterface.executeQuery(sqlGetUserData);

    if(users == null) {
      Logger.error(null,this,"getAllUsers","No users with orgid " + orgId);
    } else {
      retObj = new ArrayList<UserData>(users.size());
      for(Map<String,String> userData : users) {
        retObj.add(new MappedUserData(userData,this._mapping));
      }
    }

    return retObj;
  }

  public Collection<String> getUserProfilesForUser(UserInfoInterface userInfo, String username) {
    return getUserProfiles(username, userInfo.getCompanyID());
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
