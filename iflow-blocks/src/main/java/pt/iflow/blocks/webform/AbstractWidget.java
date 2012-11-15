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
