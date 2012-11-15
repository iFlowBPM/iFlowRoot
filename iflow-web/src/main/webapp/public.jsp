<%@ page
	language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="pt.iflow.core.*"%>
<%@ page import="pt.iflow.api.core.*"%>
<%@ page import="pt.iflow.flows.*"%>
<%@ page import="pt.iflow.api.flows.*"%>
<%@ page import="pt.iflow.api.utils.*"%>
<%@ page import="pt.iflow.utils.*"%>
<%@ page import="pt.iflow.userdata.*"%>
<%@ page import="pt.iflow.api.userdata.*"%>
<%@ page import="pt.iflow.presentation.*"%>
<%@ page import="pt.iflow.api.presentation.*"%>
<%@ page import="pt.iflow.servlets.*"%>
<%@ page import="pt.iknow.utils.*"%>
<%@ page import="pt.iknow.utils.html.*"%>
<%@ page import="pt.iflow.api.msg.IMessages"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.apache.commons.lang.*"%>
<%@ page import="pt.iflow.api.utils.*"%>
<%@ page import="pt.iflow.api.processdata.*"%>
<%
  UserInfoInterface guestInfo = null;
  Hashtable<String, Object> htSubst = new Hashtable<String, Object>();
  String sPage = "main";
  String title = "P&aacute;gina Principal";
  try {
    // TODO move sizing constants to properties (in iflow property file)
    FormData fdFormData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE, Const.fUPLOAD_TEMP_DIR);
    final String sURL_PREFIX = Const.APP_URL_PREFIX;
    long ts = System.currentTimeMillis();
    int flowId = Integer.parseInt(fdFormData.getParameter("flowid"));
    String orgId = BeanFactory.getFlowHolderBean().getFlowOrganizationid(flowId);
    OrganizationData orgData = BeanFactory.getAuthProfileBean().getOrganizationInfo(orgId);
    guestInfo = BeanFactory.getUserInfoFactory().newOrganizationGuestUserInfo(orgData);
    guestInfo.sessionLogin(Const.GUEST_NAME, Const.GUEST_SESSION);
    if (guestInfo.isLogged()) {
      IMessages messages = guestInfo.getMessages();
      String sDate = DateUtility.getToday(guestInfo);

      boolean bSysAdmin = guestInfo.isSysAdmin();
      boolean bOrgAdmin = guestInfo.isOrgAdmin();
      boolean isSupervisor = guestInfo.isProcSupervisor(-1);

      Locale loc = guestInfo.getUserSettings().getLocale();
      String lang = loc.getLanguage() + "_" + loc.getCountry();

      htSubst.put("ts", java.lang.Long.toString(ts));
      htSubst.put("url_prefix", sURL_PREFIX.substring(0, sURL_PREFIX.length() - 1));

      String sData = fdFormData.getParameter("data");
      if(null != sData)
	      htSubst.put("data", sData);
      session.setAttribute(Const.USER_INFO, guestInfo);
      session.setAttribute(Const.ORG_INFO, orgData);
      if (BeanFactory.getOrganizationThemeBean() != null) {
        session.setAttribute("themedata", BeanFactory.getOrganizationThemeBean().getOrganizationTheme(guestInfo));
      }
      session.setAttribute("SessionHelperToken", new SimpleSessionHelper());
      // Prepare process url
      String qString = request.getQueryString();
      String processURI = "inicio_flow.jsp";
      if(!StringUtils.isEmpty(qString))
        processURI = processURI+"?"+qString;
      htSubst.put("processURI", processURI);
      
      htSubst.put("date", sDate);
      htSubst.put("title", title);
      htSubst.put("url_prefix", sURL_PREFIX.substring(0, sURL_PREFIX.length() - 1));
      htSubst.put("user", "guest");
      htSubst.put("ts", java.lang.Long.toString(ts));
      htSubst.put("include_main", "yes");
      htSubst.put("sys_admin", "no");
      htSubst.put("org_admin", "no");
      htSubst.put("supervisor", "no");
      htSubst.put("guest", "yes");
      htSubst.put("cal_lang_file", "calendar-" + messages.getString("lang") + ".js");
      htSubst.put("lang", lang);
      htSubst.put("username", guestInfo.getUtilizador());
      htSubst.put("http_auth_type", Const.AUTHENTICATION_TYPE);
      htSubst.put("flowid", fdFormData.getParameter("flowid"));
      htSubst.put("user_can_admin", Boolean.FALSE);

    } else {
      throw new Exception();
    }
  } catch (Exception e) {
    ServletUtils.sendEncodeRedirect(response, "flow_error.jsp");
    return;
  }
%>
<%=PresentationManager.buildPage(response, guestInfo, htSubst, "public")%>
