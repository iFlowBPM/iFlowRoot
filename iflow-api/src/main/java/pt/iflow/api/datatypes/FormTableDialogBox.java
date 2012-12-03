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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormService;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.datatypes.msg.Messages;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessVariableValue;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

/**
 * FormTableDialogBox.
 * 
 * @author lcabral
 * @since 05/03/2010
 * @version 08/03/2010
 */
public class FormTableDialogBox implements DataTypeInterface, ArrayTableProcessingCapable {
  
  private Mode _sDialogType = Mode.parse(null); // get default
  private int _sDialogWidth = 300;
  private int _sDialogHeight = 50;
  
  public FormTableDialogBox() {
  }

  public void init(UserInfoInterface userInfo, ProcessData procData, Map<String, String> extraProps, Map<String, Object> deps) {
    if (extraProps != null) {
      try {
        if (extraProps.containsKey("mode")) { //$NON-NLS-1$
          _sDialogType = Mode.parse((String) extraProps.get("mode")); //$NON-NLS-1$
        }
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "init", 
            procData.getSignature() + "exception caught", e);
      }
      try {
        if (extraProps.containsKey("width")) { //$NON-NLS-1$
          _sDialogWidth = java.lang.Integer.valueOf((String) extraProps.get("width")); //$NON-NLS-1$
        }
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "init", 
            procData.getSignature() + "exception caught", e);
      }
      try {
        if (extraProps.containsKey("height")) { //$NON-NLS-1$
          _sDialogHeight = java.lang.Integer.valueOf((String) extraProps.get("height")); //$NON-NLS-1$
        }
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "init", 
            procData.getSignature() + "exception caught", e);
      }
    }
  }

  public int getID() {
    return 15;
  }

  public String getDescription() {
    return Messages.getString("FormTableDialogBox.description"); //$NON-NLS-1$
  }

  public String getShortDescription() {
    return Messages.getString("FormTableDialogBox.short_description"); //$NON-NLS-1$
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

  public String validateFormData(Object input) {
    return validateFormData(input, null);
  }

  public String validateFormData(Object input, Object[] aoaArgs) {
    return null;
  }

  public String format(UserInfoInterface userInfo, ProcessData procData, FormService service, int fieldNumber, String name,
      ProcessVariableValue value, Properties props, ServletUtils response) {
    return formatRow(userInfo, procData, service, fieldNumber, -1, name, -1, value, props, response);
  }

  public String formatRow(UserInfoInterface userInfo, ProcessData procData, FormService service, int fieldNumber, int varIndex,
      String name, int row, ProcessVariableValue value, Properties props, ServletUtils response) {
    return genXml(userInfo, (value != null ? value.format() : ""), (row < 0 ? name : FormUtils.getListKey(name, row)),
        _sDialogType, _sDialogWidth, _sDialogHeight);
  }

  @Deprecated
  public String formatToHtml(Object input) {
    return formatToHtml(input, null);
  }

  @Deprecated
  public String formatToHtml(Object input, Object[] aoaArgs) {
    return null;
  }

  public String formatToForm(Object input) {
    return formatToForm(input, null);
  }

  public String formatToForm(Object input, Object[] aoaArgs) {
    return formatToHtml(input, aoaArgs);
  }

  public String getText(Object input) {
    return value(input);
  }

  public double getValue(Object input) {
    return java.lang.Double.NaN;
  }

  public static String value(Object s) {
    return (String) s;
  }

  public static String genXml(UserInfoInterface userInfo, String asJSONValue, String panelid, Mode mode, int width, int height) {
//    String login = userInfo == null ? null : userInfo.getUtilizador();
    StringBuffer sb = new StringBuffer();
    DialogBoxConfig dbc = DialogBoxConfig.parse(asJSONValue);
    if (StringUtils.isNotBlank(asJSONValue) && !dbc.disabled) {
      String typeText = dbc.getTypeText(userInfo);
      String title = dbc.title == null ? "" : dbc.title;
      String message = dbc.message == null ? "" : dbc.message;
      if (userInfo != null) {
         if (userInfo.getMessages().hasKey(title)) {
           title = userInfo.getMessages().getString(title); 
         }
         if (userInfo.getMessages().hasKey(message)) {
           message = userInfo.getMessages().getString(message); 
         }
      }
      
      panelid = panelid.replace("[", "_").replace("]", "");
      String container = "container_" + panelid;
      String eventType, event;
      switch (mode) {
      case Dialog:
        eventType = "click";
        event = MessageFormat.format(
            "showPopUp(''{0}'',''{1}'',''{2}'',''{3}'',{4},{5});",
            new Object[] { container, panelid, title, message, width, height });
        break;
      case ToolTip:
        eventType = "hover";
        event = title + "::" + message;
        break;
      case Alert:
      default:
        if (StringUtils.isEmpty(title)) {
          title = "null";
        }
        else {
          title = "'" + StringEscapeUtils.escapeXml(title) + "'";
        }
        
        if (StringUtils.isEmpty(typeText)) {
          typeText = "null";
        }
        else {
          typeText = "'" + StringEscapeUtils.escapeXml(typeText) + "'";
        }        
        
        message = "'" + StringEscapeUtils.escapeXml(message) + "'";
        eventType = "alert";
        event = "showAlert(" + message + ", " + typeText + ", " + title + ");";
      }
      
      sb.append("<input><type>tabledialog</type>"); //$NON-NLS-1$
      sb.append("<icon>").append(dbc.type.getIcon()).append("</icon>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<typeText>").append(dbc.getTypeText(userInfo)).append("</typeText>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<variable>").append(container).append("</variable>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<eventType>").append(eventType).append("</eventType>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("<event>").append(event).append("</event>"); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("</input>"); //$NON-NLS-1$
    }
    return sb.toString();
  }

  public void setLocale(Locale locale) {
  }

  public void formPreProcess(UserInfoInterface userInfo, ProcessData procData, String name, Properties props,
      StringBuilder logBuffer) {
  }

  public String parseAndSet(UserInfoInterface userInfo, ProcessData procData, String name, FormData formData, Properties props,
      boolean ignoreValidation, StringBuilder logBuffer) {
    return null;
  }

  public String parseAndSetList(UserInfoInterface userInfo, ProcessData procData, int varIndex, String name, int count,
      FormData formData, Properties props, boolean ignoreValidation, StringBuilder logBuffer) {
    return null;
  }

  private enum Mode {
    ToolTip,
    Dialog,
    Alert;
    public static Mode parse(String type) {
      if (StringUtils.isNotBlank(type)) {
        for (Mode t : values()) {
          if (StringUtils.equalsIgnoreCase(type, t.name())) {
            return t;
          }
        }
      }
      return Alert;
    }
  };
}
