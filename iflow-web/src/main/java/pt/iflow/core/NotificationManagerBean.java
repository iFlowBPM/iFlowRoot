package pt.iflow.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.sql.DataSource;

import pt.iflow.api.cluster.JobManager;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.notification.Notification;
import pt.iflow.api.notification.NotificationManager;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.notification.NotificationImpl;
import pt.iflow.api.utils.Const;

public class NotificationManagerBean implements NotificationManager {
  private static NotificationManagerBean instance = null;
  private static Timer purgeThread = null;
  
  private static final int MSG_CODE_NEW = 0;
  private static final int MSG_CODE_READ = 1;
  private static final int MSG_MAX_SIZE = 500;
  
  
  public static NotificationManager getInstance() {
    if(null == instance) {
      instance = new NotificationManagerBean();
    }
    return instance;
  }
  
  
  public static void startManager() {
    if(null != purgeThread) return; // already running
    
    purgeThread = new Timer();
    purgeThread.scheduleAtFixedRate(new TimerTask() {
      public void run() {
    	  if(JobManager.getInstance().isMyBeatValid())
    		  NotificationManagerBean.getInstance().purgeOldMessages();
      }
    }, 0L, 1000L*60*60*24);
    
  }
  
  public static void stopManager() {
    if(null == purgeThread) return; // already dead
    
    purgeThread.cancel();
    purgeThread = null;
  }
  
  
  
  private NotificationManagerBean () {}
  
  
  
