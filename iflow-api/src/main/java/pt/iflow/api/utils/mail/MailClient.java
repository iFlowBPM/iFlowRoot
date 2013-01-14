package pt.iflow.api.utils.mail;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import pt.iflow.api.utils.mail.parsers.MessageParser;

public interface MailClient {

  public void setDebug(boolean debug);
  
  public void connect() throws MessagingException;
  public boolean isConnected();
  public void disconnect() throws MessagingException;
  
  public Folder getFolder(String folderName) throws MessagingException;
  
  public boolean checkNewMail() throws MessagingException;
  
  public void readUnreadMessages(MessageParser messageParser) throws MessagingException;
  
  public Message[] getFolderUnreadMessages(Folder folder) throws MessagingException;  
  
  public void setInboxFolder(String inboxFolder);
  public void setTopFolder(String topFolder);
  
  public String getId();
}
