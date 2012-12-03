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
