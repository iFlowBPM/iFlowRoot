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
