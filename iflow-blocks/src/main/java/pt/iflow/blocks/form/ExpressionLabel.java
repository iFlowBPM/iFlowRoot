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
package pt.iflow.blocks.form;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class ExpressionLabel extends TextLabel {

  public String getDescription() {
    return "Expressão Saída de Texto";
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    String userid = userInfo.getUtilizador();
    String value = null;    
    try {
      String expr = props.getProperty(FormProps.EXPRESSION);
      value = procData.transform(userInfo, expr);      
      if (Logger.isDebugEnabled()) {
        Logger.debug(userid, this, "setup", 
            procData.getSignature() + "expression: '" + expr + "' evaluated to '" + value + "'");        
      }
    } 
    catch (EvalException e) {
      Logger.warning(userid, this, "setup", 
          procData.getSignature() + "error processing expression", e);
    }
    
    if (StringUtils.isEmpty(value))
      value = "";
    
    try {
      DataTypeInterface dti = getDataType(userInfo, props);
      if (dti != null) {
        value = dti.formatToHtml(value);
        
        if (Logger.isDebugEnabled()) {
          Logger.debug(userid, this, "setup", 
              procData.getSignature() + "value formatted to: '" + value + "'");        
        }        
      }
    }
    catch (Exception e) {
      Logger.warning(userid, this, "setup", 
          procData.getSignature() + "exception trying to format value with datatype", e);
    }
    
    
    props.setProperty("value", value);
    
    super.setup(userInfo, procData, props, response);
  }
  
  private static DataTypeInterface getDataType(UserInfoInterface userInfo, Properties props) throws Exception {
    DataTypeInterface dti = null;
    
    String datatypeName = props.getProperty(FormProps.sDATATYPE);
    
    if (StringUtils.isNotEmpty(datatypeName)) {
      Class<? extends DataTypeInterface> cClass = 
        (Class<? extends DataTypeInterface>)Class.forName(datatypeName);

      dti = cClass.newInstance();
      dti.setLocale(userInfo.getUserSettings().getLocale());
    }
    return dti;
  }
}
