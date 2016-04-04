package pt.iknow.floweditor;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import types.Activities;
import types.Activity;
import types.ActivityChoice;
import types.ActivitySet;
import types.ActivitySets;
import types.Artifact;
import types.ArtifactArtifactTypeType;
import types.Artifacts;
import types.ArtifactsItem;
import types.Association;
import types.Associations;
import types.AssociationsItem;
import types.Author;
import types.BlockActivity;
import types.Condition;
import types.ConnectorGraphicsInfo;
import types.ConnectorGraphicsInfos;
import types.Coordinates;
import types.Countrykey;
import types.Created;
import types.DataInputOutputs;
import types.Description;
import types.Documentation;
import types.EndEvent;
import types.EndEventResultType;
import types.Event;
import types.ExtendedAttribute;
import types.ExtendedAttributes;
import types.ExternalPackages;
import types.Implementation;
import types.IntermediateEvent;
import types.IntermediateEventTriggerType;
import types.Lanes;
import types.Loop;
import types.LoopLoopTypeType;
import types.NodeGraphicsInfo;
import types.NodeGraphicsInfos;
import types.PackageHeader;
import types.Performer;
import types.Performers;
import types.Pool;
import types.Pools;
import types.ProcessHeader;
import types.RedefinableHeader;
import types.Route;
import types.RouteExclusiveTypeType;
import types.RouteGatewayTypeType;
import types.Script;
import types.StartEvent;
import types.StartEventTriggerType;
import types.Task;
import types.TaskScript;
import types.TaskSend;
import types.TaskService;
import types.TaskUser;
import types.TaskUserImplementationType;
import types.Transition;
import types.Transitions;
import types.Vendor;
import types.WorkflowProcess;
import types.WorkflowProcesses;
import types.XPDLVersion;

/**
 * 
 * @author Ana
 * Le diagrama iFlow e converte para XDPL BPMN
 */
public abstract class ExportXPDL {

	private static String XPDLVERSION="2.1"; /** Versão do XPDL utilizado*/
	private static String COUNTRYKEY="CO"; /**Chave do País*/
	private static String XMLVERSION="1.0";/**Versao do XML*/
	private static List<InstanciaComponente> aSets= new ArrayList<InstanciaComponente>();/**Array de activity sets**/
	private static Random n= new Random();
	protected static List<String> lostInformation=new ArrayList<String>();/**Guarda informação perdida **/
	private static boolean alreadyWrite=false;
	private static List<AssociationC> asso= new ArrayList<AssociationC>();/**Guarda informação das associações**/

	/**
	 * Carrega as informações dos blocos e escreve o ficheiro XPDL
	 */
	public static void exportXPDL(Desenho desenho, OutputStream bos) throws Exception {

		if(bos==null)throw new IllegalArgumentException("Output cannot be null!");
		try{

			types.Package pa= new types.Package();
			Writer w= new OutputStreamWriter(bos);
			pa.setId(String.valueOf(n.nextInt()));
			pa.setName(desenho.getName());
			
			PackageHeader ph= loadPackageHeader(desenho);
			RedefinableHeader rh= loadRedefinableHeader(desenho);
			ExternalPackages ep= new ExternalPackages();
			Pools pool= loadPools();
			Artifacts art= loadArtifacts(desenho);
			WorkflowProcesses wp= loadWorkflowProcess(desenho);
			Associations a= loadAssociations(desenho);
			ExtendedAttributes ea=new ExtendedAttributes();

			pa.setPackageHeader(ph);
			pa.setRedefinableHeader(rh);
			pa.setExternalPackages(ep);
			pa.setPools(pool);
			pa.setAssociations(a);
			pa.setArtifacts(art);
			pa.setWorkflowProcesses(wp);
			pa.setExtendedAttributes(ea);
			pa.marshal(w);
			w.close();

		}catch(Exception e){
			FlowEditor.log("error",e);
			throw new Exception(Mesg.ErroGravarFicheiro);
		}
	}

