<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%@ page import = "pt.iflow.api.presentation.FlowMenu" %>
<%@page import="pt.iflow.api.filters.FlowFilter"%>
<%@page import="pt.iflow.api.filters.FlowIgnorable"%>
<%@page import="pt.iflow.api.folder.Folder"%>
<%@ page import="pt.iflow.processannotation.ProcessAnnotationManagerBean"%>
<%@page import="pt.iflow.api.processannotation.*"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%
  String title = messages.getString("actividades.title");

  FolderManager fm = BeanFactory.getFolderManagerBean();
  int newPid = -1;
  String clean = fdFormData.getParameter("clean");
  if(StringUtils.isEmpty(clean)) clean = "false";
  
  String cleanFilter = fdFormData.getParameter("cleanFilter");

  //ASSIGN ACTIVITIES TO FOLDER
  String setfolder = fdFormData.getParameter("setfolder");
  if(setfolder!=null){
	  String actividades = fdFormData.getParameter("activities");
	  fm.setActivityToFolder(userInfo,setfolder,actividades);
  }
  
  
  //REMOVE ACTIVITIES FROM FOLDER
  String removeactivities = fdFormData.getParameter("removeactivities");
  if(removeactivities!=null){
	  fm.setActivityToFolder(userInfo,null,removeactivities);
  }
  
  
  //FILTERS
  	  if ("1".equals(cleanFilter)) {
	    session.removeAttribute("filterlabel");
	    session.removeAttribute("filterdays");
	    session.removeAttribute("filterfolder");
	  }
  //FILTER BY FOLDER
  int selectedFolder = 0;
  String filterfolder = fdFormData.getParameter("filterfolder");
  if (StringUtils.isEmpty(filterfolder)){
    filterfolder = (String) session.getAttribute("filterfolder");
  }else{
	session.setAttribute("filterfolder",filterfolder);
  }
	try{
		selectedFolder = Integer.parseInt(filterfolder);
	 }catch(Exception e){
	   selectedFolder = 0;
	 }

  //FILTER BY LABEL
  ProcessAnnotationManager pam = BeanFactory.getProcessAnnotationManagerBean();
  List<ProcessLabel> labels = pam.getLabelList(userInfo);
  int selectedLabel = 0;
  String filterlabel = fdFormData.getParameter("filterlabel");
  if (filterlabel == null){
    filterlabel = (String) session.getAttribute("filterlabel");
  }else{
    session.setAttribute("filterlabel",filterlabel); // Utilizador actualizou valor
  }
  try{
	selectedLabel = Integer.parseInt(filterlabel);
  }catch(Exception e){
    selectedLabel = 0;
  }
  
  //FILTER BY DAYS
  int selectedDays = 0;
  String filterdays = fdFormData.getParameter("filterdays");
  if (filterdays == null){
    filterdays = (String) session.getAttribute("filterdays");
  }else{
    session.setAttribute("filterdays",filterdays); // Utilizador actualizou valor
  }
  try{
    selectedDays = Integer.parseInt(filterdays);
  }catch(Exception e){
    selectedDays = 0;
  }   
  
  //DELETE FOLDER
  String deletefolder = fdFormData.getParameter("deletefolder");
  if(deletefolder!=null){
	  fm.deleteFolder(userInfo, deletefolder);
  }
  
  //EDIT FOLDER
  String editfolder = fdFormData.getParameter("editfolder");
  if(editfolder!=null){
	  String editname = fdFormData.getParameter("editname");
	  String color = "#"+fdFormData.getParameter("color");

	  if(editfolder.equals("0"))
		  fm.createFolder(userInfo,editname,color);
	  else
	  	  fm.editFolder(userInfo,editfolder,editname,color);
  }
  
  //GET USER FOLDERS
  List<Folder> folders = fm.getUserFolders(userInfo);
  
  String[] saCols = { messages.getString("actividades.field.application"), messages.getString("actividades.field.flow"),
      messages.getString("actividades.field.processid"), messages.getString("actividades.field.subject"),
      messages.getString("actividades.field.since") };
  ArrayList<List<String>> alData = new ArrayList<List<String>>();

    
  String scroll = null;
  if ("false".equals(clean))
  	scroll = (String) session.getAttribute("filtro_scroll");
  if(scroll == null) scroll = "0";
  
  int pid = -1;
  int subpid = -1;
  String spid = null;
  String ssubpid = null; 
  if ("false".equals(clean)) {
	  spid = (String) session.getAttribute("filtro_pid");
	  ssubpid = (String) session.getAttribute("filtro_subpid");
  }

  if(spid != null && ssubpid != null) {
	  try{
	  pid = Integer.parseInt(spid);
	  subpid = Integer.parseInt(ssubpid);
	  }catch(Exception e){ }
  }

  
  String stmp = null;
  String stmp2 = null;
  String stmp3 = null;
  String stmp4 = null;

  int ITEMS_PAGE = 500;
  int nStartIndex = 0;
  int nNextStartIndex = 0;
  int flagMode = 0;
  
  String sActivity = fdFormData.getParameter("activity");
  String sShowFlowHtml = "";
  int nShowFlowId = -1;
  
 
  String sShowFlowId = fdFormData.getParameter("showflowid");
  if ("false".equals(clean) && sShowFlowId == null) {
    sShowFlowId = (String) session.getAttribute("filtro_showflowid");
  }else{
  session.setAttribute("filtro_showflowid",sShowFlowId);
  }

  int nMode = 0;
  boolean bEnteringPage = true;
  String sProcessBatch = fdFormData.getParameter("processbatch");

	String pnumber = fdFormData.getParameter("pnumber");
	if ("false".equals(clean) && pnumber == null) {
		pnumber = (String) session.getAttribute("filtro_pnumber");
	}else{
	session.setAttribute("filtro_pnumber",pnumber);
	}
	String orderBy = fdFormData.getParameter("orderby");
	if(orderBy == null)	orderBy = (String) session.getAttribute("filtro_order");
	if(orderBy == null)	orderBy = "desc";

	session.setAttribute("filtro_order", orderBy);

	int nItems = ITEMS_PAGE;

  
  if (sShowFlowId != null && !sShowFlowId.equals(""))
    bEnteringPage = false;

  if (userflowid > 0) {
    sShowFlowId = String.valueOf(userflowid);
  }
  if (sShowFlowId == null || sShowFlowId.equals(""))
    sShowFlowId = "-1";

  try {
    nShowFlowId = Integer.parseInt(sShowFlowId);
  } catch (Exception e) {
  }
  try {
    stmp = fdFormData.getParameter("numitemspage");
    if ("false".equals(clean) && stmp == null) {
	    stmp = (String) session.getAttribute("filtro_nItems");
    }

    nItems = Integer.parseInt(stmp); 
    session.setAttribute("filtro_nItems",stmp);   
    
    bEnteringPage = false;
  } catch (Exception e) {
  }
    
  try {
    stmp = fdFormData.getParameter("mode");
    nMode = Integer.parseInt(stmp);
    bEnteringPage = false;
  } catch (Exception e) {
    nMode = 0;
  }
  try {
    stmp = fdFormData.getParameter("startindex");
    if( stmp == null){
		if ("false".equals(clean)) 
    		nStartIndex = (Integer) session.getAttribute("filtro_startindex");
    	flagMode = -1;
    } else {
	    nStartIndex = Integer.parseInt(stmp);
    }
    bEnteringPage = false;
  } catch (Exception e) {
  }

  try {
    stmp = fdFormData.getParameter("nextstartindex");
    if (stmp == null) {
	    if ("false".equals(clean))
    		nNextStartIndex = (Integer) session.getAttribute("filtro_nextstartindex");
    	flagMode = -1;
    } else {
	    nNextStartIndex = Integer.parseInt(stmp);
    }
    bEnteringPage = false;
  } catch (Exception e) {
  }
  
  session.setAttribute("filtro_startindex",nStartIndex);
  session.setAttribute("filtro_nextstartindex",nNextStartIndex);

  if(flagMode != -1) { //caso tenha vindo da sessão não é para actualizar
		  if (nMode == 0) {
		    nStartIndex = 0;
		    nNextStartIndex = 0;
		  } else if (nMode > 0 ) {
		    nStartIndex = nNextStartIndex;
		  } else if (nMode < 0) {
		    nStartIndex = nStartIndex - nItems;
		    nNextStartIndex = nStartIndex;
		  }
  }
  String sDtBefore = fdFormData.getParameter("dtbefore");
  String sDtAfter = fdFormData.getParameter("dtafter");

  Date dtBefore = null;
  Date dtAfter = null;

  try {

	if(sDtBefore == null) {
	    if ("false".equals(clean))
			dtBefore = (Date) session.getAttribute("filtro_dtBefore");
    } else {
    	dtBefore = DateUtility.parseFormDate(userInfo, sDtBefore);
    }

  } catch (Exception e) {
  }

  try {
	if(sDtAfter == null) {
	    if ("false".equals(clean))
			dtAfter = (Date) session.getAttribute("filtro_dtAfter");
	} else {
    	dtAfter = DateUtility.parseFormDate(userInfo, sDtAfter);
    }
  } catch (Exception e) {
  }

	session.setAttribute("filtro_dtBefore",dtBefore);
	session.setAttribute("filtro_dtAfter",dtAfter);
	
	
  HashMap<String, String[]> hmFlowInfo = new HashMap<String, String[]>();
  List<Activity> alActivities = new ArrayList<Activity>();

  boolean bAdminUser = false;
  Set<Integer> hsPrivs = new HashSet<Integer>();
  Flow flow = BeanFactory.getFlowBean();
  IFlowData[] fda = null;
  try {

    if (userInfo.isOrgAdmin()) {
      // iflow administrator has full access
      bAdminUser = true;
    } else {

      FlowRolesTO[] fra = flow.getAllUserFlowRoles(userInfo);
      for (int i = 0; i < fra.length; i++) {

        if (fra[i].hasPrivilege(FlowRolesTO.WRITE_PRIV)) {
          hsPrivs.add(fra[i].getFlowid());
        }
      }
    }

    
    FlowType[] flowTypeExcluded = {FlowType.SUPPORT,FlowType.SEARCH,FlowType.REPORTS};
    //fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo,FlowType.WORKFLOW);
    fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo,null,flowTypeExcluded);

    // get online flows with app information
    FlowMenu menu = BeanFactory.getFlowApplicationsBean().getAllApplicationOnlineFlows(userInfo,null,flowTypeExcluded);

    // now build map with key flowid and value flowdata
    Iterator<FlowAppMenu> itera = menu.getAppMenuList().iterator();
    while (itera != null && itera.hasNext()) {
      FlowAppMenu appMenu = itera.next();
      FlowMenuItems menuItems = appMenu.getMenuItems();

      List<IFlowData> al = menuItems.getFlows();

      for (int i = 0; al != null && i < al.size(); i++) {
        IFlowData fd = al.get(i);
        String sAppName = fd.getApplicationName();
        if (sAppName == null)
          sAppName = ""; // support for non-categorized flows
        String sFlow = fd.getName();
        String[] names = new String[] { sAppName, sFlow };
        hmFlowInfo.put(String.valueOf(fd.getId()), names);
      }
    }
  } catch (Exception e) {
    Logger.errorJsp(login, "flows", " checking admin profile exception: " + e.getMessage());
    e.printStackTrace();
  }

  String sACT_CONF = "ACTIVITY_CONFIG";
  String sCONF_FLOW = "CONFIG_FLOWID";
  String sCONF_DT_BEFORE = "CONFIG_DT_BEFORE";
  String sCONF_DT_AFTER = "CONFIG_DT_AFTER";
  String sCONF_ITEMS_PAGE = "CONFIG_ITEMS_PAGE";

  String sACT_IND = "ACTIVITY_INDEX";
  String sACT_START_IND = "ACTIVITY_START_INDEX";
  String sACT_NEXT_START_IND = "ACTIVITY_NEXT_START_INDEX";

  String sPreConfigVar = "pre_config";
  String sConfigVar = "config";
  String sPreIndexVar = "pre_index";
  String sIndexVar = "index";

  String sPreBatchVar = "pre_batch";
  String sBatchVar = "batch";

  String sVarSet = "set";
  String sVarUnset = "unset";

  // TODO usar um objecto (bean) para estas tarefas...
  Map<String, Long> hmConfig = (Map<String, Long>) session.getAttribute(sACT_CONF);
  Map<String, Integer> hmIndex = (Map<String, Integer>) session.getAttribute(sACT_IND);
  Map<String, Object> hmBatch = (Map<String, Object>) session.getAttribute(sACT_BATCH);

  boolean bSessionConfig = false;
  boolean bSessionIndex = false;
  boolean bBatchProcessing = false;

  String sConfig = fdFormData.getParameter(sConfigVar);
  String sPreConfig = fdFormData.getParameter(sPreConfigVar);

  String sIndex = fdFormData.getParameter(sIndexVar);
  String sPreIndex = fdFormData.getParameter(sPreIndexVar);

  String sBatch = fdFormData.getParameter(sBatchVar);
  String sPreBatch = fdFormData.getParameter(sPreBatchVar);

  // batch
  List<String> alBatchPids = null;
  Map<String, String> hmBatchLinks = null;

  boolean showApplicationName = false;

  // CONFIG
  if (sConfig != null && sConfig.equals(sVarSet) && (sPreConfig == null || !sPreConfig.equals(sConfig))) {
    hmConfig = new HashMap<String, Long>();
  } else if (sConfig == null && sPreConfig != null && sPreConfig.equals(sVarSet)) {
    session.removeAttribute(sACT_CONF);
    bSessionConfig = false;
    hmConfig = null;
  }

  if (hmConfig != null) {
    bSessionConfig = true;

    if (bEnteringPage) {
      nShowFlowId = hmConfig.get(sCONF_FLOW).intValue();
      Long l = null;
      l = hmConfig.get(sCONF_DT_BEFORE);
      dtBefore = l == null ? null : new Date(l);
      l = hmConfig.get(sCONF_DT_AFTER);
      dtAfter = l == null ? null : new Date(l);
      nItems = hmConfig.get(sCONF_ITEMS_PAGE).intValue();
    } else {
      hmConfig.put(sCONF_FLOW, new Long(nShowFlowId));
      hmConfig.put(sCONF_DT_BEFORE, dtBefore == null ? null : dtBefore.getTime());
      hmConfig.put(sCONF_DT_AFTER, dtAfter == null ? null : dtAfter.getTime());
      hmConfig.put(sCONF_ITEMS_PAGE, new Long(nItems));
      session.setAttribute(sACT_CONF, hmConfig);
    }
  }
  String sPreConfigHidden = sConfig;
  if (sPreConfig == null && hmConfig != null) {
    sPreConfigHidden = sVarSet;
  }

  // INDEX
  if (sIndex != null && sIndex.equals(sVarSet) && (sPreIndex == null || !sPreIndex.equals(sIndex))) {
    hmIndex = new HashMap<String, Integer>();
  } else if (sIndex == null && sPreIndex != null && sPreIndex.equals(sVarSet)) {
    session.removeAttribute(sACT_IND);
    bSessionIndex = false;
    hmIndex = null;
  }

  if (hmIndex != null) {
    bSessionIndex = true;
    if (bEnteringPage) {
      nStartIndex = hmIndex.get(sACT_START_IND);
      nNextStartIndex = hmIndex.get(sACT_NEXT_START_IND);
      nMode = 0;
    } else {
      hmIndex.put(sACT_START_IND, nStartIndex);
      hmIndex.put(sACT_NEXT_START_IND, nNextStartIndex);
      session.setAttribute(sACT_IND, hmIndex);
    }
  }

  if (userflowid == -1) {
    List<String> alValues = new ArrayList<String>();
    List<String> alNames = new ArrayList<String>();
    alValues.add("-1");
    alNames.add(Const.SELECT_TXT);
    for (int i = 0; fda != null && i < fda.length; i++) {
      if (!bAdminUser && !hsPrivs.contains(new Integer(fda[i].getId()))) {
        continue;
      }
      alValues.add(String.valueOf(fda[i].getId()));
      alNames.add(fda[i].getName());
    }
    sShowFlowHtml = Utils.genHtmlSelect("showflowid", "class=\"txt\"", sShowFlowId, alValues, alNames);
  }

  final boolean isAdmin = bAdminUser;
  final Set<Integer> privs = hsPrivs;
  final HashMap<String, String[]> flowInfo = hmFlowInfo;
  FlowFilter filter = new FlowFilter(pnumber, dtAfter, dtBefore, nNextStartIndex, nItems);
  filter.setIgnoreFlow(new FlowIgnorable() {
    public boolean ignoreFlow(int flowid) {
      boolean retObj = false;
      if (!isAdmin && !privs.contains(new Integer(flowid))) {
        retObj = true;
  	} else if (!flowInfo.containsKey(String.valueOf(flowid))) {
        retObj = true;
  	}
      return retObj;
    }
  });
  
  //Controlar ordem ASC ou DESC
  filter.setOrderType(orderBy);
  filter.setFolderid(""+selectedFolder);
  filter.setLabelid(""+selectedLabel);
  filter.setDeadline(""+selectedDays);
  
  ListIterator<Activity> it = pm.getUserActivitiesOrderFilters(userInfo, nShowFlowId, filter);
  Activity a;
  Map<String, String> hmLinks = new HashMap<String, String>(); // key=flowid_pid_subpid;value=activity url 

  while (it != null && it.hasNext()) {
    a = it.next();
    if (sShowFlowId != null && !sShowFlowId.equals("") && !sShowFlowId.equals("-1")) {
      if (!sShowFlowId.equals(String.valueOf(a.flowid))) {
        continue;
      }
    }
    alActivities.add(a);
    hmLinks.put(a.flowid + "_" + a.pid + "_" + a.subpid, response.encodeURL(a.url.substring(0, a.url.indexOf("?"))));
  }

  // BATCH
  if (sBatch != null && sBatch.equals(sVarSet) && (sPreBatch == null || !sPreBatch.equals(sBatch))) {
    hmBatch = new HashMap<String, Object>();
  } else if (sBatch == null && sPreBatch != null && sPreBatch.equals(sVarSet)) {
    session.removeAttribute(sACT_BATCH);
    bBatchProcessing = false;
    hmBatch = null;
  }

  if (hmBatch != null) {
    bBatchProcessing = true;

    alBatchPids = (List<String>) hmBatch.get(sACT_BATCH_PIDS);
    hmBatchLinks = (Map<String, String>) hmBatch.get(sACT_BATCH_LINKS);

    if (!bEnteringPage) {
      // process form
      for (int i = 0; i < nItems; i++) {

        stmp = fdFormData.getParameter(sBatchVar + "_" + i);

        if (stmp == null || stmp.equals(""))
          continue;

        // stmp = <flowid>_<pid>_<subpid>
        if (stmp.indexOf("_") == -1)
          continue;

        // get activity url
        stmp2 = (String) hmLinks.get(stmp);
        if (stmp2 == null || stmp2.equals(""))
          continue;

        if (alBatchPids == null)
          alBatchPids = new ArrayList<String>();
        if (hmBatchLinks == null)
          hmBatchLinks = new HashMap<String, String>();

        if (!hmBatchLinks.containsKey(stmp)) {
          alBatchPids.add(stmp);
        }
        hmBatchLinks.put(stmp, response.encodeURL(stmp2)); // update url
      }

      hmBatch.put(sACT_BATCH_PIDS, alBatchPids);
      hmBatch.put(sACT_BATCH_LINKS, hmBatchLinks);

      session.setAttribute(sACT_BATCH, hmBatch);
    }
  }

  String sPreBatchHidden = sBatch;
  if (sPreBatch == null && hmBatch != null) {
    sPreBatchHidden = sVarSet;
  }

  if (sActivity != null && !sActivity.equals("") && sActivity.indexOf("_") > -1) {
    stmp = (String) hmLinks.get(sActivity);
    if (stmp != null && !stmp.equals("")) {
      ServletUtils.sendEncodeRedirect(response, sURL_PREFIX + stmp);
      return;
    }
  }

  if (sProcessBatch != null && sProcessBatch.equals("1")) {
    ServletUtils.sendEncodeRedirect(response, sURL_PREFIX + "processBatchProc.jsp?ts=" + ts);
    return;
  }

  boolean bFirstPage = true;
  boolean bHasMoreItems = false;
  if (nStartIndex > 0) {
    bFirstPage = false;
  } else if (nStartIndex < 0) { 
    nStartIndex = 0;
  }
  if (nMode < 0) {
    bHasMoreItems = true;
  }
  
  for (int i = 0; i < alActivities.size(); i++) {
    a = alActivities.get(i);
 
	    String readStyle = "";
	    if (!a.isRead()) {
	      readStyle = "style='font-weight:bold;'";
	    }
    stmp = "<a " + readStyle + "href=\"javascript:hidePopup(); open_process(2, " + a.flowid + ", '"
        + response.encodeURL(a.url.substring(0, a.url.indexOf("?"))) + "', 'flowid=" + a.flowid + "&pid=" + a.pid
        + "&subpid=" + a.subpid + "')\">";
	    stmp2 = "</a>";
	    stmp3 = String.valueOf(a.flowid);
	
	    if (!hmFlowInfo.containsKey(stmp3)) {
	      // flow does exist or is disabled
	      i--;
	      continue;
	    }
	
	    String[] names = hmFlowInfo.get(stmp3);
	    String appName = names[0];
	    if (StringUtils.isNotEmpty(appName))
	      showApplicationName = true;
	    String flowName = names[1];
	
	    if (i >= nItems) {
	      bHasMoreItems = true;
	      break;
	    }
	
	    List<String> actividade = new ArrayList<String>();
	    actividade.add(a.flowid + "_" + a.pid + "_" + a.subpid);
	    actividade.add(stmp + appName + stmp2);
	    if(BeanFactory.getFlowHolderBean().getFlow(userInfo, a.getFlowid()).getFlowType().compareTo(FlowType.DOCUMENT)==0)
	    	stmp4="<img class=\"toolTipImg\" src=\"images/flow_type_D.png\" border=\"0\"/>";
	    else
	    	stmp4="";
	    if (a.delegated) {
	      actividade.add("<a title=\"" + messages.getString("actividades.msg.taskdeleg")
	          + "\"><img src=\"images/icon_delegations.png\" height=\"10\"/></a>" + stmp + flowName + stmp2 + stmp4);
	    } else {
	      actividade.add(stmp4 + stmp + flowName + stmp2);
	    }
	
	    // replaced by process number
	    //altmp.add(stmp + a.pid + stmp2);
	    actividade.add(stmp + a.pnumber + stmp2);
	    actividade.add(String.valueOf(a.subpid));
	    actividade.add(stmp + a.description + stmp2);
	
	    // reprocess timestamp
	    java.sql.Timestamp tsDate = new java.sql.Timestamp(a.created.getTime());
	    String sCreated = DateUtility.formatTimestamp(userInfo, tsDate);
	    actividade.add(stmp + sCreated + stmp2);
	    
	    actividade.add(fm.getFolderColor(a.getFolderid(), folders));
	    actividade.add(fm.getFolderName(a.getFolderid(), folders));
        actividade.add(a.getAnnotationIcon());
	   	alData.add(actividade);
	   	
	   	
	   	if(pid == a.pid && subpid == a.subpid)
	   		newPid = pid;
	   	
	   	  
  }

  boolean bDisableNavigationPrev = bFirstPage;
  boolean bDisableNavigationNext = !bHasMoreItems;

  PersistSession ps = new PersistSession();
  ps.setSession(userInfo, session);
