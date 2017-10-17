package pt.iflow.blocks;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

import com.flashline.registry.openapi.base.OpenAPIException;
import com.infosistema.OERIflow.IflowAplicacaoMain;
import com.infosistema.OERIflow.IflowServicoMain;
import com.infosistema.oerrexiflowlib.data.OERAsset;
import com.infosistema.oerrexiflowlib.data.OERAssetServico;
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

public abstract class BlockP15032OER extends Block {
  public Port portIn, portSuccess, portEmpty, portError;

  static final String userName = "userName";
  static final String password = "password";
  static final String assetId = "assetId";
  
  public ArrayList<String> turnToStringArray(ProcessListVariable pListVar){
	  ArrayList<String> result = new ArrayList<String>();
	  for(int i=0; i<pListVar.size(); i++)
		  if(pListVar.getItem(i)!=null)
			  result.add(pListVar.getItem(i).getRawValue());
	  
	  return result;
  }
  
  public List<OERAsset> turnToAssetList(ProcessListVariable assetNameList, ProcessListVariable assetVersionList) throws OpenAPIException, RemoteException, MalformedURLException, ServiceException{
	  ArrayList<OERAsset> result = new ArrayList<OERAsset>();
	  for (int i=0; i< assetNameList.size(); i++)
		  if(assetNameList.getItem(i)!=null)
			  //result.add(new OERAsset(assetNameList.getItem(i).getRawValue(), assetVersionList.getItem(i).getRawValue()));	  
			  ;
	  return result;
  }
  
  public List<OERAsset> turnToAssetList(ProcessListVariable assetIdList) throws OpenAPIException, RemoteException, MalformedURLException, ServiceException{
	  IflowServicoMain servicoMain =  new IflowServicoMain();
	  ArrayList<OERAsset> result = new ArrayList<OERAsset>();
	  
	  for (int i=0; i< assetIdList.size(); i++){
		  OERAsset asset = (OERAsset) servicoMain.getServicoById(Config.getOERUsername(), Config.getOERPassword(),Long.parseLong( assetIdList.getItem(i).getRawValue())).getResponse();
		  result.add(asset);
	  }
		  	  
	  return result;
  }
  
  public void fillAssetServiceValues(OERAssetServico asset, ProcessData procData){
	  if(asset!=null){    
		 
    	//procData.set(getAttribute("OUT_Id"), asset.getId());
		procData.set(getAttribute("OUT_Nome"), asset.getNome() );
		procData.set(getAttribute("OUT_Versao"), asset.getVersao() );
		procData.set(getAttribute("OUT_Estado"), asset.getEstado() );		
		
		//Used to be a list
		ProcessListVariable listVar = procData.getList(getAttribute("OUT_OwnerList"));
		if(listVar!=null){
			listVar.clear();
			listVar.addNewItem(asset.getOwner());		
		}
		
		procData.set(getAttribute("OUT_Categoria"), asset.getCategoria() );
		procData.set(getAttribute("OUT_Objectivo"), asset.getObjectivo() );
		procData.set(getAttribute("OUT_LayerSoa"), asset.getLayerSoa() );
		procData.set(getAttribute("OUT_Driver"), asset.getDriver() );
		
		listVar = procData.getList(getAttribute("OUT_AreasList"));
		if(listVar!=null){
			listVar.clear();
		for(int i=0; i< asset.getAreasList().size(); i++)      		        		        		
			listVar.addNewItem(asset.getAreasList().get(i));        	    		    		    		
		}
		procData.set(getAttribute("OUT_Descricao"), asset.getDescricao() );
		procData.set(getAttribute("OUT_ConformidadeSoa"), asset.getConformidadeSoa()?"true":"false" );
		procData.set(getAttribute("OUT_Revisao"), asset.getRevisao() );
				
		listVar = procData.getList(getAttribute("OUT_AplicacoesList"));
		if(listVar!=null){
			listVar.clear();
		for(int i=0; i< asset.getAplicacoesList().size(); i++)      		        		        		
			listVar.addNewItem("" + asset.getAplicacoesList().get(i).getId()); 
		}		
		
		procData.set(getAttribute("OUT_Companhia"), asset.getCompanhia() );
		procData.set(getAttribute("OUT_Consumidor"), asset.getConsumidor() );
		procData.set(getAttribute("OUT_Estimativa"), asset.getEstimativa() );
		procData.set(getAttribute("OUT_TipoDeImpacto"), asset.getTipoDeImpacto() );
		procData.set(getAttribute("OUT_AnaliseDeImpacto"), asset.getAnaliseDeImpacto() );
		procData.set(getAttribute("OUT_TipoDeRisco"), asset.getTipoDeRisco() );
		procData.set(getAttribute("OUT_AnaliseDeRisco"), asset.getAnaliseDeRisco() );
		procData.set(getAttribute("OUT_Complexidade"), asset.getComplexidade() );
		
		//Used to be a list
		listVar = procData.getList(getAttribute("OUT_AuthorList"));
		if(listVar!=null){
			listVar.clear();
			listVar.addNewItem(asset.getAuthor());
		}
		
		procData.set(getAttribute("OUT_DataDasEspecificacoes"), asset.getDataDasEspecificacoes() );
		procData.set(getAttribute("OUT_AprovadorDasEspecificacoes"), asset.getAprovadorDasEspecificacoes() );
		procData.set(getAttribute("OUT_DataDoServico"), asset.getDataDoServico() );
		procData.set(getAttribute("OUT_AprovadorDoServico"), asset.getAprovadorDoServico() );
		procData.set(getAttribute("OUT_Endpoint"), asset.getEndpoint() );
		procData.set(getAttribute("OUT_EspecificacaoFuncional"), asset.getEspecificacaoFuncional() );
		procData.set(getAttribute("OUT_EspecificacaoTestes"), asset.getEspecificacaoTestes() );
		procData.set(getAttribute("OUT_DesempenhoPreMigracao"), asset.getDesempenhoPreMigracao() );
		procData.set(getAttribute("OUT_DesempenhoServico"), asset.getDesempenhoServico() );
		//TODO
		//procData.set(getAttribute("OUT_ArquitecturaReferencia"), asset.getArquitecturaReferenciaList() );
		
		procData.set(getAttribute("OUT_ProxyClient"), asset.getProxyClient() );
		
		procData.set(getAttribute("OUT_CreatedDate"), asset.getCreatedDate() );
		procData.set(getAttribute("OUT_CreatedUser"), asset.getCreatedUser() );
		procData.set(getAttribute("OUT_LastUpdatedDate"), asset.getLastUpdatedDate() );
		procData.set(getAttribute("OUT_LastUpdatedUser"), asset.getLastUpdatedUser() );
		procData.set(getAttribute("OUT_CicloDeVida"), asset.getCicloDeVida() );

		listVar = procData.getList(getAttribute("OUT_KeywordsList"));
		if(listVar!=null){
			listVar.clear();
		for(int i=0; i< asset.getKeywordsList().size(); i++)      		        		        		
			listVar.addNewItem(asset.getKeywordsList().get(i));
		}
		
		procData.set(getAttribute("OUT_AssetFolder"), asset.getAssetFolder());
	  } 	 
  }

  public BlockP15032OER(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
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

  @Override
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

@Override
public Port after(UserInfoInterface userInfo, ProcessData procData) {
	// TODO Auto-generated method stub
	return null;
}

}
