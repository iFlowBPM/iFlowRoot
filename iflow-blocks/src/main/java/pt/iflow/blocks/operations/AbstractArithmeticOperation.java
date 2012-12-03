/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
