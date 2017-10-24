package pt.iflow.authentication;
import org.alfresco.webservice.accesscontrol.HasPermissionsResult;
import org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.lowagie.text.html.HtmlEncoder;

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.licensing.LicenseServiceFactory;
import pt.iflow.api.presentation.OrganizationTheme;
import pt.iflow.api.presentation.OrganizationThemeData;
import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.ServletUtilsRoutesEnum;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.UserSettings;
import pt.iflow.api.utils.Utils;
import pt.iflow.core.PersistSession;
import pt.iflow.servlets.SimpleSessionHelper;


/**
 * Servlet implementation class for Servlet: AuthenticationServlet
 *
 * @web.servlet name="AuthenticationServlet"
 * 
 * @web.servlet-mapping url-pattern="/AuthenticationServlet"
 */
public class AuthWinUserServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

  public static final String ADMIN_SESSION_NAME = "is_admin";

  static final long serialVersionUID = 1L;

  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#HttpServlet()
   */
  public AuthWinUserServlet() {
    super();
  }   	

  static AuthenticationResult authenticate(final HttpServletRequest request, final HttpServletResponse response, final String username, final String password, final String nextUrl)
  throws ServletException, IOException {
    AuthenticationResult result = new AuthenticationResult();
    result.nextUrl = nextUrl;

    HttpSession session = request.getSession();

    Boolean bIsSystem = (Boolean) session.getAttribute(ADMIN_SESSION_NAME);
    boolean isSystem = false;

    if(bIsSystem != null) isSystem = bIsSystem.booleanValue();

    String login = Utils.decrypt(username);
    String pass = Utils.decrypt(password);

    if (login != null) {
      login = login.trim();
    }

    boolean licenseOk = LicenseServiceFactory.getLicenseService().isLicenseOK();

    AuthProfile ap = BeanFactory.getAuthProfileBean();

    UserInfoInterface ui = null;

    if(isSystem)
      ui = BeanFactory.getUserInfoFactory().newSystemUserInfo();
    else
      ui = BeanFactory.getUserInfoFactory().newUserInfo();

    
    Hashtable<String,String> cookies = ServletUtils.getCookies(request);
    if (cookies != null) {
      ui.setCookieLang(cookies.get(Const.LANG_COOKIE));
    }
    
    ui.login(login, pass);

    // check license status
    if(!licenseOk && !isSystem) {
      result.nextUrl = "Admin/login.jsp";
      session.invalidate();
      return result;
    }

    boolean isAuth = result.isAuth = ui.isLogged();

    if (isAuth) {

      /////////////////////////////
      //
      // Now set some session vars
      //
      /////////////////////////////

      //Application Data
      session.setAttribute("login",login);

      session.setAttribute(Const.USER_INFO, ui);
      UserSettings settings = ui.getUserSettings();
      OrganizationData orgData = ap.getOrganizationInfo(ui.getOrganization());
      session.setAttribute(Const.ORG_INFO,orgData);


      OrganizationTheme orgTheme = BeanFactory.getOrganizationThemeBean();
      if (orgTheme != null) {
        OrganizationThemeData themeData = orgTheme.getOrganizationTheme(ui);
        session.setAttribute("themedata",themeData);    
      }


      if(ui.isPasswordExpired()) {
        result.nextUrl = "changePassword";
      }

      if(!isSystem && settings.isDefault() && Const.USE_INDIVIDUAL_LOCALE && Const.ASK_LOCALE_AT_LOGIN) { 
        result.nextUrl = "setupUser";
      }

      // check license status
      if(!licenseOk && isSystem) {
        result.nextUrl = "Admin/licenseValidation.jsp";
      }

      session.setAttribute("SessionHelperToken", new SimpleSessionHelper());

    } else {
      result.nextUrl = "main.jsp";
      result.errorMsg = ui.getError();
      session.setAttribute("login_error", result.errorMsg);
    }
    PersistSession ps = new PersistSession();
    ps.getSession(ui, session);
    
    response.sendRedirect(response.encodeRedirectURL("main.jsp"));
    
    return result;
  }


  /* (non-Java-doc)
   * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   
    String login = request.getParameter("login");
    String password = request.getParameter("password");
    String sDoRedirect = request.getParameter("do_redirect");
    String nextUrl = request.getParameter("url"); 
    String source = request.getParameter("source");
    String keepSession = request.getParameter("keep_session");


    boolean doRedirect = false;

    if(StringUtils.equals(sDoRedirect, "true")) {
      doRedirect = true;
      if(null == nextUrl || nextUrl.trim().length() == 0) {
        nextUrl="main.jsp";
      }
    }

    AuthenticationResult result = authenticate(request, response, login, password, nextUrl);

    // keep session in cookie
    Cookie sessionUsername = ServletUtils.newCookie(Const.SESSION_COOKIE_USERNAME, "");
    Cookie sessionPassword = ServletUtils.newCookie(Const.SESSION_COOKIE_PASSWORD, "");
    response.addCookie(sessionUsername);
    response.addCookie(sessionPassword);

    if (result.isAuth && StringUtils.equals(keepSession, "on")) {
      sessionUsername = ServletUtils.newCookie(Const.SESSION_COOKIE_USERNAME, login);
      sessionPassword = ServletUtils.newCookie(Const.SESSION_COOKIE_PASSWORD, Utils.encrypt(password));
      response.addCookie(sessionUsername);
      response.addCookie(sessionPassword);
    }

    // used in ibox login
    if(StringUtils.equals(source, "assync") && result.isAuth) {
      ServletUtils.forward(request, response, "/javascript/encodedURLS.jsp");
      return;
    }
    
       
    
    if(doRedirect)
    	ServletUtils.sendEncodeRedirect(response, ServletUtilsRoutesEnum.MAIN, null);
     
  }


  static class AuthenticationResult {
    public String nextUrl = "main.jsp";
    public String errorMsg = null;
    public boolean isAuth = false;
  }
}