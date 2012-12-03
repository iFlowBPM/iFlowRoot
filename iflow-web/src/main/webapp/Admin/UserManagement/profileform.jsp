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
<%@ page import="java.util.HashMap" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.msg.IMessages" %>
<%@ page import="pt.iflow.api.userdata.views.*" %>
<%@ page import="pt.iflow.userdata.views.*" %>
<%@ page import="pt.iflow.api.transition.ProfilesTO"%>
<%@ include file = "../../inc/defs.jsp" %>


<if:checkUserAdmin type="org">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%
	UserManager manager = BeanFactory.getUserManagerBean();
	String sOper = fdFormData.getParameter("oper");
	String sErrorMsg = "";
	boolean bError = false;
	UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
	
	if ("add".equals(sOper)) {
	
		String profileId = fdFormData.getParameter("profileid");
		String name = fdFormData.getParameter("name");
		String description = fdFormData.getParameter("description");
	    boolean success = false;
	
	    try {
			if(profileId == null || "".equals(profileId)) {
				success = manager.createProfile(ui, new ProfilesTO(name, description, ui.getCompanyID()));
			} else {
				success = manager.modifyProfile(ui, new ProfilesTO(Integer.parseInt(profileId), name, description, ui.getCompanyID()));
			}
		}
		catch (Exception e) {
			success = false;
		}
	
		if (success) {
      		ServletUtils.sendEncodeRedirect(response, "profileadm.jsp");
			return;
		}
		else {
			bError = true;
			if(profileId == null || "".equals(profileId)) {
				sErrorMsg = messages.getString("profileform.error.unableToCreate");
			}
			else {
				sErrorMsg = messages.getString("profileform.error.unableToModify");					
			}
		}
	}
		
	String titulo = messages.getString("profileform.title.add");
	String botao = messages.getString("button.add");
	String profileId = fdFormData.getParameter("profileid");
	ProfilesTO profile = new ProfilesTO();
	if (StringUtils.isNotEmpty(profileId)) {
		// Get organization;
		try {
			profile = manager.getProfile(ui, profileId);
			titulo = messages.getString("profileform.title.modify");
			botao = messages.getString("button.modify");
		}
		catch (Exception e) {
		}
	} else {
	  	profileId = "";
	}
	
	String profileName = profile != null && profile.getName() != null ? profile.getName() : "";
	String profileDesc = profile != null && profile.getDescription() != null ? profile.getDescription() : "";
%>

<form method="post" name="formulario" id="formulario">
	<input type="hidden" name="profileid" value="<%=profileId%>" />

	<h1 id="title_admin"><%=titulo%></h1>

<% if (bError) { %>
	<div class="error_msg">
		<%=sErrorMsg%>
	</div>
<% } %>

  	<fieldset>
  		<legend></legend>
	    <ol>
		<if:formInput type="text" name="name" value='<%=profileName%>' labelkey="profileform.field.name" edit="true" required="true" maxlength="50"/>
		<if:formInput type="text" name="description" value='<%=profileDesc%>' labelkey="profileform.field.description" edit="true" required="true" maxlength="125"/>
		</ol>
	</fieldset>
    <fieldset class="submit">
        <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/profileadm.jsp")%>');"/>
		<input class="regular_button_01" type="button" name="clear" value="<%=messages.getString("button.clear")%>" onClick="javascript:document.formulario.reset()"/>
		<input class="regular_button_01" type="button" name="add" value="<%=botao%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/profileform.jsp")%>','oper=add&' + get_params(document.formulario));"/>
	</fieldset>
</form>
