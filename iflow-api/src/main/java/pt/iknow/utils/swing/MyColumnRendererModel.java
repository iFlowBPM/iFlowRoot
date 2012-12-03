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

import javax.swing.table.TableCellRenderer;

public class MyColumnRendererModel {
  private Hashtable<Integer, TableCellRenderer> columnRenderer;
  private Hashtable<Integer, Hashtable<Integer, TableCellRenderer>> cellRenderer;

  public MyColumnRendererModel() {
    columnRenderer = new Hashtable<Integer, TableCellRenderer>();
    cellRenderer = new Hashtable<Integer, Hashtable<Integer,TableCellRenderer>>();
  }

  public void addRendererForColumn(int col, TableCellRenderer e) {
    columnRenderer.put(col, e);
  }

  public void addRendererForCell(int row, int col, TableCellRenderer e) {
    Hashtable<Integer, TableCellRenderer> cc;
    if(cellRenderer.containsKey(row)) {
      cc = cellRenderer.get(row);
    } else {
      cc = new Hashtable<Integer, TableCellRenderer>();
      cellRenderer.put(row, cc);
    }
    cc.put(col, e);
  }

  public void removeRendererForColumn(int col) {
    columnRenderer.remove(col);
  }

  public TableCellRenderer getRenderer(int row, int col) {
    if(cellRenderer.containsKey(row)) {
      Hashtable<Integer, TableCellRenderer> cc = cellRenderer.get(row);
      if(cc.containsKey(col)) return cc.get(col);
    }
    
    return columnRenderer.get(col);
  }
}
