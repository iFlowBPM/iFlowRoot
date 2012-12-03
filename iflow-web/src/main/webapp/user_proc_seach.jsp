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

