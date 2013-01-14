/*
 * SignInfo.java
 *
 * Created on March 23, 2009, 6:48 PM
 */

package pt.iflow.utils.scanner;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import pt.iflow.applet.Messages;


/**
 *
 * @author  ombl
 */
public class PDFSignInfo extends JPanel {
  private static final long serialVersionUID = 6931865242027895138L;

  public static final int OK = 1;
  
  public static final int CANCEL = 0;
  

  private javax.swing.JButton okButton;
  private javax.swing.JButton browseButton;
  private javax.swing.JLabel jlReason;
  private javax.swing.JLabel jlLocation;
  private javax.swing.JLabel jlContact;
  private javax.swing.JLabel jlSignature;
  private javax.swing.JTextField jtfReason;
  private javax.swing.JTextField jtfLocation;
  private javax.swing.JTextField jtfContact;
  private javax.swing.JTextField jtfStore;
  private javax.swing.JButton tokenButton;
  private javax.swing.JComboBox jcbAlias;
  private javax.swing.JTextArea jtaCertInfo;
  
  private JDialog dialog;
  
  private KeyStore store;
  
  private String reason, location, contact;

  private PrivateKey key;
  private Certificate[] certificateChain;
  private int result = CANCEL;
  private char[] password = new char[0];
  private char[] keyPassword = new char[0];
  
  
  /** Creates new form SignInfo */
  public PDFSignInfo() {
    initComponents();
  }

  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    okButton = new javax.swing.JButton();
    jtfReason = new javax.swing.JTextField();
    jtfLocation = new javax.swing.JTextField();
    jtfContact = new javax.swing.JTextField();
    jtfStore = new javax.swing.JTextField();
    jlReason = new javax.swing.JLabel();
    jlLocation = new javax.swing.JLabel();
    jlContact = new javax.swing.JLabel();
    jlSignature = new javax.swing.JLabel();
    tokenButton = new javax.swing.JButton();
    browseButton = new javax.swing.JButton();
    jcbAlias = new javax.swing.JComboBox();
    jtaCertInfo = new javax.swing.JTextArea();

    setLayout(new java.awt.GridBagLayout());

    jtfReason.setText(""); //$NON-NLS-1$
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(2,2,2,2);
    add(jtfReason, gridBagConstraints);

    jtfLocation.setText(""); //$NON-NLS-1$
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(2,2,2,2);
    add(jtfLocation, gridBagConstraints);

    jtfContact.setText(""); //$NON-NLS-1$
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(2,2,2,2);
    add(jtfContact, gridBagConstraints);

    jlReason.setText(Messages.getString("PDFSignInfo.3")); //$NON-NLS-1$
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new Insets(2,2,2,5);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    add(jlReason, gridBagConstraints);

    jlLocation.setText(Messages.getString("PDFSignInfo.4")); //$NON-NLS-1$
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new Insets(2,2,2,5);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    add(jlLocation, gridBagConstraints);

    jlContact.setText(Messages.getString("PDFSignInfo.5")); //$NON-NLS-1$
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new Insets(2,2,2,5);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    add(jlContact, gridBagConstraints);

    jlSignature.setText(Messages.getString("PDFSignInfo.6")); //$NON-NLS-1$
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new Insets(2,2,2,5);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    add(jlSignature, gridBagConstraints);


    ActionListener listener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        boolean fail = true;
        store = null;

