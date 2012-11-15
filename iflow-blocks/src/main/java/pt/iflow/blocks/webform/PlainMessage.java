package pt.iflow.blocks.webform;

import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;
/**
 * Simple message presented to user. Types implementing this class:
 *  - header
 *  - sub header
 *  - text message
 * 
 * 
 * @author ombl
 *
 */
public class PlainMessage extends AbstractWidget {

  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Generating a plain message. type: "+field.getType());
    String type = field.getType().substring(2); // remove w_
    ch.startElement("field");
    ch.addElement("type", type);
    ch.addElement("text", field.getProperties().get("text"));
    ch.addElement("even_field", String.valueOf(even));
    ch.endElement("field");
  }

  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
  }
  
}