	/**
	 * Escreve informação relativamente as Associações do fluxo
	 */
	private static Associations loadAssociations(Desenho desenho) {

		BizagiInformationComponentes bic= new BizagiInformationComponentes();
		Associations as= new Associations();
		int count=100;
		if(asso.size()!=0){
			String lost="Associations/Artifacts: Block comment lose out connection";
			lostInformation.add(lost);
		}
		for(int k=0; k<asso.size();k++){
			Association a= new Association();
			AssociationsItem ai= new AssociationsItem();
			ConnectorGraphicsInfos cgis=new ConnectorGraphicsInfos();
			ConnectorGraphicsInfo cgi= new ConnectorGraphicsInfo();
			Coordinates c= new Coordinates();
			Coordinates c2=new Coordinates();
			a.setId(String.valueOf(count));
			count--;
			AssociationC s=asso.get(k);
			a.setSource(s.getSource());
			a.setTarget(s.getTarget());

			for(int i=0; i<desenho.getComponentList().size(); i++){
				InstanciaComponente id=desenho.getComponentList().get(i);
				if(id.ID==Integer.parseInt(s.getTarget())){
					int x1= id.Posicao_X;
					int y1=id.Posicao_Y;
					c.setXCoordinate(x1);
					c.setYCoordinate(y1);
				}
				else if(id.ID==Integer.parseInt(s.getSource())){
					int x1= id.Posicao_X;
					int y1=id.Posicao_Y;
					c2.setXCoordinate(x1);
					c2.setYCoordinate(y1);
				}
			}

			cgi.setToolId(bic.TOOLDID);
			cgi.setBorderColor(bic.TRANS_BORDERCOLOR);
			cgi.addCoordinates(c);
			cgi.addCoordinates(c2);
			cgis.addConnectorGraphicsInfo(cgi);
			a.setConnectorGraphicsInfos(cgis);
			ai.setAssociation(a);
			as.addAssociationsItem(ai);	
		}
		return as;
	}

	/**
	 * Escreve informação dos Artefactos do fluxo
	 */
	private static Artifacts loadArtifacts(Desenho desenho) {

		Artifacts arts= new Artifacts();
		BizagiInformationComponentes bic= new BizagiInformationComponentes();
		List<InstanciaComponente> comp = desenho.getComponentList();
		for(int i=0; i<comp.size();i++){
			InstanciaComponente ic=comp.get(i);
			if(ic.C_B.Nome.equals("BlockComment")||ic.C_B.Nome.equals("BlockToDo")){
				Artifact art= new Artifact();
				ArtifactsItem at= new ArtifactsItem();
				art.setId(String.valueOf(ic.ID));
				art.setArtifactType(ArtifactArtifactTypeType.ANNOTATION);
				art.setTextAnnotation("");

				NodeGraphicsInfos ngis= new NodeGraphicsInfos();
				NodeGraphicsInfo ngi= new NodeGraphicsInfo();
				Coordinates c= new Coordinates();
				c.setXCoordinate(ic.Posicao_X);
				c.setYCoordinate(ic.Posicao_Y);
				ngi.setCoordinates(c);
				ngi.setToolId(bic.TOOLDID);
				ngi.setHeight(bic.ANNOT_HEIGHT);
				ngi.setWidth(bic.ANNOT_WIDTH);
				ngi.setBorderColor(bic.ANNOT_BORDERCOLOR);
				ngi.setFillColor(bic.ANNOT_FILLCOLOR);
				ngis.addNodeGraphicsInfo(ngi);
				art.setNodeGraphicsInfos(ngis);
				at.setArtifact(art);
				arts.addArtifactsItem(at);
			}
		}
		return arts;
	}

