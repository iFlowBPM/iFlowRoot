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
	int nField = -1;

try {
  nField = Integer.parseInt(sField);
}
catch (Exception e) {
}

if (nField == -1) {
  String sSTYLESHEET_SUFFIX = ".xsl";
  String sTEMPLATE_SUFFIX = ".fo";
  
  // 10: print stylesheet attribute
  String sPrintStyleSheet = (String)bBlock.execute(10,null);
  if (StringUtils.isNotEmpty(sPrintStyleSheet)) {
    if (sPrintStyleSheet.toLowerCase().endsWith(sTEMPLATE_SUFFIX)) {
%><%@ include file = "printFOP.jspf" %><%
	}
    else {
%><%@ include file = "printXSL.jspf" %><%
	}
  }
}
else {
  // printing field...force xsl printing
%><%@ include file = "printXSL.jspf" %><%
}
%>
