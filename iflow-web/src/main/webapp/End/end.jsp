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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"
%><%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"
%><%@ include file = "../inc/defs.jsp"
%><%@ include file = "../inc/batchProcessing.jspf"
%><%@ page import="pt.iflow.api.blocks.MessageBlock"
%><%@ page import="pt.iflow.api.blocks.Block"
%><%
    String title = messages.getString("end.title");
    
    String sBatchLink = "";
    String sColspan = "";
    String sTDBefore = "<td>";
    
    if (hmSessionBatch != null && alSessionBatchPids != null
            && hmSessionBatchLinks != null
            && alSessionBatchPids.size() > 0
            && hmSessionBatchLinks.size() > 0) {
        
        sColspan = "colspan=\"5\"";
        
        sTDBefore = "<td></td>";
        sTDBefore += "<td>";
        
        sBatchLink = "<td></td>";
        sBatchLink += "<td><a href=\""
                + response.encodeURL("../processBatchProc.jsp") + "\">"
                + messages.getString("end.link.nextBatchProcess")
                + "</a></td>";
        
        sBatchLink += "<td></td>";
    }
    
    int flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
    int pid = Integer.parseInt(fdFormData.getParameter("pid"));
    int subpid = 1;
    int blockId = -1;
    String bidParam = fdFormData.getParameter("bid");
    if (bidParam != null && !bidParam.equals("")) {
   		try {
      		blockId = Integer.parseInt(bidParam);
   		}
   		catch (NumberFormatException nfe) {   		  
   		}
    }
    String cancelParam = fdFormData.getParameter("c");
    
    
    String sOutput = "";
    String msgBlock = null;
    
	if (blockId > -1) {
    	Flow flow = BeanFactory.getFlowBean();
    	ProcessHeader header = new ProcessHeader(flowid, pid, subpid);
    	Block block = flow.getBlockById(userInfo, header, blockId);
    	if (block instanceof MessageBlock) {
	      if (((MessageBlock)block).hasMessage()) {  
    	  	ProcessData proc = null;
            if (pid == Const.nSESSION_PID || subpid == Const.nSESSION_SUBPID) {
				proc = (ProcessData)session.getAttribute(Const.SESSION_PROCESS + flowExecType);
        	}
            else {
    	  	  proc = pm.getProcessData(userInfo, header, Const.nCLOSED_PROCS_READONLY);
            }
			if (proc == null) {
			  Logger.errorJsp(userInfo.getUtilizador(), "end", 
			      "[pid: " + pid + "] null process data");
			}
			else {
			  msgBlock = ((MessageBlock) block).getMessage(userInfo, proc);
			  if (StringUtilities.isEmpty(msgBlock))
				msgBlock = null;
			}
	      }
	    }
	}
    
    if (fdFormData.getParameter("mined") != null) {
        sOutput = messages.getString("end.msg.concurrentCancel");
    } else if (fdFormData.getParameter("canceled") != null) {
      	sOutput = messages.getString("end.msg.userCancel");
    } else {
        try {
            subpid = Integer.parseInt(fdFormData
                    .getParameter("subpid"));
        } catch (Exception ee) {
        }
            
        if (pid == Const.nSESSION_PID || subpid == Const.nSESSION_SUBPID) {
            // remove session proc
            session.removeAttribute(Const.SESSION_PROCESS + flowExecType);
        }
           
        ProcessHeader ph = new ProcessHeader(flowid, pid, subpid);
        int[] subpids = pm.getProcessSubPids(userInfo, ph);
            
        if ((subpids != null && subpids.length == 0)
                || (pid == Const.nSESSION_PID || subpid == Const.nSESSION_SUBPID)) {
           // no more sub processes
                
            sOutput = messages.getString("end.msg.finish");
        } else {
            sOutput = messages.getString("end.msg.finishBranch",
                    String.valueOf(subpid), String.valueOf(pid));
        }        
    }
    
    if (StringUtilities.isNotEmpty(msgBlock))
    	sOutput = msgBlock;
%>
<%@ include file = "../inc/process_top.jspf" %>
<div class="info_msg" style="font-family: Verdana,Arial,sans-serif;"><%=sOutput%></div>
<div class="button_box">
  <form action="#" onsubmit="return false;">
    <input class="regular_button_00" <%if (userInfo.isGuest()) {%> type="hidden" <%} else {%> type="button" <%}%> name="close" value="<%=messages.getString("button.close")%>" onclick="if(parent && parent.close_process) parent.close_process(3); return false;" />
  </form>
</div>
<% 
	if ("".equals(flowExecType)) {
		out.println(ProcessEndDisplay.processTasks(userInfo, response,flowid)); 
	}
%>
<%@ include file = "../inc/process_bottom.jspf" %>
