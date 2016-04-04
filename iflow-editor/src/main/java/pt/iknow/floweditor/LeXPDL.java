package pt.iknow.floweditor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pt.iflow.api.flows.FlowUpgrade;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlBlock;
import pt.iflow.api.xml.codegen.flow.XmlFlow;
import pt.iflow.api.xml.codegen.flow.XmlPort;
import pt.iflow.api.xml.codegen.flow.XmlPosition;
import pt.iknow.floweditor.LerFicheiro.Ficheiro;
import pt.iknow.utils.StringUtilities;

/**
 * 
 * @author Ana
 * Le ficheiro XPDL e converte para XML iFlow
 */
public class LeXPDL {

	private static Map<String,BlocoXML> blocos; /**Guarda a informação dos blocos do fluxo**/
	private static String packageDescription; /**Guarda a descrição do diagrama**/ 
	private static String packageAuthor; /**Guarda o autor do fluxo**/
	private static int countBlocos; /** Contador de blocos do fluxo**/
	private static boolean isRoute; /**Quando passa por uma route;**/
	private static boolean isRouteSin; /**Quando passa por uma route do tipo Parallel**/
	private static String idRoute; 	/**Guarda id da Route**/
	private static boolean isError; /** Diz se um bloco é tipo Compensation**/
	private static List<AssociationC> asso; /**Lista de Associações**/
	private static boolean alreadyStart; /** Caso exista varios start no BPMN, garante que o fluxo do iFlow so tem um start*/
	protected static List<String> lostInformation; /** Guarda informação sobre os dados perdidos**/
	protected static boolean exclusive=false;

	/**
	 * Inicializa variaveis globais.
	 */
	public static void inicializaVar(){
		blocos= new HashMap<String,BlocoXML>(); 
		packageDescription=""; 
		packageAuthor=""; 
		countBlocos=1;
		isRoute=false; 	
		isRouteSin=false; 
		idRoute=""; 
		isError=false;
		lostInformation=new ArrayList<String>();
		asso= new ArrayList<AssociationC>();
		alreadyStart=false;
	}

	/***
	 * Trata informação do ficheiro xpdl.
	 * @param desenho
	 * @param ficheiro - ficheiro xpdl do fluxo
	 * @return um ficheiro com a informação dos blocos e ligações.
	 * @throws Exception
	 */
	public static Ficheiro readFile(Desenho desenho, File ficheiro) throws Exception {

		inicializaVar();
		DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db= dbf.newDocumentBuilder();
			Document dom = db.parse(ficheiro);

			//Normalize the XML Structure;
			dom.getDocumentElement().normalize();
			//PackageHeader
			readPackageHeader(dom);
			//RedefinableHeader
			readRedefinableHeader(dom);
			//Artifacts
			readArtifacts(dom);
			//Activities
			readActivities(dom);
			//Associations
			readAssociations(dom);
			//Transitions
			readTransitions(dom);

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		Ficheiro f=convertToXML(desenho);
		return f;
	}

