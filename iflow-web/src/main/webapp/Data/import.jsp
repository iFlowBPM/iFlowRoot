<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<%@ page import = "pt.iflow.api.blocks.Block" %>
<%@ include file = "../inc/initProcInfo.jspf" %>
<%@ include file = "../inc/checkProcAccess.jspf" %>
<%
	String title = "SpreadSheet Import";

Block bBlock = null;

String sHtml = null;

Flow flow = BeanFactory.getFlowBean();
try {

  bBlock = flow.getBlock(userInfo, procData);

  if (bBlock.getClass().getName().indexOf("BlockDataImport") == -1) {
    throw new Exception("Not BlockDataImport!");
  }
  
  sHtml = (String) bBlock.execute(2, new Object[]{userInfo, procData, new ServletUtils(response)});
}
catch (Exception e) {
  Logger.debugJsp(userInfo.getUtilizador(), "Data/import.jsp", "Excepção. Ver log.");
  e.printStackTrace();
  // send to main page...
  // not able to get flow or process is not in jsp state (if
  // a casting exception occurs..)
  ServletUtils.sendEncodeRedirect(response, sURL_PREFIX+"flow_error.jsp");
  return;
}
%>

<%= sHtml %>

<%@ include file="../inc/initProcInfoEndPage.jspf"%>
