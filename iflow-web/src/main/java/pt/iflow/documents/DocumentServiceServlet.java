package pt.iflow.documents;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.PassImage;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.documents.DocumentSessionHelper;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.applet.AppletDocParameters;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormFile;
import pt.iknow.utils.html.FormUtils;

/**
 * Document Upload and Download
 * 
 * @author ombl
 * 
 * @web.servlet name="DocumentService"
 * 
 * @web.servlet-mapping url-pattern="/DocumentService"
 * 
 */
public class DocumentServiceServlet extends HttpServlet implements AppletDocParameters {

  private static final long serialVersionUID = -3535202556875137862L;

  public DocumentServiceServlet() {
  }

  public void init() throws ServletException {
  }
  
  protected void setHeaders(DocumentData doc, HttpServletResponse response) throws ServletException, IOException {
    response.setHeader("Content-Disposition", "attachment;filename=\"" + doc.getFileName().replace(' ', '_') + "\";");
    String mimeType = getServletContext().getMimeType(doc.getFileName());
   // //logger.debug("", this, "", "file: "+doc.getFileName()+"; mime-type: "+mimeType);
  response.setContentLength(doc.getLength());
    response.setHeader("X-iFlow-DocId", String.valueOf(doc.getDocId()));
    response.setDateHeader("Last-Modified", doc.getUpdated().getTime());
    
  }
  
  protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = getSessionFixedForJNLP(request);

    // User must be authenticated
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    if (null == userInfo) {
      
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }

    int flowid = -1;
    int pid = -1;
    int subpid = -1;
    int docid = -1;
    String varName = null;
    ProcessData procData = null;
    // use of fdFormData defined in /inc/defs.jsp
    flowid = Integer.parseInt(request.getParameter(FLOWID_PARAM));
    pid = Integer.parseInt(request.getParameter(PID_PARAM));
    subpid = Integer.parseInt(request.getParameter(SUBPID_PARAM));
    docid = Integer.parseInt(request.getParameter(DOCID_PARAM));
    varName = request.getParameter(VARIABLE_PARAM);

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

    if (pid == Const.nSESSION_PID) {
      subpid = Const.nSESSION_SUBPID; // reset subpid to session subpid
      procData = (ProcessData) session.getAttribute(Const.SESSION_PROCESS + flowExecType);
    } else {
      procData = BeanFactory.getProcessManagerBean().getProcessData(userInfo, flowid, pid, subpid, session, Const.nALL_PROCS);
      // clean session process just in case...
      session.removeAttribute(Const.SESSION_PROCESS + flowExecType);
    }

    DocumentData doc = getDocument(getHelper(session, flowid, pid, subpid), docid, userInfo, procData, varName, false);

