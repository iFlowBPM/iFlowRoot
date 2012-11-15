<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" 
%><%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" 
%><%@ page import="org.apache.commons.collections15.map.ListOrderedMap"
%><%@ page import="org.apache.commons.collections15.OrderedMap"
%><%@ include file = "inc/defs.jsp" 
%><%!
final int nMAX_SELECT_NAME_SIZE = 30;
final String sCONTINUATION_MARK = "...";

// move this to java class please!! repeated in proc_undo_select.jsp

  public static String genFormDate(HttpServletResponse response, UserInfoInterface userInfo, String asRequestName, java.util.Date adtDate, String asFieldID) {
    StringBuffer retObj = new StringBuffer();
  
	String sDate = "";
	if (adtDate != null) {
		sDate = DateUtility.formatFormDate(userInfo, adtDate);
	}
	
	retObj.append("<input class=\"calendaricon\" type=\"text\" size=\"12\"");
	retObj.append(" id=\"").append(asFieldID).append("\"");
	retObj.append(" name=\"").append(asRequestName).append("\"");
	retObj.append(" value=\"").append(sDate).append("\"");
	retObj.append(" onmouseover=\"caltasks(this.id);this.onmouseover=null;\"/>");
	
	retObj.append("<img class=\"icon_clear\" src=\"images/icon_delete.png\"");
	retObj.append(" onclick=\"javascript:document.getElementById('" + asFieldID + "').value='';\"/>");

    return retObj.toString();
  }
  
%><% 

int ITEMS_PAGE = 20;
int nStartIndex = 0;
int nNextStartIndex = 0;

String stmp = null;

String sShowFlowId = fdFormData.getParameter("showflowid");
String sBeforeHtml = "";
String sAfterHtml = "";
int nShowFlowId = -1;
int nItems = ITEMS_PAGE;
int nMode = 0;
boolean bEnteringPage = true;
String pnumber = fdFormData.getParameter("pnumber");
if (StringUtils.isEmpty(pnumber))
	pnumber = "";

if (sShowFlowId != null && !sShowFlowId.equals("")) {
  bEnteringPage = false;
}

if (userflowid > 0) {
	sShowFlowId = String.valueOf(userflowid);
}
if (sShowFlowId == null || sShowFlowId.equals("")) sShowFlowId = "-1";

try {
	nShowFlowId = Integer.parseInt(sShowFlowId);
}
catch (Exception e) {
}
try {
	stmp = fdFormData.getParameter("numitemspage");
	nItems = Integer.parseInt(stmp);
	bEnteringPage = false;
}
catch (Exception e) {
}
try {
	stmp = fdFormData.getParameter("mode");
	nMode = Integer.parseInt(stmp);
	bEnteringPage = false;
}
catch (Exception e) {
	nMode = 0;
}
try {
	stmp = fdFormData.getParameter("startindex");
	nStartIndex = Integer.parseInt(stmp);
	bEnteringPage = false;
}
catch (Exception e) {
}
try {
	stmp = fdFormData.getParameter("nextstartindex");
	nNextStartIndex = Integer.parseInt(stmp);
	bEnteringPage = false;
}
catch (Exception e) {
}

if (nMode == 0) {
  nStartIndex = 0;
  nNextStartIndex = 0;
}
else if (nMode > 0) {
  nStartIndex = nNextStartIndex;
}
else if (nMode < 0) {
  nNextStartIndex = nStartIndex;
  nStartIndex--;
}

Date dtBefore = Utils.getFormDate(request, "dtbefore");
Date dtAfter = Utils.getFormDate(request, "dtafter");

HashMap<String,String> hmFlowInfo = new HashMap<String,String>();

