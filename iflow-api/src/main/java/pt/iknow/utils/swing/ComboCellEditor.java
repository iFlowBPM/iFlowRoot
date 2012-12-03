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