    if (doc == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }
    setHeaders(doc, response);

  }
  
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  HttpSession session = getSessionFixedForJNLP(request);

    // User must be authenticated
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    if (null == userInfo) {
     
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }

    int flowid = -1;
    int pid = -1;
    int subpid = -1;
    int docid = -1;
    String varName = null;
    String remove = null;
    String maxsize = null;
    ProcessData procData = null;
    // use of fdFormData defined in /inc/defs.jsp
    flowid = Integer.parseInt(request.getParameter(FLOWID_PARAM));
    pid = Integer.parseInt(request.getParameter(PID_PARAM));
    subpid = Integer.parseInt(request.getParameter(SUBPID_PARAM));
    String docidtemp = request.getParameter(DOCID_PARAM);
    
    if(StringUtilities.isNotEmpty(docidtemp))
    		docid = Integer.parseInt(docidtemp);
    
    varName = request.getParameter(VARIABLE_PARAM);
    remove = request.getParameter(REMOVE_PARAM);
    maxsize = request.getParameter(MAXSIZE_PARAM);
    if(null == remove) remove = "false";
    if(null == maxsize) maxsize = "false";

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

    if (pid == Const.nSESSION_PID) {
      subpid = Const.nSESSION_SUBPID; // reset subpid to session subpid
      procData = (ProcessData) session.getAttribute(Const.SESSION_PROCESS + flowExecType);
    } else {
      procData = BeanFactory.getProcessManagerBean().getProcessData(userInfo, flowid, pid, subpid, session, Const.nALL_PROCS);
      // clean session process just in case...
      session.removeAttribute(Const.SESSION_PROCESS + flowExecType);
    }

    DocumentSessionHelper helper = getHelper(session, flowid, pid, subpid);
    
    byte[] ba = new byte[0];
    if("true".equalsIgnoreCase(remove)) {
      boolean result = removeDocument(helper, docid, userInfo, procData, varName);
      
      ba = String.valueOf(result).getBytes("UTF-8");
    } else if("true".equalsIgnoreCase(maxsize)) {
      ba = String.valueOf(Const.nUPLOAD_MAX_SIZE).getBytes("UTF-8");
      
    } else {
      //logger.debug(userInfo.getUtilizador(), this, "doGet", "Document requested: ");
      DocumentData doc = getDocument(helper, docid, userInfo, procData, varName, true);

      if (doc == null) {
       
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        return;
      }

      //Numero da proxima assinatura (comeca em 1)
      PassImage pi = BeanFactory.getPassImageManagerBean();
      response.setHeader("NUMASS", ""+pi.getNumAss(userInfo, docid));
      
     
      setHeaders(doc, response);
      ba = doc.getContent();
    }
    OutputStream out = response.getOutputStream();
    out.write(ba);
    out.flush();
    out.close();   
  }

  private int isInProcess(ProcessListVariable listVar, int docid) {
    if(null == listVar) return -1;
    Iterator<ProcessListItem> iter = listVar.getItemIterator();
    if(null == iter) return -1;
    
    boolean found = false;
    int pos = 0;
    while(iter.hasNext()) {
      ProcessListItem item = iter.next();
      if(null == item) continue;
      Object o = item.getValue();
      if(null == o) continue;
      if(o instanceof String) {
        try {
          found = (Integer.parseInt((String)o) == docid);
        } catch(NumberFormatException e) {}
      } else if(o instanceof Number) {
        Number n = (Number) o;
        found = (n.intValue() == docid);
      }
      if(found) break;
      pos++;
    }
    if(!found) return -1;
    return pos;
  }

  private DocumentData getDocument(DocumentSessionHelper helper, int docid, UserInfoInterface userInfo, ProcessData procData, String varName, boolean fetch) {
    Documents rep = BeanFactory.getDocumentsBean();
    
    // check if process contains document
    ProcessListVariable listVar = procData.getList(varName);
    boolean found = helper.hasDocument(varName, docid) || isInProcess(listVar, docid)!=-1;
    
    if(!found) {
      
      return null;
    }
    
    DocumentData doc = null;
    if(fetch)
      doc = (DocumentData) rep.getDocument(userInfo, procData, docid);
    else
      doc = (DocumentData) rep.getDocumentInfo(userInfo, procData, docid);
    return doc;
  }

  private boolean removeDocument(DocumentSessionHelper helper, int docid, UserInfoInterface userInfo, ProcessData procData, String varName) {
    Documents rep = BeanFactory.getDocumentsBean();
    
    // check if process contains document
    ProcessListVariable listVar = procData.getList(varName);
    int pos = isInProcess(listVar, docid);
    boolean found = helper.hasDocument(varName, docid) || pos != -1;

    if(!found) {
      
      return false;
    }
    
    boolean success = rep.removeDocument(userInfo, procData, docid);
    
    if(success && pos != -1) {
      // remove from processData
      listVar.removeItemAt(pos);
    }
    return success;
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	  int docid = -1;
	  String id = "";
	  //String rub = "";
	  
	  HttpSession session = getSessionFixedForJNLP(request);
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    if (null == userInfo) {
     
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }

    //logger.debug(userInfo.getUtilizador(), this, "doPost", "Post called");
    byte[] ba = new byte[0];
    ServletOutputStream outStream = null;
    try {
      FormData fdFormData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE, Const.fUPLOAD_TEMP_DIR);

      int flowid = -1;
      int pid = -1;
      int subpid = -1;
      String varName = null;
      ProcessData procData = null;
      // use of fdFormData defined in /inc/defs.jsp
      flowid = Integer.parseInt(fdFormData.getParameter(FLOWID_PARAM));
      pid = Integer.parseInt(fdFormData.getParameter(PID_PARAM));
      subpid = Integer.parseInt(fdFormData.getParameter(SUBPID_PARAM));
      varName = fdFormData.getParameter(VARIABLE_PARAM);

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

      if (pid == Const.nSESSION_PID) {
        subpid = Const.nSESSION_SUBPID; // reset subpid to session subpid
        procData = (ProcessData) session.getAttribute(Const.SESSION_PROCESS + flowExecType);
      } else {
        procData = BeanFactory.getProcessManagerBean().getProcessData(userInfo, flowid, pid, subpid, session);
        // clean session process just in case...
        session.removeAttribute(Const.SESSION_PROCESS + flowExecType);
      }
      if (procData == null) {
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        return;
      }

      FormFile file = fdFormData.getFileParameter(FILE_PARAM);
      
      //rub = fdFormData.getParameter("RUBRICAR");
      
      String update = fdFormData.getParameter(UPDATE_PARAM);
      if(null == update) update = "false";
      boolean isUpdate = "true".equalsIgnoreCase(update);

     

      DocumentSessionHelper helper = getHelper(session, flowid, pid, subpid);
      
	 PassImage pi = BeanFactory.getPassImageManagerBean();
     int numass = Integer.parseInt(fdFormData.getParameter("NUMASS"));
      DocumentData doc = null;
      if(isUpdate) {
        docid = Integer.parseInt(fdFormData.getParameter(DOCID_PARAM));
        doc = getDocument(helper, docid, userInfo, procData, varName, false);
        //actualizar o numero de assinaturas no documento
        pi.updateNumAss(userInfo,docid, numass);
      } else {
        doc = new DocumentData();
        doc.setFileName(URLDecoder.decode(file.getFileName(), "UTF-8"));
      }

      if (doc == null) {
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        return;
      }
      doc.setContent(file.getData());
      doc.setUpdated(new Date());

      Document savedDocument = null;
      if(isUpdate) {
        savedDocument = BeanFactory.getDocumentsBean().updateDocument(userInfo, procData, doc);
      } else {
        // check if list exists
        ProcessListVariable list = procData.getList(varName);
        if(list == null) {
         
          response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
          return;
        }

       
        savedDocument = BeanFactory.getDocumentsBean().addDocument(userInfo, procData, doc);
        // list.addNewItem(savedDocument.getDocId());
        pi.updateNumAss(userInfo,savedDocument.getDocId(),numass);
        helper.addDocument(varName, savedDocument.getDocId());
      }

      if (null != savedDocument) {
        id = String.valueOf(savedDocument.getDocId());
        
        ba = id.getBytes("UTF-8");
      } else {
        
      }

      outStream = response.getOutputStream();
      outStream.write(ba);
    } catch (Exception e) {
     
      throw new ServletException(e);
    } finally {
      if(null != outStream)outStream.close();
    }
    
    //TRATAR DE RUBRICAR O DOCUMENTO  
    //CHAMADA QUE FAZIA A RUBRICA DO LADO DO SERVER... 
    //SOLUÇAO TEMPORARIA - PASSOU PARA A APPLET
    
