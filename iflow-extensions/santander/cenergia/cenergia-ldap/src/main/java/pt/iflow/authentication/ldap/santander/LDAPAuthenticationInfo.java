package pt.iflow.authentication.ldap.santander;

import java.util.Map;

import pt.iflow.api.authentication.AuthenticationInfo;

public class LDAPAuthenticationInfo implements AuthenticationInfo {

  private String _userId;
  private String _sessionId;
  
  public LDAPAuthenticationInfo(Map<String,String> userMap, String sessionId) {
    this._userId = userMap.get("loginpublico");
    if (this._userId==null || "".equals(this._userId))
      this._userId = userMap.get("numeroempregado");
    this._sessionId = sessionId;
  }

  public LDAPAuthenticationInfo(Map<String,String> userMap, String userId, String sessionId) {
    if(Character.isDigit(userId.charAt(0)))
      this._userId = userMap.get("numeroempregado");
    else
      this._userId = userMap.get("loginpublico");
    this._sessionId = sessionId;
  }

  public String getUserId() {
    return  this. _userId;
  }

  public String getSessionId() {
    return this._sessionId;
  }
}
