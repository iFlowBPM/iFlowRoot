<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.userdata.views.*"%>
<%@ page import="pt.iflow.userdata.views.*"%>
<%@ include file="../../inc/defs.jsp"%>
<%

// TiagOld

String title = messages.getString("organization.profilesadm.title");
String sPage = "Admin/UserManagement/profilesadm";

StringBuffer sbError = new StringBuffer();
int flowid = -1;

if (!(userInfo.isOrgAdmin() || userInfo.isSysAdmin())) {
	out.println("<div class=\"error_msg\">" + messages.getString("admin.error.unauthorizedaccess") + "</div>");
	return;
}
%>

<%@ include file = "../auth.jspf" %>

<%
String sOper = fdFormData.getParameter("oper");
String sErrorMsg = "";
boolean bError = false;
InterfacesManager manager = BeanFactory.getInterfacesManager();
UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
// Get Interfaces;
  ProfilesTO[] profiles;
  
  try {
    UserManager manager2 = BeanFactory.getUserManagerBean();
    profiles = manager2.getAllProfiles(ui);
			  }
			  catch (Exception e) {
			    profiles = new ProfilesTO[0];
			  }
%>
      <h1 id="title_admin"><%=title%></h1>
<% if (bError) { %>
			<div class="error_msg">
			    <%=sErrorMsg%>
			</div>
<% } %>

<% if (profiles.length == 0) { %>
			<div class="info_msg">
			    <if:message string="organization.profilesadm.msg.noProfiles" />
			</div>
<% } else { %>

      <div class="table_inc">  
        <table class="item_list">
          <tr class="tab_header">
				<td/>
				<td>
					<if:message string="organization.profilesadm.field.name" />
				</td>
				<td>
					<if:message string="organization.profilesadm.field.description" />
				</td>
			</tr>
			<%
		
        for (int i = 0; i < profiles.length; i++) {       
          ProfilesTO perfil = (ProfilesTO) profiles[i];
%>
			<tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>">
				
				<td class="itemlist_icon">
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/profilesinterfaceform.jsp")%>','profileid=<%=perfil.getProfileId()%>');"><img class="toolTipImg" src="images/icon_interface.png" border="0" title="<%=messages.getString("organization.profilesadm.tooltip.profiles")%>"></a>
				</td>
				<td>
					<%=perfil.getName()%>
				</td>
				<td>
					<%=perfil.getDescription()%>
				</td>
			</tr>
			<%
}
			
			%>
			<tr class="<%=0==0?"tab_row_even":"tab_row_odd"%>">
				
				<td class="itemlist_icon">
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/profilesinterfaceform.jsp")%>','profileid=<%="0"%>');"><img class="toolTipImg" src="images/icon_interface.png" border="0" title="<%=messages.getString("organization.profilesadm.tooltip.profiles")%>"></a>
				</td>
				<td>
					<%="Default"%>
				</td>
				<td>
					<%="Default"%>
				</td>
			</tr>
			<%
      }

    %>
		</table>
	</div>
	<if:generateHelpBox context="interface_profiles_adm"/>