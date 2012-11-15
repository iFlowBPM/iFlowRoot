<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%@ include file = "inc/batchProcessing.jspf" %>

<%
	String sNextPage = "error.jsp";
if (hmSessionBatch != null && alSessionBatchPids != null && hmSessionBatchLinks != null &&
    alSessionBatchPids.size() > 0 && hmSessionBatchLinks.size() > 0) {
      
  String sFP = (String)alSessionBatchPids.get(0);
  String sFPURL = (String)hmSessionBatchLinks.get(sFP);
      
  if (sFPURL != null) {
    alSessionBatchPids.remove(0);
    hmSessionBatchLinks.remove(sFP);
    sNextPage = sFPURL;
  }
}
ServletUtils.sendEncodeRedirect(response, sURL_PREFIX + sNextPage+ "&ts=" + ts) ;
%>