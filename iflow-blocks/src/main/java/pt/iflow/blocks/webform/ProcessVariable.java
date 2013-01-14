package pt.iflow.blocks.webform;

import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class ProcessVariable extends AbstractWidget {

  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even)
      throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Generating a Process Data token");

    AbstractWidget formWidget = getWidget(field.getProperties().get("input"));
    if (null == formWidget) {
      Logger.warning(userInfo.getUtilizador(), this, "generate", "Type implementation not found. Assuming text out...");

      formWidget = new TextOutput();
    } else {
      formWidget.generate(userInfo, process, ch, field, even);
    }

  }

  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    AbstractWidget formWidget = getWidget(field.getProperties().get("input"));
    if (null == formWidget) {
      Logger.warning(userInfo.getUtilizador(), this, "process", "Type implementation not found. Ignoring...");
    } else {
      formWidget.process(userInfo, process, field, request);
    }
  }

  AbstractInput getWidget(String type) {
    ProcessVariableInputTypeEnum widgetEnum = null;
    try {
      widgetEnum = ProcessVariableInputTypeEnum.valueOf(type);
    } catch (Throwable e) {
      Logger.debug(null, this, "getWidget", "Process Variable type not found: " + type+". Assuming text label.");
      widgetEnum = ProcessVariableInputTypeEnum.label;
    }
    if (null == widgetEnum)
      return null;
    Class<? extends AbstractInput> widgetClass = widgetEnum.getWidgetClass();
    if (null == widgetClass)
      return null;

    AbstractInput widget = null;
    try {
      widget = widgetClass.newInstance();
    } catch (Throwable t) {
    }
    return widget;
  }

}
