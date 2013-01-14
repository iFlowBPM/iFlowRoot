package pt.iknow.utils.swing;

import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class MyJTableX extends JTable {
  private static final long serialVersionUID = -6167229331252058689L;

  protected MyColumnEditorModel cem;
  
  protected MyColumnRendererModel crm;


  public MyJTableX() {
    super();
    init(null);
  }

  public MyJTableX(TableModel tm) {
    super(tm);
    init(null);
  }

  public MyJTableX(TableModel tm, TableColumnModel cm) {
    super(tm, cm);
    init(null);
  }

  public MyJTableX(TableModel tm, TableColumnModel cm, ListSelectionModel sm) {
    super(tm, cm, sm);
    init(null);
  }

  public MyJTableX(int rows, int cols) {
    super(rows, cols);
    init(null);
  }

  public MyJTableX(final Vector<?> rowData, final Vector<?> columnNames) {
    super(rowData, columnNames);
    init(null);
  }

  public MyJTableX(final Object[][] rowData, final Object[] colNames) {
    super(rowData, colNames);
    init(null);
  }

  public MyJTableX(TableModel tm, MyColumnEditorModel cm) {
    super(tm, null, null);
    init(cm);
  }

  private void init(MyColumnEditorModel cm) {
    this.cem = cm;
  }
  
  public void setMyColumnEditorModel(MyColumnEditorModel cm) {
    this.cem = cm;
  }

  public MyColumnEditorModel getMyColumnEditorModel() {
    return cem;
  }

  public TableCellEditor getCellEditor(int row, int col) {
    TableCellEditor tmpEditor = null;
    if (cem != null)
      tmpEditor = cem.getEditor(row, col);
    if (tmpEditor != null)
      return tmpEditor;
    return super.getCellEditor(row, col);
  }
  
  
  public void setMyColumnRendererModel(MyColumnRendererModel crm) {
    this.crm = crm;
  }

  public MyColumnRendererModel getMyColumnRendererModel() {
    return crm;
  }

  public TableCellRenderer getCellRenderer(int row, int col) {
    TableCellRenderer tmpRenderer = null;
    if (crm != null)
      tmpRenderer = crm.getRenderer(row, col);
    if (tmpRenderer != null)
      return tmpRenderer;
    return super.getCellRenderer(row, col);
  }

  public void processEnter() {
  }
  
  public void tableChanged(TableModelEvent ev) {
    super.tableChanged(ev);

    try {
      scrollToBottom((MyTableModel)getModel());
    } catch (ClassCastException ex){
      // ignore exception
    }
}

  public void scrollToBottom(MyTableModel model) {
    if(!model.isScrollToBottom()) return;
    int rowToGo = this.getRowCount() - 1;
    this.getSelectionModel().setSelectionInterval(rowToGo, rowToGo);
    Rectangle visibleRect = this.getVisibleRect();
    int centerY = visibleRect.y + visibleRect.height / 2;
    Rectangle cellRectangle = this.getCellRect(rowToGo, 0, true);
    if (centerY < cellRectangle.y) {
      // need to scroll UP
      cellRectangle.y = cellRectangle.y - visibleRect.y + centerY;
    } else {
      // need to scroll DOWN
      cellRectangle.y = cellRectangle.y + visibleRect.y - centerY;
    }
    this.scrollRectToVisible(cellRectangle);
  }

  
  public void setModel(TableModel m) {
    super.setModel(m);
    if(m instanceof MyTableModel) {
      ((MyTableModel)m).initMyJTableX(this);
    }
  }

  public void stopEditing() {
    CellEditor currentEditor = getCellEditor();
    if(null != currentEditor) currentEditor.stopCellEditing();
  }
  
  public String getStringAt(int row, int column) {
    return (String) getValueAt(row, column);
  }
}
