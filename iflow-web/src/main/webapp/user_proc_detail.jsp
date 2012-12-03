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
<%@ include file = "inc/defs.jsp" %>
<%
String sFlowId = fdFormData.getParameter("flowid");
String sPid = fdFormData.getParameter("pid");
String sSubPid = fdFormData.getParameter("subpid");
String status = fdFormData.getParameter("procStatus");
String detailURL = response.encodeURL(sURL_PREFIX+"/Form/detail.jsp?flowid="+sFlowId+"&pid="+sPid+"&subpid="+sSubPid+"&procStatus="+status+"&fwSearch=true");
if(sPid != null && sSubPid != null){
  session.setAttribute("filtro_pid",sPid);
  session.setAttribute("filtro_subpid",sSubPid);
}
String scroll = (String) fdFormData.getParameter("scroll");
if(scroll != null) 
  session.setAttribute("filtro_scroll",scroll);
%>
<form name="form_proc_detail" action="#" method="POST">
<input type="hidden" name="flowid" value="<%=sFlowId%>">
<input type="hidden" name="pid" value="<%=sPid%>">
<input type="hidden" name="subpid" value="<%=sSubPid%>">
<input type="hidden" name="procStatus" value="<%=status%>">

<iframe onload="calcHeight();" name="proc_detail" id="iframe_proc_detail" scrolling="auto" height="1" width="100%" frameborder="0" src="<%=detailURL%>">
<!--<iframe onload="resizeProcDetail()" scrolling="auto" frameborder="0" name="proc_detail" id="iframe_proc_detail" src="<%=detailURL%>">-->
</iframe>
<div id="buttons_proc_detail">
<fieldset class="submit">
<% if(status.equals("-2")){ %>

  <input class="regular_button_01" type="button" name="back" value="Fechar" 
  onClick="javascript:tabber('<%=response.encodeURL("main.jsp")%>');"/>
  
<%} else if(status.equals("-3")){ %>

  <input class="regular_button_01" type="button" name="back" value="Fechar" 
  onClick="javascript:tabber('inbox','','',inboxJSP);"/>
  
<%} else {%> 
  <input class="regular_button_01" type="button" name="back" value="<if:message string="button.search"/>" 
  onClick="javascript:tabber_right(8, '<%=response.encodeURL("user_procs.jsp")%>', get_params(document.user_procs_filter));"/>
  
  <input class="regular_button_01" type="button" name="back" value="<if:message string="button.proc_hist"/>" 
  onClick="javascript:tabber_right(8, '<%=response.encodeURL("user_proc_tasks.jsp")%>', get_params(document.form_proc_detail));"/>
<% } %>
</fieldset>
</div>
</form>
