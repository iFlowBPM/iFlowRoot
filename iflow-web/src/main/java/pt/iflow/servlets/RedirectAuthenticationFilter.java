package pt.iflow.servlets;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.ServletUtils;

/**
 * 
 * @web.filter name="RedirectAuthenticationFilter"
 * @web.filter-mapping url-pattern="/login.jsp"
 * @web.filter-mapping url-pattern="/logout.jsp"
 * @author oscar
 * 
 */
public class RedirectAuthenticationFilter extends IFlowFilter {
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (Const.AUTHENTICATION_HTTP.equalsIgnoreCase(Const.AUTHENTICATION_TYPE)) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpRequest.getSession().invalidate();
      sendError(httpResponse);
      ServletUtils.sendEncodeRedirect(httpResponse, Const.APP_URL_PREFIX + "main.jsp");
      return;
    }
    chain.doFilter(request, response);
  }

}
