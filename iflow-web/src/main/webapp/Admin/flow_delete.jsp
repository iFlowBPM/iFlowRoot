<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>

<if:checkUserAdmin type="org">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%
  String title = messages.getString("flow_delete.title");
  String sPage = "Admin/flow_delete";

  StringBuffer sbError = new StringBuffer();
  int flowid = -1;

  String sOp = fdFormData.getParameter("op");
  if (sOp == null)
    sOp = "0";
  int op = Integer.parseInt(sOp);

  String sHtml = "";
  String sFlowName = fdFormData.getParameter("flowname");
  String sFlowFile = fdFormData.getParameter("flowfile");
  String sFlowId = fdFormData.getParameter(DataSetVariables.FLOWID);

  if (StringUtils.isEmpty(sFlowFile) && op == 1) {
    op = 0;
    sbError.append(messages.getString("flow_delete.error.invalid"));
  }

  boolean procsChecked = StringUtils.equals("true", fdFormData.getParameter("procs"));

  Flow flow = BeanFactory.getFlowBean();

  if (op == 1 && !procsChecked) {
    sbError.append(messages.getString("flow_delete.confirm")).append("<br>");
    op = 0;
  } else if (op == 1 && procsChecked) {

    if (flow != null) {

      try {
    	String undeploySuccess = BeanFactory.getFlowHolderBean().undeployFlow(userInfo, sFlowFile);
    	if (undeploySuccess != null) {
      		sbError.append(messages.getString("flow_delete.error.net_deleted")).append("<br>");
      		op = 0;    		
    	}
    	else {
        	boolean result = BeanFactory.getFlowHolderBean().deleteFlow(userInfo, sFlowFile, true);
        	if (result) {
          		sHtml = messages.getString("flow_delete.sucess");
        	} else {
          		sbError.append(messages.getString("flow_delete.error.net_deleted")).append("<br>");
          		op = 0;
        	}
    	}
      } catch (Exception e) {
        Logger.errorJsp(login, sPage, "exception2: " + e.getMessage());
        e.printStackTrace();
        sbError.append(messages.getString("flow_delete.error.internal")).append("<br>");
      }

    }
  } else {
    flowid = Integer.parseInt(sFlowId);
    sFlowFile = BeanFactory.getFlowHolderBean().getFlowFileName(userInfo, flowid);
    sHtml = messages.getString("flow_delete.confirm");
  }
%>


<form name="flowdelete" id="flowdelete" method="POST">
  <input type="hidden" name="flowfile" value="<%= sFlowFile %>">
  <input type="hidden" name="flowname" value="<%= sFlowName %>">
  <input type="hidden" name="<%= DataSetVariables.FLOWID %>" value="<%= sFlowId %>">


  <h1 id="title_admin"><%=title%></h1>

<%
  if (sbError.length() > 0) {
%>
      <div class="error_msg">
        <%=sbError%>
	  </div>
<%
  }
  if (StringUtils.isNotEmpty(sHtml)) {
%>
      <div class="info_msg">
        <%=sHtml%>
	  </div>
<%
  }
%>

<% if (!(op == 1 && sbError.length() == 0)) { %>

<fieldset><legend></legend>
<ol>
  <if:formInput name="dummy2" labelkey="flow_delete.flowfile" type="text" value='<%=sFlowFile%>' edit="false" />
  <if:formInput name="dummy3" labelkey="flow_delete.flowname" type="text" value='<%=sFlowName%>' edit="false" />
  <li>
    <label><if:message string="flow_delete.disclaimer" /></label>
    <input type="button" name="offline" value="<%=messages.getString("button.offline")%>" 
           onclick="tabber_right(4, '<%=response.encodeURL("Admin/flow_deployer.jsp") %>', 'action=undeploy&'+get_params(document.flowdelete));"/>
  </li>
  <if:formInput name="procs"  labelkey="flow_delete.agree" type="checkbox" value="false" edit="true" />
</ol>
</fieldset>
<% } %>
<fieldset class="submit"><legend></legend>
<%
  if (op == 1 && sbError.length() == 0) {
%>
  <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" 
         onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_settings.jsp") %>','ts=<%=ts%>');"/>
<%
  } else {
%>
  <input class="regular_button_01" type="button" name="cancel" value="<%=messages.getString("button.cancel")%>" 
         onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_settings.jsp") %>','ts=<%=ts%>');"/>
  <input class="regular_button_01" type="button" name="delete" value="<%=messages.getString("button.delete")%>" 
         onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_delete.jsp") %>','op=1&' + get_params(document.flowdelete));"/>
<%
  }
%>
</fieldset>
</form>

