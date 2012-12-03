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

import org.apache.commons.lang.StringEscapeUtils;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class Link implements FieldInterface {

  public String getDescription() {
    return "Link";
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

      String stmp = null;
      String stmp2 = null;

      boolean bDisabled = false;
      stmp = prop.getProperty("output_only");
      if (stmp != null && stmp.equalsIgnoreCase("true")) bDisabled = true;

      sb.append("<field><type>link</type>");
      sb.append("<text>").append(prop.getProperty("text")).append("</text>");

      stmp = prop.getProperty("css_class");
      if (stmp == null) stmp = "";
      sb.append("<cssclass>").append(stmp).append("</cssclass>");

      stmp = prop.getProperty("onclick");
      if (stmp == null) stmp = "";
      sb.append("<onclick>").append(stmp).append("</onclick>");

      stmp = prop.getProperty("onmouse_over_status");
      if (stmp == null || stmp.equals("")) stmp = "center";
      sb.append("<onmouseover>").append(stmp).append("</onmouseover>");

      stmp = prop.getProperty("align");
      if (stmp == null) stmp = "";
      sb.append("<align>").append(stmp).append("</align>");

      stmp = prop.getProperty("href");
      if (stmp == null) return ""; // if no href, abort...
      sb.append("<href>").append(StringEscapeUtils.escapeXml(stmp)).append("</href>");


      stmp = prop.getProperty("open_new_window");
      if (stmp != null && stmp.equalsIgnoreCase("true")) {
	stmp = "true";
	stmp2 = prop.getProperty("window_name");
	if (stmp2 == null) stmp2 = "";
      }
      else {
	stmp = "false";
	stmp2 = "";
      }      
      sb.append("<newwindow>").append(stmp).append("</newwindow>");
      sb.append("<newwindowname>").append(stmp2).append("</newwindowname>");


      if (bDisabled) stmp = "true";
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
  }
}
