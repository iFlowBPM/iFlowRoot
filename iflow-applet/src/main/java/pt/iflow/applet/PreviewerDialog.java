package pt.iflow.applet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.Provider;
import java.util.UUID;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker.StateValue;

import pt.iflow.applet.cipher.CipherType;
import pt.iflow.applet.cipher.FileCipher;
import pt.iflow.applet.signer.FileSigner;
import pt.iflow.applet.signer.SignatureType;

import com.toedter.calendar.JDateChooser;

public class PreviewerDialog extends JFrame implements PropertyChangeListener, ActionListener {
  private static final long serialVersionUID = -1502926852420989215L;

  private static Log log = LogFactory.getLog(PreviewerDialog.class);
  
  public static final int CANCEL = 0;
  public static final int OK = 1;
  
  public static final String ACTION_OK = "OK"; //$NON-NLS-1$
  public static final String ACTION_CANCEL = "CANCEL"; //$NON-NLS-1$
  
  private Component okButton;
  private Component cancelButton;
  private JProgressBar progressBar;
  private final Container formPanel;
  private final UtilityApplet applet;
  private final FileSigner signer;
  private final FileCipher cipher;
  private final PreviewTask task;
  private final TaskStatus status;
  private final IVFileProvider fileProvider;
  private final WebClient webClient;
  private boolean guiRequired;

  public PreviewerDialog(UtilityApplet applet, WebClient webClient, FileSigner signer, FileCipher cipher, IVFileProvider fileProvider) {
    this.applet = applet;
    this.signer = signer;
    this.cipher = cipher;
    this.fileProvider = fileProvider;
    this.webClient = webClient;
    this.status = new TaskStatus(UUID.randomUUID().toString());
    this.task = new PreviewTask(this);
    this.task.addPropertyChangeListener(this);
    this.guiRequired = false;
    this.formPanel = createFormPanel();
    
    setTitle(Messages.getString("PreviewerDialog.2")); //$NON-NLS-1$
    Container container = getContentPane();
    container.setLayout(new BorderLayout());
    add(this.formPanel, BorderLayout.CENTER);
    
  }

  public UtilityApplet getUtilityApplet() {
    return applet;
  }

  public IVFileProvider getFileProvider() {
    return fileProvider;
  }

  public WebClient getWebClient() {
    return webClient;
  }

  public FileSigner getFileSigner() {
    return signer;
  }

  public FileCipher getFileCipher() {
    return cipher;
  }

  private Container createFormPanel() {
    GridBagConstraints gbc = null;
    
    // Instanciar botões
    okButton = SwingUtils.newButton(Messages.getString("PreviewerDialog.3"), ACTION_OK, this); //$NON-NLS-1$
    cancelButton = SwingUtils.newButton(Messages.getString("PreviewerDialog.4"), ACTION_CANCEL, this); //$NON-NLS-1$
    progressBar = new JProgressBar();
    
    
    Container panel = SwingUtils.newPanel();
    panel.setLayout(new GridBagLayout());
    // Prepare signer form
    Component filePanel = createFilePanel();
    Component signerPanel = createPanel(signer.getForm());
    Component cipherPanel = createPanel(cipher.getForm());

    
    if(null != filePanel) {
      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      gbc.fill = GridBagConstraints.BOTH;
      panel.add(filePanel, gbc);
    }
    
    if(null != signerPanel) {
      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      gbc.fill = GridBagConstraints.BOTH;
      panel.add(signerPanel, gbc);
    }

    if(null != cipherPanel) {
      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 2;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      gbc.fill = GridBagConstraints.BOTH;
      panel.add(cipherPanel, gbc);
    }
    
    
    // Adicionar botões
    Container buttonPannel = SwingUtils.newPanel();
    buttonPannel.add(okButton);
    buttonPannel.add(cancelButton);
    
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(buttonPannel, gbc);

    // Barra de progresso
    progressBar.setStringPainted(false);
    progressBar.setString(""); //$NON-NLS-1$
    
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(progressBar, gbc);

    return panel;
  }

