package pt.iknow.floweditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pt.iknow.floweditor.messages.Messages;
import pt.iknow.utils.StringUtilities;

/*******************************************************************************
 * 
 * Project FLOW EDITOR
 * 
 * class: FindComponent
 * 
 * desc: dialogo para encontrar um dado bloco no fluxo
 * 
 ******************************************************************************/

public class ProxyDialog extends JDialog {

  private static final long serialVersionUID = -321L;
  
  private JButton okButton;
  private JButton cancelButton;
  private JTextField jtProxyHost;
  private JTextField jtProxyPort;
  private JTextField jtProxyUser;
  private JPasswordField jpProxyPass;
  private JTextField jtNtDomain;
  private JCheckBox jcbUseProxy;
  private JCheckBox jcbProxyAuth;
  private JCheckBox jcbNTAuth;
  
  private JLabel lblProxyHost;
  private JLabel lblProxyPort;
  private JLabel lblProxyUser;
  private JLabel lblProxyPass;
  private JLabel lblProxyDomain;
  
  private FlowEditorConfig cfg;
  
  /** Creates new form EncontraComponente */
  public ProxyDialog(Dialog janela, FlowEditorConfig cfg) {
    super(janela, true);
    this.cfg = cfg;
    initComponentsGB();
    setTitle(Mesg.Proxy);
  }

  /** Creates new form EncontraComponente */
  public ProxyDialog(Frame janela, FlowEditorConfig cfg) {
    super(janela, true);
    this.cfg = cfg;
    initComponentsGB();
    setTitle(Mesg.Proxy);
  }
  
  public static void open(Dialog janela, FlowEditorConfig cfg) {
    ProxyDialog fc = new ProxyDialog(janela, cfg);
    fc.setVisible(true);
  }
  
  public static void open(Frame janela, FlowEditorConfig cfg) {
    ProxyDialog fc = new ProxyDialog(janela, cfg);
    fc.setVisible(true);
  }

  private void initComponentsGB() {
    setResizable(false);

    JPanel jPanel2 = new JPanel();
    jtProxyHost = new JTextField();
    jtProxyPort = new JTextField();
    jtProxyUser = new JTextField();
    jpProxyPass = new JPasswordField();
    jtNtDomain = new JTextField();

    jcbUseProxy = new JCheckBox(Messages.getString("ProxyDialog.use_proxy")); //$NON-NLS-1$
    jcbProxyAuth = new JCheckBox(Messages.getString("ProxyDialog.auth")); //$NON-NLS-1$
    jcbNTAuth = new JCheckBox(Messages.getString("ProxyDialog.nt_auth")); //$NON-NLS-1$

    ActionListener jcbChangeListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changeFormStatus();
      }
    };
    DocumentListener docChangeListener = new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        changeFormStatus();
      }

      public void insertUpdate(DocumentEvent e) {
        changeFormStatus();
      }

      public void removeUpdate(DocumentEvent e) {
        changeFormStatus();
      }
    };

    jcbUseProxy.addActionListener(jcbChangeListener);
    jcbProxyAuth.addActionListener(jcbChangeListener);
    jcbNTAuth.addActionListener(jcbChangeListener);
    
    jtProxyHost.getDocument().addDocumentListener(docChangeListener);
    jtProxyPort.getDocument().addDocumentListener(docChangeListener);
    jtProxyUser.getDocument().addDocumentListener(docChangeListener);
    jpProxyPass.getDocument().addDocumentListener(docChangeListener);
    jtNtDomain.getDocument().addDocumentListener(docChangeListener);



    cancelButton = new JButton();
    okButton = new JButton();

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        cancelButtonActionPerformed();
      }
    });

    jPanel2.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    jtProxyHost.setMaximumSize(new Dimension(150, 20));
    jtProxyHost.setMinimumSize(new Dimension(150, 20));
    jtProxyHost.setPreferredSize(new Dimension(150, 20));

    jtProxyPort.setMaximumSize(new Dimension(150, 20));
    jtProxyPort.setMinimumSize(new Dimension(150, 20));
    jtProxyPort.setPreferredSize(new Dimension(150, 20));

    jtProxyUser.setMaximumSize(new Dimension(150, 20));
    jtProxyUser.setMinimumSize(new Dimension(150, 20));
    jtProxyUser.setPreferredSize(new Dimension(150, 20));

    jpProxyPass.setMaximumSize(new Dimension(150, 20));
    jpProxyPass.setMinimumSize(new Dimension(150, 20));
    jpProxyPass.setPreferredSize(new Dimension(150, 20));

    jtNtDomain.setMaximumSize(new Dimension(150, 20));
    jtNtDomain.setMinimumSize(new Dimension(150, 20));
    jtNtDomain.setPreferredSize(new Dimension(150, 20));

    // common to all
    gbc.insets = new Insets(2,2,2,2);
