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
