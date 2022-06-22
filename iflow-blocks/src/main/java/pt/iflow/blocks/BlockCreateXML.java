package pt.iflow.blocks;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.documents.DocumentDataStream;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.StringUtilities;



public class BlockCreateXML extends Block{
	public Port portIn, portSuccess, portEmpty, portError;

	private static final String ANO = "ano";
	private static final String MES = "mes";
	private static final String entReportada = "entReportada";
	private static final String entReportante = "entReportante";
	private static final String dtReferencia = "dtReferencia";
	private static final String outputFile = "outputFile";
	
	public BlockCreateXML(int anFlowId, int id, int subflowblockid, String filename) {
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

	public String before(UserInfoInterface userInfo, ProcessData procData) {
		return "";
	}

	public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
		return true;
	}

	/**
	 * Executes the block main action
	 * 
	 * @param dataSet
	 *            a value of type 'DataSet'
	 * @return the port to go to the next block
	 */
	public Port after(UserInfoInterface userInfo, ProcessData procData) {
		Port outPort = portSuccess;
		String login = userInfo.getUtilizador();
		StringBuffer logMsg = new StringBuffer();
		Documents docBean = BeanFactory.getDocumentsBean();

		String entReportadaVar = null;
		String entReportanteVar = null;
		String dtReferenciaVar = null;
		String outputFileVar = null;
		String ano = null;
		String mes = null;
		
		String dtReferenciaDate = null;
		String dtReferenciaTitle = null;
		
		//Database Connection
		Connection connection = null;
		String url = "jdbc:mysql://bbva.cl.uniksystem.pt:3306/paripersi";
		
		try {
			outputFileVar = this.getAttribute(outputFile);
			entReportadaVar = procData.transform(userInfo, this.getAttribute(entReportada));
			entReportanteVar = procData.transform(userInfo, this.getAttribute(entReportante));
			dtReferenciaVar = procData.transform(userInfo, this.getAttribute(dtReferencia));
			ano = procData.transform(userInfo, this.getAttribute(ANO));
			mes = procData.transform(userInfo, this.getAttribute(MES));

			dtReferenciaVar = dtReferenciaVar.replace("\"", "");
			LocalDate datetime = LocalDate.parse(dtReferenciaVar, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			dtReferenciaDate = datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			dtReferenciaTitle = datetime.format(DateTimeFormatter.ofPattern("yyyyMM"));
		} catch (Exception e1) {
			Logger.error(login, this, "after", procData.getSignature() + "error transforming attributes", e1);
		}		
		try{
			connection = DriverManager.getConnection(url, "iflow", "iflow");
		} catch(Exception e){
			Logger.error(login, this, "after", procData.getSignature() + "error establishing connection");
			outPort = portEmpty;
		}
		if (StringUtilities.isEmpty(outputFileVar) || StringUtilities.isEmpty(entReportadaVar) 
				|| StringUtilities.isEmpty(entReportanteVar) || StringUtilities.isEmpty(dtReferenciaVar)
				|| StringUtilities.isEmpty(ano) || StringUtilities.isEmpty(mes)) {
			Logger.error(login, this, "after", procData.getSignature() + "empty value for attributes");
			outPort = portEmpty;
		} 
			
		try {
			
			XMLOutputFactory f = XMLOutputFactory.newInstance();
			File tmpFile = File.createTempFile(this.getClass().getName(), ".xml");
			FileOutputStream fos = new FileOutputStream(tmpFile);
			XMLStreamWriter writer = f.createXMLStreamWriter(fos, "UTF-8");			
			writer.flush();
			fos.flush();
			fos.close();
			writer.close();	
			
			String entReportadaStr = String.valueOf(entReportadaVar);
		    while(entReportadaStr.length()<4) {
		    	entReportadaStr = "0" + entReportadaStr;
		    }		    

			//Generate XML
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		    Document doc = docBuilder.newDocument();
		    Element rootElement = doc.createElement("ReporteIncumprimento");
		    doc.appendChild(rootElement);
		    
		    	//header
			    Element header = doc.createElement("Header");
			    rootElement.appendChild(header);
			    createElement("EntidadeReportante", formatStringSize(entReportanteVar), header, doc);
			    createElement("EntidadeReportada", formatStringSize(entReportadaVar), header, doc);
			    createElement("DataReferencia", ""+dtReferenciaDate, header, doc);
			    createElement("VersaoXSD", "2.00", header, doc);
			    
		    	//body
			    Element body = doc.createElement("Body");
			    rootElement.appendChild(body);			   
			    
		    	//Quadro 1
				String query = "select * from t_rbp_quadro1 where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro1Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
			    for (int i = 0; i < quadro1Values.size() && i < 36; i++) {
					Element quadro1 = doc.createElement("Quadro1");				    
				    body.appendChild(quadro1);
				    createElement("CategoriaCredito", (String) quadro1Values.get(i).get("categoria"), quadro1, doc);
				    createElement("NumTotalContr", ""+(String) quadro1Values.get(i).get("numcontratos"), quadro1, doc);
				    createElement("MntDivTotalContr", formatField(quadro1Values.get(i), "montantedivida", "BigDecimalStr"), quadro1, doc);
				    createElement("NumContrIncump", ""+(String) quadro1Values.get(i).get("numcontratosinc"), quadro1, doc);
				    createElement("MntDivContrIncump", formatField(quadro1Values.get(i), "montantedividainc", "BigDecimalStr"), quadro1, doc);
				    createElement("MntIncumpContr", formatField(quadro1Values.get(i), "montanteinc", "BigDecimalStr"), quadro1, doc);
			    }
			    
			    //Quadro 2
				query = "select * from t_rbp_quadro2 where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro2Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
			    if(quadro2Values.size() > 0) {
					Element quadro2 = doc.createElement("Quadro2");
				    body.appendChild(quadro2);
				    createElement("NumContratosPERSI", ""+(String) quadro2Values.get(0).get("q2_Persi_numContratos"), quadro2, doc);
				    createElement("NumMutuariosPERSI", ""+(String) quadro2Values.get(0).get("q2_Persi_numTitularesContratos"), quadro2, doc);
				    createElement("MntDivContrPERSI", formatField(quadro2Values.get(0), "q2_Persi_montanteDivida", "BigDecimalStr"), quadro2, doc);
				    createElement("MntIncumpContrPERSI", formatField(quadro2Values.get(0), "q2_Persi_montanteIncump", "BigDecimalStr"), quadro2, doc);
				    createElement("NumContratosPARI", ""+(String) quadro2Values.get(0).get("q2_Pari_numContratos"), quadro2, doc);
				    createElement("NumMutuariosPARI", ""+(String) quadro2Values.get(0).get("q2_Pari_numTitularesContratos"), quadro2, doc);
				    createElement("MntDivContrPARI", formatField(quadro2Values.get(0), "q2_Pari_montanteDivida", "BigDecimalStr"), quadro2, doc); 
			    }
				    
			    //Quadro 3
				query = "select * from t_rbp_quadro3 where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro3Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro3Values.size(); i++) {
				    Element quadro3 = doc.createElement("Quadro3");
				    body.appendChild(quadro3);
				    createElement("CodigoIC", formatStringSize((String) quadro3Values.get(i).get("codigoic")), quadro3, doc);
				    createElement("IdContrato", (String) quadro3Values.get(i).get("nic"), quadro3, doc);
				    createElement("IdContratoCRC", (String) quadro3Values.get(i).get("nic"), quadro3, doc);
				    createElement("IdInstrumentoCRC", (String) quadro3Values.get(i).get("nic"), quadro3, doc);
				    createElement("CategoriaCredito", (String) quadro3Values.get(i).get("catcredito"), quadro3, doc);
				    if(((String) quadro3Values.get(i).get("catcredito")).equals("AA30") || ((String) quadro3Values.get(i).get("catcredito")).equals("AA31")){
				    	createElement("RegimeCH", (String) quadro3Values.get(i).get("regimecredito"), quadro3, doc);
				    }
				    createElement("DataCelebracao", formatField(quadro3Values.get(i), "datacontrato", "Date"), quadro3, doc);
				    createElement("DataTermo", formatField(quadro3Values.get(i), "datatermocontrato", "Date"), quadro3, doc);
				    createElement("MntInicial", formatField(quadro3Values.get(i), "montanteinicial", "String"), quadro3, doc);
				    createElement("MntDividaPERSI", formatField(quadro3Values.get(i), "montantedivida", "String"), quadro3, doc);
				    createElement("TipoTaxaJuro", (String) quadro3Values.get(i).get("tipotaxa"), quadro3, doc);
				    createElement("TAN", formatField(quadro3Values.get(i), "taxa", "String5"), quadro3, doc); 
				    if(((String) quadro3Values.get(i).get("tipotaxa")).equals("C02") || ((String) quadro3Values.get(i).get("tipotaxa")).equals("C03")){
				    	createElement("IndexanteTxJuro", (String) quadro3Values.get(i).get("indexante"), quadro3, doc);
				    	createElement("SpreadTxJuro", formatField(quadro3Values.get(i), "spread", "String5"), quadro3, doc);
				    }
				    createElement("PerCarDif", (String) quadro3Values.get(i).get("periodocarencia"), quadro3, doc);
				    if(((String) quadro3Values.get(i).get("dataincumprimento")) != null && !((String) quadro3Values.get(i).get("dataincumprimento")).equals("")){
				    	createElement("DataInicioIncump", formatField(quadro3Values.get(i), "dataincumprimento", "Date"), quadro3, doc);
				    }
				    createElement("MntIncumpPERSI", formatField(quadro3Values.get(i), "montanteactual", "String"), quadro3, doc); 
				    createElement("DataIniPERSI", formatField(quadro3Values.get(i), "dataentradapersi", "Date"), quadro3, doc);
				    createElement("MotivoIniPERSI", (String) quadro3Values.get(i).get("motivoentradapersi"), quadro3, doc);
				    createElement("PERSIFiador", (String) quadro3Values.get(i).get("persifiador"), quadro3, doc);
				}
				
			    //Quadro 4
				query = "select * from t_rbp_quadro4 where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro4Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro4Values.size(); i++) {
				    Element quadro4 = doc.createElement("Quadro4");
				    body.appendChild(quadro4);
				    createElement("CodigoIC", formatStringSize((String) quadro4Values.get(i).get("codigoic")), quadro4, doc);
				    createElement("IdContrato", (String) quadro4Values.get(i).get("nic"), quadro4, doc);
				    createElement("IdContratoCRC", (String) quadro4Values.get(i).get("nic"), quadro4, doc);
				    createElement("IdInstrumentoCRC", (String) quadro4Values.get(i).get("nic"), quadro4, doc);
				    createElement("DataRenegociacao", formatField(quadro4Values.get(i), "datarenegociacao", "Date"), quadro4, doc);
				    createElement("MntDividaReneg", formatField(quadro4Values.get(i), "montantedivida", "String"), quadro4, doc);
				    createElement("MntIncumpReneg", formatField(quadro4Values.get(i), "montanteactual", "String"), quadro4, doc);
				    createElement("MntReneg", formatField(quadro4Values.get(i), "montanterenegociado", "String"), quadro4, doc);
				    if(((String) quadro4Values.get(i).get("spread")).equals("0")) {
				    	createElement("VarSpread", formatField(quadro4Values.get(i), "spread", "String"), quadro4, doc);
				    }
				    if(((String) quadro4Values.get(i).get("taxajuro")).equals("0")) {
				    	createElement("VarTxJuro", formatField(quadro4Values.get(i), "taxajuro", "String"), quadro4, doc);
				    }
				    if(((String) quadro4Values.get(i).get("prazocontrato")).equals("0")) {
				    	createElement("VarPrazoContrato", formatField(quadro4Values.get(i), "prazocontrato", "String"), quadro4, doc);
				    }
				    if(((String) quadro4Values.get(i).get("prazocarenciacap")).equals("0")) {
				    	createElement("VarPrazoCarencia", formatField(quadro4Values.get(i), "prazocarenciacap", "String"), quadro4, doc);
				    }
				    if(((String) quadro4Values.get(i).get("prazocarenciajuros")).equals("0")) {
				    	createElement("VarPrazoCarenciaCapJuros", formatField(quadro4Values.get(i), "prazocarenciajuros", "String"), quadro4, doc);
				    }
				    if(((String) quadro4Values.get(i).get("capitalDiferido")).equals("0")) {
				    	createElement("VarDifUltPrestacao", formatField(quadro4Values.get(i), "capitalDiferido", "String"), quadro4, doc);
				    }
			    	createElement("VarOutras", ""+(String) quadro4Values.get(i).get("outras"), quadro4, doc);
				}
			    
			    //Quadro 5A
				query = "select * from t_rbp_quadro5a where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro5AValues = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro5AValues.size(); i++) {
				    Element quadro5A = doc.createElement("Quadro5A");
				    body.appendChild(quadro5A);
				    createElement("CodigoIC", formatStringSize((String) quadro5AValues.get(i).get("codigoic")), quadro5A, doc);
				    createElement("IdContrato", (String) quadro5AValues.get(i).get("nic"), quadro5A, doc);
				    createElement("IdContratoCRC", (String) quadro5AValues.get(i).get("nic"), quadro5A, doc);
				    createElement("IdInstrumentoCRC", (String) quadro5AValues.get(i).get("nic"), quadro5A, doc);
				    createElement("DataCelebracao", formatField(quadro5AValues.get(i), "datacontrato", "Date"), quadro5A, doc);
				    createElement("DataTermo", formatField(quadro5AValues.get(i), "datatermocontrato", "Date"), quadro5A, doc); 
				    createElement("MntInicial", formatField(quadro5AValues.get(i), "montante", "String"), quadro5A, doc);
				    createElement("TipoTaxaJuro", (String) quadro5AValues.get(i).get("tipotaxa"), quadro5A, doc);
				    createElement("TAN", formatField(quadro5AValues.get(i), "taxa", "BigDecimalStr5"), quadro5A, doc);
				    if(((String) quadro5AValues.get(i).get("tipotaxa")).equals("C02") || ((String) quadro5AValues.get(i).get("tipotaxa")).equals("C03")) {	
				    	createElement("IndexanteTxJuro", (String) quadro5AValues.get(i).get("indexante"), quadro5A, doc);
				    	createElement("SpreadTxJuro", formatField(quadro5AValues.get(i), "spread", "BigDecimalStr5"), quadro5A, doc);
				    }
				    createElement("PerCarDif", (String) quadro5AValues.get(i).get("periodocarencia"), quadro5A, doc);
				    createElement("Hipoteca", (String) quadro5AValues.get(i).get("contratogarantido"), quadro5A, doc);
				}
			    
