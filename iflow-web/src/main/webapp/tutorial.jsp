<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.utils.Utils"%>
<%@ page import="pt.iflow.api.errors.IErrorHandler"%>
<%@ page import="pt.iflow.api.errors.ErrorCode"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.msg.IMessages" %>
<%@ page import="pt.iflow.api.errors.UserErrorCode"%>
<%@ include file = "inc/defs.jsp" %>
<%

	UserInfoInterface ui = (UserInfoInterface)session.getAttribute(Const.USER_INFO);

    // sel is defined in left navigation links
    String sSel = fdFormData.getParameter("sel");
    if ("2".equals(sSel) || "3".equals(sSel)) {
      out.println("<div class=\"info_msg\">" + messages.getString("iflow.msg.notImplemented") + "</div>");
      return; 
    }

	  String title = messages.getString("personal_account.title");
      request.setAttribute("title",title);
      
      boolean isTut = ui.getUserSettings().isTutorialMode();
      String linkName = isTut?messages.getString("tutorial.deactivate"):messages.getString("tutorial.activate");
      String functionName = (isTut?"tutorialModeOn();":"tutorialModeOff();") + "tabber(6,'personal_account_nav.jsp','sel=4','tutorial.jsp','sel=4');";
%>

<form method="post" name="userform">

  <div style="vertical-align: middle;">
    <img src="images/icon_tab_account.png" class="icon_item"/>
    <h1><c:out value="${title}" escapeXml="false"/></h1>
  </div>

    <fieldset>
      <ol>
      	<li><a href="javascript:<%=functionName%>"><%=linkName%></a></li>
      </ol>
    </fieldset>
</form>
