<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.msg.IMessages" %>
<%@ page import="pt.iflow.api.errors.*" %>
<%@ page import="pt.iflow.errors.*" %>
<%@ page import="pt.iflow.api.userdata.views.*" %>
<%@ page import="pt.iflow.userdata.views.*" %>
<%@ include file = "../../inc/defs.jsp" %>
<if:checkUserAdmin type="org">
	<div class="error_msg">
		<if:message string="admin.error.unauthorizedaccess" />
	</div>
</if:checkUserAdmin>
<%!
  private static String getErrorMessage(IMessages msg, ErrorCode errCode, String defaultMsg) {
    String sErrorMsg = "";
    if (UserErrorCode.FAILURE_DUPLICATE_USER.equals(errCode)) {
      sErrorMsg += "<br>" + msg.getString("userform.error.duplicateUser");
    } else if (UserErrorCode.FAILURE_DUPLICATE_EMAIL.equals(errCode)) {
      sErrorMsg += "<br>" + msg.getString("userform.error.duplicateEmail");
    } else if (ErrorCode.SEND_EMAIL.equals(errCode)) {
      sErrorMsg += "<br>" + msg.getString("userform.error.sendEmail");
    } else {
      sErrorMsg += "<br>" + msg.getString(defaultMsg);
    }
    return sErrorMsg;
  }
