<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../../inc/defs.jsp" %>

<%@ page import = "java.sql.Timestamp" %>
<%@ page import = "pt.iflow.api.core.Activity" %>
<%@ page import = "pt.iflow.api.notification.Email" %>
<%
	String title = messages.getString("proc_undo_hist.title");  
String sPage = "Admin/proc_undo_hist";

StringBuffer sbError = new StringBuffer();

String sONGOING = "<div class=\"error_msg\">" + messages.getString("proc_undo_hist.msg.ongoing") + "</div>";

int subpid = -1;
int pid = -1;
int flowid = -1;
int undo = -1;
int flowstate = -1;
int mid = -1;
int exit_flag = -1;

try {
    subpid = Integer.parseInt(fdFormData.getParameter("subpid"));
    pid = Integer.parseInt(fdFormData.getParameter("pid"));
    flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
}
catch (Exception e) {
    Logger.errorJsp(login,sPage,"exception: " + e.getMessage());
	flowid = -1;
	pid = -1;
	subpid = -1;
    sbError.append("<br>").append(messages.getString("proc_undo_hist.error.noFlowAndProcess"));
}
try {
    undo = Integer.parseInt(fdFormData.getParameter("undo"));
    flowstate = Integer.parseInt(fdFormData.getParameter("flowstate"));
    mid = Integer.parseInt(fdFormData.getParameter("mid"));
    exit_flag = Integer.parseInt(fdFormData.getParameter("exitflag"));
}
catch (Exception e) {
    Logger.errorJsp(login, sPage,"exception: " + e.getMessage());
	undo = -1;
    flowstate = -1;
    mid = -1;
    exit_flag = -1;
}
%>
<%@ include file = "../auth.jspf" %>
<%

ProcessData procData = null;
List<FlowStateHistoryTO> alStates = null;
boolean bSuccess = false;

if (subpid != -1 && pid != -1 && flowid != -1) {
    
    Flow flow = BeanFactory.getFlowBean();
    try {
        
        if (undo == 1 && flowstate > 0 && mid > 0) {
            Email email = new Email(Const.sMAIL_SERVER);
            email.setHtml(true);
            
            String[] saParams = { String.valueOf(pid), String.valueOf(subpid), String.valueOf(flowstate)};
            if (!flow.undoProcess(userInfo, flowid, pid, subpid, flowstate, mid, exit_flag)) {
              sbError.append("<br>").append(messages.getString("proc_undo_hist.error.noRevert", saParams));
            } else {
              bSuccess = true;
              sbError.append("<br>").append(messages.getString("proc_undo_hist.msg.revertSuccess", saParams));
            }
        }
        
        try {
    	    if (pm == null) {
    	      pm = BeanFactory.getProcessManagerBean();
        	}
        	ProcessHeader procHeader = new ProcessHeader(flowid, pid, subpid);
            procData = pm.getProcessData(userInfo, procHeader, Const.nREADONLY_OFFSET);

            if (procData == null)
                throw new Exception();
        }
        catch (Exception e) {
            Logger.errorJsp(login,sPage,"exception: " + e.getMessage());
            sbError.append("<br>").append(messages.getString("proc_undo_hist.error.processData"));
        }
        
        if (procData != null) {
    	    alStates = flow.getPossibleUndoStates(userInfo, procData);
            if (alStates == null) {
              sbError.append("<br>").append(messages.getString("proc_undo_hist.error.flowStates"));
            }
        }
    }
    catch (Exception e) {
        Logger.errorJsp(login,sPage,"exception: " + e.getMessage());
        e.printStackTrace();
        sbError.append("<br>").append(messages.getString("proc_undo_hist.error.flowManager"));
    }
}
%>

<form name="prochist" method="POST">
  <input type="hidden" name="showflowid" value="<%=flowid %>"/>
  <input type="hidden" name="pid" value="<%=pid %>"/>

  <h1 id="title_admin"><%=title%></h1>
      
<% if (sbError.length() > 0) { %>
  <div class="<%=bSuccess?"info_msg":"error_msg"%>">
    <%=sbError.toString()%>
  </div>
<% } %>

