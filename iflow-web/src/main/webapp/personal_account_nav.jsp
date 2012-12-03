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
		
		/* PELA TUA SAUDINHA NAO ALTERES DO NOT CHANGE OS VALORES DESTAS CONSTANTES */
		final int USER_DATA = 1;
		final int USER_PASSWORD = 2;
		final int PREFS_LOCALE = 3;
		final int PREFS_TUTORIAL = 4;

		String sel = fdFormData.getParameter("sel");
		int nSel = 0;
		try {
			nSel = Integer.parseInt(sel);
		}
		catch (Exception e) {
		}


		%>

      <h1 id="title_account"><if:message string="personal_account_nav.title"/></h1>
      <h2><if:message string="personal_account.section.userdata.title"/></h2>
      <ul>
        <li><a class="li_link <%=(nSel == USER_DATA)?"li_selected":""%>" href="javascript:tabber(6,'<%= response.encodeURL("personal_account_nav.jsp") %>','sel=<%=USER_DATA%>','<%= response.encodeURL("personal_account.jsp") %>','sel=<%=USER_DATA%>&ts=<%=ts%>');"><if:message string="personal_account.section.userdata.link.data"/></a></li>
      </ul>

