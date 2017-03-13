<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%@page import="java.util.Date" %>
<%@page import="pt.iflow.api.filters.FlowFilter"%>
<%
  final String ALL_PROCS = "__ALL_PROCS__";
  final String MY_PROCS = "__MY_PROCS__";
  final String INT_PROCS = "__INT_PROCS__";

  String scroll = (String)session.getAttribute("filtro_scroll");
  if(scroll == null) scroll = "0";
  String spid = (String) session.getAttribute("filtro_pid");
  String ssubpid = (String) session.getAttribute("filtro_subpid");
  if (spid == null)
	spid = "";
  if (ssubpid == null)
	ssubpid = "";

  String sPROTECTED = messages.getString("user_procs.const.protected");

  int nPOPUP_MIN_LENGTH = 3;

  int ITEMS_PAGE = 20;
  int nShowFlowId = -1;
  int nItems = ITEMS_PAGE;
  int nStartIndex = 0;
  int nMode = 0;
  String sShowFlowId = fdFormData.getParameter("showflowid");
  String pnumber = fdFormData.getParameter("pnumber");

  String tmp = fdFormData.getParameter("showUserProcs");
  boolean showUserProcs = false;
  if (StringUtils.isNotBlank(tmp) && StringUtils.equalsIgnoreCase(tmp, "true")) {
    showUserProcs = true;
  }

  Map<String,Object> hmConfig = null;
  String sClear = fdFormData.getParameter("clearsearch");
  if (sClear==null || !sClear.equals("true")) {
    hmConfig = (Map<String,Object>)session.getAttribute(Const.SESSION_USER_PROCS);
    showUserProcs = (hmConfig!=null);
  }

  if (userflowid > 0) {
    sShowFlowId = String.valueOf(userflowid);
  }
  if (StringUtils.isEmpty(sShowFlowId))
    sShowFlowId = "-1";

  try {
    nShowFlowId = Integer.parseInt(sShowFlowId);
  } catch (Exception e) {
  }

  try {
    nItems = Integer.parseInt(fdFormData.getParameter("numitemspage"));
  } catch (Exception e) {
  }
  try {
    nMode = Integer.parseInt(fdFormData.getParameter("mode"));
  } catch (Exception e) {
    nMode = 0;
  }
  try {
    nStartIndex = Integer.parseInt(fdFormData.getParameter("startindex"));
  } catch (Exception e) {
  }

  if (nMode == 0) {
    nStartIndex = 0;
  } else if (nMode > 0) {
    nStartIndex = nStartIndex + nItems;
  } else if (nMode < 0) {
    nStartIndex = nStartIndex - nItems;
  }

  String sDtBefore = fdFormData.getParameter("dtbefore");
  String sDtAfter = fdFormData.getParameter("dtafter");

  Date dtBefore = null;
  Date dtAfter = null;

  try {
    dtBefore = DateUtility.parseFormDate(userInfo, sDtBefore);
  } catch (Exception e) {
  }

  try {
    dtAfter = DateUtility.parseFormDate(userInfo, sDtAfter);
  } catch (Exception e) {
  }

  String sTargetUser = fdFormData.getParameter("targetUser");
  String targetUser = null;
  boolean isIntervenient = false;
  if (StringUtils.isEmpty(sTargetUser) || MY_PROCS.equals(sTargetUser)) {
    targetUser = userInfo.getUtilizador();
  } else if (INT_PROCS.equals(sTargetUser)) {
    targetUser = userInfo.getUtilizador();
    isIntervenient = true;
  } else if (ALL_PROCS.equals(sTargetUser)) {
    targetUser = null;
  }

  String orderBy = fdFormData.getParameter("orderby");
  String orderType = fdFormData.getParameter("ordertype");

  boolean closedProcesses = false;
  String processStatus = fdFormData.getParameter("processStatus");
  if (StringUtils.isEmpty(processStatus) || "__OPEN__".equals(processStatus)) {
    closedProcesses = false;
  } else if ("__CLOSED__".equals(processStatus)) {
    closedProcesses = true;
  }

  if (session.getAttribute(login + "_userlist") != null) {
    session.removeAttribute(login + "_userlist");
  }
