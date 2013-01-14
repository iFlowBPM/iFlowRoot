package pt.iflow.api.transition;

import org.apache.commons.lang.StringUtils;

/**
 * Profiles Transition Object.
 * 
 * @author Luis Cabral
 * @version 02.03.2009
 */
public class ProfilesTO implements DBTransitionObject {

  /**
   * @see #TABLE_COLUMNS
   */
  public static String PROFILE_ID = "profileid";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String NAME = "name";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String DESCRIPTION = "description";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String ORGANIZATION_ID = "organizationid";
  
  /**
   * <table border="1">
   * <th>Property</th><th>Value</th>
   * <tr><td>schema</td><td>none defined</td></tr>
   * <tr><td>table</td><td>profiles</td></tr>
   * </table>
   * 
   * @see #TABLE_COLUMNS
   */
  public static String TABLE_NAME = "profiles";
  
  /**
   * <table border="1">
   * <th>Attribute</th><th>Column</th>
   * <tr><td>PROFILE_ID</td><td>profileid</td></tr>
   * <tr><td>NAME</td><td>name</td></tr>
   * <tr><td>DESCRIPTION</td><td>description</td></tr>
   * <tr><td>ORGANIZATION_ID</td><td>organizationid</td></tr>
   * </table>
   * 
   * @see #TABLE_NAME
   */
  public static String[] TABLE_COLUMNS = { PROFILE_ID, NAME, DESCRIPTION, ORGANIZATION_ID };

  private int profileId;
  private String name;
  private String description;
  private String organizationId;

  /**
   * Overload c'tor. Should never persist a TO initialized this way to DB
   * without first setting appropriate fields.
   */
  public ProfilesTO() {
    this(-1, null, null, null);
  }

  /**
   * Main c'tor, should avoid specifying profile id directly in profile creation.
   */
  public ProfilesTO(String aName, String aDescription, String anOrganizationId) {
    this(-1, aName, aDescription, anOrganizationId);
  }

  /**
   * C'tor for attribute initialization, should not use this c'tor unless you are
   * sure of profile id.
   */
  public ProfilesTO(int aProfileId, String aName, String aDescription, String anOrganizationId) {
    this.profileId = aProfileId;
    this.name = aName;
    this.description = aDescription;
    this.organizationId = anOrganizationId;
  }
  
  /*
   * (non-Javadoc)
   * @see pt.iflow.api.transition.DBTransitionObject#getValueOf(java.lang.String)
   */
  public String getValueOf(String columnName) {
    String retObj = null;
    if (StringUtils.equals(columnName, PROFILE_ID)) {
      retObj = "" + getProfileId();
    } else if (StringUtils.equals(columnName, NAME)) {
      if (getName() != null) {
        retObj = "'" + getName() + "'";
      }
    } else if (StringUtils.equals(columnName, DESCRIPTION)) {
      if (getDescription() != null) {
        retObj = "'" + getDescription() + "'";
      }
    } else if (StringUtils.equals(columnName, ORGANIZATION_ID)) {
      if (getOrganizationId() != null) {
        retObj = "'" + getOrganizationId() + "'";
      }
    }
    return retObj;
  }

  public int getProfileId() {
    return profileId;
  }

  public void setProfileId(int aProfileId) {
    this.profileId = aProfileId;
  }

  public String getName() {
    return name;
  }

  public void setName(String aName) {
    this.name = aName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String aDescription) {
    this.description = aDescription;
  }

  public String getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(String anOrganizationId) {
    this.organizationId = anOrganizationId;
  }

  public String toString() {
    return "{" + this.profileId + "," + this.name + "," + this.description + "," + this.organizationId + "}";
  }
}
