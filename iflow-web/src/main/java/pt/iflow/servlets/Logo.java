/**
 * PublicFiles.java
 *
 * Description:
 *
 * History: 01/15/02 - jpms - created.
 * $Id: PublicFiles.java 248 2007-08-01 13:54:31 +0000 (Qua, 01 Ago 2007) uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iflow.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2005 iKnow
 * </p>
 * 
 * @author iKnow
 * 
 * @web.servlet name="Logo"
 * 
 * @web.servlet-mapping url-pattern="/Logo"
 */
public class Logo extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -9101755201777404343L;

  public Logo() {
  }

  private static byte[] defaultLogo = null;

  private static synchronized void doInit(ServletContext ctx) {
    if (defaultLogo != null)
      return;
    InputStream input = ctx.getResourceAsStream("/images/logo_iflow.png");
    if (input == null) {
      return;
    }
    
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      copyTo(input, bout);
      defaultLogo = bout.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        input.close();
      } catch (IOException e) {
      }
    }
  }

  public void init() {
    doInit(getServletContext());
  }

  private static void copyTo(InputStream in, OutputStream out) throws IOException {
    byte[] b = new byte[8092];
    int r = -1;
    while ((r = in.read(b)) != -1)
      out.write(b, 0, r);
  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    InputStream input = null;
    try {
      Repository rep = BeanFactory.getRepBean();
      int size = 0;

      UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
      if (null != userInfo) {
        RepositoryFile repFile = rep.getLogo(userInfo);
        input = repFile.getResourceAsStream();
        size = repFile.getSize();
      }

      if (input == null) {
        if (defaultLogo == null) {
          response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
          return;
        }
        input = new ByteArrayInputStream(defaultLogo);
        size = defaultLogo.length;
      }

      response.setHeader("Content-Disposition", "inline;filename=logo.png");
      OutputStream out = response.getOutputStream();
      response.setContentLength(size);
      copyTo(input, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    } finally {
      if (null != input) {
        try {
          input.close();
        } catch (IOException e) {
        }
      }
    }
  }

}
