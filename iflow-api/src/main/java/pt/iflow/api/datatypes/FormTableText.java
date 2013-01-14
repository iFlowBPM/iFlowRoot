package pt.iflow.api.datatypes;

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
 * <p>Title: FormTableText</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class FormTableText implements DataTypeInterface, ArrayTableProcessingCapable {

  private static final int DEFAULT_SIZE = 5;
  private static final int DEFAULT_MAX_LENGTH = 20;
  
  private String _sDataType = null;
  private String _sSize = null;
  private String _sMaxLength = null;
  private String _sOnChange = null;
  private String _sSubmitOnBlur = null;
  
  private DataTypeInterface _dti = null;

  private Locale locale = Locale.getDefault();

  private boolean hidePrefix = false;
  private boolean hideSufix = false;
  
  public FormTableText() {
  }

  @SuppressWarnings("unchecked")
  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String,String> extraProps, Map<String,Object> deps) {
    // defaults
    _sDataType = pt.iflow.api.datatypes.Text.class.getName();
    _sSize = String.valueOf(DEFAULT_SIZE);
    _sMaxLength = String.valueOf(DEFAULT_MAX_LENGTH);
    
    // TODO support form table value feeder?
    
    if (extraProps != null) {
      hidePrefix = DataTypeUtils.hidePrefix(extraProps);
      hideSufix = DataTypeUtils.hideSufix(extraProps);

      try {
        if (extraProps.containsKey("datatype")) { //$NON-NLS-1$
          _sDataType = (String)extraProps.get("datatype"); //$NON-NLS-1$
        }

        if (extraProps.containsKey("size")) { //$NON-NLS-1$
          _sSize = (String)extraProps.get("size"); //$NON-NLS-1$
        }
        
        if (extraProps.containsKey("maxlength")) { //$NON-NLS-1$
          _sMaxLength = (String)extraProps.get("maxlength"); //$NON-NLS-1$
        }

        if (extraProps.containsKey("onchange")) { //$NON-NLS-1$
          _sOnChange = (String)extraProps.get("onchange"); //$NON-NLS-1$
        }

        if (extraProps.containsKey(FormProps.TEXT_SUBMIT_ON_BLUR)) { 
          _sSubmitOnBlur = (String)extraProps.get(FormProps.TEXT_SUBMIT_ON_BLUR); 
        }
                
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "init", 
            procData.getSignature() + "exception caught", e);
      }
    }
    
    if (StringUtils.isNotEmpty(_sDataType)) {
      try {
        if (extraProps.containsKey("datatype")) { //$NON-NLS-1$
          _sDataType = (String)extraProps.get("datatype"); //$NON-NLS-1$
        }
        Class<? extends DataTypeInterface> cClass = (Class<? extends DataTypeInterface>) Class.forName(_sDataType);
        _dti = cClass.newInstance();
        _dti.setLocale(locale);
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "init", 
            procData.getSignature() + "exception caught", e);
      }
    }    
  }

  public int getID() {
    return 11;
  }

  public String getDescription() {
    return Messages.getString("FormTableText.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("FormTableText.short_description"); //$NON-NLS-1$
  }

  public String getPrimitiveType() {
    return getPrimitiveTypeMethod();
  }

  public String getPrimitiveTypeMethod() {
    if (_dti != null) return _dti.getPrimitiveTypeMethod();
    return "String"; //$NON-NLS-1$
  }

  public int getFormSize() {
    return 50;
  }

  public String getFormPrefix() {
    return getFormPrefix(null);
  }
  public String getFormPrefix(Object[] aoaArgs) {
    if (!hidePrefix && _dti != null) return _dti.getFormPrefix(aoaArgs);
    return ""; //$NON-NLS-1$
  }

  public String getFormSuffix() {
    return getFormSuffix(null);
  }
  public String getFormSuffix(Object[] aoaArgs) {
    if (!hideSufix && _dti != null) return _dti.getFormSuffix(aoaArgs);
    return ""; //$NON-NLS-1$
  }

  public String validateFormData (Object input) {
    return validateFormData(input,null);
  }

  public String validateFormData (Object input, Object[] aoaArgs) {
    if (_dti != null) return _dti.validateFormData(input, aoaArgs);
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

    String ret = null;
    if (_dti != null) {
      ret = _dti.formatRow(userInfo, procData, service, fieldNumber, varIndex, name, row, value, props, response);
    }
    else {
      ret = value != null ? value.format() : "";
    }

    String myName = row < 0 ? name : FormUtils.getListKey(name,row);
    return genXml(ret, _sSize, _sMaxLength, _sOnChange, _sSubmitOnBlur, props.getProperty(FormProps.FORM_NAME), myName, outputOnly, false);

  }

  
  public String formatToHtml (Object input) {
    return formatToHtml(input,null);
  }

  public String formatToHtml (Object input, Object[] aoaArgs) {
    String retObj = ""; //$NON-NLS-1$

    if (_dti != null) {
      retObj = _dti.formatToHtml(input, aoaArgs);
    }
    else {
      if (input != null) {
	retObj = (String)input;
      }
    }

    return genXml(retObj, _sSize, _sMaxLength, _sOnChange, _sSubmitOnBlur, null, (String)aoaArgs[0], true, false);
  }

  public String formatToForm (Object input) {
    return formatToForm(input,null);
  }

  public String formatToForm (Object input, Object[] aoaArgs) {
    String retObj = ""; //$NON-NLS-1$

    if (_dti != null) {
      retObj = _dti.formatToForm(input, aoaArgs);
    }
    else {
      if (input != null) {
	retObj = (String)input;
      }
    }

    return genXml(retObj, _sSize, _sMaxLength, _sOnChange, _sSubmitOnBlur, null, (String)aoaArgs[0], false, false);
  }

  public String getText(Object input) {
    if (_dti != null) return _dti.getText(input);

    return value(input);
  }

  public double getValue(Object input) {
    if (_dti != null) return _dti.getValue(input);

    return java.lang.Double.NaN;
  }

  public static String value (Object s) {
    return (String)s;
  }


  public static String genXml(String asValue,
			       String asSize, 
			       String asMaxLength,
                   String asOnChange,
                   String asSubmitOnBlur,
                   String formName,
			       String varName,
			       boolean abDisabled,
                   boolean abReadOnly) {

    StringBuffer sb = new StringBuffer();
    
    if (abDisabled) {
      sb.append(asValue);
    }
    else {
      sb.append("<input><type>tabletext</type>"); //$NON-NLS-1$
      sb.append("<variable>").append(varName).append("</variable>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<value>").append(asValue).append("</value>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<size>").append(asSize).append("</size>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<maxlength>").append(asMaxLength).append("</maxlength>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<readonly>").append(abReadOnly).append("</readonly>"); //$NON-NLS-1$ //$NON-NLS-2$
      
      if (asOnChange != null && !asOnChange.equals("") && !asOnChange.equals("null")) //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("<onchange>").append(asOnChange).append("</onchange>"); //$NON-NLS-1$ //$NON-NLS-2$

      sb.append(FormUtils.genTextFieldSubmitOnChangeXml(varName, asSubmitOnBlur, formName));
      
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
}
