<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>

<% 
String title = messages.getString("nop.title");
int flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
%>

<%@ include file = "../inc/process_top.jspf" %>
<div class="info_msg" style="font-family: Verdana,Arial,sans-serif;">
  <if:message string="nop.msg"/>
</div>
<% out.println(ProcessEndDisplay.processTasks(userInfo, response, flowid)); %>
<%@ include file = "../inc/process_bottom.jspf" %>

