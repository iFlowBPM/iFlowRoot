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
String title = messages.getString("proc_undo_select.title");
StringBuffer sbError = new StringBuffer();

String[] saCols = { 
  messages.getString("proc_undo_select.col.flow"),
  messages.getString("proc_undo_select.col.process"),
  messages.getString("proc_undo_select.col.subprocess"),
  messages.getString("proc_undo_select.col.subject"),
  messages.getString("proc_undo_select.col.arrival"),
  messages.getString("proc_undo_select.col.currentUser"),
};

List<List<String>> alData = new ArrayList<List<String>>();

String stmp = null;
String stmp2 = null;
String stmp3 = null;

int ITEMS_PAGE = 20;
int nStartIndex = 0;
int nNextStartIndex = 0;

String sBeforeUser = fdFormData.getParameter("beforeuser");
String sActualUser = fdFormData.getParameter("actualuser");
String sPid = fdFormData.getParameter("pid");
String sShowFlowId = fdFormData.getParameter("showflowid");
try {
  sShowFlowId = "" + Integer.parseInt(sShowFlowId);
  session.setAttribute(Const.SESSION_ATTRIBUTE_FLOWID, Integer.parseInt(sShowFlowId));
} catch(Exception e) {
  try {
    sShowFlowId = "" + Integer.parseInt(session.getAttribute(Const.SESSION_ATTRIBUTE_FLOWID).toString());
  } catch(Exception ex) {
    session.setAttribute(Const.SESSION_ATTRIBUTE_FLOWID, -1);
  }
}
String sShowFlowHtml = "";
String sBeforeHtml = "";
String sAfterHtml = "";
String sNavPrevHtml = "";
String sNavNextHtml = "";
int nShowFlowId = -1;
int nItems = ITEMS_PAGE;
int nMode = 0;
int nPid = 0;
boolean bSearch = false;

if (sBeforeUser == null) sBeforeUser = "";
if (sActualUser == null) sActualUser = "";


stmp = fdFormData.getParameter("dummy");
if (stmp != null && stmp.equals("1")) {
  bSearch = true;
}

if (userflowid > 0) {
	sShowFlowId = String.valueOf(userflowid);
	nShowFlowId = userflowid;
} else {
if (sShowFlowId == null || sShowFlowId.equals("")) sShowFlowId = "-1";
try {
	nShowFlowId = Integer.parseInt(sShowFlowId);
}
catch (Exception e) {
}
}

if (sPid == null || sPid.equals("")) sPid = "";
try {
	nPid = Integer.parseInt(sPid);
}
catch (Exception e) {
}
try {
	stmp = fdFormData.getParameter("itemspage");
	nItems = Integer.parseInt(stmp);
}
catch (Exception e) {
}
try {
	stmp = fdFormData.getParameter("mode");
	nMode = Integer.parseInt(stmp);
}
catch (Exception e) {
	nMode = 0;
}
try {
	stmp = fdFormData.getParameter("startindex");
	nStartIndex = Integer.parseInt(stmp);
}
catch (Exception e) {
}
try {
	stmp = fdFormData.getParameter("nextstartindex");
	nNextStartIndex = Integer.parseInt(stmp);
}
catch (Exception e) {
}

if (nMode == 0) {
	nStartIndex = 0;
	nNextStartIndex = 0;
}
else if (nMode > 0) {
	nStartIndex = nNextStartIndex;
}
else if (nMode < 0) {
	nNextStartIndex = nStartIndex;
	nStartIndex--;
}

HashMap<Integer,String> hmFlowInfo = new HashMap<Integer,String>();
List<Activity> alActivities = new ArrayList<Activity>();

boolean bAdminUser = userInfo.isOrgAdmin();
Flow flow = BeanFactory.getFlowBean();
IFlowData[] fda = null;
try {
	if (userInfo.isOrgAdmin()) {
		// iflow administrator has full access
		bAdminUser = true;
	}
	else {

		FlowRolesTO[] fra = flow.getAllUserFlowRoles(userInfo);
		for (int i = 0; i < fra.length; i++) {

	if (fra[i].hasPrivilege(FlowRolesTO.READ_PRIV)) {
		hmFlowInfo.put(fda[i].getId(),fda[i].getName());
	}
		}
	}

	fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, null, new FlowType[] { FlowType.SEARCH, FlowType.REPORTS});

	for (int i=0; fda != null && i < fda.length; i++) {
		hmFlowInfo.put(fda[i].getId(),fda[i].getName());
	}
}
catch (Exception e) {
	Logger.errorJsp(login,"flows"," checking admin profile exception: " + e.getMessage());
	e.printStackTrace();
}

if (userflowid == -1) {
	List<String> alValues = new ArrayList<String>();
	List<String> alNames = new ArrayList<String>();
	alValues.add("-1");
	alNames.add(messages.getString("const.select"));
	for (int i=0; fda != null && i < fda.length; i++) {
		if (!bAdminUser) {
	continue;
		}
		alValues.add(String.valueOf(fda[i].getId()));
		alNames.add(fda[i].getName());
	}
	sShowFlowHtml = Utils.genHtmlSelect("showflowid","",sShowFlowId,alValues,alNames);
}


