package pt.iflow.api.utils.mail.imap;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.mail.MailConfig;

import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IMAPStore;
//import com.sun.mail.util.MailSSLSocketFactory;

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
//    Security.addProvider(new BouncyCastleProvider());
//    Security.setProperty("ssl.SocketFactory.provider","TrustedSSLSocketFactory");
//    
//    Properties props = System.getProperties();
//    
//    props.setProperty( "mail.imap.socketFactory.class", SSL_FACTORY);
//    props.setProperty( "mail.imap.socketFactory.fallback", "false");
//    props.setProperty( "mail.imap.socketFactory.port", String.valueOf(this._nPort));
//    props.setProperty("mail.store.protocol", "imaps");
//    
//    super.init(props);  
		props = new Properties();  
		props.put("mail.imap.com", _sHost);  
		props.put("mail.imap.starttls.enable", "true");
		props.put("mail.imap.auth", "true");  

		props.put("mail.imap.socketFactory.port", String.valueOf(_nPort));
		props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.imap.socketFactory.fallback", "false");
        
      
        props.put("mail.imaps.ssl.trust", "*");
       
        props.put("mail.imaps.auth.plain.disable", "true");
  }
  
  public void connect() throws MessagingException {
	  	//Session session = Session.getDefaultInstance(props , _authenticator);
	  	initSSLProps(); 
	  	Session session = Session.getInstance(props);
	    session.setDebug(true);
	    int attemptNbr = 1;
	    MessagingException exCaught = null;
	    while (true) {
	      try {
	        _store = getStore(session, attemptNbr);	        
	        if (_store == null) {
	          Logger.adminTrace("IMAPMailClient", "connect", getId() + " error connecting to mail client!");
	          break;
	        }
	        _store.connect(
	            _sHost,
	            _authenticator.getPasswordAuthentication().getUserName(),
	            _authenticator.getPasswordAuthentication().getPassword());
	        break;
	      } catch (Exception e) {
	        Logger.adminError("IMAPMailClient", "connect", "Exception caught: ", e);
	        if (e instanceof MessagingException) {
	          exCaught = (MessagingException) e;
	        }
	      } finally {
	        attemptNbr++;
	      }
	    }
	    if (_store == null) {
	      throw exCaught;
	    }
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
      //retObj = session.getStore("imap");
      retObj = session.getStore("imaps");
      clearSSLProps();
    }
    return retObj;
  }
}