	/**
	 * Guarda informação dos blocos no iFlow.
	 * @param desenho
	 * @return
	 * @throws Exception
	 */
	private static Ficheiro convertToXML(Desenho desenho) throws Exception {

		Ficheiro ret=null;
		LibrarySet cbib = desenho.getLibrarySet();

		try{
			ret=new Ficheiro();
			Random n=new Random();
			ArrayList<InstanciaComponente> comp = new ArrayList<InstanciaComponente>();
			ArrayList<Linha> linhas = new ArrayList<Linha>();
			ret.componentes = comp;
			ret.linhas = linhas;
			int MAXID=0;

			XmlFlow flow=new XmlFlow();
			int auxN=-1;
			while(auxN<0)
				auxN=n.nextInt();
			flow.setName("Diagram"+auxN);
			flow.setAuthor(packageAuthor);
			flow.setDescription(packageDescription);
			flow.setIFlowVersion(FlowUpgrade.VERSION_4X);
			ret.author=flow.getAuthor();
			ret.description=flow.getDescription();
			ret.iFlowVersion=flow.getIFlowVersion();
			ret.name=flow.getName();


			if(StringUtilities.isEmpty(ret.description) || " ".equals(ret.description)){
				ret.description=ret.name;
			}

			/**load blocks information*/
			for(Entry<String, BlocoXML> entry: blocos.entrySet()){

				if(!entry.getValue().getClasse().equals("")){
					XmlBlock block= new XmlBlock();
					block.setId(entry.getValue().getIdXmlBloco());//Cada bloco é unico 
					block.setName(entry.getValue().getName());
					block.setType(entry.getValue().getClasse());

					for(int a=0; a<entry.getValue().getExtendedAttributes().size();a++){
						XmlAttribute attribute= new XmlAttribute();
						attribute.setValue(entry.getValue().getExtendedAttributes().get(a).getValue());
						attribute.setName(entry.getValue().getExtendedAttributes().get(a).getName());
						block.addXmlAttribute(attribute);
					}
					for(int p=0; p < entry.getValue().getCounterPort(); p++){
						XmlPort port= new XmlPort();
						String b=entry.getValue().getPortOut()[p];
						port.setConnectedBlockId(blocos.get(b).getIdXmlBloco());
						port.setName(entry.getValue().getClasse());
						block.addXmlPort(port);
					}
					XmlPosition position=new XmlPosition();
					position.setX((int)entry.getValue().getXCoordinate());
					position.setY((int)entry.getValue().getYCoordinate());
					block.setXmlPosition(position);
					flow.addXmlBlock(block);
				}
			}

			/**Carrega componentes na biblioteca*/

			for(int i=0; i<flow.getXmlBlockCount();i++){

				XmlBlock bloco= flow.getXmlBlock(i);
				String tipo=bloco.getType();
				Componente_Biblioteca cb=cbib.getComponent(tipo);
				if(cb==null)
					throw new Exception(Mesg.ErroTipoNaoExiste + " "+ tipo);
				InstanciaComponente ic= cb.cria(desenho);
				ic.Mudar_Nome(bloco.getName());
				ic.ID=bloco.getId();

				MAXID=Math.max(ic.ID, MAXID);
				ic.Posicao_X=bloco.getXmlPosition().getX();
				ic.Posicao_Y=bloco.getXmlPosition().getY();

				/**Atibutos*/
				for(int k=0; k<bloco.getXmlAttributeCount(); k++){
					XmlAttribute a=bloco.getXmlAttribute(k);
					Atributo atr=ic.getAtributo(a.getName());
					if(atr!=null)
						atr.setValor(a.getValue());
					else
						ic.addAtributo(new AtributoImpl(a.getName(),a.getValue(),a.getDescription(),null));	
				}
				comp.add(ic);	
			}

			/**Liga Componentes */
			for(int i=0; i<flow.getXmlBlockCount();i++){
				XmlBlock bloco=flow.getXmlBlock(i);
				InstanciaComponente icPai= daPorID(bloco.getId(),comp);

				int countPort=0;
				for(int k=0; k < bloco.getXmlPortCount(); k++){
					XmlPort port=bloco.getXmlPort(k);
					InstanciaComponente icfilho=daPorID(port.getConnectedBlockId(),comp);
					boolean isE=false;
					for(Entry<String, BlocoXML> entry: blocos.entrySet()){	
						if(entry.getValue().getIdXmlBloco()==icfilho.ID){
							if(entry.getValue().getIsError()){
								isE=true;
							}
						}
					}
					if(isE){
						Linha l=new Linha();
						l.escolhido=1;
						Componente.Liga(l, 0, icfilho,0);//posição Zero da lista de entradas;
						l.escolhido=2;
						Componente.Liga(icPai,1,l,0); //Posição zero da lista de saidas;
						linhas.add(l);
						isE=false;
					}
					else if(bloco.getXmlPortCount()==1 && !isE){
						Linha l=new Linha();
						l.escolhido=1;
						Componente.Liga(l, 0, icfilho,0);//posição Zero da lista de entradas;
						l.escolhido=2;
						Componente.Liga(icPai,0,l,0); //Posição zero da lista de saidas;
						linhas.add(l);		
					}
					else if(bloco.getXmlPortCount()>1 && countPort<icPai.lista_estado_saidas.size()){
						Linha l =new Linha();
						l.escolhido=1;
						Componente.Liga(l,0,icfilho,0);//posiçao zero da lista de entradas;
						l.escolhido=2;
						Componente.Liga(icPai,countPort,l,0);//posicao count da lista de saidas;
						linhas.add(l);
						countPort++;	
					}
				}
			}
			ret.nextBlockNum = MAXID + 1;

		} catch (Exception e) {
			FlowEditor.log("error", e);
			throw e;
		}
		return ret;
	}

