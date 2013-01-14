/*
 *
 * Created on Jul 30, 2005 by mach
 *
  */

package pt.iflow.delegations;

import java.util.Date;

import pt.iflow.api.delegations.DelegationInfoData;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 mach</p>
 *
 * @author mach
 */

public class DelegationInfoDataImpl implements DelegationInfoData {

  private int _HIERARCHYID;
  private int _PARENTID;
  private String _USERID;
  private String _OWNERID;
  private int _FLOWID;
  private String _FLOWNAME;
  private int _SLAVE;
  private Date _EXPIRES;
  private String _PERMISSIONS;
  private int _PENDING;
  private String _ACCEPTKEY;
  private String _REJECTKEY;


  /**
   *
   * Getter method for _ACCEPTKEY
   *
   * @return Returns the _ACCEPTKEY.
   */
  public String getAcceptKey() {
    return _ACCEPTKEY;
  }
  /**
   *
   * Sets the member _ACCEPTKEY to _acceptkey value.
   *
   * @param _acceptkey The _ACCEPTKEY to set.
   */
  public void setAcceptKey(String _acceptkey) {
    _ACCEPTKEY = _acceptkey;
  }
  /**
   *
   * Getter method for _EXPIRES
   *
   * @return Returns the _EXPIRES.
   */
  public Date getExpires() {
    return _EXPIRES;
  }
  /**
   *
   * Sets the member _EXPIRES to _expires value.
   *
   * @param _expires The _EXPIRES to set.
   */
  public void setExpires(Date _expires) {
    _EXPIRES = _expires;
  }
  /**
   *
   * Getter method for _FLOWID
   *
   * @return Returns the _FLOWID.
   */
  public int getFlowID() {
    return _FLOWID;
  }
  /**
   *
   * Sets the member _FLOWID to _flowid value.
   *
   * @param _flowid The _FLOWID to set.
   */
  public void setFlowID(int _flowid) {
    _FLOWID = _flowid;
  }
  /**
   *
   * Getter method for _FLOWID
   *
   * @return Returns the _FLOWID.
   */
  public String getFlowName() {
    return _FLOWNAME;
  }
  /**
   *
   * Sets the member _FLOWNAME to _flowname value.
   *
   * @param _flowid The _FLOWNAME to set.
   */
  public void setFlowName(String _flowname) {
    _FLOWNAME = _flowname;
  }
  /**
   *
   * Getter method for _HIERARCHYID
   *
   * @return Returns the _HIERARCHYID.
   */
  public int getHierarchyID() {
    return _HIERARCHYID;
  }
  /**
   *
   * Sets the member _HIERARCHYID to _hierarchyid value.
   *
   * @param _hierarchyid The _HIERARCHYID to set.
   */
  public void setHierarchyID(int _hierarchyid) {
    _HIERARCHYID = _hierarchyid;
  }
  /**
   *
   * Getter method for _OWNERID
   *
   * @return Returns the _OWNERID.
   */
  public String getOwnerID() {
    return _OWNERID;
  }
  /**
   *
   * Sets the member _OWNERID to _ownerid value.
   *
   * @param _ownerid The _OWNERID to set.
   */
  public void setOwnerID(String _ownerid) {
    _OWNERID = _ownerid;
  }
  /**
   *
   * Getter method for _PARENTID
   *
   * @return Returns the _PARENTID.
   */
  public int getParentID() {
    return _PARENTID;
  }
  /**
   *
   * Sets the member _PARENTID to _parentid value.
   *
   * @param _parentid The _PARENTID to set.
   */
  public void setParentID(int _parentid) {
    _PARENTID = _parentid;
  }
  /**
   *
   * Getter method for _PENDING
   *
   * @return Returns the _PENDING.
   */
  public int getPending() {
    return _PENDING;
  }
  /**
   *
   * Sets the member _PENDING to _pending value.
   *
   * @param _pending The _PENDING to set.
   */
  public void setPending(int _pending) {
    _PENDING = _pending;
  }
  /**
   *
   * Getter method for _PERMISSIONS
   *
   * @return Returns the _PERMISSIONS.
   */
  public String getPermissions() {
    return _PERMISSIONS;
  }
  /**
   *
   * Sets the member _PERMISSIONS to _permissions value.
   *
   * @param _permissions The _PERMISSIONS to set.
   */
  public void setPermissions(String _permissions) {
    _PERMISSIONS = _permissions;
  }
  /**
   *
   * Getter method for _REJECTKEY
   *
   * @return Returns the _REJECTKEY.
   */
  public String getRejectKey() {
    return _REJECTKEY;
  }
  /**
   *
   * Sets the member _REJECTKEY to _rejectkey value.
   *
   * @param _rejectkey The _REJECTKEY to set.
   */
  public void setRejectKey(String _rejectkey) {
    _REJECTKEY = _rejectkey;
  }
  /**
   *
   * Getter method for _SLAVE
   *
   * @return Returns the _SLAVE.
   */
  public int getSlave() {
    return _SLAVE;
  }
  /**
   *
   * Sets the member _SLAVE to _slave value.
   *
   * @param _slave The _SLAVE to set.
   */
  public void setSlave(int _slave) {
    _SLAVE = _slave;
  }
  /**
   *
   * Getter method for _USERID
   *
   * @return Returns the _USERID.
   */
  public String getUserID() {
    return _USERID;
  }
  /**
   *
   * Sets the member _USERID to _userid value.
   *
   * @param _userid The _USERID to set.
   */
  public void setUserID(String _userid) {
    _USERID = _userid;
  }

}

