package pt.iknow.utils.swing;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;

public class ButtonChanger extends MouseAdapter {
  Color c1 = Color.lightGray; // highlight
  Color c2 = Color.darkGray; // shadow
  Border over = new SoftBevelBorder(SoftBevelBorder.RAISED, c1, c2);
  Border press = new SoftBevelBorder(SoftBevelBorder.LOWERED, c1, c2);

  public void mouseEntered(MouseEvent me) {
    JButton button = (JButton) me.getSource();
    if (button.isEnabled()) {
      button.setBorder(over);
      button.setBorderPainted(true);
    }
  }

  public void mousePressed(MouseEvent me) {
    JButton button = (JButton) me.getSource();
    button.setBorder(press);
  }

  public void mouseReleased(MouseEvent me) {
    JButton button = (JButton) me.getSource();
    button.setBorder(over);
  }

  public void mouseExited(MouseEvent me) {
    JButton button = (JButton) me.getSource();
    button.setBorderPainted(false);
  }
}