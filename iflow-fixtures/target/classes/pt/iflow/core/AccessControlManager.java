/*
 *
 * Created on May 12, 2005 by iKnow
 *
  */

package pt.iflow.core;

import java.util.Properties;

import pt.iflow.api.authentication.Authentication;
import pt.iflow.api.userdata.OrganizationDataAccess;
import pt.iflow.api.userdata.UserDataAccess;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.applicationdata.ApplicationDataAccess;
/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class AccessControlManager {

  private static Authentication _authentication;
  private static UserDataAccess _userDataAccess;
  private static UserDataAccess _systemUserDataAccess;
  private static OrganizationDataAccess _organizationDataAccess;
  private static ApplicationDataAccess _applicationDataAccess;
  
  static {

    Properties authConfig = Setup.readPropertiesFile(Const.AUTH_CONFIG);
    Properties systemAuthConfig = Setup.readPropertiesFile(Const.SYSTEM_AUTH_CONFIG);
    Properties userConfig = Setup.readPropertiesFile(Const.USER_DATA_CONFIG);
    Properties orgConfig = Setup.readPropertiesFile(Const.ORGANIZATION_DATA_CONFIG);

    try {
      _authentication = (Authentication) Class.forName(Const.AUTH_IMPL_CLASS).newInstance();

      _authentication.init(authConfig);
    }
    catch (Exception e) {
      Logger.error(null,null,"","Unable to instatiate Authentication object " + Const.AUTH_IMPL_CLASS + " : " + e.getMessage(), e);
      e.printStackTrace();
      System.exit(-1);
    }

    Logger.trace("USER DATA IMPL=" + Const.USER_DATA_IMPL_CLASS);

    try {
      _userDataAccess = (UserDataAccess) Class.forName(Const.USER_DATA_IMPL_CLASS).newInstance();
      _userDataAccess.init(userConfig);
    }
    catch (Exception e) {
      Logger.error(null,null,"","Unable to instatiate User Data object " + Const.USER_DATA_IMPL_CLASS + " : " + e.getMessage(), e);
      e.printStackTrace();
      System.exit(-1);
    }

    Logger.trace("SYSTEM USER DATA IMPL=" + Const.SYSTEM_USER_DATA_IMPL_CLASS);

    try {
      _systemUserDataAccess = (UserDataAccess) Class.forName(Const.SYSTEM_USER_DATA_IMPL_CLASS).newInstance();
      _systemUserDataAccess.init(systemAuthConfig);
    }
    catch (Exception e) {
      Logger.error(null,null,"","Unable to instatiate System User Data object " + Const.SYSTEM_USER_DATA_IMPL_CLASS + " : " + e.getMessage(), e);
      e.printStackTrace();
      System.exit(-1);
    }

    Logger.trace("ORGANIZATION DATA IMPL=" + Const.ORGANIZATION_DATA_IMPL_CLASS);

    try {
      _organizationDataAccess = (OrganizationDataAccess) Class.forName(Const.ORGANIZATION_DATA_IMPL_CLASS).newInstance();
      _organizationDataAccess.init(orgConfig);
    }
    catch (Exception e) {
      Logger.error(null,null,"","Unable to instatiate Organization Data object " + Const.ORGANIZATION_DATA_IMPL_CLASS + " : " + e.getMessage(), e);
      e.printStackTrace();
      System.exit(-1);
    }

    Logger.trace("APPLICATION DATA IMPL=" + Const.APPLICATION_DATA_IMPL_CLASS);

    try {
      _applicationDataAccess = (ApplicationDataAccess) Class.forName(Const.APPLICATION_DATA_IMPL_CLASS).newInstance();
    }
    catch (Exception e) {
      Logger.error(null,null,"","Unable to instatiate Application Data object " + Const.APPLICATION_DATA_IMPL_CLASS + " : " + e.getMessage(), e);
      e.printStackTrace();
      System.exit(-1);
    }

  }

  /**
   * 
   * Getter method for _authentication
   * 
   * @return Returns the _authentication.
   */
  public static Authentication getAuthentication() {
    return _authentication;
  }

  /**
   * 
   * Getter method for _userDataAccess
   * 
   * @return Returns the _userDataAccess.
   */
  public static UserDataAccess getUserDataAccess() {
    return _userDataAccess;
  }
  

  
  /**
   * 
   * Getter method for _organizationDataAccess
   * 
   * @return Returns the _organizationDataAccess.
   */
  public static OrganizationDataAccess getOrganizationDataAccess() {
    return _organizationDataAccess;
  }

  /**
   * 
   * Getter method for _applicationDataAccess
   * 
   * @return Returns the _applicationDataAccess.
   */
  public static ApplicationDataAccess getApplicationDataAccess() {
    return _applicationDataAccess;
  }

  public static UserDataAccess getSystemUserDataAccess() {
    return _systemUserDataAccess;
  }
  public static Authentication getSystemAuthentication() {
    return (Authentication)_systemUserDataAccess;
  }

}
