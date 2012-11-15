<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ include file = "../../inc/defs.jsp" %>
<%
UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
      try {

		String userId = fdFormData.getParameter("userid");
        String [] profiles = fdFormData.getParameterValues("inactive");
        
        if(!(StringUtils.isEmpty(userId) || profiles == null || profiles.length == 0)) {
	
          UserManager manager = BeanFactory.getUserManagerBean();
	        for(int i = 0; i < profiles.length; i++) {
	        	manager.addUserProfile(ui, userId, profiles[i]);
	        }
	        
	        if(userId.equals(ui.getUserId())) {
        	  ui.updateProfiles();
        	  ui.updatePrivileges();
	        }
        }
      }
      catch (Exception e) {
      }
%><jsp:forward page="userprofileform.jsp" />
