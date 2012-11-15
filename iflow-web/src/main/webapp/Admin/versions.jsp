<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>

<if:checkUserAdmin type="both">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%

String title = messages.getString("admin-versions.title"); 
String sPage = "Admin/versions";

String version = messages.getString("admin-versions.version");
String build = messages.getString("admin-versions.build");
String buildDate = messages.getString("admin-versions.buildDate");
String web = messages.getString("admin-versions.web");
String editor = messages.getString("admin-versions.editor");
String blocks = messages.getString("admin-versions.blocks");

String openReleaseNotes = messages.getString("admin-versions.openReleaseNotes"); 
String closeReleaseNotes = messages.getString("admin-versions.closeReleaseNotes"); 

%>
	<h1 id="title_admin"><%=title%></h1>
	
	<div class="table_inc">  

		<table>
			<tr>
			  <td class="info_msg" width="50%"><%=version %></td>
			  <td class="info_msg" width="20%"><%=Version.VERSION %></td>
			  <td width="30%"></td>
			</tr>
			<tr>
			  <td class="info_msg"><%=build %></td>
			  <td class="info_msg"><%=Version.BUILD %></td>
			  <td></td>
			</tr>
			<tr>
			  <td class="info_msg"><%=buildDate %></td>
			  <td class="info_msg"><%=Version.BUILD_DATE%></td>
			  <td></td>
			</tr>
			<tr>
			  <td class="info_msg"><%=web %></td>
			  <td class="info_msg"><%=Version.WEB %></td>
			  <td class="info_msg">
			  	<a id="web_link_open" href="javascript:openReleaseNotes('web');"><%=openReleaseNotes %></a>
			  	<a id="web_link_close" style="display:none;" href="javascript:closeReleaseNotes('web');"><%=closeReleaseNotes %></a>
			  </td>
			</tr>
			<tr id="web_release_notes" style="display:none">
			  <td class="info_msg release_notes" colspan="3">
			  	<pre><%=Version.readWebReleaseNotes() %></pre>
			  </td>
			</tr>
			<tr>
			  <td class="info_msg"><%=blocks %></td>
			  <td class="info_msg"><%=Version.BLOCKS %></td>
			  <td class="info_msg">
			  	<a id="blocks_link_open" href="javascript:openReleaseNotes('blocks');"><%=openReleaseNotes %></a>
			  	<a id="blocks_link_close" style="display:none;" href="javascript:closeReleaseNotes('blocks');"><%=closeReleaseNotes %></a>
			  </td>
			</tr>
			<tr id="blocks_release_notes" style="display:none">
			  <td class="info_msg release_notes" colspan="3">
			  	<pre><%=Version.readBlocksReleaseNotes() %></pre>
			  </td>
			</tr>
			<tr>
			  <td class="info_msg"><%=editor %></td>
			  <td class="info_msg"><%=Version.EDITOR %></td>
			  <td class="info_msg">
			  	<a id="editor_link_open" href="javascript:openReleaseNotes('editor');"><%=openReleaseNotes %></a>
			  	<a id="editor_link_close" style="display:none;" href="javascript:closeReleaseNotes('editor');"><%=closeReleaseNotes %></a>
			  </td>
			</tr>
			<tr id="editor_release_notes" style="display:none">
			  <td class="info_msg release_notes" colspan="3">
			  	<pre><%=Version.readEditorReleaseNotes() %></pre>
			  </td>
			</tr>
		</table>
	</div>