%>

<script language="JavaScript">
function filterBYfolder(){
	var folderid = document.getElementById('folderfilter').value;

		document.activities_form.mode.value='1';
		document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';
	tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>?filterfolder='+folderid);
}

function filterBYlabel(){
	var labelid = document.getElementById('labelfilter').value;

		document.activities_form.mode.value='1';
		document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';
	tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>?filterlabel='+labelid);
}

function filterBYdays(){
	var filterdays = document.getElementById('daysfilter').value;

		document.activities_form.mode.value='1';
		document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';
		tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>?filterdays='+filterdays);
}

function isAnyActivitySelected (){
    var returnElement = false;
    for (i = 0; i < <%=nItems %> ; i++) {
        var chk = document.getElementById('checkTask_'+i);
        if (chk != null && chk.checked == true) {
            returnElement = true;
        }
    }
    return returnElement;
}

function assignActivities(folderid){
	var sactivities = "";
	var tam = 0;
	for (i = 0; i < <%=nItems %> ; i++) {
		var chk = document.getElementById('checkTask_'+i);
        if (chk != null && chk.checked == true) {

            if(tam > 0)
            	sactivities = sactivities + ";" + document.getElementById('checkedflowid_'+i).value;
            else 
            	sactivities = document.getElementById('checkedflowid_'+i).value;
        	
            tam = tam + 1;
        }
    }

	if(tam > 0){
		document.activities_form.mode.value='1';
		document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';
		tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>?setfolder='+folderid+'&activities='+sactivities);
	}
}

