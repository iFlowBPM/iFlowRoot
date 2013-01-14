package pt.iflow.blocks.operations;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public class OperationArrayTrim implements DataOperation {
  private String result;
  private String length;
  private String value;
  private boolean isValid;


  public void setup(UserInfoInterface userInfo, Map<String,String> params) {
    result = params.get("result");
    length = params.get("length");
    value = params.get("value");
    isValid = (StringUtils.isNotEmpty(result) && StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(length));
  }

  public void execute(UserInfoInterface userInfo, ProcessData procData) {
    if(!isValid) return; // do nothing...
    boolean isArray = procData.isListVar(result);
    if(!isArray) return;
    
    // auto purge
    
  }

}
