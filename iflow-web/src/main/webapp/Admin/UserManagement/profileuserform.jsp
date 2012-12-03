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
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.msg.IMessages" %>
<%@ page import="pt.iflow.api.userdata.views.*" %>
<%@ page import="pt.iflow.userdata.views.*" %>
<%@ page import="pt.iflow.profiles.ProcessUserProfiles" %>

<%@ include file = "../../inc/defs.jsp" %>
<%
      UserManager manager = BeanFactory.getUserManagerBean();
	  ProcessManager processManager = BeanFactory.getProcessManagerBean();

	  String title = messages.getString("profileuserform.title");
	  String sPage = "Admin/UserManagement/profileuserform";

	  String sOper = fdFormData.getParameter("oper");
	  
      String profileId = fdFormData.getParameter("profileid");
      UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);

      String profileName = fdFormData.getParameter("profile_name");
	  String actProcess = fdFormData.getParameter("act_process");
	  StringBuffer errorMsg = new StringBuffer("");

      if ("add".equals(sOper)) {
	      String [] users = fdFormData.getParameterValues("inactive");
	        
	      if(!(profileId == null || "".equals(profileId) || users == null || users.length == 0)) {
    	  	if (!ProcessUserProfiles.addToProfile(ui, users, profileName, profileId, StringUtils.isNotEmpty(actProcess))) {
    	  	  errorMsg.append(messages.getString("profileuserform.error.addToProfile"));
    	  	}
	      }

	  }
	  else if ("del".equals(sOper)) {
	          String [] users = fdFormData.getParameterValues("active");
		  int profileSize = Integer.parseInt(fdFormData.getParameter("profile_size"));		  
		  int usersSize = (users == null) ? 0 : users.length;		  
	          if(!(profileId == null || "".equals(profileId) || users == null || users.length == 0)) {
			  	if(profileSize <= usersSize && StringUtils.isNotEmpty(actProcess) && processManager.checkProcessInProfile(ui, profileName)) {
				  errorMsg.append(messages.getString("profileuserform.error.allusers"));
		      	}
			  	else {
				  if (!ProcessUserProfiles.deleteFromProfile(ui, users, profileId, StringUtils.isNotEmpty(actProcess))) {
					errorMsg.append(messages.getString("profileuserform.error.delFromProfile"));				    
				  }
	  	        }
	          }
	        }
	  
// Get users and profiles;
      ProfilesTO profile = new ProfilesTO();
      ProfilesTO[] profiles;
      UserViewInterface[] users;
      List<String> profileUsers = new ArrayList<String>();
      try {
        profiles = manager.getAllProfiles(ui);
        users = manager.getAllUsers(ui);
        if (profileId != null && !"".equals(profileId)) {
          profile = manager.getProfile(ui, profileId);
	      String [] userProf = manager.getProfileUsers(ui, profileId);
		  profileUsers = Arrays.asList(userProf);
        }
      }
      catch (Exception e) {
        users = new UserView[0];
        profiles = new ProfilesTO[0];
        profileUsers = new ArrayList<String>();
      }
%>

<form method="post" name="formulario" id="formulario">

    <h1 id="title_admin"><%=title%></h1>
    <label class="error_msg"><%=errorMsg.toString()%></label>
    <input type="hidden" name="profile_name" id="profile_name" value="<%=profile.getName()%>">
    <input type="hidden" name="profile_size" id="profile_size" value="<%=profileUsers.size()%>">

  	<fieldset>
  		<legend></legend>
	    <ol>
	      <li>
	        <label for="userid"><%=messages.getString("profileuserform.field.profile")%></label>
			<select name="profileid" onchange="tabber_right(4, 'Admin/UserManagement/profileuserform.jsp',get_params(document.formulario));">
				<option value="" <%=profileId == null ||"".equals(profileId)?"selected":""%>>
					<%=messages.getString("const.choose")%>
				</option>
				<% for (int i = 0; i < profiles.length; i++) { %>
					<option value="<%=profiles[i].getProfileId()%>" <%= (profiles[i].getProfileId() == profile.getProfileId())?"selected":""%>>
						(<%=profiles[i].getProfileId()%>)
						<%=profiles[i].getName()%>
						-
						<%=profiles[i].getDescription()%>
					</option>
				<%}%>
			</select>
	      </li>
		</ol>
	</fieldset>
	<fieldset>
	    <ol>
	      <li>
			<label for="unit"><%=messages.getString("profileuserform.field.users")%></label>
			<div class="ft_main">
				<div class="ft_left">
					<div class="ft_caption">
						<%=messages.getString("profileuserform.field.available")%>
					</div>
					<div class="ft_select">
						<select size="10" name="inactive" MULTIPLE>
							<% for (int i = 0; i < users.length; i++) {
								if(profileUsers.contains(users[i].getUserId())) continue; %>
							<option value="<%= users[i].getUserId()%>">
								<%=users[i].getUsername()%>
							</option>
							<%}%>
						</select>
					</div>
				</div>
				<div class="ft_middle">
					<div class="ft_button">
				    	<input class="regular_button_000" type="button" name="add_profile_users" value="=&gt;" onClick="javascript:tabber_right(4, 'Admin/UserManagement/profileuserform.jsp', 'oper=add&' + get_params(document.formulario));"/>
					</div>
					<div class="ft_button">
				    	<input class="regular_button_000" type="button" name="del_profile_users" value="&lt;=" onClick="javascript:tabber_right(4, 'Admin/UserManagement/profileuserform.jsp','oper=del&' + get_params(document.formulario));"/>
					</div>
				</div>
				<div class="ft_right">
					<div class="ft_caption">
			    		<%=messages.getString("profileuserform.field.assigned")%>
					</div>
					<div class="ft_select">
						<select size="10" name="active" MULTIPLE>
							<% for (int i = 0; i < users.length; i++) {
								if(!profileUsers.contains(users[i].getUserId())) continue; %>
								<option value="<%= users[i].getUserId()%>">
									<%=users[i].getUsername()%>
								</option>
							<%}%>
						</select>
					</div>
				</div>
			</div>
	      </li>
		</ol>
	</fieldset>
	<fieldset>
		<ol>
			<li>
				<label for="act_process"><%=messages.getString("profileuserform.actprocess")%></label>
				<input type="checkbox" title="<%=messages.getString("profileuserform.actprocess")%>" value="set" id="act_process" name="act_process" <%=(actProcess == null) || !"".equals(actProcess) ? "CHECKED" : ""%>/>
			</li>
		</ol>
	</fieldset>
	<fieldset class="submit">
		<input class="regular_button_00" type="button" name="back" value="<%=messages.getString("button.back")%>" 
    		onClick="javascript:tabber_right(4, 'Admin/UserManagement/profileadm.jsp', get_params(document.formulario));"/>
	</fieldset>
</form>
