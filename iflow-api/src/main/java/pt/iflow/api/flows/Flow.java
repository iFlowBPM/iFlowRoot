package pt.iflow.api.flows;

import java.io.PrintStream;
import java.util.List;

import javax.servlet.ServletRequest;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iflow.api.utils.UserInfoInterface;

public interface Flow {
    
    /**
     * This method advances to the next block when an event is fired
     * 
     * @param flowId
     * @param processId
     */
    public abstract String eventNextBlock(UserInfoInterface userInfo,
            int flowId, int pid, int subpid);
    
    /**
     * This method jumps to block bid
     * 
     * @param flowId
     * @param processId
     */
    public abstract String jumpToBlock(UserInfoInterface userInfo,
            ProcessData procData, int jumpToBlockId);
    
    /**
     * This method advances to the next block of the flow.
     * 
     * @param dataSet
     * @return String the next url.
     */
    public abstract String nextBlock(UserInfoInterface userInfo,
            ProcessData procData);

    public abstract String nextBlock(UserInfoInterface userInfo, 
            ProcessData procData, boolean useExistingTransaction);
    

    public void storeProcess(UserInfoInterface userInfo, ProcessData procData) throws Exception;
    public void storeProcess(UserInfoInterface userInfo, ProcessData procData, boolean markActivityHasRead) throws Exception;
    
    /**
     * Saves the dataSet to the database. Calls the saveDataset method of the
     * current block.
     * 
     * @param dataSet
     * @param session
     * @return mid
     */
    public abstract int saveDataSet(UserInfoInterface userInfo,
            ProcessData procData, ServletRequest request);
    
    /**
     * 
     * TODO Add comment for method getPossibleUndoStates on FlowBean
     * 
     * @param userInfo
     * @param procData
     * @return
     */
  public abstract List<FlowStateHistoryTO> getPossibleUndoStates(UserInfoInterface userInfo, ProcessData procData);
    
    /**
     * Returns a process to another state.
     *  
     * @param userInfo User information.
     * @param flowid Process data.
     * @param pid Process ID.
     * @param subpid SubProcess ID.
     * @param flowState Flow state.
     * @param mid Modification ID.
     * @param exit_flag exit flag.
     * @return True if successful, false otherwise.
     */
  public abstract boolean undoProcess(UserInfoInterface userInfo, int flowid, int pid, int subpid, int flowState, int mid, int exit_flag);

  public abstract boolean undoProcess(UserInfoInterface userInfo, int flowid, int pid, int subpid, int flowState, int mid,
      int exit_flag, boolean registerTransaction);
    
    /**
     * 
     * TODO Add comment for method getProcessData on FlowBean
     * 
     * @param userInfo
     * @param flowid
     * @param pid
     * @param subpid
     * @return
     */
    public abstract ProcessData getProcessData(UserInfoInterface userInfo,
            int flowid, int pid, int subpid);
    
    /**
     * Gets this flow's current state block
     * 
     * @return current state block or null if an error occurs
     */
    public abstract Block getBlock(UserInfoInterface userInfo,
            ProcessData procData);
    
    /**
     * Gets this flow's given block, by it's ID.
     * 
     * @param userInfo
     *            User information
     * @param procHeader
     *            Process header
     * @param blockId
     *            Block id
     * @return Specified block
     */
    public abstract Block getBlockById(UserInfoInterface userInfo,
            ProcessHeader procHeader, int blockId);
    
    /**
     * Returns the id of the given Block
     * 
     * @param asBlockName
     *            the block's name (without package, only name)
     * @return the Block's id
     */
    public abstract int getBlockId(UserInfoInterface userInfo,
            ProcessData procData, String asBlockName);

    
    /**
     * Checks if a flow as reached the end. If so, closes it.
     * 
     * @param flowId
     * @param procData
     * @param block - the current block
     *            
     * @throws Exception
     */
    public void checkFlowEnd(UserInfoInterface userInfo, ProcessData procData,
        Block block) throws Exception;
    
