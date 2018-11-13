package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * <p>Title: BlockDelegationInfo</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Infosistema</p>
 * @author Miguel Guilherme
 */

public abstract class BlockDelegationInfo extends Block {
  public Port portIn, portSuccess, portEmpty, portError;

  public BlockDelegationInfo(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    this.hasInteraction = false;
    saveFlowState = true;
  }

  /**
   * Retrieve In Ports.
   */
  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = this.portIn;
    return retObj;
  }

  /**
   * Retrieve Out Ports.
   */
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[3];
    retObj[0] = this.portSuccess;
    retObj[1] = this.portEmpty;
    retObj[2] = this.portError;
    return retObj;
  }

  /**
   * No action.
   */
  public Port getEventPort() {
    return null;
  }

  /**
   * No action.
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action.
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * No action.
   */
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * Retireve the owner.
   * @param userInfo User Info.
   * @param procData Process Data.
   * @return The Owner.
   */
  protected Port getOwner(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portError;

    final String ownerQry = "select ownerid from activity_delegated " +
    "where userid = ? and flowid = ? and pid = ? ";

    int flowid = procData.getFlowId();
    String login = userInfo.getUtilizador();

    Connection connection = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    try {
      String userAttr = getAttribute("substitute");
      if (StringUtils.isEmpty(userAttr)) {
        throw new Exception("Invalid user attribute.");
      }

      String user = procData.transform(userInfo, userAttr);
      if (StringUtils.isEmpty(user)) {
        throw new Exception("Invalid user.");
      }

      connection = Utils.getDataSource().getConnection();
      pstmt = connection.prepareStatement(ownerQry);
      //pstmt.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
      pstmt.setString(1, user);
      pstmt.setInt(2, flowid);
      pstmt.setInt(3, procData.getPid());
      rs = pstmt.executeQuery();

      String owner = null;
      if (rs.next()) {
        owner = rs.getString("ownerid");
        outPort = portSuccess;
      }
      else {
        outPort = portEmpty;
      }

      String ownerDestVar = getAttribute("taskOwner");
      if (StringUtils.isNotEmpty(ownerDestVar)) {
        procData.parseAndSet(ownerDestVar, owner);
      }
    }
    catch (Exception e) {
      Logger.error(login, this, "after", 
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
      return portError;
    }
    finally {
      DatabaseInterface.closeResources(connection, pstmt, rs);
    }
    return outPort;
  }

  /**
   * Retireve the substitute.
   * @param userInfo User Info.
   * @param procData Process Data.
   * @return The SUbstitute.
   */
  protected Port getSubstitute(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portError;

    final String substituteQry = "select userid from activity_hierarchy " +
    "where pending = 0 and expires >= ? and ownerid = ? and flowid = ?";

    int flowid = procData.getFlowId();
    String login = userInfo.getUtilizador();

    Connection connection = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    try {
      String userAttr = getAttribute("taskOwner");
      if (StringUtils.isEmpty(userAttr)){
        throw new Exception("Invalid user attribute.");
      }

      String user = procData.transform(userInfo, userAttr);
      if (StringUtils.isEmpty(user)) {
        throw new Exception("Invalid user.");
      }
      connection = DatabaseInterface.getConnection(userInfo);
      pstmt = connection.prepareStatement(substituteQry);
      pstmt.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
      pstmt.setString(2, user);
      pstmt.setInt(3, flowid);
      rs = pstmt.executeQuery();

      String userid = null;
      if (rs.next()) {
        userid = rs.getString("userid");
        outPort = portSuccess;
      }
      else {
        outPort = portEmpty;
      }

      String substituteDestVar = getAttribute("substitute");
      if (StringUtils.isNotEmpty(substituteDestVar)) {
        procData.parseAndSet(substituteDestVar, userid);
      }
    }
    catch (Exception e) {
      Logger.error(login, this, "after", 
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
      return portError;
    }
    finally {
      DatabaseInterface.closeResources(connection, pstmt, rs);
    }
    return outPort;
  }
}