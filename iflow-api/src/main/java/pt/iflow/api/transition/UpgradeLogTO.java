package pt.iflow.api.transition;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DBQueryManager;

/**
 * Upgrade Log Transition Object.
 * 
 * @author Luis Cabral
 * @since 07.12.2009
 * @version 07.12.2009
 */
public class UpgradeLogTO implements DBTransitionObject {

  /**
   * @see #TABLE_COLUMNS
   */
  public static String SIGNATURE = "signature";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String EXECUTED = "executed";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String ERROR = "error";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String LOG_ID = "log_id";
  
  /**
   * <table border="1">
   * <th>Property</th><th>Value</th>
   * <tr><td>schema</td><td><i>undefined<i></td></tr>
   * <tr><td>table</td><td>upgrade_log</td></tr>
   * </table>
   * 
   * @see #TABLE_COLUMNS
   */
  public static String TABLE_NAME = "upgrade_log";

  /**
   * <table border="1">
   * <th>Attribute</th><th>Column</th>
   * <tr><td>{@link #SIGNATURE}</td><td>signature</td></tr>
   * <tr><td>{@link #EXECUTED}</td><td>executed</td></tr>
   * <tr><td>{@link #ERROR}</td><td>error</td></tr>
   * <tr><td>{@link #LOG_ID}</td><td>log_id</td></tr>
   * </table>
   * 
   * @see #TABLE_NAME
   */
  public static String[] TABLE_COLUMNS = { SIGNATURE, EXECUTED, ERROR, LOG_ID };

  private String signature;
  private int executed, error;
  private LogTO log;

  /**
   * Overload c'tor. Should never persist a TO initialized this way to DB
   * without first setting appropriate fields.
   */
  public UpgradeLogTO() {
    this(null, false, false, new LogTO());
  }

  public UpgradeLogTO(String signature, boolean executed, boolean error, LogTO log) {
    this.signature = signature;
    this.executed = (executed ? 1 : 0);
    this.error = (error ? 1 : 0);
    this.log = log;
  }
  
  public String getValueOf(String columnName) {
    String retObj = null;
    if (StringUtils.equals(columnName, SIGNATURE)) {
      retObj = DBQueryManager.toQueryValue(getSignature());
    } else if(StringUtils.equals(columnName, EXECUTED)) {
      retObj = DBQueryManager.toQueryValue((getExecuted() ? 1 : 0));
    } else if(StringUtils.equals(columnName, ERROR)) {
      retObj = DBQueryManager.toQueryValue((getError() ? 1 : 0));
    } else if(StringUtils.equals(columnName, LOG_ID)) {
      if(getLog() != null) {
        retObj = DBQueryManager.toQueryValue(getLog().getLogId());
      }
    }
    return retObj;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public boolean getExecuted() {
    return (executed == 1);
  }

  public void setExecuted(boolean executed) {
    this.executed = (executed ? 1 : 0);
  }

  public boolean getError() {
    return (error == 1);
  }

  public void setError(boolean error) {
    this.error = (error ? 1 : 0);
  }

  public LogTO getLog() {
    return log;
  }

  public void setLog(LogTO log) {
    this.log = log;
  }
}
