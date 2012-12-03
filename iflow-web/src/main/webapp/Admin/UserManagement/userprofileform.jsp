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
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.msg.IMessages" %>
<%@ page import="pt.iflow.api.errors.*" %>
<%@ page import="pt.iflow.errors.*" %>
<%@ page import="pt.iflow.api.userdata.views.*" %>
<%@ page import="pt.iflow.userdata.views.*" %>
<%@ include file = "../../inc/defs.jsp" %>
<%
	 String title = messages.getString("userprofileform.title");

	// Get users and profiles;
      UserViewInterface userView = new UserView(new HashMap<String,String>());
      String userId = fdFormData.getParameter("userid");
      UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
      ProfilesTO[] profiles;
      UserViewInterface[] users;
      List<String> userProfiles = new ArrayList<String>();
      try {
        UserManager manager = BeanFactory.getUserManagerBean();
        profiles = manager.getAllProfiles(ui);
        users = manager.getAllUsers(ui);
        if (userId != null && !"".equals(userId)) {
          userView = manager.getUser(ui, userId);
	      String [] userProf = manager.getUserProfiles(ui, userId);
		  userProfiles = Arrays.asList(userProf);
        }
      }
      catch (Exception e) {
        users = new UserView[0];
        profiles = new ProfilesTO[0];
        userProfiles = new ArrayList<String>();
      }
%>
<form method="post" name="formulario" id="formulario">
    <h1 id="title_admin"><%=title%></h1>
  	<fieldset>
  		<legend></legend>
	    <ol>
	      <li>
	        <label for="userid"><%=messages.getString("userprofileform.field.user")%></label>
			<select name="userid" onchange="tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/userprofileform.jsp")%>',get_params(document.formulario));">
				<option value="" <%=userId == null ||"".equals(userId)?"selected":""%>>
					<%=messages.getString("const.choose")%>
				</option>
				<% for (int i = 0; i < users.length; i++) { %>
					<option value="<%=users[i].getUserId()%>" <%= users[i].getUserId().equals(userView.getUserId())?"selected":""%>>
						<%=users[i].getUsername()%>
						-
						<%=users[i].getFirstName()%>
						<%=users[i].getLastName()%>
					</option>
				<%}%>
			</select>
	      </li>
		</ol>
	</fieldset>
	
  	<fieldset>
	    <ol>
	      <li>
			<label for="unit"><%=messages.getString("userprofileform.field.profiles")%></label>
			<div class="ft_main">
				<div class="ft_left">
					<div class="ft_caption">
						<%=messages.getString("userprofileform.field.available")%>
					</div>
					<div class="ft_select">
						<select size="10" name="inactive" MULTIPLE>
							<% for (int i = 0; i < profiles.length; i++) {
								if(userProfiles.contains("" + profiles[i].getProfileId())) continue; %>
							<option value="<%= profiles[i].getProfileId()%>">
								<%=profiles[i].getName()%><!--(<=profiles[i].getProfileId()%>) -->
							</option>
							<%}%>
						</select>
					</div>
				</div>
				<div class="ft_middle">
					<div class="ft_button">
				    	<input class="regular_button_000" type="button" name="add" value="=&gt;" 
				    		onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/adduserprofiles.jsp")%>', get_params(document.formulario));"/>
					</div>
					<div class="ft_button">
				    	<input class="regular_button_000" type="button" name="add" value="&lt;=" 
		    				onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/deluserprofiles.jsp")%>', get_params(document.formulario));"/>
					</div>
				</div>
				<div class="ft_right">
					<div class="ft_caption">
			    		<%=messages.getString("userprofileform.field.assigned")%>
					</div>
					<div class="ft_select">
						<select size="10" name="active" MULTIPLE>
							<% for (int i = 0; i < profiles.length; i++) {
									if(!userProfiles.contains("" + profiles[i].getProfileId())) continue; %>
								<option value="<%= profiles[i].getProfileId()%>">
									<%=profiles[i].getName()%> <!--(<=profiles[i].getProfileId()%>) -->
								</option>
							<%}%>
						</select>
					</div>
				</div>
			</div>
	      </li>
		</ol>
	</fieldset>
	<fieldset class="submit">
		<input class="regular_button_00" type="button" name="back" value="<%=messages.getString("button.back")%>" 
    		onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/useradm.jsp")%>', get_params(document.formulario));"/>
	</fieldset>
	
	</form>

