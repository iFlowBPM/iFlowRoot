package pt.iflow.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.iflow.api.core.AdministrationFlowScheduleInterface;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.UserManager;
import pt.iflow.api.flowSchedule.FlowScheduleDataInterface;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.transition.ProfilesTO;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.flowSchedule.FlowScheduleData;

public class FlowScheduleServlet extends HttpServlet implements Servlet {
  static final long serialVersionUID = 1L;

  private static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
  private static final String DATE_FORMAT_FULL_DATE = "dd/MM/yyyy HH:mm";

  private static final int EVENTS_TIME_FRAME_UNIT_HOURS = 0;
  private static final int EVENTS_TIME_FRAME_UNIT_DAYS = 1;

  SimpleDateFormat dateFormat_DD_MM_YYYY = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
  SimpleDateFormat dateFormat_FullDate = new SimpleDateFormat(DATE_FORMAT_FULL_DATE);

  private ArrayList<ComboBoxItems> listOfProfiles = null;
  private ArrayList<ComboBoxItems> listOfUsers = null;
  private ArrayList<ComboBoxItems> listOfFlows = null;
  private HashMap<String, String> flowsHashMap = new HashMap<String, String>();

  public FlowScheduleServlet() {
    super();
  }

  public void init() throws ServletException {  }

