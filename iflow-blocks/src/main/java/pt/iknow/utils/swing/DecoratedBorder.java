package pt.iknow.utils.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import pt.iknow.floweditor.blocks.dataProcessing.OperationField;

public class DecoratedBorder extends CompoundBorder {
  private static final long serialVersionUID = -1288083441375591119L;
  private static final int WIDTH = 9;
  private static final int SIDE = 5;

  public DecoratedBorder() {
    this(-1, null, (Border) null);
  }

  public DecoratedBorder(int type) {
    this(type, null, (Border) null);
  }

  public DecoratedBorder(Color bg) {
    this(-1, bg, (Border) null);
  }

  public DecoratedBorder(Border b) {
    this(-1, null, b);
  }

  public DecoratedBorder(int type, Border b) {
    this(type, null, b);
  }

  public DecoratedBorder(Color bg, Border b) {
    this(-1, bg, b);
  }

  public DecoratedBorder(int type, Color bg) {
    this(type, bg, (Border) null);
  }

  public DecoratedBorder(int type, Color bg, Border b) {
    super(new DecoratedBorderImpl(type, bg), b);
  }

  public DecoratedBorder(int type, Color bg, JComponent c) {
    super(new DecoratedBorderImpl(type, bg), c.getBorder());
    c.setBorder(this);
  }

  private static class DecoratedBorderImpl extends AbstractBorder {
    private static final long serialVersionUID = 2856920910445355473L;
    private int type;
    private Color bg;

    public DecoratedBorderImpl(int type, Color bg) {
      this.type = type;
      this.bg = bg;
    }

    public Insets getBorderInsets(Component c)       { 
      return new Insets(0, 0, 0, WIDTH);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
      insets.left = insets.top = insets.bottom = 0;
      insets.right = WIDTH;
      return insets;
    }

    /**
     * Returns whether or not the border is opaque.
     */
    public boolean isBorderOpaque() { return false; }


    public void paintBorder(Component comp, Graphics g, int x, int y, int width, int height) {
      Color c = g.getColor();

      int px = width-WIDTH;
      int py = y;

      // paint background
      if(null != bg) {
        g.setColor(bg);
        g.fillRect(px, py, WIDTH, comp.getHeight());
      }

      boolean paintBorder = true;
      // fill square
      switch(type) {
      case OperationField.TYPE_ANY:
        g.setColor(Color.WHITE);
        break;
      case OperationField.TYPE_ARRAY:
        g.setColor(Color.BLUE);
        break;
      case OperationField.TYPE_SCALAR:
        g.setColor(Color.GREEN);
        break;
      case OperationField.TYPE_EXPRESSION:
        g.setColor(Color.YELLOW);
        break;
      default:
        paintBorder = false;
      break;
      }

      // paint square border
      if(paintBorder) {
        g.fillRect(px+2, py+2, SIDE, SIDE);

        g.setColor(Color.BLACK);
        g.drawRect(px+1, py+1, SIDE+1, SIDE+1);
      }

      // restore original color
      g.setColor(c);
    }
  }
}
