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
<%@ include file = "../../inc/defs.jsp" %>

<%@ page import = "pt.iflow.api.blocks.Block" %>
<%@ page import = "java.sql.*" %>
<%@ page import = "javax.sql.*" %>

<%
	String title = messages.getString("flow_state_procs.title"); 
String sPage = "Admin/ProcManagement/flow_state_procs";

StringBuffer sbError = new StringBuffer();
int flowid = -1;
int state = -1;

String sESCOLHA = messages.getString("const.choose");

String sFlowId = null;
String sState = null;

sFlowId = fdFormData.getParameter("flowid");
sState = fdFormData.getParameter("state");
if (sFlowId != null && !sFlowId.equals("")) {
  try {
    flowid = Integer.parseInt(sFlowId);
  }
  catch (Exception e) {
    flowid = -1;
  }
}
else {
  flowid = -1;
}

if (sState != null && !sState.equals("")) {
  try {
    state = Integer.parseInt(sState);
  }
  catch (Exception e) {
    state = -1;
  }
}
else {
  state = -1;
}

if (flowid < 0 || state < 0) {
  ServletUtils.sendEncodeRedirect(response, "flow_states.jsp");
  return;
}
%>
<%@ include file = "../auth.jspf" %>
<%
	Flow flow = BeanFactory.getFlowBean();

IFlowData fd = null;
List<Map<String,String>> alProcs = null;
Map<String,String> hmtmp = null;
String stmp = null;
Timestamp tstmp = null;
String sPid = null;
String sSubPid = null;

if (flow != null) {
  
  fd = flow.getFlow(userInfo, flowid);

  if (fd != null) {
    DataSource dso = Utils.getDataSource();
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      db = dso.getConnection();
      st = db.createStatement();
      // first check if process is closed
      rs = st.executeQuery("select pid,subpid,result,mdate from flow_state where flowid="
	   + flowid + " and state=" + state + " order by mdate asc");
      
      alProcs = new ArrayList<Map<String,String>>();
      while (rs != null && rs.next()) {
	hmtmp = new HashMap<String,String>();

	stmp = rs.getString("pid");
	hmtmp.put("pid",stmp);
    
	stmp = rs.getString("subpid");
	hmtmp.put("subpid",stmp);
    
	stmp = rs.getString("result");
	hmtmp.put("result",stmp);

	tstmp = rs.getTimestamp("mdate");
	stmp = "&nbsp;";
	if (tstmp != null) stmp = DateUtility.formatTimestamp(userInfo, tstmp);
	hmtmp.put("mdate",stmp);
	
	alProcs.add(hmtmp);
      }
      rs.close();
      rs = null;
    }
    catch (Exception e) {
    }
    finally {
      Utils.closeDB(db,st,rs);
    }
  }
  else {
    sbError.append("<br>").append(messages.getString("flow_state_procs.error.noInfo"));
  }
}

if (alProcs == null) sbError.append("<br>").append(messages.getString("flow_state_procs.error.noListing"));
else if (alProcs.size() == 0) sbError.append("<br>").append(messages.getString("flow_state_procs.msg.noProcesses"));

String sHtml = null;

if (sbError.length() > 0) {
  sHtml = sbError.toString();
}
else {
  sHtml = messages.getString("flow_state_procs.header.processes", fd.getName(), String.valueOf(state));
}
%>


<form name="flowstates" method="POST">

  <h1 id="title_admin"><%=title%></h1>

<% if (sbError.length() > 0) { %>
  <div class="error_msg">
    <%=sHtml%>
  </div>
<% } %>
  <div class="info_msg">
    <%=sHtml%>
  </div>

  <div class="table_inc">

<% if (alProcs != null && alProcs.size() > 0) { %>
    <table class="list_item"> 
       <tr class="tab_header">
          <td />
          <td />
          <td><%=messages.getString("flow_state_procs.field.process")%></td>
          <td><%=messages.getString("flow_state_procs.field.subprocess")%></td>
          <td><%=messages.getString("flow_state_procs.field.description")%></td>
          <td><%=messages.getString("flow_state_procs.field.start")%></td>
          <td />        
       </tr>
       <% for (int i=0; i < alProcs.size(); i++) {
        hmtmp = alProcs.get(i);
        sSubPid = hmtmp.get("subpid");
        String result = hmtmp.get("result");
        String mdate = hmtmp.get("mdate");
        sPid = hmtmp.get("pid");
        
        if (sPid == null || sPid.equals("")) {
          continue;
        }
        if (sSubPid == null || sSubPid.equals("")) {
          continue;
        }
      %>
        <tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>">
          <td class="itemlist_icon">
            <a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_users.jsp") %>', 'flowid=<%=flowid%>&pid=<%=sPid%>&subpid=<%=sSubPid%>&state=<%=state%>&l=1');">
              <img src="images/icon_user.png" border="0" 
                title="<%=messages.getString("flow_state_procs.link.users")%>" 
                alt="<%=messages.getString("flow_state_procs.link.users")%>">
            </a>
          </td>
          <td class="itemlist_icon">
            <a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_hist.jsp") %>', 'flowid=<%=flowid%>&pid=<%=sPid%>&subpid=<%=sSubPid%>&state=<%=state%>&l=1');">
              <img src="images/icon_history.png" border="0" 
                title="<%=messages.getString("flow_state_procs.link.history")%>" 
                alt="<%=messages.getString("flow_state_procs.link.history")%>">
            </a>
          </td>
          <td><%=sPid%></td>
          <td><%=sSubPid%></td>
          <td><%=result%></td>
          <td><%=mdate%></td>
          <td class="itemlist_icon">
            <a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_cancel.jsp") %>', 'flowid=<%=flowid%>&pid=<%=sPid%>&subpid=<%=sSubPid%>&state=<%=state%>&l=1');">
              <img src="images/icon_delete.png" border="0" 
                title="<%=messages.getString("flow_state_procs.link.cancel")%>" 
                alt="<%=messages.getString("flow_state_procs.link.cancel")%>">
            </a>
          </td>
        </tr>
     <% } %>
       
     </table>  
     <% } %>
  </div>
  <div class="button_box">
      <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" 
        onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/flow_states.jsp") %>', 'ts=<%=ts%>&flowid=<%=flowid%>');"/>
  </div> 
</form>

<if:generateHelpBox context="flow_states"/>
