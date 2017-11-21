package pt.iflow.documents;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormFile;
import pt.iknow.utils.html.FormUtils;

/**
 *
 * @web.servlet
 * name="FileUpload"
 *
 * @web.servlet-mapping
 * url-pattern="/Docs/upload"
 */

public class FileUploadServlet extends HttpServlet {

  private static final long serialVersionUID = -4037970248025199434L;

//  @Override
//  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//  throws ServletException, IOException {
//    service(req,resp);
//  }
//
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
  throws ServletException, IOException {

    try {
      FormData fdFormData = FormUtils.parseRequest(req, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE,
          Const.fUPLOAD_TEMP_DIR);

      HttpSession session = req.getSession();
      UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
      if (userInfo == null) throw new NullPointerException();

      int flowid = -1;
      int pid = -1;
      int subpid = -1;
      ProcessData procData = null;
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
        procData = (ProcessData)session.getAttribute(Const.SESSION_PROCESS + flowExecType);
      }
      else {
        procData = BeanFactory.getProcessManagerBean().getProcessData(userInfo,flowid,pid,subpid,session);
        // clean session process just in case...
        session.removeAttribute(Const.SESSION_PROCESS + flowExecType);
      }
      if (procData == null) throw new NullPointerException();



      Map<String,FormFile> files = fdFormData.getFileParameters();
      OutputStream outStream = resp.getOutputStream();


      for(String fieldName : files.keySet()) {
        FormFile fileInfo = files.get(fieldName);
        String fileName = fileInfo.getFileName();

        // O que eh que isto esta aqui a fazer?
//        File outFile = new File("/tmp/" + fileName);
//        FileOutputStream fileStream = new FileOutputStream(outFile);
//        fileStream.write(fileInfo.getData());
//        fileStream.close();

        DocumentData newDocument = new DocumentData();

        newDocument.setFileName(fileName);
        newDocument.setContent(fileInfo.getData());
        newDocument.setUpdated(Calendar.getInstance().getTime());

        Document savedDocument = BeanFactory.getDocumentsBean().addDocument(userInfo, procData, newDocument);

        if(null != savedDocument) {
          String id = String.valueOf(savedDocument.getDocId());

          Logger.debug("<unknown>", this, "fileUpload", "STORED ID=");
          outStream.write(id.getBytes("UTF-8"));
        } else {
          Logger.warning("<unknown>", this, "fileUpload", "Could not save file ");
        }
      }

      outStream.flush();
      outStream.close();
    } catch (Throwable e) {
      Logger.error("<unknown>", this, "fileUpload", "Error uploading files.", e);
      throw new ServletException(e);
    }
  }
}
