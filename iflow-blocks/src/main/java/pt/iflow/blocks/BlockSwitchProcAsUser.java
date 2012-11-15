package pt.iflow.blocks;

import java.util.Iterator;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockSwitchProcAsUser</p>
 * <p>Description: Bloco responsavel pela transicao para outro processo noutro fluxo, criando uma actividade para o user que executa.
 * <b>NOTE: USE WITH CAUTION!!!</b></p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class BlockSwitchProcAsUser extends Block {
  public Port portIn, portError;

  public static final String sFID_VAR = "FlowIdVarName";
  public static final String sPID_VAR = "PidVarName";
  public static final String sSUBPID_VAR = "SubPidVarName";

  public BlockSwitchProcAsUser(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = true;
    bProcInDBRequired = false;
    canRunInPopupBlock = false;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portError;
    return retObj;
  }

  /**
   * @return
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    String page = this.getUrl(userInfo, procData);
    String login = userInfo.getUtilizador();

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      String sFidVar = null;
      String sPidVar = null;
      String sSubPidVar = null;
      int newfid = -1;
      int newpid = -1;
      int newsubpid = -1;

      try {
        sFidVar = this.getAttribute(sFID_VAR);
        sFidVar = procData.transform(userInfo, sFidVar);
        newfid = (int)Double.parseDouble(sFidVar);
      }
      catch (Exception e2) {
        Logger.error(login, this, "before",
            procData.getSignature() + "Caught exception processing FlowID variable: "
            + e2.getMessage(), e2);
      }

      try {
        sPidVar = this.getAttribute(sPID_VAR);
        sPidVar = procData.transform(userInfo, sPidVar);
        newpid = (int)Double.parseDouble(sPidVar);
      }
      catch (Exception e2) {
        Logger.error(login, this, "before",
            procData.getSignature() + "Caught exception processing PID variable: "
            + e2.getMessage(), e2);
      }

      try {
        sSubPidVar = this.getAttribute(sSUBPID_VAR);
        sSubPidVar = procData.transform(userInfo, sSubPidVar);
        newsubpid = (int)Double.parseDouble(sSubPidVar);
      }
      catch (Exception e2) {
        Logger.error(login, this, "before",
            procData.getSignature() + "Caught exception processing SUBPID variable: "
            + e2.getMessage(), e2);
      }

      if (newfid > -1 && newpid > -1) {
        Iterator<Activity> li = pm.getProcessActivities(userInfo, newfid, newpid, newsubpid);
        Activity a = null;
        boolean bCreate = false;

        if (li != null) {
          Activity atmp = null;
          bCreate = true;

          while (li.hasNext()) {
            atmp = (Activity)li.next();
            if (atmp.userid.equals(login)) {
              // user already has an activity scheduled for this proc.
              bCreate = false;
            }
            if (atmp.url != null && !atmp.url.equals("")) {
              a = atmp;
            }
            if (!bCreate && a != null) {
              // no more relevant info...
              break;
            }
          }
        }

        if (a != null) {
          page = a.url;

          if (bCreate) {
            a.userid = login;
            a.notify = false;
            a.mid = procData.getMid();
            
            pm.createActivity(userInfo, a);
          }
        }
        else {
          Logger.warning(login, this, "before",
              procData.getSignature() + 
              "No activity found for selected flow and pid: " + newfid + ";" + newpid + ";" + newsubpid);	  
        }
      }
    }
    catch (Exception e) {
      Logger.error(login, this, "before",
          procData.getSignature() + "Caught exception: " + e.getMessage(), e);
    }

    return page;
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    return portError;
  }


  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    String retObj = "Transição de Processo como utilizador " + userInfo.getUtilizador() + ".";
    return this.getDesc(userInfo,procData,true,retObj);
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    String retObj = "Transição de Processo como utilizador " + userInfo.getUtilizador() + " concluída.";
    return this.getDesc(userInfo, procData, false, retObj);
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    int flowid = procData.getFlowId();
    int pid    = procData.getPid();
    int subpid = procData.getSubPid();
    return "SwitchProc/switch.jsp?flowid="+ flowid+"&pid="+ pid+"&subpid="+subpid;
  }


  public Object execute (int op, Object[] aoa) {
    Object retObj = null;
    return retObj;
  }
}
