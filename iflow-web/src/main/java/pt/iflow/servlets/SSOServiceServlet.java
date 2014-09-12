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
		//samlXMLB64Response = "PHNhbWxwOlJlc3BvbnNlIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6&#xd;&#xa;U0FNTDoyLjA6cHJvdG9jb2wiIERlc3RpbmF0aW9uPSJodHRwczovL2JhcmNsYXlz&#xd;&#xa;cXVhbC5jbC5pbmZvc2lzdGVtYS5jb20vaUZsb3cvc2FtbDIvU1NPU2VydmljZSIg&#xd;&#xa;SUQ9ImlkLTdNZ001cTZySGQ4QVFPYnJFMjgtOTNmWEIzby0iIElzc3VlSW5zdGFu&#xd;&#xa;dD0iMjAxNC0wOS0wNVQxMzo1MTo1OVoiIFZlcnNpb249IjIuMCI&#x2b;PHNhbWw6SXNz&#xd;&#xa;dWVyIHhtbG5zOnNhbWw9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3Nl&#xd;&#xa;cnRpb24iIEZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOm5hbWVp&#xd;&#xa;ZC1mb3JtYXQ6ZW50aXR5Ij5odHRwOi8vZmVkZXJhdGlvbnVhdC5iYXJjYXBpbnQu&#xd;&#xa;Y29tL2ZlZC9pZHA8L3NhbWw6SXNzdWVyPjxzYW1scDpTdGF0dXM&#x2b;PHNhbWxwOlN0&#xd;&#xa;YXR1c0NvZGUgVmFsdWU9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpzdGF0&#xd;&#xa;dXM6U3VjY2VzcyIvPjwvc2FtbHA6U3RhdHVzPjxzYW1sOkFzc2VydGlvbiB4bWxu&#xd;&#xa;czpzYW1sPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXNzZXJ0aW9uIiBJ&#xd;&#xa;RD0iaWQtLUVvQ1JCZlU2VC1lRTREMnBValdCbnRIWEJ3LSIgSXNzdWVJbnN0YW50&#xd;&#xa;PSIyMDE0LTA5LTA1VDEzOjUxOjU5WiIgVmVyc2lvbj0iMi4wIj48c2FtbDpJc3N1&#xd;&#xa;ZXIgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6bmFtZWlkLWZv&#xd;&#xa;cm1hdDplbnRpdHkiPmh0dHA6Ly9mZWRlcmF0aW9udWF0LmJhcmNhcGludC5jb20v&#xd;&#xa;ZmVkL2lkcDwvc2FtbDpJc3N1ZXI&#x2b;PGRzaWc6U2lnbmF0dXJlIHhtbG5zOmRzaWc9&#xd;&#xa;Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPjxkc2lnOlNpZ25l&#xd;&#xa;ZEluZm8&#x2b;PGRzaWc6Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0&#xd;&#xa;dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPjxkc2lnOlNp&#xd;&#xa;Z25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAv&#xd;&#xa;MDkveG1sZHNpZyNyc2Etc2hhMSIvPjxkc2lnOlJlZmVyZW5jZSBVUkk9IiNpZC0t&#xd;&#xa;RW9DUkJmVTZULWVFNEQycFVqV0JudEhYQnctIj48ZHNpZzpUcmFuc2Zvcm1zPjxk&#xd;&#xa;c2lnOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAv&#xd;&#xa;MDkveG1sZHNpZyNlbnZlbG9wZWQtc2lnbmF0dXJlIi8&#x2b;PGRzaWc6VHJhbnNmb3Jt&#xd;&#xa;IEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMx&#xd;&#xa;NG4jIi8&#x2b;PC9kc2lnOlRyYW5zZm9ybXM&#x2b;PGRzaWc6RGlnZXN0TWV0aG9kIEFsZ29y&#xd;&#xa;aXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiLz48&#xd;&#xa;ZHNpZzpEaWdlc3RWYWx1ZT56ekRzVzl2aFkvaGRWRXhtOHpyOHRZK28xS2s9PC9k&#xd;&#xa;c2lnOkRpZ2VzdFZhbHVlPjwvZHNpZzpSZWZlcmVuY2U&#x2b;PC9kc2lnOlNpZ25lZElu&#xd;&#xa;Zm8&#x2b;PGRzaWc6U2lnbmF0dXJlVmFsdWU&#x2b;WUpIV1hnZVF5S1k0U2hMR2hmOTdxajZ6&#xd;&#xa;NWREMGR3SWg2VENhODVVMHBOWXg0dE9EQUE2QU9wWnk1Vzd2NlFmYkRiS1VkR0FK&#xd;&#xa;VjZld21XQWY3K0ZmYU9uSEJoZXNiVk9DazBSc2hHdXlacG5lZm1CQzRLcXZxd3dG&#xd;&#xa;aDQrSG91SFFBaVE3L1Y5UUdKc3ZucnFCNXVaYmpKSnFmeXhFd0EwakwyR09nRXpn&#xd;&#xa;TkczNm5ZbjN2akEyQ3FrUVJ6RVhvcEorSDh5cnNQVFVyRG1NYzFJMHBES3VPS1hJ&#xd;&#xa;ck5qandKdmhLWklYUkY2emFFZWRnb1VvUVJCQzFXN3pCZUM1bTdhUFR4T1ZOSHA3&#xd;&#xa;dGhPOGVTZlhnV3g3cVZNRXhuN2hmQ2orYVlrWHVKMk9LaTM1USsvaHl6SlJZa2RN&#xd;&#xa;dThhaXlRR0F6U2lyMjFaVzlFcTU5bkhDWnJOZ2pRPT08L2RzaWc6U2lnbmF0dXJl&#xd;&#xa;VmFsdWU&#x2b;PC9kc2lnOlNpZ25hdHVyZT48c2FtbDpTdWJqZWN0PjxzYW1sOk5hbWVJ&#xd;&#xa;RCBGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMTpuYW1laWQtZm9y&#xd;&#xa;bWF0Olg1MDlTdWJqZWN0TmFtZSI&#x2b;RTIwMDEwMTc4PC9zYW1sOk5hbWVJRD48c2Ft&#xd;&#xa;bDpTdWJqZWN0Q29uZmlybWF0aW9uIE1ldGhvZD0idXJuOm9hc2lzOm5hbWVzOnRj&#xd;&#xa;OlNBTUw6Mi4wOmNtOmJlYXJlciI&#x2b;PHNhbWw6U3ViamVjdENvbmZpcm1hdGlvbkRh&#xd;&#xa;dGEgTm90T25PckFmdGVyPSIyMDE0LTA5LTA1VDE0OjA2OjU5WiIgUmVjaXBpZW50&#xd;&#xa;PSJodHRwczovL2JhcmNsYXlzcXVhbC5jbC5pbmZvc2lzdGVtYS5jb20vaUZsb3cv&#xd;&#xa;c2FtbDIvU1NPU2VydmljZSIvPjwvc2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uPjwv&#xd;&#xa;c2FtbDpTdWJqZWN0PjxzYW1sOkNvbmRpdGlvbnMgTm90QmVmb3JlPSIyMDE0LTA5&#xd;&#xa;LTA1VDEzOjQxOjU5WiIgTm90T25PckFmdGVyPSIyMDE0LTA5LTA1VDE0OjA2OjU5&#xd;&#xa;WiI&#x2b;PHNhbWw6QXVkaWVuY2VSZXN0cmljdGlvbj48c2FtbDpBdWRpZW5jZT5odHRw&#xd;&#xa;czovL2JhcmNsYXlzcXVhbC5jbC5pbmZvc2lzdGVtYS5jb20vaUZsb3c8L3NhbWw6&#xd;&#xa;QXVkaWVuY2U&#x2b;PC9zYW1sOkF1ZGllbmNlUmVzdHJpY3Rpb24&#x2b;PC9zYW1sOkNvbmRp&#xd;&#xa;dGlvbnM&#x2b;PHNhbWw6QXV0aG5TdGF0ZW1lbnQgQXV0aG5JbnN0YW50PSIyMDE0LTA5&#xd;&#xa;LTA1VDEzOjUxOjU4WiIgU2Vzc2lvbkluZGV4PSJpZC1WMnZGLVQ3WW9xN2JuWWNn&#xd;&#xa;Qi1lNDJqOE55eG8tIiBTZXNzaW9uTm90T25PckFmdGVyPSIyMDE0LTA5LTA1VDEz&#xd;&#xa;OjUyOjA4WiI&#x2b;PHNhbWw6QXV0aG5Db250ZXh0PjxzYW1sOkF1dGhuQ29udGV4dENs&#xd;&#xa;YXNzUmVmPnVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphYzpjbGFzc2VzOktl&#xd;&#xa;cmJlcm9zPC9zYW1sOkF1dGhuQ29udGV4dENsYXNzUmVmPjwvc2FtbDpBdXRobkNv&#xd;&#xa;bnRleHQ&#x2b;PC9zYW1sOkF1dGhuU3RhdGVtZW50Pjwvc2FtbDpBc3NlcnRpb24&#x2b;PC9z&#xd;&#xa;YW1scDpSZXNwb25zZT4&#x3d;&#xd;&#xa;";
		samlXMLB64Response = StringEscapeUtils.unescapeHtml(samlXMLB64Response);		
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
			response.sendRedirect(result.nextUrl+"?" + Utils.makeSycnhronizerToken());
		}
	}

}
