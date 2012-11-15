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
