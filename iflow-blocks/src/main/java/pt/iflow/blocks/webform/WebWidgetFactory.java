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
