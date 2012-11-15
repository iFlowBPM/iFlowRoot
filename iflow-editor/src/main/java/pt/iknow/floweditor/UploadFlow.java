package pt.iknow.floweditor;

import java.awt.Font;

import javax.swing.JPanel;

import pt.iknow.floweditor.messages.Messages;
import pt.iknow.utils.StringUtilities;

public class UploadFlow extends javax.swing.JDialog {
  private static final long serialVersionUID = 1L;

  private String _flowId = null;
  private String _flowName = null;

  private boolean canceled = true;
  
  public String getFlowName() {
    return _flowName;
  }

  public String getFlowId() {
    return _flowId;
  }
  
  public boolean wasCanceled() {
    return canceled;
  }

  //public UploadFlow(java.awt.Frame parent, boolean modal,LibrarySet b) {
  public UploadFlow(javax.swing.JFrame parent, String title, String flowName, String flowId) {
    super(parent, true);
    initComponents(flowName, flowId);
    setTitle(title);
    setSize(350,140);
    setVisible(true);
  }

  private void initComponents(String flowName, String flowFile) {
    javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    flowidTextField = new javax.swing.JTextField();
    flowNameTextField = new javax.swing.JTextField();
    okButton = new javax.swing.JButton();
    javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    cancelButton = new javax.swing.JButton();
    flowidLabel = new javax.swing.JLabel();
    flowNameLabel = new javax.swing.JLabel();

    setModal(true);
    setResizable(false);
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent evt) { }
    });

    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        closeDialog(evt);
      }
    });

    String sFlowId = flowFile;
    if (StringUtilities.isNotEmpty(sFlowId)) { //$NON-NLS-1$
      if (sFlowId.indexOf("/") > -1) { //$NON-NLS-1$
        sFlowId = sFlowId.substring((sFlowId.lastIndexOf("/") + 1)); //$NON-NLS-1$
      }
      else if (sFlowId.indexOf("\\") > -1) { //$NON-NLS-1$
        sFlowId = sFlowId.substring((sFlowId.lastIndexOf("\\") + 1)); //$NON-NLS-1$
      }
    }

    String sFlowName = flowName;
    if (StringUtilities.isEmpty(sFlowName) || sFlowName.equals("Untitled")) { //$NON-NLS-1$
      sFlowName = flowFile;
      if (StringUtilities.isNotEmpty(sFlowName)) { //$NON-NLS-1$
        if (sFlowName.indexOf("/") > -1) { //$NON-NLS-1$
          sFlowName = sFlowName.substring((sFlowName.lastIndexOf("/") + 1)); //$NON-NLS-1$
        }
        else if (sFlowName.indexOf("\\") > -1) { //$NON-NLS-1$
          sFlowName = sFlowName.substring((sFlowName.lastIndexOf("\\") + 1)); //$NON-NLS-1$
        }
        if (sFlowName.indexOf(".") > -1) { //$NON-NLS-1$
          sFlowName = sFlowName.substring(0,sFlowName.lastIndexOf(".")); //$NON-NLS-1$
        }
      }
    }

    initComponents(jPanel2, sFlowId, sFlowName);
    getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

    okButton.setText(Mesg.OK);
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okButtonActionPerformed(evt);
      }
    });

    cancelButton.setText(Mesg.Cancelar);
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelButtonActionPerformed(evt);
      }
    });

    jPanel1.add(okButton);
    jPanel1.add(cancelButton);

    getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

    getRootPane().setDefaultButton(okButton);


    // pack();
  }

  private void initComponents(JPanel panel, String flowId, String flowName) {
    java.awt.GridBagConstraints gridBagConstraints;

    Font fFont = getFont().deriveFont(Font.BOLD);
    
    panel.setLayout(new java.awt.GridBagLayout());

    flowidLabel.setText(Messages.getString("UploadFlow.label.flowId")); //$NON-NLS-1$
    flowidLabel.setFont(fFont);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    panel.add(flowidLabel, gridBagConstraints);

    flowidTextField.setText(flowId);
    flowidTextField.setMaximumSize(new java.awt.Dimension(150, 20));
    flowidTextField.setMinimumSize(new java.awt.Dimension(150, 20));
    flowidTextField.setPreferredSize(new java.awt.Dimension(150, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    panel.add(flowidTextField, gridBagConstraints);

    flowNameLabel.setText(Messages.getString("UploadFlow.label.flowName")); //$NON-NLS-1$
    flowNameLabel.setFont(fFont);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    panel.add(flowNameLabel, gridBagConstraints);

    flowNameTextField.setText(flowName);
    flowNameTextField.setMaximumSize(new java.awt.Dimension(150, 20));
    flowNameTextField.setMinimumSize(new java.awt.Dimension(150, 20));
    flowNameTextField.setPreferredSize(new java.awt.Dimension(150, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    panel.add(flowNameTextField, gridBagConstraints);

  }

  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
    _flowId = flowidTextField.getText();
    _flowName = flowNameTextField.getText();
    if(_flowId.length() > 64 && _flowName.length() > 64) {
      new Erro(Messages.getString("Error.LARGE_FLOW_NAME_ID"), (javax.swing.JFrame)getParent());
      flowidTextField.setText(_flowId.substring(0, 64));
      flowNameTextField.setText(_flowName.substring(0, 64));
      return;
    }
    else if(_flowId.length() > 64) {
      new Erro(Messages.getString("Error.LARGE_FLOW_ID"), (javax.swing.JFrame)getParent());
      flowidTextField.setText(_flowId.substring(0, 64));
      return;
    }
    else if(_flowName.length() > 64) {
      new Erro(Messages.getString("Error.LARGE_FLOW_NAME"), (javax.swing.JFrame)getParent());
      flowNameTextField.setText(_flowName.substring(0, 64));
      return;
    }
    canceled = false;
    
    
    if (_flowId != null && _flowName != null &&
        !_flowId.equals("") && !_flowName.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
      setVisible(false);
      dispose();
    }
  }

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
    canceled = true;
    setVisible(false);
    dispose();
  }
  private void closeDialog(java.awt.event.WindowEvent evt) {
    canceled = true;
    setVisible(false);
    dispose();
  }

  private javax.swing.JLabel flowNameLabel;
  private javax.swing.JLabel flowidLabel;
  private javax.swing.JButton cancelButton;
  private javax.swing.JButton okButton;
  private javax.swing.JTextField flowidTextField;
  private javax.swing.JTextField flowNameTextField;
}
