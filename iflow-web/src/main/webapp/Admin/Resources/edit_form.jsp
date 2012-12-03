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
<%@ include file="../../inc/defs.jsp"%>

<if:checkUserAdmin type="both">
	<div class="error_msg"><if:message
		string="admin.error.unauthorizedaccess" /></div>
</if:checkUserAdmin>

<h1 id="title_admin"><if:titleMessage type="${param.type}" /></h1>

<% 
	String file = fdFormData.getParameter("filename");
	String type = fdFormData.getParameter("type");
	String editor = fdFormData.getParameter("editor");
  	if(StringUtils.isNotBlank(file) && 
  	    StringUtils.isNotBlank(type) && 
  	    StringUtils.isNotBlank(editor)) {
  	  RepositoryEditor.storeFile(userInfo, file, type, editor);
  	}
%>

<div class="upload_box table_inc">
  <form name="formulario" method="POST" enctype="multipart/form-data"
	action="<c:url value="Admin/Resources/edit_form.jsp">
				<c:param name="type" value="${param.type}" />
				<c:param name="file" value="${param.file}" />
			</c:url>"
	onsubmit="javascript:return AIM.submit(this, {'onStart' : getStartUploadCallback(), 'onComplete' : getUploadCompleteCallback('Upload complete', 4, '<%=response.encodeURL("Admin/Resources/dolist.jsp")%>', 'type=${param.type}')})">
	<input type="hidden" name="type" value="${param.type}" />
	
	<c:if test="${not empty param.file}">
	  <input type="hidden" name="filename" value="${param.file}" />
	</c:if>

	<fieldset>
	  <ol style="width: 100%;">
		<c:if test="${not empty param.file}">
		  <li>
			<if:message string="resources.file.label" />&nbsp;<c:out value="'${param.file}'" />
		  </li>
		</c:if>
  		<li style="width: 100%;">
		  <textarea id="editor" name="editor" class="textarea" wrap="off"><%=RepositoryEditor.retrieveFile(userInfo, request.getParameter("file"), request.getParameter("type")) %></textarea>
  		</li>
	  </ol>
	</fieldset>

	<fieldset class="submit">
	  <input class="regular_button_01" type="button" name="back" value="<if:message string="button.back"/>"
		onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/Resources/dolist.jsp") %>', 'type=${param.type}');" />
	  <input class="regular_button_02" type="submit" name="add" value="<if:message string="button.save"/>" />
	</fieldset>
  </form>

  <if:generateHelpBox context="dolist"/>
</div>
