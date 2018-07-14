package pt.iflow.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.UserCredentials;
import pt.iflow.api.core.UserManager;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.errors.ErrorCode;
import pt.iflow.api.errors.IErrorHandler;
import pt.iflow.api.errors.UserErrorCode;
import pt.iflow.api.events.AbstractEvent;
import pt.iflow.api.events.EventManager;
import pt.iflow.api.notification.Email;
import pt.iflow.api.notification.EmailManager;
import pt.iflow.api.notification.EmailTemplate;
import pt.iflow.api.transition.ProfilesTO;
import pt.iflow.api.userdata.Tutorial;
import pt.iflow.api.userdata.views.OrganizationViewInterface;
import pt.iflow.api.userdata.views.OrganizationalUnitViewInterface;
import pt.iflow.api.userdata.views.UserViewInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.errors.ErrorHandler;
import pt.iflow.userdata.views.OrganizationView;
import pt.iflow.userdata.views.OrganizationalUnitView;
import pt.iflow.userdata.views.UserView;
import pt.iknow.utils.Validate;

/**
 * 
 * User management
 * 
 */

public class UserManagerBean implements UserManager {

  private static UserManagerBean instance = null;

  private UserManagerBean() {}

  public static UserManagerBean getInstance() {
    if(null == instance)
      instance = new UserManagerBean();
    return instance;
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1520277121369155405L;


  private static final String CHECK_DUPLICATE_EMAIL_IN_ORG="select count(*) from users u, organizational_units ou where u.email_address=? and u.unitid=ou.unitid and ou.organizationid=?";
  private static final String CHECK_DUPLICATE_EMAIL_IN_ORG_BUT_USER="select count(*) from users u, organizational_units ou where u.email_address=? and u.unitid=ou.unitid and ou.organizationid=? and u.userid<>?";

  private static final String GET_SYSTEM_USERNAMES = "select username from system_users";
  
  
  public IErrorHandler inviteUser(UserInfoInterface userInfo, String username, String gender, String unit, String emailAddress,
      String firstName, String lastName, String phoneNumber, String faxNumber, String mobileNumber, String companyPhone,
      String orgId, String orgAdm, String[] listExtraProperties, String[] listExtraValues) {
    return createUser(userInfo, username, gender, unit, emailAddress, firstName, lastName, phoneNumber, faxNumber, mobileNumber, companyPhone, orgId, orgAdm, true, null, listExtraProperties, listExtraValues);
  }

  public IErrorHandler createUser(UserInfoInterface userInfo, String username, String gender, String unit, String emailAddress,
      String firstName, String lastName, String phoneNumber, String faxNumber, String mobileNumber, String companyPhone,
      String orgId, String orgAdm, String password, String[] listExtraProperties, String[] listExtraValues) {
    return createUser(userInfo, username, gender, unit, emailAddress, firstName, lastName, phoneNumber, faxNumber, mobileNumber, companyPhone, orgId, orgAdm, false, password, listExtraProperties, listExtraValues);
  }


  /**
   * Add a new user
   * 
   * @param userInfo - The administrator creating a new user
   * @param username - String com o username do utilizador
   * @param gender - User gender (M/F)
   * @param unit - Organizational unit
   * @param emailAddress - User email address
   * @param firstName - First Name
   * @param lastName - Last Name
   * @param phoneNumber - Phone Number
   * @param faxNumber - Fax Number
   * @param mobileNumber - Mobile Number
   * @param companyPhone - Company Phone
   * @param emailTemplate
   * @param listExtraProperties - Extra Properties 
   * @param listExtraValues - Extra Values
   * @return - true if user was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  private IErrorHandler createUser(UserInfoInterface userInfo, 
      String username, 
      String gender, 
      String unit, 
      String emailAddress, 
      String firstName, 
      String lastName, 
      String phoneNumber, 
      String faxNumber, 
      String mobileNumber, 
      String companyPhone,
      String orgId,
      String orgAdm, 
      boolean invite,
      String password,
      String[] listExtraProperties,
      String[] listExtraValues) {

    boolean result = false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;
    ResultSet rs = null;

    if(Const.bUSE_EMAIL)
      password =  RandomStringUtils.random(8, true, true);
    else {
      if(null == password) {
        Logger.warning(userInfo.getUtilizador(), this, "createUser", "no password and no email, exiting");
        new ErrorHandler(UserErrorCode.FAILURE);
      }
    }
    
    String activationCode =  RandomStringUtils.random(40, true, true);

    Logger.debug(userInfo.getUtilizador(), this, "createUser", "Creating user " + username);

    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin()) {
      Logger.error(userInfo.getUtilizador(), this, "createUser", "not sys admin nor org admin, exiting");
      return new ErrorHandler(UserErrorCode.FAILURE);
    }

    int iOrgAdm = 0;
    if(null != orgAdm && ArrayUtils.contains(new String [] {"1","true","yes"},orgAdm.trim().toLowerCase())) iOrgAdm=1;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("select count(*) from users where username = ?");
      pst.setString(1, username);
      rs = pst.executeQuery();
      if (rs.next()) {
        if (rs.getInt(1)>0) {
          return new ErrorHandler(UserErrorCode.FAILURE_DUPLICATE_USER);
        }
      }
      //rs.close();
      //pst.close();



      if(Const.bUSE_EMAIL) {
        pst = db.prepareStatement(CHECK_DUPLICATE_EMAIL_IN_ORG);
        pst.setString(1, emailAddress);
        pst.setString(2, userInfo.isSysAdmin() ? orgId : userInfo.getOrganization());
        rs = pst.executeQuery();
        if(rs.next()) {
          if(rs.getInt(1)>0) return new ErrorHandler(UserErrorCode.FAILURE_DUPLICATE_EMAIL);
        }

        //rs.close();
        //pst.close();
      } else if(null == emailAddress) {
        emailAddress = "";
      }

      String sQuery = "insert into users (GENDER,UNITID,USERNAME,USERPASSWORD,EMAIL_ADDRESS,FIRST_NAME,LAST_NAME,PHONE_NUMBER,FAX_NUMBER,MOBILE_NUMBER,COMPANY_PHONE,ACTIVATED,PASSWORD_RESET,ORGADM#EP#) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?#EV#)";
      String auxEP = "";
      String auxEV = "";
      if (listExtraProperties!=null && listExtraProperties.length>0) {
        Map<String, String> mapExtra = AccessControlManager.getUserDataAccess().getMappingExtra();
        for (int i=0; i<listExtraProperties.length; i++){
          auxEP += "," + mapExtra.get(listExtraProperties[i]);
          auxEV += ",'" + listExtraValues[i]+"'";
          }
      }
      sQuery = sQuery.replace("#EP#", auxEP).replace("#EV#", auxEV);

      pst = db.prepareStatement(sQuery, new String[]{"userid"});
      pst.setString(1, gender);
      pst.setString(2, unit);
      pst.setString(3, username);
      pst.setString(4, Utils.encrypt(password));
      pst.setString(5, emailAddress);
      pst.setString(6, firstName);
      pst.setString(7, lastName);
      pst.setString(8, phoneNumber);
      pst.setString(9, faxNumber);
      pst.setString(10, mobileNumber);
      pst.setString(11, companyPhone);
      if(Const.bUSE_EMAIL) {
        if(invite) { 
          pst.setInt(12,0);
        } else {
          pst.setInt(12,1);
        }
        pst.setInt(13,0);
      } else {
        pst.setInt(12, 1);
        pst.setInt(13, 0);
      }
      pst.setInt(14, iOrgAdm);
      //for (int i=0; listExtraValues!=null && i<listExtraValues.length; i++){
      //  pst.setString(14+i, listExtraValues[i]);
      //}

      pst.executeUpdate();
      rs = pst.getGeneratedKeys();

      int userId = 0;

      if(rs.next()) {
        userId = rs.getInt(1);
      }
      //rs.close();
      result = true;


      if(Const.bUSE_EMAIL && invite) { 
        // create activation code
        pst = db.prepareStatement("insert into user_activation (USERID,ORGANIZATIONID,UNITID,CODE) values (?,?,?,?)");
        pst.setInt(1, userId);
        pst.setString(2, orgId);
        pst.setString(3, unit);
        pst.setString(4, activationCode);
        pst.executeUpdate();
        pst.close();
      }

      db.commit();
    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "createUser", "User not inserted!", e);
      e.printStackTrace();
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    }


    if (result == false) return new ErrorHandler(ErrorCode.FAILURE);

    if(Const.bUSE_EMAIL) {
      String mailTemplate = "";
      // send confirmation and password set email
      Hashtable<String,String> ht = new Hashtable<String,String>();
      if(invite) {
        ht.put("url", "http://"+Const.APP_HOST+":"+Const.APP_PORT+"/iFlow/confirm?activation=invite&code="+activationCode);
        mailTemplate = "new_invite";
      } else {
        ht.put("url", "http://"+Const.APP_HOST+":"+Const.APP_PORT+"/iFlow/login.jsp");
        mailTemplate = "new_user";
      }

      ht.put("username", username);
      ht.put("password", password);
      ht.put("inviter", userInfo.getUserFullName());
      ht.put("organization", userInfo.getCompanyName());

      // build a default email
      Email email = EmailManager.buildEmail(ht, EmailManager.getEmailTemplate(BeanFactory.getUserInfoFactory().newGuestUserInfo(), mailTemplate));
      email.setTo(emailAddress);
      result = email.sendMsg();

      if (result == false) return new ErrorHandler(ErrorCode.SEND_EMAIL);
    }

    return new ErrorHandler(ErrorCode.SUCCESS);
  }

  /**
   * Add a new Organization
   * 
   * @param userInfo - The administrator creating the new organization
   * @param name - Organization name
   * @param description - Simple description
   * @return - true if organization was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean createOrganization(UserInfoInterface userInfo, String name, String description) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "createOrganization", "Creating organization " + name);

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "createOrganization", "not administrator, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("insert into organizations (NAME,DESCRIPTION) values (?,?)");
      pst.setString(1, name);
      pst.setString(2, description);
      pst.executeUpdate();
      db.commit();
      result = true;
    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "createOrganization", "Organization not inserted!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    }

    return result;
  }

  /**
   * Add a new Organizational Unit
   * 
   * @param userInfo - The administrator creating the new organization
   * @param organizationid - Organization name
   * @param name - Unit name
   * @param description - Simple description
   * @param parentid - Parent unit ID
   * @param managerid - Unit manager ID
   * @return - true if organizational unit was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean createOrganizationalUnit(UserInfoInterface userInfo, String organizationid, String name, String description, String parentid, String managerid) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;
    ResultSet rs = null;
    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "createOrganizationalUnit", "name="+name);
    Logger.debug(userInfo.getUtilizador(), this, "createOrganizationalUnit", "description="+description);
    Logger.debug(userInfo.getUtilizador(), this, "createOrganizationalUnit", "organizationid="+organizationid);
    Logger.debug(userInfo.getUtilizador(), this, "createOrganizationalUnit", "parentid="+parentid);
    Logger.debug(userInfo.getUtilizador(), this, "createOrganizationalUnit", "managerid="+managerid);
    if(parentid == null || "".equals(parentid)) {
      parentid="-1";
    }
    
    try {
    	OrganizationalUnitViewInterface[] orgUnits = getAllOrganizationalUnits(userInfo);
    	for(OrganizationalUnitViewInterface ou: orgUnits)
    		if(StringUtils.endsWithIgnoreCase(ou.getName(), name)){
    			Logger.debug(userInfo.getUtilizador(), this, "createOrganizationalUnit", "Cannot create, duplicated organizational unit name : " + name);
    			return false;
    		}    				
	} catch (IllegalAccessException e1) {}
    
    Logger.debug(userInfo.getUtilizador(), this, "createOrganizationalUnit", "Creating organizational unit " + name);

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "createOrganizationalUnit", "not administrator, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("insert into organizational_units (ORGANIZATIONID,PARENT_ID,NAME,DESCRIPTION) values (?,?,?,?)", new String[]{"unitid"});

      pst.setString(1, organizationid);
      pst.setString(2, parentid);
      pst.setString(3, name);
      pst.setString(4, description);
      pst.executeUpdate();
      rs = pst.getGeneratedKeys();
      int unitid = -1;
      if(rs.next()) {
        unitid = rs.getInt(1);
      }
      rs.close();
      pst.close();

      // add unit manager
      pst = db.prepareStatement("insert into unitmanagers (USERID, UNITID) values (?,?)");
      pst.setString(1, managerid);
      pst.setInt(2, unitid);
      pst.executeUpdate();
      db.commit();

      result = true;

    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "createOrganizationalUnit", "Organizational Unit not inserted!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    }

    return result;
  }


  /**
   * @see pt.iflow.api.core.UserManager#createProfile(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.transition.ProfilesTO)
   */
  public boolean createProfile(UserInfoInterface userInfo, ProfilesTO profile) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "createProfile", "Creating profile " + profile.getName());      
    }

    boolean profileExists = profileExists(userInfo, profile);
    if (profileExists) {
      Logger.warning(userInfo.getUtilizador(), this, "createProfile",
          "Cannot create profile with data that already exists in DB (profile data=" + profile.toString() + ")");
      return false;
    }
     
    if (!userInfo.isSysAdmin() || !userInfo.isOrgAdmin()) {
      Logger.warning(userInfo.getUtilizador(), this, "createProfile", "User is not an administrator, exiting!");
      
    }

    if (userInfo.isOrgAdmin() && !StringUtils.equals(userInfo.getCompanyID(), profile.getOrganizationId())) {
      Logger.warning(userInfo.getUtilizador(), this, "createProfile", "Unable to match user organization (id="
          + userInfo.getCompanyID() + ") with profile organization (id=" + profile.getOrganizationId() + ")");
      return false;
    } 

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      StringBuffer sql = new StringBuffer();
      sql.append("INSERT INTO " + ProfilesTO.TABLE_NAME);
      sql.append(" (" + ProfilesTO.NAME + "," + ProfilesTO.DESCRIPTION + "," + ProfilesTO.ORGANIZATION_ID + ")");
      sql.append(" values (?,?,?)");

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "modifyProfile", "QUERY=" + sql.toString());
      }

      //pst = db.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
      pst = db.prepareStatement(sql.toString(), new String [] {ProfilesTO.PROFILE_ID});
      pst.setString(1, profile.getName());
      pst.setString(2, profile.getDescription());
      pst.setString(3, profile.getOrganizationId());
      pst.executeUpdate();

      rs = pst.getGeneratedKeys();
      if (rs.next()) {
        profile.setProfileId(rs.getInt(1));
      }

      db.commit();
    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "createProfile", "Profile not inserted!", e);
      return false;
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    	
    }

    return true;
  }



  /**
   * Modify an existing user
   * 
   * @param userInfo - The administrator creating a new user
   * @param userId - String com o username do utilizador
   * @param gender - User gender (M/F)
   * @param unit - Organizational unit (if null, does not changes)
   * @param emailAddress - User email address
   * @param firstName - First Name
   * @param lastName - Last Name
   * @param phoneNumber - Phone Number
   * @param faxNumber - Fax Number
   * @param mobileNumber - Mobile Number
   * @param companyPhone - Company Phone
   * @param orgAdm - Organization Administration
   * @return - true if user was modified successfully
   */

  public IErrorHandler modifyUserAsAdmin(UserInfoInterface userInfo, String userId, String gender, String unit,
      String emailAddress, String firstName, String lastName, String phoneNumber, String faxNumber, String mobileNumber,
      String companyPhone, String orgAdm, String orgAdmUsers, String orgAdmFlows, String orgAdmProcesses, String orgAdmResources,
      String orgAdmOrg, String newPassword, String[] listExtraProperties, String[] listExtraValues) {

    IErrorHandler result = new ErrorHandler(ErrorCode.FAILURE);
    // check self data
    if (!(userInfo.isOrgAdmin() || userInfo.isSysAdmin())) {
      Logger.debug(userInfo.getUtilizador(), this, "modifyUserAsAdmin", "not administrator, exiting");
      return new ErrorHandler(UserErrorCode.FAILURE_NOT_AUTHORIZED);
    }

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    Logger.debug(userInfo.getUtilizador(), this, "modifyUserAsAdmin", "Modify user id " + userId);

    // check self data
    int iOrgAdm = 0;
    if(null != orgAdm && ArrayUtils.contains(new String [] {"1","true","yes"},orgAdm.trim().toLowerCase())) iOrgAdm=1;
    if(!Const.bUSE_EMAIL && emailAddress == null) emailAddress = ""; // reset email address. no error issued. 

    // - check if userId is orgAdm and install type is web
    // - if so, send email confirmation and mark user.
    boolean isWebAdmin = Const.INSTALL_WEB.equals(Const.INSTALL_TYPE) && iOrgAdm == 1;
    String oldEmail = null;
    String newEmail = emailAddress;
    String key = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      if(Const.bUSE_EMAIL) { // only update email if email mode on.
        pst = db.prepareStatement(CHECK_DUPLICATE_EMAIL_IN_ORG_BUT_USER);
        pst.setString(1, emailAddress);
        pst.setString(2, userInfo.getOrganization());
        pst.setString(3, userId);
        rs = pst.executeQuery();
        if(rs.next()) {
          if(rs.getInt(1)>0) {
            result = new ErrorHandler(UserErrorCode.FAILURE_DUPLICATE_EMAIL);
            throw new SQLException("Email already exists");
          }
        }
        //rs.close();
        //pst.close();
      }      

      if(isWebAdmin) { // check if email is the same.
        if(Const.bUSE_EMAIL) { // only update email if email mode on.
          pst = db.prepareStatement("select email_address from users where userid=?");
          pst.setString(1, userId);
          rs = pst.executeQuery();
          if(rs.next()) {
            oldEmail = rs.getString(1);
          }
          //rs.close();
          //pst.close();

          // preserve email address until confirmation
          if(!newEmail.equals(oldEmail)) {
            emailAddress = oldEmail;
            key = RandomStringUtils.random(40, true, true);

            // just in case, remove a possible older confirmation
            pst = db.prepareStatement("delete from email_confirmation where userid=? and organizationid=?");
            pst.setString(1, userId);
            pst.setString(2, userInfo.getOrganization());
            pst.executeUpdate();
            pst.close();

            // insert token to confirm email address
            pst = db.prepareStatement("insert into email_confirmation (userid,organizationid,email,code) values (?,?,?,?)");
            pst.setString(1, userId);
            pst.setString(2, userInfo.getOrganization());
            pst.setString(3, newEmail);
            pst.setString(4, key);
            pst.executeUpdate();
            pst.close();
          }
        }
      }


      String setUnitId = "";
      if(StringUtils.isNotEmpty(unit))
        setUnitId=",UNITID=?";
      
      String setPassword = "";
      String password = null;
      if(!Const.bUSE_EMAIL && StringUtils.isNotEmpty(newPassword)) {
        password = Utils.encrypt(newPassword);
        setPassword = ",PASSWORD_RESET=0,USERPASSWORD=?";
      }

      String setExtras = "";
      if (listExtraProperties!=null && listExtraProperties.length>0) {
        Map<String, String> mapExtra = AccessControlManager.getUserDataAccess().getMappingExtra();
        for (int i=0; i<listExtraProperties.length; i++){
          setExtras += "," + mapExtra.get(listExtraProperties[i]) + "=?";
        }
      }

      int pos = 0;
      pst = db
          .prepareStatement("update users set GENDER=?,EMAIL_ADDRESS=?,FIRST_NAME=?,LAST_NAME=?,PHONE_NUMBER=?,FAX_NUMBER=?,MOBILE_NUMBER=?,COMPANY_PHONE=?,ORGADM=?,ORGADM_USERS=?,ORGADM_FLOWS=?,ORGADM_PROCESSES=?,ORGADM_RESOURCES=?,ORGADM_ORG=?"
              + setUnitId + setExtras + setPassword + " where USERID=?");
      pst.setString(++pos, gender);
      pst.setString(++pos, emailAddress);
      pst.setString(++pos, firstName);
      pst.setString(++pos, lastName);
      pst.setString(++pos, phoneNumber);
      pst.setString(++pos, faxNumber);
      pst.setString(++pos, mobileNumber);
      pst.setString(++pos, companyPhone);
      pst.setInt(++pos, iOrgAdm);
      pst.setInt(++pos, StringUtils.equals(orgAdmUsers, "true") ? 1 : 0);
      pst.setInt(++pos, StringUtils.equals(orgAdmFlows, "true") ? 1 : 0);
      pst.setInt(++pos, StringUtils.equals(orgAdmProcesses, "true") ? 1 : 0);
      pst.setInt(++pos, StringUtils.equals(orgAdmResources, "true") ? 1 : 0);
      pst.setInt(++pos, StringUtils.equals(orgAdmOrg, "true") ? 1 : 0);
      if(StringUtils.isNotEmpty(unit)) pst.setString(++pos, unit);
      if (listExtraValues!=null && listExtraValues.length>0) {
        for (int i=0; i<listExtraValues.length; i++){
          pst.setString(++pos, listExtraValues[i]);
        }
      }
      if(!Const.bUSE_EMAIL && StringUtils.isNotEmpty(newPassword)) pst.setString(++pos, password);

      // where clause
      pst.setString(++pos, userId);
      pst.executeUpdate();
      db.commit();
      result = new ErrorHandler(ErrorCode.SUCCESS);
    }
    catch (SQLException e) {
      Logger.warning(userInfo.getUtilizador(), this, "modifyUserAsAdmin", "User not updated!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    }

    if(Const.bUSE_EMAIL && isWebAdmin && !newEmail.equals(oldEmail)) {
      // In this case, we must notify user about changes...
      notifyEmailChange(userInfo, oldEmail, newEmail, key);
      result = new ErrorHandler(UserErrorCode.PENDING_ORG_ADM_EMAIL);
    }

    return result;
  }

  public IErrorHandler modifyUserAsSelf(UserInfoInterface userInfo, String password, String gender, String emailAddress, String firstName, String lastName, String phoneNumber, String faxNumber, String mobileNumber, String companyPhone, String[] listExtraProperties, String[] listExtraValues) {
    IErrorHandler result = new ErrorHandler(ErrorCode.FAILURE);

    String userId = userInfo.getUserId();

    // - check if is orgAdm and install type is web
    // - if so, send email confirmation and mark user.

    if(!Const.bUSE_EMAIL && emailAddress == null) emailAddress = ""; // reset email address. no error issued. 


    boolean isWebAdmin = (userInfo.isOrgAdmin() && Const.INSTALL_WEB.equals(Const.INSTALL_TYPE));
    String oldEmail = null;
    String newEmail = emailAddress;
    String key = null;

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    Logger.debug(userInfo.getUtilizador(), this, "modifyUserAsSelf", "Modify user id " + userId);

    if(StringUtils.isEmpty(password)) {
      // Must provide self password.
      return new ErrorHandler(UserErrorCode.FAILURE_NOT_AUTHORIZED);
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      if(Const.bUSE_EMAIL) { // only update email if email mode on.
        pst = db.prepareStatement(CHECK_DUPLICATE_EMAIL_IN_ORG_BUT_USER);
        pst.setString(1, emailAddress);
        pst.setString(2, userInfo.getOrganization());
        pst.setString(3, userId);
        rs = pst.executeQuery();
        if(rs.next()) {
          if(rs.getInt(1)>0) {
            result = new ErrorHandler(UserErrorCode.FAILURE_DUPLICATE_EMAIL);
            throw new SQLException("Email already exists");
          }
        }
        rs.close();
        pst.close();
      }

      if(isWebAdmin) { // check if email is the same.
        if(Const.bUSE_EMAIL) { // only update email if email mode on.
          pst = db.prepareStatement("select email_address from users where userid=?");
          pst.setString(1, userId);
          rs = pst.executeQuery();
          if(rs.next()) {
            oldEmail = rs.getString(1);
          }
          rs.close();
          pst.close();

          // preserve email address until confirmation
          emailAddress = oldEmail;
          if(!newEmail.equals(oldEmail)) {
            key = RandomStringUtils.random(40, true, true);

            // just in case, remove a possible older confirmation
            pst = db.prepareStatement("delete from email_confirmation where userid=? and organizationid=?");
            pst.setString(1, userId);
            pst.setString(2, userInfo.getOrganization());
            pst.executeUpdate();
            pst.close();

            // insert token to confirm email address
            pst = db.prepareStatement("insert into email_confirmation (userid,organizationid,email,code) values (?,?,?,?)");
            pst.setString(1, userId);
            pst.setString(2, userInfo.getOrganization());
            pst.setString(3, newEmail);
            pst.setString(4, key);
            pst.executeUpdate();
            pst.close();
          }
        }
      }

      String setExtras = "";
      if (listExtraProperties!=null && listExtraProperties.length>0) {
        Map<String, String> mapExtra = AccessControlManager.getUserDataAccess().getMappingExtra();
        for (int i=0; i<listExtraProperties.length; i++){
          setExtras += "," + mapExtra.get(listExtraProperties[i]) + "=?";
        }
      }

      int pos = 0;
      String query = "";
      if (!userInfo.isSysAdmin()){
        query = "update users set GENDER=?,EMAIL_ADDRESS=?,FIRST_NAME=?,LAST_NAME=?,PHONE_NUMBER=?,FAX_NUMBER=?,MOBILE_NUMBER=?,COMPANY_PHONE=?"+setExtras+" where USERID=? and USERPASSWORD=?";
      }else{
        query = "update system_users set EMAIL_ADDRESS=?,FIRST_NAME=?,LAST_NAME=?,PHONE_NUMBER=?,MOBILE_NUMBER=?"+setExtras+" where USERID=? and USERPASSWORD=?";
      }
      pst = db.prepareStatement(query);
      if (!userInfo.isSysAdmin()){
      pst.setString(++pos, gender);
      pst.setString(++pos, emailAddress);
      pst.setString(++pos, firstName);
      pst.setString(++pos, lastName);
      pst.setString(++pos, phoneNumber);
      pst.setString(++pos, faxNumber);
      pst.setString(++pos, mobileNumber);
      pst.setString(++pos, companyPhone);
      }else{
        pst.setString(++pos, emailAddress);
        pst.setString(++pos, firstName);
        pst.setString(++pos, lastName);
        pst.setString(++pos, phoneNumber);
        pst.setString(++pos, mobileNumber);
      }
      if (listExtraValues!=null && listExtraValues.length>0) {
        for (int i=0; i<listExtraValues.length; i++){
          pst.setString(++pos, listExtraValues[i]);
        }
      }

      // where clause
      pst.setString(++pos, userId);
      String encPwd = Utils.encrypt(password);
      pst.setString(++pos, encPwd);
      int upd = pst.executeUpdate();
      db.commit();
      Logger.info(userInfo.getUtilizador(), this, "modifyUser", "Updated "+upd+" users");
      result = new ErrorHandler(upd==0?UserErrorCode.FAILURE_NOT_AUTHORIZED:ErrorCode.SUCCESS);
    }
    catch (SQLException e) {
      Logger.warning(userInfo.getUtilizador(), this, "modifyUser", "User not updated!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    }

    if(Const.bUSE_EMAIL && isWebAdmin && !newEmail.equals(oldEmail)) {
      // In this case, we must notify user about changes...
      notifyEmailChange(userInfo, oldEmail, newEmail, key);
      result = new ErrorHandler(UserErrorCode.PENDING_ORG_ADM_EMAIL);
    }

    return result;
  }


  private void notifyEmailChange(UserInfoInterface userInfo, String oldEmail, String newEmail, String key) {
    if(!Const.bUSE_EMAIL) return;
    // In this case, we must notify user about changes...
    EmailTemplate oldEmailTemplate = EmailManager.getEmailTemplate(userInfo, "old_email_confirm");
    EmailTemplate newEmailTemplate = EmailManager.getEmailTemplate(userInfo, "new_email_confirm");
    Hashtable<String,String> oldEmailProps = new Hashtable<String,String>();
    // generate URL 1
    String oeurl = "http://"+Const.APP_HOST+":"+Const.APP_PORT+"/iFlow/newemail.jsp?action=revert&key="+key;
    oldEmailProps.put("new_email", newEmail);
    oldEmailProps.put("old_email", oldEmail);
    oldEmailProps.put("key", key);
    oldEmailProps.put("url", oeurl);

    Hashtable<String,String> newEmailProps = new Hashtable<String,String>();
    // generate URL 2
    String neurl = "http://"+Const.APP_HOST+":"+Const.APP_PORT+"/iFlow/newemail.jsp?action=confirm&key="+key;
    newEmailProps.put("new_email", newEmail);
    newEmailProps.put("old_email", oldEmail);
    newEmailProps.put("key", key);
    newEmailProps.put("url", neurl);


    Email oldEmailNotif = EmailManager.buildEmail(oldEmailProps, oldEmailTemplate);
    Email newEmailNotif = EmailManager.buildEmail(newEmailProps, newEmailTemplate);
    oldEmailNotif.setTo(oldEmail);
    newEmailNotif.setTo(newEmail);
    oldEmailNotif.sendMsg();
    newEmailNotif.sendMsg();
    // notify user with new email
  }

  /**
   * Modify an existing Organization
   * 
   * @param userInfo - The administrator creating the new organization
   * @param organizationId - The organization to update
   * @param name - Organization name
   * @param description - Simple description
   * @return - true if organization was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean modifyOrganization(UserInfoInterface userInfo, String organizationId, String name, String description) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "modifyOrganization", "Updating organization " + name);

    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "modifyOrganization", "not administrator, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("update organizations set NAME=?,DESCRIPTION=? where ORGANIZATIONID=?");
      pst.setString(1, name);
      pst.setString(2, description);
      pst.setString(3, organizationId);
      pst.executeUpdate();
      db.commit();
      result = true;
    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "modifyOrganization", "Organization not Updating!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    }

    return result;
  }

  /**
   * Modify an existing Organizational Unit
   * 
   * @param userInfo - The administrator creating the new organization
   * @param unitId - The Organizational Unit to change
   * @param organizationid - Organization name
   * @param name - Unit name
   * @param description - Simple description
   * @param parentid - Parent unit ID
   * @param managerid - Unit manager ID
   * @return - true if organizational unit was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean modifyOrganizationalUnit(UserInfoInterface userInfo, String unitId, String organizationid, String name, String description, String parentid, String managerid) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;

    Logger.debug("", this, "", "name="+name);
    Logger.debug("", this, "", "description="+description);
    Logger.debug("", this, "", "organizationid="+organizationid);
    Logger.debug("", this, "", "parentid="+parentid);
    Logger.debug("", this, "", "managerid="+managerid);
    if(parentid == null || "".equals(parentid)) {
      parentid="-1";
    }

    Logger.debug(userInfo.getUtilizador(), this, "modifyOrganizationalUnit", "Updating organizational unit " + name);

    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "modifyOrganizationalUnit", "not administrator, exiting");
      return false;
    }

    try {
    	OrganizationalUnitViewInterface[] orgUnits = getAllOrganizationalUnits(userInfo);
    	for(OrganizationalUnitViewInterface ou: orgUnits)
    		if(StringUtils.endsWithIgnoreCase(ou.getName(), name)){
    			Logger.debug(userInfo.getUtilizador(), this, "createOrganizationalUnit", "Cannot create, duplicated organizational unit name : " + name);
    			return false;
    		}    				
	} catch (IllegalAccessException e1) {}
    
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("update organizational_units set ORGANIZATIONID=?,PARENT_ID=?,NAME=?,DESCRIPTION=? "
          + "where UNITID=?");

      pst.setString(1, organizationid);
      pst.setString(2, parentid);
      pst.setString(3, name);
      pst.setString(4, description);
      pst.setString(5, unitId);
      pst.executeUpdate();
      pst.close();

      // add unit manager
      pst = db.prepareStatement("update unitmanagers set USERID=? where UNITID=?");
      pst.setString(1, managerid);
      pst.setString(2, unitId);
      pst.executeUpdate();
      db.commit();

      result = true;

    }

    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "modifyOrganizationalUnit", "Organizational Unit not updated!", e);
    }
    finally {
      DatabaseInterface.closeResources(db, pst);
    }

    return result;
  }

  /**
   * @see pt.iflow.api.core.UserManager#modifyProfile(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.transition.ProfilesTO)
   * @ejb.interface-method view-type = "remote"
   */
  public boolean modifyProfile(UserInfoInterface userInfo, ProfilesTO profile) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;
    ResultSet rs = null;
    Statement st = null;

    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "modifyProfile", "Updating profile: " + profile.getName());
    }

    boolean profileExists = profileExists(userInfo, profile);
    if (!userInfo.isOrgAdmin() || !StringUtils.equals(userInfo.getCompanyID(), profile.getOrganizationId()) || profileExists) {

      if (Logger.isDebugEnabled()) {
    if (!userInfo.isOrgAdmin()) {
          Logger.debug(userInfo.getUtilizador(), this, "modifyProfile", "User is not an administrator, exiting!");
        } else if (!StringUtils.equals(userInfo.getCompanyID(), profile.getOrganizationId())) {
          Logger.debug(userInfo.getUtilizador(), this, "modifyProfile", "Unable to match user organization (id="
              + userInfo.getCompanyID() + ") with profile organization (id=" + profile.getOrganizationId() + ")");
        } else if (profileExists) {
          Logger.debug(userInfo.getUtilizador(), this, "modifyProfile",
              "Cannot update profile with data that already exists in DB (profile data=" + profile.toString() + ")");
        }
    }

      result = false;
    } else {
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + ProfilesTO.TABLE_NAME);
        sql.append(" SET " + ProfilesTO.NAME + "=" + profile.getValueOf(ProfilesTO.NAME));
        sql.append(", " + ProfilesTO.DESCRIPTION + "=" + profile.getValueOf(ProfilesTO.DESCRIPTION));
        sql.append(" WHERE " + ProfilesTO.PROFILE_ID + "=" + profile.getValueOf(ProfilesTO.PROFILE_ID));
        sql.append(" AND " + ProfilesTO.ORGANIZATION_ID + "=" + profile.getValueOf(ProfilesTO.ORGANIZATION_ID));

        if (Logger.isDebugEnabled()) {
          Logger.debug(userInfo.getUtilizador(), this, "modifyProfile", "QUERY=" + sql.toString());
        }

        st = db.createStatement();
        st.executeUpdate(sql.toString());
      db.commit();

      result = true;
      } catch (Exception e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "modifyProfile", "Profile not updated!", e);
      } finally {
        //DatabaseInterface.closeResources(db, st, rs);
      	try { if (db != null) db.close(); } catch (SQLException e) {}
      	try { if (st != null) st.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    	
      }
    }
    return result;
    }
  
  private boolean profileExists(UserInfoInterface userInfo, ProfilesTO profile) {
    boolean result = false;
    DataSource ds = null;
    Connection db = null;
    ResultSet rs = null;
    Statement st = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT *");
      sql.append(" FROM " + ProfilesTO.TABLE_NAME);
      sql.append(" WHERE " + ProfilesTO.NAME + " LIKE " + profile.getValueOf(ProfilesTO.NAME));
      sql.append(" AND " + ProfilesTO.ORGANIZATION_ID + "=" + profile.getValueOf(ProfilesTO.ORGANIZATION_ID));

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "profileExists", "QUERY=" + sql.toString());
    }

      st = db.createStatement();
      rs = st.executeQuery(sql.toString());
      if(rs.next()) {
        result = true;
      } else {
        result = false;
      }
    } catch (Exception e) {
      result = false;
      Logger.error(userInfo.getUtilizador(), this, "profileExists", "Unable to perform verification.", e);
    } finally {
      //DatabaseInterface.closeResources(db, st, rs);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (st != null) st.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    	
    }
    return result;
  }


  /**
   * Add a user to a profile
   * 
   * @param userInfo - The administrator creating the new profile
   * @param userId - User Id
   * @param profileId - profile id
   * @return - true if profile was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean addUserProfile(UserInfoInterface userInfo, String userId, String profileId) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "addUserProfile", "Adding user " + userId + " to profile " + profileId);

    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin()) {
      Logger.error(userInfo.getUtilizador(), this, "addUserProfile", "not sysadmin nor orgadmin, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("insert into userprofiles (userid,profileid) values (?,?)");
      pst.setString(1, userId);
      pst.setString(2, profileId);
      pst.executeUpdate();
      db.commit();
      result = true;

    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "addUserProfile", "User-Profile mapping not inserted!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    }

    return result;
  }

  /**
   * Add a user to a profile
   * 
   * @param userInfo - The administrator creating the new profile
   * @param userId - User id
   * @param profileId - profile id
   * @return - true if profile was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean addUserProfile(UserInfoInterface userInfo, int userId, int profileId) {
    return addUserProfile(userInfo, String.valueOf(userId), String.valueOf(profileId));
  }

  // ////////////////////////////////////////////////////////////////////////////////
  /**
   * Remove existing user
   * 
   * @param userInfo - The administrator creating a new user
   * @param username - String com o username do utilizador
   * @return - true if user was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean removeUser(UserInfoInterface userInfo, String userid) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "removeUser", "Removing user " + userid);

    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin()) {
      Logger.error(userInfo.getUtilizador(), this, "removeUser", "not sysadmin nor org admin, exiting");
      return false;
    }

    if (StringUtils.isEmpty(userid)) {
      Logger.error(userInfo.getUtilizador(), this, "removeUser", "empty userid.. exiting");
      return false;      
    }
    
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      // todo: delegations??
      
      UserViewInterface user = getUser(userInfo, userid);
      String username = user.getUsername();
      
      pst = db.prepareStatement("delete from activity where userid=?");
      pst.setString(1, username);
      pst.executeUpdate();
      pst.close();
      
      pst = db.prepareStatement("delete from userprofiles where userid=?");
      pst.setString(1, userid);
      pst.executeUpdate();
      pst.close();
            
      pst = db.prepareStatement("delete from user_settings where userid=?");
      pst.setString(1, username);
      pst.executeUpdate();
      pst.close();
      
      pst = db.prepareStatement("delete from email_confirmation where userid=?");
      pst.setString(1, userid);
      pst.executeUpdate();
      pst.close();
      
      pst = db.prepareStatement("delete from user_activation where userid=?");
      pst.setString(1, userid);
      pst.executeUpdate();
      pst.close();

      pst = db.prepareStatement("delete from users where userid=?");
      pst.setString(1, userid);
      pst.executeUpdate();
      pst.close();
      

      db.commit();
      result = true;

      Logger.info(userInfo.getUtilizador(), this, "removeUser", 
          "User " + username + " (id " + userid + ") REMOVED");
      

    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "removeUser", "User not removed!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    }

    return result;
  }

  /**
   * Lock an existing Organization
   * 
   * @param userInfo - The administrator creating the new organization
   * @param organizationId - Organization name
   * @return - true if organization was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean lockOrganization(UserInfoInterface userInfo, String organizationId) {

    Logger.debug(userInfo.getUtilizador(), this, "lockOrganization", "Locking organization " + organizationId);

    if (!userInfo.isSysAdmin()) {
      Logger.error(userInfo.getUtilizador(), this, "lockOrganization", "not administrator, exiting");
      return false;
    }

    return dolockOrganization(userInfo, organizationId, true);
  }

  /**
   * Lock an existing Organization
   * 
   * @param userInfo - The administrator creating the new organization
   * @param organizationId - Organization name
   * @return - true if organization was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean unlockOrganization(UserInfoInterface userInfo, String organizationId) {

    Logger.debug(userInfo.getUtilizador(), this, "unlockOrganization", "Locking organization " + organizationId);

    if (!userInfo.isSysAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "unlockOrganization", "not administrator, exiting");
      return false;
    }

    return dolockOrganization(userInfo, organizationId, false);
  }

  /**
   * Lock an existing Organization
   * 
   * @param userInfo - The administrator creating the new organization
   * @param organizationId - Organization name
   * @return - true if organization was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  private boolean dolockOrganization(UserInfoInterface userInfo, String organizationId, boolean lock) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "dolockOrganization", "Locking organization " + organizationId);

    if (!userInfo.isSysAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "dolockOrganization", "not administrator, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      // extra features
      pst = db.prepareStatement("update organizations set locked=? where organizationid=?");
      pst.setInt(1, lock?1:0);
      pst.setString(2, organizationId);
      int n = pst.executeUpdate();

      if(n == 0) throw new Exception("No organizations to lock...");

      pst = db.prepareStatement("update users set activated=? where unitid in (select unitid from organizational_units where organizationid=?)");
      pst.setInt(1, lock?0:1);
      pst.setString(2, organizationId);
      pst.executeUpdate();

      if(!lock) {
        // must check if the users are really active. so...
        pst = db.prepareStatement("update users set activated=0 where userid in (select userid from user_activation where organizationid=?)");
        pst.setString(1, organizationId);
        pst.executeUpdate();
      }

      db.commit();
      result = true;

    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "dolockOrganization", "Organization not locked!",e);
    } catch (Exception e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "dolockOrganization", "Organization not locked!",e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try { if(null != db) db.close(); } catch (SQLException e) {}
    	try { if(null != pst) pst.close(); } catch (SQLException e) {}
    }

    return result;
  }

  /**
   * Delete an existing Organization
   * 
   * @param userInfo - The administrator creating the new organization
   * @param organizationId - Organization name
   * @return - true if organization was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean removeOrganization(UserInfoInterface userInfo, String organizationId) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;
    ResultSet rs = null;

    Logger.debug(userInfo.getUtilizador(), this, "removeOrganization", "Deleting organization " + organizationId);

    if (!userInfo.isSysAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeOrganization", "not administrator, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);
      db.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
      try {
        // first of all, confirm that the org exists
        pst = db.prepareStatement("select count(*) from organizations where organizationid=?");
        pst.setString(1, organizationId);
        rs = pst.executeQuery();
        boolean failure = true;
        if(rs.next()) {
          failure = (rs.getInt(1) != 1);
        }
        //rs.close();
        if(failure) {
          throw new Exception ("No organization found.");
        }

        remove(userInfo, db, pst, organizationId);
        db.commit();
        result = true;
      } catch (SQLException e) {
        result = false;
        Logger.error(userInfo.getUtilizador(), this, "removeOrganization", "Organization not deleted!", e);
        try {
          db.rollback();
        } catch (SQLException sqlex) {
          Logger.error(userInfo.getUtilizador(), this, "removeOrganization", "Unable to perform rollback operation!", sqlex);
        }
      }
    } catch (Exception e) {
      result = false;
      Logger.error(userInfo.getUtilizador(), this, "removeOrganization", "Unable to delete organization!", e);
    } finally {
      //DatabaseInterface.closeResources(db, pst, rs);    	
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    	
    }

    return result;
  }

  /**
   * Remove an Organizational Unit
   * 
   * @param userInfo - The administrator creating the new organization
   * @param unitid - Unit name
   * @return - true if organizational unit was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean removeOrganizationalUnit(UserInfoInterface userInfo, String unitid) {
    boolean result = false;

    if(isParent(userInfo,unitid) == 1){
    	return false;
    }
    
    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "removeOrganizationalUnit", "Removing organizational unit " + unitid);

    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeOrganizationalUnit", "not administrator, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("delete from unitmanagers where UNITID=?");
      pst.setString(1, unitid);
      pst.executeUpdate();
      pst.close();
      pst = db.prepareStatement("delete from organizational_units where UNITID=?");
      pst.setString(1, unitid);
      pst.executeUpdate();
      db.commit();
      result = true;

    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "removeOrganizationalUnit", "Organizational Unit not removed!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    }

    return result;
  }
  
  private int isParent(UserInfoInterface userInfo, String unitid){
	    Logger.info(userInfo.getUtilizador(), this, "isParent", "check if parent for unit " + unitid);

	    Collection<Map<String,String>> ucol = DatabaseInterface.executeQuery("SELECT parent_id FROM organizational_units where parent_id="+unitid);
	    if(ucol != null && ucol.size() > 0) {
	        return 1;
	      }
	    return 0;
  }
  
  
  /**
   * Unmap a user to a profile
   * 
   * @param userInfo - The administrator creating the new profile
   * @param userId - User name
   * @param profileId - profile name
   * @return - true if profile was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean delUserProfile(UserInfoInterface userInfo, String userId, String profileId) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "delUserProfile", "Unmapping user " + userId + " to profile " + profileId);

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "delUserProfile", "not administrator, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("delete from userprofiles where userid=? and profileid=?");
      pst.setString(1, userId);
      pst.setString(2, profileId);
      pst.executeUpdate();
      db.commit();
      result = true;

    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "delUserProfile", "User-Profile mapping not deleted!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    }

    return result;
  }

  /**
   * Unmap a user to a profile
   * 
   * @param userInfo - The administrator creating the new profile
   * @param userId - User id
   * @param profileId - profile id
   * @return - true if profile was created successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean delUserProfile(UserInfoInterface userInfo, int userId, int profileId) {
    return delUserProfile(userInfo, String.valueOf(userId), String.valueOf(profileId));
  }

  /**
   * Delete a Profile
   * 
   * @param userInfo - The administrator creating the new profile
   * @param profileId - Profile name
   * @return - true if profile was removed successfully
   * @ejb.interface-method view-type = "remote"
   */
  public boolean removeProfile(UserInfoInterface userInfo, String profileId) {
    boolean result = false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;
    ResultSet rs = null;

    Logger.debug(userInfo.getUtilizador(), this, "removeProfile", "Removing profile " + profileId);

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeProfile", "not administrator, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("delete from profiles where PROFILEID=? and ORGANIZATIONID=?");
      pst.setString(1, profileId);
      pst.setString(2, userInfo.getCompanyID());
      pst.executeUpdate();
      db.commit();

      result = true;

    }
    catch (Exception e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "removeProfile", "Profile not deleted!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    	
    }

    return result;
  }

  // //////////////////////////////////////////////////////

  /**
   * Get an Organizational Unit
   * 
   * @param userInfo - The administrator creating the new profile
   * @return - UserInfo array with all users
   * @ejb.interface-method view-type = "remote"
   */
  public OrganizationalUnitViewInterface getOrganizationalUnit(UserInfoInterface userInfo, String unitId) {
    OrganizationalUnitViewInterface [] result = getOrganizationalUnits(userInfo, unitId);
    if(result.length > 0) return result[0];
    return new OrganizationalUnitView(new HashMap<String,String>());
  }

  /**
   * Get an Organization
   * 
   * @param userInfo - The administrator creating the new profile
   * @param orgId - Organization ID
   * @return - OrganizationView array with all users
   * @ejb.interface-method view-type = "remote"
   */
  public OrganizationViewInterface getOrganization(UserInfoInterface userInfo, String orgId) {
    OrganizationViewInterface [] result = getOrganizations(userInfo, orgId);
    if(result.length > 0) return result[0];
    return new OrganizationView(new HashMap<String,String>());
  }

  
  public UserViewInterface findUser(UserInfoInterface userInfo, String username) throws IllegalAccessException {
    return findOrganizationUser(userInfo, null, username);
  }
  
  public UserViewInterface findOrganizationUser(UserInfoInterface userInfo, String orgId, String username) throws IllegalAccessException {
    if (StringUtils.isNotEmpty(orgId) && !StringUtils.equals(orgId, userInfo.getCompanyID()) && !userInfo.isSysAdmin()) {
      throw new IllegalAccessException("Only sysadmin can find users in another organization");
    }
    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin()) {
      throw new IllegalAccessException("Not sysadmin nor org admin");
    }
    UserViewInterface [] result = findUsers(userInfo, username, orgId, false);
    if(result.length > 0) return result[0];
    return new UserView(new HashMap<String,String>());
  }
  
  /**
   * Get an User
   * 
   * @param userInfo - 
   * @param userId - User ID
   * @return - UserInfo array with all users
   * @ejb.interface-method view-type = "remote"
   */
  public UserViewInterface getUser(UserInfoInterface userInfo, String userId) {
    UserViewInterface [] result = getUsers(userInfo, userId);
    if(result.length > 0) return result[0];
    return new UserView(new HashMap<String,String>());
  }

  /**
   * @see pt.iflow.api.core.UserManager#getProfile(pt.iflow.api.utils.UserInfoInterface,
   *      java.lang.String)
   * @ejb.interface-method view-type = "remote"
   */
  public ProfilesTO getProfile(UserInfoInterface userInfo, String profileid) {
    ProfilesTO profile = null;
    ProfilesTO[] profiles = getProfiles(userInfo, null, profileid);
    if(profiles.length > 0) {
      profile = profiles[0];
    }
    return profile;
  }

  /**
   * Get all users
   * 
   * @param userInfo - The administrator creating the new profile
   * @return - UserInfo array with all users
   * @ejb.interface-method view-type = "remote"
   */
  public UserViewInterface[] getAllUsers(UserInfoInterface userInfo) {
    return getAllUsers(userInfo, false);
  }

  /**
   * Get all users for the given user's organizational unit
   * @param userInfo - The "reference" user
   * @return - UserView array with all users
   */
  public UserViewInterface[] getAllUsers( UserInfoInterface userInfo, boolean filterByOrgUnit ) {
    return findUsers(userInfo, null, null, filterByOrgUnit);
  }


  public OrganizationalUnitViewInterface[] getAllOrganizationalUnits(UserInfoInterface userInfo) throws IllegalAccessException {
    return getAllOrganizationalUnits(userInfo, null);
  }
  
  public OrganizationalUnitViewInterface[] getAllOrganizationalUnits(UserInfoInterface userInfo, String orgId) throws IllegalAccessException {
    
    if (StringUtils.isNotEmpty(orgId) && !userInfo.isSysAdmin()) {
      Logger.error(userInfo.getUtilizador(), this, "getAllOrganizationalUnits", 
          "Trying to get organizational units for " + orgId + " and not sysadmin");
      throw new IllegalAccessException("Only SysAdm can get all organizational units for an organization");
    }
    
    // return getOrganizationalUnits(userInfo, null);
    OrganizationalUnitView[] ouViewArray = getOrderedOrganizationalUnits(userInfo, orgId);
    TreeSet<OrganizationalUnitViewInterface> tree = new TreeSet<OrganizationalUnitViewInterface>(
        new Comparator<OrganizationalUnitViewInterface>() {
          public int compare(OrganizationalUnitViewInterface o1,
              OrganizationalUnitViewInterface o2) {
            return o1.getName().compareTo(o2.getName());
          }

        });
    for (OrganizationalUnitViewInterface o : ouViewArray) {
      tree.add(o);
    }

    ouViewArray = tree.toArray(ouViewArray);

    return ouViewArray;
  }

  /**
   * Get all Organizations
   * 
   * @param userInfo - The administrator creating the new profile
   * @return - UserInfo array with all users
   * @ejb.interface-method view-type = "remote"
   */
  public OrganizationViewInterface[] getAllOrganizations(UserInfoInterface userInfo) {
    return getOrganizations(userInfo, null);
  }

  /**
   * @see pt.iflow.api.core.UserManager#getAllProfiles(pt.iflow.api.utils.UserInfoInterface)
   * @ejb.interface-method view-type = "remote"
   */
  public ProfilesTO[] getAllProfiles(UserInfoInterface userInfo) {
    return getProfiles(userInfo, null, null);
  }


  /**
   * Get user profiles
   * 
   * @param userInfo - The administrator creating the new profile
   * @param userId - Get all profiles from this user
   * @return - UserInfo array with all users
   * @ejb.interface-method view-type = "remote"
   */
  public String[] getUserProfiles(UserInfoInterface userInfo, String userId) {
    String [] result = new String[0];

    Logger.debug(userInfo.getUtilizador(), this, "getUserProfiles", "Listing all profiles");

    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "getUserProfiles", "not administrator, exiting");
      return new String[0];
    }

    Collection<Map<String,String>> coll = DatabaseInterface.executeQuery("select PROFILEID from userprofiles where USERID="+userId);

    int size = coll.size();
    int i = 0;
    result = new String[size];
    Iterator<Map<String,String>> iter = coll.iterator();
    while(iter.hasNext()) {
      Map<String,String> mapping = iter.next();
      result[i] = (String) mapping.get("PROFILEID");
      i++;
    }

    return result;
  }

  /**
   * Get profile users
   * 
   * @param userInfo - The administrator creating the new profile
   * @param profileId - Get all users in this profile
   * @return - UserInfo array with all users
   * @ejb.interface-method view-type = "remote"
   */
  public String[] getProfileUsers(UserInfoInterface userInfo, String profileId) {
    String [] result = new String[0];

    Logger.debug(userInfo.getUtilizador(), this, "getProfileUsers", "Listing all users");

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "getProfileUsers", "not administrator, exiting");
      return new String[0];
    }

    Collection<Map<String,String>> coll = DatabaseInterface.executeQuery("select USERID from userprofiles where PROFILEID="+profileId);

    int size = coll.size();
    int i = 0;
    result = new String[size];
    Iterator<Map<String,String>> iter = coll.iterator();
    while(iter.hasNext()) {
      Map<String,String> mapping = iter.next();
      result[i] = (String) mapping.get("USERID");
      i++;
    }

    return result;
  }

  public String getOrganizationalUnitManager(UserInfoInterface userInfo, String unitId) {
    Logger.info(userInfo.getUtilizador(), this, "getOrganizationalUnitManager", "getting manager for unit " + unitId);

    Collection<Map<String,String>> ucol = DatabaseInterface.executeQuery("select USERID from unitmanagers where UNITID="+unitId);
    if(ucol != null && ucol.size() > 0) {
      Map<String,String> tmpMap = ucol.iterator().next();
      if(tmpMap != null) {
        return (String) tmpMap.get("USERID");
      }
    }
    return null;
  }


  public boolean isOrganizationalUnitManager(UserInfoInterface userInfo) {
    String manager = getOrganizationalUnitManager(userInfo, userInfo.getOrgUnitID());
    return (StringUtils.isNotEmpty(manager) && manager.equals(userInfo.getUserId()));
  }


  //// Auxiliary methods

  /**
   * Get all Organizational Units
   * Nota: deveria ser transaccional.... :(
   * 
   */
  private OrganizationalUnitViewInterface[] getOrganizationalUnits(UserInfoInterface userInfo, String unitId) {
    OrganizationalUnitViewInterface[] result = new OrganizationalUnitViewInterface[0];

    Logger.debug(userInfo.getUtilizador(), this, "getAllOrganizationalUnits", "Listing all getAllOrganizationalUnits");

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "getAllOrganizationalUnits", "not administrator, exiting");
      return new OrganizationalUnitViewInterface[0];
    }

    String append = " WHERE organizationid = " + userInfo.getCompanyID();
    if(unitId != null) {
      append += " and UNITID = "+unitId;
    }

    // TODO inicio de transaccao...

    Collection<Map<String,String>> coll = DatabaseInterface.executeQuery("select UNITID,PARENT_ID,ORGANIZATIONID,NAME,DESCRIPTION from organizational_units"+append);

    int size = coll.size();
    int i = 0;
    result = new OrganizationalUnitViewInterface[size];
    Iterator<Map<String,String>> iter = coll.iterator();
    while(iter.hasNext()) {
      Map<String,String> mapping = iter.next();
      result[i] = new OrganizationalUnitView(mapping);

      // Get Manager ID
      String manId = getOrganizationalUnitManager(userInfo, result[i].getUnitId());
      mapping = new HashMap<String,String>(mapping);
      mapping.put(OrganizationalUnitViewInterface.MANAGERID, manId);
      result[i] = new OrganizationalUnitView(mapping);

      i++;
    }

    return result;
  }


  private static String lineage(String unitID, Map<String,Map<String, String>> orgUnits, String baseUnitID, String rootUnitID) {


    Map<String, String> h = orgUnits.get(unitID);
    Map<String, String> hlocal = orgUnits.get(baseUnitID);


    String parentId = (String) h.get("PARENT_ID");
    if(null==parentId) return null;

    String level = (String)hlocal.get("LEVEL");
    int nLevel = 1;
    if (level == null) {
      hlocal.put("LEVEL", "1");
    }
    else {
      try {
        nLevel = Integer.parseInt(level) + 1;
      }
      catch (Exception e) {
      }
      hlocal.put("LEVEL", String.valueOf(nLevel));
    }     

    if ((rootUnitID != null && rootUnitID.equals(unitID)) || (rootUnitID == null && parentId.equals("-1"))) return (String) h.get("BASE_NAME");
    if (rootUnitID != null && parentId.equals("-1")) return null;
    String lineage = lineage(parentId, orgUnits, baseUnitID, rootUnitID);
    return (lineage == null) ? null : lineage+"\n"+h.get("BASE_NAME");    
  }

  /**
   * Get all Organizational Units
   * Nota: deveria ser transaccional.... :(
   * 
   */

  private OrganizationalUnitView[] getOrderedOrganizationalUnits(UserInfoInterface userInfo, String orgId) {
    OrganizationalUnitView[] result = new OrganizationalUnitView[0];

    Logger.debug(userInfo.getUtilizador(), this, "getOrderedOrganizationalUnits", "Listing all getOrderedOrganizationalUnits");

    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin() && !userInfo.isUnitManager()) {
      Logger.debug(userInfo.getUtilizador(), this, "getOrderedOrganizationalUnits", "not sysadmin, administrator or unit manager, exiting");
      return new OrganizationalUnitView[0];
    }

    // TODO inicio de transaccao...     
    if (orgId == null)
      orgId = userInfo.getCompanyID();
    
    //String query = DBQueryManager.processQuery("UserManager.GET_ORGANIZATIONALUNITS", orgId);
    
    //Map<String, Map<String,String>> orgUnits = DatabaseInterface.executeQuery(query, "UNITID");
    
    Map<String, Map<String,String>> orgUnits = DatabaseInterface.executeQuery("select UNITID,PARENT_ID,ORGANIZATIONID,NAME as BASE_NAME,DESCRIPTION from organizational_units WHERE organizationid = " + orgId, "UNITID");

    String rootUnitID = null;
    if (!userInfo.isOrgAdmin() && userInfo.isUnitManager()) {
      rootUnitID = userInfo.getOrgUnitID();
    }

    Map<String,String> lineageCache = new HashMap<String, String>(); 
    Iterator<Map<String,String>> it = orgUnits.values().iterator(); 
    while(it.hasNext()) {
      Map<String, String> unitData = it.next();
      String unitID = (String) unitData.get("UNITID");
      String unitFullName = lineage(unitID, orgUnits, unitID, rootUnitID);

      if (unitFullName != null)
        lineageCache.put(unitID, unitFullName);
    }


    int size = lineageCache.size();
    int i = 0;
    result = new OrganizationalUnitView[size];
    it = orgUnits.values().iterator();
    while(it.hasNext()) {
      Map<String, String> unitData = it.next();
      String unitID = (String) unitData.get("UNITID");
      if (!lineageCache.containsKey(unitID))
        continue;
      String unitFullName = lineageCache.get(unitID);
      unitData.put("NAME", unitFullName);

      result[i] = new OrganizationalUnitView(unitData);

      // Get Manager ID
      String manId = getOrganizationalUnitManager(userInfo, result[i].getUnitId());
      unitData = new HashMap<String,String>(unitData);
      unitData.put(OrganizationalUnitViewInterface.MANAGERID, manId);
      result[i] = new OrganizationalUnitView(unitData);

      i++;
    }
   
    return result;
  }

  private UserViewInterface[] findUsers(UserInfoInterface userInfo, String username, String orgId, boolean filterByOrgUnit) {
    return getUsers(userInfo, true, null, username, orgId, filterByOrgUnit);
  }
  
  private UserViewInterface[] getUsers(UserInfoInterface userInfo, String userId) {
    return getUsers(userInfo, false, userId, null, null, false);
  }

  /**
   * Get all users
   */
  private UserViewInterface[] getUsers(UserInfoInterface userInfo, boolean find, String userId, String username, String orgId, boolean filterByOrgUnit) {
    UserViewInterface[] result = new UserViewInterface[0];

    Logger.debug(userInfo.getUtilizador(), this, "getAllUsers", "Listing all users");

    String query = "";
    if(!find && userInfo.isSysAdmin()) {
      String append = ""; 
      if(userId != null) {
        append = " WHERE u.USERID = "+userId;
      }

      //query = "select u.USERID,u.USERNAME,u.EMAIL_ADDRESS,u.FIRST_NAME,u.LAST_NAME,u.PHONE_NUMBER,u.MOBILE_NUMBER from system_users u" + append;
      query = DBQueryManager.processQuery("UserManager.GET_USERS_ADMIN", append);
    }
    else {
      String append = " WHERE u.unitid = ou.unitid and ou.organizationid = o.organizationid";
      if(userId != null) {
        append += " AND u.USERID = "+userId;
      }
      else if (username != null) {
        append += " AND u.USERNAME = '"+StringEscapeUtils.escapeSql(username)+"'";
      }
      if (orgId == null)
        orgId = userInfo.getCompanyID();
      append += " AND o.ORGANIZATIONID = " + orgId;

      if (filterByOrgUnit)
        append += " AND u.unitid = " + userInfo.getOrgUnitID();
      
      String appendExtras = "";
      Map<String, String> mapExtra = AccessControlManager.getUserDataAccess().getMappingExtra();
      if (mapExtra!= null && mapExtra.size()>0) {
        for (String key : mapExtra.keySet()) {
          appendExtras += ",u." + mapExtra.get(key) + " as " + key;          
        }
      }
  
      //query = "select u.USERID,u.UNITID,u.USERNAME,u.EMAIL_ADDRESS,u.GENDER,u.FIRST_NAME,u.LAST_NAME,u.PHONE_NUMBER,u.FAX_NUMBER,u.MOBILE_NUMBER,u.COMPANY_PHONE,u.ACTIVATED,u.ORGADM"+appendExtras+" from users u, organizations o, organizational_units ou"+append;
      query = DBQueryManager.processQuery("UserManager.GET_USERS", appendExtras,append);
    }

    Collection<Map<String,String>> coll = DatabaseInterface.executeQuery(query);

    int size = coll.size();
    int i = 0;
    result = new UserViewInterface[size];
    Iterator<Map<String,String>> iter = coll.iterator();
    while(iter.hasNext()) {
      Map<String,String> mapping = iter.next();
      result[i] = new UserView(mapping);
      i++;
    }

    return result;
  }




  /**
   * Get all Organizations
   */
  private OrganizationViewInterface[] getOrganizations(UserInfoInterface userInfo, String orgId) {
    OrganizationViewInterface[] result = new OrganizationViewInterface[0];

    Logger.debug(userInfo.getUtilizador(), this, "getAllOrganizations", "Listing all getAllOrganizationalUnits");

    if (!userInfo.isSysAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "getAllOrganizations", "not administrator, exiting");
      return new OrganizationViewInterface[0];
    }

    String append = " WHERE ORGANIZATIONID <> " + Const.SYSTEM_ORGANIZATION;
    if(orgId != null) {
      append += " AND ORGANIZATIONID = "+orgId;
    }

    Collection<Map<String,String>> coll = DatabaseInterface.executeQuery("select ORGANIZATIONID,NAME,DESCRIPTION,LOCKED from organizations"+append);
 
    int size = coll.size();
    int i = 0;
    result = new OrganizationViewInterface[size];
    Iterator<Map<String,String>> iter = coll.iterator();
    while(iter.hasNext()) {
      Map<String,String> mapping = iter.next();
      result[i] = new OrganizationView(mapping);
      i++;
    }

    return result;
  }

  public ProfilesTO[] getOrganizationProfiles(UserInfoInterface sysadminUserInfo, String orgId) {
    if (!sysadminUserInfo.isSysAdmin()) {
      Logger.error(sysadminUserInfo.getUtilizador(), this, "getAllProfiles", "not sysadmin nor orgadmin, exiting");
      return new ProfilesTO[0];
    }
    return getProfiles(sysadminUserInfo, orgId, null);
  }
  
  /**
   * Retrieve profiles from DB.
   * 
   * @param userInfo
   *          User information for profile lookup.
   * @param profileId
   *          Specific profile to retrieve, if 'null' then retrieve all
   *          profiles.
   * @return Profiles for the given user's organization. If 'profileId' is
   *         'null' then retrieve all profiles, otherwise return only profile
   *         with given id.
   */
  private ProfilesTO[] getProfiles(UserInfoInterface userInfo, String orgId, String profileId) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "getAllProfiles", "Listing all profiles");
    }

    ProfilesTO[] result = new ProfilesTO[0];
    DataSource ds = null;
    Connection db = null;
    ResultSet rs = null;
    Statement st = null;

    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin()) {
      Logger.error(userInfo.getUtilizador(), this, "getAllProfiles", "not sysadmin nor orgadmin, exiting");
      return new ProfilesTO[0];
    }
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      String extra = "";
      String extra2 = "";
      
