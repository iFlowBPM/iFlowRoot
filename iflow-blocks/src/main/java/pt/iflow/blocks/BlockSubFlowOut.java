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
package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.sql.DataSource;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.errors.IErrorManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
@Deprecated
public class BlockSubFlowOut extends Block {

  public Port portIn, portInThread, portOut, portError;

  private static final String sPREFIX = "Type_";
  private static final String sACTIV = "AManter actividades do";
  private static final String[] saACTIV = { "Escolha", "Fluxo Original", "Sub Fluxo" };

  public BlockSubFlowOut(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    bProcInDBRequired = true;
    saveFlowState = false;
    canRunInPopupBlock = false;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portIn;
    retObj[1] = portInThread;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portOut;
    retObj[1] = portError;
    return retObj;
  }

  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

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

      rs = st.executeQuery("select count(*) as num from flow_state where flowid=" + flowid + " and pid=" + pid + " and state=" + blockid + " and closed=0");
      if (rs.next()) {
        int num = rs.getInt("num"); 
        Logger.debug(userInfo.getUtilizador(),this,"canProceed","Found " + num + " procs for subpid  " + subpid);
        if (num > 1) {
          retObj = true;
        }
      }
      rs.close();
      rs = null;

      Logger.info(userInfo.getUtilizador(),this,"canProceed","Found enough processes for subpid  " + subpid + ": " + retObj);

