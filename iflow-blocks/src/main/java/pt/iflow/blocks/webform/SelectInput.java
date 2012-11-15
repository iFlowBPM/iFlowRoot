package pt.iflow.blocks.webform;

import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class SelectInput extends AbstractInput {

  @Override
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Processing a Selection List Input for "+field.getVariable());
    String varName = field.getVariable();
    Map<String,String> props = field.getProperties();
    
    ch.startElement("field");
    ch.addElement("type", "selection");
    ch.addElement("text", getTitle(userInfo, process, field));
    ch.addElement("variable", varName);
    ch.addElement("onchange_submit", props.get("onchange"));
    
    // process values
    genOptions(userInfo, process, field, ch, -1);

    // TODO apply formats
    ch.addElement("even_field", String.valueOf(even));
    // ch.addElement("disabled", "true");
    ch.addElement("readonly", props.get("readonly"));

    ch.endElement("field");

  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    if(isReadOnly(field)) return;
    String nValue = request.getParameter(field.getVariable());
    try {
      process.parseAndSet(field.getVariable(), nValue);
    } catch (ParseException e) {
      Logger.warning(userInfo.getUtilizador(), this, "process", "Error parsing variable "+field.getVariable());
    }
  }

  void genOptions(UserInfoInterface userInfo, ProcessData process, Field field, ParserContext ch, int row) throws SAXException {
    String [] optValues = {};
    String [] optDescrs = {};
    Map<String,String> props = field.getProperties();
    String varName = field.getVariable();
    String value = null;
    if(row < 0) 
      value = process.getFormatted(varName);
    else
      value = process.getListItemFormatted(varName, row);
    
    
    ch.addElement("value", value);
    String source = props.get("source");
    if(StringUtils.equals(source, "txt")) { // separated values
      String separator = props.get("separator");
      if(StringUtils.isEmpty(separator)) separator = ",";
      String selValues = props.get("selValues");
      if(null == selValues) selValues = "";
      String selDesc = props.get("selDesc");
      if(null == selDesc) selDesc = "";
      
      optValues = selValues.split(separator);
      optDescrs = selDesc.split(separator);
      
    } else if(StringUtils.equals(source, "cat")) { // from catalog
      String selValuesVar = props.get("selValuesVar");
      String selDescVar = props.get("selDescVar");
      if(null == selValuesVar) selValuesVar="";
      if(null == selDescVar) selDescVar="";
      
      ProcessListVariable varValues = process.getList(selValuesVar);
      ProcessListVariable varDescrs = process.getList(selDescVar);
      
      if(null != varValues) {
        optValues = new String[varValues.size()];
        for(int i = 0; i < optValues.length; i++)
          optValues[i] = varValues.getFormattedItem(i);
      }
      
      if(null != varDescrs) {
        optDescrs = new String[varDescrs.size()];
        for(int i = 0; i < optDescrs.length; i++)
          optDescrs[i] = varDescrs.getFormattedItem(i);
      }
      
      
    } else if(StringUtils.equals(source, "sn")) { // Yes/No
      String selYes = props.get("selYes");
      String selNo = props.get("selNo");
      if(selYes == null) selYes = "true";
      if(selNo == null) selNo = "false";
      String selInvert = props.get("selInvert");
      if(StringUtils.equals("true", selInvert)) {
        optValues = new String[]{selNo,selYes};
        optDescrs = new String[]{"Não", "Sim"}; // TODO localize this
      } else {
        optValues = new String[]{selYes,selNo};
        optDescrs = new String[]{"Sim", "Não"}; // TODO localize this
      }
      
    } else {
      Logger.warning(userInfo.getUtilizador(), this, "generate", "Processing a Selection List source not implemented: "+source);
    }

    for(int i = 0; optValues != null && optDescrs != null && i < optValues.length && i < optDescrs.length; i++) {
      String optText = optDescrs[i];
      String optValue = optValues[i];
      
      // TODO (value info)
      // optText += " ("+optValue+")";
      
      ch.startElement("hidden");
      ch.addElement("name",varName+"_"+optValue);
      ch.addElement("value",optText);
      ch.endElement("hidden");

      ch.startElement("option");
      ch.addElement("text", optText);
      ch.addElement("value", optValue);
      ch.addElement("selected", StringUtils.equals(optValue, value)?"yes":"no"); // "yes" ou "true"??
      ch.endElement("option");
    }
  }
  
  @Override
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, int row) throws SAXException {
    String varName = field.getVariable();
    Map<String,String> props = field.getProperties();
    
    ch.startElement("input");
    ch.addElement("type", "tableselection");
    ch.addElement("variable", varName+"["+row+"]");
    ch.addElement("onchange", props.get("onchange"));
    
    // process values
    genOptions(userInfo, process, field, ch, row);
    
    // ch.addElement("disabled", "true");
    ch.addElement("readonly", props.get("readonly"));

    ch.endElement("input");

  }

  @Override
  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request, int row) {
    if(isReadOnly(field)) return;
    String nValue = request.getParameter(field.getVariable()+"["+row+"]");
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
