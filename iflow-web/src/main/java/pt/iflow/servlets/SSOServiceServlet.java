package pt.iflow.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

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
import pt.iflow.api.utils.Utils;
import pt.iflow.core.PersistSession;
import pt.iflow.saml.onelogin.AccountSettings;
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
		//samlXMLB64Response = "PHNhbWxwOlJlc3BvbnNlIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6cHJvdG9jb2wiIERlc3RpbmF0aW9uPSJodHRwczovL2JhcmNsYXlzLmNsLmluZm9zaXN0ZW1hLmNvbS9pRmxvdy9zYW1sMi9TU09TZXJ2aWNlIiBJRD0iaWQtcFEtdVlzdUdZTVktdEVyVzgtWlBvRlFYamtBLSIgSXNzdWVJbnN0YW50PSIyMDE0LTEwLTAzVDA5OjMxOjM1WiIgVmVyc2lvbj0iMi4wIj48c2FtbDpJc3N1ZXIgeG1sbnM6c2FtbD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFzc2VydGlvbiIgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6bmFtZWlkLWZvcm1hdDplbnRpdHkiPmh0dHA6Ly9mZWRlcmF0aW9uLmJhcmNhcGludC5jb20vZmVkL2lkcDwvc2FtbDpJc3N1ZXI+PGRzaWc6U2lnbmF0dXJlIHhtbG5zOmRzaWc9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPjxkc2lnOlNpZ25lZEluZm8+PGRzaWc6Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPjxkc2lnOlNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNyc2Etc2hhMSIvPjxkc2lnOlJlZmVyZW5jZSBVUkk9IiNpZC1wUS11WXN1R1lNWS10RXJXOC1aUG9GUVhqa0EtIj48ZHNpZzpUcmFuc2Zvcm1zPjxkc2lnOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNlbnZlbG9wZWQtc2lnbmF0dXJlIi8+PGRzaWc6VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIi8+PC9kc2lnOlRyYW5zZm9ybXM+PGRzaWc6RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiLz48ZHNpZzpEaWdlc3RWYWx1ZT51cFBxY1IwYlAwMThCdHRpRmNZWHNxNytFdDQ9PC9kc2lnOkRpZ2VzdFZhbHVlPjwvZHNpZzpSZWZlcmVuY2U+PC9kc2lnOlNpZ25lZEluZm8+PGRzaWc6U2lnbmF0dXJlVmFsdWU+enlkcEdqc2J5dEdrc2JEYWNrdjh1Ym01d2htaEpUT2Q0SGwrTkhVK3hsTm9lNU5IdGJ0Y2ZsU2tZTGJRK05Mek5FMUdYOGQ4Nlh6R1BCYkN2MkRzN09WbGN6VEV1aDMrRHI0TFV2WWM1Qm1nMDRWT05IZ2Vka09oZFRKUlNabDFTd1Z4NzQ2Ymt3dThCOVp6NVA5VDJ6eGtBdkhlek5yOEwrMXFXOG1MTDhSL1VHMWNLMG5lclNIYzF2VHR4MGFjcWNsUFBZajhoMlBodEFxb21RY3FmK2c5TlRmRnM0M1F0ZUtxdkdVd3dIQm1oTWNSdUtLa1lJbUZ0V3ppY2ZLSWxjeGwvVm9vWldDZHRucVdIWWVxYmlONEZ2dzNtRkZScGRqSHAxZFlpcUlBWTY5RktDMk05OCtyTWFJSlo1cjAzRW1VLzJ0TU1zNDRNYjQyWTlXV1FBPT08L2RzaWc6U2lnbmF0dXJlVmFsdWU+PC9kc2lnOlNpZ25hdHVyZT48c2FtbHA6U3RhdHVzPjxzYW1scDpTdGF0dXNDb2RlIFZhbHVlPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6c3RhdHVzOlN1Y2Nlc3MiLz48L3NhbWxwOlN0YXR1cz48c2FtbDpBc3NlcnRpb24geG1sbnM6c2FtbD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFzc2VydGlvbiIgSUQ9ImlkLWFMMVQ1LU5wY0p6a0lKR3lxcG1NTjZVT3gtay0iIElzc3VlSW5zdGFudD0iMjAxNC0xMC0wM1QwOTozMTozNVoiIFZlcnNpb249IjIuMCI+PHNhbWw6SXNzdWVyIEZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOm5hbWVpZC1mb3JtYXQ6ZW50aXR5Ij5odHRwOi8vZmVkZXJhdGlvbi5iYXJjYXBpbnQuY29tL2ZlZC9pZHA8L3NhbWw6SXNzdWVyPjxkc2lnOlNpZ25hdHVyZSB4bWxuczpkc2lnPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIj48ZHNpZzpTaWduZWRJbmZvPjxkc2lnOkNhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48ZHNpZzpTaWduYXR1cmVNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjcnNhLXNoYTEiLz48ZHNpZzpSZWZlcmVuY2UgVVJJPSIjaWQtYUwxVDUtTnBjSnprSUpHeXFwbU1ONlVPeC1rLSI+PGRzaWc6VHJhbnNmb3Jtcz48ZHNpZzpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIvPjxkc2lnOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPjwvZHNpZzpUcmFuc2Zvcm1zPjxkc2lnOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNzaGExIi8+PGRzaWc6RGlnZXN0VmFsdWU+NDNiWTZYR245SmhTSEloSU1FSkR5S3ZENXdNPTwvZHNpZzpEaWdlc3RWYWx1ZT48L2RzaWc6UmVmZXJlbmNlPjwvZHNpZzpTaWduZWRJbmZvPjxkc2lnOlNpZ25hdHVyZVZhbHVlPmVxY2FkazkybHpTcmRxM3N5b2V0KzJta3FwaThiTkFhZUltNTdUK1VsMUE0ang2NEJVZzA0V1B0MUFpenJMaXorSXVQaTZvWHdJNm9sVmFFZHdKRk5xR2VYVk5OalNBSlZuYjBNVVVvd2ErdDBhWldwbFAreWlDNlFEWVBZYjNCN25RSjE2Qk8zaGVNQkpsWVJsbWl0eEE0SDBISit4UzhtVEpYV3hnckVJUVhEZFlIVzdXbVNhWGR5K0c0ZVQ4ZUxZTmJQcHE3bVlVNkI4VXRuZU40VnJIdXFxUkl5eXVSdVdFdk5iSEtGRGpaQVFsK29CcDl3WHpFRm5VOGhWdklEdit5VTN4NjZkQ0NmNFRGMXIvY1ZwM2tVb3lvMjJGOW81N2VqcldUK1VUSFlDT25nNzdUQnlseU9KdnFGbmpKL1VsSkIwNlVUWldNa0ZJb3BBekgxdz09PC9kc2lnOlNpZ25hdHVyZVZhbHVlPjwvZHNpZzpTaWduYXR1cmU+PHNhbWw6U3ViamVjdD48c2FtbDpOYW1lSUQgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoxLjE6bmFtZWlkLWZvcm1hdDpYNTA5U3ViamVjdE5hbWUiPkUyMDAxMDM3Nzwvc2FtbDpOYW1lSUQ+PHNhbWw6U3ViamVjdENvbmZpcm1hdGlvbiBNZXRob2Q9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpjbTpiZWFyZXIiPjxzYW1sOlN1YmplY3RDb25maXJtYXRpb25EYXRhIE5vdE9uT3JBZnRlcj0iMjAxNC0xMC0wM1QwOTo0NjozNVoiIFJlY2lwaWVudD0iaHR0cHM6Ly9iYXJjbGF5cy5jbC5pbmZvc2lzdGVtYS5jb20vaUZsb3cvc2FtbDIvU1NPU2VydmljZSIvPjwvc2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uPjwvc2FtbDpTdWJqZWN0PjxzYW1sOkNvbmRpdGlvbnMgTm90QmVmb3JlPSIyMDE0LTEwLTAzVDA5OjIxOjM1WiIgTm90T25PckFmdGVyPSIyMDE0LTEwLTAzVDA5OjQ2OjM1WiI+PHNhbWw6QXVkaWVuY2VSZXN0cmljdGlvbj48c2FtbDpBdWRpZW5jZT5odHRwczovL2JhcmNsYXlzLmNsLmluZm9zaXN0ZW1hLmNvbS9pRmxvdzwvc2FtbDpBdWRpZW5jZT48L3NhbWw6QXVkaWVuY2VSZXN0cmljdGlvbj48L3NhbWw6Q29uZGl0aW9ucz48c2FtbDpBdXRoblN0YXRlbWVudCBBdXRobkluc3RhbnQ9IjIwMTQtMTAtMDNUMDk6MzE6MzVaIiBTZXNzaW9uSW5kZXg9ImlkLXlLYWJkN3FzZEd3QlQ0Vi1SLTViWDd0MklGby0iIFNlc3Npb25Ob3RPbk9yQWZ0ZXI9IjIwMTQtMTAtMDNUMDk6MzE6NDVaIj48c2FtbDpBdXRobkNvbnRleHQ+PHNhbWw6QXV0aG5Db250ZXh0Q2xhc3NSZWY+dXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFjOmNsYXNzZXM6S2VyYmVyb3M8L3NhbWw6QXV0aG5Db250ZXh0Q2xhc3NSZWY+PC9zYW1sOkF1dGhuQ29udGV4dD48L3NhbWw6QXV0aG5TdGF0ZW1lbnQ+PC9zYW1sOkFzc2VydGlvbj48L3NhbWxwOlJlc3BvbnNlPg==";
		samlXMLB64Response = StringEscapeUtils.unescapeHtml(samlXMLB64Response);		
		result.nextUrl = "../main.jsp";
		UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo();
		AuthProfile ap = BeanFactory.getAuthProfileBean();
		Boolean foundValidKey = false;
		
		try {			
			Integer ePId = Integer.parseInt(Setup.getProperty("ENTITY_PROVIDERS_NUMBER"));		
			String employeeid=null;
			for(int i=1; i<ePId; i++){
				String publicEPKey = Setup.getProperty("ENTITY_PROVIDER_PUBLIC_KEY_" + i);
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
			response.sendRedirect(result.nextUrl+"?" + Utils.makeSycnhronizerToken());
		}
	}

}
