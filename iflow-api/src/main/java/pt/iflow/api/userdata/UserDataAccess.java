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
