package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.axis.utils.StringUtils;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class TextLabel implements FieldInterface {

  public String getDescription() {
    return "Saída de Texto";
  }

  public void generateHTML(PrintStream out, Properties prop) {
    try {
      Class<?> cl = Class.forName(prop.getProperty("datatype"));
      Object ins = cl.newInstance();
      Method mt = cl.getMethod("getPrimitiveTypeMethod");
      String primitiveTypeMethod = (String) mt.invoke(ins);

      out.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
      out.println("  <tr>");
      out.println("    <td class=\"txtVERM\"><div align=\"left\">&nbsp;" + prop.getProperty("text") + "&nbsp;</div></td>");
      out.println("    <td class=\"txt\"><div align=\"right\"><%= " + prop.getProperty("datatype") +
		  ".formatPrint(uc.get" + primitiveTypeMethod + "(\"" + prop.getProperty("variable") +
		  "\")) %>&nbsp;</div></td>");
      out.println("  </tr>");
      out.println("</table>");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateXSL(PrintStream out) {
    try {
      out.println("<!-- TEXTLABEL -->");
      out.println("    <xsl:value-of select=\"text/text()\"/>");
      out.println("    <xsl:value-of select=\"value/text()\"/>");
      out.println("<!-- TEXTLABEL -->");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateXML(PrintStream out, Properties prop) {
    try {
      Class<?> cl = Class.forName(prop.getProperty("datatype"));
      Object ins = cl.newInstance();
      Method mt = cl.getMethod("getPrimitiveTypeMethod");
      String primitiveTypeMethod = (String) mt.invoke(ins);

      out.print("xmlStr = xmlStr + \"<field><type>textlabel</type>");
      out.print("<text>" + prop.getProperty("text") + "</text>");
      out.print("<value>\" + " + prop.getProperty("datatype") +
                ".formatPrint(uc.get" + primitiveTypeMethod + "(\"" + prop.getProperty("variable") +
                "\")) + \"</value>");
      out.println("</field>\";");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getXML(Properties prop) {
    try {
      StringBuffer sb = new StringBuffer();

      String value = prop.getProperty("value");
      String stmp = prop.getProperty("empty_not_allowed");
      
      if (stmp != null && stmp.equalsIgnoreCase("true")) {
        if (StringUtils.isEmpty(value)) {
          // empty not allowed and value's empty
          return "";
        }
      }

      sb.append("<field><type>textlabel</type>");
      sb.append("<text>").append(prop.getProperty("text")).append("</text>");
      sb.append("<value>").append(value).append("</value>");
      
      sb.append("<even_field>").append(prop.getProperty("even_field")).append("</even_field>");

      String prefix = prop.getProperty("prefix");
      String suffix = prop.getProperty("suffix");
      
      sb.append("<prefix>").append(prefix != null ? prefix : "").append("</prefix>");
      sb.append("<suffix>").append(suffix != null ? suffix : "").append("</suffix>");

      sb.append("</field>");
      
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
