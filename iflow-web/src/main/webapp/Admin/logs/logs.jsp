<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../../inc/defs.jsp"%>
<%@ page import="org.apache.commons.lang.math.NumberUtils"%>

<if:checkUserAdmin type="both">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess" /></div>
</if:checkUserAdmin>

<%
final int nLogFiles = 0;
final int nLogUpgradables = 1;

String sPage = "Admin/logs/";

final String sPageLogsFile = response.encodeURL(sPage + "logs_file.jsp" + "?type=" + nLogFiles);
final String sPageLogsUpgradables = response.encodeURL(sPage + "logs_upgradables.jsp" + "?type=" + nLogUpgradables);

String errMsg = request.getParameter("errMsg");

String title = messages.getString("admin-logs.title");

int nLogType = -1;
String sLogType = request.getParameter("type");
if (NumberUtils.isNumber(sLogType)) {
  nLogType = NumberUtils.toInt(sLogType);
}
%>
<script type="text/javascript">
  	function changeLogType(el) {
  		if (el.value == '<%=nLogFiles %>') {
  			tabber_save(4,'','sel=<%=AdminNavConsts.LOGS %>','<%=sPageLogsFile %>','ts=<%=ts%>');
  		} else if (el.value == '<%=nLogUpgradables %>') {
  			tabber_save(4,'','sel=<%=AdminNavConsts.LOGS %>','<%=sPageLogsUpgradables %>','ts=<%=ts%>');
  		}
  	}
</script>
<h1 id="title_admin"><%=title%></h1>
<div style="float: left; margin-left: 34px; margin-top: 20px;">
	<select id="log_type" name="log_type" onchange="javascript:changeLogType(this);">
		<option value="-1"><%=messages.getString("admin-logs.choose") %></option>
		<option value="0" <%=nLogType == nLogFiles ? "selected=\"selected\"" : ""%>>Files</option>
		<option value="1" <%=nLogType == nLogUpgradables ? "selected=\"selected\"" : ""%>>Upgradables</option>
	</select>
</div>

<% if (StringUtils.isNotBlank(errMsg)) { %>
	<div class="error_msg"><%=errMsg %></div>
<% } %>
