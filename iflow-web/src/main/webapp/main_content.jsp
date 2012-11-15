<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file="inc/defs.jsp"%>
<%@ page import="org.apache.velocity.app.tools.VelocityFormatter"%>
<%@ page import="org.apache.commons.collections15.OrderedMap"%>
<%@ page import="org.apache.commons.collections15.map.ListOrderedMap"%>
<%@ page import="pt.iflow.api.notification.Notification"%>
<%@ page import="pt.iflow.api.delegations.DelegationInfo"%>
<%@ page import="pt.iflow.api.delegations.DelegationInfoData"%>
<%@ page import="pt.iflow.info.DefaultInfoGenerator"%>
<%@ page import="pt.iflow.api.presentation.FlowApplications"%>
<%@ page import="pt.iflow.api.core.Activity"%>
<%@ page import="java.sql.Timestamp"%>
<%@page import="pt.iflow.api.folder.Folder"%>
<%@ page import="pt.iflow.processannotation.ProcessAnnotationManagerBean"%>
<%@page import="pt.iflow.api.processannotation.*"%>
<%@page import="pt.iflow.api.filters.FlowFilter"%>
<script language="JavaScript">
function assignActivity(folderid, sactivity){
		tabber_right(1, '<%=response.encodeURL("main_content.jsp")%>?setfolder='+folderid+'&activities='+sactivity);
}

function removeActivityFolder(sactivity){
	tabber_right(1, '<%=response.encodeURL("main_content.jsp")%>?removeactivities='+sactivity);
}

