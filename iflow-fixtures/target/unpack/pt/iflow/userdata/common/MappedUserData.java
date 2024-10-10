/*
 *
 * Created on May 12, 2005 by iKnow
 *
 */

package pt.iflow.userdata.common;

import java.io.Serializable;
import java.util.Map;

import pt.iflow.api.userdata.UserData;
import pt.iknow.utils.StringUtilities;

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
    String aux = get(EMAIL_ADDRESS);
    if (StringUtilities.isEmpty(aux)) aux = get(EMAIL_ADDRESS_DEPRECATED);
    return aux;
  }

  /* (non-Javadoc)
   * @see pt.iknow.userdata.UserData#getFirstName()
   */
  public String getName() {
    return get(FULL_NAME);
  }


  public String getNumber() {
    String aux = get(EMPLOYEE_NUMBER);
    if (StringUtilities.isEmpty(aux)) aux = get(EMPLOYEE_NUMBER_DEPRECATED);
    return aux;
  }


  public String getUnit() {
    return get(UNIT_NAME);
  }

  public String getUnitId() {
    return get(UNITID);
  }


  public String getMobileNumber() {
    return get(MOBILE_NUMBER);
  }

  public String getGender() {
    return get(GENDER);
  }

  public String getPhoneNumber() {
    return get(PHONE_NUMBER);
  }

  public String getFaxNumber() {
    return get(FAX_NUMBER);
  }

  public String getCompanyPhone() {
    return get(COMPANY_PHONE);
  }

  public String getDepartment() {
    return get(DEPARTMENT);
  }

  public String getManager() {
    return get(MANAGER);
  }

  public String getTitle() {
    return get(TITLE);
  }

  public String getFirstName() {
    return get(FIRST_NAME);
  }

  public String getLastName() {
    return get(LAST_NAME);
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
