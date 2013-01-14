<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../inc/defs.jsp"%>

<%@ page import="pt.iflow.api.msg.IMessages"%>
<%@ page import="pt.iflow.servlets.*"%>
<%@ page import="pt.iflow.core.AccessControlManager"%>
<%@ page import="pt.iflow.api.userdata.UserDataAccess"%>
<%@page import="pt.iflow.utils.UserInfo"%>


<%
  java.util.Date urldate = new java.util.Date();

  String sel = fdFormData.getParameter("sel");
  int nSel = AdminNavConsts.NONE;
  try {
    nSel = Integer.parseInt(sel);
  } catch (Exception e) {
  }

  boolean usersSelected = (nSel == AdminNavConsts.USER_USERS
      || nSel == AdminNavConsts.USER_PROFILES || nSel == AdminNavConsts.USER_ORGANICAL_UNITS);
  // por agora reset da coisa
  usersSelected = false;

  UserDataAccess userDataAccess = AccessControlManager.getUserDataAccess();
  boolean canUserAdmin = userDataAccess.canUserAdmin();
%>

<h1 id="title_admin"><%=messages.getString("admin_nav.title")%></h1>
<%
  if (userInfo.isSysAdmin()) {
%>
<h2><%=messages.getString("admin_nav.section.system.title")%></h2>
<ul>
	<li><a id="li_a_admin_<%=AdminNavConsts.SYSTEM_PROPERTIES%>"
		title="<%=messages.getString("admin_nav.section.system.tooltip.properties")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.SYSTEM_PROPERTIES%>);tabber_save(4,'<%=response.encodeURL("Admin/admin_nav.jsp") %>','sel=<%=AdminNavConsts.SYSTEM_PROPERTIES%>','<%=response.encodeURL("Admin/settings.jsp") %>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.system.link.properties")%></a></li>

	<li><a id="li_a_admin_<%=AdminNavConsts.VERSIONS%>"
		title="<%=messages.getString("admin_nav.section.system.tooltip.versions")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.VERSIONS%>);tabber_save(4,'','sel=<%=AdminNavConsts.VERSIONS%>','<%=response.encodeURL("Admin/versions.jsp")%>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.system.link.versions")%></a></li>

	<li><a id="li_a_admin_<%=AdminNavConsts.LOGS%>"
		title="<%=messages.getString("admin_nav.section.system.tooltip.logs")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.LOGS%>);tabber_save(4,'','sel=<%=AdminNavConsts.LOGS%>','<%=response.encodeURL("Admin/logs/logs.jsp")%>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.system.link.logs")%></a></li>
    <li><a id="li_a_admin_<%=AdminNavConsts.DATASOURCES%>"
        title="<%=messages.getString("admin_nav.section.system.tooltip.datasources")%>"
        class="toolTipItemLink li_link"
        href="javascript:selectedItem('admin',<%=AdminNavConsts.DATASOURCES%>);tabber_save(4,'','sel=<%=AdminNavConsts.DATASOURCES%>','<%=response.encodeURL("Admin/db/datasources.jsp")%>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.system.link.datasources")%></a></li>
	<%-- /* %>
		<li><a class="li_link <%=(nSel == AdminNavConsts.SYSTEM_LOGGING)?"li_selected":""%>" href="javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=<%=AdminNavConsts.SYSTEM_PROPERTIES%>','Admin/logging.jsp','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.system.link.logging")%></a></li>
        <li><a class="li_link <%=(nSel == AdminNavConsts.SYSTEM_INTERFACE)?"li_selected":""%>" href="javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=<%=AdminNavConsts.SYSTEM_INTERFACE%>','Admin/iflow_interface.jsp','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.system.link.interface")%></a></li>
        <li><a class="li_link <%=(nSel == AdminNavConsts.SYSTEM_MESSAGE_BOARD)?"li_selected":""%>" href="javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=<%=AdminNavConsts.SYSTEM_MESSAGE_BOARD%>','Admin/new_features.jsp','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.system.link.msgboard")%></a></li>
        <li><a class="li_link <%=(nSel == AdminNavConsts.SYSTEM_EMAIL_TEMPLATES)?"li_selected":""%>" href="javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=<%=AdminNavConsts.SYSTEM_EMAIL_TEMPLATES%>','Admin/emailcache.jsp','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.system.link.emailtemplates")%></a></li>
        <li><a class="li_link <%=(nSel == AdminNavConsts.SYSTEM_APLICATIONS)?"li_selected":""%>" href="javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=<%=AdminNavConsts.SYSTEM_APLICATIONS%>','Admin/profiles_export.jsp','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.system.link.applications")%></a></li>
<% */ --%>
</ul>
<%
  }
  boolean isClassic = StringUtils.equals("classic", BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName());
%>


