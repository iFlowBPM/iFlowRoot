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
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

/**
* 
* <p>Title: </p>
* <p>Description: </p>
* <p>Copyright (c) 2005 iKnow</p>
* 
* @author iKnow
* 
* @web.servlet
* name="SetupUser"
* 
* @web.servlet-mapping
* url-pattern="/setupUser"
*/
public class SetupUser extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -157967324765348706L;

  public SetupUser() {}
  
  public void init() throws ServletException {
    super.init();
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
    request.setAttribute("bEditTimezone",true);
    request.setAttribute("userLang",userInfo.getUserSettings().getLangString());
    Hashtable<String,String> cookies = pt.iflow.api.utils.ServletUtils.getCookies(request);
    String cookieLang = cookies.get(Const.LANG_COOKIE);
    if(userInfo.getUserSettings().isDefault() && StringUtils.isNotEmpty(cookieLang))
      request.setAttribute("userLang",cookieLang);
      
    request.setAttribute("userTimeZone",userInfo.getUserSettings().getTimeZoneID());
    

    request.getRequestDispatcher("/setupUser.jsp").forward(request, response);
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    String cancel = request.getParameter("cancel");
    if(StringUtils.isNotEmpty(cancel)) { // cancel pressed
      ServletUtils.sendEncodeRedirect(response, "logout.jsp");
      return;
    }
    
    
    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);

    String locale = request.getParameter("locale");
    String timezone = request.getParameter("timezone");
    
    String [] toks = locale.split("_");
    String lang = toks[0];
    String region = toks[1];

    BeanFactory.getSettingsBean().updateUserSettings(userInfo, lang, region, timezone);
    
    userInfo.reloadUserSettings();
    
    ServletUtils.sendEncodeRedirect(response, "main.jsp");
  }

}
