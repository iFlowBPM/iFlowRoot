<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
%><%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"
%><%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"
%><%@ page import="pt.iflow.api.blocks.Block"
%><%@ page import="pt.iflow.api.blocks.MessageBlock"
%><%@ include file="inc/defs.jsp"
%><%@ include file="inc/initProcInfo.jspf"
%><%@ include file="inc/batchProcessing.jspf"
%>
<link rel="stylesheet" type="text/css" media="all" href="javascript/calendar/calendar-iflow.css" title="cal-iflow" />
<script type="text/javascript" src="javascript/processAnnotations.js"></script>
<script type="text/javascript" src="javascript/calendar/calendar.js"></script>
<script type="text/javascript" src="javascript/calendar/lang/calendar-pt_PT.js"></script>
<script type="text/javascript" src="javascript/calendar/calendar-setup.js"></script>
<script type="text/javascript" src="javascript/iflow_main.js"></script>
<%
    String title = "Info";
    String sPage = "proc_info";
    StringBuffer sbHtml = new StringBuffer(messages.getString("proc_info.msg.fwhiddenuser"));
    
    Flow flow = BeanFactory.getFlowBean();
    Block block = flow.getBlock(userInfo, procData);
    String message = "";
    if (block instanceof MessageBlock) {
      	message = ((MessageBlock) block).getMessage(userInfo, procData);
        if (StringUtils.isNotBlank(message)) {
            sbHtml = new StringBuffer("<p>");
            sbHtml.append(message).append("</p>");
        }
    }
    
    boolean paramsOk = false;
    try {
     // use of fdFormData defined in /inc/defs.jsp
        flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
        pid = Integer.parseInt(fdFormData.getParameter("pid"));
        String sSubPid = fdFormData.getParameter("subpid");
        String inDetail = fdFormData.getParameter("inDetail");

        // process not yet "migrated".. assume default subpid
        if (StringUtilities.isEmpty(sSubPid)) {
            subpid = 1;
        } else {
            subpid = Integer.parseInt(sSubPid);
        }
        
        if (pid == Const.nSESSION_PID) {
            // reset subpid to session subpid
            subpid = Const.nSESSION_SUBPID;
        }
        paramsOk = true;
    } catch (NumberFormatException e) {
      Logger.warningJsp(login,request.getPathInfo(), "Error parsing process parameters: "+e.getMessage());
    }
    
    if (paramsOk) {
      	FlowSettings settings = BeanFactory.getFlowSettingsBean();
        FlowSetting setting = settings.getFlowSetting(flowid, Const.sSHOW_SCHED_USERS);
        String sShowUsers = (setting == null ? null : setting.getValue());
        
        if (StringUtils.isNotBlank(sShowUsers) && StringUtilities.isAnyOfIgnoreCase(sShowUsers, 
          		new String[] { Const.sSHOW_YES, "sim", "yes", "true", "1" })) {
            Iterator<Activity> it = pm.getProcessActivities(userInfo, flowid, pid, subpid);
            if (it != null) {
                if(StringUtils.isBlank(message)) {
                  sbHtml = new StringBuffer();                
                }
                else {
                  sbHtml.append("<br/>");
                }
                sbHtml.append(messages.getString("proc_info.msg.fwshownuser"));                
                sbHtml.append("</div><div class=\"table_inc\">");
                sbHtml.append("<table class=\"item_list\">");
                
                AuthProfile aptmp = BeanFactory.getAuthProfileBean();
                
                Activity a = null;
                boolean odd = false;
                while (it.hasNext()) {
                    odd = !odd;
                    a = it.next();
                    sbHtml.append("<tr><td class=\"").append(odd ? "tab_row_odd" : "tab_row_even").append("\">");
                    sbHtml.append(a.userid);
                    UserData udUserInfo = aptmp.getUserInfo(a.userid);
                    if (udUserInfo != null) {
                        sbHtml.append("&nbsp;-&nbsp;").append(udUserInfo.getName());
                    }
                    sbHtml.append("</td></tr>");
                }
                
                sbHtml.append("</table>");
            }
        }
    }
    
    String sBatchLink = "";
    String sColspan = "";
    String sTDBefore = "<td class=\"v10bAZUdec\" align=\"center\" valign=\"middle\">";
    
    if (hmSessionBatch != null && alSessionBatchPids != null && hmSessionBatchLinks != null && alSessionBatchPids.size() > 0 && hmSessionBatchLinks.size() > 0) {
        
        sColspan = "colspan=\"5\"";
        
        sTDBefore = "<td width=\"20%\">&nbsp;</td>";
        sTDBefore += "<td width=\"20%\" class=\"v10bAZUdec\" align=\"center\" valign=\"middle\">";
        
        sBatchLink = "<td width=\"20%\">&nbsp;</td>";
        sBatchLink += "<td width=\"20%\" class=\"v10bAZUdec\" align=\"center\" valign=\"middle\"><a href=\"" + response.encodeURL("processBatchProc.jsp") + "\" class=\"v10bAZUdec\"><img src=\"images/setaon.gif\" width=\"19\" height=\"19\" border=\"0\"><br>Pr&oacute;ximo Processo Lote</a></td>";
        
        sBatchLink += "<td width=\"20%\">&nbsp;</td>";
    }
%>
<%@ include file="inc/process_top.jspf"%>

<div class="info_msg" style="font-family: Verdana,Arial,sans-serif;"><%=sbHtml%></div>

<%  String sFrom = fdFormData.getParameter("from");
    if (sFrom == null){
        sFrom = "";
    }
    if (pid > 0 && "forward".equals(sFrom)){ 
      String labelId = fdFormData.getParameter("labelid");
      if (labelId == null){
        labelId = "";
      }
      String labelName = fdFormData.getParameter("labelname");
      if (labelName == null){
        labelName = "";
      }
    %>
  <span id ="end_process_process_annotations_span"></span>
  <script language="JavaScript" type="text/javascript">
	if (parent.showProcessFowardAnnotations)
	  parent.showProcessFowardAnnotations(<%=flowid%>,<%=pid%>,<%=subpid%>,'<%=sFrom%>', '<%=labelId%>', '<%=labelName%>');
  </script>
<% } %>

<div class="button_box">
  <form action="#" onsubmit="return false;">
    <input class="regular_button_01" <%if (userInfo.isGuest()) {%> type="hidden" <%} else {%> type="button" <%}%> name="close" value="<if:message string="button.close"/>" onclick="if(parent.saveForwardToProcessAnnotations)parent.saveForwardToProcessAnnotations('true');if(parent && parent.close_process) parent.close_process(3); return false;" />
    <% if (pid > 0 && "forward".equals(sFrom)){ %>
    <input id="annotationButton" class="regular_button_02" <%if (userInfo.isGuest()) {%> type="hidden" <%} else {%> type="button" <%}%> name="close" value="<if:message string="button.send.anottation"/>" onclick="parent.saveForwardToProcessAnnotations('true');if(parent && parent.close_process) parent.close_process(3); return false;" />
  <script language="JavaScript" type="text/javascript">
	if (!parent.showProcessFowardAnnotations) //not brilliant... Check if the function is the to know if annotations are to be used
      document.getElementById('annotationButton').style.display = 'none';     
  </script>

    <% } %>
  </form>
</div>
  <script language="JavaScript" type="text/javascript">
  parent.document.getElementById('section3_content_div').style.height='800px';
  </script>

<% out.println(ProcessEndDisplay.processTasks(userInfo, response)); %>

<%@ include file="inc/process_bottom.jspf"%>
