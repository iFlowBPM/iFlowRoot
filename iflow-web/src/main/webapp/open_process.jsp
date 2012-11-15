<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%@ page import = "pt.iflow.api.blocks.Block" %>

<%@ include file = "inc/initProcInfo.jspf" %>
<%@ include file = "inc/checkProcAccess.jspf" %>

<%
	Flow flow = BeanFactory.getFlowBean();
  Block block = null;
  String sNextPage = "error.jsp";

  try {
    block = (Block) flow.getBlock(userInfo, procData);

    String sDescription = null;
    String sNextURL = null;

    sNextURL = flow.jumpToBlock(userInfo, procData, block.getId());
    // try to move process
    block = (Block) flow.getBlock(userInfo, procData);

    sDescription = block.getDescription(userInfo, procData);
    sNextURL = block.getUrl(userInfo, procData);

    if (StringUtils.isNotEmpty(sDescription) && StringUtils.isNotEmpty(sNextURL)) {
      Activity a = new Activity(login, flowid, pid, subpid, 0, 0, sDescription, Block.getDefaultUrl(userInfo, procData), 0);
      pm.updateActivity(userInfo, a);
      sNextPage = sNextURL;
    }
  } catch (Exception e) {
    Logger.errorJsp(user, "open_process.jsp", "Error opening process: "+e.getMessage(), e);
  }
  
  ServletUtils.sendEncodeRedirect(response, sURL_PREFIX+sNextPage);
%>
