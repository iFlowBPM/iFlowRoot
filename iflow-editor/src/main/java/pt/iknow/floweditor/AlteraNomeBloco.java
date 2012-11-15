package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraNome
 *
 *  desc: dialogo para alterar nome de bloco
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pt.iknow.floweditor.messages.Messages;

public class AlteraNomeBloco extends JDialog {
  private static final long serialVersionUID = 1L;
  
  public static final int OK = 0;
  public static final int CANCEL = 1;

  private JPanel buttonPanel = new JPanel();
  private JPanel mainPanel = new JPanel();
  private JTextField oldNameTextField = new JTextField();
  private JLabel newNameLabel = new JLabel();
  private JTextField newNameTextField = new JTextField();
  private JLabel oldNameLabel = new JLabel();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();


  private String nome=""; //$NON-NLS-1$
  private int result;

  public AlteraNomeBloco(Frame frame, String nome) {
    super(frame, Mesg.MudaNome, true);
    jbInit(nome);
    pack();
  }
  
  
  public int openDialog() {
    setVisible(true);
    newNameTextField.requestFocus();
    
    return result;
  }


  void jbInit(String theName) {
    setSize(270,230);
    
    java.awt.GridBagConstraints gridBagConstraints = null;
    
    Font fFont = getFont().deriveFont(Font.BOLD);
    
    mainPanel.setLayout(new java.awt.GridBagLayout());

    oldNameLabel.setText(Messages.getString("AlteraNome.oldname")); //$NON-NLS-1$
    oldNameLabel.setFont(fFont);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    mainPanel.add(oldNameLabel, gridBagConstraints);

    oldNameTextField.setText(theName);
    oldNameTextField.setEditable(false);
    oldNameTextField.setMaximumSize(new java.awt.Dimension(150, 20));
    oldNameTextField.setMinimumSize(new java.awt.Dimension(150, 20));
    oldNameTextField.setPreferredSize(new java.awt.Dimension(150, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    mainPanel.add(oldNameTextField, gridBagConstraints);

    newNameLabel.setText(Messages.getString("AlteraNome.newname")); //$NON-NLS-1$
    newNameLabel.setFont(fFont);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    mainPanel.add(newNameLabel, gridBagConstraints);

    newNameTextField.setText(theName);
    newNameTextField.setMaximumSize(new java.awt.Dimension(150, 20));
    newNameTextField.setMinimumSize(new java.awt.Dimension(150, 20));
    newNameTextField.setPreferredSize(new java.awt.Dimension(150, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    mainPanel.add(newNameTextField, gridBagConstraints);

    okButton.setText(Mesg.OK);
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButtonActionPerformed(e);
      }
    });
    cancelButton.setText(Mesg.Cancelar);
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButtonActionPerformed(e);
      }
    });
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
  }

  /* OK */
  void okButtonActionPerformed(ActionEvent e) {
    nome=newNameTextField.getText();
    result = OK;
    setVisible(false);
    dispose();
  }

  /* cancel */
  void cancelButtonActionPerformed(ActionEvent e) {
    nome=oldNameTextField.getText();
    result = CANCEL;
    setVisible(false);
    dispose();
  }


  public String getNome() {
    return nome;
  }

}
