package pt.iknow.utils.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class RadioOptionPane {

  public static final int CLOSED_OPTION = -1;
  
  private JComponent parent;
  private String [] inputs;
  private String title;
  private String msg;
  private String buttonText;
  private int msgType;
  private Icon icon;
  
  private int selected;
  private int defaultSelected;
  
  public RadioOptionPane(JComponent parent, String title, String msg, String [] inputs) {
    this.parent = parent;
    this.inputs = inputs;
    this.title = title;
    this.msg = msg;
    this.selected = -1;
    this.msgType = JOptionPane.WARNING_MESSAGE;
    this.icon = null;
    this.buttonText = "Continue";
    this.defaultSelected = CLOSED_OPTION;
  }
  
  public void setDefaultOption(int position) {
    this.defaultSelected = position;
  }
  
  public void setButtonText(String text) {
    this.buttonText = text;
  }
  
  public void setMessageType(int msgType) {
    this.msgType = msgType;
  }
  
  public void setIcon(Icon icon) {
    this.icon = icon;
  }
  
  public int open() {
    this.selected = this.defaultSelected;
    JPanel comp = new JPanel();
    comp.setLayout(new BoxLayout(comp,BoxLayout.Y_AXIS));
    comp.add(new JLabel(this.msg));
    comp.add(new JPanel());
    
    final ButtonGroup buttonGroup = new ButtonGroup();
    RadioActionListener listener = new RadioActionListener();
    for(int i = 0; i < this.inputs.length; i++) {
      JRadioButton jrb = new JRadioButton(this.inputs[i]);
      jrb.addActionListener(listener);
      listener.registerButton(jrb, i);
      buttonGroup.add(jrb);
      comp.add(jrb);
      jrb.setSelected(this.selected==i);
    }
    
    Object[] options = {this.buttonText};
    int r = JOptionPane.showOptionDialog(this.parent, comp, this.title, JOptionPane.DEFAULT_OPTION, this.msgType, this.icon, options, options[0]);
    if(r == JOptionPane.CLOSED_OPTION) this.selected = CLOSED_OPTION;
    else this.defaultSelected = this.selected;
    
    return this.selected;
  }
  
  private class RadioActionListener implements ActionListener {

    private Map<JRadioButton,Integer> buttons = new HashMap<JRadioButton, Integer>();
    
    public void registerButton(JRadioButton jrb, int pos) {
      buttons.put(jrb, pos);
    }
    
    public void actionPerformed(ActionEvent e) {
      Integer pos = buttons.get(e.getSource());
      if(null == pos) selected = -1;
      else selected = pos.intValue();
      System.out.println("Selected: "+selected);
    }
    
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
