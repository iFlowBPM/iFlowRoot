/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.api.datatypes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
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

public abstract class NumericDataType implements DataTypeInterface {

  protected NumberFormat fmt;
  protected Locale locale;
 
  public String format(UserInfoInterface userInfo, ProcessData procData, FormService service, 
      int fieldNumber, String name,
      ProcessVariableValue value, Properties props, ServletUtils response) {
    return formatRow(userInfo, procData, service, fieldNumber, -1, name, -1, value, props, response);
  }

  public String formatRow(UserInfoInterface userInfo, ProcessData procData, FormService service, 
      int fieldNumber, int varIndex, String name,
      int row, ProcessVariableValue value, Properties props, ServletUtils response) {

    String propPrefix = varIndex < 0 || row < 0 ? "" : varIndex + "_" + row + "_";
    props.setProperty(propPrefix + "prefix", getFormPrefix());
    props.setProperty(propPrefix + "suffix", getFormSuffix());

    return formatNumber(value == null ? null : value.getValue(), null);
  }
  
  public String formatToForm(Object input) {
    return formatToForm(input, null);
  }

  public String formatToForm(Object input, Object[] aoaArgs) {
    return formatNumber(input, aoaArgs);
  }

  public String formatToHtml(Object input) {
    return formatToHtml(input, null);
  }

  public String formatToHtml (Object input, Object[] aoaArgs) {
    return formatNumber(input, aoaArgs);
  }

  protected String formatNumber (Object input, Object[] aoaArgs) {

    // Integer, Float, Double etc are all java.lang.Number instances
    if (input instanceof java.lang.Number) {
      return internalFormat((java.lang.Number)input);
    }
    else if (input instanceof java.lang.String) {
      // return value as is...
      String strValue = (java.lang.String)input; 
      return strValue != null ? strValue : "";
    }
    
    java.lang.Double d = new java.lang.Double(java.lang.Double.NaN);
    try {
      String var = (String)input;
      if (StringUtils.isNotEmpty(var)) {
        var = var.replace(",", "");
        d = new java.lang.Double(var);
      }
    }
    catch (Exception e) {
      Logger.error("", this, "formatNumber", "Unable to format number '" + input + "'.");
    }

    return internalFormat(d);
  }

  private String internalFormat(Number num) {
    String s = ""; //$NON-NLS-1$

    if (num != null && !java.lang.Double.isNaN(num.doubleValue())) {
//      if (num instanceof java.lang.Integer || num instanceof java.lang.Long) {
        s = fmt.format(num);
//      }
//      else {        
//        java.lang.Double d = num.doubleValue();
//        if (!d.isNaN()) {
//          if (java.lang.Double.compare(d.doubleValue(), 0) == 0) { //$NON-NLS-1$
//            s = "0"; //$NON-NLS-1$
//          }
//          else {
//            s = fmt.format(d);
//          }
//        }
//      }
    }
    return s;    
  }
  
  public String getPrimitiveType() {
    return getPrimitiveTypeMethod();
  }

  public String getPrimitiveTypeMethod() {
    return "Value";   // numeric //$NON-NLS-1$
  }

  public String getText(Object input) {
    return "";
  }

  public double getValue(Object input) {
    double d = java.lang.Double.NaN;
    try {
      d = fmt.parse((String)input).doubleValue();
    }
    catch (Exception nfe) {
      d = java.lang.Double.NaN;
    }
    return d;
  }

  public void setLocale(Locale locale) {
    if(null == locale) locale = Locale.getDefault();
    this.locale = locale;
    this.fmt = new DecimalFormat("#,##0.000", new DecimalFormatSymbols(this.locale));
  }

  
  public String parseAndSet(UserInfoInterface userInfo, 
      ProcessData procData, String name, FormData formData, Properties props, boolean ignoreValidation, StringBuilder logBuffer) {
    try {
      Number value = parseValue(name, formData);

      if (!ignoreValidation && 
          FormUtils.checkRequiredField(userInfo, procData, props) &&
          value == null) {
        return userInfo.getMessages().getString("Datatype.required_field");
      }

      DataTypeUtils.setObject(userInfo, procData, name, value, fmt);
      logBuffer.append("Set '" + name + "' with '" + procData.getFormatted(name) + "';");
      debug(userInfo, "parseAndSet", "Set '" + name + "' with '" + procData.getFormatted(name) + "'");
      return null;
    }
    catch (Exception e) {
      // TODO: return error message ??
//      return Messages.getString(validateErrorMsg()); //$NON-NLS-1$
      return userInfo.getMessages().getString("Datatype.invalid_value");
    }
  }

  public String parseAndSetList(UserInfoInterface userInfo, 
      ProcessData procData, int varIndex, String name, int count,
      FormData formData, Properties props, boolean ignoreValidation, StringBuilder logBuffer) {

    String user = userInfo.getUtilizador();
    ProcessListVariable list = procData.getList(name);
    for (int i = 0; i < count; i++) {
      try {
        if (FormUtils.checkOutputField(props, varIndex, i)) {
          debug(userInfo, "parseAndSetList", "Row " + i + " of list var '" + name + " is output only... continuing to next row.");
          continue;
        }
        Number value = parseValue(FormUtils.getListKey(name, i), formData);
        DataTypeUtils.setListObject(userInfo, procData, name, i, value, fmt);
        logBuffer.append("Set list var '" + name + "[" + i + "]' with '" + list.getFormattedItem(i) + "';");
        debug(userInfo, "parseAndSetList", "Set list var '" + name + "[" + i + "]' with '" + list.getFormattedItem(i) + "'");
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

  private Number parseValue(String name, FormData formData) throws ParseException {
    Number value = null;
    
    if (formData.hasParameter(name)) {      
      String formValue = formData.getParameter(name);
      if (StringUtils.isNotEmpty(formValue)) {
        value =  fmt.parse(formValue);
      }
    }
    
    return value;
  }

  
  
  public String validateFormData (Object input) {
    return validateFormData(input,null);
  }

  public String validateFormData (Object input, Object[] aoaArgs) {
    String error = null;
    try {
      fmt.parse((String)input);
    }
    catch (ParseException nfe) {
      // error = Messages.getString(validateErrorMsg()); //$NON-NLS-1$
    }
    return error;
  }

  protected abstract String validateErrorMsg();

  public void formPreProcess(UserInfoInterface userInfo, ProcessData procData, String name, Properties props, StringBuilder logBuffer) {
  }
  
  private void debug(UserInfoInterface userInfo, String method, String message) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, method, message);
    }
  }
}
