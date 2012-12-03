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

package pt.iflow.api.userdata;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public interface UserDataAccess {

  public abstract UserData getUserData(String userId);
  public abstract UserData getUserData(String userId, String searchField);
  public abstract Collection<String> getUsersInProfile(String profile, String organization);
  public abstract Collection<String> getUserProfiles(UserInfoInterface userInfo);
  public abstract Collection<String> getUserProfiles(String user, String organization);
  public abstract Collection<String> getAllProfiles(String organization);
  public abstract Collection<String> getOrganizationAdmins(String organization);
  public abstract void init(Properties params);
  public abstract boolean canUserAdmin();
  public abstract boolean canModifyUser();
  public abstract boolean canModifyPassword();
  public abstract boolean canDeleteUser();

  public abstract String getOrganizationalUnitID(String user);

  /**
   * Pre process username before login.
   * 
   * @param username
   * @return processed username
   */
  public abstract String fixUsername(String username);

  /**
   * List all users in a organization. Use with caution.
   * 
   * @param organization
   * @return
   */
  public abstract Collection<UserData> getAllUsers(String organization);
  
  public abstract Collection<String> getUserProfilesForUser(UserInfoInterface userInfo, String username);

  public abstract List<String> getListExtraProperties();

  public abstract Map<String,String> getMappingExtra();
}
