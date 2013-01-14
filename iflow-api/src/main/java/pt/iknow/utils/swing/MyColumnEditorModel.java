package pt.iknow.utils.swing;

import java.util.Hashtable;

import javax.swing.table.TableCellEditor;

public class MyColumnEditorModel {
  private Hashtable<Integer, TableCellEditor> columnEditor;
  private Hashtable<Integer, Hashtable<Integer, TableCellEditor>> cellEditor;

  public MyColumnEditorModel() {
    columnEditor = new Hashtable<Integer, TableCellEditor>();
    cellEditor = new Hashtable<Integer, Hashtable<Integer,TableCellEditor>>();
  }

  public void addEditorForColumn(int col, TableCellEditor e) {
    columnEditor.put(col, e);
  }

  public void addEditorForCell(int row, int col, TableCellEditor e) {
    Hashtable<Integer, TableCellEditor> cc;
    if(cellEditor.containsKey(row)) {
      cc = cellEditor.get(row);
    } else {
      cc = new Hashtable<Integer, TableCellEditor>();
      cellEditor.put(row, cc);
    }
    cc.put(col, e);
  }

  public void removeEditorForColumn(int col) {
    columnEditor.remove(col);
  }

  public TableCellEditor getEditor(int row, int col) {
    if(cellEditor.containsKey(row)) {
      Hashtable<Integer, TableCellEditor> cc = cellEditor.get(row);
      if(cc.containsKey(col)) return cc.get(col);
    }
    
    return columnEditor.get(col);
  }
}
