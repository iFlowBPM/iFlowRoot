package pt.iflow.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.UserManager;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
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
* name="ChangePassword"
* 
* @web.servlet-mapping
* url-pattern="/changePassword"
*/
public class ChangePassword extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -1579673247655966606L;

  public ChangePassword() {}
  
  public void init() throws ServletException {
    super.init();
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.getRequestDispatcher("/ResetPass/change.jsp").forward(request, response);
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // verificar os botoes
    String cancelButton = request.getParameter("cancel");
    if(null != cancelButton && cancelButton.length()>0) {
      request.getSession().invalidate();
      Logger.info(null, this, "doPost", "Cancel button detected. Redirecting to login.");
      ServletUtils.sendEncodeRedirect(response, "login.jsp");
      return;
    }
    
    UserInfoInterface ui = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
    IMessages msg = ui.getMessages();

    String userName = ui.getUtilizador();
    String oldPassword = request.getParameter("oldpassword");
    String password = request.getParameter("password");
    String repeatPass = request.getParameter("repeatpass");
    
    if(StringUtils.isEmpty(password)||StringUtils.isEmpty(repeatPass)) {
      request.setAttribute("error_msg", msg.getString("changePassword.error.invalidPassword"));
      request.getRequestDispatcher("/ResetPass/change.jsp").forward(request, response);
      return;
    }
    if(!StringUtils.equals(password, repeatPass)) {
      request.setAttribute("error_msg", msg.getString("changePassword.error.differentPassword"));
      request.getRequestDispatcher("/ResetPass/change.jsp").forward(request, response);
      return;
    }

    if (Const.PASSWORD_FORMAT!=null &&!password.matches(Const.PASSWORD_FORMAT)) {
      request.setAttribute("error_msg", msg.getString("changePassword.error.weakPassword"));
      request.getRequestDispatcher("/ResetPass/change.jsp").forward(request, response);
      return;
    }
    
    int success = BeanFactory.getUserManagerBean().changePassword(userName, oldPassword, password);
    
    if(success != UserManager.ERR_OK) {
      request.setAttribute("error_msg", msg.getString("changePassword.error.invalidPassword"));
      request.getRequestDispatcher("/ResetPass/change.jsp").forward(request, response);
      return;
    }
    
    if(ui.getUserSettings().isDefault() && Const.USE_INDIVIDUAL_LOCALE && Const.ASK_LOCALE_AT_LOGIN) {
      ServletUtils.sendEncodeRedirect(response, "setupUser");
      return;
    }

    ServletUtils.sendEncodeRedirect(response, "main.jsp");
  }

}