  private Container createFilePanel() {
    if(null == fileProvider) return null;
    DynamicField field = fileProvider.getDynamicField();
    if(null == field) return null;
    Component component = getFileComponent(field, new UpdateOkButtonStateListener());
    if (null == component) return null;
    Container panel = SwingUtils.newPanel();
    panel.setLayout(new GridBagLayout());
    // ainda não tenho nada alinhavado para isto....
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(2,2,2,2);
    gbc.anchor = GridBagConstraints.LINE_END;
    Component lbl = SwingUtils.newLabel(field.getLabel());
    panel.add(lbl, gbc);
    
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.insets = new Insets(2,2,2,2);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    
    panel.add(component, gbc);
    return panel;
  }

  private Container createPanel(DynamicForm form) {
    // no form required
    if(null == form || form.isEmpty()) return null;
    this.guiRequired = true;

//    MultiActionChangedValidator multiActionValidator = new MultiActionChangedValidator();
    UpdateOkButtonStateListener actionValidator = new UpdateOkButtonStateListener();

    Container panel = SwingUtils.newPanel();
    panel.setLayout(new GridBagLayout());

    int pos = 0;
    for(DynamicField field : form.getFields()) {
      // ainda não tenho nada alinhavado para isto....
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = pos;
      gbc.insets = new Insets(2,2,2,2);
      gbc.anchor = GridBagConstraints.LINE_END;
      Component lbl = SwingUtils.newLabel(field.getLabel());
      panel.add(lbl, gbc);

      Component component = null;
      switch(field.getType()) {
      case SIGNATURE_CERTIFICATE:
        component = getCertificateComponent(field, actionValidator);
        break;
      case CHECKBOX:
        component = getCheckboxComponent(field, actionValidator);
        break;
      case DATE:
        component = getDateComponent(field, actionValidator);
        break;
      case TEXTFIELD:
      default:
        component = getTextComponent(field, actionValidator);
        break;
      }
      
      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = pos;
      gbc.insets = new Insets(2,2,2,2);
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.weightx = 1.0;
      
      panel.add(component, gbc);
      
      // next position
      pos++;
    }

    SwingUtils.setBorder(panel, form.getTitle());
    return panel;
  }
  
  // TODO Same stuff
  private JComponent getTextComponent(DynamicField field, DocumentListener textValidator) {
    JTextField jtf = new JTextField();
    MultiDocumentListener multiDocumentListener = new MultiDocumentListener();
    multiDocumentListener.addDocumentListener(new DynamicTextListener(jtf, field));
    multiDocumentListener.addDocumentListener(textValidator);
    jtf.getDocument().addDocumentListener(multiDocumentListener);

    return jtf;
  }
  
  private Component getCertificateComponent(DynamicField field, ActionListener okButtonValidator) {
    Container aux = SwingUtils.newPanel();
    aux.setLayout(new BorderLayout());
    
    JComboBox jcbAlias = new JComboBox();
    jcbAlias.setRenderer(new MyComboBoxRenderer());
    jcbAlias.setEditable(false);
    jcbAlias.setEnabled(false);
    
    MultiActionListener multiActionListener = new MultiActionListener();
    multiActionListener.addListener(new DynamicComboListener(jcbAlias, field));
    multiActionListener.addListener(okButtonValidator);
    jcbAlias.addActionListener(multiActionListener);
    
    aux.add(jcbAlias, BorderLayout.CENTER);
    
    ActionListener action = new ReloadCertificatesAction(jcbAlias);
    Component jcbReload = SwingUtils.newButton("...", action); //$NON-NLS-1$
    aux.add(jcbReload, BorderLayout.EAST);
    
    action.actionPerformed(null);
    return aux;
  }
  