//      StringBuffer sql = new StringBuffer();
//      sql.append("SELECT " + ProfilesTO.PROFILE_ID);
//      sql.append(", " + ProfilesTO.NAME);
//      sql.append(", " + ProfilesTO.DESCRIPTION);
//      sql.append(", " + ProfilesTO.ORGANIZATION_ID);
//      sql.append(" FROM " + ProfilesTO.TABLE_NAME);
         
      if (StringUtils.isEmpty(orgId))
        orgId = userInfo.getCompanyID();
      
      //sql.append(" WHERE " + ProfilesTO.ORGANIZATION_ID + " LIKE '" + orgId + "'");
      extra = " WHERE " + ProfilesTO.ORGANIZATION_ID + " LIKE '" + orgId + "'";
      if(profileId != null) {
        //sql.append(" AND " + ProfilesTO.PROFILE_ID + "=" + profileId);
        extra2 = " AND " + ProfilesTO.PROFILE_ID + "=" + profileId;
      }

      String query = DBQueryManager.processQuery("UserManager.GET_PROFILES", ProfilesTO.PROFILE_ID,ProfilesTO.NAME,ProfilesTO.DESCRIPTION,ProfilesTO.ORGANIZATION_ID,ProfilesTO.TABLE_NAME,extra,extra2);
      
      
      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "getProfiles", "QUERY=" + query);
      }

      st = db.createStatement();
      rs = st.executeQuery(query);
      List<ProfilesTO> profiles = new ArrayList<ProfilesTO>();
      while (rs.next()) {
        int profileid = rs.getInt(ProfilesTO.PROFILE_ID);
        String name = rs.getString(ProfilesTO.NAME);
        String description = rs.getString(ProfilesTO.DESCRIPTION);
        String organizationid = rs.getString(ProfilesTO.ORGANIZATION_ID);
        profiles.add(new ProfilesTO(profileid, name, description, organizationid));
      }
      result = profiles.toArray(new ProfilesTO[profiles.size()]);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getProfiles", "Could not retrieve profiles!", e);
    } finally {
      //DatabaseInterface.closeResources(db, st, rs);
    	try { db.close(); } catch (SQLException e) {}
    	try { st.close(); } catch (SQLException e) {}
    	try { rs.close(); } catch (SQLException e) {}
    	
    }

    return result;
  }


  /**
   * Register a new user and organization
   * 
   * @param orgName - Organization Name
   * @param orgDescription - Organization Description
   * @param username - String com o username do utilizador
   * @param password - String com a password em plain-text
   * @param gender - User gender (M/F)
   * @param emailAddress - User email address
   * @param firstName - First Name
   * @param lastName - Last Name
   * @param phoneNumber - Phone Number
   * @param faxNumber - Fax Number
   * @param mobileNumber - Mobile Number
   * @param companyPhone - Company Phone
   * @return - true if user was created successfully
   */
  public int newRegistration( String orgName, String orgDescription,String username,String password,String gender,String emailAddress,String firstName,String lastName,String phoneNumber,String faxNumber,String mobileNumber,String companyPhone, String lang, String timezone) {
    int result = ERR_INTERNAL;


    // validate password with something
    if(invalidPassword(password))
      return ERR_PASSWORD;

    if(Const.bUSE_EMAIL) {  // test email address
      if(invalidEmail(emailAddress))
        return ERR_INVALID_EMAIL;
    }

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int orgId = -1;
    int unitId = -1;
    int userId = -1;
    String activationCode = generateCode(orgName, username); // generate random code with 40 bytes length

    Logger.info(username, this, "newRegistration", "Registering a new user and organization");

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      // check if organization exists
      pst = db.prepareStatement("select organizationid from organizations where upper(name)=?");
      pst.setString(1, orgName.toUpperCase());
      rs = pst.executeQuery();
      if(rs.next()) {
        throw new RegisterException(ERR_ORGANIZATION_EXISTS, "Organization exists");
      }
      //DatabaseInterface.closeResources(pst, rs);
  	try { db.close(); } catch (SQLException e) {}
  	try { pst.close(); } catch (SQLException e) {}
      pst = null;
      rs = null;

//    // check if username or email exist and belongs to the same organization. 
      pst = db.prepareStatement("select userid from users where username=?"); // TODO change to user
      pst.setString(1, username);
      rs = pst.executeQuery();
      if(rs.next()) {
        throw new RegisterException(ERR_USER_EXISTS, "User exists");
      }
      //DatabaseInterface.closeResources(pst, rs);
  	try { db.close(); } catch (SQLException e) {}
  	try { pst.close(); } catch (SQLException e) {}
      pst = null;
      rs = null;
      
      
//    pst = db.prepareStatement("select userid from users where email_address=? and ");
//    pst.setString(1, emailAddress);
//    rs = pst.executeQuery();
//    if(rs.next()) {
//    throw new RegisterException(ERR_EMAIL_EXISTS, "Email address already registered to a user");
//    }

      // create organization
      pst = db.prepareStatement("insert into organizations (NAME,DESCRIPTION) values (?,?)", new String[]{"organizationid"});
      pst.setString(1, orgName);
      pst.setString(2, orgDescription);
      pst.executeUpdate();
      rs = pst.getGeneratedKeys();
      if(rs.next()) {
        orgId = rs.getInt(1);
      }
      //DatabaseInterface.closeResources(pst, rs);
  	try { db.close(); } catch (SQLException e) {}
  	try { pst.close(); } catch (SQLException e) {}
      pst = null;
      rs = null;
      Logger.debug(username, this, "newRegistration", "Organization created successfully");

      // Create default organizational unit
      pst = db.prepareStatement("insert into organizational_units (ORGANIZATIONID,PARENT_ID,NAME,DESCRIPTION) values (?,?,?,?)", new String[]{"unitid"});
      pst.setInt(1, orgId);
      pst.setString(2, "-1");
      pst.setString(3, "iFlowOU");
      pst.setString(4, "iFlowOU");
      pst.executeUpdate();
      rs = pst.getGeneratedKeys();
      if(rs.next()) {
        unitId = rs.getInt(1);
      }
      //DatabaseInterface.closeResources(pst, rs);
  	try { db.close(); } catch (SQLException e) {}
  	try { pst.close(); } catch (SQLException e) {}
      pst = null;
      rs = null;
      Logger.debug(username, this, "newRegistration", "Organizational Unit created successfully");



      // Create the user
      pst = db.prepareStatement("insert into users (GENDER,UNITID,USERNAME,USERPASSWORD,EMAIL_ADDRESS,FIRST_NAME,LAST_NAME,PHONE_NUMBER,FAX_NUMBER,MOBILE_NUMBER,COMPANY_PHONE,ACTIVATED,PASSWORD_RESET,ORGADM) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,0,1)", new String[]{"userid"});
      pst.setString(1, gender);
      pst.setInt(2, unitId);
      pst.setString(3, username);
      pst.setString(4, Utils.encrypt(password));
      pst.setString(5, emailAddress == null ? "" : emailAddress);
      pst.setString(6, firstName);
      pst.setString(7, lastName);
      pst.setString(8, phoneNumber);
      pst.setString(9, faxNumber);
      pst.setString(10, mobileNumber);
      pst.setString(11, companyPhone);
      pst.setInt(12, Const.bUSE_EMAIL?0:1);
      pst.executeUpdate();
      rs = pst.getGeneratedKeys();
      if(rs.next()) {
        userId = rs.getInt(1);
      }
      //DatabaseInterface.closeResources(pst, rs);
  	try { db.close(); } catch (SQLException e) {}
  	try { pst.close(); } catch (SQLException e) {}
      pst = null;
      rs = null;
      Logger.debug(username, this, "newRegistration", "User created successfully");

      // add user as unit manager
      pst = db.prepareStatement("insert into unitmanagers (USERID, UNITID) values (?,?)");
      pst.setInt(1, userId);
      pst.setInt(2, unitId);
      pst.executeUpdate();
      //DatabaseInterface.closeResources(pst);
  	  	try { pst.close(); } catch (SQLException e) {}
      pst = null;
      Logger.debug(username, this, "newRegistration", "Unit manager registered successfully");

      // XXX Isto eh para remover quando se perceber o que fazer com isto.
//      // set default theme
//      pst = db.prepareStatement("insert into organization_theme (ORGANIZATIONID,THEME,STYLE_URL,LOGO_URL) values (?,?,?,?)");
//      pst.setString(1, orgName);
//      pst.setString(2, "default");
//      pst.setString(3, "PublicFiles/iflow.css");
//      pst.setString(4, "Logo");
//      pst.executeUpdate();
//      pst.close();
//
      // create activation code
      if(Const.bUSE_EMAIL) {
        pst = db.prepareStatement("insert into user_activation (USERID,ORGANIZATIONID,UNITID,CODE) values (?,?,?,?)");
        pst.setInt(1, userId);
        pst.setInt(2, orgId);
        pst.setInt(3, unitId);
        pst.setString(4, activationCode);
        pst.executeUpdate();
        //DatabaseInterface.closeResources(pst);
    	try { pst.close(); } catch (SQLException e) {}
        pst = null;
      }

      db.commit();
      result = ERR_OK;
    }
    catch (SQLException e) {
      Logger.warning(username, this, "newRegistration", "User not created!", e);
      try {                
        if (db != null) {
          db.rollback();
          Logger.info(username, this, "newRegistration", "Connection rolledback");
        }
      }
      catch (Exception e2) {
        Logger.warning(username, this, "newRegistration", "error rolling back connection", e2);
      }
    } 
    catch(RegisterException e) {
      result = e.getErrorCode();
      Logger.info(username, this, "newRegistration", "Errorcode: "+e.getErrorCode()+": "+e.getMessage());
      try {
        if (db != null) {
          db.rollback();
          Logger.info(username, this, "newRegistration", "Connection rolledback");
        }
      }
      catch (Exception e2) {
        Logger.warning(username, this, "newRegistration", "error rolling back connection", e2);
      }
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    }

    // Notify users
    if(result == ERR_OK) {
      UserInfoInterface newUser = BeanFactory.getUserInfoFactory().newUserInfoEvent(new NewUserEvent(), username);
      // update organization settings
      String [] toks = lang.split("_");
      // TODO user settings have default tutorial option - need to be verified
      BeanFactory.getSettingsBean().updateOrganizationSettings(newUser, toks[0], toks[1], timezone);
      BeanFactory.getSettingsBean().updateUserSettings(newUser, toks[0], toks[1], timezone, Tutorial.TUTORIAL_DEFAULT, true, true);
      if(Const.bUSE_EMAIL) {
        try {
          // send email
          result = ERR_EMAIL;
          Hashtable<String,String> ht = new Hashtable<String,String>();
          ht.put("username", username);
          ht.put("password", password);
          ht.put("url", "http://"+Const.APP_HOST+":"+Const.APP_PORT+"/iFlow/confirm?activation=account&code="+activationCode);

          // email with default configuration
          Email email = EmailManager.buildEmail(ht, EmailManager.getEmailTemplate(newUser, "new_register"));
          email.setTo(emailAddress);
          if(email.sendMsg()) {
            result = ERR_OK;
            Logger.info(username, this, "newRegistration", "User registered successfully");
          }
        } catch (Exception e) {
          result = ERR_EMAIL;
          Logger.warning(username, this, "newRegistration", "User email not sent!", e);
        }
      }
    }
    return result;
  }

  private static boolean invalidPassword(String password) {
    // todo: perform adicional checks to password
    return null == password || password.length()<4;
  }

  private static boolean invalidEmail(String email) {
    return !Validate.isValidEmail(email);
  }

  private static String generateCode(String org, String user) {
    return RandomStringUtils.random(40, true, true);
  }

  public boolean organizationExists(String orgName) {
    boolean result = false;

    Logger.debug(orgName, this, "organizationExists", "Checking if organization "+orgName+" exists...");
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      pst = db.prepareStatement("select ORGANIZATIONID from organizations where upper(name)=?");
      pst.setString(1, orgName.toUpperCase());
      rs = pst.executeQuery();
      result = rs.next();

    } catch (SQLException e) {
      result = false;
      Logger.warning(orgName, this, "organizationExists", "Error searching organization "+orgName, e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    }
    return result;
  }

  public UserCredentials confirmAccount(String code) {
    UserCredentials result = null;

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int userId = -1;

    Logger.info("ADMIN", this, "confirmAccount", "Registering a new user and organization");

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      // Check control code. If organization is locked, do not activate user.
      pst = db.prepareStatement("select u.USERID from user_activation u, organizations o where CODE=? and u.organizationid=o.organizationid and o.locked=0");
      pst.setString(1, code);
      rs = pst.executeQuery();
      boolean codeFound = false;
      if(rs.next()) {
        userId = rs.getInt(1);
        codeFound = true;
      }
      //rs.close();
      //pst.close();

      if(codeFound) {
        Logger.debug("ADMIN", this, "confirmAccount", "Activation code found!");

        pst = db.prepareStatement("update users set activated=1 where userid=?");
        pst.setInt(1, userId);
        pst.executeUpdate();
        pst.close();

        pst = db.prepareStatement("delete from user_activation where CODE=?");
        pst.setString(1, code);
        pst.executeUpdate();
        pst.close();

        // prepare a credentials object to auto login user
        pst = db.prepareStatement("select username,userpassword from users where userid=?");
        pst.setInt(1, userId);
        rs = pst.executeQuery();
        if(rs.next()) {
          result = new UserCredentialsImpl(rs.getString("username"),rs.getString("userpassword"));
        } else {
          // este vai ser um caso bicudo... Existe userId, mas nao existe user??
          throw new SQLException("User does not exist?");
        }
        //rs.close();
        //pst.close();


        db.commit();
        Logger.info("ADMIN", this, "confirmAccount", "User activated successfully");
      } else {
        Logger.debug("ADMIN", this, "confirmAccount", "Activation code not found");
      }
    }
    catch (SQLException e) {
      result = null;
      Logger.warning("ADMIN", this, "confirmAccount", "User not activated!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    }

    return result;
  }

  private static class UserCredentialsImpl implements UserCredentials {

    private String password;
    private String username;

    UserCredentialsImpl(String username, String password) {
      this.username = username;
      this.password = Utils.decrypt(password);
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }
  }

  public boolean resetPassword(String username) {
    Logger.info(username, this, "resetPassword", "Resetting password for user "+username);
    return resetPassword(true, username);
  }


  public boolean resetPassword(UserInfoInterface userInfo, String sUserId) {
    if(userInfo == null || !(userInfo.isOrgAdmin() || userInfo.isSysAdmin())) {
      Logger.info(userInfo.getUtilizador(), this, "resetPassword", "Resetting user password");
      return false;
    }

    Logger.info(userInfo.getUtilizador(), this, "resetPassword", "Resetting password for user "+sUserId);

    return resetPassword(false, sUserId);
  }


  private boolean resetPassword(boolean isUserName, String user) {
    if(!Const.bUSE_EMAIL) return false; // dont reset if no email since password is sent by email!! 

    boolean result = false;
    // get username
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int userId = -1;
    String password = null;
    boolean userFound = false;
    String emailAddress = null;
    String username = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      // create organization
      if(isUserName)
        pst = db.prepareStatement("select userid,email_address,username from users where username=?");
      else
        pst = db.prepareStatement("select userid,email_address,username from users where userid=?");
      pst.setString(1, user);
      rs = pst.executeQuery();
      if(rs.next()) {
        userId = rs.getInt(1);
        emailAddress = rs.getString(2);
        username = rs.getString(3);
        userFound = true;
      }
      rs.close();
      pst.close();

      if(userFound) {
        Logger.debug("ADMIN", this, "resetPassword", "User email found. Generating new password.");
        // Generate a new dummy password...
        password =  RandomStringUtils.random(8, true, true);

        pst = db.prepareStatement("update users set password_reset=1, userpassword=? where userid=?");
        pst.setString(1, Utils.encrypt(password));
        pst.setInt(2, userId);
        pst.executeUpdate();
        pst.close();

        db.commit();
        Logger.info("ADMIN", this, "resetPassword", "User password reset successfully");
        result = true;
      } else {
        Logger.debug("ADMIN", this, "resetPassword", "User not found");
      }
    }
    catch (SQLException e) {
      Logger.warning("ADMIN", this, "resetPassword", "Password not reset!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    }


    if(userFound) {
      try {
        // send email
        Hashtable<String,String> ht = new Hashtable<String,String>();
        ht.put("password", password);
        ht.put("username", username);
        Email email = EmailManager.buildEmail(ht, EmailManager.getEmailTemplate(null, "password_reset"));
        email.setTo(emailAddress);
        if(email.sendMsg()) {
          Logger.info(user, this, "resetPassword", "Notification email sent successfully");
        }
      } catch (Exception e) {
        Logger.warning(user, this, "resetPassword", "Notification email not sent!", e);
      }
    }

    return result;
  }

  public int changePassword(String username, String oldPassword, String password) {
    // get username
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int colsModified = -1;

    Logger.info("ADMIN", this, "changetPassword", "Changing user password");

    // validate password with something
    if(invalidPassword(password))
      return ERR_PASSWORD;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      // change organization
      pst = db.prepareStatement("update users set password_reset=0, userpassword=? where username=? and userpassword=?");
      pst.setString(1, Utils.encrypt(password));
      pst.setString(2, username);
      pst.setString(3, Utils.encrypt(oldPassword));
      colsModified = pst.executeUpdate();
      pst.close();

      db.commit();
      Logger.info("ADMIN", this, "changetPassword", "User password changed successfully");
    }
    catch (SQLException e) {
      Logger.warning("ADMIN", this, "changetPassword", "Password not changed!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    }


    return colsModified==1?ERR_OK:ERR_INTERNAL;
  }

 public int changePasswordAdmin(String username, String oldPassword, String password) {
    
    // get username
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int colsModified = -1;

    Logger.info("ADMIN", this, "changetPassword", "Changing system administrator password");

    // validate password with something
    if(invalidPassword(password))
      return ERR_PASSWORD;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      pst = db.prepareStatement("update system_users set userpassword=? where username=? and userpassword=?");
      pst.setString(1, Utils.encrypt(password));
      pst.setString(2, username);
      pst.setString(3, Utils.encrypt(oldPassword));
      colsModified = pst.executeUpdate();
      pst.close();

      Logger.info("ADMIN", this, "changetPassword", "administrator password changed successfully");
    }
    catch (SQLException e) {
      Logger.warning("ADMIN", this, "changetPassword", "Password not changed!", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    }


    return colsModified==1?ERR_OK:ERR_INTERNAL;
  }


  private static final int ACT_NONE = 0;
  private static final int ACT_CONFIRM = 1;
  private static final int ACT_REVERT = 2;

  public int confirmEmailAddress(String action, String key) {
    if(!Const.INSTALL_WEB.equals(Const.INSTALL_TYPE)) return CONFIRM_NOT_USED; // nothing to do...

    int nAction = ACT_NONE;
    if(StringUtils.equalsIgnoreCase(action, "confirm")) nAction = ACT_CONFIRM;
    else if(StringUtils.equalsIgnoreCase(action, "revert")) nAction = ACT_REVERT;
    else return CONFIRM_ERROR; // unknown action


    int result = CONFIRM_ERROR;
    // get username
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    Logger.info("ADMIN", this, "confirmEmailAddress", "Confirming user email. Action="+action);

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      if(nAction == ACT_CONFIRM) {
        int userId = -1;
        String email = null;

        pst = db.prepareStatement("select userid,email from email_confirmation where code=?");
        pst.setString(1, key);
        rs = pst.executeQuery();
        if(rs.next()) {
          userId = rs.getInt(1);
          email = rs.getString(2);
        } else {
          throw new SQLException("Key does not exist");
        }
        //rs.close();
        //pst.close();

        pst = db.prepareStatement("update users set email_address = ? where userid=?");
        pst.setString(1, email);
        pst.setInt(2, userId);
        pst.executeUpdate();
        //pst.close();

      }

      // common to both confirm and revert.

      // change organization
      pst = db.prepareStatement("delete from email_confirmation where code=?");
      pst.setString(1, key);
      pst.executeUpdate();
      //pst.close();

      db.commit();
      result = (nAction == ACT_CONFIRM?CONFIRM_EMAIL_CONFIRMED:CONFIRM_EMAIL_REVERTED);
      Logger.info("ADMIN", this, "confirmEmailAddress", "User email updated");
    }
    catch (SQLException e) {
      result = CONFIRM_ERROR;
      Logger.warning("ADMIN", this, "confirmEmailAddress", "Could not update email address.");
      e.printStackTrace();
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    }


    return result;
  }

  public List<String> getSystemUsers(UserInfoInterface userInfo) {	
	  Logger.trace(this, "getSystemUsers", userInfo.getUtilizador() + " call");
	  
	  DataSource ds = null;
	  Connection db = null;
	  PreparedStatement pst = null;
	  ResultSet rs = null;

	  try {
		  ds = Utils.getDataSource();
		  db = ds.getConnection();
		  db.setAutoCommit(false);

		  pst = db.prepareStatement(GET_SYSTEM_USERNAMES);
		  rs = pst.executeQuery();

		  ArrayList<String> users = new ArrayList<String>();
		  while (rs.next())
			  users.add(rs.getString(1));
		  
		  return users;
	  }
	  catch (SQLException e) {
		  Logger.warning(userInfo.getUtilizador(), this, "getSystemUsers", "Could not get users: " + e.getMessage());
		  e.printStackTrace();
		  return null;
	  }
	  finally {
		  //DatabaseInterface.closeResources(db, pst, rs);
	    	try {if (db != null) db.close(); } catch (SQLException e) {}
	    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
	    	try {if (rs != null) rs.close(); } catch (SQLException e) {}
	    	
	  }
  }

  /**
   * @see #removeOrganization(UserInfoInterface, String)
   */
  private void remove(UserInfoInterface userInfo, Connection db, PreparedStatement pst, String organizationId) throws SQLException {
    if(Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeOrganization", "Starting remove operations for organization " + organizationId + "...");
    }
    removeExtraFeatures(userInfo, db, pst, organizationId);
    removeFlowProcess(userInfo, db, pst, organizationId);
    removeProcessExtra(userInfo, db, pst, organizationId);
    removeUsersAndProfiles(userInfo, db, pst, organizationId);
    BeanFactory.getOrganizationThemeBean().removeOrganizationTheme(userInfo, organizationId);
    removeOrganizationData(userInfo, db, pst, organizationId);
    if(Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeOrganization", "Ended remove operations for organization " + organizationId + "!");
    }
  }

  /**
   * @see #remove(UserInfoInterface, Connection, PreparedStatement, String)
   */
  private void removeExtraFeatures(UserInfoInterface userInfo, Connection db, PreparedStatement pst, String organizationId) throws SQLException {
    if(Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeOrganization", "Removing extra features for organization " + organizationId);
    }
    // extra features
    pst = db.prepareStatement("delete from new_features where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from links_flows where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from iflow_errors where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();
  }

  /**
   * @see #remove(UserInfoInterface, Connection, PreparedStatement, String)
   */
  private void removeFlowProcess(UserInfoInterface userInfo, Connection db, PreparedStatement pst, String organizationId) throws SQLException {
    if(Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeOrganization", "Removing flow/process for organization " + organizationId);
    }
    // delete flow/process stuff (no keys....)
    pst = db.prepareStatement("delete from forkjoin_blocks where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from forkjoin_mines where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from forkjoin_hierarchy where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from forkjoin_state_dep where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from queue_proc where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from flow_roles where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from flow_settings where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from flow_settings_history where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();
  }

  /**
   * @see #remove(UserInfoInterface, Connection, PreparedStatement, String)
   */
  private void removeProcessExtra(UserInfoInterface userInfo, Connection db, PreparedStatement pst, String organizationId) throws SQLException {
    if(Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeOrganization", "Removing process extra for organization " + organizationId);
    }
    // something about processes is missing
    pst = db.prepareStatement("delete from event_data where fid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from activity where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from flow_state where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from flow_state_history where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

//    pst = db.prepareStatement("delete from data_numeric_history where flowid in (select flowid from flow where organizationid=?)");
//    pst.setString(1, organizationId);
//    pst.executeUpdate();
//
//    pst = db.prepareStatement("delete from data_string_history where flowid in (select flowid from flow where organizationid=?)");
//    pst.setString(1, organizationId);
//    pst.executeUpdate();
//
//    pst = db.prepareStatement("delete from data_numeric where flowid in (select flowid from flow where organizationid=?)");
//    pst.setString(1, organizationId);
//    pst.executeUpdate();
//
//    pst = db.prepareStatement("delete from data_string where flowid in (select flowid from flow where organizationid=?)");
//    pst.setString(1, organizationId);
//    pst.executeUpdate();

    pst = db.prepareStatement("delete from modification where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from activity_history where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from activity_hierarchy where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from process_history where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from process where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from flow_history where flowid in (select flowid from flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from flow where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from sub_flow_history where flowid in (select flowid from sub_flow where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from sub_flow where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();
  }

  /**
   * @see #remove(UserInfoInterface, Connection, PreparedStatement, String)
   */
  private void removeUsersAndProfiles(UserInfoInterface userInfo, Connection db, PreparedStatement pst, String organizationId) throws SQLException {
    if(Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeOrganization", "Removing users and profiles for organization " + organizationId);
    }
    // kill unit managers
    pst = db
        .prepareStatement("delete from unitmanagers where unitid in (select unitid from organizational_units where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    // kill user/profile mapping
    pst = db
        .prepareStatement("delete from userprofiles where profileid in (select profileid from profiles where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    // kill profiles
    pst = db.prepareStatement("delete from profiles where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    // kill users
    pst = db
        .prepareStatement("delete from user_settings where userid in (select username from users where unitid in (select unitid from organizational_units where organizationid=?))");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from user_activation where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    pst = db.prepareStatement("delete from users where unitid in (select unitid from organizational_units where organizationid=?)");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    // kill organizational units
    pst = db.prepareStatement("delete from organizational_units where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();
  }
  
  /**
   * @see #remove(UserInfoInterface, Connection, PreparedStatement, String)
   */
  private void removeOrganizationData(UserInfoInterface userInfo, Connection db, PreparedStatement pst, String organizationId) throws SQLException {
    if(Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeOrganization", "Removing organization data for organization " + organizationId);
    }
    // kill organization settings
    pst = db.prepareStatement("delete from organization_settings where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    // kill organizational units
    pst = db.prepareStatement("delete from organizational_units where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();

    // kill organizations
    pst = db.prepareStatement("delete from organizations where organizationid=?");
    pst.setString(1, organizationId);
    pst.executeUpdate();
  }

  //
  // Dummy event to create a delegated user info.
  private static class NewUserEvent extends AbstractEvent {
    @Override
    public Boolean processEvent(String userId, Integer id, Integer pid, Integer subpid, Integer fid, Integer blockid,
        Long starttime, String type, String properties) {
      return Boolean.TRUE;
    }

    @Override
    public Boolean processEvent() {
      return Boolean.TRUE;
    }

    public Integer initialEventCode() {
      return new Integer(EventManager.READY_TO_PROCESS);
    }
  }

@Override
public boolean changeActiveState(UserInfoInterface userInfo, String userid, Boolean active) {
    Boolean result = false;

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    Logger.info("ADMIN", this, "changeActiveState", "userid:" + userid + " to status " + active);

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

		pst = db.prepareStatement("update users set activated=? where userid=?");
		if(active)
			pst.setInt(1, 1);
		else
			pst.setInt(1, 0);
		pst.setInt(2, Integer.parseInt(userid));
		pst.executeUpdate();
		pst.close();
		db.commit();
		Logger.info("ADMIN", this, "changeActiveState", "User activate changed successfully");
		result = true;
    }
    catch (SQLException e) {
      result = false;
      Logger.warning("ADMIN", this, "confirmAccount", "User activate NOT changed", e);
    }
    finally {
      //DatabaseInterface.closeResources(db, pst, rs);
    	try { if (db != null) db.close(); } catch (SQLException e) {}
    	try { if (pst != null) pst.close(); } catch (SQLException e) {}
    	try { if (rs != null) rs.close(); } catch (SQLException e) {}
    }

    return result;
  }
}
