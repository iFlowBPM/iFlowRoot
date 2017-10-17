package pt.iflow.blocks;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.StringUtilities;

/**
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Infosistema
 * </p>
 * 
 * @author
 */

public class BlockP15032DeleteAssetServiceById extends BlockP15032OER {

  public BlockP15032DeleteAssetServiceById(int anFlowId, int id, int subflowblockid,
			String filename) {
		super(anFlowId, id, subflowblockid, filename);
		// TODO Auto-generated constructor stub
	}

/**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
	    Port outPort = portSuccess;
	    String login = userInfo.getUtilizador();
	    StringBuffer logMsg = new StringBuffer();

	    String suserNameVar = getParsedAttribute(userInfo, userName, procData);
	    String sPasswordVar = getParsedAttribute(userInfo, password, procData);	    
	    String sIdVar = getParsedAttribute(userInfo, "Id", procData);	   
	    	 
	    if (StringUtilities.isEmpty(suserNameVar) || StringUtilities.isEmpty(sPasswordVar)) {
	      Logger.error(login, this, "after", procData.getSignature() + "empty value for attributes");
	      outPort = portError;
	    } else
	      try {	    	  
	    	  Boolean deleted = null;//IflowDemoV2.DeleteAssetServiceById(suserNameVar, sPasswordVar, Long.parseLong(sIdVar));
	    	  
	    	  if(!deleted)
	    		  outPort =portEmpty;
	    	  
	      } catch (Exception e) {
	        Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
	        outPort = portError;
	      }

	    logMsg.append("Using '" + outPort.getName() + "';");
	    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
	    return outPort;
	  }

}
