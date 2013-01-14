package pt.iflow.api.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpSession;

import pt.iflow.api.audit.AuditData;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.filters.FlowFilter;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iflow.api.transition.FlowStateLogTO;
import pt.iflow.api.transition.LogTO;
import pt.iflow.api.transition.UpgradeLogTO;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * Remote interface for ProcessManager.
 * 
 */
public interface ProcessManager extends UserProcsConst {

  public abstract ProcessData createProcess(UserInfoInterface userInfo, int flowid) throws Exception;

  public abstract boolean createSubProcess(UserInfoInterface userInfo, ProcessData procData, int blockId, String description);

  /**
   * Prepares if necessary process in database (switch from session to db)
   * 
   * @return true if process was switched (created); false otherwise
   */
  public abstract boolean prepareProcInDB(UserInfoInterface userInfo, ProcessData procData) throws Exception;
  public abstract boolean prepareProcInDB(UserInfoInterface userInfo, ProcessData procData, boolean allowGuestForForward) throws Exception;

  /**
   * Gets process permissions for specific user
   * 
   * @param pid
   *          process id (0, if asking for process creation)
   * @param userid
   *          user id
   * @returns permissions: 0 - denied; 1 - read-only; 2 - read-write;
   */
  public abstract int getProcessPermissions(UserInfoInterface userInfo, ProcessData pdProc);

  public abstract boolean checkBlockIsMined(UserInfoInterface userInfo, ProcessData procData, int blockid, int blockidNext);

  public abstract boolean checkBlockIsLocked(UserInfoInterface userInfo, ProcessData procData, int blockid);

  public abstract void deployMinesInProcess(UserInfoInterface userInfo, ProcessData procData, int blockid);

  public abstract void unlockBlock(UserInfoInterface userInfo, ProcessData procData, int blockid);

  public abstract void lockBlock(UserInfoInterface userInfo, ProcessData procData, int blockid);

  /**
   * Initializes the Process Data.
   * 
   * @param pid
   *          process id
   */
  public abstract ProcessData initializeProcessData(UserInfoInterface userInfo, ProcessData procData);

  public abstract ProcessData undoProcessData(UserInfoInterface userInfo, int flowid, int pid, int subpid, int newMid, ProcessData process) throws Exception;
  
  public int getNextMid(UserInfoInterface userInfo, ProcessHeader procHeader) throws Exception;
  public int getNextMid(UserInfoInterface userInfo, ProcessData procData) throws Exception;

  /**
   * Modifies a process, if the user has permission to do it
   * 
   * @param userInfo
   *          user info
   * @param dataSet
   *          data set
   * @returns modification id
   * @param userInfo
   * @param procData
   * @return
   */
  public abstract int modifyProcessData(UserInfoInterface userInfo, ProcessData procData) throws Exception;

  /**
   * retrieves process data
   * 
   * @param pid
   *          process id
   * @returns process data stored in DataSet object
   */
  public abstract ProcessData getProcessData(UserInfoInterface userInfo, ProcessHeader procHeader);

  public abstract ProcessData getProcessData(UserInfoInterface userInfo, ProcessHeader procHeader, int anMode);

  /**
   * retrieves process data for given pids
   * 
   * @param pids
   *          process ids
   * @returns processes data stored in DataSet object for each process
   */
  public abstract ProcessData[] getProcessesData(UserInfoInterface userInfo, ProcessHeader[] procHeaders);

  /**
   * retrieves process data for given pids
   * 
   * @param pids
   *          process ids
   * @returns processes data stored in DataSet object for each process
   */
  public abstract ProcessData[] getProcessesData(UserInfoInterface userInfo, ProcessHeader[] procHeaders, int anMode);

  /**
   * retrieves process data for given pids
   * 
   * @param pids
   *          process ids
   * @returns processes data stored in DataSet object for each process
   * @deprecated
   */
  public abstract ProcessData[] getProcessesData(UserInfoInterface userInfo, ProcessHeader[] procHeaders, String[] asaFields);

