package pt.iflow.api.datatypes;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public interface FormTableValueFeeder {

  public String[] getValues(UserInfoInterface userInfo, ProcessData procData, String varName, int index);
  
}
