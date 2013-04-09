package pt.iflow.flowSchedule;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import pt.iflow.api.flowSchedule.FlowScheduleDataInterface;

public class FlowScheduleData implements FlowScheduleDataInterface {
  private static final String DATE_FORMAT_DD_MM_YYYY_HH_MM = "dd-MM-yyyy HH:mm";
  SimpleDateFormat dateFormatDDMMYYYY_HHMM = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY_HH_MM);

  private String jobName = null;
  private Timestamp startTime = null;
  private String startTimeJsp = "";
  private int flowId = -1;
  private String flowName = null;
  private String userAssigned = null;
  private String userAssignedProfile = null;
  private String extra = "";

  private boolean isSimpleEvent = true;
  private long timeBetweenFires = 0;
  private String timeBetweenFiresInHoursJsp = "";

  private String formatedTimeBetweenExecutions = "";

  private Timestamp nextFireDate = null;
  private String nextFireDateJsp = "";

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public Timestamp getStartTime() {
    return startTime;
  }

  public void setStartTime(Timestamp startTime) {
    this.startTime = startTime;
    this.startTimeJsp = dateFormatDDMMYYYY_HHMM.format(startTime);
  }

  public int getFlowId() {
    return flowId;
  }

  public void setFlowId(int flowId) {
    this.flowId = flowId;
  }

  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  public String getUserAssigned() {
    return userAssigned;
  }

  public void setUserAssigned(String userAssigned) {
    this.userAssigned = userAssigned;
  }

  public String getUserAssignedProfile() {
    return userAssignedProfile;
  }

  public void setUserAssignedProfile(String userAssignedProfile) {
    this.userAssignedProfile = userAssignedProfile;
  }

  public String getExtra() {
    return extra;
  }

  public void setExtra(String extra) {
    this.extra = extra;
  }

  public String getStartTimeJsp() {
    return startTimeJsp;
  }

  public boolean isSimpleEvent() {
    return isSimpleEvent;
  }

  public void setSimpleEvent(boolean isSimpleEvent) {
    this.isSimpleEvent = isSimpleEvent;
  }

  public Timestamp getNextFireDate() {
    return nextFireDate;
  }

  public void setNextFireDate(Timestamp nextFireDate) {
    this.nextFireDate = nextFireDate;
    this.nextFireDateJsp = dateFormatDDMMYYYY_HHMM.format(nextFireDate);
  }

  public String getNextFireDateJsp() {
    return nextFireDateJsp;
  }

  public long getTimeBetweenFires() {
    return timeBetweenFires;
  }

  public void setTimeBetweenFires(long timeBetweenFires) {
    this.timeBetweenFires = timeBetweenFires;
    if (timeBetweenFires > -1) {
      this.timeBetweenFiresInHoursJsp = String.valueOf(getTimeBetweenFires() / (60 * 60 * 1000));
    } else {
      this.timeBetweenFiresInHoursJsp = "--";
    }
  }

  public String getTimeBetweenFiresInHoursJsp() {
    return timeBetweenFiresInHoursJsp;
  }

  public String getFormatedTimeBetweenExecutions() {
    return formatedTimeBetweenExecutions;
  }

  public void setFormatedTimeBetweenExecutions(String formatedTimeBetweenExecutions) {
    this.formatedTimeBetweenExecutions = formatedTimeBetweenExecutions;
  }
}