<%
  if (canUserAdmin && userInfo.isOrgAdmin()) {
%>
<h2 onclick="javascript:toggleItemBox('admin', $('admin_section_users'))"><%=messages.getString("admin_nav.section.users.title")%><img
	id="admin_section_users" class="item_title_show" src="images/minus.png"
	<%if(isClassic){%>style="display: none;"<%}%>/></h2>
<ul id="admin_section_users_body"
	class="<%= usersSelected?"selected":"" %>">
	<li><a id="li_a_admin_<%=AdminNavConsts.USER_USERS%>"
		title="<%=messages.getString("admin_nav.section.users.tooltip.users")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.USER_USERS%>);tabber_save(4,'','sel=<%=AdminNavConsts.USER_USERS%>','<%=response.encodeURL("Admin/UserManagement/useradm.jsp")%>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.users.link.users")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.USER_PROFILES%>"
		title="<%=messages.getString("admin_nav.section.users.tooltip.profiles")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.USER_PROFILES%>);tabber_save(4,'','sel=<%=AdminNavConsts.USER_PROFILES%>','<%=response.encodeURL("Admin/UserManagement/profileadm.jsp")%>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.users.link.profiles")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.USER_ORGANICAL_UNITS%>"
		title="<%=messages.getString("admin_nav.section.users.tooltip.orgunits")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.USER_ORGANICAL_UNITS%>);tabber_save(4,'','sel=<%=AdminNavConsts.USER_ORGANICAL_UNITS%>','<%=response.encodeURL("Admin/UserManagement/unitadm.jsp")%>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.users.link.orgunits")%></a></li>
</ul>
<%
  } else if (canUserAdmin && userInfo.isSysAdmin()) {
%>
<h2 onclick="javascript:toggleItemBox('admin', $('admin_section_users'))"><%=messages.getString("admin_nav.section.users.title")%><img
	id="admin_section_users" class="item_title_show" src="images/minus.png"
	<%if(isClassic){%>style="display: none;"<%}%>/></h2>
<ul id="admin_section_users_body">
	<li><a id="li_a_admin_<%=AdminNavConsts.USER_USERS%>"
		title="<%=messages.getString("admin_nav.section.users.tooltip.organizations")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.USER_ORGANIZATIONS%>);tabber_save(4,'','sel=<%=AdminNavConsts.USER_ORGANIZATIONS%>','<%=response.encodeURL("Admin/UserManagement/organizationadm.jsp")%>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.users.link.organizations")%></a></li>
</ul>
<%
  }
%>
<%
  if (userInfo.isOrgAdmin()) {
%>

<h2 onclick="javascript:toggleItemBox('admin', $('admin_section_flows'))"><%=messages.getString("admin_nav.section.flows.title")%><img
	id="admin_section_flows" class="item_title_show" src="images/minus.png"
	<%if(isClassic){%>style="display: none;"<%}%>/></h2>
<ul id="admin_section_flows_body">
	<li><a id="li_a_admin_<%=AdminNavConsts.FLOW_CREATE_AND_EDIT%>"
		title="<%=messages.getString("admin_nav.section.flows.tooltip.createAndEdit")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.FLOW_CREATE_AND_EDIT%>);tabber_save(4,'','sel=<%=AdminNavConsts.FLOW_CREATE_AND_EDIT%>','<%=response.encodeURL("Admin/flow_editor.jsp") %>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.flows.link.createAndEdit")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.FLOW_PROPERTIES%>"
		title="<%=messages.getString("admin_nav.section.flows.tooltip.properties")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.FLOW_PROPERTIES%>);tabber_save(4,'','sel=<%=AdminNavConsts.FLOW_PROPERTIES%>','<%=response.encodeURL("Admin/flow_settings.jsp") %>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.flows.link.properties")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.FLOW_MENUS%>"
		title="<%=messages.getString("admin_nav.section.flows.tooltip.menus")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.FLOW_MENUS%>);tabber_save(4,'','sel=<%=AdminNavConsts.FLOW_MENUS%>','<%=response.encodeURL("Admin/flow_menu_edit") %>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.flows.link.menus")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.FLOW_SERIES%>"
		title="<%=messages.getString("admin_nav.section.flows.tooltip.series")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.FLOW_SERIES%>);tabber_save(4,'','sel=<%=AdminNavConsts.FLOW_SERIES%>','<%=response.encodeURL("Admin/SeriesManagement/series.jsp")%>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.flows.link.series")%></a></li>
    <li>
        <a id="li_a_admin_<%=AdminNavConsts.FLOW_SCHEDULE%>"
        title="<%=messages.getString("admin_nav.section.flows.tooltip.flow_schedule")%>"
        class="toolTipItemLink li_link"
        href="javascript:selectedItem('admin',<%=AdminNavConsts.FLOW_SCHEDULE%>);tabber_save(4,'','sel=<%=AdminNavConsts.FLOW_SCHEDULE%>','<%=response.encodeURL("Admin/flow_schedule_list")%>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.flows.link.flow_schedule")%></a></li>
