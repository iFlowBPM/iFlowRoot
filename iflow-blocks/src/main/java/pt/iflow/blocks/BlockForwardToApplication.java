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
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockForwardToApplication</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 */

public class BlockForwardToApplication extends Block {
  public Port portIn, portSuccess, portError;


  public BlockForwardToApplication(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    //setId(id);
    hasInteraction = true;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
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
   * action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    // Variables
    int flowid = procData.getFlowId();
    int pid    = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    String description = this.getDescription(userInfo, procData);
    String url         = this.getUrl(userInfo, procData);

    Logger.trace(this,"before",login + " call with subpid="+subpid+",pid="+pid+",flowid="+flowid);

    String nextPage = url;

    try {
      // Get the ProcessManager EJB
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      Activity activity = new Activity(login,flowid,pid,subpid,0,0,description,Block.getDefaultUrl(userInfo, procData),1);
      activity.mid = procData.getMid();
      pm.updateActivity(userInfo,activity);
      Logger.logFlowState(userInfo, procData, this, "Process forwarded to application: " + activity.getUrl());
    }
    catch (Exception e) {
      Logger.error(login, this, "before", 
          procData.getSignature() + "Caught an unexpected exception scheduling activities: " + e.getMessage(), e);
    }

    return nextPage;
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

    return portSuccess;
   

  }


  public String getForwardUrl(UserInfoInterface userInfo, ProcessData process) {
    String url = this.getAttribute("url");
    try {
      String stmp = process.transform(userInfo, url);
      if (stmp != null && !stmp.equals("")) {
        if (stmp.indexOf("?") >= 0) {
          stmp += "&";
    }
    else {
          stmp += "?";
        }
        stmp += "userId=" + userInfo.getUtilizador()
         + "&sessionId=" + userInfo.getIntranetSessionId();
	    return stmp;
      } 
    }
    catch (Exception e) {
    }
    return url;
  }


  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
  
  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {    
    // FIXME CHECK THIS!!
    throw new UnsupportedOperationException(); 
    
//    int flowid   = procData.getFlowId();
//    int pid      = procData.getPid();
//    int subpid   = procData.getSubPid();
//    return "BSP/ForwardToApplication/entrada.jsp?flowid="+ flowid+"&pid="+ pid+"&subpid="+subpid;
  }

  public Object execute (int op, Object[] aoa) {
    Object retObj = null;

    switch (op) {
    case 1:
      // String getForwardUrl(UserInfo, DataSet)
      retObj = this.getForwardUrl((UserInfoInterface)aoa[0],(ProcessData)aoa[1]);
      break;
    default:
    }

    return retObj;
  }

  
}
