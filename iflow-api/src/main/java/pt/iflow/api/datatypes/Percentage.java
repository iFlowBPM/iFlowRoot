package pt.iflow.api.datatypes;

import java.util.Map;

import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Percentage extends NumericDataType implements DataTypeInterface {

  public Percentage() {
    this.setLocale(null);
  }
  
  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
  }

  public String getDescription() {
    return Messages.getString("Percentage.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("Percentage.short_description"); //$NON-NLS-1$
  }

  public int getID() {
    return 3;
  }

  public String getFormPrefix() {
    return getFormPrefix(null);
  }
  public String getFormPrefix(Object[] aoaArgs) {
    return ""; //$NON-NLS-1$
  }

  public String getFormSuffix() {
    return getFormSuffix(null);
  }
  public String getFormSuffix(Object[] aoaArgs) {
    return "%"; //$NON-NLS-1$
  }

  protected String validateErrorMsg() {
    return "Percentage.validation_error";
  }
  
  public String formatToHtml (Object input, Object[] aoaArgs) {
    return formatNumber(input, aoaArgs)+getFormSuffix();
  }

}
