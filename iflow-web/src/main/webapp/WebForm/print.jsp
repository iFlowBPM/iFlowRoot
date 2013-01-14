<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "forminc.jspf" %><%
	int nField = -1;

try {
  nField = Integer.parseInt(sField);
}
catch (Exception e) {
}

if (nField == -1) {
  String sSTYLESHEET_SUFFIX = ".xsl";
  String sTEMPLATE_SUFFIX = ".fo";
  
  // 10: print stylesheet attribute
  String sPrintStyleSheet = (String)bBlock.execute(10,null);
  if (StringUtils.isNotEmpty(sPrintStyleSheet)) {
    if (sPrintStyleSheet.toLowerCase().endsWith(sTEMPLATE_SUFFIX)) {
%><%@ include file = "printFOP.jspf" %><%
	}
    else {
%><%@ include file = "printXSL.jspf" %><%
	}
  }
}
else {
  // printing field...force xsl printing
%><%@ include file = "printXSL.jspf" %><%
}
%>
