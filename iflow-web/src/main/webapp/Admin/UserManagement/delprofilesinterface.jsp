<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ include file = "../../inc/defs.jsp" %>
<%

//TiagOld

UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
	try {

		String profilesId = fdFormData.getParameter("profileid");
  		String [] interfaces = fdFormData.getParameterValues("active");
  
  		if(!(StringUtils.isEmpty(profilesId) || interfaces == null || interfaces.length == 0)) {
    		InterfacesManager manager = BeanFactory.getInterfacesManager();
      		for(int i = 0; i < interfaces.length; i++) {
      			manager.removeProfileFromInterface(ui, interfaces[i], profilesId); //As variaveis vao trocadas para reaproveitar o metodo
      		}																	   //Onde vão as interfaces deviam ir os profiles e o profileId devia ser Interfaceid
  		}
	}
	catch (Exception e) {
	}
%><jsp:forward page="profilesinterfaceform.jsp" />
