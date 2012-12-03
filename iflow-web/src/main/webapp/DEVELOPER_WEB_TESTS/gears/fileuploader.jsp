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

Logger.debugJsp("", "fileuploader.jsp", "Creating file " + filePath + "...");

OfflineManager.uploadFile(request.getInputStream(), filePath);

Logger.debugJsp("", "fileuploader.jsp", "..DONE!");

%>

