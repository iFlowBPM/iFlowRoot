package pt.iflow.flows;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.NamingException;
import javax.servlet.ServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.FolderManager;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.core.ReportManager;
import pt.iflow.api.db.DBConnectionWrapper;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.db.ExistingTransactionException;
import pt.iflow.api.events.EventManager;
import pt.iflow.api.flows.BlockInfo;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.licensing.LicenseService;
import pt.iflow.api.licensing.LicenseServiceFactory;
import pt.iflow.api.notification.NotificationManager;
import pt.iflow.api.processannotation.ProcessAnnotationManager;
import pt.iflow.api.processannotation.ProcessLabel;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iflow.api.transition.ProfilesTO;
import pt.iflow.api.transition.ReportTO;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.blocks.BlockForwardTo;
import pt.iknow.utils.StringUtilities;

/**
 * 
 * Flow related logic: (un)deployment, process evaluation, etc
 * 
 * @author mach
 * 
 */

public class FlowBean implements Flow {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final int nMODE_ADD = 0;
  private static final int nMODE_REMOVE = 1;
  private static final int nMODE_UPDATE = 2;

  private static FlowBean instance = null;

  public static FlowBean getInstance() {
    if (null == instance)
      instance = new FlowBean();
    return instance;
  }

  /**
   * This method advances to the next block when an event is fired
   * 
   * @param flowId
   * @param processId
   * @ejb.interface-method view-type = "remote"
   */
  public String eventNextBlock(UserInfoInterface userInfo, int flowId, int pid, int subpid) {
    ProcessData procData = this.getProcessData(userInfo, flowId, pid, subpid);
    long start = System.currentTimeMillis();
    String retObj = this.nextBlock(userInfo, procData, true, 0, false);
    long end = System.currentTimeMillis();
    Logger.trace("FlowBean", "eventNextBlock", "Block transition took " + (end - start) + " ms");
    return retObj;
  }

  /**
   * This method jumps to block bid
   * 
   * @param flowId
   * @param processId
   * @ejb.interface-method view-type = "remote"
   */
  public String jumpToBlock(UserInfoInterface userInfo, ProcessData procData, int jumpToBlockId) {
    long start = System.currentTimeMillis();
    String retObj = this.nextBlock(userInfo, procData, false, jumpToBlockId, false);
    long end = System.currentTimeMillis();
    Logger.trace("FlowBean", "jumpToBlock", "Block transition took " + (end - start) + " ms");
    return retObj;
  }

  /**
   * This method advances to the next block of the flow.
   * 
   * @param dataSet
   * @return String the next url.
   * @ejb.interface-method view-type = "remote"
   */
  public String nextBlock(UserInfoInterface userInfo, ProcessData procData) {
    long start = System.currentTimeMillis();
    String retObj = this.nextBlock(userInfo, procData, false, 0, false);
    long end = System.currentTimeMillis();
    Logger.trace("FlowBean", "nextBlock", "Block transition took " + (end - start) + " ms");
    return retObj;
  }

  public String nextBlock(UserInfoInterface userInfo, ProcessData procData, boolean useExistingTransaction) {
    long start = System.currentTimeMillis();
    String retObj = this.nextBlock(userInfo, procData, false, 0, useExistingTransaction);
    long end = System.currentTimeMillis();
    Logger.trace("FlowBean", "nextBlock", "Block transition took " + (end - start) + " ms");
    return retObj;
  }

  private String nextBlock(UserInfoInterface userInfo, ProcessData procData, boolean bEventFired, int jumpToBlockId,
      boolean useExistingTransaction) {

    String nextURL = null;
    LicenseService licenseService = LicenseServiceFactory.getLicenseService();
    int flowId = procData.getFlowId();
    int pid = procData.getPid();
    String login = userInfo.getUtilizador();

    Logger.trace(this, "nextBlock", login + " call with " + procData.getSignature());

    // check if user has write permissions to move process
    if (!this.checkUserFlowRoles(userInfo, flowId, "" + FlowRolesTO.WRITE_PRIV)) { // XXX AGON
      Logger.warning(login, this, "nextBlock", "User has no write privilege.. returning nopriv page");
      return "nopriv.jsp?flowid=" + flowId;
    }

    Logger.info(login, this, "nextBlock", procData.getSignature());

    ProcessManager pm = BeanFactory.getProcessManagerBean();
    Activity activity = null;
    Block block = null;
    boolean callNextBlock = false;
    boolean saveData = true;

    String saveFlowStateErrorKey = "flow_error.save_flow_state";

    Connection conn = null;
    String transactionId = null;

    try {

      if (useExistingTransaction && userInfo.inTransaction()) {
        conn = DatabaseInterface.getConnection(userInfo);
      } else {
        conn = Utils.getDataSource().getConnection();
        conn.setAutoCommit(false);
        transactionId = userInfo.registerTransaction(new DBConnectionWrapper(conn));
      }

      int mid = Const.NO_MID;

      do {

        if (procData.isInDB() && !procData.isOnPopup()) {
          mid = pm.getNextMid(userInfo, procData);
          procData.setMid(mid);

          Logger.debug(login, this, "nextBlock", procData.getSignature() + "MID: " + mid);
        }

        nextURL = null;
        saveData = true;

        int blockId = this.getFlowState(userInfo, procData);
        int blockIdOld = blockId;
        String subflowMapping = checkSubFlowMapping(procData.getFlowId(), blockId);

        Logger.info(login, this, "nextBlock",
 procData.getSignature()
            + "processing BlockId: "
            + blockId
 + subflowMapping);

        block = this.getBlockById(userInfo, procData.getProcessHeader(), blockId);
        Port outPort;

        procData.setOnPopup(block.isBlockRunningInPopup());

        if (block == null) {
          Logger.error(login, this, "nextBlock", procData.getSignature() + "block is null for blockId=" + blockId);
          break;
        }

        if (block.isStartBlock() && procData.isInDB()) {
          // process creation and dataset in db => force creator
          // activity creation

          activity = new Activity(login, flowId, pid, procData.getSubPid(), 0, 0, block.getDescription(userInfo, procData), Block
              .getDefaultUrl(userInfo, procData));
          activity.mid = procData.getMid();

          boolean createPriv = checkUserSelfFlowRoles(userInfo, flowId, "" + FlowRolesTO.CREATE_PRIV);
          pm.createActivity(userInfo, activity, createPriv);
          Logger.debug(login, this, "nextBlock", procData.getSignature() + "created activity for start block");
        }

        if (block.hasEvent() && !procData.isOnPopup()) {
          EventManager.get().deRegisterEvent(userInfo, flowId, pid, procData.getSubPid(), blockId);

          if (bEventFired) {
            // from now on, there is no more event.
            bEventFired = false;

            Logger.info(login, this, "nextBlock", procData.getSignature() + "event fired");

            outPort = block.getEventPort();
            if (outPort == null) {
              Logger.error(login, this, "nextBlock", procData.getSignature() + "eventPort is NULL for blockId=" + blockId
                  + "... aborting.");
              throw new Exception(procData.getSignature() + "eventPort is null for blockId " + block.getId());
            }

            // block hanling of event
            block.onEventFired(userInfo, procData);

            Logger.debug(login, this, "nextBlock", procData.getSignature() + "called block's onEventFired");

            Logger.logFlowState(userInfo, procData, block, "Event triggered, reverting out port to '" + outPort.getName()
                + "' and switching to block event;");

            // switch to block event
            Logger.debug(login, this, "nextBlock", procData.getSignature() + "switching from block " + blockId + " to event block "
                + outPort.getConnectedBlockId());

            blockId = outPort.getConnectedBlockId();
            block = this.getBlockById(userInfo, procData.getProcessHeader(), blockId);
          }
        }

        if (block.canProceed(userInfo, procData) == false) {
          Logger.info(login, this, "nextBlock", procData.getSignature() + "canProceed=FALSE for blockId=" + blockId);

          if (block.hasInteraction()) {
            // tell proc to stay in same page
            procData.setAppData(Const.STAY_IN_PAGE, "true");
          } else {
            nextURL = "blockmsg.jsp?flowid=" + flowId + "&pid=" + pid + "&subpid=" + procData.getSubPid() + "&pnumber="
                + procData.getPNumber();

            if (block.getCanProceedMsgCode(userInfo, procData) != null) {
              nextURL += "&msgcode=" + block.getCanProceedMsgCode(userInfo, procData).getCode();
            }
          }

          break;
        }

        procData.setAppData(Const.STAY_IN_PAGE, null);

        // Call the after method of the current block
        outPort = block.after(userInfo, procData);

        if (block instanceof pt.iflow.blocks.BlockSincronizacao) {
          procData = pm.getProcessData(userInfo, new ProcessHeader(procData.getFlowId(), procData.getPid(), procData.getSubPid()));
        }

        if (outPort == null) {
          Logger.error(login, this, "nextBlock", procData.getSignature() + "outPort is NULL for blockId=" + blockId
              + "... aborting.");
          throw new Exception(procData.getSignature() + "outport is null for blockId " + block.getId()
              + " after block's after call");
        }

        // Get the next blockId
        blockId = outPort.getConnectedBlockId();

        // nextBlock is a specific jumpTo block
        if (jumpToBlockId != 0) {
          blockId = jumpToBlockId;
          jumpToBlockId = 0; // reset jump to block id
        }

        // Save the flow state to store state result
        if (!saveFlowState(userInfo, procData, block, false, mid, outPort)) {
          Logger.error(login, this, "nextBlock", "Unable to SAVE after FLOW STATE (" + blockId + ") for proc "
              + procData.getFlowId() + "-" + procData.getPid() + "-" + procData.getSubPid());
          nextURL = getErrorUrl(saveFlowStateErrorKey);
          throw new Exception(procData.getSignature() + "unable to save flow state after block " + block.getId() + " after call");
        }

        Logger.info(login, this, "nextBlock", procData.getSignature() + "Going from: " + block.getId() + " to: "
            + outPort.getConnectedBlockId() + " (using " + outPort.getName() + ")");

        // Call the before method of the next block
        Block blockNext = getBlockById(userInfo, procData.getProcessHeader(), blockId);

        if (procData.isOnPopup() && !blockNext.canRunInPopupBlock()) {
          StringBuffer popupErrorLink = new StringBuffer();

          popupErrorLink.append("Form/closePopup.jsp?");
          popupErrorLink.append("flowid=").append(procData.getFlowId());
          popupErrorLink.append("&pid=").append(procData.getPid());
          popupErrorLink.append("&subpid=").append(procData.getSubPid());
          popupErrorLink.append("&cancelPopup=true");

          return popupErrorLink.toString();
        }

        if (blockNext != null) {
          // block by id returns a valid block..
          // set block var with it
          // otherwise, if next block is null/not valid,
          // block is not updated and therefore no changes are made
          block = blockNext;
          procData.setOnPopup(block.isBlockRunningInPopup());
        } else {
          // do not set block with blocknext (maintain old block object)
          // revert block id to old value
          blockId = blockIdOld;
        }

        // Join OR
        // se tem interaccao e vai morrer
        // devolve o url da pagina de erro e termina o subproc
        if (pm.checkBlockIsMined(userInfo, procData, blockIdOld, blockNext.getId())) {
          // close proc
          nextURL = endProc(userInfo, procData);
          nextURL += "&mined=true";

          saveData = false; // endProc handles it

          Logger.info(login, this, "nextBlock", procData.getSignature() + "block is mined");

          break;
        }

        if (block.isProcInDBRequired() && !procData.isInDB() && !procData.isOnPopup()) {

          Logger.info(login, this, "nextBlock", procData.getSignature() + "Going to prepare Proc in DB");

          if (pm.prepareProcInDB(userInfo, procData, block.isForwardBlock())) {
            pid = procData.getPid();
            mid = procData.getMid();

            if (!userInfo.isGuest()) {
              activity = new Activity(login, flowId, pid, procData.getSubPid(), 0, 0, block.getDescription(userInfo, procData),
                  Block.getDefaultUrl(userInfo, procData));
              activity.mid = procData.getMid();

              boolean createPriv = checkUserSelfFlowRoles(userInfo, flowId, "" + FlowRolesTO.CREATE_PRIV);
              pm.createActivity(userInfo, activity, createPriv);
            }
          } else {
            throw new Exception("Unable to create process data");
          }
        }

        // Register block events in DB
        if (block.hasEvent() && !procData.isOnPopup()) {

          // TODO novamente ver bem como eh
          Port portEvent = block.getEventPort();
          // Port[] eventOP = block.getOutPorts(userInfo);
          // Get portEvent, is the last port of all blocks
          // Port portEvent = eventOP[eventOP.length-1];
          if (portEvent != null) {
            Block blockEvent = this.getBlockById(userInfo, procData.getProcessHeader(), portEvent.getConnectedBlockId());
            if (!blockEvent.isDisabled(userInfo, procData)) {
              HashMap<String, String> attributes = blockEvent.getAttributeMap();

              for (int i = 0; i < attributes.size() / 2; i++) {
                String sType = attributes.get("dest" + i);
                if (sType == null)
                  break;
                String sProps = attributes.get("orig" + i);
                EventManager.get().registerEvent(userInfo, flowId, pid, procData.getSubPid(), blockId, sType, sProps);
              }
            }
          }
        }
        nextURL = block.before(userInfo, procData);
        // consume block
        licenseService.consume(userInfo, flowId, block.getCost());
        if (StringUtils.isNotEmpty(nextURL) && !block.hasInteraction() && !block.isEndBlock()) {
          // overwrite url to generic/forward
          if (Logger.isDebugEnabled()) {
            Logger.debug(login, this, "nextBlock", "pid=" + pid + ", subpid=" + procData.getSubPid() + ": overwriting NEXT URL: "
                + nextURL + " to default url");
          }
          nextURL = Block.getDefaultUrl(userInfo, procData);
        }

        if (Logger.isDebugEnabled()) {
          Logger.debug(login, this, "nextBlock", procData.getSignature() + "NEXT URL: " + nextURL + "; block desc: "
              + block.getDescription(userInfo, procData));
        }

        // Save the flow state
        if (!saveFlowState(userInfo, procData, block, true, mid, null)) {
          Logger.error(login, this, "nextBlock", procData.getSignature() + "Unable to SAVE before FLOW STATE (" + blockId + ")");
          nextURL = getErrorUrl(saveFlowStateErrorKey);
          throw new Exception(procData.getSignature() + "unable to save flow state after before in block " + block.getId());
        }

        if (procData.isInDB() && !procData.getCachedReports().isEmpty() && !procData.isOnPopup()) {
          Logger.debug(login, this, "nextBlock", "Storing cached reports in DB");
          ReportManager rm = BeanFactory.getReportManagerBean();
          for (ReportTO report : procData.getCachedReports().values()) {
            report.setPid(procData.getPid());
            report.setSubpid(procData.getSubPid());
            rm.storeReport(userInfo, procData, report);
          }
        } else if (block.isEndBlock()) {
          Logger.debug(login, this, "nextBlock", "Making sure all reports in DB have been closed");
          ReportManager rm = BeanFactory.getReportManagerBean();
          for (ReportTO report : rm.getProcessReports(userInfo, procData)) {
            boolean store = false;
            if (report.isActive()) {
              report.setActive(false);
              store = true;
            }
            if (report.getStopReporting() == null) {
              report.setStopReporting(new Timestamp(Calendar.getInstance().getTimeInMillis()));
              store = true;
            }
            if (store) {
              rm.storeReport(userInfo, procData, report);
            }
          }
        }

        callNextBlock = (blockId != blockIdOld) && !block.isEndBlock() && !block.hasInteraction();
        Logger.info(login, this, "nextBlock", procData.getSignature() + ": Block id=" + blockId + " next block's flag: "
            + callNextBlock);

        if (procData.isInDB() && !procData.isOnPopup()) {
          // check if requesting user can continue editing/accessing
          // process
          nextURL = pm.getUserProcessUrl(userInfo, procData, nextURL);

          if (nextURL == null) {
            if (callNextBlock) {
              // if here, block does not have interaction and user does
              // not have process access (no user process url), which means that we
              // must stop calling next block and show info page.
              Logger.info(login, this, "nextBlock", procData.getSignature() + ": Block id=" + blockId
                  + " user does not have process in his activities... setting next block's flag off.");
              callNextBlock = false;
            }

            String forwardBlockUpdateLabelParams = getFowardBlockUpdateLabelParams(userInfo, block, procData);

            // show info page
            nextURL = "proc_info.jsp?flowid=" + flowId + "&pid=" + pid + "&subpid=" + procData.getSubPid()
                + (block.isForwardBlock() ? "&from=forward" + forwardBlockUpdateLabelParams : "");
          }
        }

        if (!procData.isOnPopup()) {
          saveDataSet(userInfo, procData, null, mid);
        }
        saveData = false;

        Logger.debug(login, this, "nextBlock", procData.getSignature() + "going to commit iteration " + blockIdOld + "->" + blockId
            + " (mid: " + mid + ")");

        DatabaseInterface.commitConnection(conn);
        Logger.info(login, this, "nextBlock", procData.getSignature() + "db connection committed for iteration " + blockIdOld
            + "->" + blockId + " (mid: " + mid + ")");
      } while (callNextBlock);

      if (saveData) {
        saveDataSet(userInfo, procData, null, mid);
      }
      checkFlowEnd(userInfo, procData, block, false);

      DatabaseInterface.commitConnection(conn);
      Logger.info(login, this, "nextBlock", procData.getSignature() + "db connection committed (mid: " + mid + ")");

    } catch (ExistingTransactionException e) {
      Logger.error(login, this, "nextBlock", procData.getSignature() + "already in a transaction", e);
      nextURL = getErrorUrl("flow_error.existing_transaction");
    } catch (Throwable t) {
      Logger.error(login, this, "nextBlock", procData.getSignature() + "caught exception", t);
      if (conn != null) {
        try {
          DatabaseInterface.rollbackConnection(conn);
          Logger.warning(login, this, "nextBlock", procData.getSignature() + "db connection rollback");

        } catch (Exception ec) {
          Logger.error(login, this, "nextBlock", procData.getSignature() + "rolling back transaction", ec);
        }
      }
      nextURL = getErrorUrl("flow_error.generic_error");
    } finally {
      procData.setMid(Const.NO_MID);
      try {
        if (StringUtils.isNotEmpty(transactionId)) {
          try {
            userInfo.unregisterTransaction(transactionId);
          } catch (IllegalAccessException e) {
            Logger.error(login, this, "nextBlock", procData.getSignature() + "unregistering transaction in userinfo", e);

            NotificationManager notificationManager = BeanFactory.getNotificationManagerBean();
            notificationManager.notifySystemError(userInfo, "nextBlock@FlowBean", procData.getSignature()
                + "illegal access unregistering transaction in userInfo! " + e.getMessage());

            nextURL = getErrorUrl("flow_error.unregister_transaction");
          }
        }
      } finally {
        // DatabaseInterface.closeResources(conn);
      	try { conn.close(); } catch (Exception e) {}

      }
    }
    // Update Folder
    getFowardBlockUpdateFolderParams(userInfo, block, procData);
    return nextURL;
  }

