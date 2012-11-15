package pt.iknow.utils.swing;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import pt.iflow.api.processtype.DataTypeEnum;
import pt.iknow.floweditor.Formats;

public class FormatsCellRenderer extends DefaultTableCellRenderer {
  private static final long serialVersionUID = -7251162395477568127L;
  
  private int typeCol;
  public FormatsCellRenderer(int typeCol) {
    this.typeCol = typeCol;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    DataTypeEnum dataType = (DataTypeEnum) table.getValueAt(row, typeCol);
    Formats formats = Formats.getFormats(dataType);
    // mask styles...
    if(null != formats) {
      for(Formats.Format fmt : formats.getFormats()) {
        if(fmt.equals(value)) { // format can equals to a string
          value = fmt;
          break;
        }
      }
    }
    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }

}
