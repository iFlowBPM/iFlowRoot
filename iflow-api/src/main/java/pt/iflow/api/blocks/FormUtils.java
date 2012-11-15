package pt.iflow.api.blocks;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentHash;
import pt.iflow.api.documents.DocumentIdentifier;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class FormUtils {
  private FormUtils() {}
  private static FormUtils INSTANCE = new FormUtils();

  private static final Pattern PATTERN_AMP = Pattern.compile(Pattern.quote("&amp;"), Pattern.CASE_INSENSITIVE);

  // COMPAT FROM DATA SET XXX REMOVE IN THE FUTURE
  private final static String sLIST_PREFIX_START = "[";
  private final static String sLIST_PREFIX_END = "]";


  
  public static String generateDocumentURL(UserInfoInterface userInfo, ServletUtils response, ProcessData procData,
      DocumentIdentifier docId) {
    return generateDocumentURL(userInfo, response, procData.getFlowId(), procData.getPid(), procData.getSubPid(), docId);
  }

  public static String generateDocumentURL(UserInfoInterface userInfo, ServletUtils response, int flowid, int pid, int subpid,
      DocumentIdentifier docId) {
    String sUseDocHash = BeanFactory.getFlowSettingsBean().getFlowSetting(flowid, Const.sHASHED_DOCUMENT_URL).getValue();
    boolean useDocHash = StringUtils.equalsIgnoreCase(Const.sHASHED_DOCUMENT_URL_YES, sUseDocHash);
    String sLinkUrl;
    if (useDocHash)
      sLinkUrl = response.encodeURL(Const.APP_URL_PREFIX, "document", "hdoc=" + (new DocumentHash(userInfo, docId, null))
          + "&flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid);
    else
      sLinkUrl = response.encodeURL(Const.APP_URL_PREFIX, "document", "docid=" + docId.getId() + "&flowid=" + flowid + "&pid="
          + pid + "&subpid=" + subpid);
    return StringEscapeUtils.escapeXml(sLinkUrl);
  }

  public static int getIndexFromKey(String key) {
    int retObj = -1;

    if (key == null)
      return retObj;

    String stmp = null;

    try {

      if (key.endsWith(sLIST_PREFIX_END)) {
        int idx = key.lastIndexOf(sLIST_PREFIX_START) + sLIST_PREFIX_START.length();
        stmp = key.substring(idx, key.indexOf(sLIST_PREFIX_END, idx));

        retObj = Integer.parseInt(stmp);
      }
    } catch (Exception e) {
    }

    return retObj;
  }

  public static String getListKey(String key, int index) {
    String retObj = key;

    int _index = getIndexFromKey(key);

    if (_index == -1) {
      retObj = key + sLIST_PREFIX_START + index + sLIST_PREFIX_END;
    } else if (_index != index) {
      return null;
    }

    return retObj;
  }

  public static String escapeAmp(String source) {
    if (StringUtils.isEmpty(source))
      return source;

    // unescape
    Matcher m = PATTERN_AMP.matcher(source);
    String ret = m.replaceAll("&");

    // escape all
    return StringUtils.replace(ret, "&", "&amp;");
  }
  
  public static boolean checkOutputField(Properties props) {
    return checkOutputField(props, -1, -1);
  }
  
  public static boolean checkOutputField(Properties props, int varIndex) {
    return checkOutputField(props, varIndex, -1);
  }
  
  public static boolean checkOutputField(Properties props, int varIndex, int row) {
    boolean isFieldDisabled = StringUtils.equals(props.getProperty(FormProps.OUTPUT_FIELD), "true");
    
    String prefix = varIndex > -1 ? varIndex + "_" : "";    
    String outputProp = props.getProperty(prefix + FormProps.sOUTPUT_ONLY);
    
    boolean hasOutputProperty = outputProp != null;
    
    boolean conditionalCheck = false;
    if (hasOutputProperty) {
      conditionalCheck = StringUtils.equals(outputProp, "true");
    }
    else {
      conditionalCheck = isFieldDisabled; 
    }
    
    return conditionalCheck
      || (row >= 0 && StringUtils.equals(props.getProperty("row_"+ row + "_" + FormProps.sOUTPUT_ONLY), "true"))
      || (row >= 0 && StringUtils.equals(props.getProperty("row_"+ row + "_" + FormProps.DISABLED), "true"))
      || (row >= 0 && StringUtils.equals(props.getProperty(row + "_" + FormProps.sROW_CONTROL_LIST),"separator"));
  }

  public static boolean checkRequiredField(UserInfoInterface userInfo, ProcessData procData, Properties props) {
    String cond = null; 
      
    if (props != null) {
      cond = props.getProperty(FormProps.sOBLIGATORY_FIELD);
    }
    
    boolean ret = false;
    
    if (StringUtils.isNotEmpty(cond)) {
      try {
        ret = procData.query(userInfo, cond);
      }
      catch (Exception ei) {
        Logger.error(userInfo.getUtilizador(), INSTANCE, "checkRequiredField", 
            procData.getSignature() + "Exception querying obligatory condition: " + cond, ei);
      }
    }  
    
    Logger.debug(userInfo.getUtilizador(), INSTANCE, "checkRequiredField", "obligatory condition: " + cond + " => evaluated " + ret);
    
    return ret;
  }

//  public static boolean checkRequiredField(ProcessData procData, Properties props, int varIndex) {
//  }
//  
//  public static boolean checkRequiredField(ProcessData procData, Properties props, int varIndex, int row) {
//    
//  }
  

  public static String formatParsingError(Properties props, String varName, String parseResult) {
    if (parseResult != null) {
      // format to a more friendly message
      if (props.getProperty("text") != null) {
        return props.getProperty("text") + ": " + parseResult;
      } else if (props.getProperty(FormProps.sTITLE) != null) {
        return props.getProperty(FormProps.sTITLE) + ": " + parseResult;
      }
      else {
        return varName + ": " + parseResult;
      }
    }
    return parseResult;
  }

  public static String formatJavascriptDateFormat(String dateFormat) {
    if (StringUtils.isEmpty(dateFormat)) {
      return "";
    }
    
    String jsformat = StringUtils.replace(dateFormat, "dd", "d");
    jsformat = StringUtils.replace(jsformat, "d", "%d");

    jsformat = StringUtils.replace(jsformat, "MM", "M");
    jsformat = StringUtils.replace(jsformat, "M", "%m");

    jsformat = StringUtils.replace(jsformat, "yyyy", "%Y");
    jsformat = StringUtils.replace(jsformat, "yy", "%y");

    jsformat = StringUtils.replace(jsformat, "HH", "%H");
    jsformat = StringUtils.replace(jsformat, "hh", "%I");
    jsformat = StringUtils.replace(jsformat, "mm", "%M");
    jsformat = StringUtils.replace(jsformat, "s", "%S");
    jsformat = StringUtils.replace(jsformat, "a", "%P");
    
    return jsformat;
  }
  
  public static String getOnChangeSubmit(String onchange, String formName) {
    if (StringUtils.equals("true", onchange)) {
      return "camouflageForm();"
          + "document." + formName + ".op.value='" + FormOperations.OP_ONCHANGE_SUBMIT + "';"
          + "document." + formName + ".submit();";
    }
    else {
      return "";
    }
  }
  
  public static String getTransformedText(UserInfoInterface userInfo, ProcessData procData, String text) {
    String ret = text;
    if (StringUtils.isNotEmpty(ret)) {
      if (procData.isTransformable(ret, true)) {
        try {
          ret = procData.transform(userInfo, ret);
          Logger.debug(userInfo.getUtilizador(), INSTANCE, "getTransformedText", 
              procData.getSignature() + "transformed text to \"" + ret + "\"");
        } catch (EvalException e) {
          // not able to transform.. leave it as it is..
          Logger.info(userInfo.getUtilizador(), INSTANCE, "getTransformedText", 
              procData.getSignature() + "assuming text was transformable.. bad assumption! reverted to original value");
        }
      }            
    }  
    return ret;
  }
  
  private static final String EXTRA_PROP_VALUE_PATTERN = "__##__";
  public static String encodeExtraPropValue(String value) {
    return StringUtils.replace(value, ",", EXTRA_PROP_VALUE_PATTERN);
  }

  public static String decodeExtraPropValue(String value) {
    return StringUtils.replace(value, EXTRA_PROP_VALUE_PATTERN, ",");
  }
  

  public static String genTextFieldSubmitOnChangeXml(String variable, String asSubmitOnBlur, String formName) {

    String onblur = "";
    String onfocus = "";
    if (Utils.string2bool(asSubmitOnBlur)) {
      if (StringUtils.isEmpty(formName)) {
        formName = "dados";
      }

      onblur = 
        "javascript:if (textfieldChangeOnExit('" + variable + "')) { "
        + "camouflageForm();"
        + "document." + formName + ".op.value='3';"
        + "document." + formName + ".submit(); }";
      onfocus = "javascript:textfieldChangeOnEnter('" + variable + "');";

    }
    StringBuilder sb = new StringBuilder();
    sb.append("<onblur>");
    sb.append(onblur);
    sb.append("</onblur>");

    sb.append("<onfocus>");
    sb.append(onfocus);
    sb.append("</onfocus>");
    
    return sb.toString();
  }


  public static String genLinkXml(String URL, String filename, boolean disabled) {

    StringBuffer sbLink = new StringBuffer();

    if(StringUtils.isNotEmpty(filename)) {

      sbLink.append("<a><href>"); //$NON-NLS-1$
      sbLink.append(URL);
      sbLink.append("</href>"); //$NON-NLS-1$
      sbLink.append("<text>"); //$NON-NLS-1$
      sbLink.append(filename); //$NON-NLS-1$
      sbLink.append("</text>"); //$NON-NLS-1$
      sbLink.append("<disabled>").append(disabled ? "true" : "false").append("</disabled>"); //$NON-NLS-1$
      sbLink.append("</a>"); //$NON-NLS-1$
    }

    return sbLink.toString();
  }
}
