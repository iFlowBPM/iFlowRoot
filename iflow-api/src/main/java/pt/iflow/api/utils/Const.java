package pt.iflow.api.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow</p>
 * @author unascribed
 * @version 1.0
 */

public class Const {
  
  public static final String IFLOW_HOME = System.getProperty("iflow.home");
  
  public static final String ENCODING = "UTF-8";
  
  public static int nMODE = Const.nDEVELOPMENT;
  public static final int nDEVELOPMENT = 0;
  public static final int nTEST        = 1;
  public static final int nPRODUCTION  = 2;
  
  public static String sMAINTENANCE_USER = null;

  public static final int nEXPORT_MODE_CSV = 0;
  public static final int nEXPORT_MODE_EXCEL = 1;
  
  public static final int nEXCEL_LIBRARY_JXL = 0;
  public static final int nEXCEL_LIBRARY_POI = 1;
  
  public static int nEXCEL_LIBRARY = nEXCEL_LIBRARY_POI;
  
   
  public static boolean bUSE_SCANNER = false;

  public static String APP_PROTOCOL = null;
  public static String APP_HOST = null;
  public static int APP_PORT = -1;
  public static String APP_URL_PREFIX = null;

  public static final String SYSTEM_ORGANIZATION = "1";

  public static int ERROR_PROCESS_CHANGED = -1;

  public static String NAME_DB_POOL = null;
  public static String DB_TYPE = null;

  public static int EVENT_THREAD_CICLE = -1;
  public static int DELEGATION_THREAD_CICLE = -1;
  public static int PROFILESYNC_THREAD_CICLE = -1;
  public static boolean PROFILESYNC_ON = false;
  public static String PROFILESYNC_ORGID = "1";

  public static String sMAIL_ADM_ERROR_NOTIFY = null;
  public static String sMAIL_SERVER = null;
  public static int nMAIL_PORT = 25;
  public static boolean bMAIL_AUTH = false;
  public static boolean bMAIL_STARTTLS = false;
  public static String sMAIL_USERNAME = null;
  public static String sMAIL_PASSWORD = null;
  public static int iMAIL_MAXIMUM_RETRIES = 0;
  public static long lMAIL_RESCHEDULE_INTERVAL = 1L;
  public static String sAPP_EMAIL = null;
  public static String sTEST_EMAIL = null;
  public static String sTEST_SMS = null;
  public static boolean bUSE_EMAIL = true;
  public static boolean bEMAIL_MANAGER = false;
  public static int nEMAIL_MANAGER_TIMEOUT = -1;
  public static int nEMAIL_MANAGER_LIMIT = -1;

  public static String sDELEGATION_NOTIFY_REQUEST_MODE = "";
  public static String sDELEGATION_NOTIFY_ASSIGN_MODE = "";
  public static String sDELEGATION_NOTIFY_ACCEPT_MODE = "";
  public static String sDELEGATION_NOTIFY_DELETE_MODE = "";
  public static String sPROCESS_END_STYLE = "all";
  public final static String PROCESS_END_STYLE_ALL = "all";
  public final static String PROCESS_END_STYLE_FLOW = "flow";
  public final static String NOTIFICATION = "notification";
  public final static String EMAIL = "email";
  public final static String BOTH = "both";
  public final static String NONE = "none";

  public static String sSAVE_FLOW_STATE_ALLWAYS= "true";
  
  public static long CLICK_TIMEOUT = 2L; // 2 seconds by default
  public static final String CLICK_TIMEOUT_NAME = "CLICK_TIMEOUT";

  public static String DOCS_BASE_URL = null;

  public static final String MISSING_PROC_MID_ERROR_MSG = "Informa&ccedil;&atilde;o sobre estado actual do processo n&atilde;o foi encontrada.<br>Por favor tente novamente.";
  public static final String PROC_CHANGED_ERROR_MSG = "Processo alterado por outro utilizador. Os presentes dados foram actualizados (as altera&ccedil;&otilde;es efectuadas n&atilde;o foram guardadas).";

  public static final String PROC_SAVED = "Processo Actualizado.";
  public static final String PROC_UNCHANGED = "N&atilde;o foram efectuadas altera&ccedil;&otilde;es ao processo (processo inalterado).";
  public static final String PROC_UNCHANGED_ERROR = "Devido aos erros encontrados, o processo n&atilde;o foi actualizado.";

  public static final String SELECT_TXT = " -- Seleccionar -- ";
  public static final String ID_SELECT_TXT = "--";


  public static final String sUSER_FLOWID = "ufid";
  public static final String sFLOWNAME = "flowname";
  
  public static final String sEVENT_FIRED = "EVENT_FIRED";

  public static final String sMID_ATTRIBUTE = "curmid";
  
  // guest vars
  
  public static final String GUEST_NAME = "guest";
  public static final String GUEST_SESSION = "guest";
  
  // process fetch
  public static final int nALL_PROCS = 0;
  public static final int nOPENED_PROCS = 1;
  public static final int nCLOSED_PROCS = 2;
  public static final int nREADONLY_OFFSET = 10;
  public static final int nALL_PROCS_READONLY = nREADONLY_OFFSET + nALL_PROCS;
  public static final int nOPENED_PROCS_READONLY = nREADONLY_OFFSET + nOPENED_PROCS;
  public static final int nCLOSED_PROCS_READONLY = nREADONLY_OFFSET + nCLOSED_PROCS;

