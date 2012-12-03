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

import pt.iflow.api.blocks.form.IWidget;
import pt.iflow.api.blocks.form.IWidgetFactory;
import pt.iflow.api.utils.Logger;

public class WebWidgetFactory implements IWidgetFactory {

  public IWidget newWidget(String type) {
    WebWidgetEnum widgetEnum = null;
    try {
      widgetEnum = WebWidgetEnum.valueOf(type);
    } catch (Throwable e) {
      Logger.debug(null, this, "newWidget", "Widget type not found: " + type);
    }
    if (null == widgetEnum)
      return null;
    Class<? extends AbstractWidget> widgetClass = widgetEnum.getWidgetClass();
    if (null == widgetClass)
      return null;

    AbstractWidget widget = null;
    try {
      widget = widgetClass.newInstance();
      widget.setWidgetFactory(this);
    } catch (Throwable t) {
    }
    return widget;
  }

}
