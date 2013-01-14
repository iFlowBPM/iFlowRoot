<%@ include file="logs.jsp"%>
<%
  final int nLIMIT = 200;
  List<UpgradeLogTO> logs = pm.getUgradeLogs(userInfo, nLIMIT);
%>
<div class="table_inc">
	<div style="float: right; text-align: right;">
		<if:message string="admin-logs.upgradables.table.footer.results" />:&nbsp;<%=logs.size() > nLIMIT ? "+" + nLIMIT : logs.size() %>
		<br />
		<if:message string="admin-logs.upgradables.table.footer.max" />:&nbsp;<%=nLIMIT %>
	</div>
	<table class="item_list">
		<tr class="tab_header">
			<td><if:message string="admin-logs.upgradables.table.header.date" /></td>
			<%--<td><if:message string="admin-logs.upgradables.table.header.user" /></td>--%>
			<td><if:message string="admin-logs.upgradables.table.header.signature" /></td>
			<td><if:message string="admin-logs.upgradables.table.header.executed" /></td>
			<td><if:message string="admin-logs.upgradables.table.header.error" /></td>
		</tr>
		<% for (int i = 0; i < logs.size() && i < nLIMIT; i++) {
		    UpgradeLogTO log = logs.get(i);

		    Date dCreated = new Date(log.getLog().getCreationDate().getTime());
		    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
		    String sCreated = formatter.format(dCreated);

		    //String sUser = log.getLog().getUsername();
		    String sSignature = log.getSignature();

		    String imgTpl = "<img border=\"0\" width=\"16\" height=\"16\" src=\"images/{0}\" alt=\"{1}\" title=\"{1}\" class=\"toolTipImg\">";
		    String sExecuted = log.getExecuted() ?
		        java.text.MessageFormat.format(imgTpl, new Object[] { "icon_ok.png", messages.getString("admin-logs.upgradables.table.executed") }) : "";
		    String sError = log.getError() ?
		        java.text.MessageFormat.format(imgTpl, new Object[] { "box_warn.png", messages.getString("admin-logs.upgradables.table.error") }) : "";
		    %>
		    
		    <tr class="<%=((i % 2) == 0) ? "tab_row_even" : "tab_row_odd"%>">
				<td><c:out value='<%=sCreated %>'></c:out></td>
				<%--<td><c:out value='<%=sUser %>'></c:out></td>--%>
				<td><c:out value='<%=sSignature %>'></c:out></td>
				<td class="itemlist_icon"><%=sExecuted %></td>
				<td class="itemlist_icon"><%=sError %></td>
			</tr>
		<% } %>
	</table>
</div>