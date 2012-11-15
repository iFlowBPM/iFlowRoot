package pt.iflow.blocks.form;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;


public class SQLMessage extends TextMessage {

  public String getDescription() {
    return "Mensagem SQL";
  }
  
  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {

    String value = SQLFieldHelper.getSQLData(userInfo, procData, props);
    
    if (StringUtils.isNotEmpty(value)) {
      value = FormUtils.escapeAmp(value);
    }
    
    props.setProperty("text", value);

    String stmp = props.getProperty("alignment");
    if (stmp.equals("Escolha")) 
    	props.setProperty("align", "left");
    
    stmp = props.getProperty("css_class");
    if (stmp.equals("")) 
    	stmp = "txt sqllabel";
    props.setProperty("css_class", stmp);
    
    super.setup(userInfo, procData, props, response);
  }
}
