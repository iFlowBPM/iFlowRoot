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
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %><%
%><%@ page import = "pt.iflow.api.msg.IMessages"
%><%@ page import = "pt.iflow.api.utils.*"
%><%@ page import = "pt.iknow.utils.*"
%><%@ page import = "pt.iflow.api.utils.ServletUtils"
%><%@page import="org.apache.commons.lang.StringUtils"
%><%

UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
if (userInfo == null) {
  ServletUtils.sendEncodeRedirect(response, "login.jsp");
  return;
}

IMessages messages = userInfo.getMessages();
String title = messages.getString("maintenance.title");
String msg = messages.getString("maintenance.message");
String linkMsg = messages.getString("maintenance.linkMessage");


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><%=title %></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<link rel="shortcut icon" href="images/favicon.ico" />
<link rel="stylesheet" href="Themes/default/css/iflow_main.css" type="text/css">
</head>
<body style="width:100%;height:100%;">
	<div class="maintenance">
		<p class="maintenance"><%=msg %></p>             
		<% if (StringUtils.isNotEmpty(linkMsg)) { %>
		<p class="maintenance"><a href="<%= response.encodeURL("main.jsp") %>"><%=linkMsg %></a></p>
		<% } %>
	</div>
</body>
</html>






