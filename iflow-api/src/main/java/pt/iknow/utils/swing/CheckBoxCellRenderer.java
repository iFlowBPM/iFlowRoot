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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {

  /** Default serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  private Color cellColor;
  private Boolean cellEnabled;

  public CheckBoxCellRenderer(Color color, Boolean enabled) {
    this.cellColor = color;
    this.cellEnabled = enabled;
    setHorizontalAlignment(JLabel.CENTER);
  }
  
  public CheckBoxCellRenderer() {
    this(null, true);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      //super.setBackground(table.getSelectionBackground());
      setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      if(cellColor != null) {
        setBackground(cellColor);
      } else {
        setBackground(table.getBackground());
      }
    }
    setSelected((value != null && ((Boolean) value).booleanValue()));
    setEnabled(cellEnabled);
    return this;
  }
}
