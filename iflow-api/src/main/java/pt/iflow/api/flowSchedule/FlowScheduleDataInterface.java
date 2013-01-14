package pt.iflow.api.flowSchedule;

import java.sql.Timestamp;

public interface FlowScheduleDataInterface {
  public String getJobName();

  public void setJobName(String jobName);

  public Timestamp getStartTime();

  public void setStartTime(Timestamp startTime);

  public int getFlowId();

  public void setFlowId(int flowId);

  public String getFlowName();

  public void setFlowName(String flowName);

  public String getUserAssigned();

  public void setUserAssigned(String userAssigned);

  public String getUserAssignedProfile();

  public void setUserAssignedProfile(String userAssignedProfile);

  public String getExtra();

  public void setExtra(String extra);

  public boolean isSimpleEvent();

  public void setSimpleEvent(boolean isSimpleEvent);

  public Timestamp getNextFireDate();

  public void setNextFireDate(Timestamp nextFireDate);

  public String getNextFireDateJsp();

  public long getTimeBetweenFires();

  public void setTimeBetweenFires(long timeBetweenFires);

  public String getTimeBetweenFiresInHoursJsp();
  
  public String getFormatedTimeBetweenExecutions();

  public void setFormatedTimeBetweenExecutions(String formatedTimeBetweenExecutions);
}