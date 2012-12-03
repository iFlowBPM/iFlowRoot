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

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.Form;
import pt.iflow.api.blocks.form.IWidget;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.blocks.form.Tab;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

import com.twolattes.json.Marshaller;

public class Template extends AbstractWidget {

  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Generating from template");
    // TODO if disabled, return;
    
    String template = field.getProperties().get("template");
    
    IFlowData flow = BeanFactory.getFlowHolderBean().getFlow(userInfo, process.getFlowId());
    Form formTemplate = null;
    if(flow !=null)
      formTemplate = flow.getFormTemplate(template);
    if(null == formTemplate) {
      // ignore or throw error?
      Logger.error(userInfo.getUtilizador(), this, "generate", "Could not parse template: '"+template+"'");
      ch.startElement("field");
      ch.addElement("type", "message");
      ch.addElement("text", "Template de formulário inválida");
      ch.addElement("even_field", String.valueOf(even));
      ch.endElement("field");
      return;
    }
    
    // allways get first tab
    Tab tab = formTemplate.getTabs().get(0);
    for(Field subField : tab.getFields()) {
      IWidget subfieldWidget = newWidget(subField.getType());
      subfieldWidget.generate(userInfo, process, ch, subField, even);
      even = !even;
    }
    
  }

  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    // TODO if readonly or disabled, return;
    
    String template = field.getProperties().get("template");
    
    Form formTemplate = null; 
    try {
      // FIXME obter a template
      formTemplate = Marshaller.create(Form.class).unmarshall(new JSONObject("{}"));
    } catch (JSONException e) {
      // ignore or throw error?
      Logger.error(userInfo.getUtilizador(), this, "process", "Could not parse template: '"+template+"'");
      return;
    }
    
    Tab tab = formTemplate.getTabs().get(0);
    for(Field subField : tab.getFields()) {
      IWidget subfieldWidget = newWidget(subField.getType());
      subfieldWidget.process(userInfo, process, subField, request);
    }
    
  }
  
}
