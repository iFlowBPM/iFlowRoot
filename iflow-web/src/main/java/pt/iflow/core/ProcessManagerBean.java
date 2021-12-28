package pt.iflow.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.audit.AuditData;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.core.UserProcesses;
import pt.iflow.api.core.UserProcsConst;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.filters.FlowFilter;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowHolder;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.flows.FlowSettings;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.notification.Email;
import pt.iflow.api.notification.EmailManager;
import pt.iflow.api.notification.EmailTemplate;
import pt.iflow.api.notification.NotificationManager;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.processdata.ProcessXml;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iflow.api.transition.FlowStateLogTO;
import pt.iflow.api.transition.LogTO;
import pt.iflow.api.transition.UpgradeLogTO;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.userdata.views.UserViewInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.utils.series.FirstOverlimitException;
import pt.iflow.api.utils.series.SeriesManager;
import pt.iflow.api.utils.series.SeriesProcessor;
import pt.iflow.delegations.DelegationManager;
import pt.iflow.update.UpdateManager;
import bsh.org.objectweb.asm.Type;

/**
 * 
 * Process Management
 * 
 * 
 */

public class ProcessManagerBean implements ProcessManager {

  private static ProcessManagerBean instance = null;

  private ProcessManagerBean() {
  }

  public static ProcessManagerBean getInstance() {
    if (null == instance)
      instance = new ProcessManagerBean();
    return instance;
  }

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public void test(int a) {
  }

  /*****************************************************************************
   * Process Methods
   ****************************************************************************/
  /**
   * Creates a new process instance
   * 
   * @param userid
   *          user id
   * @return pid process id
   * @throws Exception
   */
  public ProcessData createProcess(UserInfoInterface userInfo, int flowid) throws Exception {

    String userid = userInfo.getUtilizador();

    Logger.trace(this, "createProcess", userid + " call for flow " + flowid);

    ProcessHeader procHeader = new ProcessHeader(flowid, Const.nSESSION_PID, Const.nSESSION_SUBPID);
    procHeader.setCreationDate(new Date());
    procHeader.setCreator(userInfo.getUtilizador());
    procHeader.setCurrentUser(userInfo.getUtilizador());

    Flow flow = BeanFactory.getFlowBean();
    ProcessCatalogue catalogue = flow.getFlowCatalogue(userInfo, flowid);
    ProcessData procData = new ProcessData(catalogue, procHeader);

    if (getProcessPermissions(userInfo, procData) <= 1) {
      Logger.trace(this, "createProcess", userid + " NO PERMISSIONS!");
      return null;
    }

    initializeProcessData(userInfo, procData);

    if (procData.isInDB()) {
      prepareProcInDB(userInfo, procData);
    }

    return procData;
  }

