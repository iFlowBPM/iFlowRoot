package pt.iflow.api.datatypes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.FormService;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessVariableValue;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class Date implements DataTypeInterface {

  @SuppressWarnings("unused")
  private Locale locale = Locale.getDefault();
  private boolean hideSufix = false;

  public Date() {
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String,Object> deps) {
    hideSufix = DataTypeUtils.hideSufix(extraProps);
  }

  public int getID() {
    return 6;
  }

  public String getDescription() {
    return Messages.getString("Date.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("Date.short_description"); //$NON-NLS-1$
  }

  public String getPrimitiveType() {
    return getPrimitiveTypeMethod();
  }

  public String getPrimitiveTypeMethod() {
    return "String"; //$NON-NLS-1$
  }

  public int getFormSize() {
    return 50;
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
    String retObj = ""; //$NON-NLS-1$

    if (!hideSufix && aoaArgs != null && aoaArgs.length > 0 && aoaArgs[0] != null) {
      retObj = " (" + aoaArgs[0] + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    return retObj;
  }

  public String validateFormData(Object input) {
    return validateFormData(input, null);
  }

  public String validateFormData(Object input, Object[] aoaArgs) {
    if (aoaArgs == null || aoaArgs.length == 0) {
      return null;
    }

    String _input = (String) input;

    if (_input != null && !_input.equals("")) { //$NON-NLS-1$
      java.util.Date dt = Date.value(input, aoaArgs);

      if (dt == null) {
        return Messages.getString("Date.validation_error"); //$NON-NLS-1$
      }
    }

    return null;
  }

  public String format(UserInfoInterface userInfo, 
      ProcessData procData,
      FormService service,
      int fieldNumber, 
      String name,
      ProcessVariableValue value, 
      Properties props,
      ServletUtils response) {
    return formatRow(userInfo, procData, service, fieldNumber, -1, name, -1, value, props, response);
  }
  
  public String formatRow(UserInfoInterface userInfo, 
      ProcessData procData,
      FormService service,
      int fieldNumber,
      int varIndex,
      String name,
      int row,
      ProcessVariableValue value, 
      Properties props,
      ServletUtils response) {

    if (value == null)
      return "";

    
    String dateFormatProp = getDateFormatProp(userInfo, procData, props);
    DateFormat dateFormat = getDateFormat(userInfo, dateFormatProp);
    
    String propPrefix = varIndex < 0 || row < 0 ? "" : varIndex + "_" + row + "_";
    props.setProperty(propPrefix + "prefix", getFormPrefix());
    props.setProperty(propPrefix + "suffix", getFormSuffix());

    String ret = "";
    
    Object theValue = value.getValue();
    if (dateFormat != null && theValue instanceof java.util.Date) {
      ret = dateFormat.format((java.util.Date)value.getValue());
    }
    else if (theValue instanceof java.lang.String) {
      ret = (java.lang.String)theValue;
    }
    else {
      ret = value.format(dateFormat);
    }
    
    if (ret == null)
      ret = "";
    
    return ret;
  }
  
  
  public String formatToHtml(Object input) {
    return formatToHtml(input, null);
  }

  public String formatToHtml(Object input, Object[] aoaArgs) {
    String retObj = ""; //$NON-NLS-1$

    if (input != null) {
      retObj = (String) input;
    }

    return retObj;
  }

  public String formatToForm(Object input) {
    return formatToForm(input, null);
  }

  public String formatToForm(Object input, Object[] aoaArgs) {
    
    if (input instanceof Date)
      return formatToFormDate((Date)input, aoaArgs);
    
    String retObj = ""; //$NON-NLS-1$

    if (input != null) {
      retObj = (String) input;
    }

    return retObj;
  }

  private String formatToFormDate(Date input, Object[] aoaArgs) {
    String retObj = ""; //$NON-NLS-1$

    if (input != null) {
      retObj = ((DateFormat)aoaArgs[0]).format(input);
    }

    return retObj;
  }
  
  public String getText(Object input) {
    return (String) input;
  }

  public double getValue(Object input) {
    return java.lang.Double.NaN;
  }

  public static java.util.Date value(Object s, Object[] aoaArgs) {
    java.util.Date retObj = null;

    try {
      DateFormat formatter = null;
      if (aoaArgs != null && aoaArgs.length > 0) {
        Object arg = aoaArgs[0];
        if (arg instanceof String)
          formatter = new SimpleDateFormat((String) arg);
        else if (arg instanceof DateFormat)
          formatter = (DateFormat) arg;
      }

      if (null == formatter) {
        // default locale format
        formatter = DateFormat.getInstance();
      }

      formatter.setLenient(false);

      retObj = formatter.parse((String) s);
    } catch (Exception e) {
      retObj = null;
    }

    return retObj;
  }

  public void setLocale(Locale locale) {
    if(null == locale) locale = Locale.getDefault();
    this.locale = locale;
  }

  public String parseAndSet(UserInfoInterface userInfo, 
      ProcessData procData, String name, FormData formData, Properties props, boolean ignoreValidation, StringBuilder logBuffer) {
    java.util.Date value = null;
    try {
      DateFormat df = getDateFormat(userInfo, procData, props);

      value = parseValue(userInfo, procData, name, formData, props, df);

      if (!ignoreValidation && 
          FormUtils.checkRequiredField(userInfo, procData, props) &&
          value == null) {
        return userInfo.getMessages().getString("Datatype.required_field");
      }

      DataTypeUtils.setObject(userInfo, procData, name, value, df);
      logBuffer.append("Set '" + name + "' with '" + procData.getFormatted(name) + "';");
      debug(userInfo, "parseAndSet", "Set '" + name + "' with '" + procData.getFormatted(name) + "'");
    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "parseAndSet", 
          procData.getSignature() + "error parsing " + name, e);
      return Messages.getString("Datatype.invalid_value");
    }
    return null;    
  }

  public String parseAndSetList(UserInfoInterface userInfo, 
      ProcessData procData, int varIndex, String name, int count,
      FormData formData, Properties props, boolean ignoreValidation, StringBuilder logBuffer) {

    DateFormat df = getDateFormat(userInfo, procData, props);
    
    String user = userInfo.getUtilizador();
    ProcessListVariable list = procData.getList(name);
    for (int i = 0; i < count; i++) {

      try {
        if (FormUtils.checkOutputField(props, varIndex, i)) {
          debug(userInfo, "parseAndSetList", "Row " + i + " of list var '" + name + " is output only... continuing to next row.");
          continue;
        }
        java.util.Date value = parseValue(userInfo, procData, FormUtils.getListKey(name, i), formData, props, df);
        DataTypeUtils.setListObject(userInfo, procData, name, i, value, df);
        logBuffer.append("Set list var '" + name + "[" + i + "]' with '" + value + "';");
        debug(userInfo, "parseAndSetList", "Set list var '" + name + "[" + i + "]' with '" + value + "'");
      }
      catch (Exception e) {
        Logger.error(user, this, "parseAndSetList", 
            procData.getSignature() + "error parsing list " + name + " item " + i, e);
        return Messages.getString("Datatype.invalid_value");
      }
    }
    if (!ignoreValidation && 
        FormUtils.checkRequiredField(userInfo, procData, props) &&
        list.size() == 0) {
      return userInfo.getMessages().getString("Datatype.required_field");
    }
    return null;
  }

  private java.util.Date parseValue(UserInfoInterface userInfo, ProcessData procData, String name, FormData formData, Properties props, DateFormat dateFormat) throws ParseException {
    java.util.Date value = null;

    if (formData.hasParameter(name)) {   
      String formValue = formData.getParameter(name);
      if (StringUtils.isNotEmpty(formValue)) {
        value = dateFormat.parse(formValue);
      }
    }
    return value;
  }

  public void formPreProcess(UserInfoInterface userInfo, ProcessData procData, String name, Properties props, StringBuilder logBuffer) {
  }

  private void debug(UserInfoInterface userInfo, String method, String message) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, method, message);
    }
  }

  private String getDateFormatProp(UserInfoInterface userInfo, ProcessData procData, Properties props) {
    String dateFormatProp = props.getProperty("date_format");
    if (StringUtils.isEmpty(dateFormatProp)) {
      try {
        String extraProps = props.getProperty("0_extra_props");
        if (!StringUtils.isEmpty(extraProps)) {
          String[] extraPropsArray = extraProps.split(",");
          for (int i = 0; i < extraPropsArray.length; i++)
            if (extraPropsArray[i].startsWith("date_format="))
              dateFormatProp = extraPropsArray[i].split("=")[1];
        }
      } catch (Exception badExtraPropFormat) {
        dateFormatProp = "";
      }
    }
    if (StringUtils.isEmpty(dateFormatProp)) {
      dateFormatProp = procData.getFormatted(Const.sFLOW_DATE_FORMAT);
      if (StringUtils.isEmpty(dateFormatProp)) {
        dateFormatProp = Const.sDEF_DATE_FORMAT;
      }
    }
    String hourFormatProp = props.getProperty(FormProps.HOUR_FORMAT);
    if (StringUtils.isNotBlank(hourFormatProp)) {
    	hourFormatProp = hourFormatProp.split("\\[")[0].trim(); // remove description labels, found after '['
    	if (StringUtils.contains(hourFormatProp, ":")) {
    		dateFormatProp += " " + hourFormatProp;
    	}
    }
    return dateFormatProp;
  }

  private DateFormat getDateFormat(UserInfoInterface userInfo, ProcessData procData, Properties props) {
    String dateFormatProp = getDateFormatProp(userInfo, procData, props);
    return getDateFormat(userInfo, dateFormatProp);
  }
  
  private DateFormat getDateFormat(UserInfoInterface userInfo, String format) {
    DateFormat dateFormat = null;
    if (StringUtils.isNotEmpty(format)) {      
      dateFormat = DateUtility.getBlockDateFormat(userInfo.getOrganization(), format);
    }
    if (dateFormat == null) {
      dateFormat = DateFormat.getDateInstance();
    }
    
    dateFormat.setLenient(false);
    
    return dateFormat;
  }

}