</ul>

<%
  }
%>
<%
  if (userInfo.isOrgAdmin()) {
%>

<h2 onclick="javascript:toggleItemBox('admin', $('admin_section_processes'))"><%=messages.getString("admin_nav.section.processes.title")%><img
	id="admin_section_processes" class="item_title_show"
	src="images/minus.png" <%if(isClassic){%>style="display: none;"<%}%>/></h2>
<ul id="admin_section_processes_body">
	<li><a id="li_a_admin_<%=AdminNavConsts.PROCESS_UNDO%>"
		title="<%=messages.getString("admin_nav.section.processes.tooltip.undo")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.PROCESS_UNDO%>);tabber_save(4,'','sel=<%=AdminNavConsts.PROCESS_UNDO%>','<%=response.encodeURL("Admin/ProcManagement/proc_undo_select.jsp") %>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.processes.link.undo")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.PROCESS_STATE%>"
		title="<%=messages.getString("admin_nav.section.processes.tooltip.status")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.PROCESS_STATE%>);tabber_save(4,'','sel=<%=AdminNavConsts.PROCESS_STATE%>','<%=response.encodeURL("Admin/ProcManagement/flow_states.jsp") %>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.processes.link.status")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.PROCESS_HISTORY%>"
		title="<%=messages.getString("admin_nav.section.processes.tooltip.hist")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.PROCESS_HISTORY%>);tabber_save(4,'','sel=<%=AdminNavConsts.PROCESS_HISTORY%>','<%=response.encodeURL("Admin/ProcManagement/proc_hist.jsp") %>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.processes.link.hist")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.PROCESS_CANCEL%>"
		title="<%=messages.getString("admin_nav.section.processes.tooltip.cancel")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.PROCESS_CANCEL%>);tabber_save(4,'','sel=<%=AdminNavConsts.PROCESS_CANCEL%>','<%=response.encodeURL("Admin/ProcManagement/proc_cancel.jsp") %>','ts=<%=ts%>&cancel=true');"><%=messages.getString("admin_nav.section.processes.link.cancel")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.PROCESS_TASK_MANAGEMENT%>"
		title="<%=messages.getString("admin_nav.section.processes.tooltip.task")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.PROCESS_TASK_MANAGEMENT%>);tabber_save(4,'','sel=<%=AdminNavConsts.PROCESS_TASK_MANAGEMENT%>','<%=response.encodeURL("Admin/ProcManagement/proc_users.jsp") %>','ts=<%=ts%>');"><%=messages.getString("admin_nav.section.processes.link.task")%></a></li>
</ul>

<%
  }
%>

<h2 onclick="javascript:toggleItemBox('admin', $('admin_section_resources'))"><%=messages.getString("admin_nav.section.resources.title")%><img
	id="admin_section_resources" class="item_title_show"
	src="images/minus.png" <%if(isClassic){%>style="display: none;"<%}%>/></h2>
<ul id="admin_section_resources_body">
	<li><a id="li_a_admin_<%=AdminNavConsts.RESOURCES_STYLESHEETS%>"
		title="<%=messages.getString("admin_nav.section.resources.tooltip.stylesheets")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.RESOURCES_STYLESHEETS%>);tabber_save(4,'','sel=<%=AdminNavConsts.RESOURCES_STYLESHEETS%>','<%=response.encodeURL("Admin/Resources/dolist.jsp") %>','type=<%=ResourceNavConsts.STYLESHEETS%>&ts=<%=ts%>');"><%=messages.getString("admin_nav.section.resources.link.stylesheets")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.RESOURCES_PRINTING%>"
		title="<%=messages.getString("admin_nav.section.resources.tooltip.print")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.RESOURCES_PRINTING%>);tabber_save(4,'','sel=<%=AdminNavConsts.RESOURCES_PRINTING%>','<%=response.encodeURL("Admin/Resources/dolist.jsp") %>','type=<%=ResourceNavConsts.PRINT_TEMPLATES%>&ts=<%=ts%>');"><%=messages.getString("admin_nav.section.resources.link.print")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.RESOURCES_EMAIL_TEMPLATES%>"
		title="<%=messages.getString("admin_nav.section.resources.tooltip.email")%>"
		class="toolTipItemLink li_link <%=(nSel == AdminNavConsts.RESOURCES_EMAIL_TEMPLATES)?"li_selected":""%>"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.RESOURCES_EMAIL_TEMPLATES%>);tabber_save(4,'','sel=<%=AdminNavConsts.RESOURCES_EMAIL_TEMPLATES%>','<%=response.encodeURL("Admin/Resources/dolist.jsp") %>','type=<%=ResourceNavConsts.EMAIL_TEMPLATES%>&ts=<%=ts%>');"><%=messages.getString("admin_nav.section.resources.link.email")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.RESOURCES_PUBLIC%>"
		title="<%=messages.getString("admin_nav.section.resources.tooltip.public")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.RESOURCES_PUBLIC%>);tabber_save(4,'','sel=<%=AdminNavConsts.RESOURCES_PUBLIC%>','<%=response.encodeURL("Admin/Resources/dolist.jsp") %>','type=<%=ResourceNavConsts.PUBLIC_FILES%>&ts=<%=ts%>');"><%=messages.getString("admin_nav.section.resources.link.public")%></a></li>
	<%
	  if (userInfo.isSysAdmin()) {
	%>
	<li><a id="li_a_admin_<%=AdminNavConsts.RESOURCES_FLOW_TEMPLATES%>"
		title="<%=messages.getString("admin_nav.section.resources.tooltip.flow_templates")%>"
		class="toolTipItemLink li_link"
		href="javascript:selectedItem('admin', <%=AdminNavConsts.RESOURCES_FLOW_TEMPLATES%>);tabber_save(4,'','sel=<%=AdminNavConsts.RESOURCES_FLOW_TEMPLATES%>','<%=response.encodeURL("Admin/flow_templates.jsp") %>','ts=<%=ts%>');"><%=messages
                .getString("admin_nav.section.resources.link.flow_templates")%></a></li>
	<%
	  }
	%>
