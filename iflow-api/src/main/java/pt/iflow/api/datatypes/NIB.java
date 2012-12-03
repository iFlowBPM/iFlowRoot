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

public class NIB implements DataTypeInterface {

  public NIB() {
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
  }

  public int getID() {
    return 7;
  }

  public String getDescription() {
    return Messages.getString("NIB.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("NIB.short_description"); //$NON-NLS-1$
  }

  public String getPrimitiveType() {
    return getPrimitiveTypeMethod();
  }

  public String getPrimitiveTypeMethod() {
    return "String";   // string //$NON-NLS-1$
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

  public String validateFormData (Object nib) {
    return validateFormData(nib,null);
  }

  public String validateFormData (Object nib, Object[] aoaArgs) {

    String _nib = (String)nib;

    int[] weights = {
      73,
      17,
      89,
      38,
      62,
      45,
      53,
      15,
      50,
      5,
      49,
      34,
      81,
      76,
      27,
      90,
      9,
      30,
      3};

    int mod = 97;

    int total = 0;
    for (int i = 0; i < 19; i++){
      total = total + (_nib.charAt(i) - 48) * weights[i];
    }

    int cd = 98 - (total % mod);

    if (java.lang.Integer.parseInt(_nib.substring(19,21)) != cd){
      Logger.debug("", this, "", "NIB: " + _nib.substring(19,21) + " != " + cd); //$NON-NLS-1$ //$NON-NLS-2$
      return Messages.getString("NIB.validation_error"); //$NON-NLS-1$
    }
    else{
      return null;
    }
  }

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
    return (value != null ? value.format() : ""); 
  }

  public String formatToHtml (Object input) {
    return formatToHtml(input,null);
  }

  public String formatToHtml (Object input, Object[] aoaArgs) {
    return (String)input;
  }

  public String formatToForm(Object input) {
    return formatToForm(input,null);
  }

  public String formatToForm(Object input, Object[] aoaArgs) {
    return (String)input;
  }

  public String getText(Object input) {
    return value(input);
  }

  public double getValue(Object input) {
    return java.lang.Double.NaN;
  }

  public String value (Object input) {
    return (String)input;
  }
  public void setLocale(Locale locale) {
  }

  public String parseAndSet(UserInfoInterface userInfo, 
      ProcessData procData, String name, FormData formData, Properties props, boolean ignoreValidation, StringBuilder logBuffer) {

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
      FormData formData, Properties props, boolean ignoreValidation, StringBuilder logBuffer) {

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
