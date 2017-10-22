package pt.iflow.blocks;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

import com.infosistema.OERIflow.IflowAplicacaoMain;
import com.infosistema.oerrexiflowlib.data.OERAssetAplicacao;
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

public class BlockP15032CreateAplicacao extends BlockP15032OER {

  public BlockP15032CreateAplicacao(int anFlowId, int id, int subflowblockid,
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
	    	IflowAplicacaoMain tester = new IflowAplicacaoMain();
	    	OERAssetAplicacao asset = new OERAssetAplicacao();	    	  	    	 
  	    	asset.setNome(getParsedAttribute(userInfo, "Nome", procData));
	  	    asset.setCompanhia(getParsedAttribute(userInfo, "Companhia", procData));
	  	    asset.setDescricao(getParsedAttribute(userInfo, "Descricao", procData));
	  	    asset.setDireccaoResponsavel(getParsedAttribute(userInfo, "DireccaoResponsavel", procData));
	  	    asset.setNumberUsers(getParsedAttribute(userInfo, "NumberUsers", procData));
	  	    asset.setResponsavelOTI(getParsedAttribute(userInfo, "ResponsavelOTI", procData));
	  	    asset.setVersao(getParsedAttribute(userInfo, "Versao", procData));
	  	    //asset.setAppId(getParsedAttribute(userInfo, "AppId", procData));
	  	    Logger.debug(login, this, "after", procData.getSignature() + "AppId value=" + getParsedAttribute(userInfo, "AppId", procData));
	  	    
	  	    ProcessListVariable keywordsLististVar = procData.getList(getAttribute("KeywordsList"));
	  		asset.setKeywordsList(turnToStringArray(keywordsLististVar));	  		
	  		ProcessListVariable tipoClienteListVar = procData.getList(getAttribute("TipoClienteList"));
	  		asset.setTipoClienteList(turnToStringArray(tipoClienteListVar));	  	    	  	    
	  	    
	  	    String userName = Config.getOERUsername();
	  		String password = Config.getOERPassword();
	    	  
	  		OERResponseData result = tester.createAplicacao(userName, password, asset);	    	 	    	  
			if(result.getResponse()!=null){
				procData.set(getAttribute("OUT_Id"), ((OERAssetAplicacao)result.getResponse()).getId());
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
