package pt.iflow.api.core;

import java.util.List;

import pt.iflow.api.errors.IErrorHandler;
import pt.iflow.api.transition.ProfilesTO;
import pt.iflow.api.userdata.views.OrganizationViewInterface;
import pt.iflow.api.userdata.views.OrganizationalUnitViewInterface;
import pt.iflow.api.userdata.views.UserViewInterface;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * Remote interface for UserManager.
 *
 * @wtp generated
 */
public interface UserManager {
    // Some error codes

    public static final int ERR_OK = 0;
    public static final int ERR_USER_EXISTS = 1;
    public static final int ERR_ORGANIZATION_EXISTS = 2;
    public static final int ERR_EMAIL = 3;
    public static final int ERR_INTERNAL = 4;
    public static final int ERR_EMAIL_EXISTS = 5;
    public static final int ERR_PASSWORD = 6;
    public static final int ERR_INVALID_EMAIL = 7;
    public static final int ERR_USERNAME = 8;

    public static final int USERNAME_MAX_LENGTH = 100;

    /**
     * Add a new user
     *
     * @param userInfo            - The administrator creating a new user
     * @param username            - String com o username do utilizador
     * @param password            - String com a password em plain-text
     * @param gender              - User gender (M/F)
     * @param unit                - Organizational unit
     * @param emailAddress        - User email address
     * @param firstName           - First Name
     * @param lastName            - Last Name
     * @param phoneNumber         - Phone Number
     * @param faxNumber           - Fax Number
     * @param mobileNumber        - Mobile Number
     * @param companyPhone        - Company Phone
     * @param activationCode      - Activation code
     * @param orgId               - Organization ID
     * @param password            - default password
     * @param listExtraProperties - Extra Properties
     * @param listExtraValues     - Extra Values
     * @return - true if user was created successfully
     */
    public IErrorHandler createUser(UserInfoInterface userInfo, String username, String gender, String unit, String emailAddress, String firstName, String lastName, String phoneNumber, String faxNumber, String mobileNumber, String companyPhone, String orgId, String orgAdm, String password, String[] listExtraProperties, String[] listExtraValues);


    /**
     * Invite a new user, generating an activation code
     *
     * @param userInfo            - The administrator creating a new user
     * @param username            - String com o username do utilizador
     * @param password            - String com a password em plain-text
     * @param gender              - User gender (M/F)
     * @param unit                - Organizational unit
     * @param emailAddress        - User email address
     * @param firstName           - First Name
     * @param lastName            - Last Name
     * @param phoneNumber         - Phone Number
     * @param faxNumber           - Fax Number
     * @param mobileNumber        - Mobile Number
     * @param companyPhone        - Company Phone
     * @param activationCode      - Activation code
     * @param orgId               - Organization ID
     * @param listExtraProperties - Extra Properties
     * @param listExtraValues     - Extra Values
     * @return - true if user was created successfully
     */
    public IErrorHandler inviteUser(UserInfoInterface userInfo, String username, String gender, String unit, String emailAddress, String firstName, String lastName, String phoneNumber, String faxNumber, String mobileNumber, String companyPhone, String orgId, String orgAdm, String[] listExtraProperties, String[] listExtraValues);


    /**
     * Add a new Organization
     *
     * @param userInfo    - The administrator creating the new organization
     * @param name        - Organization name
     * @param description - Simple description
     * @return - true if organization was created successfully
     */
    public boolean createOrganization(UserInfoInterface userInfo, String name, String description);

    /**
     * Add a new Organizational Unit
     *
     * @param userInfo       - The administrator creating the new organization
     * @param organizationid - Organization name
     * @param name           - Unit name
     * @param description    - Simple description
     * @param parentid       - Parent unit ID
     * @param managerid      - Unit manager ID
     * @return - true if organizational unit was created successfully
     */
    public boolean createOrganizationalUnit(UserInfoInterface userInfo, String organizationid, String name, String description, String parentid, String managerid);

    /**
     * Add a new Profile.
     *
     * @param userInfo The administrator creating the new profile.
     * @param profile  Profile being created.
     * @return True if profile was created successfully.
     */
    public boolean createProfile(UserInfoInterface userInfo, ProfilesTO profile);

