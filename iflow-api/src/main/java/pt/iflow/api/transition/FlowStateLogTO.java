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

import org.apache.commons.lang.StringUtils;

/**
 * Flow State Log Transition Object.
 * 
 * @author Luis Cabral
 * @version 18.05.2009
 */
public class FlowStateLogTO implements DBTransitionObject {

  /**
   * @see #TABLE_COLUMNS
   */
  public static String FLOW_ID = "flowid";
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
  public static String STATE = "state";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String LOG_ID = "log_id";

  /**
   * <table border="1">
   * <th>Property</th><th>Value</th>
   * <tr><td>schema</td><td><i>undefined<i></td></tr>
   * <tr><td>table</td><td>flow_state_log</td></tr>
   * </table>
   * 
   * @see #TABLE_COLUMNS
   */
  public static String TABLE_NAME = "flow_state_log";

  /**
   * <table border="1">
   * <th>Attribute</th><th>Column</th>
   * <tr><td>{@link #FLOW_ID}</td><td>flowid</td></tr>
   * <tr><td>{@link #PID}</td><td>pid</td></tr>
   * <tr><td>{@link #SUBPID}</td><td>subpid</td></tr>
   * <tr><td>{@link #STATE}</td><td>state</td></tr>
   * <tr><td>{@link #LOG_ID}</td><td>log_id</td></tr>
   * </table>
   * 
   * @see #TABLE_NAME
   */
  public static String[] TABLE_COLUMNS = { FLOW_ID, PID, SUBPID, STATE, LOG_ID };
  
  private int flowid, pid, subpid, state;
  private LogTO log;

  /**
   * Overload c'tor. Should never persist a TO initialized this way to DB
   * without first setting appropriate fields.
   */
  public FlowStateLogTO() {
    this(-1, -1, -1, -1 , new LogTO());
  }

  public FlowStateLogTO(int flowid, int pid, int subpid, int state, LogTO log) {
    this.flowid = flowid;
    this.pid = pid;
    this.subpid = subpid;
    this.state = state;
    this.log = log;
  }
  
  /*
   * (non-Javadoc)
   * @see pt.iflow.api.transition.DBTransitionObject#getValueOf(java.lang.String)
   */
  public String getValueOf(String columnName) {
    String retObj = null;
    if (StringUtils.equals(columnName, FLOW_ID)) {
      retObj = "" + getFlowid();
    } else if(StringUtils.equals(columnName, PID)) {
      retObj = "" + getPid();
    } else if(StringUtils.equals(columnName, SUBPID)) {
      retObj = "" + getSubpid();
    } else if(StringUtils.equals(columnName, STATE)) {
      retObj = "" + getState();
    } else if(StringUtils.equals(columnName, LOG_ID)) {
      if(getLog() != null) {
        retObj = "" + getLog().getLogId();
      }
    }
    return retObj;
  }

  public int getFlowid() {
    return flowid;
  }

  public void setFlowid(int flowid) {
    this.flowid = flowid;
  }

  public int getPid() {
    return pid;
  }

  public void setPid(int pid) {
    this.pid = pid;
  }

  public int getSubpid() {
    return subpid;
  }

  public void setSubpid(int subpid) {
    this.subpid = subpid;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public LogTO getLog() {
    return log;
  }

  public void setLog(LogTO log) {
    this.log = log;
  }

}