      if (!retObj) {
        // disable the activity
        // 0 - visible in user tasks
        // 1 - not visible
        st.executeUpdate("update activity set status=1 where flowid=" + flowid + " and pid=" + pid + " and subpid=" + subpid);

        Logger.info(userInfo.getUtilizador(),this,"canProceed","disabling activities for subpid=" + subpid);
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

  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port retObj = portOut;

    String sActivProp = sACTIV;
    // not sure if property name has changed....
    for(String attr : getAttributeMap().keySet()) {
      if(attr.charAt(0)=='A') {
        sActivProp = attr;
        break;
      }
    }
    String sActivValue = getAttribute(sActivProp);

    IErrorManager errorManager = getErrorManagerBean();

    int flowid = procData.getFlowId();
    int pid    = procData.getPid();
    int subpid = procData.getSubPid();
//    String pnumber = procData.getPNumber();
    int SLAVE = 1, MASTER = 0;

    String errorKey = errorManager.init(userInfo, procData, this, "after");
    try {
      errorManager.register(errorKey, IErrorManager.sBEAN_ERROR, "Error while getting PROCESS_MANAGER_BEAN");
      ProcessManager pm = getProcessManagerBean();

      errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error while getting subpids");
      int[] subpids = pm.getSubPidsInBlock(userInfo, procData, this);

      if (subpids == null || subpids.length < 2) {
        throw new Exception("Error while getting subpids");
      }

      ProcessData[] processes = new ProcessData[2];

      Logger.info(userInfo.getUtilizador(),this,"after","current subpid=" + subpid + "; master subpid=" + subpids[MASTER] + "; slave subpid=" + subpids[SLAVE]);

      processes[MASTER] = procData;
      processes[SLAVE] = procData;
      if (subpid == subpids[MASTER]) {
        ProcessHeader phtmp = new ProcessHeader(flowid, pid, subpids[SLAVE]);
        ProcessData pdtmp = pm.getProcessData(userInfo, phtmp, Const.nOPENED_PROCS_READONLY);
        processes[SLAVE] = pdtmp != null ? pdtmp : procData;
      } 
      else {
        ProcessHeader phtmp = new ProcessHeader(flowid, pid, subpids[MASTER]);
        ProcessData pdtmp = pm.getProcessData(userInfo, phtmp, Const.nOPENED_PROCS_READONLY);
        processes[MASTER] = pdtmp != null ? pdtmp : procData;
      }

      errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error while mapping variables");

      HashMap<String,String> hmAttr = this.getAttributeMap();
      Iterator<String> it = hmAttr.keySet().iterator();
      while (it.hasNext()) {
        String var = (String)it.next();
        if (var == null || var.equals("") || 
            (var.length() >= 5 && var.startsWith(sPREFIX))) continue;


        String subFlowVar = (String)hmAttr.get(var);

        if (processes[MASTER].isListVar(var)) {
          processes[MASTER].clearList(var);
          ProcessListVariable master = processes[MASTER].getList(var);
          ProcessListVariable slave = processes[SLAVE].getList(subFlowVar);
          for (ProcessListItem item : slave.getItems()) {
            master.setItem(item);
          }
          processes[MASTER].setList(master);

          Logger.debug(userInfo.getUtilizador(),this,"after","mapping list: Sub[" + subFlowVar + "] => Master[" + var + "]");
        }
        else {
          processes[MASTER].parseAndSet(var, processes[SLAVE].getFormatted(subFlowVar));
          Logger.debug(userInfo.getUtilizador(),this,"after","mapping: Sub[" + subFlowVar + "] => Master[" + var + "]");
        } 
      }

      procData = processes[MASTER];
      procData.setSubPid(subpids[MASTER]);

      errorManager.register(errorKey, IErrorManager.sBEAN_ERROR,"Error while getting FLOW_BEAN");

      errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error while getting processActivities from the selected SubProcess");
      // get all the activities from the selected subProcess
      @SuppressWarnings("unused")
      int KEEP = MASTER;
      @SuppressWarnings("unused")
      int REMOVE = SLAVE;
      ListIterator<Activity> liActiv = null;
      if (sActivValue != null && saACTIV[2].equals(sActivValue)) {
        KEEP = SLAVE;
        REMOVE = MASTER;
        Logger.debug(userInfo.getUtilizador(),this,"after","keeping slave activities");
      }
      else {
        KEEP = MASTER;
        REMOVE = SLAVE;
        Logger.debug(userInfo.getUtilizador(),this,"after","keeping master activities");
      }
      liActiv = pm.getProcessActivities(userInfo, flowid, pid, subpids[MASTER]);

      errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error while ending slave process and corresponding activities");

      // close slave process &
      // delete all slave activities
      getFlowBean().endProc(userInfo, processes[SLAVE]);
      Logger.info(userInfo.getUtilizador(),this,"after","ended subproc " + subpids[SLAVE]);

      errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error while saving dataSet");

      // saves the new process before creating activities
      this.saveDataSet(userInfo, procData);

      errorManager.register(errorKey, IErrorManager.sGENERIC_ERROR, "Error while rescheduling new activities");
      // reschedule the activities
      if (liActiv != null) {

        // Update activity description/values?
        pm.deleteAllActivities(userInfo, new ProcessHeader(procData.getFlowId(), procData.getPid(), subpids[MASTER]));
        List<Activity> list = new ArrayList<Activity>();
        while (liActiv.hasNext()) {
          Activity a = (Activity)liActiv.next();
          a.subpid = procData.getSubPid();
          a.url = Block.getDefaultUrl(userInfo, procData);
          a.description = this.getDescription(userInfo, procData);
          a.status = 0;
          a.mid = procData.getMid();
          list.add(a);
        }
        pm.createActivities(userInfo, list.listIterator());
      }

    } catch (Exception e) {
      errorManager.fire(errorKey);
      Logger.error(userInfo.getUtilizador(),this,"after",
          procData.getSignature() + "Exception updating activities " + e.getMessage(), e);
      retObj = portError;
    }
    errorManager.close(errorKey);

    return retObj;
  }


  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Sa&iacute; SubFluxo " + this.getSubFlowFilename());
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Sa&iacute; SubFluxo " + this.getSubFlowFilename() + " Conclu&iacute;da.");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return Block.getDefaultUrl(userInfo, procData);
  }
}
