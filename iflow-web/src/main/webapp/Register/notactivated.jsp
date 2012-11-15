<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
  UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
%>
<%@page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@page import="pt.iflow.api.core.BeanFactory"%>
<%@page import="pt.iflow.api.utils.Const"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><if:message string="register.title.notactivated" /></title>
  <link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_main.css" type="text/css">
  <link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_register.css" type="text/css">
  <link rel="stylesheet" type="text/css" media="all" href="javascript/calendar/calendar-iflow.css" title="cal-iflow" />
  <link rel="shortcut icon" href="images/favicon.ico" />
<!--[if IE]>
  <link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_ie.css" type="text/css">
<![endif]-->
</head>
<body class="rp_body">
	<div class="rc_box">
		<div class="rc_header"></div> 
		<div class="rc_content">
			<form name="formulario" id="formulario" action="<%=response.encodeURL("confirm") %>" method="post">
				<fieldset class=" rp_background">
					<legend><if:message string="register.title.notactivated" /></legend>
					<div class="info_msg  rp_background">
						<p><if:message string="register.notactivated.1"/></p>
						<p><if:message string="register.notactivated.2"/></p> 
						<p><if:message string="register.notactivated.3"/> <a href="mailto:support@iflow.com">support@iflow.com</a></p>
				    </div>
				</fieldset>
				<fieldset class="submit rp_background">
					<input class="regular_button_01" type="submit" name="cancel" value="<if:message string="button.finish"/>"/>
<%--				<input class="regular_button_01" type="submit" name=done value="<if:message string="button.finish"/>"/> --%>
				</fieldset>
			</form>
		</div>
	</div>
</body>
</html>
