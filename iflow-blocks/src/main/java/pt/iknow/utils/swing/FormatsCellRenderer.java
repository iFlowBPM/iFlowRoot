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

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import pt.iflow.api.processtype.DataTypeEnum;
import pt.iknow.floweditor.Formats;

public class FormatsCellRenderer extends DefaultTableCellRenderer {
  private static final long serialVersionUID = -7251162395477568127L;
  
  private int typeCol;
  public FormatsCellRenderer(int typeCol) {
    this.typeCol = typeCol;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    DataTypeEnum dataType = (DataTypeEnum) table.getValueAt(row, typeCol);
    Formats formats = Formats.getFormats(dataType);
    // mask styles...
    if(null != formats) {
      for(Formats.Format fmt : formats.getFormats()) {
        if(fmt.equals(value)) { // format can equals to a string
          value = fmt;
          break;
        }
      }
    }
    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }

}
