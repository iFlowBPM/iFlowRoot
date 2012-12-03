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
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="pt.iflow.api.utils.Const"%>
<%-- This is used only in registration stuff --%>
<% 
	String sIsFull = request.getParameter("full");
	boolean bIsFull = true;
	if(!StringUtils.isEmpty(sIsFull) &&
			"false".equals(sIsFull))
		bIsFull = false;
	Boolean isSystemAdmin = (Boolean) request.getAttribute("isSystemAdmin");
	if(null == isSystemAdmin) isSystemAdmin = Boolean.FALSE;
	request.setAttribute("bUseEmail", Const.bUSE_EMAIL);
	request.setAttribute("bEdit", true);
%>
