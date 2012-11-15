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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.licensing.LicenseServiceFactory;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormFile;
import pt.iknow.utils.html.FormUtils;

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
 * @web.servlet name="UpdateLicense"
 * 
 * @web.servlet-mapping url-pattern="/updateLicense"
 */
public class UpdateLicense extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -9101755201667404343L;

  public UpdateLicense() {
  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
    if (null == userInfo || !userInfo.isSysAdmin()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Operation not permitted");
      return;
    }

    FormFile file = null;
    String actionAdmin = null;

    try {
      FormData formData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE,
          Const.fUPLOAD_TEMP_DIR);
      
      actionAdmin = formData.getParameter("admin");
      if(StringUtils.isNotEmpty(actionAdmin)) {
        ServletUtils.sendEncodeRedirect(response, "main.jsp");
        return;
      }

      file = formData.getFileParameter("licfile");
      
    } catch (Exception e) {
      e.printStackTrace();
      Logger.error(userInfo.getUtilizador(), this, "service", "Error parsing request");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      return;
    }
    
    if(file != null) {
      Logger.warning(userInfo.getUtilizador(), this, "service", "Setting new license file...");
      LicenseServiceFactory.getLicenseService().load(userInfo, file.getData());
    }
    
    ServletUtils.sendEncodeRedirect(response, "Admin/licenseValidation.jsp");
    return;
  }

}
