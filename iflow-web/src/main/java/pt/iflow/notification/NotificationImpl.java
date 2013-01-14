package pt.iflow.notification;

import java.io.Serializable;
import java.util.Date;

import pt.iflow.api.notification.Notification;

public class NotificationImpl implements Serializable, Notification {
  private static final long serialVersionUID = 929163739444143735L;
  
  private int id;
  private String sender;
  private Date created;
  private String message;
  private boolean read;
  private String link = "";
  
  public NotificationImpl() {
    
  }
  
  public NotificationImpl(int id, String sender, Date created, String message, boolean read) {
    this.id = id;
    this.sender = sender;
    this.created = created;
    this.message = message;
    this.read = read;
  }
  
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#getId()
 */
public int getId() {
    return id;
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#getSender()
 */
public String getSender() {
    return sender;
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#setSender(java.lang.String)
 */
public void setSender(String sender) {
    this.sender = sender;
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#getCreated()
 */
public Date getCreated() {
    return created;
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#setCreated(java.util.Date)
 */
public void setCreated(Date created) {
    this.created = created;
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#getMessage()
 */
public String getMessage() {
    return message;
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#setMessage(java.lang.String)
 */
public void setMessage(String message) {
    this.message = message;
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#isRead()
 */
public boolean isRead() {
    return read;
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#setRead(boolean)
 */
public void setRead(boolean read) {
    this.read = read;
  }
 

public String getLink() {
    return link;
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#setRead(boolean)
 */
public void setLink(String link) {
	if(link != null)
		this.link = link;
	else
		link = "";
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.notification.NotificationInterface#toString()
 */
public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.sender).append(" ").append(this.created).append(": ").append(this.message);
    return sb.toString();
  }
}
