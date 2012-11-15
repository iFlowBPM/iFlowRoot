package pt.iknow.utils.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {

  /** Default serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  private Color cellColor;
  private Boolean cellEnabled;

  public CheckBoxCellRenderer(Color color, Boolean enabled) {
    this.cellColor = color;
    this.cellEnabled = enabled;
    setHorizontalAlignment(JLabel.CENTER);
  }
  
  public CheckBoxCellRenderer() {
    this(null, true);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      //super.setBackground(table.getSelectionBackground());
      setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      if(cellColor != null) {
        setBackground(cellColor);
      } else {
        setBackground(table.getBackground());
      }
    }
    setSelected((value != null && ((Boolean) value).booleanValue()));
    setEnabled(cellEnabled);
    return this;
  }
}
