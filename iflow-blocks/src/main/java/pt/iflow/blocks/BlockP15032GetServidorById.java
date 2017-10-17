package pt.iflow.blocks;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

import com.infosistema.OERIflow.IflowAplicacaoMain;
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

public class BlockP15032GetServidorById extends BlockP15032OER {

  public BlockP15032GetServidorById(int anFlowId, int id, int subflowblockid,
			String filename) {
		super(anFlowId, id, subflowblockid, filename);
		// TODO Auto-generated constructor stub
	}
public static void main(String[] a){
	String userName = Config.getOERUsername();
	    String password = Config.getOERPassword();
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

	    String sAssetIdVar = getParsedAttribute(userInfo, "Id", procData);
	    	 
	      try {	    	  	    	    	
	  	    String userName = Config.getOERUsername();
	  	    String password = Config.getOERPassword();
	  	    long appId = Long.parseLong(sAssetIdVar);

	  	    OERResponseData result = IFlowServidor.getServidorById(userName, password, appId);
	    	  
	    	if(result.getResponse()!=null){
	    		OERAssetServidor asset = (OERAssetServidor)result.getResponse();
	    			    		
	  	    	procData.set(getAttribute("OUT_Nome"), asset.getNome());
	  	    	procData.set(getAttribute("OUT_Versao"), asset.getVersao());
	  	    	procData.set(getAttribute("OUT_Descricao"), asset.getDescricao());
	  	    	
	  	    	procData.set(getAttribute("OUT_IP"), asset.getIP());
	  	    	procData.set(getAttribute("OUT_VLan"), asset.getVLan());
	  	    	procData.set(getAttribute("OUT_Disco"), asset.getDisco());
	  	    	procData.set(getAttribute("OUT_Ram"), asset.getRam());
	  	    	procData.set(getAttribute("OUT_Processador"), asset.getProcessador());
	  	    	procData.set(getAttribute("OUT_ServicePacks"), asset.getServicePacks());
	  	    	procData.set(getAttribute("OUT_SistemaOperativo"), asset.getSistemaOperativo());	  	    	
	  	    	procData.set(getAttribute("OUT_ImplementadoDR"), asset.isImplementadoDR()?Boolean.TRUE.toString():Boolean.FALSE.toString());
	  	    	procData.set(getAttribute("OUT_PermissoesLocais"), asset.getPermissoesLocais());	  	    		  	    	
		  	    
	  	    	ProcessListVariable listVar = procData.getList(getAttribute("OUT_KeywordsList"));
	  			if(listVar!=null){
	  				listVar.clear();
	  			for(int i=0; i< asset.getKeywordsList().size(); i++)      		        		        		
	  				listVar.addNewItem(asset.getKeywordsList().get(i));		
	  			}
	  				  			
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
