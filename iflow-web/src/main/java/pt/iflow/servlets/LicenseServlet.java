package pt.iflow.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.licensing.LicenseService;
import pt.iflow.api.licensing.LicenseServiceFactory;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.ServletUtilsRoutesEnum;
import pt.iflow.api.utils.UserInfoInterface;

public class LicenseServlet extends HttpServlet implements Servlet {
  private static final long serialVersionUID = -3473993017625529486L;

  public static final String LOCATION = "license";
  public static final String METHOD_CHARGE = "charge";

  public static final String PARAM_VOUCHER = "voucher";

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
    String method = request.getPathInfo();
    if (method == null) {
      method = "";
    } else if (method.indexOf("/") == 0) {
      method = method.substring(1);
    }
    if (StringUtils.equals(method, METHOD_CHARGE)) {
      String licData = request.getParameter(PARAM_VOUCHER);
      LicenseService licensing = LicenseServiceFactory.getLicenseService();
      licensing.load(userInfo, licData);
    } else {
      Logger.warning(userInfo.getUtilizador(), this, "service", "Unknown method invocation: " + method);
    }
       
    ServletUtils.sendEncodeRedirect(response, ServletUtilsRoutesEnum.LICENSESERVLET ,null);
  }
}
