/**
 * PublicFiles.java
 *
 * Description:
 *
 * History: 01/15/02 - jpms - created.
 * $Id: PublicFiles.java 248 2007-08-01 13:54:31 +0000 (Qua, 01 Ago 2007) uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iflow.servlets;


import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 * 
 * @web.servlet
 * name="publicFiles"
 * 
 * @web.servlet-mapping
 * url-pattern="/PublicFiles/*"
 */
public class PublicFiles extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 2568109786796106380L;

  public PublicFiles() {  }

  public void init() throws ServletException { 
    
  }
  
  private RepositoryFile common(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    long modifiedSince = request.getDateHeader("If-Modified-Since");
    Logger.debug("", this, "", "If-Modified-Since="+modifiedSince);
    
    
    String name = request.getPathInfo();

    if (name == null || name.equals(""))
      return null;


    String [] toks = name.split("/");
    if(toks.length > 0)
      name = toks[toks.length-1];
    else return null;

    Repository rep = BeanFactory.getRepBean();

    UserInfoInterface userInfo = (UserInfoInterface)request.getSession().getAttribute(Const.USER_INFO);
    RepositoryFile repFile = rep.getWebFile(userInfo, name);
    if (!repFile.exists()) {
      Logger.warning("", this, "", "file not found.");
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return null;
    } else if(repFile.getLastModified()<=modifiedSince){
      Logger.debug("", this, "", "file not modified.");
      response.sendError(HttpServletResponse.SC_NOT_MODIFIED, "File not modified");
      return null;
    } 
    
    return repFile;

  }
  
  private void sendFile(RepositoryFile repFile, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String mimeType = getServletContext().getMimeType(repFile.getName());
    Logger.debug("", this, "", "file: "+repFile.getName()+"; mime-type: "+mimeType);
    response.setContentType(mimeType);
    // response.setHeader("Content-Disposition","inline;filename=" + repFile.getName());
    response.setDateHeader("Last-Modified", repFile.getLastModified());
    response.setContentLength(repFile.getSize());
    OutputStream out = response.getOutputStream();
    repFile.writeToStream(out);
    out.flush();
    out.close();
  }

  public void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    common(request, response);
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RepositoryFile repFile = common(request, response);
    if(repFile != null) sendFile(repFile, request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RepositoryFile repFile = common(request, response);
    if(repFile != null) sendFile(repFile, request, response);
  }
}

