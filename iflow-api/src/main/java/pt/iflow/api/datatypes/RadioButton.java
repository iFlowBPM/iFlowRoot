package pt.iflow.api.datatypes;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
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
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class RadioButton implements DataTypeInterface, ArrayTableProcessingCapable {

  private String checkedValue = null;
  private String textVar = null;
  private Map<String,String> hiddenFields = null;
  private Map<String,List<ProcessVariableValue>> valueMap = null;
  
  public RadioButton() {
  }

  @SuppressWarnings("unchecked")
  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String,Object> deps) {
    if (extraProps != null) {
      Iterator<String> iterSV = extraProps.keySet().iterator();
      while (iterSV != null && iterSV.hasNext()) {

        String sKey = iterSV.next();
        String sValue = extraProps.get(sKey);

        if (StringUtils.isEmpty(sValue)) continue;

        if (sKey.startsWith(FormProps.sTEXT_VAR)) {
          textVar = sValue;
        }
        else if (sKey.startsWith(FormProps.sCHECKED_VALUE)) {
          checkedValue = sValue;
        }

        if (StringUtils.isNotEmpty(checkedValue) && StringUtils.isNotEmpty(textVar)) {
          // done
          break;
        }
      }
    }
    if (checkedValue == null) checkedValue = FormProps.sDEF_CHECKED_VALUE;

    if (deps.containsKey(FormProps.HIDDEN_FIELDS)) {
      hiddenFields = (Map<String,String>)deps.get(FormProps.HIDDEN_FIELDS);
    }
    if (deps.containsKey(FormProps.VALUE_MAP)) {
      valueMap = (Map<String,List<ProcessVariableValue>>)deps.get(FormProps.VALUE_MAP);
    }
  }

  public int getID() {
    return 9;
  }

  public String getDescription() {
    return Messages.getString("RadioButton.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("RadioButton.short_description"); //$NON-NLS-1$
  }

  public String getPrimitiveType() {
    return getPrimitiveTypeMethod();
  }

  public String getPrimitiveTypeMethod() {
    return "String"; //$NON-NLS-1$
  }

  public int getFormSize() {
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

  public String validateFormData (Object input) {
    return validateFormData(input,null);
  }

  public String validateFormData (Object input, Object[] aoaArgs) {
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

    boolean outputOnly = FormUtils.checkOutputField(props, varIndex, row);
    String propPrefix = varIndex < 0 || row < 0 ? "" : varIndex + "_" + row + "_";
    props.setProperty(propPrefix + "prefix", getFormPrefix());
    props.setProperty(propPrefix + "suffix", getFormSuffix());

    if (textVar != null && valueMap != null && hiddenFields != null) {
      List<ProcessVariableValue> values = valueMap.get(textVar);
      if (values != null) {
        ProcessVariableValue textValue = values.get(row); 
        hiddenFields.put(name + FormProps.sTEXT_VAR_SEP + row, textValue != null ? textValue.format() : "");
      }
    }

    // radiobutton value: does not depend on row
    // only value depends on row, not name.. name remains the same
    // and is not stored in a list (is handled like a single property)
    ProcessVariableValue myValue = procData.get(name);
    
    if (service == FormService.NONE) {
      return genRadioButtonXml(name,
          String.valueOf(row),
          myValue != null ? myValue.format() : "",
          outputOnly);
    }
    return genRadioButtonText(name, myValue != null ? myValue.format() : "", checkedValue);

  }

  
  public String formatToHtml (Object input) {
    return formatToHtml(input,null);
  }

  public String formatToHtml (Object input, Object[] aoaArgs) {

    // aoaArgs[0] = output mode (text or html)
    // aoaArgs[1] = input name 
    // aoaArgs[2] = input button value
    // aoaArgs[3] = checked value for text mode
    if (aoaArgs == null || aoaArgs.length < 4) {
      return ""; //$NON-NLS-1$
    }

    String stmp = (String)aoaArgs[0];
    if (stmp != null && stmp.equals("text")) { //$NON-NLS-1$
      return genRadioButtonText((String)aoaArgs[2],
				RadioButton.value(input),
				(String)aoaArgs[3]);
    }
    
    return genRadioButtonXml((String)aoaArgs[1],
			     (String)aoaArgs[2],
			     RadioButton.value(input),
			     true);
  }

  public String formatToForm (Object input) {
    return formatToForm(input,null);
  }

  public String formatToForm (Object input, Object[] aoaArgs) {

    // aoaArgs[0] = output mode (text or html)
    // aoaArgs[1] = input name 
    // aoaArgs[2] = input button value
    // aoaArgs[3] = checked value for text mode
    if (aoaArgs == null || aoaArgs.length < 4) {
      return ""; //$NON-NLS-1$
    }

    return genRadioButtonXml((String)aoaArgs[1],
			     (String)aoaArgs[2],
			     RadioButton.value(input),
			     false);
  }

  public String getText(Object input) {
    return value(input);
  }

  public double getValue(Object input) {
    return java.lang.Double.NaN;
  }

  public static String value (Object s) {
    return (String)s;
  }

  private static String genRadioButtonText(String asButtonValue,
					   String asValue,
					   String asCheckedValue) {
    String sCV = "1"; //$NON-NLS-1$

    String retObj = ""; //$NON-NLS-1$
    if (asValue != null && asValue.equals(asButtonValue)) {
      if (asCheckedValue != null && !asCheckedValue.equals("")) { //$NON-NLS-1$
        retObj = asCheckedValue;
      }
      else {
        retObj = sCV;
      }
    }

    if (retObj == null) retObj = ""; //$NON-NLS-1$
    return retObj;
  }

  private static String genRadioButtonXml(String asName,
					  String asButtonValue,
					  String asValue, 
					  boolean abDisabled) {

    String retObj = "<input><type>radio</type><name>"; //$NON-NLS-1$
    retObj += asName;
    retObj += "</name><value>"; //$NON-NLS-1$
    retObj += asButtonValue;
    retObj += "</value><checked>"; //$NON-NLS-1$
    if (StringUtils.equals(asValue, asButtonValue)) {
      retObj += "true"; //$NON-NLS-1$
    }
    else {
      retObj += "false"; //$NON-NLS-1$
    }
    retObj += "</checked><disabled>"; //$NON-NLS-1$
    if (abDisabled) {
      retObj += "true"; //$NON-NLS-1$
    }
    else {
      retObj += "false"; //$NON-NLS-1$
    }
    retObj += "</disabled></input>"; //$NON-NLS-1$

    return retObj;
  }

  public void setLocale(Locale locale) {
  }

  public void formPreProcess(UserInfoInterface userInfo, ProcessData procData, String name, Properties props, StringBuilder logBuffer) {
    // reset text var
    procData.set(name + FormProps.sLIST_TEXT_SUFFIX, null);
    logBuffer.append("Cleared '" + name + FormProps.sLIST_TEXT_SUFFIX + "';");
    debug(userInfo, "formPreProcess", "Cleared '" + name + FormProps.sLIST_TEXT_SUFFIX + "'");

    // reset checkedvalue vars
    procData.clearList(name + FormProps.sLIST_CHECKED_SUFFIX);
    logBuffer.append("Cleared list '" + name + FormProps.sLIST_CHECKED_SUFFIX + "';");
    debug(userInfo, "formPreProcess", "Cleared list '" + name + FormProps.sLIST_CHECKED_SUFFIX + "'");
  }

  public String parseAndSet(UserInfoInterface userInfo, ProcessData procData, String name, FormData formData, Properties props, boolean ignoreValidation, StringBuilder logBuffer) {
    return null;
  }

  public String parseAndSetList(UserInfoInterface userInfo, 
      ProcessData procData, int varIndex, String name, int count, FormData formData,
      Properties props, boolean ignoreValidation, StringBuilder logBuffer) {
    
    String user = userInfo.getUtilizador();

    String value = formData.getParameter(name); 
    if (StringUtils.isNotEmpty(value) && StringUtils.isNumeric(value)) {
      try {
        procData.parseAndSet(name, value);
        logBuffer.append("Set var '" + name + "' with '" + value + "';");
        debug(userInfo, "parseAndSetList", "Set var '" + name + "' with '" + value + "'");
      } catch (ParseException e) {
        Logger.error(user, this, "parseAndSetList", 
            procData.getSignature() + "error parsing " + name, e);
      }
    }

      
      
      // additional stuff
    if (StringUtils.isNotEmpty(value)) {

      int selIndex = -1;
      try {
        selIndex = java.lang.Integer.parseInt(value);
      } catch (Exception eii) {
        selIndex = -1;
      }
      
      
      // vartext
      String textVarValueVar = name + FormProps.sTEXT_VAR_SEP + value;
      String textVarValue = null;
      if (formData.hasParameter(textVarValueVar)) {
        textVarValue = formData.getParameter(textVarValueVar);
      }
      if (StringUtils.isEmpty(textVarValue) && StringUtils.isNotEmpty(textVar) && selIndex > -1) {      
        // try value still in form (not yet as hidden field)
        String procTextVar = FormUtils.getListKey(textVar, selIndex);
        if (formData.hasParameter(procTextVar)) {
          textVarValue = formData.getParameter(procTextVar);
        }
        else {
          // finally, try proc value
          String procTextVarValue = procData.getListItemFormatted(textVar, selIndex);
          if (StringUtils.isNotEmpty(procTextVarValue)) {
            textVarValue = procTextVarValue;
          }
        }
      }
      
      if (StringUtils.isNotEmpty(textVarValue)) {
        try {
          procData.parseAndSet(name + FormProps.sLIST_TEXT_SUFFIX, textVarValue);
          logBuffer.append("Set additional var '" + name + FormProps.sLIST_TEXT_SUFFIX + "' with '" + textVarValue + "';");
          Logger.debug(user, this, "parseAndSetList", 
              procData.getSignature() + "Set additional var: " + name + FormProps.sLIST_TEXT_SUFFIX + "=" + textVarValue);
        } 
        catch (ParseException e) {
          Logger.error(user, this, "parseAndSetList", 
              procData.getSignature() + 
              "error parsing vartext " + name + FormProps.sLIST_TEXT_SUFFIX, e);
        }            
      }

      // checked value
      if (selIndex > -1) {
        String varCheckedVal = name + FormProps.sLIST_CHECKED_SUFFIX;

        String myCheckedValue = checkedValue;
        if (StringUtils.equals(myCheckedValue, FormProps.sDEF_CHECKED_VALUE) &&
            StringUtils.isNotEmpty(textVar) &&
            selIndex > -1) {
          myCheckedValue = procData.getListItemFormatted(textVar, selIndex);
        }

        try {
          ProcessListVariable lvar = procData.getList(varCheckedVal);
          
          if (lvar != null) {
            lvar.parseAndSetItemValue(selIndex, myCheckedValue);
          }
          else {
            procData.parseAndSetListItem(varCheckedVal, value, selIndex);
          }
          
          logBuffer.append("Set additional var '" + varCheckedVal + "[" + selIndex + "]' as '" + myCheckedValue + "';");
          debug(userInfo, "parseAndSetList", "Set additional var '" + varCheckedVal + "[" + selIndex + "]' as '" + myCheckedValue + "'");
        }
        catch (ParseException e) {
          Logger.error(user, this, "parseAndSetList", 
              procData.getSignature() + 
              "error parsing checked value " + varCheckedVal + " item " + selIndex, e);
        }
      }
    }
    if (!ignoreValidation && 
        FormUtils.checkRequiredField(userInfo, procData, props) &&
        value == null) {
      // TODO improve check (additional vars check?)
      return userInfo.getMessages().getString("Datatype.required_field");
    }
    return null;    
    
  }

  private void debug(UserInfoInterface userInfo, String method, String message) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, method, message);
    }
  }
}