    /**
     * Modify an existing user
     *
     * @param userInfo            - The administrator creating a new user
     * @param userId              - String com o username do utilizador
     * @param gender              - User gender (M/F)
     * @param unit                - Organizational unit
     * @param emailAddress        - User email address
     * @param firstName           - First Name
     * @param lastName            - Last Name
     * @param phoneNumber         - Phone Number
     * @param faxNumber           - Fax Number
     * @param mobileNumber        - Mobile Number
     * @param companyPhone        - Company Phone
     * @param listExtraProperties - Extra Properties
     * @param listExtraValues     - Extra Values
     * @return - true if user was created successfully
     */
    public IErrorHandler modifyUserAsAdmin(UserInfoInterface userInfo, String userId, String gender, String unit,
                                           String emailAddress, String firstName, String lastName, String phoneNumber, String faxNumber, String mobileNumber,
                                           String companyPhone, String orgAdm, String orgAdmUsers, String orgAdmFlows, String orgAdmProcesses, String orgAdmResources,
                                           String orgAdmOrg, String newPassword, String[] listExtraProperties, String[] listExtraValues);

    /**
     * Modify an existing user
     *
     * @param userInfo     - The administrator creating a new user
     * @param password     - String com a password em plain-text
     * @param gender       - User gender (M/F)
     * @param unit         - Organizational unit
     * @param emailAddress - User email address
     * @param firstName    - First Name
     * @param lastName     - Last Name
     * @param phoneNumber  - Phone Number
     * @param faxNumber    - Fax Number
     * @param mobileNumber - Mobile Number
     * @param companyPhone - Company Phone
     * @return - true if user was created successfully
     */
    public IErrorHandler modifyUserAsSelf(UserInfoInterface userInfo, String password, String gender, String emailAddress, String firstName, String lastName, String phoneNumber, String faxNumber, String mobileNumber, String companyPhone, String[] listExtraProperties, String[] listExtraValues);

    /**
     * Modify an existing Organization
     *
     * @param userInfo       - The administrator creating the new organization
     * @param organizationId - The organization to update
     * @param name           - Organization name
     * @param description    - Simple description
     * @return - true if organization was created successfully
     */
    public boolean modifyOrganization(UserInfoInterface userInfo, String organizationId, String name, String description);

    /**
     * Modify an existing Organizational Unit
     *
     * @param userInfo       - The administrator creating the new organization
     * @param unitId         - The Organizational Unit to change
     * @param organizationid - Organization name
     * @param name           - Unit name
     * @param description    - Simple description
     * @param parentid       - Parent unit ID
     * @param managerid      - Unit manager ID
     * @return - true if organizational unit was created successfully
     */
    public boolean modifyOrganizationalUnit(UserInfoInterface userInfo, String unitId, String organizationid, String name, String description, String parentid, String managerid);

    /**
     * Modify an existing Profile.
     *
     * @param userInfo The administrator creating the new profile.
     * @param profile  The profile to change.
     * @return true if profile was created successfully
     */
    public boolean modifyProfile(UserInfoInterface userInfo, ProfilesTO profile);

    /**
     * Add a user to a profile
     *
     * @param userInfo  - The administrator creating the new profile
     * @param userId    - User Id
     * @param profileId - profile id
     * @return - true if profile was created successfully
     */
    public boolean addUserProfile(UserInfoInterface userInfo, String userId, String profileId);

    /**
     * Add a user to a profile
     *
     * @param userInfo  - The administrator creating the new profile
     * @param userId    - User id
     * @param profileId - profile id
     * @return - true if profile was created successfully
     */
    public boolean addUserProfile(UserInfoInterface userInfo, int userId, int profileId);

    /**
     * Remove existing user
     *
     * @param userInfo - The administrator creating a new user
     * @param username - String com o username do utilizador
     * @return - true if user was created successfully
     */
    public boolean removeUser(UserInfoInterface userInfo, String userid);

    /**
     * Delete an existing Organization
     *
     * @param userInfo       - The administrator deleting the organization
     * @param organizationId - Organization name
     * @return - true if organization was deleted successfully
     */
    public boolean removeOrganization(UserInfoInterface userInfo, String organizationId);

