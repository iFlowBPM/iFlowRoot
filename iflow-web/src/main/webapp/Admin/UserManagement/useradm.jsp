<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.userdata.views.*"%>
<%@ include file="../../inc/defs.jsp"%>

<if:checkUserAdmin type="org">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%
String title = messages.getString("useradm.title");
String sPage = "Admin/UserManagement/useradm";

boolean canModify = (!Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) || AccessControlManager.getUserDataAccess().canModifyUser();
boolean canPassword = (!Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) || AccessControlManager.getUserDataAccess().canModifyPassword();
boolean canDelete = (!Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) || AccessControlManager.getUserDataAccess().canDeleteUser();

StringBuffer sbError = new StringBuffer();
int flowid = -1;
String sOper = fdFormData.getParameter("oper");
String userId = fdFormData.getParameter("userid");

UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);

UserManager manager = BeanFactory.getUserManagerBean();
String actionMsg = "";
if ("del".equals(sOper)) {
  if(manager.removeUser(ui, userId)) {
  	actionMsg = messages.getString("useradm.info.deleted");
  } else { 
  	actionMsg = messages.getString("useradm.info.not_deleted");
  }
} else if(Const.bUSE_EMAIL && "passreset".equals(sOper)) {
  if(manager.resetPassword(ui, userId)) {
  	actionMsg = messages.getString("useradm.info.password_reset");
  } else {
  	actionMsg = messages.getString("useradm.info.password_not_reset");
  }
}

// Get users;
      UserViewInterface[] users;
      HashMap<String,OrganizationalUnitViewInterface> unitCache = new HashMap<String,OrganizationalUnitViewInterface>();
      HashMap<String,OrganizationViewInterface> organizationCache = new HashMap<String,OrganizationViewInterface>();

      try {
        users = manager.getAllUsers(ui);
	    OrganizationalUnitViewInterface [] units = manager.getAllOrganizationalUnits(ui);
	    for(int i = 0; i < units.length; i++) {
	    	unitCache.put(units[i].getUnitId(), units[i]);
	    }
	    OrganizationViewInterface [] orgs = manager.getAllOrganizations(ui);
	    for(int i = 0; i < orgs.length; i++) {
	    	organizationCache.put(orgs[i].getOrganizationId(), orgs[i]);
	    }
      }
      catch (Exception e) {
        e.printStackTrace();
        users = new UserViewInterface[0];
      }

      %>

		<!-- List users -->
<form method="post" name="formulario" id="formulario">

      <h1 id="title_admin"><%=title%></h1>
<% if (StringUtils.isNotEmpty(actionMsg)) { %>
		<div class="info_msg">
			<%= actionMsg %>
		</div>
<% } %>
			<%
if (users.length == 0) {

      %>
			<div class="info_msg">
				<%= messages.getString("useradm.error.nousers")%>
			</div>
			<%
}
      else {
%>
      <div class="table_inc">  
        <table class="item_list">
          <tr class="tab_header">
<% if (canModify) { %>
				<td />
<% } %>
				<td />
				<td>
					<%=messages.getString("useradm.field.username.name")%>
				</td>
				<td>
					<%=messages.getString("useradm.field.name.name")%>
				</td>
				<td>
					<%=messages.getString("useradm.field.orgunit.name")%>
				</td>
				<% if (Const.bUSE_EMAIL && canPassword) { %>
				<td/>
				<% } %>
				<% if (canDelete) { %>
				<td/>
				<% } %>
			</tr>
			<%
        for (int i = 0; i < users.length; i++) {
          UserViewInterface userv = users[i];
          OrganizationalUnitViewInterface unito = (OrganizationalUnitViewInterface) unitCache.get(userv.getUnitId());
          OrganizationViewInterface org = (OrganizationViewInterface) organizationCache.get(unito.getOrganizationId());
          String mgrId = unito.getManagerId();
          String mgrMod = "";
          if (userv.getUserId().equals(mgrId)) {
            mgrMod = "(" + messages.getString("useradm.msg.manager") + ")";
          }
%>
			<tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>" style="<%=("1".equals(userv.getActivated()))?"":"color:red;"%>">
				<% if (canModify) { %>
				<td class="itemlist_icon">
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/userform.jsp")%>','userid=<%=userv.getUserId()%>');">
					<img class="toolTipImg" src="images/icon_modify.png" width="16" height="16" border="0" title="<%=messages.getString("useradm.tooltip.edit")%>">
					</a>
				</td>
				<% } %>
				<td class="itemlist_icon">
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/userprofileform.jsp")%>','userid=<%=userv.getUserId()%>');"><img class="toolTipImg" src="images/icon_profile.png" border="0" title="<%=messages.getString("useradm.tooltip.profiles")%>"></a>
				</td>
				<td>
					<%=userv.getUsername()%>
				</td>
				<td>
					<%=userv.getFirstName()%> <%=userv.getLastName()%>
				</td>
				<td>
					<%=(unito.getName() + mgrMod)%>
				</td>
				<% if (Const.bUSE_EMAIL && canPassword) { %>
				<td class="itemlist_icon">
					<a href="javascript:if (confirm('<%=messages.getString("useradm.confirm.password_reset")%>') ) tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/useradm.jsp")%>','userid=<%=userv.getUserId()%>&oper=passreset&' + get_params(document.formulario) );">
					<img class="toolTipImg" src="images/reset_password.png" border="0" title="<%=messages.getString("useradm.tooltip.password_reset")%>">
					</a>
				</td>
				<% } %>
				<% if (canDelete) { %>
				<td class="itemlist_icon">
					<a href="javascript:if (confirm('<%=messages.getString("useradm.confirm.delete")%>') ) tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/useradm.jsp")%>','userid=<%=userv.getUserId()%>&oper=del&' + get_params(document.formulario) );">
					<img class="toolTipImg" src="images/icon_delete.png" border="0" title="<%=messages.getString("useradm.tooltip.delete")%>">
					</a>
				</td>
				<% } %>
			</tr>
			<%
		}
      }

    %>
    
		</table>
	</div>
<% if (canModify) { %>
	<div class="button_box">
    	<input class="regular_button_01" type="button" name="add_user" value="<%=messages.getString("button.add")%>" onClick="javascript:tabber_right('admin', '<%=response.encodeURL("Admin/UserManagement/userform.jsp")%>','operation=create');"/>
<% if(Const.bUSE_EMAIL) { %>
    	<input class="regular_button_01" type="button" name="invite_user" value="<%=messages.getString("button.invite")%>" onClick="javascript:tabber_right('admin', '<%=response.encodeURL("Admin/UserManagement/userform.jsp")%>','operation=invite');"/>
<% } %>
	</div>
<% } %>

<if:generateHelpBox context="useradm"/>

</form>
