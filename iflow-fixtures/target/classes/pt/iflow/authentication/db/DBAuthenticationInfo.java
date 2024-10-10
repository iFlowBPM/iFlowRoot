/*
 *
 * Created on May 12, 2005 by iKnow
 *
  */

package pt.iflow.authentication.db;

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

public class DBAuthenticationInfo implements AuthenticationInfo {
  
  private Map<String,String> _fullData;
  
  public DBAuthenticationInfo(Map<String,String> data) {
    this._fullData = data;
  }

  public String getUserId() {
    return (String) _fullData.get("USERNAME");
  }

  public String getSessionId() {
    return (String) _fullData.get("SESSIONID");
  }

}
