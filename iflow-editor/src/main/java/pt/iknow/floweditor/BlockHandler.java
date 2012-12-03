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
