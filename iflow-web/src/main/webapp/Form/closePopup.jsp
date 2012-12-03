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
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../inc/defs.jsp"%>
<%

    String cancel = fdFormData.getParameter("cancelPopup");
    if (cancel != null && "true".equals(cancel)){
      ProcessData procDataBackup = (ProcessData)session.getAttribute(Const.SESSION_PROCESS_POPUP_BACKUP + flowExecType);
      session.setAttribute(Const.SESSION_PROCESS + flowExecType, procDataBackup);

      session.removeAttribute(Const.SESSION_PROCESS_POPUP_BACKUP + flowExecType);
    }

    String flowid = fdFormData.getParameter("flowid");
    String pid = fdFormData.getParameter("pid");
    String subpid = fdFormData.getParameter("subpid");
    String sOp = fdFormData.getParameter("op");
    if (sOp == null) {
        sOp = "0";
    }
    int op = Integer.parseInt(sOp);
%>
  <script language="JavaScript" type="text/javascript">
  	parent.hidePopup();
	var myframe = parent.document.getElementById('open_proc_frame');
	myframe.style.display = "block";
	myframe.src = 'process_load.jsp?process_url=' + parent.escape('Form/form.jsp?tabnr=3&flowid=<%=flowid%>&pid=<%=pid%>&subpid=<%=subpid%>&op=<%=op%>'); 
  </script>
