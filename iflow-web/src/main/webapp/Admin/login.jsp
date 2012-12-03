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
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<head>

	<title>iFlow Admin Login</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="Pragma" content="no-cache"/>
	
	<link rel="stylesheet" href="../Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(null).getThemeName() %>/css/iflow_main.css" type="text/css">
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

<body class="lp_admin_body">

<form name="dados" id='dados' method="post" action="<%= response.encodeURL("../AuthenticationServlet") %>">
	<input type="hidden" name="url" value="<%= url %>"/>
	<input type="hidden" name="<%=Const.sUSER_FLOWID%>" value="<%=ufid%>"/>
	<input type="hidden" name="source" value="internet"/>
	<input type="hidden" name="do_redirect" value="true"/>
	<input type="hidden" name="url" value="../main.jsp"/>

<% if(!Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) { %>
<div class="lp_top_nav_pos">
	<div class="lp_top_nav_text" style="float:left;">
		<a class="lp_top_nav_link" href="http://www.iflow.pt/ifportal">iflow.pt</a> 
		: <a class="lp_top_nav_link" href="../login.jsp"><if:message string="login.link.login"/></a>
	</div>
</div>
<% } %>

<div class="lp_admin_login_box">
    <div class="error_msg lp_error_msg">
<% if(bError) { %>
        <%=sLoginError%>
<% } else if(notifyInvalid){ %>
	<if:message string="admin.error.license"/>
<% } %>
    </div>	

	<div class="user_info lp_field_label"> 
	  <if:message string="login.field.systemuser"/>
	</div>
	<div class="item lp_field_input">
	  <input type="text" name="login" id="login" value="<%= login %>" size="15" maxlength="40"/>
	</div>    
	<div class="user_info lp_field_label">
	  <span id="capsWarn" class="error_msg" style="display:none">Caps&nbsp;ON&nbsp;</span><if:message string="login.field.password"/>
	</div>
	<div class="item lp_field_input">
	    <input type="password" name="password" id="password" size="15" maxlength="40"/>
	</div>
    <div class="lp_login_button">
  		<input id="link_search_span" class="regular_button_01" type="button" name="filter" value="Login" onClick="document.dados.submit();" />
    </div> 
</div>
<div class="lp_footer_nav">
    <a class="lp_top_nav_link" style="color:#5D7891;" href="http://www.infosistema.pt"><if:message string="iflow.copyright"/></a>
    <br><span class="lp_top_nav_text">iFlow Version <%= Version.VERSION %></span>
</div>

</form>

<div id="languages_div">
<%
	for (int i = 0, lim = Settings.localeKeys.length; i < lim; i++) {
	  Locale locale = Settings.localeKeys[i];
	  String loc = locale.toString();
	  String append = " |";
	  String language = StringUtils.capitalize(locale.getDisplayLanguage(locale));
	  String selected = StringUtils.equals(loc, lang) ? "class=\"language_selected\"" : "";
	  if (i == (lim - 1)) {
	    append = "";
	  }
	  %>
	  <a href="login.jsp" onclick="setCookie('<%=Const.LANG_COOKIE%>', '<%=loc %>');" title="<%=language %>" <%=selected %>><%=language %></a><%=append %>
	  <%
	}
%>
</div>

</body>
</html>