function assignActivity(folderid, sactivity){
		document.activities_form.mode.value='1';
		document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';
		tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>?setfolder='+folderid+'&activities='+sactivity);
}

function removeActivityFolder(sactivity){
	document.activities_form.mode.value='1';
	document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';
	tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>?removeactivities='+sactivity);
}


function deleteFolder(folderid){
  document.activities_form.mode.value='1';
  document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';
  tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>?deletefolder='+folderid);
}

function editFolder(folderid, from){
	if (from == '1') {
		var obj = document.getElementById('name_'+folderid);
		if (obj!=null) obj.style.display='none';
		obj = document.getElementById('color_'+folderid);
		if (obj!=null) obj.style.display='none';
		obj = document.getElementById('bt_edit_'+folderid);
		if (obj!=null) obj.style.display='none';
		obj = document.getElementById('bt_delete_'+folderid);
		if (obj!=null) obj.style.display='none';
		obj = document.getElementById('bt_confirm_'+folderid);
		if (obj!=null) obj.style.display='inline-block';
		obj = document.getElementById('bt_cancel_'+folderid);
		if (obj!=null) obj.style.display='inline-block';
		obj = document.getElementById('bt_pickColor_'+folderid);
		if (obj!=null) obj.style.display='inline-block';
		obj = document.getElementById('edit_'+folderid);
		if (obj!=null) obj.style.display='inline-block';
		if (folderid=='0') {
			  if (obj!=null) obj.value='';
			  obj = document.getElementById('bt_pickColor_'+folderid);
			  if (obj!=null) obj.style.display='inline-block';
			}
		obj = document.getElementById('edit_'+folderid);
		if (obj!=null) obj.focus();	
	} else if (from == '0') {
		document.activities_form.mode.value='1';
		document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';
		var editname = escape(document.getElementById('edit_'+folderid).value);
		var cor = escape(document.getElementById('bt_pickColor_'+folderid).color);
		//alert(cor);
		if(editname != "" && cor != "ffffff") 
			tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>?editfolder='+folderid+'&editname='+editname+'&color='+cor);
	} else {
		var obj = document.getElementById('name_'+folderid);
		if (obj!=null) obj.style.display='inline-block';
		obj = document.getElementById('color_'+folderid);
		if (obj!=null) obj.style.display='';
		obj = document.getElementById('bt_edit_'+folderid);
		if (obj!=null) obj.style.display='inline-block';
		obj = document.getElementById('bt_delete_'+folderid);
		if (obj!=null) obj.style.display='inline-block';
		obj = document.getElementById('bt_confirm_'+folderid);
		if (obj!=null) obj.style.display='none';
		obj = document.getElementById('bt_cancel_'+folderid);
		if (obj!=null) obj.style.display='none';
		obj = document.getElementById('bt_pickColor_'+folderid);
		if (obj!=null) obj.style.display='inline-block';
		obj = document.getElementById('edit_'+folderid);
		if (obj!=null) obj.style.display='none';
		obj = document.getElementById('bt_pickColor_'+folderid);
		if (obj!=null) obj.style.display='none';
	}
}

