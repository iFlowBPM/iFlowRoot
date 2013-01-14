<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../../inc/defs.jsp" %>

<%
	String title = messages.getString("proc_users.title");
String sPage = "Admin/ProcManagement/proc_users";

StringBuffer sbError = new StringBuffer();
String sESCOLHA = messages.getString("const.choose");

boolean bLocked = false;
String sLocked = "";
String stmp = fdFormData.getParameter("l");
if (stmp != null && stmp.equals("1")) {
  bLocked = true;
  sLocked = " disabled ";
}


int state = -1;
String sState = null;
sState = fdFormData.getParameter("state");
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
  subpid = 1;
}

String fromPageParameter = fdFormData.getParameter("show");
boolean fromPage = false;
if (fromPageParameter != null && !fromPageParameter.equals("") && "true".equals(fromPageParameter)){
  fromPage = true;
}
%>
<%@ include file = "../auth.jspf" %>
<%


Flow flow = BeanFactory.getFlowBean();

List<String> altmp = null;
List<String> altmp2 = null;
String sFlowHtml = "";

if (flow != null) {
  
  IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlows(userInfo);

  if (fda == null) fda = new IFlowData[0];

  altmp = new ArrayList<String>();
  altmp2 = new ArrayList<String>();

  altmp.add("-1");
  altmp2.add(sESCOLHA);

  for (int i=0; i < fda.length; i++) {
    altmp.add(String.valueOf(fda[i].getId()));
    altmp2.add(fda[i].getName());
  }

  
  sFlowHtml = Utils.genHtmlSelect("flowid",
				  sLocked,
				  "" + flowid,
				  altmp,
				  altmp2);
}


ListIterator<Activity> it = null;
Activity a = null;
boolean bEmpty = true;
if (sbError.length() == 0 && flowid > 0 && fromPage) {
  // get activities
  it = pm.getProcessActivities(userInfo, flowid, pid, subpid);
}
String sParamsAdd = "";
if (sbError.length() == 0 && flowid > 0 && pid > 0 && subpid > 0) {
  sParamsAdd = "flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid + "&a=a&ts=" + ts + "&show=true";
}
%>


<form name="procusers" method="POST">
  <input type="hidden" name="state" value="<%=state%>" />
  <input type="hidden" name="show" value="true" />

  <h1 id="title_admin"><%=title%></h1>
      
<% if ((it == null || !it.hasNext()) && flowid > 0) { %>
  <div class="info_msg">
    <%=messages.getString("proc_users.error.noUsersForProc")%>
  </div>
<% } %>

  <fieldset>
    <ol>
      <li>
        <label for="flowid"><%=messages.getString("proc_users.field.flow") %></label>
        <%=sFlowHtml%>
      </li>
      <li>
        <label for="pid"><%=messages.getString("proc_users.field.pid") %></label>
        <input type="text" name="pid" value="<%=(pid > -1 ? pid : "") %>" size="10" maxlength="10" <%=sLocked%>>
      </li>
      <li>
        <label for="subpid"><%=messages.getString("proc_users.field.subpid") %></label>
        <input type="text" name="subpid" value="<%=(subpid > -1 ? subpid : "") %>" size="10" maxlength="10" <%=sLocked%>>
      </li>
    </ol>
  </fieldset>
  <fieldset class="submit">
<% if (state < 0) { %>
      <input class="regular_button_00" type="button" name="show" value="<%=messages.getString("button.show")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_users.jsp")%>', get_params(document.procusers));"/>
<% } %>
  </fieldset>
  
 
<% if (it != null && it.hasNext()) { %>
  <div class="table_inc">  
    <table class="item_list">
      <tr class="tab_header">
        <td />
        <td><%=messages.getString("proc_users.field.pnumber")%></td>
        <td><%=messages.getString("proc_users.field.pidTag")%></td>
        <td><%=messages.getString("proc_users.field.subpidTag")%></td>
        <td><%=messages.getString("proc_users.field.user")%></td>
        <td><%=messages.getString("proc_users.field.start")%></td>
        <td><%=messages.getString("proc_users.field.description")%></td>
        <td />
      </tr>

      <%
        boolean toggle = false;
        while(it.hasNext()) {
          a = it.next();
          bEmpty = false;
          String params = "";
          params = "flowid=" + a.flowid + "&pid=" + a.pid + "&subpid=" + a.subpid + "&ts=" + ts;
          String sParamsEdit = "uid=" + a.userid + "&a=e&" + params + "&show=true";
          String sParamsDelete = "uid=" + a.userid + "&a=d&" + params + "&show=true";
          if(pid > -1 && subpid > -1) {
            params = "flowid=" + a.flowid + "&ts=" + ts;
          }
          params += "&show=true";

          String zoom = "javascript:tabber_right(4, '" + response.encodeURL("Admin/ProcManagement/proc_users.jsp") + "', '" + params + "');";
      %>
        <tr class="<%=(toggle=!toggle)?"tab_row_even":"tab_row_odd"%>">
          <td class="itemlist_icon">
            <a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_users_edit.jsp")%>', '<%=sParamsEdit%>');"><img src="images/icon_modify.png" border="0" title="Modify" alt="Modify"></a>
          </td>
          <td><a href="<%=zoom %>"><%=pm.getProcessNumber(userInfo, a.flowid, a.pid)%></a></td>
          <td><a href="<%=zoom %>"><%=a.pid%></a></td>
          <td><a href="<%=zoom %>"><%=a.subpid%></a></td>
          <td><a href="<%=zoom %>"><%=a.userid%></a></td>
          <td><a href="<%=zoom %>"><%=DateUtility.formatTimestamp(userInfo, a.created)%></a></td>
          <td><a href="<%=zoom %>"><%=a.description%></a></td>
          <td class="itemlist_icon">
            <a href="javascript:if (confirm('<%=messages.getString("proc_users.confirm.delete")%>'))tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_users_edit.jsp")%>', '<%=sParamsDelete%>');"><img src="images/icon_delete.png" border="0" title="Delete" alt="Delete"></a>
          </td>
        </tr>
    <% } %>
    </table>
  </div>
<% } %>
     

<% if (state > 0 || !bEmpty) { %>
  <div class="button_box">
    <% if (state > 0) { %>
 	  <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" 
      onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/flow_state_procs.jsp")%>', get_params(document.procusers));"/>
    <% } %>
    <% if (!bEmpty && flowid > 0 && pid > 0 && subpid > 0) { %>
    <input class="regular_button_00" type="button" name="add" value="<%=messages.getString("button.add")%>" 
      onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_users_edit.jsp")%>', '<%=sParamsAdd%>');"/>
    <% } %>            
  </div>
<% } %>            

</form>

<if:generateHelpBox context="proc_users"/>
