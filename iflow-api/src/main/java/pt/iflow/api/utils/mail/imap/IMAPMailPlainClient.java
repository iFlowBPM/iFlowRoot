package pt.iflow.api.utils.mail.imap;

import java.util.Properties;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import com.sun.mail.imap.IMAPStore;

import pt.iflow.api.utils.mail.MailConfig;

public class IMAPMailPlainClient extends IMAPMailClient {

  public IMAPMailPlainClient(MailConfig config) throws Exception {
    this(config.getHost(), 
        (config.getPort() <= 0 ? DEF_IMAP_PORT : config.getPort()), 
        config.getUser(), config.getPass());
    
    setInboxFolder(config.getInbox());
    if (config.getSubsFolders() != null) {
      String[] subsFolders = new String[config.getSubsFolders().size()];
      subsFolders = config.getSubsFolders().toArray(subsFolders);
      subscribeFolders(subsFolders);
    }
  }

  public IMAPMailPlainClient(String asHost, String user, byte[] password) throws Exception {
    this(asHost, DEF_IMAP_PORT, user, password);
  }

  public IMAPMailPlainClient(String asHost, int anPort, String user, byte[] password) throws Exception {
    super(asHost, anPort, user, password);

    Properties props = System.getProperties();

    super.init(props);
  }

  @Override
  protected Store getStore(Session session, int attemptNbr) throws NoSuchProviderException {
    Store retObj = null;
    if (attemptNbr == 1) {
      retObj = new IMAPStore(session,
          new URLName("imap://" + _authenticator.getPasswordAuthentication().getUserName()));
    } else if (attemptNbr == 2) {
      retObj = session.getStore("imap");
    }
    return retObj;
  }

}