%><%
	boolean bEdit = false;
  boolean bEditUsername = false;
  IErrorHandler errorHandler = null;
  String sOper = fdFormData.getParameter("operation");
  boolean success = false;
  UserInfoInterface ui = userInfo;
  
  String infoMsg = null;

  String sErrorMsg = "";

  String userId = fdFormData.getParameter("userid");
  if(StringUtils.isEmpty(sOper) && StringUtils.isEmpty(userId)) sOper = "create";
  
  String username = "";
  String gender = "";
  String unitId = "";
  String emailAddress = "";
  String firstName = "";
  String lastName = "";
  String phoneNumber = "";
  String faxNumber = "";
  String mobileNumber = "";
  String companyPhone = "";
  String orgadm = "";
  String orgadmUsers = "";
  String orgadmFlows = "";
  String orgadmProcesses = "";
  String orgadmResources = "";
  String orgadmOrg = "";
  String orgServiceUser = "";
  String password = "";
  String repeatPass = "";

  List<String> lst = AccessControlManager.getUserDataAccess().getListExtraProperties();
  String[] listExtraProperties = lst.toArray(new String[lst.size()]);
  
  username = fdFormData.getParameter("username");
  gender = fdFormData.getParameter("gender");
  unitId = fdFormData.getParameter("unit");
  emailAddress = fdFormData.getParameter("emailAddress");
  firstName = fdFormData.getParameter("firstName");
  lastName = fdFormData.getParameter("lastName");
  phoneNumber = fdFormData.getParameter("phoneNumber");
  faxNumber = fdFormData.getParameter("faxNumber");
  mobileNumber = fdFormData.getParameter("mobileNumber");
  companyPhone = fdFormData.getParameter("companyPhone");
  orgadm = fdFormData.getParameter("orgadm");
  orgadmUsers = fdFormData.getParameter("orgadmUsers");
  orgadmFlows = fdFormData.getParameter("orgadmFlows");
  orgadmProcesses = fdFormData.getParameter("orgadmProcesses");
  orgadmResources = fdFormData.getParameter("orgadmResources");
  orgadmOrg = fdFormData.getParameter("orgadmOrg");
  orgServiceUser = fdFormData.getParameter("orgServiceUser");
  password = fdFormData.getParameter("password");
  repeatPass = fdFormData.getParameter("repeatPassword");
  String[] listExtraValues = new String[listExtraProperties.length];
  for (int i = 0; i < listExtraValues.length; i++) {
    listExtraValues[i] = fdFormData.getParameter("extra_"+listExtraProperties[i]);
  }

  if (username == null) username = "";
  if (gender == null) gender = "";
  if (emailAddress == null) emailAddress = "";
  if (firstName == null) firstName = "";
  if (lastName == null) lastName = "";
  if (phoneNumber == null) phoneNumber = "";
  if (faxNumber == null) faxNumber = "";
  if (mobileNumber == null) mobileNumber = "";
  if (companyPhone == null) companyPhone = "";
  if (orgadm == null) orgadm = "";
  if (orgadmUsers == null) orgadmUsers = "";
  if (orgadmFlows == null) orgadmFlows = "";
  if (orgadmProcesses == null) orgadmProcesses = "";
  if (orgadmResources == null) orgadmResources = "";
  if (orgadmOrg == null) orgadmOrg = "";
  if (orgServiceUser == null) orgServiceUser = "";
  if (password == null) password = "";
  if (repeatPass == null) repeatPass = "";
  for (int i = 0; i < listExtraValues.length; i++) {
    if (listExtraValues[i] == null) listExtraValues[i] = "";
  }

  UserManager manager = BeanFactory.getUserManagerBean();
  
  if ("add".equals(sOper) || "send_invite".equals(sOper)) {
    boolean sendInvite = "send_invite".equals(sOper);
    
    
    String orgID = null;
    
    if(sendInvite) {
      orgID = ui.getCompanyID();
    } else {
      orgID = null;
    }
  
    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(firstName) || StringUtils.isEmpty(lastName) || 
        (Const.bUSE_EMAIL && StringUtils.isEmpty(emailAddress)) ||
        (!Const.bUSE_EMAIL && !sendInvite && StringUtils.isEmpty(password))) {
      sErrorMsg = messages.getString("userform.error.mandatoryFields");
    }
    else if (!Const.bUSE_EMAIL && !sendInvite && !repeatPass.equals(password)){
      sErrorMsg = messages.getString("userform.error.mistypedPassword");
    }
    else {
      if (sendInvite) {
        errorHandler = manager.inviteUser(ui, username, gender, unitId, emailAddress, firstName, lastName, phoneNumber,
             faxNumber, mobileNumber, companyPhone, orgID, orgadm, listExtraProperties, listExtraValues);
      }
      else { 
        errorHandler = manager.createUser(ui, username, gender, unitId, emailAddress, firstName, lastName, phoneNumber,
            faxNumber, mobileNumber, companyPhone, orgID, orgadm, password, listExtraProperties, listExtraValues);
      }
    }
   
    ErrorCode errCode = (errorHandler != null)?errorHandler.getErrorCode():null;
   
    if (success || UserErrorCode.SUCCESS.equals(errCode)) {
		ServletUtils.sendEncodeRedirect(response, "useradm.jsp");
	    return;
    } else {
      sErrorMsg += getErrorMessage(messages, errCode,"userform.error.unableToCreate");
    }
  } else if("update".equals(sOper)) {
    if (StringUtils.isEmpty(firstName) || StringUtils.isEmpty(lastName) || (Const.bUSE_EMAIL && StringUtils.isEmpty(emailAddress))) {
      sErrorMsg = messages.getString("userform.error.mandatoryFields");
    }
    else if(!Const.bUSE_EMAIL && StringUtils.isNotEmpty(password) && !StringUtils.equals(password,repeatPass)) {
      sErrorMsg = messages.getString("userform.error.mistypedPassword");
    }
    else { 
      errorHandler = manager.modifyUserAsAdmin(ui, userId, gender, unitId, emailAddress, firstName, lastName, phoneNumber,
          faxNumber, mobileNumber, companyPhone, orgadm, orgadmUsers, orgadmFlows, orgadmProcesses, orgadmResources, orgadmOrg, orgServiceUser, password, listExtraProperties, listExtraValues);
      ErrorCode errCode = (errorHandler != null)?errorHandler.getErrorCode():null;
      
      if (UserErrorCode.SUCCESS.equals(errCode)) {
        sOper = "";
        success = true;
      } else if (UserErrorCode.PENDING_ORG_ADM_EMAIL.equals(errCode)) {
        sOper = "";
        success = true;
        infoMsg = messages.getString("userform.info.confirmPending");
      } else {
        success = false;
        sErrorMsg += getErrorMessage(messages, errCode,"userform.error.unableToModify");
      }
    }
 
  }
  
   // Get user data;
  String title = messages.getString("userform.title.add");
  String botao = messages.getString("button.add");
  UserViewInterface userView = new UserView(new HashMap<String,String>());

  OrganizationalUnitViewInterface[] units = new OrganizationalUnitViewInterface[0]; 
  try {
    units = manager.getAllOrganizationalUnits(ui); 
  }
  catch (Exception e) {
    Logger.errorJsp(userInfo.getUtilizador(), "userform", "unable to get all organizational units", e);
  }

  if(StringUtils.isEmpty(userId)) {
    userId = "";
  } else {
    userView = manager.getUser(ui, userId);
    username = userView.getUsername();
    gender = userView.getGender();
    unitId = userView.getUnitId();
    emailAddress = userView.getEmail();
    firstName = userView.getFirstName();
    lastName = userView.getLastName();
    phoneNumber = userView.getPhoneNumber();
    faxNumber = userView.getFax();
    mobileNumber = userView.getMobileNumber();
    companyPhone = userView.getCompanyPhone();
    orgadm = userView.getOrgAdm().equals("1")?"true":"false";
    orgadmUsers = userView.getOrgAdmUsers().equals("1")?"true":"false";
    orgadmFlows = userView.getOrgAdmFlows().equals("1")?"true":"false";
    orgadmProcesses = userView.getOrgAdmProcesses().equals("1")?"true":"false";
    orgadmResources = userView.getOrgAdmResources().equals("1")?"true":"false";
    orgadmOrg = userView.getOrgAdmOrg().equals("1")?"true":"false";
    orgServiceUser =  userView.getOrgServiceUser().equals("1")?"true":"false";
    for (int i = 0; i < listExtraProperties.length; i++) {
      listExtraValues[i] = userView.get(listExtraProperties[i]);
    }
    title = messages.getString("userform.title.modify");    	  
    botao = messages.getString("button.modify");
  }
     
  String newAction = "modify";
  String jspBack = "useradm.jsp";
      
  if("invite".equals(sOper) || "send_invite".equals(sOper)) {
    title = messages.getString("userform.title.invite");
    botao = messages.getString("button.invite");
    newAction = "send_invite";
    bEditUsername = true;
    bEdit = true;
  } else if("add".equals(sOper) || "create".equals(sOper)) {
    newAction = "add";
    bEditUsername = true;
    bEdit = true;
  } else if("modify".equals(sOper) || "update".equals(sOper)) {
    title = messages.getString("userform.title.modify");
    botao = messages.getString("button.update");
    newAction = "update";
    bEdit = true;
    jspBack = "userform.jsp";
  }
