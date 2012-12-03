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

import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class TextOutput extends AbstractInput {

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Processing a Text Output for "+field.getVariable());
    String varName = field.getVariable();
    
    ch.startElement("field");
    ch.addElement("type", "textlabel");
    ch.addElement("text", getTitle(userInfo, process, field));
    ch.addElement("variable", varName);
    // TODO process format
    ch.addElement("value", process.getFormatted(varName));
    ch.addElement("even_field", String.valueOf(even));
    ch.addElement("disabled", "true");
    ch.addElement("readonly", "true");
    ch.endElement("field");

  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
  }

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, int row) throws SAXException {
    ch.appendText(process.getListItemFormatted(field.getVariable(), row));
  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request, int row) {
  }

  public boolean isReadOnly(Field fld) {
    return true;
  }
}
