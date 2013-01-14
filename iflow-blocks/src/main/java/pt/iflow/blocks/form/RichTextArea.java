package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class RichTextArea implements FieldInterface {

  public static final String WIDTH = "width";
  public static final String HEIGHT = "height";

  public String getDescription() {
    return "Área de Texto rich text";
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
      String value = prop.getProperty("value");
      if (value == null){
        value = "";
      }

      String dimensionsString = "";

      String width = prop.getProperty(RichTextArea.WIDTH);
      if (width == null || "".equals(width)) {
        width = "'600px'";
      } else {
        dimensionsString = "'" + width;
        if (!width.endsWith("px")){
          dimensionsString +="px";
        }
        dimensionsString += "'";
      }

      String height = prop.getProperty(RichTextArea.HEIGHT);
      if (height != null && !"".equals(height)) {
        dimensionsString += ", '"+height;
        if (!height.endsWith("px")){
          dimensionsString +="px";
        }
        dimensionsString += "'";
      }

      String title = prop.getProperty("text");
      if (title == null || "".equals(title)){
        title = "";
      }

      sb.append("<field><type>rich_textarea</type>");
      sb.append("<text>").append(prop.getProperty("text")).append("</text>");
      sb.append("<variable>").append(prop.getProperty("variable")).append("</variable>");
      sb.append("<value>").append(value).append("</value>");
      sb.append("<formatted_value>").append(replaceLeftBar(value)).append("</formatted_value>");

      String cols = prop.getProperty("cols");
      if (StringUtils.endsWith(cols, "%")) {
        sb.append("<width>").append(cols).append("</width>");
      } else {
        sb.append("<cols>").append(cols).append("</cols>");
      }

      sb.append("<rows>").append(prop.getProperty("rows")).append("</rows>");
      sb.append("<obligatory>").append(prop.getProperty(FormProps.sOBLIGATORY_PROP)).append("</obligatory>");
      stmp = prop.getProperty("suffix");
      if (stmp == null) {
        stmp = "";
      }
      sb.append("<suffix>").append(stmp).append("</suffix>");
      stmp = prop.getProperty("output_only");
      if (stmp != null && stmp.equalsIgnoreCase("true")) {
        stmp = "true";
      } else {
        stmp = "false";
      }
      sb.append("<disabled>").append(stmp).append("</disabled>");

      sb.append("<even_field>").append(prop.getProperty("even_field")).append("</even_field>");

      sb.append("<is_rich_text_area>").append("true").append("</is_rich_text_area>");

      StringBuffer richTextAreaScript = new StringBuffer();
      richTextAreaScript.append("window.addEvent(");
      richTextAreaScript.append("'domready', InicializeRichTextField('").append(prop.getProperty("variable")).append("'");
      richTextAreaScript.append(", '").append(title).append("'");
      if (dimensionsString != null && !"".equals(dimensionsString)){
        richTextAreaScript.append(", ").append(dimensionsString);
      }
      richTextAreaScript.append(")");
      richTextAreaScript.append(");");
      sb.append("<rich_text_area_script>").append(richTextAreaScript.toString()).append("</rich_text_area_script>");

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

  private String replaceLeftBar(String textAreaValue) {
    StringBuffer result = new StringBuffer();
    if(textAreaValue != null && textAreaValue.length() > 0){
      result.append(textAreaValue.replaceAll("\\r\\n", ""));
    } else {
      return "";
    }
    return result.toString();
  }
}