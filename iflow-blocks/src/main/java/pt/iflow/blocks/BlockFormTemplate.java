package pt.iflow.blocks;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockFormTemplate extends Block {

    public BlockFormTemplate(int anFlowId, int id, int subflowblockid, String filename) {
        super(anFlowId, id, subflowblockid, filename);
    }

  @Override
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return false;
    }

  @Override
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public Port getEventPort() {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public Port[] getInPorts(UserInfoInterface userInfo) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
    public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
    public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

}
