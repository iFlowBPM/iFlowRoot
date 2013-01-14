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
  