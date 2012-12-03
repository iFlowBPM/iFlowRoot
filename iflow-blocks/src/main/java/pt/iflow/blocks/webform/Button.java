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

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class Button extends AbstractWidget {
  
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.debug(userInfo.getUtilizador(), this, "readButton", "Processing button ID: "+field.getId());
    
    String bType = field.getProperties().get("type");
    
    ch.startElement("button");
    ch.addElement("text", field.getProperties().get("text"));
    if(StringUtils.equals("custom", bType)) {
      generateCustom(userInfo, process, ch, field, even);
    } else if(StringUtils.equals("reset", bType)) {
      generateReset(userInfo, process, ch, field, even);
    } else if(StringUtils.equals("print", bType)) {
      generatePrint(userInfo, process, ch, field, even);
    } else if(StringUtils.equals("cancel", bType)) {
      generateCancel(userInfo, process, ch, field, even);
    } else if(StringUtils.equals("save", bType)) {
      generateSave(userInfo, process, ch, field, even);
    } else { // submit
      generateSubmit(userInfo, process, ch, field, even);
    }
    ch.endElement("button");
  }

  
  private void generateSubmit(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    ch.addElement("name", "_avancar");
    ch.addElement("operation", "if (CheckEmptyFields()) this.form.op.value='3'; else return false;");
  }
  
  private void generatePrint(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    ch.addElement("name", "_imprimir");
    ch.addElement("operation", "PrintService(null);return false;");
  }
  
  private void generateCancel(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    ch.addElement("name", "_cancelar");
    ch.addElement("operation", "if (confirm('Deseja cancelar/fechar o processo?')) this.form.op.value=4; else return false;");
  }
  
  private void generateCustom(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    ch.addElement("name", "_custom");
    ch.addElement("operation", "if (CheckEmptyFields()) this.form.op.value='3'; else return false;");
  }
  
  private void generateReset(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    ch.addElement("name", "_repor");
    ch.addElement("operation", "this.form.op.value='0'");
  }
  
  private void generateSave(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    ch.addElement("name", "_guardar");
    ch.addElement("operation", "this.form.op.value='2'");
  }
  
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    // TODO Clicked button
  }

}
