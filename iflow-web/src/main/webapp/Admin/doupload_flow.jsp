<!--
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
-->
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
int result = holder.writeFlowData(userInfo, flowName, flowDesc, data, versionNote);

%><if:message string="upload.complete"/>
