package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class TextArea implements FieldInterface {

  public String getDescription() {
    return "Área de Texto";
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

      sb.append("<field><type>textarea</type>");
      sb.append("<text>").append(prop.getProperty("text")).append("</text>");
      sb.append("<variable>").append(prop.getProperty("variable")).append("</variable>");
      sb.append("<value>").append(prop.getProperty("value")).append("</value>");
      
      String cols = prop.getProperty("cols");
      if (StringUtils.endsWith(cols, "%")) {
        sb.append("<width>").append(cols).append("</width>");        
      }
      else {
        sb.append("<cols>").append(cols).append("</cols>");
      }
      
      sb.append("<rows>").append(prop.getProperty("rows")).append("</rows>");
      sb.append("<obligatory>").append(prop.getProperty(FormProps.sOBLIGATORY_PROP)).append("</obligatory>");
      stmp = prop.getProperty("suffix");
      if (stmp == null) stmp = "";
      sb.append("<suffix>").append(stmp).append("</suffix>");
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
  }
}