        if(e.getSource() == tokenButton) {
          
          try {
            store = KeyStore.getInstance("PKCS11", SecurityHelper.getPKCS11Provider()); //$NON-NLS-1$
          } catch (KeyStoreException e1) {
            e1.printStackTrace();
          }
          try {
            store.load(null, password=new char[0]); // experimenta sem password
            fail = false;
            jtfStore.setText(Messages.getString("PDFSignInfo.8")); //$NON-NLS-1$
          } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
          } catch (CertificateException e1) {
            e1.printStackTrace();
          } catch (FileNotFoundException e1) {
            e1.printStackTrace();
          } catch (IOException e1) {
            try {
              store.load(null, password=askPassword(Messages.getString("PDFSignInfo.9"))); //$NON-NLS-1$
              fail = false;
              jtfStore.setText(Messages.getString("PDFSignInfo.10")); //$NON-NLS-1$
            } catch (NoSuchAlgorithmException e2) {
              e2.printStackTrace();
            } catch (CertificateException e2) {
              e2.printStackTrace();
            } catch (IOException e2) {
              JOptionPane.showMessageDialog(PDFSignInfo.this, Messages.getString("PDFSignInfo.11") + //$NON-NLS-1$
                  Messages.getString("PDFSignInfo.12"), Messages.getString("PDFSignInfo.13"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
            }
          }

        } else if(e.getSource() == browseButton) {
          
          JFileChooser fc = new JFileChooser();
          fc.setFileFilter(new ExtensionFileFilter(Messages.getString("PDFSignInfo.14"), new String[]{"cer","ks","jks"})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
          int out = fc.showOpenDialog(dialog);
          if(out != JFileChooser.APPROVE_OPTION) return;


          File keyStore = fc.getSelectedFile();

          try {
            // use Bouncy Castle??
            store = KeyStore.getInstance("JKS", "SUN"); //$NON-NLS-1$ //$NON-NLS-2$
          } catch (KeyStoreException e1) {
            e1.printStackTrace();
          } catch (NoSuchProviderException e1) {
            e1.printStackTrace();
          }


          try {
            store.load(new FileInputStream(keyStore), password=new char[0]); // experimenta sem password
            fail = false;
            jtfStore.setText(keyStore.getName());
          } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
          } catch (CertificateException e1) {
            e1.printStackTrace();
          } catch (FileNotFoundException e1) {
            e1.printStackTrace();
          } catch (IOException e1) {
            try {
              store.load(new FileInputStream(keyStore), password=askPassword(Messages.getString("PDFSignInfo.20"))); //$NON-NLS-1$
              fail = false;
              jtfStore.setText(keyStore.getName());
            } catch (NoSuchAlgorithmException e2) {
              e2.printStackTrace();
            } catch (CertificateException e2) {
              e2.printStackTrace();
            } catch (FileNotFoundException e2) {
              e2.printStackTrace();
            } catch (IOException e2) {
              JOptionPane.showMessageDialog(PDFSignInfo.this, Messages.getString("PDFSignInfo.21") + //$NON-NLS-1$
              		Messages.getString("PDFSignInfo.22"), Messages.getString("PDFSignInfo.23"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
            }
          }
        }
        
        if(fail) return;
        
        
        try {
          Enumeration aliases = store.aliases();
          jcbAlias.removeAllItems();
          jcbAlias.addItem(Messages.getString("PDFSignInfo.24")); //$NON-NLS-1$
          while(aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            X509Certificate cert = (X509Certificate) store.getCertificate(alias);
            if(store.isKeyEntry(alias) && cert != null) {
              jcbAlias.addItem(new IDEntry(alias, cert));
            }
          }
          jcbAlias.setEnabled(true);
          jcbAlias.setSelectedIndex(0);
        } catch (KeyStoreException e1) {
          e1.printStackTrace();
        }
      }
    };

    // config panel...
    {
      JPanel panel = new JPanel();
      panel.setLayout(new java.awt.GridBagLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2,2,2,2);
      add(panel, gridBagConstraints);

      tokenButton.setText(Messages.getString("PDFSignInfo.25")); //$NON-NLS-1$
      tokenButton.setToolTipText(Messages.getString("PDFSignInfo.26")); //$NON-NLS-1$
      tokenButton.setEnabled(SecurityHelper.canPKCS11());
      tokenButton.addActionListener(listener);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
      gridBagConstraints.insets = new Insets(2,2,2,2);
      panel.add(tokenButton, gridBagConstraints);

      browseButton.setText(Messages.getString("PDFSignInfo.27")); //$NON-NLS-1$
      browseButton.setToolTipText(Messages.getString("PDFSignInfo.28")); //$NON-NLS-1$
      browseButton.addActionListener(listener);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
      gridBagConstraints.insets = new Insets(2,2,2,2);
      panel.add(browseButton, gridBagConstraints);

      jtfStore.setEditable(false);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2,2,2,2);
      panel.add(jtfStore, gridBagConstraints);
    }
    // end config panel
        
    JLabel aliasLbl = new JLabel();
    aliasLbl.setText(Messages.getString("PDFSignInfo.29")); //$NON-NLS-1$
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.insets = new Insets(2,2,2,5);
    add(aliasLbl, gridBagConstraints);

