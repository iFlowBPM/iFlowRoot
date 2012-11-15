<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@page import="pt.iflow.api.blocks.Block"
%><%@page import="pt.iflow.offline.OfflineManager"
%><%@page import="pt.iflow.api.documents.DocumentData"
%><%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" 
%><%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" 
%><%@ include file = "inc/defs.jsp"
%><%

//TODO
String popupReturnBlockId = null;

String title = "Inicio iFlow";
try {

  session.removeAttribute(Const.SESSION_PROCESS + flowExecType);
  session.removeAttribute("flowid");
  
  String nextURL = null;
  int flowid = 0;
  int pid = 0;
  try {
    flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
  }
  catch (Exception e) {
    ServletUtils.sendEncodeRedirect(response, "flow_error.jsp");
    return;
  }


  Integer fid = new Integer(flowid);
  session.setAttribute("flowid", fid);
  
  //Initialize flow
  Flow flow = BeanFactory.getFlowBean();
  
  
  
  if (!flow.checkUserFlowRoles(userInfo, flowid, "" + FlowRolesTO.CREATE_PRIV)) {
    ServletUtils.sendEncodeRedirect(response, "nopriv.jsp");
    return;    
  }
  
  // check if flow is deployed
  if (!flow.checkFlowEnabled(userInfo, flowid)) {
    ServletUtils.sendEncodeRedirect(response, "nodeployedflow.jsp");
    return;
  }
  
  
  //clean the session token
  session.setAttribute("token", "0");
  
  //CreateProcess
  ProcessData procData = pm.createProcess(userInfo, flowid);

  if (procData == null) {
    session.removeAttribute(Const.SESSION_PROCESS + flowExecType);
    session.removeAttribute("flowid");
    ServletUtils.sendEncodeRedirect(response, "noinitproc.jsp");
    return;
  }
  
  Logger.debugJsp(login,"inicio_flow","PID Inicio flow: " + procData.getPid());

  Enumeration<String> paramNames = fdFormData.getParameterNames();
  while (paramNames != null && paramNames.hasMoreElements()) {
    String param = paramNames.nextElement();
    if (param != null && !param.startsWith(Const.ATTR_PREFIX)) {
      continue;
    }
    String value = fdFormData.getParameter(param);
    if (value != null && param.length() > Const.ATTR_PREFIX.length()) {
      param = param.substring(Const.ATTR_PREFIX.length());
      Logger.debugJsp(login,"inicio_flow","setting initial attribute: " + param + "=" + value);
      procData.parseAndSet(param, value);
    }
  }

  OfflineManager.appendOfflineDocuments(userInfo, fdFormData, 
		application.getRealPath("/"), procData, "docs"); //XXX replace docs with document var name
  
  String jumpMode = fdFormData.getParameter("jumpmode");
  String sBid = fdFormData.getParameter("blockid");

  if (jumpMode!=null && "jumpto".equalsIgnoreCase(jumpMode)) {
    if (sBid!=null && !"".equals(sBid)) {
      try {
        int bid = Integer.parseInt(sBid);
        nextURL = flow.jumpToBlock(userInfo, procData, bid);
      }
      catch (Exception e) {
        ServletUtils.sendEncodeRedirect(response, "flow_error.jsp");
        return;
      }
    }
    if (nextURL==null)
      nextURL = flow.nextBlock(userInfo, procData);
  } else {
    nextURL = flow.nextBlock(userInfo, procData);

    if (flowid==29) sBid="4";
    
    if (sBid!=null && !"".equals(sBid)) {
      try {
        int nBid = Integer.parseInt(sBid);
        Block block = flow.getBlock(userInfo, procData);
        while (!block.isEndBlock() && nBid > 0) {
          int currBlock = block.getId();
          if (currBlock == nBid) break;
          List<String> alStates = flow.getFlowStates(userInfo, procData);
          if (alStates != null && alStates.indexOf(String.valueOf(nBid)) > -1) break; // process already passed bid... stop
          nextURL = flow.nextBlock(userInfo, procData);
          block = flow.getBlock(userInfo, procData);
          if (currBlock == block.getId()) break; // same state... stop
        }
      }
      catch (Exception e) {
        ServletUtils.sendEncodeRedirect(response, "flow_error.jsp");
        return;
      }
    }
  }
  
  if (!"".equals(flowExecType)) {
    nextURL += (nextURL.indexOf("?")>0 ? "&" : "?") + Const.FLOWEXECTYPE + "=" + flowExecType;
  }
  
  if (!procData.isInDB()) {
    session.setAttribute(Const.SESSION_PROCESS + flowExecType, procData);
  }
  
  Logger.debugJsp(login,"inicio_flow","NEXT URL: " + nextURL);
  
  Logger.traceJsp("inicio_flow",login + " STARTING PROCESS...");

  //Caso esteja a correr em popup e o bloco não seja de popup, sair no erro
  if( popupReturnBlockId != null && !flow.getBlock(userInfo, procData).canRunInPopupBlock()){
	  nextURL = null;
  }
  
  if (nextURL == null) {
    ServletUtils.sendEncodeRedirect(response, "flow_error.jsp");
  }
  else {
    ServletUtils.sendEncodeRedirect(response, nextURL);
  }

}
catch (Exception maine) {
  maine.printStackTrace();
  Logger.errorJsp(login, "inicio_flow.jsp", "Error creating a new process", maine);
  ServletUtils.sendEncodeRedirect(response, "flow_error.jsp");
  return;
}%>