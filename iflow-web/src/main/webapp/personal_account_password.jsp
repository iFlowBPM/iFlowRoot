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
<%@ page import="pt.iflow.utils.UserInfo"%>
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.userdata.views.UserView" %>
<%@ page import="pt.iflow.api.msg.IMessages" %>
<%@ include file = "inc/defs.jsp" %>
<%@ page import="java.util.HashMap" %><%
	UserInfo ui = (UserInfo) session.getAttribute(Const.USER_INFO);
	String sErrorMsg = "";
    boolean bEdit = false;
  
    String leftMenu = "";
    if(ui.isSysAdmin()){leftMenu="4";}else{leftMenu="6";}
  
    // sel is defined in left navigation links
    String sSel = fdFormData.getParameter("sel");
    if ("2".equals(sSel) || "3".equals(sSel)) {
      out.println("<div class=\"info_msg\">" + messages.getString("iflow.msg.notImplemented") + "</div>");
      return; 
    }
	 // Get units;
	  String title = messages.getString("personal_account_password.title");

	  UserView userView = new UserView(new HashMap<String,String>());
	  String userId = ui.getUserId();
      String sOper = fdFormData.getParameter("oper");
      if (sOper == null || sOper == "") {
         sOper = "show";
      }
    
      if ("pass".equals(sOper)) {
        bEdit = true; 
      } else if ("savepass".equals(sOper)) {
        String oldpassword = fdFormData.getParameter("oldpassword");
        String password = fdFormData.getParameter("password");
        String repeatpass = fdFormData.getParameter("repeatpass");

        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(repeatpass) || StringUtils.isEmpty(oldpassword)) {
          sErrorMsg = messages.getString("personal_account_password.error.emptyPassword");            
        } else if (!StringUtils.equals(password, repeatpass)) {
          sErrorMsg = messages.getString("personal_account_password.error.differentPassword");            
        } else {
          try {
            UserManager manager = BeanFactory.getUserManagerBean();

            if(!ui.isSysAdmin()){
            if (manager.changePassword(ui.getUtilizador(), oldpassword, password) != UserManager.ERR_OK) {
              throw new Exception(); 
            }
            }else{
              if (manager.changePasswordAdmin(ui.getUtilizador(), oldpassword, password) != UserManager.ERR_OK) {
          		throw new Exception(); 
        	  }
            }
          
            request.setAttribute("password_changed","password_changed");
            request.getRequestDispatcher("/personal_account.jsp").forward(request, response);
            return;
          }
          catch (Exception e) {
              sErrorMsg = messages.getString("personal_account_password.error.unableToChange");            
          }
        }
      }

%>
<form method="post" name="userform">
  <input type="hidden" name="userid" value="<%=userId%>" />

  <h1 id="title_account"><%=title%></h1>

  <% if (sErrorMsg != null) { %>
  <div class="error_msg">
    <%=sErrorMsg %>
  </div>
  <% } %>

  <fieldset>
	<legend><%=messages.getString("personal_account_password.header")%></legend>
    <ol>
    	<if:formInput type="password" name="oldpassword" value="" labelkey="userform.field.oldpassword" edit="true" required="true" maxlength="125"/>
    	<if:formInput type="password" name="password" value="" labelkey="userform.field.password" edit="true" required="true" maxlength="125"/>
    	<if:formInput type="password" name="repeatpass" value="" labelkey="userform.field.repeatpass" edit="true" required="true" maxlength="125"/>
    </ol>
  </fieldset>
  <fieldset class="submit">
      <input class="regular_button_02" type="button" name="modify" value="<%=messages.getString("button.cancel")%>" 
          onClick="javascript:tabber_right(<%=leftMenu %>, '<%= response.encodeURL("personal_account.jsp") %>', 'oper=cancel&' + get_params(document.userform));"/>
      <input class="regular_button_02" type="button" name="modify" value="<%=messages.getString("button.change_password")%>" 
          onClick="javascript:tabber_right(<%=leftMenu %>, '<%= response.encodeURL("personal_account_password.jsp") %>', 'oper=savepass&' + get_params(document.userform));"/>
  </fieldset>
</form>
