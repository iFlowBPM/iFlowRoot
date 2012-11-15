package pt.iknow.utils.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class MyTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

  /** Default serialVersionUID. */
  private static final long serialVersionUID = 1L;

  private Color cellColor;

  public MyTableCellRenderer(Color color) {
    this.cellColor = color;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
    setBackground(cellColor);
    super.getTableCellRendererComponent(table, value, selected, focused, row, column);
    return this;
  }
}
