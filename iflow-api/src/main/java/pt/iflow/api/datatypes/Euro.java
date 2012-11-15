package pt.iflow.api.datatypes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

public class Euro extends NumericDataType implements DataTypeInterface {

  private boolean hideSufix = false;

  public Euro() {
    this.setLocale(null);
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
    hideSufix = DataTypeUtils.hideSufix(extraProps);
  }

  public String getDescription() {
    return Messages.getString("Euro.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("Euro.short_description"); //$NON-NLS-1$
  }

  public int getID() {
    return 5;
  }

  public int getFormSize() {
    return 14;
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
    return hideSufix ? "" : " €"; //$NON-NLS-1$
  }

  protected String validateErrorMsg() {
    return "Euro.validation_error"; //$NON-NLS-1$
  }

  public String formatToHtml (Object input, Object[] aoaArgs) {
    Object myinput = input;
    if (input != null && input instanceof String) {
      try {
        myinput = java.lang.Double.parseDouble((String)input);
      }
      catch (Exception e) {}
    }
    return formatNumber(myinput, aoaArgs)+getFormSuffix();
  }

  
  public void setLocale(Locale locale) {
    if(null == locale) locale = Locale.getDefault();
    this.locale = locale;
    this.fmt = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(this.locale));
  }

}
