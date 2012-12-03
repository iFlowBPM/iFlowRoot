/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
