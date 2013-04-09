<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%
	String title = messages.getString("actividade_user.title"); 

int pid = -1;
int flowid = -1;
try {
  flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
  pid = Integer.parseInt(fdFormData.getParameter("pid"));
}
catch (Exception e) {
  flowid = -1;
  pid = -1;
}

if (flowid > -1 && pid > -1) {
  Iterator<Activity> it = pm.getUserActivities(userInfo);
  Activity a;

  while(it != null && it.hasNext()) {
    a = it.next();
    if (flowid == a.flowid && pid == a.pid) {
      ServletUtils.sendEncodeRedirect(response, a.url);
      return;
    }
  }
}
%>

<div class="error_msg">
  <%=messages.getString("actividade_user.error")%>
</div>
