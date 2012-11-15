package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.Properties;

import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class Image implements FieldInterface {

  public String getDescription() {
    return "Imagem";
  }

  public void generateHTML(PrintStream out, Properties prop) {
  }

  public void generateXSL(PrintStream out) {
  }

  public void generateXML(PrintStream out, Properties prop) {
  }

  public String getXML(Properties prop) {
    try {
      StringBuffer sb = new StringBuffer();
      String stmp = null;

      sb.append("<field><type>image</type>");
      sb.append("<url>").append(prop.getProperty("url")).append("</url>");

      stmp = prop.getProperty("align");
      if (stmp == null) stmp = "center";
      sb.append("<align>").append(stmp).append("</align>");

      stmp = prop.getProperty("alt_text");
      if (stmp == null) stmp = "";      
      sb.append("<alt_text>").append(stmp).append("</alt_text>");

      stmp = prop.getProperty("width");
      if (stmp == null) stmp = "";      
      sb.append("<width>").append(stmp).append("</width>");

      stmp = prop.getProperty("height");
      if (stmp == null) stmp = "";      
      sb.append("<height>").append(stmp).append("</height>");

      sb.append("<even_field>").append(prop.getProperty("even_field")).append("</even_field>");

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

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    // check if url must be transformed.
    String sImageUrl = props.getProperty("url");

    //if (sImageUrl != null && sImageUrl.indexOf("\"") > -1) {
    if (sImageUrl != null && procData.isTransformable(sImageUrl, false)) {
      // try transformation
      try {
        sImageUrl = procData.transform(userInfo, sImageUrl);
      } catch (EvalException e) {
        Logger.warning(userInfo.getUtilizador(), this, "setup", 
            procData.getSignature() + "exception transforming imageurl " + sImageUrl, e);
      }

      if (sImageUrl != null) {
        // update props
        props.setProperty("url", sImageUrl);
      }
    }
  }

  public boolean isArrayTable() {
    return false;
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
