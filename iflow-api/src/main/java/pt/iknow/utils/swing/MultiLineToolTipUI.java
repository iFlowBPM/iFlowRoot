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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

class MultiLineToolTipUI extends BasicToolTipUI {
  static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();
  Font smallFont;
  static JToolTip tip;
  protected CellRendererPane rendererPane;

  private static JTextArea textArea;

  public static ComponentUI createUI(JComponent c) {
    return sharedInstance;
  }

  public MultiLineToolTipUI() {
    super();
  }

  public void installUI(JComponent c) {
    super.installUI(c);
    tip = (JToolTip) c;
    rendererPane = new CellRendererPane();
    c.add(rendererPane);
  }

  public void uninstallUI(JComponent c) {
    super.uninstallUI(c);

    c.remove(rendererPane);
    rendererPane = null;
  }

  public void paint(Graphics g, JComponent c) {
    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Dimension size = c.getSize();
    textArea.setBackground(c.getBackground());
    rendererPane.paintComponent(g, textArea, c, 1, 1, size.width - 1, size.height - 1, true);
  }

  public Dimension getPreferredSize(JComponent c) {
    String tipText = ((JToolTip) c).getTipText();
    if (tipText == null)
      return new Dimension(0, 0);
    textArea = new JTextArea(tipText);
    rendererPane.removeAll();
    rendererPane.add(textArea);
    textArea.setWrapStyleWord(true);
    int width = ((JMultiLineToolTip) c).getFixedWidth();
    int columns = ((JMultiLineToolTip) c).getColumns();

    if (columns > 0) {
      textArea.setColumns(columns);
      textArea.setSize(0, 0);
      textArea.setLineWrap(true);
      textArea.setSize(textArea.getPreferredSize());
    } else if (width > 0) {
      textArea.setLineWrap(true);
      Dimension d = textArea.getPreferredSize();
      d.width = width;
      d.height++;
      textArea.setSize(d);
    } else
      textArea.setLineWrap(false);

    Dimension dim = textArea.getPreferredSize();

    dim.height += 1;
    dim.width += 1;
    return dim;
  }

  public Dimension getMinimumSize(JComponent c) {
    return getPreferredSize(c);
  }

  public Dimension getMaximumSize(JComponent c) {
    return getPreferredSize(c);
  }
}