	private static InstanciaComponente daPorID(int id, ArrayList<InstanciaComponente> comp) {
		for (int i = 0; i < comp.size(); i++) {
			InstanciaComponente ic = comp.get(i);
			if (ic.ID == id)
				return ic;
		}
		return null;
	}

	/**
	 * Trata e lê a informação sobre as Associaçoes do fluxo BPMN
	 * @param dom- documento xml
	 */
	private static void readAssociations(Document dom) {

		NodeList n= dom.getElementsByTagName("Association");
		if(n.getLength()!=0){
			String lostinfo="Associations: Id; TooldId; Bordercolor; Coordinates";
			lostInformation.add(lostinfo);
		}
		for (int temp = 0; temp < n.getLength(); temp++)
		{
			Node node = n.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{	
				Element eElement = (Element) node;
				String from= eElement.getAttributeNode("Source").getTextContent();
				String to=eElement.getAttributeNode("Target").getTextContent();
				AssociationC a= new AssociationC(from, to);
				asso.add(a);		
			}
		}
	}

	/**
	 * Trata e lê a informação das transições do fluxo BPMN.
	 * @param dom - documento XML
	 */
	private static void readTransitions(Document dom) {

		String idBlock="";
		String idAux=idRoute;
		BlocoXML b=null;
		List<String> auxGatSin=new ArrayList<String>();
		List<ErrorEvent> errorList= new ArrayList<ErrorEvent>();
		boolean auxE=false;

		for(AssociationC a: asso){
			BlocoXML t=blocos.get(a.getTarget());
			t.setTo(blocos.get(a.getSource()).getID());
		}
		
		NodeList n= dom.getElementsByTagName("Transition");
		for (int temp = 0; temp < n.getLength(); temp++)
		{
			Node node = n.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{	
				Element eElement = (Element) node;
				idBlock= eElement.getAttributeNode("From").getTextContent();
				String to=eElement.getAttributeNode("To").getTextContent();
				
				if(blocos.containsKey(idBlock)&&blocos.containsKey(to)){

					/**Verifica se existem associações para aquele idBloco e caso exista, 
					 liga a entrada do bloco condição ao bloco "from" e a saida ao bloco "to" da transicao********/
					
					BlocoXML bloco= blocos.get(idBlock);
					
					/**Trata saidas de erro******************************************************************/
					if(bloco.getIsError()||blocos.get(to).getIsError()){
						trataErrorEvent(bloco, errorList,idBlock, to, auxE);
					}
					else{
						/**Trata as Routes com varias entradas e saidas**********************************************/
						String id="Block"+countBlocos;
						String name="Block"+countBlocos;
						BlocoXML aux=new BlocoXML(id,name,countBlocos);

						if(idBlock.equals(idRoute)&&!isRouteSin){

							if(bloco.isExclusive()){
								b=bloco;
								b.setTo(blocos.get(to).getID());
							}
							else{
								aux.setClasse("BlockCondicao");
								aux.setXCoordinate(String.valueOf(blocos.get(idAux).getXCoordinate()));
								aux.setYCoordinate(String.valueOf(blocos.get(idAux).getYCoordinate()+90));
								blocos.put(id, aux);
								countBlocos++;

								b= blocos.get(idAux);
								b.setTo(blocos.get(to).getID());
								b.setTo(blocos.get(id).getID());
								idAux=id;
							}
						}
						else if(to.equals(to)&&isRouteSin){
							aux.setClasse("BlockSincronizacao");
							aux.setXCoordinate(String.valueOf(blocos.get(idAux).getXCoordinate()));
							aux.setYCoordinate(String.valueOf(blocos.get(idAux).getYCoordinate()+90));
							blocos.put(id, aux);
							countBlocos++;

							b= blocos.get(idBlock);
							b.setTo(blocos.get(to).getID());
							b.setTo(blocos.get(id).getID());
							auxGatSin.add(idAux);
							idAux=id;		
						}
						else if(idBlock.equals(idRoute)&&isRouteSin){
							for(int r=0; r<blocos.get(idRoute).getCounterPort();r++){
								String idR=auxGatSin.get(r);
								blocos.get(idR).setTo(blocos.get(to).getID());
							}
						}
						else
							bloco.setTo(blocos.get(to).getID());
					}
				}
			}
		}	
		removeProblems(idAux,errorList,auxE,b);		
		String lostinfo="Transitions: Coordinates; TooldId; Border Color";
		lostInformation.add(lostinfo);
	}