	/**
	 * Devolve o PackageHeader do ficheiro;
	 */	
	private static PackageHeader loadPackageHeader(Desenho desenho) {

		/**Cria as tags **/
		PackageHeader ph= new PackageHeader();
		XPDLVersion version= new XPDLVersion();
		Description des= new Description();
		Vendor v= new Vendor();
		Created c= new Created();
		Documentation d= new Documentation();
		Date data= new Date();

		/** Carrega informação dos elementos**/
		d.setContent(""); //O iflow não contem informações sobre a documentação, logo a tag aparece vazia;
		@SuppressWarnings("deprecation")
		String datas= data.toGMTString();
		c.setContent(datas);
		v.setContent("iFlow");
		version.setContent(XPDLVERSION);
		des.setContent(desenho.getName());

		/**Actualiza o Package Header**/
		ph.setXPDLVersion(version);
		ph.setVendor(v);
		ph.setCreated(c);
		ph.setDescription(des);
		ph.setDocumentation(d);

		return ph;
	}

	/**
	 *Devolve o RedefinableHeader do ficheiro e do workflowProcess
	 */
	private static RedefinableHeader loadRedefinableHeader(Desenho desenho) {

		/**Cria Tags**/
		RedefinableHeader rh= new RedefinableHeader();
		Author author= new Author();
		types.Version v= new types.Version();
		Countrykey ck= new Countrykey();

		/**Carrega Informação de cada tag**/
		ck.setContent(COUNTRYKEY);
		author.setContent("");//Opcional
		v.setContent(XMLVERSION);

		/**Actualiza o RedefinableHeader**/
		rh.setAuthor(author);
		rh.setCountrykey(ck);
		rh.setVersion(v);
		return rh;
	}

	/**
	 * As pools nao existem no iFlow, no entanto, na exportação para XPDL é criada uma pool
	 * por defeito, para o fluxo principal 
	 * Devolve a Pool do processo principal.
	 */
	private static Pools loadPools() {

		Pools pool= new Pools();
		Pool p= new Pool();
		NodeGraphicsInfos ngis=new NodeGraphicsInfos();
		NodeGraphicsInfo ngi= new NodeGraphicsInfo();
		Coordinates c= new Coordinates();

		p.setId(String.valueOf(n.nextInt()));
		p.setName("Processo Principal");
		p.setProcess("1");
		p.setBoundaryVisible(false);
		Lanes l=new Lanes();
		p.setLanes(l);
		ngi.setToolId("");
		ngi.setHeight(0.0); 
		ngi.setWidth(0.0);
		ngi.setBorderColor("");
		ngi.setFillColor("");
		c.setXCoordinate(0.0);
		c.setYCoordinate(0.0);
		ngi.setCoordinates(c);
		ngis.addNodeGraphicsInfo(ngi);
		p.setNodeGraphicsInfos(ngis);
		pool.addPool(p);
		return pool;	
		/**o Height, Width, BorderColor, etc, são colocadas a 0.0 ou "" uma vez que nao exite informações.
		No Programa onde ira ser aberto o ficheiro, estas informações sao actualizadas :) **/
	}

	/**
	 * Devolve as informações do WorkflowProcess do processo.
	 */
	private static WorkflowProcesses loadWorkflowProcess(Desenho desenho) {

		WorkflowProcesses wps= new WorkflowProcesses();
		WorkflowProcess wp= new WorkflowProcess();
		wp.setId("1");//Assumindo que so existe 1 WP, por defeito o ID é 1
		wp.setName("");
		ProcessHeader ph=loadProcessHeader(desenho);
		RedefinableHeader rh= loadRedefinableHeader(desenho);
		DataInputOutputs dios= new DataInputOutputs();	
		Activities a= loadActivities(desenho);
		ActivitySets as= loadActivitySets();
		Transitions t= loadTransitions(desenho);

		wp.setProcessHeader(ph);
		wp.setRedefinableHeader(rh);
		wp.setActivitySets(as);
		wp.setDataInputOutputs(dios);
		wp.setActivities(a);
		wp.setTransitions(t);
		wps.addWorkflowProcess(wp);
		return wps;
	}