java.util.Date dtBefore = Utils.getFormDate(request, "dtbefore");
java.util.Date dtAfter = Utils.getFormDate(request, "dtafter");

sBeforeHtml = Utils.genFormDate(response, userInfo, "dtbefore", dtBefore, "f_date_a");
sAfterHtml = Utils.genFormDate(response, userInfo, "dtafter", dtAfter, "f_date_c");

Iterator<Activity> it = null;

if (nShowFlowId != -1 && (nPid!=0 ||
        (sBeforeUser!=null && !sBeforeUser.equals("")) ||
        (sActualUser!=null && !sActualUser.equals("")))) {
	it = pm.getActivities(userInfo, nShowFlowId, nPid, -1, dtBefore, dtAfter, sBeforeUser, sActualUser);
} else if (bSearch) {
    bSearch = false;
    sbError.append("<br>").append(messages.getString("proc_undo_select.error.chooseFlow"));
}

Activity a;

while(it != null && it.hasNext()) {
	a = it.next();
	if (nShowFlowId != -1 && nShowFlowId != a.flowid)
		continue;
	if (!bAdminUser)
		continue;
	alActivities.add(a);
}

boolean bFirstPage = true;
boolean bHasMoreItems = false;
if (nMode >= 0) {
	if (nStartIndex > 0) {
		bFirstPage = false;
	}
	for (int i=0; nNextStartIndex < alActivities.size(); i++,nNextStartIndex++) {
		a = alActivities.get(nNextStartIndex);

		stmp = "<a href=\"javascript:tabber_right(4, '"+response.encodeURL("Admin/ProcManagement/proc_undo_hist.jsp")+"', 'flowid="
                + a.flowid + "&pid=" + a.pid + "&subpid=" + a.subpid
                + "&ts=" + ts + "');\">";
		stmp2 = "</a>";

		stmp3 = String.valueOf(a.flowid);

		if (!hmFlowInfo.containsKey(a.flowid)) {
	// flow does exist or is disabled
	i--;
	continue;
		}
		stmp3 = hmFlowInfo.get(a.flowid);

		if (i >= nItems) {
	bHasMoreItems = true;
	break;
		}

		List<String> altmp = new ArrayList<String>();

		if (a.delegated) {
	altmp.add("<a title=\"" + messages.getString("proc_undo_select.msg.taskDelegated") + "\"><img src=\"../../images/icon_delegations.png\" height=\"10\"/></a>" + stmp + stmp3 + stmp2);
		} else {
	altmp.add(stmp + stmp3 + stmp2);
		}
		altmp.add(stmp + a.pid + stmp2);
		altmp.add(stmp + a.subpid + stmp2);
		altmp.add(stmp + a.description + stmp2);
		altmp.add(stmp + DateUtility.formatTimestamp(userInfo, a.created) + stmp2);
        altmp.add(stmp + a.userid + stmp2);

		alData.add(altmp);
	}
}
else {
	bHasMoreItems = true; // if going back, then more items exist.
	for (int i=0; nStartIndex >= 0; nStartIndex--, i++) {
		a = alActivities.get(nStartIndex);

		stmp = "<a href=\"javascript:tabber_right(4, '"+response.encodeURL("Admin/ProcManagement/proc_undo_hist.jsp")+"', 'flowid="
                + a.flowid + "&pid=" + a.pid + "&ts=" + ts + "');\">";
		stmp2 = "</a>";

		stmp3 = String.valueOf(a.flowid);

		if (!hmFlowInfo.containsKey(a.flowid)) {
	// flow does exist or is disabled
	i--;
	continue;
		}

		stmp3 = hmFlowInfo.get(a.flowid);

		List<String> altmp = new ArrayList<String>();
		if (a.delegated) {
	altmp.add("<a title=\"" + messages.getString("proc_undo_select.msg.taskDelegated") + "\"><img src=\"../../images/icon_delegations.png\" height=\"10\"/></a>" + stmp + stmp3 + stmp2);
		} else {
	altmp.add(stmp + stmp3 + stmp2);
		}
		altmp.add(stmp + a.pid + stmp2);
		altmp.add(stmp + a.subpid + stmp2);
		altmp.add(stmp + a.description + stmp2);
		altmp.add(stmp + DateUtility.formatTimestamp(userInfo, a.created) + stmp2);

		alData.add(0,altmp);

		if (i >= (nItems-1)) {
		  break;
		}
	}

	if (nStartIndex < 0) {
	  nStartIndex = 0;
	}

	if (nStartIndex > 0) {
	  bFirstPage = false;
	}
}
%>


<% Collection<UserData> users = BeanFactory.getAuthProfileBean().getAllUsers(userInfo.getOrganization());%>

