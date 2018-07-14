<%@ include file="logs.jsp"%>
<%!
private File[] sortFiles(File[] files) {
  List<File> retObj = new ArrayList<File>();
  for (File file : files) {
    int pos = 0;
    for (File sortedFile : retObj) {
      String fn = file.getName();
      String sfn = sortedFile.getName();
      if ((fn.contains("_") && !sfn.contains("_"))
          || (((fn.contains("_") && sfn.contains("_")) || !sfn.contains("_"))
              && file.getName().compareToIgnoreCase(sfn) < 0)) {
        break;
      }
      pos++;
    }
    retObj.add(pos, file);
  }
  return (File[]) retObj.toArray(new File[retObj.size()]);
}
%>
<%
List<NameValuePair<String, File>> logs = new ArrayList<NameValuePair<String, File>>();
File logDir = new File((Const.sIFLOW_HOME + "/log").replace("//", "/"));
if (logDir.isDirectory()) {
  
}
%>
<div class="table_inc">
	<table class="item_list">
		<tr class="tab_header">
			<td><if:message string="admin-logs.file.table.header.name" /></td>
			<td><if:message string="admin-logs.file.table.header.date" /></td>
			<td><if:message string="admin-logs.file.table.header.download" /></td>
		</tr>
		<% for (int i = 0, l = logs.size(); i < l; i++) {
		    NameValuePair<String, File> log = logs.get(i);
		    String sName = log.getValue().getName();
		    Date dModified = new Date(log.getValue().lastModified());
		    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		    String sModified = formatter.format(dModified);
		    %>
		    <tr class="<%=((i % 2) == 0) ? "tab_row_even" : "tab_row_odd"%>">
			
			</tr>
		<% } %>
	</table>
</div>