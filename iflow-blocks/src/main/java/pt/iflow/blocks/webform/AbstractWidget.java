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
import pt.iflow.api.blocks.form.IWidget;
import pt.iflow.api.blocks.form.IWidgetFactory;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public abstract class AbstractWidget implements IWidget {
  
  private IWidgetFactory widgetFactory;
  
  public AbstractWidget() {
    this.widgetFactory = null;
  }

  protected String getDefaultEmpty(String txt, String def) {
    if(StringUtils.isEmpty(txt)) return def;
    return txt;
  }
  
  protected String getDefaultBlank(String txt, String def) {
    if(StringUtils.isBlank(txt)) return def;
    return txt;
  }
  
  public abstract void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException;
  
  public abstract void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request);

  public IWidgetFactory getWidgetFactory() {
    return widgetFactory;
  }
  
  public void setWidgetFactory(IWidgetFactory widgetFactory) {
    this.widgetFactory = widgetFactory;
  }
  
  public IWidget newWidget(String type) {
    IWidget result = null;
    if(null != this.widgetFactory)
      result = this.widgetFactory.newWidget(type);
    return result;
  }
}