  // process end modes
  public static final int nCANCEL_PROC = 0;
  public static final int nEND_PROC = 1;

  public static final String sNOTIFY_USER = "NOTIFY_USER";
  public static final String sNOTIFY_USER_DESC = "Notificar Utilizador de Nova Tarefa";
  public static final String sNOTIFY_USER_YES = "Sim";
  public static final String sNOTIFY_USER_NO = "Nao";
  public static final String sFORCE_NOTIFY_FOR_PROFILES = "FORCE_NOTIFY_FOR_PROFILE";
  public static final String sFORCE_NOTIFY_FOR_PROFILES_DESC =
    "For&ccedil;ar Notifica&ccedil;&atilde;o para Perfis (quando"
    + sNOTIFY_USER + "=" + sNOTIFY_USER_NO + ")";
  public static final String sDENY_NOTIFY_FOR_PROFILES = "DENY_NOTIFY_FOR_PROFILE";
  public static final String sDENY_NOTIFY_FOR_PROFILES_DESC =
    "Abortar Notifica&ccedil;&atilde;o para Perfis (quando "
    + sNOTIFY_USER + "=" + sNOTIFY_USER_YES + ")";
  public static final String sNOTIFY_FOR_PROFILES_SEPARATOR = Const.DEFAULT_SEPARATOR;

  public static final String FLOWEXECTYPE = "flowExecType";

  public static final String USER_INFO = "UserInfo";
  public static final String ORG_INFO = "OrgInfo";
  public static final String SESSION_PROCESS = "SESSION_PROCESS";
  public static final String SESSION_PROCESS_SEARCH = "SESSION_PROCESSSEARCH";
  public static final String SESSION_PROCESS_REPORT = "SESSION_PROCESSREPORT";

  public static final String SESSION_PROCESS_POPUP_BACKUP = "SESSION_PROCESS_POP_UP";

  // switch proc as user constants
  public static final String sSWITCH_PROC_SESSION_ATTRIBUTE = "SWITCH_PROC_CALLER_PROC";  
  public static final String sSWITCH_PROC_RETURN_PARENT = "SWITCH_PROC_RETURN_PARENT";  
  public static final String sSWITCH_PROC_FLAG = "SWITCH_PROC_FLAG";  
  public static final String sSWITCH_PROC_TEMP_FID = "SWITCH_PROC_TEMP_FID";
  public static final String sSWITCH_PROC_TEMP_PID = "SWITCH_PROC_TEMP_PID";
  public static final String sSWITCH_PROC_TEMP_SUBPID = "SWITCH_PROC_TEMP_SUBPID";
  
  public static final String SESSION_ATTRIBUTE_FLOWID = "session_attribute_flowid";
  public static final String SESSION_ATTRIBUTE_PID = "session_attribute_pid";
  public static final String SESSION_ATTRIBUTE_SUBPID = "session_attribute_subpid";
  
  public static final String ATTR_PREFIX = "attr_";

  public static final String sPROCESS_LOCATION = "PROCESS_LOCATION";
  public static final String sPROCESS_LOCATION_DESC = "Iniciar Processo em";
  public static final String sPROCESS_IN_SESSION = "Sessao";
  public static final String sPROCESS_IN_DB = "Base Dados";
  public static final int nSESSION_PID = -10;
  public static final int nSESSION_SUBPID = -10;
  public static final int NO_MID = -55;

  public static String sDEFAULT_PROCESS_LOCATION = Const.sPROCESS_IN_SESSION;

  public static final String sFLOW_ENTRY_PAGE_TITLE = "FLOW_ENTRY_PAGE_TITLE";
  public static final String sFLOW_ENTRY_PAGE_TITLE_DESC = "Title P&aacute;g. Entrada";
  public static final String sFLOW_ENTRY_PAGE_LINK = "FLOW_ENTRY_PAGE_LINK";
  public static final String sFLOW_ENTRY_PAGE_LINK_DESC = "Link P&aacute;g. Entrada";

  public static String sDEF_DATE_FORMAT = null;
  public static String sDEF_INT_FORMAT = null;
  public static String sDEF_FLOAT_FORMAT = null;

  public static final String sFLOW_DATE_FORMAT = "FLOW_DATE_FORMAT";
  public static String sFLOW_DATE_FORMAT_DESC = "Formato para as Datas";
  public static final String sFLOW_INT_FORMAT = "FLOW_INT_FORMAT";
  public static String sFLOW_INT_FORMAT_DESC = "Formato para Números Inteiros";
  public static final String sFLOW_FLOAT_FORMAT = "FLOW_FLOAT_FORMAT";
  public static String sFLOW_FLOAT_FORMAT_DESC = "Formato para Números Decimais";

  public static final String sSEARCHABLE_BY_INTERVENIENT = "SEARCHABLE_BY_INTERVENIENT";
  public static final String sSEARCHABLE_BY_INTERVENIENT_DESC = "Pesquisável pelos intervenientes";
  public static final String sSEARCHABLE_BY_INTERVENIENT_YES = "Sim";
  public static final String sSEARCHABLE_BY_INTERVENIENT_NO = "Nao";

