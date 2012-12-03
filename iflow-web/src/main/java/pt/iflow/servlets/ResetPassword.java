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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.msg.Messages;

/**
* 
* <p>Title: </p>
* <p>Description: </p>
* <p>Copyright (c) 2005 iKnow</p>
* 
* @author iKnow
* 
* @web.servlet
* name="ResetPassword"
* 
* @web.servlet-mapping
* url-pattern="/resetPassword"
*/
public class ResetPassword extends HttpServlet {

  private static final long serialVersionUID = 162982537899066520L;

  public ResetPassword() {}
  
  public void init() throws ServletException {
    super.init();
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if(Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) {
      request.getSession().invalidate();
      Logger.info(null, this, "doGet", "Password reset disabled in LOCAL installation mode");
      ServletUtils.sendEncodeRedirect(response, "login.jsp");
      return;
    }
    
    request.getRequestDispatcher("/ResetPass/askUser.jsp").forward(request, response);
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if(Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) {
      request.getSession().invalidate();
      Logger.info(null, this, "doPost", "Password reset disabled in LOCAL installation mode");
      ServletUtils.sendEncodeRedirect(response, "login.jsp");
      return;
    }
    
    // verificar os botoes
    String cancelButton = request.getParameter("cancel");
    if(null != cancelButton && cancelButton.length()>0) {
      request.getSession().invalidate();
      Logger.info(null, this, "doPost", "Cancel button detected. Redirecting to login.");
      ServletUtils.sendEncodeRedirect(response, "login.jsp");
      return;
    }

    String userName = request.getParameter("username");
    String challenge = request.getParameter("challenge");
    
    String kaptcha = (String) request.getSession().getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
    
    if(kaptcha == null || !kaptcha.equals(challenge)) {
      Messages msg = Messages.getInstance();
      request.setAttribute("error_msg", msg.getString("resetPassword.error.challenge"));
      request.getRequestDispatcher("/ResetPass/askUser.jsp").forward(request, response);
      return;
    }
    
    BeanFactory.getUserManagerBean().resetPassword(userName);
    request.getRequestDispatcher("/ResetPass/result.jsp").forward(request, response);
  }

}
