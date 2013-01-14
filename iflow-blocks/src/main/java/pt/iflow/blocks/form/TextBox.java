package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Properties;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class TextBox implements FieldInterface {

  public String getDescription() {
    return "Caixa de Texto";
  }

  public void generateHTML(PrintStream out, Properties prop) {
    try {
      Class<?> cl = Class.forName(prop.getProperty("datatype"));
      Object ins = cl.newInstance();
      Method mt = cl.getMethod("getFormSuffix");
      String formSuffix = (String) mt.invoke(ins);
      mt = cl.getMethod("getPrimitiveTypeMethod");
      String primitiveTypeMethod = (String) mt.invoke(ins);

      out.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
      out.println("  <tr>");
      out.println("    <td class=\"txtVERM\"><div align=\"left\">&nbsp;" + prop.getProperty("text") + "&nbsp;</div></td>");
      out.println("    <td class=\"medium_normal\"><div align=\"right\">");
      out.println("<input type=\"text\"" +
		  " name=\"" + prop.getProperty("variable") + "\"" +
		  " value=\"<%= " + prop.getProperty("datatype") +
		  ".formatForm(uc.get" + primitiveTypeMethod +
		  "(\"" + prop.getProperty("variable") + "\")) %>\"" +
		  " size=\"" + prop.getProperty("size") + "\"" +
		  " maxlength=\"" + prop.getProperty("maxlength") + "\"" +
		  " class=\"caixasTexto\">" +
		  "<font class=\"caixasTexto\">" + formSuffix + "</font>");
      out.println("&nbsp;</div></td>");
      out.println("  </tr>");
      out.println("</table>");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void generateXSL(PrintStream out) {
    try {
      out.println("<!-- TEXTBOX -->");
      out.println("    <xsl:value-of select=\"text/text()\"/>");
      out.println("    <input type=\"text\"" +
		  " name=\"{variable}\"" +
		  " value=\"{value}\"" +
		  " size=\"{size}\"" +
		  " maxlength=\"{maxlength}\"/>" +
		  "<xsl:value-of select=\"suffix/text()\"/>");
      out.println("<!-- TEXTBOX -->");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateXML(PrintStream out, Properties prop) {
    try {
      Class<?> cl = Class.forName(prop.getProperty("datatype"));
      Object ins = cl.newInstance();
      Method mt = cl.getMethod("getFormSuffix");
      String formSuffix = (String) mt.invoke(ins);
      mt = cl.getMethod("getPrimitiveTypeMethod");
      String primitiveTypeMethod = (String) mt.invoke(ins);
      out.print("xmlStr = xmlStr + \"<field><type>textbox</type>\"");
      out.print("+ \"<text>" + prop.getProperty("text") + "</text>\"");
      out.print("+ \"<variable>" + prop.getProperty("variable") + "</variable>\"");
      out.print("+ \"<value>\" + " + prop.getProperty("datatype") +
                ".formatForm(uc.get" + primitiveTypeMethod +
                "(\"" + prop.getProperty("variable") + "\")) + \"</value>\"");
      out.print("+ \"<size>" + prop.getProperty("size") + "</size>\"");
      out.print("+ \"<maxlength>" + prop.getProperty("maxlength") + "</maxlength>\"");
      out.print("+ \"<suffix>" + formSuffix + "</suffix>\"");
      out.println("+ \"</field>\";");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getXML(Properties prop) {
    try {
      StringBuffer sb = new StringBuffer();

      String stmp = null;

      sb.append("<field><type>textbox</type>");
      sb.append("<text>").append(prop.getProperty("text")).append("</text>");
      sb.append("<variable>").append(prop.getProperty("variable")).append("</variable>");
      sb.append("<value>").append(prop.getProperty("value")).append("</value>");
      sb.append("<size>").append(prop.getProperty("size")).append("</size>");
      sb.append("<maxlength>").append(prop.getProperty("maxlength")).append("</maxlength>");
      sb.append("<obligatory>").append(prop.getProperty(FormProps.sOBLIGATORY_PROP)).append("</obligatory>");
      stmp = prop.getProperty("suffix");
      if (stmp == null) stmp = "";
      sb.append("<suffix>").append(stmp).append("</suffix>");
      stmp = prop.getProperty("output_only");
      if (stmp != null && stmp.equalsIgnoreCase("true")) stmp = "true";
      else stmp = "false";
      sb.append("<disabled>").append(stmp).append("</disabled>");
      stmp = prop.getProperty("onchange");
      if (stmp == null) stmp = "";
      sb.append("<onchange>").append(stmp).append("</onchange>");
      
      sb.append(FormUtils.genTextFieldSubmitOnChangeXml(prop.getProperty("variable"), prop.getProperty(FormProps.TEXT_SUBMIT_ON_BLUR), prop.getProperty(FormProps.FORM_NAME)));
      
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
