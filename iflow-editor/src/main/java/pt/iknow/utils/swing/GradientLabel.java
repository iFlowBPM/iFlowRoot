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
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;

public class GradientLabel extends JLabel {
  private static final long serialVersionUID = -3916716425267878034L;

  private static Color color = new Color(0xc3,0xbf,0xb7);
  
  public GradientLabel() {
    super();
  }

  public GradientLabel(String text) {
    super(text);
  }

  public void paintComponent(Graphics g) {
    setOpaque(false);
    String str = this.getText();
    FontMetrics fm = getFontMetrics(getFont());
    int width = fm.stringWidth(str);
    width = Math.min(width+30, this.getWidth());
    Graphics2D g2d = (Graphics2D) g;
    GradientPaint gradient = new GradientPaint(this.getX(), this.getY(), color, width, this.getHeight(), getBackground());
    g2d.setPaint(gradient);
    g2d.fillRect(this.getX(), this.getY(), width, this.getHeight());
    super.paintComponent(g);
  }

}
