package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: About
 *
 *  desc: mostra informação sobre editor
 *
 ****************************************************/

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RunGC extends JDialog implements ActionListener {

  /**
   * 
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel2 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton button1 = new JButton();
  JLabel imageLabel = new JLabel();
  JLabel label1 = new JLabel();
  JLabel label2 = new JLabel();
  JLabel label3 = new JLabel();
  JLabel label4 = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  GridLayout gridLayout1 = new GridLayout();
  long lFree1;
  long lUsage1;
  long lFree2;
  long lUsage2;

  public RunGC(Frame parent) {
    super(parent, true);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    lFree1 = Runtime.getRuntime().freeMemory();
    lUsage1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    System.gc();
    Runtime.getRuntime().gc();

    lFree2 = Runtime.getRuntime().freeMemory();
    lUsage2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    jbInit();
    pack();
    setVisible(true);
  }

  /** Component initialization */
  private void jbInit() {

    Icon a = new ImageIcon(Janela.getInstance().createImage("flow_editor.jpg", false));
    imageLabel.setIcon(a);
    this.setTitle(Mesg.RunGC);
    setResizable(false);
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);

    label1.setText(Mesg.RunGCLong);
    label2.setText(Mesg.RunGCDone);
    label3.setText(Mesg.RunGCBefore + ": " + Mesg.RunGCUsage + " " + lUsage1 + "; " + Mesg.RunGCFree + " " + lFree1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    label4.setText(Mesg.RunGCAfter + ": " + Mesg.RunGCUsage + " " + lUsage2 + "; " + Mesg.RunGCFree + " " + lFree2); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    button1.setText(Mesg.OK);
    button1.addActionListener(this);
    insetsPanel2.add(imageLabel, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    this.getContentPane().add(panel1, null);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(label3, null);
    insetsPanel3.add(label4, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
  }

  /** Overridden so we can exit when window is closed */
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  /** Close the dialog */
  void cancel() {
    dispose();
  }

  /** Close the dialog on a button event */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == button1) {
      cancel();
    }
  }
}
