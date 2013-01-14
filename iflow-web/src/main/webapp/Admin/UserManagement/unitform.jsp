<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.msg.IMessages" %>
<%@ page import="pt.iflow.api.userdata.views.*" %>
<%@ page import="pt.iflow.userdata.views.*" %>
<%@ include file = "../../inc/defs.jsp" %>

<if:checkUserAdmin type="org">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%
	String unitId = fdFormData.getParameter("unitid");
	String sOper = fdFormData.getParameter("oper");
	String sErrorMsg = "";
	boolean bError = false;
	UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
	
	if ("add".equals(sOper)) {
	
	    boolean success = false;
	
	    try {
	   		String name = fdFormData.getParameter("name");
	   		String description = fdFormData.getParameter("description");
	        //String organizationid = fdFormData.getParameter("organizationid");
	        String parentid = fdFormData.getParameter("parentid");
	        String managerid = fdFormData.getParameter("managerid");
	        
	        if ("-1".equals(managerid)) {
	        	managerid = String.valueOf(ui.getUserId());
	        }
	
	        UserManager manager = BeanFactory.getUserManagerBean();
	        if(StringUtils.isEmpty(unitId)) {
		        success = manager.createOrganizationalUnit(ui, ui.getCompanyID(), name, description, parentid, managerid);
	        } 
	        else {
		        success = manager.modifyOrganizationalUnit(ui, unitId, ui.getCompanyID(), name, description, parentid, managerid);
	        }
		}
		catch (Exception e) {
	success = false;
		}
	
		if (success) {
	ServletUtils.sendEncodeRedirect(response, "unitadm.jsp");
	return;
		}
		else {
	bError = true;
		      if(StringUtils.isEmpty(unitId)) {
		sErrorMsg = messages.getString("unitform.error.unableToCreate");
	}
	else {
		sErrorMsg = messages.getString("unitform.error.unableToModify");					
	}
		}
	}
	
	String titulo = messages.getString("unitform.title.add");
	String botao = messages.getString("button.add");
	OrganizationalUnitViewInterface unitView = new OrganizationalUnitView(new HashMap<String,String>());
    OrganizationalUnitViewInterface [] units;
    UserViewInterface [] users;
    try {
      UserManager manager = BeanFactory.getUserManagerBean();
		units = manager.getAllOrganizationalUnits(ui);
		users = manager.getAllUsers(ui);
		
	    
		if(unitId == null || "".equals(unitId)) {
	unitId = "";
		} else {
	unitView = manager.getOrganizationalUnit(ui, unitId);
	titulo = messages.getString("unitform.title.modify");
	botao = messages.getString("button.modify");
		}
    }
    catch (Exception e) {
    	 units = new OrganizationalUnitView[0];
		    users = new UserView[0];
    }
    
    String sUnitManager = StringUtils.isEmpty(unitId) ? userInfo.getUserId() : unitView.getManagerId();
    String curName = unitView.getName();
    String curId = unitView.getUnitId();
    for(int i = 0; i < units.length; i++) {  // como o nome da unitView eh o que vai ser apresentado, obtemos o valor da lista.
		OrganizationalUnitViewInterface punit = units[i];
		if(punit.getUnitId().equals(curId)) {
		  curName = punit.getName();
		  break;
		}
    }
%>

<form method="post" name="formulario" id="formulario">
	<input type="hidden" name="unitid" value="<%=unitId%>" />

	<h1 id="title_admin"><%=titulo%></h1>

<% if (bError) { %>
	<div class="error_msg">
		<%=sErrorMsg%>
	</div>
<% } %>

  	<fieldset>
  		<legend></legend>
	    <ol>
			<if:formInput type="text" name="name" value='<%=unitView.getName()%>' labelkey="unitform.field.name" edit="true" required="true" maxlength="50"/>
			<if:formInput type="text" name="description" value='<%=unitView.getDescription()%>' labelkey="unitform.field.description" required="true" edit="true" maxlength="150"/>
			<if:formSelect name="managerid" edit="true" value='<%=sUnitManager%>' required="true" labelkey="unitform.field.manager">
			<% for(int i = 0; i < users.length; i++) {
				UserViewInterface userView = users[i]; 
				String lbl = userView.getUsername()+" - "+userView.getFirstName()+" "+userView.getLastName();%>
				<if:formOption label="<%=lbl%>" value='<%=userView.getUserId()%>' />
			<%}%>
			</if:formSelect>
			<if:formSelect name="parentid" edit="true" value='<%=unitView.getParentId()%>' required="true" labelkey="unitform.field.parent">
				<if:formOption value="-1" labelkey="const.none" />
				<% for(int i = 0; i < units.length; i++) {
					OrganizationalUnitViewInterface punit = units[i]; 
					String lbl = punit.getDescription();
					if(StringUtils.isEmpty(lbl)) lbl = punit.getName();
					String name = punit.getName();
					if(name.startsWith(curName+":") || punit.getUnitId().equals(curId)) continue; // ignore this one.
					%>
				<if:formOption label="<%=lbl%>" value='<%=punit.getUnitId()%>' />
				<%}%>
			</if:formSelect>
		</ol>
	</fieldset>
    <fieldset class="submit">
        <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/unitadm.jsp")%>');"/>
		<input class="regular_button_01" type="button" name="clear" value="<%=messages.getString("button.clear")%>" onClick="javascript:document.formulario.reset()"/>
		<input class="regular_button_01" type="button" name="add" value="<%=botao%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/unitform.jsp")%>','oper=add&' + get_params(document.formulario));"/>
	</fieldset>
</form>
