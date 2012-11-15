package pt.iflow.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @web.filter name="EncodingFilter"
 * @web.filter-mapping url-pattern="/*"
 * @author oscar
 *
 */
public class EncodingFilter implements Filter {

  private List<String> excludes = new ArrayList<String>();
  
  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if(!excludeUrl(request)) {
      request.setCharacterEncoding("UTF-8");
      response.setCharacterEncoding("UTF-8");
    }
    chain.doFilter(request, response);
  }

  public void init(FilterConfig config) throws ServletException {
    String excludeStr = config.getInitParameter("excludes");
    if(StringUtils.isNotBlank(excludeStr)) {
      String [] alExcludes = excludeStr.split(",");
      for(String s : alExcludes) {
        if(StringUtils.isNotBlank(s))
          excludes.add(s.trim());
      }
    }
  }

  
  private boolean excludeUrl(ServletRequest request) {
    String resource = ((HttpServletRequest)request).getServletPath();
    for(String patt : excludes) {
      if(patt.equals(resource))
        return true;
    }
    return false;
  }
}
