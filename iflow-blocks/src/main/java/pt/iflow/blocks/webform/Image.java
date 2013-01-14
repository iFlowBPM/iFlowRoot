package pt.iflow.blocks.webform;

import java.util.Map;

import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class Image extends AbstractWidget {

  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Generating an Image token");
    Map<String,String> props = field.getProperties();
    ch.startElement("field");
    ch.addElement("type", "image");
    ch.addElement("url", props.get("src"));
    ch.addElement("align", props.get("align"));
    ch.addElement("alt_text", props.get("t"));
    ch.addElement("width", props.get("width"));
    ch.addElement("height", props.get("height"));
    ch.addElement("even_field", String.valueOf(even));
    ch.endElement("field");
  }

  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
  }
  
}
