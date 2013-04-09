<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>

<%
	String title = messages.getString("flow_settings_import.title"); 
String sPage = "Admin/flow_settings_import";

String sOp = fdFormData.getParameter("op");
if (sOp == null) sOp = "0";
int op = Integer.parseInt(sOp);

int flowid = 0;
String sFlowName = fdFormData.getParameter("flowname");

try {
  // var checking
  flowid = Integer.parseInt(fdFormData.getParameter(DataSetVariables.FLOWID));
  if (sFlowName == null) throw new Exception();
}
catch (Exception e) {
	ServletUtils.sendEncodeRedirect(response, "flow_settings.jsp");
	return;
}

String sError = fdFormData.getParameter("error");
String sSave = fdFormData.getParameter("save");
%>

<%@ include file = "auth.jspf" %>




<div class="upload_box table_inc">
	<form name="formulario" action="<%=response.encodeURL("Admin/flow_settings_doimport.jsp") %>" method="POST" enctype="multipart/form-data"
		onsubmit="javascript:return AIM.submit(this, {'onStart' : getStartUploadCallback(), 'onComplete' : getUploadCompleteCallback('Upload complete', 4, '<%=response.encodeURL("Admin/flow_settings_edit.jsp") %>', 'ts=<%=ts%>&<%=DataSetVariables.FLOWID%>=<%=flowid%>&flowname=<%=sFlowName%>')})">
		<input type="hidden" name="flowid" value="<%=flowid%>">
		<input type="hidden" name="flowname" value="<%=sFlowName%>">
		<input type="hidden" name="op" value="3">
		<fieldset>
			<legend></legend>
			<ol>
				<li>
					<label for="file"><if:message string="flow_settings_import.field.file"/></label>
					<input type="file" name="file" />
				</li>
			</ol>
		</fieldset>
		
		<fieldset class="submit"> 
			<input class="regular_button_01" type="button" name="back" value="<if:message string="button.back"/>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_settings_edit.jsp") %>', 'ts=<%=ts%>&<%=DataSetVariables.FLOWID%>=<%=flowid%>&flowname=<%=sFlowName%>');"/>
			<input class="regular_button_01" type="button" name="clear" value="<if:message string="button.clear"/>" onClick="javascript:document.formulario.reset()"/>
			<input class="regular_button_01" type="submit" name="add" value="<if:message string="button.import"/>"/>
   		</fieldset>
   	</form>
</div>
