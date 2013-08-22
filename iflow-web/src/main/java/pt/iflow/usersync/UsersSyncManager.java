package pt.iflow.usersync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pt.iflow.api.authentication.Authentication;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;
import pt.iflow.core.AccessControlManager;

public class UsersSyncManager extends Thread {

  private static UsersSyncManager instance = null;
  private static final String INSERT_USER = "UsersSyncManager.INSERT_USER";
  private static final String UPDATE_USER = "UsersSyncManager.UPDATE_USER";
  private int sleepTime = -1;
  private boolean keepRunning = false;
  private ArrayList<String> usersInDB = null;
  private static Authentication userAuthentication = null;
  private String orgId = "";
  private String unitId = "";

  private UsersSyncManager() {
  }

  public static UsersSyncManager get() {
    if (instance == null) {
      instance = new UsersSyncManager();
    }
    return instance;
  }

  public static void startManager() {
    userAuthentication = AccessControlManager.getAuthentication();
    if (userAuthentication.doSyncronizeUsers()) {
      UsersSyncManager mng = get();
      mng.keepRunning = true;
      mng.start();
    }
  }

  public static void stopManager() {
    if (userAuthentication.doSyncronizeUsers()) {
      UsersSyncManager mng = get();
      mng.keepRunning = false;
      mng.interrupt();
    }
  }

  public void run() {
    if (!userAuthentication.doSyncronizeUsers()) return;

    orgId = userAuthentication.getSyncOrgId();
    unitId = userAuthentication.getSyncUnitId();

    sleepTime = userAuthentication.getSyncThreadCicle();
    if (sleepTime == -1) {
      // default sleep time 24 hours
      sleepTime = 1440;
    }
    // in minutes -> sleepTime x (60000 milisec)
    sleepTime = sleepTime * 60000;

    usersInDB = getAllUsersFromDB();

    while (keepRunning) {
      try {
        try {
          get().syncUsers();
          if (Logger.isDebugEnabled()) {
            Logger.adminDebug("UsersSyncManager", "run", "NextSleepTime= " + sleepTime + " msec");
          }
        } catch (Exception e) {
          Logger.adminWarning("UsersSyncManager", "run", "Failed to check users: ", e);
        }
        sleep(sleepTime);
      } catch (InterruptedException e) {
        if (keepRunning) {
          Logger.adminInfo("UsersSyncManager", "run", "Thread interrupted: ", e);
        } else {
          Logger.adminInfo("UsersSyncManager", "run", "Stopping event manager...");
        }
      } catch (Exception e) {
        Logger.adminWarning("UsersSyncManager", "run", "Failed to check events: ", e);
      }
    }
  }

  private void syncUsers() {
    do {
      List<Map<String, String>> users = userAuthentication.getUsersForSync();
      
      Iterator<Map<String, String>> iter = users.iterator();
      while (iter.hasNext()) {
        Map<String, String> user = iter.next();
        String userId = user.get(UserData.ID);
        boolean forUpdate = usersInDB.contains(userId); 
        if (!forUpdate || userAuthentication.shouldUpdateUser()) {
          saveUser(getUserValues(user, forUpdate), forUpdate);
        }
        if (!forUpdate) {
          usersInDB.add(userId);
        }
      }
    } while (userAuthentication.hasMoreUserToProcess());
  }
    
