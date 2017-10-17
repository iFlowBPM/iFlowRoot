package pt.iflow.applet;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import pt.iflow.applet.cipher.CipherType;
import pt.iflow.applet.cipher.FileCipher;
import pt.iflow.applet.signer.FileSigner;
import pt.iflow.applet.signer.SignatureType;

import com.infosistema.crypto.CryptoUtils;

/**
 * Esta é uma applet invisível que disponibiliza um conjunto de métodos para carregamento, actualização e verificação de ficheiros.
 * A api é suficientemente genérica para suportar operações de criptografia como assinaturas digitais e (des)encriptação de ficheiros.
 * Também suporta desencriptação de dados de processo que tenham sido previamente encriptados.
 * 
 * @author Óscar Lopes
 *
 */
public class UtilityApplet extends JApplet implements UtilityConstants {

  private static Log log = LogFactory.getLog(UtilityApplet.class);

  private static final long serialVersionUID = 587357588158357570L;
  
  public static final String UPLOAD_URL_APPLET_PARAM = "uploadUrl"; //$NON-NLS-1$
  public static final String DOWNLOAD_URL_APPLET_PARAM = "downloadUrl"; //$NON-NLS-1$
  public static final String DEFAULT_DOCUMENT_URL = "DocumentService"; //$NON-NLS-1$

  public static final String SIGNATURE_SERVICE = "SignatureService"; //$NON-NLS-1$
  public static final String RUBRIC_SERVICE = "RubricService"; //$NON-NLS-1$
  
  protected static final Icon OK_ICON;
  protected static final Icon WARN_ICON;
  protected static final Icon ERROR_ICON;
  protected static final Icon LOAD_ICON;
  
  private URL BaseURL = null;
  
  protected Map<String,TaskStatus> tasks = new HashMap<String,TaskStatus>();
  
  static {
    OK_ICON = new ImageIcon(getImage("/ok.png")); //$NON-NLS-1$
    WARN_ICON = new ImageIcon(getImage("/warn.png")); //$NON-NLS-1$
    ERROR_ICON = new ImageIcon(getImage("/error.png")); //$NON-NLS-1$
    LOAD_ICON = new ImageIcon(getImage("/reload.png")); //$NON-NLS-1$
  }
  
  private static URL getImage(String name) {
    URL url = null;
    Class<?> c = UtilityApplet.class;
    url = c.getResource(name);
    return url;
  }

  private String uploadUrl;
  private String downloadUrl;
  private String signatureUrl;
  private String rubricUrl;
  
  /**
   * Inicialização da applet. Colocar aqui código de activação de scanner, etc.
   */
  public void init() {
    super.init();
    String langStr = null;//getParameter(LANG_PARAM);
    log.debug("Locale str: "+langStr); //$NON-NLS-1$
    if(null != langStr && !"".equals(langStr.trim())) {
      String [] tokens = langStr.split("_");
      Locale loc = Locale.getDefault();
      
      if(tokens.length == 1)
        loc = new Locale(tokens[0]);
      else if(tokens.length == 2)
        loc = new Locale(tokens[0], tokens[1]);
      else if(tokens.length > 2)
        loc = new Locale(tokens[0], tokens[1], tokens[2]);
      
      Locale.setDefault(loc);
    }
    log.debug("Current locale: "+Locale.getDefault()); //$NON-NLS-1$
    Messages.reset();
    CryptoUtils.loadProviders();
    JFrame.setDefaultLookAndFeelDecorated(true);
    
    // Get and Install native look and feel
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (InstantiationException e) {
      log.debug("could not set native look and feel", e); //$NON-NLS-1$
    } catch (ClassNotFoundException e) {
      log.debug("could not set native look and feel", e); //$NON-NLS-1$
    } catch (UnsupportedLookAndFeelException e) {
      log.debug("could not set native look and feel", e); //$NON-NLS-1$
    } catch (IllegalAccessException e) {
      log.debug("could not set native look and feel", e); //$NON-NLS-1$
    }

    // Ler como parametro, uma vez que tem que ser codificado pelo tomcat
    uploadUrl = "DocumentService";//getParameter(UPLOAD_URL_APPLET_PARAM,DEFAULT_DOCUMENT_URL);
    downloadUrl = "DocumentService";//getParameter(DOWNLOAD_URL_APPLET_PARAM,DEFAULT_DOCUMENT_URL);
    
    signatureUrl = SIGNATURE_SERVICE;
    rubricUrl = RUBRIC_SERVICE;
    
    log.debug("init complete"); //$NON-NLS-1$
  }