<%@page import="pt.iflow.api.transition.FlowRolesTO"%><form name="undoactivities" method="post">
  <input type="hidden" name="dummy" value="1">
  <input type="hidden" name="mode" value="0">
  <input type="hidden" name="startindex" value="<%=nStartIndex%>">
  <input type="hidden" name="nextstartindex" value="<%=nStartIndex%>">
  
  <h1 id="title_admin"><%=title%></h1>

<% if (sbError.length() > 0) { %>
  <div class="error_msg">
    <%=sbError.toString()%>
  </div>
<% } %>  

  <fieldset>
    <legend align="left"><%=messages.getString("proc_undo_select.header.criteria") %></legend>
    <ol>
      <% if (!sShowFlowHtml.equals("")) { %>
        <li>
          <label for="showflowid"><%=messages.getString("proc_undo_select.crit.flow") %>:</label>
          <%=sShowFlowHtml%>
        </li>
      <% } %>
      <li>
        <label for="dtafter"><%=messages.getString("proc_undo_select.crit.after") %>:</label>
        <%=sAfterHtml%>
      </li>
      <li>
        <label for="dtbefore"><%=messages.getString("proc_undo_select.crit.before") %>:</label>
        <%=sBeforeHtml%>
      </li>
      <li>
        <label for="itemspage"><%=messages.getString("proc_undo_select.crit.tasks") %>:</label>
        <input type="text" name="itemspage" value="<%=nItems%>" class="txt" size="5" maxlength="2" />
      </li>
      <li>
        <label for="pid"><%=messages.getString("proc_undo_select.crit.process") %>:</label>
        <input type="text" name="pid" value="<%=sPid%>" class="txt" size="8" />
      </li>
      <li>
        <% if (users != null && users.size() > 0) { %>
            <if:formSelect name="beforeuser" edit="true" labelkey="proc_undo_select.crit.previousUser" value="<%=sBeforeUser %>" required="true" onchange="">
                <if:formOption value="" labelkey="flow_schedule.add.field.combobox.default.text"/>
                <% for (UserData item : users) { %>
                    <if:formOption value="<%=item.getUsername() %>" label="<%=item.getName() %>"/>
                <% } %>
            </if:formSelect>
        <% } else { %>
            <if:formInput name='beforeuser' labelkey="proc_undo_select.crit.previousUser" type="text" value='${sBeforeUser}' edit="true" required="true" maxlength="50" />
        <% } %>
      </li>
      <li>
        <% if (users != null && users.size() > 0) { %>
            <if:formSelect name="actualuser" edit="true" labelkey="proc_undo_select.crit.currentUser" value="<%=sActualUser %>" required="true" onchange="">
                <if:formOption value="" labelkey="flow_schedule.add.field.combobox.default.text"/>
                <% for (UserData item : users) { %>
                    <if:formOption value="<%=item.getUsername() %>" label="<%=item.getName() %>"/>
                <% } %>
            </if:formSelect>
        <% } else { %>
            <if:formInput name='actualuser' labelkey="proc_undo_select.crit.currentUser" type="text" value='${sActualUser}' edit="true" required="true" maxlength="50" />
        <% } %>
      </li>
      <li>
        <label for=""></label>
      </li>
    </ol>
  </fieldset>
  <fieldset class="submit">
    <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.search")%>" 
      onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_undo_select.jsp")%>', get_params(document.undoactivities));"/>
  </fieldset>

<% if (bSearch) { %>
  <div class="table_inc">

  <% if (alData.size() > 0) { %>
   	<table class="list_item"> 
      <tr class="tab_header">  
        <% for (int i=0; i < saCols.length; i++) { %>
          <td><%=saCols[i]%></td>
        <% } %>
      </tr>

      <%
    	for (int i=0; i < alData.size(); i++) {
    		List<String> altmp = alData.get(i);
    		out.println("        <tr class=\"" + (i%2==0?"tab_row_even":"tab_row_odd") + "\">");
    		for (int j=0; j < altmp.size(); j++) {
    			out.print("          <td>");
    			out.print((String)altmp.get(j));
    			out.println("</td>");
    		}
    		out.println("        </tr>");
    	}
      %>
    </table>  
  <% } else { %>
    <div class="info_msg">
      N&atilde;o existem processos associados
    </div>
  <% } %>
  </div>
  <div class="button_box">
    <% if (!bFirstPage) { %>
      <input class="regular_button_00" type="button" name="previous" value="<%=messages.getString("button.previous")%>" 
        onClick="javascript:document.undoactivities.mode.value='-1';document.undoactivities.nextstartindex.value='<%=nNextStartIndex%>';tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_undo_select.jsp")%>', get_params(document.undoactivities));"/>
    <% } %>
    <% if (bHasMoreItems) { %>
      <input class="regular_button_00" type="button" name="next" value="<%=messages.getString("button.next")%>" 
        onClick="javascript:document.undoactivities.mode.value='1';document.undoactivities.nextstartindex.value='<%=nNextStartIndex%>';tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/proc_undo_select.jsp")%>', get_params(document.undoactivities));"/>
    <% } %>
  </div>
<% } // if (bSearch)  %>

</form>

<if:generateHelpBox context="proc_undo_select"/>
