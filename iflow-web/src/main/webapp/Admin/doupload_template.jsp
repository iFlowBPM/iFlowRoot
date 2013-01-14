<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@page import="pt.iflow.api.xml.codegen.flow.XmlFlow"%>
<%@page import="pt.iflow.api.xml.FlowMarshaller"%>
<%@ include file="../inc/defs.jsp"%>
<if:checkUserAdmin type="sys">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>
<%

FormFile file = fdFormData.getFileParameter("file");

byte [] data = file.getData();

try {
  XmlFlow xflow = FlowMarshaller.unmarshal(data);
  String name = xflow.getName()+".xml";
  String description = xflow.getDescription();
  if(StringUtils.isEmpty(description) || "--".equals(description)) { // old flow
    description = name;
    name = file.getFileName();
  }
  
  BeanFactory.getFlowHolderBean().uploadFlowTemplate(userInfo, name, description, data);
} catch (Throwable t) {
  t.printStackTrace();
}

%><if:message string="upload.complete"/>
