package pt.iknow.utils.swing;

import java.util.Hashtable;

import javax.swing.table.TableCellRenderer;

public class MyColumnRendererModel {
  private Hashtable<Integer, TableCellRenderer> columnRenderer;
  private Hashtable<Integer, Hashtable<Integer, TableCellRenderer>> cellRenderer;

  public MyColumnRendererModel() {
    columnRenderer = new Hashtable<Integer, TableCellRenderer>();
    cellRenderer = new Hashtable<Integer, Hashtable<Integer,TableCellRenderer>>();
  }

  public void addRendererForColumn(int col, TableCellRenderer e) {
    columnRenderer.put(col, e);
  }

  public void addRendererForCell(int row, int col, TableCellRenderer e) {
    Hashtable<Integer, TableCellRenderer> cc;
    if(cellRenderer.containsKey(row)) {
      cc = cellRenderer.get(row);
    } else {
      cc = new Hashtable<Integer, TableCellRenderer>();
      cellRenderer.put(row, cc);
    }
    cc.put(col, e);
  }

  public void removeRendererForColumn(int col) {
    columnRenderer.remove(col);
  }

  public TableCellRenderer getRenderer(int row, int col) {
    if(cellRenderer.containsKey(row)) {
      Hashtable<Integer, TableCellRenderer> cc = cellRenderer.get(row);
      if(cc.containsKey(col)) return cc.get(col);
    }
    
    return columnRenderer.get(col);
  }
}
