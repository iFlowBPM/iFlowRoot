package pt.iflow.api.transition;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DBQueryManager;

/**
 * Report Transition Object.
 * 
 * @author Luis Cabral
 * @version 02.03.2009
 */
public class ReportTO implements DBTransitionObject {

  /**
   * @see #TABLE_COLUMNS
   */
  public static String FLOWID = "flowid";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String PID = "pid";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String SUBPID = "subpid";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String COD_REPORTING = "cod_reporting";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String START_REPORTING = "start_reporting";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String STOP_REPORTING = "stop_reporting";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String TTL = "ttl";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String ACTIVE = "active";
  
  /**
   * <table border="1">
   * <th>Property</th><th>Value</th>
   * <tr><td>schema</td><td>iflow</td></tr>
   * <tr><td>table</td><td>reporting</td></tr>
   * </table>
   * 
   * @see #TABLE_COLUMNS
   */
  public static String TABLE_NAME = "reporting";
  
  /**
   * <table border="1">
   * <th>Attribute</th><th>Column</th>
   * <tr><td>FLOWID</td><td>flowid</td></tr>
   * <tr><td>PID</td><td>pid</td></tr>
   * <tr><td>SUBPID</td><td>subpid</td></tr>
   * <tr><td>COD_REPORTING</td><td>cod_reporting</td></tr>
   * <tr><td>START_REPORTING</td><td>start_reporting</td></tr>
   * <tr><td>STOP_REPORTING</td><td>stop_reporting</td></tr>
   * <tr><td>TTL</td><td>ttl</td></tr>
   * <tr><td>ACTIVE</td><td>active</td></tr>
   * </table>
   * 
   * @see #TABLE_NAME
   */
  public static String[] TABLE_COLUMNS = { FLOWID, PID, SUBPID, COD_REPORTING, START_REPORTING, STOP_REPORTING, TTL, ACTIVE };

  private int flowId;
  private int pid;
  private int subpid;
  private String codReporting;
  private Timestamp startReporting;
  private Timestamp stopReporting;
  private Timestamp ttl;
  private int active;

  /**
   * Overload c'tor. Should never persist a TO initialized this way to DB
   * without first setting appropriate fields.
   */
  public ReportTO() {
    this(-1, -1, -1, null, null, null, null, false);
  }

  /**
   * C'tor for easy new report creation. Attribute <b>'active'</b> defaults to
   * <b>'true'</b> and attributes <b>'stopReporting'</b> and <b>'ttl'</b>
   * default to <b>'null'</b>.
   */
  public ReportTO(int aFlowId, int aPid, int aSubPid, String aCodReporting, Timestamp aStartReporting) {
    this(aFlowId, aPid, aSubPid, aCodReporting, aStartReporting, null, null, true);
  }

  /**
   * C'tor, simple variation for setting active through boolean values instead of integer '1'/'0'.
   */
  public ReportTO(int aFlowId, int aPid, int aSubPid, String aCodReporting, Timestamp aStartReporting, Timestamp aStopReporting,
      Timestamp aTtl, boolean isActive) {
    this(aFlowId, aPid, aSubPid, aCodReporting, aStartReporting, aStopReporting, aTtl, isActive ? 1 : 0);
  }

  /**
   * Main c'tor.
   */
  public ReportTO(int aFlowId, int aPid, int aSubPid, String aCodReporting, Timestamp aStartReporting, Timestamp aStopReporting,
      Timestamp aTtl, int isActive) {
    this.flowId = aFlowId;
    this.pid = aPid;
    this.subpid = aSubPid;
    this.codReporting = aCodReporting;
    this.startReporting = aStartReporting;
    this.stopReporting = aStopReporting;
    this.active = isActive;
    this.ttl = aTtl;
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.transition.DBTransitionObject#getValueOf(java.lang.String)
   */
  public String getValueOf(String columnName) {
    String retObj = null;
    if (StringUtils.equals(columnName, FLOWID)) {
      retObj = DBQueryManager.toQueryValue(getFlowId());
    } else if (StringUtils.equals(columnName, PID)) {
      retObj = DBQueryManager.toQueryValue(getPid());
    } else if (StringUtils.equals(columnName, SUBPID)) {
      retObj = DBQueryManager.toQueryValue(getSubpid());
    } else if (StringUtils.equals(columnName, COD_REPORTING)) {
      retObj = DBQueryManager.toQueryValue(getCodReporting());
    } else if (StringUtils.equals(columnName, START_REPORTING)) {
      retObj = DBQueryManager.toQueryValue(getStartReporting());
    } else if (StringUtils.equals(columnName, STOP_REPORTING)) {
      retObj = DBQueryManager.toQueryValue(getStopReporting());
    } else if (StringUtils.equals(columnName, TTL)) {
      retObj = DBQueryManager.toQueryValue(getTtl());
    } else if (StringUtils.equals(columnName, ACTIVE)) {
      retObj = DBQueryManager.toQueryValue(getActive());
    }
    return retObj;
  }

  public int getFlowId() {
    return flowId;
  }

  public void setFlowId(int aFlowId) {
    this.flowId = aFlowId;
  }

  public int getPid() {
    return pid;
  }

  public void setPid(int aPid) {
    this.pid = aPid;
  }

  public int getSubpid() {
    return subpid;
  }

  public void setSubpid(int aSubPid) {
    this.subpid = aSubPid;
  }

  public String getCodReporting() {
    return codReporting;
  }

  public void setCodReporting(String aCodReporting) {
    this.codReporting = aCodReporting;
  }

  public Timestamp getStartReporting() {
    return startReporting;
  }

  public void setStartReporting(Timestamp aStartReporting) {
    this.startReporting = aStartReporting;
  }

  public Timestamp getStopReporting() {
    return stopReporting;
  }

  public void setStopReporting(Timestamp aStopReporting) {
    this.stopReporting = aStopReporting;
  }

  public Timestamp getTtl() {
    return ttl;
  }

  public void setTtl(Timestamp nTtl) {
    this.ttl = nTtl;
  }

  public int getActive() {
    return active;
  }

  public void setActive(int isActive) {
    this.active = isActive;
  }

  public boolean isActive() {
    return (this.active == 1);
  }

  public void setActive(boolean isActive) {
    this.active = isActive ? 1 : 0;
  }

  public String toString() {
    return "{" + this.flowId + "," + this.pid + "," + this.subpid + "," + this.codReporting + "," + this.startReporting + ","
        + this.stopReporting + "," + this.ttl + "," + this.active + "}";
  }
}
