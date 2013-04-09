<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file="../../inc/defs.jsp"%>
<%
	boolean refresh = false;
	String docid = fdFormData.getParameter("docid");
	PassImage pi = BeanFactory.getPassImageManagerBean();
	
	FormFile assFile = fdFormData.getFileParameter("assFile");
	if (assFile != null && assFile.getData() != null && StringUtils.isNotEmpty(assFile.getFileName())) {
	  pi.saveImage(userInfo, pi.getImageIconRepFromImage(assFile.getData()));
	  refresh = true;
	}

	FormFile rubFile = fdFormData.getFileParameter("rubFile");
	if (rubFile != null && rubFile.getData() != null && StringUtils.isNotEmpty(rubFile.getFileName())) {
	  pi.saveRubrica(userInfo, pi.getImageIconRepFromImage(rubFile.getData()));
	}
%>

<script>
	<%if (refresh) {%>
	window.opener.location.reload();
	<%}%>
	this.close();
</script>