    /**
     * Remove an Organizational Unit
     *
     * @param userInfo - The administrator creating the new organization
     * @param unitid   - Unit name
     * @return - true if organizational unit was created successfully
     */
    public boolean removeOrganizationalUnit(UserInfoInterface userInfo, String unitid);

    /**
     * Unmap a user to a profile
     *
     * @param userInfo  - The administrator creating the new profile
     * @param userId    - User name
     * @param profileId - profile name
     * @return - true if profile was created successfully
     */
    public boolean delUserProfile(UserInfoInterface userInfo, String userId, String profileId);

    /**
     * Unmap a user to a profile
     *
     * @param userInfo  - The administrator creating the new profile
     * @param userId    - User id
     * @param profileId - profile id
     * @return - true if profile was created successfully
     */
    public boolean delUserProfile(UserInfoInterface userInfo, int userId, int profileId);

    /**
     * Delete a Profile
     *
     * @param userInfo  - The administrator creating the new profile
     * @param profileId - Profile name
     * @return - true if profile was removed successfully
     */
    public boolean removeProfile(UserInfoInterface userInfo, String profileId);

    /**
     * Get an Organizational Unit
     *
     * @param userInfo - The administrator creating the new profile
     * @return - UserInfo array with all users
     */
    public OrganizationalUnitViewInterface getOrganizationalUnit(UserInfoInterface userInfo, String unitId);

    /**
     * Get an Organization
     *
     * @param userInfo - The administrator creating the new profile
     * @param orgId    - Organization ID
     * @return - OrganizationView array with all users
     */
    public OrganizationViewInterface getOrganization(UserInfoInterface userInfo, String orgId);


    /**
     * Finds an User
     *
     * @param userInfo - user
     * @param username - the username to search
     * @return - user found.
     */
    public UserViewInterface findUser(UserInfoInterface userInfo, String username) throws IllegalAccessException;

    public UserViewInterface findOrganizationUser(UserInfoInterface userInfo, String orgId, String username) throws IllegalAccessException;

    /**
     * Get an User
     *
     * @param userInfo - The administrator creating the new profile
     * @param userId   - User ID
     * @return - UserInfo array with all users
     */
    public UserViewInterface getUser(UserInfoInterface userInfo, String userId);

    /**
     * Get a Profile.
     *
     * @param userInfo  User information for profile retrieval.
     * @param profileId Profile ID.
     * @return Profile with given ID for given user.
     */
    public ProfilesTO getProfile(UserInfoInterface userInfo, String profileId);

    /**
     * Get all users
     *
     * @param userInfo - The administrator creating the new profile
     * @return - UserInfo array with all users
     */
    public UserViewInterface[] getAllUsers(UserInfoInterface userInfo);

    /**
     * Get all users for the given user's organizational unit
     *
     * @param userInfo - The "reference" user
     * @return - UserView array with all users
     */
    public UserViewInterface[] getAllUsers(UserInfoInterface userInfo, boolean filterByOrgUnit);


    /**
     * Get all Organizational Units for the given organization
     *
     * @param userInfo - The System Administrator
     * @return - all organization organizational units
     */
    public OrganizationalUnitViewInterface[] getAllOrganizationalUnits(UserInfoInterface systemUserInfo, String orgId) throws IllegalAccessException;

    /**
     * Get all Organizational Units
     *
     * @param userInfo - The administrator creating the new profile
     * @return - UserInfo array with all users
     */
    public OrganizationalUnitViewInterface[] getAllOrganizationalUnits(UserInfoInterface userInfo) throws IllegalAccessException;

    /**
     * Get all Organizations
     *
     * @param userInfo - The administrator creating the new profile
     * @return - UserInfo array with all users
     */
    public OrganizationViewInterface[] getAllOrganizations(UserInfoInterface userInfo);

    /**
     * Get all profiles.
     *
     * @param userInfo User information for profile retrieval.
     * @return Profiles array with all profiles for the given user's organization.
     */
    public ProfilesTO[] getAllProfiles(UserInfoInterface userInfo);