  public static final String sSHOW_SCHED_USERS = "SHOW_SCHED_USERS";
  public static final String sSHOW_SCHED_USERS_DESC = "Mostra utilizador(es) para quem foi agendado processo";
  public static final String sSHOW_YES = "Sim";
  public static final String sSHOW_NO = "Nao";

  public static final String sDIRECT_LINK_AUTHENTICATION = "DIRECT_LINK_AUTHENTICATION";
  public static final String sDIRECT_LINK_AUTHENTICATION_DESC = "Permite ou n&atilde;o iniciar um fluxo sem autentica&ccedil;&atilde;o (link)";
  public static final String sDIRECT_LINK_YES = "Sim";
  public static final String sDIRECT_LINK_NO = "Nao";

  public static final String sEMAIL_TEMPLATE_DIR = "EMAIL_TEMPLATE_DIR";
  public static final String sEMAIL_TEMPLATE_DIR_DESC = "Directoria de templates de email";

  
  public static final String sRUN_MAXIMIZED = "RUN_MAXIMIZED";
  public static final String sRUN_MAXIMIZED_DESC = "Executar fluxo maximizado";
  public static final String sRUN_MAXIMIZED_YES = "Sim";
  public static final String sRUN_MAXIMIZED_NO = "Nao";

  public static final String sENABLE_HISTORY = "ENABLE_HISTORY";
  public static final String sENABLE_HISTORY_DESC = "Permitir Visualizacao de Historicos";
  public static final String sENABLE_HISTORY_NO = "flow_settings.showHist.no";
  public static final String sENABLE_HISTORY_HIDDEN = "flow_settings.showHist.hidden";
  public static final String sENABLE_HISTORY_FULL = "flow_settings.showHist.full";

  public static final String sDEFAULT_STYLESHEET = "DEFAULT_STYLESHEET";
  public static final String sDEFAULT_STYLESHEET_DESC = "Stylesheet default";

  public static final String sDETAIL_PRINT_STYLESHEET = "DETAIL_PRINT_STYLESHEET";
  public static final String sDETAIL_PRINT_STYLESHEET_DESC = "Template de Impressao de Detalhe";

  public static final String sHASHED_DOCUMENT_URL = "HASHED_DOCUMENT_URL";
  public static final String sHASHED_DOCUMENT_URL_DESC = "Gerar link de download de Documentos com hash";
  public static final String sHASHED_DOCUMENT_URL_YES = "Sim";
  public static final String sHASHED_DOCUMENT_URL_NO = "Nao";

  public static final String sAUTO_ARCHIVE_PROCESS = "AUTO_ARCHIVE_PROCESS";
  public static final String sAUTO_ARCHIVE_PROCESS_DESC = "Tempo de vida de um processo fechado (em dias)";
  
  public static final String sSHOW_ASSIGNED_TO = "SHOW_ASSIGNED_TO";
  public static final String sSHOW_ASSIGNED_TO_DESC = "Mostra utilizador(es) onde o processo est&aacute; agendado";

  public static final String sGUEST_ACCESSIBLE = "GUEST_ACCESSIBLE";
  public static final String sGUEST_ACCESSIBLE_DESC = "Permitir acesso a utilizadores não registados.";
  public static final String sGUEST_ACCESSIBLE_YES = "Sim";
  public static final String sGUEST_ACCESSIBLE_NO = "Nao";
  public static boolean GUEST_ACCESSIBLE = false;

  public static final String sFLOW_MENU_ACCESSIBLE = "FLOW_MENU_ACCESSIBLE";
  public static final String sFLOW_MENU_ACCESSIBLE_DESC = "Permitir visualizar no menu.";
  public static final String sFLOW_MENU_ACCESSIBLE_YES = "Sim";
  public static final String sFLOW_MENU_ACCESSIBLE_NO = "Nao";

  public static int nEXPORT_MODE = 1;

  public static String DEBUG_FORM = null;
  public static boolean DEBUG_PROC_XML = false;
  public static boolean DONT_LOG_IN_DB = false;
  
  public static String USER_DATA_IMPL_CLASS = null;
  public static String USER_DATA_CONFIG = null;
  public static String AUTH_IMPL_CLASS = null;
  public static String SYSTEM_USER_DATA_IMPL_CLASS = null;
  public static String AUTH_CONFIG = null;
  public static String SYSTEM_AUTH_CONFIG = null;
  public static String ORGANIZATION_DATA_IMPL_CLASS = null;
  public static String ORGANIZATION_DATA_CONFIG = null;
  public static String APPLICATION_DATA_IMPL_CLASS = null;

  public static String FEED_GEN_IMPLEMENTATION = null;
  public static String FEED_FORMAT = null;
  public static String FEED_URL = null;

  public static String SIDEBAR_GEN_IMPLEMENTATION = "pt.iknow.sidebar.DesktopSidebarGenerator";

  public static final File fUPLOAD_TEMP_DIR = new File(System.getProperty("iflow.home"), "temp");
  public static final int nUPLOAD_THRESHOLD_SIZE = 10*1024;  // 10 KB
  
  private static int  MAX_FILE_SIZE = -1;
  public static int nUPLOAD_MAX_SIZE = -1; // 10 MB
  
