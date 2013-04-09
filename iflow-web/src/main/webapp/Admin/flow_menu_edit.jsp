<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<%
	String title = messages.getString("flow_menu_edit.title");
String sPage = "Admin/flow_menu_edit";
int flowid=-1; // this is used by auth.jsp
%><%@ include file = "auth.jspf" %>
<form method="post" name="menus">
	<input type="hidden" name="toDelete" value="">

	<h1 id="title_admin"><%=title%></h1>

	<div class="table_inc">
    	<table class="item_list">
      		<tr class="tab_header">
          		<td><if:message string="flow_menu_edit.field.description" /></td>
          		<td><if:message string="flow_menu_edit.field.remove" /></td>
      		</tr>
	<c:if test="${not empty menuItems}">
	<c:forEach var="item" items="${menuItems}">
    		<tr class="tab_row_extra">
    			<td>${ item.name }</td>
        		<td>
          			<a class="cell_button" href="javascript:document.menus.toDelete.value=${ item.linkid };tabber_right(4, '<%=response.encodeURL("Admin/flow_menu_del") %>',get_params(document.menus));"><if:message string="flow_menu_edit.link.remove"/></a>
          		</td>
          	</tr>
		<c:if test="${not empty item.children}">
		<c:forEach var="child" items="${item.children}" varStatus="status">
    		<tr class="${ status.index%2==0?'tab_row_even':'tab_row_odd' }">
    			<td class="indent">${ child.name }</td>
        		<td>
           			<a class="cell_button" href="javascript:document.menus.toDelete.value=${ child.linkid };tabber_right(4, '<%=response.encodeURL("Admin/flow_menu_del") %>',get_params(document.menus));"><if:message string="flow_menu_edit.link.remove" /></a>
           		</td>
           	</tr>
		</c:forEach>
		</c:if>
	</c:forEach>
	</c:if>
		</table>
		<fieldset class="submit">
			<input class="regular_button_01" type="button" name="add_unit" value="<if:message string="button.add"/>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_menu_add") %>','');"/>
		</fieldset>
	</div>
	
	<if:generateHelpBox context="flow_menu_edit"/>
	
</form>