			    //Quadro 5B
				query = "select * from t_rbp_quadro5b where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro5bValues = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro5bValues.size(); i++) {
				    Element quadro5B = doc.createElement("Quadro5B");
				    body.appendChild(quadro5B);
				    createElement("CodigoIC", formatStringSize((String) quadro5bValues.get(i).get("codigoic")), quadro5B, doc);
				    createElement("IdContrato", (String) quadro5bValues.get(i).get("nic"), quadro5B, doc);
				    createElement("IdContratoCRC", (String) quadro5bValues.get(i).get("nic"), quadro5B, doc);
				    createElement("IdInstrumentoCRC", (String) quadro5bValues.get(i).get("nic"), quadro5B, doc);
				    createElement("CategoriaCredito", (String) quadro5bValues.get(i).get("catcredito"), quadro5B, doc);
				    createElement("MntDividaCons", formatField(quadro5bValues.get(i), "montantedivida", "String"), quadro5B, doc);
				    createElement("MntIncumpCons", formatField(quadro5bValues.get(i), "montanteinc", "String"), quadro5B, doc);
				    createElement("IdContratoCons", (String) quadro5bValues.get(i).get("nicgeral"), quadro5B, doc);
				    createElement("IdContratoConsCRC", (String) quadro5bValues.get(i).get("nicgeral"), quadro5B, doc);
				    createElement("IdInstrumentoConsCRC", (String) quadro5bValues.get(i).get("nicgeral"), quadro5B, doc);
				}
			    
			    //Quadro 6
				query = "select * from t_rbp_quadro6 where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro6Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro6Values.size(); i++) {
				    Element quadro6 = doc.createElement("Quadro6");
				    body.appendChild(quadro6);
				    createElement("CodigoIC", formatStringSize((String) quadro6Values.get(i).get("codigoic")), quadro6, doc);
				    createElement("IdContratoOriginal", (String) quadro6Values.get(i).get("nicoriginal"), quadro6, doc);
				    createElement("IdContrato", (String) quadro6Values.get(i).get("nic"), quadro6, doc);
				    createElement("IdContratoOriginalCRC", (String) quadro6Values.get(i).get("nicoriginal"), quadro6, doc);
				    createElement("IdInstrumentoOriginalCRC", (String) quadro6Values.get(i).get("nicoriginal"), quadro6, doc);
				    createElement("IdContratoCRC", (String) quadro6Values.get(i).get("nic"), quadro6, doc);
				    createElement("IdInstrumentoCRC", (String) quadro6Values.get(i).get("nic"), quadro6, doc);
				    createElement("DataCelebracao", formatField(quadro6Values.get(i), "datacontrato", "Date"), quadro6, doc);
				    createElement("DataTermo", formatField(quadro6Values.get(i), "datatermocontrato", "Date"), quadro6, doc);
				    createElement("MntInicial", formatField(quadro6Values.get(i), "montante", "String"), quadro6, doc);
				    createElement("TipoTaxaJuro", (String) quadro6Values.get(i).get("tipotaxa"), quadro6, doc);
				    createElement("TAN", formatField(quadro6Values.get(i), "taxa", "BigDecimalStr5"), quadro6, doc);
				    if(((String) quadro6Values.get(i).get("tipotaxa")).equals("C02") || ((String) quadro6Values.get(i).get("tipotaxa")).equals("C03")) {	
				    	createElement("IndexanteTxJuro", (String) quadro6Values.get(i).get("indexante"), quadro6, doc);
				    	createElement("SpreadTxJuro", formatField(quadro6Values.get(i), "spread", "BigDecimalStr5"), quadro6, doc);
				    }
				    createElement("PerCarDif", (String) quadro6Values.get(i).get("periodocarencia"), quadro6, doc);
				    if(((String) quadro6Values.get(i).get("periodocarencia")).equals("E02") || ((String) quadro6Values.get(i).get("periodocarencia")).equals("E03") || ((String) quadro6Values.get(i).get("periodocarencia")).equals("E05")) {	
				    	createElement("PrazoCarencia", ""+(String) quadro6Values.get(i).get("duracaocarencia"), quadro6, doc);
				    }
				    createElement("Hipoteca", (String) quadro6Values.get(i).get("contratogarantido"), quadro6, doc);
				}
			    
			    //Quadro 7
				query = "select * from t_rbp_quadro7 where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro7Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro7Values.size(); i++) {
				    Element quadro7 = doc.createElement("Quadro7");
				    body.appendChild(quadro7);
				    createElement("CodigoIC", formatStringSize((String) quadro7Values.get(i).get("codigoic")), quadro7, doc);
				    createElement("IdContratoOriginal", (String) quadro7Values.get(i).get("nicoriginal"), quadro7, doc);
				    createElement("IdContrato", (String) quadro7Values.get(i).get("nic"), quadro7, doc);
				    createElement("IdContratoOriginalCRC", (String) quadro7Values.get(i).get("nicoriginal"), quadro7, doc);
				    createElement("IdInstrumentoOriginalCRC", (String) quadro7Values.get(i).get("nicoriginal"), quadro7, doc);
				    createElement("IdContratoCRC", (String) quadro7Values.get(i).get("nic"), quadro7, doc);
				    createElement("IdInstrumentoCRC", (String) quadro7Values.get(i).get("nic"), quadro7, doc);
				    createElement("DataCelebracao", formatField(quadro7Values.get(i), "datacontrato", "Date"), quadro7, doc);
				    createElement("DataTermo", formatField(quadro7Values.get(i), "datatermocontrato", "Date"), quadro7, doc);
				    createElement("MntTotalCredito", formatField(quadro7Values.get(i), "montante", "String"), quadro7, doc);
				    createElement("TipoTaxaJuro", (String) quadro7Values.get(i).get("tipotaxa"), quadro7, doc);
				    createElement("TAN", formatField(quadro7Values.get(i), "taxa", "String5"), quadro7, doc);
				    if(((String) quadro7Values.get(i).get("tipotaxa")).equals("C02") || ((String) quadro7Values.get(i).get("tipotaxa")).equals("C03")) {	
				    	createElement("IndexanteTxJuro", (String) quadro7Values.get(i).get("indexante"), quadro7, doc);
				    	createElement("SpreadTxJuro", formatField(quadro7Values.get(i), "spread", "String5"), quadro7, doc);
				    }
				    createElement("PerCarDif", (String) quadro7Values.get(i).get("periodocarencia"), quadro7, doc);
				    if(((String) quadro7Values.get(i).get("periodocarencia")).equals("E02") || ((String) quadro7Values.get(i).get("periodocarencia")).equals("E03") || ((String) quadro7Values.get(i).get("periodocarencia")).equals("E05")) {	
				    	createElement("PrazoCarencia", (String) quadro7Values.get(i).get("duracaocarencia"), quadro7, doc);
				    }
				    createElement("Hipoteca", (String) quadro7Values.get(i).get("contratogarantido"), quadro7, doc);
				}
			    
			    //Quadro 8
				query = "select * from t_rbp_quadro8 where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro8Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro8Values.size(); i++) {
				    Element quadro8 = doc.createElement("Quadro8");
				    body.appendChild(quadro8);
				    createElement("CodigoIC", formatStringSize((String) quadro8Values.get(i).get("codigoic")), quadro8, doc);
				    createElement("IdContrato", (String) quadro8Values.get(i).get("nic"), quadro8, doc);
				    createElement("IdContratoCRC", (String) quadro8Values.get(i).get("nic"), quadro8, doc);
				    createElement("IdInstrumentoCRC", (String) quadro8Values.get(i).get("nic"), quadro8, doc);
				    createElement("DataExtincaoPERSI", formatField(quadro8Values.get(i), "dataextincao", "Date"), quadro8, doc);
				    createElement("MotivoExtincao", (String) quadro8Values.get(i).get("motivo"), quadro8, doc);
				    if(((String) quadro8Values.get(i).get("motivo")).equals("G07")) {
				    	createElement("MntDividaRem", (String) quadro8Values.get(i).get("montantedivida"), quadro8, doc);
				    }
			 	}
				
			    //Quadro 9
				query = "select * from t_rbp_quadro9_pari where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro9Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro9Values.size(); i++) {
				    Element quadro9 = doc.createElement("Quadro9");
				    body.appendChild(quadro9);
				    createElement("CodigoIC", formatStringSize((String) quadro9Values.get(i).get("codigoic")), quadro9, doc);
				    createElement("IdContrato", (String) quadro9Values.get(i).get("nic"), quadro9, doc);
				    createElement("IdContratoCRC", (String) quadro9Values.get(i).get("nic"), quadro9, doc);
				    createElement("IdInstrumentoCRC", (String) quadro9Values.get(i).get("nic"), quadro9, doc);
				    createElement("CategoriaCredito", (String) quadro9Values.get(i).get("catcredito"), quadro9, doc);
				    if(((String) quadro9Values.get(i).get("catcredito")).equals("AA30") || ((String) quadro9Values.get(i).get("catcredito")).equals("AA31")) {
				    	createElement("RegimeCH", (String) quadro9Values.get(i).get("regimecredito"), quadro9, doc);
				    }
				    createElement("DataCelebracao", formatField(quadro9Values.get(i), "datacontrato", "Date"), quadro9, doc);
				    createElement("DataTermo", formatField(quadro9Values.get(i), "datatermocontrato", "Date"), quadro9, doc);
				    createElement("MntInicial", formatField(quadro9Values.get(i), "montanteinicial", "String"), quadro9, doc);
				    createElement("MntDividaPARI", formatField(quadro9Values.get(i), "montantedivida", "String"), quadro9, doc);
				    createElement("TipoTaxaJuro", (String) quadro9Values.get(i).get("tipotaxa"), quadro9, doc);
				    createElement("TAN", formatField(quadro9Values.get(i), "taxa", "String5"), quadro9, doc);
				    if(((String) quadro9Values.get(i).get("tipotaxa")).equals("C02") || ((String) quadro9Values.get(i).get("tipotaxa")).equals("C03")) {	
				    	createElement("IndexanteTxJuro", (String) quadro9Values.get(i).get("indexante"), quadro9, doc);
				    	createElement("SpreadTxJuro", formatField(quadro9Values.get(i), "spread", "String5"), quadro9, doc);
				    }
				    createElement("PerCarDif", (String) quadro9Values.get(i).get("periodocarencia"), quadro9, doc);
				    createElement("DataIniPARI", formatField(quadro9Values.get(i), "dataentradapari", "Date"), quadro9, doc);
				    createElement("MotivoIniPARI", (String) quadro9Values.get(i).get("motivoentradapari"), quadro9, doc);
				}
			    
			    //Quadro 10
				query = "select * from t_rbp_quadro10_pari where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro10Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro10Values.size(); i++) {
				    Element quadro10 = doc.createElement("Quadro10");
				    body.appendChild(quadro10);
				    createElement("CodigoIC", formatStringSize((String) quadro10Values.get(i).get("codigoic")), quadro10, doc);
				    createElement("IdContrato", (String) quadro10Values.get(i).get("nic"), quadro10, doc);
				    createElement("IdContratoCRC", (String) quadro10Values.get(i).get("nic"), quadro10, doc);
				    createElement("IdInstrumentoCRC", (String) quadro10Values.get(i).get("nic"), quadro10, doc);
				    createElement("DataRenegociacao", formatField(quadro10Values.get(i), "datarenegociacao", "Date"), quadro10, doc);
				    createElement("MntDividaReneg", formatField(quadro10Values.get(i), "montantedivida", "String"), quadro10, doc);
				    createElement("MntIncumpReneg", formatField(quadro10Values.get(i), "montanteactual", "String"), quadro10, doc);
				    createElement("MntReneg", formatField(quadro10Values.get(i), "montanterenegociado", "String"), quadro10, doc);
				    if(((String) quadro10Values.get(i).get("spread")).equals("0")) {	
				    	createElement("VarSpread", formatField(quadro10Values.get(i), "spread", "String"), quadro10, doc);
				    }
				    if(((String) quadro10Values.get(i).get("taxajuro")).equals("0")) {	
				    	createElement("VarTxJuro", formatField(quadro10Values.get(i), "taxajuro", "String"), quadro10, doc);
				    }
				    if(((String) quadro10Values.get(i).get("prazocontrato")).equals("0")) {	
				    	createElement("VarPrazoContrato", formatField(quadro10Values.get(i), "prazocontrato", "String"), quadro10, doc);
				    }
				    if(((String) quadro10Values.get(i).get("prazocarenciacap")).equals("0")) {
				    	createElement("VarPrazoCarencia", formatField(quadro10Values.get(i), "prazocarenciacap", "String"), quadro10, doc);
				    }
				    if(((String) quadro10Values.get(i).get("prazocarenciajuros")).equals("0")) {
				    	createElement("VarPrazoCarenciaCapJuros", formatField(quadro10Values.get(i), "prazocarenciajuros", "String"), quadro10, doc);
				    }
			    	if(((String) quadro10Values.get(i).get("taxajuro")).equals("0")) {	
				    	createElement("VarDifUltPrestacao", formatField(quadro10Values.get(i), "capitalDiferido", "String"), quadro10, doc);
			    	}
				    createElement("VarOutras", ""+(String) quadro10Values.get(i).get("outras"), quadro10, doc);
				}
			    
			    //Quadro 11A
				query = "select * from t_rbp_quadro11a_pari where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro11Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro11Values.size(); i++) {
				    Element quadro11A = doc.createElement("Quadro11A");
				    body.appendChild(quadro11A);
				    createElement("CodigoIC", formatStringSize((String) quadro11Values.get(i).get("codigoic")), quadro11A, doc);
				    createElement("IdContrato", (String) quadro11Values.get(i).get("nic"), quadro11A, doc);
				    createElement("IdContratoCRC", (String) quadro11Values.get(i).get("nic"), quadro11A, doc);
				    createElement("IdInstrumentoCRC", (String) quadro11Values.get(i).get("nic"), quadro11A, doc);
				    createElement("DataCelebracao", formatField(quadro11Values.get(i), "datacontrato", "Date"), quadro11A, doc);
				    createElement("DataTermo", formatField(quadro11Values.get(i), "datatermocontrato", "Date"), quadro11A, doc);
				    createElement("MntInicial", formatField(quadro11Values.get(i), "montante", "String"), quadro11A, doc);
				    createElement("TipoTaxaJuro", (String) quadro11Values.get(i).get("tipotaxa"), quadro11A, doc);
				    createElement("TAN", formatField(quadro11Values.get(i), "taxa", "BigDecimalStr5"), quadro11A, doc);
				    if(((String) quadro11Values.get(i).get("tipotaxa")).equals("C02") || ((String) quadro11Values.get(i).get("tipotaxa")).equals("C03")) {	
				    	createElement("IndexanteTxJuro", (String) quadro11Values.get(i).get("indexante"), quadro11A, doc);
				    	createElement("SpreadTxJuro", formatField(quadro11Values.get(i), "spread", "BigDecimalStr5"), quadro11A, doc);
				    }
				    createElement("PerCarDif", (String) quadro11Values.get(i).get("periodocarencia"), quadro11A, doc);
				    createElement("Hipoteca", (String) quadro11Values.get(i).get("contratogarantido"), quadro11A, doc);
				}
			    
			    //Quadro 11B
				query = "select * from t_rbp_quadro11b_pari where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro11bValues = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro11bValues.size(); i++) {
				    Element quadro11B = doc.createElement("Quadro11B");
				    body.appendChild(quadro11B);
				    createElement("CodigoIC", formatStringSize((String) quadro11bValues.get(i).get("codigoic")), quadro11B, doc);
				    createElement("IdContrato", (String) quadro11bValues.get(i).get("nic"), quadro11B, doc);
				    createElement("IdContratoCRC", (String) quadro11bValues.get(i).get("nic"), quadro11B, doc);
				    createElement("IdInstrumentoCRC", (String) quadro11bValues.get(i).get("nic"), quadro11B, doc);
				    createElement("CategoriaCredito", (String) quadro11bValues.get(i).get("catcredito"), quadro11B, doc);
				    createElement("MntDividaCons", formatField(quadro11bValues.get(i), "montantedivida", "String"), quadro11B, doc);
				    createElement("MntIncumpCons", formatField(quadro11bValues.get(i), "montanteinc", "String"), quadro11B, doc);
				    createElement("IdContratoCons", (String) quadro11bValues.get(i).get("nicgeral"), quadro11B, doc);
				    createElement("IdContratoConsCRC", (String) quadro11bValues.get(i).get("nicgeral"), quadro11B, doc);
				    createElement("IdInstrumentoConsCRC", (String) quadro11bValues.get(i).get("nicgeral"), quadro11B, doc);
				}
			    
			    //Quadro 12
				query = "select * from t_rbp_quadro12_pari where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro12Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro12Values.size(); i++) {
				    Element quadro12 = doc.createElement("Quadro12");
				    body.appendChild(quadro12);
				    createElement("CodigoIC", formatStringSize((String) quadro12Values.get(i).get("codigoic")), quadro12, doc);
				    createElement("IdContratoOriginal", (String) quadro12Values.get(i).get("nicoriginal"), quadro12, doc);
				    createElement("IdContrato", (String) quadro12Values.get(i).get("nic"), quadro12, doc);
				    createElement("IdContratoOriginalCRC", (String) quadro12Values.get(i).get("nicoriginal"), quadro12, doc);
				    createElement("IdInstrumentoOriginalCRC", (String) quadro12Values.get(i).get("nicoriginal"), quadro12, doc);
				    createElement("IdContratoCRC", (String) quadro12Values.get(i).get("nic"), quadro12, doc);
				    createElement("IdInstrumentoCRC", (String) quadro12Values.get(i).get("nic"), quadro12, doc);
				    createElement("DataCelebracao", formatField(quadro12Values.get(i), "datacontrato", "Date"), quadro12, doc);
				    createElement("DataTermo", formatField(quadro12Values.get(i), "datatermocontrato", "Date"), quadro12, doc);
				    createElement("MntInicial", formatField(quadro12Values.get(i), "montante", "String"), quadro12, doc);
				    createElement("TipoTaxaJuro", (String) quadro12Values.get(i).get("tipotaxa"), quadro12, doc);
				    createElement("TAN", formatField(quadro12Values.get(i), "taxa", "BigDecimalStr5"), quadro12, doc);
				    if(((String) quadro12Values.get(i).get("tipotaxa")).equals("C02") || ((String) quadro12Values.get(i).get("tipotaxa")).equals("C03")) {	
				    	createElement("IndexanteTxJuro", (String) quadro12Values.get(i).get("indexante"), quadro12, doc);
				    	createElement("SpreadTxJuro", formatField(quadro12Values.get(i), "spread", "BigDecimalStr5"), quadro12, doc);
				    }
				    createElement("PerCarDif", (String) quadro12Values.get(i).get("periodocarencia"), quadro12, doc);
				    if(((String) quadro12Values.get(i).get("periodocarencia")).equals("E02") || ((String) quadro12Values.get(i).get("periodocarencia")).equals("E03") || ((String) quadro12Values.get(i).get("periodocarencia")).equals("E05")) {	
				    	createElement("PrazoCarencia", ""+(String) quadro12Values.get(i).get("duracaocarencia"), quadro12, doc);
				    }
				    createElement("Hipoteca", (String) quadro12Values.get(i).get("contratogarantido"), quadro12, doc);
				}
				    
			    //Quadro 13
				query = "select * from t_rbp_quadro13_pari where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro13Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro13Values.size(); i++) {
				    Element quadro13 = doc.createElement("Quadro13");
				    body.appendChild(quadro13);
				    createElement("CodigoIC", formatStringSize((String) quadro13Values.get(i).get("codigoic")), quadro13, doc);
				    createElement("IdContratoOriginal", (String) quadro13Values.get(i).get("nicoriginal"), quadro13, doc);
				    createElement("IdContrato", (String) quadro13Values.get(i).get("nic"), quadro13, doc);
				    createElement("IdContratoOriginalCRC", (String) quadro13Values.get(i).get("nicoriginal"), quadro13, doc);
				    createElement("IdInstrumentoOriginalCRC", (String) quadro13Values.get(i).get("nicoriginal"), quadro13, doc);
				    createElement("IdContratoCRC", (String) quadro13Values.get(i).get("nic"), quadro13, doc);
				    createElement("IdInstrumentoCRC", (String) quadro13Values.get(i).get("nic"), quadro13, doc);
				    createElement("DataCelebracao", formatField(quadro13Values.get(i), "datacontrato", "Date"), quadro13, doc);
				    createElement("DataTermo", formatField(quadro13Values.get(i), "datatermocontrato", "Date"), quadro13, doc);
				    createElement("MntTotalCredito", formatField(quadro13Values.get(i), "montante", "String"), quadro13, doc);
				    createElement("TipoTaxaJuro", (String) quadro13Values.get(i).get("tipotaxa"), quadro13, doc);
				    createElement("TAN", formatField(quadro13Values.get(i), "taxa", "BigDecimalStr5"), quadro13, doc);
				    if(((String) quadro13Values.get(i).get("tipotaxa")).equals("C02") || ((String) quadro13Values.get(i).get("tipotaxa")).equals("C03")) {	
				    	createElement("IndexanteTxJuro", (String) quadro13Values.get(i).get("indexante"), quadro13, doc);
				    	createElement("SpreadTxJuro", formatField(quadro13Values.get(i), "spread", "String"), quadro13, doc);
				    }
				    createElement("PerCarDif", (String) quadro13Values.get(i).get("periodocarencia"), quadro13, doc);
				    if(((String) quadro13Values.get(i).get("periodocarencia")).equals("E02") || ((String) quadro13Values.get(i).get("periodocarencia")).equals("E03") || ((String) quadro13Values.get(i).get("periodocarencia")).equals("E05")) {	
				    	createElement("PrazoCarencia", (String) quadro13Values.get(i).get("duracaocarencia"), quadro13, doc);
				    }
				    createElement("Hipoteca", (String) quadro13Values.get(i).get("contratogarantido"), quadro13, doc);
				}
				
			    //Quadro 14
				query = "select * from t_rbp_quadro14_pari where ANO = {0} and MES = {1}";
				List<HashMap<String,Object>> quadro14Values = fillAtributtes(null, connection, userInfo, query, new Object[] { ano, mes });
				for (int i = 0; i < quadro14Values.size(); i++) {
				    Element quadro14 = doc.createElement("Quadro14");
				    body.appendChild(quadro14);
				    createElement("CodigoIC", formatStringSize((String) quadro14Values.get(i).get("codigoic")), quadro14, doc);
				    createElement("IdContrato", (String) quadro14Values.get(i).get("nic"), quadro14, doc);
				    createElement("IdContratoCRC", (String) quadro14Values.get(i).get("nic"), quadro14, doc);
				    createElement("IdInstrumentoCRC", (String) quadro14Values.get(i).get("nic"), quadro14, doc);
				    createElement("DataExtincaoPARI", formatField(quadro14Values.get(i), "dataextincao", "Date"), quadro14, doc);
				    createElement("MotivoExtincaoPARI", (String) quadro14Values.get(i).get("motivo"), quadro14, doc);
				}

				    
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		    DOMSource source = new DOMSource(doc);
		    StreamResult result = new StreamResult(tmpFile);
		    transformer.transform(source, result);
		  
		    
			pt.iflow.connector.document.Document document = new DocumentDataStream(0, null, null, null, 0, 0, 0);
			document.setFileName("INCUMP." + entReportadaStr + "." + dtReferenciaTitle +".xml");
			FileInputStream fis = new FileInputStream(tmpFile);
			((DocumentDataStream) document).setContentStream(fis);
			byte[] fileContent = Files.readAllBytes(tmpFile.toPath());
			document.setContent(fileContent);

			document = (DocumentData) docBean.addDocument(userInfo, procData, document);
			
			ProcessListVariable docsVar = procData.getList(outputFileVar);
			docsVar.parseAndAddNewItem(String.valueOf(document.getDocId()));
			fis.close();
			tmpFile.delete();
			outPort = portSuccess;
		} catch (Exception e) {
			Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
			outPort = portError;
		} finally {
			DatabaseInterface.closeResources(connection);
			logMsg.append("Using '" + outPort.getName() + "';");
			Logger.logFlowState(userInfo, procData, this, logMsg.toString());					
		}
		
		return outPort;
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

	public static ArrayList<HashMap<String, Object>> fillAtributtes(XMLStreamWriter writer, Connection connection,
			UserInfoInterface userInfo, String query, Object[] parameters) throws SQLException {
		Connection db = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String filledQuery = null;
		HashMap<String, Object> resultAux = new HashMap<>();
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		
		try {
			db = null;
			for(int i=0; i<parameters.length; i++)
				if(parameters[i] instanceof Integer)
					parameters[i] = StringUtils.remove(StringUtils.remove(parameters[i].toString(),','),'.');
			filledQuery = MessageFormat.format(query, parameters);
			pst = connection.prepareStatement(filledQuery);
			rs = pst.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			
			while(rs.next()) {
				resultAux = new HashMap<>();
				for (int i = 1; i < (rsm.getColumnCount() + 1); i++) {
					String column ="" +rsm.getColumnName(i) ;
					String value ="" + rs.getObject(i);
					resultAux.put(rsm.getColumnName(i), rs.getObject(i));
		
					if (rsm.getColumnName(i).endsWith("_id") || rsm.getColumnName(i).equals("id") || writer==null)
						continue;
					else if(rs.getObject(i)==null)
						continue;
					else if(StringUtils.equalsIgnoreCase(rsm.getColumnName(i),"type"))
						writer.writeAttribute("xsi","",rsm.getColumnName(i), rs.getString(i));
					else if (rsm.getColumnType(i) == java.sql.Types.VARCHAR) {
						writer.writeAttribute(rsm.getColumnName(i), rs.getString(i));
					} else if (rsm.getColumnType(i) == java.sql.Types.DATE) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						writer.writeAttribute(rsm.getColumnName(i), sdf.format(rs.getDate(i)));
					} else if (rsm.getColumnType(i) == java.sql.Types.TIMESTAMP) {
						SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
						String timeAux = sdfDate.format(rs.getTimestamp(i))+"T"+sdfTime.format(rs.getTimestamp(i));
						writer.writeAttribute(rsm.getColumnName(i), timeAux);
					} else if (rsm.getColumnType(i) == java.sql.Types.DECIMAL) {
						DecimalFormat df = new DecimalFormat(
								"##################################################.############################");
						writer.writeAttribute(rsm.getColumnName(i), df.format(rs.getDouble(i)));
					} else if (rsm.getColumnType(i) == java.sql.Types.BOOLEAN || rsm.getColumnType(i) == java.sql.Types.TINYINT) {
						int valAux = rs.getBoolean(i) ? 1 : 0;
						writer.writeAttribute(rsm.getColumnName(i), "" + valAux);
					} else if (rsm.getColumnType(i) == java.sql.Types.INTEGER) {
						writer.writeAttribute(rsm.getColumnName(i), String.format("%d", rs.getInt(i)));
					}
				}
				resultList.add(resultAux);
			}
		} catch (Exception e) {
			Logger.error(userInfo.getUtilizador(), "FileGeneratorUtils", "fillAtributtes",
					filledQuery + e.getMessage(), e);
			try {
				writer.writeAttribute("nome", "nome correcto");
			} catch (XMLStreamException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			DatabaseInterface.closeResources(db, pst, rs);
		}

		return resultList;
	}
	
	public void createElement(String elementStr, String value, Element parent, Document doc) {
		try {
			Element element = doc.createElement(elementStr);
			element.appendChild(doc.createTextNode(value));
			parent.appendChild(element);
		}catch(Exception e) {
			System.out.println("Element: " + elementStr);		
		}
	}
	
	public String formatField(HashMap<String,Object> quadro, String field, String type) {
		BigDecimal bd = null;
		String valor = null;		
		
		if(type.equals("BigDecimal") || type.equals("BigDecimal5")) {//BigDecimal
			bd = (BigDecimal) quadro.get(field);
			
		}else if(type.equals("BigDecimalStr") || type.equals("BigDecimalStr5")) {
			valor = (String) quadro.get(field);
			valor = valor.replace(",", ".");
			bd = new BigDecimal(valor);
			
		}else if(type.equals("Integer")) {//Integer
			valor = "" + (Integer) quadro.get(field);
			valor = valor.replace(",", ".");
			return valor;
			
		}else if(type.equals("Decimal")) {//Decimal
			valor = "" + (Float) quadro.get(field);
			valor = valor.replace(",", ".");
			return valor;
			
		}else {//String
			valor = (String) quadro.get(field);
			
			if(type.equals("Date")) {//Date
				LocalDate datetime = LocalDate.parse(valor, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
				valor = datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				return valor;	
				
			}else if(type.equals("DateAlt")) {
				LocalDate datetime = LocalDate.parse(valor, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
				valor = datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				return valor;	
			}else{
				if(valor.contains(",")) {
					valor = valor.replace(",", ".");
				}
				
				bd = new BigDecimal(valor);
			}
		}
		
		if(bd == null) {
			return ""+bd;
		}
		
		if(type.equals("String5") || type.equals("BigDecimal5") || type.equals("BigDecimalStr5")) {
			bd = bd.setScale(5, RoundingMode.HALF_UP);
		}else {
			bd = bd.setScale(2, RoundingMode.HALF_UP);
		}

		return ""+bd;
	}
	
	public String formatStringSize(String string) {
		String stringStr = String.valueOf(string);
	    while(stringStr.length()<4) {
	    	stringStr = "0" + stringStr;
	    }
	    return stringStr;
	}
}


