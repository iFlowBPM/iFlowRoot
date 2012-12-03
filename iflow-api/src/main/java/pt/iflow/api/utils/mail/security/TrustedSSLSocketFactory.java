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
package pt.iflow.api.utils.mail.security;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import pt.iflow.api.utils.Logger;

public class TrustedSSLSocketFactory extends SSLSocketFactory {

  private SSLSocketFactory factory;
  
  public TrustedSSLSocketFactory() {
    try {
      SSLContext sslcontext = SSLContext.getInstance("TLS");
      sslcontext.init(null, // No KeyManager required
                      new TrustManager[] { new TrustedTrustManager()},
                      new java.security.SecureRandom());
      factory = ( SSLSocketFactory) sslcontext.getSocketFactory();

    } catch( Exception ex) {
      Logger.error(null, this, "constructor", "caught exception", ex);
    }
  }

  public static SocketFactory getDefault() {
    return new TrustedSSLSocketFactory();
  }

  public Socket createSocket( Socket socket, String s, int i, boolean flag)
      throws IOException {
    return factory.createSocket( socket, s, i, flag);
  }

  public Socket createSocket( InetAddress inaddr, int i,
      InetAddress inaddr1, int j) throws IOException {
    return factory.createSocket( inaddr, i, inaddr1, j);
  }

  public Socket createSocket( InetAddress inaddr, int i) throws IOException {
    return factory.createSocket( inaddr, i);
  }

  public Socket createSocket( String s, int i, InetAddress inaddr, int j)
      throws IOException {
    return factory.createSocket( s, i, inaddr, j);
  }

  public Socket createSocket( String s, int i) throws IOException {
    return factory.createSocket( s, i);
  }

  public String[] getDefaultCipherSuites() {
    return factory.getSupportedCipherSuites();
  }

  public String[] getSupportedCipherSuites() {
    return factory.getSupportedCipherSuites();
  }
}
