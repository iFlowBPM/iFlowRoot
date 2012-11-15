<%@ page language="java" contentType="text/javascript; charset=UTF-8" pageEncoding="UTF-8"%><%@ 
page import="org.apache.commons.lang.StringEscapeUtils"%><%@ 
page import="org.apache.commons.lang.StringUtils"%><%@ 
page import="pt.iflow.api.utils.ServletUtils"%><%@ 
page import="pt.iflow.api.utils.Const"%><%!

// PLEASE NOTE! THIS IS TOMCAT DEPENDENT!!

protected String removeSessionid(String url) {
  if(url == null) return url;
  int s = url.indexOf(";jsessionid");
  if(s == -1) return url;
  int q = url.indexOf('?'); // query part
  if(q == -1) q = url.length();
  StringBuffer sb = new StringBuffer(url);
  return sb.replace(s,q,"").toString();
}
%><%ServletUtils utils = new ServletUtils(response);

// do some tab processing

StringBuffer histSb = new StringBuffer();
String nav = request.getParameter("nav");
if(StringUtils.isEmpty(nav)) {
  nav="";
} else {
  nav=StringEscapeUtils.escapeJavaScript(utils.encodeURL(removeSessionid(nav)));
}
histSb.append("gNavPage='").append(nav).append("';\n");
String content = request.getParameter("content");
if(StringUtils.isEmpty(content)) {
  content="";
} else {
  content=StringEscapeUtils.escapeJavaScript(utils.encodeURL(removeSessionid(content)));
}
histSb.append("gContentPage='").append(content).append("';\n");

String hist = request.getParameter("hist");
if(StringUtils.isNotEmpty(hist)) {
  int histSize = -1;
  try {
    histSize = Integer.parseInt(hist);
  } catch (Throwable t) {}
  
  if(histSize > 0) {
    for(int i = 0; i < histSize; i++) {
      String navURL = request.getParameter("nav_"+i);
      if(StringUtils.isNotEmpty(navURL)) {
        histSb.append("page_history[").append(i).append("].navpage='").append(StringEscapeUtils.escapeJavaScript(utils.encodeURL(removeSessionid(navURL)))).append("';\n");
      }
      
      String contentURL = request.getParameter("content_"+i);
      if(StringUtils.isNotEmpty(contentURL)) {
        histSb.append("page_history[").append(i).append("].contentpage='").append(StringEscapeUtils.escapeJavaScript(utils.encodeURL(removeSessionid(contentURL)))).append("';\n");
      }
    }
  }
}%>
<%-- JavaScript links --%>
mainJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "main.jsp"))%>';
gotoPersonalAccount='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "GoTo?goto=personal_account.jsp"))%>';
processLoadJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "process_load.jsp"))%>';
pingJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "ping.jsp"))%>';
auditChartServlet='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "AuditChart"))%>';
gotoOrganization='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "GoTo?goto=organization.jsp"))%>';
logoServlet='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "Logo"))%>';
userDialogServlet='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "UserDialog"))%>';
helpDialogServlet='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "HelpDialog"))%>';
msgHandlerJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "msgHandler.jsp"))%>';
<%-- Tab links --%>
mainContentJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "main_content.jsp"))%>';
actividadesFiltroJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "actividades_filtro.jsp"))%>';
actividadesJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "actividades.jsp"))%>';
userProcsFiltroJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "user_procs_filtro.jsp"))%>';
userProcsJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "user_procs.jsp"))%>';
gestaoTarefasNavJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "gestao_tarefas_nav.jsp"))%>';
gestaoTarefasJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "gestao_tarefas.jsp"))%>';
adminNavJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "Admin/admin_nav.jsp"))%>';
flowSettingsJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "Admin/flow_settings.jsp"))%>';
personalAccountJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "personal_account.jsp"))%>';
personalAccountNavJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "personal_account_nav.jsp"))%>';
inboxJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "inbox.jsp"))%>';
helpNavJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "help_nav.jsp"))%>';
helpJSP='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "help.jsp"))%>';
<%-- internal state (tab history, etc) --%>
<%=histSb%>
<%-- logout link --%>
if(document.getElementById('top_logout_link')) {
  document.getElementById('top_logout_link').href='<%= StringEscapeUtils.escapeJavaScript(utils.encodeURL(Const.APP_URL_PREFIX, "logout.jsp"))%>';
}