  /**
   * 
   * @param flowid
   *          Main flowid
   * @param blockid
   *          id of a block that may belong to a subflow
   * @return {subflow name, blockid in subflow}, null if the blockid is not in a subflow
   */
  private String checkSubFlowMapping(Integer flowid, Integer blockid) {
    String[] ret = new String[0]; // Deixou de ser null
    String result = "";
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    try {
      db = Utils.getDataSource().getConnection();
      pst = db
          .prepareStatement("SELECT sub_flowname, original_blockid FROM iflow.subflow_block_mapping s, iflow.flow f where f.flowfile=s.flowname and flowid=? and mapped_blockid=? order by id desc");
      pst.setInt(1, flowid);
      pst.setInt(2, blockid);
      rs = pst.executeQuery();

      if (rs.next()) {
        ret = new String[4];
        ret[0] = rs.getString(1);
        ret[1] = "" + rs.getInt(2);

        pst.setInt(1, flowid);
        pst.setInt(2, Integer.parseInt(ret[0].substring(ret[0].lastIndexOf("_") + 1)));
        rs = pst.executeQuery();

        if (rs.next()) {
          ret[2] = rs.getString(1);
          ret[3] = "" + rs.getInt(2);
        }
      }

      if (StringUtils.isNotBlank(ret[2]))
        result = ", original id=" + ret[1] + " of subflow=" + ret[0].substring(0, ret[0].lastIndexOf("_"))
            + " placed in mainflow at block=" + ret[3] + " of subflow=" + ret[2].substring(0, ret[2].lastIndexOf("_"))
            + " placed in mainflow at block=" + ret[2].substring(ret[2].lastIndexOf("_") + 1);
      else
        result = ", original id=" + ret[1] + " of subflow=" + ret[0].substring(0, ret[0].lastIndexOf("_"))
            + " placed in mainflow at block=" + ret[0].substring(ret[0].lastIndexOf("_") + 1);

    } catch (Exception e) {
      result = "";
    } finally {
      //DatabaseInterface.closeResources(db, pst);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (pst != null) pst.close(); } catch (SQLException e) {}
    	try {rs.close(); } catch (Exception e) {}    	
    }

    return result;
  }

  private String getFowardBlockUpdateLabelParams(UserInfoInterface userInfo, Block block, ProcessData procData) {
    String forwardBlockUpdateLabelParams = "";
    if (block.isForwardBlock()) {
      String updateTaskAnnotationLabelCond = block.getAttribute(BlockForwardTo.sFORWARD_TO_UPDATE_LABEL_COND);
      String updateTaskAnnotationLabel = block.getAttribute(BlockForwardTo.sFORWARD_TO_UPDATE_LABEL);

      boolean isUpdateLabel = false;
      if (StringUtilities.isEmpty(updateTaskAnnotationLabelCond) || StringUtilities.isEmpty(updateTaskAnnotationLabel)) {
        Logger.warning(userInfo.getUtilizador(), this, "before", "Missing update condition or label, assuming false");
        isUpdateLabel = false;
      }
      try {
        isUpdateLabel = procData.query(userInfo, updateTaskAnnotationLabelCond);
      } catch (EvalException e) {
        Logger.warning(userInfo.getUtilizador(), this, "getFowardBlockUpdateLabelParams",
            "Unable to process update label condition, assuming false", e);
        isUpdateLabel = false;
      }
      if (isUpdateLabel) {
        ProcessAnnotationManager processAnnotationManager = BeanFactory.getProcessAnnotationManagerBean();
        List<ProcessLabel> dbLabels = processAnnotationManager.getLabelList(userInfo);

        for (ProcessLabel label : dbLabels) {
          String dbLabelKey = label.getName() + " - " + label.getDescription();
          if (updateTaskAnnotationLabel.equals(dbLabelKey)) {
            forwardBlockUpdateLabelParams = "&labelid=" + label.getId() + "&labelname=" + label.getName();
          }
        }
      }
    }
    return forwardBlockUpdateLabelParams;
  }

