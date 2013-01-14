package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class TabDivision implements FieldInterface {

  private String previous = "";
  private String id = "";
  
  public String getDescription() {
    return "Divisao por tabs";
  }

  public void generateHTML(PrintStream out, Properties prop) {
  }

  public void generateXSL(PrintStream out) {
  }

  public void generateXML(PrintStream out, Properties prop) {
  }

  public String getXML(Properties prop) {
    return previous+"<tabdivision><id>"+id+"</id>";
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

    id = props.getProperty("fieldid");
    if (null == id)
      id = "";
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