%>



<form method="post" name="formulario" id="formulario">
  <input type="hidden" name="userid" value="<%=userId%>" />
  <input type="hidden" name="operation" value="<%=newAction%>" /> 

  <h1 id="title_admin"><%=title%></h1>

<% if (!success) { %>
<div class="error_msg"><%=sErrorMsg%></div>
<% } %>

<% if (null != infoMsg) { %>
<div class="info_msg"><%=infoMsg%></div>
<% } %>

<fieldset><legend></legend>
<ol>
<%
    if(bEdit && !Const.bUSE_EMAIL) {
      
%>
  <if:formInput name="infoemail" labelkey="userform.info.noEmailLabel" type="text" value='<%=messages.getString("userform.info.emailDisabled")%>' edit="false" required="false" />
  <li>&nbsp;</li>
<%
    }
%>

  <if:formInput name="username" labelkey="userform.field.username" type="text" value='<%=username%>' edit="<%=bEditUsername%>" required="true" maxlength="<%=String.valueOf(UserManager.USERNAME_MAX_LENGTH) %>" />

  <if:formSelect name="gender" edit="<%=bEdit%>" value='<%=gender%>' labelkey="userform.field.gender" required="true">
    <if:formOption value="M" labelkey="userform.field.gender.male"/>
    <if:formOption value="F" labelkey="userform.field.gender.female"/>
  </if:formSelect>
  <if:formSelect name="unit" edit="<%=bEdit%>" value='<%=unitId%>' labelkey="userform.field.orgunit" required="true">
  <% for (int i = 0; i < units.length; i++) { %>
    <if:formOption value='<%=units[i].getUnitId()%>' label="<%=units[i].getDescription()%>"/>
  <% } %>
  </if:formSelect>
  
  <if:formInput name="orgadm" onchange="if ($('orgadm').checked==true) $('orgadmSubPanel').style.display=''; else $('orgadmSubPanel').style.display='none';" labelkey="userform.field.orgadm" type="checkbox" value='<%=orgadm%>' edit="<%=bEdit%>" required="false" />
  
    <div id="orgadmSubPanel" style="<%= "true".equals(orgadm)?"":"display : none" %>" >
      <if:formInput name="orgadmUsers" labelkey="userform.field.orgadmusers" type="checkbox" value='<%=orgadmUsers%>' edit="<%=bEdit%>" required="false" />
      
      <if:formInput name="orgadmFlows" labelkey="userform.field.orgadmflows" type="checkbox" value='<%=orgadmFlows%>' edit="<%=bEdit%>" required="false" />
      
      <if:formInput name="orgadmProcesses" labelkey="userform.field.orgadmprocesses" type="checkbox" value='<%=orgadmProcesses%>' edit="<%=bEdit%>" required="false" />
      
      <if:formInput name="orgadmResources" labelkey="userform.field.orgadmresources" type="checkbox" value='<%=orgadmResources%>' edit="<%=bEdit%>" required="false" />
      
      <if:formInput name="orgadmOrg" labelkey="userform.field.orgadmorg" type="checkbox" value='<%=orgadmOrg%>' edit="<%=bEdit%>" required="false" />
  </div>
  
  <if:formInput name="orgServiceUser" labelkey="userform.field.orgserviceuser" type="checkbox" value='<%=orgServiceUser%>' edit="<%=bEdit%>" required="false" />
  
  <if:formInput name="emailAddress" labelkey="userform.field.emailaddress" type="text" value='<%=emailAddress%>' edit="<%=bEdit%>" required="<%= Const.bUSE_EMAIL %>" maxlength="100" />

  <if:formInput name="firstName" labelkey="userform.field.firstname" type="text" value='<%=firstName%>' edit="<%=bEdit%>" required="true" maxlength="50" />

  <if:formInput name="lastName" labelkey="userform.field.lastname" type="text" value='<%=lastName%>' edit="<%=bEdit%>" required="true" maxlength="50" />

