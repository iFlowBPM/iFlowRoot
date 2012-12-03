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
<%@ include file = "../inc/defs.jsp" %>
<%@ page import = "pt.iflow.api.core.AuthProfile" %>

<if:checkUserAdmin type="org">
  <div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%
	String title = messages.getString("profiles_edit.title");; 
String sPage = "Admin/profiles_edit";

StringBuffer sbError = new StringBuffer();

Flow flow = BeanFactory.getFlowBean();
UserManager manager = BeanFactory.getUserManagerBean();

boolean btmp = false;

String sOp = fdFormData.getParameter("op");
if (sOp == null) sOp = "0";
int op = Integer.parseInt(sOp);

int flowid = 0;
String stmp = null;
String sSave = "";  

try {
  // necessary var checking
  if (flow == null) {
    throw new Exception();
  } 
  flowid = Integer.parseInt(fdFormData.getParameter(DataSetVariables.FLOWID));
}
catch (Exception e) {
  op = 1;
}

if (op == 1) {
	ServletUtils.sendEncodeRedirect(response, "profiles.jsp");
	return;
}

String[] saExtraRow = fdFormData.getParameterValues("extrarow");


if (op == 3) {
  String[] paramProfiles = fdFormData.getParameterValues("profiles");
  if (paramProfiles != null && paramProfiles.length > 0) {
    // added new profiles
    ProfilesTO[] profiles = manager.getAllProfiles(userInfo);
    for (String paramProfile : paramProfiles) {
      ProfilesTO profile = null;
      for(ProfilesTO prof : profiles) {
        if(StringUtils.equals(paramProfile, prof.getName())) {
          profile = prof;
          break;
        }
      }
      FlowRolesTO fr = new FlowRolesTO(flowid, profile);
      // ADD default roles
      fr.grantPrivilege(FlowRolesTO.CREATE_PRIV);
      fr.grantPrivilege(FlowRolesTO.READ_PRIV);
      fr.grantPrivilege(FlowRolesTO.WRITE_PRIV);
      flow.addFlowRoles(userInfo, fr);
    }
  }
  else {
    sbError.append("<br>").append(messages.getString("profiles_edit.error.noAddSelect"));
  }
}
else if (op == 4) {
  stmp = fdFormData.getParameter("tmp");
  if (stmp != null && !stmp.equals("")) {
    ProfilesTO[] profiles = manager.getAllProfiles(userInfo);
    ProfilesTO profile = null;
    for(ProfilesTO prof : profiles) {
      if(StringUtils.equals(stmp, prof.getName())) {
        profile = prof;
        break;
      }
    }
    FlowRolesTO fr = new FlowRolesTO(flowid, profile);
    flow.removeFlowRoles(userInfo, fr);
  }
  else {
    sbError.append("<br>").append(messages.getString("profiles_edit.error.noDelSelect"));
  }
}
else if (op == 5) {
  if (saExtraRow != null && saExtraRow.length > 0) {
    for (int i=0; i < saExtraRow.length; i++) {
      ProfilesTO[] profiles = manager.getAllProfiles(userInfo);
      ProfilesTO profile = null;
      for(ProfilesTO prof : profiles) {
        if(StringUtils.equals(saExtraRow[i], prof.getName())) {
          profile = prof;
          break;
        }
      }
      FlowRolesTO fr = new FlowRolesTO(flowid, profile);
      flow.removeFlowRoles(userInfo, fr);
    }
  }
  else {
    sbError.append("<br>").append("profiles_edit.error.noMultiDelSelect");
  }
}


String sFlowName = fdFormData.getParameter("flowname");
if (sFlowName == null) sFlowName = "";

String[][] privinfo = FlowRolesTO.getPrivilegesInfo(userInfo);
FlowRolesTO[] fra = null;
fra = flow.getFlowRoles(userInfo, flowid);

if (op == 2) {
	if (fra != null && fra.length > 0) {


		if (saExtraRow != null && saExtraRow.length > 0) {
	// multiple update
	HashSet<String> hstmp = new HashSet<String>();
	for (int i=0; i < saExtraRow.length; i++) {
		hstmp.add(saExtraRow[i]);
	}

	for (int j=0; j < privinfo.length; j++) {
		stmp = fdFormData.getParameter("all_" + privinfo[j][0]);
		for (int i=0; i < fra.length; i++) {
			if (!hstmp.contains(fra[i].getProfile().getName())){
			  continue;
			}
			if (StringUtils.isEmpty(stmp)) {
				fra[i].revokePrivilege(privinfo[j][0]);
			}
			else {
				fra[i].grantPrivilege(privinfo[j][0]);
			}
		}
	}
	// set roles
	flow.setFlowRoles(userInfo,fra);
	// fetch updated roles
	fra = flow.getFlowRoles(userInfo, flowid);

		}
		else {
	for (int i=0; i < fra.length; i++) {
		for (int j=0; j < privinfo.length; j++) {
			stmp = fdFormData.getParameter("p" + i + "_" + privinfo[j][0]);
			if (StringUtils.isEmpty(stmp)) {
				fra[i].revokePrivilege(privinfo[j][0]);
			}
			else {
				fra[i].grantPrivilege(privinfo[j][0]);
			}
		}
	}
	// set roles
	flow.setFlowRoles(userInfo,fra);
	// fetch updated roles
	fra = flow.getFlowRoles(userInfo, flowid);
		}
	}
}
Collection<String> profiles = (Collection<String>) ap.getAllProfiles(userInfo.getOrganization());