  private void getFowardBlockUpdateFolderParams(UserInfoInterface userInfo, Block block, ProcessData procData) {

    if (block.isForwardBlock()) {
      String updateTaskFolderCond = block.getAttribute(BlockForwardTo.sFORWARD_TO_UPDATE_FOLDER_COND);
      String updateTaskFolder = block.getAttribute(BlockForwardTo.sFORWARD_TO_UPDATE_FOLDER);

      boolean isUpdateFolder = false;
      if (StringUtilities.isEmpty(updateTaskFolderCond) || StringUtilities.isEmpty(updateTaskFolder)) {
        Logger.warning(userInfo.getUtilizador(), this, "before", "Missing update condition or Category, assuming false");
        isUpdateFolder = false;
      }
      try {
        isUpdateFolder = procData.query(userInfo, updateTaskFolderCond);
      } catch (EvalException e) {
        Logger.warning(userInfo.getUtilizador(), this, "getFowardBlockUpdateFolderParams",
            "Unable to process update folder condition, assuming false", e);
        isUpdateFolder = false;
      }
      if (isUpdateFolder) {
        FolderManager fm = BeanFactory.getFolderManagerBean();
        fm.setActivityToFolderByName(userInfo, updateTaskFolder, procData.getFlowId(), procData.getPid(), procData.getSubPid());
      }
    }
  }

  private String getErrorUrl(String msgKey) {
    return "flow_error.jsp?msg_key=" + (StringUtils.isEmpty(msgKey) ? "" : msgKey);
  }

  public void storeProcess(UserInfoInterface userInfo, ProcessData procData) throws Exception {
    storeProcess(userInfo, procData, true);
  }

  public void storeProcess(UserInfoInterface userInfo, ProcessData procData, boolean markActivityHasRead) throws Exception {

    if (procData.isInDB())
      return;

    String login = userInfo.getUtilizador();
    Logger.info(login, this, "storeProcess", procData.getSignature() + "Going to prepare Proc in DB");

    ProcessManager pm = BeanFactory.getProcessManagerBean();
    Block block = this.getBlock(userInfo, procData);

    if (pm.prepareProcInDB(userInfo, procData, block.isForwardBlock())) {
      int pid = procData.getPid();
      int mid = procData.getMid();

      if (!saveFlowState(userInfo, procData, block, true, mid, null)) {
        Logger.error(userInfo.getUtilizador(), this, "storeProcess", procData.getSignature() + "Unable to SAVE before FLOW STATE "
            + block.getId());
        throw new Exception("Unable to save flow state");
      }

      if (!userInfo.isGuest()) {
        Activity activity = new Activity(login, procData.getFlowId(), pid, procData.getSubPid(), 0, 0, block.getDescription(
            userInfo, procData), Block.getDefaultUrl(userInfo, procData));
        activity.mid = procData.getMid();

        boolean createPriv = checkUserSelfFlowRoles(userInfo, procData.getFlowId(), "" + FlowRolesTO.CREATE_PRIV);
        pm.createActivity(userInfo, activity, createPriv);

        if (!markActivityHasRead) {
          Connection conn = null;
          PreparedStatement pst = null;
          try {
            conn = DatabaseInterface.getConnection(userInfo);
            pst = conn.prepareStatement("update activity set read_flag=0 where flowid=?"
                + " and pid=? and subpid=? and read_flag=1 and userid=?");
            pst.setInt(1, activity.flowid);
            pst.setInt(2, activity.pid);
            pst.setInt(3, activity.subpid);
            pst.setString(4, activity.userid);

            pst.executeUpdate();

          } catch (Exception ee) {
            Logger.warning(userInfo.getUtilizador(), this, "storeProcess", procData.getSignature()
                + "ERROR marking activity as unread", ee);
          } finally {
            // DatabaseInterface.closeResources(conn, pst);
          	try { conn.close(); } catch (Exception e) {}
          	try {if (pst != null) pst.close(); } catch (SQLException e) {}
          }
        }

      }
    } else {
      throw new Exception("Unable to create process data");
    }
  }

  /**
   * Saves the dataSet to the database. Calls the saveDataset method of the current block.
   * 
   * @param dataSet
   * @param session
   * @return mid
   * @ejb.interface-method view-type = "remote"
   */
  public int saveDataSet(UserInfoInterface userInfo, ProcessData procData, ServletRequest request) {
    return saveDataSet(userInfo, procData, request, Const.NO_MID);
  }

