<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isErrorPage="true"
    import = "pt.iflow.api.utils.*"
%><%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %><%
String _login = null;
UserInfoInterface _userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
if(null != _userInfo) _login = _userInfo.getUtilizador();
// Log Error
//Logger.errorJsp(_login, request.getPathInfo(), "Uncaptured exception in JSP", exception);

// TODO option to show/hide page header (to include somewhere)
// TODO Error reporting facility
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="pt.iflow.api.core.BeanFactory"%><html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="Pragma" content="no-cache">
  <title><if:message string="error.generic"/></title>
  <link rel="stylesheet" href="<%=Const.APP_URL_PREFIX%>Themes/<%=BeanFactory.getOrganizationThemeBean().getOrganizationTheme(_userInfo).getThemeName() %>/css/iflow_main.css" type="text/css">
  <script type="text/javascript" src="<%=Const.APP_URL_PREFIX%>javascript/ajax_processing.js"></script>
  <script type="text/javascript" src="<%=Const.APP_URL_PREFIX%>javascript/tabs.js"></script>
</head>
<body>
<div style="margin:auto; padding-top: 20px;">
<div class="error_msg">
  <if:message string="error.generic"/>
</div>
</div>
</body>
</html>