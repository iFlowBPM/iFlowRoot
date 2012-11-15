<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%@page import="java.util.Date" %>
<%@page import="pt.iflow.api.filters.FlowFilter"%>
<iframe onload="calcFrameHeight('open_proc_frame_search');" name="open_proc_frame_search" id="open_proc_frame_search" scrolling="auto" height="100%" width="100%" frameborder="0" src="" class="open_proc_frame_colapsed" style="display:none;">
  your browser does not support iframes or they are disabled at this time
</iframe>
<div id="advanced_search_message">
  <div style="vertical-align: middle;">
    <img src="images/icon_tab_tarefas.png" class="icon_item"/>
    <h1><if:message string="user_procs.title" /></h1>
  </div>
  <div class="info_msg">
    <%=messages.getString("user_procs.msg.select.advancedSearch")%>
  </div>
</div>

