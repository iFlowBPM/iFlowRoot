<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<%@ page import = "pt.iflow.api.blocks.Block" %>
<%@ include file = "../inc/initProcInfo.jspf" %>
<%@ include file = "../inc/checkProcAccess.jspf" %>
<%
String fileName = null;
String sError = null;
String fileType = null;
boolean bGoImport = false;
byte[] buffer = null;
int opAction = 1;
String requestMid = null;
String contentType = null;
String sPage = "Data/doimport.jsp";
String sNextPage = "import.jsp?flowid="+flowid+"&pid="+pid+"&subpid="+subpid+"&ts="+ts;


Block bBlock = null;
Flow flow = BeanFactory.getFlowBean();
try {

  bBlock = flow.getBlock(userInfo,procData);

  if (bBlock.getClass().getName().indexOf("BlockDataImport") == -1) {
    throw new Exception("Not BlockDataImport!");
  }
}
catch (Exception e) {
  // send to main page...
  // not able to get flow or process is not in jsp state (if
  // a casting exception occurs..)
  ServletUtils.sendEncodeRedirect(response, sURL_PREFIX+"flow_error.jsp");
  return;
}



try {
  requestMid = fdFormData.getParameter(Const.sMID_ATTRIBUTE);
  String fileFieldName = (String) bBlock.execute(4, new Object[0]);
  fileType = fdFormData.getParameter("type");
  FormFile formFile = fdFormData.getFileParameter(fileFieldName+"_add_[0]");  // using form stuff
  if(formFile == null) {
    // TODO remove previously uploaded documents...
    
    
    // Save pending documents into process data
    pt.iflow.api.documents.DocumentSessionHelper helper = (pt.iflow.api.documents.DocumentSessionHelper) session.getAttribute(pt.iflow.api.documents.DocumentSessionHelper.SESSION_ATTRIBUTE);
    if(null != helper)
      helper.updateProcessData(userInfo, procData);
    

    // novo modelo de carregamento (applet). O ficheiro já está na BD.
    ProcessListVariable docVar = procData.getList(fileFieldName);
    if(null != docVar) {
      int size = docVar.size();
      if(size > 0) {
        Integer docId = (Integer) docVar.getItem(size-1).getValue();
        pt.iflow.connector.document.Document doc = BeanFactory.getDocumentsBean().getDocument(userInfo, procData, docId);
        fileName = doc.getFileName();
        contentType = application.getMimeType(fileName);
        buffer = doc.getContent();
      }
      
    }
    
  } else {
    // carregamento legacy (input field)
	fileName = formFile.getFileName();
	contentType = formFile.getContentType();
	buffer = formFile.getData();
  }
  String sAction = fdFormData.getParameter("backPressed");
  opAction = (StringUtils.isNotEmpty(sAction) && sAction.equals("1"))?3:1;
}
catch (Exception e) {
  Logger.errorJsp(login,sPage,"exception processing form: " + e.getMessage());
}



if (!flow.checkUserFlowRoles(userInfo, flowid, "" + FlowRolesTO.WRITE_PRIV) &&
    !flow.checkUserFlowRoles(userInfo, flowid, "" + FlowRolesTO.SUPERUSER_PRIV)) {
  ServletUtils.sendEncodeRedirect(response, sURL_PREFIX+"nopriv.jsp");
  return;
}

if(opAction==3) {
  // Cancel here....
  Object[] oa = new Object[2];
  oa[0] = userInfo;
  oa[1] = procData;
  // 3: cancel
  sError = (String)bBlock.execute(3,oa);

} else {
  if (StringUtils.isEmpty(fileName)) {
    sError = "Escolha um ficheiro para importar.";
  }
  else if (buffer == null || buffer.length == 0) {
    sError = "Ficheiro inválido ou vazio.";
  }
  else {
    // import here....
    Object[] oa = new Object[5];
    oa[0] = userInfo;
    oa[1] = procData;
    oa[2] = fileType;
    oa[3] = fileName;
    oa[4] = buffer;
    // 1: import
    sError = (String)bBlock.execute(1,oa);
  }
}

if (StringUtils.isEmpty(sError)) {
  String currMid = String.valueOf(pm.getModificationId(userInfo, procData.getProcessHeader()));
  if (StringUtils.isEmpty(requestMid)) {
     sError = Const.MISSING_PROC_MID_ERROR_MSG;
  } else if (StringUtils.equals(requestMid, currMid)) {
    // next block saves...
    sNextPage = flow.nextBlock(userInfo, procData);
    boolean bStay = false;
    if (StringUtils.isNotEmpty(procData.getAppData(Const.STAY_IN_PAGE))) {
      bStay = true;
    }
    else {
      if (sNextPage == null){
        sNextPage = sURL_PREFIX + "flow_error.jsp?ts=" + ts;
      }
      else {
        bStay = false;
        sNextPage = sURL_PREFIX + sNextPage + "&ts=" + ts;
      }
    }
    if (bStay) {
      bGoImport = true;
    }
  }
  else {
    sError = Const.PROC_CHANGED_ERROR_MSG;
  }
}

if (bGoImport || StringUtils.isNotEmpty(sError)) {
  procData.setError(sError);
  sNextPage = "import.jsp?flowid="+flowid+"&pid="+pid+"&subpid="+subpid+"&ts="+ts;
}

ServletUtils.sendEncodeRedirect(response, sNextPage);
%>