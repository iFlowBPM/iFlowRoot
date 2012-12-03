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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.mail.MailClient;
import pt.iflow.api.utils.mail.parsers.MessageParseException;
import pt.iflow.api.utils.mail.parsers.MessageParser;
import pt.iflow.api.utils.mail.security.TrustedSSLSocketFactory;

public abstract class IMAPMailClient implements MailClient {


  protected static final String SSL_FACTORY = TrustedSSLSocketFactory.class.getName();  

  protected static final int DEF_IMAP_PORT = 143;
  protected static final int DEF_IMAP_SSL_PORT = 993;

  protected static enum CheckMode { SIMPLE, RECURSIVE };

  protected HashMap<String,Folder> _hmFolders = new HashMap<String,Folder>();
  protected String _sInboxFolder;
  protected String _sTopFolder;
  protected ArrayList<String> _alSubsFolders = new ArrayList<String>();
  
  private String id;
  protected String _sHost;
  protected int _nPort;
  protected SimpleAuthenticator _authenticator;
  protected Store _store;
  protected CheckMode _mode = CheckMode.SIMPLE;
  private Properties props;
  
  protected IMAPMailClient(String asHost, int anPort, String user, byte[] password) throws Exception {
    _sHost = asHost;
    _nPort = anPort;
    _authenticator = new SimpleAuthenticator(user, password);
    generateId();
  }
  
  protected void init(Properties apProps) {
    props = new Properties(apProps);
    
    props.setProperty("mail.store.protocol", "imap"); //$NON-NLS-1$ 
    props.setProperty("mail.imap.host", _sHost); //$NON-NLS-1$
    props.setProperty("mail.imap.port", String.valueOf(_nPort)); //$NON-NLS-1$ 
    props.setProperty("mail.imap.auth.plain.disable", "true"); //BugFix para o Exchange
    
  }

  private void generateId() {
    String key = String.valueOf(new Date().getTime()) + 
      "-" + _sHost + 
      "-" + _authenticator.getPasswordAuthentication().getUserName();
    String fullclassname = this.getClass().getName();
    String classname = fullclassname.substring(fullclassname.lastIndexOf('.') + 1);

    id = classname + "-" + key.hashCode();
  }

  public String getId() {
    return id;
  }
  
  public void setDebug(boolean debug) {
    Session session = Session.getDefaultInstance(props, _authenticator);

    session.setDebug(debug);    
  }
  
  public void connect() throws MessagingException {
    Session session = Session.getDefaultInstance(props, _authenticator);
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
            _nPort,
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

  public void disconnect() throws MessagingException {
   if (isConnected()) {
     _store.close();
   }
  }
  
  public boolean isConnected() {
    return _store != null && _store.isConnected();
  }
  
  public Folder getFolder(String folderName) throws MessagingException {
    if (isConnected()) {
      if (StringUtils.isNotEmpty(folderName)) {
        return _store.getFolder(folderName);      
      } else if (StringUtils.isNotEmpty(_sInboxFolder)) {
        return _store.getFolder(_sInboxFolder);
      }
    }
    return null;    
  }
  

  public void readUnreadMessages(MessageParser messageParser) throws MessagingException {
    
    // start with inbox folder
    Folder folder = getFolder(_sInboxFolder);
    if (folder != null) {
      folder.open(Folder.READ_WRITE);
      readFolderUnreadMessages(folder, messageParser);
      folder.close(false);
    }
    
    // now subscribed folders
    String folderPath = StringUtils.isNotEmpty(_sTopFolder) ? _sTopFolder + "/" : "";
    for (String sFolder : _alSubsFolders) {
      folder = getFolder(folderPath + sFolder);        
      if (folder != null) {
        folder.open(Folder.READ_WRITE);
        readFolderUnreadMessages(folder, messageParser);
        folder.close(false);
      }
    }    
  }

  private void readFolderUnreadMessages(Folder folder, MessageParser messageParser) throws MessagingException {
    Message[] folderMessages = getFolderUnreadMessages(folder);
    for (Message msg : folderMessages) {
      try {
        if (messageParser.parse(msg)) {
          Logger.debug(null, this, "readFolderUnreadMessages", "message " + msg.getSubject() + 
              " parsed");
          markMessageRead(msg);
          Logger.debug(null, this, "readFolderUnreadMessages", "message " + msg.getSubject() + 
              " marked as read");
        }
        else {
          Logger.debug(null, this, "readFolderUnreadMessages", "message " + msg.getSubject() + 
              " not parsed. Keeping message in unread state");
        }
      }
      catch (MessageParseException pe) {
        Logger.error(null, this, "readFolderUnreadMessages", "error parsing message", pe);
      }
    }    
  }

  public Message[] getFolderUnreadMessages(Folder folder) throws MessagingException {
    if (!folder.isOpen()) {
      folder.open(Folder.READ_WRITE);
    }
    return folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
  }
  
  public boolean check() throws MessagingException {
    
    Folder f = getFolder(_sInboxFolder);
    if (checkFolder(_sInboxFolder, f)) {
      return true;
    }
    
    String folderPath = StringUtils.isNotEmpty(_sTopFolder) ? _sTopFolder + "/" : "";
    for (String sFolder : _alSubsFolders) {
      f = getFolder(folderPath + sFolder);        
      if (checkFolder(sFolder, f)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean checkFolder(String asFolder, Folder fFolder) 
    throws MessagingException {    
    
    if (fFolder == null) {
      return false;
    }
    
    Folder[] fa = null;
    String sf2 = null;
    int nUnread = 0;
    
    if ((fFolder.getType() & Folder.HOLDS_FOLDERS) != 0 &&
        this._mode == CheckMode.RECURSIVE) {
      fa = fFolder.list();
      for (Folder f2 : fa) {
        sf2 = f2.getName();

        return this.checkFolder(sf2, f2);
      }
    }
    else {
      if ((fFolder.getType() & Folder.HOLDS_MESSAGES) != 0) {
        nUnread = fFolder.getUnreadMessageCount();
        return nUnread > 0;
      }      
    }
    return false;
  }
  

  public boolean checkNewMail() throws MessagingException {
    return check();
  }
  
  
  public void markMessageRead(Message message) throws MessagingException {
    message.setFlag(Flag.SEEN, true);
  }
  
  public void setInboxFolder(String asInboxFolder) {
    this._sInboxFolder = asInboxFolder;
  }
  
  public void setTopFolder(String asTopFolder) {
    this._sTopFolder = asTopFolder;
  }
  
  public void subscribeFolder(String asFolderName) {
    this._alSubsFolders.add(asFolderName);
  }

  public void subscribeFolders(String[] asaFolders) {
    for (String s: asaFolders) {
      this.subscribeFolder(s);
    }
  }
  
  public void subscribeFolders(ArrayList<String> aalFolders) {
    for (String s: aalFolders) {
      this.subscribeFolder(s);
    }
  }
  
  protected abstract Store getStore(Session session, int attemptNbr) throws NoSuchProviderException;

  class SimpleAuthenticator extends Authenticator {
    private String user = null;
    private byte[] pass = null;
    
    SimpleAuthenticator(String asUser, byte[] asPass) {
      super();
      user = asUser;
      pass = asPass;
    }
    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(user, new String(pass));
    }
  }

}
