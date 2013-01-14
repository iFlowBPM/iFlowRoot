<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@page import="pt.iflow.api.utils.series.SeriesProcessor"%>
<%@page import="pt.iflow.api.utils.series.SeriesManager"%>
<%@page import="pt.iflow.api.utils.series.NumericSeriesProcessor"%>
<%@page import="pt.iflow.api.utils.series.NumericYearSeriesProcessor"%>
<%@page import="pt.iflow.api.utils.series.SeriesException"%>
<%@page import="pt.iflow.api.utils.series.DuplicateSeriesException"%>
<%@page import="java.text.MessageFormat" %>
<%@ include file = "../../inc/defs.jsp" %>

<if:checkUserAdmin type="org">
	<div class="error_msg">
		<if:message string="admin.error.unauthorizedaccess" />
	</div>
</if:checkUserAdmin>


<%

final String PARAM_ACTION = "action";

final String ACTION_ADD = "add";
final String ACTION_PREVIEW = "preview";

final String PARAM_NAME = "name";
final String PARAM_TYPE = "type";
final String PARAM_PREFIX = "prefix";
final String PARAM_SUFFIX = "suffix";
final String PARAM_LENGTH = "size";
final String PARAM_START = "start";
final String PARAM_MAX = "max";


String title = messages.getString("series.add.title");
String sPage = "Admin/SeriesManagement/series_add";

boolean success = true;
boolean done = false;
StringBuilder infoMsg = new StringBuilder();
StringBuilder errorMsg = new StringBuilder();  


NumericSeriesProcessor nsp = null;

String action = fdFormData.getParameter(PARAM_ACTION);

String name = fdFormData.getParameter(PARAM_NAME);
String prefix = fdFormData.getParameter(PARAM_PREFIX);
String suffix = fdFormData.getParameter(PARAM_SUFFIX);
String length = fdFormData.getParameter(PARAM_LENGTH);
String start = fdFormData.getParameter(PARAM_START);
String max = fdFormData.getParameter(PARAM_MAX);

if (StringUtils.isEmpty(name)) 
	name = null;

String type = fdFormData.getParameter(PARAM_TYPE);
if (StringUtils.equals(NumericYearSeriesProcessor.TYPE, type)) {
  nsp = new NumericYearSeriesProcessor(userInfo, name);
}
else {
  nsp = new NumericSeriesProcessor(userInfo, name);
}


if (name == null) name = "";
if (prefix == null) prefix = "";
if (suffix == null) suffix = "";
if (length == null) length = "";
if (start == null) start = "";
if (max == null) max = "";


nsp.setPattern(prefix, suffix);

if (StringUtils.isNotEmpty(length)) {
	try {
		int l = Integer.parseInt(length);
		nsp.setFormat(l);
	}
	catch (Exception e) {
		errorMsg.append(messages.getString("series.add.field.length")).append(": ").append(messages.getString("series.add.input_error")).append("<br/>");
		success = false;
	}
}
if (StringUtils.isNotEmpty(start)) {
	try {
		int s = Integer.parseInt(start);
		nsp.setStartWith(s);
	}
	catch (Exception e) {
		errorMsg.append(messages.getString("series.add.field.startWith")).append(": ").append(messages.getString("series.add.input_error")).append("<br/>");
		success = false;
	}
}
if (StringUtils.isNotEmpty(max)) {
	try {
		int m = Integer.parseInt(max);
		nsp.setMaxValue(m);
	}
	catch (Exception e) {
		errorMsg.append(messages.getString("series.add.field.maxValue")).append(": ").append(messages.getString("series.add.input_error")).append("<br/>");
		success = false;
	}
}


if (ACTION_PREVIEW.equals(action)) {
	try {
		

		out.println(nsp.preview());
		return;
	}
	catch (Exception e) {
		return;
	}
}
else if (ACTION_ADD.equals(action)) {
	
	
	if (StringUtils.isEmpty(name)) {
		errorMsg.append(messages.getString("series.add.field.name")).append(": ").append(messages.getString("series.add.input_error")).append("<br/>");
		success = false;
	}
	
	if (StringUtils.isEmpty(start)) {
		errorMsg.append(messages.getString("series.add.field.startWith")).append(": ").append(messages.getString("series.add.input_error")).append("<br/>");
		success = false;
	}
	
	if (success) {
		try {
			SeriesManager.createSeries(userInfo, nsp);
			done = true;
			success = true;
			infoMsg.append(messages.getString("series.add.done"));
		}
		catch  (DuplicateSeriesException dse) {
			errorMsg.append(messages.getString("series.add.error.duplicate"));
			success = false;
			Logger.errorJsp(login,sPage,"duplicate series exception creating series: " + dse.getMessage());			
		}
		catch (SeriesException se) {
			errorMsg.append(messages.getString("series.add.error.generic"));
			success = false;
			Logger.errorJsp(login,sPage,"exception creating series: " + se.getMessage());
		}
	}
}


