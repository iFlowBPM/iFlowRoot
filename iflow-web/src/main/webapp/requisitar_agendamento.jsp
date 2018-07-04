<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%@ page import="pt.iflow.delegations.DelegationManager" %>
<%@ page import = "java.util.Calendar" %>
<%@ page import = "pt.iflow.api.notification.Email" %>
<%@ page import="pt.iflow.userdata.views.UserView" %><%
final String dateFormat = "dd/MM/yyyy";
final String MANUAL_OPTION = "__manual__";
final String MYSELF_OPTION = "__myself__";

final String DUMMY = "dummy"; 

boolean bRequest = true;
String sAction = fdFormData.getParameter("action");
if ("reassign".equals(sAction))
	bRequest = false;
else
	sAction = "request"; 

final UserInfoFactory userFactory = BeanFactory.getUserInfoFactory();
boolean bFirstTime = true;
if (StringUtils.isNotEmpty(fdFormData.getParameter(DUMMY))) {
	bFirstTime = false;
}

boolean cbRequest = StringUtils.equals("true",fdFormData.getParameter("cb_request"));
String [] cbFlowids = fdFormData.getParameterValues("cb_flowid");
if(null == cbFlowids) cbFlowids = new String[0];
for(int i=0; i<cbFlowids.length; i++)
	cbFlowids[i] = StringEscapeUtils.unescapeHtml(cbFlowids[i]);



{
ArrayList<String> alHelper = new ArrayList<String>();
for(String cbFlowid:cbFlowids) {
  if(StringUtils.isNotBlank(cbFlowid))
    alHelper.add(cbFlowid);
}
cbFlowids = alHelper.toArray(new String[alHelper.size()]);
}


