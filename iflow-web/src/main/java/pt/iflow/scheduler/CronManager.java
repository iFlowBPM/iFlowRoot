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
package pt.iflow.scheduler;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flowSchedule.FlowScheduleDataInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.flowSchedule.FlowScheduleData;

public class CronManager {

  private static CronManager instance = null;
  private Scheduler scheduler = null;

  private static final String ARCHIVER_JOB_NAME = "ArchiverJob";
  private static final String IFLOW_CORE_GROUP_NAME = "iFlowCore";
  private static final String SCHEDULED_FLOWS_GROUP_NAME = "ScheduledFlows";

  private CronManager() {
    // start scheduler
    SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
    try {
      Properties props = Setup.readPropertiesFile("quartz.properties");

      // Quartz DataSource (nao confundir com as datasources do weblogic)
      String dsName = "org.quartz.dataSource." + props.getProperty("org.quartz.jobStore.dataSource");
      props.setProperty(dsName + ".jndiURL", Const.NAME_DB_POOL);
      props.setProperty(dsName + ".jndiAlwaysLookup", "true");
      // props.setProperty(dsName + ".java.naming.factory.initial", Const.INITIAL_CONTEXT_FACTORY);
      // props.setProperty(dsName + ".java.naming.provider.url", Const.WL_URL);

      ((StdSchedulerFactory)schedFact).initialize(props);

      scheduler = schedFact.getScheduler();

    } catch (SchedulerException e) {
      throw new Error("Could not create a new Scheduler", e);
    }

  }

  public static CronManager getInstance() {
    if(null == instance) instance = new CronManager();
    return instance;
  }

  public static void startManager() {
    getInstance().start();
  }

  public static void stopManager() {
    getInstance().shutdown();
  }

  public boolean schedule(String cronExpr, String jobName, Class<? extends Job> excutor) {
    boolean result = false;
    try {
      JobDetail jobDetail = new JobDetail(jobName, null, excutor);
      Trigger cronTrigger = new CronTrigger(jobName,null,cronExpr);
      scheduler.scheduleJob(jobDetail, cronTrigger);
      result = true;
    } catch(Throwable t) {
      t.printStackTrace();
    }
    return result;
  }

  public void start() {
    try {
      scheduler.start();
    } catch (SchedulerException e) {
      e.printStackTrace();
    }

    updateArchiveJob();
  }

