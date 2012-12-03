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
    String url = "http://" + Const.APP_HOST + ":" + Const.APP_PORT + Const.APP_URL_PREFIX + "Admin/Organization/license.jsp";
    ServletUtils.sendEncodeRedirect(response, url);
  }
}
