<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<%@ page import = "pt.iflow.api.notification.EmailManager" %>

<if:checkUserAdmin type="both">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%
	String title = messages.getString("admin-emailcache.title"); 
String sPage = "Admin/emailcache";

StringBuffer sbError = new StringBuffer();
int flowid = -1;

String sOp = fdFormData.getParameter("op");
if (sOp == null) sOp = "0";
int op = Integer.parseInt(sOp);

String sHtml = "&nbsp;";

if (op == 0) {
  sHtml = "<div class=\"info_msg\">" + messages.getString("admin-emailcache.msg.confirm") + "</div>";  
}
else if (op == 1) {
  try {
    EmailManager.resetEmailCache();
    sHtml = "<div class=\"info_msg\">" + messages.getString("admin-emailcache.msg.success") + "</div>";
  }
  catch (Exception e) {
    sHtml = "<div class=\"error_msg\">" + messages.getString("admin-emailcache.error.clear") + "</div>";
  }
}
%>

<%@ include file = "auth.jspf" %>

<form name="emailcache" method="POST">
<input type="hidden" name="op" value="0">

	<h1 id="title_admin"><%=title%></h1>
	
	<div class="table_inc">  

<% if (sbError.length() > 0) { %>
		<div class="error_msg">
			<%=sbError.toString()%>
		</div>
<% } %>
		<table>
			<tr>
			  <td class="info_msg">
				<%=sHtml%>
			  </td>
			</tr>
		</table>
	    <div class="button_box">
	     	<input class="regular_button_02" type="button" name="clear_cache" value="<%=messages.getString("button.clearcache")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/emailcache.jsp") %>','op=1');"/>
	    </div>

	</div>
	
</form>