  public void stop() {
    try {
      scheduler.standby();
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

  public void shutdown() {
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

  // Flow Jobs scheduling

  public void scheduleCron(String cronExpression, int flowId, String userName, String profile, String extra) {
    String jobName = String.valueOf(flowId)+"-"+userName;
    JobDetail jobDetail = new JobDetail(jobName, SCHEDULED_FLOWS_GROUP_NAME, FlowJob.class);
    JobDataMap dataMap = jobDetail.getJobDataMap();
    dataMap.putAsString(FlowJob.PROP_FLOWID, flowId);
    dataMap.put(FlowJob.PROP_USERNAME, userName);
    dataMap.put(FlowJob.PROP_USERAUTH, profile);
    dataMap.put(FlowJob.PROP_EXTRA_PARAMS, extra);

    CronTrigger ct = new CronTrigger(jobName, SCHEDULED_FLOWS_GROUP_NAME, jobName, SCHEDULED_FLOWS_GROUP_NAME);
    try {
      ct.setCronExpression(cronExpression);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }

    try {
      scheduler.scheduleJob(jobDetail, ct);
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

  public void scheduleJob(Date startTime, long repeat, int flowId, String userName, String profile, String extra) {
    String jobName = String.valueOf(flowId)+"-"+userName;
    JobDetail jobDetail = new JobDetail(jobName, SCHEDULED_FLOWS_GROUP_NAME, FlowJob.class);
    JobDataMap dataMap = jobDetail.getJobDataMap();
    dataMap.putAsString(FlowJob.PROP_FLOWID, flowId);
    dataMap.put(FlowJob.PROP_USERNAME, userName);
    dataMap.put(FlowJob.PROP_USERAUTH, profile);
    dataMap.put(FlowJob.PROP_EXTRA_PARAMS, extra);

    Trigger trigger = new SimpleTrigger(jobName, SCHEDULED_FLOWS_GROUP_NAME, SimpleTrigger.REPEAT_INDEFINITELY, repeat);
    if (null != startTime) {
      trigger.setStartTime(startTime);
    }

    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

  public void scheduleJob(Date startTime, int flowId, String userName, String profile, String extra) {
    String jobName = String.valueOf(flowId)+"-"+userName;
    JobDetail jobDetail = new JobDetail(jobName, SCHEDULED_FLOWS_GROUP_NAME, FlowJob.class);
    JobDataMap dataMap = jobDetail.getJobDataMap();
    dataMap.putAsString(FlowJob.PROP_FLOWID, flowId);
    dataMap.put(FlowJob.PROP_USERNAME, userName);
    dataMap.put(FlowJob.PROP_USERAUTH, profile);
    dataMap.put(FlowJob.PROP_EXTRA_PARAMS, extra);

    Trigger trigger = new SimpleTrigger(jobName, SCHEDULED_FLOWS_GROUP_NAME);
    if(null != startTime) trigger.setStartTime(startTime);


    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      e.printStackTrace();
    }

  }

  public boolean unscheduleJob(int flowId, String userName) {
    String jobName = String.valueOf(flowId)+"-"+userName;
    return unscheduleJob(null, jobName);
  }

  public String [] getJobNames() {
    return getJobNames(null);
  }

  public String [] getJobNames(String userName) {
    String [] result = new String[0];

    try {
      String [] names = scheduler.getJobNames(SCHEDULED_FLOWS_GROUP_NAME);
      List<String> allJobs = new ArrayList<String>();
      for (int i = 0; i < names.length; i++) {
        String [] jobTokens = names[i].split("-"); // first is flowid, second is user name

        if(null != userName && !userName.equalsIgnoreCase(jobTokens[1])) continue;
        allJobs.add(names[i]);
      }
      result = allJobs.toArray(new String[allJobs.size()]);
    } catch (SchedulerException e) {
      e.printStackTrace();
    }

    return result;
  }

  public Map<String,Object> getJobDetail(String jobName) {
    Map<String,Object> result = new HashMap<String,Object>();
    try {
      Trigger t = scheduler.getTrigger(jobName, SCHEDULED_FLOWS_GROUP_NAME);

      JobDetail jd = scheduler.getJobDetail(jobName, SCHEDULED_FLOWS_GROUP_NAME);
      result.put("description", jd.getDescription());
      result.put("fullname", jd.getFullName());
      result.put("name", jd.getName());
      result.put("group", jd.getGroup());
      result.put("class", jd.getJobClass().getName());
      result.put("durable", String.valueOf(jd.isDurable()));
      result.put("stateful", String.valueOf(jd.isStateful()));
      result.put("volatile", String.valueOf(jd.isVolatile()));
      long firstRunTime = t.getStartTime().getTime();
      result.put("startTime", firstRunTime);
      long nextFireTime = t.getNextFireTime().getTime();
      result.put("nextFireTime", nextFireTime);

      String timeBeteweenFires = "--";
      boolean isSimpleEvent = true;
      if (t.getFinalFireTime() == null) {
        timeBeteweenFires = Utils.getDuration(new Timestamp(nextFireTime), new Timestamp(t.getFireTimeAfter(t.getNextFireTime()).getTime()));
        isSimpleEvent = false;
      }
      result.put("timeBeteweenFires", timeBeteweenFires);
      result.put("isSimpleEvent", isSimpleEvent);

      JobDataMap dm = jd.getJobDataMap();
      if(null != dm) {
        String [] keys = dm.getKeys();
        for(int i = 0; null != keys && i < keys.length; i++) {
          result.put("datamap."+keys[i], dm.get(keys[i]));
        }
      }

    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    return result;
  }

  public Map<String,Object> getJobDetail(int flowId, String userName) {
    return getJobDetail(String.valueOf(flowId)+"-"+userName);
  }

  // Process Archive

  private void updateArchiveJob() {
    try {
      scheduler.deleteJob(ARCHIVER_JOB_NAME, IFLOW_CORE_GROUP_NAME);
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    try {
      JobDetail jobDetail = new JobDetail(ARCHIVER_JOB_NAME, IFLOW_CORE_GROUP_NAME, AutoArchiveJob.class);
      Trigger cronTrigger = new CronTrigger(ARCHIVER_JOB_NAME, IFLOW_CORE_GROUP_NAME,Const.AUTO_ARCHIVE_CRON);
      scheduler.scheduleJob(jobDetail, cronTrigger);
    } catch(Throwable t) {
      t.printStackTrace();
    }
  }

  public static class AutoArchiveJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
      UserInfoInterface userInfo = BeanFactory.getUserInfoFactory().newArchiverUserInfo();

      BeanFactory.getProcessManagerBean().archiveProcesses(userInfo);
    }
  }

  public ArrayList<FlowScheduleDataInterface> getScheduledFlowsJobs(UserInfoInterface userInfo, String fromUser) {
    ArrayList<FlowScheduleDataInterface> listOfJobs = new ArrayList<FlowScheduleDataInterface>();
    String[] listOfJobsNames = getJobNames(fromUser);

    if (listOfJobsNames != null && listOfJobsNames.length > 0) {
      Map<String, Object> jobDetail = null;
      for (String jobName : listOfJobsNames) {
        FlowScheduleDataInterface job = null;
        jobDetail = getJobDetail(jobName);
        if (jobDetail != null) {
          job = new FlowScheduleData();
          String flowIdStr = String.valueOf(jobDetail.get("datamap.flowid"));
          try {
            job.setFlowId(Integer.parseInt(flowIdStr));
          } catch (NumberFormatException e) {
            Logger.error(userInfo.getUtilizador(), this, "getScheduledFlowsJobs", "Unable to process flow id", e);
          }

          job.setUserAssigned(String.valueOf(jobDetail.get("datamap.username")));
          job.setJobName(String.valueOf(jobDetail.get("name")));
          job.setUserAssignedProfile(String.valueOf(jobDetail.get("datamap.userauth")));
          job.setExtra(String.valueOf(jobDetail.get("datamap.extraParams")));

          try {
            String startTimeStr = String.valueOf(jobDetail.get("startTime"));
            job.setStartTime(new Timestamp(Long.parseLong(startTimeStr)));
          } catch (NumberFormatException e) {
            Logger.error(userInfo.getUtilizador(), this, "getScheduledFlowsJobs", "Unable to process event startTime", e);
          }

          try {
            String nextFireTimeStr = String.valueOf(jobDetail.get("nextFireTime"));
            job.setNextFireDate(new Timestamp(Long.parseLong(nextFireTimeStr)));
          } catch (NumberFormatException e) {
            Logger.error(userInfo.getUtilizador(), this, "getScheduledFlowsJobs", "Unable to process event nextFire", e);
          }

          job.setFormatedTimeBetweenExecutions(String.valueOf(jobDetail.get("timeBeteweenFires")));

          if ("true".equals(String.valueOf(jobDetail.get("timeBeteweenFires")))) {
            job.setSimpleEvent(true);
          }else {
            job.setSimpleEvent(false);
          }

          listOfJobs.add(job);
        }
      }
    }
    return listOfJobs;
  }

  public boolean unscheduleJob(UserInfoInterface userInfo, String jobName) {
    String userName = "CronManager";
    if (userInfo != null) {
      userName = userInfo.getUtilizador();
    }
    boolean result = false;
    try {
      result = scheduler.unscheduleJob(jobName, SCHEDULED_FLOWS_GROUP_NAME);
    } catch (SchedulerException e) {
      Logger.error(userName, this, "unscheduleJob", "Unable to remove flow schedule", e);
    }
    return result;
  }

}
