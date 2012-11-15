package pt.iflow.blocks.webform;

import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

/**
 * Space widgets:
 *  - separator (horizontal rule)
 *  - filler (vertical white space)
 * 
 * @author ombl
 *
 */
public abstract class Separators extends AbstractWidget {
  private final String type;
  
  private Separators(String type) {
    this.type = type;
  }

  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Generating a Separator token. Type: "+type);
    ch.startElement("field");
    ch.addElement("type", type);
    ch.addElement("size", field.getProperties().get("size"));
    ch.addElement("even_field", String.valueOf(even));
    ch.endElement("field");
  }


  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
  }

  public static class Filler extends Separators {
    public Filler() {
      super("filler");
    }
  }

  public static class HorizontalRule extends Separators {
    public HorizontalRule() {
      super("separator");
    }
  }

}
