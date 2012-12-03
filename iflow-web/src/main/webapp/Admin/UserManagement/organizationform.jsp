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
<%@ page import="pt.iflow.api.userdata.views.OrganizationViewInterface"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.msg.IMessages" %>
<%@ page import="pt.iflow.userdata.views.*" %>
<%@ include file = "../../inc/defs.jsp" %>

<%@page import="pt.iflow.api.userdata.views.OrganizationViewInterface"%>
<if:checkUserAdmin type="sys">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

		<%
			String orgId = fdFormData.getParameter("orgid");
				String sOper = fdFormData.getParameter("oper");
				String sErrorMsg = "";
				boolean bError = false;

				if ("edit".equals(sOper)) {
				
		      		String name = fdFormData.getParameter("name");
		      		String description = fdFormData.getParameter("description");

				  	UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
			      	boolean success = false;
			      	try {
			          UserManager manager = BeanFactory.getUserManagerBean();
				      success = manager.modifyOrganization(ui, orgId, name, description);
				 	}
			      	catch (Exception e) {
			        	success = false;
			      	}

				    if (success) {
				      ServletUtils.sendEncodeRedirect(response, "organizationadm.jsp");
				    	return;
				    }
				    else {
				    	bError = true;
			    		sErrorMsg = messages.getString("organizationform.error.unableToModify");		    		
				    }

				}
				
			  String titulo = messages.getString("organizationform.title.add");
		      OrganizationViewInterface org = new OrganizationView(new HashMap<String,String>()); // empty org
		      if (orgId != null && !"".equals(orgId)) {
		        // Get organization;
		        UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
		        try {
		          UserManager manager = BeanFactory.getUserManagerBean();
		          org = manager.getOrganization(ui, orgId);
		  	      titulo = messages.getString("organizationform.title.modify");
		        }
		        catch (Exception e) {
		        }
		      } else {
		        ServletUtils.sendEncodeRedirect(response, "organizationadm.jsp");
		        return;
		      }
		%>
      
            
<form action="<%=response.encodeURL("addorg.jsp")%>" method="post" name="formulario" id="formulario">
	<input type="hidden" name="orgid" value="<%=orgId%>" />

    <h1 id="title_admin"><%=titulo%></h1>

<% if (bError) { %>
	<div class="error_msg">
		<%=sErrorMsg%>
	</div>
<% } %>

  	<fieldset>
  		<legend></legend>
	    <ol>
			<if:formInput type="text" name="name" value='<%=org.getName()%>' labelkey="organizationform.field.name" edit="true" required="true" maxlength="50"/>
			<if:formInput type="text" name="description" value='<%=org.getDescription()%>' labelkey="organizationform.field.description" edit="true" maxlength="150"/>
		</ol>
	</fieldset>
    <fieldset class="submit">
        <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/organizationadm.jsp")%>');"/>
		<input class="regular_button_01" type="button" name="clear" value="<%=messages.getString("button.clear")%>" onClick="javascript:document.formulario.reset()"/>
		<input class="regular_button_01" type="button" name="add" value="<%=messages.getString("button.modify")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/organizationform.jsp")%>','oper=edit&' + get_params(document.formulario));"/>
	</fieldset>
</form>
