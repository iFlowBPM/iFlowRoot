package pt.iflow.api.core;

import java.util.ArrayList;

import pt.iflow.api.flowSchedule.FlowScheduleDataInterface;
import pt.iflow.api.utils.UserInfoInterface;

public interface AdministrationFlowScheduleInterface {

  public boolean checkIfJobExists(UserInfoInterface userInfo, FlowScheduleDataInterface flowScheduleData);

  public boolean createNewFlowScheduleEvent(UserInfoInterface userInfo, FlowScheduleDataInterface scheduleData);

  public ArrayList<FlowScheduleDataInterface> getScheduledFlowsJobs(UserInfoInterface userInfo, String user);

  public boolean deleteJob(UserInfoInterface userInfo, String jobName);

}