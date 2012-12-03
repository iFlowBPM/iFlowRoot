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
<%@ include file = "../../inc/defs.jsp" %>
<%
	String title = messages.getString("proc_users_edit.title"); 
String sPage = "Admin/ProcManagement/proc_users_edit";

StringBuffer sbError = new StringBuffer();
String sOp = fdFormData.getParameter("op");
if (sOp == null || sOp.equals("")) {
  sOp = "0";
}

String stmp = null;

int flowid = -1;
int pid = -1;
int subpid = -1;

String sHtml = "";

String sFlowId = null;
String sPid = null;
String sSubPid = null;
String sUserId = "";
String sUID = "";
String sAction = null;

boolean bEdit = false;
boolean bDel = false;
boolean bAdd = false;
sAction = fdFormData.getParameter("a");
if (sAction != null) {
  if (sAction.equals("e")) {
    bEdit = true;
  }
  else if (sAction.equals("d")) {
    bDel = true;
    if (!sOp.equals("1")) { 
      sOp = "2";
    }
  }
  else if (sAction.equals("a")) {
    bAdd = true;
  }

  if (bEdit || bDel) {
    sUID = fdFormData.getParameter("uid");
    if (sOp.equals("0")) {
      sUserId = sUID;
    }
  }
}


sFlowId = fdFormData.getParameter("flowid");
if (sFlowId != null && !sFlowId.equals("")) {
  try {
    flowid = Integer.parseInt(sFlowId);

    try {
        sPid = fdFormData.getParameter("pid");
        pid = Integer.parseInt(sPid);
        sSubPid = fdFormData.getParameter("subpid");
        subpid = Integer.parseInt(sSubPid);
    }
    catch (Exception e2) {
      sPid = "";
      pid = -1;
      sSubPid = "";
      subpid = -1;
    }
  }
  catch (Exception e) {
    sFlowId = "-1";
    flowid = -1;
  }
}
else {
  sFlowId = "-1";
  sPid = "";
  sSubPid = "";
  flowid = -1;
  pid = -1;
  subpid = -1;
}


if (flowid < 0 || pid < 0 || subpid < 0 || 
    (!bEdit && !bDel && !bAdd) ||
    ((bEdit || bDel) && (sUID == null || sUID.equals("")))) {
  // ooops...
  ServletUtils.sendEncodeRedirect(response, "proc_users.jsp?ts="+ts);
  
  return;
}
%>
<%@ include file = "../auth.jspf" %>
<div id="proc_users_edit_div">
<%
  

//Flow flow = BeanFactory.getFlowBean();

boolean bEmpty = true;
Activity a = null;
Activity activity = null;
List<Activity> altmp = null;
boolean bExists = false;
if (sbError.length() == 0 && sOp.equals("2")) {

  if (bEdit || bAdd) {
    sUserId = fdFormData.getParameter("user");
    if (sUserId == null) {
      sUserId = "";
    }
    else if (!sUserId.equals("")) {
      sUserId = sUserId.trim();
    }
  }

  Iterator<Activity> it = pm.getProcessActivities(userInfo, flowid, pid, subpid);
    
  altmp = new ArrayList<Activity>();
  while(it.hasNext()) {
    a = it.next();
    if (bEdit) {
      if (a.userid.equals(sUID)) {
	activity = a;
      }
      else if (a.userid.equals(sUserId)) {
	bExists = true;
      }
      if (activity != null && bExists) {
	break;
      }
    }
    else if (bDel) {
      if (a.userid.equals(sUID)) {
	activity = a;
      }
      altmp.add(a);
      if (activity != null && altmp.size() > 1) {
	break;
      }
    }
    else if (bAdd) {
      if (a.userid.equals(sUserId)) {
	bExists = true;
      }
      if (activity == null) activity = a;
      if (bExists) break;
    }
  }

  // validations
  if (bDel && (altmp == null || altmp.size() <= 1)) {
    sbError.append(messages.getString("proc_users_edit.cant_remove_all")).append("<br>");
  }
  else if (activity == null) {
    // oops...
    sbError.append(messages.getString("proc_users_edit.task_not_found")).append("<br>");    
  }
  else if (bEdit && sUserId.equals(sUID)) {
    sbError.append(messages.getString("proc_users_edit.change_username")).append("<br>");    
  }
  else if ((bAdd || bEdit) && bExists) {
    sbError.append(messages.getString("proc_users_edit.user_with_task")).append("<br>");
  }
  else if (sOp.equals("2") && (bAdd || bEdit) && sUserId.equals("")) {
    sbError.append(messages.getString("proc_users_edit.user_mandatory")).append("<br>");
  }
  
  if (sbError.length() == 0) {
    
    if (bAdd) {
      if (activity.userid.equals(activity.profilename))
		activity.profilename = sUserId;
      activity.userid = sUserId;
      stmp = fdFormData.getParameter("notify");
      if (stmp != null && stmp.equals("true")) {
		activity.notify = true;
      }
      else {
		activity.notify = false;
      }      
      
      pm.createActivity(userInfo, activity);
      sHtml = messages.getString("proc_users_edit.task_created");
    }
    else {
      javax.sql.DataSource dso = Utils.getDataSource();
      java.sql.Connection db = null;
      java.sql.Statement st = null;
      int ntmp = 0;
      
      try {
	db = dso.getConnection();
	db.setAutoCommit(false);
	st = db.createStatement();
	
	stmp = null;
	if (bEdit) {
	  String sProfileName = "";
	  if (activity.userid.equals(activity.profilename))
	    sProfileName = ",profilename='" + sUserId + "'";
	  stmp = "update activity set userid='" + sUserId + "'" + sProfileName + " where userid='" 
	    + activity.userid + "' and flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid;
	}
	else if (bDel) {
	  stmp = "delete from activity where userid='" 
	    + activity.userid + "' and flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid;
	}
	
	if (stmp != null) {
	  ntmp = st.executeUpdate(stmp);
	  if (ntmp == 1) {
	    if (bDel) {
	      sHtml = messages.getString("proc_users_edit.task_removed");
	    }
	    else {
	      sHtml = messages.getString("proc_users_edit.task_changed");
	    }
	    db.commit();
	  }
	  else {
	    db.rollback();
	    if(bEdit) sbError.append("proc_users_edit.task_changed_error");
	    else sbError.append("proc_users_edit.task_removed_error");
	    sbError.append("<br>");
	    
	  }
	}
      }
      catch (Exception e) {
	if (db != null) { try { db.rollback(); } catch (Exception ee) {} }
      }
      finally {
	Utils.closeDB(db,st,null);
      }
    }
  }
}

  Collection<UserData> users = BeanFactory.getAuthProfileBean().getAllUsers(userInfo.getOrganization());
