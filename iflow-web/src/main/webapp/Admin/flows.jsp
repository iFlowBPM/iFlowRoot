<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>

<if:checkUserAdmin type="org">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%

String title = messages.getString("admin-flows.title");
String sPage = "Admin/flows";

StringBuffer sbError = new StringBuffer();
int flowid = -1;

String stmp = null;
String stmp2 = null;
String sSource = null;
String sDest = null;
String sCache = fdFormData.getParameter("cache");

String[] files = null;
String[] fileListOff = null;
String[] fileListOn = null;
String[] fileListSub = null;

String sToOn = fdFormData.getParameter("toOn");
String sToOff = fdFormData.getParameter("toOff");
String sSubFlows = fdFormData.getParameter("subflows");
String[] saToOn = fdFormData.getParameterValues("filesOff");
String[] saToOff = fdFormData.getParameterValues("filesOn");
String[] saSubFlows = fdFormData.getParameterValues("filesSub");

List<String> alDeployed = new ArrayList<String>();
boolean bReloaded = false;

Flow flow = BeanFactory.getFlowBean();

if (flow != null) {

  if (sCache != null) {

    files = BeanFactory.getFlowHolderBean().listFlowNamesOffline(userInfo);
    for (int i=0; files != null && i < files.length; i++) {
      stmp = files[i];
      stmp2 = flow.undeployFlow(userInfo, stmp);
      if (stmp2 != null && !stmp2.equals("")) {
	    sbError.append("<br>").append("Undeploy ").append(stmp).append(": ").append(stmp2).append("<br>");
      }
    }

    FlowData.reloadClasses(userInfo);

    files = BeanFactory.getFlowHolderBean().listFlowNamesOnline(userInfo);
    for (int i=0; i < files.length; i++) {
      stmp = files[i];
      stmp2 = flow.deployFlow(userInfo, stmp);
    }

    if (sbError.length() == 0) bReloaded = true;
  }

  if (sToOn != null) {
    if (saToOn != null && saToOn.length > 0) {
      for (int i=0; i < saToOn.length; i++) {
		stmp = saToOn[i];

		// now deploy flow
		stmp2 = flow.deployFlow(userInfo, stmp);
		if (stmp2 != null && !stmp2.equals("")) {
	  		sbError.append("<br>").append("Deploy ").append(stmp).append(": ").append(stmp2).append("<br>");
		}
		else {
		  Logger.debugJsp(login,sPage,"Flow " + stmp + " deployed.");
		  alDeployed.add(stmp);
		}
      }
    }
  }
  if (sToOff != null) {
    if (saToOff != null && saToOff.length > 0) {
      for (int i=0; i < saToOff.length; i++) {
        stmp = saToOff[i];

		// now deploy flow
		stmp2 = flow.undeployFlow(userInfo, stmp);
		if (stmp2 != null && !stmp2.equals("")) {
		  sbError.append("<br>").append("Undeploy ").append(stmp).append(": ").append(stmp2);
		}
      }
    }
  }

  if (sSubFlows != null) {

    sSource = "Flows/SubFlows";
    if (saSubFlows != null && saSubFlows.length > 0) {

      Set<String> hsFlows2ReDeploy = new HashSet<String>();
      IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, null, FlowType.SEARCH);

      for (int i=0; saSubFlows != null && i < saSubFlows.length; i++) {
        String subflow = saSubFlows[i];
        for (int f=0; f < fda.length; f++) {
          if (BeanFactory.getFlowHolderBean().getFlow(userInfo, fda[f].getId()).hasSubFlow(subflow)) {
            hsFlows2ReDeploy.add(fda[f].getFileName());
          }
        }
      }

      stmp2 = new String("");
      Iterator<String> it = hsFlows2ReDeploy.iterator();
      while (it.hasNext()) {
        String sflow = it.next();

        stmp = flow.undeployFlow(userInfo, sflow);
        if (stmp != null && !stmp.equals("")) {
          sbError.append("<br>").append("Undeploy ").append(sflow).append(": ").append(stmp);
        }

        stmp = flow.deployFlow(userInfo, sflow);
        if (stmp != null && !stmp.equals("")) {
	      sbError.append("<br>").append("Deploy ").append(sflow).append(": ").append(stmp).append("<br>");
	      break;
        } else {
          if (stmp2 == null || stmp2.equals("")) {
            stmp2 = new String(sflow);
          } else {
            stmp2 += ", " + sflow;
          }
        }
      }

      if (stmp2 != null && !stmp2.equals("")) {
	    Logger.debugJsp(login,sPage,"Flow " + stmp2 + " deployed.");
	    alDeployed.add(stmp2);
      }
    }
  }
}

fileListOff = BeanFactory.getFlowHolderBean().listFlowNamesOffline(userInfo);
if (fileListOff == null) fileListOff = new String[0];
fileListOn = BeanFactory.getFlowHolderBean().listFlowNamesOnline(userInfo);
if (fileListOn == null) fileListOn = new String[0];
fileListSub = BeanFactory.getFlowHolderBean().listSubFlows(userInfo);
if (fileListSub == null) fileListSub = new String[0];


