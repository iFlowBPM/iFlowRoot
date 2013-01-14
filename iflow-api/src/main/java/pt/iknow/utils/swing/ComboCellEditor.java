package pt.iknow.utils.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class ComboCellEditor extends DefaultCellEditor implements ActionListener {
  private static final long serialVersionUID = -7638172271545517709L;

  public ComboCellEditor(Object[] values) {
    super(new JComboBox(values));
    // setClickCountToStart(2);
  }

  public ComboCellEditor(Object[] values, boolean editable) {
    this(injectEmpty(values));
    JComboBox jcb = ((JComboBox)getComponent());
    jcb.setEditable(editable);
    if(editable) {
      // register a special change listener to keep previous typed value
      jcb.addActionListener(this);
      
    }
  }

  public void actionPerformed(ActionEvent e) {
    JComboBox jcb = ((JComboBox)getComponent());
    int idx = jcb.getSelectedIndex();
    if(idx < 0) { // user typed this entry
      Object obj = jcb.getSelectedItem();
      final int pos = 0;
      jcb.removeItemAt(pos);
      jcb.insertItemAt(obj, pos);
      jcb.setSelectedIndex(pos);
    }
  }
  
  private static Object [] injectEmpty(Object[] values) {
    if(null == values) return values;
    Object[] newValues = new Object[values.length+1];
    System.arraycopy(values, 0, newValues, 1, values.length);
    newValues[0] = ""; // Empty string or another?
    return newValues;
  }
}
