package pt.iflow.api.transition;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.msg.IMessages;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * Profiles Transition Object.
 * 
 * @author Luis Cabral
 * @version 03.03.2009
 */
public class FlowRolesTO implements DBTransitionObject {

  /**
   * @see #PERMISSIONS
   */
  public final static char CREATE_PRIV = 'C';
  /**
   * @see #PERMISSIONS
   */
  public final static char READ_PRIV = 'R';
  /**
   * @see #PERMISSIONS
   */
  public final static char WRITE_PRIV = 'W';
  /**
   * @see #PERMISSIONS
   */
  public final static char ADMIN_PRIV = 'A';
  /**
   * @see #PERMISSIONS
   */
  public final static char SUPERUSER_PRIV = 'S';
  
  /**
   * @see #TABLE_COLUMNS
   */
  public static String FLOW_ID = "flowid";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String PROFILE_ID = "profileid";
  /**
   * <table border="1">
   * <th>Property</th><th>Value</th>
   * <tr><td>{@link #CREATE_PRIV}</td><td>'C'</td></tr>
   * <tr><td>{@link #READ_PRIV}</td><td>'R'</td></tr>
   * <tr><td>{@link #WRITE_PRIV}</td><td>'W'</td></tr>
   * <tr><td>{@link #ADMIN_PRIV}</td><td>'A'</td></tr>
   * <tr><td>{@link #SUPERUSER_PRIV}</td><td>'S'</td></tr>
   * </table>
   * @see #TABLE_COLUMNS
   */
  public static String PERMISSIONS = "permissions";

  /**
   * <table border="1">
   * <th>Property</th><th>Value</th>
   * <tr><td>schema</td><td>iflow</td></tr>
   * <tr><td>table</td><td>flow_roles</td></tr>
   * </table>
   * 
   * @see #TABLE_COLUMNS
   */
  public static String TABLE_NAME = "flow_roles";
  
  /**
   * <table border="1">
   * <th>Attribute</th><th>Column</th>
   * <tr><td>{@link #FLOW_ID}</td><td>flowid</td></tr>
   * <tr><td>{@link #PROFILE_ID}</td><td>profileid</td></tr>
   * <tr><td>{@link #PERMISSIONS}</td><td>permissions</td></tr>
   * </table>
   * 
   * @see #TABLE_NAME
   */
  public static String[] TABLE_COLUMNS = { FLOW_ID, PROFILE_ID, PERMISSIONS };

  private int flowid;
  private ProfilesTO profile;
  private String permissions;
  private String user;

  private final static String PRIVILEGE_INFO_CREATE = "privilege.create";
  private final static String PRIVILEGE_INFO_READ = "privilege.read";
  private final static String PRIVILEGE_INFO_WRITE = "privilege.write";
  private final static String PRIVILEGE_INFO_ADMIN = "privilege.admin";
  private final static String PRIVILEGE_INFO_SUPER = "privilege.super";
  private final static OrderedMap<Character, String> PRIVILEGE_INFO = new ListOrderedMap<Character, String>();
  static {
    PRIVILEGE_INFO.put(READ_PRIV, PRIVILEGE_INFO_READ);
    PRIVILEGE_INFO.put(CREATE_PRIV, PRIVILEGE_INFO_CREATE);
    PRIVILEGE_INFO.put(WRITE_PRIV, PRIVILEGE_INFO_WRITE);
    PRIVILEGE_INFO.put(ADMIN_PRIV, PRIVILEGE_INFO_ADMIN);
    PRIVILEGE_INFO.put(SUPERUSER_PRIV, PRIVILEGE_INFO_SUPER);
  }
  
  /**
   * Overload c'tor. Should never persist a TO initialized this way to DB
   * without first setting appropriate fields.
   */
  public FlowRolesTO() {
    this(-1, null);
  }

  /**
   * C'tor for flow role delegations to another user. Flow roles that are
   * delegated should usually exist only in memory, but if persisted it will be
   * to a table other than the one this TO refers to.
   * 
   * @see #TABLE_NAME
   */
  public FlowRolesTO(int aFlowid, String anUser, String aPermissions) {
    this(aFlowid, null);
    this.permissions = aPermissions;
    this.user = anUser;
  }

  /**
   * C'tor for a flow role with no defined permissions.
   */
  public FlowRolesTO(int aFlowid, ProfilesTO aProfile) {
    this(aFlowid, aProfile, null);
  }
  
