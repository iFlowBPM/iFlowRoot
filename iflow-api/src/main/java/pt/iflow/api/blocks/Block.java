package pt.iflow.api.blocks;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.core.ReportManager;
import pt.iflow.api.core.Repository;
import pt.iflow.api.errors.IErrorManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.utils.WSDLUtilsV2;
import pt.iknow.utils.wsdl.WSDLUtils;

/**
 * <p>
 * Title:
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
 * @author João Valentim unascribed
 * @version 1.0 events by Pedro Monteiro
 */
public abstract class Block {
    public enum MSG_CODES {
        OK("pt.iknow.blocks.msg.canProceed"), 
        CANNOT_PROCEED("pt.iknow.blocks.msg.cannotProceed"), 
        WAITING_JOIN("pt.iknow.blocks.msg.waitingJoin");
        
        private String _code;
        
        MSG_CODES(String code) {
            _code = code;
        }
        
        public String getCode() {
            return _code;
        }
    }
    
    public Port portEvent;
    // true if the block is an event block
    protected boolean isEvent;
    // true if the block has an associated event
    protected boolean hasEvent;
    // the block id
    private int blockId;
    // the block's flow id
    private int _nFlowId;
    
    // for blocks included from subflows
    
    // the block id in the subflow
    private int _subFlowBlockId;
    // the subflow filename from which this block was included
    private String _subFlowFilename;
    // true if the block has user interaction trough JSP
    protected boolean hasInteraction;
    // true if the block generates code for calculations
    protected boolean isCodeGenerator;
    // true if the block generates the JSPs based on its attributes.
    protected boolean isJSPGenerator;
    // true if the block instance can be executed only once. Used for
    // transaction blocks.
    protected boolean isOnceExec;
    // true if block needs process to be in database.
    protected boolean bProcInDBRequired;
    // True if block forwards process to another user.
    protected boolean isForwardBlock;
    // True if block is part of popUp flow
    protected Integer _popupReturnBlockId = null;
    // True if block can run in popup.
    protected boolean canRunInPopupBlock; 
    // LinkedHashMap is used to maintain predictable iteration order
    private HashMap<String, String> mapAttributes = new HashMap<String, String>();
    
    // check if match vars in Editor's pt.iknow.workfloweditor.AlteraAtributos
    public final static String sDESCRIPTION = "block_description";
    public final static String sRESULT = "block_result";
    
    public final static String sORG_ID_PROP = "__ORG_ID";
    
    protected boolean saveFlowState = true;
    
    public Block() {
        isEvent = false;
        hasEvent = false;
        hasInteraction = false;
        isCodeGenerator = false;
        isJSPGenerator = false;
        isOnceExec = false;
        isForwardBlock = false;
        bProcInDBRequired = false;
        canRunInPopupBlock = true;
    }
    
    public Block(int anFlowId, int anId, int subFlowId, String subFlowName) {
        this();
        this.setFlowId(anFlowId);
        this.setId(anId);
        this.setSubFlowBlockId(subFlowId);
        this.setSubFlowFilename(subFlowName);
    }
    
    /**
     * Method to get this block's event port.
     */
    abstract public Port getEventPort();
    
    public void setSaveFlowState(boolean saveFlowState){
      this.saveFlowState = saveFlowState;
    }
    
    public boolean isSaveFlowState(){
      return saveFlowState;
    }
    
    public void setHasEvent(boolean hasEvent) {
        this.hasEvent = hasEvent;
        if (hasEvent) {
            bProcInDBRequired = true;
        }
    }
    
    /**
     * Check if the block is an event
     * 
     * @return if the block is an event.
     */
    public boolean isEvent() {
        return isEvent;
    }
    
    /**
     * Check if the block has an associated event
     * 
     * @return if the block has an associated event.
     */
    public boolean hasEvent() {
        return hasEvent;
    }
    
    /**
     * Sets a block Id.
     * 
     * @param id
     *            the block's Id.
     */
    public void setId(int id) {
        blockId = id;
    }
    
    /**
     * Sets a block subflowId.
     * 
     * @param id
     *            the block's subflow Id.
     */
    public void setSubFlowBlockId(int id) {
        this._subFlowBlockId = id;
    }
    
    /**
     * Sets the block's flow Id.
     * 
     * @param id
     *            the block's flow Id.
     */
    public void setFlowId(int flowid) {
        this._nFlowId = flowid;
    }
    
    /**
     * Sets the block's subflow filename.
     * 
     * @param filename
     *            the block's subflow filename.
     */
    public void setSubFlowFilename(String filename) {
        this._subFlowFilename = filename;
    }
    
