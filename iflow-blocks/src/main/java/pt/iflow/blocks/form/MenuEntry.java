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
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class MenuEntry implements FieldInterface {

  private static final String FIELDID = "fieldid";
  private static final String BUTTON_TEXT = "text";
  private static final String BUTTON_SHOW_CONDITION = "button_show_condition";
  private static final String BUTTON_IMAGE = "button_image";
  private static final String BUTTON_CONFIRM_MESSAGE = "button_confirm_message";
  private static final String BUTTON_CUSTOM_VALUE = "button_custom_value";
  private static final String BUTTON_CUSTOM_VARIABLE = "button_custom_variable";
  private static final String BUTTON_CONFIRM_ACTION = "button_confirm_action";
  private static final String BUTTON_TYPE = "button_type";
  private static final String BUTTON_TOOLTIP = "button_tooltip";
  private static final String FORM_NAME = "_FORMNAME_";
  
  protected static final String CANCEL = "_cancelar";
  protected static final String RESET = "_repor";
  protected static final String SAVE = "_guardar";
  protected static final String PRINT = "_imprimir";
  protected static final String NEXT = "_avancar";
  protected static final String CUSTOM = "_custom";
  protected static final String RETURN_PARENT = "_retornar_parent";

  protected static final String TRUE = "true";

  private ProcessData procData = null;
  private UserInfoInterface userInfo = null;

  // TODO get them
  private boolean bPrint = false;
  private String stmp2 = "???";
  private String stmp3 = "";

  public String getDescription() {
    return "Entrada de Menu";
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

      String id = "btid" + prop.getProperty(FIELDID);
      String buttonFormName = "bfn" + prop.getProperty(FIELDID);
      String showCond = prop.getProperty(BUTTON_SHOW_CONDITION);
      String type = prop.getProperty(BUTTON_TYPE);
      String text = prop.getProperty(BUTTON_TEXT); 
      String sFormName = prop.getProperty(FORM_NAME);
      String hiddenField = null;

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
      if (CANCEL.equals(type)) {
        String procNotInCreator = procData.getAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR);
        if (StringUtils.isEmpty(procNotInCreator) && procData.isInDB()) {
          operation = "if (confirm('Deseja cancelar/fechar o processo?')) { disableForm(); document." +
          sFormName + ".op.value=4; } else { return false; }";
        }
        else {
          useIt = false;
        }
      } else if (RESET.equals(type)) {
        operation = "disableForm(); document." + sFormName + ".op.value=0;";
      } else if (SAVE.equals(type)) {
        if (userInfo.isGuest()) {
          useIt = false;
        }
        else {
          operation = "disableForm(); document." + sFormName + ".op.value=2;";
        }
      } else if (PRINT.equals(type)) {
        useIt = bPrint;
        operation = sButtonFix + "PrintService(null);";
      } else if (NEXT.equals(type)) {
        operation = "if (CheckEmptyFields()) { disableForm(); document." + sFormName + 
        ".op.value='3'; " + sButtonFix + "} else { return false; }";
      } else if (RETURN_PARENT.equals(type)) {
        //TODO CENA PÓ DETALHE
        //ProcessData pdLocal = abBlock.nTYPE == nTYPE_DETALHE && procData2 != null ? procData2 : procData;
        ProcessData pdLocal = procData;
        String switchRet = pdLocal.getTempData(Const.sSWITCH_PROC_RETURN_PARENT);
        useIt = StringUtils.equals(switchRet, TRUE);
        if (useIt) {
          String showCond2 = prop.getProperty(BUTTON_SHOW_CONDITION);
          if (StringUtils.isNotEmpty(showCond2)) {
            useIt = false;
            try {
              useIt = pdLocal.query(userInfo, stmp2);
            }
            catch (Exception ei) {
              Logger.error(userInfo.getUtilizador(), this, "generateForm",
                  procData.getSignature() + "caught exception evaluation beanshell condition <cond>"
                  + showCond2 + "</cond> (assuming false): " + ei.getMessage());
            }
          }

          operation = "document." + sFormName + ".op.value=9;";
        }
      } else if (CUSTOM.equals(type)) {
        if (useIt) {
          String image = prop.getProperty(BUTTON_IMAGE);
          if (StringUtils.isEmpty(image) && StringUtils.isEmpty(text)) {
            Logger.warning(userInfo.getUtilizador(), this, "generateForm",
                procData.getSignature() + "No text nor image defined for custom button " + id + ".");
          }
          else {                
            operation = "if (CheckEmptyFields()) { disableForm(); document." + sFormName + 
            ".op.value='3'; " + sButtonFix + "} else { return false; }";

            String customVar = prop.getProperty(BUTTON_CUSTOM_VARIABLE);
            if (StringUtils.isNotEmpty(customVar)) {
              String customValue = prop.getProperty(BUTTON_CUSTOM_VALUE);
              if (StringUtils.isNotEmpty(customValue)) {
                operation += "document." + sFormName + "." + customVar + 
                ".value='" + customValue + "';";
                // add var to hidden field list
                hiddenField = customVar;
              }
            }
          }
        }            
      } else {
        Logger.warning(userInfo.getUtilizador(), this, "generateForm",
            procData.getSignature() + "button type " + type + " not supported!!");              
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
        sb.append("<menu>");
        sb.append("<type>menuentry</type>");
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
        sb.append("</menu>");
        if (StringUtils.isNotEmpty(hiddenField)) {
          sb.append("<hidden>").append(hiddenField).append("</hidden>");
        }
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
