package pt.iflow.api.blocks;

public class FormProps {

  // FIXME: since creation of form props in api module, editor can use this properties instead
  // of duplicating them... CHANGE EDITOR JSP FIELD DATA TO USE THEM.
  
  // attribute's name manipulation vars
  // (MUST match same vars in pt.iknow.uniflow.editor.blocks.JSPFieldData, UniFlowEditor project)
  public final static String sJUNCTION = "_";
  public final static String sNAME_PREFIX = "OBJ";
  public final static String sROW = "ROW";
  public final static String sFIELD_TYPE = "type";
  public final static String sDATATYPE = "datatype";
  public final static String sVARIABLE = "variable";
  public final static String sMACROTITLE = "macrotitle";
  public final static String sTITLE = "title";
  public final static String sSTYLESHEET = "stylesheet";
  public final static String sOUTPUT_ONLY = "output_only";
  public final static String sEXTRA_PROPS = "extra_props";
  public final static String sPRINT_STYLESHEET = "print_stylesheet";
  public final static String sONCHANGE_SUBMIT = "onchange_submit";
  public final static String sDEFAULT_VALUE = "default_value";
  public final static String sDEFAULT_TEXT = "default_text";
  public final static String sQUERY = "query";
  public final static String sDATASOURCE = "datasource";
  public final static String sCLASSE = "classe";
  public final static String sARGS = "args";
  public final static String sCONNECTOR = "connector_select";
  public final static String sVALUE_KEY = "value_key";
  public final static String sTEXT_KEY = "text_key";
  public final static String sDISABLE_COND = "disabled_cond";
  public final static String sVALIDATION_EXPR = "validation_expr";
  public final static String sVALIDATION_MSG = "validation_msg";
  public final static String sOBLIGATORY_FIELD = "obligatory";
  public final static String sTEXT_AREA = "text_area";
  // Row control list: each item is set by the flow designer, or by "someone".
  // This block, allong with the arraytable object, is reponsible for setting xml
  // tags named with this array value and "true" value, in order to allow xsl designers
  // to process control tags accordingly, without the need for form block changes
  public final static String sROW_CONTROL_LIST = "rowcontrollist";
  public final static String sROW_MACROTITLE_EXTRAPROP = "macrotitulo";
  public final static String sROW_TITLE_EXTRAPROP = "titulo";
  public final static String sROW_BGCOLOR_EXTRAPROP = "bgcolor";
  public final static String CACHE_HINT = "cache_hint";
  public final static String EXPRESSION = "expression";
  public final static String DISABLED = "disabled";
  public final static String DISABLE_TABLE_HEADER = "disable_table_header";
  public final static String TEXT_SUBMIT_ON_BLUR = "text_submit_on_blur";
  public final static String ONCLICK = "onclick";
  public final static String FILE_UPLOAD_RENAME = "file_upload_rename";
  public final static String FILE_UPLOAD_EXTENSIONS = "file_upload_extensions";
  public final static String FILE_UPLOAD_PRESERVE_EXT = "file_upload_preserve_ext";
  public final static String DMS_FOLDER = "dms_folder";
  public final static String DMS_TITLES = "dms_titles";
  public final static String DMS_VARS = "dms_vars";
  public final static String DMS_ALIGN = "dms_align";
  public final static String HOUR_FORMAT = "hour_format";
  public final static String HOUR_FORMAT_ID = "hour_format_id";
  public final static String FORM_TEMPLATE = "form_template";
  public final static String KEEP_SCROLL_ONLOAD = "keep_scroll_onload";

  
  // Some extra prop properties
  public final static String sEXTRA_PROP_DISABLE_CONDITION = "disable";
  public final static String PROP_HIDE_PREFIX = "hide_prefix";
  public final static String PROP_HIDE_SUFIX = "hide_sufix";

  
  public final static String sTEXT_VAR = "textvar";
  public final static String sTEXT_SEP = "textsep";
  public final static String sDEF_TEXT_SEP = ",";
  public final static String sTEXT_VAR_SEP = "_TVS_";
  public final static String sCHECKED_VALUE = "checkedvalue";
  public final static String sDEF_CHECKED_VALUE = "1"; // check with datatypes checkbox,radiobutton

  public final static String sOBLIGATORY_PROP = "obligatory_prop";

  
  public final static String sMAX_ROW = "_MAX_ROW"; // also used in pt.iflow.blocks.form.ArrayTable


  public final static String sLIST_TEXT_SUFFIX = "_text";
  public final static String sLIST_CHECKED_SUFFIX = "_checkedtext";

  public static final String sTITLE_EXTRAPROP_VAR = "var";

  public static final String SUB_HEADER = "subheader";
  public static final String SUB_HEADER_COL_PREFIX = SUB_HEADER + "_col_";
  
  
  public static final String JSP = "_JSP_";
  public static final String FORM_NAME = "_FORMNAME_";
  public static final String OUTPUT_FIELD = "_OUTPUTFIELD_";
  public static final String HIDDEN_FIELDS = "_HIDDEN_FIELDS_";
  public static final String VALUE_MAP = "_VALUE_MAP_";
  public final static String PRINT_STYLESHEET = "print_stylesheet"; //$NON-NLS-1$
  public final static String FORWARD_ON_SUBMIT = "forward_on_submit"; //$NON-NLS-1$
  public final static String READ_ONLY = "read_only"; //$NON-NLS-1$
  public final static String DISABLE_BUTTONS = "disable_buttons"; //$NON-NLS-1$
  public final static String FRAMEWORK_DETAIL = "framework_detail"; //$NON-NLS-1$
  
  public final static String PROC_DETAIL_SEARCH_FLOWID = "block_searchflowid"; //$NON-NLS-1$
  public final static String PROC_DETAIL_SEARCH_PID = "block_searchpid"; //$NON-NLS-1$
  public final static String PROC_DETAIL_SEARCH_SUBPID = "block_searchsubpid"; //$NON-NLS-1$


  public final static String sBUTTON_ATTR_PREFIX = "button_"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_ID = "id"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_POSITION = "position"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_TYPE = "type"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_TEXT = "text"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_TOOLTIP = "tooltip"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_IMAGE = "image"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_CUSTOM_VAR = "cvar"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_CUSTOM_VALUE = "cvalue"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_SHOW_COND = "showcond"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_IGNORE_FORM_PROCESSING = "ignoreformprocessing"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_CONFIRM_ACTION = "confirmAction"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_CONFIRM_ACTION_MESSAGE = "confirmActionMessage"; //$NON-NLS-1$
  public final static String sBUTTON_ATTR_IGNORE_FORM_VALIDATION = "ignoreformvalidation"; //$NON-NLS-1$
  
  public final static String sBUTTON_CLICKED = "_button_clicked_id"; //$NON-NLS-1$
  public static final String FLOWID = "flowid";//$NON-NLS-1$
  public static final String PID = "pid";//$NON-NLS-1$
  public static final String SUBPID = "subpid";//$NON-NLS-1$
  
  private FormProps() {}
}
