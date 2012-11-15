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
