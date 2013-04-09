<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Load Process</title>
</head>
<body style="padding: 50px 0 0 50px;" onLoad="this.location='<%=response.encodeURL(fdFormData.getParameter("process_url"))%>'">
<img src="images/loading.gif"/>
</body>
</html>
