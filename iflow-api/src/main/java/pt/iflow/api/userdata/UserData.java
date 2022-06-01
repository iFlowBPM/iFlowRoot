/*
 *
 * Created on May 12, 2005 by iKnow
 *
  */

package pt.iflow.api.userdata;



/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public interface UserData extends IMappedData {
  
  public static final String ID = "ID";
  public static final String USERNAME = "USERNAME";
  public static final String FULL_NAME = "FULL_NAME";
  public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
  public static final String EMAIL_ADDRESS_DEPRECATED = "EMAIL";
  public static final String EMPLOYEE_NUMBER = "EMPLOYEE_NUMBER";
  public static final String EMPLOYEE_NUMBER_DEPRECATED = "EMPLOYEEID";
  public static final String ORG_NAME = "ORG_NAME";
  public static final String ORG_ID = "ORG_ID";
  public static final String UNIT_NAME = "UNIT_NAME";
  public static final String UNITID = "UNITID";
  public static final String MOBILE_NUMBER = "MOBILE_NUMBER";
  public static final String GENDER = "GENDER";
  public static final String PHONE_NUMBER = "PHONE_NUMBER";
  public static final String FAX_NUMBER = "FAX_NUMBER";
  public static final String COMPANY_PHONE = "COMPANY_PHONE";
  public static final String DEPARTMENT = "DEPARTMENT";
  public static final String MANAGER = "MANAGER";
  public static final String TITLE = "TITLE";
  public static final String FIRST_NAME = "FIRST_NAME";
  public static final String LAST_NAME = "LAST_NAME";
  public static final String ORGADM = "ORGADM";
  public static final String PASSWORD_RESET = "PASSWORD_RESET";
  public static final String ORGADM_USERS = "ORGADM_USERS";
  public static final String ORGADM_FLOWS = "ORGADM_FLOWS";
  public static final String ORGADM_PROCESSES = "ORGADM_PROCESSES";
  public static final String ORGADM_RESOURCES = "ORGADM_RESOURCES";
  public static final String ORGADM_ORG = "ORGADM_ORG";
  public static final String PASSWORD_CREATION_DATE = "PASSWORD_CREATION_DATE";
  
  public abstract String getEmail();
  public abstract String getUsername();
  public abstract String getName();
  public abstract String getNumber();
  public abstract String getUnit();
  public abstract String getUnitId();
  public abstract String getMobileNumber();
  public abstract String getGender();
  public abstract String getPhoneNumber();
  public abstract String getFaxNumber();
  public abstract String getCompanyPhone();
  public abstract String getDepartment();
  public abstract String getManager();
  public abstract String getTitle();
  public abstract String getFirstName();
  public abstract String getLastName();
  public abstract String getOrgAdm();
  public abstract String getOrgAdmUsers();
  public abstract String getOrgAdmFlows();
  public abstract String getOrgAdmProcesses();
  public abstract String getOrgAdmResources();
  public abstract String getOrgAdmOrg();
  public abstract String getPasswordCreationDate();
}