	/**
	 * Devolve o processHeader do workflowProcess
	 */
	private static ProcessHeader loadProcessHeader(Desenho desenho) {
		ProcessHeader p=new ProcessHeader();
		Description des= new Description();
		Created c= new Created();
		Date data= new Date();

		@SuppressWarnings("deprecation")
		String datas= data.toGMTString();
		des.setContent("");
		c.setContent(datas);
		p.setCreated(c);
		p.setDescription(des);
		return p;
	}

	/**
	 * Devolve as informações das ActivitySets do processo.(subfluxo)
	 */
	private static ActivitySets loadActivitySets() {
		
		ActivitySets as= new ActivitySets();
		if(aSets.size()!=0){
			for(InstanciaComponente comp: aSets){
				ActivitySet a= new ActivitySet();
				Artifacts art=new Artifacts();
				Associations asso=new Associations();
				Activities act = new Activities();
				Transitions t= new Transitions();
				a.setId(String.valueOf(comp.ID));
				a.setName(comp.C_B.Nome);
				a.setArtifacts(art);
				a.setAssociations(asso);
				a.setActivities(act);
				a.setTransitions(t);
				as.addActivitySet(a);
			}	
		}
		return as;
	}

	/**
	 * Devolve as informações das actividades do processo.
	 */
	private static Activities loadActivities(Desenho desenho) {

		String lost="Activity: Blocks lose error outputs information";
		lostInformation.add(lost);
		
		Activities as= new Activities();
		List<InstanciaComponente> comp = desenho.getComponentList();
		for(int i=0; i<comp.size();i++){		
			InstanciaComponente ic=comp.get(i);
			String lib=ic.C_B.Nome;
			if(!lib.equals("BlockComment")){
				Activity a=loadActivity(ic,comp);
				as.addActivity(a);
			}	
		}
		return as;
	}

