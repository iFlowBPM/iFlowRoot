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
