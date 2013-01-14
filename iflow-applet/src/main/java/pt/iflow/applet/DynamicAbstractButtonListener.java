package pt.iflow.applet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

public class DynamicAbstractButtonListener implements ActionListener {
  private final AbstractButton button;
  private final DynamicField field;

  public DynamicAbstractButtonListener(AbstractButton button, DynamicField field) {
    this.button = button;
    this.field = field;
  }

  public void actionPerformed(ActionEvent e) {
    field.setValue(button.isSelected());
  }
}
