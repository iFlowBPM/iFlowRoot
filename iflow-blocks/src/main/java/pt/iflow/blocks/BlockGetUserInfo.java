package pt.iflow.blocks;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockGetUserInfo</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockGetUserInfo extends BlockGetAuthInfo {

  public BlockGetUserInfo(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    
    this._nOp = nOP_GET_USER_INFO;
    this._sDefaultKey = sDEFAULT_KEY_USER_ID;
    this._sDefaultKey2 = sDEFAULT_KEY_NUM_EMP;
    this._sDefaultKey3 = sDEFAULT_KEY_USER_EMAIL;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Get User Info");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData dataSet) {
    return this.getDesc(userInfo, dataSet, false, "Got User Info");
  }

}
