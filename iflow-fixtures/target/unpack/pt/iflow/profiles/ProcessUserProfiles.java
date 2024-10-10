package pt.iflow.profiles;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.core.UserManager;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class ProcessUserProfiles {

  /**
   * Add users to profile
   * @param userInfo UserInfo
   * @param users Users to add
   * @param profileName Profile Name
   * @param profileId Profile ID
   * @param actProcess Process Activities
   */
  public static boolean addToProfile(UserInfoInterface userInfo, String[] users, String profileName, String profileId, boolean actProcess) {
    String userid = userInfo.getUtilizador();

    UserManager USER_MANAGER = BeanFactory.getUserManagerBean();
    ProcessManager PROCESS_MANAGER = BeanFactory.getProcessManagerBean();

    try {
      ListIterator<Activity> activities = PROCESS_MANAGER.getActivities(userInfo, profileName);
      List<Activity> activityList = new ArrayList<Activity>();
      while (activities != null && activities.hasNext()) {
        activityList.add(activities.next());
      }

      for(int i = 0; i < users.length; i++) {
        List<Activity> newActivities = new ArrayList<Activity>();
        USER_MANAGER.addUserProfile(userInfo, users[i], profileId);
        if(users[i].equals(userInfo.getUserId())) {
          userInfo.updateProfiles();
          userInfo.updatePrivileges();
        }		 
        if(actProcess) {
          String userusername = USER_MANAGER.getUser(userInfo, users[i]).getUsername();
          for (Activity activity : activityList) {
            activity.userid = userusername;
            activity.setUnread();
            activity.notify = false;
            newActivities.add(activity);
          }		        	
        }
        // Add new users to activities
        PROCESS_MANAGER.createActivities(userInfo, newActivities.listIterator());
      }		     		        
    }
    catch (Exception e) {
      Logger.error(userid, "ProcessUserProfiles", "addToProfile", "Exception: " + e.getMessage(), e);
      return false;
    }
    
    return true;
  }

  /**
   * Delete Users from profile
   * @param userInfo User Info
   * @param users Users to delete
   * @param profileId Profile ID
   * @param actProcess Process Activities
   */
  public static boolean deleteFromProfile(UserInfoInterface userInfo, String[] users, String profileId, boolean actProcess) {
    String userid = userInfo.getUtilizador();
    try {
      UserManager USER_MANAGER = BeanFactory.getUserManagerBean();
      ProcessManager PROCESS_MANAGER = BeanFactory.getProcessManagerBean();

      for(int i = 0; i < users.length; i++) {
        USER_MANAGER.delUserProfile(userInfo, users[i], profileId);
        if(users[i].equals(userInfo.getUserId())) {
          userInfo.updateProfiles();
          userInfo.updatePrivileges();
        }
        if(actProcess) {
          // Remove User Activities          
          PROCESS_MANAGER.deleteActivitiesForProfile(userInfo, USER_MANAGER.getUser(userInfo, users[i]).getUsername(), profileId);
        }
      }
    }
    catch (Exception e) {
      Logger.error(userid, "ProcessUserProfiles", "deleteFromProfile", "Exception: " + e.getMessage(), e);
      return false;
    }
    return true;
  }

}
