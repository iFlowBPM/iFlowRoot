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

import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class DateInput extends AbstractInput {

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData procData, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Processing a Date Box Input for "+field.getVariable());
    String varName = field.getVariable();
    Map<String,String> props = field.getProperties();

    // TODO apply formats
    String sValue = procData.getFormatted(varName);
    String sDateFormat = props.get("format");
    if (sDateFormat != null) sDateFormat = sDateFormat.trim();
    if (StringUtils.isEmpty(sValue)) {
        sValue = DateUtility.newBlockDate(props.get(Block.sORG_ID_PROP), sDateFormat);
    }
    
    ch.startElement("field");

    ch.addElement("type", "datecal");
    ch.addElement("text", getTitle(userInfo, procData, field));
    ch.addElement("variable", varName);
    ch.addElement("value", sValue);
    ch.addElement("size", props.get("width"));
    ch.addElement("maxlength", props.get("maxsize"));
    ch.addElement("even_field", String.valueOf(even));
    // ch.addElement("disabled", "true");
    ch.addElement("readonly", props.get("readonly"));
    ch.addElement("dateformat", sDateFormat);
    ch.addElement("dateformatid", props.get("date_format_id"));
    
//    String override = props.get("override");
//    String label = process.getCatalogue().getPublicName(field.getVariable());
//    if(StringUtils.equals(override, "true"))

    ch.endElement("field");
  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    if(isReadOnly(field)) return;
    String nValue = request.getParameter(field.getVariable());
    // TODO apply formats
    try {
      process.parseAndSet(field.getVariable(), nValue);
    } catch (ParseException e) {
      Logger.warning(userInfo.getUtilizador(), this, "process", "Error parsing variable "+field.getVariable());
    }

  }

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, int row) throws SAXException {
    // TODO Auto-generated method stub
    ch.appendText("Data nas tabelas Não está implementado");
  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request, int row) {
    // TODO Auto-generated method stub
    Logger.info(userInfo.getUtilizador(), this, "process", "Data nas tabelas Não está implementado");
  }
  
  
  public boolean isReadOnly(Field field) {
    return (StringUtils.equalsIgnoreCase(field.getProperties().get("readonly"),"true") || 
        StringUtils.equalsIgnoreCase(field.getProperties().get("disabled"),"true"));
  }


}