  public static int  MAX_DOUBLE_DECIMAL_DIGITS = 8;
  
  public static final String INSTALL_LOCAL = "local";
  public static final String INSTALL_WEB = "web";
  public static final String INSTALL_DEMO = "demo";
  
  public static String INSTALL_TYPE = null;
  
  public static String DEFAULT_SUBPID = "1";

  public static final int INDEX_COLUMN_COUNT = 20;
  
  public static final String LANG_COOKIE = "if_lang";
  
  public static String AUTHENTICATION_TYPE = null;
  public static final String AUTHENTICATION_BOTH = "BOTH";
  public static final String AUTHENTICATION_HTTP = "HTTP";
  public static final String AUTHENTICATION_FORM = "FORM";
  
  public static boolean USE_INDIVIDUAL_LOCALE = true;
  public static boolean ASK_LOCALE_AT_LOGIN = true;
  
  public static final boolean CHECK_PORT_MAPPING = false;

  public static boolean HASHED_DOCUMENT_URL = false;
  
  public static String AUTO_ARCHIVE_CRON;
  
  public static boolean SEARCH_FLOW_STATE_HISTORY = true;
  
  public static String DMS_END_POINT_ADDRESS;
  public static String DMS_DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm '('Z')'";

  public static String LOGIN_TEMPLATE_DEFAULT = "login_default";
  public static String LOGIN_TEMPLATE;
  
  public static boolean DISABLE_DATASOURCE_MANAGEMENT=true;
  public static final String iFLOW_DATASOURCE_CTX = "iflow:comp";
  
  public static final String DEFAULT_SEPARATOR = ",";
  public final static String CONNECTOR_DEFAULT_PKG = "pt.iflow.api.connectors.handlers";

  public static final String SAVE_PROCESSHISTORY_METHOD_COMPLETE = "COMPLETE";
  public static final String SAVE_PROCESSHISTORY_METHOD_COMPRESS = "COMPRESS";
  public static final String SAVE_PROCESSHISTORY_METHOD_NOTHING = "NOTHING";
  public static String SAVE_PROCESSHISTORY_METHOD = SAVE_PROCESSHISTORY_METHOD_COMPLETE;

  public static final String SAVE_PROCESSHISTORY_WHEN_ALLWAYS = "ALLWAYS";
  public static final String SAVE_PROCESSHISTORY_WHEN_ONCHANGE = "ONCHANGE";
  public static final String SAVE_PROCESSHISTORY_WHEN_NEVER = "NEVER";
  public static String SAVE_PROCESSHISTORY_WHEN = SAVE_PROCESSHISTORY_WHEN_ALLWAYS;
  
  public static final String sEXTRA_PROP = "EXTRA.";
  
  // hotfolder
  public static int HOT_FOLDER_SEARCH_INTERVAL = -1; 
  public static String HOT_FOLDER_PROCESSED_FOLDER = null; 

  public static boolean SEARCH_ALL_USER_PROCS_BY_DEFAULT = false;

  //CERTIFICATES PROPERTIES
  public static String sKEYSTORE_PATH = null;
  public static String sKEYSTORE_PASS = null;
  public static String sALIAS = null;
  public static String sALIAS_PASS = null;
  
  //SIGNATURE PROPERTIES
  public static String sSIGNATURE_CERTIFICATE=null;
  public static String sSIGNATURE_POSITION_STYLE=null;
  public static String sSIGNATURE_UPLOAD=null;
  public static String sRUBRIC_CERTIFICATE=null;
  public static String sRUBRIC_IMAGE=null;
  public static String sRUBRIC_POSITION_STYLE=null;
  public static String sRUBRIC_UPLOAD=null;
  
  public static String sALLOW = "ALLOW";
  public static String sYES = "YES"; 
  public static String sNO = "NO";
  public static String sPREVIEW = "PREVIEW";
  public static String sMATRIX = "MATRIX";  
  public static String sSIGNATURE = "SIGNATURE";
  public static String sRUBRIC = "RUBRIC";

  
  /**
   * Indicação se fica ou não na página.
   * Vai sair daqui e morar para um sitio muito mais bonito.
   */
  public static final String STAY_IN_PAGE   = "__STAY_IN_PAGE__";
  
  //Session filter variables
  public static final String SESSION_USER_PROCS = "SESSION_USER_PROCS"; //nShowFlowId
  public static final String SESSION_USER_PROCS_FLOWID = "SESSION_USER_PROCS_FLOWID"; //nShowFlowId
  public static final String SESSION_USER_PROCS_TARGETUSER = "SESSION_USER_PROCS_TARGETUSER"; //targetUser
  public static final String SESSION_USER_PROCS_PROCESSSTATUS = "SESSION_USER_PROCS_PROCESSSTATUS"; //processStatus
  public static final String SESSION_USER_PROCS_PNUMBER = "SESSION_USER_PROCS_PNUMBER"; //pnumber
  public static final String SESSION_USER_PROCS_DTAFTER = "SESSION_USER_PROCS_DTAFTER"; //dtAfter
  public static final String SESSION_USER_PROCS_DTBEFORE = "SESSION_USER_PROCS_DTBEFORE"; //dtBefore
  public static final String SESSION_USER_PROCS_STARTINDEX = "SESSION_USER_PROCS_STARTINDEX"; //nStartIndex
  public static final String SESSION_USER_PROCS_NITEMS = "SESSION_USER_PROCS_NITEMS"; //nItems, 
  public static final String SESSION_USER_PROCS_ORDERBY = "SESSION_USER_PROCS_ORDERBY"; //sOrderBy
  public static final String SESSION_USER_PROCS_ORDERTYPE = "SESSION_USER_PROCS_ORDERTYPE"; //sOrderType
  public static final String SESSION_USER_PROCS_IDX = "SESSION_USER_PROCS_IDX"; //idx
  public static final String EDITOR_MODE = "EDITOR_MODE";
  public static final String EDITOR_MODE_BPMN = "BPMN";
  public static final String EDITOR_MODE_CLASSIC = "CLASSIC";

