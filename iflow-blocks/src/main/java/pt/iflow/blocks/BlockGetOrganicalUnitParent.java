package pt.iflow.blocks;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockGetOrganicalUnitParent</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockGetOrganicalUnitParent extends BlockGetAuthInfo {

  public BlockGetOrganicalUnitParent(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    saveFlowState = false;
    this._nOp = nOP_GET_ORG_UNIT_PARENT;
    this._sDefaultKey = sDEFAULT_KEY_ORGANICAL_UNIT;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Get Organical Unit Parent");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Got Organical Unit Parent");
  }

}
