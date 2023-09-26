<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file="checkFull.jsp"%>
<% if(!isSystemAdmin.booleanValue()) { %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
  UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
%>

<%@page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@page import="pt.iflow.api.core.BeanFactory"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><if:message string="register.title.success" /></title>
  <link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_main.css" type="text/css">
  <link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_register.css" type="text/css">
  <link rel="stylesheet" type="text/css" media="all" href="javascript/calendar/calendar-iflow.css" title="cal-iflow" />
  <link rel="shortcut icon" href="images/favicon.ico" />
<!--[if IE]>
  <link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_ie.css" type="text/css">
<![endif]-->
</head>
<body class="rp_body">
	<div class="rp_fixed_width">
		<if:titlebubble message="register.title" floatpos="left" transparent="false"/>
		<if:titlebubble message="register.title.step.3" floatpos="right" transparent="false" marginpos="left"/>
		<if:titlebubble message="register.title.step.2" floatpos="right" transparent="true" marginpos="left"/>
		<if:titlebubble message="register.title.step.1" floatpos="right" transparent="true"/>
	</div>
	<div class="rc_box">
		<div class="rc_header"></div> 
		<div class="rc_content">
<% } else { %>

      <h1><if:message string="register.title" /></h1>
      <div class="table_inc">  
<% } %>
			<form name="formulario" id="formulario" action="<%=response.encodeURL("register") %>" method="post">
				<fieldset class="rp_background">
					<legend><if:message string="register.title.success" /></legend>
					<div class="info_msg  rp_background">
						<p><if:message string="register.success.1"/></p>
<% if(Const.bUSE_EMAIL) { %>
						<p><if:message string="register.success.2" param="${emailAddress}"/></p>
						<p><if:message string="register.success.3"/></p>
<% } else { %>
						<p><if:message string="register.success.4"/></p>
<% } %>
					</div>
				</fieldset>
				<fieldset class="submit rp_background">
					<c:choose>
						<c:when test="${isSystemAdmin}">
							<input class="regular_button_01" type="button" name="done" value="<if:message string="button.finish"/>"
								onclick="javascript:tabber_right(4, '<%=response.encodeURL("register") %>','done=done');" />
						</c:when>
						<c:otherwise>
							<input class="regular_button_01" type="submit" name=done value="<if:message string="button.finish"/>"/>
						</c:otherwise>
					</c:choose>
				</fieldset>
			</form>
		</div>
<% if(!isSystemAdmin.booleanValue()) { %>
	</div>
	<div class="lp_footer_nav">
	  <a class="lp_top_nav_link" style="color:#5D7891;" href="https://www.uniksystem.com"><if:message string="iflow.copyright"/></a>
    </div>
</body>
</html>
<% } %>
