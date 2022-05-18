<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<if:checkUserAdmin type="both">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>
<h1 id="title_admin"><if:message string="flow_settings.import.title"/></h1>

<div class="upload_box table_inc">
	<form name="formulario" action="<%=response.encodeURL("Admin/doupload_flow.jsp")%>" method="POST" enctype="multipart/form-data"
		onsubmit="javascript:return document.forms['formulario']['file-to-upload'].files[0].type !== 'text/xml' ? false : AIM.submit(this, {'onStart' : getStartUploadCallback(), 'onComplete' : getUploadCompleteCallback('Upload complete', 4, '<%=response.encodeURL("Admin/flow_settings.jsp")%>', 'ts=<%=ts%>')});">
		<fieldset>
			<legend></legend>
			<ol>
				<li>
					<label for="file">
						<if:message string="flow_settings.import.label"/>
					</label>
					<input onchange="(function(){document.forms['formulario']['file-to-upload'].files[0].type !== 'text/xml' ? alert(
    '<if:message string="incorrect.format.xml"/>') : ''})();" id="file-to-upload" accept="text/xml" type="file" name="file" class="form-control"/>

				</li>				
				<li>
					<label for="create_version">
						<if:message string="flow_settings.import.create_version"/>
					</label>
					<input type="checkbox" name="create_version" id="create_version" value="yes" onchange="document.formulario.version_note.disabled=!document.formulario.create_version.checked;" />
				</li>
				<li>
					<label for="version_note">
						<if:message string="flow_settings.import.version_note"/>
					</label>
					<textarea rows="5" cols="40" name="version_note" id="version_note" disabled="disabled"></textarea>
				</li>
				<li>
					<label for="is_subflow">
						<if:message string="flow_settings.import.is_subflow"/>
					</label>
					<input type="checkbox" name="is_subflow" id="is_subflow" value="yes" />
				</li>
			</ol>
		</fieldset>
		
		<fieldset class="submit"> 
			<input class="regular_button_01" type="button" name="back" value="<if:message string="button.back"/>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_settings.jsp")%>', 'ts=<%=ts%>');"/>
			<input class="regular_button_01" type="button" name="clear" value="<if:message string="button.clear"/>" onClick="javascript:document.formulario.reset()"/>
			<input class="regular_button_02" type="submit" name="add" value="<if:message string="button.import"/>"/>
   		</fieldset>
   	</form>
</div>
