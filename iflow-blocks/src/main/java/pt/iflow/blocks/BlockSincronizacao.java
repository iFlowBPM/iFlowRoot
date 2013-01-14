package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.errors.IErrorManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * <p>Title: BlockSincronizacao</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 */

public class BlockSincronizacao extends Block {
  public Port portIn, portInSubProc, portOut;

  public final static String[] sDATASETS = { "DataSet 1 (entrada superior)", "DataSet 2 (entrada inferior)" };
  
  public final static String sVARNAME = "dataset_"; 
  public final static String sDATASET = "DataSet";
  
  public final static String sSCHEDULE = "Schedule";
  
  public final static String sONFIRST  = "Utilizadores SubProc 1";
  public final static String sONSECOND = "Utilizadores SubProc 2";
  public final static String sONBOTH   = "Utilizadores SubProc 1 e 2";
  
  public final static int UP = 0;
  public final static int DOWN = 1;

  public final static int MASTER = DOWN;
  public final static int SLAVE  = DOWN;
  
  public BlockSincronizacao(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    bProcInDBRequired = true;
    canRunInPopupBlock = false;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[2];
      retObj[0] = portIn;
      retObj[1] = portInSubProc;
      return retObj;
  }
 
  public Port getEventPort() {
      return null;
  }
  
  public Port[] getOutPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[1];
      retObj[0] = portOut;
      return retObj;
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
      return "";
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return 
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
      String login = userInfo.getUtilizador();
      boolean retObj = false;
      
      DataSource ds = null;
      Connection db = null;
      Statement st = null;
      ResultSet rs = null;
      int flowid = procData.getFlowId();
      int pid = procData.getPid();
      int subpid = procData.getSubPid();
      int blockid = this.getId();
      
      try {
          ds = Utils.getDataSource();
          db = ds.getConnection();
          st = db.createStatement();
          
          rs = st.executeQuery("select count(*) as num from flow_state where flowid=" 
                  + flowid + " and pid=" + pid + " and state=" + blockid + " and closed=0");
          if (rs.next()) {
              if (rs.getInt("num") > 1) {
                  retObj = true;
              }
          }
          rs.close();
          rs = null;
          
          if (!retObj) {
              // disable the activity
              // 0 - visible in user tasks
              // 1 - not visible
              st.executeUpdate("update activity set status=1 where flowid=" + 
                      flowid + " and pid=" + pid + " and subpid=" + subpid);
          }
          
      }
      catch (SQLException sqle) {
          Logger.error(login,this,"canProceed",
              procData.getSignature() + "caught sql exception: " + sqle.getMessage(), sqle);
      }
      finally {
          DatabaseInterface.closeResources(db,st,rs);
      }
      
