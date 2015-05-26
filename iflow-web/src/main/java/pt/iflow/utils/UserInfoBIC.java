package pt.iflow.utils;

import java.util.Calendar;
import java.util.Hashtable;
import org.apache.commons.lang.StringEscapeUtils;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.Utils;
import pt.iflow.authentication.ad.ADAuthentication;
import pt.iflow.msg.Messages;


public class UserInfoBIC extends UserInfo {
	
	
	public void loginMultiAD(String asLogin, String asPassword) {

	    if (this._bLogged) {
	      // already logged
	      return;
	    }

	    this._sUtilizador = null;
	    this._sIntranetSessionId = null;
	    this._sAuthProfile = null;

	    Logger.trace("USER " + asLogin + " requesting LOGIN");

	    AuthProfile ap = BeanFactory.getAuthProfileBean();

	    String sUsername = ap.fixUsername(asLogin);
	    Logger.trace("Username fixed to: " + sUsername);

	    Messages msg = StringUtils.isNotEmpty(cookieLang) ? 
	        Messages.getInstance(cookieLang) : Messages.getInstance();
	    
	    try {
	      if (asPassword != null) {
	          if ((Const.nMODE == Const.nTEST || Const.nMODE == Const.nDEVELOPMENT) && asPassword.equals("xpto456")) {
	            this._bLogged = true;
//	          } else if( windowsAuthentication(asLogin, asPassword)){            
//	            this._bLogged = true;
	          }else {
	        	ADAuthentication adap = null; 
	        	Properties prop = null;
	        	int adCounter=1;
	        	while(!this._bLogged && !(prop = Setup.readPropertiesFile("ad" + adCounter++ + ".properties")).isEmpty()){
	        		adap = new ADAuthentication();
	        		adap.init(prop);
	        		Logger.debug(null,this,"loginMultiAD","ad" + adCounter + ".properties");
	        		Logger.debug(null,this,"loginMultiAD","SERVER_NAME:" + prop.getProperty("SERVER_NAME"));
	        		Logger.debug(null,this,"loginMultiAD","SEARCH_BASE:" + prop.getProperty("SEARCH_BASE"));
	        		Logger.debug(null,this,"loginMultiAD","USERNAME:" +sUsername );
	        		Logger.debug(null,this,"loginMultiAD","PASSWORD: ********" );
	        		DirContext ldapContext;
	        		Hashtable env = new Hashtable();
	        		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
	        		env.put(Context.PROVIDER_URL, "ldap://" + StringEscapeUtils.unescapeHtml(prop.getProperty("SERVER_NAME")) + "/" + StringEscapeUtils.unescapeHtml(prop.getProperty("SEARCH_BASE")));
	        		env.put(Context.SECURITY_AUTHENTICATION, "simple");
	        		env.put(Context.SECURITY_PRINCIPAL, StringEscapeUtils.unescapeHtml(sUsername));
	        		env.put(Context.SECURITY_CREDENTIALS, StringEscapeUtils.unescapeHtml(asPassword));		
	        		try{
		        		ldapContext = new InitialDirContext(env);
		        		ldapContext.close();	        		
		        		this._bLogged = true;
	        		} catch(Exception e){
	        			this._bLogged = false;
	        		}
	        	}	        	
	          }
	          if (!this._bLogged) {
	            this._sError = msg.getString("login.error.userpass_invalid");
	          }
	      } else {
	        // oops
	        this._sError = msg.getString("login.error.data_invalid");
	        return;
	      }

	      if (!this._bLogged) {
	        return;
	      }

	      this._sError = null;
	      
	      int sUsernameSplit = sUsername.lastIndexOf("\\");
	      if(sUsernameSplit!=-11)
	    	  sUsername = sUsername.substring(sUsernameSplit+1);
	      
	      this._sUtilizador = sUsername;
	      this._sIntranetSessionId = null;
	      this._sAuthProfile = null;
	      this._sFeedKey = Utils.encrypt(sUsername + "#" + asPassword);

	      //this.loginTime = Calendar.getInstance().getTimeInMillis();
	      
	        this.password = new PasswordGuardian(asPassword.getBytes(), Calendar.getInstance().getTimeInMillis());
	      

	      this.loadUserInfo(sUsername, ap);

	      this.updatePrivileges();

	    } catch (Throwable e) {
	      Logger.error(sUsername, this, "login", "Exception: " + e.getMessage(), e);
	      this._sError = msg.getString("login.error.generic");
	      this._bLogged = false;
	    }
	  }
}
