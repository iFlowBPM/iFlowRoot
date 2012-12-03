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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/errorhandler.jsp"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../inc/defs.jsp"%>
<%@ page import="pt.iflow.processannotation.ProcessAnnotationManagerBean"%>
<%@page import="pt.iflow.api.processannotation.*"%>

<%
int flowid = -1;
int pid = -1;
int subpid = -1;
try {
    // use of fdFormData defined in /inc/defs.jsp
  flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
  pid = Integer.parseInt(fdFormData.getParameter("pid"));
  String sSubPid = fdFormData.getParameter("subpid");
  //String pnumber = fdFormData.getParameter("pnumber");
  if (StringUtils.isEmpty(sSubPid)) {
    subpid = 1;
  } else {
    subpid = Integer.parseInt(sSubPid);
  }
} catch (Exception e) {
  e.printStackTrace();
}

ProcessAnnotationManager pam = BeanFactory.getProcessAnnotationManagerBean();
String sDeadline = pam.getProcessDeadline(userInfo,flowid,pid,subpid);
ProcessComment comment = pam.getProcessComment(userInfo,flowid,pid,subpid);
List<ProcessLabel> labels = pam.getLabelJoin(userInfo,flowid,pid,subpid);

String sHideButtonId = fdFormData.getParameter("hidebuttonid");
if (sHideButtonId == null || "".equals(sHideButtonId)){
  sHideButtonId = "hide_annotation";
}
%>

<div id="apt_popup_div">
    <h2>Processo: <%=pid %></h2>
    <ul class="apt_reg">
    	<li style="font-size:1.2em;color:black;margin-left:0px;margin-bottom:10px"><if:message string="process_annotations.field.comment" /></li>
        <li><%=comment.getComment()%></li>
    </ul>

    <hr class="apt_sep"/>
    <ul class="apt_reg">
    	<li style="font-size:1.2em;color:black;margin-left:0px;margin-bottom:10px"><if:message string="process_annotations.field.labels" /></li>
        <li>
        <% for(int i=0; i < labels.size(); i++){ 
             if(labels.get(i).getCheck()){%>
             <img src="AnnotationIconsServlet?label_name='<%=labels.get(i).getName()%>'&ts='+<%=System.currentTimeMillis() %>+'" width="16px" height="16px"/>
                <%=labels.get(i).getName()%><br/>
        <%   }
           }%>
        </li>
    </ul>
    <hr class="apt_sep"/>
    <ul class="apt_reg">
    	<li style="font-size:1.2em;color:black;margin-left:0px;margin-bottom:10px"><if:message string="process_annotations.field.deadline" /></li>
        <li><%=sDeadline%></li>
    </ul>
    <input type="button" class="apt_regular_button" value="<if:message string="button.close"/>" id="<%=sHideButtonId%>"/>
</div>
