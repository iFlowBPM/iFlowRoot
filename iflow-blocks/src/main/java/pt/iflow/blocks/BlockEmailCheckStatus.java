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
import pt.iflow.api.notification.EmailManager;
import pt.iflow.api.notification.EmailStatusResult;
import pt.iflow.api.notification.NotificationManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.processtype.DateDataType;

/**
 * <p>
 * Title: BlockEmailCheckStatus
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author João Costa
 */

public class BlockEmailCheckStatus extends Block {
	
  public Port portIn, portSuccess, portEmpty, portError;

  protected final static String sEMAIL_REQUEST_ID = "requestId"; //$NON-NLS-1$
  protected final static String sEMAIL_STATUS = "status"; //$NON-NLS-1$
  protected final static String sEMAIL_SMTP_CODE = "smtpCode"; //$NON-NLS-1$
  protected final static String sEMAIL_SMTP_MESSAGE = "smtpMessage"; //$NON-NLS-1$
  protected final static String sEMAIL_ERROR_TYPE = "errorType"; //$NON-NLS-1$
  protected final static String sEMAIL_PROCESSED_TIME = "processedTime"; //$NON-NLS-1$
  
  public BlockEmailCheckStatus(int anFlowId, int id, int subflowblockid, String filename) {
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

  public Port[] getOutPorts (UserInfoInterface userInfo) {
	    Port[] retObj = new Port[3];
	    retObj[0] = portSuccess;
	    retObj[1] = portEmpty;
	    retObj[2] = portError;
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
    
    String requestId = null;
    String status = null;
    String smtpCode = null;
    String smtpMessage = null;
    String errorType = null;
    String processedTime = null;
    
    try {
    	requestId = getAttribute(sEMAIL_REQUEST_ID);
    	if (StringUtils.isNotEmpty(requestId)) {
    		requestId = procData.transform(userInfo, requestId);
    	}
    	status = getAttribute(sEMAIL_STATUS);
    	smtpCode = getAttribute(sEMAIL_SMTP_CODE);
    	smtpMessage = getAttribute(sEMAIL_SMTP_MESSAGE);
    	errorType = getAttribute(sEMAIL_ERROR_TYPE);
    	processedTime = getAttribute(sEMAIL_PROCESSED_TIME);
    }
    catch (Exception e) {
    	Logger.error(login, this, "after", 
    	          procData.getSignature() + "Error while retrieving request Id. Caught exception: " + e.getMessage(), e);
    	outPort = portError;
    	return outPort;
    }
    
    // here we have a non empty requestId 
    try {   	
    	EmailStatusResult result = EmailManager.getEmailStatusByRequestId(requestId, null);
    	
    	if (result != null) {
    		if (result.getStatus() != null && status != null) {
    			try {procData.parseAndSet(status, result.getStatus());} catch (Exception e) {
					Logger.error(login, this, "after", 
		    	          procData.getSignature() + "Error while parsing status: " + e.getMessage(), e);
				}
    		}
    		if (result.getSmtpCode() != null && smtpCode != null) {
    			try {procData.parseAndSet(smtpCode, result.getSmtpCode());} catch (Exception e) {
    				procData.parseAndSet(smtpCode, result.getSmtpCode());
    									Logger.error(login, this, "after", 
		    	          procData.getSignature() + "Error while parsing smtpCode: " + e.getMessage(), e);
    			}
    		}
    		if (result.getSmtpMessage() != null && smtpMessage != null) {
    			try{procData.parseAndSet(smtpMessage, result.getSmtpMessage()); } catch (Exception e) {				
					Logger.error(login, this, "after", 
		    	          procData.getSignature() + "Error while parsing smtpMessage: " + e.getMessage(), e);
				}
    		}
    		if (result.getErrorType() != null && errorType != null) {
    			try{procData.parseAndSet(errorType, result.getErrorType());} catch (Exception e) {
    				Logger.error(login, this, "after", 
		    	          procData.getSignature() + "Error while parsing errorType: " + e.getMessage(), e);
    			}
    		}
    		if (result.getProcessedAt() != null  && processedTime != null) {
    			try{procData.parseAndSet(processedTime, new DateDataType().format(result.getProcessedAt()));} catch (Exception e) {
					Logger.error(login, this, "after", 
		    	          procData.getSignature() + "Error while parsing processedTime: " + e.getMessage(), e);
				}
    		}
        	outPort = portSuccess;
        	return outPort;
    	}
    	else {
        	Logger.error(login, this, "after", 
      	          procData.getSignature() + "Email with request Id " + requestId + " not found");
        	outPort = portEmpty;
        	return outPort;
    	}
    
    } catch (Exception e) {
      Logger.error(login, this, "after", 
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
	  	outPort = portError;
	  	return outPort;
    }
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Check Email Status");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Email Status Checked");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
