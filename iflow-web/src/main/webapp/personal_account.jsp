<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.utils.Utils"%>
<%@ page import="pt.iflow.api.errors.IErrorHandler"%>
<%@ page import="pt.iflow.api.errors.ErrorCode"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.msg.IMessages" %>
<%@ page import="pt.iflow.api.errors.UserErrorCode"%>
<%@ include file = "inc/defs.jsp" %>
<%

	UserInfoInterface ui = (UserInfoInterface)session.getAttribute(Const.USER_INFO);

	String sErrorMsg = "";
    boolean bEdit = false;
    UserManager manager = BeanFactory.getUserManagerBean();
    String infoMsg = null;
    
    boolean canModify = AccessControlManager.getUserDataAccess().canModifyUser();
    boolean canPassword = AccessControlManager.getUserDataAccess().canModifyPassword();
    boolean bEmailPending = false;
    boolean bEditTimezone = false;
    boolean canTimezone = Const.USE_INDIVIDUAL_LOCALE;

    // Variable to allow the correct navigation (left menu)
    String leftMenu = "";
    if(ui.isSysAdmin()){leftMenu="4";}else{leftMenu="6";}

    // sel is defined in left navigation links
    String sSel = fdFormData.getParameter("sel");
    if ("2".equals(sSel) || "3".equals(sSel)) {
      out.println("<div class=\"info_msg\">" + messages.getString("iflow.msg.notImplemented") + "</div>");
      return; 
    }

	  String title = messages.getString("personal_account.title");

      String sOper = fdFormData.getParameter("oper");
      if (sOper == null || sOper == "") {
         sOper = "show";
      }

      List<String> lst = AccessControlManager.getUserDataAccess().getListExtraProperties();
      String[] listExtraProperties = lst.toArray(new String[lst.size()]);
      String[] listExtraValues = new String[listExtraProperties.length];

      if ("edit".equals(sOper)) {
        bEdit = canModify;
        bEditTimezone = canTimezone;
      } else if ("save".equals(sOper)) {
        if(canModify) {
          String emailAddress = fdFormData.getParameter("emailAddress");
          String firstName = fdFormData.getParameter("firstName");
          String lastName = fdFormData.getParameter("lastName");
          String phoneNumber = fdFormData.getParameter("phoneNumber");
          String mobileNumber = fdFormData.getParameter("mobileNumber");
          
          String faxNumber = "";
          String companyPhone = "";
          String gender = "";
          if(!ui.isSysAdmin()){
          faxNumber = fdFormData.getParameter("faxNumber");  
          companyPhone = fdFormData.getParameter("companyPhone");
          gender = fdFormData.getParameter("gender");
          }
          for (int i = 0; i < listExtraValues.length; i++) {
            listExtraValues[i] = fdFormData.getParameter("extra_"+listExtraProperties[i]);
          }
          String password = fdFormData.getParameter("password");
          try {
            IErrorHandler errorHandler = manager.modifyUserAsSelf(ui, password, gender, emailAddress, firstName, lastName, 
                phoneNumber, faxNumber, mobileNumber, companyPhone, listExtraProperties, listExtraValues);
            ui.reloadUserData();

            if (null == errorHandler) {
              throw new Exception(); 
            }
            if(ErrorCode.SUCCESS.equals(errorHandler.getErrorCode())) {
              bEdit = false;
            } else if(UserErrorCode.PENDING_ORG_ADM_EMAIL.equals(errorHandler.getErrorCode())) {
              bEmailPending = true;
              bEdit = false;
            } else {
              throw new Exception(); 
            }
          }
          catch (Exception e) {
            sErrorMsg = messages.getString("personal_account.error.unableToSave");            
          }
        }
        
        //Blocked access to this block is is sysAdmin because that user doesn't have language or timezone settings
        if(canTimezone && !ui.isSysAdmin()) {
          String langString = fdFormData.getParameter("locale");
          String timezoneString = fdFormData.getParameter("timezone");

          String userLang = userInfo.getUserSettings().getLangString();
          String userTZ = userInfo.getUserSettings().getTimeZoneID();
          if(userInfo.getUserSettings().isDefault() || !StringUtils.equals(userLang, langString) || !StringUtils.equals(userTZ, timezoneString)) {
        	  BeanFactory.getSettingsBean().updateUserSettings(userInfo, langString.substring(0,2), langString.substring(3), timezoneString);
	        userInfo.reloadUserSettings();
	        // force page reload!!
	        out.println("session-reload");
	        return;
          }
        }
      }
      
      request.setAttribute("bEmailPending", bEmailPending);
      if(canModify) {
        UserViewInterface userView = manager.getUser(ui, ui.getUserId());
        request.setAttribute("user", userView);
        for (int i = 0; i < listExtraProperties.length; i++) {
          listExtraValues[i] = userView.get(listExtraProperties[i]);
        }
      }
      else 
        request.setAttribute("user",ui);
      try{
      request.setAttribute("bEdit",bEdit);
      request.setAttribute("err_msg",sErrorMsg);
      request.setAttribute("title",title);
      request.setAttribute("bShowModify", canModify);
      request.setAttribute("bShowPass", canPassword);
      request.setAttribute("userLang",userInfo.getUserSettings().getLangString());
      request.setAttribute("userTimeZone",userInfo.getUserSettings().getTimeZoneID());
      request.setAttribute("bEditTimezone",bEditTimezone);
      request.setAttribute("bShowTimezone",canTimezone);
      }catch(Exception e){}

%>

