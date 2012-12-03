/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
