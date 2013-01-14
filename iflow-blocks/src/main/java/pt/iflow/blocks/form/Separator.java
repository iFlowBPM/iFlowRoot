package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class Separator implements FieldInterface {

  public String getDescription() {
    return "";
  }

  public void generateHTML(PrintStream out, Properties prop) {
    try {
      out.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#e3e3e3\">");
      out.println("  <tr>");
      out.println("    <td width=\"80%\"><hr></td>");
      out.println("  </tr>");
      out.println("</table>");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateXSL(PrintStream out) {
    try {
      out.println("<!-- SEPARATOR -->");
      out.println("    <hr/>");
      out.println("<!-- SEPARATOR-->");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateXML(PrintStream out, Properties prop) {
    try {
      out.println("xmlStr = xmlStr + \"<field><type>separator</type></field>\";");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getXML(Properties prop) {
    try {
      return "<field><type>separator</type><even_field>" 
	+ prop.getProperty("even_field") 
	+ "</even_field></field>";
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
