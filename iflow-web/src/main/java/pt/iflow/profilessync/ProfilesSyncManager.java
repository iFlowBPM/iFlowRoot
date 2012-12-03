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
package pt.iflow.profilessync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.sql.DataSource;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.userdata.UserDataAccess;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;
import pt.iflow.core.AccessControlManager;

public class ProfilesSyncManager extends Thread {

  private static ProfilesSyncManager instance = null;
  private static final String INSERT_PROFILE = "ProfilesSyncManager.INSERT_PROFILE";
  private int sleepTime = -1;
  private boolean keepRunning = false;
  private ArrayList<String> profilesInDB = null;
  private UserDataAccess userDataAcess = null;
  private String orgId = Const.PROFILESYNC_ORGID;

  private ProfilesSyncManager() {
  }

  public static ProfilesSyncManager get() {
    if (instance == null) {
      instance = new ProfilesSyncManager();
    }
    return instance;
  }

  public static void startManager() {
    if (Const.PROFILESYNC_ON) {
      ProfilesSyncManager mng = get();
      mng.keepRunning = true;
      mng.start();
    }
  }

  public static void stopManager() {
    if (Const.PROFILESYNC_ON) {
      ProfilesSyncManager mng = get();
      mng.keepRunning = false;
      mng.interrupt();
    }
  }

  public void run() {
    if (!Const.PROFILESYNC_ON) return;

    userDataAcess = AccessControlManager.getUserDataAccess();
    //TODO: melhorar a forma de obter o orgid. Esta forma é muito reboscada e pouco correcta.
    //Vai buscar os admins da org vazia (que no caso da obrecol são martelados)
    profilesInDB = getAllProfilesFromDB();

    sleepTime = Const.PROFILESYNC_THREAD_CICLE;
    if (sleepTime == -1) {
      // default sleep time 24 hours
      sleepTime = 1440;
    }
    // in minutes -> sleepTime x (60000 milisec)
    sleepTime = sleepTime * 60000;

    while (keepRunning) {
      try {
        try {
          get().syncProfiles();
          if (Logger.isDebugEnabled()) {
            Logger.adminDebug("ProfilesSyncManager", "run", "NextSleepTime= " + sleepTime + " msec");
          }
        } catch (Exception e) {
          Logger.adminWarning("ProfilesSyncManager", "run", "Failed to check profiles: ", e);
        }
        sleep(sleepTime);
      } catch (InterruptedException e) {
        if (keepRunning) {
          Logger.adminInfo("ProfilesSyncManager", "run", "Thread interrupted: ", e);
        } else {
          Logger.adminInfo("ProfilesSyncManager", "run", "Stopping event manager...");
        }
      } catch (Exception e) {
        Logger.adminWarning("ProfilesSyncManager", "run", "Failed to check events: ", e);
      }
    }
  }

  private void syncProfiles() {
    Collection<String> profiles = userDataAcess.getAllProfiles(orgId);
    
    Iterator<String> iter = profiles.iterator();
    while (iter.hasNext()) {
      String profile = iter.next();
      if (!profilesInDB.contains(profile)) {
        addProfile(profile);
        profilesInDB.add(profile);
      }
    }
  }
    
  private ArrayList<String> getAllProfilesFromDB() {
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
      String query = "SELECT name FROM profiles WHERE organizationid = '" + orgId + "'";
      rs = st.executeQuery(query);
      while (rs.next()) {
        retObj.add(rs.getString("name"));
      }
    } catch (SQLException sqle) {
      Logger.adminError("ProfilesSyncManager", "getAllProfilesFromDB", "Error Getting Profiles from Database", sqle);
    } catch (Exception e) {
      Logger.adminError("ProfilesSyncManager", "getAllProfilesFromDB", "Error Getting Profiles from Database", e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  private ArrayList<String> addProfile(String profile) {
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
      String query = DBQueryManager.processQuery(ProfilesSyncManager.INSERT_PROFILE, new Object[]{profile, profile, orgId});
      st.executeUpdate(query);
    } catch (SQLException sqle) {
      Logger.adminError("ProfilesSyncManager", "addProfile", "Error Inserting Profile from Database", sqle);
    } catch (Exception e) {
      Logger.adminError("ProfilesSyncManager", "addProfile", "Error Inserting Profile from Database", e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }
}
