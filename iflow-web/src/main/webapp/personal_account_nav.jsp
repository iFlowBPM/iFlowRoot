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