    /**
     * 
     * TODO Add comment for method deployFlow on FlowBean
     * 
     * @param userInfo
     * @param asFile
     * @return
     */
    public abstract String deployFlow(UserInfoInterface userInfo, String asFile);
    
    /**
     * 
     * TODO Add comment for method undeployFlow on FlowBean
     * 
     * @param userInfo
     * @param asFile
     * @return
     */
    public abstract String undeployFlow(UserInfoInterface userInfo,
            String asFile);
    
    /**
     * 
     * TODO Add comment for method saveFlowSettings on FlowBean
     * 
     * @param userInfo
     * @param afsaSettings
     */
    public abstract void saveFlowSettings(UserInfoInterface userInfo,
            FlowSetting[] afsaSettings);
    
    /**
     * 
     * TODO Add comment for method exportFlowSettings on FlowBean
     * 
     * @param userInfo
     * @param flowid
     * @param apsOut
     */
    public abstract void exportFlowSettings(UserInfoInterface userInfo,
            int flowid, PrintStream apsOut);
    
    /**
     * 
     * TODO Add comment for method importFlowSettings on FlowBean
     * 
     * @param userInfo
     * @param flowid
     * @param file
     * @return
     */
    public abstract String importFlowSettings(UserInfoInterface userInfo,
            int flowid, byte[] file);
    
    /**
     * 
     * TODO Add comment for method refreshFlowSettings on FlowBean
     * 
     * @param userInfo
     * @param flowid
     */
    public abstract void refreshFlowSettings(UserInfoInterface userInfo,
            int flowid);
    
    /**
     * 
     * TODO Add comment for method getFlowSettings on FlowBean
     * 
     * @param userInfo
     * @param flowid
     * @return
     */
    public abstract FlowSetting[] getFlowSettings(UserInfoInterface userInfo,
            int flowid);
    
    /**
     * 
     * TODO Add comment for method getFlow on FlowBean
     * 
     * @param userInfo
     * @param anFlowId
     * @return
     */
    public abstract IFlowData getFlow(UserInfoInterface userInfo, int anFlowId);
    
    /**
     * 
     * TODO Add comment for method checkFlowEnabled on FlowBean
     * 
     * @param userInfo
     * @param anFlowId
     * @return
     */
    public abstract boolean checkFlowEnabled(UserInfoInterface userInfo,
            int anFlowId);
    
    /**
     * Retrieves available flow roles for given flowid.
     * 
     * @param userInfo User performing the operation.
     * @param anFlowId Flow ID to perform lookup on.
     * @return
     */
    public abstract FlowRolesTO[] getFlowRoles(UserInfoInterface userInfo,
            int anFlowId);
    
    /**
     * TODO Add comment for method addFlowRoles on FlowBean
     * 
     * @param userInfo User performing the operation.
     * @param afrRoles 
     */
    public abstract void addFlowRoles(UserInfoInterface userInfo,
            FlowRolesTO afrRoles);
    
    /**
     * 
     * TODO Add comment for method removeFlowRoles on FlowBean
     * 
     * @param userInfo
     * @param afrRoles
     */
    public abstract void removeFlowRoles(UserInfoInterface userInfo,
            FlowRolesTO afrRoles);
    
    /**
     * 
     * TODO Add comment for method setFlowRoles on FlowBean
     * 
     * @param userInfo
     * @param afraRoles
     */
    public abstract void setFlowRoles(UserInfoInterface userInfo,
            FlowRolesTO[] afraRoles);
    
    /**
     * 
     * TODO Add comment for method getUserFlowRolesDelegated on FlowBean
     * 
     * @param userInfo
     * @param anFlowId
     * @return
     */
    public abstract FlowRolesTO[] getUserFlowRolesDelegated(
            UserInfoInterface userInfo, int anFlowId);
    
