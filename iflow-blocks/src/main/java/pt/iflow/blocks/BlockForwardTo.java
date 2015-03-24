package pt.iflow.blocks;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.MessageBlock;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.blocks.msg.Messages;

/**
 * <p>
 * Title: BlockForwardTo
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author
 */

public class BlockForwardTo extends Block implements MessageBlock {
    public Port portIn, portSuccess, portError;
    
    public final static String sFORWARD_TO_NONE = "Escolha";
    public final static String sFORWARD_TO_TYPE = "Tipo";
    public final static String sFORWARD_TO_PROFILE = "Perfil";
    public final static String sFORWARD_TO_USER = "Utilizador";
    public final static String sFORWARD_TO_PROFILE_TEXT = "PerfilTexto";
    public final static String sPROFILE_DELIM = ",";
    public final static String sJSP = "Forward/forward.jsp";
    public final static String sFORWARD_ERROR = "FORWARD_ERROR";

    public final static String sFORWARD_TO_UPDATE_LABEL_COND = "task_annotation_update_label_condition";
    public final static String sFORWARD_TO_UPDATE_LABEL = "task_annotation_update_label";

    public final static String sFORWARD_TO_UPDATE_FOLDER_COND = "task_folder_update_condition";
    public final static String sFORWARD_TO_UPDATE_FOLDER = "task_folder_update";
    
    public BlockForwardTo(int anFlowId, int id, int subflowblockid,
            String filename) {
        super(anFlowId, id, subflowblockid, filename);
        hasInteraction = false;
        bProcInDBRequired = true;
        hasEvent = true;
        isForwardBlock = true;
    }
    
    public Port[] getInPorts(UserInfoInterface userInfo) {
        Port[] retObj = new Port[1];
        retObj[0] = portIn;
        return retObj;
    }
    
    public Port getEventPort() {
        return portEvent;
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
     *            a value of type 'DataSet'
     * @return always 'true'
     */
    public String before(UserInfoInterface userInfo, ProcessData procData) {
        final ProcessManager pm = BeanFactory.getProcessManagerBean();

        StringBuffer logMsg = new StringBuffer();
        final int flowid = procData.getFlowId();
        final String login = userInfo.getUtilizador();
        String sType = getAttribute(BlockForwardTo.sFORWARD_TO_TYPE);
        String sProfile = getAttribute(BlockForwardTo.sFORWARD_TO_PROFILE);
        String sProfileText = getAttribute(BlockForwardTo.sFORWARD_TO_PROFILE_TEXT);
        String sUserMessage = getAttribute(MESSAGE_USER);
        String sUser = getAttribute(BlockForwardTo.sFORWARD_TO_USER);

        Logger.trace(this, "before", login + " call with type=" + sType
                + ", profile=" + sProfile + ", profileText=" + sProfileText
                + ", userMessage" + sUserMessage + ", sUser=" + sUser
                + ",flowid=" + flowid);
        
        boolean bOk = false;
        
        try {
            int pid = procData.getPid();
            
            if (pid < 0) {
                throw new Exception("Invalid pid");
            }
            
            int subpid = procData.getSubPid();
            
            if (subpid < 0) {
                throw new Exception("Invalid subpid");
            }
            
            if (sType.equals(BlockForwardTo.sFORWARD_TO_PROFILE)
                    || sType.equals(BlockForwardTo.sFORWARD_TO_PROFILE_TEXT)) {
                // first get ldap profile from repository
                
                String destinationProfile = null;
                
                if (sType.equals(BlockForwardTo.sFORWARD_TO_PROFILE_TEXT)) {
                    try {                    	
                        destinationProfile = StringEscapeUtils.unescapeHtml(procData.transform(userInfo, sProfileText));
                    } catch (Exception e) {
                      // not able to transform.. assume original text
                      Logger.warning(login, this, "after", 
                          procData.getSignature() + "exception transforming '" + sProfileText + 
                          "'. Using " + sProfileText, e);
                      destinationProfile = sProfileText;
                    }
                    
                } else {
                    destinationProfile = sProfile;
                }
                
                if (StringUtils.isEmpty(destinationProfile)) {
                    bOk = false;
                } else {
                    bOk = pm.forwardToProfile(userInfo, procData, destinationProfile, this.getDescription(userInfo, procData));
                    if (bOk) {
                      logMsg.append("Process forwarded to profile(s): " + destinationProfile + ";");
                    }
                }
            } else if (sType.equals(BlockForwardTo.sFORWARD_TO_USER)) {
                String destinationUser = null;
                try {
                    destinationUser = procData.transform(userInfo, sUser);
                } catch (Exception e) {
                  Logger.warning(login, this, "before", "Unable to transform variable \"" + sUser + "\"");
                }
                if (StringUtils.isNotEmpty(destinationUser)) {
                    bOk = pm.forwardToUser(userInfo, procData, destinationUser, this.getDescription(userInfo, procData));
                    if (bOk) {
                      logMsg.append("Process forwarded to user(s): " + destinationUser + ";");
                    }
                } else {
                    bOk = false;
                }
            }

        } catch (Exception e) {
            Logger.error(login, this, "before", 
                procData.getSignature() + "exception caught: " + e.getMessage(), e);
            bOk = false;
        }
        
        if (!bOk) {
            procData.setTempData(sFORWARD_ERROR, "true");
            logMsg.append("Using '" + portError.getName() + "';");
        } else {
          logMsg.append("Using '" + portSuccess.getName() + "';");
        }
        if(StringUtils.isNotBlank(logMsg.toString())) {
          Logger.logFlowState(userInfo, procData, this, logMsg.toString());
        }
        return this.getUrl(userInfo, procData);
    }
    
    /**
     * No action in this block
     * 
     * @param dataSet
     *            a value of type 'DataSet'
     * @return always 'true'
     */
    public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
        return true;
    }
    
