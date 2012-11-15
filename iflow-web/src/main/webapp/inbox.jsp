<%@page import="pt.iflow.api.notification.Notification"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="inc/defs.jsp"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="pt.iflow.api.notification.*"%>

<%
	final int nNOTIFICATION_LIMIT = 100;

Hashtable<String,Object> hsSubstLocal = new Hashtable<String,Object>();
String title = messages.getString("inbox.title");
try {
	// prepare notification data
	Collection<Notification> notifications = BeanFactory.getNotificationManagerBean().listAllNotifications(userInfo);
	Collection<Map<String,String>> notes = new ArrayList<Map<String,String>>();
	int n = 0;
	for(Notification notification : notifications) {
		if(n >= nNOTIFICATION_LIMIT) break;
		++n;
		Map<String,String> note = new HashMap<String,String>();
		note.put("id", String.valueOf(notification.getId()));
		note.put("from", notification.getSender());
		note.put("date", DateUtility.formatTimestamp(userInfo, notification.getCreated()));
		note.put("message", StringEscapeUtils.escapeHtml(notification.getMessage()));
		note.put("read", String.valueOf(notification.isRead()));
		
		String href = "";
		
		String [] dadosproc = notification.getLink().split(",");
		
		int procid = -1; 
		
		if(dadosproc.length > 1)
			procid = Integer.parseInt(dadosproc[1]);
		
		if(notification.getLink().equals("false") || procid<=0)
			href =  "false";
		else
			href =  "8, \'user_proc_detail.jsp\'," + notification.getLink()+",-3";
		
		
		note.put("link",href);
		notes.add(note);
	}
	hsSubstLocal.put("notifications", notes);
	hsSubstLocal.put("hasMoreNotifications", notifications.size()>nNOTIFICATION_LIMIT);
	
	hsSubstLocal.put("notificationtitle", messages.getString("inbox.notificationtitle"));
	hsSubstLocal.put("notificationitem", messages.getString("inbox.notificationitem"));
	
	// messages...
	hsSubstLocal.put("notes_empty", messages.getString("inbox.empty"));
	hsSubstLocal.put("notes_from", messages.getString("inbox.field.from"));
	hsSubstLocal.put("notes_date", messages.getString("inbox.field.date"));
	hsSubstLocal.put("notes_message", messages.getString("inbox.field.message"));
	hsSubstLocal.put("tooltip_mark_read", messages.getString("inbox.tooltips.mark_read"));
	hsSubstLocal.put("tooltip_delete", messages.getString("inbox.tooltips.delete"));
	hsSubstLocal.put("notes_more", messages.getString("inbox.hasMore"));     
}
catch (Exception e) {
	Logger.errorJsp(login, "inbox.jsp", "exception: " + e.getMessage(), e);
}

hsSubstLocal.put("title", title);
hsSubstLocal.put("ts", java.lang.Long.toString(ts));
hsSubstLocal.put("url_prefix", sURL_PREFIX.substring(0, sURL_PREFIX.length() - 1));
hsSubstLocal.put("css", css);

out.println(PresentationManager.buildPage(response, userInfo, hsSubstLocal, "inbox"));
%>
