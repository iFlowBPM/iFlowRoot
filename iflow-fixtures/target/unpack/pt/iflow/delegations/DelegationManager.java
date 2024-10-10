package pt.iflow.delegations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.cluster.JobManager;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.notification.Email;
import pt.iflow.api.notification.EmailManager;
import pt.iflow.api.notification.EmailTemplate;
import pt.iflow.api.notification.NotificationManager;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iknow.utils.Crypt;


public class DelegationManager extends Thread {

  private static DelegationManager _delegationManager = null;
  private int sleepTime = -1;
  private boolean keepRunning = false;

  private final static int REQUEST = 1;
  private final static int ASSIGN = 2;
  private final static int ACCEPT = 3;
  private final static int DELETE = 4;

  private final static int SENT_BY_NONE = 0;
  private final static int SENT_BY_EMAIL = 1;
  private final static int SENT_BY_NOTIFICATION = 2;
  private final static int SENT_BY_ERROR = 3;
  
  public static final int ACCEPT_ERROR = -1;
  public static final int ACCEPT_ACCEPT_FOUND = 1;
  public static final int ACCEPT_ACCEPT_NOT_FOUND = 2;
  public static final int ACCEPT_NOT_ACCEPT_FOUND = 3;
  public static final int ACCEPT_NOT_ACCEPT_NOT_FOUND = 4;
  
  private static boolean doLog = true;

  private DelegationManager() {
  }

  public static DelegationManager get() {
    if(null == _delegationManager) _delegationManager = new DelegationManager();
    return _delegationManager;
  }

  public static void startManager() {
    DelegationManager mng = get();
    mng.keepRunning = true;
    mng.start();
  }

  public static void stopManager() {
    DelegationManager mng = get();
    mng.keepRunning = false;
    mng.interrupt();
  }

  public void run() {
    while (keepRunning) {
      
    	try {
        sleepTime = Const.DELEGATION_THREAD_CICLE;
        if (sleepTime == -1) {
          // default sleep time is 5 minutes
          sleepTime = 5;
        }
        // in minutes -> sleepTime x (60000 milisec)
        sleepTime = sleepTime * 60000; 

        if(JobManager.getInstance().isMyBeatValid()){
	        DelegationManager delegationManager = get();
	        delegationManager.checkTimeOutDelegationsDB();
	        delegationManager.checkInconsistencesDB();
        }
        Logger.adminInfo("DelegationManager", "run", "NextSleepTime= " + sleepTime + " msec");
        
        sleep(sleepTime);
        doLog = true;
      } catch (InterruptedException e) {
        Logger.adminInfo("DelegationManager", "run", "Thread interrupted: " + e.getMessage());
      } catch (Exception e) {
        if (doLog) {
          Logger.adminInfo("DelegationManager", "run", "Failed to check delegations: " + e.getMessage());
          doLog = false;
        }
      }
    }
  }

  public List<Integer> getAllChildrenIDs(Connection db, int fatherid) throws SQLException {
    List<Integer> retObj = new ArrayList<Integer>();
    recursivelyGetAllChildrenIDs(db, retObj, fatherid);
    return retObj;
  }
  
  private void recursivelyGetAllChildrenIDs(Connection db, List<Integer> idList, int currentId) throws SQLException {
    Statement st1 = null;
    Statement st2 = null;
    ResultSet delegs = null;
    ResultSet childCount = null;
    try {
      st1 = db.createStatement();
      delegs = st1.executeQuery("SELECT hierarchyid FROM activity_hierarchy WHERE hierarchyid=" + currentId);
      while(delegs.next()) {
        int delegID = delegs.getInt("hierarchyid");
        st2 = db.createStatement();
        childCount = st2.executeQuery("SELECT hierarchyid FROM activity_hierarchy WHERE parentid=" + delegID);
        while(childCount.next()) {
          recursivelyGetAllChildrenIDs(db, idList, childCount.getInt("hierarchyid"));
        }
        DatabaseInterface.closeResources(st2,childCount);
        st2 = null;
        childCount = null;
        if(!idList.contains(delegID)) {
          idList.add(delegID);
        }
      }
    } finally {
      DatabaseInterface.closeResources(st1, st2, delegs, childCount);
    }
  }

  private boolean correctInconsistenceDelegationBranch(Collection<Integer> okIDS,Connection db,int delegationID) throws SQLException {
    boolean retValue = false;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    PreparedStatement pst3 = null;
    ResultSet delegs = null;
    ResultSet childCount = null;
    try {
      pst = db.prepareStatement("select hierarchyid,parentid,slave from activity_hierarchy where hierarchyid=?");
      pst.setInt(1, delegationID);

      pst2 = db.prepareStatement("select hierarchyid,parentid,slave from activity_hierarchy where parentid=?");
      pst3 = db.prepareStatement("update activity_hierarchy set slave=0 where hierarchyid=?");

      delegs = pst.executeQuery();
      while(delegs.next()) {
        int delegID = delegs.getInt("hierarchyid");

        pst2.setInt(1, delegID);
        childCount = pst2.executeQuery();
        while(childCount.next()) {
          if(!retValue) {
            pst3.setInt(1,delegID );
            pst3.executeUpdate();
            retValue = true;
          }
          int childID = childCount.getInt("hierarchyid");
          correctInconsistenceDelegationBranch(okIDS,db,childID);
        }

        okIDS.add(new Integer(delegID));
      }
    } finally {
      DatabaseInterface.closeResources(pst, delegs, pst2, childCount, pst3);
    }
    return retValue;
  }

