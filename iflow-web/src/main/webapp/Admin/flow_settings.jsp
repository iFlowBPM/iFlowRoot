<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>

<%
	String title = messages.getString("flow_settings.title"); 
String sPage = "Admin/flow_settings";

StringBuffer sbError = new StringBuffer();
int flowid = -1;
%>
<%@ include file = "auth.jspf" %>
<%

Flow flow = BeanFactory.getFlowBean();

IFlowData[] fda = null;

fda = BeanFactory.getFlowHolderBean().listFlows(userInfo);

String deployResult = (String)request.getAttribute("deployResult");

%>


	<h1 id="title_admin"><%=title%></h1>

<% if (sbError.length() > 0) { %>
	<div class="error_msg">
		<%=sbError.toString()%>
	</div>
<% } %>
<% if (StringUtils.isNotEmpty(deployResult)) { %>
	<div class="error_msg">
		<%=deployResult%>
	</div>
<% } %>
	<div class="table_inc">  

<% if (fda != null && fda.length > 0) { %>
		<table class="item_list">
			<tr class="tab_header">
				<td/>
				<td/>
				<td/>
				<td/>
				<td/>
                <td/>
				<td>
					<%=messages.getString("flow_settings.header.flow")%>
				</td>
				<td>
					<%=messages.getString("flow_settings.header.file")%>
				</td>
				<td/>
				<td>
					<%=messages.getString("flow_settings.header.status")%>
				</td>
				<td/>
			</tr>
			<%  for (int i=0; i < fda.length; i++) {
				  IFlowData fData = fda[i];
				  String params = DataSetVariables.FLOWID + "=" + fda[i].getId() + "&flowname="  + fda[i].getName() + "&ts=" + ts;
				  // do not encode this url. it should be a "public" url
			      String sUrl = Const.APP_PROTOCOL + "://" + Const.APP_HOST + ":" + Const.APP_PORT + Const.APP_URL_PREFIX + 
		                        "GoTo?goto=inicio_flow.jsp&"+DataSetVariables.FLOWID+"=" + fData.getId();
				  FlowType type = fData.getFlowType();

                  String showInMenuIcon = "images/flow_show_menu_requirement_true.png";
                  String showInMenuTooltip = "flow_settings.tooltip.menu.show_requirement.show";
                  if (!fData.isVisibleInMenu()){
                    showInMenuIcon = "images/flow_show_menu_requirement_false.png";
                    showInMenuTooltip = "flow_settings.tooltip.menu.show_requirement.dont_show";
                  }

                  String hasScheduleIcon="images/flowScheduling_new.png";
                  if (fData.hasSchedules()){
                    hasScheduleIcon="images/flowScheduling_has.png";
                  }
			%>

				<tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>">
					<td class="itemlist_icon">
						<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_settings_edit.jsp") %>', '<%=params%>');"><img class="toolTipImg" src="images/icon_modify.png" border="0" title="<%=messages.getString("flow_settings.tooltip.edit") %>"></a>
					</td>
					<td class="itemlist_icon">
						<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/profiles_edit.jsp") %>', '<%=params%>');"><img class="toolTipImg" src="images/icon_profile.png" border="0" title="<%=messages.getString("flow_settings.tooltip.profiles") %>"></a>
					</td>
					<td class="itemlist_icon">
						<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_type_edit.jsp") %>', '<%=params%>');"><img class="toolTipImg" src="images/flow_type_<%=type.getCode()%>.png" border="0" title="<%=messages.getString("flow_settings.tooltip.flow_type."+type.getCode()) %>"></a>
					</td>
                    <td class="itemlist_icon">
                        <a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_show_in_menu_change.jsp") %>', '<%=params%>');"><img class="toolTipImg" src="<%=showInMenuIcon %>" border="0" title="<%=messages.getString(showInMenuTooltip) %>"></a>
                    </td>
                    <td class="itemlist_icon">
                        <!-- <a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_schedule_list") %>', '<%=params%>');"><img class="toolTipImg" src="<c:out value='<%=hasScheduleIcon %>'/>" border="0" title="<if:message string="flow_settings.tooltip.menu.scheduling" />"></a> -->
                        <img class="toolTipImg" src="<%=hasScheduleIcon %>" border="0" title="<if:message string="flow_settings.tooltip.menu.scheduling" />">
                    </td>
					<td class="itemlist_icon">
	    				<% if (fData.isOnline() || FlowType.SUPPORT.equals(type) || FlowType.SEARCH.equals(type)) { %>
	      					<img class="toolTipImg" src="images/icon_resync_disabled.png" border="0" title="<%=messages.getString("flow_settings.tooltip.resync") %>">     
	    				<% } else { %>
							<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_resync_edit.jsp") %>', '<%=params%>');"><img class="toolTipImg" src="images/icon_resync.png" border="0" title="<%=messages.getString("flow_settings.tooltip.resync") %>"></a>
	      				<% } %>
					</td>
					<td>
						<%=fData.getName()%>
					</td>
					<td>
						<%=fData.getFileName()%>
					</td>
					<td class="itemlist_icon">
						<a href="<%=sUrl%>" target="_blank" onclick="javascript:copy_clip(this.href);return false;"><img class="toolTipImg" src="images/icon_copy.png" border="0" title="<%=messages.getString("flow_settings.copy_link.tooltip")%>" ></a>
					</td>
					<td class="itemlist_icon">
	    				<% if (fData.isOnline()) { %>
	      					<span class="online"><a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_deployer.jsp") %>', 'action=undeploy&<%=params%>');"><img class="toolTipImg" src="images/icon_online.png" border="0" title="<%=messages.getString("flow_settings.tooltip.undeploy")%>"></a></span>      
	    				<% } else { %>
	     					<span class="offline"><a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_deployer.jsp") %>', 'action=deploy&<%=params%>');"><img class="toolTipImg" src="images/icon_offline.png" border="0" title="<%=messages.getString("flow_settings.tooltip.deploy")%>"></a></span>     
	      				<% } %>
					</td>
					<td class="itemlist_icon">
						<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_delete.jsp") %>', '<%=params%>');"><img class="toolTipImg" src="images/icon_delete.png" border="0" title="<%=messages.getString("flow_settings.tooltip.delete") %>"></a>
					</td>
				</tr>
			<% } %>
		</table>
	</div>
<% } else { %>
	<div class="info_msg">
		<%=messages.getString("flow_settings.error.noflowsdef")%>
	</div>
<% } %>

<div class="button_box">
   	<input class="regular_button_02" type="button" name="add" value="<if:message string="button.import"></if:message>" onClick="javascript:tabber_right(4, '<c:url value="Admin/uploadflow.jsp"></c:url>','ts=<%=ts%>');"></input>
</div>

<if:generateHelpBox context="flow_settings"/>
