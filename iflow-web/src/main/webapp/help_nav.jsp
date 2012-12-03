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
<%@ page import = "pt.iflow.api.msg.IMessages" %>
<%@ page import = "pt.iflow.servlets.HelpNavConsts" %>
<%@ include file = "inc/defs.jsp" %>
<%

		String sel = fdFormData.getParameter("sel");
		int nSel = 0;
		try {
			nSel = Integer.parseInt(sel);
		}
		catch (Exception e) {
		}


		%>

      <h1 id="title_help"><%=messages.getString("help_nav.title")%></h1>
      <div style="display:none"> <%-- pesquisa para ja disabled. a fazer teria que se meter os textos na base de dados (ADIADO) --%>
	      <h2>Pesquisa</h2>
	      <form name="help_search_form" method="post">
	      <if:formInput type="hidden" name="ts" edit="false" label="" value='<%=ts%>'/>
	      <ul>
	        <li><input type="text" name="search"/><a class="li_link <%=(nSel == HelpNavConsts.HELP_SEARCH)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.HELP_SEARCH%>','<%= response.encodeURL("help.jsp") %>','get_params(document.help_search_form)');">&nbsp;<img src="images/icon_search.png" border="0"/></a></li>
	      </ul>
	      </form>
      </div>
      <h2><%=messages.getString("help_nav.about.title")%></h2>
      <ul>
        <li><a class="li_link <%=(nSel == HelpNavConsts.ABOUT_HELP)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.ABOUT_HELP%>','','');show_help('help_about_help');"><%=messages.getString("help_nav.about.help.title")%></a></li>
        <li><a class="li_link <%=(nSel == HelpNavConsts.ABOUT_IFLOW)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.ABOUT_IFLOW%>','','');show_help('help_about_iflow');">iFlow</a></li>
      </ul>
      <h2><%=messages.getString("help_nav.concepts.title")%></h2>
      <ul>
        <li><a class="li_link <%=(nSel == HelpNavConsts.CONCEPTS_FLOW)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.CONCEPTS_FLOW%>','','');show_help('help_concepts_flow');"><%=messages.getString("help.concepts_flow.title")%></a></li>
      </ul>
      <h2><%=messages.getString("help_nav.topics.title")%></h2>
      <ul>
        <li><a class="li_link <%=(nSel == HelpNavConsts.TOPIC_DASHBOARD)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.TOPIC_DASHBOARD%>','','');show_help('help_dashboard');"><%=messages.getString("help.title.dashboard")%></a></li>
        <li><a class="li_link <%=(nSel == HelpNavConsts.TOPIC_TASKS)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.TOPIC_TASKS%>','','');show_help('help_tasks');"><%=messages.getString("help.title.tasks")%></a></li>
        <li><a class="li_link <%=(nSel == HelpNavConsts.TOPIC_MYPROCESSES)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.TOPIC_MYPROCESSES%>','','');show_help('help_my_processes');"><%=messages.getString("help.title.search")%></a></li>
        <li><a class="li_link <%=(nSel == HelpNavConsts.TOPIC_PROCESSES)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.TOPIC_PROCESSES%>','','');show_help('help_processes');"><%=messages.getString("help.title.processes")%></a></li>
        <li><a class="li_link <%=(nSel == HelpNavConsts.TOPIC_DELEGATIONS)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.TOPIC_DELEGATIONS%>','','');show_help('help_delegations');"><%=messages.getString("help.title.delegations")%></a></li>
        <li><a class="li_link <%=(nSel == HelpNavConsts.TOPIC_REPORTS)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.TOPIC_REPORTS%>','','');show_help('help_reports');"><%=messages.getString("help.title.reports")%></a></li>
        <li><a class="li_link <%=(nSel == HelpNavConsts.TOPIC_NOTIFICATIONS)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.TOPIC_NOTIFICATIONS%>','','');show_help('help_notifications');"><%=messages.getString("help.title.notifications")%></a></li>
        <% if (userInfo.isOrgAdmin()) { %> <li><a class="li_link <%=(nSel == HelpNavConsts.TOPIC_ADMIN)?"li_selected":""%>" href="javascript:tabber('help','<%= response.encodeURL("help_nav.jsp") %>','sel=<%=HelpNavConsts.TOPIC_ADMIN%>','','');show_help('help_admin');"><%=messages.getString("help.title.admin")%></a></li> <% } %>
      </ul>

