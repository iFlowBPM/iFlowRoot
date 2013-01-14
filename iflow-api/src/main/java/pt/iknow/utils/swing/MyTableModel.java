package pt.iknow.utils.swing;

import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel {
  private static final long serialVersionUID = 1L;

  private Object[][] data;
  private String[] cols;
  private Object[] defaultValues;
  private boolean scrollToBottom;

  // column editables (default true)
  private Hashtable<Integer, Boolean> columnEditable;
  private Hashtable<Integer, Hashtable<Integer, Boolean>> cellEditable;

  public MyTableModel(String[] colNames, Object[][] d) {
    if(colNames == null || colNames.length == 0) {
      throw new IllegalArgumentException("Invalid column names array.");
    }
    this.data = d;
    this.cols = colNames;
    this.scrollToBottom = true;
    this.columnEditable = new Hashtable<Integer, Boolean>();
    this.cellEditable = new Hashtable<Integer, Hashtable<Integer,Boolean>>();
  }

  public Object [][] getData() {
    return this.data;
  }
  
  public String getColumnName(int col) {
    return cols[col];
  }

  public int getRowCount() {
    return data.length;
  }

  public int getColumnCount() {
    return cols.length;
  }
  
  public Class<?> getColumnClass(int column) {
    Class<?> retObj = Object.class;
    if(data.length > 0 && column < data[0].length && getValueAt(0, column) != null) {
      retObj = getValueAt(0, column).getClass();
    }
    if (retObj == null) {
      retObj = Object.class;
    }
    return retObj;
  }

  public Object getValueAt(int row, int col) {
    return data[row][col];
  }

  public boolean isCellEditable(int row, int col) {
    if(cellEditable.containsKey(row)) {
      Hashtable<Integer, Boolean> cc = cellEditable.get(row);
      if(cc.containsKey(col)) {
        return cc.get(col);
      }
    }
    
    if(columnEditable.containsKey(col)) return columnEditable.get(col);

    return true;
  }

  public void setColumnEditable(int col, boolean e) {
    columnEditable.put(col, e);
  }

  public void setCellEditable(int row, int col, boolean e) {
    Hashtable<Integer, Boolean> cc;
    if(cellEditable.containsKey(row)) {
      cc = cellEditable.get(row);
    } else {
      cc = new Hashtable<Integer, Boolean>();
      cellEditable.put(row, cc);
    }
    cc.put(col, e);
  }


  public void setValueAt(Object value, int row, int col) {
    data[row][col] = value;
  }

  public Object[][] insertRow() {
    int rowToInsert = getRowCount();
    Object _data[][] = new Object[rowToInsert + 1][cols.length];

    // copy the old values
    for (int i = 0; i < rowToInsert; i++) {
      for (int j = 0; j < cols.length; j++) {
        _data[i][j] = data[i][j];
      }
    }

    for (int j = 0; j < cols.length; j++) {
      if(null != defaultValues)
        _data[rowToInsert][j] = defaultValues[j];
      else 
        _data[rowToInsert][j] = "";
    }

    data = _data;

    int rowCount = getRowCount();
    fireTableRowsInserted(rowCount - 1, rowCount);

    return data;
  }
  
  public void setScrollToBottom(boolean scroll) {
    this.scrollToBottom = scroll;
  }

  public boolean isScrollToBottom() {
    return this.scrollToBottom;
  }


  public Object[][] removeRow(int rowNumber) {
    int tableSize = getRowCount();

    // create a new data[][] structure
    Object _data[][] = new Object[tableSize - 1][cols.length];

    // copy the old values
    for (int i = 0; i < rowNumber; i++) {
      for (int j = 0; j < cols.length; j++) {
        _data[i][j] = data[i][j];
      }
    }

    for (int i = rowNumber + 1; i < tableSize; i++) {
      for (int j = 0; j < cols.length; j++) {
        _data[i - 1][j] = data[i][j];
      }
    }

    data = _data;

    fireTableRowsDeleted(rowNumber, rowNumber);

    return data;
  }
  
  public void initMyJTableX(MyJTableX table) {}
  
  public void setDefaultValuesForRow(Object [] defaultValues) {
    if(defaultValues != null && defaultValues.length != cols.length)
      throw new IllegalArgumentException("Default values array does not have the same size as columns array");
    this.defaultValues = defaultValues;
  }
}
