<!--
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
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<% 

String title = messages.getString("actividades_filtro.title"); 

int ITEMS_PAGE = 500;
int nStartIndex = 0;
int nNextStartIndex = 0;
String stmp = null;
String stmp2 = null;
String stmp3 = null;
String showflowidselection = "";
String sNavPrevHtml = "";
String sNavNextHtml = "";
int nShowFlowId = -1;
int nItems = ITEMS_PAGE;
int nMode = 0;
boolean bEnteringPage = true;
String order = "desc";
Date dtBefore = null;
Date dtAfter = null;
String pnumber = "";

//clean
String clean = fdFormData.getParameter("clean");
if(StringUtils.isEmpty(clean)) clean = "false";

if("true".equals(clean)){
	order = "desc";
	nItems = ITEMS_PAGE;
	dtBefore = null;
	dtAfter = null;
	pnumber = "";
	nShowFlowId = -1;
	nStartIndex = 0;
	nNextStartIndex = 0;
	bEnteringPage = true;
	session.setAttribute("filtro_order", order);
	session.setAttribute("filtro_nItems",nItems);
	session.setAttribute("filtro_dtBefore",dtBefore);
	session.setAttribute("filtro_dtAfter",dtAfter);
	session.setAttribute("filtro_pnumber",pnumber);
	session.setAttribute("filtro_showflowid",nShowFlowId);
	session.setAttribute("filtro_startindex",nStartIndex);
	session.setAttribute("filtro_nextstartindex",nNextStartIndex);
}else{
  pnumber = fdFormData.getParameter("pnumber");
	  if(StringUtils.isEmpty(pnumber))
	    pnumber = (String) session.getAttribute("filtro_pnumber");
	  if(StringUtils.isEmpty(pnumber))
	    pnumber = "";
	  else
	    session.setAttribute("filtro_pnumber",pnumber);
	  
  order = fdFormData.getParameter("filtro_order");
	if(StringUtils.isEmpty(order))
		order = (String) session.getAttribute("filtro_order");
	else
	  session.setAttribute("filtro_order",order);

  stmp = fdFormData.getParameter("numitemspage");
	if(StringUtils.isEmpty(stmp)) 
	  stmp = ""+session.getAttribute("filtro_nItems");
	else
	  session.setAttribute("filtro_nItems",stmp);
	try {	
		nItems = Integer.parseInt(stmp);
		bEnteringPage = false;
	}catch (Exception e) {}
	  
  stmp = fdFormData.getParameter("mode");
	if(StringUtils.isEmpty(stmp)) 
	  stmp = (String) session.getAttribute("mode");
	else
	  session.setAttribute("mode",stmp);
	try {
		nMode = Integer.parseInt(stmp);
		bEnteringPage = false;
	}catch (Exception e) {}
	
  stmp = fdFormData.getParameter("startindex");
	if (StringUtils.isEmpty(stmp)) 
	  nStartIndex = (Integer) session.getAttribute("filtro_startindex");
	else
	  session.setAttribute("filtro_startindex",nStartIndex);
	try {
		nStartIndex = Integer.parseInt(stmp);
		bEnteringPage = false;
	}catch (Exception e) {}
	
  stmp = fdFormData.getParameter("nextstartindex");
	if (StringUtils.isEmpty(stmp)) 
	  nNextStartIndex = (Integer) session.getAttribute("filtro_nextstartindex");
	else
	  session.setAttribute("filtro_nextstartindex",nNextStartIndex);
	try {
		nNextStartIndex = Integer.parseInt(stmp);
		bEnteringPage = false;
	}catch (Exception e) {}
	
  dtBefore = Utils.getFormDate(request,"dtbefore");
  dtAfter = Utils.getFormDate(request,"dtafter");
	try {
		if(dtBefore == null) 
		  dtBefore = (Date) session.getAttribute("filtro_dtBefore");
		else
		  session.setAttribute("filtro_dtBefore",dtBefore);
		if(dtAfter == null)  
		  dtAfter = (Date) session.getAttribute("filtro_dtAfter");
		else
		  session.setAttribute("filtro_dtAfter",dtAfter);
	}catch (Exception e) { }
	
	String sShowFlowId = fdFormData.getParameter("showflowid");
	if (StringUtils.isEmpty(sShowFlowId))
		sShowFlowId = (String) session.getAttribute("filtro_showflowid");
	if (StringUtils.isNotEmpty(sShowFlowId)) 
	  	bEnteringPage = false;
	if (userflowid > 0) 
	  	sShowFlowId = String.valueOf(userflowid);
	if (StringUtils.isEmpty(sShowFlowId)) 
	  	sShowFlowId = "-1";
	try {
		nShowFlowId = Integer.parseInt(sShowFlowId);
	}catch (Exception e) { }
    session.setAttribute("filtro_showflowid",sShowFlowId);
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


HashMap<String,String> hmFlowInfo = new HashMap<String,String>();

boolean bAdminUser = false;
HashSet<Integer> hsPrivs = new HashSet<Integer>();
Flow flow = BeanFactory.getFlowBean();
IFlowData[] fda = null;
try {

	if (userInfo.isOrgAdmin()) {
		// iflow administrator has full access
		bAdminUser = true;
	}
	else {

	  	FlowRolesTO[] fra = flow.getAllUserFlowRoles(userInfo);
		for (int i = 0; i < fra.length; i++) {

			if (fra[i].hasPrivilege(FlowRolesTO.WRITE_PRIV)) {
				hsPrivs.add(new Integer(fra[i].getFlowid()));
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

if (userflowid == -1) {
	  ArrayList<String> alValues = new ArrayList<String>();
	  ArrayList<String> alNames = new ArrayList<String>();
	  alValues.add("-1");
	  alNames.add(messages.getString("actividades_filtro.field.selectdefault"));
	  for (int i=0; fda != null && i < fda.length; i++) {
	    if (!bAdminUser && !hsPrivs.contains(new Integer(fda[i].getId()))) {
	      continue;
	    }
	    alValues.add(String.valueOf(fda[i].getId()));
	    
	    final int nMAX_SELECT_NAME_SIZE = 30;
	    final String sCONTINUATION_MARK = "...";
	    
	    String sName = fda[i].getName();
	    if (sName.length() > nMAX_SELECT_NAME_SIZE) {
	    	sName = sName.substring(0, nMAX_SELECT_NAME_SIZE - sCONTINUATION_MARK.length()) + sCONTINUATION_MARK;
	    }
	    
	    alNames.add(sName);
	  }
	  showflowidselection = " ";
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


//TODO usar um objecto (bean) para estas tarefas...
Map<String,Long> hmConfig = (Map<String,Long>)session.getAttribute(sACT_CONF);
Map<String,Integer> hmIndex = (Map<String,Integer>)session.getAttribute(sACT_IND);
Map<String,Object> hmBatch = (Map<String,Object>)session.getAttribute(sACT_BATCH);

boolean bSessionConfig = false;
boolean bSessionIndex = false;
boolean bBatchProcessing = false;

String sConfig = fdFormData.getParameter(sConfigVar);
String sPreConfig = fdFormData.getParameter(sPreConfigVar);

String sIndex = fdFormData.getParameter(sIndexVar);
String sPreIndex = fdFormData.getParameter(sPreIndexVar);

String sBatch = fdFormData.getParameter(sBatchVar);
String sPreBatch = fdFormData.getParameter(sPreBatchVar);

//batch
List<String> alBatchPids = null;
Map<String,String> hmBatchLinks = null;


// CONFIG
if (sConfig != null && sConfig.equals(sVarSet) && (sPreConfig == null || !sPreConfig.equals(sConfig))) {
  hmConfig = new HashMap<String,Long>();
}
else if (sConfig == null && sPreConfig != null && sPreConfig.equals(sVarSet)) {
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
    dtBefore = l==null?null:new Date(l);
    l = hmConfig.get(sCONF_DT_AFTER);
    dtAfter = l==null?null:new Date(l);
    nItems = hmConfig.get(sCONF_ITEMS_PAGE).intValue();
  }
  else {
    hmConfig.put(sCONF_FLOW, new Long(nShowFlowId));
    hmConfig.put(sCONF_DT_BEFORE, dtBefore == null? null: dtBefore.getTime());
    hmConfig.put(sCONF_DT_AFTER, dtAfter == null? null: dtAfter.getTime());
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
  hmIndex = new HashMap<String,Integer>();
}
else if (sIndex == null && sPreIndex != null && sPreIndex.equals(sVarSet)) {
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
  }
  else {
    hmIndex.put(sACT_START_IND, nStartIndex);
    hmIndex.put(sACT_NEXT_START_IND, nNextStartIndex);
    session.setAttribute(sACT_IND, hmIndex);
  }
}

String sPreIndexHidden = sIndex;
if (sPreIndex == null && hmIndex != null) {
  sPreIndexHidden = sVarSet;
}

//BATCH
if (hmBatch != null) {
  // XXX This is not implemented here...
  bBatchProcessing = true;

  alBatchPids = (List<String>)hmBatch.get(sACT_BATCH_PIDS);
  hmBatchLinks = (Map<String,String>)hmBatch.get(sACT_BATCH_LINKS);
}
String sPreBatchHidden = sBatch;

String filterAction = "javascript:tabber_right(2, '" + response.encodeURL("actividades.jsp") + "', get_params(document.activities_filter));";
request.setAttribute("flow_type", FlowType.WORKFLOW);

%>
<h1 id="title_tasks"><%=title%></h1>
	<p><%=messages.getString("actividades_filtro.introMsg")%></p>
	<div class="greybox">
	<form name="activities_filter" method="post">
	<input type="hidden" name="mode" value="0">
	<input type="hidden" name="activity" value="">
	<input type="hidden" name="startindex" value="<%=nStartIndex%>">
	<input type="hidden" name="nextstartindex" value="<%=nStartIndex%>">
	<input type="hidden" name="<%=sPreConfigVar%>" value="<%=sPreConfigHidden%>">
	<input type="hidden" name="<%=sPreIndexVar%>" value="<%=sPreIndexHidden%>">
	<input type="hidden" name="<%=sPreBatchVar%>" value="<%=sPreBatchHidden%>">
	<input type="hidden" name="processbatch" value="">

	

	<% if (!showflowidselection.equals("")) { %>
	  <p class="item"><%=messages.getString("actividades_filtro.field.select")%>:</p>
	  <p class="item_indent">
	  <%@ include file="inc/grouped_flow_list.jspf" %>
	  </p>
	<% } %>
	
	  <p class="item"><%=messages.getString("actividades_filtro.field.pnumber")%>:</p>
	  <p class="item_indent"><input type="text" id="pnumber" name="pnumber" size="12" value="<%=pnumber %>" maxlength="1024"><img class="icon_clear" src="images/icon_delete.png" onclick="javascript:document.getElementById('pnumber').value='';"/></p>

	  <p class="item"><%=messages.getString("actividades_filtro.field.fromdate")%>:</p>
	  <p class="item_indent">
		<input class="calendaricon" id="f_date_a" type="text" size="12" name="dtafter" 
		  value="<% if (dtAfter != null) {%><%=DateUtility.formatFormDate(userInfo, dtAfter) %><%} %>" 
		  onBlur="javascript:tabber_right(2, '<%=response.encodeURL("actividades.jsp") %>', get_params(document.activities_filter));" onmouseover="caltasks(this.id);this.onmouseover=null;" />
		<img class="icon_clear" src="images/icon_delete.png" onclick="javascript:document.getElementById('f_date_a').value='';" />
	  </p>
	  
	  <p class="item"><%=messages.getString("actividades_filtro.field.todate")%>:</p>
	  <p class="item_indent">
		<input class="calendaricon" id="f_date_c" type="text" size="12" name="dtbefore" 
		  value="<% if (dtBefore != null) {%><%=DateUtility.formatFormDate(userInfo, dtBefore) %><%} %>" 
		  onBlur="javascript:tabber_right(2, '<%=response.encodeURL("actividades.jsp") %>', get_params(document.activities_filter));" onmouseover="caltasks(this.id);this.onmouseover=null;" />
		<img class="icon_clear" src="images/icon_delete.png" onclick="javascript:document.getElementById('f_date_c').value='';" />
	  </p>

	  <p class="item"><%=messages.getString("actividades_filtro.field.nitems")%>:</p>
	  <p class="item_indent">
	      <select name="numitemspage" id="items_page" onchange="<%=filterAction %>">
	         <option value="5"   <%=nItems == 5 ? "selected" : "" %>>5</option>
	         <option value="20"  <%=nItems == 20 ? "selected" : "" %>>20</option>
	         <option value="50"  <%=nItems == 50 ? "selected" : "" %>>50</option>
	         <option value="100" <%=nItems == 100 ? "selected" : "" %>>100</option>
	         <option value="500" <%=nItems == 500 ? "selected" : "" %>>500</option>
	      </select>
	  </p>
	  
	  <p class="item"><%=messages.getString("actividades_filtro.order.title")%>:</p>
		<p class="item_order">
			<select name="orderby" id="orderby" onchange="<%=filterAction %>">
			     <option value="asc"  <%=order.equals("asc") ? "selected" : "" %>><%=messages.getString("actividades_filtro.order.ascendente")%></option>
			     <option value="desc" <%=order.equals("desc") ? "selected" : "" %>><%=messages.getString("actividades_filtro.order.descendente")%></option>
			 </select>
		</p>
	  
	  <p class="item"></p>
<%-- 	
	  <p class="item_indent"><input type="checkbox" name="config" value="set" title="Keep criteria" <%=bSessionConfig?"checked":""%>>Keep criteria</p>
	  <p class="item_indent"><input type="checkbox" name="index" value="set" title="Keep current page page" <%=bSessionIndex?"checked":""%>>Keep current page</p>
	  <p class="item_indent"><input type="checkbox" name="batch" value="set" title="Batch processing" <%=bBatchProcessing?"checked":""%>>Batch processing</p>
--%>




	  <div class="button_box">
		<input id="link_search_span" class="regular_button_00" type="button" name="filter" value="<%=messages.getString("button.filter")%>" 
			onClick="<%=filterAction %>"
		/>
	  </div>
	</form>
	</div>
