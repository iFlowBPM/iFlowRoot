package pt.iflow.api.blocks.form;

import org.xml.sax.SAXException;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public interface IWidget {
  
  public abstract void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException;
  
  public abstract void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request);
  
}
