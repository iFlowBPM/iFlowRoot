<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../inc/defs.jsp"%>
<%
    String flowid = fdFormData.getParameter("flowid");
    String pid = fdFormData.getParameter("pid");
    String subpid = fdFormData.getParameter("subpid");
    String sOp = fdFormData.getParameter("op");
    if (sOp == null) {
        sOp = "0";
    }
    int op = Integer.parseInt(sOp);
%>
  <div id='popupFrame' name='popupFrame' style="display:none">
	<div class="hd">Popup</div><div class="bd"><div class="dialogcontent"><div id="helpwrapper" class="help_box_wrapper"><div id="helpsection" class="help_box">
	  <iframe onload="parent.calcFrameHeight('open_proc_frame_popup');" id="open_proc_frame_popup" name="open_proc_frame_popup" frameborder="0" scrolling="auto" marginheight="0" marginwidth="0" width="100%" height="100%" class="open_proc_frame" style="display:block;" 
	  		  src="Form/form.jsp?flowid=<%=flowid%>&pid=<%=pid%>&subpid=<%=subpid%>&op=<%=op%>" >
	  </iframe>
	</div></div></div></div>
  </div>
  <script language="JavaScript" type="text/javascript">
    parent.showPopup(document.getElementById('popupFrame').innerHTML);
  </script>
