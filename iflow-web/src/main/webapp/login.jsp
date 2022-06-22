
<%@page import="pt.iflow.msg.Messages"%><%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import = "pt.iknow.utils.*" %>
<%@ page import = "pt.iflow.api.msg.IMessages" %>
<%@ page import = "pt.iflow.api.utils.*" %>
<%@ page import = "pt.iflow.core.Version" %>
<%@ page import = "java.net.URLEncoder" %>
<%@ page import = "java.util.Date" %>
<%@ page import = "java.util.Hashtable"%>
<%@ page import = "java.util.Locale"%>
<%@ page import = "pt.iflow.api.licensing.LicenseServiceFactory"%>
<%@ page import = "pt.iflow.api.licensing.LicenseService"%>
<%@ page import = "org.apache.commons.lang.StringUtils" %>
<%@ page import = "pt.iflow.api.utils.ServletUtils"%>
<%@ page import = "pt.iflow.api.core.BeanFactory"%>
<%@ page import = "pt.iflow.api.core.Settings"%>
<%@ page import = "pt.iflow.api.presentation.PresentationManager"%>
<%@ page import = "java.util.ArrayList"%>
<%@ page import = "java.util.List"%>
<%@ page import = "pt.iflow.servlets.LoginAttemptCounterController"%>
<%
	// first of all, check license status.
if(!LicenseServiceFactory.getLicenseService().isLicenseOK()) {
  session.invalidate(); // kill session
  // redirect to check configuration
  ServletUtils.sendEncodeRedirect(response, "Admin/login.jsp");
  return;
}

Hashtable<String, Object> hsSubst = new Hashtable<String, Object>();

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

UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);

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

hsSubst.put("msg", Messages.getInstance(lang).getMessages());

hsSubst.put("login", login);

hsSubst.put("version", Version.VERSION);

hsSubst.put("orgTheme", BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName());
hsSubst.put("lang", lang);
hsSubst.put("sFocus", sFocus);

Hashtable<String, Object> dados = new Hashtable<String, Object>();
dados.put("action", response.encodeURL("AuthenticationServlet"));
dados.put("url", url);
dados.put("ufidName", Const.sUSER_FLOWID);
dados.put("ufidValue", ufid);
hsSubst.put("dados", dados);

hsSubst.put("isInstallLocal", Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE));
hsSubst.put("isInstallDemo", Const.INSTALL_DEMO.equals(Const.INSTALL_TYPE));
hsSubst.put("useEmail", Const.bUSE_EMAIL);

hsSubst.put("isInMaintenance", Const.isInMaintenance());
hsSubst.put("maintenance", (Const.isInMaintenance() ? "maintenance" : ""));

hsSubst.put("hasError", bError);
if (bError) {
  hsSubst.put("loginError", sLoginError);
}

List<Hashtable<String, Object>> localeKeys = new ArrayList<Hashtable<String, Object>>();
for (int i = 0, lim = Settings.localeKeys.length; i < lim; i++) {
  Hashtable<String, Object> localeKey = new Hashtable<String, Object>();
  Locale locale = Settings.localeKeys[i];
  String loc = locale.toString();
  String language = StringUtils.capitalize(locale.getDisplayLanguage(locale));
  String selected = StringUtils.equals(loc, lang) ? "class=\"language_selected\"" : "";
  String append = " |";
  if (i == (lim - 1)) {
    append = "";
  }
  localeKey.put("langCookie", Const.LANG_COOKIE);
  localeKey.put("loc", loc);
  localeKey.put("language", language);
  localeKey.put("selected", selected);
  localeKey.put("append", append);
  localeKeys.add(localeKey);
}
hsSubst.put("localeKeys", localeKeys);
hsSubst.put("isOverFailureLimit", LoginAttemptCounterController.isOverFailureLimit(config.getServletContext() , request, ""));
%>
<%=PresentationManager.buildLoginPage(response, userInfo, hsSubst) %>