function filterActivity(id, op){
	if(op==1){ //filtrar label
		tabber_right(1, '<%=response.encodeURL("main_content.jsp")%>?filterlabel='+id);
	}
	if(op == 2){ //filtrar days
		tabber_right(1, '<%=response.encodeURL("main_content.jsp")%>?filterdays='+id);
	}
	if(op == 3){ //filtrar folder
		tabber_right(1, '<%=response.encodeURL("main_content.jsp")%>?filterfolder='+id);
	}
}
function cleanFilter(){
	tabber_right(1, '<%=response.encodeURL("main_content.jsp")%>?cleanFilter=1');
}
</script>
<%

	//ASSIGN ACTIVITIES TO FOLDER
	String setfolder = fdFormData.getParameter("setfolder");
	if(setfolder!=null){
	 	  FolderManager fm = BeanFactory.getFolderManagerBean();
	  String actividades = fdFormData.getParameter("activities");
	  fm.setActivityToFolder(userInfo,setfolder,actividades);
	}

	  //REMOVE ACTIVITIES FROM FOLDER
	  String removeactivities = fdFormData.getParameter("removeactivities");
	  if(removeactivities!=null){
	    FolderManager fm = BeanFactory.getFolderManagerBean();
		  fm.setActivityToFolder(userInfo,null,removeactivities);
	  }
	
  final DefaultInfoGenerator infoGen = new DefaultInfoGenerator();
  FlowApplications appInfo = BeanFactory.getFlowApplicationsBean();
  int MAX_ITEMS = 5;

  String sPage = "main";
  String title = "P&aacute;gina Principal";

  java.util.Hashtable<String,Object> hsSubstLocal = new java.util.Hashtable<String,Object>();
  StringBuffer sbError = new StringBuffer();

  String data = (String) fdFormData.getParameter("data");
  if (data == null)
    data = "tasks";
  String pageContent = "task_list";

  String tabnr = (String) fdFormData.getParameter("navtabnr");

  String scroll = (String) fdFormData.getParameter("scroll");
  if(scroll != null) session.setAttribute("filtro_scroll",scroll);
  
  String pid = (String) fdFormData.getParameter("pid");
  String subpid = (String) fdFormData.getParameter("subpid");
  if(pid != null && subpid != null){
	  session.setAttribute("filtro_pid",pid);
	  session.setAttribute("filtro_subpid",subpid);
  }
  
  
  try {
    if (data.equals("procs")) {
      // start processes
  
      String whichTab = "";
      try {
       whichTab = (String) fdFormData.getParameter("tabnm");
       if (whichTab == null) whichTab = "dashboard";
      } 
      catch (Exception e) {
      }
       
      // title = "Iniciar Processos";
      title = messages.getString("main_content.procs.title");

      String selectedFlow = fdFormData.getParameter("flowid");
      if (selectedFlow != null && selectedFlow != "") {
        hsSubstLocal.put("flowid", selectedFlow);
      }
      
      OrganizationThemeData orgTheme = BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo);
      hsSubstLocal.put("menuStyle", orgTheme.getMenuStyle());
	  boolean procMenuVisible = !StringUtils.equals(tabnr, String.valueOf(HelpNavConsts.TOPIC_TASKS)) || orgTheme.getProcMenuVisible();       
      hsSubstLocal.put("procMenuVisible", procMenuVisible ? "visible" : "hidden");

      if (procMenuVisible) {
      	ArrayList<OrderedMap<Object,Object>> appFlows = new ArrayList<OrderedMap<Object,Object>>();
        boolean showOnlyFlowsToBePresentInMenu = true;
      	FlowMenu flows = appInfo.getAllApplicationOnlineMenu(userInfo, null, FlowType.returnProcessExcludedTypes(), showOnlyFlowsToBePresentInMenu);

	  	Collection<FlowAppMenu> appMenuList = flows.getAppMenuList();
	  	Iterator<FlowAppMenu> iter = appMenuList.iterator();
      	while(iter != null && iter.hasNext()) {
	        FlowAppMenu appMenu = iter.next();
			String sAppName = appMenu.getAppDesc();
        	FlowMenuItems menuPart = appMenu.getMenuItems();
        	OrderedMap<Object,Object> hm = new ListOrderedMap<Object,Object>();
        	if ("".equals(sAppName)) sAppName = messages.getString("main_content.processes.appname.misc");
        	hm.put("appname", sAppName);
        
        	hm.put("appid", appMenu.getAppID());

    		List<IFlowData> currAppflows = menuPart.getFlows(); 
        
    		hm.put("selected", false);
        	if (StringUtils.isNotEmpty(selectedFlow)) {
	        	for (IFlowData appfd : currAppflows) {
    	      		if (StringUtils.equals(String.valueOf(appfd.getId()), selectedFlow)) {
        	    		hm.put("selected", true);
            			break;
	          		}
    	    	}
        	}
		        
        	hm.put("flows", menuPart.getFlows());
        	hm.put("links", menuPart.getLinks());
        	hm.put("tooltip_flow", messages.getString("main_content.processes.tooltip.flows"));
        	hm.put("tooltip_link", messages.getString("main_content.processes.tooltip.links"));
        
	        appFlows.add(hm);
      	}
      	hsSubstLocal.put("appflows", appFlows);
        hsSubstLocal.put("processesMsg", appFlows.isEmpty() ? messages.getString("main_content.procs.emptyMsg") : messages.getString("main_content.procs.processesMsg"));
      }
      hsSubstLocal.put("tabnm", whichTab);
            
      hsSubstLocal.put("rootNodeName",messages.getString("main_content.processes.tree.rootNodeName"));
      if (!hsSubstLocal.containsKey("flowid") && session.getAttribute("flowid") != null) {
        hsSubstLocal.put("flowid", session.getAttribute("flowid"));
      }
      pageContent = "proc_list";

    }
  else if (data.equals("delegs")) {
      // RECEIVED DELEGATIONS

	title = messages.getString("main_content.delegs.title");

      DelegationInfo delegInfo = BeanFactory.getDelegationInfoBean();

      List<DelegationInfoData> receivedDelegs = new ArrayList<DelegationInfoData>();
      Iterator<DelegationInfoData> iter = delegInfo.getDeployedReceivedDelegations(userInfo).iterator();
      while (iter.hasNext() && receivedDelegs.size() < MAX_ITEMS) {
        receivedDelegs.add(iter.next());
      }
      hsSubstLocal.put("receivedDelegs", receivedDelegs);
      hsSubstLocal.put("receivedSize", new Integer(receivedDelegs.size()));
    if (iter.hasNext()) {
        hsSubstLocal.put("hasMoreReceived", Boolean.TRUE);
      }

      // SENT DELEGATIONS
      List<DelegationInfoData> sentDelegs = new ArrayList<DelegationInfoData>();
      iter = delegInfo.getDeployedSentDelegations(userInfo).iterator();
      while (iter.hasNext() && sentDelegs.size() < MAX_ITEMS) {
        sentDelegs.add(iter.next());
      }
      hsSubstLocal.put("sentDelegs", sentDelegs);
      hsSubstLocal.put("sentSize", new Integer(sentDelegs.size()));
      if (iter.hasNext()) {
        hsSubstLocal.put("hasMoreSent", Boolean.TRUE);
      }
      pageContent = "deleg_list";
    }
  else if (data.equals("tasks")){
      // FLOWS, ACTIVITIES AND NOTIFICATIONS




	int nNEWEST_LIMIT = 10;
	int nOLDEST_LIMIT = 5;
	int nNOTIFICATION_LIMIT = 5;
	int nAll_Tasks = 0;

	// get online flows with app information
    FlowMenu appflows = appInfo.getAllApplicationOnlineFlows(userInfo, null);


	// now build map with key flowid and value flowdata
	Map<String, IFlowData> hmFlows = new HashMap<String, IFlowData>();
	Collection<FlowAppMenu> appMenuList = appflows.getAppMenuList();
	Iterator<FlowAppMenu> itera = appMenuList.iterator();
	
	while (itera != null && itera.hasNext()) {
	  	FlowAppMenu appMenu = itera.next();
        			String sAppName = appMenu.getAppDesc();
        			
        			FlowMenuItems items = appMenu.getMenuItems();
		List<IFlowData> al = items.getFlows();
		for (int i=0; al != null && i < al.size(); i++) {
			IFlowData fd = al.get(i);
			String sFlowId = String.valueOf(fd.getId());
			hmFlows.put(sFlowId, fd);
		}
	}
	// free unused objs
	itera = null;
	appflows = null;

//FILTROS
	//CLEAN				
	String cleanFilter = "0";
	cleanFilter = fdFormData.getParameter("cleanFilter");
	  if ("1".equals(cleanFilter)) {
	    session.removeAttribute("filterlabel");
	    session.removeAttribute("filterdays");
	    session.removeAttribute("filterfolder");
	  }
	  //FILTER BY FOLDER
	  int selectedFolder = 0;
	  String filterfolder = fdFormData.getParameter("filterfolder");
	  if(filterfolder!=null){
	    session.setAttribute("filterfolder",filterfolder); // Utilizador actualizou valor
	  }
	  if (filterfolder == null){
	    filterfolder = (String) session.getAttribute("filterfolder");
	  }
	  if(filterfolder!=null){
		  try{
			selectedFolder = Integer.parseInt(filterfolder);
		  }catch(Exception e){
			  selectedFolder = 0;
		  }
	  }
	  //FILTER BY LABEL
	  int selectedLabel = 0;
	  String filterlabel = fdFormData.getParameter("filterlabel");
	  if(filterlabel!=null){
	    session.setAttribute("filterlabel",filterlabel); // Utilizador actualizou valor
	  }
	  if (filterlabel == null){
	    filterlabel = (String) session.getAttribute("filterlabel");
	  }
	  if(filterlabel!=null){
		  try{
			selectedLabel = Integer.parseInt(filterlabel);
		  }catch(Exception e){
		    selectedLabel = 0;
		  }
	  }
	  //FILTER BY DAYS
	  int selectedDays = 0;
	  String filterdays = fdFormData.getParameter("filterdays");
	  if(filterdays!=null){
	    session.setAttribute("filterdays",filterdays); // Utilizador actualizou valor
	  }
	  if (filterdays == null){
	    filterdays = (String) session.getAttribute("filterdays");
	  }
	  if(filterdays!=null){
		  try{
		    selectedDays = Integer.parseInt(filterdays);
		  }catch(Exception e){
		    selectedDays = 0;
		  }
	  }
	  
	  //GET ACTIVITIES
	  FlowFilter filter = new FlowFilter();
	  filter.setFolderid(""+selectedFolder);
	  filter.setLabelid(""+selectedLabel);
	  filter.setDeadline(""+selectedDays);
	  filter.setOrderType("desc");
	  ListIterator<Activity> it = pm.getUserActivitiesOrderFilters(userInfo, -1, filter);

	  //PUT TO VM
	  hsSubstLocal.put("selectedLabel", selectedLabel);
	  hsSubstLocal.put("selectedDays", selectedDays);
	  hsSubstLocal.put("selectedFolder", selectedFolder);
	  
	//	now get activities
	//ListIterator<Activity> it = pm.getUserActivitiesOrderByPid(userInfo);
	Activity a;

	// move activities to new list
	List<Activity> alAct = new ArrayList<Activity>();
	while (it != null && it.hasNext()) {
		a = (Activity)it.next();
		if (a != null) {
			if (hmFlows.containsKey(String.valueOf(a.flowid))) {
				alAct.add(a);
			}
		}
	}
	it = null;

	if (alAct.size() > 0) {
      	hsSubstLocal.put("hasActivities", Boolean.TRUE);
	}
	else {
      	hsSubstLocal.put("hasActivities", Boolean.FALSE);
	}

	nAll_Tasks = alAct.size();
	title = messages.getString("main_content.tasks.title")+" ("+nAll_Tasks+")";
	
	if (alAct.size() > (nNEWEST_LIMIT)) {
      	hsSubstLocal.put("hasMoreActivities", Boolean.TRUE);
	}
	else {
      	hsSubstLocal.put("hasMoreActivities", Boolean.FALSE);
	}

	List<Map<String,String>> alNew = new ArrayList<Map<String,String>>();

	Timestamp tsNow = new Timestamp((new java.util.Date()).getTime());

	// newest
	for (int i=0,j=0; i < alAct.size() && j < nNEWEST_LIMIT; i++) {
		a = alAct.get((i));

		// build hashmap to be able to display things properly
		Map<String,String> hm = new HashMap<String,String>();

		IFlowData fd = hmFlows.get(String.valueOf(a.flowid));

		if (fd == null) continue;

		String sAppName = fd.getApplicationName();
		if (sAppName == null) sAppName = ""; // support for non-categorized flows
		String sFlow = fd.getName();
		String sFlowId = String.valueOf(fd.getId());
		String sPid = String.valueOf(a.pid);
		String sSubPid = String.valueOf(a.subpid);
		String sDesc = a.description;
		String sCreated = DateUtility.formatTimestamp(userInfo, a.created);
		String sDuration = Utils.getDuration(new Timestamp(a.created.getTime()), tsNow);
		String sUri = "";
		if (a.url != null && StringUtilities.isNotEmpty(a.url)) {
			if (a.url.indexOf("?") > -1) {
				sUri = a.url.substring(0, a.url.indexOf("?"));	
			} else {
				sUri = a.url;
			}
		} else {
			sUri = "error.jsp";
		}
	    String pnumber = a.pnumber;
	    String sRunMax = String.valueOf(Boolean.FALSE);
		FlowSetting setting = BeanFactory.getFlowSettingsBean().getFlowSetting(fd.getId(), Const.sRUN_MAXIMIZED);
		if (setting != null && !StringUtils.isEmpty(setting.getValue()) && setting.getValue().equals(Const.sRUN_MAXIMIZED_YES)) {
		  sRunMax = String.valueOf(Boolean.TRUE);
		}
        String annotationIcon = "";
        String imgParam = "";
        annotationIcon = a.getAnnotationIcon();
        if (annotationIcon == null || "".equals(annotationIcon)){
          annotationIcon = "";
        } else {
			StringBuffer sbAnnotationIcon = new StringBuffer();        	
			sbAnnotationIcon.append("<a href=\"javascript:parent.viewAnnotations('").append(sFlowId).append("','").append(sPid).append("','").append(sSubPid).append("','dashboard');\">");
			sbAnnotationIcon.append("<img width=\"16\" height=\"16\" class=\"toolTipImg\" src=\"AnnotationIconsServlet?icon_name='"+annotationIcon+"'&ts='"+System.currentTimeMillis()+"'\" border=\"0\">");
			sbAnnotationIcon.append("</a>");
			imgParam = sbAnnotationIcon.toString();
          //imgParam = "<img class=\"toolTipImg\" src=\"AnnotationIconsServlet?icon_name='"+annotationIcon+"'&ts='"+System.currentTimeMillis()+"'\" border=\"0\" \">";
        }
        
        FolderManager fm = BeanFactory.getFolderManagerBean();
        List<Folder> folders = fm.getUserFolders(userInfo);
        String colorBackgroundColor = fm.getFolderColor(a.getFolderid(), folders);
        String colorTitle = fm.getFolderName(a.getFolderid(), folders);

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
		hm.put("delegated", a.delegated ? "1" : "0");
		hm.put("delegated_alt", messages.getString("main_content.tasks.delegated.alt"));
		hm.put("read", a.isRead() ? "1" : "0");
		hm.put("runMax", sRunMax);
        hm.put("icon_name", annotationIcon);
        hm.put("task_annotation_icon", imgParam);
        hm.put("task_annotation_color_title", colorTitle);
        hm.put("task_annotation_color_backgroundColor", colorBackgroundColor);
		alNew.add(hm);
		j++;
	}


	// free unused objects
	hmFlows = null;
	alAct = null;
	a = null;

      hsSubstLocal.put("newact", alNew);
      hsSubstLocal.put("actsize", alNew.size());
      // check if contains appname
      {
        boolean contains = false;
        Iterator<Map<String,String>> iter = alNew.iterator();
        while(iter.hasNext()) {
          Map<String,String> hm = iter.next();
          if(StringUtils.isNotEmpty(hm.get("appname"))) {
            contains = true;
            break;
          }
        }
        hsSubstLocal.put("has_appname", new Boolean(contains));
      }
      
      
      // prepare notification data
      Collection<Notification> notifications = BeanFactory.getNotificationManagerBean().listNotifications(userInfo);
      Collection<Map<String,String>> notes = new ArrayList<Map<String,String>>();
      int n = 0;
      for(Notification notification : notifications) {
        if(n >= nNOTIFICATION_LIMIT) break;
        ++n;
        Map<String,String> note = new HashMap<String,String>();
        note.put("id", String.valueOf(notification.getId()));
        note.put("from", notification.getSender());
        note.put("date", DateUtility.formatTimestamp(userInfo, notification.getCreated()));
        note.put("message", StringEscapeUtils.escapeHtml(notification.getMessage()));
        
		String href = "";
		String [] dadosproc = notification.getLink().split(",");
		
		int procid = -1; 
		
		if(dadosproc.length > 1)
			procid = Integer.parseInt(dadosproc[1]);
		
		if(notification.getLink().equals("false") || procid<=0)
			href =  "false";
		else
			href =  "8, \'user_proc_detail.jsp\'," + notification.getLink()+",-2";
        
        note.put("link", href);
        notes.add(note);
      }
      
      //SET USER FOLDERS
      List<Folder> folders = BeanFactory.getFolderManagerBean().getUserFolders(userInfo);
      hsSubstLocal.put("folders", folders);
      hsSubstLocal.put("hasFolder", folders.size());
      hsSubstLocal.put("folder_label", messages.getString("actividades.folder.filterfolders"));
      hsSubstLocal.put("folder_default", messages.getString("actividades.folder.filter_all"));
      hsSubstLocal.put("removeact", messages.getString("actividades.folder.removeact"));
      
      //SET LABELS
      List<ProcessLabel> labels = BeanFactory.getProcessAnnotationManagerBean().getLabelList(userInfo);
      hsSubstLocal.put("labels", labels);
      hsSubstLocal.put("label_label", messages.getString("actividades.folder.filterlabel"));
      hsSubstLocal.put("label_default", messages.getString("actividades.folder.filter_all"));
      
      //SET DAYS
	  String[] days = new String[7];
      days[0] = messages.getString("actividades.folder.filter_all_days");
      days[1] = messages.getString("actividades.folder.filter_today");
	  days[2] = messages.getString("actividades.folder.filter_yesterday");
	  days[3] = messages.getString("actividades.folder.filter_this_week");
	  days[4] = messages.getString("actividades.folder.filter_next_week");
	  days[5] = messages.getString("actividades.folder.filter_this_month");
	  days[6] = messages.getString("actividades.folder.filter_next_month");
      hsSubstLocal.put("days", days);
      hsSubstLocal.put("count", 0);
      hsSubstLocal.put("days_label", messages.getString("actividades.folder.filterdays"));
      
      //SET CLEAN
      hsSubstLocal.put("button_cleanfilter", messages.getString("actividades.button.clean_filter"));
      
      //SET ACTION
      hsSubstLocal.put("row", 0);
      hsSubstLocal.put("iconTime", System.currentTimeMillis());
      hsSubstLocal.put("action_move", messages.getString("actividades.folder.move"));
      hsSubstLocal.put("action_close", messages.getString("actividades.folder.close"));
      
      hsSubstLocal.put("notifications", notes);
      hsSubstLocal.put("hasMoreNotifications", notifications.size()>nNOTIFICATION_LIMIT);
      hsSubstLocal.put("notificationsMsg", messages.getString("main_content.notifications.notificationsMsg"));
      
  	  hsSubstLocal.put("notificationtitle", messages.getString("inbox.notificationtitle"));
	  hsSubstLocal.put("notificationitem", messages.getString("inbox.notificationitem"));
      
      // MESSAGES --------------------------------------------------------------------------------------------------------
      hsSubstLocal.put("application", messages.getString("main_content.tasks.field.application"));
      hsSubstLocal.put("flow", messages.getString("main_content.tasks.field.flow"));
      hsSubstLocal.put("pnumber", messages.getString("main_content.tasks.field.pnumber"));
      hsSubstLocal.put("subject", messages.getString("main_content.tasks.field.subject"));
      hsSubstLocal.put("arrived", messages.getString("main_content.tasks.field.arrived"));
      hsSubstLocal.put("waiting", messages.getString("main_content.tasks.field.waiting"));
      hsSubstLocal.put("no_tasks", messages.getString("main_content.tasks.noTasks"));
      hsSubstLocal.put("no_tasks_filter", messages.getString("actividades.msg.noactivities"));
      
      hsSubstLocal.put("most_recent", messages.getString("main_content.tasks.title.mostRecent"));
      hsSubstLocal.put("oldest", messages.getString("main_content.tasks.title.oldest"));
      hsSubstLocal.put("tasksMostRecentMsg", messages.getString("main_content.tasks.mostRecentMsg"));

      hsSubstLocal.put("button_more", messages.getString("button.more"));
      
      hsSubstLocal.put("notes_empty", messages.getString("main_content.notes.emptyText"));
      hsSubstLocal.put("notes_empty_link_text", messages.getString("main_content.notes.emptyLinkText"));
      hsSubstLocal.put("notes_title", messages.getString("main_content.notes.title"));
      hsSubstLocal.put("notes_from", messages.getString("main_content.notes.field.from"));
      hsSubstLocal.put("notes_date", messages.getString("main_content.notes.field.date"));
      hsSubstLocal.put("notes_message", messages.getString("main_content.notes.field.message"));
      hsSubstLocal.put("notes_tooltip", messages.getString("main_content.notes.tooltip"));
      hsSubstLocal.put("tooltip_inbox", messages.getString("main.tooltip.inbox"));
 
      
      // DELEGATIONS --------------------------------------------------------------------------------------------------------
		  hsSubstLocal.put("delegs_title", messages.getString("main_content.delegs.mainTitle"));

      DelegationInfo delegInfo = BeanFactory.getDelegationInfoBean();

      Collection<Map<String,String>> receivedDelegs = new ArrayList<Map<String,String>>();
      Iterator<DelegationInfoData> iter = delegInfo.getDeployedReceivedDelegations(userInfo).iterator();
      while (iter.hasNext()) {
    		DelegationInfoData did = iter.next();
    		Map<String,String> dd = new HashMap<String,String>();
    		dd.put("OwnerID",did.getOwnerID());
    		dd.put("FlowName",did.getFlowName());
    		dd.put("Expires",DateUtility.formatTimestamp(userInfo, did.getExpires()));
    		dd.put("HierarchyID",String.valueOf(did.getHierarchyID()));
    		dd.put("AcceptKey",did.getAcceptKey());
    		dd.put("RejectKey",did.getRejectKey());
    		receivedDelegs.add(dd);
      }
      hsSubstLocal.put("hasDelegs", new Boolean(receivedDelegs.size() > 0));
      hsSubstLocal.put("receivedDelegs", receivedDelegs);

      hsSubstLocal.put("delegs_received_requests", messages.getString("main_content.delegs.receivedRequests"));
      hsSubstLocal.put("delegs_title_responsible", messages.getString("main_content.delegs.title.responsible"));
      hsSubstLocal.put("delegs_title_flow", messages.getString("main_content.delegs.title.flow"));
      hsSubstLocal.put("delegs_title_endDate", messages.getString("main_content.delegs.title.endDate"));
      hsSubstLocal.put("delegs_title_accept", messages.getString("button.accept"));
      hsSubstLocal.put("delegs_title_refuse", messages.getString("button.refuse"));
      hsSubstLocal.put("no_delegs", messages.getString("main_content.delegs.noDelegationsText"));
      hsSubstLocal.put("no_delegs_link_text", messages.getString("main_content.delegs.noDelegationsLinkText"));
      hsSubstLocal.put("tooltip_delegs", messages.getString("main.tooltip.delegations"));
     		  
      hsSubstLocal.put("delegationsMsg", messages.getString("main_content.delegs.delegationsMsg"));
      
      pageContent = "task_list";
    }

    hsSubstLocal.put("data", data);

  }
  catch (Exception e) {
    Logger.errorJsp(login, sPage, "exception: " + e.getMessage());
    e.printStackTrace();
    sbError
        .append("<b>N&atilde;o foi poss&iacute;vel obter o conte&uacute;do.</b>");
    hsSubstLocal.put("error", sbError.toString());
  }

  hsSubstLocal.put("title", title);
  hsSubstLocal.put("ts", java.lang.Long.toString(ts));
  hsSubstLocal.put("url_prefix", sURL_PREFIX.substring(0, sURL_PREFIX.length() - 1));
  hsSubstLocal.put("css", css);
  hsSubstLocal.put("tabnr", StringUtils.isEmpty(tabnr) ? "" : tabnr);
  out.println(PresentationManager.buildPage(response, userInfo, hsSubstLocal, pageContent));
%>