  private void forward(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
    request.getRequestDispatcher(page).forward(request, response);
  }

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String name = request.getServletPath();
    String gotoPage = "/inc/defs.jsp";

    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);

    if(null != userInfo) {
      IMessages messages = userInfo.getMessages();
      if (name.endsWith("flow_schedule_list")) {
        clearFlowScheduleParameters(userInfo, request);
        populateListData(userInfo, request);
        gotoPage = "/Admin/flowSchedule/flow_schedule_list.jsp";

      } else if (name.endsWith("flow_schedule_add")) {
        clearFlowScheduleParameters(userInfo, request);
        populateAddFormData(userInfo, request);
        gotoPage = "/Admin/flowSchedule/flow_schedule_add.jsp";

      } else if (name.endsWith("flow_schedule_add_new")) {
        boolean hasInserted = add_new_flow_schedule(userInfo, request);
        String jobInsertionResult = messages.getString("flow_schedule.add.msg.insertion.insuccess");
        if (hasInserted) {
          jobInsertionResult = messages.getString("flow_schedule.add.msg.insertion.success");
        }
        clearFlowScheduleParameters(userInfo, request);
        populateListData(userInfo, request);
        request.setAttribute("flowScheduleMsgListMsgToUser", jobInsertionResult);
        gotoPage = "/Admin/flowSchedule/flow_schedule_list.jsp";

      } else if (name.endsWith("flow_schedule_delete")) {
        String jobName = request.getParameter("jobName");

        clearFlowScheduleParameters(userInfo, request);

        boolean jobDeletionResult = deleteJob(userInfo, jobName);

        String jobDeletionMsg = messages.getString("flow_schedule.add.msg.delete.insuccess", new String[] { jobName });
        if (jobDeletionResult) {
          populateListData(userInfo, request);
          jobDeletionMsg = messages.getString("flow_schedule.add.msg.delete.success", new String[] { jobName });
        }
        request.setAttribute("flowScheduleMsgListMsgToUser", jobDeletionMsg);
        gotoPage = "/Admin/flowSchedule/flow_schedule_list.jsp";
      } else {
        gotoPage = "/Admin/flowSchedule/flow_schedule_list.jsp";
      }
    }
    forward(request, response, gotoPage);
  }

  private boolean add_new_flow_schedule(UserInfoInterface userInfo, HttpServletRequest request) {
    boolean operationResult = false;
    String flowidStr = request.getParameter("form_add_flow");
    String profileName = request.getParameter("selectedProfileName");
    String userName = request.getParameter("form_add_user");
    String eventDateStr = request.getParameter("eventDate");
    String eventTimeStr = request.getParameter("eventTime");

    int flowId = 0;
    try {
      Calendar eventTriggerTime = getEventTriggerTimeCalendarObject (dateFormat_DD_MM_YYYY.parse(eventDateStr), eventTimeStr);
      flowId = Integer.parseInt(flowidStr);

      Calendar currentDateCalendarObject = Calendar.getInstance();
      currentDateCalendarObject.add(Calendar.DAY_OF_YEAR, -1);

      if (currentDateCalendarObject.before(eventTriggerTime)) {
        String comboBoxDefaultValue = userInfo.getMessages().getString("flow_schedule.add.field.combobox.default.text");
        if (flowId != 0 && profileName != null && !comboBoxDefaultValue.equals(profileName) && userName != null
            && !comboBoxDefaultValue.equals(userName) && !"".equals(eventDateStr)) {

          FlowScheduleDataInterface scheduleData = new FlowScheduleData();
          scheduleData.setFlowId(flowId);
          scheduleData.setUserAssignedProfile(profileName);
          scheduleData.setUserAssigned(userName);
          scheduleData.setStartTime(new Timestamp(eventTriggerTime.getTime().getTime()));

          String isRepeatable = request.getParameter("isRepeatable");
          if ("true".equals(isRepeatable)){
            scheduleData.setSimpleEvent(false);
            long eventIntervalUnformated = Long.parseLong(String.valueOf(request.getParameter("eventInterval")));
            int eventIntervalUnit = Integer.parseInt(String.valueOf(request.getParameter("form_add_time_frame_time_unit")));

            scheduleData.setTimeBetweenFires(getEventIntervalInMills(eventIntervalUnformated, eventIntervalUnit));
          } else {
            scheduleData.setSimpleEvent(true);
          }

          AdministrationFlowScheduleInterface adminFlowScheduleBean = BeanFactory.getAdministrationFlowScheduleBean();

          boolean jobExists = adminFlowScheduleBean.checkIfJobExists(userInfo, scheduleData);
          if (!jobExists) {
            operationResult = adminFlowScheduleBean.createNewFlowScheduleEvent(userInfo, scheduleData);
          } else {
            Logger.error(userInfo.getUtilizador(), this, "add_new_flow_schedule", "Unable to create job, infringement of duplication restrictions");
            operationResult = false;
          }
        } else {
          StringBuffer errorMsg = new StringBuffer("Unable to complete operation. One of more fields has invalid data:");
          errorMsg.append(" flowId [").append(flowId).append("]");
          errorMsg.append(" profileName [").append(profileName).append("]");
          errorMsg.append(" userName [").append(userName).append("]");
          errorMsg.append(" eventDateStr [").append(eventDateStr).append("]");
          Logger.error(userInfo.getUtilizador(), this, "add_new_flow_schedule", errorMsg.toString());

          operationResult = false;
        }
      } else {
        StringBuffer errorMsg = new StringBuffer("Unable to complete operation.");
        errorMsg.append(" user selected date [").append(dateFormat_FullDate.format(eventTriggerTime.getTime())).append("]");
        errorMsg.append(" before today");
        Logger.error(userInfo.getUtilizador(), this, "add_new_flow_schedule", errorMsg.toString());
      }

    } catch (NumberFormatException e) {
      operationResult = false;
      Logger.error(userInfo.getUtilizador(), this, "add_new_flow_schedule", "Invalid conversion of parameter ", e);
    } catch (ParseException e) {
      operationResult = false;
      Logger.error(userInfo.getUtilizador(), this, "add_new_flow_schedule", "Invalid conversion of parameter ", e);
    } catch (Exception e) {
      operationResult = false;
      Logger.error(userInfo.getUtilizador(), this, "add_new_flow_schedule", "Unforeseen error ", e);
    }
    return operationResult;
  }

  private long getEventIntervalInMills(long eventIntervalUnformated, int eventIntervalUnit) {
    long eventIntervalInMills = 0;
    if (EVENTS_TIME_FRAME_UNIT_HOURS == eventIntervalUnit) {
      eventIntervalInMills = returnHoursInMills(eventIntervalUnformated);
    } else if (EVENTS_TIME_FRAME_UNIT_DAYS == eventIntervalUnit) {
      eventIntervalInMills = returnDaysInMills(eventIntervalUnformated);
    }
    return eventIntervalInMills;
  }

  private long returnHoursInMills(long eventIntervalUnformated) {
    long returnTime = 0;
    if (eventIntervalUnformated > 0) {
      returnTime = eventIntervalUnformated * 60 * 60 * 1000;
    }
    return returnTime;
  }

  private long returnDaysInMills(long eventIntervalUnformated) {
    long returnTime = 0;
    if (eventIntervalUnformated > 0) {
      returnTime = returnHoursInMills(eventIntervalUnformated * 24);
    }
    return returnTime;
  }

  private boolean deleteJob(UserInfoInterface userInfo, String jobName) {
    boolean jobDeletionResult = false;
    if (jobName != null && jobName.length() > 0) {
      AdministrationFlowScheduleInterface adminFlowScheduleBean = BeanFactory.getAdministrationFlowScheduleBean();
      jobDeletionResult = adminFlowScheduleBean.deleteJob(userInfo, jobName);
    } else {
      Logger.error(userInfo.getUtilizador(), this, "deleteJob", "Unable to delete flow schedule job, jobName is null or empty");
    }
    return jobDeletionResult;
  }

  private void populateListData(UserInfoInterface userInfo, HttpServletRequest request) {
    AdministrationFlowScheduleInterface adminFlowScheduleBean = BeanFactory.getAdministrationFlowScheduleBean();
    String user = null;
    updateListOfFlows(userInfo);
    ArrayList<FlowScheduleDataInterface> listOfJobs = adminFlowScheduleBean.getScheduledFlowsJobs(userInfo, user);

    for (FlowScheduleDataInterface job : listOfJobs) {
      job.setFlowName(flowsHashMap.get(String.valueOf(job.getFlowId())));
    }

    HttpSession session = request.getSession();
    session.setAttribute("flow_events_list", listOfJobs);
  }

  private void populateAddFormData(UserInfoInterface userInfo, HttpServletRequest request) {
    listOfFlows = updateListOfFlows(userInfo);
    request.setAttribute("flowItems", listOfFlows);
    listOfProfiles = updateListOfProfiles(userInfo);
    request.setAttribute("profiles", listOfProfiles);
    listOfUsers = updateListOfUsers(userInfo);
    request.setAttribute("users", listOfUsers);
    ArrayList<ComboBoxItems> listOfEventsTimeUnit = updateListOfTimeIntervalsUnits(userInfo);
    request.setAttribute("timeIntervalsUnits", listOfEventsTimeUnit);
  }

  private void clearFlowScheduleParameters(UserInfoInterface userInfo, HttpServletRequest request) {
    request.removeAttribute("form_add_flow");
    request.removeAttribute("form_add_profile");
    request.removeAttribute("selectedProfileName");
    request.removeAttribute("form_add_user");
    request.removeAttribute("selectedUserName");
    request.removeAttribute("eventDateStr");
    request.removeAttribute("flowScheduleMsgListMsgToUser");
    request.removeAttribute("jobName");
  }

  private ArrayList<ComboBoxItems> updateListOfFlows(UserInfoInterface userInfo) {
    IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo);

    listOfFlows = new ArrayList<ComboBoxItems>();
    for (IFlowData flowData : fda) {
      flowsHashMap.put(String.valueOf(flowData.getId()), flowData.getName());
      listOfFlows.add(new ComboBoxItems(String.valueOf(flowData.getId()), flowData.getName()));
    }
    return listOfFlows;
  }

  private ArrayList<ComboBoxItems> updateListOfProfiles(UserInfoInterface userInfo) {
    UserManager manager = BeanFactory.getUserManagerBean();

    listOfProfiles = new ArrayList<ComboBoxItems>();
    ProfilesTO[] profiles = manager.getAllProfiles(userInfo);
    for (ProfilesTO profile : profiles) {
      // String [] userProf = manager.getProfileUsers(ui, profileId); AQUISIÇÃO DOS USERS DO PERFIL
      listOfProfiles.add(new ComboBoxItems(String.valueOf(profile.getProfileId()), profile.getName()));
    }
    return listOfProfiles;
  }

  private ArrayList<ComboBoxItems> updateListOfUsers(UserInfoInterface userInfo) {
    Collection<UserData> users = null;
    try {
      AuthProfile authProfileBean = BeanFactory.getAuthProfileBean();
      users = authProfileBean.getAllUsers(userInfo.getOrganization());
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "updateListOfUsers", "unable to acquire list of users", e);
    }

    listOfUsers = new ArrayList<ComboBoxItems>();
    if (users != null) {
      for (UserData user : users) {
        try {
          listOfUsers.add(new ComboBoxItems(user.getUsername(), user.getUsername() + " - " + user.getName()));
        } catch (NumberFormatException e) {
          Logger.error(userInfo.getUtilizador(), this, "updateListOfUsers", "Error while processing user id", e);
        }
      }
    }
    return listOfUsers;
  }

  private Calendar getEventTriggerTimeCalendarObject(Date date, String eventTimeStr) {
    String eventStartTime = "00:00";
    if (eventTimeStr != null && eventTimeStr.trim().length() == 5){
      boolean isValidTime = Utils.checkStringToPattern(eventTimeStr, "^([0-1][0-9]|[2][0-4]):([0-5][0-9])");

      if (isValidTime){
        eventStartTime = eventTimeStr;
      }
    }

    Calendar eventTriggerTime = Calendar.getInstance();
    eventTriggerTime.setTime(date);

    String[] hourElements = eventStartTime.split(":");

    eventTriggerTime.set(Calendar.HOUR, Integer.parseInt(hourElements[0]));
    eventTriggerTime.set(Calendar.MINUTE, Integer.parseInt(hourElements[1]));

    eventTriggerTime.getTimeInMillis();

    return eventTriggerTime;
  }

  private ArrayList<ComboBoxItems> updateListOfTimeIntervalsUnits(UserInfoInterface userInfo) {
    IMessages messages = userInfo.getMessages();

    ArrayList<ComboBoxItems> listOfIntervals = new ArrayList<ComboBoxItems>();
    listOfIntervals.add(new ComboBoxItems(String.valueOf(EVENTS_TIME_FRAME_UNIT_HOURS), 
        messages.getString("flow_schedule.add.field.combobox.time_between_events_hours")));
    listOfIntervals.add(new ComboBoxItems(String.valueOf(EVENTS_TIME_FRAME_UNIT_DAYS), 
        messages.getString("flow_schedule.add.field.combobox.time_between_events_days")));
    return listOfIntervals;
  }

  public static class ComboBoxItems {
    private String comboId;
    private String comboName;

    public ComboBoxItems(String id, String name) {
      this.setComboId(id);
      this.setComboName(name);
    }

    public String getComboId() {
      return comboId;
    }

    public void setComboId(String comboId) {
      this.comboId = comboId;
    }

    public String getComboName() {
      return comboName;
    }

    public void setComboName(String comboName) {
      this.comboName = comboName;
    }
  }
}