%>

<script type="text/javascript"></script>
<form name="procusersedit" method="POST">
    <script>
      function isUserVarFilled(user){
          if (user != null && user != ''){
              return true;
          } else {
              alert ('É necessario selecionar um utilizador válido.');
              return false;
          }
    }
    </script>

  <input type="hidden" name="op" value="">
  <input type="hidden" name="flowid" value="<%=flowid%>">
  <input type="hidden" name="pid" value="<%=pid%>">
  <input type="hidden" name="subpid" value="<%=subpid%>">
  <input type="hidden" name="a" value="<%=sAction%>">
  <input type="hidden" name="uid" value="<%=sUID%>">

  <h1 id="title_admin"><%=title%></h1>
      
<% if (sbError.length() > 0) { %>
  <div class="error_msg">
    <%=sbError%>
  </div>
<% } %>

<% if (!sHtml.equals("")) { %>
  <div class="info_msg">
    <%=sHtml%>
  </div>
<% } else {%>

  <div>
    <fieldset>
      <ol>
<% if (!bDel) { %>
    <% if (users != null && users.size() > 0) { %>
        <if:formSelect name="user" edit="true" labelkey="proc_users_edit.user" value="<%=sUserId %>" required="true" onchange="">
            <if:formOption value="" labelkey="flow_schedule.add.field.combobox.default.text"/>
            <% for (UserData item : users) { %>
                <if:formOption value="<%=item.getUsername() %>" label="<%=item.getName() %>"/>
            <% } %>
        </if:formSelect>
    <% } else { %>
        <if:formInput name='user' labelkey="proc_users_edit.user" type="text" value='${sUserId}' edit="true" required="true" maxlength="50" />
    <% } %>
<% if (bAdd) { %>
        <if:formInput edit="true" name="notify" value="" type="checkbox" labelkey="proc_users_edit.notify_user"/>
<%
  }
}
%>
      </ol>
    </fieldset>
  </div>
<% } %>

  <div class="button_box">
<%
if (!sHtml.equals("") || bDel) {
%>
 	  <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" 
      onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_users.jsp")%>', get_params(document.procusersedit));"/>
<%
}
else {
%>
 	  <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" 
      onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_users.jsp")%>', get_params(document.procusersedit));"/>
 	  <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.next")%>" 
      onClick="javascript:if (isUserVarFilled(document.procusersedit.user.value)){document.procusersedit.op.value='2';tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_users_edit.jsp")%>', get_params(document.procusersedit));}"/>
<%
}
%>
  </div>
</form>
</div>

<if:generateHelpBox context="proc_users"/>
