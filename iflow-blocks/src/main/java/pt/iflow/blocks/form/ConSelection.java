package pt.iflow.blocks.form;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.connectors.ConnectorInterface;
import pt.iflow.api.connectors.ConnectorUtils;
import pt.iflow.api.connectors.handlers.SelectItem;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


public class ConSelection extends Selection {

  public String getDescription() {
    return "Lista de Selecção Conector Externo";
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    
    String login = userInfo.getUtilizador();
    
    // default value
//    String value = props.getProperty(FormProps.sDEFAULT_VALUE);
//    String text = props.getProperty(FormProps.sDEFAULT_TEXT);
//
//    value = value != null ? value : "";
//    text = text != null ? text : "";
//    
    int itemIndex = 0;
//    
//    if (StringUtils.isNotEmpty(value) ||
//        StringUtils.isNotEmpty(text)) {
//
//      props.setProperty(itemIndex + "_value", value);
//      props.setProperty(itemIndex + "_text", text);
//      itemIndex++;
//    }

    String[] transfTokens = null;
    String args = props.getProperty(FormProps.sARGS);
    if (StringUtils.isNotEmpty(args)) {
      List<String> tokens = Utils.tokenize(args, ConnectorUtils.SEPARATOR);

      if (tokens != null && tokens.size() > 0) {

        transfTokens = new String[tokens.size()];

        for (int i=0; i < tokens.size(); i++) {
          String token = tokens.get(i);

          if (StringUtils.isEmpty(token)) continue;

          try {
            transfTokens[i] = procData.transform(userInfo, token);
          } catch (EvalException e) {
            Logger.warning(login, this, "setup", 
                procData.getSignature() + "exception transforming token " + token, e);
          }
        }
      }
    }
    try {
      String conClassName = props.getProperty(FormProps.sCONNECTOR);
      if (StringUtils.isNotBlank(conClassName) && !conClassName.contains(".")) {
        // no package defined, use default
        String pkg = ConnectorUtils.CONNECTOR_PKG;
        if (StringUtils.isNotBlank(pkg) && !pkg.endsWith(".")) {
          pkg = pkg + ".";
        }
        conClassName = (pkg + conClassName);
      }
      Class<? extends ConnectorInterface> conClass = ConnectorUtils.fetchConnector(userInfo, conClassName);
      ConnectorInterface conInstance = conClass.newInstance();

      List<SelectItem> selectionList = conInstance.dispatch(userInfo, procData, (Object[]) transfTokens);

      for (SelectItem item : selectionList) {
        props.setProperty(itemIndex + "_value", item.getId());
        props.setProperty(itemIndex + "_text", item.getDescription());
        itemIndex++;
      }
    }
    catch (Exception e) {
      Logger.error(login, this, "setup", procData.getSignature() + "caught exception", e);
    }

    super.setup(userInfo, procData, props, response);
  }

  public boolean isArrayTable() {
    return false;
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
