package pt.iflow.blocks.form;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;


public class SQLLabel extends TextLabel {

  public String getDescription() {
    return "Saída de Texto SQL";
  }
  
  
  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    
    String value = SQLFieldHelper.getSQLData(userInfo, procData, props);
    
    if (StringUtils.isNotEmpty(value)) {
      value = FormUtils.escapeAmp(value);
    }

    props.setProperty("value", value);
    
    super.setup(userInfo, procData, props, response);
  }
}
