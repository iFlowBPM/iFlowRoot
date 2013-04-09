package pt.iflow.usersync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import pt.iflow.api.authentication.Authentication;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;
import pt.iflow.core.AccessControlManager;

public class UsersSyncManager extends Thread {

  private static UsersSyncManager instance = null;
  private static final String INSERT_USER = "UsersSyncManager.INSERT_USER";
  private int sleepTime = -1;
  private boolean keepRunning = false;
  private ArrayList<String> usersInDB = null;
  private Authentication userAuthentication = null;
  private String orgId = Const.USERSYNC_ORGID;
  private String unitId = Const.USERSYNC_UNITID;

  private UsersSyncManager() {
  }

  public static UsersSyncManager get() {
    if (instance == null) {
      instance = new UsersSyncManager();
    }
    return instance;
  }

  public static void startManager() {
    if (Const.USERSYNC_ON) {
      UsersSyncManager mng = get();
      mng.keepRunning = true;
      mng.start();
    }
  }

  public static void stopManager() {
    if (Const.USERSYNC_ON) {
      UsersSyncManager mng = get();
      mng.keepRunning = false;
      mng.interrupt();
    }
  }

  public void run() {
    if (!Const.USERSYNC_ON) return;

    userAuthentication = AccessControlManager.getAuthentication();
    usersInDB = getAllUsersFromDB();

    sleepTime = Const.USERSYNC_THREAD_CICLE;
    if (sleepTime == -1) {
      // default sleep time 24 hours
      sleepTime = 1440;
    }
    // in minutes -> sleepTime x (60000 milisec)
    sleepTime = sleepTime * 60000;

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
    List<String> users = userAuthentication.getAllUsersForSync(orgId);
    
    Iterator<String> iter = users.iterator();
    while (iter.hasNext()) {
      String user = iter.next();
      if (!usersInDB.contains(user)) {
        addUser(user);
        usersInDB.add(user);
      }
    }
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

  private ArrayList<String> addUser(String user) {
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
      String query = DBQueryManager.processQuery(UsersSyncManager.INSERT_USER, new Object[]{user, unitId});
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
}
