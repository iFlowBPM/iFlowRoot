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
package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: JComponentVista
 *
 *  desc: impressao
 *
 ****************************************************/

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JComponent;

public class JComponentVista extends Vista implements Printable {
  private double mScaleX;
  private double mScaleY;
  private Dimension dim;
  /**
   * The Swing component to print.
   */
  private JComponent mComponent;

  /**
   * Create a Pageable that can print a Swing JComponent over multiple pages.
   * 
   * @param c
   *          The swing JComponent to be printed.
   * 
   * @param format
   *          The size of the pages over which the componenent will be printed.
   */
  public JComponentVista(JComponent c, PageFormat format, Dimension d) {
    dim = d;
    setPageFormat(format);
    setPrintable(this);
    setComponent(c);
    /*
     * Tell the Vista we subclassed the size of the canvas.
     */
    SET();
  }

  public void SET() {
    Rectangle componentBounds;
    if (dim == null)
      componentBounds = mComponent.getBounds(null);
    else
      componentBounds = new Rectangle(0, 0, dim.width + 10, dim.height + 10);

    setSize(componentBounds.width, componentBounds.height);
    setScale(1, 1);
  }

  protected void setComponent(JComponent c) {
    mComponent = c;
  }

  protected void setScale(double scaleX, double scaleY) {
    mScaleX = scaleX;
    mScaleY = scaleY;
  }

  public void scaleToFitX() {
    PageFormat format = getPageFormat();
    Rectangle componentBounds = mComponent.getBounds(null);
    double scaleX = format.getImageableWidth() / componentBounds.width;
    double scaleY = scaleX;
    if (scaleX < 1) {
      setSize((float) format.getImageableWidth(), (float) (componentBounds.height * scaleY));
      setScale(scaleX, scaleY);
    }
  }

  public void scaleToFitY() {
    PageFormat format = getPageFormat();
    Rectangle componentBounds = mComponent.getBounds(null);
    double scaleY = format.getImageableHeight() / componentBounds.height;
    double scaleX = scaleY;
    if (scaleY < 1) {
      setSize((float) (componentBounds.width * scaleX), (float) format.getImageableHeight());
      setScale(scaleX, scaleY);
    }
  }

  public void scaleToFit(boolean useSymmetricScaling) {
    PageFormat format = getPageFormat();
    Rectangle componentBounds = mComponent.getBounds(null);
    double scaleX = format.getImageableWidth() / componentBounds.width;
    double scaleY = format.getImageableHeight() / componentBounds.height;
    // FlowEditor.log("Scale: " + scaleX + " " + scaleY);
    if (scaleX < 1 || scaleY < 1) {
      if (useSymmetricScaling) {
        if (scaleX < scaleY) {
          scaleY = scaleX;
        } else {
          scaleX = scaleY;
        }
      }
      setSize((float) (componentBounds.width * scaleX), (float) (componentBounds.height * scaleY));
      setScale(scaleX, scaleY);
    }
  }

  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
    Graphics2D g2 = (Graphics2D) graphics;
    g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    Rectangle componentBounds = mComponent.getBounds(null);
    g2.translate(-componentBounds.x, -componentBounds.y);
    g2.scale(mScaleX, mScaleY);
    boolean wasBuffered = mComponent.isDoubleBuffered();
    mComponent.paint(g2);
    mComponent.setDoubleBuffered(wasBuffered);
    return PAGE_EXISTS;

  }
}