	/**
	 * Devolve as informações de UMA actividade.
	 */
	private static Activity loadActivity(InstanciaComponente ic,List<InstanciaComponente> comp) {

		String bib=ic.C_B.Nome;
		Activity a= new Activity();
		String lost="";
		BizagiInformationComponentes bic= new BizagiInformationComponentes();

		Description des= new Description();
		Documentation d= new Documentation();
		NodeGraphicsInfos ngi= new NodeGraphicsInfos();
		NodeGraphicsInfo node= new NodeGraphicsInfo();
		Coordinates coord= new Coordinates();
		ExtendedAttributes ea= new ExtendedAttributes();
		des.setContent("");
		d.setContent("");
		coord.setXCoordinate(ic.Posicao_X);
		coord.setYCoordinate(ic.Posicao_Y);

		String name=ic.Nome;
		a.setId(String.valueOf(ic.ID));
		a.setName(name);
		a.setDescription(des);


		if(bib.equals("BlockStart")||bib.equals("BlockEnd")||bib.equals("BlockForwardUp")||
				bib.equals("BlockEmail")||bib.equals("BlockEmailInterveniente")||bib.equals("BlockEmailPerfil")||bib.equals("BlockSMS")||
				bib.equals("BlockEmailPerfil")||bib.equals("BlockNotification")||bib.equals("BlockNOP")||bib.equals("BlockEvento")||bib.equals("BlockValidacoes")){

			Event e= new Event();
			if(bib.equals("BlockStart")){
				StartEvent es= new StartEvent();
				es.setTrigger(StartEventTriggerType.NONE);
				e.setStartEvent(es);/*No Iflow o evento apenas começa, logo o trigger vai ser sempre "none" */	
				node.setToolId(bic.TOOLDID);
				node.setHeight(bic.START_HEIGHT);
				node.setWidth(bic.START_WIDTH);
				node.setBorderColor(bic.START_BORDERCOLOR);
				node.setFillColor(bic.START_FILLCOLOR);
				node.setCoordinates(coord);
				ngi.addNodeGraphicsInfo(node);
			}
			else if(bib.equals("BlockEnd")){
				EndEvent ee= new EndEvent();
				ee.setResult(EndEventResultType.NONE);
				e.setEndEvent(ee); /*No Iflow temos de testar para o caso de ser com ou sem mensagem*/
				node.setToolId(bic.TOOLDID);
				node.setHeight(bic.END_HEIGHT);
				node.setWidth(bic.END_WIDTH);
				node.setBorderColor(bic.END_BORDERCOLOR);
				node.setFillColor(bic.END_FILLCOLOR);
				node.setCoordinates(coord);
				ngi.addNodeGraphicsInfo(node);	
			}
			else if(bib.equals("BlockForwardUp")||bib.equals("BlockEmail")||bib.equals("BlockEmailInterveniente")||bib.equals("BlockEmailPerfil")||bib.equals("BlockSMS")||
					bib.equals("BlockEmailPerfil")||bib.equals("BlockNotification")||bib.equals("BlockNOP")||bib.equals("BlockEvento")||bib.equals("BlockValidacoes")){

				IntermediateEvent ie=new IntermediateEvent();

				if(bib.equals("BlockForwardUp")){
					ie.setTrigger(IntermediateEventTriggerType.ESCALATION);
					lost="Block"+name+":Escalation User Destination";
					lostInformation.add(lost);
				}
				else if(bib.equals("BlockEmail")||bib.equals("BlockEmailInterveniente")||bib.equals("BlockEmailPerfil")||bib.equals("BlockSMS")||
						bib.equals("BlockEmailPerfil")||bib.equals("BlockNotification")){
					ie.setTrigger(IntermediateEventTriggerType.MESSAGE);
					lost="Block"+name+":Notification/Email message";
					lostInformation.add(lost);
				}
				else if(bib.equals("BlockNOP")||bib.equals("BlockEvento")){
					ie.setTrigger(IntermediateEventTriggerType.TIMER);
					lost="Block"+name+":Nop/Event characteristics";
					lostInformation.add(lost);
				}
				else if(bib.equals("BlockValidacoes")){
					ie.setTrigger(IntermediateEventTriggerType.CONDITIONAL);
					lost="Block"+name+":Validations conditions";
					lostInformation.add(lost);
				}

				e.setIntermediateEvent(ie);
				node.setToolId(bic.TOOLDID);
				node.setHeight(bic.INT_HEIGHT);
				node.setWidth(bic.INT_WIDTH);
				node.setBorderColor(bic.INT_BORDERCOLOR);
				node.setFillColor(bic.INT_FILLCOLOR);
				node.setCoordinates(coord);
				ngi.addNodeGraphicsInfo(node);
			}	
			ActivityChoice ac= new ActivityChoice();
			ac.setEvent(e);
			a.setActivityChoice(ac);
		}
		else if(bib.equalsIgnoreCase("BlockCondicao")||bib.equalsIgnoreCase("BlockSincronizacao")||
				bib.equalsIgnoreCase("BlockBifurcacao")||bib.equalsIgnoreCase("BlockJuncaoExclusiva")||
				bib.equalsIgnoreCase("BlockMultiCondition")){

			Route r= new Route();
			if(bib.equalsIgnoreCase("BlockSincronizacao")){
				r.setInstantiate(true);
				r.setGatewayType(RouteGatewayTypeType.PARALLEL);
			}
			else{
				r.setInstantiate(true);
				r.setExclusiveType(RouteExclusiveTypeType.EVENT);
			}
			ActivityChoice ac= new ActivityChoice();
			ac.setRoute(r);
			a.setActivityChoice(ac);

			node.setToolId(bic.TOOLDID);
			node.setHeight(bic.GATEWAY_HEIGHT);
			node.setWidth(bic.GATEWAY_WIDTH);
			node.setBorderColor(bic.GATEWAY_BORDERCOLOR);
			node.setFillColor(bic.GATEWAY_FILLCOLOR);
			node.setCoordinates(coord);
			ngi.addNodeGraphicsInfo(node);	

			if(!alreadyWrite){
				lost="Block"+name+":Block Bifurcacao; Block JuncaoExclusiva; Block MultiCondition";
				lostInformation.add(lost);
				alreadyWrite=true;
			}

		}
		else if(bib.equals("BlockFormulario")||bib.equalsIgnoreCase("BlockBeanShell")|| bib.equals("BlockPesquisaProcesso")||bib.equals("BlockProcDetail")
				|| bib.equals("BlockCriarDocumento")||bib.equals("BlockDocumentGet")||bib.equals("BlockDocumentDelete")
				||bib.equals("BlockDocumentList")||bib.equals("BlockSaveToDB")||bib.equals("BlockSQLBatchInsert")||bib.equals("BlockSQLBatchUpdate")
				||bib.equals("BlockSQLDelete")||bib.equals("BlockSQLInsert")||bib.equals("BlockSQLSelect")||bib.equals("BlockSQLUpdate")
				||bib.equals("BlockDataImport")||bib.equals("BlockSeries")||bib.equals("BlockDocumentFolders")||bib.equals("BlockReport")
				||bib.equals("BlockOpenProc")||bib.equals("BlockSwitchProc")||bib.equals("BlockSwitchProcAsUser")
				||bib.equals("BlockAddToArray")||bib.equals("BlockRemoveFromArray")||bib.equals("BlockCleanProcError")||
				bib.equalsIgnoreCase("BlockGetUserInfo")|| bib.equals("BlockGetOrganicalUnitInfo")
				||bib.equals("BlockGetOrganicalUnitParent")|| bib.equals("BlockGetUserProfiles")
				||bib.equals("BlockGetUserInProfile")||bib.equals("BlockGetUserUp")||bib.equals("BlockWebServiceSinc")
				||bib.equals("BlockDelegationSubstitute")||bib.equals("BlockDelegationInfo")||bib.equals("BlockDelegationOwner")||
				bib.equals("BlockIsInProfiles")||bib.equals("BlockIsInProfilesText")||bib.equals("BlockCheckAuthentication")||bib.equals("BlockEmail")
				||bib.equals("BlockDateOperation")||bib.equals("BlockCopia")||bib.equals("BlockReport")||bib.equals("BlockForwardTo")){

			Implementation i= new Implementation();
			Task t= new Task();

			if(bib.equals("BlockFormulario")){
				TaskUser ts= new TaskUser();
				ts.setImplementation(TaskUserImplementationType.UNSPECIFIED);
				t.setTaskUser(ts);
				lost="Block"+name+":Formulário characteristics";
				lostInformation.add(lost);
			}
			else if(bib.equalsIgnoreCase("BlockBeanShell")|| bib.equals("BlockPesquisaProcesso")||bib.equals("BlockProcDetail")
					||bib.equals("BlockCriarDocumento")||bib.equals("BlockDocumentGet")||bib.equals("BlockDocumentDelete")
					||bib.equals("BlockDocumentList")||bib.equals("BlockSaveToDB")||bib.equals("BlockSQLBatchInsert")||bib.equals("BlockSQLBatchUpdate")
					||bib.equals("BlockSQLDelete")||bib.equals("BlockSQLInsert")||bib.equals("BlockSQLSelect")||bib.equals("BlockSQLUpdate")
					||bib.equals("BlockDataImport")||bib.equals("BlockSeries")||bib.equals("BlockDocumentFolders")||bib.equals("BlockReport")
					||bib.equals("BlockForwardTo")||bib.equals("BlockOpenProc")||bib.equals("BlockSwitchProc")||bib.equals("BlockSwitchProcAsUser")
					||bib.equals("BlockAddToArray")||bib.equals("BlockRemoveFromArray")||bib.equals("BlockCleanProcError")||bib.equals("BlockDateOperation")
					||bib.equals("BlockCopia")||bib.equals("BlockReport")){
				TaskScript ts= new TaskScript();
				Script script= new Script();
				script.setContent(null);
				ts.setScript(script);
				t.setTaskScript(ts);	
				lost="Block"+name+":BeanShell Script";
				lostInformation.add(lost);
			}
			else if(bib.equalsIgnoreCase("BlockGetUserInfo")|| bib.equals("BlockGetOrganicalUnitInfo")
					||bib.equals("BlockGetOrganicalUnitParent")||bib.equals("BlockGetUserProfiles")
					||bib.equals("BlockGetUserInProfile")||bib.equals("BlockGetUserUp")||bib.equals("BlockWebServiceSinc")
					||bib.equals("BlockDelegationSubstitute")||bib.equals("BlockDelegationInfo")||bib.equals("BlockDelegationOwner")||
					bib.equals("BlockIsInProfiles")||bib.equals("BlockIsInProfilesText")||bib.equals("BlockCheckAuthentication")){
				TaskService ts=new TaskService();
				t.setTaskService(ts);
			}
			else if(bib.equals("BlockEmail")){
				TaskSend ts=new TaskSend();
				t.setTaskSend(ts);
			}

			i.setTask(t);
			ActivityChoice ac= new ActivityChoice();
			ac.setImplementation(i);
			a.setActivityChoice(ac);
			Performers p= new Performers();
			Performer pp= new Performer();
			pp.setContent("");
			p.addPerformer(pp);

			node.setToolId(bic.TOOLDID);
			node.setHeight(bic.TASK_HEIGHT);
			node.setWidth(bic.TASK_WIDTH);
			node.setBorderColor(bic.TASK_BORDERCOLOR);
			node.setFillColor(bic.TASK_FILLCOLOR);
			node.setCoordinates(coord);
			ngi.addNodeGraphicsInfo(node);	
		}
		else if(bib.equals("BlockSubFlow")){

			BlockActivity ba= new BlockActivity();
			ba.setActivitySetId(String.valueOf(ic.ID));
			aSets.add(ic);	
			Loop l= new Loop();
			l.setLoopType(LoopLoopTypeType.STANDARD);
			Performers p= new Performers();
			Performer pp= new Performer();
			pp.setContent("");
			p.addPerformer(pp);

			node.setToolId(bic.TOOLDID);
			node.setHeight(bic.TASK_HEIGHT);
			node.setWidth(bic.TASK_WIDTH);
			node.setBorderColor(bic.TASK_BORDERCOLOR);
			node.setFillColor(bic.TASK_FILLCOLOR);
			node.setCoordinates(coord);
			ngi.addNodeGraphicsInfo(node);
			lost="Block"+name+":SubFlow Information";
			lostInformation.add(lost);
		}
		a.setDocumentation(d);
		a.setNodeGraphicsInfos(ngi);
		if(ic.getAtributos().size()>0){
			for(int at=0; at< ic.getAtributos().size();at++){
				ExtendedAttribute atr= new ExtendedAttribute();
				String nome=ic.getAtributos().get(at).getNome();
				if(!name.equals("")){
					atr.setName(nome);
					atr.setValue(ic.getAtributos().get(at).getValor());
					ea.addExtendedAttribute(atr);
				}
			}
		}
		a.setExtendedAttributes(ea);
		return a;
	}