StringBuffer sbHtmlOff = new StringBuffer();
StringBuffer sbHtmlOn  = new StringBuffer();
StringBuffer sbHtmlSub = new StringBuffer();

sbHtmlOff.append("<select name=\"filesOff\" size=\"10\" multiple");
if (fileListOff.length == 0) sbHtmlOff.append(" disabled ");
sbHtmlOff.append(">");
if (fileListOff.length == 0) sbHtmlOff.append("<option value=\"0\">").append(messages.getString("const.empty")).append("</option>");
for (int i=0; i < fileListOff.length; i++) {
  stmp = fileListOff[i];
  sbHtmlOff.append("<option value=\"").append(stmp).append("\">").append(stmp).append("</option>");
}
sbHtmlOff.append("</select>");

sbHtmlOn.append("<select name=\"filesOn\" size=\"10\" multiple");
if (fileListOn.length == 0) sbHtmlOn.append(" disabled ");
sbHtmlOn.append(">");
if (fileListOn.length == 0) sbHtmlOn.append("<option value=\"0\">").append(messages.getString("const.empty")).append("</option>");
for (int i=0; i < fileListOn.length; i++) {
  stmp = fileListOn[i];
  sbHtmlOn.append("<option value=\"").append(stmp).append("\">").append(stmp).append("</option>");
}
sbHtmlOn.append("</select>");

sbHtmlSub.append("<select name=\"filesSub\" size=\"5\" multiple class=\"txt\"");
if (fileListSub.length == 0) sbHtmlSub.append(" disabled ");
sbHtmlSub.append(">");
if (fileListSub.length == 0) sbHtmlSub.append("<option value=\"0\">").append(messages.getString("const.empty")).append("</option>");
for (int i=0; i < fileListSub.length; i++) {
  stmp = fileListSub[i];
  sbHtmlSub.append("<option value=\"").append(stmp).append("\">").append(stmp).append("</option>");
}
sbHtmlSub.append("</select>");

%>


<form name="flows" method="POST">
      <h1 id="title_admin"><%=title%></h1>

<% if (sbError.length() > 0) { %>
      <div class="error_msg">
     
	  </div>
<% } %>

<% if (alDeployed.size() > 0) { %>

      <div class="info_msg">
        <%= //messages.getString("admin-flows.msg.flowsDeployed")%>
<%
if (sCache == null) {
   	for (int i=0; i < alDeployed.size(); i++) {
		if (i > 0) out.print(", ");
			//out.print(alDeployed.get(i));	
  		}	
  	}
%>
	  </div>

<% } else if (bReloaded) { %>
      <div class="info_msg">
        <%=//messages.getString("admin-flows.msg.cacheReloaded")%>
	  </div>
<% } %>


	<fieldset>
	    <ol>
	      <li>
			<label for="unit"><%=messages.getString("admin-flows.field.flows")%></label>
			<div class="ft_main">
				<div class="ft_left">
					<div class="ft_caption">
						<%=messages.getString("admin-flows.field.flowOffline")%>
					</div>
					<div class="ft_select">
						<%=sbHtmlOff.toString()%>
					</div>
				</div>
				<div class="ft_middle">
					<div class="ft_button">
				    	<input class="regular_button_00" type="button" name="toOn" value="&raquo;" <%=((fileListOff.length == 0)?"disabled":"")%> onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flows.jsp") %>','toOn=true&' + get_params(document.flows));"/>
					</div>
					<div class="ft_button">
						<input class="regular_button_00" type="button" name="toOff" value="&laquo;" <%=((fileListOn.length == 0)?"disabled":"")%> onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flows.jsp") %>','toOff=true&' + get_params(document.flows));"/>
					</div>
				</div>
				<div class="ft_right">
					<div class="ft_caption">
			    		<%=messages.getString("admin-flows.field.flowOnline")%>
					</div>
					<div class="ft_select">
						<%=sbHtmlOn.toString()%>
					</div>
				</div>
			</div>
	      </li>
	      <li>
			<label for="unit"><%=messages.getString("admin-flows.field.reloadSubflows")%></label>
	      	<%=sbHtmlSub.toString()%>
	      	<input class="regular_button_01" type="button" name="subflows" value="<%=messages.getString("button.reload")%>" <%=((fileListSub.length == 0)?"disabled":"")%> onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flows.jsp") %>','subflows=Reload');"/>
	      </li>
		</ol>
	</fieldset>
    <fieldset class="submit">
		<input class="regular_button_02" type="button" name="cache" value="<%=messages.getString("admin-flows.button.reloadCache")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flows.jsp") %>','cache=Cache');"/>
		<input class="regular_button_01" type="button" name="cache" value="<%=messages.getString("admin-flows.button.refresh")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flows.jsp") %>','refresh=Refresh');"/>    			
	</fieldset>   

</form>