  private ArrayList<String> getAllUsersFromDB() {
    ArrayList<String> retObj = new ArrayList<String>();  
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.createStatement();
      String query = "SELECT username FROM users u INNER JOIN organizational_units ou on u.unitid=ou.unitid WHERE ou.organizationid = '" + orgId + "'";
      rs = st.executeQuery(query);
      while (rs.next()) {
        retObj.add(rs.getString("username"));
      }
    } catch (SQLException sqle) {
      Logger.adminError("UsersSyncManager", "getAllUsersFromDB", "Error Getting Users from Database", sqle);
    } catch (Exception e) {
      Logger.adminError("UsersSyncManager", "getAllUsersFromDB", "Error Getting Users from Database", e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  private ArrayList<String> saveUser(ArrayList<Object> user, boolean forUpdate) {
    ArrayList<String> retObj = new ArrayList<String>();  
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.createStatement();
      String query = DBQueryManager.processQuery((forUpdate ? UsersSyncManager.UPDATE_USER : UsersSyncManager.INSERT_USER), 
                                                  user.toArray());
      Logger.debug("", this, "saveUser", "query: " + query);
      st.executeUpdate(query);
    } catch (SQLException sqle) {
      Logger.adminError("UsersSyncManager", "addUser", "Error Inserting User from Database", sqle);
    } catch (Exception e) {
      Logger.adminError("UsersSyncManager", "addUser", "Error Inserting User from Database", e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }
  
  private ArrayList<Object> getUserValues(Map<String, String> user, boolean forUpdate) {
    ArrayList<Object> arr = new ArrayList<Object>();
    String stmp = "";
    boolean updateThis = false;
    //unitid
    if (!forUpdate) arr.add(unitId);
    //username
    stmp = user.get(UserData.USERNAME);
    arr.add(stmp != null ? "'" + stmp + "'" : "''");
    //email_address
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.EMAIL_ADDRESS);
    stmp = user.get(UserData.EMAIL_ADDRESS);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "email_address");
    //gender
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.GENDER);
    stmp = user.get(UserData.GENDER);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "gender");
    //first_name
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.FIRST_NAME);
    stmp = user.get(UserData.FIRST_NAME);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "first_name");
    //last_name
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.LAST_NAME);
    stmp = user.get(UserData.LAST_NAME);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "last_name");
    //phone_number
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.PHONE_NUMBER);
    stmp = user.get(UserData.PHONE_NUMBER);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "phone_number");
    //fax_number
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.FAX_NUMBER);
    stmp = user.get(UserData.FAX_NUMBER);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "fax_number");
    //mobile_number
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.MOBILE_NUMBER);
    stmp = user.get(UserData.MOBILE_NUMBER);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "mobile_number");
    //company_phone
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.COMPANY_PHONE);
    stmp = user.get(UserData.COMPANY_PHONE);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "company_phone");
    //department
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.DEPARTMENT);
    stmp = user.get(UserData.DEPARTMENT);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "department");
    //employee_number
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.EMPLOYEE_NUMBER);
    stmp = user.get(UserData.EMPLOYEE_NUMBER);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "employee_number");
    //manager
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.MANAGER);
    stmp = user.get(UserData.MANAGER);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "manager");
    //title
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.TITLE);
    stmp = user.get(UserData.TITLE);
    arr.add(updateThis ? (stmp != null ? "'" + stmp + "'" : "''") : "title");
    //orgadm
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.ORGADM);
    stmp = user.get(UserData.ORGADM);
    arr.add(updateThis ? (stmp != null ? stmp : "0") : "orgadm");
    //orgadm_users
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.ORGADM_USERS);
    stmp = user.get(UserData.ORGADM_USERS);
    arr.add(updateThis ? (stmp != null ? stmp : "1") : "orgadm_users");
    //orgadm_flows
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.ORGADM_FLOWS);
    stmp = user.get(UserData.ORGADM_FLOWS);
    arr.add(updateThis ? (stmp != null ? stmp : "1") : "orgadm_flows");
    //orgadm_processes
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.ORGADM_PROCESSES);
    stmp = user.get(UserData.ORGADM_PROCESSES);
    arr.add(updateThis ? (stmp != null ? stmp : "1") : "orgadm_processes");
    //orgadm_resources
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.ORGADM_RESOURCES);
    stmp = user.get(UserData.ORGADM_RESOURCES);
    arr.add(updateThis ? (stmp != null ? stmp : "1") : "orgadm_resources");
    //orgadm_org
    updateThis = !forUpdate || userAuthentication.shouldUpdateThisColumn(UserData.ORGADM_ORG);
    stmp = user.get(UserData.ORGADM_ORG);
    arr.add(updateThis ? (stmp != null ? stmp : "1") : "orgadm_org");
    return arr;
  }
}