  /**
   * retrieves process data for given ProcessData(s)
   * 
   * @param pdaProcs
   *          flow id. if <= 0, retrieves processes for all flows.
   * @param asaFields
   *          required fields. if null or empty, all proc fields are fetched
   * @param anMode
   *          fetch mode: all procs, opened procs or closed procs
   * @returns processes data stored in DataSet object for each process
   * @deprecated
   */
  public abstract ProcessData[] getProcessesData(UserInfoInterface userInfo, ProcessHeader[] procHeaders, String[] asaFields, int anMode);

  /**
   * retrieves process data
   * 
   * @param session
   * @returns process data stored in DataSet object
   */
  public abstract ProcessData getProcessData(UserInfoInterface userInfo, int flowid, int pid, int subpid, HttpSession session);

  /**
   * retrieves process data
   * 
   * @param session
   * @returns process data stored in DataSet object
   */
  public abstract ProcessData getProcessData(UserInfoInterface userInfo, int flowid, int pid, int subpid, HttpSession session, int mode);

  /**
   * retrieves process data to te used in administration block
   * 
   * @param session
   * @returns process data stored in DataSet object
   */
  public abstract ProcessData getProcessDataToBlock(UserInfoInterface userInfo, int flowid, int pid, int subpid);

  /**
   * Returns the process state
   * 
   * @return the process state
   */
  public abstract String getProcessState(UserInfoInterface userInfo, ProcessHeader procHeader);

  /**
   * Returns the process info.
   * 
   * @return
   */
  public abstract String getProcessInfo(UserInfoInterface userInfo, ProcessHeader procHeader);

  /**
   * Retrieves logs for the given state of a process.
   * 
   * @param userInfo User Information.
   * @param flowid Flow ID.
   * @param pnumber Process Identifier.
   * @param subpid Sub-Process ID.
   * @param state State of the process (block ID).
   * @return List of the given state's logs.
   */
  public List<FlowStateLogTO> getFlowStateLogs(UserInfoInterface userInfo, int flowid, String pnumber, int subpid, int state);
  
  /**
   * Retrieves state history for a given process.
   * 
   * @param userInfo User Information
   * @param flowid Flow ID
   * @param pnumber Process Identifier
   * @return List of the given process' history.
   */
  public List<FlowStateHistoryTO> getFullProcessHistory(UserInfoInterface userInfo, int flowid, String pnumber);
  
  /**
   * Retrieves a default ammount of upgrade logs from the DB.
   * 
   * @param userInfo User Information
   * @return Retrieved upgrade logs from DB, with the default limit of results.  
   * @see #getUgradeLogs(UserInfoInterface, int)
   */
  public List<UpgradeLogTO> getUgradeLogs(UserInfoInterface userInfo);
  
  /**
   * Retrieves the specified ammount of upgrade logs from DB.
   * 
   * @param userInfo User Information
   * @param limit Maximum number of results to fetch
   * @return Retrieved upgrade logs from DB
   * @see #getUgradeLogs(UserInfoInterface)
   */
  public List<UpgradeLogTO> getUgradeLogs(UserInfoInterface userInfo, int limit);
  
  /**
   * 
   * @param userInfo User Information
   * @param db DB connection
   * @param logId Log's id
   * @return Log from DB, or null if none is found.
   */
  public LogTO getLog(UserInfoInterface userInfo, Connection db, int logId);
  
  /**
   * Moves one process from a user to another.
   * 
   */
  public abstract void moveUserProcess(UserInfoInterface userInfo, ProcessHeader procHeader, String oldUserId, String newUserId);

  /**
   * Retrieves an activity in a user's current worklist
   * 
   * @param userid
   *          user id
   * @param flowid
   *          flow id
   * @param pid
   *          process id
   */
  public abstract Activity getUserProcessActivity(UserInfoInterface userInfo, ProcessHeader procHeader);

  /**
   * Retrieves an activity in a user's current worklist
   * 
   * @param userid
   *          user id
   * @param flowid
   *          flow id
   * @param pid
   *          process id
   */
  public abstract Activity getUserProcessActivity(UserInfoInterface userInfo, int flowid, int pid, int subpid);

  /**
   * Creates an activity
   * 
   * @param userid
   *          user id
   * @param pid
   *          process id
   * @param type
   *          user-related operation type
   * @param priority
   *          priority
   * @param description
   *          subject
   * @param url
   *          edition url
   */
  public abstract void createActivity(UserInfoInterface userInfo, Activity activity) throws Exception;

