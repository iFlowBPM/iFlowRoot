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
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="inc/defs.jsp"%>
<%@ page import="java.text.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*"%>
<%@ page import="pt.iflow.api.notification.Email"%>
<%@ page import="pt.iflow.delegations.DelegationManager"%>
<%
  String title = messages.getString("terminar_agendamento.title");
  String sPage = "terminar_agendamento";
  StringBuffer sbError = new StringBuffer();

  StringBuffer sbtmp = null;

  boolean cbTerminate = StringUtils.equals("true", fdFormData.getParameter("cb_terminate"));
  String[] cbFlowids = fdFormData.getParameterValues("cb_flowid");
  if (null == cbFlowids) cbFlowids = new String[0];
  ArrayList<String> alHelper = new ArrayList<String>();
  for(String cbFlowid:cbFlowids) {
    if(StringUtils.isNotBlank(cbFlowid))
      alHelper.add(cbFlowid);
  }
  cbFlowids = alHelper.toArray(new String[alHelper.size()]);

  if (!cbTerminate) {
    cbFlowids = new String[1];
    cbFlowids[0] = fdFormData.getParameter("id");
  } else if (cbFlowids.length == 0) {
    %><jsp:forward page="gestao_tarefas.jsp?action=reject"></jsp:forward>
    <%return;
  }

  for (int i = 0; i < cbFlowids.length; i++) {
    int id;
    try {
      id = Integer.parseInt(cbFlowids[i]);
    } catch (NumberFormatException nfe) {
      id = 0;
    }

    if (id > 0) {
      String[] saParams = DelegationManager.get().stopDelegation(userInfo, id);
      sbError.append("<br>").append(messages.getString("terminar_agendamento.msg.terminated", saParams));
    } else {
      sbError.append("<br>").append(messages.getString("terminar_agendamento.error.notfound"));
    }
  }
%>
<%
  if (sbError.length() > 0) {
%>
<div class="error_msg"><%=sbError.toString()%></div>
<%
  }
%>
<div class="button_box"><input class="regular_button_02"
	type="button" name="continue"
	value="<%=messages.getString("button.continue")%>"
	onClick="javascript:tabber_right(5, '<%=response.encodeURL("gestao_tarefas.jsp")%>', 'sel=2&action=reject');" />
</div>
