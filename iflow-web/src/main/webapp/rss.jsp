<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@include file="/inc/defs.jsp"%>


<%
  String sPage = "rss";
UserInfoInterface ui = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
String auth = new String(Base64.encode( (ui.getUtilizador()+":"+ui.getPasswordString()).getBytes() ));
%>

<div class="table_inc">
<h1 id="title_help"><%=messages.getString("rss.title")%></h1>
	<p><%=messages.getString("rss.introMsg")%></p>
<div class="help_box_wrapper">
	<div class="help_box">
		<ul>	
				<li><a href="FeedServlet?feed=categories"><%=messages.getString("rss.link.categories")%></a></li>
				<li><a href="FeedServlet?feed=flows"><%=messages.getString("rss.link.flows")%></a></li>
				<li><a href="FeedServlet?feed=tasks"><%=messages.getString("rss.link.tasks")%></a></li>
				<li><a href="FeedServlet?feed=tasksAtom"><%=messages.getString("rss.link.tasksAtom")%></a></li>
				<li><a href="FeedServlet?feed=notifications"><%=messages.getString("rss.link.notifications")%></a></li>
		</ul>
	</div>
</div>
</div>