  public abstract void createActivity(UserInfoInterface userInfo, Activity activity, boolean hasSelfCreatePriv) throws Exception;

  public abstract void createActivity(UserInfoInterface userInfo, Activity activity, boolean hasSelfCreatePriv, boolean forceNotRead) throws Exception;

  public abstract void createActivities(UserInfoInterface userInfo, Iterator<Activity> liActivities) throws Exception;

  public abstract void createActivities(UserInfoInterface userInfo, Iterator<Activity> liActivities, boolean hasSelfCreatePriv) throws Exception;

  public abstract void createActivities(UserInfoInterface userInfo, Iterator<Activity> liActivities, boolean hasSelfCreatePriv, boolean forceNotRead) throws Exception;

  /**
   * Retrieves the activity 'real' owners If it isn't a delegated activity,
   * returns the userInfo user
   * 
   * @param userInfo
   *          the calling user info
   * @param activity
   *          the activity whos owners are to be checked
   * @return an array with the real activity owners
   */
  public abstract String[] getActivityOwners(UserInfoInterface userInfo, Activity activity);

  /**
   * Gets the "online" sub-process ids for the given process (pid)
   * 
   * @param userInfo
   *          the requesting user
   * @param procData
   *          the process info
   * @return array of available subpids
   */
  public abstract int[] getProcessSubPids(UserInfoInterface userInfo, ProcessData procData);

  public abstract int[] getProcessSubPids(UserInfoInterface userInfo, ProcessHeader procHeader);

  /**
   * Retrieves the subpids from a given process that are in specific block
   * 
   * @param userInfo
   *          the calling user info
   * @param procData
   *          the process data
   * @param block
   *          the block where the subpids are to be checked
   * @return an array with the subpids
   */
  public abstract int[] getSubPidsInBlock(UserInfoInterface userInfo, ProcessData procData, Block block);

  /**
   * Retrieves the activities for a given process (and flow)
   * 
   * @param userInfo
   *          the calling user info
   * @param anFlowId
   *          the process' flow id
   * @param anPid
   *          the process id
   * @return iterator with process activities
   */
  public abstract ListIterator<Activity> getProcessActivities(UserInfoInterface userInfo, int anFlowId, int anPid, int anSubPid);

  public abstract ListIterator<Activity> getProcessActivities(UserInfoInterface userInfo, ProcessData procData);
  
  public abstract ListIterator<Activity> getActivities(UserInfoInterface userInfo, int anFlowId, int anPid, int anSubPid,
      Date adtBefore, Date adtAfter, String userIdBefore, String userIdActual);

  /**
   * Retrieve Activities of a given profile
   * @param userInfo The UserInfo.
   * @param profileName The Profile Name.
   * @return The activities.
   */
  public abstract ListIterator<Activity> getActivities(UserInfoInterface userInfo, String profileName);
  
  /**
   * Check if Profile Has Processes
   * @param userInfo User Info
   * @param profileName
   * @return True if has processes, false otherwise.
   */
  public boolean checkProcessInProfile(UserInfoInterface userInfo, String profileName);
  

  /**
   * Retrieves a user's current work list.
   * 
   * @param userInfo The calling user info.
   *          
   * @return ListIterator with the given user's Activities.
   * 
   * @see pt.iflow.api.core.ProcessManager#getUserActivities(UserInfoInterface, int)
   * @see pt.iflow.api.core.ProcessManager#getUserActivities(UserInfoInterface, int, FlowFilter)
   */
  public abstract ListIterator<Activity> getUserActivities(UserInfoInterface userInfo);

  /**
   * Retrieves a user's current work list.
   * 
   * @param userInfo The calling user info.
   * @param anFlowId Flow ID to search for.
   * 
   * @return ListIterator with the given user's Activities for the specified flow.
   * 
   * @see pt.iflow.api.core.ProcessManager#getUserActivities(UserInfoInterface)
   * @see pt.iflow.api.core.ProcessManager#getUserActivities(UserInfoInterface, int, FlowFilter)
   */
  public abstract ListIterator<Activity> getUserActivities(UserInfoInterface userInfo, int anFlowId);

