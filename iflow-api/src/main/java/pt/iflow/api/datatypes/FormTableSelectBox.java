package pt.iflow.api.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.FormService;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessVariableValue;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

/**
 * <p>
 * Title: FormTableText
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author iKnow
 * @version 1.0
 */

public class FormTableSelectBox implements DataTypeInterface, ArrayTableProcessingCapable {

  // Table Select Box stuff
  private final static String sDESC_DATA_FEEDER = "desc_feeder";
  private final static String sVALUE_DATA_FEEDER = "value_feeder";
  private final static String sDESCRIPTION_ARRAY_NAME_PROP = "descriptions";
  private final static String sVALUE_ARRAY_NAME_PROP = "values";

  private static final int DESCRIPTION_POS = 0;

  private static final int VALUE_POS = 1;

  private String _sDataType = null;

  private String _sOnChange = null;

  private DataTypeInterface _dti = null;

  private String[][] options;
  
  private Map<String,String> valueMapping;

  private Locale locale = Locale.getDefault();

  private FormTableValueFeeder valueFeeder = null;
  private FormTableValueFeeder descFeeder = null;
  private boolean useFeeder = false;

  private boolean hidePrefix = false;
  private boolean hideSufix = false;
  
  public FormTableSelectBox() {
    valueMapping = new HashMap<String, String>();
    options = new String[0][0];
  }

  private Value getProcessValues(UserInfoInterface userInfo, ProcessData procData, Map<String,String> extraProps) {
    String login = userInfo.getUtilizador();

    String descArrayName = extraProps.get(FormTableSelectBox.sDESCRIPTION_ARRAY_NAME_PROP);
    String valsArrayName = extraProps.get(FormTableSelectBox.sVALUE_ARRAY_NAME_PROP);

    Logger.debug(login,this,"getProcessValues",
        procData.getSignature() + "descArraName=" + descArrayName);                  
    Logger.debug(login,this,"getProcessValues",
        procData.getSignature() + "valsArrayName=" + valsArrayName);                  

    
    int descSize = 0;
    int valsSize = 0;
    if (procData.getList(valsArrayName) == null) {
      Logger.warning(login,this,"getProcessValues",
          procData.getSignature() + "FormTableSelectBox value array LIST VAR " + valsArrayName + " IS NULL (perhaps not defined in catalogue)!");                                    
    }
    else {
      valsSize = procData.getList(valsArrayName).size();
    } 
    
    if (StringUtils.isEmpty(descArrayName) || procData.getList(descArrayName) == null) {
      Logger.warning(login,this,"getProcessValues",
          procData.getSignature() + "FormTableSelectBox desc array LIST VAR " + descArrayName + " IS NULL (perhaps not defined in catalogue)! Using vals array");                  
      descArrayName = valsArrayName;
    }
    
    if (StringUtils.isNotEmpty(descArrayName) && procData.getList(descArrayName) != null) {
      descSize = procData.getList(descArrayName).size();
    }
    
    // TODO algumas validacoes aqui...
    String[] descArr = new String[descSize];
    String[] valsArr = new String[valsSize];
    for(int itks = 0; itks < descSize; itks++) {
      descArr[itks] = procData.getListItemFormatted(descArrayName, itks);
    }
    for(int itks = 0; itks < valsSize; itks++) {
      valsArr[itks] = procData.getListItemFormatted(valsArrayName, itks);
    }
    
    Value ret = new Value();
    ret.descs = descArr;
    ret.values = valsArr;
    
    return ret;
  }
  