  private int saveDataSet(UserInfoInterface userInfo, ProcessData procData, ServletRequest request, int mid) {

    String requestMid = String.valueOf(mid);
    if (null != request) {
      requestMid = (String) request.getAttribute(Const.sMID_ATTRIBUTE);
    }

    ProcessManager pm = BeanFactory.getProcessManagerBean();
    Block block = null;

    if (!procData.isInDB()) {
      if (request != null) {
        try {

          block = this.getBlock(userInfo, procData);

          if (pm.prepareProcInDB(userInfo, procData)) {

            if (mid == Const.NO_MID) {
              mid = procData.getMid();
              requestMid = String.valueOf(mid);
            }

            if (!saveFlowState(userInfo, procData, block, true, mid, null)) {
              Logger.error(userInfo.getUtilizador(), this, "saveDataSet", "Unable to SAVE before FLOW STATE (" + block.getId()
                  + ") for proc " + procData.getFlowId() + "-" + procData.getPid() + "-" + procData.getSubPid());
            }

          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        return 0;
      }
    }

    int retObj = -1;

    int flowId = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    Logger.trace(this, "saveDataSet", login + " call " + procData.getSignature());

    try {
      Activity activity = null;
      activity = pm.getUserProcessActivity(userInfo, procData.getProcessHeader());

      if (activity != null || request != null) {
        // user has process scheduled on him or
        // user made explicit request to save process data:
        // mark saved flag in process data
        procData.set(DataSetVariables.PROCESS_SAVED, "1");
      }

      if (request != null) {
        // this means that saving request was made by end-user (jsp)
        int curMid = pm.getModificationId(userInfo, procData.getProcessHeader());
        if (StringUtils.isNotEmpty(requestMid) && !StringUtils.equals(String.valueOf(curMid), requestMid)) {
          retObj = Const.ERROR_PROCESS_CHANGED;
        } else {
          retObj = pm.modifyProcessData(userInfo, procData);
        }

        if (activity == null) {
          // user does not have this process in his activity list:
          // schedule this process on him.

          // first get block to be able to get description and url
          block = this.getBlock(userInfo, procData);
          if (block != null) {
            if (Logger.isDebugEnabled()) {
              Logger.debug(login, this, "saveDataSet", "Creating activity for user for subpid=" + subpid + ",pid " + pid
                  + ", desc=" + block.getDescription(userInfo, procData) + ", url=" + Block.getDefaultUrl(userInfo, procData));
            }

            activity = new Activity(login, flowId, pid, subpid, 0, 0, block.getDescription(userInfo, procData), Block
                .getDefaultUrl(userInfo, procData));
            activity.mid = procData.getMid();

            boolean createPriv = checkUserSelfFlowRoles(userInfo, flowId, "" + FlowRolesTO.CREATE_PRIV);
            pm.createActivity(userInfo, activity, createPriv);
          }
        }
      } else {
        retObj = pm.modifyProcessData(userInfo, procData);
      }
    } catch (Exception e) {
      Logger.error(login, this, "saveDataSet", procData.getSignature() + "caught exception: ", e);
      retObj = -1;
    }

    if (retObj < 0) {
      Logger.warning(login, this, "saveDataSet", procData.getSignature() + "Not able to save DataSet");
    } else if (retObj == 0) {
      Logger.warning(login, this, "saveDataSet", procData.getSignature() + "No changes in DataSet");
    } else {
      Logger.debug(login, this, "saveDataSet", procData.getSignature() + "DataSet saved");
    }

    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.flows.Flow#getPossibleUndoStates(pt.iknow.utils.UserInfoInterface, pt.iknow.iflow.ProcessData)
   */
  public List<FlowStateHistoryTO> getPossibleUndoStates(UserInfoInterface userInfo, ProcessData procData) {
    String userid = userInfo.getUtilizador();

    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    List<FlowStateHistoryTO> retObj = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      // get fork states
      HashMap<Integer, Integer> forkStates = new HashMap<Integer, Integer>();
      st = db.prepareStatement("select state,mid,count(*) from flow_state_history "
          + "where flowid=? and pid=? and undoflag=0 and exit_flag=1 " + "group by state,mid having count(*) > 1");
      st.setInt(1, procData.getFlowId());
      st.setInt(2, procData.getPid());
      rs = st.executeQuery();
      while (rs.next()) {
        int forkstate = rs.getInt("state");
        int forkmid = rs.getInt("mid");
        forkStates.put(forkstate, forkmid);
      }
      DatabaseInterface.closeResources(rs, st);

      st = db.prepareStatement("select f.*,m.muser from flow_state_history f, modification m"
          + " where f.flowid=? and f.pid=? and f.subpid=? and f.undoflag=0"
          + " and m.flowid=f.flowid and m.pid=f.pid and m.subpid=f.subpid and m.mid=f.mid");

      st.setInt(1, procData.getFlowId());
      st.setInt(2, procData.getPid());
      st.setInt(3, procData.getSubPid());

      rs = st.executeQuery();

      retObj = new ArrayList<FlowStateHistoryTO>();
      int minmid = -1;
      while (rs.next()) {

        FlowStateHistoryTO item = new FlowStateHistoryTO(rs);
        item.setModificationUser(rs.getString("muser"));

        if (forkStates.containsKey(item.getState())) {
          int forkmid = forkStates.get(item.getState());
          if (forkmid > minmid)
            minmid = forkmid;
        }

        Block block = this.getBlockById(userInfo, procData.getProcessHeader(), item.getState().intValue());
        if (block != null && block.hasInteraction()) {
          retObj.add(item);
        }
      }

      for (int i = 0; i < retObj.size(); i++) {
        FlowStateHistoryTO item = retObj.get(i);
        item.setUndoable(item.getMid() >= minmid);
      }

    } catch (Exception e) {
      Logger.error(userid, this, "getPossibleUndoStates", procData.getSignature() + "Exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iknow.flows.Flow#undoProcess(pt.iknow.utils.UserInfoInterface, int, int, int, int, int, int)
   */
  public boolean undoProcess(UserInfoInterface userInfo, int flowid, int pid, int subpid, int flowState, int mid, int exit_flag) {
    return undoProcess(userInfo, flowid, pid, subpid, flowState, mid, exit_flag, true);
  }

  public boolean undoProcess(UserInfoInterface userInfo, int flowid, int pid, int subpid, int flowState, int mid, int exit_flag,
      boolean registerTransaction) {
    String userid = userInfo.getUtilizador();
    ProcessManager pm = BeanFactory.getProcessManagerBean();

    Connection db = null;
    Statement st = null;
    PreparedStatement updateStatement = null;
    ResultSet rs = null;
    boolean retObj = false;
    String transactionId = null;

    try {

      db = Utils.getDataSource().getConnection();
      db.setAutoCommit(false);
      if (registerTransaction) {
        transactionId = userInfo.registerTransaction(new DBConnectionWrapper(db));
      }

      st = db.createStatement();

      StringBuffer query = new StringBuffer();
      query.append("SELECT " + FlowStateHistoryTO.STATE);
      query.append(" FROM " + FlowStateHistoryTO.TABLE_NAME);
      query.append(" WHERE " + FlowStateHistoryTO.FLOW_ID + "=" + flowid);
      query.append(" AND " + FlowStateHistoryTO.PID + "=" + pid);
      query.append(" AND " + FlowStateHistoryTO.SUBPID + "=" + subpid);
      query.append(" AND " + FlowStateHistoryTO.STATE + "=" + flowState);
      query.append(" AND " + FlowStateHistoryTO.MID + "=" + mid);
      query.append(" AND " + FlowStateHistoryTO.EXIT_FLAG + "=" + exit_flag);
      query.append(" AND " + FlowStateHistoryTO.UNDO_FLAG + "=0");

      rs = st.executeQuery(query.toString());
      if (!rs.next()) {
        throw new Exception("Unable to undo to state " + flowState + ": State does not exist for mid " + mid + "!");
      }
      rs.close();
      rs = null;

      updateStatement = db
          .prepareStatement("update flow_state_history set undoflag=1 where flowid=? and pid=? and subpid=? and mid > ?");
      updateStatement.setInt(1, flowid);
      updateStatement.setInt(2, pid);
      updateStatement.setInt(3, subpid);
      updateStatement.setInt(4, mid);
      updateStatement.executeUpdate();

      String sql = DBQueryManager.processQuery("Flow.undo_get_state_result", new Object[] { String.valueOf(flowid),
          String.valueOf(pid), String.valueOf(subpid), String.valueOf(flowState), String.valueOf(mid) });

      rs = st.executeQuery(sql);
      if (rs.next()) {
        // update flow_state set result={4}, mdate=NOW(), state={3} where flowid={0} and pid={1} and subpid={2}
        sql = DBQueryManager.processQuery("Flow.update_state_undo", new Object[] { String.valueOf(flowid), String.valueOf(pid),
            String.valueOf(subpid), String.valueOf(flowState), String.valueOf(rs.getString(1)), String.valueOf(mid) });

        st.executeUpdate(sql);
      }
      rs.close();
      rs = null;

      ProcessHeader oldProcHeader = new ProcessHeader(flowid, pid, subpid);
      ProcessData oldProcData = pm.getProcessData(userInfo, oldProcHeader);

      // TODO
      // FIXME
      // actualizar para os useres associados ao mid, quando for feita a alteracao as actividades
      // para suportarem tambem os mids
      ProcessData newProcData = pm.undoProcessData(userInfo, flowid, pid, subpid, mid, oldProcData);

      if (pm.undoProcessActivities(userInfo, newProcData, mid, flowState)) {
        pm.notifyProcessUndone(userInfo, newProcData);
      }

      Logger.debug(userid, this, "undoProcess", newProcData.getSignature() + "commiting changes...");
      db.commit();
      Logger.debug(userid, this, "undoProcess", newProcData.getSignature()
          + "...done commiting changes. Process reverted to flowstate " + flowState + " (mid " + mid + ")");

      retObj = true;
    } catch (Exception e) {
      retObj = false;
      Logger.error(userid, this, "undoProcess", "Exception: " + e.getMessage(), e);
      try {
        if (db!=null) db.rollback();
      } catch (SQLException ee) {
        Logger.error(userid, this, "undoProcess", "Exception rolling back: " + e.getMessage(), e);
      }
    } finally {
      try {
        if (registerTransaction) {
          if (StringUtils.isNotEmpty(transactionId)) {
            try {
              userInfo.unregisterTransaction(transactionId);
            } catch (IllegalAccessException e) {
              Logger.error(userInfo.getUtilizador(), this, "undoProcess", "unregistering transaction in userinfo", e);

              NotificationManager notificationManager = BeanFactory.getNotificationManagerBean();
              notificationManager.notifySystemError(userInfo, "undoProcess@FlowBean",
                  "illegal access unregistering transaction in userInfo! " + e.getMessage());
            }
          }
        }
      } finally {
        // DatabaseInterface.closeResources(db, st, updateStatement, rs);
      	try { db.close(); } catch (Exception e) {}
      	try {if (st != null) st.close(); } catch (SQLException e) {}
      	try {if (updateStatement != null) updateStatement.close(); } catch (SQLException e) {}
      	try {if (rs != null) rs.close(); } catch (SQLException e) {}    	

      }
    }

    return retObj;
  }

  public ProcessData getProcessData(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();
      return pm.getProcessData(userInfo, new ProcessHeader(flowid, pid, subpid));
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getProcessData", "Caught an unexpected exception: " + e.getMessage(), e);
      return null;
    }
  }

  /**
   * Gets this flow's current state block
   * 
   * @return current state block or null if an error occurs
   * @ejb.interface-method view-type = "remote"
   */
  public Block getBlock(UserInfoInterface userInfo, ProcessData procData) {

    Block retObj = null;

    String login = userInfo.getUtilizador();

    try {
      int nBlockID = this.getFlowState(userInfo, procData);
      retObj = this.getBlockById(userInfo, procData.getProcessHeader(), nBlockID);
    } catch (Exception e) {
      Logger.error(login, this, "getBlock", procData.getSignature() + "exception caught: " + e.getMessage(), e);
    }

    return retObj;
  }

  /**
   * Gets the flow state from the database.
   * 
   * @param flowId
   * @param prefix
   * @param pid
   * @return the flow state (the current block id) or -1 if the state does not exist.
   * @throws NamingException
   */
  private int getFlowState(UserInfoInterface userInfo, ProcessData procData) throws Exception {

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    int flowState = -1;

    int flowId = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    if (!procData.isInDB()) {
      if (StringUtils.isEmpty(procData.getTempData(DataSetVariables.FLOW_STATE))) {
        flowState = getStartBlockId(userInfo, procData);
      } else {
        flowState = Integer.parseInt(procData.getTempData(DataSetVariables.FLOW_STATE));
      }
    } else {
      try {

        if (pid <= 0) {
          // shouldn't be here
          return flowState;
        }

        db = DatabaseInterface.getConnection(userInfo);
        st = db.createStatement();

        rs = st.executeQuery("select state from flow_state" + " where flowid=" + flowId + " and pid=" + pid + " and subpid="
            + subpid + " and closed=0");

        if (rs.next()) {
          flowState = rs.getInt("state");
        } else {
          if (StringUtils.isEmpty(procData.getTempData(DataSetVariables.FLOW_STATE))) {
            // check if process already existed
            rs.close();
            rs = null;
            rs = st.executeQuery("select state from flow_state_history where flowid=" + flowId + " and pid=" + pid + " and subpid="
                + subpid);
            if (rs.next()) {
              // strange!! shouldn't be here. don't set flowstate to start state
              Logger.error(login, this, "getFlowState", procData.getSignature()
                  + "process does not exist in flow state but exists in flow state history.");
              flowState = getStartBlockId(userInfo, procData);
              Logger.warning(login, this, "getFlowState", procData.getSignature() + "TESTING - returning flowstate =" + flowState);
            } else {
              flowState = getStartBlockId(userInfo, procData);
            }
            rs.close();
            rs = null;
          } else {
            // switching from session dataset to db dataset...
            // get state from dataset's temp data
            flowState = Integer.parseInt(procData.getTempData(DataSetVariables.FLOW_STATE));
          }
        }
        if (rs != null) {
          rs.close();
          rs = null;
        }
      } catch (SQLException sqle) {
        Logger.error(login, this, "getFlowState", procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
        throw sqle;
      } catch (Exception e) {
        Logger.error(login, this, "getFlowState", procData.getSignature() + "caught exception: " + e.getMessage(), e);
        throw e;
      } finally {
        DatabaseInterface.closeResources(db, st, rs);
      }
    }
    return flowState;
  }

  /**
   * Gets a block given its Id.
   * 
   * @param blockId
   *          the block's Id.
   * @return the wanted block or null if the block wasn't found.
   */
  public Block getBlockById(UserInfoInterface userInfo, ProcessHeader procHeader, int blockId) {
    Block block = null;

    if (blockId < 0) {
      return null;
    }

    IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, procHeader.getFlowId());
    Vector<Block> flowVector = null;

    if (fd != null && !fd.hasError()) {
      flowVector = fd.getFlow();
    }

    if (flowVector != null) {
      // Find the required block
      for (int index = 0; index < flowVector.size(); index++) {
        block = flowVector.get(index);
        if (block.getId() == blockId) {
          break;
        }
        block = null;
      }
    }

    return block;
  }

  /**
   * Returns the id of the BlockStart
   * 
   * @return the BlockStart id
   */
  private int getStartBlockId(UserInfoInterface userInfo, ProcessData procData) {
    int retObj = -1;

    int flowId = procData.getFlowId();

    IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, flowId);
    Vector<Block> flowVector = null;
    Block block = null;

    if (fd != null && !fd.hasError()) {
      flowVector = fd.getFlow();
    }

    if (flowVector != null) {
      for (int index = 0; index < flowVector.size(); index++) {
        block = flowVector.get(index);
        if (block.isStartBlock()) {
          retObj = block.getId();
          break;
        }
      }
    }

    return retObj;
  }

  /**
   * Stores the flow state to the database.
   * 
   * @param flowId
   * @param prefix
   * @param pid
   * @param block
   *          the current block
   * @param abBefore
   *          flag that tells that state is saved after calling block's before method; otherwise state is saved after calling
   *          block's after method.
   * @return boolean
   * @throws NamingException
   */
  private boolean saveFlowState(UserInfoInterface userInfo, ProcessData procData, Block block, boolean abBefore, int mid,
      Port outPort) throws NamingException {

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    String sResult = "";
    int nExitFlag = 0;
    if (abBefore) {
      sResult = block.getDescription(userInfo, procData);
      nExitFlag = 0;
    } else {
      sResult = block.getResult(userInfo, procData);
      nExitFlag = 1;
    }

    Logger.debug(login, this, "saveFlowState", procData.getSignature() + "SAVING FLOW STATE: BLOCKID: " + block.getId());

    if (!procData.isInDB() || procData.isOnPopup()) {
      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "saveFlowState", "Process is not in DB, no persistence performed.");
      }
      procData.setTempData(DataSetVariables.FLOW_STATE, String.valueOf(block.getId()));
      procData.setTempData(DataSetVariables.FLOW_STATE_RESULT, sResult);
    } else {
      try {
        db = DatabaseInterface.getConnection(userInfo);
        db.setAutoCommit(false);
        st = db.createStatement();

        rs = st.executeQuery("select state,result,exit_flag from flow_state" + " where flowid=" + flowid + " and pid=" + pid
            + " and subpid=" + subpid + " and closed=0");
        if (rs.next()) {
          int currState = rs.getInt("state");
          String currResult = rs.getString("result");
          int currExitFlag = rs.getInt("exit_flag");

          String query = DBQueryManager
              .processQuery("Flow.update", new Object[] { String.valueOf(block.getId()), sResult, String.valueOf(nExitFlag),
                  String.valueOf(flowid), String.valueOf(pid), String.valueOf(subpid), String.valueOf(mid) });
          st.executeUpdate(query);

          if (block.getId() != currState || !StringUtils.equals(sResult, currResult) || nExitFlag != currExitFlag) {

            // insert history entry
            if (block.isSaveFlowState() && Const.sSAVE_FLOW_STATE_ALLWAYS.equals("true")) {
              query = DBQueryManager.processQuery("Flow.insert_state_history", new Object[] { String.valueOf(flowid),
                  String.valueOf(pid), String.valueOf(subpid), String.valueOf(block.getId()), sResult, String.valueOf(nExitFlag),
                  String.valueOf(mid), outPort == null ? null : "'" + outPort.getName() + "'" });
              st.executeUpdate(query);
            }
          }

          DatabaseInterface.commitConnection(db);
        } else { // there's no blockId.

          String query = DBQueryManager.processQuery("Flow.insert_state", new Object[] { String.valueOf(flowid),
              String.valueOf(pid), String.valueOf(subpid), String.valueOf(block.getId()), sResult, String.valueOf(nExitFlag),
              String.valueOf(mid) });
          st.executeUpdate(query);

          if (block.isSaveFlowState() && Const.sSAVE_FLOW_STATE_ALLWAYS.equals("true")) {
            query = DBQueryManager.processQuery("Flow.insert_state_history", new Object[] { String.valueOf(flowid),
                String.valueOf(pid), String.valueOf(subpid), String.valueOf(block.getId()), sResult, String.valueOf(nExitFlag),
                String.valueOf(mid), outPort == null ? null : "'" + outPort.getName() + "'" });
            st.executeUpdate(query);
          }

          DatabaseInterface.commitConnection(db);
        }
        rs.close();
        rs = null;
      } catch (Exception e) {
        Logger.error(login, this, "saveFlowState", procData.getSignature() + "caught exception: " + e.getMessage(), e);
        try {
          DatabaseInterface.rollbackConnection(db);
        } catch (Exception er) {
          Logger.error(login, this, "saveFlowState", procData.getSignature() + "exception rolling back connection: "
              + er.getMessage(), er);
        }
        return false;
      } finally {
        DatabaseInterface.closeResources(db, st, rs);
      }

      procData.setTempData(DataSetVariables.FLOW_STATE, null);
      procData.setTempData(DataSetVariables.FLOW_STATE_RESULT, null);
    }

    return true;
  }

  /**
   * Checks if a flow as reached the end. If so, closes it.
   * 
   * @param flowId
   * @param procData
   * @param block
   *          - the current block
   * 
   * @throws Exception
   */
  public void checkFlowEnd(UserInfoInterface userInfo, ProcessData procData, Block block) throws Exception {
    checkFlowEnd(userInfo, procData, block, false, false);
  }

  private void checkFlowEnd(UserInfoInterface userInfo, ProcessData procData, Block block, boolean abForce) throws Exception {
    checkFlowEnd(userInfo, procData, block, abForce, false);
  }

  private void checkFlowEnd(UserInfoInterface userInfo, ProcessData procData, Block block, boolean abForce, boolean isCancel)
      throws Exception {

    if (!procData.isInDB()) {
      return;
    }

    String login = userInfo.getUtilizador();

    int blockid = -1;
    if (block != null)
      blockid = block.getId();

    Logger.debug(login, this, "checkFlowEnd", procData.getSignature() + "BLOCKID: " + blockid);

    if ((block != null && block.isEndBlock()) || abForce) {

      String transactionId = null;
      Connection conn = null;
      
      
      try {
    	  //jcosta: 20180714: moved if inside try to close conn in finally
	      if (!userInfo.inTransaction()) {
	        conn = Utils.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        transactionId = userInfo.registerTransaction(new DBConnectionWrapper(conn));
	      }

        endFlow(userInfo, procData, isCancel);

        if (conn != null) {
          conn.commit();
          Logger.debug(login, this, "checkFlowEnd", procData.getSignature() + "...done commiting changes.");
        }
      } catch (Exception e) {
        Logger.error(login, this, "checkFlowEnd", "Exception: " + e.getMessage(), e);
        if (conn != null) {
          try {
            conn.rollback();
          } catch (SQLException ee) {
            Logger.error(login, this, "checkFlowEnd", "Exception rolling back: " + e.getMessage(), e);
          }
        }
        throw e;
      } finally {
        if (StringUtils.isNotEmpty(transactionId)) {
          try {
            userInfo.unregisterTransaction(transactionId);
          } catch (IllegalAccessException e) {
            Logger.error(login, this, "checkFlowEnd", "unregistering transaction in userinfo", e);

            NotificationManager notificationManager = BeanFactory.getNotificationManagerBean();
            notificationManager.notifySystemError(userInfo, "checkFlowEnd@FlowBean",
                "illegal access unregistering transaction in userInfo! " + e.getMessage());
          } finally {
            //DatabaseInterface.closeResources(conn);
          		try {conn.close(); } catch (Exception e) {}
          }
        }
      }
    }
  }

  private synchronized void endFlow(UserInfoInterface userInfo, ProcessData procData, boolean isCancel) throws Exception {
    int flowId = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    Connection db = null;
    Statement st = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);

      Logger.info(login, this, "endFlow", "Reached flow end. Deleting flow_state associated entry");

      ProcessManager pm = BeanFactory.getProcessManagerBean();
      pm.endProc(userInfo, procData, isCancel);

      Logger.info(login, this, "endFlow", procData.getSignature() + "Deleting flow_state associated entry");

      st = db.createStatement();

      String sQuery = "update flow_state set closed=1";
      if (isCancel) {
        sQuery += ",canceled=1";
      }
      sQuery += " where flowid=" + flowId;
      sQuery += " and pid=" + pid;
      sQuery += " and subpid=" + subpid;
      st.executeUpdate(sQuery);

      Logger.info(login, this, "saveFlowState", procData.getSignature() + "process reached flow end");
    } catch (Exception sqle) {
      Logger.error(login, this, "endFlow", procData.getSignature() + "Caught exception: " + sqle.getMessage(), sqle);
      throw sqle;
    } finally {
      //DatabaseInterface.closeResources(db, st);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    }
  }

