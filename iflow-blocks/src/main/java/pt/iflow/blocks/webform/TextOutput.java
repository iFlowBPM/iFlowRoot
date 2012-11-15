package pt.iflow.blocks.webform;

import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class TextOutput extends AbstractInput {

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Processing a Text Output for "+field.getVariable());
    String varName = field.getVariable();
    
    ch.startElement("field");
    ch.addElement("type", "textlabel");
    ch.addElement("text", getTitle(userInfo, process, field));
    ch.addElement("variable", varName);
    // TODO process format
    ch.addElement("value", process.getFormatted(varName));
    ch.addElement("even_field", String.valueOf(even));
    ch.addElement("disabled", "true");
    ch.addElement("readonly", "true");
    ch.endElement("field");

  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
  }

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, int row) throws SAXException {
    ch.appendText(process.getListItemFormatted(field.getVariable(), row));
  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request, int row) {
  }

  public boolean isReadOnly(Field fld) {
    return true;
  }
}
