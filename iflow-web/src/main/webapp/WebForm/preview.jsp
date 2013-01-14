<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../inc/defs.jsp"%>
<%@ page import="pt.iflow.api.utils.ServletUtils"%>
<%@ page import="java.lang.reflect.Method"%>
<%!
  private static String exceptionToString(Throwable t) {
    if(null == t) return "null";
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    pw.close();
    return sw.toString();
  }
%>
<%
String preview = fdFormData.getParameter("preview");
if(StringUtils.equals("true",preview)) {
  String form = fdFormData.getParameter("form");
  String catalog = fdFormData.getParameter("catalog");
  String title = "Formul&aacute;rio";
  
  String formPreview = "";

  try {
    Repository repos = BeanFactory.getRepBean();
    Class<?> formBlock = repos.loadClass(userInfo, "pt.iflow.blocks.BlockWebForm");
    Method m = formBlock.getMethod("generatePreview", UserInfoInterface.class, String.class, String.class, ServletUtils.class);
    formPreview = (String) m.invoke(null, userInfo, form, catalog, new ServletUtils(request, response));
  } catch(Throwable t) {
    Logger.errorJsp(userInfo.getUtilizador(), "WebForm/preview.jsp", "Erro ao gerar o formulário de exemplo", t);
    formPreview = "<html><body>Generator error<br><pre>"+exceptionToString(t)+"</pre></body></html>";
  }
  
%><%=formPreview%>
<% } else { %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Preview Form</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<meta http-equiv="Pragma" content="no-cache"/>
		<link rel="shortcut icon" href="../images/favicon.ico" />
	</head>
	<body id="body">
		<form name="poster" id="poster" action="preview.jsp" method="POST">
			<input type="hidden" name="preview" value="true" /><br>
			Form JSON string:<br>
			<textarea rows="10" cols="132" name="form" id="form"></textarea><br>
			Catalog JSON string:<br>
			<textarea rows="10" cols="132" name="catalog" id="catalog"></textarea><br>
			<input type="submit" name="submitButton" id="submitButton" value="Submit" />
		</form>
	</body>
</html>
<% } %>
