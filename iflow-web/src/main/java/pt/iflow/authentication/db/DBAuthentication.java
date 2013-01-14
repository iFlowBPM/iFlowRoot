/*
 *
 * Created on May 12, 2005 by iKnow
 *
  */

package pt.iflow.authentication.db;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.authentication.Authentication;
import pt.iflow.api.authentication.AuthenticationInfo;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class DBAuthentication implements Authentication {

  // Modificado para nao autenticar utilizadores nao confirmados.
  private static final String SQL_GET_USER_PASSWORD = "select USERPASSWORD from users where USERNAME=''{0}'' and ACTIVATED=1";
  private static final String SQL_GET_USER_SESSION = "select SESSIONID from users where USERNAME=''{0}'' and ACTIVATED=1";
  private static final String SQL_GET_USER_INFO = "select USERNAME,SESSIONID from users where USERNAME=''{0}'' and ACTIVATED=1";
  private static final String SQL_GET_PROFILE_USERS = "select USERID from users where profileid={0} and ACTIVATED=1";
  
  private static final String SQL_SET_USER_SESSION = "update users set SESSIONID=''{0}'' where USERNAME=''{1}'' and ACTIVATED=1";
  //private static final String SQL_RESET_USER_SESSION = "update users set SESSIONID=null where USERNAME=''{0}''";
    
  private static String generateSessionId(String username) {

    String sessionId = username + Long.toString((new Date()).getTime());

    sessionId = Utils.encrypt(sessionId);
    
    return sessionId;
  }
  
  /* (non-Javadoc)   Collection users = 
       DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USER_PASSWORD,new String[]{username}));
 
   * @see pt.iknow.authentication.Authentication#checkUser(java.lang.String, java.lang.String)
   */
  public boolean checkUser(String username, String password) {
    boolean result = false;
    
    Collection<Map<String,String>> users = 
       DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USER_PASSWORD,new Object[]{username}));
    
    if(users.isEmpty()) {
      Logger.error(null,this,"checkUser","No user with username " + username);
      result = false;
    } else if (users.size() > 1) {
      Logger.error(null,this,"checkUser","More than one user with username " + username);
      result = false;
    } else {
      String checkPass = users.iterator().next().get("USERPASSWORD");
      checkPass = Utils.decrypt(checkPass);
      if(!checkPass.equals(password)){
        Logger.debug(null,this,"checkUser","Passwords do not match for username " + username);
        result = false;
      } else {
        result = true;
      }
    }
    return result;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#checkUserSession(java.lang.String, java.lang.String)
   */
  public boolean checkUserSession(String username, String sessionID) {
    boolean result = false;
    
    Collection<Map<String,String>> users = 
       DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USER_SESSION,new Object[]{username}));
    
    if(users.isEmpty()) { 
      Logger.error(null,this,"checkUser","No user with username " + username);
      result = false;
    } else if (users.size() > 1) {
      Logger.error(null,this,"checkUser","More than one user with username " + username);
      result = false;
    } else {
      String checkSession = users.iterator().next().get("SESSIONID");
      if(!checkSession.equals(sessionID)){
        Logger.debug(null,this,"checkUser","Session id's do not match for username " + username);
        result = false;
      } else {
        result = true;
      }
    }
    return result;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#loginUser(java.lang.String, java.lang.String)
   */
  public AuthenticationInfo loginUser(String username, String password) {
    AuthenticationInfo retObj = null;
    
    if(checkUser(username,password)) {
      DatabaseInterface.executeUpdate(MessageFormat.format(SQL_SET_USER_SESSION,new Object[]{generateSessionId(username),username}));
      Map<String,String> userInfo = DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_USER_INFO,new Object[]{username})).iterator().next();
      retObj = new DBAuthenticationInfo(userInfo);
    }
    
    return retObj;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.Authentication#getProfileUsers(java.lang.String)
   */
  public Collection<Map<String,String>> getProfileUsers(String profileID) {
    return  DatabaseInterface.executeQuery(MessageFormat.format(SQL_GET_PROFILE_USERS,new Object[]{profileID}));
  }
  
  public void init(Properties parameters) {
    // Do nothing
  }

  //Não há nada para sincronizar neste caso
  public List<String> getAllUsersForSync(String orgId) {
    return new ArrayList<String>();
  }

}