    /**
     * Check if the block has interaction with the user or if it is only for
     * computation. Se nao tiver erro no forward, comporta-se como bloco de
     * interaccao para parar neste bloco, caso contrario, avanca para o porto de
     * erro
     * 
     * @return if the block has interaction.
     */
    public boolean hasInteraction(UserInfoInterface userInfo,
            ProcessData procData) {
        
        boolean retObj = false;
        
        if (StringUtils.isEmpty(procData.getTempData(sFORWARD_ERROR))) {
            retObj = true;
        }
        
        return retObj;
    }
    
    /**
     * Executes the block main action
     * 
     * @param dataSet
     *            a value of type 'DataSet'
     * @return the port to go to the next block
     */
    public Port after(UserInfoInterface userInfo, ProcessData procData) {
        
        Port outPort = null;
        
        if (StringUtils.isEmpty(procData.getTempData(sFORWARD_ERROR))) {
            outPort = portSuccess;
        } else {
            procData.setTempData(sFORWARD_ERROR, null);
            outPort = portError;
        }
        
        return outPort;
    }

    @Override
    public void onEventFired(UserInfoInterface userInfo, ProcessData procData) throws Exception {

      // Revert activities to previous users/profile
      
      String login = userInfo.getUtilizador();

      ProcessManager pm = BeanFactory.getProcessManagerBean();
            
      try {
        // get activities prior to forwarding
        List<Activity> prevActivities = pm.getPreviousActivities(userInfo, procData);

        // delete current activities (implies historify)
        pm.deleteAllActivities(userInfo, procData);

        // now create new activities for each prior ones
        for (Activity a : prevActivities) {
          a.mid = procData.getMid();
          a.created = new Date();
          a.started = new Date();
          a.archived = null;
          a.url = Block.getDefaultUrl(userInfo, procData);
          a.setUnread();
          pm.createActivity(userInfo, a);
        }
        Logger.info(login, this, "onEventFired", procData.getSignature() + "Regenerated activities");
      } 
      catch (Exception e) {
        Logger.warning(login, this, "onEventFired", 
            procData.getSignature() + "Error regenerating process activities", e);
        throw e;
      }
    }

    public String getDescription(UserInfoInterface userInfo,
        ProcessData procData) {

      Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));

      String sType = getAttribute(BlockForwardTo.sFORWARD_TO_TYPE);
      String sProfile = getAttribute(BlockForwardTo.sFORWARD_TO_PROFILE);
      String sUser = getAttribute(BlockForwardTo.sFORWARD_TO_USER);
      try {
        if (sType.equals(BlockForwardTo.sFORWARD_TO_PROFILE)) {
          String defDesc = msg.getString("BlockForwardTo.defaultDesc.forwardToProfile");
          if (StringUtils.isEmpty(defDesc)) {
            defDesc = "Forward To " + sType + " {0}";
          }
          try { sProfile = procData.transform(userInfo, sProfile); } catch (Exception e) {}
          return this.getDesc(userInfo, procData, true, MessageFormat.format(defDesc, sProfile));
        } else if (sType.equals(BlockForwardTo.sFORWARD_TO_USER)) {
          String defDesc = msg.getString("BlockForwardTo.defaultDesc.forwardToUser");
          if (StringUtils.isEmpty(defDesc)) {
            defDesc = "Forward To " + sType + " {0}";
          }
          try { sUser = procData.transform(userInfo, sUser); } catch (Exception e) {}
          return this.getDesc(userInfo, procData, true, MessageFormat.format(defDesc, sUser));
        }
      }
      catch (NullPointerException npe) {
      }
      String defDesc = msg.getString("BlockForwardTo.defaultDesc.forwardTo");
      if (StringUtils.isEmpty(defDesc)) {
        defDesc = "Forward To";
      }
      return this.getDesc(userInfo, procData, true, defDesc);
    }

    public String getResult(UserInfoInterface userInfo, ProcessData procData) {
      Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));    
      return this.getDesc(userInfo, procData, false, msg.getString("BlockForwardTo.defaultResult"));
    }
    
    public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
        int flowid = procData.getFlowId();
        int pid = procData.getPid();
        int subpid = procData.getSubPid();
        
        return sJSP + "?flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid;
    }

    public String getMessage(UserInfoInterface userInfo, ProcessData procData) {      
      return super.getMessage(userInfo, procData);
    }

    public String getMessage(UserInfoInterface userInfo, ProcessData procData, String defaultMessage) {
      return super.getMessage(userInfo, procData, defaultMessage);
    }

    public boolean hasMessage() {
      return super.hasMessage();
    }
}