  @SuppressWarnings("unchecked")
  private void initFeeders(UserInfoInterface userInfo, ProcessData procData, Map<String,String> extraProps) {
    String login = userInfo.getUtilizador();
    
    String descDataFeeder = extraProps.get(FormTableSelectBox.sDESC_DATA_FEEDER);
    String valueDataFeeder = extraProps.get(FormTableSelectBox.sVALUE_DATA_FEEDER);
    
    Logger.debug(login,this,"getFeederValues",
        procData.getSignature() + "descFeeder=" + descDataFeeder);                  
    Logger.debug(login,this,"getFeederValues",
        procData.getSignature() + "valueFeeder=" + valueDataFeeder);                  

    if (StringUtils.isNotEmpty(descDataFeeder)) {
      try {
        Class descClazz = BeanFactory.getRepBean().loadClass(userInfo, descDataFeeder); 
        descFeeder = (FormTableValueFeeder)descClazz.newInstance();
      } 
      catch (Exception e) {
        Logger.error(login,this,"getFeederValues",
            procData.getSignature() + "Exception caught for desc feeder " + descDataFeeder, e);
      }
    }
    
    try {
      Class valueClazz = BeanFactory.getRepBean().loadClass(userInfo, valueDataFeeder); 
      valueFeeder = (FormTableValueFeeder)valueClazz.newInstance();
    } 
    catch (Exception e) {
      Logger.error(login,this,"getFeederValues",
          procData.getSignature() + "Exception caught for value feeder " +  valueDataFeeder, e);
    }

    if (descFeeder == null && valueFeeder != null) {
      descFeeder = valueFeeder;
    }
    
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String,String> extraProps, Map<String,Object> deps) {

    String login = userInfo.getUtilizador();
    
    if (extraProps == null)
      return;

    hidePrefix = DataTypeUtils.hidePrefix(extraProps);
    hideSufix = DataTypeUtils.hideSufix(extraProps);
    
    String[] descArr = null;
    String[] valsArr = null;

    Value dataValue = null;
    if (StringUtils.isNotEmpty( extraProps.get(FormTableSelectBox.sVALUE_ARRAY_NAME_PROP))) {
      dataValue = getProcessValues(userInfo, procData, extraProps);
    }
    else if (StringUtils.isNotEmpty( extraProps.get(FormTableSelectBox.sVALUE_DATA_FEEDER))) {
      useFeeder = true;
      initFeeders(userInfo, procData, extraProps);
    }
    if (dataValue != null) {
      descArr = dataValue.descs;
      valsArr = dataValue.values;
    }
    
    if (valsArr == null) {
      valsArr = new String[0];
    }
    if (descArr == null) {
      descArr = valsArr;  // if no descriptions supplied, use values
    }

    if (extraProps.containsKey("datatype")) { //$NON-NLS-1$
      try {
        _sDataType = extraProps.get("datatype"); //$NON-NLS-1$
        Class<?> cClass = Class.forName(_sDataType);
        _dti = (DataTypeInterface) cClass.newInstance();
        _dti.setLocale(locale);
      } catch (Exception e) {
      }
    }

    if (extraProps.containsKey(FormProps.sONCHANGE_SUBMIT)) { //$NON-NLS-1$
      _sOnChange = extraProps.get(FormProps.sONCHANGE_SUBMIT); //$NON-NLS-1$
    }
    else if (extraProps.containsKey("onchange")) { //$NON-NLS-1$
      _sOnChange = extraProps.get("onchange"); //$NON-NLS-1$
    }
    
    ArrayList<String[]> alOpts = new ArrayList<String[]>();
    for (int i = 0; i < valsArr.length; i++) {
      if (null == valsArr[i])
        continue; // ignore null values.

      String[] elem = new String[2];
      // _dti.formatToForm(values[i]); // TODO ver os datatypes
      elem[VALUE_POS] = String.valueOf(valsArr[i]);
      if (null == descArr || 0 == descArr.length || null == descArr[i]) {
        elem[DESCRIPTION_POS] = String.valueOf(descArr[i]);
      } else {
        elem[DESCRIPTION_POS] = String.valueOf(descArr[i]);
      }
      alOpts.add(elem);
      valueMapping.put(elem[VALUE_POS], elem[DESCRIPTION_POS]);

      Logger.debug(login,this,"init",
          procData.getSignature() + "Added: " + elem[DESCRIPTION_POS] + " = " + elem[VALUE_POS]); //$NON-NLS-1$ //$NON-NLS-2$
    }

    options = genOptions(userInfo, procData, (String[])valsArr, (String[])descArr);
  }

