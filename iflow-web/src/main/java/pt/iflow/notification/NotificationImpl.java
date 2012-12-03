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
