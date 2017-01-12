package pt.iflow.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.security.Provider;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

import com.toedter.calendar.JDateChooser;

import pt.iflow.applet.cipher.FileCipher;
import pt.iflow.applet.signer.FileSigner;
import pt.iflow.applet.signer.PDFSignatureImpl;

public class DynamicDialog extends JFrame implements PropertyChangeListener, ActionListener, MouseListener {
  private static final long serialVersionUID = -1502926852420989215L;

  private static Log log = LogFactory.getLog(DynamicDialog.class);
  
  public static final int CANCEL = 0;
  public static final int OK = 1;
  
  public static final String ACTION_OK = "OK"; //$NON-NLS-1$
  public static final String ACTION_CANCEL = "CANCEL"; //$NON-NLS-1$
  public static boolean toLoad = false;
  public static boolean haveCert = false;
  private Component okButton;
  private Component cancelButton;
  private JProgressBar progressBar;
  private final Container formPanel;
  private final UtilityApplet applet;
  private final FileSigner signer;
  private final FileCipher cipher;
  private final SwingTask task;
  private final TaskStatus status;
  private final IVFileProvider fileProvider;
  private final WebClient webClient;
  private boolean guiRequired;
  JLabel labelAss = new JLabel();
  JTextField pagInd = new JTextField("0/0");
  
  Container contGlobalPos = SwingUtils.newPanel();
  ImagePanel imagePDF = null;
  static int imagePDFalturaPag = 420;
  static int imagePDFlarguraPag = 297;
  String filePath = "";
  int pagActual = -1;
  int pagTotal = -1;
  File ficheiro = null;
   
  
  public DynamicDialog(UtilityApplet applet, WebClient webClient, FileSigner signer, FileCipher cipher, IVFileProvider fileProvider) {
 
    this.applet = applet;
    this.signer = signer;
    this.cipher = cipher;
    this.fileProvider = fileProvider;
    this.webClient = webClient;
    this.status = new TaskStatus(UUID.randomUUID().toString());
    this.task = new SwingTask(this);
    this.task.addPropertyChangeListener(this);
    this.guiRequired = false;
    this.formPanel = createFormPanel();
    
    setTitle(Messages.getString("DynamicDialog.2")); //$NON-NLS-1$
    Container container = getContentPane();
    container.setLayout(new BorderLayout());
    add(this.formPanel, BorderLayout.CENTER);
    
    
    if(!LoadImageAction.posMatriz){
    	Component jcbReload = getPDFSampleComponent();
    	add(jcbReload, BorderLayout.EAST);
    }

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
    
    // Instanciar bot�es
    okButton = SwingUtils.newButton(Messages.getString("DynamicDialog.3"), ACTION_OK, this); //$NON-NLS-1$
    cancelButton = SwingUtils.newButton(Messages.getString("DynamicDialog.4"), ACTION_CANCEL, this); //$NON-NLS-1$
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
      toLoad = true;
    }else{toLoad = false;}
    
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
    
    
    // Adicionar bot�es
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
    if(null == fileProvider)return null; 
    DynamicField field = fileProvider.getDynamicField();
    if(null == field) return null;
    Component component = getFileComponent(field, new UpdateOkButtonStateListener());
    if (null == component) return null;
    Container panel = SwingUtils.newPanel();
    panel.setLayout(new GridBagLayout());
    // ainda n�o tenho nada alinhavado para isto....
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
      // ainda n�o tenho nada alinhavado para isto....
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
      case IMAGE:
          component = getImageComponent(field, actionValidator);
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
    Component jcbReload = SwingUtils.newButton(Messages.getString("DynamicDialog.6"), action); //$NON-NLS-1$
    aux.add(jcbReload, BorderLayout.EAST);
    
