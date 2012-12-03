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
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../inc/defs.jsp"%>
<%
    String flowid = fdFormData.getParameter("flowid");
    String pid = fdFormData.getParameter("pid");
    String subpid = fdFormData.getParameter("subpid");
    String sOp = fdFormData.getParameter("op");
    if (sOp == null) {
        sOp = "0";
    }
    int op = Integer.parseInt(sOp);
%>
  <div id='popupFrame' name='popupFrame' style="display:none">
	<div class="hd">Popup</div><div class="bd"><div class="dialogcontent"><div id="helpwrapper" class="help_box_wrapper"><div id="helpsection" class="help_box">
	  <iframe onload="parent.calcFrameHeight('open_proc_frame_popup');" id="open_proc_frame_popup" name="open_proc_frame_popup" frameborder="0" scrolling="auto" marginheight="0" marginwidth="0" width="100%" height="100%" class="open_proc_frame" style="display:block;" 
	  		  src="Form/form.jsp?flowid=<%=flowid%>&pid=<%=pid%>&subpid=<%=subpid%>&op=<%=op%>" >
	  </iframe>
	</div></div></div></div>
  </div>
  <script language="JavaScript" type="text/javascript">
    parent.showPopup(document.getElementById('popupFrame').innerHTML);
  </script>
