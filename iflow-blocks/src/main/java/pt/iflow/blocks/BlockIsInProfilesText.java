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

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockIsInProfilesText</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockIsInProfilesText extends Block {
  public Port portIn, portTrue, portFalse, portNone;

  private static final String sCOND_PREFIX = "cond";
  private static final String sPROF_PREFIX = "prof";
  private static final String sMSG_PREFIX = "msg";

  public BlockIsInProfilesText(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = false;
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

    Logger.trace(this,"after",login + " call with flowid="+flowid + ", pid=" + pid +", subpid="+subpid);

    try {
      String sCond = null;
      String sProf = null;
      String sMsg = null;

      String sMessage = null;
      boolean btmp = false;
      boolean bNone = true;

      for (int ii=0; (sCond = this.getAttribute(sCOND_PREFIX + ii)) != null; ii++) {
        try {
          sProf = procData.transform(userInfo, this.getAttribute(sPROF_PREFIX + ii));
          if (sProf == null) {
            sProf = "";
          }
        } catch (Exception e1) {
          sProf = "";

        }
        try {
          sMsg = procData.transform(userInfo, this.getAttribute(sMSG_PREFIX + ii));
          if (sMsg == null) sMsg = "";
        } catch (Exception e1) {
          sMsg = "";

        }
        try {
          btmp = procData.query(userInfo, sCond);
        } catch (Exception ei) {
          btmp = true;
          Logger.error(login,this,"after", "caught exception evaluation beanshell (assuming true): " + ei.getMessage(), ei);
        }
        Logger.debug(login,this,"after", sCond + " evaluated " + btmp + ", profile = " + sProf);
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
      Logger.error(login,this,"after","exception caught: " + e.getMessage());
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "IsInProfilesText");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "IsInProfilesText Concluído");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