HashSet<Integer> hsPrivs = new HashSet<Integer>();
HashSet<String> hsSuperPrivs = new HashSet<String>();
Flow flow = BeanFactory.getFlowBean();
IFlowData[] fda = null;
try {

	if (!userInfo.isOrgAdmin()) {

		FlowRolesTO[] fra = flow.getAllUserFlowRoles(userInfo);
		for (int i = 0; i < fra.length; i++) {

			if (fra[i].hasPrivilege(FlowRolesTO.READ_PRIV) || fra[i].hasPrivilege(FlowRolesTO.SUPERUSER_PRIV)) {
				hsPrivs.add(new Integer(fra[i].getFlowid()));
			}
			if (fra[i].hasPrivilege(FlowRolesTO.SUPERUSER_PRIV)) {
				hsSuperPrivs.add(String.valueOf(fra[i].getFlowid()));
			}
		}
	}

	fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo,FlowType.WORKFLOW);

	for (int i=0; fda != null && i < fda.length; i++) {
		hmFlowInfo.put(String.valueOf(fda[i].getId()),fda[i].getName());
	}
}
catch (Exception e) {
	Logger.errorJsp(login,"flows"," checking admin profile exception: " + e.getMessage());
	e.printStackTrace();
}

%>
<%--
<script type="text/javascript">
function updateTargetOwner() {
	var flowid = $('showflowid').value;
<% if (userInfo.isOrgAdmin() || (userflowid != -1 && hsSuperPrivs.contains(String.valueOf(userflowid)))) { %>
	$('targetuser_label').setStyle('display','');
	$('targetuser_body').setStyle('display','');
<% } else {
	Iterator superIter = hsSuperPrivs.iterator(); 
	while (superIter.hasNext()) {
%>
		if (flowid == '<%=(String)superIter.next()%>') {
			$('targetuser_label').setStyle('display','');
			$('targetuser_body').setStyle('display','');
			return;
		}
<%
	}
%>
		
<%
   } %>
}
</script>
--%>
<%
String sTargetUpdate = "";
if (userflowid == -1) {
  sTargetUpdate = "userProcFiltrosFunc();";
  if (userInfo.isOrgAdmin() || (userflowid != -1 && hsSuperPrivs.contains(String.valueOf(userflowid))))
	  sTargetUpdate = "";
}


String sTargetExtra = "style='display:none'"; 
if (userInfo.isOrgAdmin() ||
    (userflowid == -1 && !hsSuperPrivs.isEmpty()) ||
	(userflowid != -1 && hsSuperPrivs.contains(String.valueOf(userflowid))) ||
	(sShowFlowId != null && !sShowFlowId.equals("") && hsSuperPrivs.contains(sShowFlowId)))
	sTargetExtra = ""; 

String sUP_CONF = "USER_PROCS_CONFIG";
String sUP_CONF_FLOW = "USER_PROCS_CONFIG_FLOWID";
String sUP_CONF_DT_BEFORE = "USER_PROCS_CONFIG_DT_BEFORE";
String sUP_CONF_DT_AFTER = "USER_PROCS_CONFIG_DT_AFTER";
String sUP_CONF_ITEMS_PAGE = "USER_PROCS_CONFIG_ITEMS_PAGE";

String sUP_IND = "USER_PROCS_INDEX";
String sUP_START_IND = "USER_PROCS_START_INDEX";
String sUP_NEXT_START_IND = "USER_PROCS_NEXT_START_INDEX";


String sPreConfigVar = "pre_config";
String sConfigVar = "config";
String sPreIndexVar = "pre_index";
String sIndexVar = "index";

String sVarSet = "set";

String searchTextParameter ="searchText";

Map<String,Long> hmConfig = (Map<String,Long>)session.getAttribute(sUP_CONF);
Map<String,Integer> hmIndex = (HashMap<String,Integer>)session.getAttribute(sUP_IND);

String sConfig = fdFormData.getParameter(sConfigVar);
String sPreConfig = fdFormData.getParameter(sPreConfigVar);

String sIndex = fdFormData.getParameter(sIndexVar);
String sPreIndex = fdFormData.getParameter(sPreIndexVar);