    action.actionPerformed(null);
    return aux;
  }
  
  private Component getFileComponent(DynamicField field, ActionListener okButtonValidator) {
    Container aux = SwingUtils.newPanel();
    aux.setLayout(new BorderLayout());
    
    JTextField jtfFileName = new JTextField();
    jtfFileName.setEditable(false);
    jtfFileName.setColumns(120);    
    
    MultiActionListener multiActionListener = new MultiActionListener();
    multiActionListener.addListener(new FileSelectedAction(jtfFileName,field));
    multiActionListener.addListener(okButtonValidator);
    
    aux.add(jtfFileName, BorderLayout.CENTER);
    Component jcbReload = SwingUtils.newButton(Messages.getString("DynamicDialog.7"), multiActionListener); //$NON-NLS-1$
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
  
  private Component getImageComponent(DynamicField field, ActionListener okButtonValidator) {
	    
	    //Container com as checkbox
	    Container contCB = SwingUtils.newPanel();
	    contCB.setLayout(new BorderLayout());
	    JCheckBox jcbUtil = new JCheckBox();
	    jcbUtil.setEnabled(false);
	    jcbUtil.setText(Messages.getString("SignatureImage.1")); 
	    contCB.add(jcbUtil);
	    
	    //Check para guardar imagem na bd
	    Container contCBsave = SwingUtils.newPanel();
	    contCBsave.setLayout(new BorderLayout());
	    JCheckBox jcbSave = new JCheckBox();
	    jcbSave.setEnabled(false);
	    jcbSave.setText(Messages.getString("SignatureImage.5")); 	  
	    contCBsave.add(jcbSave);
	    
	    //Check para rubricar todas as paginas
	    Container contCBrub = SwingUtils.newPanel();
	    contCBrub.setLayout(new BorderLayout());
	    JCheckBox jcbRub = new JCheckBox();
	    jcbRub.setEnabled(false);
	    jcbRub.setText(Messages.getString("SignatureImage.6")); 	  
	    contCBrub.add(jcbRub);
	    
	    	    
	    //Container com o botao e a imagem
	    Container contImg = SwingUtils.newPanel();
	    contImg.setLayout(new BorderLayout());
		FileDialog fd = new FileDialog(new Frame(), Messages.getString("SignatureImage.2"), FileDialog.LOAD);
	    ActionListener action = new LoadImageAction(fd,contImg,jcbUtil,jcbSave,jcbRub);
	    Component loadimage = SwingUtils.newButton(Messages.getString("SignatureImage.3"),action); 
	    contImg.add(loadimage, BorderLayout.EAST);
	    
	    //Container geral com os 3 anteriores
	    Container contGlobal = SwingUtils.newPanel();
	    contGlobal.setLayout(new BorderLayout());
	    contGlobal.add(contImg,BorderLayout.NORTH);
	    contGlobal.add(contCB,BorderLayout.WEST);
	    contGlobal.add(contCBsave,BorderLayout.CENTER);
	    
	    //Container com check de guardar e rubricar por causa do layout
	    Container contTemp = SwingUtils.newPanel();
	    contTemp.setLayout(new BorderLayout());   
	    contTemp.add(contCBsave,BorderLayout.WEST);
	    contTemp.add(contCBrub,BorderLayout.CENTER);
	    
	    contGlobal.add(contTemp,BorderLayout.CENTER);
	    
	    ImageIconRep imgBD = null;
	    ImageIconRep rubBD = null;
	    //Meter imagem devolvida da BD
		try {
			imgBD = this.task.getImageFromDB();
			} catch (Exception e) {	e.printStackTrace();}
		if(imgBD != null)
			LoadImageAction.setImageFromBD(imgBD);
	    
		if(!LoadImageAction.rubimgSameass){
			try {
				rubBD = this.task.getRubricImageFromDB();
				} catch (Exception e) {	e.printStackTrace();}
			if(rubBD != null)
				LoadImageAction.setRubricImageFromBD(rubBD);
		}
			
	    return contGlobal;
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
          String tooltip = Messages.getString("DynamicDialog.8"); //$NON-NLS-1$
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

      if(LoadImageAction.posMatriz){
		         setSize(600, 485);
      }else{
	      if(toLoad)
		         setSize(600, 485);
		  else
		         setSize(900,485);	  
      }
      // set window location
      setLocationByPlatform(true);

      setVisible(true);

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
      JOptionPane.showMessageDialog(this, evt.getNewValue(), Messages.getString("DynamicDialog.12"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
      
      closeDialog();
      
    } else if(SwingTask.COMPLETE.equals(propertyName)) {
      status.setStatus(TaskStatus.COMPLETE);
      status.setResult(task.getResult());
      
      if(StringUtils.isNotEmpty(task.getResult())){
            String [] temp = task.getResult().split(",");
          if(StringUtils.isNotEmpty(temp[0])){
              temp = temp[0].split(":");
            if(StringUtils.isNotEmpty(temp[1]))
              applet.executeScript("changeFileState("+temp[1].replace("\"", "")+")");
          } 
      }
      closeDialog();
    }
  }
  
  private void closeDialog() {
    cancelButton.setEnabled(false);
    progressBar.setIndeterminate(false);
    this.dispose();
    
    // trigger a timeout script
    applet.removeTask(status);
    applet.executeScript("setTimeout('updateForm(\\'"+status.getJSON().replace("'", "&apos;")+"\\')', 100);"); //$NON-NLS-1$ //$NON-NLS-2$
  }
  
  private void updateOkButtonState() {
    String fileValidation = fileProvider.validateForm();
    String signerValidation = signer.validateForm();
    String cipherValidation = cipher.validateForm();
    
    log.debug("  File Validadion: '" + fileValidation + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    log.debug("Signer Validadion: '" + signerValidation + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    log.debug("Cipher Validadion: '" + cipherValidation + "'"); //$NON-NLS-1$ //$NON-NLS-2$

    if(LoadImageAction.posMatriz){
    	    okButton.setEnabled(true);
    }else{
	    if(toLoad)
	    	okButton.setEnabled(fileValidation == null);
	    else
	        okButton.setEnabled(fileValidation == null && signerValidation == null && cipherValidation == null);
    }
    
    if(signerValidation == null && cipherValidation == null) haveCert = true;
    else haveCert= false;
    
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
      IVFile f = fileProvider.chooseFile(DynamicDialog.this);
      if(null == f) return;
 
      if(f.getName().length()> 120){
      JOptionPane.showMessageDialog(null, Messages.getString("PopUpFileApplet.122"));
      return;}
      
      jtfFileName.setText(f.getName());
      fld.setValue(f);
      filePath = f.getFile().getAbsolutePath();

      if(!LoadImageAction.posMatriz){
        pagActual = PdfSampleImages.getNumPagesByPath(filePath);
        pagTotal = pagActual;
        reloadPDFsample();
      }
      
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

  private Component getPDFSampleComponent() {
				
	    //Container para escolha da posicao
	    contGlobalPos.setLayout(new BorderLayout());
	    
	    
	    //Botoes de paginacao
	    Container contBotoes = new Container();
	    contBotoes.setLayout(new BorderLayout());
	    JButton pagPosterior = new JButton(">>");
	    JButton pagAnterior = new JButton("<<");
	    pagPosterior.addActionListener(new NextPageAction());
	    pagAnterior.addActionListener(new PrevPageAction());
	    contBotoes.add(pagPosterior,BorderLayout.EAST);
	    contBotoes.add(pagAnterior,BorderLayout.WEST);
	    Container barraNav = new Container();
	    barraNav.setLayout(new BorderLayout());
	    barraNav.add(contBotoes,BorderLayout.WEST);
	    
	    //Indicador de paginas
	    pagInd.setBorder(null);
	    barraNav.add(pagInd, BorderLayout.EAST);
	    
	    contGlobalPos.add(barraNav,BorderLayout.SOUTH);
	    
	    contGlobalPos.setVisible(false);
	    
	    if(!toLoad){
	  	  IVFile d = webClient.getDocument();
		  if(d != null)
			  ficheiro = d.getFile();
	      pagActual = PdfSampleImages.getNumPagesByPath(ficheiro.getAbsolutePath());
	      pagTotal = pagActual;
		  reloadPDFsample(); //caso ja venha o documento
	    }
	    
	    return contGlobalPos; 
		}
		  
private void reloadPDFsample(){
	if(toLoad)
		reloadPDFsampleByPath();
	if(!toLoad)
		reloadPDFsampleByFile();
}  
  
private void reloadPDFsampleByPath(){
	
	  BufferedImage j = PdfSampleImages.getImageFromPdf(filePath, pagActual);

	  LoadImageAction.setPagToSign(pagActual);
	  
      imagePDF = new ImagePanel(j.getScaledInstance(imagePDFlarguraPag, imagePDFalturaPag, Image.SCALE_SMOOTH));
	  imagePDF.addMouseListener(this);

	  contGlobalPos.add(imagePDF,BorderLayout.NORTH,1);
	  this.setSize(900,this.getHeight());
	  pagInd.setEditable(false);
	  pagInd.setText(pagActual+"/"+pagTotal);
	  contGlobalPos.repaint();
	  //contGlobalPos.setVisible(false);
	  contGlobalPos.setVisible(true);
}
	
private void reloadPDFsampleByFile(){

	
	  BufferedImage j = PdfSampleImages.getImageFromPdfByFile(ficheiro, pagActual);

	  LoadImageAction.setPagToSign(pagActual);
	  
      imagePDF = new ImagePanel(j.getScaledInstance(imagePDFlarguraPag, imagePDFalturaPag, Image.SCALE_SMOOTH));
	  imagePDF.addMouseListener(this);

	  contGlobalPos.add(imagePDF,BorderLayout.NORTH,1);
	  this.setSize(900,this.getHeight());
	  this.repaint();
	  pagInd.setEditable(false);
	  pagInd.setText(pagActual+"/"+pagTotal);
	  contGlobalPos.repaint();
	  //contGlobalPos.setVisible(false);
	  contGlobalPos.setVisible(true);
}

private class NextPageAction implements ActionListener {
    
    public NextPageAction() {
    }
    
    public void actionPerformed(ActionEvent e) {
    	if((pagActual+1) <= pagTotal)
    		pagActual = pagActual + 1;
  	  reloadPDFsample();
    }
  }  
private class PrevPageAction implements ActionListener {
    
    public PrevPageAction() {
    }
    
    public void actionPerformed(ActionEvent e) {
    	if((pagActual-1) > 0)
    		pagActual = pagActual - 1;
  	  reloadPDFsample();
    }
  }  

public void mouseClicked(MouseEvent e) {
	int x = e.getX();
	int y = e.getY();
    //Label com imagem da assinatura
	ImageIconRep imgPos = LoadImageAction.getImagePosicao();
	//se 'Usar Imagem' n esta seleccionado mas queremos preview da assinatura em que em vez da imagem temos uma cruz
	if(!LoadImageAction.getUseImageForSignature()){
		int boxLength = 140; 
		if(signer instanceof PDFSignatureImpl){
			try{
				int signLength = ((PDFSignatureImpl)signer).getSignatureText().length();
				if(((PDFSignatureImpl)signer).getContact()!=null && signLength< ((PDFSignatureImpl)signer).getContact().length())
					signLength= ((PDFSignatureImpl)signer).getContact().length();
				if(((PDFSignatureImpl)signer).getLocation()!=null && signLength< ((PDFSignatureImpl)signer).getLocation().length())
					signLength= ((PDFSignatureImpl)signer).getLocation().length();
				if(((PDFSignatureImpl)signer).getReason()!=null && signLength< ((PDFSignatureImpl)signer).getReason().length())
					signLength= ((PDFSignatureImpl)signer).getReason().length();
				boxLength = (int) (signLength * 2.6) ;
			} catch (Exception exc){
				boxLength = 140;
			}
		}			
		BufferedImage signTarget = new BufferedImage(boxLength, 40, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = signTarget.createGraphics();
		g2d.setColor(Color.BLUE);
		int[] xCord = {0,signTarget.getWidth()-5	,signTarget.getWidth()-5	,0					   ,0};
		int[] yCord = {0,0						,signTarget.getHeight()-5	,signTarget.getHeight()-5,0};
		g2d.drawPolyline(xCord, yCord, 5);		
		g2d.dispose();					
		imgPos = new ImageIconRep(signTarget);
	}		
	
    labelAss.setSize(imgPos.getIconWidth(), imgPos.getIconHeight());
    labelAss.setIcon(imgPos);
	labelAss.setLocation(x, y);
    //Adicionar label da assinatura
	imagePDF.add(labelAss);
	LoadImageAction.setAssPos(x, y+15);
}
public void mouseEntered(MouseEvent e) {}
public void mouseExited(MouseEvent e) {}
public void mousePressed(MouseEvent e) {}
public void mouseReleased(MouseEvent e) {}
  




}
