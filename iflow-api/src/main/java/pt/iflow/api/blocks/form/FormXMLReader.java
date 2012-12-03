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
package pt.iflow.api.blocks.form;

import java.io.IOException;
import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.AbstractXMLReader;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class FormXMLReader extends AbstractXMLReader {
  
  private final UserInfoInterface userInfo;
  private final IWidgetFactory widgetRegistry;
  private final ProcessData procData;
  private final Form form;
  private String formName = "dados";
  private String jsp = "webform.jsp";
  private ContentHandler debugHandler = null;
  private final ServletUtils request;
  
  public FormXMLReader(final UserInfoInterface userInfo, final ProcessData procData, final Form form, final IWidgetFactory widgetRegistry, final ServletUtils request) {
    this.procData = procData;
    this.userInfo = userInfo;
    this.form = form;
    this.widgetRegistry = widgetRegistry;
    this.request = request;
  }
  
  public void setDebugContentHandler(ContentHandler debugHandler) {
    this.debugHandler = debugHandler;
  }
  
  public String getFormName() {
    return formName;
  }

  public void setFormName(String formName) {
    this.formName = formName;
  }

  public String getJsp() {
    return jsp;
  }

  public void setJsp(String jsp) {
    this.jsp = jsp;
  }

  public UserInfoInterface getUserInfo() {
    return userInfo;
  }

  public ProcessData getProcData() {
    return procData;
  }

  public Form getForm() {
    return form;
  }

  public IWidgetFactory getWidgetRegistry() {
    return widgetRegistry;
  }
  
  public ServletUtils getRequest() {
    return request;
  }

  @Override
  public void parse(InputSource input) throws IOException, SAXException {
    // if no handler is registered to receive events, don't bother
    ParserContext ch = new ParserContext(getContentHandler(), debugHandler);

    generateForm(ch);
    
  }
  
  void generateForm(ParserContext ch) throws IOException, SAXException {
    ch.startDocument();

    ch.startElement("form");
    ch.addElement("name", formName);
    ch.addElement("action", jsp);

    List<Tab> tabs = form.getTabs();
    for(Tab tab : tabs) {
      // start stuff
      ch.startElement("blockdivision").startElement("columndivision");

      boolean even = true;
      List<Field> fields = tab.getFields();
      for(Field field:fields) {
        readField(ch, field, even = !even);
      }
      
      ch.endElement("columndivision").endElement("blockdivision");
    }
    
    // hidden fields
    ch.addHiddenField("op", "2");
    ch.addHiddenField("flowid", String.valueOf(procData.getFlowId()));
    ch.addHiddenField("pid", String.valueOf(procData.getPid()));
    ch.addHiddenField("subpid", String.valueOf(procData.getSubPid()));
    ch.addHiddenField("_serv_field_", "-1");
    

    // end document
    ch.endElement("form");
    ch.endDocument();
  }
  
  
  public void readField(final ParserContext ch, final Field field, final boolean even) throws SAXException {
    Logger.debug(userInfo.getUtilizador(), this, "readField", "Processing field ID: "+field.getId());
    IWidget widget = getWidgetRegistry().newWidget(field.getType());
    if(null == widget) {
      Logger.warning(userInfo.getUtilizador(), this, "readField", "Processing field type not implemented: "+field.getType());
      return;
    }
    
    widget.generate(getUserInfo(), getProcData(), ch, field, even);
  }

}
