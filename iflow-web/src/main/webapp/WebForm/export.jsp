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
<%@ include file = "forminc.jspf" %><%
if (sError == null || sError.equals("")) {
  response.reset();
  response.setContentType("application/vnd.ms-excel");
  
  
  String stmp = "xls";
  if (Const.nEXPORT_MODE == Const.nEXPORT_MODE_CSV) {
    stmp = "csv";
  }
  response.addHeader("Content-Disposition","attachment;filename=file." + stmp);
  
  Object[] oa = new Object[4];
  oa[0] = userInfo;
  oa[1] = procData;
  oa[2] = sField;
  oa[3] = response.getOutputStream();
  // 8: exportFieldToExcel
  sError = (String)bBlock.execute(8,oa);
  
}
  
if (sError != null && !sError.equals("")) {
  response.reset();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="Pragma" content="no-cache"/>
<script language="JavaScript">
<!--
  if (window.resizeTo) self.resizeTo(400,300);
// -->
</script>
  </head>
<body>
  <table align="center" width="100%" height="100%">
    <tr align="center" valign="middle">
      <td align="center" valign="middle"><%=sError%></td>
    </tr>
    <tr>
      <td>&nbsp;<br>&nbsp;<br></td>
    </tr>
    <tr align="center" valign="middle">
      <td align="center" valign="middle"><a href="javascript:;" onClick="self.close()">Fechar</a></td>
    </tr>
  </table>
</body>
</html>
<%
}
out.clear();
out = pageContext.pushBody(); 
%>