	/**
	 *Trata eventos do tipo compensação.
	 *Método auxiliar
	 */
	private static void trataErrorEvent(BlocoXML bloco, List<ErrorEvent> errorList, String idBlock, String to, boolean auxE) {

		if(bloco.getIsError()){
			auxE=true;
			if(errorList.size()!=0){
				for(ErrorEvent e: errorList)
					if(e.getBlockError().equalsIgnoreCase(idBlock))
						e.setBlockTO(to);
			}
			else{
				ErrorEvent ev= new ErrorEvent();
				ev.setBlockError(idBlock);
				ev.setBlockTO(to);
				errorList.add(ev);
			}
		}
		else if(blocos.get(to).getIsError()){
			if(errorList.size()!=0){
				for(ErrorEvent e: errorList)
					if(e.getBlockError().equalsIgnoreCase(to)){
						e.setBlockFrom(idBlock);
						bloco.setTo(to);
					}
			}
			else{
				ErrorEvent ev= new ErrorEvent();
				ev.setBlockError(to);
				ev.setBlockFrom(idBlock);
				bloco.setTo(to);
				errorList.add(ev);
			}
		}
	}

	/**
	 *Remove insconsistencias das transições.
	 *Metodo auxiliar
	 */
	private static void removeProblems(String idAux, List<ErrorEvent> errorList, boolean auxE, BlocoXML b) {

		if(!idRoute.equals("")){
			if(blocos.get(idAux).getCounterPort()==0){
				b.removePort(1);
				blocos.remove(idAux);
			}
		}
		else if(auxE){
			for(ErrorEvent e: errorList){
				BlocoXML frome=blocos.get(e.getBlockFrom());
				BlocoXML toe= blocos.get(e.getBlockTo());
				BlocoXML error= blocos.get(e.getBlockError());	
				String[] aux=frome.getPortOut();					
				for(int p=0; p<frome.getCounterPort();p++){
					if(aux[p].equals(error.getID())){	
						toe.setIsError(true);
						aux[p]=toe.getID();
						blocos.remove(error.getID());
					}
				}
			}
		}	
	}

