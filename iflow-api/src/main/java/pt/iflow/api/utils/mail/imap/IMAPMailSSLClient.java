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
package pt.iflow.api.utils.mail.imap;

import java.security.Security;
import java.util.Properties;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import pt.iflow.api.utils.mail.MailConfig;

import com.sun.mail.imap.IMAPSSLStore;

public class IMAPMailSSLClient extends IMAPMailClient {
  
  public IMAPMailSSLClient(MailConfig config) throws Exception {
    this(config.getHost(), 
        (config.getPort() <= 0 ? DEF_IMAP_SSL_PORT : config.getPort()), 
        config.getUser(), config.getPass());

    setInboxFolder(config.getInbox());
    if (config.getSubsFolders() != null) {
      String[] subsFolders = new String[config.getSubsFolders().size()];
      subsFolders = config.getSubsFolders().toArray(subsFolders);
      subscribeFolders(subsFolders);
    }
}
  
  public IMAPMailSSLClient(String asHost, String user, byte[] password) throws Exception {
    this(asHost, DEF_IMAP_SSL_PORT, user, password);
  }
  
  public IMAPMailSSLClient(String asHost, int anPort, String user, byte[] password) throws Exception {
    super(asHost, anPort, user, password);
    
    Properties props = System.getProperties();
    
    super.init(props);
  }
  
  private void initSSLProps() {
    Security.addProvider(new BouncyCastleProvider());
    Security.setProperty("ssl.SocketFactory.provider","TrustedSSLSocketFactory");
    
    Properties props = System.getProperties();
    
    props.setProperty( "mail.imap.socketFactory.class", SSL_FACTORY);
    props.setProperty( "mail.imap.socketFactory.fallback", "false");
    props.setProperty( "mail.imap.socketFactory.port", String.valueOf(this._nPort));
    
    super.init(props);  
  }

  private void clearSSLProps() {
    Security.removeProvider(new BouncyCastleProvider().getName());

    Properties props = System.getProperties();
    
    super.init(props); 
  }

  @Override
  protected Store getStore(Session session, int attemptNbr) throws NoSuchProviderException {
    Store retObj = null;
    if (attemptNbr == 1) {
      retObj = new IMAPSSLStore(session,
          new URLName("imap://" + _authenticator.getPasswordAuthentication().getUserName()));
    } else if (attemptNbr == 2) {
      initSSLProps();
      retObj = session.getStore("imap");
      clearSSLProps();
    }
    return retObj;
  }
}