  private String[][] genOptions(UserInfoInterface userInfo, ProcessData procData, String[] valsArr, String[] descArr) {
    ArrayList<String[]> alOpts = new ArrayList<String[]>();
    for (int i = 0; valsArr != null && i < valsArr.length; i++) {
      if (null == valsArr[i])
        continue; // ignore null values.

      String[] elem = new String[2];
      // _dti.formatToForm(values[i]); // TODO ver os datatypes
      elem[VALUE_POS] = StringEscapeUtils.escapeXml(String.valueOf(valsArr[i]));
      if (null == descArr || 0 == descArr.length || null == descArr[i]) {
        elem[DESCRIPTION_POS] = StringEscapeUtils.escapeXml(String.valueOf(valsArr[i]));
      } else {
        elem[DESCRIPTION_POS] = StringEscapeUtils.escapeXml(String.valueOf(descArr[i]));
      }
      alOpts.add(elem);
      valueMapping.put(elem[VALUE_POS], elem[DESCRIPTION_POS]);

      Logger.debug(userInfo.getUtilizador(),this,"genOptions",
          procData.getSignature() + "Added: " + elem[DESCRIPTION_POS] + " = " + elem[VALUE_POS]); //$NON-NLS-1$ //$NON-NLS-2$
    }

    return (String[][]) alOpts.toArray(new String[alOpts.size()][]);    
  }
  
  public int getID() {
    return 12;
  }

