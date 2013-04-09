<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>

<if:checkUserAdmin type="both">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%

String title = messages.getString("admin-settings.title"); 
String sPage = "Admin/settings";

StringBuffer sbError = new StringBuffer();
int flowid = -1;

String sOper = fdFormData.getParameter("oper");

String sHtml = "&nbsp;";
boolean bEdit = false;

if (null == sOper) {
  sHtml = messages.getString("admin-settings.msg.reload");  
}
else if (sOper.equals("reload")) { // RELOAD PROPERTIES FROM FILE
  try {
    Setup.loadProperties();
    Const.updateConstants();
    sHtml = messages.getString("admin-settings.msg.loading");
  }
  catch (Exception e) {
    sHtml = "<div class=\"error_msg\">" + messages.getString("admin-settings.error.loading") + "</div>";
  }
}
else if (sOper.equals("save")) { // SAVE PROPERTIES TO FILE
	  try {
		Properties newProperties = new Properties();
	     for (Enumeration<String> e = fdFormData.getParameterNames() ; e.hasMoreElements() ;) {
	    	 String sParamName = e.nextElement();
			String sParamValue = fdFormData.getParameter(sParamName);
			if(!sParamName.trim().equals("oper")) {
				newProperties.setProperty(sParamName,sParamValue);
				Logger.debugJsp(userInfo.getUtilizador(), "settings.jsp", sParamName + "=" + sParamValue);
			}
	     }
		
		Setup.setProperties(newProperties,true);
	    Const.updateConstants();
	    sHtml = messages.getString("admin-settings.msg.updated");
	  }
	  catch (Exception e) {
	    sHtml = "<div class=\"error_msg\">" + messages.getString("admin-settings.error.loading") + "</div>";
	  }
	}
else if (sOper.equals("edit")) { // TOGGLE EDIT
	  try {
		bEdit = true;
	    sHtml = messages.getString("admin-settings.msg.updated");
	  }
	  catch (Exception e) {
	    sHtml = "<div class=\"error_msg\">" + messages.getString("admin-settings.error.loading") + "</div>";
	  }
	}

%>

<form name="props" method="post">
	<input type="hidden" name="oper" value="<%=sOper%>"/>

	<h1 id="title_admin"><%=title%></h1>
	
	<div class="table_inc">  

<% if (sbError.length() > 0) { %>
		<div class="error_msg">
			<%=sbError.toString()%>
		</div>
<% } %>

  <fieldset>
	<legend><%=sHtml%></legend>
    <ol>
				<%
			     for (Enumeration<?> e = Setup.getProperties().propertyNames() ; e.hasMoreElements() ;) {
			    	 String sKey = (String) e.nextElement();
					String value = Setup.getProperty(sKey);
				%>
				<if:formInput type="text" name="<%=sKey%>" value='<%=value%>' label="<%=sKey%>" edit="<%=bEdit%>"/>
				<%
				}
				%>
				
	</ol>
  </fieldset>
  <fieldset class="submit">
    <% if (bEdit) { %>
      <input class="regular_button_01" type="button" name="modify" value="<%=messages.getString("button.cancel")%>" 
          onClick="javascript:tabber_right('admin', '<%=response.encodeURL("Admin/settings.jsp") %>', 'oper=cancel&' + get_params(document.props));"/>
      <input class="regular_button_01" type="button" name="modify" value="<%=messages.getString("button.save")%>" 
          onClick="javascript:tabber_right('admin', '<%=response.encodeURL("Admin/settings.jsp") %>', 'oper=save&' + get_params(document.props));"/>
    <% } else { %>
          <input class="regular_button_01" type="button" name="reload" value="<%=messages.getString("button.reload")%>" 
          onClick="javascript:tabber_right('admin', '<%=response.encodeURL("Admin/settings.jsp") %>', 'oper=reload&' + get_params(document.props));"/>
          
      <input class="regular_button_01" type="button" name="modify" value="<%=messages.getString("button.modify")%>" 
          onClick="javascript:tabber_right('admin', '<%=response.encodeURL("Admin/settings.jsp") %>', 'oper=edit&' + get_params(document.props));"/>
          
    <% } %>
  </fieldset>

	</div>
</form>