  /**
   * Main c'tor.
   */
  public FlowRolesTO(int aFlowid, ProfilesTO aProfile, String aPermissions) {
    this.flowid = aFlowid;
    this.profile = aProfile;
    this.permissions = aPermissions;
    this.user = null;
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.transition.DBTransitionObject#getValueOf(java.lang.String)
   */
  public String getValueOf(String columnName) {
    String retObj = null;
    if (StringUtils.equals(columnName, FLOW_ID)) {
      retObj = "" + getFlowid();
    } else if (StringUtils.equals(columnName, PROFILE_ID)) {
        retObj = "" + profile.getProfileId();
    } else if (StringUtils.equals(columnName, PERMISSIONS)) {
      if (getPermissions() != null) {
        retObj = "'" + getPermissions() + "'";
      }
    }
    return retObj;
  }

  public boolean hasPrivilege(String priviledges) {
    for (char priviledge : priviledges.toCharArray()) {
      if (!this.hasPrivilege(priviledge)) {
        return false;
      }
    }
    return true;
  }
  
  public boolean hasPrivilege(char priv) {
    if(StringUtils.contains(getPermissions(), priv)) {
      return true;
    }
    return false;
  }

  public void grantPrivilege(String priviledges) {
    for (char priviledge : priviledges.toCharArray()) {
      this.grantPrivilege(priviledge);
    }
  }
  
  public void grantPrivilege(char priviledge) {
    Character priv = Character.toUpperCase(priviledge);
    if (!StringUtils.contains(this.getPermissions(), priv)) {
      StringBuffer privs = new StringBuffer();
      if (priv.equals(READ_PRIV) || StringUtils.contains(this.getPermissions(), READ_PRIV)) {
        privs.append(READ_PRIV);
      }
      if (priv.equals(CREATE_PRIV) || StringUtils.contains(this.getPermissions(), CREATE_PRIV)) {
        privs.append(CREATE_PRIV);
      }
      if (priv.equals(WRITE_PRIV) || StringUtils.contains(this.getPermissions(), WRITE_PRIV)) {
        privs.append(WRITE_PRIV);
      }
      if (priv.equals(ADMIN_PRIV) || StringUtils.contains(this.getPermissions(), ADMIN_PRIV)) {
        privs.append(ADMIN_PRIV);
      }
      if (priv.equals(SUPERUSER_PRIV) || StringUtils.contains(this.getPermissions(), SUPERUSER_PRIV)) {
        privs.append(SUPERUSER_PRIV);
      }
      this.setPermissions(privs.toString());
    }
  }

  public void revokePrivilege(String priviledges) {
    for (char priviledge : priviledges.toCharArray()) {
      this.revokePrivilege(priviledge);
    }
  }

  public void revokePrivilege(char priviledge) {
    Character priv = Character.toUpperCase(priviledge);
    if (StringUtils.contains(this.getPermissions(), priv)) {
      StringBuffer privs = new StringBuffer();
      if (!priv.equals(READ_PRIV) && StringUtils.contains(this.getPermissions(), READ_PRIV)) {
        privs.append(READ_PRIV);
      }
      if (!priv.equals(CREATE_PRIV) && StringUtils.contains(this.getPermissions(), CREATE_PRIV)) {
        privs.append(CREATE_PRIV);
      }
      if (!priv.equals(WRITE_PRIV) && StringUtils.contains(this.getPermissions(), WRITE_PRIV)) {
        privs.append(WRITE_PRIV);
      }
      if (!priv.equals(ADMIN_PRIV) && StringUtils.contains(this.getPermissions(), ADMIN_PRIV)) {
        privs.append(ADMIN_PRIV);
      }
      if (!priv.equals(SUPERUSER_PRIV) && StringUtils.contains(this.getPermissions(), SUPERUSER_PRIV)) {
        privs.append(SUPERUSER_PRIV);
      }
      this.setPermissions(privs.toString());
    }
  }

  /**
   * This is WEB presentation of existing privileges.
   * 
   * @param userInfo
   *          User information used to retrieve web messages.
   * @return Bidimensional array (0:privilege; 1:privilege description).
   */
  public static String[][] getPrivilegesInfo(UserInfoInterface userInfo) {
    IMessages msg = userInfo.getMessages();
    String[][] retObj = new String[PRIVILEGE_INFO.size()][2];
    int index = 0;
    for (Character key : PRIVILEGE_INFO.keySet()) {
      retObj[index][0] = key.toString();
      retObj[index][1] = msg.getString(PRIVILEGE_INFO.get(key));
      index++;
    }
    return retObj;
  }
  
  public int getFlowid() {
    return flowid;
  }

  public void setFlowid(int aFlowid) {
    this.flowid = aFlowid;
  }

  public ProfilesTO getProfile() {
    return profile;
  }

  public void setProfile(ProfilesTO aProfile) {
    this.profile = aProfile;
  }

  public String getPermissions() {
    return permissions;
  }

  public void setPermissions(String aPermissions) {
    this.permissions = aPermissions;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String aUser) {
    this.user = aUser;
  }
}
