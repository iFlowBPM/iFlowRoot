package pt.iflow.blocks;

import org.apache.commons.lang.StringEscapeUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockCheckAuthentication</p>
 * <p>Description: Checks a user's authentication</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockCheckAuthentication extends Block {
  public Port portIn, portTrue, portFalse;

  private final static String LOGIN_ATTR = "LoginVar";
  private final static String PASS_ATTR = "PasswordVar";


  public BlockCheckAuthentication(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
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
    Port[] retObj = new Port[2];
    retObj[0] = portTrue;
    retObj[1] = portFalse;
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
    Port outPort = portFalse;

    try {

      String sLogin = null;
      String sPass = null;

      sLogin = this.getAttribute(LOGIN_ATTR);
      sLogin = StringEscapeUtils.unescapeHtml(procData.transform(userInfo, sLogin));

      try {
        sPass = StringEscapeUtils.unescapeHtml(procData.transform(userInfo, this.getAttribute(PASS_ATTR)));
      }
      catch (Exception ee) {}

      AuthProfile ap = BeanFactory.getAuthProfileBean();

      boolean bOk = ap.checkUser(sLogin, sPass);

      Logger.info(userInfo.getUtilizador(),this,"after", 
		  "[" + procData.getFlowId() + "," + procData.getPid() + "," + procData.getSubPid() + "] " 
		  + "checked authentication for " + sLogin + ": " + bOk);

      if (bOk) {
        outPort = portTrue;
      }
      else {
        outPort = portFalse;
      }
    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(),this,"after",
          procData.getSignature() + "Exception caught: " + e.getMessage());
      e.printStackTrace();
    }

    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Checking user authentication");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Checked user authentication");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
