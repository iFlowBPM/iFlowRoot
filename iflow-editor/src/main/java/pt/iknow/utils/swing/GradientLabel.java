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
