<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file="../../inc/defs.jsp"%>

<if:checkUserAdmin type="both">
	<div class="error_msg">
		<if:message string="admin.error.unauthorizedaccess"/>
	</div>
</if:checkUserAdmin>

<h1 id="title_admin"><if:titleMessage type="${param.type}"></if:titleMessage></h1>

<%
	String type = fdFormData.getParameter("type");
	RepositoryFile[] fileList = null;
	if(StringUtils.isNotBlank(type)) {
	  fileList = RepositoryEditor.listFiles(userInfo, type);
	}
%>

<c:if test="${not empty actionResult}">
	<div class="info_msg">
		<c:out value="${actionResult}"></c:out>
	</div>
</c:if>

<% if(fileList == null || fileList.length < 1) { %>
	<div class="info_msg">
		<if:message string="resources.table.empty"></if:message>
	</div>
<% } else { %>
	<div class="table_inc">  
		<table class="item_list">
			<tr class="tab_header">
				<td><if:message string="resources.table.list.header"></if:message></td>
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
			</tr>
		  <% 
		    for(int i = 0, l = fileList.length; i < l; i++) { 
		      RepositoryFile file = fileList[i];
		  %>
		    <tr class="<%=((l % 2) == 0) ? "tab_row_even" : "tab_row_odd" %>">
				<td><c:out value='<%=file.getName() %>'></c:out></td>
				<td class="itemlist_icon">
					<% if(RepositoryEditor.checkFileMIME(userInfo, file.getName(), new String[] {"text", "application/octet-stream"}, new String[] {".pdf"})) { %>
					  <a href="<c:url value="Admin/Resources/edit_form.jsp" ><c:param name="file" value='<%=file.getName() %>'></c:param><c:param name="type" value="${param.type}"></c:param></c:url>" onclick="javascript:tabber_right(4,this.href,'');return false;">
						<img class="toolTipImg" src="images/icon_modify.png" width="16" height="16" border="0" title="<if:message string="resources.table.tooltip.edit"></if:message>" alt="<if:message string="resources.table.list.modify"></if:message>">
					  </a>
		  			<% } %>
				</td>
				<td class="itemlist_icon">
					<a href="<c:url value="Admin/Resources/uploadform.jsp" ><c:param name="file" value='<%=file.getName() %>'></c:param><c:param name="type" value="${param.type}"></c:param></c:url>" onclick="javascript:tabber_right(4,this.href,'');return false;">
						<img class="toolTipImg" src="images/icon_resync.png" width="16" height="16" border="0" title="<if:message string="resources.table.tooltip.modify"></if:message>" alt="<if:message string="resources.table.list.modify"></if:message>">
					</a>
				</td>
				<td class="itemlist_icon">
				<a href="<c:url value="download.rep" ><c:param name="file" value='<%=file.getName() %>'></c:param><c:param name="type" value="${param.type}"></c:param></c:url>">
					<img class="toolTipImg" src="images/icon_download.png" width="16" height="16" border="0" title="<if:message string="resources.table.tooltip.download"></if:message>" alt="<if:message string="resources.table.list.download"></if:message>">
				</a>
				</td>
				<td class="itemlist_icon">
					<% if(file.isSystem() && file.isOrganization() && !userInfo.isSysAdmin()) { %>
						<a href="<c:url value="reset.rep" ><c:param name="file" value='<%=file.getName() %>'></c:param><c:param name="type" value="${param.type}"></c:param></c:url>" onclick="javascript:tabber_right(4,this.href,'');return false;">
							<img class="toolTipImg" src="images/icon_reset.png" width="16" height="16" border="0" title="<if:message string="resources.table.tooltip.reset"></if:message>" alt="<if:message string="resources.table.list.reset"></if:message>">
						</a>
		  			<% } %>
				</td>
				<td class="itemlist_icon">
					<% if(file.isOrganization()) { %>
						<a href="<c:url value="delete.rep" ><c:param name="file" value='<%=file.getName() %>'></c:param><c:param name="type" value="${param.type}"></c:param></c:url>" onclick="javascript:tabber_right(4,this.href,'');return false;">
							<img class="toolTipImg" src="images/icon_delete.png" width="16" height="16" border="0" title="<if:message string="resources.table.tooltip.delete"></if:message>" alt="<if:message string="resources.table.list.delete"></if:message>">
						</a>
		  			<% } %>
				</td>
			</tr>
		  <% } %>
		</table>
	</div>
<% } %>

<div class="button_box">
   	<input class="regular_button_02" type="button" name="add" value="<if:message string="button.add"></if:message>" onClick="javascript:tabber_right(4, '<c:url value="Admin/Resources/uploadform.jsp"></c:url>','type=${param.type}');"></input>
	<if:generateHelpBox context="dolist"/>
</div>