    /**
     * Gets a block Id.
     * 
     * @return the block's Id.
     */
    public int getId() {
        return blockId;
    }
    
    /**
     * Gets a block Id.
     * 
     * @return the block's Id.
     */
    public int getSubFlowBlockId() {
        return _subFlowBlockId;
    }
    
    /**
     * Gets the block's flow Id.
     * 
     * @return the block's flow Id.
     */
    public int getFlowId() {
        return this._nFlowId;
    }
    
    /**
     * Gets the block's subflow filename.
     * 
     * @return the block's subflow filename.
     */
    public String getSubFlowFilename() {
        return this._subFlowFilename;
    }
    
    /**
     * Check if the block has interaction with the user or if it is only for
     * computation.
     * 
     * @return if the block has interaction.
     */
    public boolean hasInteraction() {
        return hasInteraction;
    }
    
    /**
     * Check if the block generates code for calculations.
     * 
     * @return if the block generates code.
     */
    public boolean isCodeGenerator() {
        return isCodeGenerator;
    }
    
    /**
     * Check if the block generates JSP pages.
     * 
     * @return
     */
    public boolean isJSPGenerator() {
        return isJSPGenerator;
    }
    
    /**
     * Check if the block instance can be executed only once.
     * 
     * @return
     */
    public boolean isOnceExec() {
        return isOnceExec;
    }
    
