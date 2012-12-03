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
/*
 *
 * Created on Oct 7, 2005 by mach
 *
  */

package pt.iflow.sidebar;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 * 
 * @web.servlet
* name="SidebarServlet"
* 
* @web.servlet-mapping
* url-pattern="/SidebarServlet"
 */

public class SidebarServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static SidebarGenerator _dataGenerator;
 
  static {
    try {
      _dataGenerator = (SidebarGenerator) Class.forName(Const.SIDEBAR_GEN_IMPLEMENTATION).newInstance();
    }
    catch (Exception e) {
      Logger.error(null,null,"initialization","Unable to instatiate sidebar data generator " + 
                    Const.SIDEBAR_GEN_IMPLEMENTATION + "  : " + e.getMessage());
      _dataGenerator = null;
    }
  }
  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //String baseURL = Const.FEED_URL;
    
    if (_dataGenerator == null) {
      return;
    }
    
    HttpSession session = request.getSession(true);
    PrintWriter out =response.getWriter();
    
    String todo = (String) request.getParameter("todo");
    String key = (String) request.getParameter("key");
    
    if(todo.equals("login")) {
      AuthProfile ap      = BeanFactory.getAuthProfileBean();
      UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
      OrganizationData orgData = (OrganizationData) session.getAttribute(Const.ORG_INFO);

      if(userInfo == null || orgData == null) {
         String loginData = Utils.decrypt(key);
         
         String userName = loginData.substring(0, loginData.indexOf("#"));
         String userPassword = loginData.substring(
             loginData.indexOf("#") + 1, loginData.length());

         userInfo = BeanFactory.getUserInfoFactory().newUserInfo();
         userInfo.login(userName, userPassword);
         
         session.setAttribute("login",userName);
         
         orgData = ap.getOrganizationInfo(userInfo.getOrganization());
         
         session.setAttribute(Const.ORG_INFO,orgData);
         session.setAttribute(Const.USER_INFO,userInfo);
         out.print(session.getId());
         
      } else {
        out.print(session.getId());
      }
      
    } else if (todo.equals("data")) {
      UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
      if(userInfo == null) {
        return;
      }
      out.print(_dataGenerator.generateFlowData(userInfo));

    }else {
      return;
    }

  }

}
