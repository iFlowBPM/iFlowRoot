package pt.iflow.blocks;

import java.util.Date;
import java.util.List;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

import com.infosistema.OERIflow.IflowServicoMain;
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

public class BlockP15032CreateAssetServicePasso1 extends BlockP15032OER {

  public BlockP15032CreateAssetServicePasso1(int anFlowId, int id, int subflowblockid,
			String filename) {
		super(anFlowId, id, subflowblockid, filename);
		// TODO Auto-generated constructor stub
	}
public static void main(String[] args){
	Date d = new Date();	
	d.setTime(28122015080000876L);
	d.setTime(1449833339079L);
	int i =0;
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
	    
	    String sNomeVar = getParsedAttribute(userInfo, "Nome", procData);
	    String sVersaoVar = getParsedAttribute(userInfo, "Versao", procData);
	    String sDescricaoVar = getParsedAttribute(userInfo, "Descricao", procData);
	    String sCompanhiaVar = getParsedAttribute(userInfo, "Companhia", procData);
	    String sDriverVar = getParsedAttribute(userInfo, "Driver", procData);
	    String sObjectivoVar = getParsedAttribute(userInfo, "Objectivo", procData);
	    String sAsseFolderVar = getParsedAttribute(userInfo, "AssetFolder", procData);
	    
	    ProcessListVariable areasListVar = procData.getList(getAttribute("AreasList"));
	    ProcessListVariable aplicacoesListVar = procData.getList(getAttribute("AplicacoesList"));
	    ProcessListVariable keywordsLististVar = procData.getList(getAttribute("KeywordsList"));
	    ProcessListVariable ownerLististVar = procData.getList(getAttribute("OwnerList"));
	    ProcessListVariable authorListVar = procData.getList(getAttribute("AuthorList"));
	    	 
	   
	      try {	    	  
	    	  OERAssetServico asset = new OERAssetServico();
	    	  asset.setNome(sNomeVar);
	    	  asset.setVersao(sVersaoVar);
	    	  asset.setDescricao(sDescricaoVar);
	    	  asset.setCompanhia(sCompanhiaVar);
	    	  asset.setDriver(sDriverVar);
	    	  asset.setObjectivo(sObjectivoVar);
	    	  asset.setAreasList(turnToStringArray(areasListVar));
	    	  asset.setAplicacoesList(turnToAssetList(aplicacoesListVar));
	    	  asset.setKeywordsList(turnToStringArray(keywordsLististVar));
	    	  asset.setAssetFolder(sAsseFolderVar);	    	  
	    	  
	    	  List<String> ownerList = turnToStringArray(ownerLististVar);
	    	  String owner;
	    	  if (ownerList==null || ownerList.size()==0)
	    		  owner="";
	    	  else
	    		  owner=ownerList.get(0);
	    	  asset.setOwner(owner);
	    	  
	    	  List<String> authorList = turnToStringArray(authorListVar);
	    	  String author;
	    	  if (authorList==null || authorList.size()==0)
	    		  author = "";
	    	  else
	    		  author = authorList.get(0);
	    	  asset.setAuthor(author);
	    	  
	    	  
	    	  IflowServicoMain tester = new IflowServicoMain();
	    	  OERResponseData result = tester.runPasso(Config.getOERUsername(), Config.getOERPassword(), asset, 1);
	    	  if(result.getResponse()!=null){
					procData.set(getAttribute("OUT_Id"), "" + ((OERAssetServico)result.getResponse()).getId());
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
