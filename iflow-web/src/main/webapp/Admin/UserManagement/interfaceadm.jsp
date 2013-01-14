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
	String title = messages.getString("organization.interfaceadm.title");
String sPage = "Admin/UserManagement/interfaceadm";

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

/*if ("del".equals(sOper)){
	   try {
		  String profileid = fdFormData.getParameter("profileid");
		  manager.removeProfile(userInfo, profileid);
		}
		catch (Exception e) {
			bError = true;
			sErrorMsg = messages.getString("profileadm.error.unableToDelete");
		}	
}*/

// Get Interfaces;
  InterfaceInfo[] interfaces;
  //UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
  
  try {
    interfaces = manager.getAllInterfaces();
  }
  catch (Exception e) {
    interfaces = new InterfaceInfo[0];
  }
%>
      <h1 id="title_admin"><%=title%></h1>
<% if (bError) { %>
			<div class="error_msg">
			    <%=sErrorMsg%>
			</div>
<% } %>

<% if (interfaces.length == 0) { %>
			<div class="info_msg">
			    <if:message string="organization.interfaceadm.msg.noInterfaces" />
			</div>
<% } else { %>

      <div class="table_inc">  
        <table class="item_list">
          <tr class="tab_header">
				<td/>
				<td>
					<if:message string="organization.interfaceadm.field.name" />
				</td>
				<td>
					<if:message string="organization.interfaceadm.field.description" />
				</td>
			</tr>
			<%
        for (int i = 0; i < interfaces.length; i++) {
          InterfaceInfo umaInterface = interfaces[i];
%>
			<tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>">
				
				<td class="itemlist_icon">
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/interfaceprofilesform.jsp")%>','interfaceid=<%=umaInterface.getInterfaceId()%>');"><img class="toolTipImg" src="images/icon_interface.png" border="0" title="<%=messages.getString("organization.interfaceadm.tooltip.profiles")%>"></a>
				</td>
				<td>
					<%=umaInterface.getName()%>
				</td>
				<td>
					<%=umaInterface.getDescription()%>
				</td>
			</tr>
			<%
}
      }

    %>
		</table>
	</div>
	<if:generateHelpBox context="interface_adm"/>