	/**
	 * Trata e lê informação dos blocos do fluxo BPMN
	 */
	private static void readActivities(Document dom){

		String id="";
		String name="";
		String xCoordinate="";
		String yCoordinate="";
		String lostinfo="Activities: Description; Documentation; TooldID; Height; Width; Bordercolor Fillcolor; Performes; Documentation\n"
				+ "All routes are converted to exclusive or sincronizacao route";
		lostInformation.add(lostinfo);

		NodeList n= dom.getElementsByTagName("Activity");
		for (int temp = 0; temp < n.getLength(); temp++)
		{
			Node node = n.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{	
				Element eElement = (Element) node;
				id= eElement.getAttributeNode("Id").getTextContent();
				name=eElement.getAttributeNode("Name").getTextContent();

				NodeList coord=eElement.getElementsByTagName("Coordinates");
				for (int c = 0; c < coord.getLength(); c++)
				{
					Node no = coord.item(c);
					Element e = (Element) no;
					xCoordinate=e.getAttributeNode("XCoordinate").getTextContent();
					yCoordinate=e.getAttributeNode("YCoordinate").getTextContent();	
				}

				if(name.equals(""))
					name="Block"+countBlocos;

				BlocoXML bloco=new BlocoXML(id,name,countBlocos);

				bloco.setXCoordinate(xCoordinate);	
				bloco.setYCoordinate(yCoordinate);

				String classe= mapeiaXml(eElement,dom,name);

				if(!classe.equals(""))
					bloco.setClasse(classe);
				else 
					bloco.setClasse("");
				if(isError){
					bloco.setIsError(true); /**Sinaliza que o bloco é do tipo compensação ou erro e como tal tem de ser ligado a uma saida de erro */
					isError=false;
				}
				/**Guarda o id da Route, isto será util para mapear routes com mais de 2 entradas ou saidas**/
				if(isRoute==true){
					if(exclusive==true){
						bloco.setisExclusive(true);
						exclusive=false;
					}
					idRoute=id;
					isRoute=false;
				}

				//ExtendedAttributes
				NodeList ea=eElement.getElementsByTagName("ExtendedAttribute");
				for (int c = 0; c < ea.getLength(); c++)
				{
					Node no = ea.item(c);
					Element e = (Element) no;
					String nameA=e.getAttributeNode("Name").getTextContent();
					String value=e.getAttributeNode("Value").getTextContent();
					bloco.setExtendedAttributes(nameA,value);
				}
				blocos.put(id, bloco);
				countBlocos++;
			}
		}
	}

	/**
	 * Mapeia Blocos BPMN para Blocos iFlow
	 * @param name 
	 */
	private static String mapeiaXml(Element node, Document dom, String name){
		String classe="";
		String lost="";

		if(node.getElementsByTagName("StartEvent").getLength()>0 && !alreadyStart){
			classe="BlockStart";
			alreadyStart=true;
			lost="Block"+name+":Start additional information";
			lostInformation.add(lost);
		}
		else if(node.getElementsByTagName("EndEvent").getLength()>0){
			classe="BlockEnd";
			lost="Block"+ name +":End additional information";
			lostInformation.add(lost);
		}
		else if(node.getElementsByTagName("IntermediateEvent").getLength()>0){

			NodeList n = node.getElementsByTagName("IntermediateEvent");
			for (int c = 0; c < n.getLength(); c++)
			{
				Node no = n.item(c);
				Element e = (Element) no;
				String type=e.getAttributeNode("Trigger").getTextContent();

				if(type.equalsIgnoreCase("Escalation"))
					classe="BlockForwardUp";
				else if(type.equalsIgnoreCase("Message"))
					classe="BlockNotification";
				else if(type.equalsIgnoreCase("Timer"))
					classe="BlockEvento";
				else if(type.equalsIgnoreCase("Conditional"))
					classe="BlockValidacoes";
				else if(type.equalsIgnoreCase("Compensation")){
					isError=true;
					classe="BlockBeanShell";
				}
				else
					classe="BlockBeanShell";
			}
		}

		else if(node.getElementsByTagName("BlockActivity").getLength()>0){
			classe="BlockSubFlow";
			lost="Block"+name+": ActivitySets";
			lostInformation.add(lost);
		}
		else if(node.getElementsByTagName("TaskScript").getLength()>0){
			classe="BlockBeanShell";
			lost="Block"+name+": Script Information";
			lostInformation.add(lost);

		}
		else if(node.getElementsByTagName("TaskService").getLength()>0){
			lost="Block"+name+": Service Information";
			classe="BlockWebServiceSinc";
			lostInformation.add(lost);	

		}
		else if(node.getElementsByTagName("TaskUser").getLength()>0){
			lost="Block"+name+": User Information";
			lostInformation.add(lost);
			classe="BlockFormulario";	

		}
		else if(node.getElementsByTagName("TaskSend").getLength()>0){
			lost="Block"+name+":Send Information";
			lostInformation.add(lost);
			classe="BlockEmail";

		}
		else if(node.getElementsByTagName("TaskReceive").getLength()>0){
			lost="Block"+name+": Task Receive Information (Convert to BlockBeanShell)";
			lostInformation.add(lost);
			classe="BlockBeanShell";
		}
		else if(node.getElementsByTagName("TaskManual").getLength()>0){
			lost="Block"+name+": ManualTask Manual Information (Convert to BlockBeanShell)";
			lostInformation.add(lost);
			classe="BlockBeanShell";

		}
		else if(node.getElementsByTagName("TaskBusinessRule").getLength()>0){
			lost="Block"+name+": Task Business Rule Information (Convert to BlockBeanShell)";
			lostInformation.add(lost);
			classe="BlockBeanShell";
		}
		else if(node.getElementsByTagName("Route").getLength()>0){
			
			NodeList n = node.getElementsByTagName("Route");
			for (int c = 0; c < n.getLength(); c++)
			{
				Node no = n.item(c);
				Element e = (Element) no;
				Attr type=e.getAttributeNode("GatewayType");
				if(type!=null){
					if(type.getTextContent().equalsIgnoreCase("Parallel")){
						classe="BlockSincronizacao";
						isRouteSin=true;
					}
					else if(type.getTextContent().equalsIgnoreCase("Exclusive")){
						classe="BlockCondicao";
						exclusive=true;
					}	
					isRoute=true;
				}
				else
					classe="BlockCondicao";
				isRoute=true;
			}
		}
		else
			classe="BlockBeanShell";

		return classe;
	}

