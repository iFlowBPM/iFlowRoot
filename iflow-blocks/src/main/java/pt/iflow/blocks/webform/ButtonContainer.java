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

import java.util.List;

import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.IWidget;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class ButtonContainer extends AbstractWidget {

  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Generating a Button Container");
    ch.startElement("field");
    ch.addElement("type", "buttoncontainer");
    ch.addElement("title", field.getProperties().get("title"));
    List<Field> buttons = field.getFields();
    boolean evenButton = true;
    for(Field button:buttons) {
      IWidget buttonWidget = newWidget(button.getType());
      if(null == buttonWidget) {
        Logger.warning(userInfo.getUtilizador(), this, "generate", "'Button' type not found. Please check: "+button.getType());
        continue;
      }
      Logger.debug(userInfo.getUtilizador(), this, "generate", "Processing button ID: "+field.getId());
      buttonWidget.generate(userInfo, process, ch, button, evenButton = ! evenButton);
    }
    ch.addElement("even_field", String.valueOf(even));
    ch.endElement("field");
  }

  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    List<Field> buttons = field.getFields();
    for(Field button:buttons) {
      IWidget buttonProcessor = newWidget(button.getType());
      if(null == buttonProcessor) {
        Logger.warning(userInfo.getUtilizador(), this, "process", "'Button' type not found. Please check: "+button.getType());
        continue;
      }
      Logger.debug(userInfo.getUtilizador(), this, "process", "Processing button ID: "+field.getId());
      buttonProcessor.process(userInfo, process, button, request);
    }
  }
  
}