function getHex(rgb){
	var parts = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
	delete (parts[0]);
	for (var i = 1; i <= 3; ++i) {
	    parts[i] = parseInt(parts[i]).toString(16);
	    if (parts[i].length == 1) parts[i] = '0' + parts[i];
	} 
	return hexString =parts.join('').toUpperCase();	
}



function selectAll(){
	var onoff = document.getElementById('checkTask_All');	
	for (i = 0; i < <%=nItems %> ; i++) {
		var chk = document.getElementById('checkTask_'+i);
        if (chk != null) {
            if(onoff.checked == true)
				chk.checked = true;
            else
                chk.checked = false;
        }
    }
}

jscolor.bind();
</script>


<h1 id="title_tasks"><%=title%></h1>

<form name="activities_form" method="post">

      <div class="table_inc">
      <p><%=messages.getString("actividades.introMsg")%><br/><br/>
      
      <% if( (!((selectedFolder==0) && alData.size() <= 0) || !((selectedLabel==0)&& alData.size() <= 0)) || !((selectedDays==0)&& alData.size() <= 0) ) { %>
      
			<%=messages.getString("actividades.folder.filterlabel")%>
		    <select name="labelfilter" id="labelfilter" onchange="filterBYlabel()" >
		    <option value="0"><%=messages.getString("actividades.folder.filter_all")%></option> 
		      <% for (int i = 0; i < labels.size(); i++) { %>
		         <option value="<%=labels.get(i).getId()%>" <%=selectedLabel == labels.get(i).getId() ? "selected" : "" %>> 
		         	<%=labels.get(i).getName()%>
		         </option> 
			  <% } %>
		     </select>
		     
		     <%=messages.getString("actividades.folder.filterdays")%>
		    <select name="daysfilter" id="daysfilter" onchange="filterBYdays()" >
		    <option value="0"><%=messages.getString("actividades.folder.filter_all_days")%></option> 
            <option value="1" <%=selectedDays == 1 ? "selected" : "" %>>  <%=messages.getString("actividades.folder.filter_today")%></option> 
            <option value="2" <%=selectedDays == 2 ? "selected" : "" %>>  <%=messages.getString("actividades.folder.filter_yesterday")%></option> 
            <option value="3" <%=selectedDays == 3 ? "selected" : "" %>>  <%=messages.getString("actividades.folder.filter_this_week")%></option>
            <option value="4" <%=selectedDays == 4 ? "selected" : "" %>>  <%=messages.getString("actividades.folder.filter_next_week")%></option> 
            <option value="5" <%=selectedDays == 5 ? "selected" : "" %>>  <%=messages.getString("actividades.folder.filter_this_month")%></option> 
            <option value="6" <%=selectedDays == 6 ? "selected" : "" %>>  <%=messages.getString("actividades.folder.filter_next_month")%></option>  
		    </select>
		     
		    <%  if(folders.size() > 0) { %>
		    <%=messages.getString("actividades.folder.filterfolders")%>
		    <select name="folderfilter" id="folderfilter" onchange="filterBYfolder()" >
		    <option value="0"><%=messages.getString("actividades.folder.filter_all")%></option> 
		      <% for (int i = 0; i < folders.size(); i++) { %>
		         <option value="<%=folders.get(i).getFolderid()%>" <%=selectedFolder == folders.get(i).getFolderid() ? "selected" : "" %>> 
		         	<%=folders.get(i).getName()%>
		         </option> 
			  <% } %>
		     </select>	 
		    <% } %>
		<% } %>
