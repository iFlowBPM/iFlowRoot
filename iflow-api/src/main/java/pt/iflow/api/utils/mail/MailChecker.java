package pt.iflow.api.utils.mail;

import javax.mail.MessagingException;

import pt.iflow.api.cluster.JobManager;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.mail.parsers.MessageParser;

public class MailChecker implements Runnable{

  private int flowid;
  private long checkInterval;
  private MailClient client;
  private MessageParser messageParser;
  private boolean stop = false;
  private Thread worker;
  
  /**
   * Constructor
   * @param checkInterval check interval in seconds
   * @param client the mail client to use
   * @param messageParser the message parser to parse new mail messages
   */
  public MailChecker(int flowid, long checkIntervalInSeconds, MailClient client, MessageParser messageParser) {
    this.flowid = flowid;
    this.checkInterval = checkIntervalInSeconds*1000;
    this.client = client;
    this.messageParser = messageParser;   
  }

  public void stop() {
    Logger.adminInfo("MailChecker", "stop", getId() + "signaling thread " + client.getId() + " to stop.");
    stop = true;
    if (worker != null) {
      worker.interrupt();
      worker = null;
    }
  }
  
  public void start() {
    Logger.adminInfo("MailChecker", "start", getId() + "starting thread " + client.getId());
    
    if (worker != null) {
      Logger.adminInfo("MailChecker", "start", getId() + "thread is already running for client " + client.getId());
      return;
    }
    
    worker = new Thread(this, getId());
    stop=false;
    worker.start();    
  }
  
  public String getId() {
    return "[" + flowid + "-" + client.getId() + "]";
  }
  
  public void disconnectClient() {
    try {
      client.disconnect();
    } 
    catch (MessagingException e) {
      Logger.adminError("MailChecker", "disconnectClient", getId() + "error disconnecting client", e);
    }
  }
  
  public void run() {

    Logger.adminInfo("MailChecker", "run", getId() + "starting mail checking");

    try {
      if (!client.isConnected()) {
        client.connect();
      }
    } 
    catch (MessagingException e) {
      Logger.adminError("MailChecker", "run", getId() + "error connecting client", e);    
    }
    
    while (!stop) {
      try {      
    	if(JobManager.getInstance().isMyBeatValid())  
	        try {
	          if (client.checkNewMail()) {
	            Logger.adminInfo("MailChecker", "run", getId() + "found new mail");
	            client.readUnreadMessages(messageParser);
	            Logger.adminInfo("MailChecker", "run", getId() + "new mail processed");
	          }
	          else {
	            Logger.adminDebug("MailChecker", "run", getId() + "no new mail");
	          }
	        } catch (MessagingException e) {
	          // TODO
	          Logger.adminError("MailChecker", "run", getId() + "caught messaging exception", e);
	        }

        try {
          Thread.sleep(checkInterval);
        }
        catch (InterruptedException e) {        
        }
      }
      catch (Exception master) {
        Logger.adminError("MailChecker", "run", getId() + "caught unexpected exception", master);
      }
    }
    
    
    Logger.adminInfo("MailChecker", "run", getId() + "done checking mail");
  }
}
