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
<%@ include file = "../inc/defs.jsp" %>
<%@ page import = "pt.iflow.chart.AuditChartServlet" %>
<%
boolean intervalChanged = false;
String title = messages.getString("proc_perf.title");
String sPage = "Reports/proc_perf";

StringBuffer sbError = new StringBuffer();
int flowid = -1;

String sFlowId = fdFormData.getParameter(AuditChartServlet.PARAM_FLOWID);
if (StringUtils.isNotEmpty(sFlowId)) {
  try {
    flowid = Integer.parseInt(sFlowId);
  }
  catch (Exception e) {
    sFlowId = "-1";
    flowid = -1;
  }
}
else {
  sFlowId = "-1";
  flowid = -1;
}
%>
<if:checkUserAdmin type="spv">
	<div class="error_msg">
		<if:message string="admin.error.unauthorizedaccess" />
	</div>
</if:checkUserAdmin>
<%

IFlowData[] fda;

// TODO checkbox para fluxos online/offline
fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, FlowType.WORKFLOW);
if (fda == null) fda = new IFlowData[0];

Map<String,String> displayDesc = AuditChartServlet.getDisplayParams(userInfo);
%>
<form name="procperf" method="POST" onsubmit="return false;">

  <h1 id="title_reports"><%=title%></h1>
  <div class="info_msg">
	  <%=messages.getString("proc_perf.introMsg")%>
  </div>

<% if (sbError.length() > 0) { %>
  <div class="error_msg">
    <%=sbError.toString()%>
  </div>
<% } %>
  
  <div class="chart">
    <img id="chart" src="images/loading.gif" border=0 />
  </div>

  <fieldset>
    <ol>
    <li>
      <label for="perf_offline"><%=messages.getString("proc_perf.field.show_offline") %></label>
      <input class="" type="checkbox" id="perf_offline" value="set" title="<%=messages.getString("proc_perf.field.show_offline") %>" 
      	onclick="proc_rpt_offline(this,proc_perf_execute)">
    </li>
    <li>
      <if:formSelect name="flowid" edit="true" value='<%=sFlowId%>' labelkey="proc_perf.field.flow"
      		onchange="toggleDisplayTimeUnits();proc_perf_execute('<%=ts %>')" noli="true">
	      <if:formOption value="-1" labelkey="const.all"/>
		<% 
	for(int i=0; i < fda.length; i++) { 
      if (userInfo.isOrgAdmin() || userInfo.isProcSupervisor(fda[i].getId())) {
  		%>
		<if:formOption value='<%=fda[i].getId()%>' label="<%= fda[i].getName() %>"/>
		<%    
      }
	} 
		%>
      </if:formSelect>
      </li>
      <li id="dinamicTime" style="display:none">
      <if:formSelect name="<%=AuditChartServlet.PARAM_DISPLAY_UNITS%>" edit="true" 
      		value='<%=AuditChartServlet.HOURS%>' labelkey="proc_perf.field.units"
      		onchange="proc_perf_execute('<%=ts %>')" noli="true">
	      <if:formOption value='<%=AuditChartServlet.DAYS %>' label="<%= displayDesc.get(AuditChartServlet.DAYS) %>"/>
	      <if:formOption value='<%=AuditChartServlet.HOURS %>' label="<%= displayDesc.get(AuditChartServlet.HOURS) %>"/>
	      <if:formOption value='<%=AuditChartServlet.MINS %>' label="<%= displayDesc.get(AuditChartServlet.MINS) %>"/>
	      <if:formOption value='<%=AuditChartServlet.SECS %>' label="<%= displayDesc.get(AuditChartServlet.SECS) %>"/>
      </if:formSelect>
      </li>
<%
  String onChange = "toggleProcStatsDate();setIntervalValue(this.value); if (this.value != 'const.choose') { proc_perf_execute('"+ts+"'); }"; 
  String fromtoDateOnChange = "proc_perf_execute('"+ts+"')";
%><%@ include file = "proc_time_interval_inc.jspf" %>
    </ol>
  </fieldset>
  <div class="invisible">
    <img id="invisible" src="images/transpar.gif" border=0
      	onload="proc_perf_execute('<%=ts %>')" />
  </div>
</form>

<if:generateHelpBox context="proc_perf"/>


