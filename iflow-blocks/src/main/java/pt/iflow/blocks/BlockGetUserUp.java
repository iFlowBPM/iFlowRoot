package pt.iflow.blocks;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockGetUserUp</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockGetUserUp extends Block {
  public Port portIn, portSuccess, portTop, portError;

  private final static String sUSER = "User";
  private final static String sUSER_UP_VAR_NAME = "UserUpVarName";


  public BlockGetUserUp(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = true;
    canRunInPopupBlock = false;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[3];
    retObj[0] = portSuccess;
    retObj[1] = portTop;
    retObj[2] = portError;
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
    Port outPort = portSuccess;
    StringBuffer logMsg = new StringBuffer();

    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    Logger.trace(this,"after",login + " call with flowid="+flowid + ", pid=" + pid+", subpid="+subpid);

    boolean bOk = false;
    boolean bTop = false;

    try {
      // Get the AuthProfile EJB
      AuthProfile ap = BeanFactory.getAuthProfileBean();

      String sUserAttr = getAttribute(BlockGetUserUp.sUSER);
      String sUser = login;
      if (StringUtils.isNotEmpty(sUserAttr)) {
        try {
          Logger.debug(login,this,"after","user attr: " + sUserAttr);
          sUser = procData.transform(userInfo, sUserAttr);
          Logger.debug(login,this,"after","going to get userup for user=" + sUser);
        } catch (Exception e) {
          Logger.warning(login, this, "after", 
              procData.getSignature() + "exception transforming user attr: " + sUserAttr + 
              ". Assuming current user: " + login);
        }
      }
      
      String sUserUp = ap.getUpperNode(sUser);

      bOk = true;

      Logger.debug(login,this,"after","userUp=" + sUserUp + " for user " + sUser);

      if (sUserUp == null) {
        bTop = true;
        Logger.debug(login,this,"after","reached top");
      } else {
        String sVar = getAttribute(BlockGetUserUp.sUSER_UP_VAR_NAME);
        if (StringUtils.isNotEmpty(sVar)) {
          procData.parseAndSet(sVar, sUserUp);
          logMsg.append("Set '" + sVar + "' as '" + sUserUp + "' for user " + sUser + ";");
          Logger.debug(login,this,"after","set " + sVar + "=" + sUserUp + " for user " + sUser);
        } else {
          // ooops
          throw new Exception("user up var name is null or empty...");	  
        }
      }
    } catch (Exception e) {
      Logger.error(login,this,"after",
          procData.getSignature() + "exception caught: " + e.getMessage(), e);
      bOk = false;
    }

    if (bOk) {
      if (bTop) {
        outPort = portTop;
      } else {
        outPort = portSuccess;
      }
    } else {
      outPort = portError;
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Get User Up");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Upper user fetched");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
