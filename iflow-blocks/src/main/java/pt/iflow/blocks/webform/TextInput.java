package pt.iflow.blocks.webform;

import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class TextInput extends AbstractInput {

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Processing a Text Box Input for "+field.getVariable());
    String varName = field.getVariable();
    Map<String,String> props = field.getProperties();
    String type = props.get("input");
    if(StringUtils.equals(type, "text"))
      type = "textbox";

    ch.startElement("field");
    ch.addElement("type", type);
    ch.addElement("text", getTitle(userInfo, process, field));
    ch.addElement("variable", varName);
    // TODO apply formats
    ch.addElement("value", process.getFormatted(varName));
    ch.addElement("size", getDefaultBlank(props.get("width"), "10"));
    ch.addElement("maxlength", getDefaultBlank(props.get("maxsize"), "40"));
    ch.addElement("even_field", String.valueOf(even));
    // ch.addElement("disabled", "true");
    ch.addElement("readonly", props.get("readonly"));
    // in the case of a textarea
    ch.addElement("cols", getDefaultBlank(props.get("cols"), "5"));
    ch.addElement("rows", getDefaultBlank(props.get("rows"), "40"));

    ch.endElement("field");
  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    if(isReadOnly(field)) return;
    String nValue = request.getParameter(field.getVariable());
    // TODO apply formats
    try {
      process.parseAndSet(field.getVariable(), nValue);
    } catch (ParseException e) {
      Logger.warning(userInfo.getUtilizador(), this, "process", "Error parsing variable "+field.getVariable());
    }
  }

  
  
  // Table row stuff
  
  
  @Override
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, int row) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Processing a Text Box Input for "+field.getVariable());
    String varName = field.getVariable();
    Map<String,String> props = field.getProperties();
    String type = props.get("input");
    
    // TODO extender para os outros tipos de dados.
    if(StringUtils.equals(type, "text"))
      type = "tabletext";
    
    ch.startElement("input");
//    ch.addElement("type", "tabletext");
    ch.addElement("type", type);
    ch.addElement("id", varName+"["+row+"]");
    ch.addElement("name", varName+"["+row+"]");
    ch.addElement("variable", varName+"["+row+"]");
    // TODO apply formats
    ch.addElement("value", process.getListItemFormatted(varName, row));
    ch.addElement("size", getDefaultBlank(props.get("width"), "10"));
    ch.addElement("maxlength", getDefaultBlank(props.get("maxsize"), "40"));
    // ch.addElement("disabled", "true");
    ch.addElement("readonly", props.get("readonly"));
    // in the case of a textarea
    ch.addElement("cols", getDefaultBlank(props.get("cols"), "5"));
    ch.addElement("rows", getDefaultBlank(props.get("rows"), "40"));

    ch.endElement("input");
  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request, int row) {
    if(isReadOnly(field)) return;
    String varName = field.getVariable();
    String nValue = request.getParameter(varName+"["+row+"]");
    // TODO apply formats
    try {
      process.parseAndSetListItem(field.getVariable(), nValue, row);
    } catch (ParseException e) {
      Logger.warning(userInfo.getUtilizador(), this, "process", "Error parsing variable "+field.getVariable());
    }
  }

  public boolean isReadOnly(Field field) {
    return (StringUtils.equalsIgnoreCase(field.getProperties().get("readonly"),"true") || 
        StringUtils.equalsIgnoreCase(field.getProperties().get("disabled"),"true"));
  }

}
