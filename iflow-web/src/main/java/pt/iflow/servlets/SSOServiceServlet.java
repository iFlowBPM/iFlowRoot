package pt.iflow.servlets;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.presentation.OrganizationTheme;
import pt.iflow.api.presentation.OrganizationThemeData;
import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.UserSettings;
import pt.iflow.applet.StringUtils;
import pt.iflow.core.PersistSession;
import pt.iflow.saml.onelogin.AccountSettings;
import pt.iflow.saml.onelogin.saml.AuthRequest;
import pt.iflow.saml.onelogin.saml.Response;
import pt.iflow.servlets.AuthenticationServlet.AuthenticationResult;

public class SSOServiceServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	private static final long serialVersionUID = 1L;

	public SSOServiceServlet() {
		super();
	}

	String testResponse = "<samlp:Response xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://barclaysqual.cl.infosistema.com/iFlow/saml2/SSOService\" ID=\"id-cwHWo2U41-qWGk-kgrBa9gd1hVY-\" IssueInstant=\"2014-04-21T14:36:26Z\" Version=\"2.0\"><saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">http://federationuat.barcapint.com/fed/idp</saml:Issuer><samlp:Status><samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/></samlp:Status><saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"id-cbdNIqoStFFoKvzaHJDEChC00bQ-\" IssueInstant=\"2014-04-21T14:36:26Z\" Version=\"2.0\"><saml:Issuer Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">http://federationuat.barcapint.com/fed/idp</saml:Issuer><dsig:Signature xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><dsig:Reference URI=\"#id-cbdNIqoStFFoKvzaHJDEChC00bQ-\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><dsig:DigestValue>Dy1DW9DdzzM9DSRyPZ/aaa/K0ZA=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>HcOGPe0wBuojfNTbEeK1uS689j3075fAHuHTqcu8dMWeM+eYSmr56nNRQxTnLTLaCRM6du4YpWYH9v4caiQ7XWVLh1uFD/Rgw+vJmyWtgRIhZdRPhc0Q1V+HgcLzeJnYjqnGxMNfd5SPs3RhVhyqPZKxaFbrrj4SZ0ASxQlt6E8CvfZ8MzATuYAIi98S9viwtJXz0fgn4OZI8g5JB1nHherUVTuqzIjksA8K5f804svVUkSZDQ9pBjrkHlsJP0+xqLd9/RLGkJ+Neb6i6utasxHwIIow4ya+NsREn2/3EhKRiCQaR7tQdBFwVrDPx3AVxC+XhxYK26eW8guASA/XFA==</dsig:SignatureValue></dsig:Signature><saml:Subject><saml:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName\">G44530565</saml:NameID><saml:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\"><saml:SubjectConfirmationData NotOnOrAfter=\"2014-04-21T14:51:26Z\" Recipient=\"https://barclaysqual.cl.infosistema.com/iFlow/saml2/SSOService\"/></saml:SubjectConfirmation></saml:Subject><saml:Conditions NotBefore=\"2014-04-21T14:26:26Z\" NotOnOrAfter=\"2014-04-21T14:51:26Z\"><saml:AudienceRestriction><saml:Audience>https://barclaysqual.cl.infosistema.com/iFlow</saml:Audience></saml:AudienceRestriction></saml:Conditions><saml:AuthnStatement AuthnInstant=\"2014-04-21T14:36:25Z\" SessionIndex=\"id-QhOwMrM2hCezny3xmiVhhcCjDlg-\" SessionNotOnOrAfter=\"2014-04-21T14:36:35Z\"><saml:AuthnContext><saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos</saml:AuthnContextClassRef></saml:AuthnContext></saml:AuthnStatement></saml:Assertion></samlp:Response>";

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		AuthenticationResult result = new AuthenticationResult();
		String samlXMLB64Response = request.getParameter("SAMLResponse");
		if (StringUtils.isBlank(samlXMLB64Response))
			samlXMLB64Response = request.getAttribute("SAMLResponse").toString();
		//samlXMLB64Response = org.apache.commons.lang.StringUtils.replaceChars(samlXMLB64Response, ' ', '+');
		result.nextUrl = "../main.jsp";
		UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo();
		AuthProfile ap = BeanFactory.getAuthProfileBean();
		Boolean foundValidKey = false;
		
		try {			
			Integer ePId = Integer.parseInt(Setup.getProperty("ENTITY_PROVIDERS_NUMBER"));		
			String employeeid=null;
			for(int i=1; i<ePId; i++){
				String publicEPKey = Setup.getProperty("ENTITY_PROVIDER_PUBLIC_KEY_" + ePId);
				// user account specific settings. Import the certificate here
				AccountSettings accountSettings = new AccountSettings();
				accountSettings.setCertificate(publicEPKey);
				Response samlResponse = new Response(accountSettings);
				Logger.debug("System", this, "service", "Received Saml Response:" + samlXMLB64Response);
				samlResponse.loadXmlFromBase64(samlXMLB64Response);
				//samlResponse.loadXml(samlXMLB64Response);
	
				if (samlResponse.isValid()) {
					// the signature of the SAML Response is valid. The source is trusted
					foundValidKey=true;
					employeeid = samlResponse.getNameId();
					break;
				}
			}
			if(!foundValidKey) {
				// the signature of the SAML Response is not valid
				session.setAttribute("login_error", ui.getMessages().getString("login.error.sso.signature"));
				return;
			}
			////////////////////////////	
			ui.loginSSO(employeeid);
		    boolean isAuth = result.isAuth = ui.isLogged();

		    if (isAuth) {

		      /////////////////////////////
		      //
		      // Now set some session vars
		      //
		      /////////////////////////////

		      //Application Data
		      session.setAttribute("login",ui.getUtilizador());

		      session.setAttribute(Const.USER_INFO, ui);
		      UserSettings settings = ui.getUserSettings();
		      OrganizationData orgData = ap.getOrganizationInfo(ui.getOrganization());
		      session.setAttribute(Const.ORG_INFO,orgData);


		      OrganizationTheme orgTheme = BeanFactory.getOrganizationThemeBean();
		      if (orgTheme != null) {
		        OrganizationThemeData themeData = orgTheme.getOrganizationTheme(ui);
		        session.setAttribute("themedata",themeData);    
		      }
		      
		      if(settings.isDefault() && Const.USE_INDIVIDUAL_LOCALE && Const.ASK_LOCALE_AT_LOGIN) { 
		        result.nextUrl = "../setupUser";
		      }

		      // check license status
//		      if(!licenseOk && isSystem) {
//		        result.nextUrl = "Admin/licenseValidation.jsp";
//		      }

		      session.setAttribute("SessionHelperToken", new SimpleSessionHelper());

		    } else {
		      session.setAttribute("login_error", ui.getError());
		    }
		    PersistSession ps = new PersistSession();
		    ps.getSession(ui, session);		   			
		} catch (Exception e) {	
			ui.loginSSO(null);
			session.setAttribute("login_error", ui.getError());			
		} finally{
			response.sendRedirect(result.nextUrl);
		}
	}

}
