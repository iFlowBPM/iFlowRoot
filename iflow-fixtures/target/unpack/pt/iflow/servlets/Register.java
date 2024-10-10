package pt.iflow.servlets;

import java.io.IOException;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.UserManager;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.ServletUtilsRoutesEnum;
import pt.iflow.api.utils.UserInfoInterface;
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
* name="Register"
* 
* @web.servlet-mapping
* url-pattern="/register"
*/
public class Register extends HttpServlet {
  
  /**
   * 
   */
  private static final long serialVersionUID = 4781792362541777645L;

  public Register() { }
  
  public void init() { }
 
  private static final String [] SESSION_PROPERTIES = new String[]{
    "organization_name",
    "organization_desc",
    "organization_lang",
    "organization_timezone",
    "username",
    "gender",
    "emailAddress",
    "firstName",
    "lastName",
    "phoneNumber",
    "faxNumber",
    "mobileNumber",
    "companyPhone",
    "isSystemAdm",
  };
  
  
  //private static final String LOGIN_URI = "login.jsp";
  private static final String ORG_URI = "/Register/neworg.jsp";
  private static final String USER_URI = "/Register/newuser.jsp";
  private static final String ERROR_URI = "/Register/error.jsp";
  private static final String SUCCESS_URI = "/Register/success.jsp";
  private static final String ORG_ADM_URI = "/Admin/UserManagement/organizationadm.jsp"; // used by sys adm
  
  private boolean isBlank(String var) {
    return null == var || "".equals(var.trim());
  }
  
  private void clearSession(HttpSession session, boolean isSystemAdmin) {
    if(null == session) return;
    if(isSystemAdmin) {
      for (String property : SESSION_PROPERTIES) {
        session.removeAttribute(property);
      }
    } else {
      session.invalidate();
    }
  }
  
  private String orgAction(boolean isSystemAdmin, HttpServletRequest request) {
    HttpSession session = request.getSession();
    
    String next = USER_URI;
    String orgName = request.getParameter("organization_name");
    String orgDesc = request.getParameter("organization_desc");
    String orgLang = request.getParameter("organization_lang");
    String orgTimeZone = request.getParameter("organization_timezone");
    session.setAttribute("organization_name", orgName);
    session.setAttribute("organization_desc", orgDesc);
    session.setAttribute("organization_lang", orgLang);
    session.setAttribute("organization_timezone", orgTimeZone);

    IMessages msg = Messages.getInstance(orgLang);
    if(isSystemAdmin) {
      UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
      msg = userInfo.getMessages();
    }
    request.setAttribute("organization_desc", orgDesc);
    request.setAttribute("organization_name", orgName);
    request.setAttribute("organization_lang", orgLang);
    request.setAttribute("organization_timezone", orgTimeZone);

    if(isBlank(orgName)) {  // org name is mandatory
      request.setAttribute("error_msg", msg.getString("register.error.invalid_org"));
      next = ORG_URI;
    } else if(BeanFactory.getUserManagerBean().organizationExists(orgName)) {
      request.setAttribute("error_msg", msg.getString("register.error.org_exists", orgName));
      next = ORG_URI;
    }
    
    return next;
  }
  
