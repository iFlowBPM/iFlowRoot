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

public class CheckboxInput extends AbstractInput {

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData procData, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Processing a Checkbox Input for "+field.getVariable());
    String varName = field.getVariable();
    String value = procData.getFormatted(varName);
    Map<String,String> props = field.getProperties();

    ch.startElement("field");
    ch.addElement("type", "checkbox");
    ch.addElement("text", getTitle(userInfo, procData, field));
    ch.addElement("variable", varName);
    ch.addElement("value", value);
    // TODO apply formats
    ch.addElement("selected", StringUtils.equals(value, "1")?"true":"false");
    ch.addElement("onchange", props.get("onchange"));
    ch.addElement("even_field", String.valueOf(even));
    // ch.addElement("disabled", "true");
    ch.addElement("readonly", props.get("readonly"));

    ch.endElement("field");

  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    if(isReadOnly(field)) return;
    String nValue = request.getParameter(field.getVariable());
    nValue = StringUtils.isEmpty(nValue)?"0":"1";
    try {
      process.parseAndSet(field.getVariable(), nValue);
    } catch (ParseException e) {
      Logger.warning(userInfo.getUtilizador(), this, "process", "Error parsing variable "+field.getVariable());
    }
  }

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData procData, ParserContext ch, Field field, int row) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Processing a Checkbox Input for "+field.getVariable());
    String varName = field.getVariable();
    String value = procData.getListItemFormatted(varName, row);
    Map<String,String> props = field.getProperties();
    
    ch.startElement("input");
    ch.addElement("type", "checkbox");
    ch.addElement("text", getTitle(userInfo, procData, field));
    ch.addElement("variable", varName+"["+row+"]");
    ch.addElement("value", value);
    // TODO apply formats
    ch.addElement("selected", StringUtils.equals(value, "1")?"true":"false");
    ch.addElement("onchange", props.get("onchange"));
    // ch.addElement("disabled", "true");
    ch.addElement("readonly", props.get("readonly"));

    ch.endElement("input");
    
  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request, int row) {
    // TODO Auto-generated method stub
    if(isReadOnly(field)) return;
    String nValue = request.getParameter(field.getVariable()+"["+row+"]"); // compat with previous versions of BlockFormulario
    nValue = StringUtils.isEmpty(nValue)?"0":"1";
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
