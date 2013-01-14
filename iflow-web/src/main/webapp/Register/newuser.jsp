<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ include file="checkFull.jsp"%>
<%if(!isSystemAdmin.booleanValue()) { %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
  UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
%>

<%@page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@page import="pt.iflow.api.core.BeanFactory"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><if:message string="register.title.user" /></title>
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
			<if:titlebubble message="register.title.step.3" floatpos="right" transparent="true" marginpos="left"/>
			<if:titlebubble message="register.title.step.2" floatpos="right" transparent="false" marginpos="left"/>
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
			<input type="hidden" name="step" value="user" />
			<input type="hidden" name="full" value="<%= bIsFull %>" />
			
		  	<fieldset class="rp_background">
		  		<legend><if:message string="register.title.user" /></legend>
<% if(!isSystemAdmin.booleanValue()) { %>
		  		<div class="info_msg rp_background" style="text-align:left;">
					<if:message string="register.intro.user" />
				</div>
<% } %>
				<c:if test="${not empty error_msg}">
					<div class="error_msg rp_background">
						<c:out value="${error_msg}" escapeXml="false" />
					</div>
				</c:if>
		  		
			    <ol>
					<if:formInput type="text" name="username" value="${username}" labelkey="register.label.username" edit="true" required="true" maxlength="100" />
					<c:if test="${(not bUseEmail and isSystemAdmin) or (not isSystemAdmin)}">
					<if:formInput type="password" name="pass" value="" labelkey="register.label.password" edit="true" required="true" maxlength="125" />
					<if:formInput type="password" name="repeatpass" value="" labelkey="register.label.repeatpass" edit="true" required="true" maxlength="125" />
					</c:if>
					<if:formInput type="text" name="emailAddress" value="${emailAddress}" labelkey="register.label.emailaddress" edit="true" required="<%=Const.bUSE_EMAIL%>" maxlength="100" />
					<if:formInput type="text" name="firstName" value="${firstName}" labelkey="register.label.firstname" edit="true" required="true" maxlength="50" />
					<if:formInput type="text" name="lastName" value="${lastName}" labelkey="register.label.lastname" edit="true" required="true" maxlength="50" />
					<if:formSelect name="gender" value="${gender}" labelkey="register.label.gender" edit="true" >
						<if:formOption value="M" labelkey="register.label.gender.male" />
						<if:formOption value="F" labelkey="register.label.gender.female" />
					</if:formSelect>
					<if:formInput type="text" name="phoneNumber" value="${phoneNumber}" labelkey="register.label.phoneNumber" edit="true" maxlength="20" />
					<if:formInput type="text" name="faxNumber" value="${faxNumber}" labelkey="register.label.faxNumber" edit="true" maxlength="20" />
					<if:formInput type="text" name="mobileNumber" value="${mobileNumber}" labelkey="register.label.mobileNumber" edit="true" maxlength="20" />
					<if:formInput type="text" name="companyPhone" value="${companyPhone}" labelkey="register.label.companyPhone" edit="true" maxlength="20" />
					<li>&nbsp;</li>
					<c:if test="${not isSystemAdmin}">
						<if:formInput type="challenge" name="challenge" value="" label="Challenge" edit="true" required="true" />
					</c:if>
				</ol>
			</fieldset>
		    <fieldset class="submit rp_background">
				<c:choose>
					<c:when test="${isSystemAdmin}">
						<input class="regular_button_01" type="button" name="cancel" value="<if:message string="button.cancel"/>"
							onclick="javascript:tabber_right(4, '<%=response.encodeURL("register") %>','cancel=cancel');" />
				    	<input class="regular_button_01" type="button" name="back" value="<if:message string="button.back"/>"
							onclick="javascript:tabber_right(4, '<%=response.encodeURL("register") %>','back=back&'+get_params(document.formulario));" />
						<input class="regular_button_01" type="reset" name="clear" value="<if:message string="button.clear"/>"/>
						<input class="regular_button_01" type="button" name="add" value="<if:message string="button.next"/>"
							onclick="javascript:tabber_right(4, '<%=response.encodeURL("register") %>','create=create&'+get_params(document.formulario));" />
					</c:when>
					<c:otherwise>
						<input class="regular_button_01" type="submit" name="cancel" value="<if:message string="button.cancel"/>"/> 
				    	<input class="regular_button_01" type="submit" name="back" value="<if:message string="button.back"/>" />
				    	<input class="regular_button_01" type="reset"  name="clear" value="<if:message string="button.clear"/>" />
				    	<input class="regular_button_01" type="submit" name="create" value="<if:message string="button.create"/>"/>
					</c:otherwise>
				</c:choose>
			</fieldset>
		</form>
	</div>
<% if(!isSystemAdmin.booleanValue()) { %>
	<div class="lp_footer_nav">
	  <a class="lp_top_nav_link" style="color:#5D7891;" href="http://www.infosistema.pt"><if:message string="iflow.copyright"/></a>
    </div>
</body>
</html>
<% } %>
