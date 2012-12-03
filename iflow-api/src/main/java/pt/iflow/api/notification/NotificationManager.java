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
package pt.iflow.api.notification;

import java.util.Collection;

import pt.iflow.api.notification.Notification;
import pt.iflow.api.utils.UserInfoInterface;

public interface NotificationManager {

  public static final int NOTIFICATION_OK = 0;
  public static final int NOTIFICATION_ERROR = 1;
  public static final int NOTIFICATION_EMPTY = 2;

  /**
   * List all unread notification messages for the provided user.
   * @param userInfo
   * @return
   */
  public abstract Collection<Notification> listNotifications(UserInfoInterface userInfo);

  /**
   * List all notification messages for the provided user.
   * @param userInfo
   * @return
   */
  public abstract Collection<Notification> listAllNotifications(UserInfoInterface userInfo);
  
  /**
   * Send a message from "userInfo" to user "to".
   * 
   * The destination user must exist in the same organization.
   * 
   * @param userInfo
   * @param to
   * @param message
   * @return
   */
  public abstract int notifyUser(UserInfoInterface userInfo, String to, String message);

  /**
   * Send a message from user "from" to user "to".
   * 
   * The destination user must exist in the same organization.
   * 
   * @param userInfo
   * @param from
   * @param to
   * @param message
   * @return
   */
  public abstract int notifyUser(UserInfoInterface userInfo, String from, String to, String message);

  /**
   * Send a message from "userInfo" to users in "to".
   * 
   * The destination users must exist in the same organization.
   * 
   * @param userInfo
   * @param from
   * @param to
   * @param message
   * @return
   */
  public abstract int notifyUsers(UserInfoInterface userInfo, Collection<String> to, String message);

  /**
   * Send a message from user "from" to users in "to".
   * 
   * The destination users must exist in the same organization.
   * 
   * @param userInfo
   * @param from
   * @param to
   * @param message
   * @return
   */
  public abstract int notifyUsers(UserInfoInterface userInfo, String from, Collection<String> to, String message, String link);

  /**
   * Send a error message from "error context" to org admin.
   * 
   * @param userInfo calling user
   * @param errorContext
   * @param message
   * @return
   */
  public abstract int notifyOrgError(UserInfoInterface userInfo, String errorSource, String message);

  /**
   * Send a error message from "error context" to system admin.
   * 
   * @param userInfo calling user
   * @param errorContext
   * @param message
   * @return
   */
  public abstract int notifySystemError(UserInfoInterface userInfo, String errorSource, String message);

  /**
   * Mark a message as read.
   * 
   * @param userInfo
   * @param messageId
   * @return
   */
  public abstract int markMessageRead(UserInfoInterface userInfo, int messageId);

  /**
   * Remove messages older than a specified threshold (15 days by default)
   */
  public abstract void purgeOldMessages();
  
  /**
   * Get the number of new messages for user
   * 
   * @param userInfo
   * @return
   */
  public abstract int countNewMessages(UserInfoInterface userInfo);

  /**
   * Mark a message as new.
   * 
   * @param userInfo
   * @param messageId
   * @return
   */
  public abstract int markMessageNew(UserInfoInterface userInfo, int messageId);

  /**
   * Remove a message from user view. The message itself will be automatically purged.
   * 
   * @param userInfo
   * @param messageId
   * @return
   */
  public abstract int deleteMessage(UserInfoInterface userInfo, int messageId);

}
