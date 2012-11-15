<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file="checkFull.jsp"%>
<% if(!isSystemAdmin) { %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
  UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
%>
<%@page import="pt.iflow.api.core.BeanFactory"%>
<%@page import="pt.iflow.api.utils.UserInfoInterface"%><html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><if:message string="register.title.organization" /></title>
		<link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_main.css" type="text/css">
		<link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_register.css" type="text/css">
		<link rel="shortcut icon" href="images/favicon.ico" />
		<!--[if IE]>
			<link rel="stylesheet" href="Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_ie.css" type="text/css">
		<![endif]-->
	</head>
	<body class="rp_body">
		<div class="rp_fixed_width">
			<if:titlebubble message="register.title" floatpos="left" transparent="false"/>
			<if:titlebubble message="register.title.step.3" floatpos="right" transparent="true" marginpos="left"/>
			<if:titlebubble message="register.title.step.2" floatpos="right" transparent="true" marginpos="left"/>
			<if:titlebubble message="register.title.step.1" floatpos="right" transparent="false"/>
		</div>
    
		<div class="rc_box">
			<div class="rc_header"></div> 
			<div class="rc_content">
<% } else { %>

      <h1 id="title_admin"><if:message string="register.title" /></h1>
      <div class="table_inc">  
<% } %>
				<form name="formulario" id="formulario" action="<%=response.encodeURL("register") %>" method="post">
					<input type="hidden" name="step" value="org" />
					<input type="hidden" name="full" value="<%= bIsFull %>" />
					
					<fieldset class="rp_background">
						<legend><if:message string="register.title.organization" /></legend>
<% if(!isSystemAdmin) { %>
						<div class="info_msg rp_background" style="text-align:left;">
							<if:message string="register.intro.organization" />
						</div>
<% } %>
						<c:if test="${not empty error_msg}">
							<div class="error_msg rp_background">
								<c:out value="${error_msg}" escapeXml="false"/>
							</div>
						</c:if>
					
						<ol>
							<if:formInput type="text" name="organization_name" value="${organization_name}" labelkey="register.label.organization_name" edit="true" required="true" maxlength="50"/>
							<if:formInput type="text" name="organization_desc" value="${organization_desc}" labelkey="register.label.organization_desc" edit="true" maxlength="150"/>
							<if:formLocale name="organization_lang" edit="true" value="${organization_lang}" labelkey="register.label.organization_lang" />
							<if:formTimeZone name="organization_timezone" edit="true" value="${organization_timezone}" labelkey="register.label.organization_timezone" />
						</ol>
					</fieldset>
					<fieldset class="submit rp_background">
					<c:choose>
						<c:when test="${isSystemAdmin}">
							<input class="regular_button_01" type="button" name="cancel" value="<if:message string="button.cancel"/>"
								onclick="javascript:tabber_right(4, '<%=response.encodeURL("register") %>','cancel=cancel');" />
							<input class="regular_button_01" type="reset" name="clear" value="<if:message string="button.clear"/>"/>
							<input class="regular_button_01" type="button" name="add" value="<if:message string="button.next"/>"
								onclick="javascript:tabber_right(4, '<%=response.encodeURL("register") %>','add=add&'+get_params(document.formulario));" />
						</c:when>
						<c:otherwise>
							<input class="regular_button_01" type="submit" name="cancel" value="<if:message string="button.cancel"/>"/>
							<input class="regular_button_01" type="reset" name="clear" value="<if:message string="button.clear"/>"/>
							<input class="regular_button_01" type="submit" name="add" value="<if:message string="button.next"/>"/>
						</c:otherwise>
					</c:choose>
					</fieldset>
				</form>
			</div>
<% if(!isSystemAdmin) { %>
		</div>
		<div class="lp_footer_nav">
		  <a class="lp_top_nav_link" style="color:#5D7891;" href="http://www.infosistema.pt"><if:message string="iflow.copyright"/></a>
	    </div>
	</body>
</html>
<% } %>
