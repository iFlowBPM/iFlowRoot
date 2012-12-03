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