	/**
	 * Devolve as informações das Transições do processo.
	 */
	private static Transitions loadTransitions(Desenho desenho) {

		Transitions ts= new Transitions();
		List<InstanciaComponente> comp = desenho.getComponentList();
		int idT=0;
		String lost="Error outputs are not specified";
		lostInformation.add(lost);
		for(int i=0; i<comp.size();i++){
			InstanciaComponente ic=comp.get(i);
			if(!ic.C_B.Nome.equals("BlockComment")){
				Transition t[]=loadTransition(ic,idT,desenho);
				for(int k=0; k<t.length; k++){
					if(t[k]!=null){
						idT++;
						ts.addTransition(t[k]);
					}
				}
			}
		}
		return ts;
	}

	/**
	 * Devolve as informações de UMA Transição
	 */
	public static Transition[] loadTransition(InstanciaComponente ic, int idT, Desenho desenho){

		BizagiInformationComponentes bic= new BizagiInformationComponentes();
		Condition cond= new Condition();
		cond.setContent("");
		Description d= new Description();
		d.setContent("");
		ConnectorGraphicsInfos cgis= new ConnectorGraphicsInfos();
		ConnectorGraphicsInfo cgi= new ConnectorGraphicsInfo();
		cgi.setToolId(bic.TOOLDID);
		cgi.setBorderColor(bic.TRANS_BORDERCOLOR);
		cgis.addConnectorGraphicsInfo(cgi);

		Coordinates cordE= new Coordinates();
		cordE.setXCoordinate(ic.Posicao_X);
		cordE.setYCoordinate(ic.Posicao_Y);
		cgi.addCoordinates(cordE);
		ExtendedAttributes ea= new ExtendedAttributes();
		Transition transition = null;
		Transition t[]=new Transition[1000];

		int count=0;
		boolean read;
		for(int i=0; i<ic.lista_estado_saidas.size();i++){
			read=false;
			List<Conector> lista=ic.lista_estado_saidas.get(i);
			
			for(int c=0; c<lista.size(); c++){	
				transition= new Transition();		
				Conector cone=Linha.daOut(lista.get(c));
				int IdTo=((InstanciaComponente) cone.Comp).ID;
				
				
				if(IdTo!=-1){
					if(((InstanciaComponente) cone.Comp).C_B.Nome.equals("BlockComment")&&!read){
						AssociationC a= new AssociationC(String.valueOf(IdTo),String.valueOf(ic.ID));
						asso.add(a);
						read=true;
					}
					else{
						idT++;
						transition.setId(String.valueOf(idT)); //ID da Transição
						transition.setFrom(String.valueOf(ic.ID)); //ID do bloco origem;
						transition.setTo(String.valueOf(IdTo));//ID do bloco chegada;
						transition.setCondition(cond);
						transition.setDescription(d);
						Coordinates cordS= new Coordinates();
						cordS.setXCoordinate(((InstanciaComponente) cone.Comp).Posicao_X);
						cordS.setYCoordinate(((InstanciaComponente) cone.Comp).Posicao_Y);
						cgi.addCoordinates(cordS);

						//Coordenada da Entreda dos componente sucessor (to);	´
						transition.setConnectorGraphicsInfos(cgis);
						transition.setExtendedAttributes(ea);
						t[count++]=transition;	
					}
				}
			}
		}
		return t;
	}

