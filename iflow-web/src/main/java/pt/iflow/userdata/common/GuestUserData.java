/*
 *
 * Created on May 12, 2005 by iKnow
 *
  */

package pt.iflow.userdata.common;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import pt.iflow.api.userdata.UserData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class GuestUserData implements UserData,Serializable{

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
	
  private Map<String,String> _data;
  
  public GuestUserData() {
    this._data = new Hashtable<String, String>();
    this._data.put("FULL_NAME", "FULL_NAME");
    this._data.put("EMAIL", "EMAIL");
    this._data.put("EMPLOYEE_NUMBER", "EMPLOYEE_NUMBER");
    this._data.put("ORG_NAME", "ORG_NAME");
    this._data.put("UNIT_NAME", "UNIT_NAME");
    this._data.put("MOBILE_NUMBER", "MOBILE_NUMBER");
    this._data.put("ORGADM", "ORGADM");
    this._data.put("USERNAME", "USERNAME");
  }
  

  public String get(String paramName) {
      String retObj = this._data.get(paramName);

      return retObj;
  }

  /* (non-Javadoc)
   * @see pt.iknow.userdata.UserData#getEmail()
   */
  public String getEmail() {
    return get(EMAIL);
  }

  /* (non-Javadoc)
   * @see pt.iknow.userdata.UserData#getFirstName()
   */
  public String getName() {
    return get(FULL_NAME);
  }
  public Map<String,String> getData() {
    return this._data;
  }


  public String getNumber() {
    return get(EMPLOYEE_NUMBER);
  }

  public String getUnit() {
    return get(UNIT_NAME);
  }

  public String getUnitId() {
    return get(UNIT_ID);
  }

  public String getMobileNumber() {
    return get(MOBILE_NUMBER);
  }

  public String getOrgAdm() {
	return get(ORGADM);
  }

  public String getUsername() {
    return get(USERNAME);
    }

  public String toString() {
    return String.valueOf(_data);
  }
}
