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
				<li><a href="FeedServlet?feed=tasksAtom&auth=<%=auth%>"><%=messages.getString("rss.link.tasksAtom")%></a></li>
				<li><a href="FeedServlet?feed=notifications"><%=messages.getString("rss.link.notifications")%></a></li>
		</ul>
	</div>
</div>
</div>



