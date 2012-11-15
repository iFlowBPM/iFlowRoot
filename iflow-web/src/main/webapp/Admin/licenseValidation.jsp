<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import = "pt.iknow.utils.security.SecurityWrapper"%>
<%@ page import = "pt.iknow.utils.security.LibraryLoader"%>
<%@ page import = "pt.iflow.api.utils.ServletUtils" %>
<%@ page import = "pt.iflow.api.utils.Const" %>
<%@ page import = "pt.iflow.api.utils.UserInfoInterface" %>
<%@ page import = "pt.iflow.api.core.BeanFactory"%>
<%
	boolean isLoaded = LibraryLoader.isLoaded();
int status = SecurityWrapper.getLicenseStatus();
String libpath = System.getProperty("iflow.home");
String fileName = LibraryLoader.getLibraryFileName();
UserInfoInterface admin = (UserInfoInterface)session.getAttribute(Const.USER_INFO);
if(null == admin) {
  // user not authenticated. redirect
	ServletUtils.sendEncodeRedirect(response, "login.jsp");
	return;
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
  UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><if:message string="licenseValidation.title" /></title>
		<link rel="stylesheet" href="../Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_main.css" type="text/css">
		<link rel="stylesheet" href="../Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_register.css" type="text/css">
		<link rel="shortcut icon" href="../images/favicon.ico" />
		<!--[if IE]>
			<link rel="stylesheet" href="../Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName() %>/css/iflow_ie.css" type="text/css">
		<![endif]-->
		<style type="text/css">
		div.info_msg {
			text-align: left;
		}
		.mono {
			font-family: monospace;
			font-size: 12px;
/*			font-weight: bold;*/
		}
		</style>
	</head>
	<body class="rp_body">
		<div class="rc_box">
			<div class="rc_header"></div> 
			<div class="rc_content">
				<form name="fomulario" id="formulario" action="<%=response.encodeURL("../updateLicense") %>" method="post" enctype="multipart/form-data">
					<fieldset class="rp_background">
						<legend><if:message string="licenseValidation.title" /></legend> 
						<div class="info_msg">
<%
if(isLoaded) {

	switch (status) {
	
		case SecurityWrapper.LIC_LOADED:
  							%>
						<if:message string="licenseValidation.error.license_ok"/>
  							<%
		break;
	
		case SecurityWrapper.LIC_MISSING_FILE:
  							%>
						<if:message string="licenseValidation.error.file_missing"/>
  							<%
		break;
	
		case SecurityWrapper.LIC_INVALID_FILE:
		case SecurityWrapper.LIC_INVALID_TIMESTAMP:
  							%>
						<if:message string="licenseValidation.error.corrupted_file"/>
  							<%
		break;
  
		case SecurityWrapper.LIC_INVALID_IP:
		case SecurityWrapper.LIC_INVALID_MAC:
  							%>
						<if:message string="licenseValidation.error.invalid_license"/>
  							<%
		break;
  
		case SecurityWrapper.LIC_EXPIRED:
  							%>
						<if:message string="licenseValidation.error.expired"/>
  							<%
		break;
  
		case SecurityWrapper.LIC_NOT_LOADED:
		default:
  							%>
						<if:message string="licenseValidation.error.license_not_loaded"/>
  							<%
		break;
	}
	
	if(status != SecurityWrapper.LIC_LOADED) {
	%>
	<p>&nbsp;</p>
	<p style="text-align:right"><if:message string="licenseValidation.label.upload"/></p>
	<p style="text-align:right"><input type="file" name="licfile" onchange="this.form.submit();"></p>
	<%
	}
	
 } else { %>
						<if:message string="licenseValidation.error.dll_not_loaded" param="<%=fileName%>"/>
<% } %>
</div>
					</fieldset>
					<fieldset class="rp_background" style="text-align:left">
						<p class="info_msg" style="padding-left:0px;"><if:message string="licenseValidation.label.sysinfo"/></p>
						<p><if:message string="licenseValidation.version.jar" param="<%= LibraryLoader.getVersion() %>"/></p>
<% if(isLoaded) { %>
						<p><if:message string="licenseValidation.version.dll" param="<%= LibraryLoader.getDLLVersion() %>"/></p>
<% } %>
						<p>iflow.home: <span class="mono"><%=libpath %></span></p>
					</fieldset>
					<fieldset class="submit rp_background">
						<input class="regular_button_01" type="submit" name="admin" value="<if:message string="licenseValidation.button.admin"/>"/>
					</fieldset>
				</form>
			</div> 
		</div>
	<div class="lp_footer_nav">
	  <a class="lp_top_nav_link" style="color:#5D7891;" href="http://www.iknow.pt"><if:message string="iflow.copyright"/></a>
    </div>
		
	</body>
</html>