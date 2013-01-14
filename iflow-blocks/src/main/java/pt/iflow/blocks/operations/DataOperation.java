package pt.iflow.blocks.operations;

import java.util.Map;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public interface DataOperation {
  public abstract void setup(final UserInfoInterface userInfo, final Map<String,String> params);
  public abstract void execute(final UserInfoInterface userInfo, final ProcessData procData);
}
