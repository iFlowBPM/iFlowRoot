package pt.iknow.floweditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface is a simplification of java servlets.
 * 
 * @author oscar
 *
 */
public interface BlockHandler {
  /**
   * Context prefix handled by this handler.<br>
   * This is equiv to a servlet mapping like getContext()+"*"<br>
   * <br>
   * Please note that if getContext() returns "/" will handle all requests!!
   *
   * @return
   */
  public String getContext();
  
  /**
   * Handle the request itself, just like HttpServlet.service()
   * 
   * 
   * @param request
   * @param response
   */
  public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception ;
}
