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
package pt.iflow.utils.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SecurityHelper {

  private static boolean loaded = false;
  private static boolean bcOk = false;
  private static boolean pkcs11Ok = false;
  
  private static Provider bcProvider;
  private static Provider pkcs11Provider;
  private static Provider sunProvider;
  
  public static Provider getPKCS11Provider() {
    loadProviders();
    return pkcs11Provider;
  }
  
  public static Provider getBCProvider() {
    loadProviders();
    return bcProvider;
  }
  
  public static Provider getSUNProvider() {
    loadProviders();
    return sunProvider;
  }
  
  @SuppressWarnings("unchecked")
  public static synchronized void loadProviders() {
    if(loaded) return;
    
    sunProvider = Security.getProvider("SUN"); //$NON-NLS-1$
    
    try {
      bcProvider = (Provider)Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider").newInstance(); //$NON-NLS-1$
      Security.addProvider(bcProvider);
      bcOk = true;
    } catch (Throwable t) {
      System.out.println("Bouncy Castle provider not loaded"); //$NON-NLS-1$
    }

    try {
      InputStream in = SecurityHelper.class.getResourceAsStream("ikey.cfg"); //$NON-NLS-1$
      Class pkcs11Class = Class.forName("sun.security.pkcs11.SunPKCS11"); //$NON-NLS-1$
      Constructor pkcs11Constructor = pkcs11Class.getConstructor(InputStream.class);
      pkcs11Provider = (Provider) pkcs11Constructor.newInstance(in);
      Security.addProvider(pkcs11Provider);
      pkcs11Ok = true;
    } catch (Throwable t) {
      System.out.println("Sun PKCS#11 provider not loaded"); //$NON-NLS-1$
    }
  }
  
  public static boolean canPKCS11() {
    loadProviders();
    return pkcs11Ok;
  }

  public static boolean canBC() {
    loadProviders();
    return bcOk;
  }
  
  private static CertStore getCertStore(Certificate[] certificates) throws GeneralSecurityException {
    ArrayList<Certificate> list = new ArrayList<Certificate>();
    for (int i = 0, length = certificates == null ? 0 : certificates.length; i < length; i++) {
      list.add(certificates[i]);
    }
    return CertStore.getInstance("Collection", new CollectionCertStoreParameters(list), "BC"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  static byte[] bcSign(byte[] data, PrivateKey key, Certificate[] chain, String hashAlgo) throws GeneralSecurityException, CMSException, IOException {
    Security.addProvider(new BouncyCastleProvider());
    CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
    generator.addSigner(key, (X509Certificate) chain[0], getHashAlgorithm(hashAlgo));
    generator.addCertificatesAndCRLs(getCertStore(chain));
    CMSProcessable content = new CMSProcessableByteArray(data);

    CMSSignedData signedData = generator.generate(content, true, "BC"); //$NON-NLS-1$
    return signedData.getEncoded();
  }

  
  private static String getHashAlgorithm(String hashAlgo) {
    if(null == hashAlgo) return null;
    if("MD5".equalsIgnoreCase(hashAlgo)) { //$NON-NLS-1$
      return CMSSignedDataGenerator.DIGEST_MD5;
    }
    if(
        "SHA".equalsIgnoreCase(hashAlgo) ||  //$NON-NLS-1$
        "SHA1".equalsIgnoreCase(hashAlgo) || //$NON-NLS-1$
        "SHA-1".equalsIgnoreCase(hashAlgo)) { //$NON-NLS-1$
      return CMSSignedDataGenerator.DIGEST_SHA1;
    }
    
    if("SHA256".equalsIgnoreCase(hashAlgo)) { //$NON-NLS-1$
      return CMSSignedDataGenerator.DIGEST_SHA256;
    }
    if("SHA384".equalsIgnoreCase(hashAlgo)) { //$NON-NLS-1$
      return CMSSignedDataGenerator.DIGEST_SHA384;
    }
    if("SHA512".equalsIgnoreCase(hashAlgo)) { //$NON-NLS-1$
      return CMSSignedDataGenerator.DIGEST_SHA512;
    }
    
    System.err.println("Algorithm not supported: "+hashAlgo); //$NON-NLS-1$
    return null;
  }

}
