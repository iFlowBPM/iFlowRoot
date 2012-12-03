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
<%@ include file = "inc/defs.jsp" %>
<%
List<String> alUsers = null;
try {
  alUsers = (List<String>)session.getAttribute(login + "_userlist");

  if (alUsers == null || alUsers.size() == 0) throw new Exception();
}
catch (Exception e) {
%>
  <script language="JavaScript">
    self.close();
  </script>
<%
  return;
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
  <link rel="stylesheet" type="text/css" media="all" href="javascript/calendar/calendar-iflow.css" title="cal-iflow" />
  <link rel="stylesheet" type="text/css" href="javascript/yahoo/container/assets/skins/sam/container.css" />
  <link rel="stylesheet" type="text/css" href="javascript/yahoo/button/assets/skins/sam/button.css" />
  <link rel="shortcut icon" href="images/favicon.ico" />
</head>

<body leftmargin="0" bgcolor="#e3e3e3" text="#000000" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td colspan="3">&nbsp;</td>
  </tr>
  <tr>
    <td width="15%">&nbsp;</td>
    <td width="70%" class="v12bAZU">Lista de Utilizadores com Processo Agendado:</td>
    <td width="15%">&nbsp;</td>
  </tr>
  <tr>
    <td colspan="3" class="txt">&nbsp;</td>
  </tr>
  <tr valign="top">
    <td width="15%">&nbsp;</td>
    <td width="70%" height="100%" valign="top" bgcolor="#e3e3e3">
      <p>
       <table width="100%" border="0" cellspacing="0" cellpadding="4" align="center">
<%

for (int i=0; i < alUsers.size(); i++) {
  out.println("         <tr><td align=\"left\" class=\"v10bAZU\">&nbsp;-&nbsp;" + (String)alUsers.get(i) + "</td></tr>");
}
%>
       </table>
    </td>
    <td width="15%">&nbsp;</td>
  </tr>
  <tr>
    <td colspan="3">&nbsp;</td>
  </tr>
  <tr>
    <td colspan="3" align="center" class="v10bAZUdec"><a href="javascript:self.opener.W=null;self.close()" class="v10bAZUdec"><img src="images/limpar_form.gif" width="19" height="19" border="0"><br>Fechar</a></td>
  </tr>
  <tr>
    <td colspan="3">&nbsp;</td>
  </tr>
</table>
</body>
</html>
