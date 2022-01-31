package pt.iflow.api.datatypes;

import java.text.ParseException;
import java.util.Iterator;
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

public class CheckBox implements DataTypeInterface, ArrayTableProcessingCapable {

  private String checkedValue = null;
  private String textVar = null;
  private String textSep = null;
//  private Map<String,String> hiddenFields = null;
//  private Map<String,List<ProcessVariableValue>> valueMap = null;
  
  public CheckBox() {
  }

//  @SuppressWarnings("unchecked")
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
        else if (sKey.startsWith(FormProps.sTEXT_SEP)) {
          textSep = sValue;        
        }

        if (StringUtils.isNotEmpty(checkedValue) && 
            StringUtils.isNotEmpty(textVar) &&
            StringUtils.isNotEmpty(textSep)) {
          // done
          break;
        }
      }
      
    }
    if (checkedValue == null) checkedValue = FormProps.sDEF_CHECKED_VALUE;
    if (textSep == null) textSep = FormProps.sDEF_TEXT_SEP;
    
//    if (deps.containsKey(FormProps.HIDDEN_FIELDS)) {
//      hiddenFields = (Map<String,String>)deps.get(FormProps.HIDDEN_FIELDS);
//    }
//    if (deps.containsKey(FormProps.VALUE_MAP)) {
//      valueMap = (Map<String,List<ProcessVariableValue>>)deps.get(FormProps.VALUE_MAP);
//    }
  }

  public int getID() {
    return 8;
  }

  public String getDescription() {
    return Messages.getString("CheckBox.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("CheckBox.short_description"); //$NON-NLS-1$
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
	  // TODO verificar se esta tudo preenchido
    return null;
  }

  public String format(UserInfoInterface userInfo, 
      ProcessData procData,
      FormService service,
      int fieldNumber, 
      boolean isOutput, 
      String name,
      ProcessVariableValue value, 
      Properties props,
      ServletUtils response) {
    return formatRow(userInfo, procData, service, fieldNumber, isOutput, -1, name, -1, value, props, response);
  }
  
  public String formatRow(UserInfoInterface userInfo, 
      ProcessData procData,
      FormService service,
      int fieldNumber,
      boolean isOutput, 
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

//    if (textVar != null && valueMap != null && hiddenFields != null) {
//      List<ProcessVariableValue> values = valueMap.get(textVar);
//      if (values != null) {
//        ProcessVariableValue textValue = values.get(row); 
//        hiddenFields.put(name + FormProps.sTEXT_VAR_SEP + row, textValue != null ? textValue.format() : "");
//      }
//    }
    
    
    if (service == FormService.NONE) {
      String myname = row < 0 ? name : FormUtils.getListKey(name,row);

      return genCheckBoxXml(myname,value != null ? value.format() : null,checkedValue, outputOnly);

    }
    
    return genCheckBoxText(value != null ? value.format() : null, checkedValue);
    
  }
  
  public String formatToHtml (Object input) {
    return formatToHtml(input,null);
  }

  public String formatToHtml (Object input, Object[] aoaArgs) {

    // aoaArgs[0] = output mode (text or html)
    // aoaArgs[1] = input name 
    // aoaArgs[2] = checked value for text mode
    if (aoaArgs == null || aoaArgs.length < 3) {
      return ""; //$NON-NLS-1$
    }

    String stmp = (String)aoaArgs[0];
    if (stmp != null && stmp.equals("text")) { //$NON-NLS-1$
      return genCheckBoxText(CheckBox.value(input),
			     (String)aoaArgs[2]);
    }
    

    return genCheckBoxXml((String)aoaArgs[1],
			  CheckBox.value(input),
			  (String)aoaArgs[2],
			  true);
  }

  public String formatToForm (Object input) {
    return formatToForm(input,null);
  }

  public String formatToForm (Object input, Object[] aoaArgs) {

    // aoaArgs[0] = output mode (text or html)
    // aoaArgs[1] = input name 
    // aoaArgs[2] = checked value for text mode
    if (aoaArgs == null || aoaArgs.length < 3) {
      return ""; //$NON-NLS-1$
    }

    return genCheckBoxXml((String)aoaArgs[1],
			  CheckBox.value(input),
			  (String)aoaArgs[2],
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

  private static String genCheckBoxText(String asValue,
					String asCheckedValue) {
    String sCV = "1"; //$NON-NLS-1$

    String retObj = ""; //$NON-NLS-1$
    if (asValue != null && asValue.equals(sCV)) {
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

  private static String genCheckBoxXml(String asName, 
				       String asValue,
				       String asCheckedValue,
				       boolean abDisabled) {
    
    String sCV = "1"; //$NON-NLS-1$

    StringBuffer retObj = new StringBuffer("<input><type>checkbox</type><name>"); //$NON-NLS-1$
    retObj.append(asName);
    retObj.append("</name><value>"); //$NON-NLS-1$
    retObj.append(sCV);
    retObj.append("</value><checked>"); //$NON-NLS-1$

    if (null != asValue && (asValue.equals(sCV) || (asCheckedValue != null && asValue.equals(asCheckedValue)))) {
      retObj.append("true"); //$NON-NLS-1$
    }
    else {
      retObj.append("false"); //$NON-NLS-1$
    }
    retObj.append("</checked><disabled>"); //$NON-NLS-1$
    if (abDisabled) {
      retObj.append("true"); //$NON-NLS-1$
    }
    else {
      retObj.append("false"); //$NON-NLS-1$
    }
    retObj.append("</disabled></input>"); //$NON-NLS-1$

    return retObj.toString();
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
    ProcessListVariable list = procData.getList(name);
    
    for (int i=0; i < count; i++) {
      if (FormUtils.checkOutputField(props, varIndex, i)) {
        debug(userInfo, "parseAndSetList", "Row " + i + " of list var '" + name + " is output only... continuing to next row.");
        continue;
      }
      String formName = FormUtils.getListKey(name, i);
      String value = null; 
      if (formData.hasParameter(formName)) {
        // checked
        value = formData.getParameter(formName);
        try {
          list.parseAndSetItemValue(i, value);
          logBuffer.append("Set list var '" + name + "[" + i + "]' with '" + value + "';");
          debug(userInfo, "parseAndSetList", "Set list var '" + name + "[" + i + "]' with '" + value + "'");
        } catch (ParseException e) {
          Logger.error(user, this, "parseAndSetList", 
              procData.getSignature() + "error parsing list " + name + " item " + i, e);
          continue;
        }
      }
      else {
        // unchecked
        list.setItemValue(i, null);
      }
      
      
      // additional stuff
      if (StringUtils.isNotEmpty(value)) {

        String myValue = StringUtils.isNotEmpty(textVar) ? 
            procData.getListItemFormatted(textVar, i) : value;
        
        // vartext
        String textVarName = name + FormProps.sLIST_TEXT_SUFFIX;
        String textVarValue = procData.getFormatted(textVarName);
        if (StringUtils.isNotEmpty(textVarValue)) {
          textVarValue += textSep;
        }
        else {
          textVarValue = "";
        }
        
        textVarValue = textVarValue + myValue;
        try {
          procData.parseAndSet(textVarName, textVarValue);
          logBuffer.append("Set additional var '" + textVarName + "' with '" + textVarValue + "';");
          debug(userInfo, "parseAndSetList", "Set additional var '" + textVarName + "' with '" + textVarValue + "'");
        } 
        catch (ParseException e) {
          Logger.error(user, this, "parseAndSetList", 
              procData.getSignature() + 
              "error parsing vartext " + textVarName + " (item " + i + ")", e);
        }            

        // checked value
        String varCheckedVal = name + FormProps.sLIST_CHECKED_SUFFIX;

        String myCheckedValue = checkedValue;
        if (StringUtils.equals(myCheckedValue, FormProps.sDEF_CHECKED_VALUE) &&
            StringUtils.isNotEmpty(textVar)) {
          myCheckedValue = procData.getListItemFormatted(textVar, i);
        }
        
        try {
          procData.getList(varCheckedVal).parseAndSetItemValue(i, myCheckedValue);
          logBuffer.append("Set additional var '" + varCheckedVal + "[" + i + "]' as '" + myCheckedValue + "';");
          debug(userInfo, "parseAndSetList", "Set additional var '" + varCheckedVal + "[" + i + "]' as '" + myCheckedValue + "'");
        }
        catch (ParseException e) {
          Logger.error(user, this, "parseAndSetList", 
              procData.getSignature() + 
              "error parsing checked value " + varCheckedVal + " item " + i, e);
          continue;
        }            
      }
    }
    if (!ignoreValidation && 
        FormUtils.checkRequiredField(userInfo, procData, props) &&
        list.size() == 0) {
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