  // Session cookie
  public static final String SESSION_COOKIE_USERNAME = "SESSION_COOKIE_USERNAME";
  public static final String SESSION_COOKIE_PASSWORD = "SESSION_COOKIE_PASSWORD";

  //Default Locate
  public static String DEFAULT_LOCALE = null;
  public static final String sDEFAULT_LOCALE_PT_PT = "pt-PT";
  public static final String sDEFAULT_LOCALE_EN_US = "en-US";
  public static final String sDEFAULT_LOCALE_ES_ES = "es-ES";
  
  public static String MAX_LOGIN_ATTEMPTS = "MAX_LOGIN_ATTEMPTS";
  public static String MAX_LOGIN_ATTEMPTS_WAIT = "MAX_LOGIN_ATTEMPTS_WAIT";
  
  private static List<String> ALLOWED_LOCALES = new ArrayList<String>();

  static {
    ALLOWED_LOCALES.add(sDEFAULT_LOCALE_PT_PT);
    ALLOWED_LOCALES.add(sDEFAULT_LOCALE_EN_US);
    ALLOWED_LOCALES.add(sDEFAULT_LOCALE_ES_ES);
  }

  static {
    try {
      updateConstants();
    } catch (Throwable t) {
      Logger.error("", "Const", "updateConstants", t.getMessage());
      t.printStackTrace();
      updateConstants();
    }
  }

  public static boolean isInMaintenance() {
    return StringUtils.isNotEmpty(Const.sMAINTENANCE_USER); 
  }
  
