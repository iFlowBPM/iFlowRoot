package pt.iflow.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.UserCredentials;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 * 
 * @web.servlet
 * name="ConfirmAccount"
 * 
 * @web.servlet-mapping
 * url-pattern="/confirm"
 */
public class ConfirmAccount extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 4781792362541777645L;

  public ConfirmAccount() { }

  public void init() { }

  private boolean isEmpty(String var) {
    return null == var || "".equals(var.trim());
  }
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    // Check if a button was clicked
    String continueButton = request.getParameter("continue");
    if(null != continueButton && continueButton.length()>0) {
      String activation = request.getParameter("activation");

      if(null != activation && "invite".equals(activation)) {
        Logger.info(null, this, "doPost", "Continue button detected. Redirecting to personal acount page.");
        ServletUtils.sendEncodeRedirect(response, "GoTo?" + PageMapper.PARAM_GOTO + "=personal_account.jsp");
      } else {
        Logger.info(null, this, "doPost", "Continue button detected. Redirecting to main page.");
        ServletUtils.sendEncodeRedirect(response, "main.jsp");
      }
      return;
    }
    String cancelButton = request.getParameter("cancel");
    if(null != cancelButton && cancelButton.length()>0) {
      request.getSession().invalidate();
      Logger.info(null, this, "doPost", "Cancel button detected. Redirecting to login.");
      ServletUtils.sendEncodeRedirect(response, "login.jsp");
      return;
    }


    String activationCode = request.getParameter("code");
    UserCredentials user = null;
    if(!isEmpty(activationCode)) {
      user = BeanFactory.getUserManagerBean().confirmAccount(activationCode);
    }

    if(user!=null) {
      AuthenticationServlet.authenticate(request, response, user.getUsername(), user.getPassword(), "");
      request.getRequestDispatcher("/Register/activated.jsp").forward(request, response);
    } else {
      request.getRequestDispatcher("/Register/notactivated.jsp").forward(request, response);
    }
  }

}
