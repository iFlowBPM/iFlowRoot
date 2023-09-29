package pt.iflow.servlets;

import java.io.IOException;
import java.util.Hashtable;

import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.notification.Email;
import pt.iflow.api.notification.EmailManager;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.Utils;
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
//    if(Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) {
//      request.getSession().invalidate();
//      Logger.info(null, this, "doGet", "Password reset disabled in LOCAL installation mode");
//      ServletUtils.sendEncodeRedirect(response, "login.jsp");
//      return;
//    }
    
    request.getRequestDispatcher("/ResetPass/askUser.jsp").forward(request, response);
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//    if(Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) {
//      request.getSession().invalidate();
//      Logger.info(null, this, "doPost", "Password reset disabled in LOCAL installation mode");
//      ServletUtils.sendEncodeRedirect(response, "login.jsp");
//      return;
//    }
    
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
    String pageName = request.getParameter("pagename");
        
    if ("challenge".equals(pageName)) {
    	String code = (String)request.getSession().getAttribute("challengeGenCode");
    	if (code != null) code = Utils.decrypt(code);
    	if (code != null && code.contentEquals(challenge) ) {
    		BeanFactory.getUserManagerBean().resetPassword(userName);
            request.getRequestDispatcher("/ResetPass/result.jsp").forward(request, response);    	
    	}
    	else {
          Messages msg = Messages.getInstance();
          request.setAttribute("error_msg", msg.getString("resetPassword.error.challenge"));
          request.getRequestDispatcher("/ResetPass/challenge.jsp").forward(request, response);
          return;    		
    	}
    } else {
    	String code =  RandomStringUtils.random(8, true, true); 	
    	BeanFactory.getUserManagerBean().resetPasswordSendCode(userName, code);
    	request.getSession().setAttribute("challengeGenCode", Utils.encrypt(code));
    	// TODO: enviar código por email
        request.getRequestDispatcher("/ResetPass/challenge.jsp?username="+ userName).forward(request, response);    	    
      return;    	
    }
  }

}