<%@page import="pt.iflow.api.userdata.views.UserViewInterface"%><form method="post" name="userform">

  <h1 id="title_account"><c:out value="${title}" escapeXml="false"/></h1>

  <c:if test="${not empty err_msg}">
    <div class="error_msg">
      <c:out value="${err_msg}" escapeXml="false"/>
    </div>
  </c:if>
  <c:if test="${not empty password_changed}">
    <div class="info_msg">
      <if:message string="personal_account.info.password.changed"/>
    </div>
  </c:if>
  <c:if test="${bEmailPending}">
    <div class="info_msg">
      <if:message string="personal_account.info.confirmPending"/>
    </div>
  </c:if>
  <fieldset>
	<legend><if:message string="personal_account.header.userData" /></legend>
    <ol>
  <c:choose>
  <c:when test="${bShowModify}">
  
  	<if:formInput type="text" name="username" value="${user.username}" labelkey="userform.field.username" edit="false"/>
  	<if:formInput type="text" name="emailAddress" value="${user.email}" labelkey="userform.field.emailaddress" edit="${bEdit}" maxlength="100"/>
  	<if:formInput type="text" name="firstName" value="${user.firstName}" labelkey="userform.field.firstname" edit="${bEdit}" maxlength="50"/>
  	<if:formInput type="text" name="lastName" value="${user.lastName}" labelkey="userform.field.lastname" edit="${bEdit}" maxlength="50"/>
  	<%if (!ui.isSysAdmin()) {%>
  	<if:formSelect name="gender" edit="${bEdit}" value="${user.gender}" labelkey="userform.field.gender">
  	  <if:formOption value="M" labelkey="userform.field.gender.male"/>
  	  <if:formOption value="F" labelkey="userform.field.gender.female"/>
  	</if:formSelect>
  	<% } %>
  	<if:formInput type="text" name="phoneNumber" value="${user.phoneNumber}" labelkey="userform.field.phoneNumber" edit="${bEdit}" maxlength="20"/>
  	<%if (!ui.isSysAdmin()) {%>
  	<if:formInput type="text" name="faxNumber" value="${user.fax}" labelkey="userform.field.faxNumber" edit="${bEdit}" maxlength="20"/>
  	<% } %>
  	<if:formInput type="text" name="mobileNumber" value="${user.mobileNumber}" labelkey="userform.field.mobileNumber" edit="${bEdit}" maxlength="20"/>
  	<%if (!ui.isSysAdmin()) {%>
  	<if:formInput type="text" name="companyPhone" value="${user.companyPhone}" labelkey="userform.field.companyPhone" edit="${bEdit}" maxlength="20"/>
	<% } %>
	<% for (int i = 0; i < listExtraValues.length; i++) { %>
	  <if:formInput name="<%=\"extra_\"+listExtraProperties[i]%>" label="<%=listExtraProperties[i]%>" type="text" value='<%=listExtraValues[i]%>' edit="<%=bEdit%>" required="false" maxlength="50" />
	<% } %>
  </c:when>
  <c:otherwise>
  	<if:formInput type="text" name="username" value="${user.utilizador}" labelkey="userform.field.username" edit="false"/>
  	<if:formInput type="text" name="firstName" value="${user.userFullName}" labelkey="userform.field.fullname" edit="false" maxlength="50"/>
  	<if:formInput type="text" name="mobileNumber" value="${user.orgUnit}" labelkey="userform.field.unit" edit="false" maxlength="20"/>
  	<if:formInput type="text" name="mobileNumber" value="${user.companyName}" labelkey="userform.field.org" edit="false" maxlength="20"/>
  </c:otherwise>
  </c:choose>
  </ol>
  </fieldset>
  <% if ((!bEdit || canTimezone)&& !ui.isSysAdmin()) { %>
    <fieldset>
      <legend><if:message string="personal_account.userSettings" /></legend>
      <ol>
    <%@include file="inc/settings.jspf" %>
      </ol>
    </fieldset>
  <% } %>
  <c:if test="${bShowModify && bEdit}">
    <fieldset>
      <legend><if:message string="personal_account.header.enterPassword"/></legend>
      <ol>
    	<if:formInput type="password" name="password" value="" labelkey="userform.field.password" edit="true" maxlength="125" />
      </ol>
    </fieldset>
  </c:if>
  <fieldset class="submit">
    <c:choose>
    <c:when test="${bEdit or bEditTimezone}">
      <input class="regular_button_02" type="button" name="modify" value="<if:message string="button.cancel" />" 
          onClick="javascript:tabber_right(<%=leftMenu %>, '<%= response.encodeURL("personal_account.jsp") %>', 'oper=cancel&' + get_params(document.userform));"/>
      <input class="regular_button_02" type="button" name="modify" value="<if:message string="button.save" />" 
          onClick="javascript:tabber_right(<%=leftMenu %>, '<%= response.encodeURL("personal_account.jsp") %>', 'oper=save&' + get_params(document.userform));"/>
    </c:when>
    <c:otherwise>
      <c:if test="${bShowModify or bShowTimezone}">
      <input class="regular_button_02" type="button" name="modify" value="<if:message string="button.modify" />" 
          onClick="javascript:tabber_right(<%=leftMenu %>, '<%= response.encodeURL("personal_account.jsp") %>', 'oper=edit&' + get_params(document.userform));"/>
      </c:if>
      <c:if test="${bShowPass}">
      <input class="regular_button_03" type="button" name="modify" value="<if:message string="button.change_password" />" 
          onClick="javascript:tabber_right(<%=leftMenu %>, '<%= response.encodeURL("personal_account_password.jsp") %>', 'oper=pass&' + get_params(document.userform));"/>
      </c:if>
    </c:otherwise>
    </c:choose>
  </fieldset>
</form>
