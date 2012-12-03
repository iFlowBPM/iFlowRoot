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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.MessageBlock;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.blocks.msg.Messages;

/**
 * <p>
 * Title: BlockForwardUp
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

public class BlockForwardUp extends Block implements MessageBlock {
    public Port portIn, portSuccess, portTop, portError;
    
    public final static String sFORWARD_UP_ERROR = "FORWARD_UP_ERROR";
    public final static String sFORWARD_UP_TOP = "FORWARD_UP_TOP";
    public final static String sJSP = "Forward/forward.jsp";
    
    public BlockForwardUp(int anFlowId, int id, int subflowblockid,
            String filename) {
        super(anFlowId, id, subflowblockid, filename);
        hasInteraction = false;
        bProcInDBRequired = true;
        hasEvent = true;
        isForwardBlock = true;
    }
    
    public Port[] getOutPorts(UserInfoInterface userInfo) {
        Port[] retObj = new Port[3];
        retObj[0] = portSuccess;
        retObj[1] = portTop;
        retObj[2] = portError;
        return retObj;
    }
    
    public Port getEventPort() {
        return portEvent;
    }
    
    public Port[] getInPorts(UserInfoInterface userInfo) {
        Port[] retObj = new Port[1];
        retObj[0] = portIn;
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
        
        StringBuffer logMsg = new StringBuffer();
        int flowid = procData.getFlowId();
        int pid = procData.getPid();
        int subpid = procData.getSubPid();
        String login = userInfo.getUtilizador();
        
        Logger.trace(this, "after", login + " call with flowid=" + flowid
                + ", pid=" + pid + ", subpid=" + subpid);
        
        boolean bOk = false;
        boolean bTop = false;
        
        try {
            ProcessManager pm = BeanFactory.getProcessManagerBean();
            
            if (pid < 0) {
                throw new Exception("Invalid pid");
            }
            if (subpid < 0) {
                throw new Exception("Invalid subpid");
            }
            
            String sUserUp = this.getOwnerManager(userInfo, procData);
            
            if (sUserUp == null) {
                bTop = true;
                bOk = true;
            } else {
                bOk = pm.forwardToUser(userInfo, procData, sUserUp, this.getDescription(userInfo, procData));
                if (bOk) {
                  logMsg.append("Process forwarded to '" + sUserUp + "', using '" + portSuccess.getName() + "';");
                }
            }
        } catch (Exception e) {
            Logger.error(login, this, "before", 
                procData.getSignature() + "exception caught: " + e.getMessage(), e);
            bOk = false;
        }
        
        if (!bOk) {
            logMsg.append("Error forwarding process up, using '" + portError.getName() + "';");
            procData.setTempData(sFORWARD_UP_ERROR, "true");
        } else if (bTop) {
            logMsg.append("Forwarding process up, using '" + portTop.getName() + "';");
            procData.setTempData(sFORWARD_UP_TOP, "true");
        }

        Logger.logFlowState(userInfo, procData, this, logMsg.toString());
        return this.getUrl(userInfo, procData);
    }
    
    private String getOwnerManager(UserInfoInterface userInfo, ProcessData procData) {
      AuthProfile ap = BeanFactory.getAuthProfileBean();
      ProcessManager pm = BeanFactory.getProcessManagerBean();
      int flowid = procData.getFlowId();
      int pid = procData.getPid();
      int subpid = procData.getSubPid();
      String userid = userInfo.getUtilizador();
      
      String[] activityOwners = pm.getActivityOwners(userInfo,
          new Activity(userid, flowid, pid, subpid, null, null, null));
      boolean isActivityOwner = false;
      
      for (String actOwn : activityOwners) {
        if (StringUtils.equals(userid, actOwn)) {
          isActivityOwner = true;
          break;
        }
      }
      
      if (!isActivityOwner) {
        final String ownerQry = "select ownerid from activity_hierarchy "
          + "where pending = 0 and expires >= ? and userid = ? and flowid = ?";
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
          connection = Utils.getDataSource().getConnection();
          pstmt = connection.prepareStatement(ownerQry);
          pstmt.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
          pstmt.setString(2, userid);
          pstmt.setInt(3, flowid);
          rs = pstmt.executeQuery();
          if (rs.next()) {
            userid = rs.getString("ownerid");
          }
        } catch (Exception e) {
          Logger.error(userid, this, "after", "[" + flowid + "," + pid + "," + subpid + "] caught exception: " + e.getMessage(), e);
        } finally {
          DatabaseInterface.closeResources(connection, pstmt, rs);
        }
      }
      return ap.getUpperNode(userid);
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
        
        if (StringUtils.isEmpty(procData.getTempData(sFORWARD_UP_ERROR))
                && StringUtils.isEmpty(procData.getTempData(sFORWARD_UP_TOP))) {
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
        
        if (StringUtils.isNotEmpty(procData.getTempData(sFORWARD_UP_ERROR))) {
            outPort = portError;
            procData.setTempData(sFORWARD_UP_ERROR, null);
        } else if (StringUtils
                .isNotEmpty(procData.getTempData(sFORWARD_UP_TOP))) {
            outPort = portTop;
            procData.setTempData(sFORWARD_UP_TOP, null);
        } else {
            outPort = portSuccess;
        }
        
        return outPort;
    }
    
    public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
      Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));
      return this.getDesc(userInfo, procData, true, msg.getString("BlockForwardUp.defaultDesc"));
    }

    public String getResult(UserInfoInterface userInfo, ProcessData procData) {
      Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));
      return this.getDesc(userInfo, procData, false, msg.getString("BlockForwardUp.defaultResult"));
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
