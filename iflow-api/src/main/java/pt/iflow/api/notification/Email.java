package pt.iflow.api.notification;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author Jose Costa
 * @version 1.0
 */

public class Email implements Cloneable {

  String id = null;
  String host = "";
  int port = -1;
  String user = "";
  String pass = "";
  boolean debug = false;

  String from = "";
  String subject = "";
  String msgText = "";

  String processSignature = "";
  
  boolean bEmailManager = Const.bEMAIL_MANAGER;
  boolean bHtml = false;
  boolean auth = false;
  boolean startTls = false;

  HashSet<String> hsTo = new HashSet<String>();
  HashSet<String> hsCc = new HashSet<String>();

  long lCreatedTimestamp = 0;

  /**
   * 
   */
  public Email() {
    this(Const.sMAIL_SERVER);
  }

  /**
   * 
   * @param host
   */
  public Email(String host) {
    this(host, Const.nMAIL_PORT);
  }
  
  public Email(String host, int port) {
    this(host, port, Const.bMAIL_AUTH, Const.sMAIL_USERNAME, Const.sMAIL_PASSWORD, Const.bMAIL_STARTTLS);
  }

  /**
   * 
   * @param host
   * @param from
   */
  public Email(String host, String from) {
    this(host);
    this.from = from;
  }

  /**
   * 
   * @param host
   */
  public Email(String host, String user, String pass) {
    this(host, Const.nMAIL_PORT, true, user, pass, Const.bMAIL_STARTTLS);
  }

  /**
   * 
   */
  private Email(String host, int port, boolean auth, String user, String pass, boolean startTLS) {
    this.host = host;
    this.port = port;
    this.user = user;
    this.pass = pass;
    this.auth = auth;
    this.startTls = startTLS;
  }

  /**
   * Sets the email id
   * 
   * @param id email id
   */
  public void setId(String asId) {
    this.id = asId;
  }

  /**
   * Gets the email id
   */
  public String getId() {
    return this.id;
  }

  /**
   * Sets the SMTP host
   * 
   * @param host SMTP host
   */
  public void setHost(String host) {
    this.host = host;
  }


  public void setPort(int port) {
    this.port = port;
  }

  /**
   * 
   * @param from
   */
  public void setFrom(String from) {
    this.from = from;
  }

  public void resetTo() {
    this.hsTo = new HashSet<String>();
  }

  /**
   * 
   * Add this email to the TO: list
   * 
   * @param to
   */
  public void setTo(String to) {
    if(null == to) return;
    to = to.trim();
    if(StringUtils.isEmpty(to)) return;
    hsTo.add(to);
    hsCc.remove(to);
  }

  /**
   * 
   * Add all emails in list to the TO: list
   * 
   * @param alto
   */
  public void setTo(List<String> alto) {
    for (String to : alto) {
      setTo(to);
    }
  }

  /**
   * 
   * @param alcc
   */
  public void setCc(List<String> alcc) {
    String stmp = null;
    for (int i = 0; alcc != null && i < alcc.size(); i++) {
      stmp = alcc.get(i);
      stmp = stmp.trim();
      if (!this.hsTo.contains(stmp)) {
        this.hsCc.add(stmp);
      }
    }
  }

  /**
   * 
   * @param subject
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getSubject() {
    return this.subject;
  }

  /**
   * 
   * @param msgText
   */
  public void setMsgText(String msgText) {
    this.msgText = msgText;
  }

  /**
   * 
   * @param abHtml
   */
  public void setHtml(boolean abHtml) {
    this.bHtml = abHtml;
  }

  /**
   * 
   * @param alTimestamp
   */
  public void setCreatedTimestamp(long alTimestamp) {
    this.lCreatedTimestamp = alTimestamp;
  }

  /**
   * 
   */
  public long getCreatedTimestamp() {
    return this.lCreatedTimestamp;
  }

  /*
   * 
   * @param abEmailManager
   */
  protected void disableEmailManager() {
    this.bEmailManager = false;
  }