</ul>


<h2 onclick="javascript:toggleItemBox('admin', $('admin_section_organization'))"><%=messages.getString("admin_nav.section.organization.title")%><img
	id="admin_section_organization" class="item_title_show"
	src="images/minus.png" <%if(isClassic){%>style="display: none;"<%}%>/></h2>
<ul id="admin_section_organization_body">
	<li><a id="li_a_admin_<%=AdminNavConsts.ORGANIZATION_PROPERTIES%>"
		title="<%=messages.getString("admin_nav.section.system.tooltip.properties")%>"
		class="toolTipItemLink li_link <%=(nSel == AdminNavConsts.ORGANIZATION_PROPERTIES)?"li_selected":""%>"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.ORGANIZATION_PROPERTIES%>);tabber_save(4,'','sel=<%=AdminNavConsts.ORGANIZATION_PROPERTIES%>','<%=response.encodeURL("Admin/Organization/organization.jsp") %>','ts=<%=ts%>');"><%=messages
                  .getString("admin_nav.section.organization.link.properties")%></a></li>
	<li><a id="li_a_admin_<%=AdminNavConsts.ORGANIZATION_LICENSE%>"
		title="<%=messages.getString("admin_nav.section.system.tooltip.license")%>"
		class="toolTipItemLink li_link <%=(nSel == AdminNavConsts.ORGANIZATION_LICENSE)?"li_selected":""%>"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.ORGANIZATION_LICENSE%>);tabber_save(4,'','sel=<%=AdminNavConsts.ORGANIZATION_LICENSE%>','<%=response.encodeURL("Admin/Organization/license.jsp") %>','ts=<%=ts%>');"><%=messages
                  .getString("admin_nav.section.organization.link.license")%></a></li>
   	<%if (!userInfo.isSysAdmin()){%>
    <!-- link de Organização.Interfaces -->
	<li><a id="li_a_admin_<%=AdminNavConsts.ORGANIZATION_INTERFACES%>"
		title="<%=messages.getString("admin_nav.section.system.tooltip.interface")%>"
		class="toolTipItemLink li_link <%=(nSel == AdminNavConsts.ORGANIZATION_INTERFACES)?"li_selected":""%>"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.ORGANIZATION_INTERFACES%>);tabber_save(4,'','sel=<%=AdminNavConsts.ORGANIZATION_INTERFACES%>','<%=response.encodeURL("Admin/UserManagement/interfaceadm.jsp") %>','ts=<%=ts%>');"><%=messages
                  .getString("admin_nav.section.organization.link.interfaces")%></a></li>
                      <!-- link de Organização.Profiles -->
	<li><a id="li_a_admin_<%=AdminNavConsts.ORGANIZATION_PROFILES%>"
		title="<%=messages.getString("admin_nav.section.system.tooltip.profiles")%>"
		class="toolTipItemLink li_link <%=(nSel == AdminNavConsts.ORGANIZATION_PROFILES)?"li_selected":""%>"
		href="javascript:selectedItem('admin',<%=AdminNavConsts.ORGANIZATION_PROFILES%>);tabber_save(4,'','sel=<%=AdminNavConsts.ORGANIZATION_PROFILES%>','<%=response.encodeURL("Admin/UserManagement/profilesadm.jsp") %>','ts=<%=ts%>');"><%=messages
                  .getString("admin_nav.section.organization.link.profiles")%></a></li>
    <%}%>
</ul>
	
