<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="java.net.*"%>
<%@ include file = "inc/defs.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Load Process</title>
</head>
<%
String redirectURL = fdFormData.getParameter("process_url");

redirectURL = redirectURL.replace("http://", "");
redirectURL = redirectURL.replace("https://", "");
redirectURL = redirectURL.replace("//", "");
//redirectURL = redirectURL.replace("/", "");

//URI testURI = new URI(redirectURL);
//URI homeURI = new URI(request.getRequestURI());
//if(testURI.getHost()!=null && !testURI.getHost().equals(homeURI.getHost()))
//      redirectURL = null;
%>
<body style="padding: 50px 0 0 50px;" onLoad="this.location='<%=response.encodeURL(redirectURL)%>'">
<img src="images/loading.gif"/>
</body>
</html>
