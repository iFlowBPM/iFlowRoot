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
<%
	String title = messages.getString("proc_hist.title"); 
String sPage = "Admin/ProcManagement/proc_hist";

StringBuffer sbError = new StringBuffer();
String sESCOLHA = messages.getString("const.choose");

//String sFlowId = null;
//String sPid = null;
//String sSubPid = null;

boolean bLocked = false;
String sLocked = "";
String stmp = fdFormData.getParameter("l");
if (stmp != null && stmp.equals("1")) {
  bLocked = true;
  sLocked = " disabled ";
}

String fromPageParameter = fdFormData.getParameter("show");
boolean fromPage = false;
if (fromPageParameter != null && !fromPageParameter.equals("") && "true".equals(fromPageParameter)){
  fromPage = true;
}

int state = -1;
String sStateParam = null;
sStateParam = fdFormData.getParameter("state");
if (sStateParam != null && !sStateParam.equals("")) {
  try {
    state = Integer.parseInt(sStateParam);
  }
  catch (Exception e) {
    state = -1;
  }
}
else {
  state = -1;
}

int flowid = -1;
try {
  flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
  session.setAttribute(Const.SESSION_ATTRIBUTE_FLOWID, flowid);
} catch(Exception e) {
  try {
    flowid = Integer.parseInt(session.getAttribute(Const.SESSION_ATTRIBUTE_FLOWID).toString());
  } catch(Exception ex) {
    session.setAttribute(Const.SESSION_ATTRIBUTE_FLOWID, -1);
  }
}

int pid = -1;
try {
  pid = Integer.parseInt(fdFormData.getParameter("pid"));
} catch(Exception e) {
  // Not advisable to store pid/subpid in session, so do nothing
}

int subpid = -1;
try {
  subpid = Integer.parseInt(fdFormData.getParameter("subpid"));
} catch(Exception e) {
  // Not advisable to store pid/subpid in session, so do nothing
}
%>
<%@ include file = "../auth.jspf" %>
<%
	String sONGOING = "<div class=\"inline_info_msg\">" + messages.getString("proc_hist.msg.ongoing") + "</div>";
IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlows(userInfo);
if (fda == null) fda = new IFlowData[0];

ArrayList<String> alStates = new ArrayList<String>();
Map<String,Map<String,String>> hmHist = new HashMap<String,Map<String,String>>();
List<Activity> it = new ArrayList<Activity>();
ListIterator<Activity> iterator = null;
if(sbError.length() == 0 && fromPage) {
  if (flowid > 0 && pid > 0) {
    // historico processo
    sONGOING = ProcessPresentation.getProcessHistory(userInfo, sONGOING, alStates, hmHist, flowid, pid, (subpid > 0 ? subpid : 1));
    // historico tarefas pendentes
    it = pm.getProcessActivityHistory(userInfo, flowid, pid, subpid);
  } else if(flowid > 0) {
    iterator = pm.getProcessActivities(userInfo, flowid, pid, subpid);
  }
}
request.setAttribute("it",it);
request.setAttribute("alStates",alStates);
request.setAttribute("hmHist",hmHist);
request.setAttribute("sONGOING",sONGOING);
%>


<form name="prochist" method="POST">
  <input type="hidden" name="state" value="<%=state%>" />
  <input type="hidden" name="show" value="true" />

  <h1 id="title_admin"><%=title%></h1>
      
<% if (sbError.length() > 0) { %>
  <div class="error_msg">
    <%=sbError.toString()%>
  </div>
<% } %>

<% if ((alStates != null && alStates.size() == 0 ) && flowid > 0)  { %>
  <div class="info_msg">
    <if:message string="proc_hist.msg.noStateHistory"/>
  </div>
<% } %>

<% if ((it == null || it.isEmpty()) && flowid > 0 && (iterator == null || !iterator.hasNext())) { %>
  <div class="info_msg">
    <if:message string="proc_hist.msg.noTaskHistory"/>
  </div>
<% } %>

  <fieldset>
    <ol>
      <if:formSelect name="flowid" value='<%=(flowid > -1 ? "" + flowid : "")%>' labelkey="proc_hist.field.flow" edit="<%=!bLocked%>">
        <if:formOption value="-1" label="<%=sESCOLHA%>"/>
