package pt.iflow.applet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.toedter.calendar.JDateChooser;

public class DynamicDateChangeListener implements PropertyChangeListener {
  private final JDateChooser dateChooser;
  private final DynamicField field;

  public DynamicDateChangeListener(JDateChooser dateChooser, DynamicField field) {
    this.dateChooser = dateChooser;
    this.field = field;
  }

  public void propertyChange(PropertyChangeEvent evt) {
    field.setValue(dateChooser.getDate());
  }
  
}
