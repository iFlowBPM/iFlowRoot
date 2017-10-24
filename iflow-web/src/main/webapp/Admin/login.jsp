<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import = "pt.iflow.core.*" %>
<%@ page import = "pt.iflow.servlets.AuthenticationServlet" %>
<%@ page import = "pt.iflow.api.utils.*" %>
<%@ page import = "pt.iflow.api.msg.IMessages" %>
<%@ page import = "java.net.URLEncoder" %>
<%@ page import = "java.util.Date" %>
<%@ page import = "java.util.Hashtable"%>
<%@ page import = "java.util.Locale"%>
<%@ page import = "pt.iflow.api.licensing.LicenseServiceFactory"%>
<%@ page import = "pt.iflow.api.licensing.LicenseService"%>
<%@ page import = "org.apache.commons.lang.*" %>
<%@ page import = "pt.iflow.api.core.BeanFactory"%>
<%@ page import = "pt.iflow.api.core.Settings"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<%
	Date urldate = new Date();
long ts = urldate.getTime();

String sFocus = "login";

String login = request.getParameter("login");

if (login == null) {
    login = "";
}
else {
  sFocus = "password";
}


String url = (String)session.getAttribute("URL");
if (url == null) {
  url = request.getParameter("url");
}
else {
  session.removeAttribute("URL");
}

if (url == null) {
  url = "main.jsp";
}
else {
  url = response.encodeRedirectURL(url);
}


String ufid = request.getParameter(Const.sUSER_FLOWID);
if(ufid == null ){
  ufid = "";
}

boolean bError = false;
String sLoginError = (String)session.getAttribute("login_error");
if (StringUtils.isNotEmpty(sLoginError)) {
	bError = true;
    session.invalidate();
}

Hashtable<String,String> cookies = ServletUtils.getCookies(request);
String lang = cookies.get(Const.LANG_COOKIE);
if(StringUtils.isEmpty(lang)) {
  Locale loc = Locale.getDefault();
  lang = loc.getLanguage()+"_"+loc.getCountry();
}

Cookie cookie = ServletUtils.newCookie(Const.LANG_COOKIE, lang);
response.addCookie(cookie);

session.setAttribute(AuthenticationServlet.ADMIN_SESSION_NAME, Boolean.TRUE);

//first of all, check license status.
boolean notifyInvalid = !LicenseServiceFactory.getLicenseService().isLicenseOK();

boolean isInstallLocal = Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE);
boolean isInstallDemo = Const.INSTALL_DEMO.equals(Const.INSTALL_TYPE);
boolean useEmail = Const.bUSE_EMAIL;


boolean isInMaintenance = Const.isInMaintenance();
String maintenance = Const.isInMaintenance() ? "maintenance" : "";


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<head>

	<title>iFlow Admin Login</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="Pragma" content="no-cache"/>
	
	<link href="../Themes/newflow/cssNew/style_login.css" rel="stylesheet" type="text/css" >
	<link href="../javascript/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css">
    <link rel="shortcut icon" href="../images/favicon.ico" />

    <script type="text/javascript" src="../javascript/messages.js"> </script><!-- default messages -->
    <script type="text/javascript" src="../javascript/messages_<%=lang%>.js"> </script><!-- default messages -->
    <script type="text/javascript" src="../javascript/mootools.js"></script>
    <script type="text/javascript" src="../javascript/ajax_processing.js"></script>
    <script type="text/javascript" src="../javascript/iflow_main.js"></script>

	<script type="text/javascript">
      window.addEvent('domready', function(e) { // register events
          // Refresh without cache link
          $('password').addEvent('keypress', function (e) {
              var elem = $('capsWarn');
              if(!elem) return;
              if(isCapslock(e)) elem.setStyle('display','inline');
              else elem.setStyle('display','none');
          });
          $('login').addEvent('keypress', getEnterKeyHandler(nextField('password')));
          $('password').addEvent('keypress', getEnterKeyHandler(submitForm('dados')));
          $('<%=sFocus%>').focus();
      });
	</script>
</head>

<body style="background: #e2e4eb">

<form name="dados" id='dados' method="post" action="<%= response.encodeURL("../AuthenticationServlet") %>" class="form-signin">
	<input type="hidden" name="url" value=${fn:escapeXml(url)}/>
	<input type="hidden" name="<%=Const.sUSER_FLOWID%>" value=${fn:escapeXml(ufid)}/>
	<input type="hidden" name="source" value="internet"/>
	<input type="hidden" name="do_redirect" value="true"/>
	<input type="hidden" name="url" value="../main.jsp"/>

<div class="login_bartop"> 
				<%if (!isInstallLocal)  {%>
				
				<div class="loginbar_left"><a class="linklogin" href="../login.jsp"><if:message string="login.link.login"/></a>
				<span style="visibility:hidden;">|</span> <a href="register"  class="linklogin"></a> 
				</div>
				 				
				<%} %>
				</div>
				
<div class="bos_login" style="background:beige;margin-top:50px;width:460px;">

<div class="alert alert-info" style="text-align:center;width: 75%;padding: 4px;height:30px;margin: 10px 4rem;background-color: gray;color:white;font-weight: bold;"><if:message string="login.admin.title"/></div>

    <div class="img"> <img src="../Themes/newflow/images/iflowbpm_logo.png" width="196" height="68" alt=""/></div>
    
	<div class="error_msg lp_error_msg">
			       <% if(bError) { %>
        			<%=sLoginError%>
						<% } else if(notifyInvalid){ %>
					<if:message string="admin.error.license"/>
					<% } %>
	</div>
    <input type="text" name="login" id="login" value="${login}" class="form-control" placeholder="<if:message string="login.field.user"/>" required autofocus>
	<div class="user_info lp_field_label">
					<span id="capsWarn" class="error_msg" style="display:none">Caps&nbsp;ON&nbsp;</span>&nbsp;
				</div>
    <input type="password" name="password" id="password" class="form-control" placeholder="<if:message string="login.field.password"/>" required>
    <label class="checkbox">
      <!-- <input type="checkbox" name="keep_session" id="keep_logged" > -->
     <!--  $msg.get('login.field.keepSession')  --></label>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Login</button>
  </form>
  
  <div class="loginfooter" style="padding-top:20px">iFlowBPM <%= Version.VERSION %>  <a class="lp_top_nav_link" style="color:#5D7891;" href="http://www.uniksystem.pt"><if:message string="iflow.copyright"/></a><br>
    <!--iFlow Version ${version}-->
	</div>

</form>



</body>
</html>

