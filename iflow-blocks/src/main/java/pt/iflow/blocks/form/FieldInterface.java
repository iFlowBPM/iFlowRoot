package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public interface FieldInterface {

  public String getDescription();
  /**
   * 
   * @deprecated DO NOT USE THIS METHOD
   * @param out
   * @param prop
   */
  public void generateHTML(PrintStream out, Properties prop);
  /**
   * 
   * @deprecated DO NOT USE THIS METHOD
   * @param out
   */
  public void generateXSL(PrintStream out);
  /**
   * 
   * @deprecated DO NOT USE THIS METHOD
   * @param out
   * @param prop
   */
  public void generateXML(PrintStream out, Properties prop);
  public String getXML(Properties prop);
  public boolean isOutputField();
  
  /**
   * Initializes private data for this field interface. The main objective of this method is to remove
   * all occurences of <br>
   * <code>
   * if(stmp3.equals("pt.iflow.blocks.form.FieldClass")) {<br>
   *   // Do some stuff... <br>
   * }<br>
   *  </code>
   * @param userInfo User invoking the current form field
   * @param procData Process data
   * @param props Properties to store internal data. (Maybe should be removed in the future)
   */
  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response);
  
  public boolean isArrayTable();

  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props);
}
