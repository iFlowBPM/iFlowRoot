package pt.iflow.blocks;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.StringUtilities;

import com.infosistema.OERIflow.IflowServicoMain;
import com.infosistema.oerrexiflowlib.data.OERAssetAplicacao;
import com.infosistema.oerrexiflowlib.data.OERAssetServico;
import com.infosistema.oerrexiflowlib.data.OERResponseData;
import com.infosistema.oerrexiflowlib.utils.Config;

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

public class BlockP15032ReadAssetServiceById extends BlockP15032OER {

  public BlockP15032ReadAssetServiceById(int anFlowId, int id, int subflowblockid,
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

	    String userName = Config.getOERUsername();
  	    String password = Config.getOERPassword();
  	    String sAssetIdVar = getParsedAttribute(userInfo, "Id", procData);
	    	 	  
	      try {	    	  
	    	  IflowServicoMain tester = new IflowServicoMain();
	    	  OERResponseData result = tester.getServicoById(userName, password, Long.parseLong(sAssetIdVar));
	    	  
	    	  if(result.getResponse()!=null){
	    		    fillAssetServiceValues((OERAssetServico) result.getResponse(), procData);
	    	  } else if (result.getError()!=null){	    		
					outPort =portError;
					procData.set(getAttribute("OUT_Error"),result.getError());				
	    	  } else
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