  /**
   * 
   * @param from
   * @param to
   * @param subject
   * @param msgText
   * @return true if mail was sent correctly, false otherwise
   */
  public boolean sendMsg(String from, String to, String subject, String msgText) {
    if(!Const.bUSE_EMAIL) return true;
    boolean result = false;

    try {
      Email sample = (Email) this.clone();
      
      sample.setFrom(from);
      sample.setTo(to);
      sample.setSubject(subject);
      sample.setMsgText(msgText);
      
      result = sample.sendMsg();
    } catch(CloneNotSupportedException e) {
      e.printStackTrace();
      result = false;
    }
    
    return result;
  }

  /**
   * 
   * @param to
   * @param subject
   * @param msgText
   * @return true if mail was sent correctly, false otherwise
   */
  public boolean sendMsg(String to, String subject, String msgText) {
    if(!Const.bUSE_EMAIL) return true;
    return sendMsg(Const.sAPP_EMAIL, to, subject, msgText);
  }

  /**
   * 
   * @return true if mail was sent correctly, false otherwise
   */
  public boolean sendMsg() {
    if(!Const.bUSE_EMAIL) return true;
    boolean retObj = false;

    try {

      StringBuilder sbTo = new StringBuilder();

      if (this.bEmailManager) {
        retObj = EmailManager.setEmail(this);
        if (retObj) {
          Logger.info("", this, "sendMsg", processSignature + "set mail in email manager");
        }
        else {
          Logger.warning("", this, "sendMsg", processSignature + "mail NOT SET in email manager");
        }
      }
      else {
        // first of all validate email addresses (from, to and cc)
        InternetAddress iaFrom = null;
        InternetAddress[] iaaTo = null;
        InternetAddress[] iaaCc = null;

        boolean emailValidationError = true;
        try {
          iaFrom = new InternetAddress(from);
          if(Const.nMODE != Const.nPRODUCTION) {
            Logger.info(null, this, "sendMsg", 
                "not production mode: reseting email addresses to application test email: " + 
                Const.sTEST_EMAIL);

            InternetAddress testAddress = new InternetAddress(Const.sTEST_EMAIL);
            Logger.debug("", this, "sendMsg", "Test Email=" + testAddress);
            iaaTo = new InternetAddress[]{testAddress};
            if (!this.hsCc.isEmpty()) {
              iaaCc = new InternetAddress[]{testAddress};
            }
            sbTo.append(testAddress);
          } else {

            iaaTo = new InternetAddress[this.hsTo.size()];
            Iterator<String> iter = this.hsTo.iterator();
            int counter = 0;
            while (iter.hasNext()) {
              iaaTo[counter] = new InternetAddress(iter.next());

              Logger.debug("", this, "sendMsg", "TO[" + counter + "]=" + iaaTo[counter]);

              if (counter > 0)
                sbTo.append(";");
              sbTo.append(iaaTo[counter]);
              
              counter++;
            }

            if (this.hsCc.size() > 0) {
              iaaCc = new InternetAddress[this.hsCc.size()];
              iter = this.hsCc.iterator();
              counter = 0;
              while (iter.hasNext()) {
                iaaCc[counter] = new InternetAddress( iter.next());

                Logger.debug("", this, "sendMsg", "CC[" + counter + "]=" + iaaCc[counter]);

                counter++;
              }
            }
          }
          emailValidationError = false;
        }
        catch (AddressException ae) {
          Logger.error("", this, "sendMsg", processSignature + "ADDRESS EXCEPTION", ae);
          emailValidationError = true;
        }
        catch (Exception ei) {
          Logger.error("", this, "sendMsg", processSignature + "EXCEPTION", ei);
          emailValidationError = true;
        }

        if(emailValidationError) {
          Logger.error(null, this, "sendMsg", processSignature + "Some email addresses are invalid. Returning false.");
          return false;
        }

        // create some properties and get the default Session
        // Security.setProperty( "ssl.SocketFactory.provider", "pt.iknow.notification.DummySSLSocketFactory");
        Properties props = new Properties();
        Authenticator authenticator = null;

        props.put("mail.smtp.host", host);
        if (port > 0) {
          props.put("mail.smtp.port", String.valueOf(port));
        }
        props.put("mail.smtp.starttls.enable", getStartTls());
        props.put("mail.debug", "true");
        // if (debug) props.put("mail.debug", "false");

        props.put("mail.smtp.auth", getAuth());
        authenticator = new SMTPAuthenticator(getUser(), getPass());

        Session session = Session.getInstance(props, authenticator);
        session.setDebug(debug);

        // create a message
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(iaFrom);
        msg.setRecipients(Message.RecipientType.TO, iaaTo);

        if (iaaCc != null) {
          msg.setRecipients(Message.RecipientType.CC, iaaCc);
        }

        msg.setSubject(subject, "UTF-8");
        msg.setSentDate(new java.util.Date());

        if (this.bHtml) {
          msg.setContent(msgText, "text/html; charset=UTF-8");
        }
        else {
          // If the desired charset is known, you can use
          // setText(text, charset)
          // msg.setText(msgText, "UTF-8");
          msg.setContent(msgText, "text/plain; charset=UTF-8");
        }

        Transport.send(msg);
        Logger.info("", this, "sendMsg", processSignature + "Mail sent to " + sbTo.toString());
        retObj = true;
      }
    }
    catch (MessagingException mex) {
      Logger.error(null, this, "sendMsg", processSignature + "\n-- pt.iflow.api.notification.Email: Exception handling for mail: ");
      if (this.id != null) 
        Logger.error(null, this, "sendMsg", "\t\t - ID: " + this.id);
      Logger.error(null, this, "sendMsg", "\t\t - Host: " + this.host);
      Logger.error(null, this, "sendMsg", "\t\t - From: " + this.from);
      Logger.error(null, this, "sendMsg", "\t\t - To: " + this.hsTo);
      Logger.error(null, this, "sendMsg", "\t\t - Cc: " + this.hsCc);
      Logger.error(null, this, "sendMsg", "\t\t - Subject: " + this.subject);
      Logger.error(null, this, "sendMsg", "\t REASON: ");

      mex.printStackTrace();
      MessagingException ex = mex;
      do {
        if (ex instanceof SendFailedException) {
          SendFailedException sfex = (SendFailedException) ex;
          Address[] invalid = sfex.getInvalidAddresses();
          if (invalid != null) {
            Logger.error(null, this, "sendMsg", "    ** Invalid Addresses");
            if (invalid != null) {
              for (int i = 0; i < invalid.length; i++)
                Logger.error(null, this, "sendMsg", "         " + invalid[i]);
            }
          }
          Address[] validUnsent = sfex.getValidUnsentAddresses();
          if (validUnsent != null) {
            Logger.error(null, this, "sendMsg", "    ** ValidUnsent Addresses");
            if (validUnsent != null) {
              for (int i = 0; i < validUnsent.length; i++)
                Logger.error(null, this, "sendMsg", "         " + validUnsent[i]);
            }
          }
          Address[] validSent = sfex.getValidSentAddresses();
          if (validSent != null) {
            Logger.error(null, this, "sendMsg", "    ** ValidSent Addresses");
            if (validSent != null) {
              for (int i = 0; i < validSent.length; i++)
                Logger.error(null, this, "sendMsg", "         " + validSent[i]);
            }
          }
        }
      }
      while ((ex = (MessagingException) ex.getNextException()) != null);
      retObj = false;
    } catch(Exception e) {
      Logger.error(null, this, "sendMsg", processSignature + "Error sending email (unspecified) ", e);
    }

    return retObj;
  }

  
  protected String getPass() {
    return pass;
  }

  
  public void setPass(String pass) {
    this.pass = pass;
  }

  
  public String getUser() {
    return user;
  }

  
  public void setUser(String user) {
    this.user = user;
  }

  
  public boolean isAuth() {
    return auth;
  }

  public String getAuth() {
    return String.valueOf(auth);
  }

  
  public void setAuth(boolean auth) {
    this.auth = auth;
  }

  
  public boolean isStartTls() {
    return startTls;
  }

  public String getStartTls() {
    return String.valueOf(startTls);
  }

  
  public void setStartTls(boolean startTls) {
    this.startTls = startTls;
  }

  public void setCallingProcess(ProcessHeader procHeader) {
    this.processSignature = procHeader.getSignature();
  }

  public String getCallingProcessSignature() {
    return this.processSignature;
  }
}
