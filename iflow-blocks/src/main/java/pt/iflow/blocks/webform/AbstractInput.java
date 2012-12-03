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
package pt.iflow.blocks.webform;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public abstract class AbstractInput extends AbstractWidget implements ITableColumn {
  
  public abstract void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException;
  
  public abstract void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request);
  
  public String getTitle(UserInfoInterface userInfo, ProcessData process, Field field) {
    Map<String,String> props = field.getProperties();
    String override = props.get("override");
    String overrideLabel = props.get("text");
    String label = process.getCatalogue().getPublicName(field.getVariable());
    if(StringUtils.equals(override, "true") && StringUtils.isNotEmpty(overrideLabel))
      label = overrideLabel;
    
    if(StringUtils.isEmpty(label)) {
      Logger.warning(userInfo.getUtilizador(), this, "getTitle", "Label is empty. Assuming var name...");
      label = field.getVariable();
    }
    
    return label;
  }
  
  public abstract void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, int row) throws SAXException;
  
  public abstract void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request, int row);

}