    /**
     * 
     * TODO Add comment for method getUserFlowRoles on FlowBean
     * 
     * @param userInfo
     * @param anFlowId
     * @return
     */
    public abstract FlowRolesTO[] getUserFlowRoles(UserInfoInterface userInfo,
            int anFlowId);
    
    /**
     * 
     * TODO Add comment for method getAllUserFlowRoles on FlowBean
     * 
     * @param userInfo
     * @return
     */
    public abstract FlowRolesTO[] getAllUserFlowRoles(
            UserInfoInterface userInfo);
    
    /**
     * 
     * TODO Add comment for method checkUserSelfFlowRoles on FlowBean
     * 
     * @param userInfo
     * @param anFlowId
     * @param asPrivilege
     * @return
     */
    public abstract boolean checkUserSelfFlowRoles(UserInfoInterface userInfo,
            int anFlowId, String asPrivilege);
    
    /**
     * 
     * TODO Add comment for method checkUserFlowRoles on FlowBean
     * 
     * @param userInfo
     * @param anFlowId
     * @param asPrivilege
     * @return
     */
    public abstract boolean checkUserFlowRoles(UserInfoInterface userInfo,
            int anFlowId, String asPrivilege);
    
    /**
     * 
     * TODO Add comment for method endProc on FlowBean
     * 
     * @param userInfo
     * @param procData
     * @return
     */
    public abstract String endProc(UserInfoInterface userInfo,
            ProcessData procData);

    public abstract boolean endProccessInBlockAdministration(UserInfoInterface userInfo, ProcessData processData);

    /**
     * 
     * TODO Add comment for method resyncFlow on FlowBean
     * 
     * @param userInfo
     * @param anFlowId
     * @param anOldBlockId
     * @param anNewBlockId
     * @return
     */
    public abstract String resyncFlow(UserInfoInterface userInfo, int anFlowId,
            int anOldBlockId, int anNewBlockId);

  /**
   * method to resyncFlow on FlowBean, allows forcing resync and does not use FlowHolderBean
   * 
   * @param userInfo
   * @param anFlowId
   * @param anOldBlockId
   * @param anNewBlockId
   * @param force
   * @return
   */
  public String resyncFlow(UserInfoInterface userInfo, int anFlowId, Integer anOldBlockId, Integer anNewBlockId, boolean force,
      IFlowData fd);

  /**
   * Retrives a given flow's info.
   * 
   * @param userInfo
   *          caller user
   * @param anFlowId
   *          desired flow id
   * 
   * @return list of hashmaps with each block's info (key/value: id/string,type/string,hasinteraction/boolean,
   *         outblocks/arraylist(hmtmp(portname,connectedblockid)))
   */
    public abstract List<BlockInfo> getFlowInfo(UserInfoInterface userInfo,
            int anFlowId);
    
    /**
     * Retrives a given flow's states.
     * 
     * @param userInfo
     *            caller user
     * @param procData
     *            the process data to fetch the states
     * 
     * @return arraylist with the process states (string)
     */
    public abstract List<String> getFlowStates(UserInfoInterface userInfo,
            ProcessData procData);
    
    /**
     * Gets the flows catalogue
     * 
     * @param userInfo
     *            caller user
     * @param anFlowId
     *            desired flow id
     */
    public abstract ProcessCatalogue getFlowCatalogue(
            UserInfoInterface userInfo, int anFlowId);
    
    public abstract String endSubProc(UserInfoInterface userInfo,
            ProcessData procData, String endMsg);
    
    /**
     * Get flow type
     * 
     * @param userInfo
     * @param flowid
     * @return
     */
    public abstract FlowType getFlowType(UserInfoInterface userInfo, int flowid);
    
    /**
     * Set flow type. User must be org adm.
     * @param userInfo
     * @param flowid
     * @param flowType
     * @return
     */
    public abstract boolean setFlowType(UserInfoInterface userInfo, int flowid, FlowType flowType);

}
