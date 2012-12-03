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
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.userdata.views.*"%>
<%@ page import="pt.iflow.userdata.views.*"%>
<%@ include file="../../inc/defs.jsp"%>
<%
	String title = messages.getString("profileadm.title");
String sPage = "Admin/UserManagement/profileadm";

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
UserManager manager = BeanFactory.getUserManagerBean();

if ("del".equals(sOper)){
	   try {
		  String profileid = fdFormData.getParameter("profileid");
		  manager.removeProfile(userInfo, profileid);
		}
		catch (Exception e) {
			bError = true;
			sErrorMsg = messages.getString("profileadm.error.unableToDelete");
		}	
}

// Get profiles;
      ProfilesTO[] profiles;
      UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
      
      try {
        profiles = manager.getAllProfiles(ui);
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
			    <if:message string="profileadm.msg.noProfiles" />
			</div>
<% } else { %>

      <div class="table_inc">  
        <table class="item_list">
          <tr class="tab_header">
				<td/>
				<td/>
				<td>
					<if:message string="profileadm.field.name" />
				</td>
				<td>
					<if:message string="profileadm.field.description" />
				</td>
				<td/>
			</tr>
			<%
        for (int i = 0; i < profiles.length; i++) {
          ProfilesTO profile = profiles[i];
          String profileOrg = profile.getOrganizationId();
%>
			<tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>">
				<td class="itemlist_icon">
				<% if(!StringUtils.isEmpty(profileOrg)) { %>
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/profileform.jsp")%>','profileid=<%=profile.getProfileId()%>');"><img class="toolTipImg" src="images/icon_modify.png" border="0" title="<%=messages.getString("profileadm.tooltip.edit")%>"></a>
				<% } %>
				</td>
				<td class="itemlist_icon">
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/profileuserform.jsp")%>','profileid=<%=profile.getProfileId()%>');"><img class="toolTipImg" src="images/icon_profile.png" border="0" title="<%=messages.getString("profileadm.tooltip.profiles")%>"></a>
				</td>
				<td>
					<%=profile.getName()%>
				</td>
				<td>
					<%=profile.getDescription()%>
				</td>
				<td class="itemlist_icon">
				<% if(!StringUtils.isEmpty(profileOrg)) { %>
					<a href="javascript:if (confirm('<%=messages.getString("profileadm.confirm.delete")%>') ) tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/profileadm.jsp")%>','oper=del&profileid=<%=profile.getProfileId()%>');"><img class="toolTipImg" src="images/icon_delete.png" border="0" title="<%=messages.getString("profileadm.tooltip.delete")%>"></a>
				<% } %>
				</td>
			</tr>
			<%
}
      }

    %>
		</table>
	</div>
	<div class="button_box">
    	<input class="regular_button_01" type="button" name="add" value="<%=messages.getString("button.add")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/profileform.jsp")%>','');"/>
	</div>
	<if:generateHelpBox context="profileadm"/>