	/**
	 * Trata e lê informação BPMN dos artefactos
	 */
	private static void readArtifacts(Document dom) throws XPathExpressionException {

		String id="";
		String artifactType="";
		String textAnnotation="";
		String xCoordinate="";
		String yCoordinate="";

		if(dom.getElementsByTagName("Artifacts")!=null){
			String lostinfo="Artifacts: ToldId;Height;Width;Border Color;Fill Color";
			lostInformation.add(lostinfo);
			
			NodeList n= dom.getElementsByTagName("Artifact");
			for (int temp = 0; temp < n.getLength(); temp++)
			{
				Node node = n.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{	
					Element eElement = (Element) node;
					id= eElement.getAttributeNode("Id").getTextContent();
					artifactType=eElement.getAttributeNode("ArtifactType").getTextContent();
					textAnnotation=eElement.getAttributeNode("TextAnnotation").getTextContent();

					NodeList coord=eElement.getElementsByTagName("Coordinates");
					for (int c = 0; c < coord.getLength(); c++)
					{
						Node no = coord.item(c);
						Element e = (Element) no;
						xCoordinate=e.getAttributeNode("XCoordinate").getTextContent();
						yCoordinate=e.getAttributeNode("YCoordinate").getTextContent();	
					}		
				}
			}

			if(artifactType.equals("Annotation")){
				if(!blocos.containsKey(id)){	
					BlocoXML b= new BlocoXML(id,artifactType,countBlocos );
					b.setClasse("BlockComment");
					b.setArtifactTextAnotation(textAnnotation);
					b.setXCoordinate(xCoordinate);
					b.setYCoordinate(yCoordinate);
					blocos.put(id, b);
					countBlocos++;		
				}
			}
		}
	}

	/**
	 * Lê e trata informação do RedefinableHeader
	 */
	private static void readRedefinableHeader(Document dom) {

		NodeList n= dom.getElementsByTagName("RedefinableHeader");
		for (int temp = 0; temp < n.getLength(); temp++)
		{
			Node node = n.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element eElement = (Element) node;
				packageAuthor= eElement.getElementsByTagName("Author").item(0).getTextContent();
				String lostinfo="RedefinableHeader: Version;Country Key";
				lostInformation.add(lostinfo);
				break;
			}
		}	
	}

	/**
	 * Lê e trata informação do PackageHeader
	 */
	private static void readPackageHeader(Document dom) {

		NodeList n= dom.getElementsByTagName("PackageHeader");
		for (int temp = 0; temp < n.getLength(); temp++)
		{
			Node node = n.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element eElement = (Element) node;
				packageDescription= eElement.getElementsByTagName("Description").item(0).getTextContent();
			}
			String lostinfo="PackageHeader: XPDLVersion; VendorInformation; Created Information; Documentation";
			lostInformation.add(lostinfo);
		}
	}

}
