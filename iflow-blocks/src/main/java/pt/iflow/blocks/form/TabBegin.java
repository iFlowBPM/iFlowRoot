package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class TabBegin implements FieldInterface {

  private String text = "";

  private String action = "";
  
  private String id = "";

  private String display = "none";
  
  private String previous = "";
  
  public String getDescription() {
    return "Inicio de tab";
  }

  public void generateHTML(PrintStream out, Properties prop) {
  }

  public void generateXSL(PrintStream out) {
  }

  public void generateXML(PrintStream out, Properties prop) {
  }

  public String getXML(Properties prop) {
    StringBuffer sb = new StringBuffer();
    sb.append(previous).append("<tab><name>");
    sb.append(text).append("</name><action>").append(action).append("</action><id>").append(id).append("</id><display>").append(display).append("</display>");
    sb.append("<blockdivision><columndivision>");
    return sb.toString();
  }

  public boolean isOutputField() {
    return true;
  }

  public boolean isArrayTable() {
    return false;
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {

    text = props.getProperty("text");
    if (null == text)
      text = "";
    action = props.getProperty("onclick");
    if (null == action)
      action = "";
    id = props.getProperty("fieldid");
    if (null == id)
      id = "";
    display = props.getProperty("display");
    if(null == display) display = "none";
    
    previous = props.getProperty("close_previous");
    if(null == previous) previous = "";
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
