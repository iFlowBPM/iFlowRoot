package pt.iknow.utils.swing;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import pt.iflow.api.processtype.DataTypeEnum;
import pt.iknow.floweditor.Formats;

public class FormatsCellEditor extends DefaultCellEditor {
  private static final long serialVersionUID = -7638172271545514543L;
  JComboBox combo;
//  FormatsComboBoxModel model;
  int typeCol;
  
  public FormatsCellEditor(int typeCol) {
    super(new JComboBox(new FormatsComboBoxModel()));
    combo = (JComboBox) getComponent();
    combo.setEditable(true);
//    model = (FormatsComboBoxModel) combo.getModel();
    this.typeCol = typeCol;
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    // update Combo box
    DataTypeEnum dataType = (DataTypeEnum) table.getValueAt(row, typeCol);
    Formats formats = Formats.getFormats(dataType);
    combo.removeAllItems();
    if(null == formats) {
      combo.setEnabled(false);
    } else {
      combo.setEnabled(true);
      combo.addItem("");
      for(Formats.Format fmt : formats.getFormats())
        combo.addItem(fmt);
    }
    
    // invoke...
    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
  }
}