String onchange = "$('" + PARAM_ACTION + "').value='" + ACTION_PREVIEW + "';new Ajax('" + response.encodeURL("Admin/SeriesManagement/series_add.jsp")+"', {method: 'get', data: $('series_add').toQueryString(), update: 'preview'}).request();$('previewItem').setStyle('display','');";

%>

<form method="post" name="series_add" id="series_add">
<input type="hidden" name="<%=PARAM_ACTION %>" id="<%=PARAM_ACTION %>" value=""/>
<input type="hidden" name="ts" id="ts" value="<%=ts %>"/>
<h1 id="title_admin"><%=title%></h1>

<% if (!success) { %>
<div class="error_msg"><%=errorMsg.toString()%></div>
<% } %>

<% if (infoMsg.length() > 0) { %>
<div class="info_msg"><%=infoMsg.toString()%></div>
<% } %>

<% if (!done) { %>
<fieldset><legend></legend>
<ol>
  <if:formInput name="<%=PARAM_NAME %>" labelkey="series.add.field.name" type="text" value='<%=name%>' edit="true" required="true" maxlength="15" />
  
  <% String typeOnChange = "tabber_right(4, '"+response.encodeURL("Admin/SeriesManagement/series_add.jsp")+"',get_params(document.series_add));"; %>
  <if:formSelect name="<%=PARAM_TYPE %>" edit="true" value='<%=nsp.getType() %>' labelkey="series.add.field.type" onchange="<%=typeOnChange%>">
  	<if:formOption value='<%=NumericSeriesProcessor.TYPE %>' label="<%=messages.getString(MessageFormat.format(SeriesProcessor.DESCRIPTION_KEY_FORMAT, NumericSeriesProcessor.PROCESSOR)) %>"/>
  	<if:formOption value='<%=NumericYearSeriesProcessor.TYPE %>' label="<%=messages.getString(MessageFormat.format(SeriesProcessor.DESCRIPTION_KEY_FORMAT, NumericYearSeriesProcessor.PROCESSOR)) %>"/>
  </if:formSelect>
  
  <if:formInput name="<%=PARAM_PREFIX %>" labelkey="series.add.field.prefix" type="text" value='<%=prefix%>' edit="true" required="false" maxlength="15" onchange="<%=onchange %>" />
  <if:formInput name="<%=PARAM_LENGTH %>" labelkey="series.add.field.length" type="text" value='<%=length%>' edit="true" required="false" maxlength="15" onchange="<%=onchange %>" />
  <if:formInput name="<%=PARAM_SUFFIX %>" labelkey="series.add.field.suffix" type="text" value='<%=suffix%>' edit="true" required="false" maxlength="15" onchange="<%=onchange %>" />
  <if:formInput name="<%=PARAM_START %>" labelkey="series.add.field.startWith" type="text" value='<%=start%>' edit="true" required="true" maxlength="15" />
  <if:formInput name="<%=PARAM_MAX %>" labelkey="series.add.field.maxValue" type="text" value='<%=max%>' edit="true" required="false" maxlength="15" />
  
  <li></li>
  <li id="previewItem" style="display:none"><label for="preview">Preview:</label><div id="preview"></div></li>

</ol>
</fieldset>
<% } %>
<fieldset class="submit">
  <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" onClick="tabber_right(4, '<%=response.encodeURL("Admin/SeriesManagement/series.jsp")%>','ts=<%=ts %>');" />
<% if (!done) { %>
  <input class="regular_button_01" type="button" name="add" value="<%=messages.getString("button.add")%>" onClick="$('<%=PARAM_ACTION%>').value='<%=ACTION_ADD%>';tabber_right(4, '<%=response.encodeURL("Admin/SeriesManagement/series_add.jsp")%>', get_params(document.series_add));" />
<% } %>
</fieldset>
</form>