//    if(rub.equals("true")){
//	    RubricarDocumento rd = new RubricarDocumento();
//	    int idDoc = 0;
//	       
//	    if(docid!=-1) 
//	    	idDoc = docid; 
//	    else 
//	    	idDoc = Integer.parseInt(id);
//	    
//	    if(idDoc > 0)
//	    	rd.insertImage(idDoc, userInfo);
//	    else
//	    	//logger.error(userInfo.getUtilizador(), this, "doPost", "Impossivel rubricar o documento.");
//    }
  }

  private DocumentSessionHelper getHelper(HttpSession session, int flowid, int pid, int subpid) {
    DocumentSessionHelper helper = (DocumentSessionHelper) session.getAttribute(DocumentSessionHelper.SESSION_ATTRIBUTE);
    if(null == helper) {
      helper = new DocumentSessionHelper(flowid, pid, subpid);
      session.setAttribute(DocumentSessionHelper.SESSION_ATTRIBUTE, helper);
    }
    return helper;
  }
  

  /**
   * BUG FIX in tomcat session tracking coupled with JNLP applet launching,
   * if applet is initialized trough WebStart this request will have 2 session cookies!!!
   * making it impossible to determine the correct session and so the logged user cannot be validated
   * @param request
   * @return
   */
  public static HttpSession getSessionFixedForJNLP(HttpServletRequest request){
	  HttpSession session;
	  ServletContext ctx = request.getSession().getServletContext();
	  
	  Cookie[] cookies = request.getCookies();
	  for(Cookie coo: cookies)
		  if (StringUtils.equals(coo.getName(),"APPLETJSESSIONID")){
			  HashMap activeSessions = (HashMap) ctx.getAttribute(DocumentSessionListener.ACTIVE_SESSIONS);
			  String id = StringUtils.remove(coo.getValue(),"APPLETJSESSIONID=");
			  session = (HttpSession) activeSessions.get(id);
			  if(session!=null)
				  return session;
		  }
	  
	  return request.getSession();	  
  }
}
