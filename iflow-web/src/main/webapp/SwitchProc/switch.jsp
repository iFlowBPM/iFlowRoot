<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<%@ page import = "pt.iflow.api.blocks.Block" %>
<%@ include file = "../inc/initProcInfo.jspf" %>
<%@ include file = "../inc/checkProcAccess.jspf" %>
<%
	Flow flow = BeanFactory.getFlowBean();
String sNextPage = "error.jsp";

try {
  sNextPage = flow.nextBlock(userInfo, procData);

}
catch (Exception e) {
  Logger.errorJsp(user, "switch.jsp", "Exsception occured while switching process: "+e.getMessage(), e);
}

sNextPage = response.encodeRedirectURL(sURL_PREFIX + sNextPage+ "&ts=" + ts) ;
ServletUtils.sendEncodeRedirect(response, sNextPage);
return;
%>