<%  if (alData.size() > 0) { %>
      <%=(nShowFlowId > 0) ? messages.getString("actividades.introMsg.flowId", "<b>"
            + BeanFactory.getFlowHolderBean().getFlow(userInfo, nShowFlowId).getName() + "</b>") : ""%>
      <%=(StringUtils.isNotBlank(pnumber)) ? messages.getString("actividades.introMsg.pnumber", "<b>" + pnumber
            + "</b>") : ""%>
      <%=(dtAfter != null) ? messages.getString("actividades.introMsg.dateAfter", "<b>"
            + DateUtility.formatFormDate(userInfo, dtAfter) + "</b>") : ""%>
      <%=(dtBefore != null) ? messages.getString("actividades.introMsg.dateBefore", "<b>"
            + DateUtility.formatFormDate(userInfo, dtBefore) + "</b>") : ""%>
      
<%} if( (!((selectedFolder==0) && alData.size() <= 0) || !((selectedLabel==0)&& alData.size() <= 0)) || !((selectedDays==0)&& alData.size() <= 0) ) { %> 
	<a href="javascript:tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>', 'cleanFilter=1');"><if:message string="actividades.button.clean_filter" /></a>
</p>
<% }if (alData.size() <= 0) { %>
	<div class="info_msg">
		<%=messages.getString("actividades.msg.noactivities")%>
	</div>
<%  } else { %>      
<div class="yui-skin-sam">
<div id ="view_proc_annotation" ></div>
</div>

        <table style="position:relative;right:10px;" width="100%" cellpadding="2">
        	<tr class="tab_header">
        <td bgcolor="white"></td>
        <td style="text-align:center;"><input type="checkbox" onclick="javascript:selectAll()" id="checkTask_All" name="checkTask_All" edit="true"/></td>
        <td></td>
        <td></td>
<%
          int cols_length = saCols.length;
            if (bBatchProcessing) {
              cols_length++;
            }
            if (!showApplicationName)
              cols_length--;

            for (int i = 0; i < saCols.length; i++) {
              if (!showApplicationName && i == 0)
                continue;
        %>
          <td><%=saCols[i]%></td>
          
<%
  }%>
  <td></td>
  <%
    if (bBatchProcessing) {
%>
          <td>Batch processing</td>
<%
  }
%>
        </tr>
<%
  if (alData.size() > 0) {
	  int flagDone = 0;
  
      for (int row = 0; row < alData.size(); row++, nNextStartIndex++) {
      //for (int i = 0; i < alData.size(); i++) {
 	     	  
        List<String> altmp = (List<String>) alData.get(row);
        out.println("        <tr class=\"" + ((row % 2 == 0) ? "tab_row_even" : "tab_row_odd") + "\">");

    	  String [] vars = altmp.get(0).split("_");
    	  
		  int npid = 0;
	      int nsubpid = 0;
	      int nflowid = 0;
    	  try{
            nflowid = Integer.parseInt(vars[0]);
    		npid = Integer.parseInt(vars[1]);  
    	    nsubpid = Integer.parseInt(vars[2]);
    		  }catch(Exception e){ }
    	  
            if(newPid != -1 && npid == newPid && nsubpid == subpid){
            	out.println("<td style=\"text-align:right\" bgcolor=\"white\" class=\"itemlist_icon\"><a><img style=\"position:relative;left:5px\" class=\"toolTipImg\" src=\"images/icon_task_enable.png\" border=\"0\"></a> </td>");
            }else if(newPid == -1 && ( (npid > pid || nsubpid > subpid )&& subpid != -1 && orderBy.equals("asc") && flagDone == 0) ){
            	flagDone = 1;
            	out.println("<td style=\"text-align:right\" bgcolor=\"white\" class=\"itemlist_icon\"><a><img style=\"position:relative;left:5px\" class=\"toolTipImg\" src=\"images/icon_task_disable.png\" border=\"0\"></a> </td>");
            }else if(newPid == -1 && ( (npid < pid || nsubpid < subpid ) && subpid != -1 && orderBy.equals("desc") && flagDone == 0) ){
            	flagDone = 1;
            	out.println("<td style=\"text-align:right\" bgcolor=\"white\" class=\"itemlist_icon\"><a><img style=\"position:relative;left:5px\" class=\"toolTipImg\" src=\"images/icon_task_disable.png\" border=\"0\"></a> </td>");
            }else
            	out.println("<td bgcolor=\"white\"> </td>");	
        
		String checks = "<td style=\"text-align:center;\"> <input type=\"checkbox\" id=\"checkTask_"+row+"\" name=\"checkTask_"+row+"\" labelkey=\"\" value='' edit=\"true\" required=\"false\" />" +
						"<input type=\"hidden\" name=\"checkedflowid_" + row + "\" id=\"checkedflowid_" + row + "\" value=\"" + altmp.get(0) + "\" /></td>";
        
		out.println(checks);

        String caixacor = "<td></td>";
        if(altmp.get(altmp.size()-3) != ""){
          caixacor = "<td title=\""+altmp.get(altmp.size()-2)+"\" style=\"background-color:"+altmp.get(altmp.size()-3)+";\" ></td>";     
        }
        out.println(caixacor);

        String accao = "<td></td>";
        if(folders.size() > 0){
          
          String imgParam = "AnnotationIconsServlet?icon_name=action.png&ts='"+System.currentTimeMillis()+"'";
          imgParam = "<img class=\"toolTipImg\" src=\""+imgParam+"\" border=\"0\">";  
          
            accao = "<td onclick=\"javascript:menuonoff('atribui"+row+"')\" title=\""+messages.getString("actividades.folder.move")+"\">";
            accao += imgParam;
	   		accao += "<div id=\"atribui"+row+"\" style=\"display:none;position:absolute;z-index:1;background:none repeat scroll 0 0 #FFFFFF;border-color:#888888;";
			accao += "border-style:solid;border-width:1px 1px 2px;text-align:left;padding:2px 5px\">";
			accao += "<table>";
	    	for (int i = 0; i < folders.size(); i++) { 
	    	  accao += "<tr>";	
	    	  accao += "<td style=\"background:"+folders.get(i).getColor()+";width:4px;\">";
	    	  accao += "</td>";
	    	  accao += "<td>";
	    	  accao += "<input type=\"button\" class=\"apt_regular_button\" style=\"width:15em;text-align:left;margin:0px\" value=\""+folders.get(i).getName()+"\" id=\""+folders.get(i).getFolderid()+"\" name=\""+folders.get(i).getName()+"\" onclick=\"assignActivity('"+folders.get(i).getFolderid()+"','"+altmp.get(0)+"')\">";
	    	  accao += "</td>";
	    	  accao += "</tr>	";
	  		}
	    	  accao += "<tr>";	
	    	  accao += "<td style=\"background:#CCCCCC;width:4px;\">";
	    	  accao += "</td>";
	    	  accao += "<td>";
	    	  accao += "<input type=\"button\" class=\"apt_regular_button\" style=\"width:15em;text-align:left;margin:0px\" value=\""+messages.getString("actividades.folder.removeact")+"\" id=\"-1\" name=\""+messages.getString("actividades.folder.removeact")+"\" onclick=\"removeActivityFolder('"+altmp.get(0)+"')\">";
	    	  accao += "</td>";
	    	  accao += "</tr>	";
	    	
	    	accao += " </tr>";
	    	accao += "</table>";
	    	accao += "<input class=\"apt_regular_button\" type=\"button\" value=\""+messages.getString("actividades.folder.close")+"\" id=\"bt_close2\" name=\""+messages.getString("actividades.folder.close")+"\" style=\"width:6em;text-align:center\" onclick=\"\">";
	    	accao += "</div></td>";       
        }
        out.println(accao);
        
        String field = "";
        for (int j = 1; j < altmp.size()-3; j++) {  
        	       
          if (!showApplicationName && j == 1)
            continue;
          if (j != 4) { // é o subpid
            out.print("          <td>"); 
            field = (String) altmp.get(j);
            if (j == 3) { // este é o pid
              if (!Const.DEFAULT_SUBPID.equals((String) altmp.get(4))) {                 
                field += "/" + (String) altmp.get(4);
              }
            }
            out.print(field);
            out.println("</td>");
          }
        }
        
        StringBuffer annotationIcon = new StringBuffer("<td class=\"itemlist_icon\">");
        String iconName = altmp.get(9);
        if (iconName != null && !"".equals(iconName)) {
          annotationIcon.append("<a href=\"javascript:parent.viewAnnotations('").append(nflowid).append("','").append(npid).append("','").append(nsubpid).append("');\">");
          String imgParam = "";
          imgParam = "AnnotationIconsServlet?icon_name="+iconName+"&ts='"+System.currentTimeMillis()+"'";
          annotationIcon.append("<img width=\"16\" height=\"16\" class=\"toolTipImg\" src=\"").append(imgParam).append("\" border=\"0\" title=\"").append(messages.getString("actividades.msg.dica.annotation")).append("\">");
          annotationIcon.append("</a>");
        }
        annotationIcon.append("</td>");
        out.println(annotationIcon.toString());
        
        if (bBatchProcessing) {
          stmp = (String) altmp.get(0);
          out.print("          <td>");
          out.print("<input type=\"checkbox\" name=\"" + sBatchVar + "_" + row + "\" value=\"" + stmp + "\" title=\""
              + messages.getString("actividades.msg.addtoprocess") + "\"");
          if (hmBatchLinks != null && hmBatchLinks.containsKey(stmp)) {
            out.print(" checked ");
          }
          out.print(">");
          out.println("</td>");
        }
     
        out.println("        </tr>");
      }
    }
%>
      </table>
    <%
      int nTab = 0;
      if (folders.size() > 0) {%>
      <p>Pastas:</p>
	  <table>
      <% for (int i = 0; i < folders.size(); i++) { 
      		if (nTab == 0) { %>
      		<tr> 
		  <%}%>
		  		<td style="text-align:left;width:8px;background-color:<%= folders.get(i).getColor() %>;height:8px;"></td>
	      	    <td style="width:150px;" ><span style="padding-left:5px;"><%=folders.get(i).getName()%></span></td>
		  <%nTab++;
      		if (nTab == 5) { 
      			nTab = 0;%>
      		</tr> 
		  <%}
		 } 
      	 if (nTab > 0) { 
	       for (int i = nTab; i < 5; i++) {%>
		  		<td style="text-align:left;width:8px;background-color:white;height:8px;"></td>
	      	    <td style="width:150px;" ><span style="padding-left:5px;">&nbsp;</span></td>
		 <%} %>
      	  </tr> 
	  <% } %>
	</table>
	<%}%>

       </div>
<%
  }
