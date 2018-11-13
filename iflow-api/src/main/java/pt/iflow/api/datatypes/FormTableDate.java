package pt.iflow.api.datatypes;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
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
import pt.iflow.api.utils.Utils;
import pt.iknow.utils.html.FormData;

/**
 * <p>Title: FormTableText</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class FormTableDate implements DataTypeInterface, ArrayTableProcessingCapable {

  private static final int DEFAULT_SIZE = 10;
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
  
  public FormTableDate() {
  }

  @SuppressWarnings("unchecked")
  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String,String> extraProps, Map<String,Object> deps) {
    // defaults
    _sDataType = pt.iflow.api.datatypes.Date.class.getName();
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
    return 17;
  }

  public String getDescription() {
    return Messages.getString("FormTableDate.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("FormTableDate.short_description"); //$NON-NLS-1$
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
    
    String dateFormatProp = getDateFormatProp(userInfo, procData, props);
    DateFormat dateFormat = getDateFormat(userInfo, dateFormatProp);
    props.setProperty("date_format", dateFormatProp);

    String ret = null;
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

    String myName = row < 0 ? name : FormUtils.getListKey(name,row);
    return genXml(ret, _sSize, _sMaxLength, _sOnChange, _sSubmitOnBlur, props.getProperty(FormProps.FORM_NAME), myName, outputOnly, false, props);

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

    return genXml(retObj, _sSize, _sMaxLength, _sOnChange, _sSubmitOnBlur, null, (String)aoaArgs[0], true, false, null);
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

    return genXml(retObj, _sSize, _sMaxLength, _sOnChange, _sSubmitOnBlur, null, (String)aoaArgs[0], false, false,null);
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
                   boolean abReadOnly,
                   Properties prop) { 

    try {
      StringBuffer sb = new StringBuffer();

      String stmp = null;

      sb.append("<input><type>datecal</type>");
      sb.append("<variable>").append(varName).append("</variable>");
      String sValue = asValue;
      if (sValue != null) sValue = sValue.trim();


      String sDateFormat = prop.getProperty("date_format");
      if (sDateFormat != null) {
      	sDateFormat = sDateFormat.trim();
      }
      if (prop.containsKey(FormProps.HOUR_FORMAT)) {
      	String sHourFormat = prop.getProperty(FormProps.HOUR_FORMAT).trim();
      	sHourFormat = sHourFormat.split("\\[")[0].trim(); // remove description labels, found after '['
      	if (StringUtils.contains(sHourFormat, ":")) {
      		sDateFormat += " " + sHourFormat;
      	}
      }

      if (StringUtils.isEmpty(sValue)) {
        // check if it's configured to use current date
        stmp = prop.getProperty("currdate_ifempty");
        if (stmp != null && stmp.equalsIgnoreCase("true")) {
          // yes
          sValue = DateUtility.newBlockDate(prop.getProperty(Block.sORG_ID_PROP), sDateFormat);
        }
      }

      // set size for the text box from the pattern
      try {    	
      	int aSize = asValue.length();
      	if (aSize > java.lang.Integer.parseInt(asSize)) 
      		asSize = "" + aSize;      	
      } catch (NumberFormatException ex) {}
      
      sb.append("<value>").append(sValue).append("</value>");
      sb.append("<size>").append(asSize).append("</size>");
      sb.append("<maxlength>").append(asSize).append("</maxlength>");
      sb.append("<formname>").append(formName).append("</formname>");
      sb.append("<disabled>").append(abDisabled).append("</disabled>");
      sb.append("<dateformat>").append(FormUtils.formatJavascriptDateFormat(sDateFormat)).append("</dateformat>");
      sb.append("<dateformattext>").append(sDateFormat).append("</dateformattext>");
      sb.append("<dateformatid>").append(prop.getProperty("date_format_id")).append("</dateformatid>");
      
      sb.append("<hour_format_id>");
      int hourId = 0;
      if (prop.containsKey(FormProps.HOUR_FORMAT_ID)) {
      	try {
      		hourId = java.lang.Integer.parseInt(prop.getProperty(FormProps.HOUR_FORMAT_ID));
      	} catch (NumberFormatException ex) {
      	}
      }
      sb.append(hourId);
      sb.append("</hour_format_id>");

      if (prop.containsKey(FormProps.sONCHANGE_SUBMIT)) {
      	sb.append("<onchange_submit>");
      	sb.append(prop.getProperty(FormProps.sONCHANGE_SUBMIT));
      	sb.append("</onchange_submit>");
      }

      sb.append("<obligatory>");
      sb.append(Utils.string2bool(prop.getProperty(FormProps.sOBLIGATORY_PROP)));
      sb.append("</obligatory>");
      
      sb.append("<even_field>").append(prop.getProperty("even_field")).append("</even_field>");
      sb.append("</input>");

      return sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  
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
	    	value = dateFormat.parse(StringEscapeUtils.unescapeHtml(formValue));
	      }
	    }
	    return value;
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