  /**
   * Returns the id of the given Block
   * 
   * @param asBlockName
   *          the block's name (without package, only name)
   * @return the Block's id
   * @ejb.interface-method view-type = "remote"
   */
  public int getBlockId(UserInfoInterface userInfo, ProcessData procData, String asBlockName) {
    Block block = null;
    int blockId = -1;
    String sFullBlockName = "pt.iknow.blocks." + asBlockName;

    int flowId = procData.getFlowId();

    IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, flowId);
    Vector<Block> flowVector = null;

    String login = userInfo.getUtilizador();

    if (fd != null && !fd.hasError()) {
      flowVector = fd.getFlow();
    }

    if (flowVector != null) {

      try {

        Class<?> cBlock = Class.forName(sFullBlockName);

        Logger.debug(login, this, "getBlockId", procData.getSignature() + "CLASS=" + cBlock);

        Logger.debug(login, this, "getBlockId", procData.getSignature() + "FLOW VECTOR SIZE=" + flowVector.size());

        // Find the start block
        for (int index = 0; index < flowVector.size(); index++) {
          block = flowVector.get(index);

          Logger.debug(login, this, "getBlockId", "BLOCK AT INDEX " + index + "=" + block);

          if (cBlock.isInstance(block)) {
            blockId = block.getId();
            break;
          }
        }
      } catch (Exception e) {
        Logger.error(login, this, "getBlockId", procData.getSignature() + "getBlockId for " + asBlockName + ": " + e.getMessage(),
            e);
      }
    }

