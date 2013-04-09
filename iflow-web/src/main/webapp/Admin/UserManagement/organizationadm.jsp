<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.userdata.views.*"%>

<%@ include file="../../inc/defs.jsp"%>

<%@page import="pt.iflow.userdata.views.OrganizationView"%>
<if:checkUserAdmin type="sys">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%
  String title = messages.getString("organizationadm.title");
  String sPage = "Admin/UserManagement/organizationadm";

  StringBuffer sbError = new StringBuffer();
  int flowid = -1;

  // Get users;
  OrganizationViewInterface[] orgs;
  UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);

  String sOper = fdFormData.getParameter("oper");
  String sErrorMsg = "";
  boolean bError = false;

  if ("del".equals(sOper)) {
    try {
      String orgid = fdFormData.getParameter("orgid");
      UserManager manager = BeanFactory.getUserManagerBean();
      if(!manager.removeOrganization(ui, orgid)) throw new Exception();
      request.setAttribute("infoMsg", messages.getString("organizationadm.deleteOk"));
    } catch (Exception e) {
      sErrorMsg = messages.getString("organizationadm.error.unableToDelete");
    }
  } else if ("lock".equals(sOper)) {
    try {
      String orgid = fdFormData.getParameter("orgid");
      UserManager manager = BeanFactory.getUserManagerBean();
      if(!manager.lockOrganization(ui, orgid)) throw new Exception();
    } catch (Exception e) {
      sErrorMsg = messages.getString("organizationadm.error.unableToLock");
    }
  } else if ("unlock".equals(sOper)) {
    try {
      String orgid = fdFormData.getParameter("orgid");
      UserManager manager = BeanFactory.getUserManagerBean();
      if(!manager.unlockOrganization(ui, orgid)) throw new Exception();
    } catch (Exception e) {
      sErrorMsg = messages.getString("organizationadm.error.unableToUnlock");
    }
  }

  try {
    UserManager manager = BeanFactory.getUserManagerBean();
    orgs = manager.getAllOrganizations(ui);
  } catch (Exception e) {
    orgs = new OrganizationView[0];
  }
  
  String infoMsg = (String)request.getAttribute("infoMsg");
  
  
%>

		<!-- List organizations -->
      <h1 id="title_admin"><%=title%></h1>
<%
  if (bError) {
%>
			<div class="error_msg">
			    <%=sErrorMsg%>
			</div>
<%
  }

  if (StringUtils.isNotEmpty(infoMsg)) {
%>
			<div class="info_msg">
				<%=infoMsg%>
			</div>
<%
  }
%>
			<%
			  if (orgs.length == 0) {
			%>
			<div class="info_msg">
				<%=messages.getString("organizationadm.msg.noOrgs")%>
			</div>
			<%
			  } else {
			%>
      <div class="table_inc">  
        <table class="item_list">
          <tr class="tab_header">
				<td/>
				<td>
					<%=messages.getString("organizationadm.field.name")%>
				</td>
				<td>
					<%=messages.getString("organizationadm.field.description")%>
				</td>
				<td/>
				<td/>
			</tr>
			<%
			  for (int i = 0; i < orgs.length; i++) {
			      OrganizationViewInterface org = orgs[i];
			%>
			<tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>">
				<td class="itemlist_icon">
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/organizationform.jsp")%>','orgid=<%=org.getOrganizationId()%>');"><img class="toolTipImg" src="images/icon_modify.png" width="16" height="16" border="0" title="<%=messages.getString("organizationadm.tooltip.edit")%>"></a>
				</td>
				<td>
					<%=org.getName()%>
				</td>
				<td>
					<%=org.getDescription()%>
				</td>
				<td class="itemlist_icon">
<%
  if (org.isLocked()) {
%>
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/organizationadm.jsp")%>','oper=unlock&orgid=<%=org.getOrganizationId()%>');"><img class="toolTipImg" src="images/icon_offline.png" border="0" title="<%=messages.getString("organizationadm.tooltip.unlock")%>"></a>
<%
  } else {
%>
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/organizationadm.jsp")%>','oper=lock&orgid=<%=org.getOrganizationId()%>');"><img class="toolTipImg" src="images/icon_online.png" border="0" title="<%=messages.getString("organizationadm.tooltip.lock")%>"></a>
<%
  }
%>
				</td>
				<td class="itemlist_icon">
					<a href="javascript:if (confirm('<%=messages.getString("organizationadm.confirm.delete")%>') ) tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/organizationadm.jsp")%>','oper=del&orgid=<%=org.getOrganizationId()%>');"><img class="toolTipImg" src="images/icon_delete.png" border="0" title="<%=messages.getString("organizationadm.tooltip.delete")%>"></a>
				</td>
			</tr>
			<%
			  }
			  }
			%>
		</table>
	</div>
	<div class="button_box">
    	<input class="regular_button_01" type="button" name="add" value="<%=messages.getString("button.add")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("register")%>','full=false');"/>
	</div>
