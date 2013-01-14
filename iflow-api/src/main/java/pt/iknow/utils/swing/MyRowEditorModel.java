package pt.iknow.utils.swing;

import java.util.Hashtable;

import javax.swing.table.TableCellEditor;

/**
 * 
 * @author oscar
 *@deprecated use MyColumnEditorModel
 */
@Deprecated
public class MyRowEditorModel {
  private Hashtable<Integer, TableCellEditor> data;

  public MyRowEditorModel() {
    data = new Hashtable<Integer, TableCellEditor>();
  }

  public void addEditorForRow(int row, TableCellEditor e) {
    data.put(row, e);
  }

  public void removeEditorForRow(int row) {
    data.remove(row);
  }

  public TableCellEditor getEditor(int row) {
    return data.get(row);
  }
}