String[] satmp = null;
if(profiles != null) satmp = profiles.toArray(new String[profiles.size()]);

String[] saProfiles = new String[0];
if (satmp == null) {
  satmp = new String[0];
}

 
// now remove adm profile and profiles already assigned to this flow
int size = 0;
for (int i=0; i < satmp.length; i++) {
    for (int j=0; j < fra.length; j++) {
      if (satmp[i].equals(fra[j].getProfile().getName())) {
	satmp[i] = null;
	break;
      }
    }

  if (satmp[i] == null) {
    continue;
  }

  size ++;
}


saProfiles = new String[size];
size = 0;
for (int i=0; i < satmp.length; i++) {
  if (satmp[i] != null) {
    saProfiles[size++] = satmp[i];
  }
}
%>

<form name="flows" method="post">

  <input type="hidden" name="flowid" value="<%=flowid%>">
  <input type="hidden" name="flowname" value="<%=(sFlowName)%>">
  <input type="hidden" name="op" value="0">
  <input type="hidden" name="tmp" value="">

      <h1 id="title_admin"><%=title%></h1>
<% if (sbError.length() > 0) { %>
      <div class="error_msg">
        <%=sbError.toString()%>
	  </div>
<% } %>
      <div class="info_msg">
        <%=messages.getString("profiles_edit.msg.flowName", sFlowName)%>
	  </div>
      <div class="table_inc">  
        <table width="100%" cellpadding="2">
          <tr class="tab_header">
            <td><%=messages.getString("profiles_edit.field.profile")%></td>
<%
	for (int i=0; i < privinfo.length; i++) {
%>	
            <td><%=privinfo[i][1]%></td>
<%
	}
%>
            <td><%=messages.getString("profiles_edit.field.delete")%></td>
          </tr>
          <tr class="tab_row_extra">
            <td></td>
<%
	for (int i=0; i < privinfo.length; i++) {
%>	
            <td><a class="cell_button" href="javascript:toggleall(<%=i%>,<%=fra.length%>);"><%=messages.getString("profiles_edit.link.toggleAll")%></a></td>
<%
	}
%>
            <td />
          </tr>
<%
        if (fra != null && fra.length > 0) {
	  for (int i=0; i < fra.length; i++) {
%>
          <tr class="<%=(i%2==0?"tab_row_even":"tab_row_odd")%>">
      	     <td><%=fra[i].getProfile().getName()%></td>
<%
	    for (int j=0; j < privinfo.length; j++) {
	      stmp = "";
	      if (fra[i].hasPrivilege(privinfo[j][0])) {
			  stmp = "checked";
	      }
%>	
              <td>
                <input type="checkbox" name="p<%=i%>_<%=privinfo[j][0]%>" id="p<%=i%>_<%=privinfo[j][0]%>" <%=stmp%>>
              </td>
<%
	    }
%>
            <td><a <%=Utils.genHRef("javascript:tabber_right(4,'"+response.encodeURL("Admin/profiles_edit.jsp")+"','tmp=" + StringEscapeUtils.escapeJavaScript(fra[i].getProfile().getName()) + "&op=4&'+get_params(document.flows));", btmp)%>><img src="images/icon_delete.png" border="0" title="Delete" alt="Delete"></a></td>
          </tr>
<%
	  }
	  if(fra.length > 0) {
%>
		<tr class="tab_row_extra">
			<td>
				<select name="extrarow" size="5" multiple="multiple" >
<%
		for (int i=0; i < fra.length; i++) {
  			btmp = true;
  			String profile = fra[i].getProfile().getName();
%>
					<option value="<%=profile%>"><%=profile%></option>
<%		} %>
				</select>
			</td>
<% 		for (int j=0; j < privinfo.length; j++) {%>
			<td><%-- <input type="checkbox" name="all_<%=privinfo[j][0]%>"> --%></td>
<% 		} %>
			<td>
				<a class="cell_button" href="javascript:tabber_right(4,'<%=response.encodeURL("Admin/profiles_edit.jsp") %>','op=5&'+get_params(document.flows));">
					<if:message string="profiles_edit.field.deleteSelected" />
				</a>
			</td>
		</tr>
<% 	  } 
	}
	else {
%>
<tr><td colspan="<%=2+privinfo.length%>">
      <div class="info_msg">
       <if:message string="profiles_edit.msg.noProfiles" />
      </div>
</td></tr>
<%
        }
%>

	</table>
	</div>

    <div class="button_box">
 		<input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_settings.jsp") %>');"/>
    	<input class="regular_button_01" type="button" name="update" value="<%=messages.getString("button.update")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/profiles_edit.jsp") %>','op=2&' + get_params(document.flows));"/>
	</div>    

	<fieldset>
		<ol>
			<li>
				<label for="profiles"><%=messages.getString("profiles_edit.field.select")%></label>
				<select name="profiles" class="txt" <%= saProfiles.length==0?"disabled=\"disabled\"":"multiple size=\"10\""%> >
<% for (int i=0; i < saProfiles.length; i++) {%>
					<option value="<%=saProfiles[i] %>"><%= saProfiles[i] %></option>
<% } %>
				</select>
				<input class="regular_button_01" type="button" name="add" value="<%=messages.getString("button.add")%>" 
						onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/profiles_edit.jsp") %>','op=3&' + get_params(document.flows));"
				/>
			</li>
		</ol>
	</fieldset>
</form>

