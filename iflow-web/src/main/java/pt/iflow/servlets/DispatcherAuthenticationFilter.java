package pt.iflow.servlets;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @web.filter name="DispatcherAuthenticationFilter"
 * @web.filter-mapping url-pattern="/dispatcher"
 * @author oscar
 *
 */
public class DispatcherAuthenticationFilter extends IFlowFilter {

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    request.setAttribute(SKIP_AUTH, SKIP_AUTH_TOKEN);
    chain.doFilter(request, response);
  }

}
