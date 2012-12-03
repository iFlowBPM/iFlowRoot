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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.servlets.AuthenticationServlet.AuthenticationResult;

/**
 * 
 * @web.filter name="SysAdmAuthenticationFilter"
 * @web.filter-mapping url-pattern="/Admin/login.jsp"
 * @author oscar
 *
 */
public class SysAdmAuthenticationFilter extends IFlowFilter {

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    Object o = request.getAttribute(SKIP_AUTH);
    if(!Const.AUTHENTICATION_FORM.equalsIgnoreCase(Const.AUTHENTICATION_TYPE) && o != SKIP_AUTH_TOKEN) {
      try {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();
        UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
        if(null == userInfo) {
          boolean mustAuthenticate = true;
          String [] credentials = getCredentials(httpRequest, httpResponse);
          if(null != credentials) {
            String username = credentials[0];
            String password = credentials[1];
            if(null == userInfo || !userInfo.getUtilizador().equals(username)) {
              session.setAttribute(pt.iflow.servlets.AuthenticationServlet.ADMIN_SESSION_NAME, Boolean.TRUE);
              AuthenticationResult result = AuthenticationServlet.authenticate(httpRequest, httpResponse, username, password, "");
              mustAuthenticate = !result.isAuth;
              if(result.isAuth) {
                ServletUtils.sendEncodeRedirect(httpResponse, Const.APP_URL_PREFIX+"main.jsp");
                return;
              }
            }
          } else {
            mustAuthenticate = true;
          }

          if(mustAuthenticate && Const.AUTHENTICATION_HTTP.equalsIgnoreCase(Const.AUTHENTICATION_TYPE)) {
            session.invalidate();
            sendError(httpResponse);
            return;
          }
        }
      } catch (Throwable t) {
      }
    }
    chain.doFilter(request, response);
  }

}
