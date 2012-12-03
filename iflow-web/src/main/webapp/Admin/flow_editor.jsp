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
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>

<h1 id="title_admin"><if:message string="flow_editor.title"/></h1>

  <p class="info_msg"><if:message string="flow_editor.msg.welcome"/></p>
  <fieldset>
    <ol>
      <li>
        <label><if:message string="flow_editor.label.editorJar"/></label>
        <a href="<%=response.encodeURL("PublicFiles/FlowEditor.jar")%>">floweditor.jar</a>
      </li>
      <li>
        <label><if:message string="flow_editor.label.editorWin"/></label>
        <a href="<%=response.encodeURL("PublicFiles/FlowEditor.exe")%>">floweditor.exe</a>
      </li>
      <li>
        <label><if:message string="flow_editor.label.editorDoc"/></label>
        <a href="<%=response.encodeURL("PublicFiles/floweditormanual.pdf")%>">floweditormanual.pdf</a>
      </li>
      <li>
      <%
        String pre = "<span style=\"white-space: nowrap; color: black; font-weight: bold; \">";
        String pos = "</span>";
        
        String url = pre + Const.APP_PROTOCOL + "://" + Const.APP_HOST;
        if (Const.APP_PORT != 80) {
          url += ":" + Const.APP_PORT;
        }
        url += Const.APP_URL_PREFIX + pos;
        
        String message = "";
        UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
        if (userInfo != null) {
          String name = pre + userInfo.getUtilizador() + pos;
          message = userInfo.getMessages().getString("flow_editor.label.editorDsc", name, url);
        }
      %>
        <p><%=message %></p>
        <img src="images/iflow4.login.png" alt="Flow Editor" style="width: auto;" />
      </li>
	  </ol>
  </fieldset>
  <fieldset class="submit"/>
  <if:generateHelpBox context="flow_editor"/>
  
