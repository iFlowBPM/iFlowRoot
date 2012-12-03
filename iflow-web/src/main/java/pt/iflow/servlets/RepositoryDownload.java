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
/**
 * RepositoryDownload.java
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
* name="RepositoryDownload"
* 
* @web.servlet-mapping
* url-pattern="/download.rep"
*/
public class RepositoryDownload extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 256810234796106380L;

  public RepositoryDownload() {  }

  public void init() throws ServletException {  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Logger.trace("Memory before: "+Runtime.getRuntime().freeMemory()+" ("+Runtime.getRuntime().totalMemory()+")");
    String name = request.getParameter("file");
    String type = request.getParameter("type");
    
    UserInfoInterface userInfo = (UserInfoInterface)request.getSession().getAttribute(Const.USER_INFO);
    
    if (name == null || name.equals("") || type == null || type.equals("") || null == userInfo) { 
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }

    try {
      Repository rep = BeanFactory.getRepBean();
      RepositoryFile repFile = null;
      
      if (ResourceNavConsts.STYLESHEETS.equals(type))
        repFile = rep.getStyleSheet(userInfo, name);
      else if (ResourceNavConsts.EMAIL_TEMPLATES.equals(type))
        repFile = rep.getEmailTemplate(userInfo, name);
      else if (ResourceNavConsts.PRINT_TEMPLATES.equals(type))
        repFile = rep.getPrintTemplate(userInfo, name);
      else if (ResourceNavConsts.PUBLIC_FILES.equals(type))
        repFile = rep.getWebFile(userInfo, name);
      else if (ResourceNavConsts.LOGS.equals(type))
        repFile = rep.getLogFile(userInfo, name);

      if (repFile == null || !repFile.exists()) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      } else {
        response.setHeader("Content-Disposition","attachment;filename=" + name);
        OutputStream out = response.getOutputStream();
        response.setContentLength(repFile.getSize());
        repFile.writeToStream(out);
        out.flush();
        out.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
    
    Logger.trace("Memory after: "+Runtime.getRuntime().freeMemory()+" ("+Runtime.getRuntime().totalMemory()+")");
  }

}