%>
<%
  if (alData.size() > 0 && bBatchProcessing) {
%>
    <div class="button_box">
    	<input class="regular_button_01" type="button" name="batch" value="Process Batch" 
    		onClick="javascript:document.activities_form.processbatch.value='1';tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>', get_params(document.activities_form));"/>
	</div>
<%
  }
%>
  
   <%
         if (alData.size() > 0) {
       %>
    <div class="button_box">
    <%
      if (!bDisableNavigationPrev) {
    %>
    	<input class="regular_button_01" type="button" name="previous" value="<%=messages.getString("button.previous")%>" 
    		onClick="javascript:document.activities_form.mode.value='-1';document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>', get_params(document.activities_form));"/>
    <%
      }
    %>

    <%
      if (!bDisableNavigationNext) { %>
    	<input class="regular_button_01" type="button" name="next" value="<%=messages.getString("button.next")%>" 
    		onClick="javascript:document.activities_form.mode.value='1';document.activities_form.nextstartindex.value='<%=nNextStartIndex%>';tabber_right(2, '<%=response.encodeURL("actividades.jsp")%>', get_params(document.activities_form));"/>    
    <% } %>


        <input type="button" onclick="javascript:menuonoff('editar','1'); return false;" value="<%=messages.getString("actividades.folder.manager")%>" id="folderedit" name="folderedit" class="regular_button_03" style="float:left;">  
		<div id="editar" style="display:none;position:absolute;z-index:1;bottom:50px; 
								background:none repeat scroll 0 0 #FFFFFF;
								border-color:#888888;border-style:solid;border-width:1px 1px 2px;
								text-align:left;padding: 2px 5px;width:50em;left:30px;">	

	      <% for (int i = 0; i < folders.size(); i++) { %>
			<div style="margin:1px;border:1px solid #eee">
			<table style="padding:0px;">
			<tr>
			<td width="4px" id="color_<%=folders.get(i).getFolderid()%>" style="background:<%=folders.get(i).getColor()%>">
			</td>
			<td style="width:15em;text-align:left;font-family:sans-serif;font-size:13px;color:#333;">
				<input id="bt_pickColor_<%=folders.get(i).getFolderid()%>" class="color {valueElement:'myValue_<%=folders.get(i).getFolderid()%>'}"  style="width: 10px;display:none;" maxlength="0" > 
				<input id="myValue_<%=folders.get(i).getFolderid()%>" type="hidden" value = "<%=folders.get(i).getColor()%>" style="width: 0px;">
				<span id="name_<%=folders.get(i).getFolderid()%>" style="padding-left:5px"><%=folders.get(i).getName()%></span>
		  		<input type="text" value="<%=folders.get(i).getName()%>" id="edit_<%=folders.get(i).getFolderid()%>" 
		  		style="display:none;width:10em">
			</td>
			<td style="width:4em;">
			  	<a id="bt_edit_<%=folders.get(i).getFolderid()%>" 
			  		style="display:inline-block" title="<%=messages.getString("actividades.folder.editar")%>" href="javascript:editFolder('<%=folders.get(i).getFolderid()%>','1');">
					<img border="0" src="images/icon_modify.png" class="toolTipImg" href="javascript:editFolder('<%=folders.get(i).getFolderid()%>','1');">
				</a>
	
			  	<a id="bt_delete_<%=folders.get(i).getFolderid()%>" 
			  		style="display:inline-block" title="<%=messages.getString("actividades.folder.apagar")%>" href="javascript:if(confirm('<%=messages.getString("actividades.folder.delete")%>')){deleteFolder('<%=folders.get(i).getFolderid()%>')};">
					<img border="0" src="images/button_cancel.png" class="toolTipImg" href="javascript:if(confirm('<%=messages.getString("actividades.folder.delete")%>')){deleteFolder('<%=folders.get(i).getFolderid()%>')};">
				</a>
			  	
			  	<a id="bt_confirm_<%=folders.get(i).getFolderid()%>" 
			  		style="display:none" href="javascript:editFolder('<%=folders.get(i).getFolderid()%>','0');" title="<%=messages.getString("actividades.folder.confirm")%>">
					<img border="0" src="images/icon_ok.png"  class="toolTipImg" href="javascript:editFolder('<%=folders.get(i).getFolderid()%>','0');">
				</a>
	
			  	<a id="bt_cancel_<%=folders.get(i).getFolderid()%>" 
			  		style="display:none" href="javascript:editFolder('<%=folders.get(i).getFolderid()%>','-1');" title="<%=messages.getString("actividades.folder.cancelar")%>">
					<img border="0" src="images/icon_revert.png" class="toolTipImg" href="javascript:editFolder('<%=folders.get(i).getFolderid()%>','-1');">
				</a>
	
			</td>
			</tr>
			</table>
			</div>
		  <% } %>
			<table>
			<tr>
			<td>
							
			</td>
			<td>
			<input id="bt_pickColor_0"  class="color {valueElement:'myValue'}"  style="display:none;width: 10px;" maxlength="0">
			<input type="text" value="" id="edit_0" style="display:none;width:15em">
			</td>
			<td>
		  	<a id="bt_confirm_0" 
		  		style="display:none" href="javascript:editFolder('0','0');">
				<img border="0" src="images/icon_ok.png" title="<%=messages.getString("actividades.folder.confirm")%>::<%=messages.getString("actividades.folder.confirm")%>" class="toolTipImg" href="javascript:editFolder('0','0');">
			</a>

		  	<a id="bt_cancel_0" 
		  		style="display:none" href="javascript:editFolder('0','-1');">
				<img border="0" src="images/icon_revert.png" title="<%=messages.getString("actividades.folder.cancelar")%>::<%=messages.getString("actividades.folder.cancelar")%>" class="toolTipImg" href="javascript:editFolder('0','-1');">
			</a>
	

			
		
			</td>
			
			</tr>
			<tr>
			<td colspan="3">
			<input class="apt_regular_button" type="button" value="Criar Nova..." id="bt_edit_0" name="Criar Nova" onclick="editFolder('0','1')">	
			<input class="apt_regular_button" type="button" value="<%=messages.getString("actividades.folder.close")%>" id="bt_close" name="<%=messages.getString("actividades.folder.close")%>" style="width:6em;text-align:center" onclick="javascript:menuonoff('editar','1')">
			</td>
			</tr>	  		
			</table>
		</div>
        
        <input type="button" onclick="javascript:if (isAnyActivitySelected ()){menuonoff('atribuir');}else{alert('<if:message string="actividades.msg.no_activity_selected" />');} return false;" value="<%=messages.getString("actividades.folder.movetasks")%>" id="folderassign" name="folderassign" class="regular_button_03" style="float:left;">  
		<div id="atribuir" 
		style="display:none;position:absolute;z-index:1;background:none repeat scroll 0 0 #FFFFFF;border-color:#888888;
		border-style:solid;border-width:1px 1px 2px;text-align:left;left:228px;bottom:50px;padding:2px 5px">	
		<table>
	      <% for (int i = 0; i < folders.size(); i++) { %>
		  <tr>	
	        <td style="background:<%=folders.get(i).getColor()%>;width:4px;">
	      	</td>
	      	<td>
			<input type="button" class="apt_regular_button" style="width:15em;text-align:left;margin:0px" value="<%=folders.get(i).getName()%>" id="<%=folders.get(i).getFolderid()%>" name="<%=folders.get(i).getName()%>" onclick="assignActivities('<%=folders.get(i).getFolderid()%>')">
			</td>
		  </tr>	
		  <% } %>
		  </tr>
		</table>
		<input class="apt_regular_button" type="button" value="<%=messages.getString("actividades.folder.close")%>" id="bt_close2" name="<%=messages.getString("actividades.folder.close")%>" style="width:6em;text-align:center" onclick="javascript:menuonoff('atribuir','1')">
		</div>
		
   
    </div>
    
<%
  }
%>
		<input type="hidden" name="mode" value="0">
		<input type="hidden" name="activity" value="">
		<input type="hidden" name="startindex" value="${fn:escapeXml(nStartIndex)}">
		<input type="hidden" name="nextstartindex" value="${fn:escapeXml(nNextStartIndex)}">
		<input type="hidden" name="numitemspage" value="${fn:escapeXml(nItems)}">
		<input type="hidden" name="showflowid" value="${fn:escapeXml(nShowFlowId)}" >
		<input type="hidden" name="dtafter" value="${fn:escapeXml(dtAfter)}" >
		<input type="hidden" name="dtbefore" value="${fn:escapeXml(dtBefore)}" >
		<input type="hidden" name="orderby" value="${fn:escapeXml(orderBy)}" >
   </form>
   
<script language="JavaScript" >
	function setScroll(pos) {	
		setScrollPosition(pos);
	}
	setScroll(<%=scroll%>);
</script>
