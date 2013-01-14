package pt.iflow.blocks.operations;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.UserInfoInterface;

public class OperationInit implements DataOperation {
  private String result;
  private String length;
  private String value;
  private boolean isValid;


  public void setup(final UserInfoInterface userInfo, final Map<String,String> params) {
    result = params.get("result");
    length = params.get("length");
    value = params.get("value");
    isValid = (StringUtils.isNotEmpty(result) && StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(length));
  }

  public void execute(final UserInfoInterface userInfo, final ProcessData procData) {
    if(!isValid) return; // do nothing...
    boolean isArray = procData.isListVar(result);
    if(!isArray) return;

    final int len = Integer.parseInt(length);

    procData.clearList(result);
    String val = null;
    try {
      val = procData.transform(userInfo, value);
    } catch (EvalException e1) {
      // TODO 
    }
    ProcessListVariable lv = procData.getList(result);
    for(int i = 0; i < len; i++) {
    	try {
    		lv.parseAndSetItemValue(i, val);
    	}
    	catch (Exception e) {
    		// TODO
    	}
    }
    procData.setList(lv);
  }

}