  /**
   * Retrieves a user's current work list.
   * 
   * @param userInfo The calling user info.
   * @param anFlowId Flow ID to search for.
   * @param filter Filter properties to apply.
   *          
   * @return ListIterator with the given user's Activities for the specified flow, filtered accordingly.
   * 
   * @see pt.iflow.api.core.ProcessManager#getUserActivities(UserInfoInterface)
   * @see pt.iflow.api.core.ProcessManager#getUserActivities(UserInfoInterface, int)
   */
  public abstract ListIterator<Activity> getUserActivities(UserInfoInterface userInfo, int anFlowId, FlowFilter filter);
  
  /**
   * retrieves the worklist history of a user
   * 
   * @param userid
   *          user id
   */
  public abstract ListIterator<Activity> getUserActivityHistory(UserInfoInterface userInfo) ;

  /**
   * Retrieves the url for this user's activity in requested process
   * 
   * @param asUrl
   *          the output value if no activities exist for this process
   * @return url or null if user does not exist in process activity list
   */
  public abstract String getUserProcessUrl(UserInfoInterface userInfo, ProcessData procData, String asUrl);

  /**
   * Retrieves the worklist history of a process
   * 
   * @param flowid
   *          flow id
   * @param pid
   *          process id
   */
  public abstract List<Activity> getProcessActivityHistory(UserInfoInterface userInfo, ProcessData procData);

  /**
   * Retrieves the worklist history of a process
   * 
   * @param flowid
   *          flow id
   * @param pid
   *          process id
   */
  public abstract List<Activity> getProcessActivityHistory(UserInfoInterface userInfo, int flowid, int pid, int subpid);

  /**
   * Checks if given process exists somewhere in process history.
   * @param userInfo User Information.
   * @param flowid Flow ID.
   * @param pid Process ID.
   * @param subpid SubProcess ID
   * @return True if process exists, false otherwise.
   */
  public boolean processExists(UserInfoInterface userInfo, ProcessHeader procInfo);
  
  /**
   * Checks if given process is closed in DB.
   * 
   * @param userInfo User Information.
   * @param flowid Flow ID.
   * @param pid Process ID.
   * @param subpid SubProcess ID
   * @return True if process is closed. False if process is either still open or does not exist.
   */
  public boolean isProcessClosed(UserInfoInterface userInfo, ProcessHeader procInfo) throws SQLException;
  
  /**
   * Delete activities of a given user
   * @param userInfo The UserInfo
   * @param userid The UserID
   * @throws SQLException
   */
  public void deleteActivities(UserInfoInterface userInfo, String userid) throws SQLException;
  
  /**
   * Delete activities of a given user and profile
   * @param userInfo The UserInfo
   * @param userid The UserID
   * @throws SQLException
   */
  public void deleteActivitiesForProfile(UserInfoInterface userInfo, String userid, String profileId) throws SQLException;
  
  /**
   * Delete all activities of the process
   * 
   * @param userid
   * @param pid
   */
  public abstract void deleteAllActivities(UserInfoInterface userInfo, ProcessHeader procHeader) throws SQLException;

  /**
   * Delete all activities of the process
   * 
   * @param userid
   * @param pid
   */
  public abstract void deleteAllActivities(UserInfoInterface userInfo, ProcessData procData) throws SQLException;

  /**
   * Updates a process activities with the new specified url and description
   * 
   * @param userInfo
   *          the calling user info
   * @param activity
   *          the activity data to update
   */
  public abstract void updateActivity(UserInfoInterface userInfo, Activity activity);

  /**
   * Updates a process activities with the new specified url and description If
   * aalGroupProcs is not null, then activities are "grouped": only one activity
   * for pair description-userid. This means that if 2 procs in same state have
   * the same description, only one will have an activity associated. CAUTION:
   * block must support this "mode"
   * 
   */
  public abstract void updateActivity(UserInfoInterface userInfo, Activity activity, List<String> aalGroupProcs);

  /**
   * Moves all activities of the process to a new set of users with the specified profile.
   * 
   * @return true if succeeds; false otherwise
   */
  public abstract boolean forwardToProfile(UserInfoInterface userInfo, ProcessData procData, String asProfile, String asDescription);

