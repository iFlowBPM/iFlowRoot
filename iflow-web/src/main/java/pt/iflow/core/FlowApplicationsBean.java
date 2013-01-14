/*
 *
 * Created on Oct 17, 2005 by mach
 *
 */

package pt.iflow.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.presentation.ApplicationItem;
import pt.iflow.api.presentation.FlowAppMenu;
import pt.iflow.api.presentation.FlowApplications;
import pt.iflow.api.presentation.FlowMenu;
import pt.iflow.api.presentation.FlowMenuItems;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class FlowApplicationsBean implements FlowApplications {

  private static final String SQL_GET_APPS="select LINKID,NAME from links_flows where parentid=0 and flowid=0 and organizationid=?";
  private static final String SQL_GET_SINGLE_APP="select LINKID,NAME from links_flows where organizationid=? and linkid=?";
  private static final String SQL_GET_APP_FLOWS="select LINKID,FLOWID,NAME from links_flows where parentid=? and organizationid=?";
  private static final String SQL_GET_APP_ONLINE_FLOWS="select distinct a.linkid,a.flowid,a.name,a.url from links_flows a, flow b where a.parentid=? and a.organizationid=b.organizationid and b.organizationid=? and ((a.flowid=b.flowid and b.enabled=1) or (a.flowid=0)) order by a.linkid";

  private static final long serialVersionUID = 1L;
  
  private static FlowApplicationsBean instance = new FlowApplicationsBean();
  
  public static FlowApplicationsBean getInstance() {
    if(null == instance)
      instance = new FlowApplicationsBean();
    return instance;
  }

  protected Collection<ApplicationItem> getApplicationList(UserInfoInterface userInfo){
    ArrayList<ApplicationItem> retObj = new ArrayList<ApplicationItem>();

    DataSource ds = Utils.getDataSource();
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    try {
      db = ds.getConnection();
      st = db.prepareStatement(SQL_GET_APPS);
      st.setString(1, userInfo.getCompanyID());
      rs = st.executeQuery();

      while (rs.next()) {
        retObj.add(new ApplicationItem(rs.getInt("LINKID"), rs.getString("NAME")));
      }
      rs.close();
    }
    catch (SQLException sqle) {
      sqle.printStackTrace();
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return retObj;
  }

  protected Collection<ApplicationItem> getApplicationByID(UserInfoInterface userInfo, int anAppID){
    ArrayList<ApplicationItem> retObj = new ArrayList<ApplicationItem>();

    DataSource ds = Utils.getDataSource();
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    try {
      db = ds.getConnection();
      st = db.prepareStatement(SQL_GET_SINGLE_APP);
      st.setString(1, userInfo.getCompanyID());
      st.setInt(2, anAppID);
      rs = st.executeQuery();

      while (rs.next()) {
        retObj.add(new ApplicationItem(rs.getInt("LINKID"), rs.getString("NAME")));
      }
      rs.close();
    }
    catch (SQLException sqle) {
      sqle.printStackTrace();
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return retObj;
  }

  public Collection<IFlowData> getApplicationFlows(UserInfoInterface userInfo,int anAppID){
    ArrayList<IFlowData> retObj = new ArrayList<IFlowData>();

    DataSource ds = Utils.getDataSource();
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    Flow flow = BeanFactory.getFlowBean();

    try {
      db = ds.getConnection();
      st = db.prepareStatement(SQL_GET_APP_FLOWS);
      st.setLong(1, anAppID);
      st.setString(2, userInfo.getCompanyID());
      rs = st.executeQuery();

      while (rs.next()) {
        String id = rs.getString("FLOWID");

        IFlowData aFlow = flow.getFlow(userInfo, Integer.parseInt(id));
        
        if (aFlow != null) {
          retObj.add(aFlow);
        }
      }
      rs.close();
    }
    catch (Exception sqle) {
      sqle.printStackTrace();
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return retObj;
  }

  public FlowMenu getAllApplicationOnlineFlows(UserInfoInterface userInfo) {
    return getAllApplicationOnlineFlows(userInfo, FlowApplications.ORPHAN_GROUP_ID, null, null);
  }
  public FlowMenu getAllApplicationOnlineFlows(UserInfoInterface userInfo, FlowType type) {
    return getAllApplicationOnlineFlows(userInfo, FlowApplications.ORPHAN_GROUP_ID, type, null);
  }
  
  public FlowMenu getAllApplicationOnlineFlows(UserInfoInterface userInfo, FlowType type, FlowType[] typeExclude) {
    return getAllApplicationOnlineFlows(userInfo, FlowApplications.ORPHAN_GROUP_ID, type, typeExclude);
  }
  
  public FlowMenu getAllApplicationOnlineFlows(UserInfoInterface userInfo, int anAppID) {
    return getAllApplicationOnlineFlows(userInfo, anAppID, null, null);
  }
  
  public FlowMenu getAllApplicationOnlineFlows(UserInfoInterface userInfo, int anAppID, FlowType type, FlowType[] typeExclude) {
   
   FlowMenu menu = new FlowMenu();

   DataSource ds = Utils.getDataSource();
   Connection db = null;
   PreparedStatement pst = null;
   ResultSet rs = null;

   try {
     // first get all online flows
     IFlowData[] fdOnlineFlows = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, type, typeExclude);

     // put them in an hashmap for fast index
     HashMap<String,IFlowData> hmFlows = new HashMap<String,IFlowData>();
     for (int i=0; i < fdOnlineFlows.length; i++) {
       hmFlows.put(String.valueOf(fdOnlineFlows[i].getId()), fdOnlineFlows[i]);
     }
     fdOnlineFlows = null;

     // now get applications' info and update flow data
     Collection<ApplicationItem> colApplications = null;
     if (anAppID == ORPHAN_GROUP_ID) {
       colApplications = this.getApplicationList(userInfo);       
     }
     else {
       colApplications = this.getApplicationByID(userInfo, anAppID);
     }
     // now get application flows
     db = ds.getConnection();
     pst = db.prepareStatement(SQL_GET_APP_ONLINE_FLOWS);

     for (ApplicationItem item : colApplications) {

    	 FlowAppMenu appMenu = null;

    	 if ((appMenu = menu.getAppMenu(item.getLinkid())) == null) {
    		 appMenu = new FlowAppMenu(item.getLinkid(), item.getText());
    	 }

    	 FlowMenuItems appMenuItems = appMenu.getMenuItems();

    	 pst.setLong(1, item.getLinkid());
    	 pst.setString(2, userInfo.getCompanyID());
    	 rs = pst.executeQuery();

    	 while (rs.next()) {
    		 String sFlowId = rs.getString("FLOWID");

    		 IFlowData fd = hmFlows.remove(sFlowId);

    		 if (fd != null) {
    			 fd.setApplicationId(String.valueOf(item.getLinkid()));
    			 fd.setApplicationName(item.getText());
    			 appMenuItems.addFlowData(fd);
    		 }
    	 }
    	 rs.close();
    	 rs = null;

    	 appMenu.setMenuItems(appMenuItems);
    	 menu.addAppMenu(appMenu);	 
     }
     colApplications = null;
     pst.close();
     pst = null;

     if (hmFlows.size() > 0) {
    	 FlowAppMenu orphanControl = new FlowAppMenu(ORPHAN_GROUP_ID, ORPHAN_GROUP_DESC);
    	 FlowMenuItems menuItems = new FlowMenuItems();
       // now uncategorized flows

       Iterator<String> itera = hmFlows.keySet().iterator();
       while (itera != null && itera.hasNext()) {
         String sFlowId = itera.next();

         IFlowData fd = hmFlows.get(sFlowId);
         if (fd != null) {
        	 menuItems.addFlowData(fd);
         }
       }
       orphanControl.setMenuItems(menuItems);

       if (!orphanControl.isEmpty()) {
    	   
         menu.addAppMenu(orphanControl);
       }
     }

     hmFlows = null;
   }
   catch (Exception e) {
        Logger.error(userInfo.getUtilizador(),this,"getFlows","exception (" + e.getClass().getName() + ") caught: " + e.getMessage());
        e.printStackTrace();
   }
   finally {
     DatabaseInterface.closeResources(db, pst, rs);
   }
   return menu;
 }
 
  
    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo) {
      return getAllApplicationOnlineMenu(userInfo, FlowApplications.ORPHAN_GROUP_ID, null, null);
    }
    
    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo, FlowType type) {
      return getAllApplicationOnlineMenu(userInfo, FlowApplications.ORPHAN_GROUP_ID, type, null);
    }

    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo, FlowType type, boolean showOnlyFlowsToBePresentInMenu) {
      return getAllApplicationOnlineMenu(userInfo, FlowApplications.ORPHAN_GROUP_ID, type, null, showOnlyFlowsToBePresentInMenu);
    }

    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo, FlowType type, FlowType[] typeExclude) {
      return getAllApplicationOnlineMenu(userInfo, FlowApplications.ORPHAN_GROUP_ID, type, typeExclude);
    }

    public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, FlowType type, FlowType[] typeExclude, boolean showOnlyFlowsToBePresentInMenu){
      return getAllApplicationOnlineMenu(userInfo, FlowApplications.ORPHAN_GROUP_ID, type, typeExclude, showOnlyFlowsToBePresentInMenu);
    }

    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo, int anAppID) {
      return getAllApplicationOnlineMenu(userInfo, anAppID, null, null);
    }
    
    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo, int anAppID, char flowRolesTOPriv) {
      return getAllApplicationOnlineMenu(userInfo, anAppID, null, null, flowRolesTOPriv);
    }

    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo, int anAppID, FlowType type, FlowType[] typeExclude) {
      return getAllApplicationOnlineMenu(userInfo, anAppID, type, typeExclude, FlowRolesTO.CREATE_PRIV, false);
    }

    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo, int anAppID, FlowType type, FlowType[] typeExclude, boolean showOnlyFlowsToBePresentInMenu) {
      return getAllApplicationOnlineMenu(userInfo, anAppID, type, typeExclude, FlowRolesTO.CREATE_PRIV, showOnlyFlowsToBePresentInMenu);
    }

    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo, int anAppID, FlowType type, 
        FlowType[] typeExclude, char flowRolesTOPriv){
      return getAllApplicationOnlineMenu(userInfo, anAppID, type, typeExclude, flowRolesTOPriv, false);
    }
        
    public FlowMenu getAllApplicationOnlineMenu(UserInfoInterface userInfo, int anAppID, FlowType type, 
        FlowType[] typeExclude, char flowRolesTOPriv, boolean showOnlyFlowsToBePresentInMenu) {

	  FlowMenu menu = new FlowMenu();
	  
	  DataSource ds = Utils.getDataSource();
	  Connection db = null;
	  PreparedStatement pst = null;
	  ResultSet rs = null;

	  try {
		  // first get all online flows
	    IFlowData[] fdOnlineFlows = null;
	    fdOnlineFlows = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, type, typeExclude, showOnlyFlowsToBePresentInMenu);

		  // put them in an hashmap for fast index
		  OrderedMap<Integer,IFlowData> hmFlows = new ListOrderedMap<Integer,IFlowData>();
		  for (int i=0; i < fdOnlineFlows.length; i++) {
			  if(BeanFactory.getFlowBean().checkUserFlowRoles(userInfo, fdOnlineFlows[i].getId(), "" + flowRolesTOPriv))
				  hmFlows.put(fdOnlineFlows[i].getId(), fdOnlineFlows[i]);
		  }
		  fdOnlineFlows = null;

      // now get applications' info and update flow data
      Collection<ApplicationItem> colApplications = null;
	     if (anAppID == ORPHAN_GROUP_ID) {
	       colApplications = this.getApplicationList(userInfo);       
	     }
	     else {
	       colApplications = this.getApplicationByID(userInfo, anAppID);
	     }

		  // now get application flows/URLs
		  db = ds.getConnection();
		  pst = db.prepareStatement(SQL_GET_APP_ONLINE_FLOWS);

		  for (ApplicationItem item : colApplications) {

			  
			  FlowAppMenu appMenu = null;

			  if ((appMenu = menu.getAppMenu(item.getLinkid())) == null) {
				  appMenu = new FlowAppMenu(item.getLinkid(), item.getText());
			  }

			  FlowMenuItems menuPart = appMenu.getMenuItems();

			  pst.setLong(1, item.getLinkid());
			  pst.setString(2, userInfo.getCompanyID());
			  rs = pst.executeQuery();

			  while (rs.next()) {
				  int flowId = rs.getInt("FLOWID");

				  if(flowId == 0) {
					  // this is a link
					  String url = rs.getString("URL");
					  String name = rs.getString("NAME");
					  ApplicationItem menuItem = new ApplicationItem(0, name, url);
					  menuPart.addLink(menuItem);
				  } else {
					  IFlowData fd = hmFlows.remove(flowId);

					  if (fd != null) {
						  fd.setApplicationId(String.valueOf(item.getLinkid()));
						  fd.setApplicationName(item.getText());
						  menuPart.addFlowData(fd);
					  }
				  }
			  }
			  rs.close();
			  rs = null;

			  appMenu.setMenuItems(menuPart);

			  if(!appMenu.isEmpty()){
				  menu.addAppMenu(appMenu);
			  }
		  }
		  colApplications = null;
		  pst.close();
		  pst = null;

		  if (hmFlows.size() > 0) {
			  // now uncategorized flows
			  FlowAppMenu orphanControl = null;
			  FlowMenuItems menuPart = null;

			  if ((orphanControl = menu.getAppMenu(ORPHAN_GROUP_ID)) == null) {
				  orphanControl = new FlowAppMenu(ORPHAN_GROUP_ID, ORPHAN_GROUP_DESC);
			  }
			  menuPart = orphanControl.getMenuItems();

			  Iterator<Integer> itera = hmFlows.keySet().iterator();
			  while (itera != null && itera.hasNext()) {
				  Integer sFlowId = itera.next();

				  IFlowData fd = (IFlowData)hmFlows.get(sFlowId);
				  if (fd != null) {
					  menuPart.addFlowData(fd);
				  }
			  }

			  orphanControl.setMenuItems(menuPart);
			  if(!orphanControl.isEmpty()){
				  menu.addAppMenu(orphanControl);
			  }
		  }

		  hmFlows = null;
	  }
	  catch (Exception e) {
		  Logger.error(userInfo.getUtilizador(),this,"getFlows","exception (" + e.getClass().getName() + ") caught: " + e.getMessage());
		  e.printStackTrace();
	  }
	  finally {
	    DatabaseInterface.closeResources(db, pst, rs);
	  }
	  return menu;
  }
}