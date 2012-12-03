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
<%@page import="pt.iflow.api.utils.series.SeriesProcessor"%>
<%@page import="pt.iflow.api.utils.series.SeriesManager"%>
<%@ include file = "../../inc/defs.jsp" %>
<%@page import="pt.iflow.api.utils.series.SeriesFilter"%>
<if:checkUserAdmin type="org">
	<div class="error_msg">
		<if:message string="admin.error.unauthorizedaccess" />
	</div>
</if:checkUserAdmin>
<%
	final String PARAM_ID = "id";
	final String PARAM_AVAILABLE = "available";
	final String PARAM_ASSIGNED = "assigned";
	final String PARAM_ACTION = "action";
	
	final String ACTION_ADD = "add";
	final String ACTION_DEL = "del";
	
	String title = messages.getString("series.series_flows.title");
	String sPage = "Admin/SeriesManagement/series_flows";

	List<SeriesProcessor> seriesList = null;
	int id = -1;
	SeriesProcessor selectedSeries = null;
	HashMap<String,SeriesProcessor> seriesMap = new HashMap<String,SeriesProcessor>();
	
	try {
		seriesList = SeriesManager.listSeries(userInfo, EnumSet.of(SeriesFilter.ENABLED, SeriesFilter.NEW, SeriesFilter.USED));
		id = Integer.parseInt(fdFormData.getParameter(PARAM_ID));
		
		for (SeriesProcessor sp : seriesList) {
	seriesMap.put(String.valueOf(sp.getId()), sp);
	
	if (sp.getId() == id) {
		selectedSeries = sp;
	}
		}
		
		if (selectedSeries == null)
	throw new Exception("series with id " + id + " not found in series list");
	}
	catch (Exception e) {
		Logger.errorJsp(userInfo.getUtilizador(), sPage, "Unable to get Series: " + e.getMessage());
		ServletUtils.sendEncodeRedirect(response, "series.jsp");
		return;
	}
	
	FlowHolder fh = BeanFactory.getFlowHolderBean();
	
	// PROCESS ACTION
	String action = fdFormData.getParameter(PARAM_ACTION);
	if (ACTION_ADD.equals(action) || ACTION_DEL.equals(action)) {
		String param = PARAM_AVAILABLE;
		int seriesId = id;
		
		if (ACTION_DEL.equals(action)) {
	param = PARAM_ASSIGNED;
	seriesId = IFlowData.NO_SERIES;
		}
		
		String[] flows = fdFormData.getParameterValues(param);
		if (flows != null) {
	int[] flowids = new int[flows.length];
	for(int i=0; i < flows.length; i++) {
		flowids[i] = Integer.parseInt(flows[i]);
	}
	fh.updateFlowsSeries(userInfo, flowids, seriesId);
		}
	}
	
	
	IFlowData[] fda = fh.listFlows(userInfo);

	ArrayList<IFlowData> flowsAvailable = new ArrayList<IFlowData>();
	ArrayList<IFlowData> flowsAssigned = new ArrayList<IFlowData>();
	
	for (IFlowData fd : fda) {
		if (fd.getSeriesId() == id)
	flowsAssigned.add(fd);
		else
	flowsAvailable.add(fd);			
	}
%>

<form method="post" name="series_flows" id="series_flows">
   	<input type="hidden" name="<%=PARAM_ACTION %>" id="<%=PARAM_ACTION %>" value=""/>
	<input type="hidden" name="ts" id="ts" value="<%=ts %>"/>

  	<h1 id="title_admin"><%=title%></h1>
  	<fieldset>
  		<legend></legend>
	    <ol>
			<% String idselectaction = "tabber_right(4, '"+response.encodeURL("Admin/SeriesManagement/series_flows.jsp")+"',get_params(document.series_flows));"; %>
			<if:formSelect name="<%=PARAM_ID %>" edit="true" value='<%=String.valueOf(id) %>' labelkey="series.series_flows.input.series" onchange="<%=idselectaction%>">
			<% for (SeriesProcessor sp : seriesList) { %>
					<if:formOption value='<%=sp.getId() %>' label="<%=sp.getName() %>"/>
			<% } %>
			</if:formSelect>
		</ol>
	</fieldset>
	
  	<fieldset>
	    <ol>
	      <li>
			<label for="unit"><%=messages.getString("series.series_flows.label.flows")%></label>
			<div class="ft_main">
				<div class="ft_left">
					<div class="ft_caption">
						<%=messages.getString("series.series_flows.label.available")%>
					</div>
					<div class="ft_select">
						<if:formSelect name="<%=PARAM_AVAILABLE %>" edit="true" value="" multiple="true" size="10" noli="true">
						<% 
						   for(IFlowData fdAv : flowsAvailable) {
							   String labelSuffix = " [-]";
							   if (fdAv.getSeriesId() != IFlowData.NO_SERIES) {
									SeriesProcessor sp = seriesMap.get(String.valueOf(fdAv.getSeriesId()));
								   	if (sp != null)
								   		labelSuffix = " [" + sp.getName() + "]"; 
							   }
							   String label = fdAv.getName() + labelSuffix;
						%>
								<if:formOption value='<%=fdAv.getId() %>' label="<%=label %>"/>	
						<% } %>						
						</if:formSelect>
					</div>
				</div>
				<div class="ft_middle">
					<div class="ft_button">
				    	<input class="regular_button_000" type="button" name="add" value="=&gt;" 
				    		onClick="$('<%=PARAM_ACTION %>').value='<%=ACTION_ADD %>';tabber_right(4, '<%=response.encodeURL("Admin/SeriesManagement/series_flows.jsp")%>', get_params(document.series_flows));"/>
					</div>
					<div class="ft_button">
				    	<input class="regular_button_000" type="button" name="del" value="&lt;=" 
		    				onClick="$('<%=PARAM_ACTION %>').value='<%=ACTION_DEL %>';tabber_right(4, '<%=response.encodeURL("Admin/SeriesManagement/series_flows.jsp")%>', get_params(document.series_flows));"/>
					</div>
				</div>
				<div class="ft_right">
					<div class="ft_caption">
			    		<%=messages.getString("series.series_flows.label.assigned")%>
					</div>
					<div class="ft_select">
						<if:formSelect name="<%=PARAM_ASSIGNED %>" edit="true" value="" multiple="true" size="10" noli="true">
						<% for(IFlowData fdAs : flowsAssigned) { %>
								<if:formOption value='<%=fdAs.getId() %>' label="<%=fdAs.getName() %>"/>	
						<% } %>						
						</if:formSelect>
					</div>
				</div>
			</div>
	      </li>
		</ol>
	</fieldset>
	<fieldset class="submit">
		<input class="regular_button_00" type="button" name="back" value="<%=messages.getString("button.back")%>" onClick="tabber_right(4, '<%=response.encodeURL(sPage+".jsp") %>', 'ts=<%=ts%>');"/>
	</fieldset>
	
</form>

