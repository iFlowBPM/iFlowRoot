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
