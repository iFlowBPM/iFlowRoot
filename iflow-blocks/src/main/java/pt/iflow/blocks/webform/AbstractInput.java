package pt.iflow.blocks.webform;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public abstract class AbstractInput extends AbstractWidget implements ITableColumn {
  
  public abstract void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException;
  
  public abstract void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request);
  
  public String getTitle(UserInfoInterface userInfo, ProcessData process, Field field) {
    Map<String,String> props = field.getProperties();
    String override = props.get("override");
    String overrideLabel = props.get("text");
    String label = process.getCatalogue().getPublicName(field.getVariable());
    if(StringUtils.equals(override, "true") && StringUtils.isNotEmpty(overrideLabel))
      label = overrideLabel;
    
    if(StringUtils.isEmpty(label)) {
      Logger.warning(userInfo.getUtilizador(), this, "getTitle", "Label is empty. Assuming var name...");
      label = field.getVariable();
    }
    
    return label;
  }
  
  public abstract void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, int row) throws SAXException;
  
  public abstract void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request, int row);

}
