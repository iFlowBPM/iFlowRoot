<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@include file="/inc/defs.jsp"%>
<%!
public void makeHelpData(javax.servlet.jsp.JspWriter outstream, UserInfoInterface userInfo, String spanid, String selectedSpanId, String file, boolean indirect) {
	try {
		outstream.print("<span id=\"");
		outstream.print(spanid);
		if (selectedSpanId.equals(spanid)) {
	        outstream.println("\" class=\"topic_show\">");
		}
		else {
	        outstream.println("\" class=\"topic_hidden\">");			
		}
		InputStream contentStream = null;
		try {
			Repository rep = BeanFactory.getRepBean();
			RepositoryFile contentFile = rep.getHelp(userInfo, file);
			if (contentFile != null) {
				contentStream = contentFile.getResourceAsStream();
				Hashtable<String, Object> htSubst = new Hashtable<String, Object>();
				htSubst.put("version", Version.VERSION);
				htSubst.put("build", Version.BUILD);
				if(userInfo.getUserSettings() ==null || userInfo.getUserSettings().getLocale()==null)
					htSubst.put("messages", pt.iflow.msg.Messages.getInstance());
				else
					htSubst.put("messages", pt.iflow.msg.Messages.getInstance(userInfo.getUserSettings().getLocale()));
					
		        StringBuffer sHtml = new StringBuffer();
		
		        sHtml.append("<div id=\"helpwrapper\" class=\"help_box_wrapper\">");
		        		        
		        //if (indirect)
		        //	sHtml.append("<input id=\"link_search_span\" class=\"regular_button_00\" type=\"button\" name=\"filter\" value=\"").append(userInfo.getMessages().getString("button.back")).append("\" onClick=\"javascript:helpBack('").append(spanid).append("')\">");
		  			
		        sHtml.append("<div id=\"helpsection\" class=\"help_box\">");        
		        sHtml.append(VelocityUtils.processTemplate(htSubst, new InputStreamReader(contentStream, "UTF-8")));
		        sHtml.append("</div>");        
		        sHtml.append("</div>");        

				outstream.write(sHtml.toString());
			}
		}
		catch (Exception e) {
			// XXX: show error!
		}
		finally { 
			try { if (contentStream != null) contentStream.close(); } catch (Exception e) {}
		}
		outstream.println("</span>");
	}
	catch (IOException ioe) {
		// XXX ??
	}
}

%>

<%
	boolean indirect = true;
  String sPage = "help";
  String selectedSpanId = request.getParameter("helpPage");
  if (StringUtils.isBlank(selectedSpanId)) {
    selectedSpanId = "help_about_help";
    indirect = false;
  }
	
%>

<h1 id="title_help"><%=messages.getString("helpbox.title")%></h1>

<div>
  <% makeHelpData(out, userInfo, "help_about_help", selectedSpanId, "main.vm", indirect); %>
  <% makeHelpData(out, userInfo, "help_about_iflow", selectedSpanId, "about.vm", indirect); %>
	<% makeHelpData(out, userInfo, "help_concepts_flow", selectedSpanId, "concepts_flow.vm", indirect); %>
	<% makeHelpData(out, userInfo, "help_dashboard", selectedSpanId, "dashboard.vm", indirect); %>
	<% makeHelpData(out, userInfo, "help_tasks", selectedSpanId, "tasks.vm", indirect); %>
    <% makeHelpData(out, userInfo, "help_my_processes", selectedSpanId, "myprocesses.vm", indirect); %>
	<% makeHelpData(out, userInfo, "help_processes", selectedSpanId, "processes.vm", indirect); %>
	<% makeHelpData(out, userInfo, "help_delegations", selectedSpanId, "delegations.vm", indirect); %>
	<% makeHelpData(out, userInfo, "help_reports", selectedSpanId, "reports.vm", indirect); %>
	<% makeHelpData(out, userInfo, "help_notifications", selectedSpanId, "notifications.vm", indirect); %>
	<% if (userInfo.isOrgAdmin()) makeHelpData(out, userInfo, "help_admin", selectedSpanId, "admin.vm", indirect); %>
</div>