  private Component getFileComponent(DynamicField field, ActionListener okButtonValidator) {
    Container aux = SwingUtils.newPanel();
    aux.setLayout(new BorderLayout());
    
    JTextField jtfFileName = new JTextField();
    jtfFileName.setEditable(false);
    
    MultiActionListener multiActionListener = new MultiActionListener();
    multiActionListener.addListener(new FileSelectedAction(jtfFileName,field));
    multiActionListener.addListener(okButtonValidator);
    
    aux.add(jtfFileName, BorderLayout.CENTER);
    
    Component jcbReload = SwingUtils.newButton("...", multiActionListener); //$NON-NLS-1$
    aux.add(jcbReload, BorderLayout.EAST);
    return aux;
  }
  
  private JComponent getCheckboxComponent(DynamicField field, ActionListener okButtonValidator) {
    JCheckBox jcb = new JCheckBox();
    Boolean val = (Boolean)field.getValue();
    jcb.setSelected(null == val?false:val);
    MultiActionListener multiActionListener = new MultiActionListener();
    multiActionListener.addListener(new DynamicAbstractButtonListener(jcb, field));
    multiActionListener.addListener(okButtonValidator);
    jcb.addActionListener(multiActionListener);
    return jcb;
  }
  
  private JComponent getDateComponent(DynamicField field, PropertyChangeListener okButtonValidator) {
    JDateChooser dateChooser = new JDateChooser("dd/MM/yyyy", "##/##/####", ' '); //$NON-NLS-1$ //$NON-NLS-2$
    MultiPropertyChangeListerner multiListener = new MultiPropertyChangeListerner();
    multiListener.addPropertyChangeListener(new DynamicDateChangeListener(dateChooser, field));
    multiListener.addPropertyChangeListener(okButtonValidator);
    dateChooser.addPropertyChangeListener("date", multiListener); //$NON-NLS-1$
    return dateChooser;
  }
  
