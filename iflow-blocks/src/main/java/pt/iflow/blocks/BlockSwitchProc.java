package pt.iflow.blocks;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockSwitchProc</p>
 * <p>Description: Bloco responsavel pela transicao para outro processo</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class BlockSwitchProc extends Block {
  public Port portIn, portError;

  public static final String sPID_VAR = "PidVarName";
  public static final String sSUBPID_VAR = "SubPidVarName";

  public BlockSwitchProc(int anFlowId,int id, int subflowblockid, String filename) {
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
      
      String sPidVar = this.getAttribute(sPID_VAR);
      String sSubPidVar = this.getAttribute(sSUBPID_VAR);
      
      sPidVar = procData.transform(userInfo, sPidVar);
      int newpid = (int)Double.parseDouble(sPidVar);

      sSubPidVar = procData.transform(userInfo, sSubPidVar);
      int newsubpid = (int)Double.parseDouble(sSubPidVar);
      
      Activity a = pm.getUserProcessActivity(userInfo,procData.getFlowId(),newpid, newsubpid);

      if (a != null) {
        page = a.url;
        Logger.debug(login, this, "before",
            procData.getSignature() + "Found activity for pid/subpid: " + newpid + "/" + newsubpid);
      }
      else {
        Logger.warning(login, this, "before",
            procData.getSignature() + "No activity found for selected pid/subpid: " + newpid + "/" + newsubpid);	  
      }
    }
    catch (Exception e) {
      Logger.error(login, this, "before",
          procData.getSignature() + "Caught exception: " + e.getMessage());
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
    String retObj = "Transição de Processo.";
    return this.getDesc(userInfo,procData,true,retObj);
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    String retObj = "Transição de Processo Concluída.";
    return this.getDesc(userInfo, procData,false,retObj);
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
