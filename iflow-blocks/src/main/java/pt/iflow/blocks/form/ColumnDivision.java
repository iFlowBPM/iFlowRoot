package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class ColumnDivision implements FieldInterface {

  public String getDescription() {
    return "Divisao por colunas";
  }

  public void generateHTML(PrintStream out, Properties prop) {
    try {
      out.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
      out.println("  <tr>");
      out.println("    <td height=\"20\" bgcolor=\"#4b6e98\" bordercolorlight=\"#4b6e98\" bordercolordark=\"#ffffff\"><div align=\"center\" class=\"v10bBRAnd\">" +  prop.getProperty("text") + "</div>");
      out.println("    </td>");
      out.println("  </tr>");
      out.println("</table>");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateXSL(PrintStream out) {
    try {
      out.println("<!-- TEXTMESSAGE -->");
      out.println("    <xsl:value-of select=\"text/text()\"/>");
      out.println("<!-- TEXTMESSAGE -->");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateXML(PrintStream out, Properties prop) {
    try {
      out.print("xmlStr = xmlStr + \"<field><type>textmessage</type>\"");
      out.print("+ \"<text>" + prop.getProperty("text") + "</text>\"");
      out.println("+ \"</field>\";");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getXML(Properties prop) {
    try {
      StringBuffer sb = new StringBuffer("</columndivision><columndivision");

      if (prop.containsKey("col_width")) {
        try {
          String width = prop.getProperty("col_width");
          int w = Integer.parseInt(width);
          if (0 <= w && w <= 100) {
            sb.append(" width=\"").append(width).append("\"");
          }          
        }
        catch (Exception e) {          
        }
      }
      sb.append(">");
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
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
