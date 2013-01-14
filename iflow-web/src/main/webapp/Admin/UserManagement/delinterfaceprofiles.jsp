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

		String interfaceId = fdFormData.getParameter("interfaceid");
  		String [] profiles = fdFormData.getParameterValues("active");
  
  		if(!(StringUtils.isEmpty(interfaceId) || profiles == null || profiles.length == 0)) {
    		InterfacesManager manager = BeanFactory.getInterfacesManager();
      		for(int i = 0; i < profiles.length; i++) {
      			manager.removeProfileFromInterface(ui, interfaceId, profiles[i]);
      		}
  		}
	}
	catch (Exception e) {
	}
%><jsp:forward page="interfaceprofilesform.jsp" />
