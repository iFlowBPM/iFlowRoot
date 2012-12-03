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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.FormService;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessVariableValue;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class Link implements DataTypeInterface {

  Map<String,String> hmData = new HashMap<String, String>();
  
  public Link() {
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String,Object> deps) {

    if (extraProps != null) {
      // handle special vars
      hmData = new HashMap<String,String>();
      Iterator<String> iterSV = extraProps.keySet().iterator();
      while (iterSV != null && iterSV.hasNext()) {
        String sKey = iterSV.next();
        String sValue = extraProps.get(sKey);

        hmData.put(sKey, sValue);

        if (sKey.startsWith("vardep")) {
          String dep = sKey + "_" + sValue;  // stored var is vardep<n>_<var>
          // get value from proc for this var dependency
          hmData.put(dep, sValue);
        }
      }

      // hidden fields to be able to build link
      int hiddenIndex = 0;
      hmData.put("varhid" + hiddenIndex++, "op");      
      hmData.put("op","3");
      hmData.put("varhid" + hiddenIndex++, DataSetVariables.FLOWID);
      hmData.put(DataSetVariables.FLOWID,String.valueOf(procData.getFlowId()));
      hmData.put("varhid" + hiddenIndex++, DataSetVariables.PID);
      hmData.put(DataSetVariables.PID,String.valueOf(procData.getPid()));
      hmData.put("varhid" + hiddenIndex++, DataSetVariables.SUBPID);
      hmData.put(DataSetVariables.SUBPID,String.valueOf(procData.getSubPid()));
    }
  }

  public int getID() {
    return 10;
  }

  public String getDescription() {
    return Messages.getString("Link.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("Link.short_description"); //$NON-NLS-1$
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
    
    String maxRowProp = props.getProperty(fieldNumber + FormProps.sMAX_ROW) != null ? 
        props.getProperty(fieldNumber + FormProps.sMAX_ROW) : 
          props.getProperty(FormProps.sMAX_ROW);
    int maxRow = StringUtils.isNumeric(maxRowProp) ? java.lang.Integer.parseInt(maxRowProp) : -1;
    
    String linkVar = row < 0 ? name : name + "_" + row;
    
    debug(userInfo, "formatRow", "output?" + outputOnly + "; maxrow:" + maxRow + "; linkvar:" + linkVar);
    
    return genLinkXml(value != null ? value.format() : "",
        props.getProperty(FormProps.JSP),
        linkVar,
        props.getProperty(FormProps.FORM_NAME),
        hmData,
        outputOnly,
        procData,
        fieldNumber,
        row,
        maxRow);
  }
  
  
  public String formatToHtml (Object input) {
    return formatToHtml(input,null);
  }

  @SuppressWarnings("unchecked")
  public String formatToHtml (Object input, Object[] aoaArgs) {
    return genLinkXml(value(input),
        (String)aoaArgs[0],
        (String)aoaArgs[1],
        (String)aoaArgs[2],
        (Map<String,String>)aoaArgs[3],
        true,null,
        -1,-1,-1);
  }

  public String formatToForm (Object input) {
    return formatToForm(input,null);
  }

  @SuppressWarnings("unchecked")
  public String formatToForm (Object input, Object[] aoaArgs) {
    return genLinkXml(value(input),
        (String)aoaArgs[0],
        (String)aoaArgs[1],
        (String)aoaArgs[2],
        (Map<String,String>)aoaArgs[3],
        false,null,
        -1,-1,-1);
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

  private static String genLinkXml(String asValue, 
      String link,
      String var,
      String formName,
      Map<String,String> data,
      boolean abDisabled,
      ProcessData procData,
      int fieldNumber, int row, int maxRow) {


    boolean bDisabled = false;
    if (abDisabled) {
      bDisabled = true;
    }
    if (data.containsKey("disabled")) { //$NON-NLS-1$
      String dis = data.get("disabled"); //$NON-NLS-1$
      if (dis.equalsIgnoreCase("true")) { //$NON-NLS-1$
        bDisabled = true;
      }
    }

    StringBuffer sb = new StringBuffer();

    sb.append("<a><href>"); //$NON-NLS-1$

    if (bDisabled) {
      sb.append("#"); //$NON-NLS-1$
    }
    else {
      sb.append(link);
    }
    sb.append("</href>"); //$NON-NLS-1$

    if (!bDisabled) {

      boolean btmp = true;

      // first hidden fields
      for (int i=0; true; i++) {
        String varHid = "varhid" + i; //$NON-NLS-1$
        if (!data.containsKey(varHid)) {
          break;
        }
        String name = data.get(varHid);
        String value = data.get(name);

        sb.append(genArgXml(btmp, name, value));

        btmp = false;
      }

      sb.append(genArgXml(btmp, fieldNumber + FormProps.sMAX_ROW, String.valueOf(maxRow)));
      btmp = false;
      if (row >= 0) {
        sb.append(genArgXml(btmp, var + "_row", String.valueOf(row)));
      }
      
      
      // now link var
      sb.append(genArgXml(btmp, var, "1"));
      
      // now dependency fields
      for (int i=0; true; i++) {
        String varDep = "vardep" + i; //$NON-NLS-1$
        if (!data.containsKey(varDep)) {
          break;
        }
        String name = data.get(varDep);
        
        String valueVar = data.get(varDep + "_" + name); //$NON-NLS-1$
        String value = row < 0 ? procData.getFormatted(valueVar) : procData.getListItemFormatted(valueVar, row);
        
        
        // name in the form of <linkvar>_<vardep name> 
        sb.append(genArgXml(false, var + "_" + name, value));
      }
    }


    sb.append("<text>"); //$NON-NLS-1$
    if (data.containsKey("text")) { //$NON-NLS-1$
      sb.append((String)data.get("text")); //$NON-NLS-1$
    }
    else {
      sb.append("Link"); //$NON-NLS-1$
    }
    sb.append("</text>"); //$NON-NLS-1$


    sb.append("<disabled>"); //$NON-NLS-1$
    if (bDisabled) {
      sb.append("true"); //$NON-NLS-1$
    }
    else {
      sb.append("false"); //$NON-NLS-1$
    }
    sb.append("</disabled>"); //$NON-NLS-1$

    if (data.containsKey("stylesheet")) { //$NON-NLS-1$
      sb.append("<stylesheet>"); //$NON-NLS-1$
      sb.append(data.get("stylesheet")); //$NON-NLS-1$
      sb.append("</stylesheet>"); //$NON-NLS-1$
    }

    sb.append("</a>"); //$NON-NLS-1$


    return sb.toString();
  }
  
  private static String genArgXml(boolean first, String name, String value) {
    StringBuilder sb = new StringBuilder();
    String myValue = value == null ? "" : StringEscapeUtils.escapeXml(value);
    sb.append("<arg><first>").append(first).append("</first>"); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append("<name>").append(name).append("</name>"); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append("<value>").append(myValue).append("</value></arg>"); //$NON-NLS-1$ //$NON-NLS-2$
    return sb.toString();
  }

  public void setLocale(Locale locale) {
  }

  public void formPreProcess(UserInfoInterface userInfo, ProcessData procData, String name, Properties props,
      StringBuilder logBuffer) {
  }

  public String parseAndSet(UserInfoInterface userInfo, ProcessData procData, String name, FormData formData, Properties props,
      boolean ignoreValidation, StringBuilder logBuffer) {

    String user = userInfo.getUtilizador(); 
    
    String linkValue = null;
    if (formData.hasParameter(name)) {
      String depPrefix = name + "_";
      Iterator<String> iterParams = formData.getFileParameters().keySet().iterator();
      while (iterParams.hasNext()) {
        String param = iterParams.next();
        if (StringUtils.isEmpty(param))
          continue;
        
        if (param.startsWith(depPrefix)) {
          // stored var is <link_var>_<vardep_name>
          String depName = depPrefix + param.substring(depPrefix.length());

          try {
            procData.parseAndSet(depName, formData.getParameter(param));
            logBuffer.append("Set '" + depName + "' with '" + formData.getParameter(param) + "';");
          } catch (ParseException e) {
            Logger.error(user, this, "parseAndSet", 
                procData.getSignature() + "error setting link " + name, e);
            return Messages.getString("Link.invalidDepField");
          }
        }
      }

      linkValue = formData.getParameter(name);

      debug(userInfo, "parseAndSet", 
          "link variable=" + name + ", value=" + linkValue);
      if (!StringUtils.equals(linkValue, "1")) {
        Logger.warning(user, this, "parseAndSet", 
            procData.getSignature() + "link variable=" + name
            + ": found one link clicked but it's value is not \"1\"... assuming \"1\"");
        linkValue = "1";
      }
    } else {
      // no link clicked.. store "0" value to indicate that no link was clicked
      linkValue = "0";

      // now reset var deps
      if (hmData != null) {
        Iterator<String> iterDeps = hmData.keySet().iterator();
        while (iterDeps != null && iterDeps.hasNext()) {
          String sKey = iterDeps.next();
          String sValue = hmData.get(sKey);
          if (sKey.startsWith("vardep") || sKey.startsWith("varhid")) {
            String tmpKey = name + "_" + sValue;
            
            debug(userInfo, "parseAndSet", 
                  "link variable=" + name + ": cleaning simple link dep: " + tmpKey);
            procData.clear(tmpKey);
            logBuffer.append("Cleared '" + tmpKey + "';");
          }          
        }
      }
    }
    
    if (linkValue != null) {
      debug(userInfo, "parseAndSet", 
            "SETTING LINK " + name + "=" + linkValue);
      try {
        procData.parseAndSet(name, linkValue);
        logBuffer.append("Set '" + name + "' with '" + linkValue + "';");      
        debug(userInfo, "parseAndSet", "Set '" + name + "' with '" + linkValue + "'");
      } catch (ParseException e) {
        Logger.error(user, this, "parseAndSet", 
            procData.getSignature() + "error setting link " + name, e);
        return Messages.getString("Datatype.invalid_value");
      }
    }
    
    return null;
  }

  public String parseAndSetList(UserInfoInterface userInfo, ProcessData procData, 
      int varIndex, String name, int count, FormData formData,
      Properties props, boolean ignoreValidation, StringBuilder logBuffer) {
 
    String user = userInfo.getUtilizador(); 
    
    String linkValue = null;

    Set<String> paramKeys = formData.getParameters().keySet(); 
    
    String iterName = null; 
    for (int i = 0; i < count; i++) {
      if (FormUtils.checkOutputField(props, varIndex, i)) {
        debug(userInfo, "parseAndSetList", "Row " + i + " of list var '" + name + " is output only... continuing to next row.");
        continue;
      }
      iterName = name + "_" + i;
      if (formData.hasParameter(iterName)) {
        // process other link's vars
        String otherVarPrefix = iterName + "_"; 
        Iterator<String> iterParams = paramKeys.iterator();
        while (iterParams.hasNext()) {
          String formParam = iterParams.next();
          if (StringUtils.isEmpty(formParam))
            continue;
          if (formParam.startsWith(otherVarPrefix)) {
            // stored var is <link_var>_<vardep_name>
            String addonVar = name + "_" + formParam.substring(otherVarPrefix.length());
            String value = formData.getParameter(formParam);
            try {
              procData.parseAndSet(addonVar, value);
              logBuffer.append("Set '" + addonVar + "' with '" + value + "';");
              debug(userInfo, "parseAndSetList", 
                  "set link addon variable=" + addonVar + "=" + value);
            } catch (ParseException e) {
              Logger.error(user, this, "parseAndSetList", 
                  procData.getSignature() + "error setting list link " + name, e);
              return Messages.getString("Link.invalidDepField");
            }
          }
        }
        break;
      }
      iterName = null;
    }
    if (iterName != null) {
      linkValue = formData.getParameter(iterName);
      debug(userInfo, "parseAndSetList", 
            "link list variable " + name + ", value=" + linkValue);
      if (!StringUtils.equals(linkValue, "1")) {
        Logger.warning(user, this, "parseAndSetList", 
            procData.getSignature() + "link list variable " + name
            + ": found one link clicked but it's value is not \"1\"... assuming \"1\"");
        linkValue = "1";
      }

    } else {
      // no link clicked.. store "0" value to indicate that no link was clicked
      linkValue = "0";

      // reset var deps
      if (hmData != null) {
        Iterator<String> iterDeps = hmData.keySet().iterator();
        while (iterDeps != null && iterDeps.hasNext()) {
          String sKey = iterDeps.next();
          String sValue = hmData.get(sKey);
          if (sKey.startsWith("vardep") || sKey.startsWith("varhid")) {
            String tmpKey = name + "_" + sValue;
            
            debug(userInfo, "parseAndSetList", 
                  "link list variable=" + name + ": cleaning multiple link dep: " + tmpKey);
            procData.clear(tmpKey);
            logBuffer.append("Cleared '" + tmpKey + "';");
          }          
        }
      }
    }
    
    if (linkValue != null) {
      try {
        procData.parseAndSet(name, linkValue);
        logBuffer.append("Set '" + name + "' with '" + linkValue + "';");      
        debug(userInfo, "parseAndSetList", "Set '" + name + "' with '" + linkValue + "'");
      } catch (ParseException e) {
        Logger.error(user, this, "parseAndSetList", 
            procData.getSignature() + "error setting list link " + name, e);
        return Messages.getString("Datatype.invalid_value");
      }
    }
    
    return null;
  }

  private void debug(UserInfoInterface userInfo, String method, String message) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, method, message);
    }
  }
}