String searchType = fdFormData.getParameter("searchtype");
if (searchType == null){
  // Se for null é tratado como pesquisa avançada
  searchType = "as";
}

String searchText = fdFormData.getParameter(searchTextParameter);
if (StringUtils.isEmpty(searchText))searchText = ""; 

// CONFIG
if (sConfig != null && sConfig.equals(sVarSet) && (sPreConfig == null || !sPreConfig.equals(sConfig))) {
  hmConfig = new HashMap<String,Long>();
}
else if (sConfig == null && sPreConfig != null && sPreConfig.equals(sVarSet)) {
  session.removeAttribute(sUP_CONF);
  hmConfig = null;
}


if (hmConfig != null) {
  if (bEnteringPage) {
    nShowFlowId = hmConfig.get(sUP_CONF_FLOW).intValue();
    Long l = null;
    l = hmConfig.get(sUP_CONF_DT_BEFORE);
    l = hmConfig.get(sUP_CONF_DT_AFTER);
    dtBefore = (dtBefore == null ? (l == null ? null : new Date(l)) : dtBefore);
    dtAfter = (dtAfter == null ? (l == null ? null : new Date(l)) : dtAfter);
    nItems = hmConfig.get(sUP_CONF_ITEMS_PAGE).intValue();
  }
  else {
    hmConfig.put(sUP_CONF_FLOW, new Long(nShowFlowId));
    hmConfig.put(sUP_CONF_DT_BEFORE, (dtBefore == null ? null : dtBefore.getTime()));
    hmConfig.put(sUP_CONF_DT_AFTER, (dtAfter == null ? null : dtAfter.getTime()));
    hmConfig.put(sUP_CONF_ITEMS_PAGE, new Long(nItems));
    session.setAttribute(sUP_CONF, hmConfig);    
  }
}
String sPreConfigHidden = sConfig;
if (sPreConfig == null && hmConfig != null) {
  sPreConfigHidden = sVarSet;
}


// INDEX
if (sIndex != null && sIndex.equals(sVarSet) && (sPreIndex == null || !sPreIndex.equals(sIndex))) {
  hmIndex = new HashMap<String,Integer>();
}
else if (sIndex == null && sPreIndex != null && sPreIndex.equals(sVarSet)) {
  session.removeAttribute(sUP_IND);
  hmIndex = null;
}


if (hmIndex != null) {
  if (bEnteringPage) {
    nStartIndex = hmIndex.get(sUP_START_IND);
    nNextStartIndex = hmIndex.get(sUP_NEXT_START_IND);
    nMode = 0;
  }
  else {
    hmIndex.put(sUP_START_IND, nStartIndex);
    hmIndex.put(sUP_NEXT_START_IND, nNextStartIndex);
    session.setAttribute(sUP_IND, hmIndex);
  }
}

String sPreIndexHidden = sIndex;
if (sPreIndex == null && hmIndex != null) {
  sPreIndexHidden = sVarSet;
}

//BATCH

sBeforeHtml = genFormDate(response, userInfo, "dtbefore", dtBefore, "f_up_date_a");
sAfterHtml = genFormDate(response, userInfo, "dtafter", dtAfter, "f_up_date_c");

String showflowidselection = "onchange=\"getSearchQuery(this, '" + response.encodeURL("user_proc_search_filter.jsp") + "', 'searchForm');" + sTargetUpdate + "\"";


String filterAction = "javascript:if ($('showflowid').value != -1) tabber_right(8, '" + response.encodeURL("user_procs.jsp") + "', get_params(document.user_procs_filter));";
request.setAttribute("flow_type", FlowType.WORKFLOW);

