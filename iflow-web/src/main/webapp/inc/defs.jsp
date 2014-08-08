<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/errorhandler.jsp"
%><%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"
%><%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"
%><%@ page import="pt.iflow.core.*"
%><%@ page import="pt.iflow.api.core.*"
%><%@ page import="pt.iflow.flows.*"
%><%@ page import="pt.iflow.api.flows.*"
%><%@ page import="pt.iflow.api.utils.*"
%><%@ page import="pt.iflow.utils.*"
%><%@ page import="pt.iflow.userdata.*"
%><%@ page import="pt.iflow.api.userdata.*"
%><%@ page import="pt.iflow.presentation.*"
%><%@ page import="pt.iflow.api.presentation.*"
%><%@ page import="pt.iflow.api.repository.*"
%><%@ page import="pt.iflow.servlets.*"
%><%@ page import="pt.iknow.utils.*"
%><%@ page import="pt.iknow.utils.html.FormUtils"
%><%@ page import="pt.iknow.utils.html.FormData"
%><%@ page import="pt.iknow.utils.html.FormFile"
%><%@ page import="pt.iflow.api.msg.IMessages"
%><%@ page import="java.net.URLEncoder"
%><%@ page import="java.util.*"
%><%@ page import="pt.iflow.api.utils.UserInfoInterface"
%><%@ page import="java.io.*"
%><%@ page import="org.apache.commons.lang.StringEscapeUtils"
%><%@ page import="org.apache.commons.lang.StringUtils"
%><%@ page import="org.apache.commons.lang.RandomStringUtils"
%><%@ page import="org.apache.commons.lang.ArrayUtils"
%><%@ page import="pt.iflow.api.utils.*"
%><%@ page import="pt.iflow.api.processdata.ProcessData"
%><%@ page import="pt.iflow.api.processdata.ProcessHeader"
%><%@ page import="pt.iflow.api.processdata.ProcessVariable"
%><%@ page import="pt.iflow.api.processdata.ProcessSimpleVariable"
%><%@ page import="pt.iflow.api.processdata.ProcessListVariable"
%><%@ page import="pt.iflow.api.processdata.ProcessListItem"
%><%@ page import="pt.iflow.api.transition.*"
%><%@ page import="com.infosistema.crypto.Base64"
%><%@ page import="pt.iflow.utils.FormDataSanitizer"
%><%@ page import="org.apache.commons122.fileupload.servlet.ServletFileUpload"
%><%
    // TODO move sizing constants to properties (in iflow property file)
FormData fdFormData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE,Const.fUPLOAD_TEMP_DIR);
if(ServletFileUpload.isMultipartContent(request))
	FormDataSanitizer.FormDataParameterSanitize(fdFormData);
%><%@ include file = "checkSession.jspf" 
%><%////////////////////////// Application Data //////////////////////////
    // Date urldate = new Date();
    // long ts = urldate.getTime();
    long ts = System.currentTimeMillis();
    
    AuthProfile ap = BeanFactory.getAuthProfileBean();
    ProcessManager pm = BeanFactory.getProcessManagerBean();
	OrganizationThemeData themeData = (OrganizationThemeData) session.getAttribute("themedata");
    
    String sURL_PREFIX = Const.APP_URL_PREFIX;
    
    
	UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    
	OrganizationData orgData = (OrganizationData) session.getAttribute(Const.ORG_INFO);
    
    IMessages messages = userInfo.getMessages();
    
    String key = fdFormData.getParameter("key");
    
    if (StringUtils.isNotEmpty(key)) {
        if (!key.equals(userInfo.getFeedKey())) {
        	userInfo = BeanFactory.getUserInfoFactory().newGuestUserInfo();
        }
    }
    
    String login = userInfo.getUtilizador();
    
    String user = userInfo.getUserFullName();
    String feed_key = userInfo.getFeedKey();
    String unit = userInfo.getOrgUnit();
    String bank = userInfo.getCompanyName();
    
    String css = themeData.getCssURL();
    String logo = themeData.getLogoURL();
    
    int userflowid = -1;
    String userflowname = null;
    String userflowtitle = null;
    String userflowlink = null;
    
    if (!userInfo.isSysAdmin()
            && session.getAttribute(Const.sUSER_FLOWID) != null) {
        try {
   			userflowid = java.lang.Integer.parseInt((String)session.getAttribute(Const.sUSER_FLOWID));
    		userflowname = (String)session.getAttribute(Const.sFLOWNAME);
    		userflowtitle = (String)session.getAttribute(Const.sFLOW_ENTRY_PAGE_TITLE);
    		userflowlink = (String)session.getAttribute(Const.sFLOW_ENTRY_PAGE_LINK);
  		}
  		catch (Exception e) {
        }
    }
    
    if (request.getRequestURI().indexOf("logout.jsp") == -1) {
        // CHECK MAINTENANCE MODE
  		if (Const.isInMaintenance() && !(Const.sMAINTENANCE_USER.equals(login) || userInfo.isSysAdmin())) {
   			 ServletUtils.sendEncodeRedirect(response, sURL_PREFIX + "maintenance.jsp");
            return;
        }
    }

    String flowExecType = fdFormData.getParameter(Const.FLOWEXECTYPE);
    if (flowExecType==null || "".equals(flowExecType)) { //verificação de segurança: se falhou o link procurar directamente no fluxo (talvez até fosse a melhor forma...
      flowExecType = "";
      try {
        int fid = Integer.parseInt(fdFormData.getParameter("flowid"));
        FlowType flowType = BeanFactory.getFlowBean().getFlowType(userInfo, fid);
        if (FlowType.SEARCH.equals(flowType)) {
          flowExecType = "SEARCH";
        } else if (FlowType.REPORTS.equals(flowType)) {
          flowExecType = "REPORT";
        }
      } catch (Exception e) {
      }
    }

    // activity batch processing contants
    String sACT_BATCH = "ACTIVITY_BATCH";
    String sACT_BATCH_PIDS = "ACTIVITY_BATCH_PIDS";
	String sACT_BATCH_LINKS = "ACTIVITY_BATCH_LINKS";%>