  /**
   * Moves all activities of the process to the specified user.
   * 
   * @return true if succeeds; false otherwise
   */
  public abstract boolean forwardToUser(UserInfoInterface userInfo, ProcessData procData, String asNewUser, String asDescription);
  
  public abstract boolean undoProcessActivities(UserInfoInterface userInfo, ProcessData procData, int targetMid, int flowState) throws Exception;

  public abstract void notifyProcessUndone(UserInfoInterface userInfo, ProcessData procData);

  /**
   * Get the modification id for a given process
   * 
   * @return the mid.
   */
  public abstract int getModificationId(UserInfoInterface userInfo, ProcessHeader procHeader);

  /**
   * Method to fetch user/process activity statistics audit data.
   * 
   * @param userInfo
   *          User information.
   * @param flowid
   *          Flow id.
   * @param interval
   *          A two index array containing desired Start and End dates in
   *          position 0 and 1, respectively.
   * @return Audit data array.
   */
  public abstract AuditData[] getProcessActivityStatistics(UserInfoInterface userInfo, int flowid, Date[] interval);
  
  /**
   * Method to fetch user/profile activity performance audit data
   * 
   * @param userInfo
   * @param procData
   * @return audit data array
   */
  public abstract AuditData[] getProcessActivityPerformance(UserInfoInterface userInfo, int flowid , Date[] interval);

  /**
   * Retrieves user's processes.
   * 
   * @param userInfo User performing the lookup.
   * @param nShowFlowId Processes flow ID, if -1 then the lookup is not limited by flow. 
   * @param targetOwner Process creator.
   * @param idx Processes indexing columns.
   * @param closedProcesses Flag indicating if the lookup should be for closed processes. 
   * @param filter Filter object to use during processes lookup.
   * @return An UserProcesses object containing the user's processes, filtered accordingly.
   */
  public abstract UserProcesses getUserProcesses(UserInfoInterface userInfo, int nShowFlowId, String targetOwner, String[] idx, boolean closedProcesses, FlowFilter filter);
  
  /**
   * Retrieve all process intervenients 
   * @param userInfo current user
   * @param procData process
   * @return
   */
  public abstract Collection<String> getProcessIntervenients(UserInfoInterface userInfo, ProcessData procData);

  /**
   * Returns the process creator.
   * 
   * @return
   */
  public abstract String getProcessCreator(UserInfoInterface userInfo, int flowid, int pid, int subpid);

  /**
   * Returns the process number.
   * 
   * @return
   */
  public abstract String getProcessNumber(UserInfoInterface userInfo, int flowid, int pid);

  /**
   * Returns the process number for process id.
   * 
   * @return
   */
  public abstract ProcessData[] getProcessesNumbers(UserInfoInterface userInfo, ProcessData[] procs);

  public abstract ProcessHeader findProcess(UserInfoInterface userInfo, ProcessHeader proc);
  
  public abstract ProcessHeader[] findProcesses(UserInfoInterface userInfo, ProcessHeader[] proc);
  
  public abstract boolean canViewProcess(UserInfoInterface userInfo, ProcessData procData);

  public abstract boolean canViewProcess(UserInfoInterface userInfo, ProcessHeader procData);

  public abstract void archiveProcesses(UserInfoInterface userInfo);
  public abstract Collection<ProcessHeader> listExpiredProcesses(UserInfoInterface userInfo);

  public abstract void endProc(UserInfoInterface userInfo, ProcessData procData, boolean isCancel) throws Exception;
  
  public abstract int countFlowProcesses(UserInfoInterface userInfo, int flowid, boolean includeClosed, boolean includeArchived);

  public void disableActivities(UserInfoInterface userInfo, ProcessData procData) throws Exception;
  public void enableActivities(UserInfoInterface userInfo, ProcessData procData) throws Exception;

  public List<Activity> getPreviousActivities(UserInfoInterface userInfo, ProcessData procData) throws Exception;

  public abstract ListIterator<Activity> getUserActivitiesOrderByPid(UserInfoInterface userInfo);
  public abstract ListIterator<Activity> getUserActivitiesOrderFilters(UserInfoInterface userInfo, int anFlowId, FlowFilter filter);
}
