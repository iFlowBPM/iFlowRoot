package pt.iflow.blocks;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockIsInProfiles</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockIsInProfiles extends Block {
  public Port portIn, portTrue, portFalse, portNone;

  private static final String sCOND_PREFIX = "cond";
  private static final String sPROF_PREFIX = "prof";
  private static final String sMSG_PREFIX = "msg";

  public BlockIsInProfiles(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = true;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[3];
    retObj[0] = portTrue;
    retObj[1] = portFalse;
    retObj[2] = portNone;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
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
    Port outPort = portNone;
    StringBuffer logMsg = new StringBuffer();

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    Logger.trace(this,"after",login + " call with flowid="+flowid + ", pid=" + pid + ", subpid="+subpid);

    try {
      String sCond = null;
      String sProf = null;
      String sMsg = null;

      String sMessage = null;
      boolean btmp = false;
      boolean bNone = true;

      for (int ii=0; (sCond = this.getAttribute(sCOND_PREFIX + ii)) != null; ii++) {
        sProf = this.getAttribute(sPROF_PREFIX + ii);
        try {
          sMsg = procData.transform(userInfo, this.getAttribute(sMSG_PREFIX + ii));
          if (sMsg == null) {
            sMsg = "";
          }
        } catch (Exception e1) {
          sMsg = "";
        }

        //					btmp = ConditionParser.evaluate(sCond,dataSet);
        try {
          btmp = procData.query(userInfo, sCond);
        } catch (Exception ei) {
          btmp = true;
          Logger.error(login,this,"after", "caught exception evaluation beanshell (assuming true): " + ei.getMessage(), ei);
        }

        Logger.debug(login,this,"after", sCond + " evaluated " + btmp);

        if (btmp) {
          // condition verifies

          bNone = false;

          if (userInfo.hasProfile(sProf)) {
            // user has profile... TRUE
            sMessage = null;
            break;
          } else {
            // user has not profile... FALSE
            sMessage = sMsg;
          }
        }
      }

      if (!bNone) {
        if (sMessage == null) {
          procData.clearError();
          outPort = portTrue;
        } else {
          if (!sMessage.equals("")) {
            procData.setError(sMessage);
          }
          outPort = portFalse;
        }
      } else {
        procData.clearError();
        outPort = portNone;
      }
    } catch (Exception e) {
      Logger.error(login,this,"after","exception caught: " + e.getMessage(), e);
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "IsInProfiles");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "IsInProfiles Concluído");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
