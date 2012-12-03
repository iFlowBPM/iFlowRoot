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

import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;


public class SQLLabel extends TextLabel {

  public String getDescription() {
    return "Saída de Texto SQL";
  }
  
  
  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    
    String value = SQLFieldHelper.getSQLData(userInfo, procData, props);
    
    if (StringUtils.isNotEmpty(value)) {
      value = FormUtils.escapeAmp(value);
    }

    props.setProperty("value", value);
    
    super.setup(userInfo, procData, props, response);
  }
}
