package pt.iflow.blocks;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.notification.NotificationManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>
 * Title: BlockNotification
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author Oscar Lopes
 */

public class BlockNotification extends Block {
  public Port portIn, portSuccess, portError;

  protected final static String sNOTIFICATION_TYPE = "type"; //$NON-NLS-1$
  protected final static String sNOTIFICATION_TO = "to"; //$NON-NLS-1$
  protected final static String sNOTIFICATION_TO_PROFILE = "profile"; //$NON-NLS-1$
  protected final static String sNOTIFICATION_MESSAGE = "message"; //$NON-NLS-1$

  protected static final String toTypeUser = "user"; //$NON-NLS-1$
  protected static final String toTypeProfile = "profile"; //$NON-NLS-1$
  protected static final String toTypeTextProfile = "textProfile"; //$NON-NLS-1$
  protected static final String toTypeIntervenients = "intervenient"; //$NON-NLS-1$
  
  protected static final String slinkDetalhe = "linkDetalhe";

  protected static final String sSEPARATOR = ";"; //$NON-NLS-1$
  
  public BlockNotification(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portError;
    StringBuffer logMsg = new StringBuffer();
    String login = userInfo.getUtilizador();
    
    String link = getAttribute(slinkDetalhe);
    
    try {
      String type = getAttribute(sNOTIFICATION_TYPE);
      Set<String> usersToNotify = new HashSet<String>();
      String to = getAttribute(sNOTIFICATION_TO);
      if (StringUtils.isNotEmpty(to)) {
        try {
          to = procData.transform(userInfo, getAttribute(sNOTIFICATION_TO));
        }
        catch (Exception ee) {        
        }
      }

      AuthProfile ap = BeanFactory.getAuthProfileBean();

      if (toTypeUser.equals(type)) {
        // get all users
        String [] users = to.split(sSEPARATOR);
        for (String user : users) {
          if(StringUtils.isNotEmpty(user)) {
            usersToNotify.add(user);
          }
        }
      } else if (toTypeProfile.equals(type)) {
        to = getAttribute(sNOTIFICATION_TO_PROFILE);
        // get all users in profile
        Collection<String> users = ap.getUsersInProfile(userInfo, to);
        if(null != users) 
          usersToNotify.addAll(users);
      } else if (toTypeTextProfile.equals(type)) {
        // get all users in profile(s)
        String [] profiles = to.split(sSEPARATOR);
        for (String profile : profiles) {
          if(StringUtils.isNotEmpty(profile)) {
            Collection<String> users = ap.getUsersInProfile(userInfo, profile);
            if(null == users) continue;
            usersToNotify.addAll(users);
          }
        }
      } else if (toTypeIntervenients.equals(type)) {
        // get all intervenientes
        Collection<String> users = BeanFactory.getProcessManagerBean().getProcessIntervenients(userInfo, procData);
        if(null != users) usersToNotify.addAll(users);
        usersToNotify.add(login);
      }
      
      if(usersToNotify.size() == 0) {
        // No users to notify
        Logger.warning(login, this, "after", "No users to notify.");
        outPort = portError;
      } else {
        IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, procData.getFlowId());
        String application = fd.getApplicationName();
        if (null == application)
          application = "";
        String procId = "";
        if (Const.nSESSION_PID != procData.getPid())
          procId = procData.getPid() + "," + procData.getSubPid();
        String from = "process[" + application + "/" + fd.getName() + ":" + procId + "]";

        String linkparams = "";
        if(Boolean.parseBoolean(link))
        	linkparams = procData.getFlowId() + "," + procData.getPid() + "," + procData.getSubPid();
        else
        	linkparams = "false";
        
        String message = procData.transform(userInfo, getAttribute("message"));
        int r = BeanFactory.getNotificationManagerBean().notifyUsers(userInfo, from, usersToNotify, message, linkparams);
        if (r == NotificationManager.NOTIFICATION_OK) {
          outPort = portSuccess;
        }
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", 
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portError;
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Notificacao");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Utilizador Notificado");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