  private void checkInconsistencesDB() {
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    StringBuffer sbtmp = null;
    int nrows = 0;
    String stmp = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.createStatement();

      rs = st.executeQuery("select pid from activity where delegated=1 and pid not in (select pid from activity_delegated)");
      pst = db.prepareStatement("update activity set delegated=0 where pid=?");

      boolean isFirst = true;
      boolean foundPids = false;
      while (rs.next()) {
        int pid = rs.getInt(1);
        pst.setInt(1, pid);
        foundPids = true;
        Logger.adminDebug("DelegationManager", "checkInconsistencesDB", "update activity set delegated=0 where pid=" + pid);
        pst.executeUpdate();
      }
      rs.close();
      rs = null;
      pst.close();
      pst = null;

      if (foundPids) {
        stmp = "Some of the activities in the database had the flag delegated=1 and no hierarchy that could support that. For those activities, delegated=0.";
        warnExistingInconsistence(stmp);
      }

      // checks for more than one slave in a delegation hierarchy

      boolean branchWithMultipleSlaves = false;

      int currParent = 0;

      rs = st.executeQuery("select hierarchyid,parentid,slave from activity_hierarchy where parentid=0");

      Set<Integer> okDelgations = new TreeSet<Integer>();
      String multipleSlaves = " ,hierarchyid(s): ";
      while (rs.next()) {
        currParent = rs.getInt("hierarchyid");
        if(correctInconsistenceDelegationBranch(okDelgations, db, currParent)) {
          multipleSlaves+= currParent + ",";
          branchWithMultipleSlaves = true;
        }
        okDelgations.add(currParent);
      }
      rs.close();
      rs = null;
      
      if(branchWithMultipleSlaves) {
        warnExistingInconsistence("Between the owner and the 'final' slave in a delegation, there were more than one 'slave'" + multipleSlaves);
      }

      StringBuffer okDelegationList = new StringBuffer();

      Iterator<Integer> iter = okDelgations.iterator();

      isFirst = true;

      while(iter.hasNext()){
        int currID = iter.next();

        if(!isFirst)
          okDelegationList.append(",");

        okDelegationList.append(currID);
        isFirst = false;
      }


      // set activities:delegated=0 that are associated to dangling branches
      pst = db.prepareStatement("update activity set delegated=0 where pid=?");
      if(okDelegationList.length() > 0) {
        sbtmp = new StringBuffer();
        sbtmp.append("select pid from activity_delegated where hierarchyid not in (");
        sbtmp.append(okDelegationList);
        sbtmp.append(")");
        Logger.adminDebug("DelegationManager", "checkInconsistencesDB", "Query4=" + sbtmp.toString());

        rs = st.executeQuery(sbtmp.toString());
        nrows = 0;
        while(rs.next()) {
          pst.setInt(1, rs.getInt(1));
          nrows += pst.executeUpdate();
        }
        DatabaseInterface.closeResources(pst, rs);
        pst = null;
        rs = null;

        if (nrows > 0) {
          stmp = "There were activities belonging to dangling (no owner) delegations that were 'undelegated'";
          warnExistingInconsistence(stmp);
        }

        // delete dangling branches in the hierarchy
        sbtmp = new StringBuffer();
        sbtmp.append("delete from activity_hierarchy where hierarchyid not ");
        sbtmp.append("in (");
        sbtmp.append(okDelegationList);
        sbtmp.append(")");
        
        Logger.adminDebug("DelegationManager", "checkInconsistencesDB", "Query5=" + sbtmp.toString());
        if(okDelegationList.length() > 0)
          nrows = st.executeUpdate(sbtmp.toString());

        if (nrows > 0) {
          stmp = "There were dangling delegations in the hierarchy and were deleted. Or they losted the parentid=0 or losted the slave=1.";
          warnExistingInconsistence(stmp);
        }
      }

    } catch (SQLException sqle) {
      sqle.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(db,st,pst,rs);
    }

  }

  private void warnExistingInconsistence(String message) {
    if(!Const.bUSE_EMAIL) return;

    String subject = "iFlow - delegation inconsistence in DB";
    String sFrom = pt.iflow.api.utils.Const.sAPP_EMAIL;
    String sTo   = pt.iflow.api.utils.Const.sAPP_EMAIL_ADMIN;


    Hashtable<String,String> htProps = new Hashtable<String, String>();
    // default variables
    if (sFrom != null) htProps.put("from", sFrom);
    if (subject != null) htProps.put("subject", subject);
    if (message != null) htProps.put("message", message);
    htProps.put("app_host", Const.APP_HOST);
    htProps.put("app_port", String.valueOf(Const.APP_PORT));

    EmailTemplate etemplate = EmailManager.getEmailTemplate(null, "delegation_inconsistence");
    Email email = EmailManager.buildEmail(htProps, etemplate);

    email.setTo(sTo);

    email.sendMsg();
  }

  private void checkTimeOutDelegationsDB() {
    DelegationManager delegationManager = get();
    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    ArrayList<DelegationData> altmp = new ArrayList<DelegationData>();

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);

      String query = DBQueryManager.getQuery("DelegationManager.CHECK_TIMEOUT_DELEGATIONS");
      st = db.prepareStatement(query);
      Date now = Calendar.getInstance().getTime();
      st.setTimestamp(1, new Timestamp(now.getTime()));

      Logger.adminDebug("DelegationManager", "checkDelegationsDB", "Query1=" + query + " timestamp=" + now);
      rs = st.executeQuery();

      while (rs.next()) {
        DelegationData dd = new DelegationData(
            rs.getInt("hierarchyid"),
            rs.getInt("parentid"),
            rs.getInt("flowid"),
            rs.getInt("slave"),
            rs.getString("userid"),
            rs.getString("ownerid"));
        altmp.add(dd);
      }

    } catch (SQLException sqle) {
      sqle.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(db,st,rs);
    }
    for (int i=0; i < altmp.size(); i++) {
      delegationManager.delegationTimeOut((DelegationData)altmp.get(i));
    }
  }

  private void delegationTimeOut(DelegationData dd) {
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    StringBuffer sbtmp = null;
    if (dd == null) {
      return;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.createStatement();

      if (dd.getParentid() == 0) {

        // delete the hierarchy
        List<Integer> deletedIDS = this.deleteDelegationCascade(null, "", dd.getId(), false);

        if(deletedIDS.size() > 0) {
          // This delegation is the parent, when deleted, all
          // delegations under this one will disappear
          // All the activities of the hierarchy are set delegated=0
          sbtmp = new StringBuffer();

          sbtmp.append("select pid from activity_delegated where hierarchyid ");
          sbtmp.append("in (");

          boolean isFirst = true;
          Iterator<Integer> iter = deletedIDS.iterator();
          while(iter.hasNext()) {
            Integer elem = iter.next();
            if(!isFirst)
              sbtmp.append(",");

            sbtmp.append(elem);

            isFirst = false;
          }

          sbtmp.append(")");

          rs = st.executeQuery(sbtmp.toString());

          ArrayList<Integer> pids = new ArrayList<Integer>();

          while(rs.next()) {
            int pid = rs.getInt("pid");
            pids.add(pid);
          }
          rs.close();
          rs = null;

          if(pids.size() > 0) {
            sbtmp = new StringBuffer();
            sbtmp.append("update activity set delegated=0 where pid in (");
            isFirst = true;
            iter = pids.iterator();
            while(iter.hasNext()) {
              Integer elem = (Integer)iter.next();
              if(!isFirst)
                sbtmp.append(",");

              sbtmp.append(elem);

              isFirst = false;
            }

            sbtmp.append(")");

            Logger.adminDebug("DelegationManager", "delegationTimeOut", "Query1=" + sbtmp.toString());
            st.executeUpdate(sbtmp.toString());
          }
        }

      } else {

        sbtmp = new StringBuffer();
        sbtmp.append("update activity_hierarchy set slave=1 ");
        sbtmp.append("where hierarchyid=").append(dd.getParentid());
        Logger.adminDebug("DelegationManager", "delegationTimeOut", "Query1=" + sbtmp.toString());
        st.executeUpdate(sbtmp.toString());

      }

    } catch (SQLException sqle) {
      sqle.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(db,st,rs);
    }
  }

  private boolean checkDelegationRecursive(Connection db,int flowid,String ownerID,String userID,int parentID) throws SQLException {
    boolean retObj = false;

    String query = "select ownerid,userid,slave,hierarchyid from activity_hierarchy where userid=? and flowid=? and parentid=?";

    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      pst = db.prepareStatement(query);

      pst.setString(1, ownerID);
      pst.setInt(2, flowid);
      pst.setInt(3, parentID);

      rs = pst.executeQuery();
      while (rs.next()) {

        if (rs.getString("ownerid").equals(userID)) {
          retObj = true;
          break;
        }
        // when slave == 1 don't test if userinfo == userid
        else if (rs.getInt("slave") == 0 && rs.getString("userid").equals(userID)) {
          retObj = true;
          break;
        } else if(checkDelegationRecursive(db,flowid,ownerID,userID,rs.getInt("hierarchyid"))) {
          retObj = true;
          break;
        }
      }
    } finally {
      DatabaseInterface.closeResources(pst, rs);
    }
    return retObj;
  }

  public boolean hasDelegationBetween(UserInfoInterface userInfo, String user,int flowid) {
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    String ownerID = userInfo.getUtilizador();
    boolean retObj = false;
    try {

      UserData userData = BeanFactory.getAuthProfileBean().getUserInfo(user);
      if(null == userData) return false;
      String userid = userData.getUsername();
      if(StringUtils.isEmpty(userid)) return false;
      String query = "select ownerid,userid,slave,hierarchyid from activity_hierarchy where userid=? and flowid=?";
      // prepare db connection
      ds = Utils.getDataSource();
      db = ds.getConnection();
      pst = db.prepareStatement(query);

      pst.setString(1, ownerID);
      pst.setInt(2, flowid);

      rs = pst.executeQuery();

      while (rs.next()) {
        if (rs.getString("ownerid").equals(userid)) {
          retObj = true;
          break;
        }
        // when slave == 1 don't test if userinfo == userid
        else if (rs.getInt("slave") == 0 && rs.getString("userid").equals(userid)) {
          retObj = true;
          break;
        } else if(checkDelegationRecursive(db,flowid,ownerID,userid,rs.getInt("hierarchyid"))) {
          retObj = true;
          break;
        }
      }

    }
    catch (SQLException e) {
      Logger.error(ownerID, this, "hasDelegationBetween", "Error checking delegation between", e);
      retObj = false;
    }
    finally {
      //DatabaseInterface.closeResources(db, st, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}

    }
    return retObj;
  }

  public String[] stopDelegation(UserInfoInterface userInfo,int delegationID) {
    String[] results = null;

    int flowid = 0;
    String flowName = "";
    String colega = null;

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      DataSource ds = Utils.getDataSource();


      db = ds.getConnection();
      db.setAutoCommit(false);
      st = db.createStatement();

      String query = "select parentid,userid,flowid from activity_hierarchy where hierarchyid=" + delegationID;

      rs = st.executeQuery(query);

      if(!rs.next())
        return null;

      int parentid = rs.getInt("parentid");

      /* get delegated user */
      colega = rs.getString("userid");

      /* get delegated flow id */
      flowid = rs.getInt("flowid");

      rs.close();
      rs = null;
      
      /* get delegated flow name */
      flowName = BeanFactory.getFlowHolderBean().getFlowFileName(userInfo, flowid);

      if (parentid > 0) {
        /* update parent (if exists) */
        st.executeUpdate("update activity_hierarchy set slave=1 where hierarchyid="+parentid);
      } else {
        /* set activity.delegated=0 where no more parents in hierarchy */
        st.executeUpdate(
            "update activity set delegated=0 where flowid=" 
            + flowid 
            + " and userid in (select ownerid from activity_hierarchy where hierarchyid="
            + delegationID
            + ")");
      }

      db.commit();
      Logger.info(userInfo.getUtilizador(), this, "stopDelegation", "delegation " + delegationID + " stopped");
      
      DatabaseInterface.closeResources(db, st, rs);
      db = null;
      st = null;
      rs = null;
      
      this.deleteDelegationCascade(userInfo, flowName, delegationID, true);				
      Logger.info(userInfo.getUtilizador(), this, "stopDelegation", "Delegation " +delegationID + " cascade deletion");

      results = new String[]{ flowName, colega };
    } catch (Exception e) {
      try {
        if (db!=null) db.rollback();
      } catch (SQLException e1) {
        Logger.error(userInfo.getUtilizador(), this, "stopDelegation", "Exception rolling back delegation " + delegationID, e1);
      }
      Logger.error(userInfo.getUtilizador(), this, "stopDelegation", "Exception stopping delegation " + delegationID, e);
    }
    finally {
      DatabaseInterface.closeResources(db,st,rs);
    }

    return results;

  }

  private List<Integer> deleteDelegationCascade(UserInfoInterface userInfo, String flowName, int delegationID, boolean notify) {
    ArrayList<Integer> deletedIDS = new ArrayList<Integer>();

    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    ArrayList<String> altmp = new ArrayList<String>();
    String stmp = "";
    String ownerid = null;

    int currentDelegation = delegationID;

    try {
      /* select userid, id of all the sons */
      DataSource ds = Utils.getDataSource();

      db = ds.getConnection();
      db.setAutoCommit(false);
      pst = db.prepareStatement("select hierarchyid,userid,ownerid,parentid from activity_hierarchy where hierarchyid=?");

      while(currentDelegation > 0) {
        pst.setInt(1, currentDelegation);

        rs = pst.executeQuery();

        while (rs.next()) {
          if (stmp.equals("")) {
            stmp += rs.getInt("hierarchyid");
            ownerid = rs.getString("ownerid");
          } else {
            stmp += "," + rs.getInt("hierarchyid");
          }

          deletedIDS.add(new Integer(rs.getInt("hierarchyid")));

          altmp.add(rs.getString("userid"));

          currentDelegation = rs.getInt("parentid");
        }
        rs.close();
        rs = null;
      }
      pst.close();
      pst = null;
      if(stmp.length() > 0) {
        String query = "delete from activity_hierarchy where hierarchyid in (" + stmp + ")";        

        db.createStatement().executeUpdate(query);
      }
      db.commit();

    } catch (Exception e) {
      try {
        if (db != null) db.rollback();
      } catch (SQLException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      e.printStackTrace();
    }
    finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    //notificação
    if (notify && userInfo != null) {
      IMessages messages = userInfo.getMessages();
      String subject = messages.getString("terminar_agendamento.notification.subject", ownerid);
      String message = messages.getString("terminar_agendamento.notification.message", flowName, ownerid);
      String messageMail = messages.getString("terminar_agendamento.notification.messagemail", flowName, ownerid);
      for (int i=0; i < altmp.size(); i++) {
        String to = (String)altmp.get(i);
        notify(userInfo, to, subject, message, messageMail, DELETE);
      }
    }

    return deletedIDS;
  }

  private boolean checkCircularDelegation(UserInfoInterface userInfo, String flowid, String owner, String delegated) throws Exception {
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      
      final StringBuilder sbQueryDelegation = new StringBuilder();
      /* If the userid is delegating a flow to delegateduser
           becoming a circular delegation */
      sbQueryDelegation.append("select * from activity_hierarchy where flowid = ? and userid = ? and ownerid = ?");
      
      PreparedStatement stm = db.prepareStatement( sbQueryDelegation.toString() );
 
      stm.setString(1, flowid);
      stm.setString(2, owner);
      stm.setString(3, delegated);
      
      rs = stm.executeQuery();

      return rs.next();
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
  }

  public boolean superDelegation(UserInfoInterface superUserInfo, 
      String flowid, String flowname,String ownerId, String delegatedId, Date expires, Date includeTasksFrom, String privs,StringBuffer sbError) {
  
    // FIXME: re-delegations !!!!! (parentid's, parent slave update, expires check, etc..)
    
    IMessages messages = superUserInfo.getMessages();
    
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    PreparedStatement pst3 = null;
    ResultSet rs = null;

    boolean recordInserted = false;

    String owner;
    String delegated;
    String user = superUserInfo.getUtilizador();

    AuthProfile ap = BeanFactory.getAuthProfileBean();
    UserData ownerUserData = ap.getUserInfo(ownerId);
    UserData delegatedUserData = ap.getUserInfo(delegatedId);
   
    if (ownerUserData == null || delegatedUserData == null)
      return false;
    
    try {
      owner = ownerUserData.getUsername();
      delegated = delegatedUserData.getUsername();
    } catch(Throwable t) {
      Logger.error(user, this, "superDelegation", "error verifying usernames");
      return false;
    }

    try {

      if (!checkCircularDelegation(superUserInfo, flowid, owner, delegated)) {

        int fatherid = 0;
        ds = Utils.getDataSource();
        db = ds.getConnection();
        db.setAutoCommit(false);

        
        pst = db.prepareStatement(DBQueryManager.getQuery("DelegationManager.SUPER_DELEGATION"), new String[]{"hierarchyid"});
        pst.setInt(1, fatherid);
        pst.setString(2, delegated);
        pst.setString(3, owner);
        pst.setInt(4, Integer.parseInt(flowid));
        if(null != expires)
          pst.setTimestamp(5, new Timestamp(expires.getTime()));
        else
          pst.setTimestamp(5, new Timestamp(Calendar.getInstance().getTimeInMillis()));
        pst.setString(6, privs.toString());
        pst.executeUpdate();
        rs = pst.getGeneratedKeys();
        int id = 0;
        if(rs.next()) {
          id = rs.getInt(1);  // first autogenerated key
        } else {
          Logger.error(user, this, "superDelegation", "Unable to get generated hierarchyid. [" + DBQueryManager.getQuery("DelegationManager.SUPER_DELEGATION") + "]");
          throw new Exception("Unable to get generated hierarchyid");
        }
        rs.close();
        rs = null;

        /* Update the main activity to delegated = 1 */
        st = db.createStatement();
        rs = st.executeQuery("select flowid,ownerid from activity_hierarchy where hierarchyid=" + id);
        if(rs.next()) {
          pst2 = db.prepareStatement("update activity set delegated=1 where flowid=? and userid=?");
          pst2.setInt(1, rs.getInt("flowid"));
          pst2.setString(2, rs.getString("ownerid"));
          pst2.executeUpdate();
        }
        DatabaseInterface.closeResources(st, rs);
        st = null;
        rs = null;
        
        
        // create delegation history entry
        pst3 = db.prepareStatement("insert into activity_hierarchy_history " +
            "(hierarchyid,parentid,userid,ownerid,flowid,started,expires,permissions)" +
            " values (?,?,?,?,?,?,?,?)");
        pst3.setInt(1,id);
        pst3.setInt(2, fatherid);
        pst3.setString(3, delegated);
        pst3.setString(4, owner);
        pst3.setInt(5, Integer.parseInt(flowid));
        pst3.setDate(6, new java.sql.Date((new java.util.Date().getTime())));
        if(null != expires)
          pst3.setTimestamp(7, new Timestamp(expires.getTime()));
        else
          pst3.setTimestamp(7, new Timestamp(Calendar.getInstance().getTimeInMillis()));
        pst3.setString(8, privs.toString());

        pst3.executeUpdate();

        db.commit();

        Logger.info(superUserInfo.getUtilizador(), this, "superDelegation", 
            "Delegation from '" + ownerId + "' to '" + delegated + "' made by superuser '" + user + "' of flowid " + flowid);

        recordInserted = true;
      }
    } 
    catch (Exception e) {
      Logger.error(superUserInfo.getUtilizador(), this, "superDelegation", "Error creating a new super delegation", e);
      try { if (db!=null) db.rollback(); } catch (Exception e1) { }
    } 
    finally {
      //DatabaseInterface.closeResources(db, pst, st, pst2, pst3, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (pst2 != null) pst2.close(); } catch (SQLException e) {}
    	try {if (pst3 != null) pst3.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    }

    if (recordInserted) { //xxx
      sbError.append("<br>").append(messages.getString("requisitar_agendamento.super_error.1"));

      boolean notifyOwner = !StringUtils.equals(user, owner);
      boolean notifyUser = !StringUtils.equals(user, delegated);
      final String[] saParams = {
          flowname, 
          delegated,
          owner
      };

      //notificação
      // USER
      if (notifyUser) {
        String subject = messages.getString("requisitar_agendamento.notification.super.user.subject", saParams);
        final String message = messages.getString("requisitar_agendamento.notification.super.user.message", saParams);
        String messageMail = messages.getString("requisitar_agendamento.notification.super.user.messagemail", saParams);
        String to = delegatedId;
        int notifySuccess = notify(superUserInfo, to, subject, message, messageMail, ASSIGN);
        if (notifySuccess == SENT_BY_EMAIL || notifySuccess == SENT_BY_NOTIFICATION) {
          sbError.append("<br>").append(messages.getString("requisitar_agendamento.super_error.4", saParams));
        } else if (notifySuccess == SENT_BY_ERROR) {
          sbError.append("<br>").append(messages.getString("requisitar_agendamento.super_error.3", saParams));              
        }
      }
      // OWNER
      if (notifyOwner) {
        String subject = messages.getString("requisitar_agendamento.notification.super.owner.subject", saParams);
        final String message = messages.getString("requisitar_agendamento.notification.super.owner.message", saParams);
        String messageMail = messages.getString("requisitar_agendamento.notification.super.owner.messagemail", saParams);
        String to = ownerId;
        int notifySuccess = notify(superUserInfo, to, subject, message, messageMail, ASSIGN);
        if (notifySuccess == SENT_BY_EMAIL || notifySuccess == SENT_BY_NOTIFICATION) {
          sbError.append("<br>").append(messages.getString("requisitar_agendamento.super_error.6", saParams));
        } else if (notifySuccess == SENT_BY_ERROR) {
          sbError.append("<br>").append(messages.getString("requisitar_agendamento.super_error.5", saParams));              
        }
      }
    }
    else {
      sbError.append("<br>").append(messages.getString("requisitar_agendamento.super_error.2"));
    }

    return recordInserted;
  }

  public boolean requestDelegation(UserInfoInterface userInfo, 
      String flowid, String flowname,String ownerId, String delegatedId, Date expires, Date includeTasksFrom, String privs,StringBuffer sbError) {

    // TODO: processar includeTasksFrom !!!
    IMessages messages = userInfo.getMessages();

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    boolean recordInserted = false;

    String owner;
    String delegated;
    String user = userInfo.getUtilizador();

    try {
      owner = BeanFactory.getAuthProfileBean().getUserInfo(ownerId).getUsername();
      delegated = BeanFactory.getAuthProfileBean().getUserInfo(delegatedId).getUsername();
    } catch(Throwable t) {
      Logger.error(user, this, "requestDelegation", "error verifying usernames", t);
      return false;
    }


    try {
      if (!owner.equalsIgnoreCase(delegated)) {

        int fatherid = 0;

        if (!checkCircularDelegation(userInfo, flowid, owner, delegated)) {

          ds = Utils.getDataSource();
          db = ds.getConnection();
          db.setAutoCommit(false);
          st = db.createStatement();


          final StringBuilder sbQueryUserDelegation = new StringBuilder();
          /* If the userid is delegating a flow that was delegated to him */
          //sbQueryUserDelegation.append("select * from activity_hierarchy where flowid = ");
          //sbQueryUserDelegation.append(flowid).append(" and userid='");
          //sbQueryUserDelegation.append(StringEscapeUtils.escapeSql(user)).append("' and ownerid='");
          //sbQueryUserDelegation.append(StringEscapeUtils.escapeSql(owner)).append("'");
          
          sbQueryUserDelegation.append("select * from activity_hierarchy where flowid = ? and userid = ? and ownerid = ?");
       
          PreparedStatement stm = db.prepareStatement( sbQueryUserDelegation.toString() );
          stm.setString(1, flowid);
          stm.setString(2, user);
          stm.setString(3, owner);
          
          rs = stm.executeQuery();

          if (rs.next()) {
            fatherid = rs.getInt("hierarchyid");

            /* If the user delegation expires after his own delegation
             * then 'user delegation = his own delegation' */
            Timestamp oldExpires = rs.getTimestamp("expires");

            if (expires == null || oldExpires.before(expires)) {
              expires = oldExpires;
            }

            privs = rs.getString("permissions");
          }
          rs.close();
          rs = null;

          String acceptkey = Crypt.encrypt("accept_delegation" + delegated + owner + flowid);
          String rejectkey = Crypt.encrypt("reject_delegation" + delegated + owner + flowid);

          /* Insercao do pedido de agendamento da actividade  */

          pst = db.prepareStatement(DBQueryManager.getQuery("DelegationManager.DELEGATION_REQUEST"), new String[]{"hierarchyid"});
          pst.setInt(1, fatherid);
          pst.setString(2, delegated);
          pst.setString(3, owner);
          pst.setInt(4, Integer.parseInt(flowid));

          if(null != expires)
            pst.setTimestamp(5, new Timestamp(expires.getTime()));
          else
            pst.setTimestamp(5, new Timestamp(Calendar.getInstance().getTimeInMillis()));

          pst.setString(6, privs.toString());
          pst.setString(7, acceptkey);
          pst.setString(8, rejectkey);

          pst.executeUpdate();

          /* qual e o ID atribuido? */
          rs = pst.getGeneratedKeys();
          int id = 0;
          if(rs.next()) {
            id = rs.getInt(1);  // first autogenerated key
          } else {
            Logger.error(user, this, "requestDelegation", "Unable to get generated hierarchyid. [" + DBQueryManager.getQuery("DelegationManager.DELEGATION_REQUEST") + "]");
            throw new Exception("Unable to get generated hierarchyid");
          }

          rs.close();
          rs = null;

          pst.close();
          pst = null;


          db.commit();

          UserData delegatedUserData = BeanFactory.getAuthProfileBean().getUserInfo(delegated);

          //notificação
          final String sUrl = Const.APP_PROTOCOL+"://"+Const.APP_HOST+":"+Const.APP_PORT+Const.APP_URL_PREFIX+"GoTo?goto=confirmar_agendamento.jsp";
          String sParams = "id=" + id + "&owner=" + owner + "&dkey=";
          final String[] saParams = {
              userInfo.getUtilizador(), 
              flowname, 
              sUrl + "&" + sParams + acceptkey, 
              sUrl + "&" + sParams + rejectkey
          };
          String subject = messages.getString("requisitar_agendamento.notification.subject");
          String message = messages.getString("requisitar_agendamento.notification.message", saParams);
          String messageMail = messages.getString("requisitar_agendamento.notification.messagemail", saParams);
          String to = delegatedId;
          int notifySuccess = notify(userInfo, to, subject, message, messageMail, REQUEST);
          if (notifySuccess == SENT_BY_EMAIL) {
            sbError.append("<br>").append(messages.getString("requisitar_agendamento.error.1"));
          } else if (notifySuccess == SENT_BY_NOTIFICATION) {
            sbError.append("<br>").append(messages.getString("requisitar_agendamento.error.7"));
          } else if (notifySuccess == SENT_BY_NONE) {
            sbError.append("<br>").append(messages.getString("requisitar_agendamento.error.8"));
          } else if (notifySuccess == SENT_BY_ERROR) {
            sbError.append("<br>").append(messages.getString("requisitar_agendamento.error.2"));
          }
          
          recordInserted = true;
        } else {
          sbError.append("<br>").append(messages.getString("requisitar_agendamento.error.3"));
        }
      } else {
        sbError.append("<br>").append(messages.getString("requisitar_agendamento.error.4"));
      }

    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "requestDelegation", "Error creating a new delegation request", e);
      try { if (db!=null) db.rollback(); } catch (Exception e1) { }
    } finally {
      //DatabaseInterface.closeResources(db, pst, st, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    }
    return recordInserted;
  }

  public int acceptDelegation(UserInfoInterface userInfo, String id, String sKey, String ownerid, StringBuffer sbError) {
    int result = -1;
    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    PreparedStatement st2 = null;
    ResultSet rs = null;
    boolean accept = false;
    boolean foundActivity = false;
    String flowName = "";
    try {
      flowName = getDelegation(userInfo, Integer.parseInt(id)).getFlowName();
    } catch(Exception e) {
      Logger.error(userInfo.getUtilizador(), "DelegationManager", "acceptDelegation", "Error getting delegation", e);
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      int ahParentId = -1;
      String ahUserid = null;
      String ahOwnerid = null;
      int ahFlowid = -1;
      java.sql.Date ahExpires = null;
      String ahPermissions = null;
      java.sql.Date ahRequested = null;
      java.sql.Date ahResponded = null;
      
      
      st = db.prepareStatement("select * from activity_hierarchy where hierarchyid=? and acceptkey=? and ownerid=? and userid=?");

      st.setString(1, id);
      st.setString(2, sKey);
      st.setString(3, ownerid);
      st.setString(4, userInfo.getUtilizador());

      rs = st.executeQuery();

      /* se aceitou o pedido */
      if (rs.next() && id.equals(String.valueOf(rs.getInt("hierarchyid")))) {
        accept = true;
        foundActivity = true;
        ahParentId = rs.getInt("parentid");
        ahUserid = rs.getString("userid");
        ahOwnerid = rs.getString("ownerid");
        ahFlowid = rs.getInt("flowid");
        ahExpires = rs.getDate("expires");
        ahPermissions = rs.getString("permissions");
        ahRequested = rs.getDate("expires");
        ahResponded = rs.getDate("expires");
      }
      rs.close();
      rs = null;
      st.close();
      st = null;

      if(accept) {
        st = db.prepareStatement("update activity_hierarchy set slave=1,pending=0,acceptkey=NULL,rejectkey=NULL where hierarchyid=?");
        st.setString(1,id);
        st.executeUpdate();
        st.close();
        st = null;

        // O mysql nao permite queries do genero: "update activity_hierarchy set slave=0 where hierarchyid in (select parentid from activity_hierarchy where hierarchyid=?)"
        st = db.prepareStatement("select parentid from activity_hierarchy where hierarchyid=?");
        st2 = db.prepareStatement("update activity_hierarchy set slave=0 where hierarchyid=?");
        st.setString(1,id);
        rs = st.executeQuery();
        while(rs.next()) {
          st2.setInt(1, rs.getInt(1));
          st2.executeUpdate();
        }
        rs.close();
        rs = null;
        st.close();
        st = null;
        st2.close();
        st2 = null;

        /* Update the main activity to delegated = 1 */
        st = db.prepareStatement("select flowid,ownerid from activity_hierarchy where hierarchyid=?");
        st2 = db.prepareStatement("update activity set delegated=1 where flowid=? and userid=?");
        st.setString(1,id);
        rs = st.executeQuery();
        if(rs.next()) {
          st2.setInt(1, rs.getInt("flowid"));
          st2.setString(2, rs.getString("ownerid"));
          st2.executeUpdate();
        }
        rs.close();
        rs = null;
        st2.close();
        st2 = null;
        st.close();
        st = null;

        // create delegation history entry
        st = db.prepareStatement("insert into activity_hierarchy_history " +
            "(hierarchyid,parentid,userid,ownerid,flowid,started,expires,permissions,requested,responded)" +
            " values (?,?,?,?,?,?,?,?,?,?)");
        st.setString(1,id);
        st.setInt(2, ahParentId);
        st.setString(3, ahUserid);
        st.setString(4, ahOwnerid);
        st.setInt(5, ahFlowid);
        st.setDate(6, new java.sql.Date((new java.util.Date().getTime())));
        st.setDate(7, ahExpires);
        st.setString(8, ahPermissions);
        st.setDate(9, ahRequested);
        st.setDate(10, ahResponded);
        
        st.executeUpdate();
        Logger.info(userInfo.getUtilizador(), this, "acceptDelegation", 
            "delegation requested by " + ownerid + " accepted by " + userInfo.getUtilizador());

        st.close();
        st = null;

      } else {
        st = db.prepareStatement("select hierarchyid from activity_hierarchy where hierarchyid=? and rejectkey=? and ownerid=? and userid=?");
        st.setString(1, id);
        st.setString(2, sKey);
        st.setString(3, ownerid);
        st.setString(4, userInfo.getUtilizador());
        rs = st.executeQuery();

        /* se rejeitou o pedido */
        String hid = "";
        boolean f = false;
        if (rs.next()) {
          f = true;
          hid = rs.getString("hierarchyid");
        }
        rs.close();
        rs = null;
        st.close();
        st = null;

        if(f && id.equals(hid)) {
          accept = false;
          foundActivity = true;
          st = db.prepareStatement("delete from activity_hierarchy where hierarchyid=?");
          st.setString(1, id);
          st.executeUpdate();

          Logger.info(userInfo.getUtilizador(), this, "acceptDelegation", 
              "delegation requested by " + ownerid + " rejected by " + userInfo.getUtilizador());
          
          st.close();
          st = null;
        }
      }
      db.commit();
      // prepare result...
      if(accept && foundActivity) result = ACCEPT_ACCEPT_FOUND;
      else if(accept && !foundActivity) result = ACCEPT_ACCEPT_NOT_FOUND;
      else if(!accept && foundActivity) result = ACCEPT_NOT_ACCEPT_FOUND;
      else result = ACCEPT_NOT_ACCEPT_NOT_FOUND;

      IMessages messages = userInfo.getMessages();

      if (foundActivity) {
        //notificação
        String owner = BeanFactory.getAuthProfileBean().getUserInfo(ownerid).getUsername();
        String subject = "";
        String message = "";
        String messageMail = "";
        String[] saParams = { userInfo.getUtilizador(), flowName, owner};
        if (accept) {
          subject = messages.getString("confirmar_agendamento.notification.accepted.subject");
          message = messages.getString("confirmar_agendamento.notification.accepted.message", saParams);
          messageMail = messages.getString("confirmar_agendamento.notification.accepted.messagemail", saParams);
          sbError.append("<br>").append(messages.getString("confirmar_agendamento.msg.accepted", saParams));
        } else {
          subject = messages.getString("confirmar_agendamento.notification.rejected.subject");
          message = messages.getString("confirmar_agendamento.notification.rejected.message", saParams);
          messageMail = messages.getString("confirmar_agendamento.notification.rejected.messagemail", saParams);
          sbError.append("<br>").append(messages.getString("confirmar_agendamento.msg.rejected", saParams));
        }      
        String to = ownerid;    
        int notifySuccess = notify(userInfo, to, subject, message, messageMail, ACCEPT);
        if (notifySuccess == SENT_BY_EMAIL) {
          sbError.append("<br>").append(messages.getString("confirmar_agendamento.notification.sentmail"));
        } else if (notifySuccess == SENT_BY_NOTIFICATION) {
          sbError.append("<br>").append(messages.getString("confirmar_agendamento.notification.sent"));
        } else if (notifySuccess == SENT_BY_ERROR) {
          sbError.append("<br>").append(messages.getString("confirmar_agendamento.notification.notsent"));
        }
      } else {
        sbError.append("<br>").append(messages.getString("confirmar_agendamento.error.notfound"));
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "acceptDelegation", "Error acceptin delegation", e);
      result = -1;
    } finally {
      //jcosta: DatabaseInterface.closeResources(db, st, st2, rs);
      if (db != null) DatabaseInterface.safeClose(db);
      if (st != null) DatabaseInterface.safeClose(st);
      if (st2 != null) DatabaseInterface.safeClose(st2);
      if (rs != null) DatabaseInterface.safeClose(rs);
    }

    return result;
  }
  
  /**
   * Retrieve DelegationData of a given Hierarchy ID
   * @param userInfo The User Info
   * @param hierarchyid The Hierarchy ID
   * @return The DelegationData
   * @throws Exception
   */
  public DelegationData getDelegation(UserInfoInterface userInfo, int hierarchyid) throws Exception {
    String login = userInfo.getUtilizador();
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    DelegationData dd = null;

    try {
      DataSource ds = Utils.getDataSource();
      db = ds.getConnection();

      st = db.createStatement();
    rs = st.executeQuery("select a.*,f.flowname from activity_hierarchy a, flow f where f.flowid=a.flowid and hierarchyid="
          + hierarchyid);
      if (rs.next()) {
        dd = new DelegationData();
        dd.setId(rs.getInt("hierarchyid"));
        dd.setParentid(rs.getInt("parentid"));                
        dd.setFlowid(rs.getInt("flowid"));
        dd.setFlowName(rs.getString("flowname"));
        dd.setSlave(rs.getInt("slave"));
        dd.setUserid(rs.getString("userid"));
        dd.setOwnerid(rs.getString("ownerid"));
      }

    } catch (Exception e) {
      Logger.error(login, this, "getDelegation", "Error getting delegation", e);
      throw e;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return dd;
  }
  
  private int notify(UserInfoInterface userInfo, String to, String subject, String message, String messageMail, int oper) {
    String mode = "";
    switch (oper) {
      case REQUEST: mode = Const.sDELEGATION_NOTIFY_REQUEST_MODE; break;
      case ASSIGN:  mode = Const.sDELEGATION_NOTIFY_ASSIGN_MODE; break;
      case ACCEPT:  mode = Const.sDELEGATION_NOTIFY_ACCEPT_MODE; break;
      case DELETE:  mode = Const.sDELEGATION_NOTIFY_DELETE_MODE; break;
    }

    if (mode.equals(Const.NONE)){
      return SENT_BY_NONE;
    }

    boolean notify = (Const.NOTIFICATION.equals(mode) ||
        Const.BOTH.equals(mode));

    message = StringEscapeUtils.unescapeHtml(message);
    messageMail = StringEscapeUtils.unescapeHtml(messageMail);
    subject = StringEscapeUtils.unescapeHtml(subject);
    
    if ((Const.EMAIL.equals(mode) || Const.BOTH.equals(mode) || ("".equals(mode) && Const.bUSE_EMAIL))) {
      /* Envio de mail para a confirmacao do pedido de agendamento */
      String sFrom = Const.sAPP_EMAIL;
      String sTo = null;

      UserData tmpUser = BeanFactory.getAuthProfileBean().getUserInfo(to);    
      sTo = tmpUser.getEmail();

      if (null != sTo && sTo.trim().length() > 0) {
        sTo = sTo.trim();

        Email email = new Email(Const.sMAIL_SERVER);
        email.setHtml(true);

        if (email.sendMsg(sFrom, sTo, subject, messageMail)) {
          return SENT_BY_EMAIL;
        } else {
          notify = true;
        }
      }
    }
    if (notify) {
      if (BeanFactory.getNotificationManagerBean().notifyUser(userInfo, to, message) == NotificationManager.NOTIFICATION_OK) {
        return SENT_BY_NOTIFICATION;
      } else {
        return SENT_BY_ERROR;
      }
    }
    return SENT_BY_NONE;
  }
}
