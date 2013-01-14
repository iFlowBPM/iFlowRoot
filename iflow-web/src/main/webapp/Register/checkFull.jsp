<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="pt.iflow.api.utils.Const"%>
<%-- This is used only in registration stuff --%>
<% 
	String sIsFull = request.getParameter("full");
	boolean bIsFull = true;
	if(!StringUtils.isEmpty(sIsFull) &&
			"false".equals(sIsFull))
		bIsFull = false;
	Boolean isSystemAdmin = (Boolean) request.getAttribute("isSystemAdmin");
	if(null == isSystemAdmin) isSystemAdmin = Boolean.FALSE;
	request.setAttribute("bUseEmail", Const.bUSE_EMAIL);
	request.setAttribute("bEdit", true);
%>