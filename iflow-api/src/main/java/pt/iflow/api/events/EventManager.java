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
package pt.iflow.api.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.sql.DataSource;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * EventManager.java <br />
 * <br />
 * <i>ATTENTION</i> <br />
 * The processed flag value in <i>"event_data"</i> table have the following meanings: <br />
 * 0 - Not processed (ready to process) <br />
 * 1 - Being processed by the EventManagerRemote <br />
 * 2 - Not processed (not ready to process, for example, waiting for an external order)
 * 
 * @author ptgm
 * @author lcabral
 * @since 2005
 * @version 20.08.2009
 */
public class EventManager extends Thread {

  public final static int READY_TO_PROCESS = 0;
  public final static int EVENT_PROCESSED = 1;
  public final static int NOT_READY_TO_PROCESS = 2;

  private static EventManager _eventManager = null;
  private int sleepTime = -1;
  private boolean keepRunning = false;

  private static boolean doLog = true;

  private EventManager() {
  }

  public static EventManager get() {
    if (_eventManager == null) {
      _eventManager = new EventManager();
    }
    return _eventManager;
  }

  public static void startManager() {
    EventManager mng = get();
    mng.keepRunning = true;
    mng.start();
  }

  public static void stopManager() {
    EventManager mng = get();
    mng.keepRunning = false;
    mng.interrupt();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    while (keepRunning) {
      try {
        sleepTime = Const.EVENT_THREAD_CICLE;
        if (sleepTime == -1) {
          // default sleep time 5 minutes
          sleepTime = 5;
        }
        // in minutes -> sleepTime x (60000 milisec)
        sleepTime = sleepTime * 60000;
        get().checkEventsDB();
        if (Logger.isDebugEnabled()) {
          Logger.adminDebug("EventManager", "run", "NextSleepTime= " + sleepTime + " msec");
        }

        sleep(sleepTime);
        doLog = true;
      } catch (InterruptedException e) {
        if (keepRunning) {
          Logger.adminInfo("EventManager", "run", "Thread interrupted: ", e);
        } else {
          Logger.adminInfo("EventManager", "run", "Stopping event manager...");
        }
      } catch (Exception e) {
        if (doLog) {
          Logger.adminWarning("EventManager", "run", "Failed to check events: ", e);
          doLog = false;
        }
      }
    }
  }

  /**
   * Apaga todos os eventos do bloco, mesmo os que não estão ainda a ser processados (processed = 0).
   */
  public boolean deRegisterEvent(UserInfoInterface userInfo, int fid, int pid, int subpid, int blockid) {
    boolean result = false;
    Connection db = null;
    Statement st = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();
      String removeEvtQuery = DBQueryManager.processQuery("EventManager.REMOVE_BLOCK_EVENTS", 
          new Object[] { String.valueOf(fid), String.valueOf(pid), String.valueOf(subpid), String.valueOf(blockid) });
      st.executeUpdate(removeEvtQuery);
      result = true;
    } catch (SQLException e) {
      Logger.error(userInfo.getUtilizador(), this, "deRegisterEvent", "Could not delete event: ", e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }
    return result;
  }

