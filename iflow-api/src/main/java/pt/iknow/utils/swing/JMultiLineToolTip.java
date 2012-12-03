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

import javax.swing.JComponent;
import javax.swing.JToolTip;

/**
 * JMultiLineToolTip (retrieved from
 * http://www.codeguru.com/java/articles/122.shtml)
 * 
 * @author Zafir Anjum
 */

public class JMultiLineToolTip extends JToolTip {
  private static final long serialVersionUID = -143175965584977353L;

  String tipText;
  JComponent component;
  
  public JMultiLineToolTip() {
    updateUI();
  }

  public void updateUI() {
    setUI(MultiLineToolTipUI.createUI(this));
  }

  public void setColumns(int columns) {
    this.columns = columns;
    this.fixedwidth = 0;
  }

  public int getColumns() {
    return columns;
  }

  public void setFixedWidth(int width) {
    this.fixedwidth = width;
    this.columns = 0;
  }

  public int getFixedWidth() {
    return fixedwidth;
  }

  protected int columns = 0;
  protected int fixedwidth = 0;
}