%>

<div style="vertical-align: middle;">
  <img src="images/icon_tab_tarefas.png" class="icon_item"/>
  <h1><if:message string="user_procs.title" /></h1>
</div>
<%
  if (!showUserProcs) {
%>
	<div class="info_msg">
		<%=messages.getString("user_procs.msg.select")%>
	</div>
<%
  } else {
    String[] idx = new String[Const.INDEX_COLUMN_COUNT];
    String proc_search = fdFormData.getParameter("proc_search");
    if (StringUtils.equals("true", proc_search)) {
      for (int i = 0; i < Const.INDEX_COLUMN_COUNT; i++){
        idx[i] = fdFormData.getParameter("idx" + i);
        if("null".equals(idx[i]))
        	idx[i]="";
      }
    } else {
      for (int i = 0; i < Const.INDEX_COLUMN_COUNT; i++)
        idx[i] = "";
    }

    if (hmConfig != null) {
      nShowFlowId = (Integer)hmConfig.get(Const.SESSION_USER_PROCS_FLOWID);
      
      sTargetUser = (String)hmConfig.get(Const.SESSION_USER_PROCS_TARGETUSER);
      targetUser = null;
      isIntervenient = false;
      if (StringUtils.isEmpty(sTargetUser) || MY_PROCS.equals(sTargetUser)) {
        targetUser = userInfo.getUtilizador();
      } else if (INT_PROCS.equals(sTargetUser)) {
        targetUser = userInfo.getUtilizador();
        isIntervenient = true;
      } else if (ALL_PROCS.equals(sTargetUser)) {
        targetUser = null;
      }
      
      processStatus = fdFormData.getParameter("processStatus");
      if (StringUtils.isEmpty(processStatus) || "__OPEN__".equals(processStatus)) {
        closedProcesses = false;
      } else if ("__CLOSED__".equals(processStatus)) {
        closedProcesses = true;
      }

      pnumber = (String)hmConfig.get(Const.SESSION_USER_PROCS_PNUMBER); 
      dtBefore = (Date)hmConfig.get(Const.SESSION_USER_PROCS_DTAFTER);
      dtAfter = (Date)hmConfig.get(Const.SESSION_USER_PROCS_DTBEFORE);
      nItems = (Integer)hmConfig.get(Const.SESSION_USER_PROCS_NITEMS);  
      orderBy = (String)hmConfig.get(Const.SESSION_USER_PROCS_ORDERBY);
      orderType = (String)hmConfig.get(Const.SESSION_USER_PROCS_ORDERTYPE);
      idx = (String[])hmConfig.get(Const.SESSION_USER_PROCS_IDX);
      if (nMode == 0){
        try {
          nStartIndex = (Integer)hmConfig.get(Const.SESSION_USER_PROCS_STARTINDEX);
        } catch (Exception e) {}
      } else {
        hmConfig.put(Const.SESSION_USER_PROCS_STARTINDEX, nStartIndex);
        session.setAttribute(Const.SESSION_USER_PROCS, hmConfig);
      }
    } else {
      hmConfig = new HashMap<String,Object>();
      hmConfig.put(Const.SESSION_USER_PROCS_FLOWID, nShowFlowId);
      hmConfig.put(Const.SESSION_USER_PROCS_TARGETUSER, sTargetUser);
      hmConfig.put(Const.SESSION_USER_PROCS_PNUMBER, pnumber); 
      hmConfig.put(Const.SESSION_USER_PROCS_DTAFTER, dtBefore);
      hmConfig.put(Const.SESSION_USER_PROCS_DTBEFORE, dtAfter);
      hmConfig.put(Const.SESSION_USER_PROCS_STARTINDEX, nStartIndex);
      hmConfig.put(Const.SESSION_USER_PROCS_NITEMS, nItems);  
      hmConfig.put(Const.SESSION_USER_PROCS_ORDERBY, orderBy);
      hmConfig.put(Const.SESSION_USER_PROCS_ORDERTYPE, orderType);
      hmConfig.put(Const.SESSION_USER_PROCS_IDX, idx);
      session.setAttribute(Const.SESSION_USER_PROCS, hmConfig);
    }

    UserProcesses getUserProcessesResult = BeanFactory.getProcessManagerBean().getUserProcesses(userInfo, nShowFlowId,
        targetUser, idx, closedProcesses, new FlowFilter(pnumber, dtAfter, dtBefore, nStartIndex, nItems, 
            isIntervenient, orderBy, orderType));

    List<List<String>> alData = getUserProcessesResult.getAlData();
    Map<String, Map<String, List<String>>> hmFlowUsers = getUserProcessesResult.getHmFlowUsers();

    if (alData != null && alData.size() > 0) {
%>

	<form name="userprocsForm" method="post">

	<div class="table_inc">
	  <p>
	  <%
	    if (StringUtils.isNotBlank(pnumber)) {
	  %>
        <%=messages.getString("user_procs.introMsg.selectedProcess", "<b>" + pnumber + "</b>")%>
	  <%
	    } else {
	  %>
        <%=messages.getString("user_procs.introMsg", "<b>"
                    + (closedProcesses ? messages.getString("user_procs.introMsgClosed") : messages
                        .getString("user_procs.introMsgOpened")) + "</b>")%>
      	<%=(nShowFlowId > 0) ? messages.getString("actividades.introMsg.flowId", "<b>"
                + BeanFactory.getFlowHolderBean().getFlow(userInfo, nShowFlowId).getName() + "</b>") : ""%>
      	<%=(dtAfter != null) ? messages.getString("actividades.introMsg.dateAfter", "<b>"
                + DateUtility.formatFormDate(userInfo, dtAfter) + "</b>") : ""%>
      	<%=(dtBefore != null) ? messages.getString("actividades.introMsg.dateBefore", "<b>"
                + DateUtility.formatFormDate(userInfo, dtBefore) + "</b>") : ""%>
	  	<%
	  	  }
	  	%>
      </p>
	  <table width="100%" cellpadding="2">
	  	<tr class="tab_header">
	  		<td />
		  	<td><if:message string="user_procs.header.flow" /></td>
		  	<td><if:message string="user_procs.header.process" /></td>
		  	<td><if:message string="user_procs.header.status" /></td>
		  	<td><if:message string="user_procs.header.statusDate" /></td>
		  	<td style="min-width: 100px;"><if:message string="user_procs.header.assignedTo" /></td>	  	
			<%=(StringUtils.isEmpty(targetUser) || isIntervenient) ? "<td>" + messages.getString("user_procs.header.owner") + "</td>" : ""%>
		</tr>
	   <% boolean bFirstPage = true;
	      boolean bHasMoreItems = false;
	      if (nStartIndex < 0) {
	        nStartIndex = 0;
	      } else if (nStartIndex > 0) {
	        bFirstPage = false;
	      }
	      if (nMode < 0) {
	        bHasMoreItems = true;
	      }
	      for (int row = 0; row < alData.size(); row++) {
	        if (row >= nItems) {
	          bHasMoreItems = true;
	          break;
	        } %> 
		<tr class="<%=((row % 2 == 0) ? "tab_row_even" : "tab_row_odd")%>">
		<%
	         List<String> alMixedData = alData.get(row);
	         String flowid = alMixedData.get(UserProcsConst.FLOW_ID);
	         String flowName = alMixedData.get(UserProcsConst.FLOW_NAME);
	         String pid = alMixedData.get(UserProcsConst.PID);
	         String subpid = alMixedData.get(UserProcsConst.SUBPID);
	         String result = alMixedData.get(UserProcsConst.RESULT);
	         String tstamp = alMixedData.get(UserProcsConst.MDATE);
	         String procCreator = alMixedData.get(UserProcsConst.CREATOR);
	         // process number
	         String pn = alMixedData.get(UserProcsConst.PNUMBER);
	         // reprocess timestamp
	         String sDate = "";
	         if(StringUtils.isNotBlank(tstamp)) {
		       	java.sql.Timestamp tsDate = java.sql.Timestamp.valueOf(tstamp);
		       	sDate = DateUtility.formatTimestamp(userInfo, tsDate);
	         }
	         String pidShow = pn;
	         if (!Const.DEFAULT_SUBPID.equals(subpid)) {
	           pidShow = pn + "/" + subpid;
	         }
	         String params = flowid + "," + pid + "," + subpid + "," + (closedProcesses ? "1" : "0");
	         String href = "javascript:process_detail(8, '" + response.encodeURL("user_proc_detail.jsp") + "', " + params + ")";
	         boolean canView = new Boolean(alMixedData.get(UserProcsConst.SHOW_DETAIL));
	         boolean showAssigned = new Boolean(alMixedData.get(UserProcsConst.SHOW_ASSIGNED));
	         if (showAssigned && !canView) {
	           href = "javascript:process_detail(8, '" + response.encodeURL("user_proc_tasks.jsp") + "', " + params + ")";
	         }
	
	         if (!showAssigned && !canView) {
	           href = null;
	         }
	    %>
         <% if(spid.equals(pid) && ssubpid.equals(subpid)){
         	out.println("<td class=\"itemlist_icon\"><a><img class=\"toolTipImg\" src=\"images/icon_task_enable.png\" border=\"0\"></a> </td>");
         }else
         	out.println("<td> </td>");
         %>
		<td>
	  	  <% if (href != null) { %>
	  	  <a href="<%=href%>" class="toolTipImg" title="<%=messages.getString("user_procs.tooltip.view_history")%>">
	  	  <% } %>
  	  	  <% if(BeanFactory.getFlowHolderBean().getFlow(userInfo, Integer.parseInt(flowid)).getFlowType().compareTo(FlowType.DOCUMENT)==0) {%>
	    		<img class="toolTipImg" src="images/flow_type_D.png" border="0"/>
	      <% } %>
	  	    <%=flowName%>
	  	  <% if (href != null) { %>
	  	  </a>
	  	  <% } %>
	  	</td>
	  	<td>
	  	  <% if (href != null) { %>
	  	  <a href="<%=href%>" class="toolTipImg" title="<%=messages.getString("user_procs.tooltip.view_history")%>">
	  	  <% } %>
	  	    <%=pidShow%>
	  	  <% if (href != null) { %>
	  	  </a>
	  	  <% } %>
	  	</td>
	  	<td>
	  	  <% if (href != null) { %>
	  	    <a href="<%=href%>" class="toolTipImg" title="<%=messages.getString("user_procs.tooltip.view_history")%>">
	  	  <% } %>
	  	    <%=result%>
	  	  <% if (href != null) { %>
	  	  </a>
	  	  <% } %>
	  	</td>
	  	<td>
	  	  <% if (href != null) { %>
	  	  <a href="<%=href%>" class="toolTipImg" title="<%=messages.getString("user_procs.tooltip.view_history")%>">
	  	  <% } %>
	  	    <%=sDate%>
	  	  <% if (href != null) { %>
	  	  </a>
	  	  <% } %>
	  	</td>
	    <% if (showAssigned) {
	          // assigned
	          Map<String, List<String>> hmPidUsers = new HashMap<String, List<String>>();

	          StringBuffer sbHtml = new StringBuffer();
	          if (hmFlowUsers.containsKey(flowid)) {
	            hmPidUsers = hmFlowUsers.get(flowid);
	          }

	          List<String> alUsers = null;
	          if (hmPidUsers != null && hmPidUsers.containsKey(pid)) {
	            alUsers = hmPidUsers.get(pid);

	            if (alUsers != null && alUsers.size() > 0) {

	              if (alUsers.size() > 0 /*nPOPUP_MIN_LENGTH*/) {
	                sbHtml = new StringBuffer("<a href=\"javascript:W=window.open('");
	                sbHtml.append(response.encodeURL("show_users.jsp"));
	                sbHtml.append("?ts=").append(ts);
	                sbHtml.append("','_show_users','menubar=no,status=no,scrollbars=yes,resizable=yes,");
	                sbHtml.append("width=300,height=200');W.focus()\" class=\"v10AZU\">");

	                session.setAttribute(login + "_userlist", alUsers);
	              }

	              for (int i = 0; i < alUsers.size(); i++) {
	                if (i >= nPOPUP_MIN_LENGTH) {
	                  sbHtml.append(", ...");
	                  break;
	                }

	                if (i > 0) {
	                  sbHtml.append(", ");
	                }
	                String sUserName = (String) alUsers.get(i);
	                sbHtml.append("<a href=\"javascript:showUserDialog('").append(sUserName).append("');\">").append(sUserName)
	                    .append("</a>");
	              }

	              if (alUsers.size() > nPOPUP_MIN_LENGTH) {
	                sbHtml.append("</a>");
	              }

	            }
	          }
	          out.print("          <td>");
	          if (sbHtml.length() == 0)
	            out.print(sPROTECTED);
	          else
	            out.print(sbHtml.toString());
	          out.println("</td>");
	        } else {
	          out.print("          <td>");
	          out.print(sPROTECTED);
	          out.println("</td>");
	        }

        // assigned
	        if (StringUtils.isEmpty(targetUser) || isIntervenient) {
	          out.println("          <td>" + procCreator + "</td>");
	        }
	%>
	</tr>
    <% } %>			
  </table>
</div>
<%
   boolean bDisableNavigationPrev = bFirstPage;
      boolean bDisableNavigationNext = !bHasMoreItems;
%>
   <div class="button_box">
   <% if (!bDisableNavigationPrev) { %>
   	<input class="regular_button_01" type="button" name="previous" value="<%=messages.getString("button.previous")%>" 
   		onClick="javascript:document.userprocsForm.mode.value='-1';tabber_right(8, '<%=response.encodeURL("user_procs.jsp")%>', get_params(document.userprocsForm));"/>
   <% } %>
   <% if (!bDisableNavigationNext) { %>
   	<input class="regular_button_01" type="button" name="next" value="<%=messages.getString("button.next")%>" 
   		onClick="javascript:document.userprocsForm.mode.value='1';tabber_right(8, '<%=response.encodeURL("user_procs.jsp")%>', get_params(document.userprocsForm));"/>    
   <% } %>
   </div>
	<input type="hidden" name="mode" value="0">
	<input type="hidden" name="startindex" value="<%=nStartIndex%>">
	<input type="hidden" name="numitemspage" value="<%=nItems%>">
	<input type="hidden" name="showUserProcs" value="<%=showUserProcs%>" >
	<input type="hidden" name="dtafter" value="<%=sDtAfter%>" >
	<input type="hidden" name="dtbefore" value="<%=sDtBefore%>" >
	<input type="hidden" name="processStatus" value="<%=processStatus%>" >
	<input type="hidden" name="showflowid" value="<%=nShowFlowId%>" >
	<input type="hidden" name="targetUser" value="<%=sTargetUser%>" >
	<input type="hidden" name="orderby" value="<%=orderBy%>" >
	<input type="hidden" name="ordertype" value="<%=orderType%>" >
	<input type="hidden" name="pnumber" value="<%=pnumber%>" >
	<input type="hidden" name="proc_search" value="<%=proc_search%>" >
    <%if (StringUtils.equals("true", proc_search)) {
      for (int i = 0; i < Const.INDEX_COLUMN_COUNT; i++) { 
      	String idxVal = (idx[i] == null ? "" : String.valueOf(idx[i]));%>
	<input type="hidden" name="idx<%=i%>" value="<%=idxVal%>" >
		<%}
     }%>
</form>		
<script type="text/javascript">
	function setScroll(pos) {	
		setScrollPosition(pos);
	}
	setScroll(<%=scroll%>);
</script>
 <% } else { %>
<div class="info_msg">
	<%=messages.getString("user_procs.msg.noProcs")%>
</div>
 <%
   }
 }
%>