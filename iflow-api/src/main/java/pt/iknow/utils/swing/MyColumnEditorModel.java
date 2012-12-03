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

import java.util.Hashtable;

import javax.swing.table.TableCellEditor;

public class MyColumnEditorModel {
  private Hashtable<Integer, TableCellEditor> columnEditor;
  private Hashtable<Integer, Hashtable<Integer, TableCellEditor>> cellEditor;

  public MyColumnEditorModel() {
    columnEditor = new Hashtable<Integer, TableCellEditor>();
    cellEditor = new Hashtable<Integer, Hashtable<Integer,TableCellEditor>>();
  }

  public void addEditorForColumn(int col, TableCellEditor e) {
    columnEditor.put(col, e);
  }

  public void addEditorForCell(int row, int col, TableCellEditor e) {
    Hashtable<Integer, TableCellEditor> cc;
    if(cellEditor.containsKey(row)) {
      cc = cellEditor.get(row);
    } else {
      cc = new Hashtable<Integer, TableCellEditor>();
      cellEditor.put(row, cc);
    }
    cc.put(col, e);
  }

  public void removeEditorForColumn(int col) {
    columnEditor.remove(col);
  }

  public TableCellEditor getEditor(int row, int col) {
    if(cellEditor.containsKey(row)) {
      Hashtable<Integer, TableCellEditor> cc = cellEditor.get(row);
      if(cc.containsKey(col)) return cc.get(col);
    }
    
    return columnEditor.get(col);
  }
}
