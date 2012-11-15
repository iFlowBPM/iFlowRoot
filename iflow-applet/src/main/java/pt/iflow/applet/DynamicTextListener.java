package pt.iflow.applet;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class DynamicTextListener implements DocumentListener, TextListener {
  private final JTextComponent jTextComponent;
  private final TextComponent textComponent;
  private final DynamicField field;

  public DynamicTextListener(TextComponent textComponent, DynamicField field) {
    this(textComponent, null, field);
  }

  public DynamicTextListener(JTextComponent jTextComponent, DynamicField field) {
    this(null, jTextComponent, field);
  }

  private DynamicTextListener(TextComponent textComponent, JTextComponent jTextComponent, DynamicField field) {
    this.textComponent = textComponent;
    this.jTextComponent = jTextComponent;
    this.field = field;
  }

  public void changedUpdate(DocumentEvent e) {
    setValue();
  }

  public void insertUpdate(DocumentEvent e) {
    setValue();
  }

  public void removeUpdate(DocumentEvent e) {
    setValue();
  }
  
  public void textValueChanged(TextEvent e) {
    setValue();
  }
  
  
  private void setValue() {
    if(null != this.textComponent)
      this.field.setValue(this.textComponent.getText());
    else
      this.field.setValue(this.jTextComponent.getText());
  }
}