  private String userAction(boolean isSystemAdmin, HttpServletRequest request) {
    String sErrorMsg = null;
    HttpSession session = request.getSession();

    String username = request.getParameter("username");
    String password = request.getParameter("pass");
    String repeatpass = request.getParameter("repeatpass");
    String gender = request.getParameter("gender");
    String emailAddress = StringEscapeUtils.unescapeHtml(request.getParameter("emailAddress"));;
    String firstName = request.getParameter("firstName");
    String lastName = request.getParameter("lastName");
    String phoneNumber = request.getParameter("phoneNumber");
    String faxNumber = request.getParameter("faxNumber");
    String mobileNumber = request.getParameter("mobileNumber");
    String companyPhone = request.getParameter("companyPhone");

    String backButton = request.getParameter("back");
    if(null != backButton && backButton.length()>0) {
      session.setAttribute("username", username);
      session.setAttribute("gender", gender);
      session.setAttribute("emailAddress", emailAddress);
      session.setAttribute("firstName", firstName);
      session.setAttribute("lastName", lastName);
      session.setAttribute("phoneNumber", phoneNumber);
      session.setAttribute("faxNumber", faxNumber);
      session.setAttribute("mobileNumber", mobileNumber);
      session.setAttribute("companyPhone", companyPhone);
      return ORG_URI;
    }

    request.setAttribute("username", username);
    request.setAttribute("gender", gender);
    request.setAttribute("emailAddress", emailAddress);
    request.setAttribute("firstName", firstName);
    request.setAttribute("lastName", lastName);
    request.setAttribute("phoneNumber", phoneNumber);
    request.setAttribute("faxNumber", faxNumber);
    request.setAttribute("mobileNumber", mobileNumber);
    request.setAttribute("companyPhone", companyPhone);

    String next = SUCCESS_URI;
    String orgName = (String) session.getAttribute("organization_name");
    String orgDescription = (String) session.getAttribute("organization_desc");
    String orgLang = (String) session.getAttribute("organization_lang");
    String orgTimeZone = (String) session.getAttribute("organization_timezone");
    
    IMessages msg = Messages.getInstance(orgLang);
    if(isSystemAdmin) {
      UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
      msg = userInfo.getMessages();
    }
    
    if(isBlank(orgName)) {
      request.setAttribute("organization_name", orgName);
      request.setAttribute("organization_desc", orgDescription);
      request.setAttribute("organization_lang", orgLang);
      request.setAttribute("organization_timezone", orgTimeZone);
      return ORG_URI;
    }

    if(isBlank(username) || isBlank(firstName) || isBlank(lastName)) {
      request.setAttribute("error_msg", msg.getString("register.error.empty_fields"));
      return USER_URI;
    }
    
    if(Const.bUSE_EMAIL && isBlank(emailAddress)) {
      request.setAttribute("error_msg", msg.getString("register.error.empty_fields"));
      return USER_URI;
    }
    
    if(!isSystemAdmin) { // System admin dont need captcha
      String kaptcha = (String) request.getSession().getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
      
      String challenge = request.getParameter("challenge");
      if(kaptcha == null || !kaptcha.equals(challenge)) {
        request.setAttribute("error_msg", msg.getString("register.error.challenge"));
        return USER_URI;
      }
    }

    try {
      if(Const.bUSE_EMAIL && isSystemAdmin) {
        password = repeatpass = RandomStringUtils.random(8, true, true);
      }

      if (!isBlank(password) && !isBlank(repeatpass) && password.equals(repeatpass)) {
        UserManager manager = BeanFactory.getUserManagerBean();

        int success = manager.newRegistration(orgName, orgDescription, username, password, gender, emailAddress, firstName, lastName, phoneNumber, faxNumber, mobileNumber, companyPhone, orgLang, orgTimeZone);
        switch(success) {
        case UserManager.ERR_OK:
          if(isSystemAdmin) {
            next = ORG_ADM_URI;
            clearSession(session, isSystemAdmin);
            request.setAttribute("infoMsg", msg.getString("register.success.adm", orgName, orgDescription));
          } else {
            next = SUCCESS_URI;
          }
          break;
        case UserManager.ERR_ORGANIZATION_EXISTS:
          next = ORG_URI;
          sErrorMsg = msg.getString("register.error.org_exists", orgName);
          break;
        case UserManager.ERR_USER_EXISTS:
          next = USER_URI;
          sErrorMsg = msg.getString("register.error.user_exists", username);
          break;
        case UserManager.ERR_EMAIL_EXISTS:
          next = USER_URI;
          sErrorMsg = msg.getString("register.error.email_exists", orgName);
          break;
        case UserManager.ERR_EMAIL:
          next = ERROR_URI;
          sErrorMsg = msg.getString("register.error.email_not_sent", orgName);
          break;
        case UserManager.ERR_PASSWORD:
          next = USER_URI;
          sErrorMsg = msg.getString("register.error.invalid_pass", orgName);
          break;
        case UserManager.ERR_INVALID_EMAIL:
          next = USER_URI;
          sErrorMsg = msg.getString("register.error.invalid_email", orgName);
          break;
        default:
          next = ERROR_URI;
        sErrorMsg = msg.getString("register.error.internal");
        break;
        }
      } else {
        next = USER_URI;
        sErrorMsg = msg.getString("register.error.pass_not_match");
      }
    }
    catch (Exception e) {
      next = USER_URI;
      sErrorMsg = msg.getString("register.error.internal");
      e.printStackTrace();
      Logger.warning(username, this, "userAction", "Error registering a new user");
    }

    request.setAttribute("error_msg", sErrorMsg);
    return next;
  }
  
  
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();// create a session if does not exists
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    
    boolean isSystemAdmin = (null != userInfo && userInfo.isSysAdmin());
    if(Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE) && !isSystemAdmin ) {
      clearSession(session, isSystemAdmin);
      Logger.info(null, this, "doGet", "Registration disabled in LOCAL installation mode");
      ServletUtils.sendEncodeRedirect(response, ServletUtilsRoutesEnum.LOGIN, null);
      return;
    }

    
    request.setAttribute("isSystemAdmin", isSystemAdmin);

    // first time setup get the cookie lang
    java.util.Hashtable<String,String> cookies = pt.iflow.api.utils.ServletUtils.getCookies(request);
    request.setAttribute("organization_lang", cookies.get(pt.iflow.api.utils.Const.LANG_COOKIE));
    request.setAttribute("organization_timezone", TimeZone.getDefault().getID());

    clearSession(session, isSystemAdmin);
    request.getRequestDispatcher(ORG_URI).forward(request, response);
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    boolean isSystemAdmin = (null != userInfo && userInfo.isSysAdmin());
    
    if(Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE) && !isSystemAdmin ) {
      clearSession(session, isSystemAdmin);
      Logger.info(null, this, "doPost", "Registration disabled in LOCAL installation mode");
      ServletUtils.sendEncodeRedirect(response, ServletUtilsRoutesEnum.LOGIN, null);
      return;
    }
    
    request.setAttribute("isSystemAdmin", isSystemAdmin);
    
    String next = ORG_URI;
    String step = request.getParameter("step");

    if(null == step) step = "start";
    
    // verificar os botoes
    String doneButton = request.getParameter("done");
    if(StringUtils.isNotEmpty(doneButton)) {
      clearSession(session, isSystemAdmin);
      if(isSystemAdmin) {
        Logger.info(null, this, "doPost", "System admin done it all! redirecting");
        request.getRequestDispatcher(ORG_ADM_URI).forward(request, response);
      } else {
        Logger.info(null, this, "doPost", "Done button detected. Redirecting to login.");
        ServletUtils.sendEncodeRedirect(response, ServletUtilsRoutesEnum.LOGIN, null);
      }
      return;
    }
    
    String cancelButton = request.getParameter("cancel");
    if(StringUtils.isNotEmpty(cancelButton)) {
      clearSession(session, isSystemAdmin);
      if(isSystemAdmin) {
        Logger.info(null, this, "doPost", "System admin cancel...redirecting");
        request.getRequestDispatcher(ORG_ADM_URI).forward(request, response);
      } else {
        Logger.info(null, this, "doPost", "Cancel button detected. Redirecting to login.");
        ServletUtils.sendEncodeRedirect(response, ServletUtilsRoutesEnum.LOGIN, null);
      }
      return;
    }

    if("org".equals(step)) {
      next = orgAction(isSystemAdmin, request);
    } else if("user".equals(step)) {
      next = userAction(isSystemAdmin, request);
    } else {
      clearSession(session, isSystemAdmin);
    }
    
    request.getRequestDispatcher(next).include(request, response);
  }

}
