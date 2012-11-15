package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class FormTemplate implements FieldInterface {

  public String getDescription() {
    return "Form Template";
  }

  public void generateHTML(PrintStream out, Properties prop) {
    try {
      out.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
      out.println("  <tr>");
      out
          .println("    <td height=\"20\" bgcolor=\"#4b6e98\" bordercolorlight=\"#4b6e98\" bordercolordark=\"#ffffff\"><div align=\"center\" class=\"v10bBRAnd\">"
              + prop.getProperty(FormProps.FORM_TEMPLATE) + "</div>");
      out.println("    </td>");
      out.println("  </tr>");
      out.println("</table>");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateXSL(PrintStream out) {
    try {
      out.println("<!-- HEADER -->");
      out.println("    <xsl:value-of select=\"text/text()\"/>");
      out.println("<!-- HEADER -->");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateXML(PrintStream out, Properties prop) {
    try {
      out.print("xmlStr = xmlStr + \"<field><type>header</type>\"");
      out.print("+ \"<form_template>" + prop.getProperty(FormProps.FORM_TEMPLATE) + "</form_template>\"");
      out.println("+ \"</field>\";");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getXML(Properties prop) {
    try {
      return "<field><type>header</type><form_template>" + prop.getProperty(FormProps.FORM_TEMPLATE)
          + "</form_template><edition_cond>" + prop.getProperty("edition_cond") + "</edition_cond>" + "<disable_cond>"
          + prop.getProperty("disable_cond") + "</disable_cond></field>";
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
    String text = props.getProperty("text");
    if (StringUtils.isNotEmpty(text))
      props.put("text", FormUtils.getTransformedText(userInfo, procData, text));
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
