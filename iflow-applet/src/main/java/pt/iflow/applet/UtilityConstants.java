package pt.iflow.applet;

/**
 * Conjunto de constantes para comunicação applet<->javascript
 * @author iKnow
 *
 */
public interface UtilityConstants {
  
  /**
   * ID Fluxo
   */
  public static final String FLOWID = "flowid"; //$NON-NLS-1$
  
  /**
   * ID Processo
   */
  public static final String PID = "pid"; //$NON-NLS-1$
  
  /**
   * ID sub-processo
   */
  public static final String SUBPID = "subpid"; //$NON-NLS-1$
  
  /**
   * ID ficheiro
   */
  public static final String FILEID = "fileid"; //$NON-NLS-1$
  
  /**
   * Nome da variavel de ficheiro
   */
  public static final String VARIABLE = "variable"; //$NON-NLS-1$

  /**
   * Método de assinatura
   */
  public static final String SIGNATURE_TYPE = "signatureType"; //$NON-NLS-1$

  /**
   * Método de encriptação
   */
  public static final String ENCRYPTION_TYPE = "encryptionType"; //$NON-NLS-1$

  /**
   * Formato do ficheiro
   */
  public static final String FILE_FORMAT = "fileFormat"; //$NON-NLS-1$

  /**
   * ID utilizador
   */
  public static final String USERID = "userid"; //$NON-NLS-1$

  /**
   * Tipo de certificado
   */
  public static final String CERTIFICATE_TYPE = "certificateType"; //$NON-NLS-1$

  
  // Tipos de certificado
  
  /**
   * Certificado de assinatura
   */
  public static final String CERT_TYPE_SIGNATURE = "SIGN"; //$NON-NLS-1$
  
  /**
   * Certificado de encriptação
   */
  public static final String CERT_TYPE_ENRCYPTION = "ENCR"; //$NON-NLS-1$

  /**
   * Certificado de autenticação
   */
  public static final String CERT_TYPE_AUTHENTICATION = "AUTH"; //$NON-NLS-1$
  
  public static final String LANG_PARAM = "appletLang";  //$NON-NLS-1$
}
