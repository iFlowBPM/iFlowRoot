<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@page import="pt.iflow.api.utils.series.SeriesProcessor"%>
<%@page import="pt.iflow.api.utils.series.SeriesManager"%>
<%@page import="java.sql.Timestamp"%>
<%@ include file = "../../inc/defs.jsp" %>
<%@page import="java.text.MessageFormat"%>
<if:checkUserAdmin type="org">
	<div class="error_msg">
		<if:message string="admin.error.unauthorizedaccess" />
	</div>
</if:checkUserAdmin>
<%
	String title = messages.getString("series.title");
String sPage = "Admin/SeriesManagement/series";

final String PARAM_ID = "id";
final String PARAM_ACTION = "action";

final String ACTION_ENABLE = "enable";
final String ACTION_DISABLE = "disable";


String action = fdFormData.getParameter(PARAM_ACTION);
if (StringUtils.isNotEmpty(action)) {
	
	String seriesId = fdFormData.getParameter(PARAM_ID);	
	if (StringUtils.isNotEmpty(seriesId)) {
		int id = Integer.parseInt(seriesId);
		SeriesProcessor sp = SeriesManager.getSeries(userInfo, id);
		if (action.equals(ACTION_ENABLE))
	sp.enable();
		else
	sp.disable();
	}
	
	
}



List<SeriesProcessor> seriesList = SeriesManager.listSeries(userInfo);
Timestamp tsNow = new Timestamp((new java.util.Date()).getTime());
String valueDate = DateUtility.formatTimestamp(userInfo, tsNow);
%>

<form method="post" name="series">
	<h1 id="title_admin"><%=title%></h1>

	<div class="table_inc">
    	<table class="item_list">
      		<tr class="tab_header">
          		<td></td>
          		<td><if:message string="series.name" /></td>
          		<td><if:message string="series.type" /></td>
          		<td><if:message string="series.state" /></td>
          		<td><if:message string="series.pattern" /></td>
          		<td><if:message string="series.value" /> <%=valueDate%></td>
          		<td><if:message string="series.enabled" /></td>
      		</tr>
<% 
	for (int i=0; i < seriesList.size(); i++) { 
		SeriesProcessor sp = seriesList.get(i);
		String params = PARAM_ID + "=" + sp.getId() + "&ts=" + ts;
		String descriptionKey = MessageFormat.format(SeriesProcessor.DESCRIPTION_KEY_FORMAT, sp.getProcessor());
		String description = userInfo.getMessages().getString(descriptionKey);
		String stateKey = MessageFormat.format(SeriesProcessor.STATE_DESCRIPTION_KEY_FORMAT, String.valueOf(sp.getState()));
		String state = userInfo.getMessages().getString(stateKey);
%>
    		<tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>" style="<%=(sp.isEnabled())?"":"color:red;"%>">
    			<td class="itemlist_icon">
    				<%if (sp.isEnabled()) { %>
						<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/SeriesManagement/series_flows.jsp")%>','id=<%=sp.getId()%>&ts=<%=ts%>');">
					<%} %>
					<img class="toolTipImg" <%=(sp.isEnabled()) ? "" : "style=\"filter:alpha(opacity=40);-moz-opacity:.40;opacity:.40;\"" %> src="images/icon_flow_series.png" border="0" title="<%=messages.getString("series.tooltip.series_flows")%>">
					<%if (sp.isEnabled()) { %>
						</a>
					<%} %>
				</td>
    			<td><%=sp.getName() %></td>
    			<td><%=description %></td>
    			<td><%=state %></td>
    			<td><%=sp.getPattern() %></td>
    			<td><%= (sp.isEnabled() && sp.getState() != SeriesProcessor.STATE_NEW) ? sp.getCurrentValue() : "-" %></td>
        		<td class="itemlist_icon">
	    			<% if (sp.isEnabled()) { %>
	     				<span class="online"><a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/SeriesManagement/series.jsp")%>', '<%=PARAM_ACTION %>=<%=ACTION_DISABLE %>&<%=params%>');"><img class="toolTipImg" src="images/icon_online.png" border="0" title="<%=messages.getString("series.tooltip.enabled")%>"></a></span>      
	    			<% } else { %>
	      				<span class="offline"><a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/SeriesManagement/series.jsp")%>', '<%=PARAM_ACTION %>=<%=ACTION_ENABLE %>&<%=params%>');"><img class="toolTipImg" src="images/icon_offline.png" border="0" title="<%=messages.getString("series.tooltip.disabled")%>"></a></span>      
	      			<% } %>				
          		</td>
          	</tr>
<% 
	} 
%>
		</table>
		<fieldset class="submit">
			<input class="regular_button_01" type="button" name="add_series" value="<if:message string="button.add"/>" onClick="tabber_right(4, '<%=response.encodeURL("Admin/SeriesManagement/series_add.jsp")%>','ts=<%=ts%>');"/>
		</fieldset>
	</div>
</form>
<if:generateHelpBox context="series"/>
