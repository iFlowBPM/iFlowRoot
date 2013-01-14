/*
 *
 * Created on Jul 29, 2005 by mach
 *
  */

package pt.iflow.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.notification.Notification;
import pt.iflow.api.presentation.FlowAppMenu;
import pt.iflow.api.presentation.FlowApplications;
import pt.iflow.api.presentation.FlowMenu;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 mach</p>
 *
 * @author mach
 */

public class DefaultInfoGenerator {

  public Collection<IFlowData> generateFlowsInfo(UserInfoInterface userInfo, int anCategoryID) {

    ArrayList<IFlowData> retObj = new ArrayList<IFlowData>();
    IFlowData[] tmp = null;

    if (userInfo.isLogged()) {
      if (anCategoryID == FlowApplications.ORPHAN_GROUP_ID) {
        tmp = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo);
        for(int i=0;i<tmp.length;i++) {
          retObj.add(tmp[i]);
        }
      }
      else {
        Collection<IFlowData> flows = BeanFactory.getFlowApplicationsBean().getApplicationFlows(userInfo, anCategoryID);
        return flows;
      }
    }
    return retObj;
  }

  /**
   * generate messages info
   * @param userInfo
   * @return
   */
  public Collection<Notification> generateMessagesInfo(UserInfoInterface userInfo) {
	  Collection<Notification> notifications = BeanFactory.getNotificationManagerBean().listNotifications(userInfo); 
	  return notifications;
  }

  /**
   * generate flow category info
   * @param userInfo
   * @return
   */
  public Collection<FlowAppMenu> generateCategoriesInfo(UserInfoInterface userInfo, int anAppID) {
    FlowApplications appInfo = BeanFactory.getFlowApplicationsBean();
    
    FlowMenu menu = appInfo.getAllApplicationOnlineMenu(userInfo, anAppID, FlowRolesTO.CREATE_PRIV);
    Collection<FlowAppMenu> categories = menu.getAppMenuList(); 
    return categories;
  }
  
  public Collection<FlowAppMenu> generateCategoriesInfoToUpdate(UserInfoInterface userInfo, int anAppID) {
	  FlowApplications appInfo = BeanFactory.getFlowApplicationsBean();
	  
	  FlowMenu menu = appInfo.getAllApplicationOnlineMenu(userInfo, anAppID, FlowRolesTO.WRITE_PRIV);
	  Collection<FlowAppMenu> categories = menu.getAppMenuList(); 
	  return categories;
  }

  /**
   *
   * Generates a given user's task list info, grouped by application and then flows
   *
   * @param userInfo the user's info
   * @return a map with key application name (sorted alphabetically) and value a map with
   *    key flowname and value iterator activities
   */
  public Map<Integer,Iterator<Activity>> generateTaskInfo(UserInfoInterface userInfo) {
    Map<Integer,Iterator<Activity>> retObj = null;

    FlowApplications fa = BeanFactory.getFlowApplicationsBean();
    ProcessManager pm = BeanFactory.getProcessManagerBean();

   // Map<String,Collection<FlowData>> hmAppFlows = null;
    FlowMenu menu = null;

    try {
    	menu = fa.getAllApplicationOnlineFlows(userInfo, null, null);
    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(),this,"generateTaskInfo","Error getting beans or online app flows: " + e.getMessage());
      e.printStackTrace();
    }

    if (menu != null) {
    	Collection<FlowAppMenu> categories = menu.getAppMenuList(); 
    	
      retObj = new TreeMap<Integer,Iterator<Activity>>();

      Iterator<FlowAppMenu> itera = categories.iterator();
      while (itera != null && itera.hasNext()) {
        try {
        	FlowAppMenu appMenu = itera.next();

          Collection<IFlowData> alFD = appMenu.getMenuItems().getFlows();

          for (IFlowData fd : alFD) {
            Iterator<Activity> activities = pm.getUserActivities(userInfo, fd.getId());
            retObj.put(fd.getId(), activities);
          }

        }
        catch (Exception e) {
        }
      }
    }

    return retObj;
  }
}