  /**
   * Arranque da applet. Método chamado quando a applet termina o processo de inicialização e está pronta a ser apresentada ao utilizador.
   */
  public void start() {
    super.start();
    log.debug("start complete"); //$NON-NLS-1$
    Runtime runtime = Runtime.getRuntime();
    System.out.println(" Free memory: "+runtime.freeMemory()); //$NON-NLS-1$
    System.out.println("Total memory: "+runtime.totalMemory()); //$NON-NLS-1$
    System.out.println("  Max memory: "+runtime.maxMemory()); //$NON-NLS-1$
    System.out.println("Running:"); //$NON-NLS-1$
    System.out.println("  "+System.getProperty("java.vm.name")+" - " + System.getProperty("java.vm.version")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    System.out.println("  "+System.getProperty("java.vm.vendor")); //$NON-NLS-1$ //$NON-NLS-2$
    System.out.println("  "+System.getProperty("os.name")+" (" + System.getProperty("os.arch")+") " + System.getProperty("os.version")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    
    
  }

  /**
   * Término da applet. Chamado quando a página do browser muda ou é fechada. Usado para libertar recursos.
   */
  public void stop() {
    super.stop();
    CryptoUtils.releaseProviders();
    SwingUtils.dispose();
    log.debug("stop complete"); //$NON-NLS-1$
  }

  /**
   * Versão do código da applet
   * @return String com o número de versão da applet
   */
  public String getVersion() {
    log.debug("getVersion called"); //$NON-NLS-1$
    return Version.getVersion();
  }

  /**
   * Check if the applet can scan a file using file format
   * 
   * @param fileFormat file format to test or <code>null</code> to test any
   * @return <code>true</code> if can scan, <code>false</code> otherwise.
   */
  public boolean canScan(final String fileFormat) {
    log.info("canScan started"); //$NON-NLS-1$
    boolean result = false;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
        public Boolean run() {
          return doCanScan(fileFormat); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("canScan complete"); //$NON-NLS-1$
    return result;
  }

  /**
   * Check if the applet can create a signature using algorithm
   * 
   * @param signatureType algorithm to test
   * @return <code>true</code> if can sign, <code>false</code> otherwise.
   */
  public boolean canSign(final String signatureType) {
    log.info("canSign started"); //$NON-NLS-1$
    boolean result = false;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
        public Boolean run() {
          return doCanSign(signatureType); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("canSign complete"); //$NON-NLS-1$
    return result;  
  }

  /**
   * Check if the applet can encrypt using algorithm
   * 
   * @param encryptType algorithm to test
   * @return <code>true</code> if can encrypt, <code>false</code> otherwise.
   */
  public boolean canEncrypt(final String encryptType) {
    log.info("canEncrypt started"); //$NON-NLS-1$
    boolean result = false;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
        public Boolean run() {
          return doCanEncrypt(encryptType); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("canEncrypt complete"); //$NON-NLS-1$
    return result; 
  }


  // Métodos de processo

  /**
   * Carregamento de ficheiros para o iFlow
   * 
   * @param cookie page cookie
   * @param flowid Identificador do fluxo
   * @param pid Identificador do processo
   * @param subpid Identificador do subprocesso
   * @param varName Variável onde será guardado o ficheiro
   * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
   * @param encrptionType Tipo de encriptação a efectuar (NONE, PKI)
   * 
   * @return Identificador do ficheiro carregado
   */
  public String uploadFile(final String cookie, final String jsonRequest, final String sig_pos_style_java) {
    log.debug("uploadFile called: "+jsonRequest); //$NON-NLS-1$
    String result = null;
    log.info("Signature_Position_style from javascript, value: "+sig_pos_style_java);
    LoadImageAction.setSignature_position_style(sig_pos_style_java);
    
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          try {
            return doUploadFile(createWebClient(cookie, jsonRequest));
          } catch (Throwable t) {
            log.error("Error occurred.", t); //$NON-NLS-1$
          }
          return null;
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("uploadFile complete"); //$NON-NLS-1$
    return result; 
  }

  public String uploadFileFromDisk(final String cookie, final String jsonRequest, final byte[] byteArr, final String filename, final String sig_pos_style_java) {
    String result = null;
    LoadImageAction.setSignature_position_style(sig_pos_style_java);
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          try {
            return doUploadFileFromDisk(createWebClient(cookie, jsonRequest), filename, byteArr);
          } catch (Throwable t) {
            log.error("Error occurred.", t); //$NON-NLS-1$
          }
          return null;
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    return result; 
  }

  /**
   * Carregamento de ficheiros para o iFlow com pré-visualização
   * 
   * @param cookie page cookie
   * @param flowid Identificador do fluxo
   * @param pid Identificador do processo
   * @param subpid Identificador do subprocesso
   * @param varName Variável onde será guardado o ficheiro
   * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
   * @param encrptionType Tipo de encriptação a efectuar (NONE, PKI)
   * 
   * @return Identificador do ficheiro carregado
   */
  public String previewFile(final String cookie, final String jsonRequest) {
    log.debug("previewFile called: "+jsonRequest); //$NON-NLS-1$
    String result = null;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          try {
            return doPreviewFile(createWebClient(cookie, jsonRequest));
          } catch (Throwable t) {
            log.error("Error occurred.", t); //$NON-NLS-1$
          }
          return null;
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("previewFile complete"); //$NON-NLS-1$
    return result; 
  }

  /**
   * Modificar ficheiro existente. Permite reassinar ou encriptar um documento existente na base de dados.
   * 
   * @param cookie page cookie
   * @param flowid Identificador do fluxo
   * @param pid Identificador do processo
   * @param subpid Identificador do subprocesso
   * @param fileId Identificador do ficheiro
   * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
   * @param encrptionType Tipo de encriptação a efectuar (NONE, PKI)
   * 
   * @return Identificador do ficheiro carregado
   */
  public String modifyFile(final String cookie, final String jsonRequest, final String sig_pos_style_java) {
    log.debug("modifyFile called: "+jsonRequest); //$NON-NLS-1$
    String result = null;
    
    log.info("Signature_Position_style from javascript, value: "+sig_pos_style_java);
    LoadImageAction.setSignature_position_style(sig_pos_style_java);
    
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          return doModifyFile(createWebClient(cookie, jsonRequest)); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("modifyFile complete"); //$NON-NLS-1$
    return result; 
  }

  /**
   * Modificar ficheiro existente. Permite reassinar ou encriptar um documento existente na base de dados.
   * 
   * @param cookie page cookie
   * @param flowid Identificador do fluxo
   * @param pid Identificador do processo
   * @param subpid Identificador do subprocesso
   * @param fileId Identificador do ficheiro
   * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
   * @param encrptionType Tipo de encriptação a efectuar (NONE, PKI)
   * 
   * @return Identificador do ficheiro carregado
   */
  public String replaceFile(final String cookie, final String jsonRequest, final String sig_pos_style_java) {
    log.debug("replaceFile called: "+jsonRequest); //$NON-NLS-1$
    String result = null;
    
    log.info("Signature_Position_style from javascript, value: "+sig_pos_style_java);
    LoadImageAction.setSignature_position_style(sig_pos_style_java);
    
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          return doReplaceFile(createWebClient(cookie, jsonRequest)); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("replaceFile complete"); //$NON-NLS-1$
    return result; 
  }

  /**
   * Descarregar documento existente. Desencriptar um documento previamente encriptado ou simplesmente efectuar o download do mesmo.
   * 
   * @param cookie page cookie
   * @param flowid Identificador do fluxo
   * @param pid Identificador do processo
   * @param subpid Identificador do subprocesso
   * @param fileId Identificador do ficheiro
   * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
   * @param encrptionType Tipo de encriptação a efectuar (NONE, PKI)
   * 
   * @return Identificador do ficheiro carregado
   */
  public String downloadFile(final String cookie, final String jsonRequest) {
    log.debug("downloadFile called: "+jsonRequest); //$NON-NLS-1$
    String result = null;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          return doDownloadFile(createWebClient(cookie, jsonRequest)); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("downloadFile complete"); //$NON-NLS-1$
    return result;
  }

  /**
   * Verifica a assinatura digital de um determinado documento.
   * 
   * @param cookie page cookie
   * @param flowid Identificador do fluxo
   * @param pid Identificador do processo
   * @param subpid Identificador do subprocesso
   * @param fileId Identificador do ficheiro
   * 
   * @return Mensagem com resultado da validação
   */
  public String verifiyFile(final String cookie, final String jsonRequest) {
    log.debug("verifiyFile called: "+jsonRequest); //$NON-NLS-1$
    String result = null;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          return doVerifiyFile(createWebClient(cookie, jsonRequest)); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("verifiyFile complete"); //$NON-NLS-1$
    return result; 
  }

  /**
   * Apaga um documento carregado previamente
   * 
   * @param cookie page cookie
   * @param flowid Identificador do fluxo
   * @param pid Identificador do processo
   * @param subpid Identificador do subprocesso
   * @param fileId Identificador do ficheiro
   * 
   * @return Mensagem com resultado da operação
   */
  public String removeFile(final String cookie, final String jsonRequest) {
    log.debug("removeFile called: "+jsonRequest); //$NON-NLS-1$
    String result = null;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          return doRemoveFile(createWebClient(cookie, jsonRequest)); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("removeFile complete"); //$NON-NLS-1$
    return result; 
  }

  /**
   * Scan e Carregamento de ficheiros para o iFlow
   * 
   * @param cookie page cookie
   * @param flowid Identificador do fluxo
   * @param pid Identificador do processo
   * @param subpid Identificador do subprocesso
   * @param varName Variável onde será guardado o ficheiro
   * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
   * @param encrptionType Tipo de encriptação a efectuar (NONE, PKI)
   * @param fileFormat Formato do ficheiro (PDF, JPG, TIFF, etc)
   * 
   * @return Identificador do ficheiro carregado
   */
  public String scanFile(final String cookie, final String jsonRequest) {
    log.debug("scanFile called: "+jsonRequest); //$NON-NLS-1$
    String result = null;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          return doScanFile(createWebClient(cookie, jsonRequest)); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("scanFile complete"); //$NON-NLS-1$
    return result; 
  }

  // Gestão de certificados

  /**
   * Carregamento e/ou substituição de certificados digitais de utilizador
   * 
   * @param cookie page cookie
   * @param userid Identificador do utilizador
   * @param certificateType Tipo de certificado (autenticação, encriptação, assinatura)
   * 
   * @return 
   */
  public JSONObject setCertificate(final String cookie, final String jsonRequest) {
    log.debug("setCertificate called: "+jsonRequest); //$NON-NLS-1$
    JSONObject result = null;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<JSONObject>() {
        public JSONObject run() {
          return doSetCertificate(createWebClient(cookie, jsonRequest)); 
        }
      });
    } catch (PrivilegedActionException e) {
      log.error("Privilegios insuficientes para executar a operacao pretendida", e); //$NON-NLS-1$
    }
    log.info("setCertificate complete"); //$NON-NLS-1$
    return result; 
  }

  
  public String getTaskStatus(String taskId) {
    log.debug("getTaskStatus called for task: "+taskId); //$NON-NLS-1$
    String result = doGetTaskStatus(taskId);
    log.debug("getTaskStatus result: "+result); //$NON-NLS-1$
    return result;
  }
  
  
  //////////////////////
  // Implementacoes
  
  private String doGetTaskStatus(String taskId) {
    if(StringUtils.isBlank(taskId)) return TaskStatus.getInvalidStatus();
    TaskStatus status = this.tasks.get(taskId);
    if(null == status) return TaskStatus.getInvalidStatus();
    return status.toString();
  }
  
  
  private boolean doCanScan(String fileFormat) {
    log.debug("canScan called: "+fileFormat); //$NON-NLS-1$
    return false;
  }

  private boolean doCanSign(String signatureType) {
    log.debug("canSign called: "+signatureType); //$NON-NLS-1$
    boolean canSign = false;
    canSign = SignatureType.getFileSigner(signatureType, null).isActive();
    log.debug("canSign complete"); //$NON-NLS-1$
    return canSign;
  }

  private boolean doCanEncrypt(String encryptType) {
    log.debug("canEncrypt called: "+encryptType); //$NON-NLS-1$
    boolean canEncrypt = false;
    canEncrypt = CipherType.getFileCipher(encryptType, null).isActive();
    log.debug("canEncrypt complete"); //$NON-NLS-1$
    return canEncrypt;
  }


  // Métodos de processo

  private String doUploadFile(final WebClient webClient) {
    FileSigner signer = SignatureType.getFileSigner(webClient);
    FileCipher cipher = CipherType.getFileCipher(webClient);
    DynamicDialog dialog = new DynamicDialog(this, webClient, signer, cipher, new FileDialogProvider(webClient.getVariable(), false));
    TaskStatus task = dialog.openDialog();
    this.tasks.put(task.getTaskId(), task);
    return task.getTaskId();
  }

  private String doUploadFileFromDisk(final WebClient webClient, String fileName, byte[] byteArr) {
    FileSigner signer = SignatureType.getFileSigner(webClient);
    FileCipher cipher = CipherType.getFileCipher(webClient);
    IVFile ivFile = null;
    String[] fileNameAux = fileName.split("\\.");
    fileName = fileNameAux.length > 0 ? fileNameAux[0] + "_" : "tmp_";  
    String fileExt = fileNameAux.length > 1 ? "." + fileNameAux[1] : "";  

    File theFile = null;
    try {
      theFile = File.createTempFile(fileName, fileExt);
      FileOutputStream stream = new FileOutputStream(theFile);
      stream.write(byteArr);
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    
    if (theFile != null && theFile.canRead() && theFile.isFile())
      ivFile = new FileVFile(theFile, webClient.getVariable());
    
    DynamicDialog dialog = new DynamicDialog(this, webClient, signer, cipher, new FileDialogProvider(webClient.getVariable(), false, ivFile));
    TaskStatus task = dialog.openDialog();
    this.tasks.put(task.getTaskId(), task);
    return task.getTaskId();
  }

  private String doPreviewFile(final WebClient webClient) {
    final FileSigner signer = SignatureType.getFileSigner(webClient);
    final FileCipher cipher = CipherType.getFileCipher(webClient);
    
    ViewerSwingWorker w = new ViewerSwingWorker(this, signer, cipher, webClient);
    w.execute();
    return w.getWorkerId();
  }

  private String doReplaceFile(final WebClient webClient) {
    FileSigner signer = SignatureType.getFileSigner(webClient);
    FileCipher cipher = CipherType.getFileCipher(webClient);
    DynamicDialog dialog = new DynamicDialog(this, webClient, signer, cipher, new FileDialogProvider(webClient.getVariable(), true));
    TaskStatus task = dialog.openDialog();
    this.tasks.put(task.getTaskId(), task);
    return task.getTaskId();
  }

  private String doModifyFile(final WebClient webClient) {
    FileSigner signer = SignatureType.getFileSigner(webClient);
    FileCipher cipher = CipherType.getFileCipher(webClient);
    DynamicDialog dialog = new DynamicDialog(this, webClient, signer, cipher, new DownloadFileProvider(webClient));
    dialog.setMinimumSize(new Dimension(250,350));
    TaskStatus task = dialog.openDialog();
    this.tasks.put(task.getTaskId(), task);
    return task.getTaskId();
  }

  private String doDownloadFile(final WebClient webClient) {
    return null;
  }

  private String doVerifiyFile(final WebClient webClient) {
    return null;
  }

  private String doRemoveFile(final WebClient webClient) {
    String result = null;
    Map<String,String> params = webClient.getParameters();
    params.put(WebClient.REMOVE_PARAM, "true"); //$NON-NLS-1$
    result = webClient.getString(params);
    log.debug("doRemoveFile result: '"+result+"'"); //$NON-NLS-1$ //$NON-NLS-2$
    return result==null?null:result.trim();
  }

  private String doScanFile(final WebClient webClient) {
    FileSigner signer = SignatureType.getFileSigner(webClient);
    FileCipher cipher = CipherType.getFileCipher(webClient);

    
    IVFile theFile = SwingUtils.showOpenFileDialog(this, 
        Messages.getString("UtilityApplet.0"), //$NON-NLS-1$
        new ExtensionFileFilter(Messages.getString("UtilityApplet.40"), new String[] { "PDF" }), //$NON-NLS-1$ //$NON-NLS-2$
        webClient.getVariable()
    );
    log.debug("The file is a lie: "+theFile); //$NON-NLS-1$
    
    DynamicDialog dialog = new DynamicDialog(this, webClient, signer, cipher, new FileDialogProvider(webClient.getVariable(), false));
    TaskStatus task = dialog.openDialog();
    this.tasks.put(task.getTaskId(), task);
    return task.getTaskId();
  }

  // Gestão de certificados

  private JSONObject doSetCertificate(final WebClient webClient) {
    return null;
  }

  
  // Utility methods
  protected String getParameter(String param, String Default) {
    String val = getParameter(param);
    if(null == val || "".equals(val.trim())) { //$NON-NLS-1$
      return Default;
    }
    return val;
  }
  

  protected JSONObject parseJSON(String jsonString) {
    JSONObject jsonObject = null;
    try {
      jsonObject = new JSONObject(jsonString);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject;
  }

  
  protected WebClient createWebClient(final String cookie, final String request) {
    WebClient webClient = new WebClient(cookie, parseJSON(request));    
    URL auxURL = getBaseURL();
	if(auxURL==null)
		auxURL = getDocumentBase();
    webClient.setBaseURL(auxURL);					    
    webClient.setDownloadLocation(downloadUrl);
    webClient.setUploadLocation(uploadUrl);
    webClient.setSignatureServiceLocation(signatureUrl);
    webClient.setRubricServiceLocation(rubricUrl);
    return webClient;
  }

  protected void executeScript(String script) {
    log.debug("Evaluating script: '"+script+"'"); //$NON-NLS-1$ //$NON-NLS-2$
    try {
      JSObject.getWindow(this).eval(script);
    } catch (Throwable t) {
      log.warn("Script execution failed.", t); //$NON-NLS-1$
    }
  }

  protected void removeTask(TaskStatus status) {
    this.tasks.remove(status.getTaskId());
  }

public URL getBaseURL() {
	return BaseURL;
}

public void setBaseURL(URL baseURL) {
	BaseURL = baseURL;
}

}
