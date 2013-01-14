<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%@ page import = "pt.iflow.api.notification.Email" %>
<%@ page import = "pt.iflow.delegations.DelegationManager" %>
<%@page import="pt.iflow.api.notification.NotificationManager"%>
<%
  String title = messages.getString("confirmar_agendamento.title");
  String sPage = "confirmar_agendamento";

  String[] ids = new String[0];
  String[] ownerids = new String[0];
  String[] sKeys = new String[0];
          
  boolean cbAccept = StringUtils.equals("true",fdFormData.getParameter("cb_accept"));
  boolean cbReject = StringUtils.equals("true",fdFormData.getParameter("cb_reject"));
  String [] cbFlowids = fdFormData.getParameterValues("cb_flowid");
  if(null == cbFlowids) cbFlowids = new String[0];
  ArrayList<String> alHelper = new ArrayList<String>();
  for(String cbFlowid:cbFlowids) {
    if(StringUtils.isNotBlank(cbFlowid))
      alHelper.add(cbFlowid);
  }
  cbFlowids = alHelper.toArray(new String[alHelper.size()]);
  
  if(cbAccept || cbReject) {
	if (cbFlowids.length == 0) {
      %><jsp:forward page="gestao_tarefas.jsp?action=approve"></jsp:forward><%
      return;
	} else {
	  int nKey = cbAccept ? 2 : 3; 
	  ids = new String[cbFlowids.length];
	  ownerids = new String[cbFlowids.length];
	  sKeys = new String[cbFlowids.length];
	  for (int i = 0; i < cbFlowids.length; i++) {
		  String[] aux = cbFlowids[i].split(";");
		  ids[i] = aux[0];
		  ownerids[i] = aux[1];
		  sKeys[i] = aux[nKey];
	  }
	}
  } else {
    ids = new String[1];
    sKeys = new String[1];
    ownerids = new String[1];
    ids[0] = fdFormData.getParameter("id");
    sKeys[0] = fdFormData.getParameter("dkey");
    ownerids[0] = fdFormData.getParameter("owner");
  }
  
  StringBuffer sbError = new StringBuffer();
  for (int i = 0; i < ids.length; i++) {
    String id = ids[i];
    String sKey = sKeys[i];
    String ownerid = ownerids[i];

    boolean foundActivity = false;
    boolean accept = true;

    if (id != null && sKey != null && ownerid != null && !id.equals("") && !sKey.equals("") && !ownerid.equals("")) {
	  int res = DelegationManager.get().acceptDelegation(userInfo, id, sKey, ownerid, sbError);
      switch (res) {
	  case DelegationManager.ACCEPT_ACCEPT_FOUND:
	    accept = true;
	    foundActivity = true;
	    break;
	  case DelegationManager.ACCEPT_ACCEPT_NOT_FOUND:
	    accept = true;
	    foundActivity = false;
	    break;
	  case DelegationManager.ACCEPT_NOT_ACCEPT_FOUND:
	    accept = false;
	    foundActivity = true;
	    break;
	  case DelegationManager.ACCEPT_NOT_ACCEPT_NOT_FOUND:
	    accept = false;
	    foundActivity = false;
	    break;
	  case DelegationManager.ACCEPT_ERROR:
	    sbError.append("Error accepting delegation");
	    break;
	  }
    }
  }

  if (sbError.length() > 0) {
%>
	<div class="error_msg">
		<%=sbError.toString()%>
	</div>
<%
  }
%>
	<div class="button_box">
		<input class="regular_button_02" type="button" name="continue" value="<%=messages.getString("confirmar_agendamento.button.continue")%>" onClick="javascript:tabber_right(5, '<%= response.encodeURL("gestao_tarefas.jsp") %>', 'sel=3');"/>
	</div>
