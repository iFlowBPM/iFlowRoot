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
