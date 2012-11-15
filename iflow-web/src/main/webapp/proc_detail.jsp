<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%><%
request.setAttribute("inFrame", "true");
%><%@ include file="inc/defs.jsp"%>
<%@ page import="pt.iflow.api.blocks.Block"%>
<%@ include file="inc/initProcInfo.jspf"%>
<%@ include file="inc/checkProcAccess.jspf"%><%
	String title = "Detail";
String sAction = fdFormData.getParameter("action");

try {
Flow flow = BeanFactory.getFlowBean();
Block bBlockDetail = null;
try {

  bBlockDetail = flow.getBlock(userInfo, procData);
  // To appear blockId in title
  title = bBlockDetail.getDescription(userInfo, procData);
  if (Const.nMODE == Const.nDEVELOPMENT) {
    title = title + " block" + bBlockDetail.getId();
  }

  if (bBlockDetail.getClass().getName().indexOf("BlockProcDetail") == -1) {
    throw new Exception("Not BlockProcDetail!");
  }
}
catch (Exception e) {
  // send to main page...
  // not able to get flow or process is not in jsp state (if
  // a casting exception occurs..)
  ServletUtils.sendEncodeRedirect(response, sURL_PREFIX+"flow_error.jsp");
  return;
}

//check permissions 
if (!pm.canViewProcess(userInfo, procData)) {
  ServletUtils.sendEncodeRedirect(response, sURL_PREFIX+"nopriv.jsp?flowid="+flowid);
  return;
}

String sOp = fdFormData.getParameter("op");
int op = -1;
try {
  op = Integer.parseInt(sOp);
} catch (Throwable t) {}
if(op == 3) {
  String next_page = flow.nextBlock(userInfo, procData);
  if (next_page == null) {
    next_page = sURL_PREFIX + "flow_error.jsp" + "?ts=" + ts;
  }
  else {
    next_page = sURL_PREFIX + next_page + "&ts=" + ts;
  }

  ServletUtils.sendEncodeRedirect(response, next_page);
  return;
}

request.setAttribute("printForm", Const.APP_URL_PREFIX+"/proc_detail.jsp?action=print&");
request.setAttribute("exportForm", Const.APP_URL_PREFIX+"/proc_detail.jsp?action=export&");
String sFormName="dados";
%>
<%@ include file="Form/servicesjs.jspf"%><%
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
Object [] oa = new Object[4];
oa[0] = userInfo;
oa[1] = procData;
oa[2] = new ServletUtils(response);
oa[3] = fdFormData.getParameter("field");

int executeOp = 1;
if(StringUtils.equals("print",sAction)) {
  executeOp = 2;
  String redirURL = (String)bBlockDetail.execute(3,oa);
  if(StringUtils.isNotEmpty(redirURL)) {
    ServletUtils.sendEncodeRedirect(response, redirURL);
    return;
  }
} else if (StringUtils.equals("export",sAction)) {
  // 8: exportFieldToExcel
  Object [] data = (Object[])bBlockDetail.execute(4,oa);
  // 1. Error
  // 2. Mime type
  // 3. filename
  // 4. Generated data
  String exportError = (String) data[0];
  String type = (String) data[1];
  String filename = (String) data[2];
  byte [] xslData = (byte[]) data[3];
  
  if(StringUtils.isNotEmpty(exportError)) {
    // error occurred
      throw new Exception(exportError);
  } else {
    response.reset();
    response.setContentType(type);
    response.addHeader("Content-Disposition", "attachment;filename="+filename);
    response.setContentLength(xslData.length);
    response.getOutputStream().write(xslData);
  }

  return;
}

// generateForm
Object genForm = bBlockDetail.execute(executeOp,oa);

if(null == genForm) {
  // error occurred
  throw new Exception("Não foi possível gerar o formulário com o detalhe de processo.");
}
%><%=genForm%>
<%@ include file="inc/initProcInfoEndPage.jspf"%><%
  if(executeOp == 2) {
%>
<script language="JavaScript" type="text/javascript">
if (window.print) window.print();
</script>
<%
  }
} catch(Throwable t) {
  // TODO log stuff....
  String onclick = "onclick=\"self.close();return false;\"";
  if(StringUtils.isEmpty(sAction)) onclick="";
  %>
  <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
  <html>
    <head>
    	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  	<meta http-equiv="Pragma" content="no-cache"/>
    </head>
  <body>
  <div><%=t.getMessage()%></div>
  <div>
  <form action="proc_detail.jsp" method="get" name="dados">
  <input type="hidden" value="3" name="op">
  <button <%=onclick%>><if:message string="button.close"/></button>
  </form>
  </div>
  </body>
  </html>
<%
}
%>
