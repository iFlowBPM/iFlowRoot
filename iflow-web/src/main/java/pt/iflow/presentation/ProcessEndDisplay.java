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
package pt.iflow.presentation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.presentation.FlowAppMenu;
import pt.iflow.api.presentation.FlowApplications;
import pt.iflow.api.presentation.FlowMenu;
import pt.iflow.api.presentation.FlowMenuItems;
import pt.iflow.api.presentation.PresentationManager;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.presentation.ProcessEndDisplaySettings.TaskType;
import pt.iknow.utils.StringUtilities;

public class ProcessEndDisplay {

  public static String processTasks(UserInfoInterface userInfo, HttpServletResponse response) {
    return processTasks(userInfo, response, null);
  }
 
  public static String processTasks(UserInfoInterface userInfo, HttpServletResponse response, int flowid) {
	  ProcessEndDisplaySettings peds = new ProcessEndDisplaySettings();
		  if(Const.sPROCESS_END_STYLE.equals(Const.PROCESS_END_STYLE_FLOW)){
			  peds.setFlowId(flowid);
			  peds.setTaskType(TaskType.LATEST);
		  }
	   return processTasks(userInfo, response, peds);
	  }
  
  public static String processTasks(UserInfoInterface userInfo, HttpServletResponse response, ProcessEndDisplaySettings settings) {
    String pageContent = "proc_end";
    return PresentationManager.buildPage(response, userInfo, buildTasks(userInfo, settings), pageContent);
  }

  private static Hashtable<String, Object> buildTasks(UserInfoInterface userInfo, ProcessEndDisplaySettings settings) {
    IMessages messages = userInfo.getMessages();
    ProcessManager pm = BeanFactory.getProcessManagerBean();
    Map<String, IFlowData> hmFlows = new HashMap<String, IFlowData>();
    
    if (settings == null)
      settings = ProcessEndDisplaySettings.getDefault();
    
    String flowName = "";
    if (settings.hasFlowid()) {
      IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, settings.getFlowid());
      hmFlows.put(String.valueOf(fd.getId()), fd);
      flowName = fd.getName();
    }
    else {
      FlowApplications appInfo = BeanFactory.getFlowApplicationsBean();
      FlowMenu appflows = appInfo.getAllApplicationOnlineFlows(userInfo, null, null);
      Collection<FlowAppMenu> appMenuList = appflows.getAppMenuList();
      Iterator<FlowAppMenu> itera = appMenuList.iterator();
      while (itera != null && itera.hasNext()) {
        FlowAppMenu appMenu = itera.next();
        FlowMenuItems items = appMenu.getMenuItems();
        List<IFlowData> al = items.getFlows();
        for (int i = 0; al != null && i < al.size(); i++) {
          IFlowData fd = al.get(i);
          String sFlowId = String.valueOf(fd.getId());
          hmFlows.put(sFlowId, fd);
        }
      }
    }
    ListIterator<Activity> it = pm.getUserActivitiesOrderByPid(userInfo);
    List<Activity> alAct = new ArrayList<Activity>();
    Activity a;
    while (it != null && it.hasNext()) {
      a = (Activity) it.next();
      if (a != null) {
        if (hmFlows.containsKey(String.valueOf(a.flowid))) {
          if (settings != null && settings.getType() == ProcessEndDisplaySettings.TaskType.LATEST) {
            alAct.add(a);            
          }
          else {
            alAct.add(0,a);                        
          }
        }
      }
    }
    List<Map<String, String>> alNew = new ArrayList<Map<String, String>>();
    Timestamp tsNow = new Timestamp((new java.util.Date()).getTime());
    for (Activity act : alAct) {
      Map<String, String> hm = new HashMap<String, String>();
      IFlowData fd = hmFlows.get(String.valueOf(act.flowid));
      if (fd == null) {
        continue;
      }
      String sAppName = fd.getApplicationName();
      if (sAppName == null) {
        sAppName = ""; // support for non-categorized flows
      }
      String sFlow = fd.getName();
      String sFlowId = String.valueOf(fd.getId());
      String sPid = String.valueOf(act.pid);
      String sSubPid = String.valueOf(act.subpid);
      String sDesc = act.description;
      String sCreated = DateUtility.formatTimestamp(userInfo, act.created);
      String sDuration = Utils.getDuration(new Timestamp(act.created.getTime()), tsNow);
      String sUri = "";
      if (act.url != null && StringUtilities.isNotEmpty(act.url)) {
        if (act.url.indexOf("?") > -1) {
          sUri = act.url.substring(0, act.url.indexOf("?"));
        } else {
          sUri = act.url;
        }
      } else {
        sUri = "error.jsp";
      }
      String pnumber = act.pnumber;
      hm.put("appname", sAppName);
      hm.put("flowname", sFlow);
      hm.put("flowid", sFlowId);
      hm.put("pid", sPid);
      hm.put("subpid", sSubPid);
      hm.put("desc", sDesc);
      hm.put("created", sCreated);
      hm.put("duration", sDuration);
      hm.put("uri", sUri);
      hm.put("pnumber", pnumber);
      hm.put("delegated", act.delegated ? "1" : "0");
      hm.put("delegated_alt", messages.getString("main_content.tasks.delegated.alt"));
      hm.put("runMax", String.valueOf(fd.runMaximized()));
      hm.put("read", act.isRead() ? "1" : "0");
      alNew.add(hm);
      
      if (alNew.size() >= settings.getNumTasks())
        break;
    }
    boolean contains = false;
    Iterator<Map<String, String>> iter = alNew.iterator();
    while (iter.hasNext()) {
      Map<String, String> hm = iter.next();
      if (StringUtils.isNotEmpty(hm.get("appname"))) {
        contains = true;
        break;
      }
    }
    Hashtable<String, Object> hsSubstLocal = new Hashtable<String, Object>();
    hsSubstLocal.put("messages", messages);
    String labelKey = settings.getType() == ProcessEndDisplaySettings.TaskType.NEWEST ? "proc_end.tasks.mostRecentMsg" : "proc_end.tasks.mostLatestMsg"; 
    if (settings.hasFlowid() && StringUtils.isNotEmpty(flowName)) 
      labelKey += "WithFlow";    
    hsSubstLocal.put("label", messages.getString(labelKey, flowName));
    hsSubstLocal.put("hasActivities", Boolean.valueOf(alAct.size() > 0));
    boolean hasMore = alAct.size() > settings.getNumTasks();
    hsSubstLocal.put("hasMoreActivities", hasMore);
    String moreParams = "";
    if (hasMore) {
      if (settings.hasFlowid()) {
        long ts = new Date().getTime();
        moreParams = "flowid=" + settings.getFlowid() + 
          "&showflowid=" + settings.getFlowid() + "&ts=" + ts;
      }
    }
    hsSubstLocal.put("moreParams", moreParams);
    hsSubstLocal.put("newact", alNew);
    hsSubstLocal.put("has_appname", new Boolean(contains));
    hsSubstLocal.put("url_prefix", Const.APP_URL_PREFIX.substring(0, Const.APP_URL_PREFIX.length() - 1));
    hsSubstLocal.put("ts", Long.toString(System.currentTimeMillis()));
    hsSubstLocal.put("closeType", ((userInfo.isGuest()) ? "hidden" : "button"));
    return hsSubstLocal;
  }
}
