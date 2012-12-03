/*****************************************************************************************
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
****************************************************************************************/
package pt.iflow.documents;


import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentHash;
import pt.iflow.api.documents.DocumentIdentifier;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 *
 * @author Campa
 *
 * @web.servlet
 * name="documents"
 *
 * @web.servlet-mapping
 * url-pattern="/Docs/*"
 * 
 * @web.servlet-mapping
 * url-pattern="/document"
 * 
 */
public class DocumentServlet extends HttpServlet {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DocumentServlet() {  }

  public void init() throws ServletException {  }

  
  // TODO Improve by implementing methods GET, POST and HEAD with caching
  protected void service(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {
    HttpSession session = request.getSession();

    // User must be authenticated
    UserInfoInterface userInfo = (UserInfoInterface)session.getAttribute(Const.USER_INFO);
    if(null == userInfo) {
      Logger.error("<unknown>", this, "getFile", "Invalid user/user not authenticated.");
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }

    int flowid = -1;
    int pid = -1;
    int subpid = -1;
    ProcessData procData = null;
    // use of fdFormData defined in /inc/defs.jsp
    flowid = Integer.parseInt(request.getParameter("flowid"));
    pid = Integer.parseInt(request.getParameter("pid"));
    String sSubPid = request.getParameter("subpid");

    if (StringUtils.isEmpty(sSubPid)) {
      // process not yet "migrated".. assume default subpid
      subpid = 1;
    }
    else {
      subpid = Integer.parseInt(sSubPid);
    }

    String sUseDocHash = BeanFactory.getFlowSettingsBean().getFlowSetting(flowid, Const.sHASHED_DOCUMENT_URL).getValue();
    boolean useDocHash = StringUtils.equalsIgnoreCase(Const.sHASHED_DOCUMENT_URL_YES, sUseDocHash);
    
    if (pid == Const.nSESSION_PID) {
      subpid = Const.nSESSION_SUBPID; // reset subpid to session subpid
      String flowExecType = "";
      try {
        FlowType flowType = BeanFactory.getFlowBean().getFlowType(userInfo, flowid);
        if (FlowType.SEARCH.equals(flowType)) {
          flowExecType = "SEARCH";
        } else if (FlowType.REPORTS.equals(flowType)) {
          flowExecType = "REPORT";
        }
      }catch (Exception e) {
      }
      procData = (ProcessData)session.getAttribute(Const.SESSION_PROCESS + flowExecType);
    }
    else {
      procData = BeanFactory.getProcessManagerBean().getProcessData(userInfo,flowid,pid,subpid,session,Const.nALL_PROCS);
    }

    String logVar = useDocHash ? "[Using DocHash]" : "";
    if(!useDocHash) {
      if(null == procData) {
        Logger.error(userInfo.getUtilizador(), this, "getFile", "Invalid process.");
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        return;
      }
      Logger.debug(userInfo.getUtilizador(), this, "getFile", "Process found: "+logVar+" - proceeding with file retrieval...");
    }

    Document doc = getDocument(request, userInfo, procData, logVar);

    if (doc == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }
    byte [] ba = doc.getContent();
    response.setHeader("Content-Disposition","attachment;filename=\"" + doc.getFileName().replace(' ', '_')+"\";");
    response.setContentLength(ba.length);
    OutputStream out = response.getOutputStream();
    out.write(ba);
    out.flush();
    out.close();
  }

  
  private Document getDocument(HttpServletRequest request, UserInfoInterface userInfo, ProcessData procData, String logVar) {

    Documents rep = BeanFactory.getDocumentsBean();
    Document doc = null;
    String user = userInfo.getUtilizador();
    
    try {
      if(StringUtils.equals(request.getServletPath(), "/document")) {
        String hdoc = request.getParameter("hdoc");
        if(StringUtils.isNotEmpty(hdoc)) {
          Logger.debug(user, this, "getDocument", "Hashed document: "+logVar+" - proceeding with file retrieval...");
          DocumentHash docHash = new DocumentHash(hdoc);

          // validate hash
          if(docHash.isValid(userInfo)) {
            doc = rep.getDocument(userInfo, procData, docHash.getDocId());
            Logger.debug(user, this, "getDocument", "Got hashed document");
          } else {
            Logger.warning(user, this, "getDocument", 
                procData.getSignature() + "Invalid hash for user: "+userInfo.getUtilizador()+" <> "+docHash.getUsersAsString());
          }
        } else {
          DocumentIdentifier did = DocumentIdentifier.getInstance(request.getParameter("docid")); 
          doc = rep.getDocument(userInfo, procData, did);
          Logger.debug(user, this, "getDocument", "Got document");
        }
      } else {
        String name = request.getPathInfo();

        if (StringUtils.isEmpty(name)) {
          Logger.error(user, this, "getDocument", procData.getSignature() + "empty name");      
          return null;
        }

        // TODO check if support for document identifier is needed...
        
        String snameNoSlash = name.substring(1);
        int indexMiddleSlash = snameNoSlash.indexOf("/");
        String sdocidPart = snameNoSlash.substring(0,indexMiddleSlash);
        String sfileNamePart = URLDecoder.decode(snameNoSlash.substring(indexMiddleSlash + 1), "UTF-8");

        int docid = Integer.parseInt(sdocidPart);
        doc = rep.getDocument(userInfo, procData, docid, sfileNamePart);
      }
    }
    catch (Throwable t) {
      Logger.error(user, this, "getDocument", procData.getSignature() + "exception", t);      
    }
    
    return doc;
  }
}

