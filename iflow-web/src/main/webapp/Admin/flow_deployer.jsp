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
<%@ include file = "../inc/defs.jsp" %><%
	String sPage = "Admin/flow_deployer";
int flowid = 0;
%>
<%@ include file = "auth.jspf" %>
<%

String sNextPage = "/Admin/flow_settings.jsp";
String result = messages.getString("flow_deployer.invalid_flow");

try {
  flowid = Integer.parseInt(fdFormData.getParameter(DataSetVariables.FLOWID));
} catch(Throwable t) {
  flowid = -1;
}

if(flowid != -1) {
  String sAction = fdFormData.getParameter("action");

  FlowHolder flowHolder = BeanFactory.getFlowHolderBean();
  Flow flow = BeanFactory.getFlowBean();
  try {
	  if("deploy".equals(sAction)) {
	    result = flow.deployFlow(userInfo, flowHolder.getFlowFileName(userInfo, flowid));
	  } else if("undeploy".equals(sAction)) {
	    result = flow.undeployFlow(userInfo, flowHolder.getFlowFileName(userInfo, flowid));
	  } else {
	    result = messages.getString("flow_deployer.unknown_action");
	  }
  }
  catch (Exception e) {
    Logger.errorJsp(user, sPage, "Error deploying/undeploying flow "+flowid, e);
    result = messages.getString("flow_deployer.error");
  }
}

request.setAttribute("deployResult", result);
application.getRequestDispatcher(sNextPage).forward(request,response);
%>
