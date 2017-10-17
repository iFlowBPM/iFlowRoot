<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%><%
request.setAttribute("inFrame", "true");
%><%@ include file="../inc/defs.jsp"%>
<%@ page import="pt.iflow.api.blocks.Block"%>
<%@ page import="pt.iflow.api.blocks.Attribute"%>
<%@ page import="pt.iflow.api.blocks.FormProps"%>
<%@ page import="org.apache.commons.lang.StringUtils"%><%
	String title = "Detail";

int flowid = -1;
int pid = -1;
int subpid = -1;
//boolean closedProcess = false;
String status = fdFormData.getParameter("procStatus");
int processFlag = Const.nALL_PROCS;
if (StringUtils.isNotEmpty(status)) {
  if (StringUtils.equals("1",status))
    processFlag = Const.nCLOSED_PROCS;
  else if (StringUtils.equals("0",status))
    processFlag = Const.nOPENED_PROCS;
    
}
//if(null != status && "1".equals(status)) closedProcess=true;
boolean frameworkSearch = StringUtils.equals("true", fdFormData.getParameter("fwSearch"));

ProcessData procData = null;
try {
	// use of fdFormData defined in /inc/defs.jsp
  flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
  pid = Integer.parseInt(fdFormData.getParameter("pid"));
  String sSubPid = fdFormData.getParameter("subpid");

  if (StringUtils.isEmpty(sSubPid)) {
    // process not yet "migrated".. assume default subpid
    subpid = 1;
  }
  else {
    subpid = Integer.parseInt(sSubPid);
  }

  procData = pm.getProcessData(userInfo,new ProcessHeader(flowid,pid,subpid),processFlag);
  if (procData == null) throw new Exception();

}
catch (Exception e) {
  Logger.errorJsp(login, "detail", "exception caught: " + e.getMessage());
  ServletUtils.sendEncodeRedirect(response, sURL_PREFIX+"flow_error.jsp");
  return;
}




// use of fdFormData defined in /inc/defs.jsp
String sOp = fdFormData.getParameter("op");
if (sOp == null) {
  sOp = "0";
}
int op = Integer.parseInt(sOp);

Block bBlockJSP = null;

HashMap<String,String> hmHidden = new HashMap<String,String>();
hmHidden.put("subpid",String.valueOf(subpid));
hmHidden.put("pid",String.valueOf(pid));
hmHidden.put("flowid",String.valueOf(flowid));
hmHidden.put("op",String.valueOf(op));
hmHidden.put("_serv_field_","-1");
hmHidden.put("procStatus", status);
hmHidden.put("isProcDetail", "true");
hmHidden.put("inDetail", "true");

Flow flowBean = BeanFactory.getFlowBean();
IFlowData flow = BeanFactory.getFlowHolderBean().getFlow(userInfo, flowid);
try {

  if(procData == null) throw new Exception();
  
  bBlockJSP = flow.getDetailForm();
  if (null == bBlockJSP) {
    throw new Exception("No Form block configured");
  }
  Class<?> cc = bBlockJSP.getClass().getClassLoader().loadClass("pt.iflow.blocks.BlockFormulario");

  if (!cc.isAssignableFrom(bBlockJSP.getClass())) {
    throw new Exception("Not BlockFormulario!");
  }
  
  procData.setTempData(FormProps.FRAMEWORK_DETAIL, (frameworkSearch ? "true" : "false"));

}
catch (Exception e) {
  // this is the default, no block or could not fetch process data.
  
  Map<String,String> processDetail = null;
  if(flow != null && flow.hasDetail())
    processDetail = ProcessPresentation.getProcessDetail(userInfo, procData, new ServletUtils(response));
  if(null == processDetail) processDetail = new HashMap<String,String>();
  Hashtable<String,Object> htSubst = new Hashtable<String, Object>();
  htSubst.put("processDetail", processDetail);
  htSubst.put("processKeys", processDetail.keySet());
  htSubst.put("make_head",true);
  htSubst.put("url_prefix", Const.APP_URL_PREFIX);
  htSubst.put("sJSP", "detail.jsp");
  htSubst.put("hmHidden", hmHidden);
  htSubst.put("procSubpid",String.valueOf(subpid));
  htSubst.put("procPid",String.valueOf(pid));
  htSubst.put("procFlowid",String.valueOf(flowid));
  htSubst.put("procStatus", status);
  htSubst.put("isProcDetail", "true");
  htSubst.put("inDetail", "true");
  List<Map<String,String>> buttons = new ArrayList<Map<String,String>>(1);
  Map<String,String> printButton = new HashMap<String,String>();
  buttons.add(printButton);
  printButton.put("type","_imprimir");
  printButton.put("text","Imprimir");
  htSubst.put("buttonList", buttons);

  //  messages.....
  htSubst.put("noDetail",userInfo.getMessages().getString("user_proc_detail.msg.noProcessDetail"));
  htSubst.put("variableLabel",userInfo.getMessages().getString("user_proc_detail.field.variable"));
  htSubst.put("valueLabel",userInfo.getMessages().getString("user_proc_detail.field.value"));
  %><%=PresentationManager.buildPage(response, userInfo, htSubst, "proc_detail")%><%
  return;
}


// OP: 0 - entering page/reload
//     1 - unused
//     2 - save
//     3 - next
//     4 - cancel
//     5 - service print
//     6 - service print field
//     7 - service export field
//     8 - only process form
//     9 - return to parent

// check permissions 
if(!pm.canViewProcess(userInfo, procData)) {
  ServletUtils.sendEncodeRedirect(response, sURL_PREFIX+"nopriv.jsp?flowid="+flowid);
  return;
}

String sHtml = "";
String sFormName = "";

Object [] oa = new Object[4];
oa[0] = userInfo;
oa[1] = procData;
oa[2] = hmHidden;
oa[3] = new ServletUtils(response);
// 2: generateForm
sHtml = (String)bBlockJSP.execute(2,oa);

  // 7: var FORM_NAME
  sFormName = (String)bBlockJSP.execute(7,null);

  // Adjust print and export JSPs a little bit...
  request.setAttribute("printForm",Const.APP_URL_PREFIX+"/Form/print.jsp?inDetail=true&");
  request.setAttribute("exportForm",Const.APP_URL_PREFIX+"/Form/export.jsp?inDetail=true&");
%>
<%@ include file="servicesjs.jspf"%>
<%

if (op == 5 || op == 6 || op == 7) {
  String sField = fdFormData.getParameter("_serv_field_");

  if (op == 5) {
    // print
%>
<script language="JavaScript" type="text/javascript">
      PrintServiceOpen();
</script>
<%
  }
  else if (op == 6) {
    // printfield
%>
<script language="JavaScript" type="text/javascript">
      PrintServiceOpen(<%=sField%>);
</script>
<%
  }
  else if (op == 7) {
    // exportfield
%>
<script language="JavaScript" type="text/javascript">
      ExportServiceOpen(<%=sField%>);
</script>
<%
  }
}


%><%=sHtml%>