<% for (int i=0; i < fda.length; i++) { %>
        <if:formOption value='<%=String.valueOf(fda[i].getId())%>' label="<%=fda[i].getName()%>"/>
<% } %>
      </if:formSelect>
      <if:formInput name="pid" type="text" value='<%=(pid > -1 ? "" + pid : "")%>' labelkey="proc_hist.field.pid" maxlength="10" size="10" edit="<%=!bLocked%>"/>
      <if:formInput name="subpid" type="text" value='<%=(subpid > -1 ? "" + subpid : "")%>' labelkey="proc_hist.field.subpid" maxlength="10" size="10" edit="<%=!bLocked%>"/>
    </ol>
  </fieldset>
    <fieldset class="submit">
<% if (state < 0) { %>
      <input class="regular_button_00" type="button" name="show" value="<if:message string="button.show"/>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_hist.jsp") %>', get_params(document.prochist));"/>
<% } %>
    </fieldset>

<%@ include file = "../../inc/proc_hist_tables.jspf" %>
<% if(iterator != null && iterator.hasNext()) { %>
       <div class="table_inc">  
    <table class="item_list">
      <tr class="tab_header">
        <td><%=messages.getString("proc_cancel.table.pnumber")%></td>
        <td><%=messages.getString("proc_cancel.table.pid")%></td>
        <td><%=messages.getString("proc_cancel.table.subpid")%></td>
        <td><%=messages.getString("proc_cancel.table.creator")%></td>
        <td><%=messages.getString("proc_cancel.table.user")%></td>
      </tr>
      <%
        boolean toggle = false;
      	Activity a = null;
      	// assuring no repetition through this list
      	List<Activity> activities = new ArrayList<Activity>();
        while(iterator.hasNext()) {
          a = iterator.next();
          // check if list contains item
          boolean contains = false;
          for(Activity activity : activities) {
            if(a.flowid == activity.flowid
                && a.pid == activity.pid 
                && a.subpid == activity.subpid) {
              contains = true;
              break;
            }
          }
          if(!contains) {
            activities.add(a);
            ProcessData process = null;
            try {
              boolean procError = false;
              process = pm.getProcessData(userInfo, a.flowid, a.pid, a.subpid, session);
              if (process == null)
                procError = true;
              String params = "flowid=" + a.flowid + "&pid=" + a.pid + "&subpid=" + a.subpid + "&ts=" + ts;
              if(pid > -1 && subpid > -1) {
                params = "flowid=" + a.flowid + "&ts=" + ts;
              }
              params +="&show=true";
              String zoom = "javascript:tabber_right(4, '" + response.encodeURL("Admin/ProcManagement/proc_hist.jsp") + "', '" + params + "');";
      		  String trclazz = (toggle=!toggle)?"tab_row_even":"tab_row_odd";
      		  String clazz = procError ? "class=\"error_msg_cell\"" : "";
      		  
      		  String procPnumber = procError ? a.pnumber : process.getPNumber();
      		  String procPid = String.valueOf(procError ? a.pid : process.getPid());
      		  String procSubPid = String.valueOf(procError ? a.subpid : process.getSubPid());
      		  String procCreator = procError ? "-" : process.getCreator();
      		  String procUser = procError ? a.userid : process.getCurrentUser();
      %>
        <tr class="<%=trclazz%>">
          <td><a <%=clazz%> href="<%=zoom %>"><%=procPnumber %></a></td>
          <td><a <%=clazz%> href="<%=zoom %>"><%=procPid %></a></td>
          <td><a <%=clazz%> href="<%=zoom %>"><%=procSubPid %></a></td>
          <td><a <%=clazz%> href="<%=zoom %>"><%=procCreator %></a></td>
          <td><a <%=clazz%> href="<%=zoom %>"><%=procUser %></a></td>
        </tr>
    <% 
            } catch (Exception e) {
              Logger.errorJsp(login, sPage, "get dataset exception: " + e.getMessage());
            }
          }
    	}
    %>
    </table>
  </div>
<% } %>
<% if (state > 0) { %>
  <div class="button_box">
    <input class="regular_button_01" type="button" name="back" value="<if:message string="button.back"/>" 
      onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/flow_state_procs.jsp") %>', get_params(document.prochist));"/>
  </div>
<% } %>

</form>

<if:generateHelpBox context="proc_hist"/>

