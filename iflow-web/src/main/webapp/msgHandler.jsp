<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%@ page import="pt.iflow.api.notification.NotificationManager" %>
<%
boolean success = false;
int count = 0;
int id = -1;
String action = "";
try {
  id = Integer.parseInt(fdFormData.getParameter("id"));
  action = fdFormData.getParameter("action");
  switch(action.charAt(0)) {
  case 'C': // Count messages
  	success = true;
  	break;
  case 'M': // Mark message read (dashboard)
  case 'R': // Mark message read
    success = BeanFactory.getNotificationManagerBean().markMessageRead(userInfo, id) == NotificationManager.NOTIFICATION_OK;
    break;
  case 'U': // Unmark message read
    success = BeanFactory.getNotificationManagerBean().markMessageNew(userInfo, id) == NotificationManager.NOTIFICATION_OK;
    break;
  case 'D': // Delete message
    success = BeanFactory.getNotificationManagerBean().deleteMessage(userInfo, id) == NotificationManager.NOTIFICATION_OK;
    break;
  default:
    success=false;
    break;
  }

  count = BeanFactory.getNotificationManagerBean().countNewMessages(userInfo);
  if(action.charAt(0)=='C') success=(count!=-1);
  if(count == -1) count = 0;

} catch(Throwable t) {
  Logger.errorJsp(userInfo.getUtilizador(), "msgHandler.jsp", "Error occurred.", t);
  success=false;
}
// now prepare the json string
%>{success:<%=success%>,id:<%=id%>,action:"<%=StringEscapeUtils.escapeJavaScript(action)%>",count:<%=count%>}