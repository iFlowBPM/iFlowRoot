package pt.iknow.utils.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.util.Arrays;

import javax.swing.JTable;

public class SortFilterModel extends MyTableModel {
  private static final long serialVersionUID = 3122064829273806915L;

  private int sortColumn;
  
  private int unsortableIndexRange;

  private Row[] rows;

  public SortFilterModel(String[] colNames, Object[][] d) {
    this(colNames, d, -1);
  }
  
  public SortFilterModel(String[] colNames, Object[][] d, int unsortableIndexRange) {
    super(colNames, d);
    updateRows();
    this.unsortableIndexRange = unsortableIndexRange;
  }

  public Object[][] insertRow() {
    Object[][] d = super.insertRow();
    int rowCount = getRowCount();
    updateRows();
    fireTableRowsInserted(rowCount - 1, rowCount);
    return d;
  }

  public Object[][] removeRow(int rowNumber) {
    Object[][] d = super.removeRow(rowNumber);
    updateRows();
    fireTableRowsDeleted(rowNumber, rowNumber);
    return d;
  }

  public void updateRows() {
    rows = new Row[getRowCount()];
    for (int i = 0; i < rows.length; i++) {
      rows[i] = new Row();
      rows[i].index = i;
    }
  }

  public void sort(int c) {
    sortColumn = c;
    int startIndex = unsortableIndexRange;
    if (startIndex < 0 || startIndex >= rows.length) {
      startIndex = 0;
    }
    Row[] tmpRows = new Row[rows.length - startIndex];
    int index = 0;
    for (int i = startIndex; i < rows.length; i++) {
      tmpRows[index] = rows[i];
      index++;
    }
    Arrays.sort(tmpRows);
    index = 0;
    for (int i = startIndex; i < rows.length; i++) {
      rows[i] = tmpRows[index];
      index++;
    }
    fireTableDataChanged();
  }

  public void initMyJTableX(MyJTableX table) {
    super.initMyJTableX(table);
    addMouseListener(table);
  }

  public void addMouseListener(final JTable table) {
    table.getTableHeader().addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent event) { // check for double
        // click
        if (event.getClickCount() < 2)
          return;

        // find column of click and
        int tableColumn = table.columnAtPoint(event.getPoint());

        // translate to table model index and sort
        int modelColumn = table.convertColumnIndexToModel(tableColumn);
        sort(modelColumn);
      }
    });
  }

  /*
   * compute the moved row for the three methods that access model elements
   */

  public Object getValueAt(int r, int c) {
    return super.getValueAt(rows[r].index, c);
  }

  public boolean isCellEditable(int r, int c) {
    return super.isCellEditable(rows[r].index, c);
  }

  public void setValueAt(Object aValue, int r, int c) {
    super.setValueAt(aValue, rows[r].index, c);
  }

  /**
   * this inner class holds the index of the model row Rows are compared by
   * looking at the model row entries in the sort column
   */

  private class Row implements Comparable<Row> {
    public int index;

    public int compareTo(Row other) {
      return doCompare(this, other);
    }
  }

  /**
   * Compare two rows or current row and another Row.
   * 
   * 
   * 
   * @param current Current row
   * @param other Object to compare
   * @return 
   */
  @SuppressWarnings("unchecked")
  private int doCompare(Row current, Row other) {
    Object a = super.getValueAt(current.index, sortColumn);
    Object b = super.getValueAt(other.index, sortColumn);
    if (a instanceof String && b instanceof String)
      return Collator.getInstance().compare(a, b); // cast to string
    // internally
    else if (a instanceof Comparable)
      return ((Comparable) a).compareTo(b);
    else
      return current.index - other.index;
  }

}