  public void purgeOldMessages() {
    Calendar cal = Calendar.getInstance();
    
    int notifications_keep_days = Const.nNotifications_keep_days * -1;
        
    cal.add(Calendar.DATE, notifications_keep_days);
    final String query = "delete from notifications where created < ?";

    // apaga mensagens antigas
    Connection db = null;
    PreparedStatement st = null;
    DataSource ds = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.prepareStatement(query);
      st.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));

      int n = st.executeUpdate();

      st.close();
      st = null;
      Logger.adminInfo("NotificationManager", "purgeOldMessages", "Removed "+n+" old notification messages.");
    } catch (SQLException e) {
      Logger.adminWarning("NotificationManager", "purgeOldMessages", "Error removing old notification messages.",e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }
  }

  public Collection<Notification> listNotifications(UserInfoInterface userInfo) {
    return listAllNotifications(userInfo, true);
  }

  public Collection<Notification> listAllNotifications(UserInfoInterface userInfo) {
    return listAllNotifications(userInfo, false);
  }

  private Collection<Notification> listAllNotifications(UserInfoInterface userInfo, boolean listNew) {
    if(userInfo == null) return null;
    String user = userInfo.getUtilizador();
    final String query = "select a.*,b.isread from notifications a, user_notifications b where a.id=b.notificationid "+(listNew?"and b.isread=0":"")+" and b.userid=? order by a.created desc";

    ArrayList<Notification> notifications = new ArrayList<Notification>();

    // lista mensagens
    Connection db = null;
    PreparedStatement st = null;
    DataSource ds = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.prepareStatement(query);
      st.setString(1, user);
      
      rs = st.executeQuery();
      while(rs.next()) {
        NotificationImpl notification = new NotificationImpl(rs.getInt("id"), rs.getString("sender"), rs.getTimestamp("created"), rs.getString("message"), rs.getInt("isread")!=0);
        notification.setLink(rs.getString("link"));
        notifications.add(notification);
      }
      rs.close();
      rs = null;

      st.close();
      st = null;
      Logger.debug(user, this, "listAllNotifications", "Found "+notifications.size()+" messages.");
    } catch (SQLException e) {
      Logger.warning(user, this, "listAllNotifications", "Error retrieving user notifications",e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return notifications;
  }
  
  public int countNewMessages(UserInfoInterface userInfo) {
    int count = -1;
    if(userInfo == null) return -1;
    String user = userInfo.getUtilizador();
    final String query = "select count(*) from user_notifications where isread=0 and userid=?";

    // lista mensagens
    Connection db = null;
    PreparedStatement st = null;
    DataSource ds = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.prepareStatement(query);
      st.setString(1, user);
      
      count = 0;
      rs = st.executeQuery();
      if(rs.next()) {
        count = rs.getInt(1);
      }
      rs.close();
      rs = null;

      st.close();
      st = null;
      Logger.debug(user, this, "countNewMessages", "Found "+count+" new messages.");
    } catch (SQLException e) {
      Logger.warning(user, this, "countNewMessages", "Error retrieving user notifications count",e);
      count = -1;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return count;
  }

  
  public int notifyUser(UserInfoInterface userInfo, String to, String message) {
    return notifyUser(userInfo, userInfo.getUtilizador(), to, message);
  }

  public int notifyUser(UserInfoInterface userInfo, String from, String to, String message) {
    Collection<String> destination = new ArrayList<String>(1);
    destination.add(to);
    return notifyUsers(userInfo, from, destination, message, "false");
  }

  public int notifyUsers(UserInfoInterface userInfo, Collection<String> to, String message) {
    return notifyUsers(userInfo, userInfo.getUtilizador(), to, message, "false");
  }

  public int notifyUsers(UserInfoInterface userInfo, String from, Collection<String> to, String message, String link) {

    ArrayList<String> toNotify = new ArrayList<String>();
    final boolean isAdmin = userInfo.isSysAdmin();

    // lets check if users are Ok
    AuthProfile ap = BeanFactory.getAuthProfileBean();
    String theOrg = userInfo.getOrganization();
    for (String user : to) {
      UserData userData = ap.getUserInfo(user);
      String orgID = userData.get(UserData.ORG_ID);
      String userId = userData.getUsername(); // fix username
      // notify user if sys adm call or same organization
      if(isAdmin || theOrg.equals(orgID)) toNotify.add(userId);
    }

    return notify(userInfo, from, toNotify, message, link);
  }
    
  private int notify(UserInfoInterface userInfo, String from, Collection<String> toNotify, String message){
	  return notify(userInfo, from, toNotify, message, "false");
  }
  
  private int notify(UserInfoInterface userInfo, String from, Collection<String> toNotify, String message, String link) {
    
	  String userId = userInfo.getUtilizador();

	  if(toNotify.isEmpty()) { 
		  Logger.warning(userId, this, "notify", "No one to notify.");
		  return NOTIFICATION_EMPTY;
	  }
	  Logger.debug(userId, this, "notify", "Notify "+toNotify.size()+" users.");

	  if(message.length()>MSG_MAX_SIZE)message = message.substring(0, MSG_MAX_SIZE);

	  // created,sender,message
	  final String queryMsg = DBQueryManager.getQuery("Notification.CREATE_MESSAGE");
	  final String queryUsr = "insert into user_notifications (userid,notificationid,isread) values (?,?,0)";

	  // criar nova mensagem
	  Connection db = null;
	  PreparedStatement st = null;
	  DataSource ds = null;
	  ResultSet rs = null;
	  int result = NOTIFICATION_ERROR;
	  try {
		  ds = Utils.getDataSource();
		  db = ds.getConnection();
		  db.setAutoCommit(false);
		  st = db.prepareStatement(queryMsg, new String[]{"id"});
		  st.setTimestamp(1, new Timestamp(new Date().getTime()));
		  st.setString(2, from);
		  st.setString(3, message);
		  st.setString(4, link);
		  int n = st.executeUpdate();
		  int messageId = -1;

		  Logger.debug(userId, this, "notify", "Inserted "+n+" notification messages.");
		  if(n > 0) {
			  // was inserted.
			  rs = st.getGeneratedKeys();
			  if(rs.next()) {
				  messageId = rs.getInt(1);
			  }
			  rs.close();
			  rs = null;
		  }
		  st.close();
		  st = null;
		  Logger.debug(userId, this, "notify", "Message ID is "+messageId);

		  // atribui mensagem a utilizadores.
		  if(messageId != -1) {
			  st = db.prepareStatement(queryUsr);

			  for (String user : toNotify) {
				  st.setString(1, user);
				  st.setInt(2, messageId);
				  n = st.executeUpdate();
				  Logger.debug(userId, this, "notify", "User "+user+" inserts "+n);
			  }

			  st.close();
			  st = null;

			  db.commit();
			  result = NOTIFICATION_OK;
		  }

	  } catch (SQLException e) {
		  Logger.warning(userId, this, "notify", "Error creating new message.", e);
	  } finally {
	    DatabaseInterface.closeResources(db, st, rs);
	  }
	  return result;
  }
  


  public int notifyOrgError(UserInfoInterface userInfo, String errorSource, String message) {
	  return notifyError(userInfo, ErrorNotificationType.ORG, errorSource, message);
  }


  public int notifySystemError(UserInfoInterface userInfo, String errorSource, String message) {
	  return notifyError(userInfo, ErrorNotificationType.SYSTEM, errorSource, message);
  }

  private int notifyError(UserInfoInterface userInfo, ErrorNotificationType type, String errorSource, String message) {
	  Collection<String> toNotify = null;	  
	  
	  switch (type) { 
	  case SYSTEM:
		  toNotify = BeanFactory.getUserManagerBean().getSystemUsers(userInfo); 
		  break;
	  case ORG:
		  toNotify = AccessControlManager.getUserDataAccess().getOrganizationAdmins(userInfo.getOrganization());
		  break;
	  }	  
	  
	  return notify(userInfo, errorSource, toNotify, message);
  }
  
  private int markMessage(UserInfoInterface userInfo, int messageId, int code) {
    if(userInfo == null) return NOTIFICATION_ERROR;
    final String query = "update user_notifications set isread=? where userid=? and notificationid=?";
    String userId = userInfo.getUtilizador();
    int result = NOTIFICATION_ERROR;
    
    // marca mensagen como lida
    Connection db = null;
    PreparedStatement st = null;
    DataSource ds = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.prepareStatement(query);
      st.setInt(1, code);
      st.setString(2, userId);
      st.setInt(3, messageId);

      int n = st.executeUpdate();

      st.close();
      st = null;
      result = NOTIFICATION_OK;
      Logger.debug(userId, this, "markMessage", "Marked "+n+" notification messages.");
    } catch (SQLException e) {
      Logger.warning(userId, this, "markMessage", "Error marking messages.", e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }
    return result;
  }

  public int markMessageRead(UserInfoInterface userInfo, int messageId) {
    return markMessage(userInfo, messageId, MSG_CODE_READ);
  }

  public int markMessageNew(UserInfoInterface userInfo, int messageId) {
    return markMessage(userInfo, messageId, MSG_CODE_NEW);
  }
  

  public int deleteMessage(UserInfoInterface userInfo, int messageId) {
    if(userInfo == null) return NOTIFICATION_ERROR;
    final String query = "delete from user_notifications where userid=? and notificationid=?";
    String userId = userInfo.getUtilizador();
    int result = NOTIFICATION_ERROR;
    
    // marca mensagen como lida
    Connection db = null;
    PreparedStatement st = null;
    DataSource ds = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.prepareStatement(query);
      st.setString(1, userId);
      st.setInt(2, messageId);

      int n = st.executeUpdate();

      st.close();
      st = null;
      result = NOTIFICATION_OK;
      Logger.debug(userId, this, "deleteMessage", "Deleted "+n+" notification messages.");
    } catch (SQLException e) {
      Logger.warning(userId, this, "deleteMessage", "Error deleting messages.", e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }
    return result;
  }

  private enum ErrorNotificationType {
	  SYSTEM,
	  ORG;
  }
}
