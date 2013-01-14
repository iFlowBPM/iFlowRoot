<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file="../../inc/defs.jsp"%>
<%@page import="pt.iflow.servlets.ResourceNavConsts"%>
<if:checkUserAdmin type="both">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>
<%

String type = fdFormData.getParameter("type");
String filename = fdFormData.getParameter("filename");
FormFile file = fdFormData.getFileParameter("file");

if(StringUtils.isEmpty(filename)) {
  filename = file.getFileName();
  // eh necessario "sanar" o path
  filename = filename.replace('\\','/');
  int slashPos = filename.lastIndexOf('/');
  if(slashPos >= 0) {
    filename = filename.substring(slashPos + 1);
  }
}

Logger.debugJsp(userInfo.getUtilizador(), "doupload.jsp", "Type: " + type);
Logger.debugJsp(userInfo.getUtilizador(), "doupload.jsp", "formfile: " + filename);

Repository rep = BeanFactory.getRepBean();

if(ResourceNavConsts.STYLESHEETS.equals(type)) {
  rep.setStyleSheet(userInfo, filename, file.getData());
} else if(ResourceNavConsts.PRINT_TEMPLATES.equals(type)) {
  rep.setPrintTemplate(userInfo, filename, file.getData());
} else if(ResourceNavConsts.EMAIL_TEMPLATES.equals(type)) {
  rep.setEmailTemplate(userInfo, filename, file.getData());
} else if(ResourceNavConsts.PUBLIC_FILES.equals(type)) {
  rep.setWebFile(userInfo, filename, file.getData());
} else {
  Logger.debugJsp(userInfo.getUtilizador(), "doupload.jsp", "Deu erro");
}



%><if:message string="upload.complete"/>
