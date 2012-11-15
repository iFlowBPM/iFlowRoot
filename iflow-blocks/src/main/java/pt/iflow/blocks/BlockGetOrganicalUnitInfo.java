package pt.iflow.blocks;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockGetOrganicalUnitInfo</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockGetOrganicalUnitInfo extends BlockGetAuthInfo {

  public BlockGetOrganicalUnitInfo(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = false;
    this._nOp = nOP_GET_ORG_UNIT_INFO;
    this._sDefaultKey = sDEFAULT_KEY_ORGANICAL_UNIT;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Get Organical Unit Info");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Got Organical Unit Info");
  }

}
