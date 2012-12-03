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
	<%@ page language="java" contentType="text/html; charset=UTF-8"
		pageEncoding="UTF-8"%>
	<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
	<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
	<%@ page import="java.util.Vector"%>
	<%@ page import="java.util.ArrayList"%>
	<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
	<%@ page import="pt.iflow.api.core.UserManager"%>
	<%@ page import="pt.iflow.api.msg.IMessages"%>
	<%@ page import="pt.iflow.api.errors.*"%>
	<%@ page import="pt.iflow.errors.*"%>
	<%@ page import="pt.iflow.api.userdata.views.*"%>
	<%@ page import="pt.iflow.userdata.views.*"%>
	<%@ include file="../../inc/defs.jsp"%>
	<%
	  String title = messages.getString("interfaceprofilesform.title");
	
	  UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
	
	  // Get users and profiles;
	  String interfaceId = fdFormData.getParameter("interfaceid");
	  if(interfaceId.equals("")) interfaceId = "-1";
	  InterfacesManager interfaceManager = BeanFactory.getInterfacesManager();
	
	  ProfilesTO defaultProfile = new ProfilesTO(0, "default", "", "");
	
	  ProfilesTO[] profiles;
	  List<ProfilesTO> profiles_aux = new ArrayList<ProfilesTO>();
	  InterfaceInfo[] listOfInterfaces;
	  String[] interfaceProfiles;
	  List<String> listOfInterfaceProfiles = new ArrayList<String>();
	  try {
	    UserManager manager = BeanFactory.getUserManagerBean();
	    listOfInterfaces = interfaceManager.getAllInterfaces();
	    profiles = manager.getAllProfiles(ui);
	
	    for (int i = 0; i < profiles.length; i++) {
	      profiles_aux.add(profiles[i]);
	    }
	    profiles_aux.add(defaultProfile);
	    profiles = profiles_aux.toArray(new ProfilesTO[profiles_aux.size()]);
	
	    interfaceProfiles = BeanFactory.getInterfacesManager().getProfilesForInterface(ui, interfaceId);
	
	    if (interfaceManager.isInterfaceDisabledByDefault(ui, interfaceId)) {
	      listOfInterfaceProfiles.add("0");
	      for (int i = 0; i < interfaceProfiles.length; i++) {
	        listOfInterfaceProfiles.add(interfaceProfiles[i]);
	      }
	    } else {
	      listOfInterfaceProfiles = Arrays.asList(interfaceProfiles);
	    }
	
	  } catch (Exception e) {
	    listOfInterfaces = new InterfaceInfo[0];
	    profiles = new ProfilesTO[0];
	    listOfInterfaceProfiles = new ArrayList<String>();
	  }
	%>
	<form method="post" name="formulario" id="formulario">
	<h1 id="title_admin"><%=title%></h1>
	<fieldset><legend></legend>
	
	<ol>
		<li><label for="interfaceid"><%=messages.getString("interfaceprofilesform.field.user")%></label>
		<select name="interfaceid"
			onchange="tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/interfaceprofilesform.jsp")%>',get_params(document.formulario));">
			<option value=""
				<%=interfaceId == null || "".equals(interfaceId) ? "selected" : ""%>>
			<%=messages.getString("const.choose")%></option>
			<%
			  for (int i = 0; i < listOfInterfaces.length; i++) {
			    String interfaceIdFromList = "" + listOfInterfaces[i].getInterfaceId();
			%>
			<option value="<%=listOfInterfaces[i].getInterfaceId()%>"
				<%=interfaceIdFromList.equals(interfaceId) ? "selected" : ""%>>
			<%=listOfInterfaces[i].getName()%></option>
	
			<%
			  }
			%>
		</select></li>
	</ol>
	</fieldset>
	
	<fieldset>
	<ol>
		<li><label for="unit"><%=messages.getString("interfaceprofilesform.field.profiles")%></label>
		<div class="ft_main">
		<div class="ft_left">
		<div class="ft_caption"><%=messages.getString("interfaceprofilesform.field.available")%>
		</div>
		<div class="ft_select"><select size="10" name="inactive" MULTIPLE>
			<%
			  for (int i = 0; i < profiles.length; i++) {
			    if (listOfInterfaceProfiles.contains("" + profiles[i].getProfileId()))
			      continue;
			%>
			<option value="<%=profiles[i].getProfileId()%>"><%=profiles[i].getName()%><!--(<=profiles[i].getProfileId()%>) -->
			</option>
			<%
			  }
			%>
		</select></div>
		</div>
		<div class="ft_middle">
		<div class="ft_button"><input class="regular_button_000"
			type="button" name="add" value="=&gt;"
			onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/addinterfaceprofiles.jsp")%>', get_params(document.formulario));" />
		</div>
		<div class="ft_button"><input class="regular_button_000"
			type="button" name="add" value="&lt;="
			onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/delinterfaceprofiles.jsp")%>', get_params(document.formulario));" />
		</div>
		</div>
		<div class="ft_right">
		<div class="ft_caption"><%=messages.getString("interfaceprofilesform.field.assigned")%>
		</div>
		<div class="ft_select"><select size="10" name="active" MULTIPLE>
			<%
			  for (int i = 0; i < profiles.length; i++) {
			    if (!listOfInterfaceProfiles.contains("" + profiles[i].getProfileId()))
			      continue;
			%>
			<option value="<%=profiles[i].getProfileId()%>"><%=profiles[i].getName()%>
			<!--(<=profiles[i].getProfileId()%>) --></option>
			<%
			  }
			%>
		</select></div>
		</div>
		</div>
		</li>
	</ol>
	</fieldset>
	<fieldset class="submit"><input class="regular_button_00"
		type="button" name="back"
		value="<%=messages.getString("button.back")%>"
		onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/interfaceadm.jsp")%>', get_params(document.formulario));" />
		<input class="regular_button_03" type="submit" name="save" value="<if:message string="button.updateinterface"/>" />
	</fieldset>
	
	
	</form>

