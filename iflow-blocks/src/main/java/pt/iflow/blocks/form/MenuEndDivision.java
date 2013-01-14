package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class MenuEndDivision implements FieldInterface {

  private String previous ="";
  
  public String getDescription() {
    return "Fim de Divisao por menus";
  }

  public void generateHTML(PrintStream out, Properties prop) {
  }

  public void generateXSL(PrintStream out) {
  }

  public void generateXML(PrintStream out, Properties prop) {
  }

  public String getXMLfirst(Properties prop) {
    return previous+"</field>";
  }

  public String getXML(Properties prop) {
      
      if(prop.getProperty("first").equals("0")) 
          return getXMLfirst(prop);
  
    return previous+"</menu>";
  }
  
  public boolean isOutputField() {
    return true;
  }

  public boolean isArrayTable() {
    return false;
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    previous = props.getProperty("close_previous");
    if(null == previous) previous = "";
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
