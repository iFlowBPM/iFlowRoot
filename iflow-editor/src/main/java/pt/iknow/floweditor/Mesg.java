package pt.iknow.floweditor;

import java.util.Locale;

import pt.iknow.floweditor.messages.Messages;
import pt.iknow.utils.StringUtilities;

/*******************************************************************************
 * 
 * Project FLOW EDITOR
 * 
 * class: Mesg
 * 
 * desc: contem as mensagems de editor
 * 
 ******************************************************************************/

public class Mesg {

  /* files */
  static final String FileExtension = "xml"; //$NON-NLS-1$
  static final String LibraryExtension = "xml"; //$NON-NLS-1$
  static final String s2rExtension = "s2r"; //$NON-NLS-1$

  static final String[] CompComCor = { "Background", //$NON-NLS-1$
    "Block", //$NON-NLS-1$
    "Active", //$NON-NLS-1$
    "Dot", //$NON-NLS-1$
    "Circle", //$NON-NLS-1$
    "Rectangle", //$NON-NLS-1$
    "Cross", //$NON-NLS-1$
    "RectangleInternal", //$NON-NLS-1$
    "BlockInternal" //$NON-NLS-1$
  };

  static final String[] ValidateClumnNames = { "Block", "Class", "Port", "Error Type" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

  // Esta classe esta construida desta forma para permitir recarregar as strings
  // apos actualizacao do default locale.



  /* program name */
  static String ProgramName;
  static String Version;

  /* Block names */
  static String Comp;
  static String In;
  static String Out;

  /* Menu -File */
  static String Save;
  static String Load;
  static String Sair;
  static String Novo;
  static String LerFicheiro;
  static String GravarFicheiro;
  static String GravarFicheiroComo;
  static String ImportarFicheiro;
  static String ExportarFicheiro;
  static String ViewProcessState;
  static String RefreshProcessState;
  static String ToggleProcessState;
  static String ImportarSubFlow;
  static String ExportarSubFlow;
  static String AdicionarBiblioteca;
  static String DownloadBiblioteca;
  static String FileClose;
  static String FileCloseAll;
  static String FileCloseOthers;
  static String SwitchWorkspace = "Switch Workspace";
  static String ConfirmarSaida;

  static String ImportarComposto;
  static String ExportarComposto;
  static String LerComposto;
  static String MenuFicheiro;

  /* Menu */
  static String MudaNome;
  static String MudaNomeFluxo;
  static String NomeMenuNome;
  static String MenuEditar;

  /* library */
  static String NomeLerBiblioteca;

  /* Route */
  static String RouteTreminado;
  static String MenuRoute;
  static String MenuRoute_1;
  static String DarNivel;
  static String RetirarPontos;
  static String RecalcularNiveis;
  static String Ordenar;
  static String InserirPontos;
  static String Concluido;

  /* Menu Opcoes */
  static String MenuOpcoes;
  static String VerLinhas;
  static String VerComponentes;
  static String DrawAll;
  static String SaveColors;
  static String MudaCores;
  static String AntiAlias;
  static String ShowBlockLibrary;
  static String OpenIFlow;

  /* Menu R2R */
  static String s2rInstall;
  static String s2rUninstall;

  /* Toolbar */
  static String ToolNovo;
  static String ToolAbre;
  static String ToolGrava;
  static String ToolImporta;
  static String ToolImprime;
  static String ToolRoute;

  /* Error */
  static String WARN;

  static String ErroImportarComposto;
  static String ErroLerFicheiro;
  static String ErroLerFicheiroIdsIguais;

  static String ErroDeleteStart;
  static String ErroGravarFicheiro;
  static String ErroDialogo;
  static String ErroBiblioteca;
  static String ErroTipoNaoExiste;
  static String ErroLerBiblioteca;

  /* OK CANCEL */
  public static String Cancelar;
  public static String OK;
  public static String Close;

  /* Menu Block */
  static String AlteraAtributos;
  static String MenuComponente;
  static String MenuLinha;
  static String MenuVazio;
  static String MenuNovoComponente;
  static String MenuNovaLinha;
  static String MenuSubCircuito;
  static String MenuUndo;
  static String MenuDuplicar;
  static String MenuDuplicarPara;
  static String MenuEliminar;
  static String MenuMudarNome;
  static String MenuInserePontoQuebra;
  static String MenuRetiraPontoQuebra;
  static String MenuAccaoComponente;
  static String MenuVerRegistos;
  static String EntradasSaidas;
  static String PortasSimples;
  static String CompCompostos;
  static String Vazio;
  static String MenuGotoStart;
  static String MenuGotoEnd;

  /* Print */
  static String MenuPageFormat;
  static String MenuPrint;

  /* About */
  static String About;
  static String Aboutproduct;
  static String Aboutversion;
  static String Aboutcopyright;
  static String Aboutcomments;

  /* RunGC */
  static String RunGC;
  static String RunGCLong;
  static String RunGCDone;
  static String RunGCBefore;
  static String RunGCAfter;
  static String RunGCUsage;
  static String RunGCFree;

  static String ChooseBlock;
  static String SearchBlock;
  static String SearchBlockWithVar;
  static String TemplateManager;
  static String TemplateManagerTooltip;

  /* janela library */
  static String NomeJanelaBib;

  /* Informacao */
  static String Informacao;

  /* Program agruments */
  static String ParametrosErrados;
  static String Par1;
  static String Par2;
  static String Par3;

  /* AlteraAtributos */
  static String[] AlteraAtributosColumnNames;
  static String AlteraAtributosTitle;

  /* SAving */
  static String Sim;
  static String Nao;
  /* para iSAIR */
  static String MenuSair;
  static String Pergunta;
  static String Repetir;
  static String Nome_Janela;
  /* gravar */
  static String MenuGravar;
  static String Pergunta2;
  static String Nome_Janela2;
  /* confirma e alterados */
  static String ConfirmaAlterados;
  static String Pergunta3;
  static String Nome_Janela3;
  /* confirma ficheiro existente */
  static String ConfirmaExistente;
  static String Pergunta4;
  static String Nome_Janela4;


  /* AlteraAtributosCalculosSimples */
  static String[] AlteraAtributosCalculosSimplesColumnNames;
  static String AlteraAtributosCalculosSimplesTitle;

  /* AlteraAtributosCalculosCondUniDim */
  static String[] AlteraAtributosCalculosCondUniDimColumnNames;
  static String AlteraAtributosCalculosCondUniDimTitle;

  /* AlteraAtributosCalculosCondBiDim */
  static String[] AlteraAtributosCalculosCondBiDimColumnNames;
  static String AlteraAtributosCalculosCondBiDimTitle;

  /* AlteraAtributosValidacoes */
  static String[] AlteraAtributosValidacoesColumnNames;
  static String AlteraAtributosValidacoesTitle;

  /* AlteraAtributosVariaveis */
  static String[] AlteraAtributosVariaveisColumnNames;
  static String AlteraAtributosVariaveisTitle;

  /* validate */
  static String Validate;
  static String ERROIn;
  static String ERROOut;
  static String WithoutError;
  static String WithError;
  static String ValidateTitle;

  /* choose cb */
  static String TitleChooseCB;
  static String TitleDownloadFlow;
  static String TitleUploadFlow;
  static String TitleDownloadSubFlow;
  static String TitleUploadSubFlow;
  static String TitleSaveFlowAs;
  static String TitleNewFlow;
  static String Search;
  static String TitleDownloadLibrary;

  static String Proxy;

  static String Install;
  static String Uninstall;
  static String Installer;
  static String Uninstaller;
  static String Application;

  static {
    reload();
  }

  static void setLocale(Locale locale) {
    if (locale == null) return;
    Locale.setDefault(locale);
    reload();
  }
  
  static void setLocale(String localeKey) {
    if(StringUtilities.isEmpty(localeKey))return;
    String []parts = localeKey.split("_");
    Locale loc = null;
    if(parts.length == 1) {
      loc = new Locale(parts[0]);
    } else if(parts.length == 2) {
      loc = new Locale(parts[0], parts[1]);
    } else if(parts.length == 3) {
      loc = new Locale(parts[0], parts[1], parts[2]);
    } else {
      return;
    }
    Locale.setDefault(loc);
    reload();
  }

  static void reload() {
    Messages.reload();
    /* program name */
    ProgramName = Messages.getString("APP_NAME"); //$NON-NLS-1$
    Version = Messages.getString("APP_VERSION"); //$NON-NLS-1$

    /* Block names */
    Comp = Messages.getString("BLOCK_NAME"); //$NON-NLS-1$
    In = Messages.getString("BLOCK_PORT_IN"); //$NON-NLS-1$
    Out = Messages.getString("BLOCK_PORT_OUT"); //$NON-NLS-1$

    /* Menu -File */
    Save = Messages.getString("Menu.SAVE_WORKFLOW"); //$NON-NLS-1$
    Load = Messages.getString("Menu.LOAD_WORKFLOW"); //$NON-NLS-1$
    Sair = Messages.getString("Menu.EXIT"); //$NON-NLS-1$
    Novo = Messages.getString("Menu.NEW_FLOW"); //$NON-NLS-1$
    LerFicheiro = Messages.getString("Menu.OPEN_FLOW"); //$NON-NLS-1$
    GravarFicheiro = Messages.getString("Menu.SAVE_FLOW"); //$NON-NLS-1$
    GravarFicheiroComo = Messages.getString("Menu.SAVE_FLOW_AS"); //$NON-NLS-1$
    ImportarFicheiro = Messages.getString("Menu.DOWNLOAD_FLOW"); //$NON-NLS-1$
    ExportarFicheiro = Messages.getString("Menu.UPLOAD_FLOW"); //$NON-NLS-1$
    ViewProcessState = Messages.getString("Menu.VIEW_PROCESS_STATE");
    RefreshProcessState = Messages.getString("Menu.REFRESH_PROCESS_STATE");
    ToggleProcessState = Messages.getString("Menu.TOGGLE_PROCESS_STATE");
    ImportarSubFlow = Messages.getString("Menu.DOWNLOAD_SUBFLOW"); //$NON-NLS-1$
    ExportarSubFlow = Messages.getString("Menu.UPLOAD_SUBFLOW"); //$NON-NLS-1$
    ConfirmarSaida = Messages.getString("Menu.CONFIRM_EXIT"); //$NON-NLS-1$
    AdicionarBiblioteca = Messages.getString("Menu.ADD_LIBRARY"); //$NON-NLS-1$
    DownloadBiblioteca = Messages.getString("Menu.DOWNLOAD_LIBRARY"); //$NON-NLS-1$
    FileClose = Messages.getString("Menu.CLOSE"); //$NON-NLS-1$
    FileCloseAll = Messages.getString("Menu.CLOSE_ALL"); //$NON-NLS-1$
    FileCloseOthers = Messages.getString("Menu.CLOSE_OTHERS"); //$NON-NLS-1$
    SwitchWorkspace = Messages.getString("Menu.SWITCH_WORKSPACE");

    ImportarComposto = Messages.getString("Menu.IMPORT_SUB_FLOW"); //$NON-NLS-1$
    ExportarComposto = Messages.getString("Menu.EXPORT_SUB_FLOW"); //$NON-NLS-1$
    LerComposto = Messages.getString("Menu.OPEN_SUB_FLOW"); //$NON-NLS-1$
    MenuFicheiro = Messages.getString("Menu.FILE"); //$NON-NLS-1$

    /* Menu */
    MudaNome = Messages.getString("Menu.CHANGE_NAME"); //$NON-NLS-1$
    NomeMenuNome = Messages.getString("Menu.CHANGE_NAME"); //$NON-NLS-1$
    MenuEditar = Messages.getString("Menu.EDIT"); //$NON-NLS-1$
    MudaNomeFluxo = Messages.getString("Menu.CHANGE_FLOW_NAME"); //$NON-NLS-1$

    /* library */
    NomeLerBiblioteca = Messages.getString("Generic.LIBRARY"); //$NON-NLS-1$

    /* Route */
    RouteTreminado = Messages.getString("Label.ROUTE_FINISH"); //$NON-NLS-1$
    MenuRoute = Messages.getString("Menu.ROUTING"); //$NON-NLS-1$
    MenuRoute_1 = Messages.getString("Menu.ROUTING_WITH_SPACE"); //$NON-NLS-1$
    DarNivel = Messages.getString("Generic.CALCULATING_BLOCK_LEVELS"); //$NON-NLS-1$
    RetirarPontos = Messages.getString("Generic.REMOVE_LINE_BREAKS"); //$NON-NLS-1$
    RecalcularNiveis = Messages.getString("Generic.RECALCULATING_LEVELS"); //$NON-NLS-1$
    Ordenar = Messages.getString("Generic.ORDERING_BLOCKS"); //$NON-NLS-1$
    InserirPontos = Messages.getString("Generic.INSERTING_LINE_BREAKS"); //$NON-NLS-1$
    Concluido = Messages.getString("Generic.FINISH"); //$NON-NLS-1$

    /* Menu Opcoes */
    MenuOpcoes = Messages.getString("Menu.OPTIONS"); //$NON-NLS-1$
    VerLinhas = Messages.getString("Menu.DRAW_LINES"); //$NON-NLS-1$
    VerComponentes = Messages.getString("Menu.DRAW_BLOCKS"); //$NON-NLS-1$
    DrawAll = Messages.getString("Menu.DRAW_ALL"); //$NON-NLS-1$
    SaveColors = Messages.getString("Menu.SAVE_COLORS"); //$NON-NLS-1$
    MudaCores = Messages.getString("Menu.CHANGE_COLORS"); //$NON-NLS-1$
    AntiAlias = Messages.getString("Menu.ANTI_ALIAS"); //$NON-NLS-1$
    ShowBlockLibrary = Messages.getString("Menu.SHOW_LIBRARY"); //$NON-NLS-1$
    OpenIFlow = Messages.getString("Menu.OPEN_IFLOW"); //$NON-NLS-1$

    /* R2R */
    s2rInstall = Messages.getString("Menu.S2R_INSTAL");
    s2rUninstall = Messages.getString("Menu.S2R_UNINSTAL");

    /* Toolbar */
    ToolNovo = Messages.getString("Toolbar.NEW"); //$NON-NLS-1$
    ToolAbre = Messages.getString("Toolbar.OPEN"); //$NON-NLS-1$
    ToolGrava = Messages.getString("Toolbar.SAVE"); //$NON-NLS-1$
    ToolImporta = Messages.getString("Toolbar.IMPORT"); //$NON-NLS-1$
    ToolImprime = Messages.getString("Toolbar.PRINT"); //$NON-NLS-1$
    ToolRoute = Messages.getString("Toolbar.ROUTE"); //$NON-NLS-1$

    /* Error */
    WARN = Messages.getString("Generic.WARN"); //$NON-NLS-1$

    ErroImportarComposto = Messages.getString("Error.IMPORT_SUBFLOW"); //$NON-NLS-1$
    ErroLerFicheiro = Messages.getString("Error.READ_FILE"); //$NON-NLS-1$
    ErroLerFicheiroIdsIguais = Messages.getString("Error.DUPLICATE_BLOCK_ID"); //$NON-NLS-1$

    ErroDeleteStart = Messages.getString("Error.DELETING_BLOCKSTART"); //$NON-NLS-1$
    ErroGravarFicheiro = Messages.getString("Error.SAVE_FILE"); //$NON-NLS-1$
    ErroDialogo = Messages.getString("Error.OPEN_DIALOG"); //$NON-NLS-1$
    ErroBiblioteca = Messages.getString("Error.READ_FLOW_LIBRARY"); //$NON-NLS-1$
    ErroTipoNaoExiste = Messages.getString("Error.MISSING_BLOCK_TYPE"); //$NON-NLS-1$
    ErroLerBiblioteca = Messages.getString("Error.READ_LIBRARY_FILE"); //$NON-NLS-1$

    /* OK CANCEL */
    Cancelar = Messages.getString("Button.CANCEL"); //$NON-NLS-1$
    OK = Messages.getString("Button.OK"); //$NON-NLS-1$
    Close = Messages.getString("Button.CLOSE"); //$NON-NLS-1$

    /* Menu Block */
    AlteraAtributos = Messages.getString("Menu.CHANGE_ATTRIBUTES"); //$NON-NLS-1$
    MenuComponente = Messages.getString("Menu.BLOCK_LIBRARY"); //$NON-NLS-1$
    MenuLinha = Messages.getString("Menu.LINE"); //$NON-NLS-1$
    MenuVazio = Messages.getString("Menu.NO_BLOCK"); //$NON-NLS-1$
    MenuNovoComponente = Messages.getString("Menu.NEW_BLOCK"); //$NON-NLS-1$
    MenuNovaLinha = Messages.getString("Menu.NEW_LINE"); //$NON-NLS-1$
    MenuSubCircuito = Messages.getString("Menu.OPEN_FLOW"); //$NON-NLS-1$
    MenuUndo = Messages.getString("Menu.UNDO"); //$NON-NLS-1$
    MenuDuplicar = Messages.getString("Menu.DUPLICATE"); //$NON-NLS-1$
    MenuDuplicarPara = Messages.getString("Menu.DUPLICATE_TO"); //$NON-NLS-1$
    MenuEliminar = Messages.getString("Menu.REMOVE"); //$NON-NLS-1$
    MenuMudarNome = Messages.getString("Menu.CHANGE_NAME"); //$NON-NLS-1$
    MenuInserePontoQuebra = Messages.getString("Menu.INSERT_LINE_BREAK"); //$NON-NLS-1$
    MenuRetiraPontoQuebra = Messages.getString("Menu.REMOVE_LINE_BREAK"); //$NON-NLS-1$
    MenuAccaoComponente = Messages.getString("Menu.ACTION_BLOCK"); //$NON-NLS-1$
    MenuVerRegistos = Messages.getString("Menu.VIEW_FLOW_STATE_LOGS");
    EntradasSaidas = Messages.getString("Menu.IO"); //$NON-NLS-1$
    PortasSimples = Messages.getString("Menu.SIMPLE_BLOCK"); //$NON-NLS-1$
    CompCompostos = Messages.getString("Menu.SUBFLOW_BLOCK"); //$NON-NLS-1$
    Vazio = "empty"; //$NON-NLS-1$
    MenuGotoStart = Messages.getString("Menu.LINE_GOTO_START"); //$NON-NLS-1$
    MenuGotoEnd = Messages.getString("Menu.LINE_GOTO_END"); //$NON-NLS-1$

    /* Print */
    MenuPageFormat = Messages.getString("Menu.PAGE_SETUP"); //$NON-NLS-1$
    MenuPrint = Messages.getString("Menu.PRINT"); //$NON-NLS-1$

    /* About */
    About = Messages.getString("Menu.ABOUT"); //$NON-NLS-1$
    Aboutproduct = Messages.getString("APP_NAME"); //$NON-NLS-1$
    Aboutversion = Messages.getString("About.PARAM1", pt.iknow.floweditor.Version.VERSION); //$NON-NLS-1$
    Aboutcopyright = Messages.getString("About.PARAM2"); //$NON-NLS-1$
    Aboutcomments = Messages.getString("About.PARAM3"); //$NON-NLS-1$

    /* RunGC */
    RunGC = Messages.getString("Menu.RUN_GC"); //$NON-NLS-1$
    RunGCLong = Messages.getString("Menu.RUN_GC_LONG"); //$NON-NLS-1$
    RunGCDone = Messages.getString("Menu.RUN_GC_DONE"); //$NON-NLS-1$
    RunGCBefore = Messages.getString("Menu.RUN_GC_BEFORE"); //$NON-NLS-1$
    RunGCAfter = Messages.getString("Menu.RUN_GC_AFTER"); //$NON-NLS-1$
    RunGCUsage = Messages.getString("Menu.RUN_GC_USAGE"); //$NON-NLS-1$
    RunGCFree = Messages.getString("Menu.RUN_GC_FREE"); //$NON-NLS-1$

    ChooseBlock = Messages.getString("Desenho.tooltip.choose.block"); //$NON-NLS-1$
    SearchBlock = Messages.getString("Menu.SEARCH_BLOCK"); //$NON-NLS-1$
    SearchBlockWithVar = Messages.getString("Menu.SEARCH_BLOCK_VAR"); //$NON-NLS-1$
    TemplateManager = Messages.getString("Menu.TemplateManager"); //$NON-NLS-1$
    TemplateManagerTooltip = Messages.getString("Desenho.tooltip.templateManager"); //$NON-NLS-1$

    /* janela library */
    NomeJanelaBib = Messages.getString("Window.LIBRARY"); //$NON-NLS-1$

    /* Informacao */
    Informacao = Messages.getString("Generic.INFORMATION"); //$NON-NLS-1$

    /* Program agruments */
    ParametrosErrados = Messages.getString("Info.WRONG_ARGUMENTS_NUMBER"); //$NON-NLS-1$
    Par1 = Messages.getString("Info.NO_ARGUMENTS"); //$NON-NLS-1$
    Par2 = "2. <Library File>"; //$NON-NLS-1$
    Par3 = "3. <Library File> <Flow File>"; //$NON-NLS-1$

    /* AlteraAtributos */
    AlteraAtributosColumnNames = new String[]{
        Messages.getString("Window.ChangeAtrribute.COLUMN1"), Messages.getString("Window.ChangeAtrribute.COLUMN2") }; //$NON-NLS-1$ //$NON-NLS-2$
    AlteraAtributosTitle = Messages.getString("Window.CHANGE_ATTRIBUTES_DIALOG"); //$NON-NLS-1$

    /* SAving */
    Sim = Messages.getString("Generic.YES"); //$NON-NLS-1$
    Nao = Messages.getString("Generic.NO"); //$NON-NLS-1$
    /* para iSAIR */
    MenuSair = "1"; //$NON-NLS-1$
    Pergunta = Messages.getString("Generic.CONFIRM_EXIT"); //$NON-NLS-1$
    Repetir = Messages.getString("Generic.REPEAT_EXIT");
    Nome_Janela = Messages.getString("Generic.EXIT"); //$NON-NLS-1$
    /* gravar */
    MenuGravar = "2"; //$NON-NLS-1$
    Pergunta2 = Messages.getString("Generic.CONFIRM_SAVE_FLOW"); //$NON-NLS-1$
    Nome_Janela2 = Messages.getString("Window.SAVE_FLOW_DIALOG"); //$NON-NLS-1$
    /* confirmar alterador */
    ConfirmaAlterados = "3"; //$NON-NLS-1$
    Pergunta3 = Messages.getString("Generic.CONFIRM_MODIFIED"); //$NON-NLS-1$
    Nome_Janela3 = Messages.getString("Window.EXIT_MODIFIED_DIALOG"); //$NON-NLS-1$
    /* confirmar existente */
    ConfirmaExistente = "4"; //$NON-NLS-1$
    Pergunta4 = Messages.getString("Generic.CONFIRM_EXISTING_FILE"); //$NON-NLS-1$
    Nome_Janela4 = Messages.getString("Window.EXISTING_FILE_DIALOG"); //$NON-NLS-1$

    /* AlteraAtributosCalculosSimples */
    AlteraAtributosCalculosSimplesColumnNames = new String[]{ Messages.getString("Window.ChangeAtrributeCals.COLUMN1") }; //$NON-NLS-1$
    AlteraAtributosCalculosSimplesTitle = Messages.getString("Window.CANGE_ATTRIBUTES_CALCS_DIALOG"); //$NON-NLS-1$

    /* AlteraAtributosCalculosCondUniDim */
    AlteraAtributosCalculosCondUniDimColumnNames = new String[]{ "Condições", "Valores" }; //$NON-NLS-1$ //$NON-NLS-2$
    AlteraAtributosCalculosCondUniDimTitle = "Change Attribute Values"; //$NON-NLS-1$

    /* AlteraAtributosCalculosCondBiDim */
    AlteraAtributosCalculosCondBiDimColumnNames = new String[]{ "Condições" }; //$NON-NLS-1$
    AlteraAtributosCalculosCondBiDimTitle = "Change Attribute Values"; //$NON-NLS-1$

    /* AlteraAtributosValidacoes */
    AlteraAtributosValidacoesColumnNames = new String[]{
        Messages.getString("Window.ChangeAttributeValid.COLUMN1"), Messages.getString("Window.ChangeAttributeValid.COLUMN2") }; //$NON-NLS-1$ //$NON-NLS-2$
    AlteraAtributosValidacoesTitle = Messages.getString("Window.CHANGE_ATTRIBUTE_VALID"); //$NON-NLS-1$

    /* AlteraAtributosVariaveis */
    AlteraAtributosVariaveisColumnNames = new String[]{
        Messages.getString("Window.ChangeAttributeVar.COLUMN1"), Messages.getString("Window.ChangeAttributeVar.COLUMN2") }; //$NON-NLS-1$ //$NON-NLS-2$
    AlteraAtributosVariaveisTitle = Messages.getString("Window.CHANGE_ATTRIBUTE_VAR"); //$NON-NLS-1$

    /* validate */
    Validate = Messages.getString("Generic.VALIDATE"); //$NON-NLS-1$
    ERROIn = Messages.getString("Error.INPUT_DISCONNECTED"); //$NON-NLS-1$
    ERROOut = Messages.getString("Error.OUTPUT_DISCONNECTED"); //$NON-NLS-1$
    WithoutError = Messages.getString("Info.ALL_IO_CONNECTED"); //$NON-NLS-1$
    WithError = Messages.getString("Error.SOME_IO_NOT_CONNECTED"); //$NON-NLS-1$
    ValidateTitle = Messages.getString("Window.VALIDATE"); //$NON-NLS-1$

    /* choose cb */
    TitleChooseCB = Messages.getString("Window.CHOOSE_CB"); //$NON-NLS-1$
    TitleDownloadFlow = Messages.getString("Window.ChooseCB.DOWNLOAD_FLOW"); //$NON-NLS-1$
    TitleUploadFlow = Messages.getString("Window.ChooseCB.UPLOAD_FLOW"); //$NON-NLS-1$
    TitleDownloadSubFlow = Messages.getString("Window.ChooseCB.DOWNLOAD_SUB_FLOW"); //$NON-NLS-1$
    TitleUploadSubFlow = Messages.getString("Window.ChooseCB.UPLOAD_SUB_FLOW"); //$NON-NLS-1$
    TitleSaveFlowAs = Messages.getString("Window.ChooseCB.SAVE_FLOW_AS"); //$NON-NLS-1$
    TitleNewFlow = Messages.getString("Window.ChooseCB.NEW_FLOW"); //$NON-NLS-1$
    Search = Messages.getString("Generic.SEARCH"); //$NON-NLS-1$
    TitleDownloadLibrary = Messages.getString("Window.ChooseCB.DOWNLOAD_LIBRARY"); //$NON-NLS-1$

    // S2R
    Install = Messages.getString("Generic.Install"); //$NON-NLS-1$
    Uninstall = Messages.getString("Generic.Uninstall"); //$NON-NLS-1$
    Installer = Messages.getString("Generic.Installer"); //$NON-NLS-1$
    Uninstaller = Messages.getString("Generic.Uninstaller"); //$NON-NLS-1$
    Application = Messages.getString("Generic.Application"); //$NON-NLS-1$

    Proxy = Messages.getString("ProxyWindow.title"); //$NON-NLS-1$
  }
}