    /**
     * Adds an attribute to the block.
     * 
     * @param attrib
     * @return
     */
    public boolean addAttribute(Attribute attrib) {
        if (mapAttributes.put(attrib.getName(), attrib.getValue()) == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Returns the attribute map.
     * 
     * @return
     */
    public HashMap<String, String> getAttributeMap() {
        return mapAttributes;
    }
    
    /**
     * Returns an attribute value given the name.
     * 
     * @param name
     * @return value
     */
    public String getAttribute(String name) {
        return (String) mapAttributes.get((String) name);
    }
    
    /**
     * Returns a parsed attribute value for a given name.
     * 
     * @param name
     * @return parsed value
     */
    public String getParsedAttribute(UserInfoInterface userInfo, String name, ProcessData ds) {
        return getParsedAttribute(userInfo, name, ds, -1);
    }
    
    /**
     * Returns a parsed attribute value for a given name.
     * 
     * @param name
     * @return parsed value
     */
    public String getParsedAttribute(UserInfoInterface userInfo, String name, ProcessData process,
            int anIndex) {
        String retObj = "";
        String value = (String) mapAttributes.get((String) name);
        
        if (value == null || value.equals(""))
            return retObj;
        
        if (true) {
            // stringparser transform does not work with arrays...
            // ignore transformation!!
            // TODO: implement support in string parser !!!!!!!!!
            if (anIndex >= 0) {
                retObj = process.getListItemFormatted(name, anIndex);
                if (retObj == null)
                    retObj = "";

                Logger.info(null, this, "getParsedAttribute", 
                    "GET PARSED ATTRIBUTE ARRAY: " + value + "["
                    + anIndex + "]=" + retObj);

                return retObj;
            }
//        } else {
//            if (anIndex >= 0 && value.charAt(0) != '"') {
//                value = DataSetUtils.getListKey(value, anIndex);
//                if (StringUtils.isEmpty(value))
//                    return retObj;
//            }
        }
        
        try {
            retObj = process.transform(userInfo, value);
        } catch (EvalException e) {
        }
        if (retObj == null)
            retObj = "";
        
        return retObj;
    }
    
    /**
     * Method implemented by blocks that generate code.
     * 
     * @param outFile
     */
    public void generateCode(BufferedWriter outFile) {
        return;
    }
    
    /**
     * Method implemented by blocks to generate JSP.
     */
    public void generateJSP(UserInfoInterface userInfo, ProcessData procData) {
        return;
    }
    
    /**
     * Executes the actions in this block before the block is processed.
     * 
     * @param userInfo
     *          User calling this procedure.
     * @param procData
     *          Current process' data.
     */
    public abstract String before(UserInfoInterface userInfo,
            ProcessData procData);
    
    /**
     * Method called to verify if all the conditions exist to call after.
     * 
     * @param userInfo
     *          User calling this procedure.
     * @param procData
     *          Current process' data.
     */
    public abstract boolean canProceed(UserInfoInterface userInfo,
            ProcessData procData);
    
    // get messages code for correct presentation
    public MSG_CODES getCanProceedMsgCode(UserInfoInterface userInfo,
            ProcessData procData) {
        if (canProceed(userInfo, procData))
            return MSG_CODES.OK;
        else {
            return MSG_CODES.CANNOT_PROCEED;
        }
    }
    
    /**
     * Method called to advance in the flow.
     * 
     * @param userInfo
     *          User calling this procedure.
     * @param procData
     *          Current process' data.
     */
    public abstract Port after(UserInfoInterface userInfo, ProcessData procData);
    
    /**
     * Code executed when event is fired 
     * (when event is fired, canProceed and after methods are NOT executed)  
     * @param userInfo
     * @param procData
     */
    public void onEventFired(UserInfoInterface userInfo, ProcessData procData) throws Exception {
    }
    
    /**
     * Method to get this block description text.
     * 
     * @param userInfo
     *          User calling this procedure.
     * @param procData
     *          Current process' data.
     */
    public abstract String getDescription(UserInfoInterface userInfo,
            ProcessData procData);
    
    /**
     * Method to get this block resulting text.
     * 
     * @param userInfo
     *          User calling this procedure.
     * @param procData
     *          Current process' data.
     */
    public abstract String getResult(UserInfoInterface userInfo,
            ProcessData procData);
    
    /**
     * Method to get this block URL (associated page).
     * 
     * @param userInfo
     *          User calling this procedure.
     * @param procData
     *          Current process' data.
     */
    public abstract String getUrl(UserInfoInterface userInfo,
            ProcessData procData);

    public static String getDefaultUrl(UserInfoInterface userInfo, ProcessData procData) {
      return getDefaultUrl(userInfo, procData.getProcessHeader());
    }
    
    public static String getDefaultUrl(UserInfoInterface userInfo, ProcessHeader procHeader) {
      int flowid = procHeader.getFlowId();
      int pid    = procHeader.getPid();
      int subpid = procHeader.getSubPid();
      return Block.getDefaultUrl(userInfo, flowid, pid, subpid);
    }
    
    protected static String getDefaultUrl(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
      return "Forward/forward.jsp?flowid="+ flowid+"&pid="+ pid+"&subpid="+subpid;    
    }
    
    /**
     * Method to get this block output ports.
     * 
     * @param userInfo
     *          User calling this procedure.
     */
    public abstract Port[] getOutPorts(UserInfoInterface userInfo);
    
    /**
     * Method to get this block input ports.
     * 
     * @param userInfo
     *          User calling this procedure.
     */
    public abstract Port[] getInPorts(UserInfoInterface userInfo);
    
    // method to tell block to refresh it's cache
    public void refreshCache(UserInfoInterface userInfo) {
        return;
    }
    
    // method to check if block is start block
    public boolean isStartBlock() {
        return false;
    }
    
    // method to check if block is end block
    public boolean isEndBlock() {
        return false;
    }
    
    public boolean isProcInDBRequired() {
        return this.bProcInDBRequired;
    }
    
    public boolean isForwardBlock() {
        return this.isForwardBlock;
    }
    
    public boolean canRunInPopupBlock() {
        return this.canRunInPopupBlock;
    }
    
    public Object execute(int op, Object[] aoa) {
        return new Object();
    }
    
    public int saveDataSet(UserInfoInterface userInfo, ProcessData procData) {
        try {
            ProcessManager pm = BeanFactory.getProcessManagerBean();
            
            return pm.modifyProcessData(userInfo, procData);
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "saveDataSet",
                    "caught exception: " + e.getMessage(), e);
            return -1;
        }
    }
    
    /**
     * 
     * 
     * @param userInfo
     * @param dataSet
     * @param abDesc
     * @param asDesc
     * @return
     */
    protected String getDesc(UserInfoInterface userInfo, ProcessData procData,
            boolean abDesc, String asDesc) {
        String stmp = null;
        try {
            if (abDesc) {
                stmp = getAttribute(Block.sDESCRIPTION);
            } else {
                stmp = getAttribute(Block.sRESULT);
            }
            
            if (StringUtils.isNotEmpty(stmp)) {
                stmp = Utils.transformStringAndPrepareForDB(userInfo, stmp, procData);
                if (StringUtils.isNotEmpty(stmp)) {
                    return stmp;
                }
            }
        } catch (Exception e) {
        }
        return asDesc;
    }
    
    protected WSDLUtils setWSDLUtils(InputStream aisWsdl, String asUrl)
            throws Exception {
        return new WSDLUtils(aisWsdl, asUrl);
    }    
    
    protected Repository getRepBean() {
        return BeanFactory.getRepBean();
    }
    
    protected IErrorManager getErrorManagerBean() {
        return BeanFactory.getErrorManagerBean();
    }
    
    protected ProcessManager getProcessManagerBean() {
        return BeanFactory.getProcessManagerBean();
    }
    
    protected ReportManager getReportManagerBean() {
        return BeanFactory.getReportManagerBean();
    }
    
    protected Flow getFlowBean() {
        return BeanFactory.getFlowBean();
    }

    protected List<String> logs;
    protected String LOG_DIV = ";[...];";
    protected int MAX_LOG_SIZE = 1024 - (LOG_DIV.length() * 2);

    protected void addToLog(String logMsg) {
      if (StringUtils.isEmpty(logMsg))
        return;
      
      if (logs == null) {
        logs = new ArrayList<String>();
      }
      if (logs.isEmpty()) {
        logs.add(logMsg);
      } else {
        int workingIndex = logs.size() - 1;
        String tmpMsg = logs.get(workingIndex);
        if((tmpMsg.length() + logMsg.length()) < MAX_LOG_SIZE) {
          logs.set(workingIndex, tmpMsg + logMsg);
        } else {
          logs.set(workingIndex, tmpMsg + LOG_DIV);
          logs.add(LOG_DIV + logMsg);
        }
      }
    }

    protected void saveLogs(UserInfoInterface userInfo, ProcessData procData, Block block, Object caller, String method) {
      if (logs != null) {
        for(String logMsg : this.logs) {
          Logger.logFlowState(userInfo, procData, block, logMsg, caller, method);
        }
      }
    }

    protected void saveLogs(UserInfoInterface userInfo, ProcessData procData, Block block) {
      this.saveLogs(userInfo, procData, block, null, null);
    }
    
    /**
     * Blocks execution cost.
     * 
     * @return 1 by default
     */
    public long getCost() {
      return 1;
    }

    public String processText(UserInfoInterface userInfo, ProcessData procData, String text) {
      if (StringUtils.isNotEmpty(text)) {
    	/*if (StringUtils.contains(text, "\"") || procData.getCatalogue().hasVar(text)
        		|| StringUtils.contains(text.replace(" ", ""), "${")) {*/
    	if (procData.isTransformable(text, true)) {
          try {
            String transfText = procData.transform(userInfo, text);
            return transfText;
          } catch (EvalException e) {
            // not able to transform.. leave it as it is..
            Logger.info(userInfo.getUtilizador(), this, "processText", 
                procData.getSignature() + 
                "assumed text '" + text + "' was transformable.. bad assumption! reverted to original value");
          }
        }            
      }
      return text;
    }  
    
    protected String getMessage(UserInfoInterface userInfo, ProcessData procData) {      
      return getMessage(userInfo, procData, null);
    }

    protected String getMessage(UserInfoInterface userInfo, ProcessData procData, String defaultMessage) {
      String newText = null;
      if (hasMessage()) 
        newText = processText(userInfo, procData, this.getAttribute(MessageBlock.MESSAGE_USER));
      return StringUtils.isNotEmpty(newText) ? newText : defaultMessage;
    }

    protected boolean hasMessage() {
      return StringUtils.isNotEmpty(this.getAttribute(MessageBlock.MESSAGE_USER));
    }

    /**
     * Checks if a given condition is true
     * @param userInfo User Info
     * @param procData Process Data
     * @return True if is disable false otherwise
     */
    public boolean isDisabled(UserInfoInterface userInfo, ProcessData procData) {
      String login = userInfo.getUtilizador();

      try {
        String sCondition = null;
        boolean bEvalResult = false;

        sCondition = this.getAttribute("disableIf");

        if (StringUtils.isEmpty(sCondition)) {
          Logger.warning(login,this,"after", 
              procData.getSignature() + "empty condition to evaluate!! Assuming false");
          bEvalResult = false;
        }
        else {
          try {
            bEvalResult = procData.query(userInfo, sCondition);
            Logger.debug(login,this,"after", sCondition + " evaluated " + bEvalResult);
          } catch (Exception ei) {
            bEvalResult = false;
            Logger.error(login,this,"after", 
                procData.getSignature() + "caught exception evaluation condition (assuming false): " + sCondition, ei);
          }
        }

        return bEvalResult;
      }
      catch (Exception e) {
        Logger.error(login,this,"after",
            procData.getSignature() + "caught exception: " + e.getMessage(), e);
        return false;
      }
    }

    public Integer getBlockRunningInPopup() {
      return _popupReturnBlockId > 0 ? _popupReturnBlockId : null;
    }

    public void setBlockRunningInPopup(Integer popupReturnBlockId) {
      this._popupReturnBlockId = popupReturnBlockId;
    }
    
    public boolean isBlockRunningInPopup() {
      return !(getBlockRunningInPopup() == null);     
    }
    
    public static List<String> nullSafe(List<String> c) {
        return (List<String>) ((c == null) ? Collections.emptyList() : c);
    }
    
}