	/**
	 * como cada imagem tem as suas caracteristcas no bizage, criar uma biblioteca com essa informação
	 * */
	private static class BizagiInformationComponentes{


		private String TOOLDID = "BizAgi_Process_Modeler";
		/**BlocoStart**/
		private int START_HEIGHT=30;
		private int START_WIDTH=30;
		private String START_BORDERCOLOR="-10311914";
		private String START_FILLCOLOR="-1638505";
		/**Bloco End**/
		private int END_HEIGHT=30;
		private int END_WIDTH=30;
		private String END_BORDERCOLOR="-6750208";
		private String END_FILLCOLOR="-1135958";
		/**Bloco Evento Intermedio*/
		public String INT_FILLCOLOR = "-66833";
		public String INT_BORDERCOLOR ="-6909623";
		public int INT_WIDTH = 30;
		public int INT_HEIGHT =30;
		/**Bloco Task**/
		private int TASK_HEIGHT=60;
		private int TASK_WIDTH=90;
		private String TASK_BORDERCOLOR="-16553830";
		private String TASK_FILLCOLOR="-1249281";
		/**Bloco Gateway*/
		private	String GATEWAY_BORDERCOLOR = "-5855715";
		private String GATEWAY_FILLCOLOR="-52";
		private int GATEWAY_WIDTH = 40;
		private int GATEWAY_HEIGHT = 40;
		/**Transitions**/
		private String TRANS_BORDERCOLOR ="-16777216";
		/**Annotation**/
		private int ANNOT_HEIGHT=60;
		private int ANNOT_WIDTH=100;
		private String ANNOT_BORDERCOLOR="-2763307";
		private String ANNOT_FILLCOLOR="-2763307";

	}
}