if(cbRequest && cbFlowids.length == 0) {
  %><jsp:forward page="gestao_tarefas.jsp"></jsp:forward><%
  return;
} 

  String title = bRequest ? messages.getString("requisitar_agendamento.title") : messages.getString("reassignDelegation.title");
  String sPage = "requisitar_agendamento";
  StringBuffer sbError = new StringBuffer();

  String stmp = null;
  String sflowid = fdFormData.getParameter("flowid");
  String sflowname = fdFormData.getParameter("flowname");
  String sownerid = fdFormData.getParameter("ownerid");
  if (!bRequest && !cbRequest) {
    sownerid = fdFormData.getParameter("owneridt");
  }
  
  // if there is only one flow checked, assume "regular" stuff
  if(cbRequest && cbFlowids.length == 1) {
    String [] splt = cbFlowids[0].split(";");
    sflowid = splt[0];
    sownerid = splt[1];
    cbRequest = false;
  }
  
  String delegatedUserid = fdFormData.getParameter("user");
  if(delegatedUserid == null) delegatedUserid = "";
  String manualUserid = fdFormData.getParameter("manualuser");
  if(manualUserid == null) manualUserid = "";
  
  
  if (!cbRequest && StringUtils.isEmpty(sflowname)) {
	  int flowid = Integer.parseInt(sflowid);
	  IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, flowid, false);
	  sflowname = fd.getName();
  }
  Flow flow = BeanFactory.getFlowBean();

  boolean bRead   = StringUtils.equals("true", fdFormData.getParameter("read"));
  boolean bCreate = StringUtils.equals("true", fdFormData.getParameter("create"));
  boolean bWrite  = StringUtils.equals("true", fdFormData.getParameter("write"));
  boolean bSuperv = StringUtils.equals("true", fdFormData.getParameter("superv"));

  if (StringUtils.isNotEmpty(sflowid) && StringUtils.isNotEmpty(sownerid) && StringUtils.isNotEmpty(delegatedUserid)) {
    	boolean recordInserted = false;


    	String theUser = delegatedUserid.trim();
    	if (theUser.equals(MYSELF_OPTION))
    		theUser = userInfo.getUtilizador();
    	else if (theUser.equals(MANUAL_OPTION))
    		theUser = manualUserid;
    	
    	if (StringUtils.isNotEmpty(theUser)) {
    	
    		sownerid = sownerid.trim();

    		StringBuffer sbPrivs = new StringBuffer();
    		if(bRead) sbPrivs.append("R");
    		if(bCreate) sbPrivs.append("C");
    		if(bWrite) sbPrivs.append("W");
    		if(bSuperv) sbPrivs.append("S");

    		Date expires = Utils.string2date(fdFormData.getParameter("expires"), dateFormat);
    		Date tasksFrom = null;
    		if (!bRequest)
    			tasksFrom = Utils.string2date(fdFormData.getParameter("tasksFrom"), dateFormat);

    		UserInfoInterface ownerInfo = userInfo;
    	
    		if (!bRequest) {
	    		// switch to supervised user
    			ownerInfo = userFactory.newUserInfoDelegate(userInfo, sownerid);
    		}

    		if (!bRequest && !cbRequest) {
    		  recordInserted = DelegationManager.get().superDelegation(userInfo, sflowid, sflowname, sownerid, theUser, expires, tasksFrom, sbPrivs.toString(), sbError);
    		}
    		else {
    		  recordInserted = DelegationManager.get().requestDelegation(ownerInfo, sflowid, sflowname, sownerid, theUser, expires, tasksFrom, sbPrivs.toString(), sbError);
    		}

	    	if (!recordInserted) {
      			sbError.append("<br>").append(messages.getString("requisitar_agendamento.error.5"));
    		}

		    if (sbError.length() > 0) {
%>
  <div class="<%=(recordInserted)?"info_msg":"error_msg"%>">
    <%=sbError.toString()%>
  </div>
<% if(recordInserted) {%>
  <div class="button_box">
    <input class="regular_button_01" type="button" name="continue" value="<%=messages.getString("button.go")%>" onClick="javascript:tabber_right(5, '<%= response.encodeURL("gestao_tarefas.jsp") %>', 'ts=<%= ts %>&action=<%=sAction%>');"/>
  </div>
<%
}
			}

	    	if (recordInserted)
	    		return;
    	}
	} else if(cbFlowids.length>0 && cbRequest && StringUtils.isNotEmpty(delegatedUserid)) {
  	boolean recordsInserted = true;


	String theUser = delegatedUserid.trim();
	if (theUser.equals(MYSELF_OPTION))
		theUser = userInfo.getUtilizador();
	else if (theUser.equals(MANUAL_OPTION))
		theUser = manualUserid;
	StringBuilder sbMsg = new StringBuilder();
	if (StringUtils.isNotEmpty(theUser)) {
	  FlowHolder fh = BeanFactory.getFlowHolderBean();
		for(String cbFlowid : cbFlowids) {
		  	boolean recordInserted = false;
			StringBuffer sbAux = new StringBuffer();
		  try {
		  	String [] splt = cbFlowid.split(";");
		  	sflowid = splt[0];
		  	sownerid = splt[1];
		  	int flowid = Integer.parseInt(sflowid);
		  	IFlowData fd = fh.getFlow(userInfo, flowid, false);
		  	sflowname = fd.getName();
		  	
			sownerid = sownerid.trim();
	
			UserInfoInterface ownerInfo = userInfo;
			
			if (!bRequest) {
	    		// switch to supervised user
				ownerInfo = userFactory.newUserInfoDelegate(userInfo, sownerid);
			}
		
			Map<Character, Boolean> allprivs = new HashMap<Character, Boolean>();
			allprivs.put(FlowRolesTO.CREATE_PRIV, false);
			allprivs.put(FlowRolesTO.READ_PRIV, false);
			allprivs.put(FlowRolesTO.WRITE_PRIV, false);
			allprivs.put(FlowRolesTO.ADMIN_PRIV, false);
			allprivs.put(FlowRolesTO.SUPERUSER_PRIV, false);

			// fazer o flowroles e se nao tiver permissoes fazer disable da checkbox
			FlowRolesTO[] fra = flow.getUserFlowRoles(ownerInfo, flowid);
			if (fra != null && fra.length > 0) {
				for (int j = 0; j < fra.length; j++) { // foreach profile
					String privs = fra[j].getPermissions();
					for (char allprivsKey : allprivs.keySet()) { // foreach permission
					  	allprivs.put(allprivsKey, allprivs.get(allprivsKey) || StringUtils.contains(privs, allprivsKey));
					}
				}
			}
			FlowRolesTO[] frd = flow.getUserFlowRolesDelegated(ownerInfo, flowid);
			if (frd != null && frd.length > 0) {
				for (int j = 0; j < frd.length; j++) { // foreach profile
					String privs = fra[j].getPermissions();
					for (char allprivsKey : allprivs.keySet()) { // foreach permission
					  	allprivs.put(allprivsKey, allprivs.get(allprivsKey) ||
					  	    StringUtils.contains(privs, allprivsKey));
					}
				}
			}

			
			
			StringBuffer sbPrivs = new StringBuffer();
			if(allprivs.get(FlowRolesTO.READ_PRIV)) sbPrivs.append("R");
			if(allprivs.get(FlowRolesTO.CREATE_PRIV)) sbPrivs.append("C");
			if(allprivs.get(FlowRolesTO.WRITE_PRIV)) sbPrivs.append("W");
			if(allprivs.get(FlowRolesTO.SUPERUSER_PRIV)) sbPrivs.append("S");
	
			Date expires = Utils.string2date(fdFormData.getParameter("expires"), dateFormat);
			Date tasksFrom = null;
			if (!bRequest)
				tasksFrom = Utils.string2date(fdFormData.getParameter("tasksFrom"), dateFormat);
	
			sbAux.append(messages.getString("requisitar_agendamento.request",sflowid,sflowname,ownerInfo.getUserFullName()));
			
	    	recordInserted = DelegationManager.get().requestDelegation(ownerInfo, sflowid, sflowname, sownerid, theUser, expires, tasksFrom, sbPrivs.toString(), sbAux);
	    	sbAux.append("<br>");
	
		  } catch(Exception e) {
		    Logger.errorJsp(user,"requisitar_agendamento","Excepcao ao guardar delegacao para fluxo;user "+cbFlowid, e);
		    recordInserted = false;
		  }
	    	if (recordInserted) {
	    	  if(sbMsg.length()>0) sbMsg.append("<br>");
	    	  sbMsg.append(sbAux);
	    	} else {
	    	  sbError.append(sbAux);
	  		  sbError.append("<br>").append(messages.getString("requisitar_agendamento.error.5"));
			}
	    	recordsInserted &= recordInserted;
			}
		
			// restore previous values
		  	sflowid = "-1";
		  	sownerid = "";

		    if (sbError.length() > 0) {
%>
<div class="error_msg">
<%=sbError%>
</div>
<%
			}

		    if (sbMsg.length() > 0) {
		      %>
		      <div class="info_msg">
		      <%=sbMsg%>
		      </div>
		      <%
  			}
		    
if(recordsInserted) {%>
<div class="button_box">
<input class="regular_button_01" type="button" name="continue" value="<%=messages.getString("button.go")%>" onClick="javascript:tabber_right(5, '<%= response.encodeURL("gestao_tarefas.jsp") %>', 'ts=<%= ts %>&action=<%=sAction%>');"/>
</div>
<%
return;
}
		}
	}

	try {

		if (StringUtils.isNotEmpty(sflowid)) {
			int flowid = Integer.parseInt(sflowid);
			
			TreeSet<String> users = new TreeSet<String>();
			TreeSet<String> usersToDelegate = new TreeSet<String>();
			Map<Character, Boolean> allprivs = new HashMap<Character, Boolean>();
			allprivs.put(FlowRolesTO.CREATE_PRIV, false);
			allprivs.put(FlowRolesTO.READ_PRIV, false);
			allprivs.put(FlowRolesTO.WRITE_PRIV, false);
			allprivs.put(FlowRolesTO.ADMIN_PRIV, false);
			allprivs.put(FlowRolesTO.SUPERUSER_PRIV, false);
			boolean bHidden = true;
			
			if (!bRequest) {

			  	FlowRolesTO[] fraReassign = flow.getFlowRoles(userInfo, flowid);
			
				
				HashSet<String> profiles = new HashSet<String>(); 
				
				for (FlowRolesTO fr : fraReassign) {
					if (profiles.contains(fr.getProfile())) continue;
					
					Collection<String> profUsers = ap.getUsersInProfile(userInfo, fr.getProfile().getName());

					for  (String userid : profUsers) {
						if (userInfo.getUtilizador().equals(userid)) continue;
						users.add(userid);
					}
				}
			}
			
			bHidden = !cbRequest;

			if (!cbRequest && (bRequest || StringUtils.isNotEmpty(sownerid))) {
				
				UserInfoInterface ownerInfo = userFactory.newUserInfoDelegate(userInfo, sownerid);
				
				// fazer o flowroles e se nao tiver permissoes fazer disable da checkbox
				FlowRolesTO[] fra = flow.getUserFlowRoles(ownerInfo, flowid);
				if (fra != null && fra.length > 0) {
					for (int j = 0; j < fra.length; j++) { // foreach profile
						String privs = fra[j].getPermissions();
						for (char allprivsKey : allprivs.keySet()) { // foreach permission
						  	allprivs.put(allprivsKey, allprivs.get(allprivsKey) || StringUtils.contains(privs, allprivsKey));
						}
					}
				}
				FlowRolesTO[] frd = flow.getUserFlowRolesDelegated(ownerInfo, flowid);
				if (frd != null && frd.length > 0) {
					for (int j = 0; j < frd.length; j++) { // foreach profile
						String privs = fra[j].getPermissions();
						for (char allprivsKey : allprivs.keySet()) { // foreach permission
						  	allprivs.put(allprivsKey, allprivs.get(allprivsKey) ||
						  	    StringUtils.contains(privs, allprivsKey));
						}
					}
				}
				bHidden = false;
				
				if (bRequest) {
				  Collection<UserData> allusers = ap.getAllUsers(userInfo.getOrganization());
					for (UserData ud : allusers) {
						String userid = ud.getUsername();
						if (userInfo.getUtilizador().equals(userid) || sownerid.equals(userid)) continue;
						usersToDelegate.add(userid);
					}				
				}
			}

			if (bFirstTime) {
				// default privileges: set to true if user have it
				bCreate = allprivs.get(FlowRolesTO.CREATE_PRIV);
				bRead = allprivs.get(FlowRolesTO.READ_PRIV);
				bWrite = allprivs.get(FlowRolesTO.WRITE_PRIV);
				bSuperv = allprivs.get(FlowRolesTO.SUPERUSER_PRIV);
			}
			
			Calendar cal = Calendar.getInstance();
			
			// tasks from today by default
			String sFromDate = Utils.date2string(cal.getTime(), dateFormat);
			
			// Expires (by default) after one week
			cal.add(Calendar.WEEK_OF_YEAR, 1);
			String sDate = Utils.date2string(cal.getTime(), dateFormat);


%>

<form name="flowForm" method="post">
  <input type="hidden" name="action" value=${fn:escapeXml(sAction)} />
  <input type="hidden" name="flowid" value=${fn:escapeXml(flowid)} />
  <input type="hidden" name="flowname" value=${fn:escapeXml(sflowname)} />
  <input type="hidden" name="ownerid" value=${fn:escapeXml(sownerid)} />
  <input type="hidden" name="cb_request" value=${fn:escapeXml(cbRequest)} />
  <input type="hidden" name="<%=DUMMY %>" value="<%=StringUtils.isNotEmpty(sownerid) ? DUMMY : "" %>"/>
  <% if(cbRequest) {
    for(String cbFlowid: cbFlowids) {
    %>
    <input type="hidden" name="cb_flowid" value=${fn:escapeXml(cbFlowid)} />
  <% 
    }
    } %>

	<div style="vertical-align: middle;">
		<img src="images/icon_tab_delegations.png" class="icon_item"/>
		<h1><%=title%></h1>
	</div>
  
  	<fieldset>
  		<legend></legend>  
	    <ol>
	    <% if(cbRequest) { %>
	        <if:formInput name="cb_name" type="text" labelkey="requisitar_agendamento.msg.flows" edit="false" value='' />
	    <% FlowHolder fh = BeanFactory.getFlowHolderBean();
	    for(String cbFlowid:cbFlowids) {
	      try {
	        String cbFlowname = fh.getFlow(userInfo, Integer.parseInt(cbFlowid.split(";")[0]), false).getName();
	    %>
	        <if:formInput name="cb_name" type="text" label="&nbsp;" edit="false" value='<%= cbFlowname %>' />
	    <%
	      } catch(Exception e) {
	      }
	    }

	    } else { %>
	        <if:formInput name="name" type="text" labelkey="requisitar_agendamento.msg.1" edit="false" value='<%= sflowname %>' />
			<% if (bRequest) { %>
	      		<if:formInput name="owneridt" type="text" labelkey="requisitar_agendamento.msg.2" edit="false" value='<%=sownerid%>' />
			<% } else {
				String owneridselectaction = "tabber_right(5, '" + response.encodeURL("requisitar_agendamento.jsp") + "', get_params(document.flowForm));";
			%>
		  		<if:formSelect name="owneridt" value='<%=sownerid%>' labelkey="requisitar_agendamento.msg.2" edit="true" onchange="<%=owneridselectaction%>">
			  		<% if (users.size() > 1 && StringUtils.isEmpty(sownerid)) { %> <if:formOption value="" labelkey="const.choose"/> <% } %>
			  <% for (String userid : users) { %>
			    	<if:formOption value='<%=userid%>' label="<%=userid%>" />
			  <% } %>
		  		</if:formSelect>
			<% } %>
	    <% } %>

	      <% if (!bHidden) { %>
	      
		  <% if (bRequest) { %>
            <% Collection<UserData> iflowUsers = BeanFactory.getAuthProfileBean().getAllUsers(userInfo.getOrganization());
               if (iflowUsers != null && iflowUsers.size() > 0) { %>
                <if:formSelect name="user" edit="true" labelkey="requisitar_agendamento.msg.3" value="" required="true" onchange="">
                    <% for (UserData item : iflowUsers) { %>
                        <if:formOption value="<%=item.getUsername() %>" label="<%=item.getName() %>"/>
                    <% } %>
                </if:formSelect>
            <% } else { %>
                <if:formInput name='user' labelkey="requisitar_agendamento.msg.3" type="text" value='' edit="true" required="true" maxlength="50" />
            <% } %>
		  <% } else { %>
	      <li>
	        <label for="user"><%=messages.getString("requisitar_agendamento.msg.3")%><em>*</em></label>
			<select name="user" id="user" onchange="if ($('user').value == '<%=MANUAL_OPTION%>') { $('manualuser').setStyle('display', ''); } else { $('manualuser').setStyle('display', 'none'); }">
				<option value=""><%=messages.getString("const.choose")%></option>
				<option value="<%=MYSELF_OPTION%>" <%=(MYSELF_OPTION.equals(delegatedUserid) ? "selected" : "")%>><%=messages.getString("requisitar_agendamento.msg.myself")%></option>
				<% for (String userid : usersToDelegate) { %>
				<option value="<%=userid%>" <%=(userid.equals(delegatedUserid) ? "selected" : "")%> ><%=userid%></option>
				<% } %>
				<option value="<%=MANUAL_OPTION%>" <%=(MANUAL_OPTION.equals(delegatedUserid) ? "selected" : "")%>><%=messages.getString("requisitar_agendamento.msg.manual")%></option>
	      	</select>
			<input type="text" name="manualuser" id="manualuser" value="<%=manualUserid%>" style="display:none"/> 
	      </li>			
 		  <% } %>
 		  <% if(!cbRequest) { %>
	      <li>
	      	<fieldset>  
			  <legend><if:message string="requisitar_agendamento.msg.4"/></legend>  
			  <ol>
				<if:formInput name="read"   type="checkbox" labelkey="requisitar_agendamento.msg.7"  value='<%=bRead%>'   edit="<%=allprivs.get(FlowRolesTO.READ_PRIV) %>"/>
				<if:formInput name="create" type="checkbox" labelkey="requisitar_agendamento.msg.8"  value='<%=bCreate%>' edit="<%=allprivs.get(FlowRolesTO.CREATE_PRIV) %>"/>
				<if:formInput name="write"  type="checkbox" labelkey="requisitar_agendamento.msg.9"  value='<%=bWrite%>'  edit="<%=allprivs.get(FlowRolesTO.WRITE_PRIV) %>"/>
				<if:formInput name="superv" type="checkbox" labelkey="requisitar_agendamento.msg.5"  value='<%=bSuperv%>' edit="<%=allprivs.get(FlowRolesTO.SUPERUSER_PRIV) %>"/>
			  </ol>
			</fieldset>
	      </li>
	      <% } %>
	      <if:formCalendar name="expires" value='<%=sDate%>' labelkey="requisitar_agendamento.msg.6" edit="true" required="true"/>
	      <% if (false && !bRequest) {
	        // TODO: PROCESSAR (e testar) TASKS FROM NO DELEGATION MANAGER !!!
	      %>
	      <if:formCalendar name="tasksFrom" value='<%=sFromDate%>' labelkey="requisitar_agendamento.msg.11" edit="true" required="true"/>
	      <% } %>
	    <% } %>
 		  <% if(cbRequest) { %>
 		  <li><if:message string="requisitar_agendamento.msg.auto_permission"></if:message></li>
 		  <% } %>
		</ol>
  	</fieldset>

	<fieldset class="submit">
		<input class="regular_button_02" type="button" name="cancel" value="<%=messages.getString("button.cancel")%>" onClick="tabber_right(5, '<%= response.encodeURL("gestao_tarefas.jsp") %>', 'ts=<%= ts %>&action=<%=sAction %>');"/>
		<input class="regular_button_02" type="button" name="request" value="<%=messages.getString("button.request")%>" onClick="tabber_right(5, '<%= response.encodeURL("requisitar_agendamento.jsp") %>', get_params(document.flowForm));"/>
	</fieldset>
</form>
<%
		}

	} catch (Exception e) {
		Logger.errorJsp(login, sPage, "exception: " + e.getMessage(), e);
		sbError.append(messages.getString("requisitar_agendamento.error.6"));
	}
%>