<% if (alStates.isEmpty()) { %>
    <div class="info_msg">
      <%=messages.getString("proc_undo_hist.msg.noHist")%>
    </div>
<% } %>

  
<% if (alStates != null && !alStates.isEmpty()) { %>

  <div class="table_inc">  
    <div class="tab_title">
      <%=messages.getString("proc_undo_hist.header.hist")%>
    </div>
  
    <table class="item_list">
      <tr class="tab_header">
          <!--td><%=messages.getString("proc_undo_hist.field.state")%></td>
          <td><%=messages.getString("proc_undo_hist.field.start")%></td>
          <td><%=messages.getString("proc_undo_hist.field.description")%></td>
          <td><%=messages.getString("proc_undo_hist.field.end")%></td>
          <td><%=messages.getString("proc_undo_hist.field.result")%></td>
		  <td><%=messages.getString("proc_undo_hist.field.user")%></td>
          <td /-->
          <td style="border-bottom:1px solid #FFF;"><%=messages.getString("proc_undo_hist.field.state")%></td>
          <td style="border-bottom:1px solid #FFF;"><%=messages.getString("proc_undo_hist.field.inOut")%></td>
          <td style="border-bottom:1px solid #FFF;"><%=messages.getString("proc_undo_hist.field.data")%></td>
          <td style="border-bottom:1px solid #FFF;"><%=messages.getString("proc_undo_hist.field.description")%></td>
		  <td style="border-bottom:1px solid #FFF;"><%=messages.getString("proc_undo_hist.field.user")%></td>
          <td />
        </tr>
<%
		boolean toggle = false;
		for(int index = 0, lim = alStates.size(); index < lim; index++) {
		  FlowStateHistoryTO item = alStates.get(index);
          String sEndDate = null;
          String sResultado = null;
          String sLink= null; 
          String users = "";
          int qMid = item.getMid();
          if (item.getExitFlag() == 0) { 
              sResultado = messages.getString("proc_undo_hist.msg.in");
              Activity actv = new Activity(item.getModificationUser(), flowid, pid, subpid, 0, 0, "", "");
              users = actv.getUserid();
			  // FIXME isto esta mal. deveria ir buscar os activity owners associados ao state history 
			  // que estamos a processar. O pm.getActivityOwners vai buscar os users ACTUAIS.
			  // ou seja, para estados anteriores, mostra sempre os users actuais!
			  // para ja mudou-se para mostrar o user que fez a modificacao no respectivo estado.
              //String[] owners = pm.getActivityOwners(userInfo, actv);
              /*for (int i=0; owners!=null && i<owners.length; i++) {
                  if (!users.equals("")) users += ", ";
					users += owners[i];
              }*/
              if (index == lim - 1) {
                // current state... show the old fashion way
				users = "";
                String[] owners = pm.getActivityOwners(userInfo, actv);
                for (int i=0; owners!=null && i<owners.length; i++) {
                    if (!users.equals("")) users += ", ";
					users += owners[i];
                }
             }
          } else {
              users = item.getModificationUser();
              sResultado = messages.getString("proc_undo_hist.msg.out");
          }
          sLink = ""; 
          //XXX This won't let you revert to out port
          if (item.isUndoable() && item.getExitFlag() == 0) {
            sLink = "<a href=\"javascript:tabber_right(4, '"+response.encodeURL("Admin/ProcManagement/proc_undo_hist.jsp")
                + "','undo=1&flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid + "&flowstate=" 
            	+ item.getState() + "&mid=" + qMid + "&exitflag=" + item.getExitFlag() + "&ts=" + ts 
            	+ "');\"><img src=\"images/icon_revert.png\" border=\"0\" title=\"Revert\" alt=\"Revert\"></a>";
          }
          // FOR DEBUG, UNCOMMENT : sLink += " " + qMid;
%>
        <tr>
        <% if (index%2 == 0) {
		  toggle = !toggle; %>
          <td rowspan="2" class="<%=toggle ? "tab_row_even" : "tab_row_odd" %>" style="border-top:1px solid #FFF; border-bottom:1px solid #FFF;"><%=item.getState() %></td>
        <% } %>
          <td class="<%=index%2 == 0 ? "tab_row_even" : "tab_row_odd" %>" style="<%=(index % 2 == 0 ? "border-top:" : "border-bottom:") + "1px solid #FFF" %>"><%=sResultado %></td>
          <td class="<%=index%2 == 0 ? "tab_row_even" : "tab_row_odd" %>" style="<%=(index % 2 == 0 ? "border-top:" : "border-bottom:") + "1px solid #FFF" %>"><%=DateUtility.formatTimestamp(userInfo, (Date)item.getMDate()) %></td>
          <td class="<%=index%2 == 0 ? "tab_row_even" : "tab_row_odd" %>" style="<%=(index % 2 == 0 ? "border-top:" : "border-bottom:") + "1px solid #FFF" %>"><%=item.getResult() %></td>
          <%--td><%=sEndDate %></td--%>
          <td class="<%=index%2 == 0 ? "tab_row_even" : "tab_row_odd" %>" style="<%=(index % 2 == 0 ? "border-top:" : "border-bottom:") + "1px solid #FFF" %>"><%=users %></td>
          <td class="itemlist_icon" style="<%=(index % 2 == 0 ? "border-top:" : "border-bottom:") + "1px solid #FFF" %>"><%=sLink%></td>
        </tr>
        <% if (index == lim - 1 && index%2 == 0) { %>
        <tr>
          <td colspan="4" class="tab_row_odd" style="border-bottom:1px solid #FFF; text-align: center;"><%=sONGOING %></td>
        </tr>
        <% }
  		}
	}
%>
      </table>
  </div>

  <div class="button_box">
    <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.search")%>" 
      onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_undo_select.jsp")%>', get_params(document.prochist));"/>
  </div>
</form>

<if:generateHelpBox context="proc_undo_select"/>

