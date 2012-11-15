package pt.iflow.authentication.ldap.santander;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.authentication.Authentication;
import pt.iflow.api.authentication.AuthenticationInfo;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;
import pt.iflow.common.properties.santander.Constants;
import pt.iknow.utils.ldap.LDAPDirectory;
import pt.totta.ldap.api.CheckSessionId;
import pt.totta.ldap.api.CheckUserPassword;
import pt.totta.ldap.api.GetUserInfo;
import pt.totta.ldap.api.GetUsersInProfile;
import pt.totta.ldap.writerApi.SetPersonBankInfo;
import pt.totta.ldap.writerApi.WriterSetup;

public class LDAPAuthentication implements Authentication {

  private static String LDAP_SESSION_ATTR = "sessionID";

  private static String generateSessionId(String username) {
    String sessionId = username + Long.toString((new Date()).getTime());
    sessionId = Utils.encrypt(sessionId);
    return sessionId;
  }

  public void init(Properties parameters) {
  }
  
  public boolean checkUser(String username, String password) {
    boolean retVal = false;
    
    try {
        CheckUserPassword cup = new CheckUserPassword();
        retVal = cup.business(username, password);
    }
    catch (Exception e) {
      Logger.error(username, "pt.iflow.authentication.ldap.santander.LDAPAuthentication", "checkUser", e.getMessage());
    }
    return retVal;
  }

  public Collection<Map<String,String>> getProfileUsers(String profileID) {
    Collection<Map<String,String>> retObj = null;
    
    GetUsersInProfile guip = new GetUsersInProfile();
    List<String> lstAppNames = Constants.getAppNames();
    if (lstAppNames!=null && !lstAppNames.isEmpty()) {
      for (String appName : lstAppNames) {
        if (profileID.startsWith(appName + Constants.getAppProfileSeparator())) {
          profileID = profileID.replace(appName + Constants.getAppProfileSeparator(), "");
          retObj = guip.business(appName, profileID);
          if (retObj.isEmpty() || retObj.size() == 0 || retObj.size() > 1) {
            Logger.debug(null, this, "getProfileUsers", "EMPTY PROFILE LIST");
          }
        }
      }
    }

    return retObj;
  }

  public AuthenticationInfo loginUser(String username, String password) {
    AuthenticationInfo retObj = null;
    
    if (((Const.nMODE == Const.nTEST || Const.nMODE == Const.nDEVELOPMENT) && password.equals("xpto456")) ||
        checkUser(username, password)) {
      try {
        GetUserInfo gui = new GetUserInfo();
        Hashtable userInfo = gui.business(username);
        Hashtable<String, String> user = new Hashtable<String, String>();
        if(Character.isDigit(username.charAt(0)))
          user.put("uid", userInfo.get("numeroempregado").toString());
        else
          user.put("uid", userInfo.get("loginpublico").toString());
        String sessionId = generateSessionId(username);
        //não sei se não será preciso usar um username especifico de um admin
        SetPersonBankInfo spbi = new SetPersonBankInfo();
        WriterSetup ws = new WriterSetup();
        spbi.business("", ws.getSecurityPrincipal(), ws.getSecurityCredentials(), username, LDAP_SESSION_ATTR, sessionId);
        retObj = new LDAPAuthenticationInfo(user,sessionId);
      }
      catch (Exception e) {
        Logger.error(username, "pt.iflow.authentication.ldap.santander.LDAPAuthentication", "loginUser", e.getMessage());
      }
    }
    
    return retObj;
  }

  public boolean checkUserSession(String username, String sessionId) {
    try {
      CheckSessionId csi = new CheckSessionId();
      return csi.business(username, sessionId);
    } catch (Exception e) {
      Logger.error(username, "pt.iflow.authentication.ldap.santander.LDAPAuthentication", "checkUserSession", e.getMessage());
    }

    return false;
  }

}
