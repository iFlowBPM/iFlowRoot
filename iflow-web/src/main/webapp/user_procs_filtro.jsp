<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" 
%><%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" 
%><%@ page import="org.apache.commons.collections15.map.ListOrderedMap"
%><%@ page import="org.apache.commons.collections15.OrderedMap"
%><%@ include file = "inc/defs.jsp" 
%><%
int ITEMS_PAGE = 20;

String stmp = null;

String sShowFlowId = fdFormData.getParameter("showflowid");
String sBeforeHtml = "";
String sAfterHtml = "";
int nShowFlowId = -1;
int nItems = ITEMS_PAGE;
int nMode = 0;

String pnumber = fdFormData.getParameter("pnumber");
if (StringUtils.isEmpty(pnumber))
	pnumber = "";

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
}
catch (Exception e) {
}
try {
	stmp = fdFormData.getParameter("mode");
	nMode = Integer.parseInt(stmp);
}
catch (Exception e) {
	nMode = 0;
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
<%
String sTargetUpdate = "";
//if (userflowid == -1) {
  sTargetUpdate = "userProcFiltrosFunc();";
  if (userInfo.isOrgAdmin() || (userflowid != -1 && hsSuperPrivs.contains(String.valueOf(userflowid))))
	  sTargetUpdate = "";
//}

boolean isAdmin = false;
String sTargetExtra = "style='display:none'"; 
if (userInfo.isOrgAdmin() ||
	(nShowFlowId != -1 && hsSuperPrivs != null && hsSuperPrivs.contains(String.valueOf(userflowid))))
  isAdmin = true; 
boolean isSearchableByInterv = false;
if (nShowFlowId != -1) {
	FlowSetting fs = BeanFactory.getFlowSettingsBean().getFlowSetting(nShowFlowId, Const.sSEARCHABLE_BY_INTERVENIENT);
	if (fs != null && Const.sSEARCHABLE_BY_INTERVENIENT_YES.equals(fs.getValue())) {
	  isSearchableByInterv = true;
	}
}

String paramAux = "searchtype=as";

String searchTextParameter ="searchText";

Map<String,Object> hmConfig = (Map<String,Object>)session.getAttribute(Const.SESSION_USER_PROCS);

String targetUser = "_MY_PROCS__";
String processStatus = "__OPEN__"; 
String orderBy = "p.pnumber";
String orderType = "asc";
if (hmConfig != null) {
  nShowFlowId = (Integer)hmConfig.get(Const.SESSION_USER_PROCS_FLOWID);
  targetUser = (String)hmConfig.get(Const.SESSION_USER_PROCS_TARGETUSER);
  processStatus = (String)hmConfig.get(Const.SESSION_USER_PROCS_PROCESSSTATUS);
  pnumber = (String)hmConfig.get(Const.SESSION_USER_PROCS_PNUMBER); 
  dtAfter = (Date)hmConfig.get(Const.SESSION_USER_PROCS_DTAFTER); 
  dtBefore = (Date)hmConfig.get(Const.SESSION_USER_PROCS_DTBEFORE); 
  nItems = (Integer)hmConfig.get(Const.SESSION_USER_PROCS_NITEMS);  
  orderBy = (String)hmConfig.get(Const.SESSION_USER_PROCS_ORDERBY);
  orderType = (String)hmConfig.get(Const.SESSION_USER_PROCS_ORDERTYPE);
}

String searchType = fdFormData.getParameter("searchtype");
if (searchType == null){
  // Se for null é tratado como pesquisa avançada
  searchType = "as";
}

String searchText = fdFormData.getParameter(searchTextParameter);
if (StringUtils.isEmpty(searchText))searchText = ""; 

sBeforeHtml = Utils.genFormDate(response, userInfo, "dtbefore", dtBefore, "f_up_date_a");
sAfterHtml = Utils.genFormDate(response, userInfo, "dtafter", dtAfter, "f_up_date_c");

String showflowidselection = "onchange=\"getSearchQuery(this, '" + response.encodeURL("user_proc_search_filter.jsp") + "', 'searchForm');" + sTargetUpdate + "\"";


String filterAction = "";
if (Const.SEARCH_ALL_USER_PROCS_BY_DEFAULT)
	filterAction = "javascript:tabber_right(8, '" + response.encodeURL("user_procs.jsp") + "', get_params(document.user_procs_filter)+'&clearsearch=true');";
else 
	filterAction = "javascript:if ($('showflowid').value != -1) tabber_right(8, '" + response.encodeURL("user_procs.jsp") + "', get_params(document.user_procs_filter)+'&clearsearch=true');";

request.setAttribute("flow_type", FlowType.WORKFLOW);

//check if it's simple search
if (StringUtils.equals(searchType,"ss")) { 
	if (fdFormData.getParameter("ssonly")==null || !StringUtils.equals(fdFormData.getParameter("ssonly"), "true")) { %>
		<form name="simpleSearchForm" method="post">
			<a id="searchlink" title="<%=messages.getString("user_procs_filtro.advancedsearch.tooltip")%>" class="toolTipItemLink li_link"
				href="javascript:tabber_save(8,'<%=response.encodeRedirectUrl("user_procs_filtro.jsp") %>','<%=paramAux%>','<%=response.encodeRedirectUrl("user_proc_seach.jsp") %>','<%=paramAux%>')"><%=messages.getString("user_procs_filtro.advancedsearch")%></a>
		</form>
	<% } %>

	<h1 id="title_tasks"><%=messages.getString("user_procs_filtro.simplesearch.title")%></h1>
	<!--  <p>< %=messages.getString("user_procs_filtro.simplesearch.introMsg") %></p> -->

	<div class="greybox">
		<form name="user_procs_filter" method="post">
			<input type="hidden" name="mode" value="0">
			<input type="hidden" name="process" value="">
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
			<if:formSelect name="processStatus" edit="true" value="<%=processStatus %>" noli="true" >
				<if:formOption value="__OPEN__" labelkey="user_procs_filtro.field.open"/>
				<if:formOption value="__CLOSED__" labelkey="user_procs_filtro.field.closed"/>
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
		   
			<input type="hidden" name="showUserProcs" value="true" > 
			<div id="searchForm">
			<% if (isAdmin || isSearchableByInterv) { %>
			<p class="item" id="targetuser_label"><if:message string="user_procs_filtro.field.targetuserlabel"/>:</p>
			<p class="item_indent" id="targetuser_body">
			<if:formSelect name="targetUser" edit="true" value="<%=targetUser %>" noli="true">
				<if:formOption value="__MY_PROCS__" labelkey="user_procs_filtro.field.mytargetuser"/>
				<% if (isAdmin) { %>
				<if:formOption value="__ALL_PROCS__" labelkey="user_procs_filtro.field.alltargetuser"/>
				<% } %>
				<% if (isSearchableByInterv) { %>
				<if:formOption value="__INT_PROCS__" labelkey="user_procs_filtro.field.inttargetuser"/>
				<% } %>
			</if:formSelect>
			<%} %>
			</p>
			<p class="item" id="orderby_label"><if:message string="user_procs_filtro.field.orderbylabel"/>:</p>
			<p class="item_indent" id="orderby_body">
			<if:formSelect name="orderby" edit="true" value="<%=orderBy %>" noli="true">
				<if:formOption value="f.flowname" label="fluxo"/>
				<if:formOption value="p.pnumber" label="processo"/>
				<if:formOption value="fs.result" label="estado"/>
				<if:formOption value="fs.mdate" label="desde"/>
				<if:formOption value="p.creator" label="dono"/>
			</if:formSelect>
			<if:formSelect name="ordertype" edit="true" value="<%=orderType %>" noli="true">
				<if:formOption value="asc" label="asc"/>
				<if:formOption value="desc" label="desc"/>
			</if:formSelect>
			</p>
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
	<form name="advancedSearch" method="post">
		<a id="searchlink"
			title="<%=messages.getString("user_procs_filtro.simplesearch.tooltip")%>" class="toolTipItemLink li_link"
			href="javascript:tabber_save(8,'<%=response.encodeRedirectUrl("user_procs_filtro.jsp") %>','searchtype=ss','<%=response.encodeRedirectUrl("user_procs.jsp") %>','searchtype=ss')"><%=messages.getString("user_procs_filtro.simplesearch")%></a>
	</form>

	<form name="advanced_user_procs_filter" method="post">
	<% //load flows
	FlowApplications appInfo = BeanFactory.getFlowApplicationsBean();
	//only loads search flows
	boolean showOnlyFlowsToBePresentInMenu = true;
	FlowMenu flows = BeanFactory.getFlowApplicationsBean().getAllApplicationOnlineMenu(userInfo,FlowType.SEARCH, showOnlyFlowsToBePresentInMenu);

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
