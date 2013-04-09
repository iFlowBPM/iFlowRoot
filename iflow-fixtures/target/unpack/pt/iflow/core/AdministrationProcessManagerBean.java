package pt.iflow.core;

import java.util.Iterator;

import pt.iflow.api.core.Activity;
import pt.iflow.api.core.AdministrationProcessManager;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class AdministrationProcessManagerBean implements AdministrationProcessManager{
  private static AdministrationProcessManagerBean instance = null;

  private AdministrationProcessManagerBean() {
  }

  public static AdministrationProcessManagerBean getInstance() {
    if (null == instance)
      instance = new AdministrationProcessManagerBean();
    return instance;
  }

  public boolean terminateProcess(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    boolean operationResult = false;
    ProcessManager pm = BeanFactory.getProcessManagerBean();
    if (pm != null){
      ProcessData processData = pm.getProcessDataToBlock(userInfo, flowid, pid, subpid);
      if (processData != null){
        Flow flow = BeanFactory.getFlowBean();
        if (flow != null){
          operationResult = flow.endProccessInBlockAdministration(userInfo, processData);
          if (operationResult){
            Logger.debug(userInfo.getUtilizador(), this, "terminateProcess", "Terminated process referente to: flowid ["+flowid+"], pid ["+pid+"], subpid ["+subpid+"]");
          }else {
            operationResult = false;
            Logger.error(userInfo.getUtilizador(), this, "terminateProcess", "Unable to Terminated process referente to: flowid ["+flowid+"], pid ["+pid+"], subpid ["+subpid+"]");
          }
        } else {
          operationResult = false;
          Logger.error(userInfo.getUtilizador(), this, "terminateProcess", "Error while attempting to initialize FlowBean");
        }
      } else {
        operationResult = false;
        Logger.error(userInfo.getUtilizador(), this, "terminateProcess", "Error while retrieving process data, using parameters flowid ["+flowid+"], pid ["+pid+"], subpid ["+subpid+"]");
      }
    } else {
      operationResult = false;
      Logger.error(userInfo.getUtilizador(), this, "terminateProcess", "Error while attempting to initialize ProcessManagerBean");
    }
    return operationResult;
  }

  public boolean redirectProcessToUser(UserInfoInterface userInfo, int flowid, int pid, int subpid, String currentUser,
      String newUser) {
    boolean operationResult = false;
    ProcessManager pm = BeanFactory.getProcessManagerBean();


    Iterator<Activity> it = pm.getProcessActivities(userInfo, flowid, pid, subpid);
    Activity activity = null;
    boolean bExists = false;

    while(it.hasNext()) {
      Activity a = it.next();
      if (a.userid.equals(currentUser)) {
        activity = a;
      } else if (a.userid.equals(newUser)) {
        bExists = true;
      }
      if (activity != null && bExists) {
        break;
      }
    }

    if (activity != null && !bExists){
      operationResult = updateActivityUser(userInfo, activity, newUser);
    } else {
      operationResult = false;
    }

    return operationResult;
  }

  private boolean updateActivityUser(UserInfoInterface userInfo, Activity activity, String newUser) {
    boolean updateSuccessful = false;
    javax.sql.DataSource dso = Utils.getDataSource();
    java.sql.Connection db = null;
    java.sql.Statement st = null;
    int ntmp = 0;

    try {
      String oldUser = activity.getUserid();
      db = dso.getConnection();
      db.setAutoCommit(false);
      st = db.createStatement();

      StringBuffer query = new StringBuffer();
      String sProfileName = "";
      String activityProfileName = activity.profilename;
      if (activity.userid.equals(activityProfileName)) {
        sProfileName = ",profilename='" + newUser + "'";
      }
      query.append("update activity ");
      query.append("set userid='").append(newUser).append("'");
      query.append(sProfileName);
      query.append(" where userid='").append(activity.getUserid()).append("' ");
      query.append(" and flowid='").append(activity.getFlowid()).append("' ");
      query.append(" and pid='").append(activity.pid).append("' ");
      query.append(" and subpid='").append(activity.getSubpid()).append("' ");

      if (query != null) {
        ntmp = st.executeUpdate(query.toString());
        if (ntmp == 1) {
          updateSuccessful = true;
          Logger.info(userInfo.getUtilizador(), this, "updateActivityUser", "Successful update to user [" + newUser
              + "], from user [" + oldUser + "], of activity represented by flowid [" + activity.getFlowid() + "], pid ["
              + activity.getPid() + "], subpid [" + activity.getSubpid() + "] ");
          db.commit();
        } else {
          Logger.error(userInfo.getUtilizador(), this, "updateActivityUser", "Unable to update activity represented by flowid ["
              + activity.getFlowid() + "], pid [" + activity.getPid() + "], subpid [" + activity.getSubpid() + "], user ["
              + oldUser + "], to new user [" + newUser + "] ");
          db.rollback();
          updateSuccessful = false;
        }
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "updateActivityUser", "Error while updating activity with new user. ", e);
      if (db != null) {
        try {
          db.rollback();
        } catch (Exception ee) {
        }
      }
    } finally {
      DatabaseInterface.closeResources(dso, db, st);
    }
    return updateSuccessful;
  }

  public boolean undoProcess(UserInfoInterface userInfo, int flowId, int pid, int subPid, int flowState, int mid,
      boolean registerTransaction) {
    boolean operationResult = false;
    Flow flow = BeanFactory.getFlowBean();
    if (flow != null) {
      int exit_flag = 0;
      operationResult = flow.undoProcess(userInfo, flowId, pid, subPid, flowState, mid, exit_flag, registerTransaction);
    } else {
      operationResult = false;
      Logger.error(userInfo.getUtilizador(), this, "undoProcess", "Error while attempting to initialize FlowBean");
    }
    return operationResult;
  }
}