  private static class MyComboBoxRenderer extends BasicComboBoxRenderer {
    private static final long serialVersionUID = -7278868658182801547L;

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (isSelected) {
        if (-1 < index && value != null) {
          IDEntry entry = (IDEntry) value;
          Provider prov = entry.getProvider();
          String tooltip = Messages.getString("PreviewerDialog.8"); //$NON-NLS-1$
          if(null != prov)
            tooltip = prov.getName().replace("SunPKCS11-", ""); //$NON-NLS-1$ //$NON-NLS-2$
          list.setToolTipText(tooltip);
        }
      }
      return c;
    }
  }

  public TaskStatus openDialog() {

    if(this.guiRequired) {
      // reset internal state
      updateOkButtonState();
      cancelButton.setEnabled(true);

      // do some computation
      pack();

      setSize(400, 300);

      // set window location
      setLocationByPlatform(true);

      setVisible(true);

      //    
      //    // simulate modality
      //    while(dialog.isVisible()) {
      //      try {
      //        Thread.sleep(100);
      //      } catch (InterruptedException e) {
      //      }
      //    }
      //    
      //    
    } else {
      actionPerformed(new ActionEvent(okButton, 0, ACTION_OK));
    }
    return this.status;
  }

  
  public void actionPerformed(ActionEvent e) {
    System.out.println(task.getState());
    if(ACTION_OK.equals(e.getActionCommand())) {
      okButton.setEnabled(false);
      // start signature process
      task.execute();
    } else if (ACTION_CANCEL.equals(e.getActionCommand())) {
      if(StateValue.STARTED.equals(task.getState()))
        task.cancel(true);
      cancelButton.setEnabled(false);
      task.firePropertyChange(SwingTask.COMPLETE, CANCEL, OK);
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();
    
    // Listen to these events
    if(SwingTask.START_TASK.equals(propertyName)) {
      okButton.setEnabled(false);
      progressBar.setIndeterminate(true);
    } else if(SwingTask.PROGRESS_CHANGE.equals(propertyName)) {
      progressBar.setIndeterminate(false);
      progressBar.setStringPainted(true);
      ProgressEvent pEvt = (ProgressEvent) evt.getNewValue();
      progressBar.setString(pEvt.getNote());
      progressBar.setValue(pEvt.getProgress());
      
    } else if(SwingTask.ERROR_OCCURED.equals(propertyName)) {
      log.error("Error property detected: "+evt.getNewValue(), (Throwable)evt.getOldValue()); //$NON-NLS-1$
      progressBar.setString((String) evt.getNewValue());
      progressBar.setValue(0);
      status.setStatus(TaskStatus.ERROR);
      
      // popup error display
      JOptionPane.showMessageDialog(this, evt.getNewValue(), Messages.getString("PreviewerDialog.12"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
      
      closeDialog();
      
    } else if(SwingTask.COMPLETE.equals(propertyName)) {
      status.setStatus(TaskStatus.COMPLETE);
      status.setResult(task.getResult());
      closeDialog();
    }
  }
  
  private void closeDialog() {
    cancelButton.setEnabled(false);
    progressBar.setIndeterminate(false);
    this.dispose();
    
    // trigger a timeout script
    applet.removeTask(status);
    applet.executeScript("setTimeout('updateForm(\\'"+status.getJSON()+"\\')', 100);"); //$NON-NLS-1$ //$NON-NLS-2$
  }
  
  private void updateOkButtonState() {
    String fileValidation = fileProvider.validateForm();
    String signerValidation = signer.validateForm();
    String cipherValidation = cipher.validateForm();
    
    log.debug("  File Validadion: '" + fileValidation + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    log.debug("Signer Validadion: '" + signerValidation + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    log.debug("Cipher Validadion: '" + cipherValidation + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    
    okButton.setEnabled(fileValidation == null && signerValidation == null && cipherValidation == null);
    if (fileValidation != null) {
      progressBar.setString(fileValidation);
    } else if (signerValidation != null) {
      progressBar.setString(signerValidation);
    } else if (cipherValidation != null) {
      progressBar.setString(cipherValidation);
    }
  }
  
  private class FileSelectedAction implements ActionListener {
    private DynamicField fld;
    private JTextField jtfFileName;
    
    public FileSelectedAction(JTextField jtfFileName, DynamicField fld) {
      this.fld = fld;
      this.jtfFileName = jtfFileName;
    }
    
    public void actionPerformed(ActionEvent e) {
      IVFile f = fileProvider.chooseFile(PreviewerDialog.this);
      if(null == f) return;
      jtfFileName.setText(f.getName());
      fld.setValue(f);
    }
  }
  
  private class UpdateOkButtonStateListener implements ActionListener, DocumentListener, PropertyChangeListener {
    public void actionPerformed(ActionEvent e) {
      updateOkButtonState();
    }
    public void changedUpdate(DocumentEvent e) {
      updateOkButtonState();
    }
    public void insertUpdate(DocumentEvent e) {
      updateOkButtonState();
    }
    public void removeUpdate(DocumentEvent e) {
      updateOkButtonState();
    }
    public void propertyChange(PropertyChangeEvent evt) {
      updateOkButtonState();
    }
  }

  
  public static void main(String [] args) {
    FileSigner signer = SignatureType.getFileSigner("PDF", null); //$NON-NLS-1$
    FileCipher cipher = CipherType.getFileCipher("NONE", null); //$NON-NLS-1$
    
    PreviewerDialog dialog = new PreviewerDialog(null, null, signer, cipher, new FileDialogProvider("teste", false)); //$NON-NLS-1$
    
    TaskStatus status = dialog.openDialog();
    
    while(status.getStatus()!=TaskStatus.COMPLETE) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
    }
    System.out.println("Result: "+status.toString()); //$NON-NLS-1$
    System.exit(0);
  }

}