    if (blockId == -1) {
      Logger.debug(login, this, "getBlockId", procData.getSignature() + "Block " + asBlockName + " not found.");
    } else {
      Logger.debug(login, this, "getBlockId", procData.getSignature() + "BLOCK " + asBlockName + " ID: " + blockId);
    }
    return blockId;
  }

  public String deployFlow(UserInfoInterface userInfo, String asFile) {
    return BeanFactory.getFlowHolderBean().deployFlow(userInfo, asFile);
  }

  public String undeployFlow(UserInfoInterface userInfo, String asFile) {
    return BeanFactory.getFlowHolderBean().undeployFlow(userInfo, asFile);
  }

  public void saveFlowSettings(UserInfoInterface userInfo, FlowSetting[] afsaSettings) {
    BeanFactory.getFlowSettingsBean().saveFlowSettings(userInfo, afsaSettings);
  }

  public void exportFlowSettings(UserInfoInterface userInfo, int flowid, PrintStream apsOut) {
    BeanFactory.getFlowSettingsBean().exportFlowSettings(userInfo, flowid, apsOut);
  }

  public String importFlowSettings(UserInfoInterface userInfo, int flowid, byte[] file) {
    return BeanFactory.getFlowSettingsBean().importFlowSettings(userInfo, flowid, file);
  }

  public void refreshFlowSettings(UserInfoInterface userInfo, int flowid) {
    BeanFactory.getFlowSettingsBean().refreshFlowSettings(userInfo, flowid);
  }

  public FlowSetting[] getFlowSettings(UserInfoInterface userInfo, int flowid) {
    return BeanFactory.getFlowSettingsBean().getFlowSettings(userInfo, flowid);
  }

  public IFlowData getFlow(UserInfoInterface userInfo, int anFlowId) {
    return BeanFactory.getFlowHolderBean().getFlow(userInfo, anFlowId);
  }

  public boolean checkFlowEnabled(UserInfoInterface userInfo, int anFlowId) {
    return BeanFactory.getFlowHolderBean().isOnline(userInfo, anFlowId);
  }

  public FlowRolesTO[] getFlowRoles(UserInfoInterface userInfo, int anFlowId) {
    FlowRolesTO[] retObj = null;

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    List<FlowRolesTO> altmp = null;
    FlowRolesTO fr = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);
      st = db.createStatement();
      rs = null;

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT r." + FlowRolesTO.FLOW_ID);
      sql.append(", r." + FlowRolesTO.PERMISSIONS);
      sql.append(", r." + FlowRolesTO.PROFILE_ID);
      sql.append(", p." + ProfilesTO.NAME);
      sql.append(", p." + ProfilesTO.DESCRIPTION);
      sql.append(" FROM " + FlowRolesTO.TABLE_NAME + " r");
      sql.append(" , " + ProfilesTO.TABLE_NAME + " p");
      sql.append(" WHERE r." + FlowRolesTO.PROFILE_ID + "=p." + ProfilesTO.PROFILE_ID);
      if (anFlowId > 0) {
        sql.append(" AND " + FlowRolesTO.FLOW_ID + "=" + anFlowId);
      }
      sql.append(" ORDER BY " + FlowRolesTO.FLOW_ID);

      rs = st.executeQuery(sql.toString());

      altmp = new ArrayList<FlowRolesTO>();

      while (rs.next()) {
        int flowid = rs.getInt(FlowRolesTO.FLOW_ID);
        ProfilesTO profile = new ProfilesTO(rs.getInt(FlowRolesTO.PROFILE_ID), rs.getString(ProfilesTO.NAME), rs
            .getString(ProfilesTO.DESCRIPTION), userInfo.getCompanyID());
        String permissions = rs.getString(FlowRolesTO.PERMISSIONS);
        fr = new FlowRolesTO(flowid, profile, permissions);
        altmp.add(fr);
      }
      rs.close();
      rs = null;
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getFlowRoles", "exception caught: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    if (altmp != null) {
      retObj = new FlowRolesTO[altmp.size()];
      retObj = altmp.toArray(retObj);
    }

    return retObj;
  }

  public void addFlowRoles(UserInfoInterface userInfo, FlowRolesTO afrRoles) {
    FlowRolesTO[] fra = new FlowRolesTO[1];
    fra[0] = afrRoles;
    this.setFlowRoles(userInfo, fra, nMODE_ADD);
  }

  public void removeFlowRoles(UserInfoInterface userInfo, FlowRolesTO afrRoles) {
    FlowRolesTO[] fra = new FlowRolesTO[1];
    fra[0] = afrRoles;
    this.setFlowRoles(userInfo, fra, nMODE_REMOVE);
  }

  public void setFlowRoles(UserInfoInterface userInfo, FlowRolesTO[] afraRoles) {
    this.setFlowRoles(userInfo, afraRoles, nMODE_UPDATE);
  }

  private void setFlowRoles(UserInfoInterface userInfo, FlowRolesTO[] afraRoles, int anMode) {
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);
      st = db.createStatement();
      rs = null;
      for (int fr = 0; fr < afraRoles.length; fr++) {
        StringBuffer sql = new StringBuffer();
        if (anMode == nMODE_ADD) {
          sql.append("INSERT INTO " + FlowRolesTO.TABLE_NAME);
          sql.append(" (" + FlowRolesTO.FLOW_ID);
          sql.append("," + FlowRolesTO.PROFILE_ID);
          sql.append("," + FlowRolesTO.PERMISSIONS + ")");
          sql.append(" values (" + afraRoles[fr].getValueOf(FlowRolesTO.FLOW_ID));
          sql.append("," + afraRoles[fr].getValueOf(FlowRolesTO.PROFILE_ID));
          sql.append("," + afraRoles[fr].getValueOf(FlowRolesTO.PERMISSIONS) + ")");
        } else if (anMode == nMODE_REMOVE) {
          sql.append("DELETE FROM " + FlowRolesTO.TABLE_NAME);
          sql.append(" WHERE " + FlowRolesTO.FLOW_ID + "=" + afraRoles[fr].getValueOf(FlowRolesTO.FLOW_ID));
          sql.append(" AND " + FlowRolesTO.PROFILE_ID + "=" + afraRoles[fr].getValueOf(FlowRolesTO.PROFILE_ID));
        } else if (anMode == nMODE_UPDATE) {
          sql.append("UPDATE " + FlowRolesTO.TABLE_NAME);
          sql.append(" SET " + FlowRolesTO.PERMISSIONS + "=" + afraRoles[fr].getValueOf(FlowRolesTO.PERMISSIONS));
          sql.append(" WHERE " + FlowRolesTO.FLOW_ID + "=" + afraRoles[fr].getValueOf(FlowRolesTO.FLOW_ID));
          sql.append(" AND " + FlowRolesTO.PROFILE_ID + "=" + afraRoles[fr].getValueOf(FlowRolesTO.PROFILE_ID));
        } else {
          throw new Exception("NO PREDIFINED MODE.. exiting");
        }

        if (Logger.isDebugEnabled()) {
          Logger.debug(userInfo.getUtilizador(), this, "setFlowRoles", "query[" + fr + "]=" + sql);
        }

        st.executeUpdate(sql.toString());
      }
      db.commit();

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "setFlowRoles", "set commited");
      }
    } catch (Exception e) {
      try {
        if (db!=null) db.rollback();
      } catch (Exception ei) {
      }
      Logger.error(userInfo.getUtilizador(), this, "setFlowRoles", "exception caught: " + e.getMessage(), e);
    } finally {
      //DatabaseInterface.closeResources(db, st, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}    	

    }
  }

  public FlowRolesTO[] getUserFlowRolesDelegated(UserInfoInterface userInfo, int anFlowId) {
    FlowRolesTO[] retObj = null;

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    List<FlowRolesTO> altmp = null;
    FlowRolesTO fr = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();

      StringBuffer sbtmp = new StringBuffer();
      sbtmp.append("select distinct flowid, userid, permissions from ");
      sbtmp.append("activity_hierarchy where pending=0 ");
      sbtmp.append("and userid like '");
      sbtmp.append(userInfo.getUtilizador().toUpperCase()).append("'");
      if (anFlowId > 0) {
        sbtmp.append(" and flowid=").append(anFlowId);
      }

      rs = st.executeQuery(sbtmp.toString());

      altmp = new ArrayList<FlowRolesTO>();

      while (rs.next()) {
        fr = new FlowRolesTO(rs.getInt("flowid"), rs.getString("userid"), rs.getString("permissions"));
        altmp.add(fr);
      }
      rs.close();
      rs = null;

    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getUserFlowRolesDelegated", "exception caught: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    if (altmp != null) {
      retObj = new FlowRolesTO[altmp.size()];
      retObj = altmp.toArray(retObj);
    }

    return retObj;
  }

  public FlowRolesTO[] getUserFlowRoles(UserInfoInterface userInfo, int anFlowId) {
    FlowRolesTO[] retObj = null;

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    List<FlowRolesTO> altmp = null;
    FlowRolesTO fr = null;

    String[] saProfiles = userInfo.getProfiles();

    if (saProfiles != null && saProfiles.length > 0) {
      try {
        ds = Utils.getDataSource();
        db = ds.getConnection();
        st = db.createStatement();
        rs = null;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT r." + FlowRolesTO.FLOW_ID);
        sql.append(", r." + FlowRolesTO.PROFILE_ID);
        sql.append(", r." + FlowRolesTO.PERMISSIONS);
        sql.append(", p." + ProfilesTO.NAME);
        sql.append(", p." + ProfilesTO.DESCRIPTION);
        sql.append(" FROM " + FlowRolesTO.TABLE_NAME + " r, flow f");
        sql.append(", " + ProfilesTO.TABLE_NAME + " p");
        sql.append(" WHERE r." + FlowRolesTO.PROFILE_ID + "=p." + ProfilesTO.PROFILE_ID);
        sql.append(" AND p." + ProfilesTO.NAME + " in (");
        for (int i = 0; i < saProfiles.length; i++) {
          if (i > 0) {
            sql.append(",");
          }
          sql.append("'" + StringEscapeUtils.escapeSql(saProfiles[i]) + "'");
        }
        sql.append(")");
        if (anFlowId > 0) {
          sql.append(" AND r." + FlowRolesTO.FLOW_ID + "=" + anFlowId);
        }
        sql.append(" AND f.flowid=r." + FlowRolesTO.FLOW_ID);
        sql.append(" AND f.organizationid LIKE '" + userInfo.getCompanyID() + "'");
        sql.append(" ORDER BY " + FlowRolesTO.FLOW_ID);

        rs = st.executeQuery(sql.toString());
        altmp = new ArrayList<FlowRolesTO>();
        while (rs.next()) {
          int flowid = rs.getInt(FlowRolesTO.FLOW_ID);
          ProfilesTO profile = new ProfilesTO(rs.getInt(FlowRolesTO.PROFILE_ID), rs.getString(ProfilesTO.NAME), rs
              .getString(ProfilesTO.DESCRIPTION), userInfo.getCompanyID());
          String permissions = rs.getString(FlowRolesTO.PERMISSIONS);
          fr = new FlowRolesTO(flowid, profile, permissions);
          altmp.add(fr);
        }
        rs.close();
        rs = null;
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "getUserFlowRoles", "exception caught: " + e.getMessage(), e);
      } finally {
        DatabaseInterface.closeResources(db, st, rs);
      }
    }

    if (altmp != null) {
      retObj = altmp.toArray(new FlowRolesTO[altmp.size()]);
    }

    return retObj;
  }

  public FlowRolesTO[] getAllUserFlowRoles(UserInfoInterface userInfo) {
    FlowRolesTO[] fr = this.getUserFlowRoles(userInfo, -1);
    FlowRolesTO[] frd = this.getUserFlowRolesDelegated(userInfo, -1);

    int size = 0;
    if (fr != null) {
      size += fr.length;
    }
    if (frd != null) {
      size += frd.length;
    }

    FlowRolesTO[] retObj = new FlowRolesTO[size];
    int i = 0;
    for (; fr != null && i < fr.length; i++) {
      retObj[i] = fr[i];
    }
    for (int j = 0; frd != null && j < frd.length; j++) {
      retObj[i + j] = frd[j];
    }

    return retObj;
  }

  public boolean checkUserSelfFlowRoles(UserInfoInterface userInfo, int anFlowId, String asPrivilege) {
    boolean retObj = BeanFactory.getFlowSettingsBean().isGuestAccessible(userInfo, anFlowId);
    if (!retObj) {
      FlowRolesTO[] fra = this.getUserFlowRoles(userInfo, anFlowId);
      char[] privs = asPrivilege.toCharArray();
      for (char priv : privs) {
        if (fra != null) {
          for (int i = 0; i < fra.length; i++) {
            if (fra[i].hasPrivilege(priv)) {
              return true;
            }
          }
        }
      }
    }
    return retObj;
  }

  private boolean checkUserDelegatedFlowRoles(UserInfoInterface userInfo, int anFlowId, String asPrivilege) {
    FlowRolesTO[] frad = this.getUserFlowRolesDelegated(userInfo, anFlowId);
    if (frad != null) {
      char[] privs = asPrivilege.toCharArray();
      for (char priv : privs) {
        for (int i = 0; i < frad.length; i++) {
          if (frad[i].hasPrivilege(priv)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean checkUserFlowRoles(UserInfoInterface userInfo, int anFlowId, String asPrivilege) {

    if (userInfo.isManager())
      return true;

    boolean retObj = false;
    retObj = this.checkUserSelfFlowRoles(userInfo, anFlowId, asPrivilege);
    if (!retObj) {
      retObj = this.checkUserDelegatedFlowRoles(userInfo, anFlowId, asPrivilege);
    }
    return retObj;
  }

  public String endProc(UserInfoInterface userInfo, ProcessData procData) {

    String login = userInfo.getUtilizador();

    Logger.trace("FlowBean", "endProc", procData.getSignature() + login + "call");

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();

    String retPage = "End/end.jsp?flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid + "&canceled=true";

    if (!procData.isInDB()) {
      return retPage;
    }

    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    String transactionId = null;

    try {

      ProcessManager pm = BeanFactory.getProcessManagerBean();

      if (userInfo.inTransaction()) {
        conn = DatabaseInterface.getConnection(userInfo);
      } else {
        conn = Utils.getDataSource().getConnection();
        conn.setAutoCommit(false);
        transactionId = userInfo.registerTransaction(new DBConnectionWrapper(conn));
      }

      if (procData.getMid() < 0 && procData.isInDB()) {
        int mid = pm.getNextMid(userInfo, procData);
        procData.setMid(mid);
        Logger.debug(login, this, "endProc", procData.getSignature() + "MID: " + mid);
      }

      procData.set(DataSetVariables.PROCESS_STATE_DESC, DataSetVariables.USER_CANCELED + ": " + login);

      try {
        pm.deleteAllActivities(userInfo, procData);
      } catch (Exception e) {
        Logger.error(login, this, "endProc", procData.getSignature() + "caught exception unscheduling activities: "
            + e.getMessage(), e);
        throw e;
      }

      st = conn.createStatement();

      rs = st.executeQuery("select state from flow_state" + " where flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid
          + " and closed=0");
      if (rs.next()) {
        String stmp = rs.getString("state");
        String query = DBQueryManager.processQuery("Flow.update_state", new Object[] { stmp,
            procData.get(DataSetVariables.PROCESS_STATE_DESC).format(), String.valueOf(flowid), String.valueOf(pid) });
        st.executeUpdate(query);

        this.saveDataSet(userInfo, procData, null);

        this.checkFlowEnd(userInfo, procData, null, true, true);

        DatabaseInterface.commitConnection(conn);

        Logger.info(login, this, "endProc", procData.getSignature() + "proc ended");
      }
      rs.close();
      rs = null;

    } catch (Throwable t) {
      Logger.error(login, this, "endProc", procData.getSignature() + "caught exception", t);
      if (conn != null) {
        try {
          DatabaseInterface.rollbackConnection(conn);
          Logger.warning(login, this, "endProc", procData.getSignature() + "db connection rollback");

        } catch (Exception ec) {
          Logger.error(login, this, "endProc", procData.getSignature() + "rolling back transaction", ec);
        }
      }
      getErrorUrl("flow_error.end_proc");
    } finally {
      procData.setMid(Const.NO_MID);
      try {
        if (StringUtils.isNotEmpty(transactionId)) {
          try {
            userInfo.unregisterTransaction(transactionId);
          } catch (IllegalAccessException e) {
            Logger.error(login, this, "endProc", procData.getSignature() + "unregistering transaction in userinfo", e);

            NotificationManager notificationManager = BeanFactory.getNotificationManagerBean();
            notificationManager.notifySystemError(userInfo, "endProc@FlowBean", procData.getSignature()
                + "illegal access unregistering transaction in userInfo! " + e.getMessage());

            retPage = getErrorUrl("flow_error.unregister_transaction");
          }
        }
      } finally {
        //DatabaseInterface.closeResources(conn, st, rs);
      	try {if (conn != null) conn.close(); } catch (SQLException e) {}
      	try {if (st != null) st.close(); } catch (SQLException e) {}
      	try {if (rs != null) rs.close(); } catch (SQLException e) {}    	

      }
    }

    return retPage;
  }

  public boolean endProccessInBlockAdministration(UserInfoInterface userInfo, ProcessData procData) {
    boolean result = false;

    String login = userInfo.getUtilizador();
    Logger.trace("FlowBean", "endProccessInBlockAdministration", procData.getSignature() + login + "call");

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();

    if (!procData.isInDB()) {
      return true;
    }

    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    String transactionId = null;

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();
      if (userInfo.inTransaction()) {
        conn = DatabaseInterface.getConnection(userInfo);
      } else {
        conn = Utils.getDataSource().getConnection();
        conn.setAutoCommit(false);
        transactionId = userInfo.registerTransaction(new DBConnectionWrapper(conn));
      }
      if (procData.getMid() < 0 && procData.isInDB()) {
        int mid = pm.getNextMid(userInfo, procData);
        procData.setMid(mid);
        Logger.debug(login, this, "endProccessInBlockAdministration", procData.getSignature() + "MID: " + mid);
      }

      procData.set(DataSetVariables.PROCESS_STATE_DESC, DataSetVariables.USER_CANCELED + ": " + login);

      try {
        pm.deleteAllActivities(userInfo, procData);
      } catch (Exception e) {
        Logger.error(login, this, "endProc", procData.getSignature() + "caught exception unscheduling activities: "
            + e.getMessage(), e);
        throw e;
      }

      st = conn.createStatement();

      rs = st.executeQuery("select state from flow_state" + " where flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid
          + " and closed=0");
      if (rs.next()) {
        String stmp = rs.getString("state");
        String query = DBQueryManager.processQuery("Flow.update_state", new Object[] { stmp,
            procData.get(DataSetVariables.PROCESS_STATE_DESC).format(), String.valueOf(flowid), String.valueOf(pid) });
        st.executeUpdate(query);

        this.saveDataSet(userInfo, procData, null);

        this.checkFlowEnd(userInfo, procData, null, true, true);

        DatabaseInterface.commitConnection(conn);

        Logger.info(login, this, "endProc", procData.getSignature() + "proc ended");
        result = true;
      }
      rs.close();
      rs = null;
    } catch (Throwable t) {
      Logger.error(login, this, "endProc", procData.getSignature() + "caught exception", t);
      if (conn != null) {
        try {
          DatabaseInterface.rollbackConnection(conn);
          Logger.warning(login, this, "endProc", procData.getSignature() + "db connection rollback");

        } catch (Exception ec) {
          Logger.error(login, this, "endProc", procData.getSignature() + "rolling back transaction", ec);
        }
      }
      result = false;
    } finally {
      procData.setMid(Const.NO_MID);
      try {
        if (StringUtils.isNotEmpty(transactionId)) {
          try {
            userInfo.unregisterTransaction(transactionId);
          } catch (IllegalAccessException e) {
            Logger.error(login, this, "endProc", procData.getSignature() + "unregistering transaction in userinfo", e);

            NotificationManager notificationManager = BeanFactory.getNotificationManagerBean();
            notificationManager.notifySystemError(userInfo, "endProc@FlowBean", procData.getSignature()
                + "illegal access unregistering transaction in userInfo! " + e.getMessage());

            result = false;
          }
        }
      } finally {
        //DatabaseInterface.closeResources(conn, st, rs);
      	try { conn.close(); } catch (Exception e) {}
      	try {if (st != null) st.close(); } catch (SQLException e) {}
      	try {if (rs != null) rs.close(); } catch (SQLException e) {}    	

      }
    }
    return result;
  }

  public String endSubProc(UserInfoInterface userInfo, ProcessData procData, String endMsg) {
    String login = userInfo.getUtilizador();

    try {
      procData.parseAndSet(DataSetVariables.PROCESS_STATE_DESC, endMsg);
    } catch (ParseException e) {
    }

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      pm.deleteAllActivities(userInfo, procData);
    } catch (Exception e) {
      Logger.error(login, this, "endProc", procData.getSignature() + "caught exception unscheduling activities: " + e.getMessage(),
          e);
    }

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();

    Connection db = null;
    PreparedStatement pst = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);

      db.setAutoCommit(false);

      pst = db.prepareStatement("update flow_state set mdate=?,result=? where flowid=? and pid=? and subpid=?");
      pst.setTimestamp(1, new Timestamp(Calendar.getInstance().getTimeInMillis()));
      pst.setString(2, endMsg);
      pst.setInt(3, flowid);
      pst.setInt(4, pid);
      pst.setInt(5, subpid);
      pst.executeUpdate();
      pst.close();
      pst = null;

      this.saveDataSet(userInfo, procData, null);

      this.checkFlowEnd(userInfo, procData, null, true);

      DatabaseInterface.commitConnection(db);

      Logger.info(login, this, "endSubProc", procData.getSignature() + "subProc ended");

    } catch (Exception e) {
      Logger.error(login, this, "endSubProc", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      try {
        DatabaseInterface.rollbackConnection(db);
      } catch (Exception e2) {
      }
      return getErrorUrl("flow_error.end_sub_proc");
    } finally {
      DatabaseInterface.closeResources(db, pst);
    }

    return "End/end.jsp?flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid;
  }

  public String resyncFlow(UserInfoInterface userInfo, int anFlowId, int anOldBlockId, int anNewBlockId) {
    return resyncFlow(userInfo, anFlowId, anOldBlockId, anNewBlockId, false);
  }

  public String resyncFlow(UserInfoInterface userInfo, int anFlowId, int anOldBlockId, int anNewBlockId, boolean force) {

    String retObj = null;
    StringBuilder sbError = new StringBuilder();
    LicenseService licenseService = LicenseServiceFactory.getLicenseService();

    String login = userInfo.getUtilizador();
    StringBuilder sbPids = null;

    Connection db = null;
    Statement st = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String transactionId = null;

    try {
      IFlowData fd = this.getFlow(userInfo, anFlowId);
      Block block = null;
      ProcessHeader procHeader = new ProcessHeader(anFlowId, Const.nSESSION_PID, Const.nSESSION_SUBPID);

      if (fd == null || fd.hasError()) {
        sbError.append("Flow inv&aacute;lido.<br>");
      } else if (fd.isOnline()) {
        sbError.append("O flow est&aacute; online, pelo que n&atilde;o pode ser re-sincronizado.<br>");
      } else {
        block = this.getBlockById(userInfo, procHeader, anOldBlockId);
        if (!force && block != null) {
          sbError.append("O estado antigo escolhido corresponde a um estado ainda v&aacute;lido no flow.<br>");
        } else {
          block = this.getBlockById(userInfo, procHeader, anNewBlockId);
          if (block == null) {
            sbError.append("O novo estado escolhido n&atilde;o &eacute; v&aacute;lido no flow.<br>");
          } else {
            boolean isDeployed = BeanFactory.getFlowHolderBean().isOnline(userInfo, anFlowId);
            if (isDeployed) {
              sbError.append("O flow encontra-se activo.<br>");
            } else {

              db = Utils.getDataSource().getConnection();
              db.setAutoCommit(false);
              transactionId = userInfo.registerTransaction(new DBConnectionWrapper(db));

              st = db.createStatement();
              rs = null;

              // now get all (sub)processes in anOldBlockId state
              String query = "select pid, subpid, mid from flow_state" + " where state=" + anOldBlockId + " and flowid=" + anFlowId
                  + " and closed=0" + " order by pid asc";

              List<ProcessHeader> alProcHeaders = new ArrayList<ProcessHeader>();
              rs = st.executeQuery(query);
              while (rs.next()) {
                ProcessHeader phHeader = new ProcessHeader(anFlowId, rs.getInt("pid"), rs.getInt("subpid"));
                phHeader.setMid(rs.getInt("mid"));
                alProcHeaders.add(phHeader);
              }
              rs.close();
              rs = null;

              if (alProcHeaders.size() == 0) {
                sbError.append("N&atilde;o existem processos no estado ");
                sbError.append(anOldBlockId).append(".<br>");
              } else {
                ProcessManager pm = BeanFactory.getProcessManagerBean();

                ps = db.prepareStatement("update flow_state " + "set state=?, result=result||' (FlowResync in progress)' "
                    + "where flowid=? and pid=? and subpid=?");

                sbPids = new StringBuilder();
                for (int header = 0; header < alProcHeaders.size(); header++) {
                  ProcessHeader ph = alProcHeaders.get(header);

                  int pid = ph.getFlowId();
                  int subpid = ph.getPid();
                  int mid = ph.getMid();

                  try {
                    // update state in db
                    ps.setInt(1, anNewBlockId);
                    ps.setInt(2, anFlowId);
                    ps.setInt(3, pid);
                    ps.setInt(4, subpid);

                    ps.executeUpdate();

                    if (header > 0)
                      sbPids.append(",");
                    sbPids.append(pid);

                    // now call block's before method
                    ProcessData pdProc = pm.getProcessData(userInfo, ph);
                    String blockUrl = block.before(userInfo, pdProc);
                    licenseService.consume(userInfo, anFlowId, block.getCost());
                    if (!block.isEndBlock() && !block.hasInteraction()) {
                      // call next block if block has no
                      // user interaction
                      blockUrl = this.nextBlock(userInfo, pdProc, true);

                      // refetch block
                      int state = this.getFlowState(userInfo, pdProc);
                      block = this.getBlockById(userInfo, ph, state);
                    } else {
                      // save flow state and dataset
                      this.saveFlowState(userInfo, pdProc, block, true, mid, null);
                      this.saveDataSet(userInfo, pdProc, null, mid);
                    }

                    if (blockUrl != null) {
                      // call update activity (url and
                      // description)
                      // (everything should be ok, but is
                      // better to ensure)
                      Activity activity = new Activity(login, anFlowId, pid, subpid, 0, 0, block.getDescription(userInfo, pdProc),
                          Block.getDefaultUrl(userInfo, pdProc), 1);
                      activity.mid = mid;
                      pm.updateActivity(userInfo, activity);
                    }

                    db.commit();

                    Logger.info(login, this, "resyncFlow", pdProc.getSignature() + "db connection commit");

                  } catch (Exception ei) {

                    db.rollback();

                    Logger.error(login, getClass(), "resyncFlow", "Error resync process: " + pid, ei);
                    sbError.append("Ocorreu um erro ao sincronizar processo id=");
                    sbError.append(pid).append(".<br>");
                  }
                }
                ps.close();
                ps = null;
              }
            }
          }
        }
      }

    } catch (Exception e) {

      if (db != null) {
        try {
          db.rollback();
        } catch (SQLException e1) {
        }
      }

      Logger.error(login, getClass(), "resyncFlow", "Error resync flow: " + anFlowId, e);
      sbError.append("Ocorreu um erro ao sincronizar o fluxo.<br>");
    } finally {
      try {
        if (StringUtils.isNotEmpty(transactionId)) {
          try {
            userInfo.unregisterTransaction(transactionId);
          } catch (IllegalAccessException e) {
            Logger.error(login, this, "resyncFlow", "unregistering transaction in userinfo", e);

            NotificationManager notificationManager = BeanFactory.getNotificationManagerBean();
            notificationManager.notifySystemError(userInfo, "resyncFlow@FlowBean",
                "illegal access unregistering transaction in userInfo! " + e.getMessage());
          }
        }
      } finally {
        // DatabaseInterface.closeResources(db, st, rs, ps);
      	try { db.close(); } catch (Exception e) {}
      	try {if (st != null) st.close(); } catch (SQLException e) {}
      	try {if (rs != null) rs.close(); } catch (SQLException e) {}    	
      	try {if (ps != null) ps.close(); } catch (SQLException e) {}

      }
    }

    if (sbError.length() > 0) {
      retObj = sbError.toString();
    }

    if (sbPids != null) {
      Logger.warning(login, this, "resyncFlow", "flowid=" + anFlowId + " from blockid " + anOldBlockId + " to " + anNewBlockId
          + " for procs " + sbPids.toString() + ". ERROR: " + retObj);
    } else {
      Logger.warning(login, this, "resyncFlow", "flowid=" + anFlowId + " from blockid " + anOldBlockId + " to " + anNewBlockId
          + ". ERROR: " + retObj);
    }

    return retObj;
  }

  /**
   * Retrives a given flow's info.
   * 
   * @param userInfo
   *          caller user
   * @param anFlowId
   *          desired flow id
   * 
   * @return list of hashmaps with each block's info (key/value: id/string,type/string,hasinteraction/boolean,
   *         outblocks/arraylist(hmtmp(portname,connectedblockid)))
   * @ejb.interface-method view-type = "remote"
   */
  public List<BlockInfo> getFlowInfo(UserInfoInterface userInfo, int anFlowId) {
    List<BlockInfo> retObj = null;

    IFlowData fd = getFlow(userInfo, anFlowId);
    Vector<Block> flowVector = null;
    String stmp = null;
    Port[] pa = null;
    Block block = null;

    if (fd != null && !fd.hasError()) {
      flowVector = fd.getFlow();
    }

    if (flowVector != null) {

      retObj = new ArrayList<BlockInfo>();

      for (int index = 0; index < flowVector.size(); index++) {
        block = flowVector.get(index);
        BlockInfo bInfo = new BlockInfo();

        stmp = String.valueOf(block.getId());
        bInfo.setId(stmp);

        stmp = block.getClass().getName();
        stmp = stmp.substring((stmp.lastIndexOf(".") + 1));
        bInfo.setType(stmp);

        bInfo.setInteraction(block.hasInteraction());

        pa = block.getOutPorts(userInfo);
        if (pa != null) {
          for (int ii = 0; ii < pa.length; ii++) {
            if (pa[ii] == null)
              continue;
            Map<String, String> outblock = new HashMap<String, String>();
            outblock.put("name", pa[ii].getName());
            outblock.put("connectedblockid", String.valueOf(pa[ii].getConnectedBlockId()));
            bInfo.addOutblock(outblock);
          }
        }

        retObj.add(bInfo);
      }
    }

    return retObj;
  }

  /**
   * Retrives a given flow's states.
   * 
   * @param userInfo
   *          caller user
   * @param procData
   *          the process data to fetch the states
   * 
   * @return arraylist with the process states (string)
   * @ejb.interface-method view-type = "remote"
   */
  public List<String> getFlowStates(UserInfoInterface userInfo, ProcessData procData) {

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    List<String> retObj = null;

    int flowId = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    if (procData.isInDB()) {
      try {

        if (pid <= 0) {
          // shouldn't be here
          return retObj;
        }

        db = DatabaseInterface.getConnection(userInfo);
        st = db.createStatement();

        rs = st.executeQuery("select distinct(state) from flow_state" + " where flowid=" + flowId + " and pid=" + pid
            + " and subpid=" + subpid + " and closed=0");

        retObj = new ArrayList<String>();
        while (rs.next()) {
          retObj.add(rs.getString(1));
        }
      } catch (Exception e) {
        Logger.error(login, this, "getFlowStates", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      } finally {
        DatabaseInterface.closeResources(db, st, rs);
      }
    }
    return retObj;
  }

  public ProcessCatalogue getFlowCatalogue(UserInfoInterface userInfo, int anFlowId) {
    IFlowData flow = getFlow(userInfo, anFlowId);
    if (null == flow)
      return null;
    return flow.getCatalogue();
  }

  public FlowType getFlowType(UserInfoInterface userInfo, int anFlowId) {
    IFlowData flow = getFlow(userInfo, anFlowId);
    if (null == flow)
      return null;
    return flow.getFlowType();
  }

  public boolean setFlowType(UserInfoInterface userInfo, int flowid, FlowType flowType) {
    if (null == userInfo || !userInfo.isOrgAdmin()) {
      Logger.warning(userInfo == null ? null : userInfo.getUtilizador(), this, "setFlowType", "Access denied");
      return false;
    }

    // TODO Auto-generated method stub
    return false;
  }

  private Block getBlockById(IFlowData fd, Integer anOldBlockId) {
    Block block = null;
    Vector<Block> flowVector = null;
    if (fd != null && !fd.hasError()) {
      flowVector = fd.getFlow();
    }

    if (flowVector != null) {
      // Find the required block
      for (int index = 0; index < flowVector.size(); index++) {
        block = flowVector.get(index);
        if (block.getId() == anOldBlockId) {
          break;
        }
        block = null;
      }
    }

    return block;
  }

  public String resyncFlow(UserInfoInterface userInfo, int anFlowId, Integer anOldBlockId, Integer anNewBlockId, boolean force,
      IFlowData fd) {

    String retObj = null;
    StringBuilder sbError = new StringBuilder();
    LicenseService licenseService = LicenseServiceFactory.getLicenseService();

    String login = userInfo.getUtilizador();
    StringBuilder sbPids = null;

    Connection db = null;
    Statement st = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String transactionId = null;

    try {
      Block block = null;
      if (fd == null || fd.hasError()) {
        sbError.append("Flow inv&aacute;lido.<br>");
      } else if (fd.isOnline() && !force) {
        sbError.append("O flow est&aacute; online, pelo que n&atilde;o pode ser re-sincronizado.<br>");
      } else {
        block = getBlockById(fd, anOldBlockId);
        if (!force && block != null) {
          sbError.append("O estado antigo escolhido corresponde a um estado ainda v&aacute;lido no flow.<br>");
        } else {
          block = this.getBlockById(fd, anNewBlockId);
          if (block == null && !force) {
            sbError.append("O novo estado escolhido n&atilde;o &eacute; v&aacute;lido no flow.<br>");
          } else {
            boolean isDeployed = BeanFactory.getFlowHolderBean().isOnline(userInfo, anFlowId);
            if (isDeployed && !force) {
              sbError.append("O flow encontra-se activo.<br>");
            } else {

              db = Utils.getDataSource().getConnection();
              db.setAutoCommit(false);
              transactionId = userInfo.registerTransaction(new DBConnectionWrapper(db));

              st = db.createStatement();
              rs = null;

              // now get all (sub)processes in anOldBlockId state
              String query = "select pid, subpid, mid from flow_state" + " where state=" + anOldBlockId + " and flowid=" + anFlowId
                  + " and closed=0" + " order by pid asc";

              List<ProcessHeader> alProcHeaders = new ArrayList<ProcessHeader>();
              rs = st.executeQuery(query);
              while (rs.next()) {
                ProcessHeader phHeader = new ProcessHeader(anFlowId, rs.getInt("pid"), rs.getInt("subpid"));
                phHeader.setMid(rs.getInt("mid"));
                alProcHeaders.add(phHeader);
              }
              rs.close();
              rs = null;

              if (alProcHeaders.size() == 0) {
                sbError.append("N&atilde;o existem processos no estado ");
                sbError.append(anOldBlockId).append(".<br>");
              } else {
                ProcessManager pm = BeanFactory.getProcessManagerBean();

                ps = db.prepareStatement("update flow_state " + "set state=?, result=result||' (FlowResync in progress)' "
                    + "where flowid=? and pid=? and subpid=?");

                sbPids = new StringBuilder();
                for (int header = 0; header < alProcHeaders.size(); header++) {
                  ProcessHeader ph = alProcHeaders.get(header);

                  int pid = ph.getFlowId();
                  int subpid = ph.getPid();
                  int mid = ph.getMid();

                  try {
                    // update state in db
                    ps.setInt(1, anNewBlockId);
                    ps.setInt(2, anFlowId);
                    ps.setInt(3, pid);
                    ps.setInt(4, subpid);

                    ps.executeUpdate();

                    if (header > 0)
                      sbPids.append(",");
                    sbPids.append(pid);

                    // now call block's before method
                    ProcessData pdProc = pm.getProcessData(userInfo, ph);
                    String blockUrl = block.before(userInfo, pdProc);
                    licenseService.consume(userInfo, anFlowId, block.getCost());
                    if (!block.isEndBlock() && !block.hasInteraction()) {
                      // call next block if block has no
                      // user interaction
                      blockUrl = this.nextBlock(userInfo, pdProc, true);

                      // refetch block
                      int state = this.getFlowState(userInfo, pdProc);
                      block = this.getBlockById(fd, state);
                    } else {
                      // save flow state and dataset
                      this.saveFlowState(userInfo, pdProc, block, true, mid, null);
                      this.saveDataSet(userInfo, pdProc, null, mid);
                    }

                    if (blockUrl != null) {
                      // call update activity (url and
                      // description)
                      // (everything should be ok, but is
                      // better to ensure)
                      Activity activity = new Activity(login, anFlowId, pid, subpid, 0, 0, block.getDescription(userInfo, pdProc),
                          Block.getDefaultUrl(userInfo, pdProc), 1);
                      activity.mid = mid;
                      pm.updateActivity(userInfo, activity);
                    }

                    db.commit();

                    Logger.info(login, this, "resyncFlow", pdProc.getSignature() + "db connection commit");

                  } catch (Exception ei) {

                    db.rollback();

                    Logger.error(login, getClass(), "resyncFlow", "Error resync process: " + pid, ei);
                    sbError.append("Ocorreu um erro ao sincronizar processo id=");
                    sbError.append(pid).append(".<br>");
                  }
                }
                ps.close();
                ps = null;
              }
            }
          }
        }
      }

    } catch (Exception e) {

      if (db != null) {
        try {
          db.rollback();
        } catch (SQLException e1) {
        }
      }

      Logger.error(login, getClass(), "resyncFlow", "Error resync flow: " + anFlowId, e);
      sbError.append("Ocorreu um erro ao sincronizar o fluxo.<br>");
    } finally {
      try {
        if (StringUtils.isNotEmpty(transactionId)) {
          try {
            userInfo.unregisterTransaction(transactionId);
          } catch (IllegalAccessException e) {
            Logger.error(login, this, "resyncFlow", "unregistering transaction in userinfo", e);

            NotificationManager notificationManager = BeanFactory.getNotificationManagerBean();
            notificationManager.notifySystemError(userInfo, "resyncFlow@FlowBean",
                "illegal access unregistering transaction in userInfo! " + e.getMessage());
          }
        }
      } finally {
        // DatabaseInterface.closeResources(db, st, rs, ps);
      	try { db.close(); } catch (Exception e) {}
      	try {if (st != null) st.close(); } catch (SQLException e) {}
      	try {if (rs != null) rs.close(); } catch (SQLException e) {}    	
      	try {if (ps != null) ps.close(); } catch (SQLException e) {}
      }
    }

    if (sbError.length() > 0) {
      retObj = sbError.toString();
    }

    if (sbPids != null) {
      Logger.warning(login, this, "resyncFlow", "flowid=" + anFlowId + " from blockid " + anOldBlockId + " to " + anNewBlockId
          + " for procs " + sbPids.toString() + ". ERROR: " + retObj);
    } else {
      Logger.warning(login, this, "resyncFlow", "flowid=" + anFlowId + " from blockid " + anOldBlockId + " to " + anNewBlockId
          + ". ERROR: " + retObj);
    }

    return retObj;
  }
  
  public void purge(UserInfoInterface userInfo, Long purgingAgeDays){
	  Connection db = null;
	  PreparedStatement pst = null;
	  try{
		  java.sql.Date old = new java.sql.Date( (new java.util.Date()).getTime() - purgingAgeDays*24*60*60*1000);
		  db = DatabaseInterface.getConnection(userInfo);
		  db.setAutoCommit(false);
		  
		  pst = db.prepareStatement(DBQueryManager.getQuery("Flow.delete_flow_state_history"));
		  pst.setDate(1, old);
		  pst.executeUpdate();
		  pst.close();
		  		  
		  pst = db.prepareStatement(DBQueryManager.getQuery("Flow.delete_flow_state_log"));		  		  		 
		  pst.setDate(1, old);
		  pst.executeUpdate();
		  pst.close();
		  
		  pst = db.prepareStatement(DBQueryManager.getQuery("Flow.delete_log"));		  		  		 
		  pst.setDate(1, old);
		  pst.executeUpdate();
		  pst.close();
		
		  DatabaseInterface.commitConnection(db);		
	      } catch (Exception e) {
	        Logger.adminError("FlowBean", "purge", "caught exception: " + e.getMessage(), e);
	        try {
	          DatabaseInterface.rollbackConnection(db);
	        } catch (Exception er) {
	          Logger.adminError("FlowBean", "purge", "exception rolling back connection: " + er.getMessage(), er);
	        }	        
	      } finally {
	        DatabaseInterface.closeResources(db, pst);
	      }
  }

}
