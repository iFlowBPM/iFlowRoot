package pt.iflow.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.licensing.LicenseServiceFactory;
import pt.iflow.api.presentation.OrganizationTheme;
import pt.iflow.api.presentation.OrganizationThemeData;
import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.UserSettings;
import pt.iflow.api.utils.Utils;
import pt.iflow.core.PersistSession;
import pt.iflow.servlets.AuthenticationServlet.AuthenticationResult;
import pt.iflow.utils.UserInfoBIC;




public class AuthenticationBICServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	  public static final String ADMIN_SESSION_NAME = "is_admin";

	  static final long serialVersionUID = 1L;

	  /* (non-Java-doc)
	   * @see javax.servlet.http.HttpServlet#HttpServlet()
	   */
	  public AuthenticationBICServlet() {
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

	    String login = username;
	    if (login != null) {
	      login = login.trim();
	    }

	    boolean licenseOk = LicenseServiceFactory.getLicenseService().isLicenseOK();

	    AuthProfile ap = BeanFactory.getAuthProfileBean();

	    UserInfoInterface ui = null;

	    if(isSystem){
	      ui = BeanFactory.getUserInfoFactory().newSystemUserInfo();
	      ui.login(login, password);
	    }
	    else{
	      ui = new UserInfoBIC();
	      ((UserInfoBIC)ui).loginMultiAD(login, password);
	    }	    	   

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
	    return result;
	  }
	  
	  static AuthenticationResult authenticate(final HttpServletRequest request, final HttpServletResponse response, final String username, final String password, final String nextUrl, boolean isBlocked)
			  throws ServletException, IOException {
			    AuthenticationResult result = new AuthenticationResult();
			    result.nextUrl = nextUrl;    

			    HttpSession session = request.getSession();

			    Boolean bIsSystem = (Boolean) session.getAttribute(ADMIN_SESSION_NAME);
			    boolean isSystem = false;

			    if(bIsSystem != null) isSystem = bIsSystem.booleanValue();

			    String login = username;
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
			    
			    ui.login(login, password);

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
			      result.errorMsg = isBlocked ? "Utilizador bloqueado devido a ter excedido o limite de tentativas" : ui.getError();
			      session.setAttribute("login_error", result.errorMsg);
			    }
			    PersistSession ps = new PersistSession();
			    ps.getSession(ui, session);
			    return result;
			  }


	  /* (non-Java-doc)
	   * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	   */
	  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    String login = StringEscapeUtils.unescapeHtml(request.getParameter("login"));
	    String password = StringEscapeUtils.unescapeHtml(request.getParameter("password"));
	    String sDoRedirect = request.getParameter("do_redirect");
	    String nextUrl = request.getParameter("url"); 
	    //Fix for stupid ESAPI non causal bug
	    if(StringUtils.contains(nextUrl, "main.jsp") && !StringUtils.equals(nextUrl, "main.jsp"))
	    	nextUrl="main.jsp";
	    String source = request.getParameter("source");
	    String keepSession = request.getParameter("keep_session");


	    boolean doRedirect = false;

	    if(StringUtils.equals(sDoRedirect, "true")) {
	      doRedirect = true;
	      if(null == nextUrl || nextUrl.trim().length() == 0) {
	        nextUrl="main.jsp";
	      }
	    }
	    
	    //if Kaptcha is activated and invalid nullify credentials
	    Boolean isOverFailureLimit = LoginAttemptCounterController.isOverFailureLimit(getServletContext() , request, login);
	    /*String kaptcha = (String) request.getSession().getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);    
	    String challenge = request.getParameter("challenge");
	    if(isOverFailureLimit && (kaptcha == null || !kaptcha.equals(challenge))) {
	    	login=null;
	    	password=null;
	    }*/
	    
	    boolean isBlocked = LoginAttemptCounterController.isBlocked(login);      
	    
	    AuthenticationResult result = authenticate(request, response, login, password, nextUrl, isBlocked);
	    
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

	    if (result.isAuth)
	    	SynchronizerTokenController.register(getServletContext(), login);
	    else
	    	LoginAttemptCounterController.markFailedAttempt(getServletContext(), request);
	    
	    // used in ibox login
	    if(StringUtils.equals(source, "assync") && result.isAuth) {
	      ServletUtils.forward(request, response, "/javascript/encodedURLS.jsp");
	      return;
	    }

	    if(doRedirect)
	      ServletUtils.sendEncodeRedirect(response, result.nextUrl);
	  }


	  static class AuthenticationResult {
	    public String nextUrl = "main.jsp";
	    public String errorMsg = null;
	    public boolean isAuth = false;
	    public boolean isBlocked = false;
	  }
	  
	}