package pt.iflow.blocks;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

import com.infosistema.oerrexiflowlib.data.OERAssetAplicacao;
import com.infosistema.oerrexiflowlib.data.OERAssetServidor;
import com.infosistema.oerrexiflowlib.data.OERResponseData;
import com.infosistema.oerrexiflowlib.main.IFlowServidor;
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

public class BlockP15032CreateServidor extends BlockP15032OER {

  public BlockP15032CreateServidor(int anFlowId, int id, int subflowblockid,
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
	    	 
	      try {	    		    	
	    	OERAssetServidor asset = new OERAssetServidor();	 
	    	
  	    	asset.setNome(getParsedAttribute(userInfo, "Nome", procData));
	  	    asset.setVersao(getParsedAttribute(userInfo, "Versao", procData));
	  	    asset.setDescricao(getParsedAttribute(userInfo, "Descricao", procData));
	  	    asset.setIP(getParsedAttribute(userInfo, "IP", procData));
	  	    asset.setVLan(getParsedAttribute(userInfo, "VLan", procData));
	  	    asset.setDisco(getParsedAttribute(userInfo, "Disco", procData));
	  	    asset.setRam(getParsedAttribute(userInfo, "Ram", procData));
	  	    asset.setProcessador(getParsedAttribute(userInfo, "Processador", procData));
	  	    asset.setServicePacks(getParsedAttribute(userInfo, "ServicePacks", procData));
	  	    asset.setSistemaOperativo(getParsedAttribute(userInfo, "SistemaOperativo", procData));
	  	    
	  	    String implementadoDRText = getParsedAttribute(userInfo, "ImplementadoDR", procData);
	  	    Boolean implementadoDR = "1".equals(implementadoDRText) || "true".equalsIgnoreCase(implementadoDRText);
	  	    asset.setImplementadoDR(implementadoDR);
	  	    
	  	    asset.setPermissoesLocais(getParsedAttribute(userInfo, "PermissoesLocais", procData));
	  	    
	  	    ProcessListVariable keywordsLististVar = procData.getList(getAttribute("KeywordsList"));
	  		asset.setKeywordsList(turnToStringArray(keywordsLististVar));	  		
	  		
	  	    String userName = Config.getOERUsername();
	  		String password = Config.getOERPassword();
	    	  
	  		OERResponseData result = IFlowServidor.criarServidor(userName, password, asset);	    	 	    	  
			if(result.getResponse()!=null){
				procData.set(getAttribute("OUT_Id"), ((OERAssetServidor)result.getResponse()).getId());
			} else {
				outPort =portError;
				procData.set(getAttribute("OUT_Error"),result.getError());
			}
			  
	      } catch (Exception e) {
	        Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
	        outPort = portError;
	      }

	    logMsg.append("Using '" + outPort.getName() + "';");
	    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
	    return outPort;
	  }

}
