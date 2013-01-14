package pt.iflow.blocks.operations;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public abstract class AbstractArithmeticOperation implements DataOperation {
  private String result;
  private String op1;
  private String op2;
  private boolean isValid;


  public void setup(final UserInfoInterface userInfo, final Map<String,String> params) {
    result = params.get("result");
    op1 = params.get("op1");
    op2 = params.get("op2");
    isValid = (StringUtils.isNotEmpty(result) && StringUtils.isNotEmpty(op1) && StringUtils.isNotEmpty(op2));
  }

  public void execute(final UserInfoInterface userInfo, final ProcessData procData) {
    if(!isValid) return; // do nothing...

    try {
      Object val1 = "";
      Object val2 = "";

      try {
        val1 = procData.eval(userInfo, op1); // evaluates 3.14 and xpto+34*12-arr[idx]
      } catch(EvalException e) {}

      try {
        val2 = procData.eval(userInfo, op2);
      } catch(EvalException e) {}

      double dVal1 = ((Number)val1).doubleValue();
      double dVal2 = ((Number)val2).doubleValue();

      Double dResult = new Double(numericOperation(dVal1, dVal2));

      ProcessSimpleVariable simpleVar = procData.get(result);
      if(null == simpleVar) {
        ProcessListVariable listVar = procData.getList(result);
        if(null == listVar) throw new Exception("Variable "+result+" does not exist");
        for(int i = 0; i < listVar.size(); i++)
          listVar.setItemValue(i, dResult);
      } else {
        simpleVar.setValue(dResult);
      }

    } catch(Throwable t) {
      Logger.info(userInfo.getUtilizador(), this, "execute", "Error executing aritmetic operation.", t);
    }
  }

  protected abstract double numericOperation(double op1, double op2);

}