    jcbAlias.setEditable(false);
    jcbAlias.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(2,2,2,2);
    add(jcbAlias, gridBagConstraints);
    jcbAlias.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        key = null;
        certificateChain = null;
        if(jcbAlias.getSelectedIndex() == 0) return; // ignore
        IDEntry idEntry = (IDEntry) jcbAlias.getSelectedItem();
        String alias = idEntry.getAlias();
        boolean fail = true;
        keyPassword = password;
        while(keyPassword != null && fail) {
          try {
            key = (PrivateKey) store.getKey(alias, keyPassword);
            fail = false;
          } catch (ClassCastException e1) {
            e1.printStackTrace();
            keyPassword = null;
          } catch (KeyStoreException e1) {
            e1.printStackTrace();
            keyPassword = null;
          } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            keyPassword = null;
          } catch (UnrecoverableKeyException e1) {
            keyPassword = askPassword(Messages.getString("PDFSignInfo.30")); //$NON-NLS-1$
          }
        }
        
        if(fail) return;
        
        Certificate cert = null;
        try {
          cert = store.getCertificate(alias);
          certificateChain = store.getCertificateChain(alias);
        } catch (KeyStoreException e1) {
          e1.printStackTrace();
        }
        
        okButton.setEnabled(key != null && certificateChain != null);
        jtaCertInfo.setText(""); //$NON-NLS-1$
        if(cert!=null) {
          jtaCertInfo.setText(cert.toString());
        }
        
      }
    });
    
    
    jtaCertInfo.setEditable(false);
    jtaCertInfo.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new Insets(2,2,2,2);
    JScrollPane pScroll = new JScrollPane(jtaCertInfo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    add(pScroll, gridBagConstraints);


    
    okButton.setText(Messages.getString("PDFSignInfo.32")); //$NON-NLS-1$
    okButton.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.insets = new Insets(2,2,2,2);
    gridBagConstraints.gridwidth = 3;
    add(okButton, gridBagConstraints);
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        reason = jtfReason.getText();
        location = jtfLocation.getText();
        contact = jtfContact.getText();
        
        result = OK;
        dialog.setVisible(false);
      }
    });

  }
  
  
  private static class IDEntry {
    final String alias;
    final String txt;
    
    public IDEntry(String alias, X509Certificate certificate) {
      this.alias = alias;
      this.txt = certificate.getSubjectDN().getName();
    }
    
    public String toString() {
      return txt;
    }
    
    public String getAlias() {
      return this.alias;
    }
    
  }
  
  public PrivateKey getSignKey() {
    return key;
  }
  
  public Certificate[] getCertificateChain() {
    return certificateChain;
  }
  
  public String getSignReason() {
    return reason;
  }

  public String getSignLocation() {
    return location;
  }

  public String getSignContact() {
    return contact;
  }

  public int showDialog(Component parent) {
    result = CANCEL;
    JFrame frame = new JFrame();
    dialog = new JDialog(frame, Messages.getString("PDFSignInfo.33"), true); //$NON-NLS-1$

    Container contentPane = dialog.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(this, BorderLayout.CENTER);

    dialog.pack();
    dialog.setSize(400, 300);

    dialog.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        result = CANCEL;
      }
    });

    dialog.setVisible(true);
    frame.dispose();
    frame = null;

    return result;
  }
  
  
  private char [] askPassword(String msg) {
    JPasswordField jpf = new JPasswordField(30);
    JPanel messagePanel = new JPanel();
    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
    messagePanel.add(new JLabel(msg+":")); //$NON-NLS-1$
    messagePanel.add(jpf);
    int result = JOptionPane.showConfirmDialog(dialog, messagePanel,  msg, JOptionPane.OK_CANCEL_OPTION);
    if(result == JOptionPane.OK_OPTION) return jpf.getPassword();
    return null;
  }
  
  
  public static void main(String [] args) {
    Security.addProvider(new BouncyCastleProvider());
    JFrame frame = new JFrame();
    
    int result = (new PDFSignInfo()).showDialog(frame);
    
    System.out.println("Result = "+result); //$NON-NLS-1$
    System.exit(0);
  }
  
 
  
}
