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
import java.sql.SQLException;

import javax.sql.DataSource;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * <p>
 * Title: BlockLaunchAsyncEvent
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author pussman
 * @version 1.0
 */

public class BlockEventTrigger extends Block {
    private static final String sQueryNoBlock = "UPDATE iflow.event_data set processed=0 WHERE type='AsyncWait' AND fid=? AND pid=? AND subpid=? ;";
    private static final String sQueryWithBlock = "UPDATE iflow.event_data set processed=0 WHERE type='AsyncWait' AND fid=? AND pid=? AND subpid=? AND blockid=? ;";
    public static final String sFLOWID = "flowid";
    public static final String sPID = "pid";
    public static final String sSUBPID = "subPid";
    public static final String sBLOCKID = "blockid";
    public static final String sISASYNCHRONOUS = "isAsynchronous";
    public Port portIn, portOk, portEmpty, portError;

    public BlockEventTrigger(int anFlowId, int id, int subflowblockid, String filename) {
        super(anFlowId, id, subflowblockid, filename);
        isCodeGenerator = true;
        hasInteraction = false;
    }

    public Port[] getOutPorts(UserInfoInterface userInfo) {
        Port[] retObj = new Port[3];
        retObj[0] = portOk;
        retObj[1] = portEmpty;
        retObj[2] = portError;
        return retObj;
    }

    public Port getEventPort() {
        return null;
    }

    public Port[] getInPorts(UserInfoInterface userInfo) {
        Port[] retObj = new Port[1];
        retObj[0] = portIn;
        return retObj;
    }

    /**
     * Update activities and assign tasks to event user
     * 
     * @return always 'true'
     */
    public String before(UserInfoInterface userInfo, ProcessData procData) {
        return Block.getDefaultUrl(userInfo, procData);
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
     * Executes the block main action
     * 
     * @param dataSet
     *            a value of type 'DataSet'
     * @return the port to go to the next block
     */
    public Port after(UserInfoInterface userInfo, ProcessData procData) {
        Port outPort = portOk;
        StringBuffer logMsg = new StringBuffer();
        String login = userInfo.getUtilizador();
        String flowid = "-1", pid = "-1", subpid = "-1", blockid = "-1";
        DataSource ds = null;
        Connection db = null;
        PreparedStatement pst = null;
        String sDataSource = null;

        // Get and check correction of flowid, pid, subpid of process to wake up
        try {
            flowid = "" + procData.eval( userInfo, this.getAttribute(sFLOWID));
            pid = "" + procData.eval( userInfo, this.getAttribute(sPID));
            subpid= "" + procData.eval( userInfo, this.getAttribute(sSUBPID));
            blockid = "" + procData.eval( userInfo, this.getAttribute(sBLOCKID));

            if (flowid == null || pid == null || subpid == null || Integer.parseInt(flowid) < 0
                    || Integer.parseInt(pid) < 0 || Integer.parseInt(subpid) < 0) {
                Logger.error(login, this, "after", procData.getSignature() + "Error in flowid, pid, subpid: " + flowid
                        + "," + pid + "," + subpid);
                outPort = portError;
            }
        } catch (Exception e) {
            Logger.error(login, this, "after", procData.getSignature() + "Error getting flowid, pid, subpid");
            outPort = portError;
        }

        // Set event READY_TO_PROCESS
        if (outPort != portError) {
            try {
                ds = Utils.getDataSource();
                if (null == ds) {
                    Logger.error(login, this, "after", procData.getSignature() + "null datasource for " + sDataSource);
                    outPort = portError;
                } else {
                    db = ds.getConnection();
                    if (blockid == null || blockid.equals("") || blockid.equals("null")) {
                        pst = db.prepareStatement(sQueryNoBlock.toString());
                        pst.setInt(1, Integer.parseInt(flowid));
                        pst.setInt(2, Integer.parseInt(pid));
                        pst.setInt(3, Integer.parseInt(subpid));
                    } else {
                        pst = db.prepareStatement(sQueryWithBlock.toString());
                        pst.setInt(1, Integer.parseInt(flowid));
                        pst.setInt(2, Integer.parseInt(pid));
                        pst.setInt(3, Integer.parseInt(subpid));
                        pst.setInt(4, Integer.parseInt(blockid));
                    }

                    Logger.debug(login, this, "after", "Going to set event READY_TO_PROCESS flowid, pid, subpid: "
                            + flowid + "," + pid + "," + subpid);
                    int nCols = pst.executeUpdate();
                    Logger.debug(login, this, "after", "Number of updated columns = " + nCols);

                    if (nCols == 0) {
                        outPort = portEmpty;
                        Logger.debug(login, this, "after", "No AsyncWait event was found for flowid, pid, subpid: "
                                + flowid + "," + pid + "," + subpid);
                    }

                }
            } catch (SQLException sqle) {
                Logger.error(login, this, "after", "caught sql exception: " + sqle.getMessage(), sqle);
                outPort = portError;
            } catch (Exception e) {
                Logger.error(login, this, "after", "caught exception: " + e.getMessage(), e);
                outPort = portError;
            } finally {
                DatabaseInterface.closeResources(db, pst);
            }

        }

        logMsg.append("Using '" + outPort.getName() + "';");
        Logger.logFlowState(userInfo, procData, this, logMsg.toString());
        return outPort;
    }

    public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
        return this.getDesc(userInfo, procData, true, "Evento");
    }

    public String getResult(UserInfoInterface userInfo, ProcessData procData) {
        return this.getDesc(userInfo, procData, false, "Evento Efectuado");
    }

    public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
        return "";
    }
}
