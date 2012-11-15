<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<%@page import="java.util.Date" %>
<%@page import="pt.iflow.api.filters.FlowFilter"%>
<%@ page import = "pt.iflow.chart.AuditChartServlet" %>
<script type="text/javascript" src="../javascript/iflow_main.js"></script>
<iframe onload="calcFrameHeight('open_proc_frame_report');" name="open_proc_frame_report" id="open_proc_frame_report" scrolling="auto" height="100%" width="100%" frameborder="0" src="" class="open_proc_frame_colapsed" style="display:block;">
  your browser does not support iframes or they are disabled at this time
</iframe>
<%
	StringBuilder params = new StringBuilder();
	for (String name : fdFormData.getParameters().keySet()) {
	  if (params.length()>0) params.append("&");
	  params.append(name);
	  params.append("=");
	  params.append(fdFormData.getParameter(name));
	}
%>
<script language="JavaScript" type="text/javascript">
	open_process_report_exec('/iFlow/inicio_flow.jsp', '<%=params.toString()%>');
</script>
