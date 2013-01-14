package pt.iflow.api.transition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

/**
 * Flow State History Transition Object.
 * 
 * @author Luis Cabral
 * @version 03.03.2009
 */
public class FlowStateHistoryTO implements DBTransitionObject {

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
  public static String RESULT = "result";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String MDATE = "mdate";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String MID = "mid";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String EXIT_FLAG = "exit_flag";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String UNDO_FLAG = "undoflag";
  /**
   * @see #TABLE_COLUMNS
   */
  public static String EXIT_PORT = "exit_port";
  
  /**
   * <table border="1">
   * <th>Property</th><th>Value</th>
   * <tr><td>schema</td><td><i>undefined<i></td></tr>
   * <tr><td>table</td><td>flow_state_history</td></tr>
   * </table>
   * 
   * @see #TABLE_COLUMNS
   */
  public static String TABLE_NAME = "flow_state_history";

  /**
   * <table border="1">
   * <th>Attribute</th><th>Column</th>
   * <tr><td>{@link #FLOW_ID}</td><td>flowid</td></tr>
   * <tr><td>{@link #PID}</td><td>pid</td></tr>
   * <tr><td>{@link #SUBPID}</td><td>subpid</td></tr>
   * <tr><td>{@link #STATE}</td><td>state</td></tr>
   * <tr><td>{@link #RESULT}</td><td>result</td></tr>
   * <tr><td>{@link #MDATE}</td><td>mdate</td></tr>
   * <tr><td>{@link #MID}</td><td>mid</td></tr>
   * <tr><td>{@link #EXIT_FLAG}</td><td>exit_flag</td></tr>
   * <tr><td>{@link #UNDO_FLAG}</td><td>undoflag</td></tr>
   * <tr><td>{@link #EXIT_PORT}</td><td>exit_port</td></tr>
   * </table>
   * 
   * @see #TABLE_NAME
   */
  public static String[] TABLE_COLUMNS = { FLOW_ID, PID, SUBPID, STATE, RESULT, MDATE, MID, EXIT_FLAG, UNDO_FLAG, EXIT_PORT };
  
  private Integer flowid, pid, subpid, state, mid, exitFlag, undoFlag;
  private String result, exitPort;
  private Timestamp mDate;
  
  private String modificationUser;
  private boolean undoable = true;
  
  /**
   * Overload c'tor. Should never persist a TO initialized this way to DB
   * without first setting appropriate fields.
   */
  public FlowStateHistoryTO() {
    this(-1, -1, -1, -1);
  }
  
  /**
   * C'tor with only required attributes in it.
   */
  public FlowStateHistoryTO(Integer nFlowId, Integer nPid, Integer nState, Integer nExitFlag) {
    this(nFlowId, nPid, null, nState, null, null, null, nExitFlag, null, null);
  }
  
  /**
   * Main c'tor.
   */
  public FlowStateHistoryTO(Integer nFlowId, Integer nPid, Integer nSubPid, Integer nState, String nResult, Timestamp nMDate, Integer nMid, Integer nExitFlag, Integer nUndoFlag, String nExitPort) {
    this.flowid = nFlowId;
    this.pid = nPid;
    this.subpid = nSubPid;
    this.state = nState;
    this.result = nResult;
    this.mDate = nMDate;
    this.mid = nMid;
    this.exitFlag = nExitFlag;
    this.undoFlag = nUndoFlag;
    this.exitPort = nExitPort;
  }

  public FlowStateHistoryTO(ResultSet rs) throws SQLException {
    this.flowid = rs.getInt(FlowStateHistoryTO.FLOW_ID);
    this.pid = rs.getInt(FlowStateHistoryTO.PID);
    this.subpid = rs.getInt(FlowStateHistoryTO.SUBPID);
    this.state = rs.getInt(FlowStateHistoryTO.STATE);
    this.result = rs.getString(FlowStateHistoryTO.RESULT);
    this.mDate = rs.getTimestamp(FlowStateHistoryTO.MDATE);
    this.mid = rs.getInt(FlowStateHistoryTO.MID);
    this.exitFlag = rs.getInt(FlowStateHistoryTO.EXIT_FLAG);
    this.undoFlag = rs.getInt(FlowStateHistoryTO.UNDO_FLAG);
    this.exitPort = rs.getString(FlowStateHistoryTO.EXIT_PORT);
  }
  
  /*
   * (non-Javadoc)
   * @see pt.iflow.api.transition.DBTransitionObject#getValueOf(java.lang.String)
   */
  public String getValueOf(String columnName) {
    String retObj = null;
    if (StringUtils.equals(columnName, FLOW_ID)) {
      retObj = "" + getFlowid();
    } else if (StringUtils.equals(columnName, PID)) {
        retObj = "" + getPid();
    } else if (StringUtils.equals(columnName, SUBPID)) {
      if (getSubpid() != null) {
        retObj = "" + getSubpid();
      }
    } else if(StringUtils.equals(columnName, STATE)) {
      retObj = "" + getState();
    } else if(StringUtils.equals(columnName, RESULT)) {
      if (getResult() != null) {
        retObj = "'" + getResult() + "'";
      }
    } else if(StringUtils.equals(columnName, MDATE)) {
      if (getMDate() != null) {
        retObj = "'" + getMDate() + "'";
      }
    } else if(StringUtils.equals(columnName, MID)) {
      if (getMid() != null) {
        retObj = "" + getMid();
      }
    } else if(StringUtils.equals(columnName, EXIT_FLAG)) {
      retObj = "" + getExitFlag();
    } else if(StringUtils.equals(columnName, UNDO_FLAG)) {
      if (getUndoFlag() != null) {
        retObj = "" + getUndoFlag();
      }
    } else if(StringUtils.equals(columnName, EXIT_PORT)) {
      if (getExitPort() != null) {
        retObj = "'" + getExitPort() + "'";
      }
    }
    return retObj;
  }

  public Integer getFlowid() {
    return flowid;
  }

  public void setFlowid(Integer flowid) {
    this.flowid = flowid;
  }

  public Integer getPid() {
    return pid;
  }

  public void setPid(Integer pid) {
    this.pid = pid;
  }

  public Integer getSubpid() {
    return subpid;
  }

  public void setSubpid(Integer subpid) {
    this.subpid = subpid;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public Integer getMid() {
    return mid;
  }

  public void setMid(Integer mid) {
    this.mid = mid;
  }

  public Integer getExitFlag() {
    return exitFlag;
  }

  public void setExitFlag(Integer exitFlag) {
    this.exitFlag = exitFlag;
  }

  public Integer getUndoFlag() {
    return undoFlag;
  }

  public void setUndoFlag(Integer undoFlag) {
    this.undoFlag = undoFlag;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public Timestamp getMDate() {
    return mDate;
  }

  public void setMDate(Timestamp date) {
    mDate = date;
  }

  public String getExitPort() {
    return exitPort;
  }

  public void setExitPort(String exitPort) {
    this.exitPort = exitPort;
  }
  
  public String getModificationUser() {
    return modificationUser;
  }

  public void setModificationUser(String moduser) {
    modificationUser = moduser;
  }

  public void setUndoable(boolean undoable) {
    this.undoable = undoable;
  }
  
  public boolean isUndoable() {
    return undoable;
  }
  
}
