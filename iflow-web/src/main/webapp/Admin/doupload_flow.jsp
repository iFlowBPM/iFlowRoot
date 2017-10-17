<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file="../inc/defs.jsp"%>
<%@page import="pt.iflow.servlets.ResourceNavConsts"%>
<%@page import="pt.iflow.api.xml.codegen.flow.XmlFlow"%>
<%@page import="pt.iflow.api.xml.FlowMarshaller"%>
<if:checkUserAdmin type="both">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>
<%

String isSubflow = fdFormData.getParameter("is_subflow");
String createVersion = fdFormData.getParameter("create_version");
String versionNote = fdFormData.getParameter("version_note");
FormFile file = fdFormData.getFileParameter("file");

// do not create version
if(!"yes".equals(createVersion)) versionNote=null;

Logger.debugJsp(userInfo.getUtilizador(), "doupload_flow.jsp", "formfile: " + file.getFileName());

byte [] data = file.getData();

// parse xml to extract flow (file) name and description
XmlFlow xmlFlow = FlowMarshaller.unmarshal(data);
String flowName = xmlFlow.getName();
String flowDesc = xmlFlow.getDescription();

Logger.debugJsp(userInfo.getUtilizador(), "doupload_flow.jsp", "flowName: " + flowName);
Logger.debugJsp(userInfo.getUtilizador(), "doupload_flow.jsp", "flowDesc: " + flowDesc);

// write flow to DB
FlowHolder holder = BeanFactory.getFlowHolderBean();
int result;
if("yes".equals(isSubflow))
	result = holder.writeSubFlowData(userInfo, flowName, flowDesc, data);
else
	result = holder.writeFlowData(userInfo, flowName, flowDesc, data, versionNote);

%><if:message string="upload.complete"/>
