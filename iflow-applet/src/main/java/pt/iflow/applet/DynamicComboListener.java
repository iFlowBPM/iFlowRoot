package pt.iflow.applet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class DynamicComboListener implements ActionListener {
  private final JComboBox combo;
  private final DynamicField field;

  public DynamicComboListener(JComboBox combo, DynamicField field) {
    this.combo = combo;
    this.field = field;
  }

  public void actionPerformed(ActionEvent e) {
    field.setValue(combo.getSelectedItem());
  }
}