//  gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor=GridBagConstraints.LINE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    jPanel2.add(jcbUseProxy, gbc);
    gbc.gridwidth = 1;

    lblProxyHost = new JLabel(Messages.getString("ProxyDialog.host")); //$NON-NLS-1$
    lblProxyHost.setLabelFor(jtProxyHost);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor=GridBagConstraints.LINE_END;
    jPanel2.add(lblProxyHost,gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor=GridBagConstraints.LINE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    jPanel2.add(jtProxyHost, gbc);

    lblProxyPort = new JLabel(Messages.getString("ProxyDialog.port")); //$NON-NLS-1$
    lblProxyPort.setLabelFor(jtProxyPort);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor=GridBagConstraints.LINE_END;
    jPanel2.add(lblProxyPort,gbc);
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor=GridBagConstraints.LINE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    jPanel2.add(jtProxyPort,gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor=GridBagConstraints.LINE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    jPanel2.add(jcbProxyAuth, gbc);
    gbc.gridwidth = 1;


    lblProxyUser = new JLabel(Messages.getString("ProxyDialog.username")); //$NON-NLS-1$
    lblProxyUser.setLabelFor(jtProxyUser);
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor=GridBagConstraints.LINE_END;
    jPanel2.add(lblProxyUser,gbc);
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.anchor=GridBagConstraints.LINE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    jPanel2.add(jtProxyUser,gbc);

    lblProxyPass = new JLabel(Messages.getString("ProxyDialog.password")); //$NON-NLS-1$
    lblProxyPass.setLabelFor(jpProxyPass);
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor=GridBagConstraints.LINE_END;
    jPanel2.add(lblProxyPass,gbc);
    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.anchor=GridBagConstraints.LINE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    jPanel2.add(jpProxyPass,gbc);

    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor=GridBagConstraints.LINE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    jPanel2.add(jcbNTAuth, gbc);
    gbc.gridwidth = 1;

    lblProxyDomain = new JLabel(Messages.getString("ProxyDialog.domain")); //$NON-NLS-1$
    lblProxyDomain.setLabelFor(jtNtDomain);
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor=GridBagConstraints.LINE_END;
    jPanel2.add(lblProxyDomain,gbc);
    gbc.gridx = 1;
    gbc.gridy = 7;
    gbc.anchor=GridBagConstraints.LINE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    jPanel2.add(jtNtDomain,gbc);


    JPanel panel = new JPanel();

    okButton.setText(Mesg.OK);
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        updateConfig();
        closeWindow();
      }
    });

    panel.add(okButton);

    cancelButton.setText(Mesg.Cancelar);
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cancelButtonActionPerformed();
      }
    });

    panel.add(cancelButton);

    Container rootPane = getContentPane();
    rootPane.setLayout(new BorderLayout());

    rootPane.add(jPanel2, BorderLayout.CENTER);
    rootPane.add(panel, BorderLayout.SOUTH);

    pack();
    updateForm();
}

  /*****************************************************************************
   * EXIT
   */
  private void cancelButtonActionPerformed() {
    closeWindow();
  }

  private void closeWindow() {
    setVisible(false);
    dispose();
  }
  
  
  public static void main(String [] args) {
    JFrame xpto = new JFrame("Sasda"); //$NON-NLS-1$
    open(xpto, null);
  }
  
  
  private void updateConfig() {
    if(null == cfg) return;
    cfg.setUseProxy(jcbUseProxy.isSelected());
    cfg.setProxyHost(jtProxyHost.getText());
    cfg.setProxyPort(jtProxyPort.getText());
    
    cfg.setUseProxyAuth(jcbProxyAuth.isSelected());
    cfg.setProxyUser(jtProxyUser.getText());
    cfg.setProxyPass(new String(jpProxyPass.getPassword()));
    
    cfg.setUseNTAuth(jcbNTAuth.isSelected());
    cfg.setProxyDomain(jtNtDomain.getText());
    
    cfg.saveConfig();
  }
  
  private void updateForm() {
    if(null == cfg) return;
    jcbUseProxy.setSelected(cfg.isUseProxy());
    jtProxyHost.setText(cfg.getProxyHost());
    jtProxyPort.setText(cfg.getProxyPort());
    
    jcbProxyAuth.setSelected(cfg.isUseProxyAuth());
    jtProxyUser.setText(cfg.getProxyUser());
    jpProxyPass.setText(cfg.getProxyPass());
    
    jcbNTAuth.setSelected(cfg.isUseNTAuth());
    jtNtDomain.setText(cfg.getProxyDomain());
    
    changeFormStatus();
  }
  
  private void changeFormStatus() {
    boolean useProxy = jcbUseProxy.isSelected();
    jtProxyHost.setEnabled(useProxy);
    lblProxyHost.setEnabled(useProxy);
    jtProxyPort.setEnabled(useProxy);
    lblProxyPort.setEnabled(useProxy);

    // some ctrl vars
    boolean okEnabled = true;
    if (useProxy) {
      okEnabled = StringUtilities.isNotEmpty(jtProxyHost.getText()) && StringUtilities.isNotEmpty(jtProxyPort.getText());
    }

    jcbProxyAuth.setEnabled(useProxy);
    boolean useAuth = jcbProxyAuth.isSelected();
    jtProxyUser.setEnabled(useProxy && useAuth);
    lblProxyUser.setEnabled(useProxy && useAuth);
    jpProxyPass.setEnabled(useProxy && useAuth);
    lblProxyPass.setEnabled(useProxy && useAuth);

    if (okEnabled && useProxy && useAuth) {
      okEnabled = StringUtilities.isNotEmpty(jtProxyUser.getText())
          && StringUtilities.isNotEmpty(new String(jpProxyPass.getPassword()));
    }

    jcbNTAuth.setEnabled(useProxy && useAuth);
    boolean useNT = jcbNTAuth.isSelected();
    jtNtDomain.setEnabled(useProxy && useAuth && useNT);
    lblProxyDomain.setEnabled(useProxy && useAuth && useNT);

    if (okEnabled && useProxy && useAuth && useNT) {
      okEnabled = StringUtilities.isNotEmpty(jtNtDomain.getText());
    }

    okButton.setEnabled(okEnabled);
  }
}