  /**
   * Creates a new sub process instance
   * 
   * @param userInfo
   *          user information
   * @param dataSet
   *          process data
   */
  public boolean createSubProcess(UserInfoInterface userInfo, ProcessData procData, int blockId, String description) {

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String userid = userInfo.getUtilizador();
    boolean retObj = false;

    Logger.trace(this, "createSubProcess", userid + " call for flow " + flowid + " and pid " + pid + " and subpid " + subpid + ".");

    if (getProcessPermissions(userInfo, procData.getProcessHeader()) <= 1) {
      Logger.trace(this, "createSubProcess", userid + " NO PERMISSIONS!");
      return retObj;
    }

    if (!this.getNextSubPid(userInfo, procData)) {
      Logger.error(userid, this, "createSubProcess", procData.getSignature() +"Could not get next sub pid!");
      return retObj;

    }

    int newsubpid = procData.getSubPid();

    Connection db = null;
    Statement st = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      String query = DBQueryManager.processQuery("ProcessManager.insert_state", new Object[]{
          String.valueOf(flowid),
          String.valueOf(pid),
          String.valueOf(newsubpid),
          String.valueOf(blockId),
          description
      });
      st.execute(query);

      procData.setCreationDate(new Date());
      procData.setCreator(procData.getCurrentUser());

      // ensure subprocess is saved...
      modifyProcessData(userInfo, procData);

      retObj = true;

    } catch (SQLException sqle) {
      Logger.error(userid, this, "createSubProcess", procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
      retObj = false;
    } catch (Exception e) {
      Logger.error(userid, this, "createSubProcess", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      retObj = false;
    } finally {
      DatabaseInterface.closeResources(db, st);
    }

    return retObj;
  }

  private boolean getNextSubPid(UserInfoInterface userInfo, ProcessData procData) {
    boolean retObj = false;

    String userid = userInfo.getUtilizador();
    int flowid = procData.getFlowId();
    int pid = procData.getPid();

    Connection db = null;
    CallableStatement cst = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);

      // cst = db.prepareCall("call get_next_sub_pid(?," + flowid + "," + pid
      // + "," + sCreated + ")");

      cst = db.prepareCall("{call get_next_sub_pid(?,?,?,?,?,?)}");

      cst.registerOutParameter(1, java.sql.Types.NUMERIC);
      cst.setInt(2, flowid);
      cst.setInt(3, pid);
      cst.setTimestamp(4, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
      cst.setString(5, userid);
      cst.setInt(6, procData.getSubPid());

      cst.execute();
      int newsubpid = cst.getInt(1);
      if (newsubpid > 0) {
        procData.setSubPid(newsubpid);
        retObj = true;
      }
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getNextSubPid", procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
      retObj = false;
    } catch (Exception e) {
      Logger.error(userid, this, "getNextSubPid", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      retObj = false;
    } finally {
      DatabaseInterface.closeResources(db, cst);
    }
    return retObj;
  }

  private boolean getNextPid(UserInfoInterface userInfo, ProcessData procData) {
    boolean retObj = false;

    int flowid = procData.getFlowId();
    String userid = userInfo.getUtilizador();

    Connection db = null;
    CallableStatement cst = null;
    Date dt = procData.getCreationDate();
    String sCreator = procData.getCreator();

    if (dt == null)
      dt = new Date();

    try {
      db = DatabaseInterface.getConnection(userInfo);

      // cst = db.prepareCall("call get_next_pid(?, ?," + flowid + "," +
      // sCreated
      // + ")");

      cst = db.prepareCall("{call get_next_pid(?,?,?,?,?)}");

      cst.registerOutParameter(1, java.sql.Types.NUMERIC);
      cst.registerOutParameter(2, java.sql.Types.NUMERIC);

      cst.setInt(3, flowid);
      cst.setTimestamp(4, new java.sql.Timestamp(dt.getTime()));
      cst.setString(5, sCreator);

      cst.execute();
      int pid = cst.getInt(1);
      int subpid = cst.getInt(2);
      if (pid > 0 && subpid > 0) {
        procData.setPid(pid);
        procData.setSubPid(subpid);
        retObj = true;
      }
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getNextPid", procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
      retObj = false;
    } catch (Exception e) {
      Logger.error(userid, this, "getNextPid", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      retObj = false;
    } finally {
      DatabaseInterface.closeResources(db, cst);
    }

    // series
    if (retObj){
      retObj = updateProcessNumber(userInfo, procData);
      updatePidDocs(userInfo, procData);
    }
    
    return retObj;
  }

  private boolean updateProcessNumber(UserInfoInterface userInfo, ProcessData procData) {
    Connection db = null;
    PreparedStatement pst = null;
    PreparedStatement pstHist = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);

      IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, procData.getFlowId());
      String pnumber = String.valueOf(procData.getPid());
      if (fd.getSeriesId() != IFlowData.NO_SERIES) {
        SeriesProcessor sp = SeriesManager.getSeries(userInfo, fd.getSeriesId());
        try {
          pnumber = sp.getNext();
        } catch (FirstOverlimitException foe) {
          String unformatterSource = userInfo.getMessages().getString("series.error.firstoverlimit.source");
          String source = MessageFormat.format(unformatterSource, fd.getName());
          String message = userInfo.getMessages().getString("series.error.firstoverlimit.message");
          NotificationManager notificationManager = BeanFactory.getNotificationManagerBean();
          notificationManager.notifyOrgError(userInfo, source, message);
          throw foe;
        }
      }
      procData.setPNumber(pnumber);

      // UPDATE PROCESS TABLE
      pst = db.prepareStatement("update process set pnumber=? where flowid=? and pid=? and subpid=?");
      pst.setString(1, pnumber);
      pst.setInt(2, procData.getFlowId());
      pst.setInt(3, procData.getPid());
      pst.setInt(4, procData.getSubPid());

      pstHist = db.prepareStatement("update process_history set pnumber=? where flowid=? and pid=? and subpid=?");
      pstHist.setString(1, pnumber);
      pstHist.setInt(2, procData.getFlowId());
      pstHist.setInt(3, procData.getPid());
      pstHist.setInt(4, procData.getSubPid());

      pst.executeUpdate();
      pstHist.executeUpdate();

      DatabaseInterface.commitConnection(db);
    }
    catch (Exception se) {
        try {
        DatabaseInterface.rollbackConnection(db);
        }
      catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "updateProcessNumber", procData.getSignature() + "unable to rollback: " + e.getMessage(),e);
      }
      Logger.error(userInfo.getUtilizador(), this, "updateProcessNumber", procData.getSignature() + "caught exception: " + se.getMessage(), se);
      return false;
    } finally {
      DatabaseInterface.closeResources(db, pst, pstHist);
    }
    return true;
  }


  /**
   * Prepares if necessary process in database (switch from session to db)
   * 
   * @return true if process was switched (created); false otherwise
   */
  public boolean prepareProcInDB(UserInfoInterface userInfo, ProcessData procData) throws Exception {
    return prepareProcInDB(userInfo, procData, false);
  }
  
  /**
   * Prepares if necessary process in database (switch from session to db)
   * 
   * @return true if process was switched (created); false otherwise
   */
  public boolean prepareProcInDB(UserInfoInterface userInfo, ProcessData procData, boolean allowGuestForForward) throws Exception {

    if (userInfo.isGuest()) {
      if (allowGuestForForward || procData.getPid() != Const.nSESSION_PID) {
        Logger.info(userInfo.getUtilizador(), this, "prepareProcInDB", "Guest user saving process for forward");        
      }
      else {
        Logger.error(userInfo.getUtilizador(), this, "prepareProcInDB", "Guest user cannot save process... throwing exception...");
        throw new Exception("Guest user cannot save process");
      }
    }

    try {
      // procData.set(Const.sPROCESS_LOCATION, Const.sPROCESS_IN_DB);
      procData.setInDB(true);

      if (procData.getPid() == Const.nSESSION_PID) {
        Logger.debug(userInfo.getUtilizador(), this, "prepareProcInDB", procData.getSignature() + "Going for get next pid");

          if (!this.getNextPid(userInfo, procData)) {
            Logger.error(userInfo.getUtilizador(), this, "prepareProcInDB", procData.getSignature() + "Unable to get next pid");
            return false;
          }

        int tmpmid = this.modifyProcessData(userInfo, procData);

        procData.setTempData("mid", String.valueOf(tmpmid));

      } else {
        procData.setTempData("mid", null);
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "prepareProcInDB", procData.getSignature() + "exception caught: " + e.getMessage(), e);
      return false;
    }
    return true;
  }

  /**
   * Gets process permissions for specific user
   * 
   * @param pid
   *          process id (0, if asking for process creation)
   * @param userid
   *          user id
   * @returns permissions: 0 - denied; 1 - read-only; 2 - read-write;
   */
  public int getProcessPermissions(UserInfoInterface userInfo, ProcessData procData) {
    return getProcessPermissions(userInfo, procData.getProcessHeader());
  }

  public int getProcessPermissions(UserInfoInterface userInfo, ProcessHeader procHeader) {
    // XXX
    // TODO: SO PERMITE ENTRAR EM READ-WRITE SE O PROCESSO ESTIVER NA
    // WORKLIST DO USER
    return 2;
  }

  /**
   * Checks if a process is leaving or arriving to a mined block
   * 
   * @param userInfo
   * @param procData
   * @param blockIdOld
   * @param blockIdNext
   */
  public boolean checkBlockIsMined(UserInfoInterface userInfo, ProcessData procData, int blockid, int blockidNext) {

    String login = userInfo.getUtilizador();
    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    boolean retObj = false;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      rs = st.executeQuery("select count(mined) as mined from forkjoin_mines where " 
      + "flowid=" + flowid + " and pid=" + pid
          + " and mined <> " + subpid + " and (blockid in (" + blockid + "," + blockidNext + ") or blockid in "
          + "(select parentblockid from forkjoin_state_dep where flowid=" + flowid + " and blockid in (" + blockid + ","
          + blockidNext + ")))");
      rs.next();
      if (rs.getInt("mined") > 0) {
        retObj = true;
      }
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(login, this, "checkBlockMined", procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(login, this, "checkBlockMined", procData.getSignature() + "caught exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  /**
   * The block is blocked if lock flag is not 0 or not is own subpid
   * 
   * @param userInfo
   * @param procData
   * @param blockid
   * @return
   */
  public boolean checkBlockIsLocked(UserInfoInterface userInfo, ProcessData procData, int blockid) {

    String login = userInfo.getUtilizador();
    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    boolean retObj = false;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      rs = st.executeQuery("select count(locked) as num from forkjoin_mines " 
      + "where flowid=" + flowid + " and pid=" + pid
          + " and locked<>" + subpid + " and blockid=" + blockid);
      rs.next();
      if (rs.getInt("num") > 0) {
        retObj = true;
      }
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(login, this, "checkBlockIsLocked", procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(login, this, "checkBlockIsLocked", procData.getSignature() + "caught exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  public void deployMinesInProcess(UserInfoInterface userInfo, ProcessData procData, int blockid) {
    String login = userInfo.getUtilizador();
    Connection db = null;
    Statement st = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);
      st = db.createStatement();

      this.deployMinesInProcess(userInfo, procData, st, blockid);
      DatabaseInterface.commitConnection(db);
    }
    catch (Exception e) {
      Logger.error(login, this, "deployMinesInProcess", 
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
      try {
        DatabaseInterface.rollbackConnection(db);
      }
      catch (Exception e1) {
      }
      }
    finally {
      DatabaseInterface.closeResources(db, st);
    }
  }

  private void deployMinesInProcess(UserInfoInterface userInfo, ProcessData procData, Statement st, int blockid)
      throws SQLException {

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();

    ResultSet rs = null;
    int mined = 0;
    int count = 0;

    try {

      rs = st.executeQuery("select count(mined) as num from forkjoin_mines where " + "flowid=" + flowid + " and pid=" + pid
          + " and blockid=" + blockid);
      rs.next();
      count = rs.getInt("num");
      rs.close();
      rs = null;

      if (count > 0) {
        rs = st.executeQuery("select mined from forkjoin_mines where flowid=" + flowid + " and pid=" + pid + " and blockid="
            + blockid);
        rs.next();
        mined = rs.getInt("mined");
        if (mined == 0) {
          st.executeUpdate("update forkjoin_mines set mined=" + subpid + " where flowid=" + flowid + " and pid=" + pid
              + " and blockid" + blockid);

        }

      } else { // no records
        rs = st.executeQuery("select blockid from forkjoin_hierarchy where " + "flowid=" + flowid + " and parentblockid=" + blockid);
        List<Integer> altmp = new ArrayList<Integer>();
        while (rs.next()) {
          altmp.add(rs.getInt("blockid"));
        }
        rs.close();
        rs = null;

        for (Integer sonBlockid : altmp) {
          this.deployMinesInProcess(userInfo, procData, st, sonBlockid);
        }

        st.executeUpdate("insert into forkjoin_mines (flowid,pid,blockid,mined,locked) values (" + flowid + "," + pid + ","
            + blockid + "," + subpid + ", 0)");

      }

    } catch (SQLException e) {
      throw e;
    } finally {
      DatabaseInterface.closeResources(rs);
    }
  }

  public void unlockBlock(UserInfoInterface userInfo, ProcessData procData, int blockid) {

    String login = userInfo.getUtilizador();
    int flowid = procData.getFlowId();
    int pid = procData.getPid();

    Connection db = null;
    Statement st = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();

      st.executeQuery("update forkjoin_mines set locked=0 where flowid=" + flowid + " and pid=" + pid + " and blockid=" + blockid);

    } catch (SQLException sqle) {
      Logger.error(login, this, "unlockBlock", procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(login, this, "unlockBlock", procData.getSignature() + "caught exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }
  }

  public void lockBlock(UserInfoInterface userInfo, ProcessData procData, int blockid) {

    String login = userInfo.getUtilizador();
    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getPid();

    Connection db = null;
    Statement st = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();

      st.executeQuery("update forkjoin_mines set locked=" + subpid + " where flowid=" + flowid + " and pid=" + pid
          + " and blockid=" + blockid);

    } catch (SQLException sqle) {
      Logger.error(login, this, "lockBlock", procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(login, this, "lockBlock", procData.getSignature() + "caught exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }

  }

  /*****************************************************************************
   * DataSet Operations
   ****************************************************************************/

  /**
   * Initializes the Process Data.
   * 
   * @param pid
   *          process id
   */
  public ProcessData initializeProcessData(UserInfoInterface userInfo, ProcessData procData) {

    ProcessHeader header = procData.getProcessHeader();
    if (header.getCreator() == null) {
      header.setCreator(userInfo.getUtilizador());
    }
    if (header.getCreationDate() == null) {
      header.setCreationDate(new Date());
    }
    if (header.getCurrentUser() == null) {
      header.setCurrentUser(userInfo.getUtilizador());
    }

    userInfo.updateProcessData(procData);

    FlowSetting procLocation = BeanFactory.getFlowSettingsBean().getFlowSetting(procData.getFlowId(), Const.sPROCESS_LOCATION);
    procData.setInDB(StringUtils.equals(procLocation.getValue(), Const.sPROCESS_IN_DB));

    FlowHolder holder = BeanFactory.getFlowHolderBean();

    IFlowData fd = holder.getFlow(userInfo, procData.getFlowId());
    if(FlowType.SUPPORT.equals(fd.getFlowType()) || FlowType.SEARCH.equals(fd.getFlowType()))
      procData.setInDB(false);
    
    // set catalogue vars
    setProcessCatalogueVars(userInfo, procData, fd.getCatalogue());

    return procData;
  }

  ProcessData setProcessCatalogueVars(UserInfoInterface userInfo, ProcessData procData, ProcessCatalogue catalogue) {
    String user = userInfo.getUtilizador();
    
    for (String key : catalogue.getSimpleVariableNames()) {
      String valueExpression = catalogue.getDefaultValueExpression(key);
      if (StringUtils.isEmpty(valueExpression))
        continue;
      
      Object value;
      try {
        value = procData.eval(userInfo, valueExpression);
        procData.set(key, value);

        Logger.warning(user, this, "setProcessCatalogueVars", 
            procData.getSignature() + "set " + key + " with initial value " + value);
      } 
      catch (EvalException e) {
        Logger.warning(user, this, "setProcessCatalogueVars", 
            procData.getSignature() + "eval exception for var " + key + ": " + valueExpression, e);
      }
    }

    return procData;
  }

  // XXX agon: rever metodo.. ir buscar xml para o mid respectivo e fazer
  // overwrite
  public ProcessData undoProcessData(UserInfoInterface userInfo, int flowid, int pid, int subpid, int newMid, ProcessData procData) throws Exception {

    String userid = userInfo.getUtilizador();
    Logger.trace(this, "undoProcessData", userid + " call.");

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    ProcessData previousProcess = null;
    String pdatazip = "";
    Reader pdata;
    try {
      
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      
      String query = DBQueryManager.processQuery("ProcessManager.undo_process_data", 
          new Object[]{String.valueOf(flowid),String.valueOf(pid),String.valueOf(subpid),String.valueOf(newMid)});
      Logger.debug(userid, this, "undoProcessData", "executing query: "+query);
      rs = st.executeQuery(query);
      if (rs.next()) {
          if(rs.getBytes("procdatazip")==null || rs.getBytes("procdatazip").length == 0){ //Caso nao esteja comprimido
            previousProcess = new ProcessXml(procData.getCatalogue(), rs.getCharacterStream("procdata")).getProcessData();
          }else{                                      //Caso esteja comprimido
            pdatazip = uncompress(rs.getBytes("procdatazip"));
            pdata = new StringReader(pdatazip);
            previousProcess = new ProcessXml(procData.getCatalogue(), pdata).getProcessData();
          }
      }
      rs.close();
      rs = null;

      st.executeUpdate("update process_history set undoflag=1 where flowid=" + flowid + " and pid=" + pid + " and subpid="
          + subpid + " and mid>" + newMid + " and undoflag=0");

      st.executeUpdate("update process set mid=" + newMid + " where flowid=" + flowid + " and pid=" + pid + " and subpid="
              + subpid);

      previousProcess.setMid(newMid);
      modifyProcessData(userInfo, previousProcess, true);

      Logger.info(userid, this, "undoProcessData", previousProcess.getSignature() + "process undone to mid " + previousProcess.getMid());
      
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return previousProcess;
  }

  // gets next modification id and updates tables accordingly
  public int getNextMid(UserInfoInterface userInfo, ProcessData procData) throws Exception {
    return getNextMid(userInfo, procData.getProcessHeader());
  }
  
  // gets next modification id and updates tables accordingly
  public int getNextMid(UserInfoInterface userInfo, ProcessHeader procHeader) throws Exception {
    return internalGetNextMid(userInfo, procHeader, true);
  }
  
  private int getNextActivityMid(UserInfoInterface userInfo, ProcessHeader procHeader) throws Exception {
    return internalGetNextMid(userInfo, procHeader, false);
  }

  private int internalGetNextMid(UserInfoInterface userInfo, ProcessHeader procHeader, 
      boolean updateProcess) throws Exception {

    int flowid = procHeader.getFlowId();
    int pid = procHeader.getPid();
    int subpid = procHeader.getSubPid();
    String userid = userInfo.getUtilizador();

    Logger.trace(this, "internalGetNextMid", userid + " call.");

    Connection db = null;
    Statement st = null;

    int mid = -1;

    try {

      mid = atomicGetNextMid(userInfo, procHeader);

      if (updateProcess) {
        db = DatabaseInterface.getConnection(userInfo);
        db.setAutoCommit(false);

        String querySuffix = "set mid=" + mid +
        " where subpid=" + subpid +
        " and pid=" + pid +
        " and flowid=" + flowid;

        st = db.createStatement();

        st.executeUpdate("update process " + querySuffix);
        st.executeUpdate("update flow_state " + querySuffix);

        DatabaseInterface.commitConnection(db);

        Logger.info(userid, this, "getNextMid", procHeader.getSignature() + "process and flow_state updated with mid " + mid);
      }
    }
    catch (Exception e) {
      Logger.warning(userid, this, "getNextMid", 
          procHeader.getSignature() + "caught exception (throwing to caller to handle it): " + e.getMessage());
      throw e;
    }
    finally {
      DatabaseInterface.closeResources(db, st);
    }
    return mid;
  }

  Object getNextMidSync = new Object();
  private int atomicGetNextMid(UserInfoInterface userInfo, ProcessHeader procHeader) throws Exception {
    int flowid = procHeader.getFlowId();
    int pid = procHeader.getPid();
    int subpid = procHeader.getSubPid();
    String userid = userInfo.getUtilizador();

    Logger.trace(this, "atomicGetNextMid", userid + " call.");

    Connection db = null;
    CallableStatement cst = null;
    int mid = 0;
    try {
      // use "atomic"/dedicated connection
      db = Utils.getDataSource().getConnection();
      db.setAutoCommit(true);
      
      cst = db.prepareCall("{call get_next_mid(?,?,?,?,?)}");

      cst.registerOutParameter(1, java.sql.Types.NUMERIC);
      cst.setString(2, userid);
      cst.setInt(3, flowid);
      cst.setInt(4, pid);
      cst.setInt(5, subpid);

      synchronized (getNextMidSync) {
        cst.execute();        
        mid = cst.getInt(1);
      }
      Logger.debug(userid, this, "atomicGetNextMid", procHeader.getSignature() + "Next mid is " + mid);
    }
    finally {
      DatabaseInterface.closeResources(db, cst);
    }
    return mid;
  }

  
  /**
   * 
   * Modifies a process, if the user has permission to do it
   * 
   * @param userInfo
   *          user info
   * @param procData
   * @return
   */
  public int modifyProcessData(UserInfoInterface userInfo, ProcessData procData) throws Exception {
    return modifyProcessData(userInfo, procData, false);
  }

  private int modifyProcessData(UserInfoInterface userInfo, ProcessData procData, boolean isUndo) throws Exception {
    String userid = userInfo.getUtilizador();
  
    Logger.trace(this, "modifyProcessData", userid + " call.");

    if (getProcessPermissions(userInfo, procData) < 2) {
      Logger.trace(this, "modifyProcessData", "NO PERMISSIONS: USER: " + userid);
      return -1;
    }

    if (this.prepareProcInDB(userInfo, procData)) { 
      if (procData == null) {
        return -1;
      }

      String stmp = procData.getTempData("mid"); 
      if (StringUtils.isNotEmpty(stmp)) {
        procData.setTempData("mid", null);
        return Integer.parseInt(stmp);
      }
    }

    if (!procData.isModified())
      return 0;

    int mid = procData.getMid();
    if (mid < 0) {
      mid = this.getNextMid(userInfo, procData);
      procData.setMid(mid);
      Logger.debug(userid, this, "modifyProcessData", procData.getSignature() + "set procMid: " + mid);
    }

    if (mid == 0) {
      // mid = 0 !!! abort
      Logger.warning(userid, this, "modifyProcessData", procData.getSignature() + "MODIFYING ID IS 0 !!");
      return -1;
    } else {
      Logger.debug(userid, this, "modifyProcessData", procData.getSignature() + "MODIFYING ID IS " + mid);
    }

    Date modDate = new Date();

    procData.setCurrentUser(userid);
    procData.setLastUpdate(modDate);

    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    long start = System.currentTimeMillis();
    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);

      final String xml = new ProcessXml(procData).getXml();

      Timestamp tsLastUpdate = new Timestamp(procData.getLastUpdate().getTime());

      StringBuilder sbQuery = new StringBuilder();
      sbQuery.append("update process set ");
      sbQuery.append("mid=?,currentuser=?,lastupdate=?,procdata=?");

      ArrayList<String> parameters = new ArrayList<String>();
      Map<String, Integer> idxCatalog = BeanFactory.getFlowHolderBean().getFlow(userInfo, procData.getFlowId()).getIndexVars();
      Iterator<String> indexVars = idxCatalog.keySet().iterator();
      StringBuilder sbIndexHist = new StringBuilder();
      while (indexVars.hasNext()) {
        String indexVar = indexVars.next();
        String colname = "idx" + idxCatalog.get(indexVar);
        sbQuery.append(",").append(colname).append("=?");
        sbIndexHist.append(",").append(colname);
        ProcessSimpleVariable sv = procData.get(indexVar);
        if (sv != null) {
          parameters.add(sv.getRawValue());
          Logger.info(userid, this, "modifyProcessData", procData.getSignature() + "Added " + sv.getName() + " to index vars");
        } else {
          parameters.add(null);
        }

      }
      sbQuery.append(" where flowid=? and pid=? and subpid=?");

      Logger.debug(userid, this, "modifyProcessData", procData.getSignature() + "QUERY="+sbQuery);
      
      pst = db.prepareStatement(sbQuery.toString());

      int index = 1;
      pst.setInt(index++, mid);
      pst.setString(index++, procData.getCurrentUser());
      pst.setTimestamp(index++, tsLastUpdate);

      pst.setCharacterStream(index++, new StringReader(xml), xml.length());

      if(Const.DEBUG_PROC_XML) {
        Logger.debug(userid, this, "modifyProcessData", procData.getSignature() + "PROC_XML ("+xml.length()+"): " + xml);
      }
      
      for (int i = 0; i < parameters.size(); i++) {
        pst.setString(index++, parameters.get(i));
      }

      pst.setInt(index++, procData.getFlowId());
      pst.setInt(index++, procData.getPid());
      pst.setInt(index++, procData.getSubPid());

      pst.execute();
      pst.close();
      pst = null;

      procData.setInDB(true);

      if (!isUndo) {
        // HISTORY
        
        // check if history with mid already exists
        sbQuery = new StringBuilder();
        sbQuery.append("select 1 from process_history ");
        sbQuery.append("where flowid=? and pid=? and subpid=? and mid=?");
        
        pst = db.prepareStatement(sbQuery.toString());

        Logger.debug(userid, this, "modifyProcessData", 
            procData.getSignature() + "HIST QUERY="+sbQuery);

        pst.setInt(1, procData.getFlowId());
        pst.setInt(2, procData.getPid());
        pst.setInt(3, procData.getSubPid());
        pst.setInt(4, mid);

        rs = pst.executeQuery();
        boolean hasHistEntry = rs.next();
        rs.close();
        rs = null;
        pst.close();
        pst = null;
        
        Logger.debug(userid, this, "modifyProcessData", 
            procData.getSignature() + "Has HIST entry=" + hasHistEntry);
        
        if (hasHistEntry) {
          // update it
          Logger.warning(userid, this, "modifyProcessData", 
              procData.getSignature() + "Has HIST entry. Updating it");

          sbQuery = new StringBuilder();
          sbQuery.append("update process_history set ");
          sbQuery.append("currentuser=?,lastupdate=?,pnumber=?,closed=? ");
          if (doSaveProcessHistory(procData.hasChanged())) {
            sbQuery.append(", procdata=?, procdatazip=? ");
          }
          
          sbQuery.append("where flowid=? and pid=? and subpid=? and mid=?");
          
          pst = db.prepareStatement(sbQuery.toString());

          Logger.debug(userid, this, "modifyProcessData", 
              procData.getSignature() + "HIST UPDATE QUERY="+sbQuery);

          int i = 0;
          pst.setString(++i, procData.getCurrentUser());
          pst.setTimestamp(++i, tsLastUpdate);
          pst.setString(++i, procData.getPNumber());
          pst.setInt(++i, procData.isClosed() ? 1 : 0);
          
          if (doSaveProcessHistory(procData.hasChanged())) {
            if (Const.SAVE_PROCESSHISTORY_METHOD.equals(Const.SAVE_PROCESSHISTORY_METHOD_COMPRESS)){
              pst.setNull(++i, Type.CHAR);
              pst.setBytes(++i, compress(xml));
            }else if (Const.SAVE_PROCESSHISTORY_METHOD.equals(Const.SAVE_PROCESSHISTORY_METHOD_NOTHING)){
              pst.setNull(++i, Type.CHAR);
              pst.setNull(++i, Type.BYTE);            
            }else{
              pst.setCharacterStream(++i, new StringReader(xml), xml.length());
              pst.setNull(++i, Type.BYTE);            
            }
          }
          
          pst.setInt(++i, procData.getFlowId());
          pst.setInt(++i, procData.getPid());
          pst.setInt(++i, procData.getSubPid());
          pst.setInt(++i, mid);
          
          pst.execute();
          pst.close();
          pst = null;

          Logger.debug(userid, this, "modifyProcessData", 
              procData.getSignature() + "HIST UPDATE EXECUTED");
          
        }
        else {
          if (doSaveProcessHistory(procData.hasChanged())) {
            sbQuery = new StringBuilder();
            sbQuery.append("insert into process_history ");
            sbQuery.append("(flowid,pid,subpid,mid,creator,created,");
            sbQuery.append("currentuser,lastupdate,pnumber,procdata,procdatazip,closed");
            sbQuery.append(sbIndexHist.toString()).append(")");
            sbQuery.append(" values (?,?,?,?,?,?,?,?,?,?,?,?");
            for (int i = 0; i < parameters.size(); i++)
              sbQuery.append(",?");
            sbQuery.append(")");

            pst = db.prepareStatement(sbQuery.toString());

            Logger.debug(userid, this, "modifyProcessData", 
                procData.getSignature() + "HIST INSERT QUERY="+sbQuery);


            pst.setInt(1, procData.getFlowId());
            pst.setInt(2, procData.getPid());
            pst.setInt(3, procData.getSubPid());
            pst.setInt(4, mid);
            pst.setString(5, procData.getCreator());
            pst.setTimestamp(6, new Timestamp(procData.getCreationDate().getTime()));
            pst.setString(7, procData.getCurrentUser());
            pst.setTimestamp(8, tsLastUpdate);
            pst.setString(9, procData.getPNumber());

            if (doSaveProcessHistory(procData.hasChanged())) {
              if (Const.SAVE_PROCESSHISTORY_METHOD.equals(Const.SAVE_PROCESSHISTORY_METHOD_COMPRESS)){
                pst.setNull(10, Type.CHAR);
                pst.setBytes(11, compress(xml));
              }else if (Const.SAVE_PROCESSHISTORY_METHOD.equals(Const.SAVE_PROCESSHISTORY_METHOD_NOTHING)){
                pst.setNull(10, Type.CHAR);
                pst.setNull(11, Type.BYTE);            
              }else{
                pst.setCharacterStream(10, new StringReader(xml), xml.length());
                pst.setNull(11, Type.BYTE);            
              }
            }
            else {
              pst.setNull(10, Type.CHAR);
              pst.setNull(11, Type.BYTE);            
            }

            pst.setInt(12, procData.isClosed() ? 1 : 0);
            for (int i = 0, j = 13; i < parameters.size(); i++, j++)
              pst.setString(j, parameters.get(i));

            pst.execute();
            pst.close();
            pst = null;

            Logger.debug(userid, this, "modifyProcessData", 
                procData.getSignature() + "HIST INSERT EXECUTED");
          }
        }
        // xml = null;
      }

      Logger.debug(userid, this, "modifyProcessData",
          procData.getSignature() + "commiting changes...");

      DatabaseInterface.commitConnection(db);

      Logger.info(userid, this, "modifyProcessData",
          procData.getSignature() + "...done commiting changes");

      procData.resetModified();
    } catch (Exception e) {
      Logger.error(userid, this, "modifyProcessData", procData.getSignature() + "exception: " + e.getMessage(), e);
      try {
        DatabaseInterface.rollbackConnection(db);
        Logger.info(userid, this, "modifyProcessData", procData.getSignature() + "connection rollback.");
      }
      catch (Exception e2) {
        Logger.error(userid, this, "modifyProcessData", procData.getSignature() + "exception rolling back connection: " + e2.getMessage(), e);
      }
      
      throw e;

    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    long end = System.currentTimeMillis();
    Logger.trace("ProcessManagerBean","modifyProcessData", "Process serialization took "+(end-start)+" ms");

    return mid;
  }

  /**
   * retrieves process data
   * 
   * @param pid
   *          process id
   * @returns process data stored in DataSet object
   */
  public ProcessData getProcessData(UserInfoInterface userInfo, ProcessHeader procHeader) {

    ProcessData retObj = null;
    ProcessHeader[] headers = new ProcessHeader[1];
    headers[0] = procHeader;
    ProcessData[] ret = this.getProcessesData(userInfo, headers);

    if (ret != null && ret.length == 1) {
      retObj = ret[0];
    }

    return retObj;
  }

  public ProcessData getProcessData(UserInfoInterface userInfo, ProcessHeader procHeader, int anMode) {

    ProcessData retObj = null;
    ProcessHeader[] headers = new ProcessHeader[1];
    headers[0] = procHeader;
    ProcessData[] ret = this.getProcessesData(userInfo, headers, null, anMode);

    if (ret != null && ret.length == 1) {
      retObj = ret[0];
    }

    return retObj;
  }

  /**
   * retrieves process data for given pids
   * 
   * @param pids
   *          process ids
   * @returns processes data stored in DataSet object for each process
   */
  public ProcessData[] getProcessesData(UserInfoInterface userInfo, ProcessHeader[] headers) {
    return this.getProcessesData(userInfo, headers, null);
  }

  /**
   * retrieves process data for given pids
   * 
   * @param pids
   *          process ids
   * @returns processes data stored in DataSet object for each process
   */
  public ProcessData[] getProcessesData(UserInfoInterface userInfo, ProcessHeader[] headers, int anMode) {

    ProcessData[] retObj = null;

    String userid = userInfo.getUtilizador();

    if (headers.length == 0) {
      Logger.warning(userid, this, "getProcessesData", "no procs to get. returning null");
      return null;
    }

    for (int i = 0; i < headers.length; i++) {
      if (getProcessPermissions(userInfo, headers[i]) <= 0) {
        Logger.trace(this, "getProcessesData", userid + " NO PERMISSIONS!!");
        return null;
      }
    }

    long start = System.currentTimeMillis();
    
    boolean bReadOnly = false;
    int nMode = anMode;
    if (nMode >= Const.nREADONLY_OFFSET) {
      bReadOnly = true;
      nMode = nMode - Const.nREADONLY_OFFSET;
    }

    ArrayList<ProcessData> procs = new ArrayList<ProcessData>();

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      StringBuffer sbQuery = new StringBuffer();

      for (int i = 0; i < headers.length; i++) {
        int flowid = headers[i].getFlowId();
        int pid = headers[i].getPid();
        int subpid = headers[i].getSubPid();

        if (sbQuery.length() == 0)
          sbQuery.append("(");
        else
          sbQuery.append(" or ");
        sbQuery.append("(");

        boolean bFirst = true;
        if (flowid > 0) {
          sbQuery.append("x.flowid=").append(flowid);
          bFirst = false;
        }
        if (pid > 0) {
          if (!bFirst)
            sbQuery.append(" and ");
          sbQuery.append("x.pid=").append(pid);
          bFirst = false;
        }
        if (subpid > 0) {
          if (!bFirst)
            sbQuery.append(" and ");
          sbQuery.append("x.subpid=").append(subpid);
          bFirst = false;
        }
        sbQuery.append(")");
      }

      if (sbQuery.length() > 0)
        sbQuery.append(")");

      String query = null;

      if (nMode == Const.nOPENED_PROCS) {
        sbQuery.append(" and x.closed=0 ");
      } else if (nMode == Const.nCLOSED_PROCS) {
        sbQuery.append(" and x.closed=1 ");
      }
      query = DBQueryManager.processQuery("ProcessManager.get_process_data", new Object[]{ sbQuery});

      Logger.debug(userid, this, "getProcessesData", "QUERY=" + query);

      Hashtable<Integer, ProcessCatalogue> flowCatalogues = new Hashtable<Integer, ProcessCatalogue>();
      Flow flow = BeanFactory.getFlowBean();

      rs = st.executeQuery(query);
      while (rs.next()) {
        int flowid = rs.getInt("flowid");
        if (!flowCatalogues.containsKey(flowid)) {
          flowCatalogues.put(flowid, flow.getFlowCatalogue(userInfo, flowid));
        }
        ProcessCatalogue catalogue = flowCatalogues.get(flowid);
        
        ProcessXml reader = new ProcessXml(catalogue, rs.getCharacterStream("procdata"));
          
        ProcessData procData = reader.getProcessData();
        procData.setReadOnly(bReadOnly);
        procData.setInDB(true);
        if (!procData.isReadOnly()) {
          // avoid error trying to set closed on readonly process
          procData.setClosed(rs.getInt("closed") == 1);
        }
        procs.add(procData);
      }
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessesData", "sql exception " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessesData", "exception " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    retObj = new ProcessData[procs.size()];
    for (int i = 0; i < procs.size(); i++) {
      retObj[i] = procs.get(i);
    }

    long end = System.currentTimeMillis();
    Logger.trace("ProcessManagerBean", "getProcessData", "Process retrieval took "+(end-start)+" ms");
    
    return retObj;
  }

  /**
   * retrieves process data for given pids
   * 
   * @param pids
   *          process ids
   * @returns processes data stored in DataSet object for each process
   */
  public ProcessData[] getProcessesData(UserInfoInterface userInfo, ProcessHeader[] headers, String[] asaFields) {
    return this.getProcessesData(userInfo, headers, Const.nOPENED_PROCS);
  }

  /**
   * retrieves process data for given ProcessData(s)
   * 
   * @param pdaProcs
   *          flow id. if <= 0, retrieves processes for all flows.
   * @param asaFields
   *          required fields. if null or empty, all proc fields are fetched
   * @param anMode
   *          fetch mode: all procs, opened procs or closed procs
   * @returns processes data stored in DataSet object for each process
   */
  public ProcessData[] getProcessesData(UserInfoInterface userInfo, ProcessHeader[] headers, String[] asaFields, int anMode) {
    return this.getProcessesData(userInfo, headers, anMode);
  }

  /**
   * retrieves process data
   * 
   * @param session
   * @returns process data stored in DataSet object
   */
  public ProcessData getProcessData(UserInfoInterface userInfo, int flowid, int pid, int subpid, HttpSession session, int mode) {
    ProcessHeader header = new ProcessHeader(flowid, pid, subpid);
    ProcessData procData = this.getProcessData(userInfo, header, mode);
    return procData;
  }

  /**
   * retrieves process data
   * 
   * @param session
   * @returns process data stored in DataSet object
   */
  public ProcessData getProcessData(UserInfoInterface userInfo, int flowid, int pid, int subpid, HttpSession session) {
    return getProcessData(userInfo, flowid, pid, subpid, session, Const.nOPENED_PROCS);
  }

  public ProcessData getProcessDataToBlock(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    return getProcessData(userInfo, flowid, pid, subpid, null, Const.nOPENED_PROCS);
  }
  /**
   * Returns the process state
   * 
   * @return the process state
   */
  public String getProcessState(UserInfoInterface userInfo, ProcessHeader procHeader) {

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    String state = "";
    String userid = userInfo.getUtilizador();

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();
      rs = st.executeQuery("select description from activity where flowid=" + procHeader.getFlowId() + " and pid="
          + procHeader.getPid() + " and subpid=" + procHeader.getSubPid());
      if (rs.next()) {
        state = rs.getString("description");
      }
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessState", procHeader.getSignature() + "sql exception " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessState", procHeader.getSignature() + "exception " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return state;
  }

  /**
   * Returns the process info.
   * 
   * @return
   */
  public String getProcessInfo(UserInfoInterface userInfo, ProcessHeader procHeader) {

    String userid = userInfo.getUtilizador();

    Logger.trace(this, "getProcessInfo", userid + " call.");

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    String retObj = "";

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();
      rs = st.executeQuery("select info from process"
          + " where flowid=" + procHeader.getFlowId()
          + " and pid=" + procHeader.getPid()
          + " and subpid=" + procHeader.getSubPid()
          + " and closed=0");
      if (rs.next()) {
        if (rs.getString("info") != null) {
          retObj = rs.getString("info");
        }
      }
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessInfo", procHeader.getSignature() + "sql exception " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessInfo", procHeader.getSignature() + "exception " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#getFlowStateLogs(pt.iflow.api.utils.UserInfoInterface, int, java.lang.String, int, int)
   */
  public List<FlowStateLogTO> getFlowStateLogs(UserInfoInterface userInfo, int flowid, String pnumber, int subpid, int state) {
    String login = userInfo.getUtilizador();
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    List<FlowStateLogTO> retObj = new ArrayList<FlowStateLogTO>();
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      StringBuffer query = new StringBuffer();
      query.append("SELECT pid FROM process");
      query.append(" WHERE closed=0 and flowid=" + flowid);
      if(subpid > 0) {
        query.append(" AND subpid=" + subpid);
      }
      query.append(" AND pnumber LIKE '" + pnumber + "'");
      if (Logger.isDebugEnabled()) {
        Logger.debug(login, this, "getFlowStateLogs", "QUERY=" + query.toString());
      }
      rs = st.executeQuery(query.toString());
      int pid = -1;
      if(rs.next()) {
        pid = rs.getInt("pid");
      } else {
        try {
          pid = Integer.parseInt(pnumber);
        } catch(NumberFormatException ex) {
          Logger.error(login, this, "getFlowStateLogs", "[pnumber: " + pnumber + "] Unable to get PID from pnumber=" + pnumber, ex);
          DatabaseInterface.closeResources(db, st, rs);
          return retObj;
        }
      }
      DatabaseInterface.closeResources(rs);
      rs = null;
      query = new StringBuffer();
      query.append("SELECT log.*");
      query.append(" FROM " + FlowStateLogTO.TABLE_NAME + " as fsl");
      query.append(", " + LogTO.TABLE_NAME + " as log");
      query.append(" WHERE fsl." + FlowStateLogTO.LOG_ID + "=log." + LogTO.LOG_ID);
      query.append(" AND fsl." + FlowStateLogTO.FLOW_ID + "=" + flowid);
      query.append(" AND fsl." + FlowStateLogTO.PID + "=" + pid);
      if(subpid > 0) {
        query.append(" AND fsl." + FlowStateLogTO.SUBPID + "=" + subpid);
      }
      query.append(" AND fsl." + FlowStateLogTO.STATE + "=" + state);
      if (Logger.isDebugEnabled()) {
        Logger.debug(login, this, "getFlowStateLogs","QUERY=" + query.toString());
      }
      rs = st.executeQuery(query.toString());
      while (rs.next()) {
        LogTO log = new LogTO(rs.getInt(LogTO.LOG_ID), rs.getString(LogTO.USERNAME), 
            rs.getString(LogTO.CALLER), rs.getString(LogTO.METHOD), rs.getString(LogTO.LOG), 
            rs.getTimestamp(LogTO.CREATION_DATE));
        retObj.add(new FlowStateLogTO(flowid, pid, subpid, state, log));
      }
    } catch (Exception ex) {
      Logger.error(login, this, "getFlowStateLogs", "[pnumber: " + pnumber + "] Caught exception:  ", ex);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }
  
  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#getFullProcessHistory(pt.iflow.api.utils.UserInfoInterface, int, java.lang.String)
   */
  public List<FlowStateHistoryTO> getFullProcessHistory(UserInfoInterface userInfo, int flowid, String pnumber) {
    String login = userInfo.getUtilizador();
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    List<FlowStateHistoryTO> retObj = new ArrayList<FlowStateHistoryTO>();
    try {
      db = DatabaseInterface.getConnection(userInfo);
      int pid = Const.nSESSION_PID;
      
      StringBuffer query = new StringBuffer();
      query.append("SELECT pid FROM process_history");
      query.append(" WHERE flowid=?").append(" AND pnumber LIKE ?");
      if(Logger.isDebugEnabled()) {
        Logger.debug(login, this, "getFullProcessHistory", "QUERY=" + query.toString());
      }
      pst = db.prepareStatement(query.toString());
      pst.setInt(1, flowid);
      pst.setString(2, pnumber);
      rs = pst.executeQuery();
      if(rs.next()) {
        pid = rs.getInt("pid");
      } else {
        try {
          pid = Integer.parseInt(pnumber);
        } catch (NumberFormatException nfe) {
          Logger.error(login, this, "getFullProcessHistory", "[pnumber: " + pnumber + "] Unable to parse pnumber to pid (pnumber=" + pnumber + ")", nfe);
        }
      }
      DatabaseInterface.closeResources(rs);
      
      if (pid != Const.nSESSION_PID) {
        query = new StringBuffer();
        query.append("SELECT * FROM ").append(FlowStateHistoryTO.TABLE_NAME);
        query.append(" WHERE ").append(FlowStateHistoryTO.FLOW_ID).append("=?");
        query.append(" AND ").append(FlowStateHistoryTO.PID).append("=?");
        query.append(" AND ").append(FlowStateHistoryTO.EXIT_FLAG).append("=0");
        query.append(" ORDER BY ").append(FlowStateHistoryTO.MID).append(" ASC");
        if(Logger.isDebugEnabled()) {
          Logger.debug(login, this, "getFullProcessHistory", "QUERY=" + query.toString());
        }
        pst = db.prepareStatement(query.toString());
        pst.setInt(1, flowid);
        pst.setInt(2, pid);
        rs = pst.executeQuery();
        while (rs.next()) {
          FlowStateHistoryTO flowState = new FlowStateHistoryTO(rs);
          String moduser = retrieveModificationUser(userInfo, db, flowState);
          flowState.setModificationUser(StringUtils.isEmpty(moduser) ? "" : moduser);
          retObj.add(flowState);
        }
      }
    } catch (Exception ex) {
      Logger.error(login, this, "getFullProcessHistory", "[pnumber: " + pnumber + "] Caught exception:  ", ex);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    return retObj;
  }

  private String retrieveModificationUser(UserInfoInterface userInfo, Connection db, FlowStateHistoryTO flowState) {
    String retObj = null;
    Statement st = null;
    ResultSet rs = null;
    String login = userInfo.getUtilizador();
    try {
      StringBuffer query = new StringBuffer();
      query.append("SELECT muser FROM modification");
      query.append(" WHERE " + FlowStateHistoryTO.FLOW_ID + "=" + flowState.getFlowid());
      query.append(" AND " + FlowStateHistoryTO.PID + "=" + flowState.getPid());
      query.append(" AND " + FlowStateHistoryTO.SUBPID + "=" + flowState.getSubpid());
      query.append(" AND " + FlowStateHistoryTO.MID + "=" + flowState.getMid());
      if (Logger.isDebugEnabled()) {
        Logger.debug(login, this, "retrieveModificationUser", "QUERY=" + query.toString());
      }
      st = db.createStatement();
      rs = st.executeQuery(query.toString());
      if (rs.next()) {
        retObj = rs.getString("muser");
        if (Logger.isDebugEnabled()) {
          Logger.debug(login, this, "retrieveModificationUser", "Found user '" + retObj + "' for mid '" + flowState.getMid() + "'");
        }
      }
    } catch (Exception ex) {
      Logger.error(login, this, "retrieveModificationUser", "[pid: " + flowState.getPid() + "] Caught exception:  ", ex);
    } finally {
      DatabaseInterface.closeResources(st, rs);
    }
    return retObj;
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#getUgradeLogs(pt.iflow.api.utils.UserInfoInterface)
   */
  public List<UpgradeLogTO> getUgradeLogs(UserInfoInterface userInfo) {
    return getUgradeLogs(userInfo, -1);
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#getUgradeLogs(pt.iflow.api.utils.UserInfoInterface, int)
   */
  public List<UpgradeLogTO> getUgradeLogs(UserInfoInterface userInfo, int limit) {
    String myName = UpdateManager.class.getName();
    String login = userInfo.getUtilizador();
    List<UpgradeLogTO> retObj = new ArrayList<UpgradeLogTO>();
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    if (limit < 1) {
      limit = 200;
    }
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      StringBuffer query = new StringBuffer();
      query.append("SELECT * FROM ").append(UpgradeLogTO.TABLE_NAME);
      query.append(" LIMIT ").append(limit + 1);
      if (Logger.isDebugEnabled()) {
        Logger.debug(login, myName, "getUpdateLogs", "QUERY: " + query.toString());
      }
      rs = st.executeQuery(query.toString());
      while (rs.next()) {
        String signature = rs.getString(UpgradeLogTO.SIGNATURE);
        boolean executed = rs.getBoolean(UpgradeLogTO.EXECUTED);
        boolean error = rs.getBoolean(UpgradeLogTO.ERROR);
        int logId = rs.getInt(UpgradeLogTO.LOG_ID);
        retObj.add(new UpgradeLogTO(signature, executed, error, getLog(userInfo, db, logId)));
      }
    } catch (Exception e) {
      Logger.error(login, myName, "getUpdateLogs", "Exception caught: ", e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#getLog(pt.iflow.api.utils.UserInfoInterface, java.sql.Connection, int)
   */
  public LogTO getLog(UserInfoInterface userInfo, Connection db, int logId) {
    String myName = UpdateManager.class.getName();
    String login = userInfo.getUtilizador();
    LogTO retObj = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      StringBuffer query = new StringBuffer();
      query.append("SELECT * FROM ").append(LogTO.TABLE_NAME);
      query.append(" WHERE ").append(LogTO.LOG_ID).append("=?");
      if (Logger.isDebugEnabled()) {
        Logger.debug(login, myName, "getLog", "QUERY: " + query.toString());
      }
      pst = db.prepareStatement(query.toString());
      pst.setInt(1, logId);
      rs = pst.executeQuery();
      if (rs.next()) {
        String username = rs.getString(LogTO.USERNAME);
        String caller = rs.getString(LogTO.CALLER);
        String method = rs.getString(LogTO.METHOD);
        String log = rs.getString(LogTO.LOG);
        Timestamp creationDate = rs.getTimestamp(LogTO.CREATION_DATE);
        retObj = new LogTO(logId, username, caller, method, log, creationDate);
      }
    } catch (Exception e) {
      Logger.error(login, myName, "getUpdateLogs", "Exception caught: ", e);
    } finally {
      DatabaseInterface.closeResources(pst, rs);
    }
    return retObj;
  }
  
  /**
   * Moves one process from a user to another.
   * 
   * @throws SQLException
   */
  public void moveUserProcess(UserInfoInterface userInfo, ProcessHeader procHeader, String oldUserId, String newUserId) {

    Connection db = null;
    Statement st = null;

    int flowid = procHeader.getFlowId();
    int pid = procHeader.getPid();
    String userid = userInfo.getUtilizador();

    boolean updated = false;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);

      historifyActivities(userInfo, db, procHeader, oldUserId, oldUserId);
      
      String query = DBQueryManager.processQuery("ProcessManager.update_activity", new Object[] { newUserId, oldUserId,
          String.valueOf(flowid), String.valueOf(pid) });

      st = db.createStatement();
      st.executeUpdate(query);

      DatabaseInterface.commitConnection(db);
      updated = true;

      }
    catch (Exception e) {
      Logger.error(userid, this, "moveUserProcess", 
          procHeader.getSignature() + "exception " + e.getMessage(), e);
      try {
        DatabaseInterface.rollbackConnection(db);
      } catch (SQLException e1) {
        Logger.error(userid, this, "moveUserProcess", 
            procHeader.getSignature() + "exception rolling back" + e.getMessage(), e1);        
      }
      updated = false;
      }
    finally {
      DatabaseInterface.closeResources(db, st);
    }

    if (updated) {
      Logger.info(userid, this, "moveUserProcess", procHeader.getSignature() + "oldUserId=[" + oldUserId + "] newUserId=[" + newUserId + "] flowid=[" + flowid
          + "] pid=[" + pid + "]");
    }
  }

  /**
   * Retrieves an activity in a user's current worklist
   * 
   * @param userid
   *          user id
   * @param flowid
   *          flow id
   * @param pid
   *          process id
   */
  public Activity getUserProcessActivity(UserInfoInterface userInfo, ProcessHeader procHeader) {
    return this.getUserProcessActivity(userInfo, procHeader.getFlowId(), procHeader.getPid(), procHeader.getSubPid());
  }

  /**
   * Retrieves an activity in a user's current worklist
   * 
   * @param userid
   *          user id
   * @param flowid
   *          flow id
   * @param pid
   *          process id
   */
  public Activity getUserProcessActivity(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    String userid = userInfo.getUtilizador();

    Logger.trace(this, "getUserProcessActivity", userid + " call.");

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    Activity result = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();

      String escapedUserId = escapeSQL(userid);

      final String activityDelegationsQuery = "select * from activity_delegated where userid='" + escapedUserId + "'"
          + " and flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid;

      rs = st.executeQuery(activityDelegationsQuery);

      if (rs.next()) {
        result = new Activity(userid, rs.getInt("flowid"), rs.getInt("pid"), rs.getInt("subpid"), rs.getInt("type"), 
            rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), rs.getTimestamp("archived"), 
            rs.getString("description"), rs.getString("url"), rs.getInt("status"), rs.getInt("notify"));
        if (rs.getInt("read_flag") == 1) {
          result.setRead();
        }
        else {
          result.setUnread();
        }
        result.mid = rs.getInt("mid");
      } else {
        rs.close();
        final String activityiesQuery = "select * from activity where userid='" + escapedUserId + "'" + " and flowid=" + flowid
            + " and pid=" + pid + " and subpid=" + subpid;

        rs = st.executeQuery(activityiesQuery);

        if (rs.next()) {
          result = new Activity(userid, rs.getInt("flowid"), rs.getInt("pid"), rs.getInt("subpid"), rs.getInt("type"), 
              rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), rs.getTimestamp("archived"), 
              rs.getString("description"), rs.getString("url"), rs.getInt("status"), rs.getInt("notify"));
          result.profilename = rs.getString("profilename");
          if (rs.getInt("read_flag") == 1) {
            result.setRead();
          }
          else {
            result.setUnread();
          }
          result.mid = rs.getInt("mid");
        }
      }

      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getUserProcessActivity", "sql exception " + sqle.getMessage(), sqle);
      result = null;
    } catch (Exception e) {
      Logger.error(userid, this, "getUserProcessActivity", "exception " + e.getMessage(), e);
      result = null;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return result;
  }

  /*****************************************************************************
   * Activity Methods
   ****************************************************************************/

  /**
   * Creates an activity
   * 
   * @param userid
   *          user id
   * @param pid
   *          process id
   * @param type
   *          user-related operation type
   * @param priority
   *          priority
   * @param description
   *          subject
   * @param url
   *          edition url
   */
  public void createActivity(UserInfoInterface userInfo, Activity activity) throws Exception {
    this.createActivity(userInfo, activity, true, false);
  }

  public void createActivity(UserInfoInterface userInfo, Activity activity, boolean hasSelfCreatePriv) throws Exception {
      this.createActivity(userInfo, activity, hasSelfCreatePriv, false);
  }

  public void createActivity(UserInfoInterface userInfo, Activity activity, boolean hasSelfCreatePriv, boolean forceNotRead) throws Exception {
      List<Activity> altmp = new ArrayList<Activity>(1);
      altmp.add(activity);
      this.createActivities(userInfo, altmp.listIterator(), hasSelfCreatePriv, forceNotRead);
  }
  

  public void createActivities(UserInfoInterface userInfo, Iterator<Activity> liActivities) throws Exception {
    this.createActivities(userInfo, liActivities, true);
  }

  public void createActivities(UserInfoInterface userInfo, Iterator<Activity> liActivities, boolean hasSelfCreatePriv)
  throws Exception {
    createActivities(userInfo, liActivities, hasSelfCreatePriv, false);
  }


  public void createActivities(UserInfoInterface userInfo, Iterator<Activity> liActivities, boolean hasSelfCreatePriv, boolean forceNotRead)
      throws Exception {

    String userid = userInfo.getUtilizador();

    Logger.trace(this, "createActivities", userid + " call");

    if (userInfo.isGuest()) {
      Logger.error(userInfo.getUtilizador(), this, "createActivities", "Guest user cannot have activities... throwing exception...");
      throw new Exception("Guest user cannot have activities");
    }

    if (liActivities == null)
      return;

    Connection db = null;

    PreparedStatement pst = null;
    PreparedStatement pstOwner = null;
    PreparedStatement pstUser = null;
    ResultSet rs = null;

    boolean delegated = false;

    java.sql.Timestamp currentDate = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);


      pst = db.prepareStatement("insert into activity (userid,flowid,pid,subpid,type,"
          + "priority,created,started,archived,description,url,status,"
          + "delegated,profilename,read_flag,mid"
          + ") values (?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?, 0, ?, ?, ?, ?)");

      pstUser = db.prepareStatement("select distinct ownerid from activity_hierarchy where flowid=? and userid=?");
      pstOwner = db.prepareStatement("select distinct ownerid from activity_hierarchy where flowid=? and ownerid=?");

      HashMap<String,String> cache = new HashMap<String, String>();
      while (liActivities.hasNext()) {
        Activity a = liActivities.next();

        if (StringUtils.isEmpty(a.url)) {
          Logger.debug(userid, this, "createActivities", "empty url for flowid=" + a.flowid + " and pid=" + a.pid + " and subpid="
              + a.subpid + ". Forcing forward jsp url.");
          a.url = "Forward/forward.jsp?flowid=" + a.flowid + "&pid=" + a.pid + "&subpid=" + a.subpid;
        }
        
        if (a.mid < 0) {
          a.mid = getNextActivityMid(userInfo, new ProcessHeader(a.flowid, a.pid, a.subpid));
        }

        a.created = currentDate;
        a.started = currentDate;
        
        pst.setInt(2, a.flowid);
        pst.setInt(3, a.pid);
        pst.setInt(4, a.subpid);
        pst.setInt(5, a.type);
        pst.setInt(6, a.priority);
        pst.setTimestamp(7, new Timestamp(a.created.getTime()));
        pst.setTimestamp(8, new Timestamp(a.started.getTime()));
        pst.setString(9, a.description);
        pst.setString(10, a.url);
        pst.setInt(14, a.mid);
        
        if (!userInfo.isOrgAdmin() && !hasSelfCreatePriv) {

          /* check if this is a delegated activity */
          pstUser.setInt(1, a.flowid);
          pstUser.setString(2, a.userid);
          rs = pstUser.executeQuery();
          while (rs.next()) {
            delegated = true;
            String ownerid = rs.getString("ownerid");
            String profile = a.profilename == null ? ownerid : a.profilename;
            int read = !forceNotRead? 0 : (StringUtils.equals(userid, ownerid) ? 1 : 0);
            pst.setString(1, ownerid);
            pst.setInt(11, 1); // delegated
            pst.setString(12, profile);
            pst.setInt(13, read);
            pst.execute();
            
            Logger.debug(userid, this, "createActivities", 
                "created delegated activity for user " + ownerid);
            
          }
          rs.close();
          rs = null;

        } else { // if the owner has delegated this flow -> task.delegated = 1

          pstOwner.setInt(1, a.flowid);
          pstOwner.setString(2, a.userid);
          rs = pstOwner.executeQuery();
          if (rs.next()) {
            delegated = true;
            String profile = a.profilename == null ? a.userid : a.profilename;
            int read = !forceNotRead? 0 : (StringUtils.equals(userid, a.userid) ? 1 : 0);
            pst.setString(1, a.userid);
            pst.setInt(11, 1); // delegated
            pst.setString(12, profile);
            pst.setInt(13, read);
            pst.execute();

            Logger.debug(userid, this, "createActivities", 
                "created delegated activity for user " + a.userid);

          }
          rs.close();
          rs = null;
        }

        if (!delegated) {
          String profile = a.profilename == null ? a.userid : a.profilename;
          int read = !forceNotRead? 0 : (StringUtils.equals(userid, a.userid) ? 1 : 0);
          pst.setString(1, a.userid);
          pst.setInt(11, 0); // delegated
          pst.setString(12, profile);
          pst.setInt(13, read);
          pst.execute();

          Logger.debug(userid, this, "createActivities", 
              "created activity for user " + a.userid);

        }

        // check notification
        if (this.prepareNotifyUserActivity(userInfo, a)) {
          String pnum = getActivityPNumber(userInfo, cache, a);
          this.notifyUserActivity(userInfo, a, pnum);
        }

      }
      DatabaseInterface.commitConnection(db);

      Logger.debug(userid, this, "createActivities", "connection committed");

    }
    catch (SQLException sqle) {
      DatabaseInterface.rollbackConnection(db);
      Logger.error(userid, this, "createActivities", "sql exception: " + sqle.getMessage(), sqle);
      // propagate exception so caller may act properly
      throw sqle;
    }
    catch (Exception e) {
      DatabaseInterface.rollbackConnection(db);
      Logger.error(userid, this, "createActivities", "exception: " + e.getMessage(), e);
      // propagate exception so caller may act properly
      throw new SQLException(e.getMessage());
    }
    finally {
      DatabaseInterface.closeResources(db, pst, pstOwner, pstUser, rs);
    }
  }

  /**
   * Retrieves the activity 'real' owners If it isn't a delegated activity,
   * returns the userInfo user
   * 
   * @param userInfo
   *          the calling user info
   * @param activity
   *          the activity whos owners are to be checked
   * @return an array with the real activity owners
   */
  public String[] getActivityOwners(UserInfoInterface userInfo, Activity activity) {

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    List<String> lUsers = new ArrayList<String>();
    String[] retObj = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      if (activity.pid > 0) {
        // when the pid is specified, get all activity owners
        final StringBuilder sbQuery = new StringBuilder("select distinct userid from activity where pid=");
        sbQuery.append(activity.pid);
        if (activity.flowid > 0) {
          sbQuery.append(" and flowid=").append(activity.flowid);
        }
        if (activity.subpid > 0) {
          sbQuery.append(" and subpid=").append(activity.subpid);
        }
        
        rs = st.executeQuery(sbQuery.toString());

        while (rs.next()) {
          lUsers.add(rs.getString("userid"));
        }
        rs.close();
        rs = null;

      } else {

        // check if this is a delegated activity
        final StringBuilder sbQueryHierarchy = new StringBuilder("select ownerid from activity_hierarchy where flowid=");
        sbQueryHierarchy.append(activity.flowid).append(" and userid='").append(escapeSQL(activity.userid)).append("'");
        rs = st.executeQuery(sbQueryHierarchy.toString());

        while (rs.next()) {
          lUsers.add(rs.getString("ownerid"));
        }
        rs.close();
        rs = null;

        // when the userInfo user, is also the owner of a process
        StringBuilder sbQueryActivity = new StringBuilder();
        sbQueryActivity.append("select count(1) from activity where flowid="); // just check if exists
        sbQueryActivity.append(activity.flowid).append(" and userid='").append(escapeSQL(activity.userid)).append("'");
        rs = st.executeQuery(sbQueryActivity.toString());

        if (rs.next()) {
          lUsers.add(activity.userid);
        }
        rs.close();
        rs = null;
      }

    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getActivityOwners", "exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    if (lUsers != null && lUsers.size() > 0) {
      retObj = lUsers.toArray(new String[lUsers.size()]);
    } else {
      retObj = new String[] { activity.userid };
    }

    return retObj;
  }

  /**
   * 
   * Gets the "online" sub-process ids for the given process (pid)
   * 
   * @param userInfo
   *          the requesting user
   * @param procData
   *          the process info
   * @return array of available subpids
   */
  public int[] getProcessSubPids(UserInfoInterface userInfo, ProcessData procData) {
    int[] retObj = null;

    if (!procData.isInDB()) {
      retObj = new int[1];
      retObj[0] = procData.getSubPid();
      return retObj;
    }

    return getProcessSubPids(userInfo, procData.getProcessHeader());
  }

  public int[] getProcessSubPids(UserInfoInterface userInfo, ProcessHeader procHeader) {
    String login = userInfo.getUtilizador();
    int flowid = procHeader.getFlowId();
    int pid = procHeader.getPid();

    int[] retObj = null;

    if (pid == Const.nSESSION_PID || procHeader.getSubPid() == Const.nSESSION_SUBPID) {
      retObj = new int[1];
      retObj[0] = procHeader.getSubPid();
      return retObj;
    }

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    // get the subpids still "online" for this pid
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      rs = st.executeQuery("select distinct(subpid) as subpid from process"
          + " where flowid=" + flowid
          + " and pid=" + pid
          + " and closed=0");

      List<Integer> lSubpids = new ArrayList<Integer>();
      while (rs.next()) {
        lSubpids.add(rs.getInt("subpid"));
      }
      rs.close();
      rs = null;

      retObj = new int[lSubpids.size()];
      for (int i = 0; i < lSubpids.size(); i++) {
        retObj[i] = lSubpids.get(i);
      }
    } catch (Exception e) {
      Logger.error(login, this, "getProcessSubPids", procHeader.getSignature() + "caught exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db,st,rs);
    }

    return retObj;
  }

  /**
   * Retrieves the subpids from a given process that are in specific block
   * 
   * @param userInfo
   *          the calling user info
   * @param procData
   *          the process data
   * @param block
   *          the block where the subpids are to be checked
   * @return an array with the subpids
   */
  public int[] getSubPidsInBlock(UserInfoInterface userInfo, ProcessData procData, Block block) {
    String login = userInfo.getUtilizador();
    int flowid = procData.getFlowId();
    int pid = procData.getPid();

    int[] retObj = null;

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    // get the min subpid that entered block Join
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      Port[] portsIn = block.getInPorts(userInfo);
      Set<Integer> sSubpids = new TreeSet<Integer>();

      for (int i = 0; i < portsIn.length; i++) {
        if (portsIn[i] == null)
          continue;
        int beforeBlockId = portsIn[i].getConnectedBlockId();
        rs = st.executeQuery("select min(subpid) as subpid from flow_state_history where flowid=" + flowid + " and pid=" + pid
            + " and state=" + beforeBlockId);
        if (rs.next()) {
          sSubpids.add(rs.getInt("subpid"));
        }
        rs.close();
        rs = null;
      }

      retObj = new int[sSubpids.size()];
      Iterator<Integer> it = sSubpids.iterator();
      int i = 0;
      while (it.hasNext()) {
        retObj[i++] = it.next().intValue();
      }

    } catch (SQLException sqle) {
      Logger.error(login, this, "getSubPidsInBlock", procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
      retObj = null;
    } finally {
      DatabaseInterface.closeResources(db,st,rs);
    }
    return retObj;
  }

  public java.util.ListIterator<Activity> getProcessActivities(UserInfoInterface userInfo, ProcessData procData) {
    return getProcessActivities(userInfo, procData.getFlowId(), procData.getPid(), procData.getSubPid());
  }

  /**
   * Retrieves the activities for a given process (and flow)
   * 
   * @param userInfo
   *          the calling user info
   * @param anFlowId
   *          the process' flow id
   * @param anPid
   *          the process id
   * @return iterator with process activities
   */
  public java.util.ListIterator<Activity> getProcessActivities(UserInfoInterface userInfo, int anFlowId, int anPid, int anSubPid) {

    String userid = userInfo.getUtilizador();

    Logger.trace(this, "getProcessActivities", userid + " call for flow=" + anFlowId + " and pid=" + anPid + " and subpid="
        + anSubPid);

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    LinkedList<Activity> l = new LinkedList<Activity>();
    java.util.ListIterator<Activity> result = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();

      final StringBuilder sbQuery = new StringBuilder();
      sbQuery.append("SELECT * FROM activity");
      if (anPid > -1 || anSubPid > -1 || anFlowId > -1) {
        boolean order = false;
        if (anPid > -1) {
          sbQuery.append(" WHERE ");
          sbQuery.append("pid=" + anPid);
          order = true;
        }
        if (anSubPid > -1) {
          sbQuery.append(order ? " AND " : " WHERE ");
          sbQuery.append("subpid=" + anSubPid);
          order = true;
        }
        if (anFlowId > -1) {
          sbQuery.append(order ? " AND " : " WHERE ");
          sbQuery.append("flowid=").append(anFlowId);
        }
      }
      sbQuery.append(" ORDER BY created ASC");

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "getProcessActivities", "QUERY=" + sbQuery.toString());
      }

      rs = st.executeQuery(sbQuery.toString());
      while (rs.next()) {
        Activity wle = new Activity(rs.getString("userid"), rs.getInt("flowid"), rs.getInt("pid"), rs.getInt("subpid"), rs.getInt("type"), rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), rs.getTimestamp("archived"), rs.getString("description"), rs.getString("url"), rs.getInt("status"), rs.getInt("notify"));
        wle.profilename = rs.getString("profilename");
        if (rs.getInt("read_flag") == 1) {
          wle.setRead();
        }
        else {
          wle.setUnread();
        }
        wle.mid = rs.getInt("mid");
        l.add(wle);
      }
      rs.close();
      rs = null;
      result = l.listIterator();
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessActivities", "[pid: " + anPid + "] sql exception: " + sqle.getMessage(), sqle);
      result = null;
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessActivities", "[pid: " + anPid + "] exception: " + e.getMessage(), e);
      result = null;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return result;
  }

  public ListIterator<Activity> getActivities(UserInfoInterface userInfo, String profileName) {
      String userid = userInfo.getUtilizador();
      Logger.trace(this, "getActivities", userid + " call.");

      Connection connection = null;
      PreparedStatement prepStatement = null;
      ResultSet resultSet = null;
      ListIterator<Activity> result = null;
      LinkedList<Activity> activities = new LinkedList<Activity>();

      try {
          if(StringUtils.isEmpty(profileName)) throw new Exception("No Profile Name given.");
          connection = DatabaseInterface.getConnection(userInfo);
          prepStatement = connection.prepareStatement("SELECT * FROM activity WHERE profilename = ? " +
                "GROUP BY pid, subpid ORDER BY created ASC");
          prepStatement.setString(1, profileName);
          resultSet = prepStatement.executeQuery();

          while(resultSet.next()) {
              Activity activity = new Activity(resultSet.getString("userid"), resultSet.getInt("flowid"),
                      resultSet.getInt("pid"), resultSet.getInt("subpid"), resultSet.getInt("type"),
                      resultSet.getInt("priority"), resultSet.getTimestamp("created"), resultSet.getTimestamp("started"), 
                      resultSet.getTimestamp("archived"), resultSet.getString("description"), resultSet.getString("url"),
                      resultSet.getInt("status"), resultSet.getInt("notify"), resultSet.getString("profilename"));

              if (resultSet.getInt("read_flag") == 1) {
                  activity.setRead();
              }
              else {
                  activity.setUnread();
              }
              activity.mid = resultSet.getInt("mid");
              activities.add(activity);
          }

          result = activities.listIterator();

      } catch (SQLException sqle) {
          Logger.error(userid, this, "getActivities", "sql exception: " + sqle.getMessage(), sqle);
          result = null;
      } catch (Exception e) {
          Logger.error(userid, this, "getActivities", "exception: " + e.getMessage(), e);
          result = null;
      } finally {
          DatabaseInterface.closeResources(connection, prepStatement, resultSet);
      }
      return result;
  }

  public ListIterator<Activity> getActivities(UserInfoInterface userInfo, int anFlowId, int anPid, int anSubPid, Date adtBefore,
      Date adtAfter, String userIdBefore, String userIdActual) {

    String userid = userInfo.getUtilizador();
    Logger.trace(this, "getActivities", userid + " call.");

    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    ListIterator<Activity> result = null;
    LinkedList<Activity> l = new LinkedList<Activity>();
    final StringBuilder sbFilters = new StringBuilder();
    boolean firstFilter = true;

    adtAfter = Utils.fixDateAfter(adtAfter);
    adtBefore = Utils.fixDateBefore(adtBefore);

    try {
      db = DatabaseInterface.getConnection(userInfo);

      if (!userIdActual.equals("")) {
        sbFilters.append(" where userid=?");
        firstFilter = false;
      }
      if (anFlowId > 0) {
        sbFilters.append(firstFilter ? " where" : " and").append(" flowid=?");
        firstFilter = false;
      }
      if (anPid > 0) {
        sbFilters.append(firstFilter ? " where" : " and").append(" pid=?");
        firstFilter = false;
      }
      if (anSubPid > 0) {
        sbFilters.append(firstFilter ? " where" : " and").append(" subpid=?");
        firstFilter = false;
      }
      if (adtAfter != null) {
        sbFilters.append(firstFilter ? " where" : " and").append(" created >= ?");
        firstFilter = false;
      }
      if (adtBefore != null) {
        sbFilters.append(firstFilter ? " where" : " and").append(" created < ?");
        firstFilter = false;
      }

      sbFilters.append(" order by created asc");

      st = db.prepareStatement("select * from activity" + sbFilters);
      {
        int nPos = 0;
        if (!userIdActual.equals("")) {
          st.setString(++nPos, userIdActual.toUpperCase());
        }
        if (anFlowId > 0) {
          st.setInt(++nPos, anFlowId);
        }
        if (anPid > 0) {
          st.setInt(++nPos, anPid);
        }
        if (anSubPid > 0) {
          st.setInt(++nPos, anSubPid);
        }
        if (adtAfter != null) {
          st.setTimestamp(++nPos, new Timestamp(adtAfter.getTime()));
        }
        if (adtBefore != null) {
          st.setTimestamp(++nPos, new Timestamp(adtBefore.getTime()));
        }
      }
      rs = st.executeQuery();

      while (rs.next()) {
        Activity wle = new Activity(rs.getString("userid"), rs.getInt("flowid"), rs.getInt("pid"), rs.getInt("subpid"), 
            rs.getInt("type"), rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), 
            rs.getTimestamp("archived"), rs.getString("description"), rs.getString("url"), rs.getInt("status"), rs.getInt("notify"));
        wle.profilename = rs.getString("profilename");
        if (rs.getInt("read_flag") == 1) {
          wle.setRead();
        }
        else {
          wle.setUnread();
        }
        wle.mid = rs.getInt("mid");
        l.add(wle);
      }
      rs.close();
      rs = null;
      st.close();
      st = null;

      /*
       * after getting the 'normal' activities, we get the 'delegated ones'
       */
      st = db.prepareStatement("select * from activity_delegated" + sbFilters);
      {
        int nPos = 0;
        if (!userIdActual.equals("")) {
          st.setString(++nPos, userIdActual.toUpperCase());
        }
        if (anFlowId > 0) {
          st.setInt(++nPos, anFlowId);
        }
        if (anPid > 0) {
          st.setInt(++nPos, anPid);
        }
        if (anSubPid > 0) {
          st.setInt(++nPos, anSubPid);
        }
        if (adtAfter != null) {
          st.setTimestamp(++nPos, new Timestamp(adtAfter.getTime()));
        }
        if (adtBefore != null) {
          st.setTimestamp(++nPos, new Timestamp(adtBefore.getTime()));
        }
      }

      rs = st.executeQuery();

      while (rs.next()) {
        Activity wle = new Activity(rs.getString("userid"), rs.getInt("flowid"), rs.getInt("pid"), rs.getInt("subpid"), 
            rs.getTimestamp("created"), rs.getString("description"), rs.getString("url"));
        if (rs.getInt("read_flag") == 1) {
          wle.setRead();
        }
        else {
          wle.setUnread();
        }
        wle.mid = rs.getInt("mid");
        l.add(wle);
      }
      rs.close();
      rs = null;
      st.close();
      st = null;

      result = l.listIterator();

    } catch (SQLException sqle) {
      Logger.error(userid, this, "getActivities", "sql exception: " + sqle.getMessage(), sqle);
      result = null;
    } catch (Exception e) {
      Logger.error(userid, this, "getActivities", "exception: " + e.getMessage(), e);
      result = null;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#getUserActivities(pt.iflow.api.utils.UserInfoInterface)
   */
  public ListIterator<Activity> getUserActivities(UserInfoInterface userInfo) {
    return this.getUserActivities(userInfo, -1);
  }
  
  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#getUserActivities(pt.iflow.api.utils.UserInfoInterface, int)
   */
  public ListIterator<Activity> getUserActivities(UserInfoInterface userInfo, int anFlowId) {
    return getUserActivities(userInfo, anFlowId, null);
  }
  
  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#getUserActivities(pt.iflow.api.utils.UserInfoInterface, int, pt.iflow.api.filters.FlowFilter)
   */
  public ListIterator<Activity> getUserActivities(UserInfoInterface userInfo, int anFlowId, FlowFilter filter) {
    String userid = userInfo.getUtilizador();
    Logger.trace(this, "getUserActivities", userid + " call.");
    
    // first, fix filter
    if (filter == null) {
      filter = new FlowFilter();
    }
    filter.setDateAfter(Utils.fixDateAfter(filter.getDateAfter()));
    filter.setDateBefore(Utils.fixDateBefore(filter.getDateBefore()));

    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    LinkedList<Activity> l = new LinkedList<Activity>();
    ListIterator<Activity> result = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      int nField = 1;
      // 1: userid
      final String userFilter = "userid=?";
      final StringBuilder sQuery = new StringBuilder(
          "select a.*,p.pnumber from activity a,process p where p.flowid=a.flowid and p.pid=a.pid and p.subpid=a.subpid and status=0 and ")
          .append(userFilter);
      final StringBuilder sQueryDelegated = new StringBuilder(
          "select a.*,p.pnumber from activity_delegated a,process p where p.flowid=a.flowid and p.pid=a.pid and p.subpid=a.subpid and status=0 and ")
          .append(userFilter);
      // 2: anFlowId
      if (anFlowId > -1) {
        sQuery.append(" and a.flowid=?");
        sQueryDelegated.append(" and a.flowid=?");
      }
      // 3: adtAfter
      if (filter.getDateAfter() != null) {
        sQuery.append(" and a.created >= ?");
        sQueryDelegated.append(" and a.created >= ?");
      }
      // 4: adtBefore
      if (filter.getDateBefore() != null) {
        sQuery.append(" and a.created < ?");
        sQueryDelegated.append(" and a.created < ?");
      }
      // 5: pnumber
      if (StringUtils.isNotEmpty(filter.getPnumber())) {
        sQuery.append(" and upper(p.pnumber) like upper('%").append(escapeSQL(filter.getPnumber())).append("%')");
        sQueryDelegated.append(" and upper(p.pnumber) like upper('%").append(escapeSQL(filter.getPnumber())).append("%')");
      }

      if(filter.getOrderType() != null && filter.getOrderType().equals("desc")){
          sQuery.append(" order by a.created desc");
          sQueryDelegated.append(" order by a.created desc"); 
      }else{
          sQuery.append(" order by a.created asc");
          sQueryDelegated.append(" order by a.created asc");
      }
      
      st = db.prepareStatement(sQuery.toString());
      st.setString(nField, userid);

      if (anFlowId > -1) {
        ++nField;
        st.setInt(nField, anFlowId);
      }
      if (filter.getDateAfter() != null) {
        ++nField;
        st.setTimestamp(nField, new java.sql.Timestamp(filter.getDateAfter().getTime()));
      }
      if (filter.getDateBefore() != null) {
        ++nField;
        st.setTimestamp(nField, new java.sql.Timestamp(filter.getDateBefore().getTime()));
      }

      rs = st.executeQuery();
      int counter = -1;
      while (rs.next()) {
        int flowid = rs.getInt("flowid");
        if(filter.ignoreFlow(flowid)) {
          continue;
        }
        if(filter.hasSizeLimit()) {
          counter++;
          if (counter - filter.getStartIndex() > filter.getNumElements()) {
            break;
          } else if (counter < filter.getStartIndex()) {
            continue;
          }
        }
        Activity wle = new Activity(userid, flowid, rs.getInt("pid"), rs.getInt("subpid"), rs.getInt("type"), 
            rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), rs.getTimestamp("archived"), 
            rs.getString("description"), rs.getString("url"), rs.getInt("status"), rs.getInt("notify"));
        wle.profilename = rs.getString("profilename");
        wle.pnumber = rs.getString("pnumber");
        if (rs.getInt("read_flag") == 1) {
          wle.setRead();
        }
        else {
          wle.setUnread();
        }
        wle.mid = rs.getInt("mid");
        l.add(wle);
      }
      DatabaseInterface.closeResources(st, rs);
      st = null;
      rs = null;
      
      if (!filter.hasSizeLimit() || l.size() <= filter.getNumElements()) {
        // after getting the 'normal' activities, we get the 'delegated ones'
        nField = 1;
        st = db.prepareStatement(sQueryDelegated.toString());
        st.setString(nField, userid);
        if (anFlowId > -1) {
          ++nField;
          st.setInt(nField, anFlowId);
        }
        if (filter.getDateAfter() != null) {
          ++nField;
          st.setTimestamp(nField, new Timestamp(filter.getDateAfter().getTime()));
        }
        if (filter.getDateBefore() != null) {
          ++nField;
          st.setTimestamp(nField, new Timestamp(filter.getDateBefore().getTime()));
        }
        rs = st.executeQuery();
        while (rs.next()) {
          int flowid = rs.getInt("flowid");
          if(filter.ignoreFlow(flowid)) {
            continue;
          }
          if(filter.hasSizeLimit()) {
            counter++;
            if (counter - filter.getStartIndex() > filter.getNumElements()) {
              break;
            } else if (counter < filter.getStartIndex()) {
              continue;
            }
          }
          Activity wle = new Activity(userid, flowid, rs.getInt("pid"), rs.getInt("subpid"), rs.getTimestamp("created"),
              rs.getString("description"), rs.getString("url"));
          wle.profilename = rs.getString("profilename");
          wle.pnumber = rs.getString("pnumber");
          if (rs.getInt("read_flag") == 1) {
            wle.setRead();
          }
          else {
            wle.setUnread();
          }
          wle.mid = rs.getInt("mid");
          l.add(wle);
        }
      }
      result = l.listIterator();
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getUserActivities", "sql exception: " + sqle.getMessage(), sqle);
      result = null;
    } catch (Exception e) {
      Logger.error(userid, this, "getUserActivities", "exception: " + e.getMessage(), e);
      result = null;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return result;
  }

  /**
   * retrieves the worklist history of a user
   * 
   * @param userid
   *          user id
   */
  public ListIterator<Activity> getUserActivityHistory(UserInfoInterface userInfo) {

    String userid = userInfo.getUtilizador();

    Logger.trace(this, "getUserActivityHistory", userid + " call.");

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    LinkedList<Activity> l = new LinkedList<Activity>();
    ListIterator<Activity> result = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();

      final String userFilter = "userid='" + escapeSQL(userid) + "'";

      StringBuilder query = new StringBuilder();
      query.append("select * from (select userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,profilename,read_flag,mid from activity where ");
      query.append(userFilter).append(" union select userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,profilename,read_flag,mid from activity_history where ");
      query.append(userFilter).append(" and archived is not null and undoflag=0) order by created desc");

      rs = st.executeQuery(query.toString());

      while (rs.next()) {
        Activity wle = new Activity(userid, rs.getInt("flowid"), rs.getInt("pid"), rs.getInt("subpid"), rs.getInt("type"), 
            rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), rs.getTimestamp("archived"), 
            rs.getString("description"), rs.getString("url"), 1, rs.getInt("notify"));
        wle.profilename = rs.getString("profilename");
        if (rs.getInt("read_flag") == 1) {
          wle.setRead();
        }
        else {
          wle.setUnread();
        }
        wle.mid = rs.getInt("mid");
        l.add(wle);
      }
      rs.close();
      rs = null;
      result = l.listIterator();
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getUserActivityHistory", "sql exception: " + sqle.getMessage(), sqle);
      result = null;
    } catch (Exception e) {
      Logger.error(userid, this, "getUserActivityHistory", "exception: " + e.getMessage(), e);
      result = null;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return result;
  }

  /**
   * Retrieves the url for this user's activity in requested process
   * 
   * @param asUrl
   *          the output value if no activities exist for this process
   * @return url or null if user does not exist in process activity list
   */
  public String getUserProcessUrl(UserInfoInterface userInfo, ProcessData procData, String asUrl) {

    String retObj = null;

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    Logger.trace(this, "getUserProcessUrl", "Call for user=" + login + ", flowid=" + flowid + ", pid=" + pid + ", subpid=" + subpid
        + " and url=" + asUrl);

    // check privileges
    Flow flow = BeanFactory.getFlowBean();
    String priv = new String(new char[] { FlowRolesTO.SUPERUSER_PRIV, FlowRolesTO.READ_PRIV, FlowRolesTO.WRITE_PRIV });
    if (!flow.checkUserFlowRoles(userInfo, flowid, priv)) {
      Logger.warning(login, this, "getUserProcessUrl",
          procData.getSignature() + "User has no access privileges... returning null");
      return null;
    }

    
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    boolean bHasActivities = false;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      final StringBuilder activityQuery = new StringBuilder("select userid,url from activity where flowid=");
      activityQuery.append(flowid).append(" and pid=").append(pid).append(" and subpid=").append(subpid);

      Logger.debug(login, this, "getUserProcessUrl", "Query1=" + activityQuery);

      rs = st.executeQuery(activityQuery.toString());

      while (rs.next()) {
        bHasActivities = true;
        String user = rs.getString("userid");
        if (StringUtils.equals(user, login)) {
          // user has activity.. break and return activity url
          retObj = rs.getString("url");
          Logger.debug(login, this, "getUserProcessUrl", procData.getSignature() + "Found url for user: " + retObj);
          break;
        }
      }
      rs.close();
      rs = null;

      if (retObj == null) {
        // get activities for this process and delegated user
        final StringBuilder activityDelegatedQuery = new StringBuilder("select userid,url from activity_delegated where flowid=");
        activityDelegatedQuery.append(flowid).append(" and pid=").append(pid).append(" and subpid=").append(subpid);

        Logger.debug(login, this, "getUserProcessUrl", "Query2=" + activityDelegatedQuery);

        rs = st.executeQuery(activityDelegatedQuery.toString());

        while (rs.next()) {
          bHasActivities = true;

          String user = rs.getString("userid");
          if (StringUtils.equals(user, login)) {
            // user has activity.. break and return activity url
            retObj = rs.getString("url");
            Logger.debug(login, this, "getUserProcessUrl", "Found url for user: " + retObj);
            break;
          }
        }
        rs.close();
        rs = null;

        if (!bHasActivities) {
          // no activities for process... return original url
          retObj = asUrl;
          Logger.info(login, this, "getUserProcessUrl", procData.getSignature() + "no activities found for process... returning original url");
        }
      }
    } catch (Exception e) {
      Logger.error(login, this, "getUserProcessUrl", "exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    Logger.trace(this, "getUserProcessUrl", "OUTPUT for user=" + login + ", flowid=" + flowid + ", pid=" + pid + ", subpid="
        + subpid + " and url=" + asUrl + " IS = " + retObj);

    return retObj;
  }

  /**
   * Retrieves the worklist history of a process
   * 
   * @param flowid
   *          flow id
   * @param pid
   *          process id
   */
  public List<Activity> getProcessActivityHistory(UserInfoInterface userInfo, ProcessData procData) {
    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();

    return this.getProcessActivityHistory(userInfo, flowid, pid, subpid);
  }

  /**
   * Retrieves the worklist history of a process
   * 
   * @param flowid
   *          flow id
   * @param pid
   *          process id
   */
  public List<Activity> getProcessActivityHistory(UserInfoInterface userInfo, int flowid, int pid, int subpid) {

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    List<Activity> l = new LinkedList<Activity>();
    List<Activity> result = null;

    final String userid = userInfo.getUtilizador();

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();

      // We hope that the compiler optimizes with StringBuilder
      final String procFilter = "where flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid;
      final String theQuery = "select * from (select userid,flowid,pid,subpid,type,priority,created,started,"
          + "archived,description,url,status,notify,delegated,profilename,read_flag,mid from activity " + procFilter
          + " union select userid,flowid,pid,subpid,type,priority,created,started,"
          + "archived,description,url,status,notify,delegated,profilename,read_flag,mid from "
          + "activity_history "+procFilter
          + " and archived is not null and undoflag=0) act_hist order by created desc";
      Logger.debug(userInfo.getUtilizador(), this, "getProcessActivityHistory", "Query: " + theQuery);

      rs = st.executeQuery(theQuery);

      while (rs.next()) {
        Activity wle = new Activity(rs.getString("userid"), flowid, pid, subpid, rs.getInt("type"), rs.getInt("priority"), 
            rs.getTimestamp("created"), rs.getTimestamp("started"), rs.getTimestamp("archived"), rs.getString("description"),
            rs.getString("url"), 1, rs.getInt("notify"));
        wle.profilename = rs.getString("profilename");
        if (rs.getInt("read_flag") == 1) {
          wle.setRead();
        }
        else {
          wle.setUnread();
        }
        wle.mid = rs.getInt("mid");
        l.add(wle);
      }
      rs.close();
      rs = null;
      result = Collections.unmodifiableList(l);
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessActivityHistory", "sql exception: " + sqle.getMessage(), sqle);
      result = null;
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessActivityHistory", "exception: " + e.getMessage(), e);
      result = null;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return result;
  }

  /**
   * @see ProcessManager#processExists(UserInfoInterface, int, int, int)
   */
  public boolean processExists(UserInfoInterface userInfo, ProcessHeader procInfo) {
    String userid = userInfo.getUtilizador();
    if (Logger.isDebugEnabled()) {
      Logger.debug(userid, this, "processExists", "entered");
    }
    
    boolean processExists = false;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      
      StringBuffer query = new StringBuffer();
      query.append("SELECT 1 FROM process_history");
      query.append(" WHERE flowid=").append(procInfo.getFlowId());
      query.append(" AND pid=").append(procInfo.getPid());
      query.append(" AND subpid=").append(procInfo.getSubPid());
      
      rs = st.executeQuery(query.toString());
      processExists = rs.next();
    } catch (Exception e) {
      Logger.error(userid, this, "processExists", procInfo.getSignature() + "caught exception", e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return processExists;
  }

  /**
   * @see ProcessManager#isProcessClosed(UserInfoInterface, int, int, int)
   */
  public boolean isProcessClosed(UserInfoInterface userInfo, ProcessHeader procInfo) {
    String userid = userInfo.getUtilizador();
    if (Logger.isDebugEnabled()) {
      Logger.debug(userid, this, "isProcessClosed", "entered");
    }

    boolean processIsOpen = false;

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);      
      st = db.createStatement();
      
      StringBuffer query = new StringBuffer();
      query.append("SELECT 1 FROM process_history");
      query.append(" WHERE flowid=").append(procInfo.getFlowId());
      query.append(" AND pid=").append(procInfo.getPid());
      query.append(" AND subpid=").append(procInfo.getSubPid());
      query.append(" AND closed=1");
      
      rs = st.executeQuery(query.toString());
      processIsOpen = rs.next(); 
    } catch (Exception e) {
      Logger.error(userid, this, "isProcessClosed", procInfo.getSignature() + "caught exception", e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return processIsOpen;
  }
  
  public boolean checkProcessInProfile(UserInfoInterface userInfo, String profileName) {
      String userid = userInfo.getUtilizador();
      Connection connection = null;
      PreparedStatement prepStatement = null;
      ResultSet resultSet = null;
      try {
        connection = DatabaseInterface.getConnection(userInfo);
        prepStatement =  connection.prepareStatement("select count(distinct(profilename))," +
                "pid,subpid from activity where pid in (select distinct(pid) from activity " +
                "where profilename=?) group by pid,subpid having count(distinct(profilename)) = 1");
        prepStatement.setString(1, profileName);
        Logger.debug(userid, this, "checkProcessInProfile", "Query=" + prepStatement);
        resultSet = prepStatement.executeQuery();
        if(resultSet.next()) {
            return true;
        }
        
    } catch (SQLException sqle) {
        Logger.error(userid, this, "checkProcessInProfile", "sql exception: " + sqle.getMessage(), sqle);
        return true;
    } finally {
        DatabaseInterface.closeResources(connection, prepStatement, resultSet);
    }
      return false;
  }

  
  public void deleteActivities(UserInfoInterface userInfo, String userid) throws SQLException {
    deleteActivities(userInfo, userid, null);
  }

  public void deleteActivitiesForProfile(UserInfoInterface userInfo, String userid, String profileId) throws SQLException {
    if (StringUtils.isEmpty(profileId)) {
      // throw exception?
      return;   
    }
    deleteActivities(userInfo, userid, profileId);
  }
  
  private void deleteActivities(UserInfoInterface userInfo, String userid, String profileId) throws SQLException {
      Connection connection = null;
      PreparedStatement statement = null;

      try {
          connection = DatabaseInterface.getConnection(userInfo);
          String stmt = "delete from activity where userid=?";
          if (profileId != null) {
            stmt += " and profilename=(select name from profiles where profileid=? and organizationid=?)";
          }
          statement = connection.prepareStatement(stmt);
          statement.setString(1, userid);
          if (profileId != null) {
            statement.setString(2, profileId);
            statement.setString(3, userInfo.getOrganization());
          }
          
          Logger.debug(userid, this, "deleteActivities", "QUERY=" + stmt + "; USERID=" + userid + "; PROFILE=" + profileId);
          statement.executeUpdate();
      
      } catch (SQLException sqle) {
          Logger.error(userid, this, "deleteActivities", "sql exception: " + sqle.getMessage(), sqle);
          try {
              DatabaseInterface.rollbackConnection(connection);
          }
          catch (Exception e2) {
          }
          // now throw caught exception so caller handle it properly.
          throw sqle;
      } catch (Exception e) {
          Logger.error(userid, this, "deleteActivities", "exception: " + e.getMessage(), e);
          try {
              DatabaseInterface.rollbackConnection(connection);
          }
          catch (Exception e2) {
          }
          // now throw caught exception so caller handle it properly.
          throw new SQLException(e.getMessage());
      } finally {
          DatabaseInterface.closeResources(connection, statement);
      }
  }

  /**
   * Delete all activities of the process
   * 
   * @param userid
   * @param pid
   * 
   * @throws SQLException
   */
  public void deleteAllActivities(UserInfoInterface userInfo, ProcessData procData) throws SQLException {
    deleteAllActivities(userInfo, procData.getProcessHeader());
  }

  /**
   * Delete all activities of the process
   * 
   * @param userid
   * @param pid
   * 
   * @throws SQLException
   */
  public void deleteAllActivities(UserInfoInterface userInfo, ProcessHeader procHeader) throws SQLException {

    int flowid = procHeader.getFlowId();
    int pid = procHeader.getPid();
    int subpid = procHeader.getSubPid();

    String userid = userInfo.getUtilizador();

    Logger.trace(this, "deleteAllActivities", "Call for user=" + userid + ", flowid=" + flowid + " and pid=" + pid + " and subpid="
        + subpid);

    if (pid == Const.nSESSION_PID) {
      Logger.debug(userid, this, "deleteAllActivities", "Process still in session... returning");
      return;
    }

    Connection db = null;
    Statement st = null;
    String stmp = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);
      st = db.createStatement();

      historifyActivities(userInfo, db, procHeader, userid);

      stmp = "delete from activity where flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid;
      Logger.debug(userid, this, "deleteAllActivities", "Query3=" + stmp);
      st.executeUpdate(stmp);

      DatabaseInterface.commitConnection(db);
    }
    catch (SQLException sqle) {
      Logger.error(userid, this, "deleteAllActivities", procHeader.getSignature() + "sql exception: " + sqle.getMessage(), sqle);
      try {
        DatabaseInterface.rollbackConnection(db);
      }
      catch (Exception e2) {
      }
      // now throw caught exception so caller handle it properly.
      throw sqle;
    } catch (Exception e) {
      Logger.error(userid, this, "deleteAllActivities", procHeader.getSignature() + "exception: " + e.getMessage(), e);
      try {
        DatabaseInterface.rollbackConnection(db);
      }
      catch (Exception e2) {
      }
      // now throw caught exception so caller handle it properly.
      throw new SQLException(e.getMessage());
    }
    finally {
      DatabaseInterface.closeResources(db, st);
    }
  }

  /**
   * Updates a process activities with the new specified url and description
   * 
   * @param userInfo
   *          the calling user info
   * @param activity
   *          the activity data to update
   */
  public void updateActivity(UserInfoInterface userInfo, Activity activity) {
    this.updateActivity(userInfo, activity, null);
  }

  /**
   * Updates a process activities with the new specified url and description If
   * aalGroupProcs is not null, then activities are "grouped": only one activity
   * for pair description-userid. This means that if 2 procs in same state have
   * the same description, only one will have an activity associated. CAUTION:
   * block must support this "mode"
   * 
   */
  public void updateActivity(UserInfoInterface userInfo, Activity activity, List<String> aalGroupProcs) {

    String userid = userInfo.getUtilizador();

    Logger.trace(this, "updateActivity", "Call for user=" + userid + ", flowid=" + activity.flowid + ", pid=" + activity.pid
        + ", subpid=" + activity.subpid + ", description=" + activity.description + ", url=" + activity.url + ", read=" + activity.isRead() 
        + " and GROUPPROCS=" + aalGroupProcs);

    if (activity.pid == Const.nSESSION_PID) {
      Logger.debug(userid, this, "updateActivity", "Process still in session... returning");
      return;
    }

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    Statement st2 = null;
    ResultSet rs2 = null;
    List<String> alNotify = new ArrayList<String>();

    int nUpdatedRows = 0;
    boolean bHasActivities = false;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);
      st = db.createStatement();
      st2 = db.createStatement();

      // first check if there are activities for this process
      final StringBuilder sbActivitiesQuery = new StringBuilder("select userid,notify,delegated from activity where flowid=");
      sbActivitiesQuery.append(activity.flowid).append(" and pid=").append(activity.pid);
      sbActivitiesQuery.append(" and subpid=").append(activity.subpid);
      Logger.debug(userid, this, "updateActivity", "Query1=" + sbActivitiesQuery.toString());

      rs = st.executeQuery(sbActivitiesQuery.toString());
      while (rs.next()) {
        bHasActivities = true;
        if (rs.getInt("notify") == 1) {
          String user = rs.getString("userid");

          if (rs.getInt("delegated") == 1) {
            /* if it is a delegated activity */
            final StringBuilder sbDelegatedActivity = new StringBuilder("select userid from activity_delegated where flowid=");
            sbDelegatedActivity.append(activity.flowid).append(" and pid=").append(activity.pid).append(" and subpid=");
            sbDelegatedActivity.append(activity.subpid);//.append(" and slave=1");
            sbDelegatedActivity.append(" and ownerid='").append(escapeSQL(user)).append("'");
            rs2 = st2.executeQuery(sbDelegatedActivity.toString());
            if (rs2.next()) {
              user = rs2.getString("userid");
            }
            rs2.close();
          }

          if (!StringUtils.equals(user, userid)) {
            alNotify.add(user);
          }
        }
      }
      rs.close();
      rs = null;

      st2.close();
      st2 = null;

      if (bHasActivities) {
        // update activity

        boolean bUpdate = true;

        if (aalGroupProcs != null) {
          // TODO since the introduction of subpids, this part of allGroupProcs
          // has not been changed
          // throw exception to avoid unexpected behaviour!
          throw new UnsupportedOperationException();
          
//          // check if other procs exist in same state and same user
//          final StringBuilder sbActivityCountQuery = new StringBuilder("select count(1) from activity where flowid=");
//          sbActivityCountQuery.append(activity.flowid);
//          if (aalGroupProcs.size() > 0) {
//            sbActivityCountQuery.append(" and pid in (");
//            for (int ii = 0; ii < aalGroupProcs.size(); ii++) {
//              if (ii > 0) {
//                sbActivityCountQuery.append(",");
//              }
//              sbActivityCountQuery.append((String) aalGroupProcs.get(ii));
//            }
//            sbActivityCountQuery.append(")");
//          }
//          sbActivityCountQuery.append(" and pid<>").append(activity.pid);
//          sbActivityCountQuery.append(" and description='").append(escapeSQL(activity.description)).append("'");
//
//          Logger.debug(userid, this, "updateActivity", "Query(GROUP)=" + sbActivityCountQuery);
//
//          ntmp = -1;
//          rs = st.executeQuery(sbActivityCountQuery.toString());
//          if (rs.next()) {
//            ntmp = rs.getInt(1);
//          }
//          rs.close();
//          rs = null;
//
//          Logger.debug(userid, this, "updateActivity", "Query(GROUP) COUNT=" + ntmp);
//
//          if (ntmp > 0) {
//            // found at least one activity for group with same
//            // description.. abort this activity
//            bUpdate = false;
//          }
        }

        if (bUpdate) {
          // update proc activities
          final StringBuilder sbUpdateActivity = new StringBuilder("update activity set ");
          if (activity.notify) {
            sbUpdateActivity.append("notify=0,");
          }
          if (activity.mid > 0) {
            sbUpdateActivity.append("mid=?,");
          }
          sbUpdateActivity.append("description=?,url=? where flowid=? and pid=? and subpid=? ");

          Logger.debug(userid, this, "updateActivity", "Update: Query2(UPD)=" + sbUpdateActivity);
          st = db.prepareStatement(sbUpdateActivity.toString());
          int counterAux=1;
          if (activity.mid > 0) 
            ((PreparedStatement)st).setInt(counterAux++, activity.mid);
          ((PreparedStatement)st).setString(counterAux++, activity.description);
          ((PreparedStatement)st).setString(counterAux++, activity.url);
          ((PreparedStatement)st).setInt(counterAux++, activity.flowid);
          ((PreparedStatement)st).setInt(counterAux++, activity.pid);
          ((PreparedStatement)st).setInt(counterAux++, activity.subpid);
          nUpdatedRows = ((PreparedStatement)st).executeUpdate();
          Logger.debug(userid, this, "updateActivity", nUpdatedRows + " activities updated.");
          st.close();

          if (activity.isRead()) {
            // update user activity to read
            final StringBuilder sbUpdateActivityRead = new StringBuilder("update activity set ");
            sbUpdateActivityRead.append("read_flag=1");
            sbUpdateActivityRead.append(" where flowid=").append(activity.flowid);
            sbUpdateActivityRead.append(" and pid=").append(activity.pid);
            sbUpdateActivityRead.append(" and subpid=").append(activity.subpid);
            sbUpdateActivityRead.append(" and read_flag=0");
            sbUpdateActivityRead.append(" and userid='").append(escapeSQL(userid)).append("'");

            Logger.debug(userid, this, "updateActivity", "Update: Query Read=" + sbUpdateActivityRead);
            st = db.prepareStatement(sbUpdateActivityRead.toString());
            nUpdatedRows = ((PreparedStatement)st).executeUpdate();
            Logger.debug(userid, this, "updateActivity", nUpdatedRows + " activities updated as read.");
          }

        
        } else {
          // delete proc activities
          final StringBuilder sbDeleteActivity = new StringBuilder("delete from activity ");
          sbDeleteActivity.append(" where flowid=").append(activity.flowid);
          sbDeleteActivity.append(" and pid=").append(activity.pid);
          sbDeleteActivity.append(" and subpid=").append(activity.subpid);

          Logger.debug(userid, this, "updateActivity", "Delete: Query2(DEL)=" + sbDeleteActivity);

          nUpdatedRows = st.executeUpdate(sbDeleteActivity.toString());
          Logger.info(userid, this, "updateActivity", 
              "deleted " + nUpdatedRows + " activities for flow " + 
              activity.flowid + " and pid " + activity.pid);
        }

        DatabaseInterface.commitConnection(db);
        // free db connections
        DatabaseInterface.closeResources(db, st, rs, st2, rs2);
        db = null;
        st = st2 = null; 
        rs = rs2 = null;

        if (bUpdate && activity.notify && alNotify != null && alNotify.size() > 0) {
          HashMap<String,String> cache = new HashMap<String, String>();
          for (String user : alNotify) {
            Activity a = new Activity(user, activity.flowid, activity.pid, activity.subpid, activity.type, activity.priority,
                activity.description, activity.url, 1);
            String pnum = getActivityPNumber(userInfo, cache, a);
            this.notifyUserActivity(userInfo, a, pnum);
          }
        }
      } else {
        // do nothing... no activities...
        Logger.warning(userid, this, "updateActivity", 
            "Trying to update activities for proc " + activity.pid + " in flow "
            + activity.flowid + " with mid=" + activity.mid + ", URL="
            + activity.url + ", DESC=" + activity.description + " but NO ACTIVITIES scheduled.");
      }

    } catch (Exception e) {
      Logger.error(userid, this, "updateActivity", "exception: " + e.getMessage(), e);
      try {
        if (db != null)
          DatabaseInterface.rollbackConnection(db);
      }
      catch (Exception ei) {}
      }
    finally {
      DatabaseInterface.closeResources(db, st, rs, st2, rs2);
    }
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#forwardToProfile(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData, java.lang.String, java.lang.String)
   */
  public boolean forwardToProfile(UserInfoInterface userInfo, ProcessData procData, String asProfile, String asDescription) {
    return this.forwardTo(userInfo, procData, asProfile, null, asDescription);
  }

  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#forwardToUser(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData, java.lang.String, java.lang.String)
   */
  public boolean forwardToUser(UserInfoInterface userInfo, ProcessData procData, String asNewUser, String asDescription) {
    return this.forwardTo(userInfo, procData, null, asNewUser, asDescription);
  }

  /**
   * Moves all activities of the process to the specified user or set of users
   * with the specified profile
   * 
   * @return true if succeeds; false otherwise
   */
  private boolean forwardTo(UserInfoInterface userInfo, ProcessData procData, String asProfile, String asNewUser, String asDescription) {

    boolean retObj = false;

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String userid = userInfo.getUtilizador();
    String organization = userInfo.getOrganization();
    IMessages msg = userInfo.getMessages();

    asDescription = (StringUtils.isNotBlank(asDescription) ? asDescription : msg.getString("ProcessManagerBean.forwardTo").replace("{0}", userid));

    if (StringUtils.isEmpty(asProfile) && StringUtils.isEmpty(asNewUser)) {
      Logger.warning(userid, this, "forwardTo", "no profile nor user??.. aborting");
      return false;
    }

    Logger.trace(this, "forwardTo", "Call for user=" + userid + " and pid=" + pid + " and subpid=" + subpid + ": new profile="
        + asProfile + ", asNewUser=" + asNewUser);

    Connection db = null;
    Statement st = null;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    ResultSet rs = null;

    AuthProfile ap = BeanFactory.getAuthProfileBean();
    Activity activity = null;
    Set<String> hsProfiles = null;
    // until proof otherwise, is not a delegated activity
    try {

      // prepare db connection
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);
      st = db.createStatement();

      String sProfileName = asProfile;
      if (StringUtils.isEmpty(sProfileName))
        sProfileName = asNewUser;

      List<String> alUsers = new ArrayList<String>();
      List<Set<String>> alProfiles = new ArrayList<Set<String>>();
      // now create activities for users in new profile or specified
      // user(s)
      if (StringUtils.isNotEmpty(asNewUser)) {

        List<String> lUsers = Utils.tokenize(asNewUser, ",");
        if (lUsers != null) {
          for (int us = 0; us < lUsers.size(); us++) {
            String username = lUsers.get(us);
            if (username == null)
              continue;
            username = username.trim();
            if (username.equals(""))
              continue;

            // check if user really exists...
            UserData userData = ap.getUserInfo(username);
            if (null == userData)
              continue; // no user...
            username = userData.getUsername();
            if (StringUtils.isEmpty(username))
              continue;

            alUsers.add(username);
            hsProfiles = new HashSet<String>();
            Iterator<String> userProfilesIterator = (ap.getUserProfiles(username, organization)).iterator();
            while (userProfilesIterator != null && userProfilesIterator.hasNext()) {
              hsProfiles.add(userProfilesIterator.next());
            }
            alProfiles.add(hsProfiles);
          }
        } else {
          // treat at least user
          UserData userData = ap.getUserInfo(asNewUser);
          if (null != userData) {
            asNewUser = userData.getUsername();
            if (StringUtils.isNotEmpty(asNewUser)) {
              alUsers.add(asNewUser);
              Iterator<String> userProfilesIterator = (ap.getUserProfiles(asNewUser, organization)).iterator();
              hsProfiles = new HashSet<String>();
              while (userProfilesIterator != null && userProfilesIterator.hasNext()) {
                hsProfiles.add(userProfilesIterator.next());
              }
              alProfiles.add(hsProfiles);
            }
          }
        }

      } else {

        // Get list of users in the profile
        hsProfiles = new HashSet<String>();
        String[] asProfileList = asProfile.split(",");
        for(String asProfileAux: asProfileList){
          hsProfiles.add(asProfileAux);
          Iterator<String> userIdIterator = (ap.getUsersInProfile(userInfo, asProfileAux)).iterator();
          if (userIdIterator != null && userIdIterator.hasNext()) {
            // Create the activity for each user in the profile
            while (userIdIterator.hasNext()) {
            String nextUser = userIdIterator.next();
            if(!alUsers.contains(nextUser)){
              alUsers.add(nextUser);
              alProfiles.add(hsProfiles);
            }
            }
          }
        }
      }

      if (alUsers.size() > 0) {
        java.sql.Timestamp now = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
        
        // first og all, historify activities and delete previous ones
        historifyActivities(userInfo, db, procData.getProcessHeader(), userid);

        StringBuilder sbDeleteActivities = new StringBuilder("delete from activity where flowid=");
        sbDeleteActivities.append(flowid).append(" and pid=").append(pid);
        sbDeleteActivities.append(" and subpid=").append(subpid);
        Logger.debug(userid, this, "forwardTo (profile=" + asProfile + ",newuser=" + asNewUser + ")", "Query3="
            + sbDeleteActivities.toString());
        st.executeUpdate(sbDeleteActivities.toString());

        
        int mid = procData.getMid();
        if (mid < 0) {
          mid = getNextMid(userInfo, procData);
          procData.setMid(mid);
          Logger.debug(userid, this, "forwardTo (profile=" + asProfile + ",newuser=" + asNewUser + ")", 
              "using new mid " + mid);          
        }
        
        String sInsertActivity = DBQueryManager.getQuery("ProcessManager.insert_forward_activity");
        pst = db.prepareStatement(sInsertActivity);
        
        Logger.debug(userid, this, "forwardTo (profile=" + asProfile + ",newuser=" + asNewUser + ")", "Query insert activity="
            + sInsertActivity);

        pst2 = db.prepareStatement("select * from activity_hierarchy where flowid=? and ownerid=? and pending=0 and expires > ? and slave=1");
        
        List<Activity> usersToNotify = new ArrayList<Activity>();
        
        for (int i = 0; i < alUsers.size(); i++) {
          int read = StringUtils.equals(userid, alUsers.get(i)) ? 1 : 0;

          hsProfiles = null;
          
          // if delegated user is owner of a delegation
          // mark activity as delegated = 1
          pst2.setInt(1, flowid);
          pst2.setString(2, alUsers.get(i));
          pst2.setTimestamp(3, now);
          rs = pst2.executeQuery();

          int delegated = 0;          
          String delegatedUser = null;
          if (rs.next()) {
            delegated = 1;
            delegatedUser = rs.getString("userid"); 
          }
          rs.close();
          rs = null;

          activity = new Activity((String) alUsers.get(i), flowid, pid, subpid, 0, 0, asDescription, 
            "Forward/forward.jsp?flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid);
          activity.profilename = sProfileName;
          activity.mid = mid;

          pst.setString(1, activity.userid);
          pst.setInt(2, activity.flowid);
          pst.setInt(3, activity.pid);
          pst.setInt(4, activity.subpid);
          pst.setTimestamp(5, now);
          pst.setTimestamp(6, now);
          pst.setString(7, activity.description);
          pst.setString(8, activity.url);

          boolean notify = false;
          if (delegatedUser == null) {
            hsProfiles = alProfiles.get(i);
            notify = this.prepareNotifyUserActivity(userInfo, activity, hsProfiles);
            if (notify)
              usersToNotify.add(activity);
          }
          else {
            // activity is delegated
            Activity delegatedActivity = new Activity(activity);
            delegatedActivity.userid = delegatedUser;
            notify = this.prepareNotifyUserActivity(userInfo, delegatedActivity);
            if (notify)
              usersToNotify.add(delegatedActivity);
          }
//          if (notify) {
//            pst.setInt(9, 1);
//          } else {
//            pst.setInt(9, 0);
//          }
          pst.setInt(9, 0);
          pst.setInt(10, delegated);
          pst.setString(11, sProfileName);
          pst.setInt(12, read);
          pst.setInt(13, activity.mid);
          
          Logger.debug(userid, this, "forwardTo (profile=" + asProfile + ",newuser=" + asNewUser + ")", 
              "Going for insert Query[" + i + "] for user " + activity.userid);
          pst.executeUpdate();
        }
        pst2.close();
        pst2 = null;
        pst.close();
        pst = null;
        
        DatabaseInterface.commitConnection(db);
        
        notifyUserActivities(userInfo, usersToNotify);
        
        retObj = true;
      } else {
        Logger.warning(userid, this, "forwardTo (profile=" + asProfile + ",newuser=" + asNewUser + ")",
            procData.getSignature() + "No users to create activities. aborting!");
      }
    } catch (Exception e) {
      Logger.error(userid, this, "forwardTo (profile=" + asProfile + ",newuser=" + asNewUser + ")", 
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
    } finally {
      if (!retObj && db != null) {
        try {
          DatabaseInterface.rollbackConnection(db);
        }
        catch (Exception e) {
        }
      }
      DatabaseInterface.closeResources(db, st, rs, pst, pst2);
    }

    return retObj;
  }

  private boolean checkPrepareNotifyUserActivity(UserInfoInterface userInfo, Activity activity) {
    if (!Const.bUSE_EMAIL)
      return false;
    
    if (activity == null) {
      return false;
    }

    if (!activity.notify) {
      return false;
    }

    if (userInfo.getUtilizador().equals(activity.userid)) {
      // activity belongs to calling user... don't notify calling user.
      return false;
    }

    // now checks for circular dependences
    if (DelegationManager.get().hasDelegationBetween(userInfo, activity.userid, activity.flowid)) {
      return false;
    }

    return true;
  }

  private boolean prepareNotifyUserActivity(UserInfoInterface userInfo, Activity activity) {
    Set<String> hsProfiles = new HashSet<String>();

    if (!checkPrepareNotifyUserActivity(userInfo, activity)) {
      return false;
    }

    // get user profiles
    AuthProfile ap = BeanFactory.getAuthProfileBean();
// FIXME REMOVE    Iterator<?> it = ap.getUserProfiles(Const.SYSTEM_ORGANIZATION, activity.userid).iterator();
    Iterator<?> it = ap.getUserProfiles(activity.userid, userInfo.getOrganization()).iterator();
    while (it != null && it.hasNext()) {
      hsProfiles.add((String) it.next());
    }
    return prepareNotifyUserActivity(userInfo, activity, hsProfiles, true);
  }

  private boolean prepareNotifyUserActivity(UserInfoInterface userInfo, Activity activity, Set<String> ahsProfiles) {

    if (!checkPrepareNotifyUserActivity(userInfo, activity)) {
      return false;
    }

    return prepareNotifyUserActivity(userInfo, activity, ahsProfiles, true);
  }

  private boolean prepareNotifyUserActivity(UserInfoInterface userInfo, Activity activity, Set<String> ahsProfiles, boolean dummy) {
    String sNotifyUser = null;
    String sForceNotify = null;
    String sDenyNotify = null;
    boolean bCheckNotifyProf = true;

    if (ahsProfiles.size() == 0) {
      // no profiles to check for force or deny... don't bother to look
      bCheckNotifyProf = false;
    }

    FlowSetting[] fsa = BeanFactory.getFlowSettingsBean().getFlowSettings(userInfo, activity.flowid);
    for (int ii = 0; ii < fsa.length; ii++) {
      if (fsa[ii].getName().equals(Const.sNOTIFY_USER)) {
        sNotifyUser = fsa[ii].getValue();
        if (sNotifyUser == null)
          sNotifyUser = "";
        if (!bCheckNotifyProf || (sForceNotify != null && sDenyNotify != null)) {
          break;
        }
      }
      if (bCheckNotifyProf) {
        if (fsa[ii].getName().equals(Const.sFORCE_NOTIFY_FOR_PROFILES)) {
          sForceNotify = fsa[ii].getValue();
          if (sForceNotify == null)
            sForceNotify = "";
          if (sNotifyUser != null && sDenyNotify != null) {
            break;
          }
        }
        if (fsa[ii].getName().equals(Const.sDENY_NOTIFY_FOR_PROFILES)) {
          sDenyNotify = fsa[ii].getValue();
          if (sDenyNotify == null)
            sDenyNotify = "";
          if (sForceNotify != null && sNotifyUser != null) {
            break;
          }
        }
      }
    } // for

    if (sNotifyUser == null || sNotifyUser.equals(Const.sNOTIFY_USER_NO)) {
      // check force notify
      if (bCheckNotifyProf && sForceNotify != null && !sForceNotify.equals("")) {
        List<String> toNotify = Utils.tokenize(sForceNotify, Const.sNOTIFY_FOR_PROFILES_SEPARATOR);
        for (int ii = 0; ii < toNotify.size(); ii++) {
          if (ahsProfiles.contains(toNotify.get(ii))) {
            // Logger.debug(userInfo.getUtilizador(),this,"prepareNotifyUserActivity",
            // "Forcing notify for user " + activity.userid
            // + " for profile " + (String)altmp.get(ii));
            return true;
          }
        }
      }
      return false;
    } else {
      // check deny notify
      if (bCheckNotifyProf && sDenyNotify != null && !sDenyNotify.equals("")) {
        List<String> noNotify = Utils.tokenize(sDenyNotify, Const.sNOTIFY_FOR_PROFILES_SEPARATOR);
        for (int ii = 0; ii < noNotify.size(); ii++) {
          if (ahsProfiles.contains(noNotify.get(ii))) {
            // Logger.debug(userInfo.getUtilizador(),this,"prepareNotifyUserActivity",
            // "Deny notify for user " + activity.userid
            // + " for profile " + (String)altmp.get(ii));
            return false;
          }
        }
      }
      return true;
    }
  }

  public boolean undoProcessActivities(UserInfoInterface userInfo, ProcessData procData,
      int targetMid, int flowState) throws Exception {

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String userid = userInfo.getUtilizador();

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    boolean retObj = false;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      rs = st.executeQuery("select muser from modification where flowid=" + flowid 
          + " and pid=" + pid + " and subpid=" + subpid
          + " and mid=" + targetMid);

      if (!rs.next()) {
        rs.close();
        rs = null;
        throw new Exception("Could not find reverted state");
      }
      String modUser = rs.getString("muser");
      rs.close();
      rs = null;

      rs = st.executeQuery("select min(mid) from activity_history where flowid=" + flowid 
          + " and pid=" + pid + " and subpid=" + subpid
          + " and mid>=" + targetMid + " and undoflag=0");
      int actMid = -1;
      if (rs.next()) {
        actMid = rs.getInt(1);
      }
      rs.close();
      rs = null;

      String defUrl = Block.getDefaultUrl(userInfo, procData);
      String desc = userInfo.getMessages().getString("ProcessManagerBean.undo.description");

      
      if (actMid > 0) {
        // use entries from historic activities
        
        this.deleteAllActivities(userInfo, procData);
                
        rs = st.executeQuery("select * from activity_history where flowid=" + flowid 
            + " and pid=" + pid + " and subpid=" + subpid
            + " and mid=" + actMid + " and undoflag=0");
        
        while(rs.next()) {
          Activity a = new Activity(rs.getString("userid"), flowid, pid, subpid, rs.getInt("type"), 
              rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), null, 
              desc,defUrl, rs.getInt("status"), rs.getInt("notify"));
          a.profilename = rs.getString("profilename");
          a.setUnread();
          a.mid = rs.getInt("mid");
          
          createActivity(userInfo, a);
        }
        rs.close();
        rs = null;
        
        st.executeUpdate("update activity_history set undoflag=1 where flowid=" + flowid 
            + " and pid=" + pid + " and subpid=" + subpid
            + " and mid>=" + actMid);
      }
      else {
        // use entries from current activities
        StringBuilder sb = new StringBuilder("update activity set ");
        sb.append("mid=").append(targetMid);
        sb.append(",description='").append(escapeSQL(desc));
        sb.append("',url='").append(escapeSQL(defUrl));
        sb.append("' where flowid=").append(flowid);
        sb.append(" and pid=").append(pid);
        sb.append(" and subpid=").append(subpid);
        st.executeUpdate(sb.toString());       
      }
      
      // check at least 1 activity
      rs = st.executeQuery("select * from activity where flowid=" + flowid 
          + " and pid=" + pid + " and subpid=" + subpid);
      if (!rs.next()) {
        Activity a = new Activity(modUser, flowid, pid, subpid, 0, 0, desc, defUrl);
        a.setUnread();
        a.mid = targetMid;
        a.profilename = modUser;
        
        createActivity(userInfo, a);
      }
      rs.close();
      rs = null;
      
      retObj = true;
    }
    catch (Exception e) {
      Logger.error(userid, this, "undoProcessActivities", procData.getSignature() + "exception: "
          + e.getMessage(), e);      
      throw e;
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  public void notifyProcessUndone(UserInfoInterface userInfo, ProcessData procData) {

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getPid();

    FlowHolder holder = BeanFactory.getFlowHolderBean();
    IFlowData fd = holder.getFlow(userInfo, flowid);
    if (fd == null || fd.hasError()) {
      Logger.error(userInfo.getUtilizador(), this, "notifyProcessUndone", "not valid flow with id " + flowid);
      return;
    }
    String sFlowName = fd.getName();

    if (Const.bUSE_EMAIL) {
      try {
        ListIterator<Activity> litmp = getProcessActivities(userInfo, flowid, pid, subpid);

        Activity activity = (Activity) litmp.next();
        String[] users = this.getActivityOwners(userInfo, activity);

        Hashtable<String, String> htProps = new Hashtable<String, String>();
        htProps.put("flowname", sFlowName);
        htProps.put("pid", String.valueOf(pid));
        htProps.put("subpid", String.valueOf(subpid));
        htProps.put("description", activity.description);
        htProps.put("app_host", Const.APP_HOST);
        htProps.put("app_port", String.valueOf(Const.APP_PORT));

        String sTemplateDir = null;
        FlowSetting[] fsa = BeanFactory.getFlowSettingsBean().getFlowSettings(userInfo, fd.getId());
        for (int i = 0; fsa != null && i < fsa.length; i++) {
          if (fsa[i].getName().equals(Const.sEMAIL_TEMPLATE_DIR)) {
            sTemplateDir = fsa[i].getValue();
            break;
          }
        }

        EmailTemplate etemplate = EmailManager.getEmailTemplate(userInfo, sTemplateDir, "notify_undone");

        AuthProfile ap = BeanFactory.getAuthProfileBean();

        for (int i = 0; users != null && i < users.length; i++) {
          UserData ud = ap.getUserInfo(users[i]);
          String sTo = ud.getEmail();

          sTo = sTo.trim();

          // defaults...
          Email email = EmailManager.buildEmail(htProps, etemplate);
          email.setTo(sTo);

          email.setCallingProcess(procData.getProcessHeader());
          
          email.sendMsg();
        }
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "notifyProcessUndone", 
            procData.getSignature() + "exception: " + e.getMessage(), e);
      }
    }
  }

  private void notifyUserActivities(UserInfoInterface userInfo, List<Activity> activities) {
    if (activities != null) {
      HashMap<String,String> cache = new HashMap<String, String>();
      for (Activity a : activities) {
        String pnum = getActivityPNumber(userInfo, cache, a);
        notifyUserActivity(userInfo, a, pnum);
      }
    }
  }
  
  private void notifyUserActivity(UserInfoInterface userInfo, Activity activity, String pnumber) {
    FlowHolder holder = BeanFactory.getFlowHolderBean();
    IFlowData fd = holder.getFlow(userInfo, activity.flowid);
    if (fd == null || fd.hasError()) {
      Logger.error(userInfo.getUtilizador(), this, "notifyUserActivity", "not valid flow with id " + activity.flowid);
      return;
    }

    String sFlowName = fd.getName();

    String sBaseUrl = Const.APP_PROTOCOL + "://" + Const.APP_HOST + ":" + Const.APP_PORT + Const.APP_URL_PREFIX;
    String sRelUrl = "GoTo?goto=actividade_user.jsp&flowid=" + activity.flowid + "&pid=" + activity.pid;

    String sFullUrl = sBaseUrl + sRelUrl;

    String to = activity.userid;

    if (Const.bUSE_EMAIL) {
      try {
        // get user email address from auth profile bean
        AuthProfile ap = BeanFactory.getAuthProfileBean();

        UserData ud = ap.getUserInfo(to);

        to = ud.getEmail();

        to = to != null ? to.trim() : null;

        if (StringUtils.isEmpty(to)) {
          Logger.warning(userInfo.getUtilizador(), this, "notifyUserActivity", "empty email for user " + activity.userid + ". skipping mail send");
          return;
        }
        
        Hashtable<String, String> htProps = new Hashtable<String, String>();
        htProps.put("flowname", sFlowName);
        htProps.put("base_url", sBaseUrl);
        htProps.put("rel_url", sRelUrl);
        htProps.put("url", sFullUrl);
        htProps.put("pnumber", pnumber);
        htProps.put("description", activity.description);
        htProps.put("app_host", Const.APP_HOST);
        htProps.put("app_port", String.valueOf(Const.APP_PORT));

        String sTemplateDir = null;
        FlowSetting[] fsa = BeanFactory.getFlowSettingsBean().getFlowSettings(userInfo, fd.getId());
        for (int i = 0; fsa != null && i < fsa.length; i++) {
          if (fsa[i].getName().equals(Const.sEMAIL_TEMPLATE_DIR)) {
            sTemplateDir = fsa[i].getValue();
            break;
          }
        }

        // build a default config email
        EmailTemplate etemplate = EmailManager.getEmailTemplate(userInfo, sTemplateDir, "new_activity");
        Email email = EmailManager.buildEmail(htProps, etemplate);

        if (Const.nMODE != Const.nPRODUCTION) {
          String sNewSubject = email.getSubject();
          sNewSubject = "(de user: " + userInfo.getUtilizador() + ") " + sNewSubject + " (para user " + to + ")";
          email.setSubject(sNewSubject);
        }

        email.setTo(to);
        
        email.setCallingProcess(new ProcessHeader(activity.flowid, activity.pid, activity.subpid));

        if (email.sendMsg()) {
          Logger.info(userInfo.getUtilizador(), this, "notifyUserActivity", "mail sent to " + to);
        } else {
          throw new Exception("Email not sent to " + to);
        }
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "notifyUserActivity", "exception: " + e.getMessage(), e);
      }
    }
  }

  private String getActivityPNumber(UserInfoInterface userInfo, HashMap<String, String> cache, Activity activity) {
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    ProcessHeader ph = new ProcessHeader(activity.flowid, activity.pid, activity.subpid);
    String sig = ph.getSignature();
    
    if (cache.containsKey(sig)) {
      // return cached
      return cache.get(sig);
    }
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      rs = st.executeQuery("select pnumber from process where flowid=" + 
          activity.flowid + " and pid=" + 
          activity.pid + " and subpid=" + activity.subpid);
      if (rs.next()) {
        String pnum = rs.getString("pnumber");
        cache.put(sig, pnum);
        Logger.debug(userInfo.getUtilizador(), this, "getActivityPNumber", sig + " found pnumber: " + pnum);      
        return pnum;
      }
    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getActivityPNumber", sig + "exception: " + e.getMessage(), e);      
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return null; 
  }
  
  /**
   * Get the modification id for a given process
   * 
   * @return the mid.
   */
  public int getModificationId(UserInfoInterface userInfo, ProcessHeader procHeader) {

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    int mid = 0;

    int flowid = procHeader.getFlowId();
    int pid = procHeader.getPid();
    int subpid = procHeader.getSubPid();
    String userid = userInfo.getUtilizador();

    if (pid == Const.nSESSION_PID)
      return 0;
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      // get the mid
      rs = st.executeQuery("select mid from process where flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid);
      if (rs.next()) {
        mid = rs.getInt("mid");
      } else {
        mid = -1;
      }
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getModificationId", procHeader.getSignature() + "sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getModificationId", procHeader.getSignature() + "exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return mid;
  }

  /**
   * @see {@link pt.iflow.api.core.ProcessManager#getProcessActivityStatistics(UserInfoInterface, int, Date[])}
   */
  public AuditData[] getProcessActivityStatistics(UserInfoInterface userInfo, int flowid, Date[] interval) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "entered (flowid=" + flowid + ", interval["
          + interval[0] + "," + interval[1] + "])");
    }

    Date startDate = interval[0];
    Date endDate = interval[1];

    if (startDate.after(endDate)) {
      Logger.error(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "Start date must come before end date (start="
          + startDate + ", end=" + endDate + ")");
      return new AuditData[] {};
    }
   
    int open = 0;
    int closed = 0;
    int treated = 0;
    int leftin = 0;

    closed  = getClosedProcess(userInfo, flowid, interval);
    open    = getOpenProcess(userInfo, flowid, interval);
    treated = getTreatedProcess(userInfo, flowid, interval);
    leftin  = getLeftInProcess(userInfo, flowid, interval);
    
      AuditData[] response = new AuditData[] {};     
      response = new AuditData[4];
      response[0] = new AuditData("proc_stats.process.closed", "" + closed);
      response[1] = new AuditData("proc_stats.process.open", "" + open);
      response[2] = new AuditData("proc_stats.process.treated", "" + treated);
      response[3] = new AuditData("proc_stats.process.unchanged","" + leftin);

    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "leaving (flowid=" + flowid + ", closed="
          + closed + ", open=" + open + ", treated=" + treated + ", left overs=" + leftin + ")");
    }
    return response;
  }

  /**
   * Count the closed processes in the interval [StartDate , EndDate]
   */
  public int getClosedProcess(UserInfoInterface userInfo,int flowid, Date[] interval) {
    
    Date startDate = interval[0];
    Date endDate = interval[1];
    
    int total = 0;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);

      String query = "select count(pid)from process_history"
                    +" where undoflag=0 and closed=1 and created < ? " 
                    +" and lastupdate > ? and lastupdate < ? ";
      if(flowid > 0)
        query = query + " and flowid = " + flowid;
      
      pst = db.prepareStatement(query);
      pst.setTimestamp(1,  new Timestamp(startDate.getTime()));
      pst.setTimestamp(2,  new Timestamp(startDate.getTime()));
      pst.setTimestamp(3, new Timestamp( endDate.getTime()));
      rs = pst.executeQuery();
      
      if (Logger.isDebugEnabled()) {Logger.debug(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "QUERY=" + query);}
      
      rs.next();
      total = rs.getInt(1);
      

    } catch (SQLException sqle) {Logger.error(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) { Logger.error(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "exception: " + e.getMessage(), e);
    } finally {DatabaseInterface.closeResources(db, pst, rs);
    }  
    return total;
  }

  /**
   * Count the open processes in the interval [StartDate , EndDate]
   */
  public int getOpenProcess(UserInfoInterface userInfo,int flowid, Date[] interval) {
    
    Date startDate = interval[0];
    Date endDate = interval[1];
    
    int total = 0;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);

      String query = " select count(pid) from process "
                     +"where created > ? and created < ? "
                     +"and (lastupdate > ? or closed = 0 )"; 
      if(flowid > 0)
        query = query + " and flowid = " + flowid;
      
      pst = db.prepareStatement(query);
      pst.setTimestamp(1,  new Timestamp(startDate.getTime()));
      pst.setTimestamp(2,  new Timestamp(endDate.getTime()));
      pst.setTimestamp(3, new Timestamp( endDate.getTime()));
      rs = pst.executeQuery();
      
      if (Logger.isDebugEnabled()) {Logger.debug(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "QUERY=" + query);}
      
      rs.next();
      total = rs.getInt(1);
      

    } catch (SQLException sqle) {Logger.error(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) { Logger.error(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "exception: " + e.getMessage(), e);
    } finally {DatabaseInterface.closeResources(db, pst, rs);
    }  
    return total;
  }
  
  /**
   * Count the Treated processes in the interval [StartDate , EndDate]
   */
  public int getTreatedProcess(UserInfoInterface userInfo,int flowid, Date[] interval) {
    
    Date startDate = interval[0];
    Date endDate = interval[1];
    
    int total = 0;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);

      String query = "select count(pid) from process_history " +
            "where undoflag=0 and closed=1 and created > ? and created < ?"
          + "and lastupdate > ? and lastupdate < ?";
      if(flowid > 0)
        query = query + " and flowid = " + flowid;
      
      pst = db.prepareStatement(query);
      pst.setTimestamp(1, new Timestamp(startDate.getTime()));
      pst.setTimestamp(2, new Timestamp( endDate.getTime()));
      pst.setTimestamp(3, new Timestamp(startDate.getTime()));
      pst.setTimestamp(4, new Timestamp( endDate.getTime()));
      rs = pst.executeQuery();
      
      if (Logger.isDebugEnabled()) {Logger.debug(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "QUERY=" + query);}
      
      rs.next();
      total = rs.getInt(1);

    } catch (SQLException sqle) {Logger.error(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) { Logger.error(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "exception: " + e.getMessage(), e);
    } finally {DatabaseInterface.closeResources(db, pst, rs);
    }   
    return total;
  }
  
  /**
   * Count the LeftIn processes in the interval [StartDate , EndDate]
   */
  public int getLeftInProcess(UserInfoInterface userInfo,int flowid, Date[] interval) {
    
    Date startDate = interval[0];
    Date endDate = interval[1];
    
    int total = 0;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);

      String query = "select count(pid) from process " +
            "where created < ? and ( ( closed = 1 && lastupdate > ?) or closed = 0 )"; 
     if(flowid > 0)
       query = query + " and flowid = " + flowid;
     
      pst = db.prepareStatement(query);
      pst.setTimestamp(1,  new Timestamp(startDate.getTime()));
      pst.setTimestamp(2, new Timestamp( endDate.getTime()));
      rs = pst.executeQuery();
      
      if (Logger.isDebugEnabled()) {Logger.debug(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "QUERY=" + query);}
      
      rs.next();
      total = rs.getInt(1);

    } catch (SQLException sqle) {Logger.error(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) { Logger.error(userInfo.getUtilizador(), this, "getProcessActivityStatistics", "exception: " + e.getMessage(), e);
    } finally {DatabaseInterface.closeResources(db, pst, rs);
    }   
    return total;
  }
  
  /**
   * Method to fetch user/profile activity performance audit data
   * 
   * @param userInfo
   * @param procData
   * @return audit data array
   * @throws NamingException
   */
  public AuditData[] getProcessActivityPerformance(UserInfoInterface userInfo, int flowid, Date[] interval) {
    Date f_date_a = interval[0];
    Date f_date_c = interval[1];
    AuditData[] retObj = null;

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    String userid = userInfo.getUtilizador();

    try {

      ds = Utils.getDataSource();
      db = ds.getConnection();

      String query = DBQueryManager.processQuery("ProcessManager.get_performance",
          new Object[] { ((flowid > 0) ? " and flowid=" + String.valueOf(flowid) : "") });
      
      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "getProcessActivityPerformance", 
            "QUERY='" + query + "' / PARAMS: fid:" + flowid + 
            ";arch: " + f_date_a + ";created:" + f_date_c);
      }
      
      pst = db.prepareStatement(query);
      pst.setTimestamp(1,  new Timestamp(f_date_a.getTime()));
      pst.setTimestamp(2, new Timestamp( f_date_c.getTime()));
      rs = pst.executeQuery();

      
      List<AuditData> alData = new ArrayList<AuditData>();
      while (rs.next()) {
        String sName = rs.getString(1);
        String sValue = rs.getString(2);
        alData.add(new AuditData(sName, sValue));
      }
      rs.close();
      rs = null;

      retObj = new AuditData[alData.size()];
      for (int i = 0; i < retObj.length; i++) {
        retObj[i] = (AuditData) alData.get(i);
      }
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessActivityPerformance", "sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessActivityPerformance", "exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    return retObj;
  }
  /*
   * (non-Javadoc)
   * @see pt.iflow.api.core.ProcessManager#getUserProcesses(pt.iflow.api.utils.UserInfoInterface, int, java.lang.String, java.lang.String[], boolean, pt.iflow.api.filters.FlowFilter)
   */
  public UserProcesses getUserProcesses(UserInfoInterface userInfo, int nShowFlowId, String targetOwner, String[] idx,
      boolean closedProcesses, FlowFilter filter) {
    // first, fix filter
    if (filter == null) {
      filter = new FlowFilter();
    }
    filter.setDateAfter(Utils.fixDateAfter(filter.getDateAfter()));
    filter.setDateBefore(Utils.fixDateBefore(filter.getDateBefore()));
    
    Connection db = null;
    PreparedStatement pst = null;
    Statement st = null;
    ResultSet rs = null;

    final Map<Integer, Boolean> hmShowAssignedCache = new HashMap<Integer, Boolean>();
    final Map<Integer, Boolean> hmReadProcessCache = new HashMap<Integer, Boolean>();
    final Map<Integer, Boolean> hmSuperProcessCache = new HashMap<Integer, Boolean>();
    final Map<Integer, Boolean> hmProcessHasDetailCache = new HashMap<Integer, Boolean>();
    final List<List<String>> alData = new ArrayList<List<String>>();
    final Map<String, String> hmFlowNames = new HashMap<String, String>();
    final Map<String, List<String>> hmFlowPids = new HashMap<String, List<String>>();
    final Map<String, Map<String, List<String>>> hmFlowUsers = new HashMap<String, Map<String, List<String>>>();
    final FlowSettings flowSettings = BeanFactory.getFlowSettingsBean();

    String activityTable = "activity";

    if (closedProcesses) {
      activityTable = activityTable + "_history";
    }
    if (nShowFlowId >= 0 && !canViewProcess(userInfo, nShowFlowId)) {
      return new UserProcesses(new ArrayList<List<String>>(), new HashMap<String, Map<String, List<String>>>());
    }
    Flow flow = BeanFactory.getFlowBean();

    IFlowData[] arrFlowData = null;
    if (nShowFlowId < 0) {
      FlowType[] flowTypeExcluded = {FlowType.SUPPORT,FlowType.SEARCH,FlowType.REPORTS};
      arrFlowData = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, null, flowTypeExcluded);
    } else {
      arrFlowData = new IFlowData[] { BeanFactory.getFlowHolderBean().getFlow(userInfo, nShowFlowId) };
    }
    if (arrFlowData != null && arrFlowData.length > 0) {
      for (int i = 0; i < arrFlowData.length; i++) {
        int nflowid = arrFlowData[i].getId();
        // permission related stuff. First Read permission. Second Show assigned
        if (!hmReadProcessCache.containsKey(nflowid)) {
          boolean val = canViewProcess(userInfo, nflowid);
          hmReadProcessCache.put(nflowid, val);
        }
        if (!hmShowAssignedCache.containsKey(nflowid)) {
          FlowSetting setting = flowSettings.getFlowSetting(nflowid, Const.sSHOW_ASSIGNED_TO);
          boolean val = false;
          if (null != setting) {
            val = StringUtils.equalsIgnoreCase(Const.sSHOW_YES, setting.getValue());
          }
          hmShowAssignedCache.put(nflowid, val);
        }
        if (!hmProcessHasDetailCache.containsKey(nflowid)) {
          IFlowData fd = flow.getFlow(userInfo, nflowid); // must instantiate...  :-(
          boolean val = false;
          if (fd != null && fd.hasDetail()) {
            if (fd.getDetailForm() != null)
              val = true;
            else if (fd.getCatalogue() != null)
              val = true;
          }
          hmProcessHasDetailCache.put(nflowid, val);
        }
        if (!hmSuperProcessCache.containsKey(nflowid)) {
          boolean val = false;
          if (filter.isIntervenient())
            val = canViewProcess(userInfo, nflowid);
          else
            val = canSuperProcess(userInfo, nflowid);
          hmSuperProcessCache.put(nflowid, val);
        }
      }
      if (nShowFlowId > 0 && !hmSuperProcessCache.get(nShowFlowId)) {
        targetOwner = userInfo.getUtilizador();
      }
      try {
        db = DatabaseInterface.getConnection(userInfo);
        {
        StringBuilder sbQuery = new StringBuilder(2048);
        sbQuery.append("select p.flowid,p.pid,p.subpid,p.created,p.creator,p.pnumber,f.flowname");
        if (!closedProcesses || Const.SEARCH_FLOW_STATE_HISTORY) {
          sbQuery.append(",fs.result,fs.mdate");
        }
        sbQuery.append(" from process p inner join flow f on p.flowid = f.flowid");
        if (!closedProcesses || Const.SEARCH_FLOW_STATE_HISTORY) {
          sbQuery.append(" left join flow_state fs on fs.flowid=p.flowid and fs.pid=p.pid and fs.closed=" + (closedProcesses ? "1" : "0"));
        }
        if (StringUtils.isNotEmpty(targetOwner) && filter.isIntervenient()) 
          sbQuery.append(" inner join " + (closedProcesses ? "activity_history" : "activity") + " a on p.pid=a.pid and a.userid = ?");
        sbQuery.append(" where p.closed=" + (closedProcesses ? "1" : "0") + " and ");
        
        sbQuery.append("p.flowid in (");
        boolean comma = false;
        for (int i = 0; i < arrFlowData.length; i++) {
          if (!hmReadProcessCache.get(arrFlowData[i].getId()))
            continue;
          if (comma)
            sbQuery.append(",");
          comma = true;
          sbQuery.append(arrFlowData[i].getId());
          hmFlowNames.put(String.valueOf(arrFlowData[i].getId()), arrFlowData[i].getName());
        }

        sbQuery.append(")");

        if (StringUtils.isNotEmpty(targetOwner) && !filter.isIntervenient()) {
            sbQuery.append(" and p.creator=?");
        }

        if (filter.getDateAfter() != null) {
          sbQuery.append(" and p.created >= ?");
        }

        if (filter.getDateBefore() != null) {
          sbQuery.append(" and p.created < ?");
        }

        for (int i = 0; i < idx.length && i < Const.INDEX_COLUMN_COUNT; i++) {
          if (StringUtils.isNotEmpty(idx[i]))
            sbQuery.append(" and lower(p.idx" + i + ") like lower('%").append(escapeSQL(idx[i])).append("%')");
        }

        if (StringUtils.isNotEmpty(filter.getPnumber())) {
          sbQuery.append(" and lower(p.pnumber) like lower('%").append(escapeSQL(filter.getPnumber())).append("%')");
        }

        if (filter.getOrderBy()== null || "".equals(filter.getOrderBy())) {
          sbQuery.append(" order by p.pid asc, p.flowid asc");
        } else {
          sbQuery.append(" order by ").append(filter.getOrderBy()).append(" ").append(filter.getOrderType());
        }

        Logger.debug(userInfo.getUtilizador(), this, "getUserProcesses", "Executing query: " + sbQuery);

        pst = db.prepareStatement(sbQuery.toString());
        }
        int pos = 0;

        if (StringUtils.isNotEmpty(targetOwner)) {
          pst.setString(++pos, targetOwner);
        }
        if (filter.getDateAfter() != null) {
          pst.setTimestamp(++pos, new Timestamp(filter.getDateAfter().getTime()));
        }
        if (filter.getDateBefore() != null) {
          pst.setTimestamp(++pos, new Timestamp(filter.getDateBefore().getTime()));
        }

        rs = pst.executeQuery();

        int counter = -1;
        while (rs.next()) {
          int nflowid = rs.getInt(DataSetVariables.FLOWID);
          if(filter.ignoreFlow(nflowid)) {
            continue;
          }
          String flowid = rs.getString(DataSetVariables.FLOWID);
          String pid = rs.getString(DataSetVariables.PID);
          String subpid = rs.getString(DataSetVariables.SUBPID);
          String pn = rs.getString(DataSetVariables.PNUMBER);
          String procCreator = rs.getString(DataSetVariables.CREATOR);
          String result = "";
          Timestamp tstamp = null;
          if (!closedProcesses || Const.SEARCH_FLOW_STATE_HISTORY) {
            result = rs.getString(DataSetVariables.RESULT);
            tstamp = rs.getTimestamp(DataSetVariables.MDATE);
          }

          // post security check: this pid must be owned or user must be able to supervise process
          if (!StringUtils.equals(procCreator, userInfo.getUtilizador()) && !hmSuperProcessCache.get(nflowid)) {
            Logger.debug(userInfo.getUtilizador(), this, "getUserProcesses", "User is not supervisor. Access to process denied: "+procCreator);
            continue;
          }

          if (filter.hasSizeLimit()) {
            counter++;
            if (counter - filter.getStartIndex() > filter.getNumElements()) {
              break;
            } else if (counter < filter.getStartIndex()) {
              continue;
            }
          }
          
          List<String> alFlowPids = null;
          if (hmFlowPids.containsKey(flowid)) {
            alFlowPids = hmFlowPids.get(flowid);
          } else {
            alFlowPids = new ArrayList<String>();
            hmFlowPids.put(flowid, alFlowPids);
          }
          alFlowPids.add(pid);

       // ArrayList<String> alFlowFields = new ArrayList<String>(UserProcsConst.FIELD_COUNT);
          String [] saFlowFields = new String [UserProcsConst.FIELD_COUNT];

          // flow (id and name)
          saFlowFields[UserProcsConst.FLOW_ID] = flowid;
          
          saFlowFields[UserProcsConst.FLOW_NAME] = hmFlowNames.get(flowid);
          // pid
          saFlowFields[UserProcsConst.PID] = pid;
          // subpid
          saFlowFields[UserProcsConst.SUBPID] = subpid;
          // result
          saFlowFields[UserProcsConst.RESULT] = result == null ? "" : result;
          // mdate
          saFlowFields[UserProcsConst.MDATE] = tstamp == null ? "" : tstamp.toString();
          //creator
          saFlowFields[UserProcsConst.CREATOR] = procCreator;
          // pnumber
          saFlowFields[UserProcsConst.PNUMBER] = pn;

          // pode ler o processo (ver detalhe)
          saFlowFields[UserProcsConst.SHOW_DETAIL] = String.valueOf(hmReadProcessCache.get(nflowid)&&hmProcessHasDetailCache.get(nflowid));
          // pode ver os utilizadores agendados
          saFlowFields[UserProcsConst.SHOW_ASSIGNED] = String.valueOf(hmShowAssignedCache.get(nflowid)&&hmReadProcessCache.get(nflowid));

          alData.add(arrayToList(saFlowFields));
        }// while
        rs.close();
        rs = null;

        pst.close();
        pst = null;

        for (int i = 0; i < arrFlowData.length; i++) {
          // discard protected flows
          int nflowid = arrFlowData[i].getId();
          boolean show = hmShowAssignedCache.get(nflowid) || hmReadProcessCache.get(nflowid);
          if (!show)
            hmFlowPids.remove(nflowid);
        }

        st = db.createStatement();
        if (hmFlowPids.size() > 0 && !closedProcesses) {
          Iterator<String> iter = hmFlowPids.keySet().iterator();

          while (iter != null && iter.hasNext()) {
            String sFid = iter.next();
            List<String> alFlowPids = hmFlowPids.get(sFid);
            if (alFlowPids == null || alFlowPids.size() == 0) {
              continue;
            }
            StringBuilder sbQuery = new StringBuilder();
            sbQuery.append("select pid, userid from activity where flowid=");
            sbQuery.append(sFid);
            sbQuery.append(" and pid in (");
            for (int i = 0; i < alFlowPids.size(); i++) {
              if (i > 0) {
                sbQuery.append(",");
              }
              sbQuery.append((String) alFlowPids.get(i));
            }
            sbQuery.append(")");
            Map<String, List<String>> hmPidUsers = new HashMap<String, List<String>>();
            Logger.debug(userInfo.getUtilizador(), this, "getUserProcesses", "Executing query: " + sbQuery);
            rs = st.executeQuery(sbQuery.toString());
            List<String> alPidUsers = new ArrayList<String>();
            while (rs != null && rs.next()) {
              String sPid = rs.getString("pid");
              if (hmPidUsers.containsKey(sPid)) {
                alPidUsers = hmPidUsers.get(sPid);
              } else {
                alPidUsers = new ArrayList<String>();
              }
              alPidUsers.add(rs.getString("userid"));
              hmPidUsers.put(sPid, alPidUsers);
            }// while
            alPidUsers = null;
            hmFlowUsers.put(sFid, hmPidUsers);
          }
        }
      } catch (SQLException sqle) {
        Logger.error(userInfo.getUtilizador(), this, "getUserProcesses", "Error retrieving data from DB.", sqle);
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "getUserProcesses", "Error retrieving processes.", e);
      } finally {
        DatabaseInterface.closeResources(db,st,pst,rs);
      }
    }
    return new UserProcesses(alData, hmFlowUsers);
  }
  
  public java.util.Collection<String> getProcessIntervenients(UserInfoInterface userInfo, ProcessData procData) {
    if (null == userInfo) {
      Logger.warning("N/A", this, "getProcessIntervenients", "Invalid user");
      return null;
    }
    if (null == procData) {
      Logger.warning(userInfo.getUtilizador(), this, "getProcessIntervenients", "Invalid process data");
      return null;
    }

    int flowid, pid, subpid;

    flowid = procData.getFlowId();
    pid = procData.getPid();
    subpid = procData.getSubPid();

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    Collection<String> users = new HashSet<String>();

    try {
      db = DatabaseInterface.getConnection(userInfo);      
      st = db.createStatement();

      final String query = "select distinct userid from activity_history where flowid=" + flowid + " and pid=" + pid
          + " and subpid=" + subpid + " and archived is not null and worker=1 and undoflag=0";

      Logger.debug(userInfo.getUtilizador(), this, "getProcessIntervenients", "Executing query: " + query);
      rs = st.executeQuery(query);
      while (rs.next()) {
        String user = rs.getString(1);
        users.add(user);
      }
    } catch (SQLException e) {
      Logger.error(userInfo.getUtilizador(), this, "getProcessIntervenients", 
          procData.getSignature() + "Error retrieving data from DB.", e);
      users = null;
    } finally {
      // free db connections
      DatabaseInterface.closeResources(db,st,rs);
    }

    return users;
  }

  /**
   * Returns the process info.
   * 
   * @return
   */
  public String getProcessCreator(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    String userid = userInfo.getUtilizador();

    Logger.trace(this, "getProcessCreator", userid + " call.");

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    String retObj = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(true);
      st = db.createStatement();
      rs = st.executeQuery("select creator from process where flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid);
      if (rs.next()) {
        retObj = rs.getString("creator");
      }
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessCreator", "sql exception " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessCreator", "exception " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  public String getProcessNumber(UserInfoInterface userInfo, int flowid, int pid) {
    Flow flow = BeanFactory.getFlowBean();
    ProcessCatalogue catalogue = flow.getFlowCatalogue(userInfo, flowid);
    ProcessHeader procHeader = new ProcessHeader(flowid, pid, -1);
    ProcessData pd = new ProcessData(catalogue, procHeader);
    ProcessData[] pda = getProcessesNumbers(userInfo, new ProcessData[] { pd });
    if (pda != null && pda.length > 0) {
      pd = pda[0];
      return pd.getPNumber();
    }
    return null;
  }

  public ProcessData[] getProcessesNumbers(UserInfoInterface userInfo, ProcessData[] procs) {
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);

      pst = db.prepareStatement("select pnumber from process where flowid=? and pid=?");

      for (int i = 0; i < procs.length; i++) {
        pst.setInt(1, procs[i].getFlowId());
        pst.setInt(2, procs[i].getPid());

        rs = pst.executeQuery();
        if (rs.next()) {
          procs[i].setPNumber(rs.getString(1));
        }
        rs.close();
        rs = null;
      }

      return procs;

    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getProcessesNumbers", "exception " + e.getMessage(), e);
      return null;
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
  }

  public ProcessHeader findProcess(UserInfoInterface userInfo, ProcessHeader proc) {
    ProcessHeader[] pda = new ProcessHeader[] { proc };
    pda = findProcesses(userInfo, pda);
    if (null == pda || pda.length == 0)
      return null;
    return pda[0];
  }

  public ProcessHeader[] findProcesses(UserInfoInterface userInfo, ProcessHeader[] procs) {
    if (null == userInfo) {
      Logger.error(null, this, "findProcesses", "Not authorized.");
      return null;
    }

    if (null == procs || procs.length == 0) {
      Logger.error(userInfo.getUtilizador(), this, "findProcesses", "No process to find");
      return null;
    }

    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);

      StringBuilder sbQuery = new StringBuilder("select flowid,pid,subpid,pnumber from process");
      //pponte: por erro na versão 4 deixou de ser possível obter aqui processos fechados
      //esta alteração corrige essa situação (consultar SVN).
      sbQuery.append(" where (? = -1 or flowid=?)");
      sbQuery.append(" and (? = -1 or pid=?)");
      sbQuery.append(" and (? = -1 or subpid=?)");
      sbQuery.append(" and (? is null or pnumber=?)");

      Logger.debug(userInfo.getUtilizador(), this, "findProcesses", "prepared statement query: " + sbQuery);

      pst = db.prepareStatement(sbQuery.toString());

      for (int i = 0; i < procs.length; i++) {
        if (null == procs[i])
          continue;
        int flowid = procs[i].getFlowId();
        int pid = procs[i].getPid();
        int subpid = procs[i].getSubPid();
        String pnumber = procs[i].getPNumber();

        pst.setInt(1, flowid);
        pst.setInt(2, flowid);

        pst.setInt(3, pid);
        pst.setInt(4, pid);

        pst.setInt(5, subpid);
        pst.setInt(6, subpid);

        pst.setString(7, StringUtils.isEmpty(pnumber) ? null : pnumber);
        pst.setString(8, StringUtils.isEmpty(pnumber) ? null : pnumber);

        Logger.debug(userInfo.getUtilizador(), this, "findProcesses", "Executing query: " + sbQuery);

        rs = pst.executeQuery();
        if (rs.next()) {
          procs[i].setFlowId(rs.getInt("flowid"));
          procs[i].setPid(rs.getInt("pid"));
          procs[i].setSubPid(rs.getInt("subpid"));
          procs[i].setPNumber(rs.getString("pnumber"));
        }
        rs.close();
        rs = null;

      }
    } catch (Throwable t) {
      Logger.error(userInfo.getUtilizador(), this, "findProcesses", "Error finding processes.", t);
      procs = null;
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    return procs;
  }

  /**
   * 
   * @param userInfo
   *          User
   * @param procData
   *          Process
   * @return true if User can view process.
   */
  public boolean canViewProcess(UserInfoInterface userInfo, ProcessData procData) {

    if (userInfo == null)
      return false;
    if (null == procData)
      return false;

    int flowid = procData.getFlowId();

    return canViewProcess(userInfo, flowid);
  }

  public boolean canViewProcess(UserInfoInterface userInfo, ProcessHeader process) {

    if (userInfo == null)
      return false;
    if (null == process)
      return false;

    int flowid = process.getFlowId();

    return canViewProcess(userInfo, flowid);
  }

  private boolean canViewProcess(UserInfoInterface userInfo, int flowid) {

    if (userInfo == null)
      return false;
    if (flowid < 0)
      return false;

    if (userInfo.isOrgAdmin())
      return true;
    
    String priv = new String(new char[] { FlowRolesTO.SUPERUSER_PRIV, FlowRolesTO.READ_PRIV }); 
    
    if (BeanFactory.getFlowBean().checkUserFlowRoles(userInfo, flowid, priv))
      return true;

    return false;
  }

  private boolean canSuperProcess(UserInfoInterface userInfo, int flowid) {

    if (userInfo == null)
      return false;
    if (flowid < 0)
      return false;

    if (BeanFactory.getFlowBean().checkUserFlowRoles(userInfo, flowid, "" + FlowRolesTO.SUPERUSER_PRIV))
      return true;

    return false;
  }

  private static String escapeSQL(String sql) {
    return StringEscapeUtils.escapeSql(sql);
  }

  public synchronized void archiveProcesses(UserInfoInterface adm) {
    if (null == adm)
      return; // invalid user
    if (!adm.isLogged())
      return; // please login
    if (!adm.isSysAdmin() && !adm.isOrgAdmin())
      return; // must be admin
    Collection<ProcessHeader> processes = listExpiredProcesses(adm);
    archiveProcesses(adm, processes);
  }

  public Collection<ProcessHeader> listExpiredProcesses(UserInfoInterface adm) {
    if (null == adm)
      return null; // invalid user
    if (!adm.isLogged())
      return null; // please login
    if (!adm.isSysAdmin() && !adm.isOrgAdmin())
      return null; // must be admin
    List<Integer> flows = new ArrayList<Integer>();
    if (adm.isSysAdmin()) {
      flows.addAll(BeanFactory.getFlowHolderBean().listFlowIds(adm));
    } else {
      IFlowData[] userFlows = BeanFactory.getFlowHolderBean().listFlows(adm);
      if (null == userFlows || userFlows.length == 0)
        return null; // nothing to do here...
      for (IFlowData flow : userFlows)
        flows.add(flow.getId());
    }
    if (flows.isEmpty())
      return null; // nothing to do here...
    FlowSettings fSettings = BeanFactory.getFlowSettingsBean();
    Map<Integer, Long> mTTL = new HashMap<Integer, Long>();
    for (Integer flowid : flows) {
      FlowSetting fs = fSettings.getFlowSetting(flowid, Const.sAUTO_ARCHIVE_PROCESS);
      if (fs == null)
        continue;
      String expire = fs.getValue();

      long limit = -1L;
      final long msPerDay = 86400000L; // 24 hours * 60 minutes * 60 seconds *
                                       // 1000 ms
      // parse expire date...
      try {
        if (expire != null) {
          limit = NumberFormat.getIntegerInstance().parse(expire.trim()).longValue() * msPerDay;
        }
      } catch (Throwable t) {
        continue;
      }

      if (limit > 0L)
        mTTL.put(flowid, limit);
    }
    if (mTTL.isEmpty())
      return null; // nothing to do here...

    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    List<ProcessHeader> processes = new LinkedList<ProcessHeader>();

    Date now = new Date();

    try {
      db = DatabaseInterface.getConnection(adm);
      // final String sqlExpired =
      // "select a.flowid,a.pid,a.subpid,a.mdate from flow_state_history a, process_history p " +
      // "where a.flowid=? and a.mdate<? and p.closed=1 and a.flowid=p.flowid and a.pid=p.pid and a.subpid=p.subpid and " +
      // "a.mid = (select max(b.mid) from flow_state_history b where b.flowid=a.flowid and b.pid=a.pid and b.subpid=a.subpid)";

      // Esta query é muito mais eficiente. Visitar:
      final String sqlExpired = "SELECT a.flowid,a.pid,a.subpid,a.mdate FROM flow_state_history a "
          + "INNER JOIN process_history p ON (a.flowid=p.flowid and a.pid=p.pid and a.subpid=p.subpid and p.closed=1) "
          + "INNER JOIN (SELECT concat_ws(',',c.flowid,c.pid,c.subpid) code, MAX(c.mid) AS mid, c.flowid, c.pid, c.subpid FROM flow_state_history c WHERE c.flowid=? GROUP BY code) b "
          + "ON (a.flowid = b.flowid AND a.pid=b.pid AND a.subpid = b.subpid AND a.mid=b.mid) WHERE a.mdate < ?";
      pst = db.prepareStatement(sqlExpired);

      for (Integer flowid : mTTL.keySet()) {

        pst.setInt(1, flowid);
        pst.setTimestamp(2, new Timestamp(now.getTime() - mTTL.get(flowid)));

        rs = pst.executeQuery();
        while (rs.next()) {
          int pid = rs.getInt(2);
          int subpid = rs.getInt(3);
          processes.add(new ProcessHeader(flowid, pid, subpid));
        }
        rs.close();
      }
      pst.close();
      pst = null;

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    return Collections.unmodifiableCollection(processes);
  }

  private Collection<ProcessHeader> archiveProcesses(UserInfoInterface userInfo, Collection<ProcessHeader> processes) {
    if (null == userInfo || null == processes)
      return null;
    Connection db = null;
    CallableStatement cst = null;
    List<ProcessHeader> removed = new ArrayList<ProcessHeader>(processes.size());

    String login = userInfo.getUtilizador();
    
    Date now = new Date();

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);

      // args: OUT resultado, IN flowid, IN pid, IN now
      cst = db.prepareCall("{call archiveProc(?,?,?,?)}");

      for (ProcessHeader process : processes) {
        boolean rollback = false;
        try {
        int flowid = process.getFlowId();
        int pid = process.getPid();
        cst.setInt(2, flowid);
        cst.setInt(3, pid);
        cst.setTimestamp(4, new Timestamp(now.getTime()));
        cst.registerOutParameter(1, java.sql.Types.INTEGER);
        cst.executeUpdate();
        int result = cst.getInt(1);
          if(result == 1) {            
            DatabaseInterface.commitConnection(db);
            removed.add(process);
            Logger.info(login, this,"archiveProcesses",
                process.getSignature() + "archived");
          }
          else {
            rollback = true;
          }
        }
        catch (Exception e) {
          Logger.error(login, this, "archiveProcesses",
              process.getSignature() + "error", e);
          rollback = true;
        }
        
        if (rollback) {
          DatabaseInterface.rollbackConnection(db);
        }
      }

    } catch (Exception oe) {
      Logger.error(login, this, "archiveProcesses", "error", oe);
    } finally {
      DatabaseInterface.closeResources(db, cst);
    }

    return removed;
  }

  private void historifyActivities(UserInfoInterface userInfo, Connection db, ProcessHeader procHeader, String userid) 
  throws Exception {
    Activity target = new Activity(userid, procHeader.getFlowId(), procHeader.getPid(), procHeader.getSubPid(), null, null, null);
    String[] activityUsers = this.getActivityOwners(userInfo, target);
    historifyActivities(userInfo, db, procHeader, userid, activityUsers);
  }  

  private void historifyActivities(UserInfoInterface userInfo, Connection db, ProcessHeader procHeader, String userid, String...users) 
    throws Exception {
    
    Statement st = null;
    PreparedStatement pst = null;

    int flowid = procHeader.getFlowId();
    int pid = procHeader.getPid();
    int subpid = procHeader.getSubPid();
    
    try {

      st = db.createStatement();

      StringBuilder sbUsers = new StringBuilder();
      for (int i = 0; i < users.length; i++) {
        if (i > 0)
          sbUsers.append(",");
        sbUsers.append("'").append(escapeSQL(users[i])).append("'");
      }

      String query = DBQueryManager.processQuery("ProcessManager.update_activity_users", new Object[] {
          sbUsers.toString(),
          String.valueOf(flowid),
          String.valueOf(pid),
          String.valueOf(subpid)
      });

      int nUpdated = st.executeUpdate(query);

      if (nUpdated > 0) {
        pst = db.prepareStatement("insert into activity_history (userid,flowid,"
            + "pid,subpid,type,priority,created,started,archived,description"
            + ",url,status,notify,delegated,delegateduserid,profilename,read_flag,mid,worker,undoflag) "
            + "(select userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,"
            + "delegated,? as delegateduserid,profilename,read_flag, mid, ? as worker,0 as undoflag from activity where userid=? and flowid=? and pid=? and subpid=?)");

        
        /* For every possible activityOwner */
        for (int i = 0; i < users.length; i++) {
          pst.setString(1, userid); // delegated user id   
          pst.setInt(2, (StringUtils.equals(userid, users[i]) ? 1 : 0)); // worker
          pst.setString(3, users[i]);
          pst.setInt(4, flowid);
          pst.setInt(5, pid);
          pst.setInt(6, subpid);

          Logger.debug(userInfo.getUtilizador(), this, "historifyActivities",
              "insert into activity_history for user " + users[i] + 
              " and flow:" + flowid + ";pid:" + pid + ";subpid:" + subpid);

          pst.executeUpdate();
        }
      }
    }
    finally {
      DatabaseInterface.closeResources(st, pst);
    }
  }
  
  public void endProc(UserInfoInterface userInfo, ProcessData procData, boolean isCancel) throws Exception {
    String login = userInfo.getUtilizador();

    Statement st = null;
    ResultSet rs = null;
    Connection db = null;
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      this.deleteAllActivities(userInfo, procData);

      st = db.createStatement();
      rs = st.executeQuery("select * from process"
          + " where flowid=" + procData.getFlowId()
          + " and pid=" + procData.getPid()
          + " and subpid=" + procData.getSubPid()
          + " and closed=0");

      if (rs.next()) {
        rs.close();
        rs = null;

        // insert mid in modification table
        int newmid = atomicGetNextMid(userInfo, procData.getProcessHeader());
        procData.setMid(newmid);
        
        // checks if is the last subprocess belonging to a specific process
        rs = st.executeQuery("select count(1) from process"
            + " where flowid=" + procData.getFlowId()
            + " and pid=" + procData.getPid()
            + " and closed=0");
        rs.next();
        boolean lastSubProc = (rs.getInt(1) == 1);
        rs.close();
        rs = null;
        if (lastSubProc) {
          // last subprocess belonging to a specific process => delete from forkjoin_mines
          st.execute("delete from forkjoin_mines where flowid=" + procData.getFlowId() + " and pid=" + procData.getPid());
        }

        // close it
        StringBuilder sQuery = new StringBuilder("update process set closed=1,mid=");
        sQuery.append(newmid);
        if (isCancel) {
          sQuery.append(",canceled=1");
        }
        sQuery.append(" where flowid=").append(procData.getFlowId());
        sQuery.append(" and pid=").append(procData.getPid());
        sQuery.append(" and subpid=").append(procData.getSubPid());
        st.execute(sQuery.toString());

        Logger.info(login, this, "endProc", procData.getSignature() + "Process ended.");

        
        StringBuilder indexCols = new StringBuilder();
        Map<String, Integer> idxCatalog = BeanFactory.getFlowHolderBean().getFlow(userInfo, procData.getFlowId()).getIndexVars();
        Iterator<String> indexVars = idxCatalog.keySet().iterator();
        while (indexVars.hasNext()) {
          String indexVar = indexVars.next();
          String colname = "idx" + idxCatalog.get(indexVar);
          indexCols.append(",").append(colname);
        }
        
        // historify it
        
        //TODO
        if (doSaveProcessHistory(true)) {
          sQuery = new StringBuilder();
          sQuery.append("insert into process_history (flowid,pid,subpid,mid,creator,created,");
          sQuery.append("currentuser,lastupdate,pnumber,procdata,procdatazip,closed,canceled");
          sQuery.append(indexCols.toString());
          sQuery.append(")"); 
          sQuery.append(" select flowid,pid,subpid,mid,creator,created,");
          sQuery.append("currentuser,lastupdate,pnumber,");
          if (Const.SAVE_PROCESSHISTORY_METHOD.equals(Const.SAVE_PROCESSHISTORY_METHOD_COMPRESS)){
            sQuery.append("null,?,");
          }else if (Const.SAVE_PROCESSHISTORY_METHOD.equals(Const.SAVE_PROCESSHISTORY_METHOD_NOTHING)){
            sQuery.append("null,null,");
          }else{
            sQuery.append("procdata,null,");
          }
          sQuery.append("closed,canceled");
          sQuery.append(indexCols.toString());        
          sQuery.append(" from process where ");
          sQuery.append(" flowid=").append(procData.getFlowId());
          sQuery.append(" and pid=").append(procData.getPid());
          sQuery.append(" and subpid=").append(procData.getSubPid());
          PreparedStatement pst = db.prepareStatement(sQuery.toString());
          if (Const.SAVE_PROCESSHISTORY_METHOD.equals(Const.SAVE_PROCESSHISTORY_METHOD_COMPRESS)){
            StringBuilder sSelect = new StringBuilder("select procdata from process where");
            sSelect.append(" flowid=").append(procData.getFlowId());
            sSelect.append(" and pid=").append(procData.getPid());
            sSelect.append(" and subpid=").append(procData.getSubPid());
            rs = st.executeQuery(sSelect.toString());
            rs.next();
            String pdata = rs.getString("procdata");
            pst.setBytes(1, compress(pdata));
          }
          pst.executeUpdate();
        }
        Logger.info(login, this, "endProc", procData.getSignature() + "Process historified.");

      }
      // else, flow is being deleted... don't historify process.
    } catch (Exception e) {
      Logger.error(login, this, "endProc", procData.getSignature() + "caught exception", e);
      throw e;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
  }

  private static List<String> arrayToList(String [] a) {
    return java.util.Arrays.asList(a);
  }
  
  public int countFlowProcesses(UserInfoInterface userInfo, int flowid, boolean includeClosed, boolean includeArchived) {
    if (null == userInfo)
      return -1; // invalid user
    if (!userInfo.isLogged())
      return -1; // please login
    if (!userInfo.isSysAdmin() && !userInfo.isOrgAdmin())
      return -1; // must be admin
    IFlowData flowData = BeanFactory.getFlowHolderBean().getFlow(userInfo, flowid, false);
    if(flowData == null) return -1;
    
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    int count = 0;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();

      String query = "select count(1) from process where flowid="+flowid+" and closed=0";
      rs = st.executeQuery(query);
      if(rs.next()) count += rs.getInt(1);
      rs.close();
      rs = null;
      
      if(includeClosed) {
        query = "select count(1) from process_history where flowid="+flowid;
        rs = st.executeQuery(query);
        if(rs.next()) count += rs.getInt(1);
        rs.close();
        rs = null;
        
        if(includeArchived) {
          query = "select count(1) from process_archive where flowid="+flowid;
          rs = st.executeQuery(query);
          if(rs.next()) count += rs.getInt(1);
          rs.close();
          rs = null;
        }
      }
      st.close();
      st = null;

    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "countFlowProcesses", "Error counting processes", e);
      count = -1;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return count;
  }

  public void disableActivities(UserInfoInterface userInfo, ProcessData procData) throws Exception {
    toggleActivities(userInfo, procData, false);
  }
  public void enableActivities(UserInfoInterface userInfo, ProcessData procData) throws Exception {
    toggleActivities(userInfo, procData, true);    
  }

  private void toggleActivities(UserInfoInterface userInfo, ProcessData procData, boolean enable) throws Exception {
    String userid = userInfo.getUtilizador();

    Logger.trace(this, "toggleActivities", 
        "Call for user=" + userid + ", " + procData.getSignature() + ", enable?" + enable);

    Connection db = null;
    PreparedStatement pst = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("update activity set status=? where flowid=? and pid=? and subpid=? and delegated=0");

      // STATUS
      // 0 - visible in user tasks
      // 1 - not visible
      pst.setInt(1, enable?0:1);
      pst.setInt(2, procData.getFlowId());
      pst.setInt(3, procData.getPid());
      pst.setInt(4, procData.getSubPid());
      
      boolean res = pst.execute();
      
      Logger.info(userid, this, "toggleActivities", 
          procData.getSignature() + "ENABLE: " + enable + " => activities toggled?" + res);
    }      
    finally {
      DatabaseInterface.closeResources(db, pst);
    }    
  }

  public List<Activity> getPreviousActivities(UserInfoInterface userInfo, ProcessData procData) throws Exception {

    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    List<Activity> acts = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("select max(mid) from activity_history where flowid=? and pid=? and subpid=? and undoflag=0");

      pst.setInt(1, procData.getFlowId());
      pst.setInt(2, procData.getPid());
      pst.setInt(3, procData.getSubPid());
      
      rs = pst.executeQuery();
      if (rs.next()) {
        int maxmid = rs.getInt(1);
        
        rs.close();
        pst.close();
        
        
        pst = db.prepareStatement("select userid,flowid,pid,subpid,type,priority,created,started,archived,description," +
                "url,status,notify,delegated,profilename,read_flag,mid from activity_history " +
                "where flowid=? and pid=? and subpid=? and undoflag=0 and mid=?");
        pst.setInt(1, procData.getFlowId());
        pst.setInt(2, procData.getPid());
        pst.setInt(3, procData.getSubPid());
        pst.setInt(4, maxmid);
        
        rs = pst.executeQuery();
        
        acts = new ArrayList<Activity>();
        while (rs.next()) {
          Activity a = new Activity(
              rs.getString("userid"), 
              rs.getInt("flowid"), 
              rs.getInt("pid"), 
              rs.getInt("subpid"), 
              rs.getInt("type"), 
              rs.getInt("priority"), 
              rs.getTimestamp("created"), 
              rs.getTimestamp("started"), 
              rs.getTimestamp("archived"), 
              rs.getString("description"), 
              rs.getString("url"), 
              rs.getInt("status"), 
              rs.getInt("notify"));
          a.delegated = rs.getInt("delegated") == 1;
          a.profilename = rs.getString("profilename");
          if (rs.getInt("read_flag") == 1) 
            a.setRead();
          else
            a.setUnread();
          a.mid = rs.getInt("mid");
          
          acts.add(a);
        }
      }
    }      
    finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    
    return acts;     
  }

  private byte[] compress(String str) throws IOException {
    int size = 1024;
    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(str.getBytes("UTF-8")));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    GZIPOutputStream gzip = new GZIPOutputStream(baos);
    byte[] buffer = new byte[size];
    int len;
    while ((len = bis.read(buffer, 0, size)) != -1) {
      gzip.write(buffer, 0, len);
    }
    gzip.finish();
    bis.close();
    gzip.close();
    return baos.toByteArray();
  }

  private String uncompress(byte[] data) throws IOException {
    int size = 1024;
    GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[size];
    int len;
    while ((len = gzip.read(buffer, 0, size)) != -1) {
      baos.write(buffer, 0, len);
    }
    gzip.close();
    baos.close();
    return new String(baos.toByteArray(), "UTF-8");
  }
  
  private boolean doSaveProcessHistory(boolean hasChanged) {
    return Const.SAVE_PROCESSHISTORY_WHEN.equals(Const.SAVE_PROCESSHISTORY_WHEN_ALLWAYS) ||
            (Const.SAVE_PROCESSHISTORY_WHEN.equals(Const.SAVE_PROCESSHISTORY_WHEN_ONCHANGE) && hasChanged); 
  }

  public void updatePidDocs(UserInfoInterface userInfo, ProcessData procData){

      Logger.debug(userInfo.getUtilizador(), this, "updatePidDocs", "PID "+procData.getPid());
      
      //GET DOCIDS
      Collection <String> n = procData.getListVariableNames();
      Iterator <String> nomes = n.iterator();
      String docids = null;
      
      while(nomes.hasNext()) {
        
        ProcessListVariable varlist = procData.getList(nomes.next());

        Logger.debug("", this, "", "VAR "+varlist.getName()+" TIPO: "+varlist.getType().toString());
        
          if( varlist != null && "Document" == varlist.getType().toString() ){
            docids = "("+ varlist.getFormattedItem(0);
            
            for(int i = 1; i < varlist.size(); i++)
            {
              docids = docids +","+ varlist.getFormattedItem(i);
            }     
            
             docids = docids +")";
          }
      }
      
     if(docids != null){
        int flowid = procData.getFlowId();
        int pid = procData.getPid();
        int mid = procData.getSubPid();
  
        //UPDATE PIDs 
        Connection db = null;
        Statement st = null;
        try {
            db = DatabaseInterface.getConnection(userInfo);
            st = db.createStatement();
            String query = "update documents set pid = "+pid+" , subpid= "+mid+" where flowid = "+flowid+" and docid in "+docids;
            st.executeUpdate(query);              
        } catch (SQLException sqle) {Logger.error(userInfo.getUtilizador(), this, "updatePidDocs", "sql exception: " + sqle.getMessage(), sqle);
        } catch (Exception e) { Logger.error(userInfo.getUtilizador(), this, "updatePidDocs", "exception: " + e.getMessage(), e);
        } finally {DatabaseInterface.closeResources(db, st);
        } 
     }
  }

  public ListIterator<Activity> getUserActivitiesOrderByPid(UserInfoInterface userInfo) {
      String userid = userInfo.getUtilizador();
      Logger.trace(this, "getUserActivitiesOrderByPid", userid + " call.");
      


      Connection db = null;
      PreparedStatement st = null;
      ResultSet rs = null;
      LinkedList<Activity> l = new LinkedList<Activity>();
      ListIterator<Activity> result = null;

      try {
        db = DatabaseInterface.getConnection(userInfo);
        db.setAutoCommit(true);
  
        final StringBuilder sQuery = new StringBuilder(
              "select a.flowid , a.pid, a.mid, a.subpid, a.type, a.priority, a.created, a.started, a.archived, a.description, a.url, a.status, a.notify, a.profilename, a.read_flag, a.delegated, p.pnumber, PAI.icon, a.folderid from activity a,process p LEFT JOIN process_annotation_icon PAI ON (p.flowid=PAI.flowid AND p.pid=PAI.pid AND p.subpid=PAI.subpid) where p.flowid=a.flowid and p.pid=a.pid and p.subpid=a.subpid and status=0 and userid='"+userid+"' ");

          final StringBuilder sQueryDelegated = new StringBuilder(
              "select a.flowid , a.pid, a.mid, a.subpid, a.type, a.priority, a.created, a.started, a.archived, a.description, a.url, a.status, a.notify, a.profilename, a.read_flag, 1 as delegated, p.pnumber, PAI.icon, -1 as folderid from activity_delegated a,process p LEFT JOIN process_annotation_icon PAI ON (p.flowid=PAI.flowid AND p.pid=PAI.pid AND p.subpid=PAI.subpid) where p.flowid=a.flowid and p.pid=a.pid and p.subpid=a.subpid and status=0 and userid='"+userid+"' ");
              
         

          String queryUnion = sQuery.toString()+" UNION "+sQueryDelegated.toString()+" order by created";
                  
        st = db.prepareStatement(queryUnion);

        rs = st.executeQuery();

        while (rs.next()) {
          
          Activity wle = new Activity(userid, rs.getInt("flowid"), rs.getInt("pid"), rs.getInt("subpid"), rs.getInt("type"), 
              rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), rs.getTimestamp("archived"), 
              rs.getString("description"), rs.getString("url"), rs.getInt("status"), rs.getInt("notify"));
          wle.profilename = rs.getString("profilename");
          wle.pnumber = rs.getString("pnumber");
          if (rs.getInt("read_flag") == 1) {
            wle.setRead();
          }
          else {
            wle.setUnread();
          }
          wle.mid = rs.getInt("mid");
          
          if(rs.getInt("delegated") == 0)
            wle.delegated =  false;
          else
            wle.delegated =  true;  

            String icon = "";
            try {
              icon = rs.getString("icon");
            } catch (Exception e) {
              Logger.debug(userInfo.getUtilizador(), this, "getUserActivitiesOrderFilters", "No icon assign");
            }
            wle.setAnnotationIcon(icon);

            int folderid = rs.getInt("folderid");
            if( folderid > 0)
                wle.setFolderid(folderid);

          l.add(wle);
        }
        DatabaseInterface.closeResources(st, rs);
        
        result = l.listIterator();
      } catch (SQLException sqle) {
        Logger.error(userid, this, "getUserActivitiesOrderByPid", "sql exception: " + sqle.getMessage(), sqle);
        result = null;
      } catch (Exception e) {
        Logger.error(userid, this, "getUserActivitiesOrderByPid", "exception: " + e.getMessage(), e);
        result = null;
      } finally {
        DatabaseInterface.closeResources(db, st, rs);
      }
      return result;
    }
  
  private String getMaxDeadline(String op){
    int select = 0;   
    select = Integer.parseInt(op);
    
    int all_days = 0;
    int today = 1;
    int yesterday = 2;
    int this_week = 3;
    int next_week = 4;
    int this_month = 5;
    int next_month = 6;

    Calendar cal = Calendar.getInstance();
    String DATE_FORMAT_NOW = "yyyy/MM/dd";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

    if(select == all_days){
      return "";
      
    }else if(select == today){
        return sdf.format(cal.getTime());  
        
    }else if(select == yesterday){       
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-1);
        return sdf.format(cal.getTime());
               
    }else if(select == this_week){     
        int daysToEnd = 7 - cal.get(Calendar.DAY_OF_WEEK);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+daysToEnd);
        return sdf.format(cal.getTime());     
        
    }else if(select == next_week){
        int daysToEnd = 14 - cal.get(Calendar.DAY_OF_WEEK);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+daysToEnd);
        return sdf.format(cal.getTime());
        
    }else if(select == this_month){     
        int total = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH,total);
        return sdf.format(cal.getTime());
        
    }else if(select == next_month){
        int nextMonth = cal.get(Calendar.MONTH)+1; 
        cal.set(Calendar.MONTH, nextMonth);
        int total = cal.getActualMaximum(Calendar.DAY_OF_MONTH);     
        cal.set(Calendar.DAY_OF_MONTH, total);
        cal.set(Calendar.MONTH, nextMonth);
        return sdf.format(cal.getTime());      
    }

    return "";
  }
  
  public ListIterator<Activity> getUserActivitiesOrderFilters(UserInfoInterface userInfo, int anFlowId, FlowFilter filter) {
      String userid = userInfo.getUtilizador();
      Logger.trace(this, "getUserActivitiesOrderFilters", userid + " call.");
      String union="";
      int folderid = -1;
      String maxDeadline = "";
      
      // first, fix filter
      if (filter == null) {
        filter = new FlowFilter();
      }
      filter.setDateAfter(Utils.fixDateAfter(filter.getDateAfter()));
      filter.setDateBefore(Utils.fixDateBefore(filter.getDateBefore()));

      if(!filter.getDeadline().equals("0"))
        maxDeadline = getMaxDeadline(filter.getDeadline());
      
      Connection db = null;
      PreparedStatement st = null;
      ResultSet rs = null;
      LinkedList<Activity> l = new LinkedList<Activity>();
      ListIterator<Activity> result = null;

      try {
        db = DatabaseInterface.getConnection(userInfo);
        db.setAutoCommit(true);
        int nField = 1;
        // 1: userid

        StringBuilder sQuery = new StringBuilder(DBQueryManager.processQuery("ProcessManager.get_activity_filters_user", new Object[]{ userid }));
        
        if(!filter.getFolderid().equals("0"))
          sQuery.append(" and folderid="+filter.getFolderid());
        
         if(!filter.getLabelid().equals("0"))
             sQuery.append(" and a.pid in ( select pid from process_label where labelid="+filter.getLabelid()+") ");
        
         if(!maxDeadline.equals(""))
             sQuery.append(" and a.pid in ( select pid from deadline where deadline < '"+maxDeadline+"' AND deadline != '') ");
        
         StringBuilder sQueryDelegated = new StringBuilder(DBQueryManager.processQuery("ProcessManager.get_activity_filters_delegated", new Object[]{ userid }));

        // 2: anFlowId
        if (anFlowId > -1) {
          sQuery.append(" and a.flowid=?");
          sQueryDelegated.append(" and a.flowid=?");
        }
        // 3: adtAfter
        if (filter.getDateAfter() != null) {
          sQuery.append(" and a.created >= ?");
          sQueryDelegated.append(" and a.created >= ?");
        }
        // 4: adtBefore
        if (filter.getDateBefore() != null) {
          sQuery.append(" and a.created < ?");
          sQueryDelegated.append(" and a.created < ?");
        }
        // 5: pnumber
        if (StringUtils.isNotEmpty(filter.getPnumber())) {
          sQuery.append(" and upper(pnumber) like upper('%").append(escapeSQL(filter.getPnumber())).append("%')");
          sQueryDelegated.append(" and upper(pnumber) like upper('%").append(escapeSQL(filter.getPnumber())).append("%')");
        }

        if(filter.getOrderType() != null && filter.getOrderType().equals("desc")){
            sQueryDelegated.append(" order by iconid asc, created desc");
          //sQueryDelegated.append(" order by created desc");
        }else{
            sQueryDelegated.append(" order by iconid asc, created asc");
          //sQueryDelegated.append(" order by created asc");
        }
        
        
        //union = sQuery.toString()+" UNION "+sQueryDelegated.toString();
        union = "select * from (" + sQuery.toString()+")B order by created desc";
        
        st = db.prepareStatement(union);


        if (anFlowId > -1) {
          st.setInt(nField, anFlowId);
          ++nField;
        }
        if (filter.getDateAfter() != null) {
          st.setTimestamp(nField, new java.sql.Timestamp(filter.getDateAfter().getTime()));
          ++nField;
        }
        if (filter.getDateBefore() != null) {
          st.setTimestamp(nField, new java.sql.Timestamp(filter.getDateBefore().getTime()));
          ++nField;
        }

  
        int counter = -1;
  
        
        if (!filter.hasSizeLimit() || l.size() <= filter.getNumElements()) {
          // after getting the 'normal' activities, we get the 'delegated ones'
          //nField = 1;

          if (anFlowId > -1) {
            st.setInt(nField, anFlowId);
            ++nField;
          }
          if (filter.getDateAfter() != null) {
            st.setTimestamp(nField, new Timestamp(filter.getDateAfter().getTime()));
            ++nField;
          }
          if (filter.getDateBefore() != null) {
            st.setTimestamp(nField, new Timestamp(filter.getDateBefore().getTime()));
            ++nField;
          }
          
          rs = st.executeQuery();
          while (rs.next()) {
            int flowid = rs.getInt("flowid");
            if(filter.ignoreFlow(flowid)) {
              continue;
            }
            if(filter.isComment() && StringUtils.isBlank(BeanFactory.getProcessAnnotationManagerBean().getProcessComment(userInfo, rs.getInt("flowid"), rs.getInt("pid"), rs.getInt("subpid")).getComment())){
              continue;
            }
            if(filter.hasSizeLimit()) {
              counter++;
              if (counter - filter.getStartIndex() > filter.getNumElements()) {
                break;
              } else if (counter < filter.getStartIndex()) {
                continue;
              }
            }
            Activity wle = new Activity(userid, rs.getInt("flowid"), rs.getInt("pid"), rs.getInt("subpid"), rs.getInt("type"), 
                  rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), rs.getTimestamp("archived"), 
                  rs.getString("description"), rs.getString("url"), rs.getInt("status"), rs.getInt("notify"));
              wle.profilename = rs.getString("profilename");
              wle.pnumber = rs.getString("pnumber");
              if (rs.getInt("read_flag") == 1) {
                wle.setRead();
              }
              else {
                wle.setUnread();
              }
              wle.mid = rs.getInt("mid");
              
              if(rs.getInt("delegated") == 0)
                wle.delegated =  false;
              else
                wle.delegated =  true;  
              
              folderid = rs.getInt("folderid");
              if( folderid > 0)
                wle.setFolderid(folderid);

                    String icon = "";
                    try {
                      icon = rs.getString("icon");
                    } catch (Exception e) {
                      Logger.debug(userInfo.getUtilizador(), this, "getUserActivitiesOrderFilters", "No icon assign");
                    }
                    wle.setAnnotationIcon(icon);

                    //Detail
                    HashMap<String,String> detailItemMap = new HashMap<>();
                    for(int j=0; j<19; j++)             
                      detailItemMap.put(rs.getString("name_idx" + j), rs.getString("idx"+j));           
                    wle.setDetailItemList(detailItemMap);
          
              l.add(wle);
          }
        }
        result = l.listIterator();
      } catch (SQLException sqle) {
        Logger.error(userid, this, "getUserActivitiesOrderFilters", "sql exception: " + sqle.getMessage()+" QUERY:"+union, sqle);
        result = null;
      } catch (Exception e) {
        Logger.error(userid, this, "getUserActivitiesOrderFilters", "exception: " + e.getMessage(), e);
        result = null;
      } finally {
        DatabaseInterface.closeResources(db, st, rs);
      }
      return result;
    }
  
  public ListIterator<Activity> getUserAndSubordinatesActivities(UserInfoInterface userInfo) {
      String userid = userInfo.getUtilizador();
      Logger.trace(this, "getUserActivities", userid + " call.");

      Connection db = null;
      PreparedStatement st = null;
      ResultSet rs = null;
      LinkedList<Activity> l = new LinkedList<Activity>();
      ListIterator<Activity> result = null;

      String usersInUnitTxt = "( ";
      UserViewInterface[] usersInUnit = BeanFactory.getUserManagerBean().getAllUsers(userInfo, true);
      for (UserViewInterface ui : usersInUnit)
        usersInUnitTxt += "'" + ui.getUsername() + "',";
      usersInUnitTxt = StringUtils.chop(usersInUnitTxt) + " )";

      try {
        db = DatabaseInterface.getConnection(userInfo);
        db.setAutoCommit(true);
        int nField = 1;
        // 1: userid
        final String userFilter = "userid=?";
        final StringBuilder sQuery = new StringBuilder(
            "select a.*,p.pnumber from activity a,process p where p.flowid=a.flowid and p.pid=a.pid and p.subpid=a.subpid and status=0 and userid in ")
            .append(usersInUnitTxt);
        final StringBuilder sQueryDelegated = new StringBuilder(
            "select a.*,p.pnumber from activity_delegated a,process p where p.flowid=a.flowid and p.pid=a.pid and p.subpid=a.subpid and status=0 and  userid in ")
            .append(usersInUnitTxt);

        st = db.prepareStatement(sQuery.toString());

        rs = st.executeQuery();
        int counter = -1;
        while (rs.next()) {
          int flowid = rs.getInt("flowid");

          Activity wle = new Activity(userid, flowid, rs.getInt("pid"), rs.getInt("subpid"), rs.getInt("type"),
              rs.getInt("priority"), rs.getTimestamp("created"), rs.getTimestamp("started"), rs.getTimestamp("archived"), rs
                  .getString("description"), rs.getString("url"), rs.getInt("status"), rs.getInt("notify"));
          wle.profilename = rs.getString("profilename");
          wle.pnumber = rs.getString("pnumber");
          if (rs.getInt("read_flag") == 1) {
            wle.setRead();
          } else {
            wle.setUnread();
          }
          wle.mid = rs.getInt("mid");
          l.add(wle);
        }
        DatabaseInterface.closeResources(st, rs);
        st = null;
        rs = null;

        result = l.listIterator();
      } catch (SQLException sqle) {
        Logger.error(userid, this, "getUserActivities", "sql exception: " + sqle.getMessage(), sqle);
        result = null;
      } catch (Exception e) {
        Logger.error(userid, this, "getUserActivities", "exception: " + e.getMessage(), e);
        result = null;
      } finally {
        DatabaseInterface.closeResources(db, st, rs);
      }
      return result;
    }
}
