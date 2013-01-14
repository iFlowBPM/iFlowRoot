<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%

String sFlowId = fdFormData.getParameter("flowid");
int flowid = Integer.parseInt(sFlowId);
String sPid = fdFormData.getParameter("pid");
int pid = Integer.parseInt(sPid);
String sSubPid = fdFormData.getParameter("subpid");
int subpid = Integer.parseInt(sSubPid);
String status = fdFormData.getParameter("procStatus");
boolean fromNotification = false;
if(status.equals("-2")) fromNotification = true; 

ArrayList<String> alStates = new ArrayList<String>();
Map<String,Map<String,String>> hmHist = new HashMap<String,Map<String,String>>();
List<Activity> it = null;
StringBuffer sbError = new StringBuffer();

String sONGOING = "";

if (flowid > 0 && pid > 0 && subpid > 0) {
  // historico processo
  sONGOING = ProcessPresentation.getProcessHistory(userInfo, sONGOING, alStates, hmHist, flowid, pid, subpid);
  // historico tarefas pendentes
  it = pm.getProcessActivityHistory(userInfo, flowid, pid, subpid);
} else {
  sbError.append(messages.getString("user_proc_detail.error.noprocess"));
}
request.setAttribute("it",it);
request.setAttribute("alStates",alStates);
request.setAttribute("hmHist",hmHist);
request.setAttribute("sONGOING",sONGOING);

boolean showButton = false;

try {
  IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, Integer.parseInt(sFlowId));
  boolean val = false;
  if(fd != null && fd.hasDetail()) {
    if(fd.getDetailForm() != null) val = true;
    else if(fd.getCatalogue()!= null) val = true;
  }
  boolean view = pm.canViewProcess(userInfo, new ProcessHeader(Integer.parseInt(sFlowId),Integer.parseInt(sPid),Integer.parseInt(sSubPid)));
  showButton = val && view;
} catch (Throwable t) {}



%>
<form name="userproctasksForm" method="POST">
<input type="hidden" name="flowid" value="<%=sFlowId%>">
<input type="hidden" name="pid" value="<%=sPid%>">
<input type="hidden" name="subpid" value="<%=sSubPid%>">
<input type="hidden" name="procStatus" value="<%=status%>">

  <div style="vertical-align: middle;">
    <img src="images/icon_tab_tarefas.png" class="icon_item"/>
    <h1><if:message string="user_proc_detail.title"/></h1>
  </div>
      
<% if (sbError.length() > 0) { %>
  <div class="error_msg">
    <%=sbError.toString()%>
  </div>
<% } else if ((it == null || it.isEmpty()) && flowid > 0) { %>
<div class="info_msg">
  <if:message string="proc_hist.msg.noTaskHistory"/>
</div>
<% } else { %>
<%@ include file = "inc/proc_hist_tables.jspf" %>
<% } %>

  <fieldset class="submit">
  <% if(!fromNotification){ %>
    <input class="regular_button_01" type="button" name="back" value="<if:message string="button.back"/>" 
    onClick="javascript:tabber_right(8, '<%=response.encodeURL("user_procs.jsp")%>', get_params(document.user_procs_filter));"/>
<% } if(showButton) { %>
    <input class="regular_button_01" type="button" name="proc_detail" value="<if:message string="button.proc_detail"/>" 
    onClick="javascript:tabber_right(8, '<%=response.encodeURL("user_proc_detail.jsp")%>', get_params(document.userproctasksForm));"/>
<% } %>
  </fieldset>

</form>
