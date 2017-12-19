
<%@page import="pt.iflow.api.utils.Logger"%>
<%@page import="pt.iflow.offline.OfflineManager"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.FileReader"%>
<%@page import="pt.iflow.api.utils.Const"%>
<%@page import="pt.iknow.utils.html.FormFile"%>
<%@page import="pt.iknow.utils.html.FormUtils"%>
<%@page import="pt.iknow.utils.html.FormData"%>

<%

FormData fdFormData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE,Const.fUPLOAD_TEMP_DIR);


String filename = fdFormData.getParameter("filename");
String filePath = OfflineManager.getOfflineFilePath(application.getRealPath("/"), filename); 

//logger.debugJsp("", "fileuploader.jsp", "Creating file ");

OfflineManager.uploadFile(request.getInputStream(), filePath);

//logger.debugJsp("", "fileuploader.jsp", "..DONE!");

%>

