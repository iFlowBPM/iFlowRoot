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
<%@ include file = "../inc/defs.jsp" %>
<%@ page import = "pt.iflow.api.blocks.Block" %>
<%@ page import = "pt.iflow.api.core.Activity"%>
<%@page import="pt.iflow.api.errors.IErrorManager"%>
<%@page import="pt.iflow.api.blocks.MessageBlock"%>
<%@ include file = "../inc/initProcInfo.jspf" %>
<%@ include file = "../inc/checkProcAccess.jspf" %>
<%
//TODO
String popupReturnBlockId = null;
  String sPage = "forward.jsp";
  Flow flow = BeanFactory.getFlowBean();
  Block block = null;
  String sNextPage = sURL_PREFIX + "error.jsp";

  try {
    block = flow.getBlock(userInfo, procData);
    
  	
    //Caso esteja a correr em popup e o bloco não seja de popup, sair no erro
    if( popupReturnBlockId != null && !block.canRunInPopupBlock())
    	block = null;
	
	if (block == null) {
      sNextPage += "?msg_key=flow_error.no_block";
      
      final IErrorManager errorManager = BeanFactory.getErrorManagerBean();
      String errorKey = errorManager.init(userInfo, procData, this, "after");
      errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, 
          procData.getSignature() + "Null block (flow has changed?)");
	  errorManager.fire(errorKey);
	  errorManager.close(errorKey);
        
      throw new Exception("null block");
    }
    
    if (block.isEndBlock()) {
      // try again...
      flow.checkFlowEnd(userInfo, procData, block);
      sNextPage = block.getUrl(userInfo, procData);
    }
    else if (!block.hasInteraction()) {
      // try to move process
      String nextBlockUrl = flow.nextBlock(userInfo, procData);
      sNextPage = nextBlockUrl;
      block = flow.getBlock(userInfo, procData);
    } else {
      sNextPage = block.getUrl(userInfo, procData);
    }

    if (StringUtilities.isEmpty(sNextPage)) {
      throw new Exception("empty url for block " + block.getId());
    }

    String sDescription = block.getDescription(userInfo, procData);
    String url = Block.getDefaultUrl(userInfo, procData);
    Activity a = new Activity(login, flowid, pid, subpid, 0, 0, sDescription, url, 0);
	a.setRead();
    pm.updateActivity(userInfo, a);

    sNextPage = sURL_PREFIX + sNextPage;

  } catch (Exception e) {
    Logger.errorJsp(user, sPage, 
        procData.getSignature() + "Exception occured while resuming process: " + e.getMessage(), e);
  }

  if (Logger.isDebugEnabled()) {
    Logger.debugJsp(user, sPage, "Forwarding to: " + sNextPage);
  }

  ServletUtils.sendEncodeRedirect(response, sNextPage);
%>
