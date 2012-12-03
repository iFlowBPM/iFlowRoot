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
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class DateCal implements FieldInterface {

  public String getDescription() {
    return "Caixa de Data";
  }

  public void generateHTML(PrintStream out, Properties prop) {
  }
  public void generateXSL(PrintStream out) {
  }

  public void generateXML(PrintStream out, Properties prop) {
  }

  public String getXML(Properties prop) {
    try {
      StringBuffer sb = new StringBuffer();

      String stmp = null;

      sb.append("<field><type>datecal</type>");
      sb.append("<text>").append(prop.getProperty("text")).append("</text>");
      sb.append("<variable>").append(prop.getProperty("variable")).append("</variable>");
      String sValue = prop.getProperty("value");
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
      int size = sDateFormat.length();
//      try {
//      	int aSize = Integer.parseInt(prop.getProperty("size"));
//      	if (aSize > size) {
//      		size = aSize;
//      	}
//      } catch (NumberFormatException ex) {
//      }
      
      sb.append("<value>").append(sValue).append("</value>");
      sb.append("<size>").append(size).append("</size>");
      sb.append("<maxlength>").append(prop.getProperty("maxlength")).append("</maxlength>");
      sb.append("<formname>").append(prop.getProperty("formname")).append("</formname>");
      sb.append("<dateformat>").append(FormUtils.formatJavascriptDateFormat(sDateFormat)).append("</dateformat>");
      sb.append("<dateformattext>").append(sDateFormat).append("</dateformattext>");
      sb.append("<dateformatid>").append(prop.getProperty("date_format_id")).append("</dateformatid>");
      
      sb.append("<hour_format_id>");
      int hourId = 0;
      if (prop.containsKey(FormProps.HOUR_FORMAT_ID)) {
      	try {
      		hourId = Integer.parseInt(prop.getProperty(FormProps.HOUR_FORMAT_ID));
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
      
      stmp = prop.getProperty("output_only");
      if (stmp != null && stmp.equalsIgnoreCase("true")) stmp = "true";
      else stmp = "false";
      sb.append("<disabled>").append(stmp).append("</disabled>");
      sb.append("<even_field>").append(prop.getProperty("even_field")).append("</even_field>");
      sb.append("</field>");

      return sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean isOutputField() {
    return false;
  }

  public boolean isArrayTable() {
    return false;
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
  }

  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {

    ProcessSimpleVariable var = procData.get(name);
    if (var == null || var.getValue() == null) {
      // check if it's configured to use current date
      String currDateProp = props.getProperty("currdate_ifempty");
      if (StringUtils.equalsIgnoreCase(currDateProp, "true")) {
        Date dt = new Date();
        procData.set(name, dt);
        Logger.debug(userInfo.getUtilizador(), this, "initVariable", name + " initialized to " + dt);
      }
    }
  }
}
