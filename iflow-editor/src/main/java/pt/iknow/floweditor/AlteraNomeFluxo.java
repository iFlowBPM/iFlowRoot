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

public class AlteraNomeFluxo extends JDialog {
  private static final long serialVersionUID = 1L;

  public static final int OK = 0;
  public static final int CANCEL = 1;

  private JPanel mainPanel = new JPanel();
  private JTextField flowIdTextField = new JTextField();
  private JLabel nameLabel = new JLabel();
  private JTextField flowNameTextField = new JTextField();
  private JLabel idLabel = new JLabel();
  private JPanel buttonPanel = new JPanel();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();

  private String id = ""; //$NON-NLS-1$
  private String nome = ""; //$NON-NLS-1$
  private int result = CANCEL;

  public AlteraNomeFluxo(Frame frame, String id, String nome) {
    super(frame, Mesg.MudaNomeFluxo, true);
    jbInit(id, nome);
    this.setLocationRelativeTo(getParent());
    pack();
  }

  public int openDialog() {
    flowNameTextField.requestFocus();
    setVisible(true);

    return result;
  }

  void jbInit(String id, String name) {
    setSize(270,230);
    java.awt.GridBagConstraints gridBagConstraints = null;
    
    Font fFont = getFont().deriveFont(Font.BOLD);
    
    mainPanel.setLayout(new java.awt.GridBagLayout());

    idLabel.setText(Messages.getString("AlteraNomeFluxo.label.id", id)); //$NON-NLS-1$
    idLabel.setFont(fFont);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    mainPanel.add(idLabel, gridBagConstraints);

    flowIdTextField.setText(id);
    flowIdTextField.setMaximumSize(new java.awt.Dimension(150, 20));
    flowIdTextField.setMinimumSize(new java.awt.Dimension(150, 20));
    flowIdTextField.setPreferredSize(new java.awt.Dimension(150, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    mainPanel.add(flowIdTextField, gridBagConstraints);

    nameLabel.setText(Messages.getString("AlteraNomeFluxo.label.name", name)); //$NON-NLS-1$
    nameLabel.setFont(fFont);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    mainPanel.add(nameLabel, gridBagConstraints);

    flowNameTextField.setText(name);
    flowNameTextField.setMaximumSize(new java.awt.Dimension(150, 20));
    flowNameTextField.setMinimumSize(new java.awt.Dimension(150, 20));
    flowNameTextField.setPreferredSize(new java.awt.Dimension(150, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    mainPanel.add(flowNameTextField, gridBagConstraints);

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
    id = flowIdTextField.getText();
    nome = flowNameTextField.getText();
    if(id.length() > 64 && nome.length() > 64) {
      new Erro(Messages.getString("Error.LARGE_FLOW_NAME_ID"), (javax.swing.JFrame)getParent());
      flowIdTextField.setText(id.substring(0, 64));
      flowNameTextField.setText(nome.substring(0, 64));
      return;
    }
    else if(id.length() > 64) {
      new Erro(Messages.getString("Error.LARGE_FLOW_ID"), (javax.swing.JFrame)getParent());
      flowIdTextField.setText(id.substring(0, 64));
      return;
    }
    else if(nome.length() > 64) {
      new Erro(Messages.getString("Error.LARGE_FLOW_NAME"), (javax.swing.JFrame)getParent());
      flowNameTextField.setText(nome.substring(0, 64));
      return;
    }
    result = OK;
    setVisible(false);
    dispose();
  }

  /* cancel */
  void cancelButtonActionPerformed(ActionEvent e) {
    result = CANCEL;
    setVisible(false);
    dispose();
  }

  public String getNome() {
    return nome;
  }

  public String getId() {
    return id;
  }
}
