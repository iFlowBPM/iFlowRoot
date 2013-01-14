package pt.iflow.blocks;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.blocks.msg.Messages;

/**
 * <p>Title: BlockDelegationSubstitute</p>
 * <p>Description: Retrieve the substitute of a delegated flow</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Infosistema</p>
 * @author Miguel Guilherme
 */

public class BlockDelegationSubstitute extends BlockDelegationInfo {
  
  public BlockDelegationSubstitute(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    saveFlowState = false;
  }

  /**
   * Block main action.
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    return getSubstitute(userInfo, procData);
  }

  /**
   * Retrieve description.
   */
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));
    return this.getDesc(userInfo, procData, true, msg.getString("BlockDelegationSubstitute.description"));
  }

  /**
   * Retrieve result.
   */
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));
    return this.getDesc(userInfo, procData, true, msg.getString("BlockDelegationSubstitute.result"));
  }
}