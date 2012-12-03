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
/**
 * 
 */
package pt.iflow.applet;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;

public class IDEntry {
  private static DateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$


  private final String alias;
  private final String issuer;
  private final String expires;
  private final String subject;
  private final X509Principal subjectPrincipal;
  private final X509Principal issuerPrincipal;
  private final X509Certificate certificate;
  private final Certificate [] chain;
  private final KeyStore store;

  public IDEntry(String alias, KeyStore store) throws KeyStoreException {
    this.alias = alias;
    this.store = store;
    X509Certificate certificate = null;
    Certificate [] chain = null;
    
    if(null != store) {
      certificate  = (X509Certificate) store.getCertificate(alias);
      chain = store.getCertificateChain(alias);
    }
    this.certificate = certificate;
    this.chain = chain;
    if(null != certificate) {
      // Parse the string name into a CompositeName
      String desc = certificate.getSubjectX500Principal().getName();

      // Get subject Common Name (CN)
      X509Principal name = new  X509Principal(desc);

      String txt = desc;
      Vector<?> v = name.getValues(X509Principal.CN);
      if(!v.isEmpty())
        txt = String.valueOf(v.get(0));
      this.subject = txt;
      this.subjectPrincipal = name;

      // Get issuer Common Name (CN)
      desc = certificate.getIssuerX500Principal().getName();
      this.issuerPrincipal = name = new  X509Principal(desc);

      txt = desc;
      v = name.getValues(X509Name.CN);
      if(!v.isEmpty())
        txt = String.valueOf(v.get(0));

      this.issuer = txt;

      this.expires = dateFmt.format(certificate.getNotAfter());
    } else {
      this.subject = ""; //$NON-NLS-1$
      this.subjectPrincipal = null;
      this.issuerPrincipal = null;
      this.issuer = ""; //$NON-NLS-1$
      this.expires = ""; //$NON-NLS-1$
    }
    
  }

  public String toString() {
    return this.subject;
  }

  public String getSubject() {
    return this.subject;
  }

  public String getAlias() {
    return this.alias;
  }

  public String getExpires() {
    return this.expires;
  }

  public String getIssuer() {
    return this.issuer;
  }

  public Certificate[] getCertificateChain() {
    return this.chain;
  }
  
  public Certificate[] getChain() {
    return this.chain;
  }
  
  /**
   * 
   * @return Subject's X509Principal
   * @deprecated Use {@link #getSubjectPrincipal()}
   */
  public X509Principal getName() {
    return this.subjectPrincipal;
  }
  
  /**
   * 
   * @return Subject's X509Principal
   */
  public X509Principal getSubjectPrincipal() {
    return this.subjectPrincipal;
  }
  
  public boolean isSubjectEquals(String subject) {
    return isSubjectEquals(new X509Principal(subject));
  }

  public boolean isSubjectEquals(X509Principal subject) {
    return subjectPrincipal.equals(subject);
  }

  public boolean isIssuerEquals(String issuer) {
    return issuerPrincipal.equals(new X509Principal(issuer));
  }

  public X509Certificate getCertificate() {
    return this.certificate;
  }
  
  public static String getCN(String dn) {
    return getCN(new X509Principal(dn));
  }

  public static String getCN(X509Principal dn) {
    return String.valueOf(dn.getValues(X509Principal.CN).get(0));
  }

  
  // Mais informacao, consultar RFC 2459, seccao 4.2.1.3 Key Usage
  // KeyUsage ::= BIT STRING {
  //   digitalSignature        (0),
  //   nonRepudiation          (1),
  //   keyEncipherment         (2),
  //   dataEncipherment        (3),
  //   keyAgreement            (4),
  //   keyCertSign             (5),
  //   cRLSign                 (6),
  //   encipherOnly            (7),
  //   decipherOnly            (8) }

  public boolean isAuthentication() {
    boolean[] keyUsage = certificate.getKeyUsage();
    if(null == keyUsage) return true; // accept... Maybe is good
    return keyUsage[0];
  }

  public boolean isSignature() {
    boolean[] keyUsage = certificate.getKeyUsage();
    if(null == keyUsage) return true; // accept... Maybe is good
    return keyUsage[1];
  }

  public boolean isValid() {
    try {
      certificate.checkValidity();
      return true;
    } catch (GeneralSecurityException e) {}
    
    return false;
  }
  
  public PrivateKey getPrivateKey() throws GeneralSecurityException {
    return (PrivateKey) store.getKey(alias, new char[]{'1','2','3','4','5','6'});
  }
  
  public Provider getProvider() {
    if(null == store) return null;
    return store.getProvider();
  }

}