<% if(!"invite".equals(sOper) && !"send_invite".equals(sOper)) { %>
  <if:formInput name="phoneNumber" labelkey="userform.field.phoneNumber" type="text" value='<%=phoneNumber%>' edit="<%=bEdit%>" required="false" maxlength="20" />
  <if:formInput name="faxNumber" labelkey="userform.field.faxNumber" type="text" value='<%=faxNumber%>' edit="<%=bEdit%>" required="false" maxlength="20" />
  <if:formInput name="mobileNumber" labelkey="userform.field.mobileNumber" type="text" value='<%=mobileNumber%>' edit="<%=bEdit%>" required="false" maxlength="20" />
  <if:formInput name="companyPhone" labelkey="userform.field.companyPhone" type="text" value='<%=companyPhone%>' edit="<%=bEdit%>" required="false" maxlength="20" />
<% } %>
<% for (int i = 0; i < listExtraValues.length; i++) { 
	String fieldText = "userform.field."+listExtraProperties[i];%>
  <if:formInput name="<%=\"extra_\"+listExtraProperties[i]%>" labelkey="<%=fieldText%>" type="text" value='<%=listExtraValues[i]%>' edit="<%=bEdit%>" required="false" maxlength="50" />
<% } %>
<% if(!Const.bUSE_EMAIL && bEdit) { %>
<% if("add".equals(newAction)) { %>
  <if:formInput name="__xxxxx__" label="&nbsp;" type="text" value="" edit="false" required="false" />
<% } else {%>
  <if:formInput name="__xxxxx__" labelkey="userform.field.change_password" type="text" value='<%=messages.getString("userform.field.change_password_value")%>' edit="false" required="false" />
<% } %>
  <if:formInput name="password" labelkey="userform.field.password" type="password" value="" edit="true" required='<%="add".equals(newAction)%>' maxlength="125" />
  <if:formInput name="repeatPassword" labelkey="userform.field.repeatpass" type="password" value="" edit="true" required='<%="add".equals(newAction)%>' maxlength="125" />
<% } %>
</ol>
</fieldset>
<fieldset class="submit">
  <input class="regular_button_01" type="button" name="back" value='<%=messages.getString("button.back")%>' onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/"+jspBack)%>','userid=<%=userId%>');" />
<% if(bEdit) { %>
  <input class="regular_button_01" type="button" name="clear" value='<%=messages.getString("button.clear")%>' onClick="javascript:document.formulario.reset();" />
<% } %>
  <input class="regular_button_01" type="button" name="add" value="<%=botao%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/UserManagement/userform.jsp")%>', get_params(document.formulario));" />
</fieldset>
</form>
