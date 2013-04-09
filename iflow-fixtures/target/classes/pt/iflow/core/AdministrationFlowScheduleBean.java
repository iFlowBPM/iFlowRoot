package pt.iflow.core;

import java.util.ArrayList;
import java.util.Date;

import pt.iflow.api.core.AdministrationFlowScheduleInterface;
import pt.iflow.api.flowSchedule.FlowScheduleDataInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.scheduler.CronManager;

public class AdministrationFlowScheduleBean implements AdministrationFlowScheduleInterface {
  private static AdministrationFlowScheduleBean instance = null;

  private AdministrationFlowScheduleBean() {
  }

  public static AdministrationFlowScheduleBean getInstance() {
    if (null == instance)
      instance = new AdministrationFlowScheduleBean();
    return instance;
  }

  public boolean createNewFlowScheduleEvent(UserInfoInterface userInfo, FlowScheduleDataInterface flowScheduleData) {
    boolean insertionResult = false;

    if (userInfo != null && flowScheduleData != null) {
      String userName = flowScheduleData.getUserAssigned();
      Date eventTriggerDate = flowScheduleData.getStartTime();
      String extra = flowScheduleData.getExtra();
      int flowId = flowScheduleData.getFlowId();
      String profileName = flowScheduleData.getUserAssignedProfile();

      CronManager cronManager = CronManager.getInstance();

      if (flowScheduleData.isSimpleEvent()){
        cronManager.scheduleJob(eventTriggerDate, flowId, userName, profileName, extra);
      } else {
        cronManager.scheduleJob(eventTriggerDate, flowScheduleData.getTimeBetweenFires(), flowId, userName, profileName, extra);
      }

      insertionResult = checkIfJobExists(userInfo, flowScheduleData);
    }
    return insertionResult;
  }

  public boolean checkIfJobExists(UserInfoInterface userInfo, FlowScheduleDataInterface flowScheduleData) {
    boolean jobExists = true;
    CronManager cronManager = CronManager.getInstance();
    if (cronManager != null) {
      if (flowScheduleData != null) {
        String jobName = String.valueOf(flowScheduleData.getFlowId()) + "-" + flowScheduleData.getUserAssigned();
        try {
          cronManager.getJobDetail(jobName);
        } catch (Exception e) {
          jobExists = false;
        }
      } else {
        Logger.error(userInfo.getUtilizador(), this, "checkIfJobExists", "flowScheduleData is null");
      }
    } else {
      Logger.error(userInfo.getUtilizador(), this, "checkIfJobExists", "Unable to acess CronManager");
    }
    return jobExists;
  }

  public ArrayList<FlowScheduleDataInterface> getScheduledFlowsJobs(UserInfoInterface userInfo, String fromUser) {
    CronManager cronManager = CronManager.getInstance();
    ArrayList<FlowScheduleDataInterface> jobsList = new ArrayList<FlowScheduleDataInterface>();
    if (cronManager != null) {
      jobsList = cronManager.getScheduledFlowsJobs(userInfo, fromUser);
    }
    return jobsList;
  }

  public boolean deleteJob(UserInfoInterface userInfo, String jobName) {
    boolean jobDeletionResult = false;
    CronManager cronManager = CronManager.getInstance();
    if (cronManager != null) {
      jobDeletionResult = cronManager.unscheduleJob(userInfo, jobName);
    }
    return jobDeletionResult;
  }
  // cronManager.scheduleJob(new Date(), 19, "olopes", "todos", "");

}
