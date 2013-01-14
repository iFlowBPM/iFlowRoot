<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<%@ page import = "java.io.PrintStream" %>

<%
try {

  Flow flow = BeanFactory.getFlowBean();

  int flowid = Integer.parseInt(fdFormData.getParameter(DataSetVariables.FLOWID));

  response.reset();
  response.setContentType("application/octet-stream");
  response.addHeader("Content-Disposition","attachment;filename=flow" + flowid + "_settings.ufs");

  flow.exportFlowSettings(userInfo, flowid, new PrintStream(response.getOutputStream()));

}
catch (Exception e) {
%>
  <script language="JavaScript">
  <!--
    self.close();
  // -->
  </script>
<%
  return;
}

%>

