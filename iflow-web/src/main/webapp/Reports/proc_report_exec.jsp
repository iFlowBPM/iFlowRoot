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
