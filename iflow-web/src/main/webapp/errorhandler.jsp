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
Logger.errorJsp(_login, request.getPathInfo(), "Uncaptured exception in JSP", exception);

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
