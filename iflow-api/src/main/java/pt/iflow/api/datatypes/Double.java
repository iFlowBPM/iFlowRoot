package pt.iflow.api.datatypes;

import java.util.Map;

import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Double extends NumericDataType implements DataTypeInterface {

  public Double() {
    this.setLocale(null);
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
    this.fmt.setMaximumFractionDigits(Const.MAX_DOUBLE_DECIMAL_DIGITS);
  }

  public String getDescription() {
    return Messages.getString("Double.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("Double.short_description"); //$NON-NLS-1$
  }

  public int getID() {
    return 2;
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
    return ""; //$NON-NLS-1$
  }

  public int getFormSize() {
    return 10;
  }

  protected String validateErrorMsg() {
    return "Double.validation_error"; //$NON-NLS-1$
  }

}
