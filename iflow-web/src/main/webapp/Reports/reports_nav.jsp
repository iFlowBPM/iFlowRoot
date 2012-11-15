<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ page import="pt.iflow.api.msg.IMessages"%>
<%@ page import="org.apache.commons.collections15.map.ListOrderedMap"
%><%@ page import="org.apache.commons.collections15.OrderedMap"
%><%@ include file="../inc/defs.jsp"%>

<%
  java.util.Date urldate = new java.util.Date();

  String sel = fdFormData.getParameter("sel");
  int nSel = AdminNavConsts.NONE;
  try {
    nSel = Integer.parseInt(sel);
  } catch (Exception e) {
  }

  UserDataAccess userDataAccess = AccessControlManager.getUserDataAccess();
  boolean canUserAdmin = userDataAccess.canUserAdmin();
  
  boolean isClassic = StringUtils.equals("classic", BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName());
%>

<%
  if (userInfo.isOrgAdmin() || userInfo.isProcSupervisor(-1)) {
%>
<h1 id="title_reports"><%=messages.getString("reports_nav.title")%></h1>
<p><%=messages.getString("reports_nav.introMsg")%></p>

<h2 onclick="javascript:toggleItemBox('reports', $('reports_section'));"
><%=messages.getString("reports_nav.section.title")%><img
	id="reports_section" class="item_title_show"
	src="images/minus.png" <%if(isClassic){%>style="display: none;"<%}%>/></h2>
	
	<ul id="reports_section_body">
		<li><a id="li_a_reports_<%=ReportsNavConsts.PERFORMANCE_CHARTS%>"
				title="<%=messages.getString("reports_nav.section.performance.tooltip")%>"
				class="toolTipItemLink li_link"
				href="javascript:selectedItem('reports', <%=ReportsNavConsts.PERFORMANCE_CHARTS%>);tabber_save(10,'','sel=<%=ReportsNavConsts.PERFORMANCE_CHARTS%>','<%=response.encodeURL("Reports/proc_perf.jsp")%>','ts=<%=ts%>');"><%=messages.getString("reports_nav.section.performance.link")%></a></li>
		<li><a id="li_a_reports_<%=ReportsNavConsts.PROCESS_STATISTICS%>"
					title="<%=messages.getString("reports_nav.section.statistics.tooltip")%>"
					class="toolTipItemLink li_link"
					href="javascript:selectedItem('reports', <%=ReportsNavConsts.PROCESS_STATISTICS%>);tabber_save(10,'','sel=<%=ReportsNavConsts.PROCESS_STATISTICS%>','<%=response.encodeURL("Reports/proc_stats.jsp")%>','ts=<%=ts%>');"><%=messages.getString("reports_nav.section.statistics.link")%></a></li>
		<li><a id="li_a_reports_<%=ReportsNavConsts.PROCESS_SLA%>"
					title="<%=messages.getString("reports_nav.section.sla.tooltip")%>"
					class="toolTipItemLink li_link"
					href="javascript:selectedItem('reports', <%=ReportsNavConsts.PROCESS_SLA%>);tabber_save(10,'','sel=<%=ReportsNavConsts.PROCESS_SLA%>','<%=response.encodeURL("Reports/proc_sla.jsp")%>','ts=<%=ts%>');"><%=messages.getString("reports_nav.section.sla.link")%></a></li>
	</ul>
<%  boolean showOnlyFlowsToBePresentInMenu = true;
    if(BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, FlowType.REPORTS, showOnlyFlowsToBePresentInMenu).length > 0){//show reports_personalized%>
<h2 onclick="javascript:toggleItemBox('reports', $('reports_personalized_section'));"
><%=messages.getString("reports_nav.personalized_section.title")%><img
	id="reports_personalized_section" class="item_title_show"
	src="images/minus.png" <%if(isClassic){%>style="display: none;"<%}%>/></h2>
	<ul id="reports_personalized_section_body">
		<form name="reports_personalized_section_form" method="post">
			<%
				//load flows
				FlowApplications appInfo = BeanFactory.getFlowApplicationsBean();
				//only loads search flows
				FlowMenu flows = BeanFactory.getFlowApplicationsBean().getAllApplicationOnlineMenu(userInfo,FlowType.REPORTS, showOnlyFlowsToBePresentInMenu);

				ArrayList<OrderedMap<Object,Object>> appFlows = new ArrayList<OrderedMap<Object,Object>>();
				Collection<FlowAppMenu> appMenuList = flows.getAppMenuList();
			  	Iterator<FlowAppMenu> iter = appMenuList.iterator();
			  	while(iter != null && iter.hasNext()) {
					FlowAppMenu appMenu = iter.next();
					String sAppName = appMenu.getAppDesc();
					FlowMenuItems menuPart = appMenu.getMenuItems();
					OrderedMap<Object,Object> hm = new ListOrderedMap<Object,Object>();
					if ("".equals(sAppName)) sAppName = messages.getString("main_content.processes.appname.misc");
					hm.put("appname", sAppName);
			  
					hm.put("appid", appMenu.getAppID());
			
					List<IFlowData> currAppflows = menuPart.getFlows(); 
			  
					hm.put("selected", false);
				        
			  		hm.put("flows", menuPart.getFlows());
			  		hm.put("links", menuPart.getLinks());
			  		hm.put("tooltip_flow", messages.getString("main_content.processes.tooltip.flows"));
			  		hm.put("tooltip_link", messages.getString("main_content.processes.tooltip.links"));
			  
			      appFlows.add(hm);
			  	}
			    String tabnr = (String) fdFormData.getParameter("navtabnr");
			    String pageContent = "proc_list";
			    java.util.Hashtable<String,Object> hsSubstLocal = new java.util.Hashtable<String,Object>();
			    hsSubstLocal.put("appflows", appFlows);
			    hsSubstLocal.put("processesMsg", "");
			    hsSubstLocal.put("tabnr", tabnr);
			    hsSubstLocal.put("title", "");
			    hsSubstLocal.put("ts", java.lang.Long.toString(ts));
			    hsSubstLocal.put("url_prefix", sURL_PREFIX.substring(0, sURL_PREFIX.length() - 1));
			    hsSubstLocal.put("css", css);
			    hsSubstLocal.put("procMenuVisible", "visible");
			    String html = PresentationManager.buildPage(response, userInfo, hsSubstLocal, pageContent);
			    out.println(html);
			%>
		</form>
	</ul>
<%
  }//end reports_personalized
  }
%>