  public boolean registerEvent(UserInfoInterface userInfo, int fid, int pid, int subpid, int blockid, String type, String properties) {
    boolean registerStatus = false;
    Connection db = null;
    PreparedStatement pst = null;
    try {
      Class<?> eventClass = Class.forName("pt.iflow.api.events." + type + "Event");
      AbstractEvent event = (AbstractEvent) eventClass.newInstance();
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      String createEvtQuery = DBQueryManager.getQuery("EventManager.CREATE_EVENT");
      pst = db.prepareStatement(createEvtQuery);
      pst.setInt(1, fid);
      pst.setInt(2, pid);
      pst.setInt(3, subpid);
      pst.setInt(4, blockid);
      pst.setLong(5, Calendar.getInstance().getTime().getTime());
      pst.setString(6, type);
      pst.setString(7, properties);
      pst.setInt(8, event.initialEventCode());
      pst.setString(9, userInfo.getUtilizador());
      pst.executeUpdate();
      registerStatus = true;
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "registerEvent", "SQL exception caught:", sqle);
    } catch (Exception ex) {
      Logger.error(userInfo.getUtilizador(), this, "registerEvent", "Exception caught:", ex);
    } finally {
      DatabaseInterface.closeResources(db, pst);
    }
    return registerStatus;
  }

  private int getEventId(int fid, int pid, int subpid, int blockid, String type) {
    int retObj = -1;

    DataSource ds = Utils.getDataSource();
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      db = ds.getConnection();
      st = db.createStatement();

      String getEvtQuery = DBQueryManager.processQuery("EventManager.GET_BLOCK_EVENT", 
          new Object[] { String.valueOf(fid), String.valueOf(pid), String.valueOf(subpid), String.valueOf(blockid), type });
      rs = st.executeQuery(getEvtQuery);
      if (rs.next()) {
        retObj = rs.getInt("eventid");
      }

      rs.close();
      rs = null;
      st.close();
      st = null;
    } catch (Exception e) {
      Logger.adminError("EventManager", "getEventId", 
          "error getting event id for [" + fid + ";" + pid + ";" + subpid + 
          "]block:" + blockid + ";type:" + type, e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return retObj;
  }

  public boolean setReadyToProcess(int fid, int pid, int subpid, int blockid) {
    int evid = this.getEventId(fid, pid, subpid, blockid, "AsyncWait");
    return this.setReadyToProcess(evid, true);
  }

  private boolean setReadyToProcess(int evid, boolean bToProcess) {
    boolean result = false;
    if (evid < 1) {
      return false;
    }
    int toProcess = bToProcess ? READY_TO_PROCESS : NOT_READY_TO_PROCESS;
    Connection db = null;
    Statement st = null;
    try {
      DataSource ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.createStatement();
      String updateEvtQuery = DBQueryManager.processQuery("EventManager.MARK_EVENT",
          new Object[] { String.valueOf(evid), String.valueOf(toProcess) });
      st.executeUpdate(updateEvtQuery);
      result = true;
    } catch (Exception e) {
      Logger.adminError("EventManager", "setReadyToProcess", 
          "error in eventid: " + evid + "; process?" + bToProcess, e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }
    return result;
  }

  private void checkEventsDB() {
    Connection db = null;
    Statement st = null;
    Statement st2 = null;
    Statement st3 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    DataSource ds = null;
    ArrayList<EventData> eventList = new ArrayList<EventData>();
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.createStatement();
      st2 = db.createStatement();
      st3 = db.createStatement();
      String mainQuery = DBQueryManager.getQuery("EventManager.GET_UNPROCESSED_EVENTS");
      rs = st.executeQuery(mainQuery);
      while (rs.next()) {
        long starttime = rs.getLong("starttime"); // getLong first avoids "Stream has already been closed" SQLExceptions
        int eventid = rs.getInt("eventid");
        int fid = rs.getInt("fid");
        int pid = rs.getInt("pid");
        int subpid = rs.getInt("subpid");
        int blockid = rs.getInt("blockid");
        String type = rs.getString("type");
        String properties = rs.getString("properties");
        String userId = rs.getString("userid");
        String stateQuery = DBQueryManager.processQuery("EventManager.GET_FLOW_STATE",
            new Object[] { String.valueOf(fid), String.valueOf(pid), String.valueOf(subpid )}); 
        rs2 = st2.executeQuery(stateQuery);
        // Se estiver no bloco 'blockid'
        if (rs2.next() && blockid == rs2.getInt("state")) {
          EventData ev = new EventData(userId, eventid, fid, pid, subpid, blockid, starttime, type, properties);
          eventList.add(ev);
          String updateEvtQuery = DBQueryManager.processQuery("EventManager.MARK_EVENT",
              new Object[] { String.valueOf(eventid), String.valueOf(EVENT_PROCESSED) });
          st3.executeUpdate(updateEvtQuery);

        } else {
          // o estado do fluxo e noutro bloco => apaga todos os eventos do bloco
          String removeEvtQuery = DBQueryManager.processQuery("EventManager.REMOVE_BLOCK_EVENTS", 
              new Object[] { String.valueOf(fid), String.valueOf(pid), String.valueOf(subpid), String.valueOf(blockid)});
          st3.executeUpdate(removeEvtQuery);
        }
        rs2.close();
        rs2 = null;
      }
      rs.close();
      rs = null;

      Iterator<EventData> iEventIterator = eventList.iterator();
      while (iEventIterator.hasNext()) {
        EventData eventData = iEventIterator.next();

        try {
          Class<?> eventClass = Class.forName("pt.iflow.api.events." + eventData.getType() + "Event");
          AbstractEvent event = (AbstractEvent) eventClass.newInstance();
          Boolean eventProcessed = event.processEvent(eventData);

          if (eventProcessed.booleanValue()) {
            // Insere na fila para remocao da base de dados
            String removeEvtQuery = DBQueryManager.processQuery("EventManager.REMOVE_EVENT", 
                new Object[] { String.valueOf(eventData.getId()) });
            st3.executeUpdate(removeEvtQuery);

          } else {
            // alNotProcessed.add(new Integer(edtmp.getId()));
            String updateEvtQuery = DBQueryManager.processQuery("EventManager.MARK_EVENT", 
                new Object[] { String.valueOf(eventData.getId()), String.valueOf(READY_TO_PROCESS) });
            st3.executeUpdate(updateEvtQuery);

          }
        } catch (Exception e) {
          Logger.adminError("EventManager", "checkEventsDB", "Could not invoke Event " + eventData.getType(), e);
        }
      }

    } catch (SQLException sqle) {
      Logger.adminError("EventManager", "checkEventsDB", "Error processing events in database", sqle);
    } finally {
      DatabaseInterface.closeResources(db, st, rs, st2, rs2, st3);
    }
  }

  // TODO LOG INFO IN THESE
  public String[] listEvents(UserInfoInterface userInfo) {
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    Logger.debug(userInfo.getUtilizador(), this, "listEvents", "Listing existing events from DB");

    ArrayList<String> eventList = new ArrayList<String>();

    try {
      db = Utils.getDataSource().getConnection();
      st = db.createStatement();

      String mainQuery = "select name from event_info";
      Logger.debug(userInfo.getUtilizador(), this, "listEvents", "Execute query: " + mainQuery);
      rs = st.executeQuery(mainQuery);
      while (rs.next()) {
        eventList.add(rs.getString(1)); // getLong first avoids "Stream has already been closed" SQLExceptions
      }
      rs.close();
      st.close();
    } catch (SQLException e) {
      Logger.error(userInfo.getUtilizador(), this, "listEvents", "Error occurred executing query: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return eventList.toArray(new String[eventList.size()]);
  }

  public String getEventDescription(UserInfoInterface userInfo, String eventName) {
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    Logger.debug(userInfo.getUtilizador(), this, "getEventDescription", "Getting event description for \"" + eventName + "\"");

    String description = null;

    try {
      db = Utils.getDataSource().getConnection();

      String mainQuery = "select description from event_info where name=?";
      Logger.debug(userInfo.getUtilizador(), this, "getEventDescription", "Execute query: " + mainQuery);
      st = db.prepareStatement(mainQuery);
      st.setString(1, eventName);
      rs = st.executeQuery();
      if (rs.next()) {
        description = rs.getString(1); // getLong first avoids "Stream has already been closed" SQLExceptions
      }
      rs.close();
      st.close();
    } catch (SQLException e) {
      Logger.error(userInfo.getUtilizador(), this, "getEventDescription", "Error occurred executing query: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return description;
  }

}
