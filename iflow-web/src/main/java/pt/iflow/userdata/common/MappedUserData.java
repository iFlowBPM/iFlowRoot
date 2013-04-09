/*
 *
 * Created on May 12, 2005 by iKnow
 *
 */

package pt.iflow.userdata.common;

import java.io.Serializable;
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

public class MappedUserData extends MappedData implements UserData,Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public MappedUserData(Map<String,String> data, Map<String,String> map) {
    super(data, map);
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

  public String getOrgAdmFlows() {
    return get(ORGADM_FLOWS);
  }

  public String getOrgAdmOrg() {
    return get(ORGADM_ORG);
  }

  public String getOrgAdmProcesses() {
    return get(ORGADM_PROCESSES);
  }

  public String getOrgAdmResources() {
    return get(ORGADM_RESOURCES);
  }

  public String getOrgAdmUsers() {
    return get(ORGADM_USERS);
  }
}