//check if it's simple search
if (StringUtils.equals(searchType,"ss")) { 
	if (fdFormData.getParameter("ssonly")==null || !StringUtils.equals(fdFormData.getParameter("ssonly"), "true")) { %>
		<form name="simpleSearchForm" method="post">
			<a id="searchlink" title="<%=messages.getString("user_procs_filtro.advancedsearch.tooltip")%>" class="toolTipItemLink li_link"
				href="javascript:tabber_save(8,'<%=response.encodeRedirectUrl("user_procs_filtro.jsp") %>','searchtype=as','<%=response.encodeRedirectUrl("user_proc_seach.jsp") %>','searchtype=as')"><%=messages.getString("user_procs_filtro.advancedsearch")%></a>
		</form>
	<% } %>

	<h1 id="title_tasks"><%=messages.getString("user_procs_filtro.simplesearch.title")%></h1>
	<!--  <p>< %=messages.getString("user_procs_filtro.simplesearch.introMsg") %></p> -->

	<div class="greybox">
		<form name="user_procs_filter" method="post">
			<input type="hidden" name="mode" value="0">
			<input type="hidden" name="process" value="">
			<input type="hidden" name="startindex" value="<%=nStartIndex%>">
			<input type="hidden" name="nextstartindex" value="<%=nStartIndex%>">
			<input type="hidden" name="<%=sPreConfigVar%>" value="<%=sPreConfigHidden%>">
			<input type="hidden" name="<%=sPreIndexVar%>" value="<%=sPreIndexHidden%>">
			<input type="hidden" id="proc_search" name="proc_search" value="false">
			<input type="hidden" id="atLeastOneSuper" name="atLeastOneSuper" value="<%= !hsSuperPrivs.isEmpty() %>">
			<!-- SearchTextRelated -->
			<input type="hidden" id="searchText" name="searchText" value="<%=searchText%>">
			
			<%  Iterator<String> superIter = hsSuperPrivs.iterator(); 
				while (superIter.hasNext()) { 
					String inputFlow = superIter.next();%>
					<input type="hidden" name="<%=inputFlow%>" id="<%=inputFlow%>" value="">
			<%	}
		    if(fda.length > 0) { %>
				<p class="item"><if:message string="user_procs_filtro.field.select"/>:</p>
				<p class="item_indent">
					<%@ include file="inc/grouped_flow_list.jspf" %>
				</p>
			<% } %>
	
			<p class="item"><if:message string="user_procs_filtro.field.pnumber"/>:</p>
			<p class="item_indent"><input type="text" id="pnumber" name="pnumber" size="12" value="<%=pnumber %>" maxlength="1024"/><img class="icon_clear" src="images/icon_delete.png" onclick="javascript:document.getElementById('pnumber').value='';"/></p>
			
			<p class="item"><if:message string="user_procs_filtro.field.fromdate"/>:</p>
			<p class="item_indent"><%=sAfterHtml%></p>
			
			<p class="item"><if:message string="user_procs_filtro.field.todate"/>:</p>
			<p class="item_indent"><%=sBeforeHtml%></p>
			
			<p class="item"><if:message string="user_procs_filtro.field.process_status"/>:</p>
			<p class="item_indent">
			<if:formSelect name="processStatus" edit="true" value="__OPEN__" noli="true" >
				<if:formOption value="__OPEN__" labelkey="user_procs_filtro.field.open"/>
				<if:formOption value="__CLOSED__" labelkey="user_procs_filtro.field.closed"/>
			</if:formSelect>
			</p>
			
			<p class="item" id="targetuser_label" <%=sTargetExtra %>><if:message string="user_procs_filtro.field.targetuserlabel"/>:</p>
			<p class="item_indent" id="targetuser_body" <%=sTargetExtra %>>
			<if:formSelect name="targetUser" edit="true" value="__MY_PROCS__" noli="true">
				<if:formOption value="__MY_PROCS__" labelkey="user_procs_filtro.field.mytargetuser"/>
				<if:formOption value="__ALL_PROCS__" labelkey="user_procs_filtro.field.alltargetuser"/>
			</if:formSelect>
			</p>
			
			<p class="item"><%=messages.getString("user_procs_filtro.field.nitems")%>:</p>
			<p class="item_indent">
			    <select name="numitemspage" id="items_page" onchange="<%=filterAction %>">
			       <option value="5">5</option>
			       <option value="20" selected="selected">20</option>
			       <option value="50">50</option>
			       <option value="100">100</option>
			    </select>
			</p>
			
			<!-- "Texto a pesquisar" -->
			<p class="item" style="display:none"><if:message string="user_procs_filtro.field.searchtext"/>:</p>
			<p class="item_indent" style="display:none"><input type="text" id="searchText" name="searchText" size="15" value="<%=searchText %>" maxlength="1024"/></p>
			
			<p class="item" style="display:none"><if:message string="user_procs_filtro.field.searchtext.checkbox"/>:
			<input class="" type="checkbox" id="searchTextCheckbox" value="set" title="<%=messages.getString("user_procs_filtro.field.searchtext.checkbox") %>" 
		   		onclick="proc_sla_execute('<%=ts%>')">
			</p>
			   
			
			<p class="item"></p>
			
			<input type="hidden" name="showUserProcs" value="true" > 
			<div id="searchForm">
			</div>
	 
			<div class="button_box">
				<input id="link_search_span" class="regular_button_00" type="button" name="filter" value="<%=messages.getString("button.filter")%>" 
					onClick="<%=filterAction %>" />
			</div>
		</form> 
	</div>
<%//end of SimpleSearch
}
if(StringUtils.equals(searchType,"as")) {%>
<!-- 
	<form name="advancedSearch" method="post">
		<a id="searchlink"
			title="<%=messages.getString("user_procs_filtro.simplesearch.tooltip")%>" class="toolTipItemLink li_link"
			href="javascript:tabber_save(8,'<%=response.encodeRedirectUrl("user_procs_filtro.jsp") %>','searchtype=ss','<%=response.encodeRedirectUrl("user_procs.jsp") %>','searchtype=ss')"><%=messages.getString("user_procs_filtro.simplesearch")%></a>
	</form>
 -->
	<form name="advanced_user_procs_filter" method="post">
	<% //load flows
	FlowApplications appInfo = BeanFactory.getFlowApplicationsBean();
	//only loads search flows
	FlowMenu flows = BeanFactory.getFlowApplicationsBean().getAllApplicationOnlineMenu(userInfo,FlowType.SEARCH);
	
	ArrayList<OrderedMap<Object,Object>> appFlows = new ArrayList<OrderedMap<Object,Object>>();
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
	        
  		hm.put("flows", menuPart.getFlows());
  		hm.put("links", menuPart.getLinks());
  		hm.put("tooltip_flow", messages.getString("main_content.processes.tooltip.flows"));
  		hm.put("tooltip_link", messages.getString("main_content.processes.tooltip.links"));
  
		appFlows.add(hm);
  	}

	if (appFlows.isEmpty()) {
	  ServletUtils.sendEncodeRedirect(response, "user_procs_filtro.jsp?searchtype=ss&ssonly=true");
	}
  	
	//Teste vm
	String tabnr = (String) fdFormData.getParameter("navtabnr");
	String pageContent = "proc_list";
	java.util.Hashtable<String,Object> hsSubstLocal = new java.util.Hashtable<String,Object>();
	hsSubstLocal.put("appflows", appFlows);
	
	hsSubstLocal.put("processesMsg", messages.getString("user_procs_filtro.advancedsearch.introMsg"));
	hsSubstLocal.put("tabnr", tabnr);
	
	hsSubstLocal.put("title", messages.getString("user_procs_filtro.advancedsearch.title"));
	hsSubstLocal.put("ts", java.lang.Long.toString(ts));
	hsSubstLocal.put("url_prefix", sURL_PREFIX.substring(0, sURL_PREFIX.length() - 1));
	hsSubstLocal.put("css", css);
	hsSubstLocal.put("procMenuVisible", "visible");
	
	String html = PresentationManager.buildPage(response, userInfo, hsSubstLocal, pageContent);
	out.println(html);
}
%>
</form>
