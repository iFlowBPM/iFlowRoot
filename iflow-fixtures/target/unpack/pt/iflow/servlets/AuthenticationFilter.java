package pt.iflow.servlets;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.servlets.AuthenticationServlet.AuthenticationResult;

/**
 * 
 * @web.filter name="AuthenticationFilter"
 * @web.filter-mapping url-pattern="/*"
 * @author oscar
 *
 */
public class AuthenticationFilter extends IFlowFilter {

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    Object o = request.getAttribute(SKIP_AUTH);
    if (!Const.AUTHENTICATION_FORM.equalsIgnoreCase(Const.AUTHENTICATION_TYPE) && o != SKIP_AUTH_TOKEN) {
      try {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        boolean mustAuthenticate = false;
        UserInfoInterface userInfo = (UserInfoInterface) httpRequest.getSession().getAttribute(Const.USER_INFO);
        Hashtable<String, String> cookies = ServletUtils.getCookies((HttpServletRequest) request);
        if(null == userInfo) {
          String [] credentials = getCredentials(httpRequest, httpResponse);
          if(null != credentials) {
            String username = credentials[0];
            String password = credentials[1];
            if(null == userInfo || !userInfo.getUtilizador().equals(username)) {
              AuthenticationResult result = AuthenticationServlet.authenticate(httpRequest, httpResponse, username, password, "");
              mustAuthenticate = !result.isAuth;
            }
          } else if (cookies.containsKey(Const.SESSION_COOKIE_USERNAME) && cookies.containsKey(Const.SESSION_COOKIE_PASSWORD)) {
            String username = cookies.get(Const.SESSION_COOKIE_USERNAME);
            String password = cookies.get(Const.SESSION_COOKIE_PASSWORD);
            AuthenticationResult result = AuthenticationServlet.authenticate(httpRequest, httpResponse, username, Utils
                .decrypt(password), "");
            mustAuthenticate = !result.isAuth;
          } else {
            mustAuthenticate = true;
          }

          if(mustAuthenticate && Const.AUTHENTICATION_HTTP.equalsIgnoreCase(Const.AUTHENTICATION_TYPE)) {
            httpRequest.getSession().invalidate();
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
