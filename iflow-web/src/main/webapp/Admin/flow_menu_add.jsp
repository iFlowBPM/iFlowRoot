<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<%
	String title = messages.getString("flow_menu_add.title");
int flowid = -1;
String sPage = "Admin/flow_menu_edit";
%>

<%@ include file = "auth.jspf" %>
<form name="flows" method="post">

	<h1 id="title_admin"><%=title%></h1>

	<c:if test="${not empty err_msg}">
		<div class="error_msg">
			<c:out value="${err_msg}" escapeXml="false"/>
		</div>
	</c:if>

	<fieldset>
		<ol>
			<if:formSelect name="parentid" edit="true" value="0" labelkey="flow_menu_edit.field.parentNode">
				<if:formOption value="0" labelkey="flow_menu_add.const.root"/>
				<c:forEach var="item" items="${rootItems}">
					<if:formOption value="${item.linkid}" label="${item.name}"/>
				</c:forEach>
			</if:formSelect>
			<if:formSelect name="addedflow" edit="true" labelkey="flow_menu_edit.field.item" value="0" onchange="javascript:var sel = document.flows.addedflow.options[selectedIndex].value != '0'; document.flows.texto.disabled = sel; document.flows.url.disabled = sel;">
				<if:formOption value="0" labelkey="flow_menu_add.const.text"/>
				<c:forEach var="item" items="${flowItems}">
					<if:formOption value="${item.linkid}" label="${item.name}"/>
				</c:forEach>
			</if:formSelect>
			<if:formInput name="texto" type="text" edit="true" value="" labelkey="flow_menu_edit.field.description" />
			<if:formInput name="url" type="text" edit="true" value="" labelkey="flow_menu_edit.field.url" />
		</ol>	
	</fieldset>
	<fieldset class="submit">
        <input class="regular_button_01" type="button" name="back" value="<if:message string="button.back"/>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_menu_edit") %>');"/>
		<input class="regular_button_01" type="button" name="clear" value="<if:message string="button.clear"/>" onClick="javascript:document.flows.reset()"/>
		<input class="regular_button_01" type="button" name="add" value="<if:message string="button.add"/>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_menu_add") %>',get_params(document.flows));"/>	
	</fieldset>
</form>
