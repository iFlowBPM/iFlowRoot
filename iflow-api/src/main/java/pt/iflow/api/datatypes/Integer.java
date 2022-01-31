package pt.iflow.api.datatypes;

import java.text.NumberFormat;
import java.util.Locale;
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

public class Integer extends NumericDataType implements DataTypeInterface {

  public Integer() {
    this.setLocale(null);
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
  }

  public String getDescription() {
    return Messages.getString("Integer.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("Integer.short_description"); //$NON-NLS-1$
  }

  public int getID() {
    return 1;
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

  protected String validateErrorMsg() {
    return "Integer.validation_error";
  }
  
  public void setLocale(Locale locale) {
    if(null == locale) locale = Locale.getDefault();
    this.locale = locale;
    this.fmt = NumberFormat.getIntegerInstance(this.locale);
    this.fmtInput = NumberFormat.getIntegerInstance(this.locale);
  }
}
