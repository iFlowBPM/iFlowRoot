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
package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class PopupFormField implements FieldInterface {

  private static final String FIELDID = "fieldid";
  private static final String BUTTON_TEXT = "text";
  private static final String BUTTON_SHOW_CONDITION = "button_show_condition";
  private static final String BUTTON_IMAGE = "button_image";
  private static final String BUTTON_CONFIRM_MESSAGE = "button_confirm_message";
  private static final String BUTTON_CONFIRM_ACTION = "button_confirm_action";
  private static final String BUTTON_TOOLTIP = "button_tooltip";
  private static final String FORM_NAME = "_FORMNAME_";
  public static final String POPUP_FLOW_START_BLOCK_ID ="popupFlowStartBlockId";

  protected static final String TRUE = "true";
  public static final String POP_UP_ID_PREFIX = "btid";
  public static final String POP_UP_NAME_PREFIX = "bfn";

  public static final String WIDTH = "width";
  public static final String HEIGHT = "height";

  private ProcessData procData = null;
  private UserInfoInterface userInfo = null;

  // TODO get them
  private String stmp3 = "";

  public String getDescription() {
    return "PopUp field";
  }

  public void generateHTML(PrintStream out, Properties prop) {
    // TODO
  }

  public void generateXSL(PrintStream out) {
    // TODO
  }

  public void generateXML(PrintStream out, Properties prop) {
    // TODO
  }

  public String getXML(Properties prop) {
    try {
      StringBuffer sb = new StringBuffer();

      String value = prop.getProperty("value");
      String stmp = prop.getProperty("empty_not_allowed");

      if (stmp != null && stmp.equalsIgnoreCase(TRUE)) {
        if (StringUtils.isEmpty(value)) {
          // empty not allowed and value's empty
          return "";
        }
      }

      String id = POP_UP_ID_PREFIX + prop.getProperty(FIELDID);
      String buttonFormName = POP_UP_NAME_PREFIX + prop.getProperty(FIELDID);
      String showCond = prop.getProperty(BUTTON_SHOW_CONDITION);
      String text = prop.getProperty(BUTTON_TEXT); 
      String sFormName = prop.getProperty(FORM_NAME);
      String popupFormBlockStartId = prop.getProperty(POPUP_FLOW_START_BLOCK_ID);
      String hiddenField = null;

      String dimensionsString = "";

      String width = prop.getProperty(PopupFormField.WIDTH);
      if (width == null || "".equals(width)) {
        width = "'800px'";
      } else {
        dimensionsString = "'" + width;
        if (!width.endsWith("px")){
          dimensionsString +="px";
        }
        dimensionsString += "'";
      }

      String height = prop.getProperty(PopupFormField.HEIGHT);
      if (height != null && !"".equals(height)) {
        dimensionsString += ", '"+height;
        if (!height.endsWith("px")){
          dimensionsString +="px";
        }
        dimensionsString += "'";
      }

      // IE button fix
      String sButtonFix = "document.getElementById('" + FormProps.sBUTTON_CLICKED + "').value='" + id + "'; ";

      boolean useIt = true;

      String operation = "";

      if (StringUtils.isNotEmpty(showCond)) {
        useIt = false;
        try {
          useIt = procData.query(userInfo, showCond);
        }
        catch (Exception ei) {
        }
      }

      // OP's
      // 0 - entering page/reload
      // 1 - unused
      // 2 - save
      // 3 - next
      // 4 - cancel
      // 5 - service print
      // 6 - service print field
      // 7 - service export field
      // 8 - only process form
      // 9 - return to parent
      if (useIt) {
        String image = prop.getProperty(BUTTON_IMAGE);
        if (StringUtils.isEmpty(image) && StringUtils.isEmpty(text)) {
          Logger.warning(userInfo.getUtilizador(), this, "generateForm",
              procData.getSignature() + "No text nor image defined for custom button " + id + ".");
        } else {
          operation += "document." + sFormName + ".op.value='3'; " + sButtonFix;
          operation += "document." + sFormName + ".popupStartBlockId.value='" + popupFormBlockStartId + "';";
          operation += "blockPopupCallerForm();";
          operation += "parent.showPopup(getPopupUrlParams()";
          if (dimensionsString != null && !"".equals(dimensionsString)){
            operation += ", "+ dimensionsString ;
          }
          operation += "); return false;";
                // add var to hidden field list
          hiddenField = "popupStartBlockId";
        }
      }

      if (TRUE.equals(prop.getProperty(BUTTON_CONFIRM_ACTION))) {
        String cnfMsg = prop.getProperty(BUTTON_CONFIRM_MESSAGE);
        try {
          cnfMsg = procData.transform(userInfo, cnfMsg);
        } catch (EvalException ex) {
          // maintain as was
        }
        if (StringUtils.isBlank(cnfMsg)) {
          cnfMsg = userInfo.getMessages().getString("blockmsg.default.confirmMessage");
        }
        operation = "if (confirm('" + cnfMsg + "')) { " + operation + " } else { return false; }";
      }

      if (useIt) {
        sb.append("<field><type>popup_field</type>");
        sb.append("<id>").append(id).append("</id>");
        sb.append("<name>").append(buttonFormName).append("</name>");
        sb.append("<text>").append(text).append("</text>");
        sb.append("<operation>").append(operation).append("</operation>");

        // TOOLTIP
        String tooltip = prop.getProperty(BUTTON_TOOLTIP);
        tooltip = StringUtils.isNotEmpty(tooltip) ? tooltip : text; 
        sb.append("<tooltip>").append(tooltip).append("</tooltip>");

        // IMAGE
        String image = prop.getProperty(BUTTON_IMAGE);
        if (StringUtils.isNotEmpty(image)) {
          if (image.indexOf("http://") == -1) {
            if (Const.APP_PORT == -1) {
              image = Const.APP_PROTOCOL + "://" + Const.APP_HOST + stmp3;
            }
            else {
              image = Const.APP_PROTOCOL + "://" + Const.APP_HOST + ":" + Const.APP_PORT + stmp3;
            }
          }
          sb.append("<buttonimage><src>").append(image).append("</src>");
          sb.append("<alt>").append(tooltip).append("</alt>");
          sb.append("</buttonimage>");
        }
        sb.append("</field>");
        if (StringUtils.isNotEmpty(hiddenField)) {
          sb.append("<hidden>");
          sb.append("<name>").append(hiddenField).append("</name>");
          sb.append("<value></value>");
          sb.append("</hidden>");
        }
        sb.append("<hidden><name>iFrameInneHTML</name><value></value></hidden>");
      }

      return sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean isOutputField() {
    return true;
  }

  public boolean isArrayTable() {
    return false;
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    this.procData = procData;
    this.userInfo = userInfo;
  }

  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
