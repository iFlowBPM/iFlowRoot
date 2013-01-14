<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %><%
	String sPage = "Admin/flow_settings_doimport";
int flowid = 0;
%>
<%@ include file = "auth.jspf" %>
<%

StringBuffer sbError = new StringBuffer();

String stmp = null;
byte[] buffer = null;
String sOp = null;
String fileName = null;
String sError = null;
String sSave = null;
String sFlowName = null;

try {
  flowid = Integer.parseInt(fdFormData.getParameter(DataSetVariables.FLOWID));
  sOp = fdFormData.getParameter("op");
  sFlowName = fdFormData.getParameter("flowname");
  FormFile formFile = fdFormData.getFileParameter("file");
  if(null == formFile) throw new Exception("Could not retrieve file argument.");
  fileName = formFile.getFileName();
  buffer = formFile.getData();

}
catch (Exception e) {
  Logger.errorJsp(login,sPage,"exception processing form: " + e.getMessage());
  sOp = null;
}

Flow flow = BeanFactory.getFlowBean();

//if (flow == null || sOp == null || sFlowName == null || (sOp != null && !sOp.equals("3"))) {
//      ServletUtils.sendEncodeRedirect(response, sNextPage);
//	return;
//}

if (fileName == null || fileName.equals("")) {
	sError = messages.getString("flow_settings_doimport.error.selectFile");
}
else {
  // import here
  sError = flow.importFlowSettings(userInfo, flowid, buffer);

  if (sError != null && !sError.equals("")) {
    sError = messages.getString("flow_settings_doimport.error", fileName) + "<br>";
  }
  else {
    sSave = messages.getString("flow_settings_doimport.msg.success", fileName);
    // reset vars
    fileName = null;
  }
  
}
out.println(sError);
out.println(sSave);
%>
