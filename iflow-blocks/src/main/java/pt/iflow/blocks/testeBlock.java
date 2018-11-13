package pt.iflow.blocks;

import java.util.ArrayList;

import pt.iflow.api.core.BeanFactory;

import com.infosistema.OERIflow.IFlowUtilsMain;
import com.infosistema.OERIflow.IflowServicoMain;
import com.infosistema.oerrexiflowlib.data.OERAsset;
import com.infosistema.oerrexiflowlib.data.OERAssetServico;
import com.infosistema.oerrexiflowlib.utils.Config;

public class testeBlock {
	
	public static void main (String [] args){
		//byte[] content = BeanFactory.getDocumentsBean().getDocument(userInfo, procData, _artifactZip[0].getDocId()).getContent();
		
	IflowServicoMain servicoMain =  new IflowServicoMain();
	OERAssetServico asset = new OERAssetServico();
	asset.setId(1);
	asset.setNome("");
	asset.setAssetFolder("");
	asset.setArquitecturaReferenciaList(null);
	//servicoMain.getPreview(Config.getOERUsername(), Config.getOERPassword(), asset, content, tipoAdapter, descricaoAdapter, dadosInputAdapter, descricaoPipeline);
	Integer.parseInt("");
	
//	tester.getAplicationsList(Config.getOERUsername(), Config.getOERPassword()).getResponse();
//	tester.getAssetFolderList(Config.getOERUsername(), Config.getOERPassword()).getResponse();
	String[] _aplicacaoNameVersionList;
	String[] _aplicacaoIdList;
	
	IFlowUtilsMain tester = new IFlowUtilsMain();
	OERAsset[] aList = (OERAsset[]) tester.getAplicationsList(Config.getOERUsername(), Config.getOERPassword()).getResponse();
	_aplicacaoNameVersionList = new String[aList.length];
	_aplicacaoIdList = new String[aList.length];
	for(int i=0; i<aList.length; i++){
		_aplicacaoNameVersionList[i] = aList[i].getNome() +" - ";
		_aplicacaoIdList[i] = "" + aList[i].getId();
	}
	String[] _aplicacoesNameVersionSelectedList = null;
	String[] _aplicacoesIdSelectedList = null;
	for(int i=0; i<_aplicacoesIdSelectedList.length; i++){
		OERAsset asset1 = (OERAsset) servicoMain.getServicoById(Config.getOERUsername(), Config.getOERPassword(),Integer.parseInt(_aplicacoesIdSelectedList[i])).getResponse();
		_aplicacoesNameVersionSelectedList[i] =asset1.getNome()+" - " + asset1.getVersao();
	}
		

	ArrayList arqList = (ArrayList) tester.getArquitecturaDeReferenciaList(Config.getOERUsername(), Config.getOERPassword()).getResponse();
	for(int i=0; i< arqList.size(); i++){
		OERAsset arqAsset = (OERAsset) arqList.get(i); 
		arqAsset.getNome();
		arqAsset.getVersao();
	}
	
	tester.getValuesByName(Config.getOERUsername(), Config.getOERPassword(), "CompanhiaList").getResponse();
//
//
////	    tester.getValuesByName(Config.getOERUsername(), Config.getOERPassword(), "DriverList").getResponse();
//
//
//
//
//
//	    com.infosistema.oerrexiflowlib.data.OERAssetServico asset = new OERAssetServico();
//	    List<OERDocument> documentslist = new ArrayList<OERDocument>();
//	    //OERDocument doc = new OERDocument(nome, url);
		//asset.setDocumentslist(documentslist );
	}

}
