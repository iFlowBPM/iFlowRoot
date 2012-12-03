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
package pt.iflow.api.utils;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

public class ServletUtils {

  public static void sendEncodeRedirect(HttpServletResponse response, String url) throws IOException {
    response.sendRedirect(response.encodeRedirectURL(url));
  }
  
  public static void forward(HttpServletRequest request, HttpServletResponse response, String destination) throws ServletException, IOException {
    request.getRequestDispatcher(destination).forward(request, response);
  }

  public static void encodeURL(HttpServletResponse response, String url) {
    response.encodeURL(url);
  }

  private HttpServletRequest request;
  private HttpServletResponse response;

  public ServletUtils() {
    this(null, null);
  }

  public ServletUtils(HttpServletRequest request) {
    this(request, null);
  }

  public ServletUtils(HttpServletResponse response) {
    this(null,response);
  }

  public ServletUtils(HttpServletRequest request, HttpServletResponse response) {
    this.request = request;
    this.response = response;
  }

  public HttpSession getSession() {
    if(null == this.request) return null;
    return this.request.getSession(false);
  }
  
  public String getRequestURL() {
    if(null == this.request) return null;
    Logger.debug("", this, "", this.request.getRequestURI());
    String scheme = this.request.getScheme();
    String host = this.request.getServerName();
    int port = this.request.getServerPort();
    String context = this.request.getContextPath();
    return scheme+"://"+host+":"+port+context;
  }

  public String encodeURL(String url) {
    return encodeURL((String)null, url, (String)null);
  }

  public String encodeRedirectURL(String url) {
    return encodeRedirectURL(null, url, null);
  }

  public void sendRedirectURL(String url) throws IOException {
    sendRedirectURL(null, url, null);
  }

  public String encodeURL(String base, String uri) {
    return encodeURL(base, uri, null);
  }

  public String encodeRedirectURL(String base, String uri) {
    return encodeRedirectURL(base, uri, null);
  }

  public void sendRedirectURL(String base, String uri) throws IOException {
    sendRedirectURL(base, uri, null);
  }

  public String encodeURL(String base, String uri, String params) {
    String dest = getURL(base, uri, params);
    if (null == response)
      return dest;
    return response.encodeURL(dest);
  }

  public String encodeRedirectURL(String base, String uri, String params) {
    String dest = getURL(base, uri, params);
    if (null == response)
      return dest;
    return response.encodeRedirectURL(dest);
  }

  public void sendRedirectURL(String base, String uri, String params) throws IOException {
    if (null == response)
      return;
    String url = response.encodeRedirectURL(getURL(base, uri, params));
    response.sendRedirect(url);
  }

  public static String getURL(String base, String uri, String params) {
    StringBuilder result = new StringBuilder();
    if (StringUtils.isNotEmpty(base)) {
      result.append(base);
      if(!(result.toString().endsWith("/") || result.toString().endsWith("\\"))) {
        result.append('/');        
      }
    }
    result.append(uri);
    if (StringUtils.isNotEmpty(params)) {
      result.append('?').append(params);
    }

    return result.toString();
  }

  /** this function converts an array of cookies into a hashtable */
  public static Hashtable<String,String> getCookies(HttpServletRequest request) {
    Hashtable<String,String> result = new Hashtable<String, String>();

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (int i=0; i < cookies.length; i++) {
        result.put(cookies[i].getName(), cookies[i].getValue());
      }
    }

    return result;
  }
  
  private static final int DAY = 60*60*24;
  public static Cookie newCookie(String name, String value) {
    // guardar lang actual
    Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(15*DAY);
    // cookie.setPath("/");
    return cookie;
  }
  

}
