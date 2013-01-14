package pt.iflow.blocks;

import java.sql.Timestamp;
import java.util.Calendar;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.MessageBlock;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.transition.ReportTO;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>
 * Title: Block End
 * </p>
 * <p>
 * Description: Implements the end block.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author iKnow
 * @version 1.0
 */

public class BlockEnd extends Block implements MessageBlock {
    
    public Port portIn;
    
    public BlockEnd(int anFlowId, int id, int subflowblockid, String filename) {
        super(anFlowId, id, subflowblockid, filename);
    }
    
    public boolean isEndBlock() {
        return true;
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
        return null;
    }
    
    /**
     * Executes the actions in this block before the block is executed.
     * 
     * @return true if ok.
     */
    public String before(UserInfoInterface userInfo, ProcessData procData) {
        String login = userInfo.getUtilizador();
        
        Logger.trace(this, "before", login + " call");
        
        String url = this.getUrl(userInfo, procData);
        try {
            procData.parseAndSet(DataSetVariables.PROCESS_STATE_DESC,
                    DataSetVariables.CLOSED);

            try {
              // update activities
              Activity activity = new Activity(login, 
                  procData.getFlowId(), procData.getPid(), procData.getSubPid(), 
                  0, 0, this.getDescription(userInfo, procData), url, 1);
              activity.setRead();
              activity.mid = procData.getMid();
              getProcessManagerBean().updateActivity(userInfo, activity);

            } catch (Exception ei) {
              Logger.error(login, this, "before", procData.getSignature() + "Caught an unexpected exception updateing activities: "
                  + ei.getMessage(), ei);
            }
        
        } catch (Exception e) {
            Logger.error(login, this, "before",
                procData.getSignature() + "END BLOCK: caught exception unscheduling activities: "
                            + e.getMessage(), e);
        }
        
        for(ReportTO report : procData.getCachedReports().values()) {

          if(Logger.isDebugEnabled()) {
            Logger.debug(login, this, "before", report.toString());            
          }
          
          if(report.isActive()) {
            report.setActive(false);
            report.setStopReporting(new Timestamp(Calendar.getInstance().getTimeInMillis()));
          }
        }
        return url;
    }
    
    /**
     * Checks if all is right to procced.
     * 
     * @param dataSet
     * @return true if it can proceed, false otherwise.
     */
    public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
        return true;
    }
    
    /**
     * Executes the actions after this block is executed.
     * 
     * @param dataSet
     * @return the outPort.
     */
    public Port after(UserInfoInterface userInfo, ProcessData procData) {
        return null;
    }
    
    public String getDescription(UserInfoInterface userInfo,
            ProcessData procData) {
        return this.getDesc(userInfo, procData, true, "Fim");
    }
    
    public String getResult(UserInfoInterface userInfo, ProcessData procData) {
        return this.getDesc(userInfo, procData, false, "Fim");
    }
    
    public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
        int flowid = procData.getFlowId();
        int pid = procData.getPid();
        int subpid = procData.getSubPid();
        
        return "End/end.jsp?flowid=" + flowid + "&pid=" + pid + "&subpid="
                + subpid + "&bid=" + this.getId();
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
