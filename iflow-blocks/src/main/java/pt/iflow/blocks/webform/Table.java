package pt.iflow.blocks.webform;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class Table extends AbstractWidget {

  public void generate(UserInfoInterface userInfo, ProcessData process, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Generating a Table");
    
    List<Field> fldCols = field.getFields();

    int tableLength = -1;
    List<Field> fields = new ArrayList<Field>(fldCols.size());
    List<ITableColumn> columns = new ArrayList<ITableColumn>(fldCols.size());
    ProcessVariable procVar = new ProcessVariable();
    for(Field col : fldCols) {
      ITableColumn wgt = procVar.getWidget(col.getProperties().get("input"));
      if(null == wgt) continue;
      Logger.info(userInfo.getUtilizador(), this, "generate", "Found column with ID: "+col.getId());
      columns.add(wgt);
      fields.add(col);
      
      ProcessListVariable var = process.getList(col.getVariable());
      int colLength = 0;
      if(null != var) colLength = var.size();
      if(colLength > tableLength)
        tableLength = colLength;
    }
    
    
    String title = field.getProperties().get("title");
    int maxSize = -1;
    try {
      maxSize = Integer.parseInt(field.getProperties().get("max_row")); // TODO somehow this is different
    } catch (Throwable t) {
    }
    
    if(maxSize > 0) tableLength = maxSize;
    
    ch.startElement("field");
    ch.addElement("type", "arraytable");
    ch.addElement("even_field", String.valueOf(even));
    
    if(StringUtils.isNotEmpty(title)) {
      ch.startElement("row")
        .startElement("col")
        .addElement("colspan", String.valueOf(columns.size()))
        .addElement("align", "center")
        .addElement("header", "true")
        .addElement("value", title)
        .endElement("col")
        .endElement("row");
    }
    
    ch.startElement("row");
    for(int i = 0; i < columns.size(); i++) {
      ITableColumn col = columns.get(i);
      Field fldCol = fields.get(i);
      String colTitle = col.getTitle(userInfo, process, fldCol);
      
      ch.startElement("col")
        .addElement("align", "center")
        .addElement("header", "true")
        .addElement("value", colTitle)
        .endElement("col");
    }
    ch.endElement("row");
    
    for(int row = 0; row < tableLength; row++) {
      ch.startElement("row");
      for(int i = 0; i < columns.size(); i++) {
        ITableColumn col = columns.get(i);
        Field fldCol = fields.get(i);
        String align = fldCol.getProperties().get("tblalign");
        
        ch.startElement("col")
          .addElement("align", align)
          .addElement("header", "false");
        ch.startElement("value");
        col.generate(userInfo, process, ch, fldCol, row);
        ch.endElement("value");
        ch.endElement("col");
      }
      ch.endElement("row");
    }
    ch.endElement("field");
    ch.addHiddenField("ROW_COUNT_"+field.getId(), String.valueOf(tableLength));
  }

  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
    List<Field> fldCols = field.getFields();

    int tableLength = -1;
    List<Field> fields = new ArrayList<Field>(fldCols.size());
    List<ITableColumn> columns = new ArrayList<ITableColumn>(fldCols.size());
    ProcessVariable procVar = new ProcessVariable();
    for(Field col : fldCols) {
      ITableColumn wgt = procVar.getWidget(col.getProperties().get("input"));
      if(null == wgt) continue;
      Logger.info(userInfo.getUtilizador(), this, "process", "Found column with ID: "+col.getId());
      columns.add(wgt);
      fields.add(col);
      ProcessListVariable var = process.getList(col.getVariable());
      int colLength = 0;
      if(null != var) colLength = var.size();
      if(colLength > tableLength)
        tableLength = colLength;
    }
    String sMaxLength = request.getParameter("ROW_COUNT_"+field.getId());
    int maxLength = -1;
    try {
      maxLength = Integer.parseInt(sMaxLength);
    } catch (Throwable t) {}
    
    if(maxLength <= 0) return;
    
    // clear dataset
    for(int i = 0; i < fields.size(); i++) {
      ITableColumn col = columns.get(i);
      Field fld = fields.get(i);
      if(col.isReadOnly(fld)) continue;
      process.setList(new ProcessListVariable(process.getVariableDataType(fld.getVariable()),fld.getVariable()));
    }
    
    // read values
    for(int row = 0; row < maxLength; row++) {
      for(int i = 0; i < columns.size(); i++) {
        ITableColumn col = columns.get(i);
        Field fld = fields.get(i);
        if(col.isReadOnly(fld)) continue;
        col.process(userInfo, process, fld, request, row);
      }
    }
    
    
  }
  
}
