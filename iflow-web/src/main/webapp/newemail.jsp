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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@page import="pt.iflow.api.utils.Utils"%>
<%@page import="pt.iflow.api.core.BeanFactory"%>
<%@page import="pt.iflow.api.core.UserManager"%>
<%@page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@page import="pt.iflow.api.utils.Const"%>
<%
  UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><if:message string="email_confirmation.title" /></title>
  <link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_main.css" type="text/css">
  <link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_register.css" type="text/css">
  <link rel="stylesheet" type="text/css" media="all" href="javascript/calendar/calendar-iflow.css" title="cal-iflow" />
  <link rel="shortcut icon" href="images/favicon.ico" />
<!--[if IE]>
  <link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_ie.css" type="text/css">
<![endif]-->
</head>
<%
int result = BeanFactory.getUserManagerBean().confirmEmailAddress(request.getParameter("action"), request.getParameter("key"));
%>
<body class="rp_body">
	<div class="rc_box">
		<div class="rc_header"></div> 
		<div class="rc_content">
			<form name="formulario" id="formulario" action="<%= response.encodeURL("main.jsp") %>" method="get">
				<fieldset class="rp_background">
					<legend><if:message string="email_confirmation.title" /></legend>
					<div class="info_msg  rp_background">
<% if(result == UserManager.CONFIRM_EMAIL_CONFIRMED) { %>
						<p><if:message string="email_confirmation.confirmed.1"/></p>
						<p><if:message string="email_confirmation.confirmed.2"/></p>
<% } else if(result == UserManager.CONFIRM_EMAIL_REVERTED) { %>
						<p><if:message string="email_confirmation.reverted.1"/></p>
						<p><if:message string="email_confirmation.reverted.2"/></p>
<% } else { %>
						<p><if:message string="email_confirmation.error.1"/></p>
						<p><if:message string="email_confirmation.error.2"/></p>
<% } %>
					</div>
				</fieldset>
				<fieldset class="submit rp_background">
					<input class="regular_button_01" type="submit" id="continues" name="continues" value="<if:message string="button.continue"/>"/>
				</fieldset>
			</form>
		</div>
	</div>
	<div class="lp_footer_nav">
	  <a class="lp_top_nav_link" style="color:#5D7891;" href="http://www.iknow.pt"><if:message string="iflow.copyright"/></a>
    </div>
</body>
</html>