  public static synchronized void updateConstants() {

    String stmp = null;

    // direct setting constants
    APP_PROTOCOL = Setup.getProperty("APP_PROTOCOL");
    APP_HOST = Setup.getProperty("APP_HOST");
    APP_PORT = Setup.getPropertyInt("APP_PORT");
    APP_URL_PREFIX = Setup.getProperty("APP_URL_PREFIX");

    NAME_DB_POOL = Setup.getProperty("DB_POOL_NAME");
    DB_TYPE = Setup.getProperty("DB_TYPE");

    EVENT_THREAD_CICLE = Setup.getPropertyInt("EVENT_THREAD_CICLE");
    DELEGATION_THREAD_CICLE = Setup.getPropertyInt("DELEGATION_THREAD_CICLE");
    PROFILESYNC_THREAD_CICLE = Setup.getPropertyInt("PROFILESYNC_THREAD_CICLE");
    PROFILESYNC_ON = false;
    DEBUG_PROC_XML = false;
    DONT_LOG_IN_DB = false;
    try {
      PROFILESYNC_ON = Boolean.parseBoolean(Setup.getProperty("PROFILESYNC_ON"));
    } catch (Exception e) {}
    try {
      DEBUG_PROC_XML = Boolean.parseBoolean(Setup.getProperty("DEBUG_PROC_XML"));
    } catch (Exception e) {}
    try {
      DONT_LOG_IN_DB = Boolean.parseBoolean(Setup.getProperty("DONT_LOG_IN_DB"));
    } catch (Exception e) {}
    try {
      SEARCH_ALL_USER_PROCS_BY_DEFAULT = Boolean.parseBoolean(Setup.getProperty("SEARCH_ALL_USER_PROCS_BY_DEFAULT"));
    } catch (Exception e) {}
    PROFILESYNC_ORGID = Setup.getProperty("PROFILESYNC_ORGID");
    sMAIL_ADM_ERROR_NOTIFY = Setup.getProperty("MAIL_ADM_ERROR_NOTIFY");
    sMAIL_SERVER = Setup.getProperty("MAIL_SERVER");
    nMAIL_PORT = Setup.getPropertyInt("MAIL_PORT");
    if(nMAIL_PORT == -1) nMAIL_PORT = 25;
    String sMAIL_AUTH = Setup.getProperty("MAIL_AUTH");
    bMAIL_AUTH = ("true".equalsIgnoreCase(sMAIL_AUTH)||"yes".equalsIgnoreCase(sMAIL_AUTH));
    stmp = Setup.getProperty("MAIL_STARTTLS");
    bMAIL_STARTTLS = StringUtils.isNotEmpty(stmp) && ArrayUtils.contains(new String[]{"yes","true","on","1"}, stmp.toLowerCase());
    String sMAIL_MAXIMUM_RETRIES = Setup.getProperty("MAIL_MAXIMUM_RETRIES");
    try {
      iMAIL_MAXIMUM_RETRIES = Integer.parseInt(sMAIL_MAXIMUM_RETRIES);
    } catch (NumberFormatException e) {
      iMAIL_MAXIMUM_RETRIES = 3;
    }
    String sMAIL_RESCHEDULE_INTERVAL = Setup.getProperty("MAIL_RESCHEDULE_INTERVAL");
    try {
      lMAIL_RESCHEDULE_INTERVAL = Long.parseLong(sMAIL_RESCHEDULE_INTERVAL);
    } catch (NumberFormatException e) {
      lMAIL_RESCHEDULE_INTERVAL = 1L;
    }
    sMAIL_USERNAME = Setup.getProperty("MAIL_USERNAME");
    sMAIL_PASSWORD = Setup.getProperty("MAIL_PASSWORD");
    sAPP_EMAIL = Setup.getProperty("APP_EMAIL");
    sTEST_EMAIL = Setup.getProperty("TEST_EMAIL");
    sTEST_SMS = Setup.getProperty("TEST_SMS");
    nEMAIL_MANAGER_TIMEOUT = Setup.getPropertyInt("EMAIL_MANAGER_TIMEOUT");
    nEMAIL_MANAGER_LIMIT = Setup.getPropertyInt("EMAIL_MANAGER_LIMIT");

    Const.sMAINTENANCE_USER = Setup.getProperty("MAINTENANCE_USER");

    Const.DEBUG_FORM = Setup.getProperty("DEBUG_FORM");
    String uploadmax = Setup.getProperty("UPLOAD_MAX_SIZE");
    if (StringUtils.isNotEmpty(uploadmax)) {
      try {
        // PROPERTY IN MB
        nUPLOAD_MAX_SIZE = Integer.parseInt(uploadmax) * 1024*1024;
      }
      catch (NumberFormatException e) {        
      }
    }
        
    // constants that need processing before setting.
    stmp = Setup.getProperty("MODE");
    if (stmp != null) {
      if (stmp.equalsIgnoreCase("production")) {
        Const.nMODE = Const.nPRODUCTION;
      }
      else if (stmp.equalsIgnoreCase("test")){
        Const.nMODE = Const.nTEST;
      }
      else {
        Const.nMODE = Const.nDEVELOPMENT;
      }
    }
    
    if(StringUtils.isEmpty(sTEST_EMAIL) && Const.nMODE != Const.nPRODUCTION) {
      System.err.println();
      System.err.println("WARNING: Empty Test Email in Test Mode!");
      System.err.println("Please configure a valid test email");
      System.err.println();
    }
    
    
    stmp = Setup.getProperty("USE_EMAIL");
    bUSE_EMAIL = StringUtils.isNotEmpty(stmp) && ArrayUtils.contains(new String[]{"yes","true","on","1"}, stmp.toLowerCase());
    
    stmp = Setup.getProperty("EMAIL_MANAGER");
    bEMAIL_MANAGER = StringUtils.isNotEmpty(stmp) && ArrayUtils.contains(new String[]{"yes","true","on","1"}, stmp.toLowerCase());
    
    stmp = Setup.getProperty("USE_SCANNER");
    bUSE_SCANNER = StringUtils.isNotEmpty(stmp) && ArrayUtils.contains(new String[]{"yes","true","on","1"}, stmp.toLowerCase());
    
    stmp = Setup.getProperty("DEFAULT_PROCESS_LOCATION");
    if (stmp != null && stmp.equalsIgnoreCase("session")) {
      Const.sDEFAULT_PROCESS_LOCATION = sPROCESS_IN_SESSION;
    }
    else {
      Const.sDEFAULT_PROCESS_LOCATION = sPROCESS_IN_DB;
    }
    
    stmp = Setup.getProperty("DEFAULT_DATE_FORMAT");
    if (StringUtils.isNotBlank(stmp)) {
      sDEF_DATE_FORMAT = stmp;
    }
    else {
      sDEF_DATE_FORMAT = "yyyy/MM/dd";
    }
    sFLOW_DATE_FORMAT_DESC = "Formato para as Datas (" + sDEF_DATE_FORMAT + ")";

    stmp = Setup.getProperty("DEFAULT_INT_FORMAT");
    if (StringUtils.isNotBlank(stmp)) {
      sDEF_INT_FORMAT = stmp;
    }
    else {
      sDEF_INT_FORMAT = "#0";
    }
    sFLOW_INT_FORMAT_DESC = "Formato para Números Inteiros (" + sDEF_INT_FORMAT + ")";

    stmp = Setup.getProperty("DEFAULT_FLOAT_FORMAT");
    if (StringUtils.isNotBlank(stmp)) {
      sDEF_FLOAT_FORMAT = stmp;
    }
    else {
      sDEF_FLOAT_FORMAT = "#,##0.00";
    }
    sFLOW_FLOAT_FORMAT_DESC = "Formato para Números Decimais (" + sDEF_FLOAT_FORMAT + ")";


    // default is EXCEL
    nEXPORT_MODE = nEXPORT_MODE_EXCEL;
    stmp = Setup.getProperty("EXPORT_MODE");
    if (stmp != null && stmp.equalsIgnoreCase("csv")) {
      nEXPORT_MODE = nEXPORT_MODE_CSV;
    }

    stmp = Setup.getProperty("DEFAULT_LOCALE");
    if (stmp != null && ALLOWED_LOCALES.contains(stmp)) {
      Const.DEFAULT_LOCALE = stmp;
    }
    
    // click timeout stuff
    CLICK_TIMEOUT = Setup.getPropertyInt(CLICK_TIMEOUT_NAME);  

    USER_DATA_IMPL_CLASS = Setup.getProperty("USER_DATA_IMPL_CLASS");
    USER_DATA_CONFIG = Setup.getProperty("USER_DATA_CONFIG");
    AUTH_IMPL_CLASS = Setup.getProperty("AUTH_IMPL_CLASS");
    SYSTEM_USER_DATA_IMPL_CLASS = Setup.getProperty("SYSTEM_USER_DATA_IMPL_CLASS");
    AUTH_CONFIG = Setup.getProperty("AUTH_CONFIG");
    SYSTEM_AUTH_CONFIG = Setup.getProperty("SYSTEM_AUTH_CONFIG");
    ORGANIZATION_DATA_IMPL_CLASS = Setup.getProperty("ORGANIZATION_DATA_IMPL_CLASS");
    ORGANIZATION_DATA_CONFIG = Setup.getProperty("ORGANIZATION_DATA_CONFIG");
    APPLICATION_DATA_IMPL_CLASS = Setup.getProperty("APPLICATION_DATA_IMPL_CLASS");

    FEED_GEN_IMPLEMENTATION = Setup.getProperty("FEED_GEN_IMPLEMENTATION");
    FEED_FORMAT = Setup.getProperty("FEED_FORMAT");
    FEED_URL = Setup.getProperty("FEED_URL");

    SIDEBAR_GEN_IMPLEMENTATION = Setup.getProperty("SIDEBAR_GEN_IMPLEMENTATION");
    
    INSTALL_TYPE = Setup.getProperty("INSTALL_TYPE");

    if(!ArrayUtils.contains(new String[]{INSTALL_LOCAL, INSTALL_DEMO, INSTALL_WEB}, INSTALL_TYPE))
      INSTALL_TYPE=INSTALL_LOCAL;

    AUTHENTICATION_TYPE = Setup.getProperty("AUTHENTICATION_TYPE");
    if(null == AUTHENTICATION_TYPE) AUTHENTICATION_TYPE = AUTHENTICATION_BOTH;

    stmp = Setup.getProperty("USE_INDIVIDUAL_LOCALE");
    USE_INDIVIDUAL_LOCALE = StringUtils.isNotEmpty(stmp) && ArrayUtils.contains(new String[]{"yes","true","on","1"}, stmp.toLowerCase());
    stmp = Setup.getProperty("ASK_LOCALE_AT_LOGIN");
    ASK_LOCALE_AT_LOGIN = StringUtils.isNotEmpty(stmp) && ArrayUtils.contains(new String[]{"yes","true","on","1"}, stmp.toLowerCase());
    stmp = Setup.getProperty("HASHED_DOCUMENT_URL");
    HASHED_DOCUMENT_URL = StringUtils.isNotEmpty(stmp) && ArrayUtils.contains(new String[]{"yes","true","on","1"}, stmp.toLowerCase());
    
    AUTO_ARCHIVE_CRON = Setup.getProperty("AUTO_ARCHIVE_CRON","0 0 3 * * ?"); // todos os dias as 3 horas
    stmp = Setup.getProperty("SEARCH_FLOW_STATE_HISTORY");
    SEARCH_FLOW_STATE_HISTORY = StringUtils.isEmpty(stmp) || ArrayUtils.contains(new String[]{"yes","true","sim","1"}, stmp.toLowerCase());
    
    DMS_END_POINT_ADDRESS = Setup.getProperty("DMS_END_POINT_ADDRESS");
    
    LOGIN_TEMPLATE = Setup.getProperty("LOGIN_TEMPLATE");
    
    stmp = Setup.getProperty("DISABLE_DATASOURCE_MANAGEMENT");
    DISABLE_DATASOURCE_MANAGEMENT=StringUtils.isEmpty(stmp) || ArrayUtils.contains(new String[]{"yes","true","sim","1"}, stmp.toLowerCase());

    stmp = Setup.getProperty("SAVE_PROCESSHISTORY_METHOD");
    if (stmp!=null && (stmp.equalsIgnoreCase(SAVE_PROCESSHISTORY_METHOD_COMPRESS) || stmp.equalsIgnoreCase(SAVE_PROCESSHISTORY_METHOD_NOTHING)))
      SAVE_PROCESSHISTORY_METHOD = stmp.toUpperCase();
    
    stmp = Setup.getProperty("SAVE_PROCESSHISTORY_WHEN");
    if (stmp!=null && (stmp.equalsIgnoreCase(SAVE_PROCESSHISTORY_WHEN_ONCHANGE) || stmp.equalsIgnoreCase(SAVE_PROCESSHISTORY_WHEN_NEVER)))
      SAVE_PROCESSHISTORY_WHEN = stmp.toUpperCase();

    // interval in setup is in minutes
    HOT_FOLDER_SEARCH_INTERVAL = Setup.getPropertyInt("HOT_FOLDER_SEARCH_INTERVAL");
    if (HOT_FOLDER_SEARCH_INTERVAL == -1) {
      HOT_FOLDER_SEARCH_INTERVAL = 5;
    }
    HOT_FOLDER_SEARCH_INTERVAL = HOT_FOLDER_SEARCH_INTERVAL * 60000;  // interval in miliseconds
    HOT_FOLDER_PROCESSED_FOLDER = Setup.getProperty("HOT_FOLDER_PROCESSED_FOLDER");
    if (StringUtils.isEmpty(HOT_FOLDER_PROCESSED_FOLDER)) {
      HOT_FOLDER_PROCESSED_FOLDER = FilenameUtils.concat(IFLOW_HOME, "hot_folder_processed");
      try {
        FileUtils.forceMkdir(new File(HOT_FOLDER_PROCESSED_FOLDER));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    MAX_FILE_SIZE = Setup.getPropertyInt("MAX_FILE_SIZE");
    int aux_MAX_DOUBLE_DECIMAL_DIGITS = Setup.getPropertyInt("MAX_DOUBLE_DECIMAL_DIGITS");
    if (aux_MAX_DOUBLE_DECIMAL_DIGITS > 0) {
      MAX_DOUBLE_DECIMAL_DIGITS = aux_MAX_DOUBLE_DECIMAL_DIGITS;
    }
    nUPLOAD_MAX_SIZE = MAX_FILE_SIZE *1024*1024; // 10 MB
    
    String sEXCEL_LIBRARY = Setup.getProperty("EXCEL_LIBRARY");
    if(sEXCEL_LIBRARY != null && sEXCEL_LIBRARY.equals("JXL"))
    	nEXCEL_LIBRARY = nEXCEL_LIBRARY_JXL;

    DOCS_BASE_URL = Setup.getProperty("DOCS_BASE_URL");
    
    try {
      sDELEGATION_NOTIFY_REQUEST_MODE = Setup.getProperty("DELEGATION_NOTIFY_REQUEST_MODE").toLowerCase();
    } catch (Exception e) { }
    try {
      sDELEGATION_NOTIFY_ASSIGN_MODE = Setup.getProperty("DELEGATION_NOTIFY_ASSIGN_MODE").toLowerCase();
    } catch (Exception e) { }
    try {
      sDELEGATION_NOTIFY_ACCEPT_MODE = Setup.getProperty("DELEGATION_NOTIFY_ACCEPT_MODE").toLowerCase();
    } catch (Exception e) { }
    try {
      sDELEGATION_NOTIFY_DELETE_MODE = Setup.getProperty("DELEGATION_NOTIFY_DELETE_MODE").toLowerCase();
    } catch (Exception e) { }

    try {
        sPROCESS_END_STYLE = Setup.getProperty("PROCESS_END_STYLE").toLowerCase();
      } catch (Exception e) { }

    try {
      sSAVE_FLOW_STATE_ALLWAYS = Setup.getProperty("SAVE_FLOW_STATE_ALLWAYS").toLowerCase();
    } catch (Exception e) { }
    
    try {    
    sKEYSTORE_PATH = Setup.getProperty("KEYSTORE_PATH");
    } catch (Exception e) { }
    try { 
    sKEYSTORE_PASS = Setup.getProperty("KEYSTORE_PASS");
    } catch (Exception e) { }
  	try { 
    sALIAS = Setup.getProperty("ALIAS");
  	} catch (Exception e) { }
	try { 
    sALIAS_PASS = Setup.getProperty("ALIAS_PASS");
	} catch (Exception e) { }
    
	//SIGNATURE PROPERTIES
	try { 
		sSIGNATURE_CERTIFICATE=Setup.getProperty("SIGNATURE_CERTIFICATE");
    } catch (Exception e) { 
    	sSIGNATURE_CERTIFICATE=sYES; 
    }
	try { 
		sSIGNATURE_POSITION_STYLE=Setup.getProperty("SIGNATURE_POSITION_STYLE");
    } catch (Exception e) { 
    	sSIGNATURE_POSITION_STYLE=sMATRIX; 
    }
	try { 
		sSIGNATURE_UPLOAD=Setup.getProperty("SIGNATURE_UPLOAD");
    } catch (Exception e) { 
    	sSIGNATURE_UPLOAD=sYES; 
    }
	try { 
		sRUBRIC_CERTIFICATE=Setup.getProperty("RUBRIC_CERTIFICATE");
    } catch (Exception e) { 
    	sRUBRIC_CERTIFICATE=sYES; 
    }
	try { 
		sRUBRIC_IMAGE=Setup.getProperty("RUBRIC_IMAGE");
    } catch (Exception e) {
    	sRUBRIC_IMAGE=sSIGNATURE; 
    }
	try { 
		sRUBRIC_POSITION_STYLE=Setup.getProperty("RUBRIC_POSITION_STYLE");
    } catch (Exception e) { 
    	sRUBRIC_POSITION_STYLE=sMATRIX; 
    }
	try { 
		sRUBRIC_UPLOAD=Setup.getProperty("RUBRIC_UPLOAD");
    } catch (Exception e) { 
    	sRUBRIC_UPLOAD=sNO; 
    }
    
  }

  public static void main(String[] args) {
  }
}


