package pt.iflow.blocks.webform;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class Link extends AbstractWidget {

  
  // Adapt and evolve new interface properties
  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Generating a Link");
    Map<String,String> prop = field.getProperties();

    boolean bDisabled = false;
    String sOutputOnly = prop.get("output_only");
    bDisabled = (sOutputOnly != null && sOutputOnly.equalsIgnoreCase("true"));

    ch.startElement("field");
    ch.addElement("type", "link");
    ch.addElement("text", prop.get("text"));

    String cssClass = prop.get("css_class");
    if(null == cssClass) cssClass="";
    ch.addElement("cssclass", cssClass);

    String onclick = prop.get("onclick");
    if(null == onclick) onclick="";
    ch.addElement("onclick", onclick);

    String onmouseover = prop.get("onmouse_over_status");
    if(null == onmouseover) onmouseover="";
    ch.addElement("onmouseover", onmouseover);

    String align = prop.get("align");
    if(null == align) align="";
    ch.addElement("align", align);

    String href = prop.get("url");
    if(null == href) href="";
    ch.addElement("href", href);

    String sNewWindow = prop.get("popup");
    String sWindowName = "";
    if(StringUtils.isEmpty(sNewWindow)) sNewWindow = "false";
    if(StringUtils.equalsIgnoreCase(sNewWindow, "true")) {
      sWindowName = prop.get("window_name");
      if(null == sWindowName) sWindowName = "";
    }
    ch.addElement("newwindow", sNewWindow);
    ch.addElement("newwindowname", sWindowName);

    ch.addElement("disabled", String.valueOf(bDisabled));
    ch.addElement("even_field", String.valueOf(even));
    ch.endElement("field");
  }

  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    
    // TODO clicked var...
    
  }

}
