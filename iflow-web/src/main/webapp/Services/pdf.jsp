<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" 
%><%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" 
%><%@ include file = "../inc/defs.jsp" %><%
int subpid = -1;
    int pid = -1;
    int flowid = -1;
    ProcessData process = null;

    try {
      try {
	    subpid = Integer.parseInt(fdFormData.getParameter("subpid"));
      }
      catch (Exception e2) {
	    // support for old processes
	    subpid = 1;
      }
      pid = Integer.parseInt(fdFormData.getParameter("pid"));
      flowid = Integer.parseInt(fdFormData.getParameter("flowid"));

      if (pid == Const.nSESSION_PID || subpid == Const.nSESSION_SUBPID) {
        process = (ProcessData) session.getAttribute(Const.SESSION_PROCESS + flowExecType);
        Logger.debugJsp(login, "Service/pdf.jsp", "Foi buscar o process data ah sessao: "+(process ==null?"null":"nao null"));
      } else {
        ProcessHeader phtmp = new ProcessHeader(flowid, pid, subpid);
        process = pm.getProcessData(userInfo, phtmp, Const.nALL_PROCS);
        Logger.debugJsp(login, "Service/pdf.jsp", "Foi buscar o process data ao outro lado: "+(process ==null?"null":"nao null"));
      }
      if (process == null || !pm.canViewProcess(userInfo, process)) {
        process = null;
      }
    } catch (Exception e) {
      Logger.errorJsp(login, "Service/pdf.jsp",
          "exception getting dataset for subpid " + subpid + ", pid " + pid
              + ", flow " + flowid + ": " + e.getMessage());
    }

    if (process == null) {
      String sHtml = "Ocorreu um erro ao gerar a p&aacute;gina.<br>";
      String sOnLoad = "";
      Logger.debugJsp(login, "Service/pdf.jsp", "O processdata eh null");

      %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="pt.iflow.documents.AppendDocuments"%><html>
<head>
<title>iFlow - Impressao</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<link rel="stylesheet" href="<%=sURL_PREFIX%>css/iflow.css" type="text/css">
</head>

<body leftmargin="0" bgcolor="#e3e3e3" text="#000000" topmargin="0" marginwidth="0" marginheight="0" <%=sOnLoad%>>
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top">
    <td width="100%" height="100%" valign="top" bgcolor="#e3e3e3">
      <p>
       <table width="100%" border="0" cellspacing="0" cellpadding="4" align="center">
         <tr>
            <td height="10" width="100%"><img src="../images/transpar.gif"></td>
         </tr>
         <tr>
            <td valign="top"><%=sHtml%></td>
         </tr>
         <tr>
            <td height="10"><img src="../images/transpar.gif" width="1" height="1"></td>
         </tr>
       </table>
    </td>
  </tr>
</table>
</body>
</html>
<%
    return;
  }

  InputStream src = null;
  pt.iknow.pdf.PDFGenerator pdfGen = null;
  try {
    Repository rep = BeanFactory.getRepBean();
    String sTemplate = fdFormData.getParameter("template");

    src = rep.getPrintTemplate(userInfo, sTemplate).getResourceAsStream();

    pt.iknow.xslfo.FoTemplate tpl = pt.iknow.xslfo.FoTemplate.compile(src);
    tpl.setUseLegacyExpressions(true);

    pdfGen = new pt.iknow.pdf.PDFGenerator(tpl);
    pdfGen.addURIResolver(new RepositoryURIResolver(userInfo));
    rep = null;
  } catch (Exception ei) {
    pdfGen = null;
    Logger.errorJsp(login, "Service/pdf.jsp", "Ocorreu excepcao a processar o FO: " + ei.getMessage(), ei);
  } finally {
    if (src != null) {
      try {
        src.close();
      } catch (Throwable t) {
      }
    }
  }

  if (pdfGen != null) {
    bsh.Interpreter bsh = process.getInterpreter(userInfo);
    InputStream in = null;
    try {
      File tmpPdf = File.createTempFile("print_", ".pdf");
      OutputStream pdfOut = new BufferedOutputStream(new FileOutputStream(tmpPdf));
      boolean success = pdfGen.getContents(bsh, pdfOut);
      pdfOut.close();
     
      if (success && tmpPdf.length() > 0) {
        String appendDocs = fdFormData.getParameter("appendDocs");
        if (!StringUtils.isEmpty(appendDocs))
          tmpPdf = AppendDocuments.postProcessPDF(userInfo, process, tmpPdf, appendDocs);
        response.reset();
        response.setContentType("application/x-pdf");
        response.addHeader("Content-Disposition", "attachment;filename=print.pdf");
        response.setContentLength((int)tmpPdf.length());
        java.io.OutputStream outputStream = response.getOutputStream();

        in = new FileInputStream(tmpPdf);
        int r;
        byte[] buffer = new byte[8192];
        while ((r = in.read(buffer)) > 0)
          outputStream.write(buffer, 0, r);

        pdfOut = null;
        outputStream.flush();
        response.flushBuffer();
        return;
      }
    } catch (Exception e) {
      Logger.errorJsp(login, "Service/pdf.jsp", "Ocorreu excepcao a gerar o PDF: " + e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not generate PDF. Cause: "+e.getMessage());
    } finally {
      process.returnInterpreter(bsh);
      try {
        if (in != null) in.close();
      } catch (IOException e) {
      }
    }
  }
  return;
%>