    /**
     * Get all profiles for the given organization.
     *
     * @param sysadminUserInfo SysAdmin user
     * @return Profiles array with all profiles for the given organization.
     */
    public ProfilesTO[] getOrganizationProfiles(UserInfoInterface sysadminUserInfo, String orgId);

    /**
     * Get user profiles
     *
     * @param userInfo - The administrator creating the new profile
     * @param userId   - Get all profiles from this user
     * @return - UserInfo array with all users
     */
    public String[] getUserProfiles(UserInfoInterface userInfo, String userId);

    /**
     * Get profile users
     *
     * @param userInfo  - The administrator creating the new profile
     * @param profileId - Get all users in this profile
     * @return - UserInfo array with all users
     */
    public String[] getProfileUsers(UserInfoInterface userInfo, String profileId);

    /**
     * Register a new user and organization
     *
     * @param orgName        - Organization Name
     * @param orgDescription - Organization Description
     * @param username       - String com o username do utilizador
     * @param password       - String com a password em plain-text
     * @param gender         - User gender (M/F)
     * @param emailAddress   - User email address
     * @param firstName      - First Name
     * @param lastName       - Last Name
     * @param phoneNumber    - Phone Number
     * @param faxNumber      - Fax Number
     * @param mobileNumber   - Mobile Number
     * @param companyPhone   - Company Phone
     * @return - true if user was created successfully
     */
    public int newRegistration(String orgName, String orgDescription, String username, String password, String gender, String emailAddress, String firstName, String lastName, String phoneNumber, String faxNumber, String mobileNumber, String companyPhone, String lang, String timezone);

    /**
     * Check if an organization exists.
     *
     * @param orgName Organiztion name
     * @return
     */
    public boolean organizationExists(String orgName);

    /**
     * Confirm user account
     *
     * @param code
     * @return The newly created user
     */
    public UserCredentials confirmAccount(String code);

    /**
     * Reset user password
     *
     * @param username
     */
    public boolean resetPassword(String username);

    /**
     * Reset user password
     *
     * @param userInfo
     * @param userId
     */
    public boolean resetPassword(UserInfoInterface userInfo, String userId);

    /**
     * Change user password, marking as valid
     *
     * @param username
     * @param oldPassword
     * @param password
     * @return Error code
     */
    public int changePassword(String username, String oldPassword, String password);

    /**
     * Lock an existing Organization.
     * <p>
     * Mark organization as locked and mark users as not active, so they cannot login.
     *
     * @param userInfo       - The administrator creating the new organization
     * @param organizationId - Organization name
     * @return - true if organization was locked successfully
     * @ejb.interface-method view-type = "remote"
     */
    public boolean lockOrganization(UserInfoInterface userInfo, String organizationId);

    /**
     * Unlock an existing Organization
     * <p>
     * Mark organization as unlocked and mark users as active, if they where already actived.
     *
     * @param userInfo       - The administrator creating the new organization
     * @param organizationId - Organization name
     * @return - true if organization was unlocked successfully
     * @ejb.interface-method view-type = "remote"
     */
    public boolean unlockOrganization(UserInfoInterface userInfo, String organizationId);


    public static final int CONFIRM_ERROR = 0;
    public static final int CONFIRM_NOT_USED = -1;
    public static final int CONFIRM_EMAIL_CONFIRMED = 1;
    public static final int CONFIRM_EMAIL_REVERTED = 2;

    /**
     * Confirm email address for organization administrator if instalation type is web.
     *
     * @param action
     * @param key
     * @return
     */
    public int confirmEmailAddress(String action, String key);

    /**
     * Gets a given organizational unit id manager
     *
     * @param userInfo the caller user
     * @param unitId   the unit id
     * @return the unit's manager
     */
    public String getOrganizationalUnitManager(UserInfoInterface userInfo, String unitId);

    /**
     * Checks if a given user is a unit manager
     *
     * @param userInfo the caller user
     * @return true if user is manager of it's organizational unit; false otherwise.
     */
    public boolean isOrganizationalUnitManager(UserInfoInterface userInfo);

    public List<String> getSystemUsers(UserInfoInterface userInfo);

    public int changePasswordAdmin(String username, String oldPassword, String password);

    public abstract boolean blockUser(String paramString);
}
