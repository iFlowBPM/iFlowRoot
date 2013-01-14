<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import = "pt.iflow.api.blocks.Block" %>
<%@ include file = "inc/defs.jsp" %>
<%
String title = "";


messages.getString("nopriv.title");

String pnumber = messages.getString("blockmsg.undefined");
if (fdFormData.getParameter("pnumber") != null) {
	pnumber = fdFormData.getParameter("pnumber");
}

String msgCode = messages.getString("blockmsg.undefined");
if (fdFormData.getParameter("msgcode") != null) {
	msgCode = fdFormData.getParameter("msgcode");
}

String[] saParams = { pnumber };

String level = "error";
String msg = "";

if (Block.MSG_CODES.WAITING_JOIN.getCode().equals(msgCode)) {
	msg = messages.getString("blockmsg.error.waitingJoin", saParams);
	level = "warning";
}
else if (Block.MSG_CODES.CANNOT_PROCEED.getCode().equals(msgCode)) {
	msg = messages.getString("blockmsg.error.cannotProceed", saParams);
	level = "error";	
}
else {
	msg = messages.getString("blockmsg.error.ok", saParams);
	level = "info";		
}

%>

<%@ include file = "inc/process_top.jspf" %>
<div class="<%=level%>_msg">
    	<%=msg%>
</div>
<%@ include file = "inc/process_bottom.jspf" %>

