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
package pt.iflow.applet.cipher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.iflow.applet.WebClient;


public enum CipherType {
  /**
   * No cipher
   */
  NONE(NoneCipherImpl.class),
  
  /**
   * RSA cipher
   */
  RSA(NoneCipherImpl.class),
  
  /**
   * Pre-Shared Key cipher
   */
  PSK(NoneCipherImpl.class),
  
  /**
   * Password Based Encryption cipher
   */
  PBE(NoneCipherImpl.class);

  private final Class<? extends FileCipher> implClass;
  
  private CipherType(Class<? extends FileCipher> implClass) {
    this.implClass = implClass;
  }

  public FileCipher getCipherInstance() throws InstantiationException, IllegalAccessException {
    return implClass.newInstance();
  }
  
  private static Log log = LogFactory.getLog(CipherType.class);
  
  public static FileCipher getFileCipher(WebClient webClient) {
    return getFileCipher(webClient.getCipherType(), webClient);
  }
  
  public static FileCipher getFileCipher(String cipherType, WebClient webClient) {
    FileCipher cipher;
    try {
      cipher = CipherType.valueOf(cipherType).getCipherInstance();
      if(null != webClient) cipher.init(webClient);
    } catch (Exception e) {
      log.warn("Invalid signature type: "+cipherType, e); //$NON-NLS-1$
      try {
        cipher = CipherType.NONE.getCipherInstance();
      } catch (Exception e1) {
        log.error("Could not load default signature type", e1); //$NON-NLS-1$
        throw new RuntimeException(e1);
      }
    }
    return cipher;
  }


}
