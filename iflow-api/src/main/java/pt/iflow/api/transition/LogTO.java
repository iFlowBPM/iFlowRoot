/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.api.transition;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DBQueryManager;

/**
 * Log Transition Object.
 * 
 * @author Luis Cabral
 * @version 18.05.2009
 */
public class LogTO implements DBTransitionObject {

  /**
   * @see #TABLE_COLUMNS
   */
  public static String LOG_ID = "log_id";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String LOG = "log";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String USERNAME = "username";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String CALLER = "caller";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String METHOD = "method";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String CREATION_DATE = "creation_date";
  
  /**
   * <table border="1">
   * <th>Property</th><th>Value</th>
   * <tr><td>schema</td><td><i>undefined<i></td></tr>
   * <tr><td>table</td><td>log</td></tr>
   * </table>
   * 
   * @see #TABLE_COLUMNS
   */
  public static String TABLE_NAME = "log";

  /**
   * <table border="1">
   * <th>Attribute</th><th>Column</th>
   * <tr><td>{@link #LOG_ID}</td><td>log_id</td></tr>
   * <tr><td>{@link #LOG}</td><td>log</td></tr>
   * <tr><td>{@link #USERNAME}</td><td>username</td></tr>
   * <tr><td>{@link #CALLER}</td><td>caller</td></tr>
   * <tr><td>{@link #METHOD}</td><td>method</td></tr>
   * <tr><td>{@link #CREATION_DATE}</td><td>creation_date</td></tr>
   * </table>
   * 
   * @see #TABLE_NAME
   */
  public static String[] TABLE_COLUMNS = { LOG_ID, LOG, USERNAME, CALLER, METHOD, CREATION_DATE };
  
  private int logId;
  private String log, username, caller, method;
  private Timestamp creationDate;

  /**
   * Overload c'tor. Should never persist a TO initialized this way to DB
   * without first setting appropriate fields.
   */
  public LogTO() {
    this(-1, "", null);
  }


  /**
   * C'tor with only required attributes in it.
   */
  public LogTO(int logId, String log, Timestamp creationDate) {
    this(logId, null, null, null, log, creationDate);
  }

  /**
   * Main c'tor.
   */
  public LogTO(int logId, String username, String caller, String method, String log, Timestamp creationDate) {
    this.logId = logId;
    this.username = username;
    this.caller = caller;
    this.method = method;
    this.log = log;
    this.creationDate = creationDate;
  }
  
  /*
   * (non-Javadoc)
   * @see pt.iflow.api.transition.DBTransitionObject#getValueOf(java.lang.String)
   */
  public String getValueOf(String columnName) {
    String retObj = null;
    if (StringUtils.equals(columnName, LOG_ID)) {
      retObj = DBQueryManager.toQueryValue(getLogId());
    } else if(StringUtils.equals(columnName, LOG)) {
      if (getLog() != null) {
        retObj = DBQueryManager.toQueryValue(getLog());
      }
    } else if(StringUtils.equals(columnName, USERNAME)) {
      if (getUsername() != null) {
        retObj = DBQueryManager.toQueryValue(getUsername());
      }
    } else if(StringUtils.equals(columnName, CALLER)) {
      if (getCaller() != null) {
        retObj = DBQueryManager.toQueryValue(getCaller());
      }
    } else if(StringUtils.equals(columnName, METHOD)) {
      if (getMethod() != null) {
        retObj = DBQueryManager.toQueryValue(getMethod());
      }
    } else if(StringUtils.equals(columnName, CREATION_DATE)) {
      if (getCreationDate() != null) {
        retObj = DBQueryManager.toQueryValue(getCreationDate());
      }
    }
    return retObj;
  }
  
  public int getLogId() {
    return logId;
  }

  public void setLogId(int logId) {
    this.logId = logId;
  }

  public String getLog() {
    return log;
  }

  public void setLog(String log) {
    this.log = log;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getCaller() {
    return caller;
  }

  public void setCaller(String caller) {
    this.caller = caller;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Timestamp getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Timestamp creationDate) {
    this.creationDate = creationDate;
  }
}