      return retObj;
  }
  
  
  @Override
  public MSG_CODES getCanProceedMsgCode(UserInfoInterface userInfo,
    ProcessData procData) {
    if (canProceed(userInfo, procData))
      return MSG_CODES.OK;
    else
      return MSG_CODES.WAITING_JOIN;
  }
  
  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
      final String login = userInfo.getUtilizador();
      final int flowid = procData.getFlowId();
      final int pid    = procData.getPid();
      final int subpid    = procData.getSubPid();
      final IErrorManager errorManager = BeanFactory.getErrorManagerBean();
      
      Port outPort = null;
      
      String errorKey = errorManager.init(userInfo, procData, this, "after");
      
      try {
        errorManager.register(errorKey, IErrorManager.sBEAN_ERROR, "Error getting PROCESS_MANAGER_BEAN");
        ProcessManager pm = BeanFactory.getProcessManagerBean();

        errorManager.register(errorKey, IErrorManager.sDB_ERROR, "Error getting subpids that arrived at block join");
        // get main & secondary subpids
        // int[] subpids = pm.getSubPidsInBlock(userInfo, procData, this);
        final int[] subpids = getBlockSubpids(login, flowid, pid);
        
        
        errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "The current process ("+flowid+","+pid+","+subpid+") is not one of the block processes ("+flowid+","+pid+","+subpids[UP]+"/"+subpids[DOWN]+")");
        if(subpids[UP] != subpid && subpids[DOWN] != subpid) throw new Exception("Invalid process state");


        errorManager.register(errorKey, IErrorManager.sBEAN_ERROR, "Error getting FLOW_BEAN");
        final Flow flow = BeanFactory.getFlowBean();

        errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error retrieving DataSets from both ports");


        // O que é que queremos aqui?
        ProcessData topProcess = pm.getProcessData(userInfo, new ProcessHeader(flowid, pid, subpids[UP]));
        ProcessData bottomProcess = pm.getProcessData(userInfo, new ProcessHeader(flowid, pid, subpids[DOWN]));
        ProcessData toKeep, toRemove;

        errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error merging variables");
        // merge data sets
        if (StringUtils.equals(getAttribute(sDATASET), sDATASETS[UP])) {  // master is top
          toKeep = topProcess;
          toRemove = bottomProcess;
        } else {
          toKeep = bottomProcess;
          toRemove = topProcess;
        }
        mergeDataSets(toRemove, toKeep);
        
        errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error saving dataset");
        // save dataset
        this.saveDataSet(userInfo, toKeep);
        
        
        errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error getting processActivities");
        ListIterator<Activity> liTopActv = pm.getProcessActivities(userInfo, topProcess);
        ListIterator<Activity> liBottomActv = pm.getProcessActivities(userInfo, bottomProcess);


        errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error updating current activities");
        final List<Activity> newActivities = new ArrayList<Activity>();
        final String resultMsg = getResult(userInfo, toKeep);
        // filter activities.....
        if (StringUtils.equals(getAttribute(sSCHEDULE), sONFIRST) || 
            StringUtils.equals(getAttribute(sSCHEDULE), sONBOTH)) {
          while (liTopActv.hasNext()) {
            Activity a = liTopActv.next();
            a.subpid = toKeep.getSubPid();
            a.description = resultMsg;
            a.url = Block.getDefaultUrl(userInfo, a.flowid, a.pid, a.subpid);
            a.mid = procData.getMid();            
            newActivities.add(a);
          }
        }
        if (StringUtils.equals(getAttribute(sSCHEDULE), sONSECOND) || 
            StringUtils.equals(getAttribute(sSCHEDULE), sONBOTH)) {
          while (liBottomActv.hasNext()) {
            Activity a = liBottomActv.next();
            a.subpid = toKeep.getSubPid();
            a.description = resultMsg;
            a.url = Block.getDefaultUrl(userInfo, a.flowid, a.pid, a.subpid);
            a.mid = procData.getMid();            
            newActivities.add(a);
          }
        }

        errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error removing current activities");
        // kill all existing activities....
        pm.deleteAllActivities(userInfo, topProcess);
        pm.deleteAllActivities(userInfo, bottomProcess);

        errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error rescheduling new activities");
        // add new activities
        pm.createActivities(userInfo, newActivities.listIterator());

        errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error closing slave process and corresponding activities");
        // close the "other" process
        flow.endSubProc(userInfo, toRemove, resultMsg);
        procData.setSubPid(toKeep.getSubPid());

      }
      catch (Exception e) {
          errorManager.fire(errorKey);
          Logger.error(login, this, "after", 
              procData.getSignature() + "Exception joining processes", e);
          return null;
      }
      errorManager.close(errorKey);
      
      outPort = portOut;
      return outPort;
  }

  private int[] getBlockSubpids(String login, int flowid, int pid) {
    int [] result = new int[]{-1,-1};
    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    int topBlockId = this.portIn.getConnectedBlockId();
    int bottomBlockId = this.portInSubProc.getConnectedBlockId();
    
    try {
        ds = Utils.getDataSource();
        db = ds.getConnection();
        st = db.prepareStatement("select subpid from flow_state_history where flowid=? and pid=? and state=?");
        st.setInt(1, flowid);
        st.setInt(2, pid);
        st.setInt(3, topBlockId);
        
        rs = st.executeQuery();
        if (rs.next()) {
          result[UP] = rs.getInt("subpid");
        }
        rs.close();
        rs = null;

        st.setInt(3, bottomBlockId);
        rs = st.executeQuery();
        if (rs.next()) {
          result[DOWN] = rs.getInt("subpid");
        }
        rs.close();
        rs = null;
        
    }
    catch (SQLException sqle) {
        Logger.error(login,this,"getBlockSubpids",
            "caught sql exception: " + sqle.getMessage(), sqle);
    }
    finally {
        DatabaseInterface.closeResources(db,st,rs);
    }
    
    return result;
  }
  
  
  private void mergeDataSets(ProcessData from, ProcessData to) {
    String sVar = null;
    for (int i=0; (sVar = this.getAttribute(sVARNAME + i)) != null; i++) {
      if(from.isListVar(sVar))
        to.copyFrom(from.getList(sVar));
      else
        to.copyFrom(from.get(sVar));
    }
  }
  
  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
      return this.getDesc(userInfo, procData, true, "Junção AND de Processo.");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
      return this.getDesc(userInfo, procData, false, "Junção AND de Processo Concluída.");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
      return "";
  }
}
