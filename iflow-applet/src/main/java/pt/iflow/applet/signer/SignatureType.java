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
package pt.iflow.applet.signer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.iflow.applet.WebClient;
import pt.iflow.applet.signer.FileSigner;
import pt.iflow.applet.signer.NoneSignatureImpl;
import pt.iflow.applet.signer.PDFSignatureImpl;

public enum SignatureType {
  NONE(NoneSignatureImpl.class),
  PDF(PDFSignatureImpl.class),
  // TODO: XAdES and PKCS7 not to be supported in the future
  //XAdES(XAdESSignatureImpl.class),
  //PKCS7(PKCS7SignatureImpl.class);
  ;
  private final Class<? extends FileSigner> implClass;
  
  private SignatureType(Class<? extends FileSigner> implClass) {
    this.implClass = implClass;
  }

  public FileSigner getSignerInstance() throws InstantiationException, IllegalAccessException {
    return implClass.newInstance();
  }
  
  private static Log log = LogFactory.getLog(SignatureType.class);
  
  public static FileSigner getFileSigner(WebClient client) {
    return getFileSigner(client.getSignatureType(), client);
  }
  
  public static FileSigner getFileSigner(String signatureType, WebClient webClient) {
    FileSigner signer;
    try {
      signer = SignatureType.valueOf(signatureType).getSignerInstance();
      if(null != webClient) signer.init(webClient);
    } catch (Exception e) {
      log.warn("Invalid signature type: "+signatureType, e); //$NON-NLS-1$
      try {
        signer = SignatureType.NONE.getSignerInstance();
      } catch (Exception e1) {
        log.error("Could not load default signature type", e1); //$NON-NLS-1$
        throw new RuntimeException(e1);
      }
    }
    return signer;
  }


}
