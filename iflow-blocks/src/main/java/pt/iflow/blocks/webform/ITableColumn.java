package pt.iflow.blocks.webform;

import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public interface ITableColumn {

  public String getTitle(UserInfoInterface userInfo, ProcessData process, Field field);
  
  public abstract void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, int row) throws SAXException;
  
  public abstract void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request, int row);

  public abstract boolean isReadOnly(Field fld);
}
