package pt.iflow.api.datatypes;

import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormService;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessVariableValue;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BI implements DataTypeInterface {

  public BI() {
  }

  public String getDescription() {
    return Messages.getString("BI.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("BI.short_description"); //$NON-NLS-1$
  }

  public int getID() {
    return 7;
  }

  public String getPrimitiveType() {
    return getPrimitiveTypeMethod();
  }

  public String getPrimitiveTypeMethod() {
    return "String"; //$NON-NLS-1$
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

  public String validateFormData (Object s) {
    return validateFormData(s,null);
  }
  public String validateFormData (Object s, Object[] aoaArgs) {
    int sum = 0;

    String _s = (String)s;

    for (int i = 0; i < 8; i++) {
      sum += (_s.charAt(i) - '0') * (9 - i);
    }
    sum %= 11;

    if ((sum == 10 && _s.charAt(11) != '0') ||
	sum != java.lang.Integer.parseInt(_s.substring(9))) {
      return Messages.getString("BI.validation_error"); //$NON-NLS-1$
    }
    else {
      return null;
    }
  }

  public String formatToHtml (Object input) {
    return formatToHtml(input,null);
  }
  public String formatToHtml (Object input, Object[] aoaArgs) {
    return (String)input;
  }

  public String formatToForm (Object input) {
    return formatToForm(input,null);
  }
  public String formatToForm (Object input, Object[] aoaArgs) {
    return ""; //$NON-NLS-1$
  }

  public String getText(Object input) {
    return (String)input;
  }

  public double getValue(Object input) {
    return value(input);
  }

  public double value (Object input) {
    return 0;
  }
  public void setLocale(Locale locale) {
  }

  public String format(UserInfoInterface userInfo, ProcessData procData, FormService service, 
      int fieldNumber, boolean isOutput, String name,
      ProcessVariableValue value, Properties props, ServletUtils response) {
    return formatRow(userInfo, procData, service, fieldNumber, isOutput, -1, name, -1, value, props, response);
  }

  public String formatRow(UserInfoInterface userInfo, ProcessData procData, FormService service, 
      int fieldNumber, boolean isOutput, int varIndex, String name,
      int row, ProcessVariableValue value, Properties props, ServletUtils response) {
    return value != null ? value.format() : ""; 
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
  }

  public String parseAndSet(UserInfoInterface userInfo, 
      ProcessData procData, String name, FormData formData, Properties props,
      boolean ignoreValidation, StringBuilder logBuffer) {
    
    String value = parseValue(name, formData);
    
    if (!ignoreValidation && 
        FormUtils.checkRequiredField(userInfo, procData, props) &&
        value == null) {
      return userInfo.getMessages().getString("Datatype.required_field");
    }

    try {
      procData.parseAndSet(name, value);
    } catch (ParseException e) {
      procData.set(name, value);
    }
    logBuffer.append("Set '" + name + "' with '" + value + "';");
    debug(userInfo, "parseAndSet", "Set '" + name + "' with '" + value + "'");
    return null;
  }

  public String parseAndSetList(UserInfoInterface userInfo, 
      ProcessData procData, int varIndex, String name, int count,
      FormData formData, Properties props,
      boolean ignoreValidation, StringBuilder logBuffer) {
    
    ProcessListVariable list = procData.getList(name);
    for (int i = 0; i < count; i++) {
      if (FormUtils.checkOutputField(props, varIndex, i)) {
        debug(userInfo, "parseAndSetList", "Row " + i + " of list var '" + name + " is output only... continuing to next row.");
        continue;
      }
      String value = parseValue(FormUtils.getListKey(name, i), formData);
      try {
        list.parseAndSetItemValue(i, value);
      } catch (ParseException e) {
        list.setItemValue(i, value);
      }
      logBuffer.append("Set list var '" + name + "[" + i + "]' with '" + value + "';");
      debug(userInfo, "parseAndSetList", "Set list var '" + name + "[" + i + "]' with '" + value + "'");
    }
    if (!ignoreValidation && 
        FormUtils.checkRequiredField(userInfo, procData, props) &&
        list.size() == 0) {
      return userInfo.getMessages().getString("Datatype.required_field");
    }

    return null;
  }

  private String parseValue(String name, FormData formData) {
    String value = null;
    
    if (formData.hasParameter(name)) {      
      String formValue = formData.getParameter(name);
      if (StringUtils.isNotEmpty(formValue)) {
        value = formValue;
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
}