  public String getDescription() {
    return Messages.getString("FormTableSelectBox.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("FormTableSelectBox.short_description"); //$NON-NLS-1$
  }

  public String getPrimitiveType() {
    return getPrimitiveTypeMethod();
  }

  public String getPrimitiveTypeMethod() {
    if (_dti != null)
      return _dti.getPrimitiveTypeMethod();
    return "String"; //$NON-NLS-1$
  }

  public int getFormSize() {
    return 1;
  }

  public String getFormPrefix() {
    return getFormPrefix(null);
  }

  public String getFormPrefix(Object[] aoaArgs) {
    if (!hidePrefix && _dti != null)
      return _dti.getFormPrefix(aoaArgs);
    return ""; //$NON-NLS-1$
  }

  public String getFormSuffix() {
    return getFormSuffix(null);
  }

  public String getFormSuffix(Object[] aoaArgs) {
    if (!hideSufix && _dti != null)
      return _dti.getFormSuffix(aoaArgs);
    return ""; //$NON-NLS-1$
  }

  public String validateFormData(Object input) {
    return validateFormData(input, null);
  }

  public String validateFormData(Object input, Object[] aoaArgs) {
    if (_dti != null)
      return _dti.validateFormData(input, aoaArgs);
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
    
    String ret = null;
    if (_dti != null) {
      ret = _dti.formatRow(userInfo, procData, service, fieldNumber, isOutput, varIndex, name, row, value, props, response);
    }
    else {
      ret = value != null ? value.format() : "";
    }

    String myName = row < 0 ? name : FormUtils.getListKey(name,row);
    
    if (useFeeder) {
      String[] vals = valueFeeder.getValues(userInfo, procData, name, row);
      String[] descs = vals;
      if (descFeeder != valueFeeder)
        descs = descFeeder.getValues(userInfo, procData, name, row);
      
      options = genOptions(userInfo, procData, vals, descs); 
    }
    
    String onChange = FormUtils.getOnChangeSubmit(_sOnChange, props.getProperty(FormProps.FORM_NAME));
    
    return genXml(ret, onChange, myName, outputOnly, false);
  }
  
  public String formatToHtml(Object input) {
    return formatToHtml(input, null);
  }

  public String formatToHtml(Object input, Object[] aoaArgs) {
    String retObj = ""; //$NON-NLS-1$

    if (_dti != null) {
      retObj = _dti.formatToHtml(input, aoaArgs);
    } else {
      if (input != null) {
        retObj = (String) input;
      }
    }

    return genXml(retObj, _sOnChange, (String)aoaArgs[0], true, false);
  }

  public String formatToForm(Object input) {
    return formatToForm(input, null);
  }

  public String formatToForm(Object input, Object[] aoaArgs) {
    String retObj = ""; //$NON-NLS-1$

    if (_dti != null) {
      retObj = _dti.formatToForm(input, aoaArgs);
    } else {
      if (input != null) {
        retObj = (String) input;
      }
    }

    return genXml(retObj, _sOnChange, (String)aoaArgs[0], false, false);
  }

  public String getText(Object input) {
    if (_dti != null)
      return _dti.getText(input);

    return value(input);
  }

  public double getValue(Object input) {
    if (_dti != null)
      return _dti.getValue(input);

    return java.lang.Double.NaN;
  }

  public static String value(Object s) {
    return (String) s;
  }

  private String genXml(String asValue, String asOnChange, String var, boolean abDisabled, boolean abReadOnly) {

    StringBuffer sb = new StringBuffer();

    if (abDisabled) {
      String displayValue = valueMapping.get(asValue);
      if(null == displayValue) displayValue=""; //$NON-NLS-1$
      sb.append(displayValue);
    } else {
      sb.append("<input><type>tableselection</type>"); //$NON-NLS-1$
      sb.append("<variable>").append(var).append("</variable>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<value>").append(asValue).append("</value>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<readonly>").append(abReadOnly).append("</readonly>"); //$NON-NLS-1$ //$NON-NLS-2$
      if (StringUtils.isNotEmpty(asOnChange) && !StringUtils.equals("null", asOnChange)) {
        sb.append("<onchange>").append(asOnChange).append("</onchange>"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      
      for (int i = 0; i < options.length; i++) {
        sb.append("<option><text>").append(options[i][0]).append("</text><value>").append(options[i][1]).append("</value>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (options[i][1].equals(asValue)) {
          sb.append("<selected>yes</selected>"); //$NON-NLS-1$
        }
        sb.append("</option>"); //$NON-NLS-1$
      }

      sb.append("</input>"); //$NON-NLS-1$
    }

    return sb.toString();
  }

  public void setLocale(Locale locale) {
    if(null == locale) locale = Locale.getDefault();
    this.locale = locale;
    if(null != _dti) _dti.setLocale(locale);
  }
  
  public void formPreProcess(UserInfoInterface userInfo, ProcessData procData, String name, Properties props, StringBuilder logBuffer) {
  }

  public String parseAndSet(UserInfoInterface userInfo, ProcessData procData, String name, FormData formData, Properties props, boolean ignoreValidation, StringBuilder logBuffer) {
    return null;
  }

  public String parseAndSetList(UserInfoInterface userInfo, ProcessData procData, 
      int varIndex, String name, int count, FormData formData,
      Properties props, boolean ignoreValidation, StringBuilder logBuffer) {

    String user = userInfo.getUtilizador();
    ProcessListVariable list = procData.getList(name);
    for (int i = 0; i < count; i++) {
      try {
        if (FormUtils.checkOutputField(props, varIndex, i)) {
          debug(userInfo, "parseAndSetList", "Row " + i + " of list var '" + name + " is output only... continuing to next row.");
          continue;
        }
        String formName = FormUtils.getListKey(name, i);
        String value = formData.hasParameter(formName) ? formData.getParameter(formName) : null; 
        list.parseAndSetItemValue(i, value);
        
        logBuffer.append("Set list var '" + name + "[" + i + "]' as '" + value + "';");
        debug(userInfo, "parseAndSetList", "Set list var '" + name + "[" + i + "]' as '" + value + "'");
      }
      catch (Exception e) {
        Logger.error(user, this, "parseAndSetList", 
            procData.getSignature() + "error parsing list " + name + " item " + i, e);
      }
    }
    if (!ignoreValidation && 
        FormUtils.checkRequiredField(userInfo, procData, props) &&
        list.size() == 0) {
      return userInfo.getMessages().getString("Datatype.required_field");
    }
    return null;
  }

  private void debug(UserInfoInterface userInfo, String method, String message) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, method, message);
    }
  }

  private class Value {
    String[] descs;
    String[] values;
  }
}
