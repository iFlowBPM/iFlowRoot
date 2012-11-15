<%@page import="pt.iflow.api.utils.series.SeriesProcessor"%>
<%@page import="pt.iflow.api.utils.series.SeriesManager"%>
<%@page import="pt.iflow.api.utils.series.NumericSeriesProcessor"%>
<%@page import="pt.iflow.api.utils.series.SeriesFilter"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="pt.iflow.api.core.BeanFactory"%>
<%@page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@page import="java.util.List"%>
<%@page import="java.util.EnumSet"%>
<%
SeriesProcessor series = null;

boolean create = false;
boolean next = false;
boolean enable = false;
boolean disable = false;
boolean list = false;
UserInfoInterface guest = BeanFactory.getUserInfoFactory().newGuestUserInfo();

String name = request.getParameter("name");
if (StringUtils.isEmpty(name))
	name = "test";


if (StringUtils.isNotEmpty(request.getParameter("create")))
	create = true;

if (StringUtils.isNotEmpty(request.getParameter("next")))
	next = true;

if (StringUtils.isNotEmpty(request.getParameter("enable")))
	enable = true;

if (StringUtils.isNotEmpty(request.getParameter("disable")))
	disable = true;

if (StringUtils.isNotEmpty(request.getParameter("list")))
	list = true;


%>
<html>
<body>
<p>
<% 
if (create) {
try {
	NumericSeriesProcessor numseries = new NumericSeriesProcessor(guest, name);

	numseries.setPattern(request.getParameter("prefix"), request.getParameter("suffix"));
	if (StringUtils.isNotEmpty(request.getParameter("format"))) {
		int digits = Integer.parseInt(request.getParameter("format"));
		numseries.setFormat(digits);
	}
	if (StringUtils.isNotEmpty(request.getParameter("start"))) {
		int start = Integer.parseInt(request.getParameter("start"));
		numseries.setStartWith(start);
	}
	if (StringUtils.isNotEmpty(request.getParameter("max"))) {
		int max = Integer.parseInt(request.getParameter("max"));
		numseries.setMaxValue(max);
	}
	
	series = SeriesManager.createSeries(guest, numseries);

	out.write("series <b>" + series.getName() + "</b> created successfully with id=" + series.getId());
	
}
catch (Exception e) {
	e.printStackTrace();
	out.write("series <b>" + series.getName() + "</b> NOT created: " + e.getMessage());
	series = null;
}
}
%>
</p>
<p>
<%

if (series == null && (next || enable || disable)) {
	try {
		series = SeriesManager.getSeriesFromName(guest, name);
		out.write("series <b>" + series.getName() + "</b> LOADED (" +series.toString() + ")");
	}
	catch (Exception e) {
		e.printStackTrace();
		out.write("unable to load series <b>" + (series != null ? series.getName() : name) + "</b>: " + e.getMessage());	
	}
}
%>
</p>
<p>
<%
	if (enable || disable) {
	try {
		if (series != null) {
	if (enable) {
		series.enable();
		out.write("series <b>" + series.getName() + "</b> ENABLED");
	}
	else {
		series.disable();
		out.write("series <b>" + series.getName() + "</b> DISABLED");
	}
		}
	}
	catch (Exception e) {
		e.printStackTrace();
		out.write("unable to " + (enable ? "enable" : "disable") + " series <b>" + series.getName() + "</b>: " + e.getMessage());	
	}
}
%>
</p>
<p>
<%
if (next) {
try {
	if (series != null) {
		out.write("series <b>" + series.getName() + "</b> NEXT VALUE: " + series.getNext());					
	}
}
catch (Exception e) {
	e.printStackTrace();
	out.write("series <b>" + (series != null ? series.getName() : name) + "</b> throw error for get next: " + e.getMessage());	
}

%>
</p>
<p>
<%

try {
	if (series != null) {
		out.write("series <b>" + series.getName() + "</b> CURRENT VALUE AFTER GET NEXT: " + series.getCurrentValue());		
	}
}
catch (Exception e) {
	e.printStackTrace();
	out.write("series <b>" + series.getName() + "</b> throw error for get current value: " + e.getMessage());	
}
}
%>
</p>


<%
if (list) {
try {
	EnumSet<SeriesFilter> filter = SeriesFilter.getEnumSet();
	
	if ("true".equals(request.getParameter("enabled")))
		filter.add(SeriesFilter.ENABLED);
	if ("true".equals(request.getParameter("disabled")))
		filter.add(SeriesFilter.DISABLED);
	if ("true".equals(request.getParameter("new")))
		filter.add(SeriesFilter.NEW);
	if ("true".equals(request.getParameter("used")))
		filter.add(SeriesFilter.USED);
	if ("true".equals(request.getParameter("burned")))
		filter.add(SeriesFilter.BURNED);

	List<SeriesProcessor> seriesList = SeriesManager.listSeries(guest, filter);

	if (seriesList.size() == 0) {
		out.write("NO RESULTS");
	}
	else {
%>
<table border="1"><tr>
<td>ID</td>
<td>Name</td>
<td>Desc</td>
<td>Active</td>
<td>State</td>
<td>Pattern</td>
<td>Value</td>
</tr>
<%
for (SeriesProcessor s : seriesList) {
	%>
<tr>
<td><%=s.getId()%></td>
<td><%=s.getName()%></td>
<td><%=s.getProcessor() %></td>
<td><%=s.isEnabled() %></td>
<td><%=s.getState() %></td>
<td><%=s.getPattern() %></td>
<td><% if (s.isEnabled()) { try { out.print(s.getCurrentValue()); } catch (Exception e) { out.print("--err--"); } } else out.print("-"); %></td>	
</tr>
<%
}
%>
</table>
<%
	}
}
catch (Exception e) {
	e.printStackTrace();
	out.write("unable to list series: " + e.getMessage());	
}
}
%>


</body>
</html>