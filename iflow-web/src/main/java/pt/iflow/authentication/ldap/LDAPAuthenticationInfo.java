/*
 *
 * Created on May 12, 2005 by iKnow
 *
  */

package pt.iflow.authentication.ldap;

import java.util.Map;

import pt.iflow.api.authentication.AuthenticationInfo;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class LDAPAuthenticationInfo implements AuthenticationInfo {

  private String _userId;
  private String _sessionId;
  
  public LDAPAuthenticationInfo(Map<String,String> userMap, String sessionId) {
    this._userId = userMap.get("uid");
    this._sessionId = sessionId;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.AuthenticationInfo#getUserId()
   */
  public String getUserId() {
    return  this. _userId;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.AuthenticationInfo#getSessionId()
   */
  public String getSessionId() {
    return this._sessionId;
  }

}
