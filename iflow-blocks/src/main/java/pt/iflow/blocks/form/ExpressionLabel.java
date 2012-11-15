package pt.iflow.blocks.form;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class ExpressionLabel extends TextLabel {

  public String getDescription() {
    return "Expressão Saída de Texto";
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    String userid = userInfo.getUtilizador();
    String value = null;    
    try {
      String expr = props.getProperty(FormProps.EXPRESSION);
      value = procData.transform(userInfo, expr);      
      if (Logger.isDebugEnabled()) {
        Logger.debug(userid, this, "setup", 
            procData.getSignature() + "expression: '" + expr + "' evaluated to '" + value + "'");        
      }
    } 
    catch (EvalException e) {
      Logger.warning(userid, this, "setup", 
          procData.getSignature() + "error processing expression", e);
    }
    
    if (StringUtils.isEmpty(value))
      value = "";
    
    try {
      DataTypeInterface dti = getDataType(userInfo, props);
      if (dti != null) {
        value = dti.formatToHtml(value);
        
        if (Logger.isDebugEnabled()) {
          Logger.debug(userid, this, "setup", 
              procData.getSignature() + "value formatted to: '" + value + "'");        
        }        
      }
    }
    catch (Exception e) {
      Logger.warning(userid, this, "setup", 
          procData.getSignature() + "exception trying to format value with datatype", e);
    }
    
    
    props.setProperty("value", value);
    
    super.setup(userInfo, procData, props, response);
  }
  
  private static DataTypeInterface getDataType(UserInfoInterface userInfo, Properties props) throws Exception {
    DataTypeInterface dti = null;
    
    String datatypeName = props.getProperty(FormProps.sDATATYPE);
    
    if (StringUtils.isNotEmpty(datatypeName)) {
      Class<? extends DataTypeInterface> cClass = 
        (Class<? extends DataTypeInterface>)Class.forName(datatypeName);

      dti = cClass.newInstance();
      dti.setLocale(userInfo.getUserSettings().getLocale());
    }
    return dti;
  }
}
