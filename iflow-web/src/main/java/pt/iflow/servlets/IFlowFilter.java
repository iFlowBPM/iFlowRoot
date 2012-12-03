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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.Base64;

public abstract class IFlowFilter implements Filter {

  public static final String SKIP_AUTH = "skip_auth";
  public static final String SKIP_AUTH_TOKEN = "skip_auth";

  private static final String AUTHENTICATION_METHOD="basic ";
  private static final char USERNAME_SEPARATOR=':';
  
  public void destroy() {
  }

  public abstract void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException;

  public void init(FilterConfig arg0) throws ServletException {
  }

  protected String[] getCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String authHeader = request.getHeader("Authorization");
    if (StringUtils.isNotEmpty(authHeader) && authHeader.toLowerCase().startsWith(AUTHENTICATION_METHOD)) {
      String credentials = authHeader.substring(AUTHENTICATION_METHOD.length());
      String decodedCredentials = new String(Base64.decode(credentials));
      int pos = decodedCredentials.indexOf(USERNAME_SEPARATOR);
      String username = decodedCredentials.substring(0, pos);
      String password = decodedCredentials.substring(pos + 1);
      return new String[] { username, password };
    }

    return null;
  }

  protected void sendError(HttpServletResponse response) throws IOException {
    response.setHeader("WWW-Authenticate", "Basic realm=\"iFlow\"");
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
