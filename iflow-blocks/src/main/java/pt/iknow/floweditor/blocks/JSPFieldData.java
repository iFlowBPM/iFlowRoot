package pt.iknow.floweditor.blocks;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iflow.api.utils.NameValuePair;
import pt.iflow.api.xml.ConnectorMarshaller;
import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlAttributeType;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttributeType;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarsType;
import pt.iflow.api.xml.codegen.flow.XmlFlow;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.swing.JMultiLineToolTip;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;


/**
 * Class that contains the jsp field data
 * Should not be used directly...
 */
public class JSPFieldData {

  private static final Logger logger = Logger.getLogger(JSPFieldData.class);

  public static String sCHOOSE;

  // Reorder operations
  private final static int REORDER_UP = 1;
  private final static int REORDER_DOWN = 2;


  // array indices for export data
  public final static int nNAMES_INDEX = 0;
  public final static int nVALUES_INDEX = 1;

  public final static String sJUNCTION = "_"; //$NON-NLS-1$
  public final static String sNAME_PREFIX = "OBJ"; //$NON-NLS-1$
  public final static String sROW = "ROW"; //$NON-NLS-1$

  // field type identifiers
  // The identifiers are now in a ENUM Class JSPFieldTypeEnum


  // field type associated texts
  // KEY: Integer with field type id
  // VALUE: String with field type's text
  protected final static HashMap<JSPFieldTypeEnum,String> _hmFieldTypes = new HashMap<JSPFieldTypeEnum,String>();


  // type's associated tooltips
  // KEY: Integer with type id
  // VALUE: String with type's tooltip
  protected static HashMap<JSPFieldTypeEnum,String> _hmFieldTypeTooltips = new HashMap<JSPFieldTypeEnum,String>();



  // field's class names
  // KEY: Integer with field type id
  // VALUE: String with class name
  protected final static HashMap<JSPFieldTypeEnum,String> _hmFieldTypeClassNames = new HashMap<JSPFieldTypeEnum,String>();

  // field's code names
  // KEY: Integer with field type id
  // VALUE: String with code name
  protected final static HashMap<JSPFieldTypeEnum,String> _hmFieldTypeCodeNames = new HashMap<JSPFieldTypeEnum,String>();

  // field's code name mapped to field ID (reverse _hmFieldTypeCodeNames)
  // KEY: String with code name
  // VALUE: Integer with field type id
  protected final static HashMap<String,JSPFieldTypeEnum> _hmFieldCodeNameTypes = new HashMap<String,JSPFieldTypeEnum>();


  // field's available properties
  public final static int nPROP_NONE = 0;
  public final static int nPROP_FIELD_TYPE = 1;
  public final static int nPROP_TEXT = 2;
  public final static int nPROP_DATA_TYPE = 3;
  public final static int nPROP_VAR_NAME = 4;
  public final static int nPROP_SIZE = 5;
  public final static int nPROP_MAXLENGTH = 6;
  public final static int nPROP_FIELD_TYPE_CODE = 7; // used only for output/export(not imported)
  public final static int nPROP_TITLE = 8;
  public final static int nPROP_ALIGNMENT = 9;
  public final static int nPROP_COLUMN = 10;
  public final static int nPROP_ID = 11;
  public final static int nPROP_VALUE = 12;
  public final static int nPROP_DATA_TYPE_CLASS = 13; // used only for output/export(not imported)
  public final static int nPROP_STYLESHEET = 14;
  public final static int nPROP_ALIGNMENT_CODE = 15; // used only for output/export(not imported)
  public final static int nPROP_COLS = 16;
  public final static int nPROP_ROWS = 17;
  public final static int nPROP_OUTPUT_ONLY = 18;
  public final static int nPROP_EMPTY_NOT_ALLOWED = 19;
  public final static int nPROP_EXTRA_PROPS = 20;
  public final static int nPROP_SERVICE_PRINT = 21;
  public final static int nPROP_SERVICE_EXPORT = 22;
  public final static int nPROP_CSS_CLASS = 23;
  public final static int nPROP_ONCLICK = 24;
  public final static int nPROP_ONMOUSE_OVER_STATUS = 25;
  public final static int nPROP_CONTROL_VAR = 26;
  public final static int nPROP_CONTROL_ON_COND = 27;
  public final static int nPROP_PROC_LINK = 28;
  public final static int nPROP_URL = 29;
  public final static int nPROP_VAR_VALUE = 30;
  public final static int nPROP_OPEN_NEW_WINDOW = 31;
  public final static int nPROP_WINDOW_NAME = 32;
  public final static int nPROP_ONCHANGE_SUBMIT = 33;
  public final static int nPROP_DATE_FORMAT = 34;
  public final static int nPROP_DATE_FORMAT_ID = 35; // used only for output/export(not imported)
  public final static int nPROP_DEFAULT_VALUE = 36;
  public final static int nPROP_DEFAULT_TEXT = 37;
  public final static int nPROP_QUERY = 38;
  public final static int nPROP_DATASOURCE = 39;
  public final static int nPROP_CLASSE = 40;
  public final static int nPROP_ARGS = 41;
  public final static int nPROP_CONNECTOR_SELECT = 42;
  public final static int nPROP_TEXT_VALUE = 43;
  public final static int nPROP_VALUE_KEY = 44;
  public final static int nPROP_TEXT_KEY = 45;
  public final static int nPROP_DISABLE_COND = 46;
  public final static int nPROP_OBLIGATORY_FIELD = 47;
  public final static int nPROP_VALIDATION_EXPR = 48;
  public final static int nPROP_VALIDATION_MSG = 49;
  public final static int nPROP_MACRO_TITLE = 50;
  public final static int nPROP_ROW_CONTROL_LIST = 51;
  public final static int nPROP_ALT_TEXT = 52;
  public final static int nPROP_WIDTH = 53;
  public final static int nPROP_HEIGHT = 54;
  public final static int nPROP_LINK_COND = 55;
  public final static int nPROP_LINK_TEXT = 56;
  public final static int nPROP_LINK_LABEL = 57;
  public final static int nPROP_EDIT_COND = 58;
  public final static int nPROP_EDIT_LABEL = 59;
  public final static int nPROP_REMOVE_COND = 60;
  public final static int nPROP_REMOVE_LABEL = 61;
  public final static int nPROP_FILE_LABEL = 62;
  public final static int nPROP_CURRDATE_IF_EMPTY = 63;
  public final static int nPROP_TEXT_AREA = 64;
  public final static int nPROP_CHART_TEMPLATE = 65;
  public final static int nPROP_CHART_TEMPLATE_CODE = 66; // used only for output/export(not imported)
  public final static int nPROP_CHART_TITLE = 67;
  public final static int nPROP_CHART_DSL = 68;
  public final static int nPROP_CHART_DSV = 69;
  public final static int nPROP_CHART_WIDTH = 70;
  public final static int nPROP_CHART_HEIGHT = 71;
  public final static int nPROP_TABLE_SIZE = 72;
  public final static int nPROP_USE_LINKS = 73;
  public final static int nPROP_PP_RESULT_ARRAY = 74;
  public final static int nPROP_PP_PREFETCH = 75;
  public final static int nPROP_PP_PASS_TO_LINK = 76;
  public final static int nPROP_FILE_UPLOAD_LIMIT = 77;
  public final static int nPROP_FILE_UPLOAD_LABEL = 78;
  public final static int nPROP_FILE_UPLOAD_COND = 79;
  public final static int nPROP_FILE_SCANNER_COND = 80;
  public final static int nPROP_FILE_PERMISSION = 81;
  public final static int nPROP_FILE_SIGNATURE_TYPE = 82;
  public final static int nPROP_FILE_SIGN_NEW = 83;
  public final static int nPROP_FILE_SIGN_EXISTING = 84;
  public final static int nPROP_FILE_ENCRYPT_USERS = 85;
  public final static int nPROP_COL_WIDTH_PERCENT = 86;
  public final static int nPROP_FILE_UPLOAD_RENAME = 87;
  public final static int nPROP_FILE_UPLOAD_EXTENSIONS = 88;
  public final static int nPROP_FILE_UPLOAD_PRESERVE_EXT = 89;
  public final static int nPROP_CACHE_HINT = 90;
  public final static int nPROP_EXPRESSION = 91;
  public final static int nPROP_DISABLE_TABLE_HEADER = 92;
  public final static int nPROP_TEXT_SUBMIT_ON_BLUR = 93;
  public final static int nPROP_DMS_FOLDER = 94;
  public final static int nPROP_DMS_TITLES = 95;
  public final static int nPROP_DMS_VARS = 96;
  public final static int nPROP_DMS_ALIGN = 97;
  public final static int nPROP_HOUR_FORMAT = 98;
  public final static int nPROP_HOUR_FORMAT_ID = 99; // used only for output/export(not imported)
  public final static int nPROP_FILE_SIGN_METHOD = 100;
  public final static int nPROP_FILE_IS_IMAGE = 101;
  public final static int nPROP_FILE_IS_IMAGE_WIDTH = 102;
  public final static int nPROP_FILE_IS_IMAGE_HEIGHT = 103;
  public final static int nPROP_FILE_UPLOAD_VALID_EXTENSIONS = 104;
  public final static int nPROP_BUTTON_TYPE = 105;
  public final static int nPROP_BUTTON_TOOLTIP = 106;
  public final static int nPROP_BUTTON_IMAGE = 107;
  public final static int nPROP_BUTTON_SHOW_CONDITION = 108;
  public final static int nPROP_BUTTON_IGNORE_FORM_PROCESSING = 109;
  public final static int nPROP_BUTTON_IGNORE_FORM_VALIDATION = 110;
  public final static int nPROP_BUTTON_CONFIRM_ACTION = 111;
  public final static int nPROP_BUTTON_CONFIRM_MESSAGE = 112;
  public final static int nPROP_BUTTON_CUSTOM_VARIABLE = 113;
  public final static int nPROP_BUTTON_CUSTOM_VALUE = 114;
  public final static int nPROP_LIST_OF_POPUP_FLOWS = 115;
  public final static int nPROP_POPUP_CALLER_VARIABLE = 116;
  public final static int nPROP_POPUP_VARIABLES = 117;
  public final static int nPROP_FORM_TEMPLATE = 118;
  public final static int nPROP_KEEP_SCROLL_ONLOAD = 119;
  // ...


  // property's restriction types
  public final static int nDATA_TYPE = 0;
  public final static int nPOSITIVE_NUMBER = 1;
  public final static int nALIGNMENT = 2;
  public final static int nDATE_FORMAT = 3;
  public final static int nNOT_CHOSEN = 4;
  public final static int nCHART_TEMPLATES = 5;
  public final static int nPOSITIVE_NUMBER_PERCENT = 6;


  // props's associated coded names (for export)
  // KEY: Integer with prop id
  // VALUE: String with property's code name
  protected static HashMap<Integer,String> _hmPropCodeNames = new HashMap<Integer,String>();

  // props's associated edit labels
  // KEY: Integer with prop id
  // VALUE: String with property's edit label
  protected static HashMap<Integer,String> _hmPropLabels = new HashMap<Integer,String>();



  public final static int nDATA_TYPE_NONE = 0;


  // data type's associated texts
  // KEY: Integer with data type id
  // VALUE: String with data type's text
  protected final static HashMap<Integer,String> _hmDataTypes = new HashMap<Integer,String>();

  // data type's associated class names
  // KEY: Integer with data type id
  // VALUE: String with data class
  protected final static HashMap<Integer,String> _hmDataTypeClasses = new HashMap<Integer,String>();


  public final static String ALIGNMENT_NONE = "";

  // available alignments
  public final static int nALIGNMENT_NONE = 0;
  public final static int nALIGNMENT_LEFT = 1;
  public final static int nALIGNMENT_CENTER = 2;
  public final static int nALIGNMENT_RIGHT = 3;

  // Chart templates
  public final static int nCHART_NONE = 0;
  public final static int nCHART_START_POS = 1;

  // available date formats
  public final static String sDATE_FORMAT_NONE = sCHOOSE;
  public final static String sDATE_FORMAT1 = "dd/MM/yyyy"; //$NON-NLS-1$
  public final static String sDATE_FORMAT2 = "dd/MM/yy"; //$NON-NLS-1$
  public final static String sDATE_FORMAT3 = "yyyy/MM/dd"; //$NON-NLS-1$
  public final static String sDATE_FORMAT4 = "yy/MM/dd"; //$NON-NLS-1$

  // available hour formats
  public static String sHOUR_FORMAT_NONE;
  public final static String sHOUR_FORMAT1 = "HH:mm [0-23]"; //$NON-NLS-1$
  public final static String sHOUR_FORMAT2 = "hh:mm [1-12]"; //$NON-NLS-1$
  public final static String sHOUR_FORMAT3 = "hh:mm a [1-12 am/pm]"; //$NON-NLS-1$

  // alignment's associated texts
  // KEY: Integer with alignment id
  // VALUE: String with alignment's text
  protected final static HashMap<Integer,String> _hmAlignments = new HashMap<Integer,String>();

  // alignment's associated codes
  // KEY: Integer with alignment id
  // VALUE: String with alignment's text
  protected final static HashMap<Integer,String> _hmAlignmentCodes = new HashMap<Integer,String>();

  // Chart type codes texts
  // KEY: Integer with type id
  // VALUE: String with types's text
  protected HashMap<Integer,String> _hmChartTemplates = new HashMap<Integer,String>();

  // Chart type codes
  // KEY: Integer with type id
  // VALUE: String with type's text
  protected HashMap<Integer,String> _hmChartTemplateCodes = new HashMap<Integer,String>();

  // date formats
  protected final static String[] _saDateFormats = {
    sDATE_FORMAT_NONE,
    sDATE_FORMAT1,
    sDATE_FORMAT2,
    sDATE_FORMAT3,
    sDATE_FORMAT4    
  };
  protected final static HashMap<String,String> _hmDateFormatCodes = new HashMap<String,String>();

  // hour formats
  protected static String[] _saHourFormats = {
    sHOUR_FORMAT_NONE,
    sHOUR_FORMAT1,
    sHOUR_FORMAT2,
    sHOUR_FORMAT3
  };
  protected final static HashMap<String,String> _hmHourFormatCodes = new HashMap<String,String>();
  
  // file permissions
  protected final static String[] _saFilePermissions = {
    "public",
    "organization",
    "flow",
    "process",
  };

  protected static String[] _saFilePermissionsDesc;

  protected static final Map<String,String> permissionMap = new HashMap<String, String>();
  protected static final Map<String,String> permissionDescMap = new HashMap<String, String>();

  public static final String MASTER_FLOW_VARIABLES = "master_flow_variables";

  public static final String POPUP_VARIABLES = "popup_variables";

  protected static NameValuePair<String, String>[] _saConnectors;

  protected static String[] _saDataSources = new String[]{""}; //$NON-NLS-1$

  protected static String[] _saFormTemplates = new String[] { "" }; //$NON-NLS-1$

  protected static String[] listOfSuportFlows = new String[]{""};

  private static boolean initComplete = false;
  
  synchronized static void initMaps(FlowEditorAdapter adapter) {
    if(initComplete) return;
    initComplete = true;
    
    sCHOOSE = adapter.getString("JSPFieldData.choose"); //$NON-NLS-1$
    sHOUR_FORMAT_NONE = adapter.getString("JSPFieldData.tootltip." + FormProps.HOUR_FORMAT);
    _saHourFormats[0] = sHOUR_FORMAT_NONE;

    _saFilePermissionsDesc = new String [] {
      adapter.getString("JSPFieldData.proplabel.file_permissions.public"),  //$NON-NLS-1$
      adapter.getString("JSPFieldData.proplabel.file_permissions.organization"),  //$NON-NLS-1$
      adapter.getString("JSPFieldData.proplabel.file_permissions.flow"),  //$NON-NLS-1$
      adapter.getString("JSPFieldData.proplabel.file_permissions.process"),  //$NON-NLS-1$
    };
    
    // PACKAGE NAME
    final String _sPackageName = JSPFieldData.class.getPackage().getName()+".";  //$NON-NLS-1$
    
    // Helper maps
    for(JSPFieldTypeEnum field : JSPFieldTypeEnum.values()) {
      // FIELD TYPES
      _hmFieldTypes.put(field, adapter.getString(field.getDescrKey()));
      
      // FIELD TYPES TOOLTIPS
      _hmFieldTypeTooltips.put(field, adapter.getString(field.getTooltipKey()));

      // FIELD TYPES CLASS NAMES
      _hmFieldTypeClassNames.put(field, _sPackageName + field.getEditorClass());

      // FIELD TYPE CODE NAMES
      _hmFieldTypeCodeNames.put(field, field.getEngineClass());
      _hmFieldCodeNameTypes.put(field.getEngineClass(), field);
      
    }
    


    // PROP CODE NAMES
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_NONE),"na"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FIELD_TYPE),
    "fieldtype"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FIELD_TYPE_CODE),
    "type"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_TEXT),
    "text"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DATA_TYPE),
    "editordatatype"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DATA_TYPE_CLASS),
    "datatype"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_VAR_NAME),
    "variable"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_SIZE),
    "size"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_MAXLENGTH),
    "maxlength"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_TITLE),
    "title"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ALIGNMENT),
    "alignment"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ALIGNMENT_CODE),
    "align"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_COLUMN),
    "column"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ID),
    "ID"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_VALUE),
    "value"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_STYLESHEET),
    "stylesheet"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_COLS),
    "cols"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ROWS),
    "rows"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY),
    "output_only"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_EMPTY_NOT_ALLOWED),
    "empty_not_allowed"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_EXTRA_PROPS),
    "extra_props"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_SERVICE_PRINT),
    "service_print"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_SERVICE_EXPORT),
    "service_export"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CSS_CLASS),
    "css_class"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ONCLICK),
    "onclick"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ONMOUSE_OVER_STATUS),
    "onmouse_over_status"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CONTROL_VAR),
    "control_var"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CONTROL_ON_COND),
    "control_on_cond"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_PROC_LINK),
    "proc_link"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_URL),
    "url"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_VAR_VALUE),
    "var_value"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_OPEN_NEW_WINDOW),
    "open_new_window"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_WINDOW_NAME),
    "window_name"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ONCHANGE_SUBMIT),
    FormProps.sONCHANGE_SUBMIT); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DATE_FORMAT),
    "date_format"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DATE_FORMAT_ID),
    "date_format_id"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DEFAULT_VALUE),
    "default_value"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DEFAULT_TEXT),
    "default_text"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_QUERY),
    "query"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DATASOURCE),
    "datasource"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CLASSE),
    "classe"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ARGS),
    "args"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CONNECTOR_SELECT),
    "connector_select"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_TEXT_VALUE),
    "text_value"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_VALUE_KEY),
    "value_key"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_TEXT_KEY),
    "text_key"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DISABLE_COND),
    "disabled_cond"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD),
    FormProps.sOBLIGATORY_FIELD); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_VALIDATION_EXPR),
    "validation_expr"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_VALIDATION_MSG),
    "validation_msg"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_MACRO_TITLE),
    "macrotitle"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ROW_CONTROL_LIST),
    "rowcontrollist"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_ALT_TEXT),
    "alt_text"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_WIDTH),
    "width"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_HEIGHT),
    "height"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_LINK_COND),
    "link_cond"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_LINK_TEXT),
    "link_text"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_LINK_LABEL),
    "link_label"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_EDIT_COND),
    "edition_cond"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_EDIT_LABEL),
    "edition_label"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_REMOVE_COND),
    "remove_cond"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_REMOVE_LABEL),
    "remove_label"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_LABEL),
    "file_label"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CURRDATE_IF_EMPTY),
    "currdate_ifempty"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_TEXT_AREA),
    "text_area"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CHART_TEMPLATE),
    "chart_template"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CHART_TEMPLATE_CODE),
    "chart_template_code"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CHART_TITLE),
    "chart_title"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CHART_DSV),
    "chart_dsv"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CHART_DSL),
    "chart_dsl"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CHART_WIDTH),
    "chart_width"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CHART_HEIGHT),
    "chart_height"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_TABLE_SIZE),
    "results_per_page"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_USE_LINKS),
    "use_links"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_PP_RESULT_ARRAY),
    "pp_result_array"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_PP_PREFETCH),
    "pp_prefetch"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_PP_PASS_TO_LINK),
    "pp_pass_link"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_LIMIT),
    "file_upload_limit"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_LABEL),
    "file_upload_label"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_COND),
    "file_upload_cond"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_VALID_EXTENSIONS),
    "file_upload_valid_extensions"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_SCANNER_COND),
    "file_scanner_cond"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_PERMISSION),
    "file_permission"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_SIGNATURE_TYPE),
    "file_signature_type"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_SIGN_NEW),
    "file_sign_new"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_SIGN_METHOD),
    "file_sign_method"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_IS_IMAGE),
    "file_is_image"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_IS_IMAGE_WIDTH),
    "file_is_image_width"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_IS_IMAGE_HEIGHT),
    "file_is_image_height"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_SIGN_EXISTING),
    "file_sign_existing"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_ENCRYPT_USERS),
    "file_encrypt_users"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_COL_WIDTH_PERCENT),
    "col_width"); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_RENAME),
        FormProps.FILE_UPLOAD_RENAME); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_EXTENSIONS),
        FormProps.FILE_UPLOAD_EXTENSIONS); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_PRESERVE_EXT),
        FormProps.FILE_UPLOAD_PRESERVE_EXT); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_CACHE_HINT), FormProps.CACHE_HINT);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_EXPRESSION), FormProps.EXPRESSION);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DISABLE_TABLE_HEADER),
        FormProps.DISABLE_TABLE_HEADER);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_TEXT_SUBMIT_ON_BLUR),
        FormProps.TEXT_SUBMIT_ON_BLUR);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DMS_FOLDER),
        FormProps.DMS_FOLDER);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DMS_TITLES),
        FormProps.DMS_TITLES);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DMS_VARS),
        FormProps.DMS_VARS);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_DMS_ALIGN),
        FormProps.DMS_ALIGN);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_HOUR_FORMAT),
        FormProps.HOUR_FORMAT); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_HOUR_FORMAT_ID),
        FormProps.HOUR_FORMAT_ID); //$NON-NLS-1$
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_TYPE), "button_type");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_TOOLTIP), "button_tooltip");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_IMAGE), "button_image");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_SHOW_CONDITION), "button_show_condition");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_IGNORE_FORM_PROCESSING), "button_ignore_from_processing");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_IGNORE_FORM_VALIDATION), "button_ignore_from_validation");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_CONFIRM_ACTION), "button_confirm_action");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_CONFIRM_MESSAGE), "button_confirm_message");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VARIABLE), "button_custom_variable");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VALUE), "button_custom_value");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_LIST_OF_POPUP_FLOWS), "suport_flow");
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_POPUP_CALLER_VARIABLE), MASTER_FLOW_VARIABLES);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_POPUP_VARIABLES), POPUP_VARIABLES);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_FORM_TEMPLATE), FormProps.FORM_TEMPLATE);
    _hmPropCodeNames.put(new Integer(JSPFieldData.nPROP_KEEP_SCROLL_ONLOAD), FormProps.KEEP_SCROLL_ONLOAD);

    // PROP LABELS
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_NONE),adapter.getString("217")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FIELD_TYPE),
        adapter.getString("JSPFieldData.proplabel.field_type")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FIELD_TYPE_CODE),
        adapter.getString("JSPFieldData.proplabel.field_type_code")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_TEXT),
        adapter.getString("JSPFieldData.proplabel.text")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DATA_TYPE),
        adapter.getString("JSPFieldData.proplabel.data_type")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DATA_TYPE_CLASS),
        adapter.getString("JSPFieldData.proplabel.data_type_class")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_VAR_NAME),
        adapter.getString("JSPFieldData.proplabel.var_name")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_SIZE),
        adapter.getString("JSPFieldData.proplabel.size")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_MAXLENGTH),
        adapter.getString("JSPFieldData.proplabel.maxlength")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_TITLE),
        adapter.getString("JSPFieldData.proplabel.title")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ALIGNMENT),
        adapter.getString("JSPFieldData.proplabel.alignement")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ALIGNMENT_CODE),
        adapter.getString("JSPFieldData.proplabel.alignement_code")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_COLUMN),
        adapter.getString("JSPFieldData.proplabel.column")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ID),
        adapter.getString("JSPFieldData.proplabel.id")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_VALUE),
        adapter.getString("JSPFieldData.proplabel.value")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_STYLESHEET),
        adapter.getString("JSPFieldData.proplabel.stylesheet")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_COLS),
        adapter.getString("JSPFieldData.proplabel.cols")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ROWS),
        adapter.getString("JSPFieldData.proplabel.rows")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY),
        adapter.getString("JSPFieldData.proplabel.output_only")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_EMPTY_NOT_ALLOWED),
        adapter.getString("JSPFieldData.proplabel.empty_not_allowed")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_EXTRA_PROPS),
        adapter.getString("JSPFieldData.proplabel.extra_props")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_SERVICE_PRINT),
        adapter.getString("JSPFieldData.proplabel.service_print")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_SERVICE_EXPORT),
        adapter.getString("JSPFieldData.proplabel.service_export")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CSS_CLASS),
        adapter.getString("JSPFieldData.proplabel.css_class")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ONCLICK),
        adapter.getString("JSPFieldData.proplabel.onclick")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ONMOUSE_OVER_STATUS),
        adapter.getString("JSPFieldData.proplabel.onmouse_over_status")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CONTROL_VAR),
        adapter.getString("JSPFieldData.proplabel.control_var")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CONTROL_ON_COND),
        adapter.getString("JSPFieldData.proplabel.control_on_cond")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_PROC_LINK),
        adapter.getString("JSPFieldData.proplabel.proc_link")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_URL),
        adapter.getString("JSPFieldData.proplabel.url")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_VAR_VALUE),
        adapter.getString("JSPFieldData.proplabel.var_value")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_OPEN_NEW_WINDOW),
        adapter.getString("JSPFieldData.proplabel.open_new_window")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_WINDOW_NAME),
        adapter.getString("JSPFieldData.proplabel.window_name")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ONCHANGE_SUBMIT),
        adapter.getString("JSPFieldData.proplabel.onchange_submit")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DATE_FORMAT),
        adapter.getString("JSPFieldData.proplabel.date_format")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DATE_FORMAT_ID),
        adapter.getString("JSPFieldData.proplabel.date_format_id")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DEFAULT_VALUE),
        adapter.getString("JSPFieldData.proplabel.default_value")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DEFAULT_TEXT),
        adapter.getString("JSPFieldData.proplabel.default_text")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_QUERY),
        adapter.getString("JSPFieldData.proplabel.query")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DATASOURCE),
        adapter.getString("JSPFieldData.proplabel.datasource")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CLASSE),
        adapter.getString("JSPFieldData.proplabel.classe")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ARGS),
        adapter.getString("JSPFieldData.proplabel.args")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CONNECTOR_SELECT),
        adapter.getString("JSPFieldData.proplabel.connector_select")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_TEXT_VALUE),
        adapter.getString("JSPFieldData.proplabel.text_value")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_VALUE_KEY),
        adapter.getString("JSPFieldData.proplabel.value_key")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_TEXT_KEY),
        adapter.getString("JSPFieldData.proplabel.text_key")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DISABLE_COND),
        adapter.getString("JSPFieldData.proplabel.disable_cond")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD),
        adapter.getString("JSPFieldData.proplabel.obligatory_field")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_VALIDATION_EXPR),
        adapter.getString("JSPFieldData.proplabel.validation_expr")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_VALIDATION_MSG),
        adapter.getString("JSPFieldData.proplabel.validation_msg")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_MACRO_TITLE),
        adapter.getString("JSPFieldData.proplabel.macro_title")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ROW_CONTROL_LIST),
        adapter.getString("JSPFieldData.proplabel.row_control_list")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_ALT_TEXT),
        adapter.getString("JSPFieldData.proplabel.alt_text")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_WIDTH),
        adapter.getString("JSPFieldData.proplabel.prop_width")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_HEIGHT),
        adapter.getString("JSPFieldData.proplabel.prop_height")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_LINK_COND),
        adapter.getString("JSPFieldData.proplabel.link_cond")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_LINK_TEXT),
        adapter.getString("JSPFieldData.proplabel.link_text")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_LINK_LABEL),
        adapter.getString("JSPFieldData.proplabel.link_label")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_EDIT_COND),
        adapter.getString("JSPFieldData.proplabel.edit_cond")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_EDIT_LABEL),
        adapter.getString("JSPFieldData.proplabel.edit_label")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_REMOVE_COND),
        adapter.getString("JSPFieldData.proplabel.remove_cond")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_REMOVE_LABEL),
        adapter.getString("JSPFieldData.proplabel.remove_label")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_LABEL),
        adapter.getString("JSPFieldData.proplabel.file_label")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CURRDATE_IF_EMPTY),
        adapter.getString("JSPFieldData.proplabel.currdate_if_empty")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_TEXT_AREA),
    ""); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CHART_TEMPLATE),
        adapter.getString("JSPFieldData.proplabel.chart_template")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CHART_TEMPLATE_CODE),
        adapter.getString("JSPFieldData.proplabel.chart_template_code")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CHART_TITLE),
        adapter.getString("JSPFieldData.proplabel.chart_title")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CHART_DSL),
        adapter.getString("JSPFieldData.proplabel.chart_dsl")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CHART_DSV),
        adapter.getString("JSPFieldData.proplabel.chart_dsv")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CHART_WIDTH),
        adapter.getString("JSPFieldData.proplabel.chart_width")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CHART_HEIGHT),
        adapter.getString("JSPFieldData.proplabel.chart_height")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_TABLE_SIZE),
        adapter.getString("JSPFieldData.proplabel.table_size")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_USE_LINKS),
        adapter.getString("JSPFieldData.proplabel.use_links")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_PP_RESULT_ARRAY),
        adapter.getString("JSPFieldData.proplabel.pp_result_array")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_PP_PREFETCH),
        adapter.getString("JSPFieldData.proplabel.pp_prefetch")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_PP_PASS_TO_LINK),
        adapter.getString("JSPFieldData.proplabel.pp_pass_to_link")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_LIMIT),
        adapter.getString("JSPFieldData.proplabel.file_upload_limit")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_LABEL),
        adapter.getString("JSPFieldData.proplabel.file_upload_label")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_COND),
        adapter.getString("JSPFieldData.proplabel.file_upload_cond")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_VALID_EXTENSIONS),
        adapter.getString("JSPFieldData.proplabel.file_valid_extensions"));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_SCANNER_COND),
        adapter.getString("JSPFieldData.proplabel.file_scanner_cond")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_PERMISSION),
        adapter.getString("JSPFieldData.proplabel.file_permission")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_FILE_SIGNATURE_TYPE),
        adapter.getString("JSPFieldData.proplabel.signature_type")); // $NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_FILE_SIGN_NEW),
        adapter.getString("JSPFieldData.proplabel.file_sign_new")); // $NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_FILE_SIGN_METHOD),
            adapter.getString("JSPFieldData.proplabel.file_sign_method")); // $NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_FILE_IS_IMAGE),
        adapter.getString("JSPFieldData.proplabel.file_is_image")); // $NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_FILE_IS_IMAGE_WIDTH),
        adapter.getString("JSPFieldData.proplabel.file_is_image.width")); // $NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_FILE_IS_IMAGE_HEIGHT),
        adapter.getString("JSPFieldData.proplabel.file_is_image.height")); // $NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_FILE_SIGN_EXISTING),
        adapter.getString("JSPFieldData.proplabel.file_sign_existing")); // $NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_FILE_ENCRYPT_USERS),
        adapter.getString("JSPFieldData.proplabel.file_encrypt_users")); // $NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_COL_WIDTH_PERCENT),
        adapter.getString("JSPFieldData.proplabel.col_width")); // $NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_RENAME),
        adapter.getString("JSPFieldData.proplabel." + FormProps.FILE_UPLOAD_RENAME)); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_EXTENSIONS),
        adapter.getString("JSPFieldData.proplabel." + FormProps.FILE_UPLOAD_EXTENSIONS)); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_PRESERVE_EXT),
        adapter.getString("JSPFieldData.proplabel." + FormProps.FILE_UPLOAD_PRESERVE_EXT)); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_CACHE_HINT),
        adapter.getString("JSPFieldData.proplabel.cache_hint")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_EXPRESSION),
        adapter.getString("JSPFieldData.proplabel.expression")); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DISABLE_TABLE_HEADER),
        adapter.getString("JSPFieldData.proplabel." + FormProps.DISABLE_TABLE_HEADER));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_TEXT_SUBMIT_ON_BLUR),
        adapter.getString("JSPFieldData.proplabel." + FormProps.TEXT_SUBMIT_ON_BLUR));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DMS_FOLDER),
        adapter.getString("JSPFieldData.proplabel." + FormProps.DMS_FOLDER));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DMS_TITLES),
        adapter.getString("JSPFieldData.proplabel." + FormProps.DMS_TITLES));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DMS_VARS),
        adapter.getString("JSPFieldData.proplabel." + FormProps.DMS_VARS));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_DMS_ALIGN),
        adapter.getString("JSPFieldData.proplabel." + FormProps.DMS_ALIGN));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_HOUR_FORMAT),
        adapter.getString("JSPFieldData.proplabel." + FormProps.HOUR_FORMAT)); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_HOUR_FORMAT_ID),
        adapter.getString("JSPFieldData.proplabel." + FormProps.HOUR_FORMAT_ID)); //$NON-NLS-1$
    _hmPropLabels.put(new Integer(nPROP_BUTTON_TYPE), 
        adapter.getString("JSPFieldData.proplabel.button_type"));
    _hmPropLabels.put(new Integer(nPROP_BUTTON_TOOLTIP), 
        adapter.getString("JSPFieldData.proplabel.button_tooltip"));
    _hmPropLabels.put(new Integer(nPROP_BUTTON_IMAGE), 
        adapter.getString("JSPFieldData.proplabel.button_image"));
    _hmPropLabels.put(new Integer(nPROP_BUTTON_SHOW_CONDITION), 
        adapter.getString("JSPFieldData.proplabel.button_show_condition"));
    _hmPropLabels.put(new Integer(nPROP_BUTTON_IGNORE_FORM_PROCESSING), 
        adapter.getString("JSPFieldData.proplabel.button_ignore_form_processing"));
    _hmPropLabels.put(new Integer(nPROP_BUTTON_IGNORE_FORM_VALIDATION), 
        adapter.getString("JSPFieldData.proplabel.button_ignore_form_validation"));
    _hmPropLabels.put(new Integer(nPROP_BUTTON_CONFIRM_ACTION), 
        adapter.getString("JSPFieldData.proplabel.button_confirm_action"));
    _hmPropLabels.put(new Integer(nPROP_BUTTON_CONFIRM_MESSAGE), 
        adapter.getString("JSPFieldData.proplabel.button_confirm_message"));
    _hmPropLabels.put(new Integer(nPROP_BUTTON_CUSTOM_VARIABLE),
        adapter.getString("JSPFieldData.proplabel.button_variable"));
    _hmPropLabels.put(new Integer(nPROP_BUTTON_CUSTOM_VALUE),
        adapter.getString("JSPFieldData.proplabel.button_value"));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_LIST_OF_POPUP_FLOWS), 
        adapter.getString("JSPFieldData.proplabel.popup_flows"));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_POPUP_CALLER_VARIABLE),
        adapter.getString("JSPFieldData.proplabel.popup.caller_variable"));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_POPUP_VARIABLES),
        adapter.getString("JSPFieldData.proplabel.popup.popup_flow_variables"));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_FORM_TEMPLATE), adapter.getString("JSPFieldData.proplabel."
        + FormProps.FORM_TEMPLATE));
    _hmPropLabels.put(new Integer(JSPFieldData.nPROP_KEEP_SCROLL_ONLOAD), adapter.getString("JSPFieldData.proplabel."
		+ FormProps.KEEP_SCROLL_ONLOAD));

    // DATA TYPES
    _hmDataTypes.put(new Integer(JSPFieldData.nDATA_TYPE_NONE),
        JSPFieldData.sCHOOSE);
    _hmDataTypeClasses.put(new Integer(JSPFieldData.nDATA_TYPE_NONE),
        null);
    String[] saClasses = pt.iflow.api.datatypes.Manifest.getClasses();
    Integer iKey = null;
    String sValue = null;
    pt.iflow.api.datatypes.DataTypeInterface dti = null;
    for (int i=0; i < saClasses.length; i++) {
      try {
        dti = loadDataType(adapter, saClasses[i]);
        iKey = new Integer(dti.getID());
        sValue = dti.getDescription();
        _hmDataTypes.put(iKey,sValue);
        _hmDataTypeClasses.put(iKey,saClasses[i]);
      }
      catch (Exception e) {
        adapter.log("JSPFieldData: static: caught exception: ",e);  //$NON-NLS-1$
      }
    }

    // ALIGNMENTS
    _hmAlignments.put(new Integer(JSPFieldData.nALIGNMENT_NONE),
        JSPFieldData.sCHOOSE);
    _hmAlignments.put(new Integer(JSPFieldData.nALIGNMENT_LEFT),
        adapter.getString("JSPFieldData.alignments.left")); //$NON-NLS-1$
    _hmAlignments.put(new Integer(JSPFieldData.nALIGNMENT_CENTER),
        adapter.getString("JSPFieldData.alignments.center")); //$NON-NLS-1$
    _hmAlignments.put(new Integer(JSPFieldData.nALIGNMENT_RIGHT),
        adapter.getString("JSPFieldData.alignments.right")); //$NON-NLS-1$

    _hmAlignmentCodes.put(new Integer(JSPFieldData.nALIGNMENT_NONE),
        ALIGNMENT_NONE); //$NON-NLS-1$
    _hmAlignmentCodes.put(new Integer(JSPFieldData.nALIGNMENT_LEFT),
    "left"); //$NON-NLS-1$
    _hmAlignmentCodes.put(new Integer(JSPFieldData.nALIGNMENT_CENTER),
    "center"); //$NON-NLS-1$
    _hmAlignmentCodes.put(new Integer(JSPFieldData.nALIGNMENT_RIGHT),
    "right"); //$NON-NLS-1$


    // DATE FORMATS
    for (int i=0; i < _saDateFormats.length; i++) {
      _hmDateFormatCodes.put(_saDateFormats[i] , String.valueOf(i));
    }
    
    // HOUR FORMATS
    for (int i = 0; i < _saHourFormats.length; i++) {
        _hmHourFormatCodes.put(_saHourFormats[i] , String.valueOf(i));
    }

    // Permissions
    for(int i = 0; i < _saFilePermissions.length; i++) {
      permissionMap.put(_saFilePermissions[i], _saFilePermissionsDesc[i]);
      permissionDescMap.put(_saFilePermissionsDesc[i], _saFilePermissions[i]);
    }

    // ...
    String [] dataSources = JSPFieldData._saDataSources;
    if(adapter.isRepOn()) {
      String [] repVals = adapter.getRepository().listDataSources();
      if(null == repVals) repVals = new String[0];
      dataSources = new String[repVals.length+1];
      dataSources[0]="";
      for(int kk = 0; kk < repVals.length; kk++)
        dataSources[kk+1] = "\""+repVals[kk]+"\"";
    }
    JSPFieldData._saDataSources = dataSources;

    String [] auxiliarListOfSuportFlows = JSPFieldData.listOfSuportFlows;
    if(adapter.isRepOn()) {
      String [] repositoryListOfSuportFlows = adapter.getRepository().listSubFlows();
      if(null == repositoryListOfSuportFlows) repositoryListOfSuportFlows = new String[0];
      auxiliarListOfSuportFlows = new String[repositoryListOfSuportFlows.length+1];
      auxiliarListOfSuportFlows[0]=adapter.getString("JSPFieldData.form_popup_field.choose");
      for(int idx = 0; idx < repositoryListOfSuportFlows.length; idx++){
        auxiliarListOfSuportFlows[idx+1] = repositoryListOfSuportFlows[idx];
      }
    }
    JSPFieldData.listOfSuportFlows = auxiliarListOfSuportFlows;

  }

  @SuppressWarnings("unchecked")
  private static void initConnectors(FlowEditorAdapter adapter) {
    
    // FIXME criar object cache para flow editor.
    // colocar la os connectors (e outras listas estaticas que venham do repositorio)
    // qd se faz switch workspace, limpar a cache, para garantir o carregamento ao ligar a outro repositorio
    
    List<NameValuePair<String, String>> connectorList = new ArrayList<NameValuePair<String,String>>();
    connectorList.add(new NameValuePair<String, String>(sCHOOSE));
    try {
      NameValuePair<String, String>[] connectors = ConnectorMarshaller.unmarshal(adapter.getRepository().getConnectors());
      for (NameValuePair<String, String> connector : connectors) {
        connectorList.add(new ConnectorNameValuePair<String, String>(connector));
      }
    } catch (Exception e) {
      adapter.log("JSPFieldData#initConnectors: Caught exception: ", e);
    }
    _saConnectors = connectorList.toArray(new NameValuePair[connectorList.size()]);
  }

  // the default text size
  public final static int nDEFAULT_TEXT_SIZE = 15;


  // flag that indicates if the field is new
  protected boolean _bNew;

  // the field id
  protected int _nID = -1;

  // the field position in field list
  protected int _nPosition = -1;

  // the field type identifier
  protected JSPFieldTypeEnum _nFieldType = JSPFieldTypeEnum.FIELD_TYPE_NONE;

  // the edit/create panel
  protected JPanel _editPanel = null;

  // an HashMap of field data properties
  // KEY: Integer with prop's id
  // VALUE: String with the prop's value
  protected HashMap<Integer,String> _hmProps = null;

  // an HashMap of edit components for field data
  // KEY: Integer with prop's id
  // VALUE: Jcomponent
  protected HashMap<Integer,JComponent> _hmEditComponents = null;

  // a list of HashMap 2D field data properties
  // HashMap: KEY: Integer with prop's id
  //          VALUE: String with the prop's value
  protected ArrayList<HashMap<Integer,String>> _alPropsList = null;

  // a list of HashMap edit components for 2D field data (e.g. TableField)
  // HashMap: KEY: Integer with prop's id
  //          VALUE: Jcomponent
  protected ArrayList<HashMap<Integer,JComponent>> _alEditComponentsList = null;

  // the ordered displayable table properties ids
  protected List<Integer> _alTableProps = null;

  // the static property's id list
  // this should have all the static/unchanged/constant/non-editable
  // field's properties...must NOT contain editable properties.
  // only used to export static data purposes...
  protected HashMap<Integer,String> _hmStaticProps = new HashMap<Integer, String>();

  // the ordered displayable single edit properties ids
  // this should have all the single field's properties...
  protected ArrayList<Integer> _alEditSingleProps = null;

  // the ordered displayable edit multiple properties ids
  // this should have all the multiple field's properties...
  protected ArrayList<Integer> _alEditMultipleProps = null;

  // the required properties
  protected ArrayList<Integer> _alRequiredProps = null;

  // property's dependencies
  // KEY: Integer with prop's id
  // VALUE: PropDependency with dependencies
  protected HashMap<Integer,PropDependency> _hmPropDependencies = null;

  // the properties class types for non-string properties
  // KEY: Integer with prop's id
  // VALUE: Integer with prop's restriction type
  protected HashMap<Integer,Integer> _hmPropTypes = null;

  // the list of data types to disable for this field
  // VALUE: Integer with data type id
  protected HashSet<Integer> _hsDisableDataTypes = null;

  protected int nTEXT_SIZE = JSPFieldData.nDEFAULT_TEXT_SIZE;

  private String[] _saFileSignatureType = null;
  private String[] _saFileSignatureTypeDesc = null;
  private Map<String,String> signatureTypeMap = new HashMap<String, String>();
  private Map<String,String> signatureTypeDescMap = new HashMap<String, String>();

  private String[] _saButtonType = null;
  private String[] _saButtonTypeDesc = null;
  private Map<String,String> buttonTypeMap = new HashMap<String, String>();
  private Map<String,String> buttonTypeDescMap = new HashMap<String, String>();
  
  protected JDialog _parent = null;

  protected transient FlowEditorAdapter adapter;
  
  public JSPFieldData(FlowEditorAdapter adapter) {
    initMaps(adapter);
    this.adapter = adapter;
    this.doInit();
  }

  public JSPFieldData(FlowEditorAdapter adapter, int anID) {
    this(adapter);
    this.setID(anID);
  }

  // used to import field data
  public JSPFieldData(FlowEditorAdapter adapter, int anPosition,
      List<String> aalNames,
      List<String> aalValues,
      List<Integer> aalTableProps) {
    this(adapter);

    this.setPosition(anPosition);
    this.setTableProps(aalTableProps);

    String sName = null;
    String sVal = null;
    String stmp = null;
    int row = 0;
    int idx = -1;
    int propid = 0;
    for (int i=0; i < aalNames.size(); i++) {

      sName = aalNames.get(i);
      sVal = aalValues.get(i);

      idx = sName.indexOf(JSPFieldData.sROW
          + JSPFieldData.sJUNCTION);
      if (idx > -1) {
        idx += JSPFieldData.sROW.length()
        + JSPFieldData.sJUNCTION.length();
        stmp = sName.substring(idx,
            sName.indexOf(JSPFieldData.sJUNCTION,idx));
        row = Integer.parseInt(stmp);
        idx += stmp.length()
        + JSPFieldData.sJUNCTION.length();

        sName = sName.substring(idx);
      }
      else {
        row = -1;
      }

      propid = JSPFieldData.getPropCodeNameID(sName);

      switch (propid) {
      case JSPFieldData.nPROP_ID:
        this.setID(Integer.parseInt(sVal));
        break;
      case JSPFieldData.nPROP_FIELD_TYPE:
        // Do nothing. Right now field type is fetched from nPROP_FIELD_TYPE_CODE
        this._nFieldType = JSPFieldData.getFieldTypeID(sVal);
        break;
      case JSPFieldData.nPROP_DATA_TYPE:
        // ignore this and set with data type class.
        // this.setProperty(row,propid,sVal);
        break;
        //      case JSPFieldData.nPROP_ALIGNMENT:  // default action
        //        this.setProperty(row,propid,sVal);
        //        break;
        //      case JSPFieldData.nPROP_CHART_TEMPLATE:
        //        this.setProperty(row,propid,sVal);
        //        break;
      case JSPFieldData.nPROP_FIELD_TYPE_CODE:
        this._nFieldType = JSPFieldData.getFieldTypeID(sVal);
        break;
      case JSPFieldData.nPROP_DATA_TYPE_CLASS:
        this.setProperty(row, JSPFieldData.nPROP_DATA_TYPE, getDataTypeName(sVal));
        break;
      default:
        this.setProperty(row,propid,sVal);
      }
    } // for
  }


  /**
   * Copy constructor
   */
  public JSPFieldData(JSPFieldData afd) {
    this(afd.adapter, afd.getID());
    this.setPosition(afd.getPosition());
    this._nFieldType = afd.getFieldType();

    this.setParent(afd._parent);

    HashMap<Integer,String> hmTmp = null;
    Iterator<Integer> iter = null;
    Integer iKey = null;
    String sVal = null;

    // single props
    this._hmProps = new HashMap<Integer, String>();
    this._hmEditComponents = new HashMap<Integer, JComponent>();
    iter = afd._hmProps.keySet().iterator();
    while (iter.hasNext()) {
      iKey = iter.next();
      sVal = afd.getProperty(iKey);
      if (iKey == nPROP_BUTTON_TYPE && StringUtils.isNotEmpty(sVal) && getButtonTypeMap().get(sVal) != null)
        sVal = getButtonTypeMap().get(sVal);
      else if (iKey == nPROP_FILE_SIGNATURE_TYPE && StringUtils.isNotEmpty(sVal) && getFileSignatureTypeDescMap().get(sVal) != null)
        sVal = getFileSignatureTypeDescMap().get(sVal);

      this.setProperty(iKey, sVal);
    }

    // multiple props
    this._alPropsList = new ArrayList<HashMap<Integer,String>>();
    this._alEditComponentsList = new ArrayList<HashMap<Integer,JComponent>>();
    for (int i=0; i < afd._alPropsList.size(); i++) {
      hmTmp = afd._alPropsList.get(i);
      iter = hmTmp.keySet().iterator();
      while (iter.hasNext()) {
        iKey = iter.next();
        sVal = afd.getProperty(new Integer(i), iKey);
        this.setProperty(new Integer(i), iKey, sVal);
      }
    }

    hmTmp = null;
    for (int i=0; i < afd._alTableProps.size(); i++) {
      if(!this._alTableProps.contains(afd._alTableProps.get(i)))
        this._alTableProps.add(afd._alTableProps.get(i));
    }
    
    for (int i=0; i < afd._alEditSingleProps.size(); i++) {
      if(!this._alEditSingleProps.contains(afd._alEditSingleProps.get(i)))
        this._alEditSingleProps.add(afd._alEditSingleProps.get(i));
    }

    for (int i=0; i < afd._alEditMultipleProps.size(); i++) {
      if(!this._alEditMultipleProps.contains(afd._alEditMultipleProps.get(i)))
        this._alEditMultipleProps.add(afd._alEditMultipleProps.get(i));
    }

    for (int i=0; i < afd._alRequiredProps.size(); i++) {
      if(!this._alRequiredProps.contains(afd._alRequiredProps.get(i)))
        this._alRequiredProps.add(afd._alRequiredProps.get(i));
    }

    this._hmPropDependencies.putAll(afd._hmPropDependencies);// = new HashMap<Integer, PropDependency>(afd._hmPropDependencies);

    this._hmPropTypes.putAll(afd._hmPropTypes);//  = new HashMap<Integer, Integer>(afd._hmPropTypes);

    this._hsDisableDataTypes.addAll(afd._hsDisableDataTypes);//  = new HashSet<Integer>(afd._hsDisableDataTypes);

  }

  public NameValuePair<String, String>[] getConnectors() {
    if (_saConnectors == null)
      initConnectors(adapter);
    
    return _saConnectors;
  }
  

  private void doInit() {
    this._hmProps = new HashMap<Integer, String>();
    this._hmEditComponents = new HashMap<Integer, JComponent>();
    this._alPropsList = new ArrayList<HashMap<Integer,String>>();
    this._alPropsList.add(new HashMap<Integer, String>());
    this._alEditComponentsList = new ArrayList<HashMap<Integer,JComponent>>();
    this._alEditComponentsList.add(new HashMap<Integer, JComponent>());
    this._alTableProps = new ArrayList<Integer>();
    this._alEditSingleProps = new ArrayList<Integer>();
    this._alEditMultipleProps = new ArrayList<Integer>();
    this._alRequiredProps = new ArrayList<Integer>();
    this._hmPropDependencies = new HashMap<Integer, PropDependency>();
    this._hmPropTypes = new HashMap<Integer, Integer>();
    this._hsDisableDataTypes = new HashSet<Integer>();

    if (this._nID >= 0) {
      this._bNew = false;
    }
    else {
      this._bNew = true;
    }

    this._hmChartTemplateCodes = new HashMap<Integer, String>();
    this._hmChartTemplates = new HashMap<Integer, String>();
    init();
  }

  
  protected void init() {
  }


  /**
   * Basic data copy: copies id, position and table props from existing object.
   */
  public void init(JSPFieldData afdData) {
    this.setID(afdData.getID());
    this.setPosition(afdData.getPosition());
    this.setTableProps(afdData.getTableProps());
    this.setParent(afdData._parent);
  }


  public int setID(int anID) {
    if (anID >= 0) {
      this._nID = anID;
      this._bNew = false;
    }
    return this.getID();
  }

  public int getID() {
    return this._nID;
  }

  public int setPosition(int anPosition) {
    this._nPosition = anPosition;
    return this.getPosition();
  }

  public int getPosition() {
    return this._nPosition;
  }

  public JSPFieldTypeEnum getFieldType() {
    return this._nFieldType;
  }

  public boolean isNew() {
    return this._bNew;
  }


  public void setParent(JDialog aParent) {
    this._parent = aParent;
  }

  private void revalidateParent() {
    if (this._parent != null) {
      this._parent.validate();
    }
  }


  // set static property
  public void setStaticProperty(int anPropID, String asValue) {
    this.setStaticProperty(new Integer(anPropID),asValue);
  }

  // set static property
  public void setStaticProperty(Integer aiPropID, String asValue) {
    this._hmStaticProps.put(aiPropID,asValue);
  }


  // set property for single field data objects (e.g. text fields)
  public void setProperty(int anPropID, String asValue) {
    this.setProperty(new Integer(anPropID),asValue);
  }

  // set property for single field data objects (e.g. text fields)
  public void setProperty(Integer aiPropID,String asValue) {
    this.setProperty(null,aiPropID,asValue);
  }


  // set property for multiple field data objects (e.g. table fields)
  public void setProperty(int anListIndex, int anPropID, String asValue) {
    this.setProperty(new Integer(anListIndex),new Integer(anPropID),asValue);
  }

  // set property for multiple field data objects (e.g. table fields)
  public void setProperty(Integer aiListIndex,
      Integer aiPropID,
      String asValue) {

    HashMap<Integer,String> hmProps = null;

    if (aiListIndex == null || aiListIndex.intValue() < 0) {
      hmProps = this._hmProps;
    }
    else {
      if (this._alPropsList.isEmpty() ||
          this._alPropsList.size() <= aiListIndex.intValue()) {
        // initialize lists
        for (int i=this._alPropsList.size(); i<=aiListIndex.intValue(); i++) {
          addListProp();
        }
      }
      hmProps = this._alPropsList.get(aiListIndex);
    }

    hmProps.put(aiPropID,asValue);

    JComponent ec = this.getEditComponent(aiListIndex,aiPropID);

    switch (aiPropID.intValue()) {
    case JSPFieldData.nPROP_CLASSE:
      break;
    case JSPFieldData.nPROP_CONNECTOR_SELECT:
      if (ec != null) {
        ((JComboBox)ec).setSelectedItem(getConnectorItem(asValue));
      }
      break;
    case JSPFieldData.nPROP_DATA_TYPE:
    case JSPFieldData.nPROP_ALIGNMENT:
    case JSPFieldData.nPROP_CHART_TEMPLATE:
    case JSPFieldData.nPROP_DATE_FORMAT:
    case JSPFieldData.nPROP_HOUR_FORMAT:
    case JSPFieldData.nPROP_FILE_PERMISSION:
    case JSPFieldData.nPROP_FILE_SIGNATURE_TYPE:
    case JSPFieldData.nPROP_BUTTON_TYPE:
    case JSPFieldData.nPROP_DATASOURCE:
    case JSPFieldData.nPROP_LIST_OF_POPUP_FLOWS:
      if (ec != null) {
        ((JComboBox)ec).setSelectedItem(asValue);
      }
      break;
    case JSPFieldData.nPROP_PP_PASS_TO_LINK:
    case JSPFieldData.nPROP_USE_LINKS:
    case JSPFieldData.nPROP_OUTPUT_ONLY:
    case JSPFieldData.nPROP_EMPTY_NOT_ALLOWED:
    case JSPFieldData.nPROP_SERVICE_PRINT:
    case JSPFieldData.nPROP_SERVICE_EXPORT:
    case JSPFieldData.nPROP_OPEN_NEW_WINDOW:
    case JSPFieldData.nPROP_PROC_LINK:
    case JSPFieldData.nPROP_ONCHANGE_SUBMIT:
    case JSPFieldData.nPROP_TEXT_VALUE:
    case JSPFieldData.nPROP_CURRDATE_IF_EMPTY:
    case JSPFieldData.nPROP_FILE_SIGN_NEW:
    case JSPFieldData.nPROP_FILE_SIGN_METHOD:
    case JSPFieldData.nPROP_FILE_SIGN_EXISTING:
    case JSPFieldData.nPROP_FILE_UPLOAD_PRESERVE_EXT:
    case JSPFieldData.nPROP_CACHE_HINT:
    case JSPFieldData.nPROP_FILE_IS_IMAGE:
    case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_PROCESSING:
    case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_VALIDATION:
    case JSPFieldData.nPROP_KEEP_SCROLL_ONLOAD:
    case JSPFieldData.nPROP_BUTTON_CONFIRM_ACTION:
      if (ec != null) {
        ((JCheckBox)ec).setSelected((new Boolean(asValue)).booleanValue());
      }      
      break;
    case JSPFieldData.nPROP_TEXT_AREA:
      if (ec != null) {
        ((JTextArea)ec).setText(asValue);
      }
      break;
    case JSPFieldData.nPROP_FORM_TEMPLATE:
      if (ec != null) {
        ((JComboBox) ec).setSelectedItem(asValue);
      }
      break;
    case JSPFieldData.nPROP_VAR_NAME:
      if (ec != null) {
        ((JComboBox) ec).setSelectedItem(asValue);
      }
      break;
    default:
      // DEFAULT IS TEXT...
      if (ec != null) {
        ((JTextField)ec).setText(asValue);
      }
    } // switch

  } // setProperty

  // get single property
  public String getProperty(Integer aiPropID) {
    return this.getProperty(null, aiPropID);
  }

  // get multiple property
  public String getProperty(Integer aiListIndex, Integer aiPropID) {
    String ret = null;

    HashMap<Integer,String> hmProps = null;

    if (aiListIndex == null || aiListIndex.intValue() < 0) {
      hmProps = this._hmProps;
    }
    else {
      hmProps = this._alPropsList.get(aiListIndex);
    }

    if (hmProps.containsKey(aiPropID)) {
      ret = (String)hmProps.get(aiPropID);
    }

    return ret;
  } // getProperty

  private void addListProp() {
    this._alPropsList.add(new HashMap<Integer, String>());
    this._alEditComponentsList.add(new HashMap<Integer, JComponent>());
  }

  private void removeListProp() {
    this._alPropsList.remove(this._alPropsList.size()-1);
    this._alEditComponentsList.remove(this._alEditComponentsList.size()-1);

    if (this._alPropsList.size() == 0
        && this._alEditComponentsList.size() == 0) {
      this.addListProp();
    }
  }

  public void addTableProp(int anProp) {
    this._alTableProps.add(anProp);
  }

  public void setTableProps(List<Integer> alProps) {
    this._alTableProps = alProps;
  }

  public List<Integer> getTableProps() {
    return this._alTableProps;
  }

  public static String getPropCodeName(int anPropID) {
    return JSPFieldData.getPropCodeName(new Integer(anPropID));
  }

  public static String getPropCodeName(Integer aiPropID) {
    String ret = null;
    if (JSPFieldData._hmPropCodeNames.containsKey(aiPropID)){
      ret = (String)JSPFieldData._hmPropCodeNames.get(aiPropID);
    }
    return ret;
  }

  public static int getPropCodeNameID(String asPropCodeName) {
    if(null == asPropCodeName) return 0;
    if (JSPFieldData._hmPropCodeNames.containsValue(asPropCodeName)){
      for(Entry<Integer, String> entry : _hmPropCodeNames.entrySet()) {
        if(asPropCodeName.equals(entry.getValue())) return entry.getKey();
      }
    }
    return 0;
  }

  public static String getPropLabel(int anPropID) {
    return JSPFieldData.getPropLabel(new Integer(anPropID));
  }

  public static String getPropLabel(Integer aiPropID) {
    String ret = null;
    if (JSPFieldData._hmPropLabels.containsKey(aiPropID)){
      ret = (String)JSPFieldData._hmPropLabels.get(aiPropID);
    }
    return ret;
  }

  public static String getDataTypeName(String className) {
    String result = "";
    try {
      Class<?> c = Class.forName(className);
      result = ((DataTypeInterface)c.newInstance()).getDescription();
    } catch (Throwable t) {
    }
    return result;
  }

  public static String getDataTypeText(int anDataTypeID) {
    return JSPFieldData.getDataTypeText(new Integer(anDataTypeID));
  }

  public static String getDataTypeText(Integer aiDataTypeID) {
    String ret = null;
    if (JSPFieldData._hmDataTypes.containsKey(aiDataTypeID)){
      ret = (String)JSPFieldData._hmDataTypes.get(aiDataTypeID);
    }
    return ret;
  }

  public static String getDataTypeClass(int anDataTypeID) {
    return JSPFieldData.getDataTypeClass(new Integer(anDataTypeID));
  }

  public static String getDataTypeClass(Integer aiDataTypeID) {
    String ret = null;
    if (JSPFieldData._hmDataTypeClasses.containsKey(aiDataTypeID)){
      ret = (String)JSPFieldData._hmDataTypeClasses.get(aiDataTypeID);
    }
    return ret;
  }

  public static int getDataTypeID(String asDataTypeText) {
    if(null == asDataTypeText) return 0;
    if (JSPFieldData._hmDataTypes.containsValue(asDataTypeText)){
      for(Entry<Integer, String> entry : _hmDataTypes.entrySet()) {
        if(asDataTypeText.equals(entry.getValue())) return entry.getKey();
      }
    }
    return 0;
  }

  public static String getAlignmentText(int anAlignmentID) {
    return JSPFieldData.getAlignmentText(new Integer(anAlignmentID));
  }

  public static String getAlignmentText(Integer aiAlignmentID) {
    String ret = null;
    if (JSPFieldData._hmAlignments.containsKey(aiAlignmentID)){
      ret = (String)JSPFieldData._hmAlignments.get(aiAlignmentID);
    }
    return ret;
  }

  public static int getAlignmentID(String asAlignmentText) {
    if(null == asAlignmentText) return 0;
    if (JSPFieldData._hmAlignments.containsValue(asAlignmentText)){
      for(Entry<Integer, String> entry : _hmAlignments.entrySet()) {
        if(asAlignmentText.equals(entry.getValue())) return entry.getKey();
      }
    }
    return 0;
  }

  public static Integer getAlignmentIdFromCode(String asAlignmentCode) {
    if(null == asAlignmentCode) return 0;
    if (_hmAlignmentCodes.containsValue(asAlignmentCode)){
      for(Entry<Integer, String> entry : _hmAlignmentCodes.entrySet()) {
        if(asAlignmentCode.equals(entry.getValue())) return entry.getKey();
      }
    }
    return new Integer(0);
  }

  public static String getAlignmentCode(Integer aiAlignmentID) {
    String ret = null;
    if (_hmAlignmentCodes.containsKey(aiAlignmentID)){
      ret = (String)_hmAlignmentCodes.get(aiAlignmentID);
    }
    return ret;
  }

  protected synchronized void loadSignatureTypes() {
    if(null != _saFileSignatureTypeDesc) return;
    
    signatureTypeMap.clear();
    signatureTypeDescMap.clear();
    
    RepositoryClient repos = adapter.getRepository();
    if(null != repos) {
      String [] types = repos.listSignatureTypes();
      
      List<String> alTypes = new ArrayList<String>();
      List<String> alDescs = new ArrayList<String>();

      for(String type : types) {
        if(StringUtils.isBlank(type)) continue;
        type = type.trim();
        String desc = adapter.getString("JSPFieldData.proplabel.signature_type."+type);
        alTypes.add(type);
        alDescs.add(desc);
        signatureTypeMap.put(type, desc);
        signatureTypeDescMap.put(desc, type);
      }
      
      _saFileSignatureType = alTypes.toArray(new String[alTypes.size()]);
      _saFileSignatureTypeDesc = alDescs.toArray(new String[alDescs.size()]);
    }
  }
  
  protected String [] getFileSignatureType() {
    loadSignatureTypes();
    return _saFileSignatureType;
  }

  protected String [] getFileSignatureTypeDesc() {
    loadSignatureTypes();
    return _saFileSignatureTypeDesc;
  }

  protected Map<String,String> getFileSignatureTypeMap() {
    loadSignatureTypes();
    return signatureTypeMap;
  }

  protected Map<String,String> getFileSignatureTypeDescMap() {
    loadSignatureTypes();
    return signatureTypeDescMap;
  }

  protected synchronized void loadButtonTypes() {
    if(null != _saButtonTypeDesc) return;
    
    buttonTypeMap.clear();
    buttonTypeDescMap.clear();

    List<String> alTypes = new ArrayList<String>();
    List<String> alDescs = new ArrayList<String>();

    //CANCEL
    String typeName = AlteraAtributosJSP.sCANCEL_TYPE;
    String desc = adapter.getString("AlteraAtributosJSP.button.type.cancel"); //$NON-NLS-1$
    alTypes.add(typeName);
    alDescs.add(desc);
    buttonTypeMap.put(typeName, desc);
    buttonTypeDescMap.put(desc, typeName);
    //RESET
    typeName = AlteraAtributosJSP.sRESET_TYPE;
    desc = adapter.getString("AlteraAtributosJSP.button.type.reset");
    alTypes.add(typeName);
    alDescs.add(desc);
    buttonTypeMap.put(typeName, desc);
    buttonTypeDescMap.put(desc, typeName);
    //SAVE
    typeName = AlteraAtributosJSP.sSAVE_TYPE;
    desc = adapter.getString("AlteraAtributosJSP.button.type.save");
    alTypes.add(typeName);
    alDescs.add(desc);
    buttonTypeMap.put(typeName, desc);
    buttonTypeDescMap.put(desc, typeName);
    //PRINT
    typeName = AlteraAtributosJSP.sPRINT_TYPE;
    desc = adapter.getString("AlteraAtributosJSP.button.type.print");
    alTypes.add(typeName);
    alDescs.add(desc);
    buttonTypeMap.put(typeName, desc);
    buttonTypeDescMap.put(desc, typeName);
    //NEXT
    typeName = AlteraAtributosJSP.sNEXT_TYPE;
    desc = adapter.getString("AlteraAtributosJSP.button.type.forward");
    alTypes.add(typeName);
    alDescs.add(desc);
    buttonTypeMap.put(typeName, desc);
    buttonTypeDescMap.put(desc, typeName);
    //CUSTOM
    typeName = AlteraAtributosJSP.sCUSTOM_TYPE;
    desc = adapter.getString("AlteraAtributosJSP.button.type.custom");
    alTypes.add(typeName);
    alDescs.add(desc);
    buttonTypeMap.put(typeName, desc);
    buttonTypeDescMap.put(desc, typeName);
    //RETURN_PARENT
    typeName = AlteraAtributosJSP.sRETURN_PARENT_TYPE;
    desc = adapter.getString("AlteraAtributosJSP.button.type.return_to_parent");
    alTypes.add(typeName);
    alDescs.add(desc);
    buttonTypeMap.put(typeName, desc);
    buttonTypeDescMap.put(desc, typeName);

    _saButtonType = alTypes.toArray(new String[alTypes.size()]);
    _saButtonTypeDesc = alDescs.toArray(new String[alDescs.size()]);
  }
  
  protected String [] getButtonType() {
    loadButtonTypes();
    return _saButtonType;
  }

  protected String [] getButtonTypeDesc() {
    loadButtonTypes();
    return _saButtonTypeDesc;
  }

  protected Map<String,String> getButtonTypeMap() {
    loadButtonTypes();
    return buttonTypeMap;
  }

  protected Map<String,String> getButtonTypeDescMap() {
    loadButtonTypes();
    return buttonTypeDescMap;
  }

  protected void loadChartTemplates() {

    // Chart Types
    _hmChartTemplates.put(new Integer(JSPFieldData.nCHART_NONE), JSPFieldData.sCHOOSE);
    _hmChartTemplateCodes.put(new Integer(JSPFieldData.nCHART_NONE), ""); //$NON-NLS-1$

    RepositoryClient repos = adapter.getRepository();
    if(null != repos) {
      String [] files = repos.listChartTemplates();

      for(int i = 0, k = 0; null != files && i < files.length; i++) {
        String file = files[i];
        if(file.endsWith(".chart")) { //$NON-NLS-1$

          int pos = JSPFieldData.nCHART_START_POS+k;
          _hmChartTemplates.put(new Integer(pos), file);
          _hmChartTemplateCodes.put(new Integer(pos), file.substring(0, file.length()-6));
          k++;
        }


      }
    }
  }

  public String getChartTemplateText(int anTypeID) {
    return getChartTemplateText(new Integer(anTypeID));
  }

  public String getChartTemplateText(Integer aiTypeID) {
    String ret = null;
    if (_hmChartTemplates.containsKey(aiTypeID)){
      ret = (String)_hmChartTemplates.get(aiTypeID);
    }
    return ret;
  }

  public int getChartTemplateID(String asChartTypeText) {
    if(null == asChartTypeText) return 0;
    if (_hmChartTemplates.containsValue(asChartTypeText)){
      for(Entry<Integer, String> entry : _hmChartTemplates.entrySet()) {
        if(asChartTypeText.equals(entry.getValue())) return entry.getKey();
      }
    }
    return 0;
  }

  public String getChartTemplateCode(int anTypeID) {
    return getChartTemplateCode(new Integer(anTypeID));
  }

  public String getChartTemplateCode(Integer aiTypeID) {
    String ret = null;
    if (_hmChartTemplateCodes.containsKey(aiTypeID)){
      ret = (String)_hmChartTemplateCodes.get(aiTypeID);
    }
    return ret;
  }


  public String[] getDataTypes() {
    return JSPFieldData.getDataTypes(this);
  }

  public static String[] getDataTypes(JSPFieldData afdField) {

    String[] ret = new String[0];

    Iterator<Integer> iter = JSPFieldData._hmDataTypes.keySet().iterator();

    HashSet<Integer> hstmp = new HashSet<Integer>();

    Integer iKey;
    // first find biggest id
    while (iter.hasNext()) {
      iKey = iter.next();

      if (afdField._hsDisableDataTypes.contains(iKey)) {
        continue;
      }

      hstmp.add(iKey);
    }

    ret = new String[hstmp.size()];

    for (int i=0,ii=0; ii < ret.length; i++) {
      iKey = new Integer(i);

      if (hstmp.contains(iKey)) {
        ret[ii++] = JSPFieldData.getDataTypeText(iKey);
        hstmp.remove(iKey);
      }

      if (hstmp.size() == 0) {
        // all items processed... 
        break;
      }
    }

    return ret;
  }

  public static String[] getAlignments() {
    String[] ret = new String[JSPFieldData._hmAlignments.size()];

    Iterator<Integer> iter = JSPFieldData._hmAlignments.keySet().iterator();

    Integer iKey;
    while (iter.hasNext()) {
      iKey = iter.next();
      ret[iKey.intValue()] = JSPFieldData.getAlignmentText(iKey);
    }

    return ret;
  }

  public String[] getChartTemplates() {
    if(null == _hmChartTemplates || 0 == _hmChartTemplates.size()) loadChartTemplates();
    String[] ret = new String[_hmChartTemplates.size()];

    Iterator<Integer> iter = _hmChartTemplates.keySet().iterator();

    Integer iKey;
    while (iter.hasNext()) {
      iKey = iter.next();
      ret[iKey.intValue()] = getChartTemplateText(iKey);
    }

    return ret;
  }

  public static JSPFieldTypeEnum[] getFieldTypesIDs() {
    return JSPFieldTypeEnum.values();
  }

  public static String[] getFieldTypes() {
    JSPFieldTypeEnum[] values = JSPFieldTypeEnum.values();
    String[] ret = new String[values.length];
    
    for(int i = 0; i < values.length; i++) {
      ret[i] = _hmFieldTypes.get(values[i]);
    }
    return ret;
  }

  public static String getFieldTypeText(JSPFieldTypeEnum aiFieldType) {
    String ret = null;
    if (_hmFieldTypes.containsKey(aiFieldType)){
      ret = _hmFieldTypes.get(aiFieldType);
    }
    return ret;
  }

  public String getFieldTypeText() {
    return JSPFieldData.getFieldTypeText(this._nFieldType);
  }


  public static JSPFieldTypeEnum getFieldTypeID(String asFieldTypeText) {
    if (_hmFieldCodeNameTypes.containsKey(asFieldTypeText)){
      return _hmFieldCodeNameTypes.get(asFieldTypeText);
    }
    return JSPFieldTypeEnum.FIELD_TYPE_NONE;
  }

  public static String getFieldTypeTooltip(JSPFieldTypeEnum aiFieldTypeID) {
    String ret = null;
    if (JSPFieldData._hmFieldTypeTooltips.containsKey(aiFieldTypeID)) {
      ret = JSPFieldData._hmFieldTypeTooltips.get(aiFieldTypeID);
    }
    return ret;
  }


  public static Iterator<JSPFieldTypeEnum> getFieldTypeClassNamesKeys() {
    return _hmFieldTypeClassNames.keySet().iterator();
  }


  public static String getFieldTypeClassName(JSPFieldTypeEnum anFieldTypeID) {
    String ret = null;
    if (JSPFieldData._hmFieldTypeClassNames.containsKey(anFieldTypeID)) {
      ret = JSPFieldData._hmFieldTypeClassNames.get(anFieldTypeID);
    }
    return ret;
  }

  public String getFieldTypeClassName() {
    String ret = null;
    if (_hmFieldTypeClassNames.containsKey(this._nFieldType)) {
      ret = _hmFieldTypeClassNames.get(this._nFieldType);
    }
    return ret;
  }
  
  public String getEngineClass() {
    return this._nFieldType.getEngineClass();
  }

  public static String getFieldTypeCode(JSPFieldTypeEnum aiFieldType) {
    String ret = null;
    if (_hmFieldTypeCodeNames.containsKey(aiFieldType)){
      ret = _hmFieldTypeCodeNames.get(aiFieldType);
    }
    return ret;
  }

  public String getFieldTypeCode() {
    return getFieldTypeCode(this._nFieldType);
  }


  // get single property edit component
  public JComponent getEditComponent(int anPropID) {
    return this.getEditComponent(new Integer(anPropID));
  }

  // get single property edit component
  public JComponent getEditComponent(Integer aiPropID) {
    return this.getEditComponent(null,aiPropID);
  }

  // get multiple property edit component
  public JComponent getEditComponent(int anListIndex, int anPropID) {
    return this.getEditComponent(new Integer(anListIndex),
        new Integer(anPropID));
  }

  // get multiple property edit component
  public JComponent getEditComponent(Integer aiListIndex, Integer aiPropID) {
    JComponent ret = null;

    HashMap<Integer,JComponent> hmEditComponents = null;

    if (aiListIndex == null || aiListIndex.intValue() < 0) {
      hmEditComponents = this._hmEditComponents;
    }
    else {
      hmEditComponents = 
        this._alEditComponentsList.get(aiListIndex.intValue());
    }

    if (hmEditComponents.containsKey(aiPropID)) {
      ret = hmEditComponents.get(aiPropID);
    }

    return ret;
  }

  // set single property's edit component
  public void setEditComponent(Integer aiPropID,
      JComponent ajcValue) {
    this.setEditComponent(null, aiPropID, ajcValue);

  }

  // set multiple property's edit component
  public void setEditComponent(Integer aiListIndex,
      Integer aiPropID,
      JComponent ajcValue) {

    HashMap<Integer,JComponent> hmEditComponents = null;

    if (aiListIndex == null || aiListIndex.intValue() < 0) {
      hmEditComponents = this._hmEditComponents;
    }
    else {
      hmEditComponents = this._alEditComponentsList.get(aiListIndex.intValue());
    }

    hmEditComponents.put(aiPropID,ajcValue);
  }

  /**
   *
   * Exports the prop's names and values.
   *
   * @return array of ArrayList's with prop's "coded" names at index
   * nNAMES_INDEX and prop's values at nVALUES_INDEX
   */
  public Map<Integer, ArrayList<String>> exportData () {

    Map<Integer, ArrayList<String>> alRet = new HashMap<Integer, ArrayList<String>>(2);
    alRet.put(JSPFieldData.nNAMES_INDEX, new ArrayList<String>());
    alRet.put(JSPFieldData.nVALUES_INDEX, new ArrayList<String>());

    String sPrefix = JSPFieldData.sNAME_PREFIX
    + JSPFieldData.sJUNCTION
    + this.getPosition()
    + JSPFieldData.sJUNCTION;

    // initialize array with field id
    ArrayList<String> al = alRet.get(JSPFieldData.nNAMES_INDEX);

    // names
    // id
    al.add(sPrefix + JSPFieldData.getPropCodeName(JSPFieldData.nPROP_ID));
    // field type
    al.add(sPrefix + JSPFieldData.getPropCodeName(JSPFieldData.nPROP_FIELD_TYPE));
    // field type code
    al.add(sPrefix + JSPFieldData.getPropCodeName(JSPFieldData.nPROP_FIELD_TYPE_CODE));

    // values
    // id
    al = alRet.get(JSPFieldData.nVALUES_INDEX);
    al.add(String.valueOf(this.getID()));
    // field type
    al.add(String.valueOf(this.getFieldTypeText()));
    // field type code
    al.add(String.valueOf(this.getFieldTypeCode()));



    Iterator<Integer> iter = this._hmStaticProps.keySet().iterator();
    String sName = sPrefix;
    Integer iRow = null;
    Integer iProp = null;
    Integer iProp2 = null;
    Integer iProp3 = null;
    Integer iProp4 = null;
    String sVal = null;
    String sVal2 = null;
    String sVal3 = null;
    String sVal4 = null;
    int nProp = 0;

    // first static and single props
    for (int i=0; i < (this._hmStaticProps.size() + this._alEditSingleProps.size()); i++) {

      iProp2 = null;
      sVal2 = null;
      iProp3 = null;
      sVal3 = null;
      iProp4 = null;
      sVal4 = null;


      if (i >= this._hmStaticProps.size()) {
        iProp = this._alEditSingleProps.get(i - this._hmStaticProps.size());
        sVal = this.getProperty(iProp);
      }
      else {
        if (iter.hasNext()) {
          iProp = iter.next();
          sVal = this._hmStaticProps.get(iProp);
        }
        else {
          // just in case...
          continue;
        }
      }

      nProp = iProp.intValue();

      if (sVal == null) {
        continue;
      }

      try {
        switch (nProp) {
        case JSPFieldData.nPROP_DATA_TYPE:
          // sVal has data type text
          // in this case get the appropriate data type id from text
          // to be able to get data type class name
          iProp2 = new Integer(JSPFieldData.nPROP_DATA_TYPE_CLASS);
          sVal2 = getDataTypeClass(new Integer(getDataTypeID(sVal)));
          // do nothing to sVal
          break;
        case JSPFieldData.nPROP_DATA_TYPE_CLASS:
          // already done
          continue;
        case JSPFieldData.nPROP_ALIGNMENT:    
          // sVal has aligment text
          // set alignment code
          iProp2 = new Integer(JSPFieldData.nPROP_ALIGNMENT_CODE);
          sVal2 = getAlignmentCode(new Integer(getAlignmentID(sVal)));
          // do nothing to sVal
          break;
        case JSPFieldData.nPROP_CHART_TEMPLATE:
          // sVal has aligment text
          // set alignment code
          iProp2 = new Integer(JSPFieldData.nPROP_CHART_TEMPLATE_CODE);
          sVal2 = getChartTemplateCode(new Integer(getChartTemplateID(sVal)));
          // do nothing to sVal
          break;
        case JSPFieldData.nPROP_DATE_FORMAT:      
          // set additional fields: size, maxlength and id related to selected date format
          int ntmp = sVal.length();
          iProp2 = new Integer(JSPFieldData.nPROP_SIZE);
          sVal2 = String.valueOf(ntmp + 4);
          iProp3 = new Integer(JSPFieldData.nPROP_MAXLENGTH);
          sVal3 = String.valueOf(ntmp);
          iProp4 = new Integer(JSPFieldData.nPROP_DATE_FORMAT_ID);
          sVal4 = _hmDateFormatCodes.get(sVal);
          // do nothing to sVal
          break;
        case JSPFieldData.nPROP_HOUR_FORMAT:
          // pretty much keep doing the same as above
          iProp2 = new Integer(JSPFieldData.nPROP_SIZE);
          sVal2 = String.valueOf(sVal.length() + 4);
          iProp3 = new Integer(JSPFieldData.nPROP_MAXLENGTH);
          sVal3 = String.valueOf(sVal.length());
          iProp4 = new Integer(JSPFieldData.nPROP_HOUR_FORMAT_ID);
          sVal4 = _hmHourFormatCodes.get(sVal);
          // do nothing to sVal
          break;
        case JSPFieldData.nPROP_FILE_PERMISSION:    
          // map value...
          sVal = permissionDescMap.get(sVal);
          // do nothing to sVal
          break;
        case JSPFieldData.nPROP_FILE_SIGNATURE_TYPE:    
          // map value...
          sVal = getFileSignatureTypeDescMap().get(sVal);
          // do nothing to sVal
          break;
        case JSPFieldData.nPROP_BUTTON_TYPE:    
          // map value...
          sVal = getButtonTypeDescMap().get(sVal);
          // do nothing to sVal
          break;
        default:
          // default is text
          // sVal ok

          // check if value depends on other value



        } // switch
      }
      catch (Exception e) {
        continue;
      }

      alRet.get(JSPFieldData.nNAMES_INDEX).add(sName + JSPFieldData.getPropCodeName(iProp));
      alRet.get(JSPFieldData.nVALUES_INDEX).add(sVal);

      // extra props
      if (iProp2 != null && sVal2 != null) {
        alRet.get(JSPFieldData.nNAMES_INDEX).add(sName + JSPFieldData.getPropCodeName(iProp2));
        alRet.get(JSPFieldData.nVALUES_INDEX).add(sVal2);
      }
      if (iProp3 != null && sVal3 != null) {
        alRet.get(JSPFieldData.nNAMES_INDEX).add(sName + JSPFieldData.getPropCodeName(iProp3));
        alRet.get(JSPFieldData.nVALUES_INDEX).add(sVal3);
      }
      if (iProp4 != null && sVal4 != null) {
        alRet.get(JSPFieldData.nNAMES_INDEX).add(sName + JSPFieldData.getPropCodeName(iProp4));
        alRet.get(JSPFieldData.nVALUES_INDEX).add(sVal4);
      }
    }


    // then multiple props
    for (int row=0; row < this._alPropsList.size(); row++) {

      iRow = new Integer(row);

      sName = sPrefix
      + JSPFieldData.sROW
      + JSPFieldData.sJUNCTION
      + iRow.toString()
      + JSPFieldData.sJUNCTION;

      for (int i=0; i < this._alEditMultipleProps.size(); i++) {

        iProp2 = null;
        sVal2 = null;
        iProp3 = null;
        sVal3 = null;
        iProp4 = null;
        sVal4 = null;

        iProp = this._alEditMultipleProps.get(i);
        nProp = iProp.intValue();

        // data
        sVal = this.getProperty(iRow, iProp);
        if (sVal == null) {
          continue;
        }

        try {
          switch (nProp) {
          case JSPFieldData.nPROP_DATA_TYPE:
            // sVal has data type text
            // in this case get the appropriate data type id from text
            // to be able to get data type class name
            iProp2 = new Integer(JSPFieldData.nPROP_DATA_TYPE_CLASS);
            sVal2 = JSPFieldData.getDataTypeClass(new Integer(JSPFieldData.getDataTypeID(sVal)));
            // do nothing to sVal
            break;
          case JSPFieldData.nPROP_DATA_TYPE_CLASS:
            // already done
            continue;
          case JSPFieldData.nPROP_ALIGNMENT:
            // sVal has aligment text
            // set alignment code
            iProp2 = new Integer(JSPFieldData.nPROP_ALIGNMENT_CODE);
            sVal2 = JSPFieldData.getAlignmentCode(new Integer(JSPFieldData.getAlignmentID(sVal)));
            // do nothing to sVal
            break;
          case JSPFieldData.nPROP_CHART_TEMPLATE:
            // sVal has aligment text
            // set alignment code
            iProp2 = new Integer(JSPFieldData.nPROP_CHART_TEMPLATE_CODE);
            sVal2 = getChartTemplateCode(new Integer(getChartTemplateID(sVal)));
            // do nothing to sVal
            break;
          case JSPFieldData.nPROP_DATE_FORMAT:    
            // set additional fields: size and maxlength related to selected date format
            int ntmp = sVal.length();
            iProp2 = new Integer(JSPFieldData.nPROP_SIZE);
            sVal2 = String.valueOf(ntmp + 4);
            iProp3 = new Integer(JSPFieldData.nPROP_MAXLENGTH);
            sVal3 = String.valueOf(ntmp);
            iProp4 = new Integer(JSPFieldData.nPROP_DATE_FORMAT_ID);
            sVal4 = _hmDateFormatCodes.get(sVal);
            // do nothing to sVal
            break;
          case JSPFieldData.nPROP_HOUR_FORMAT:
            // pretty much keep doing the same as above
            iProp2 = new Integer(JSPFieldData.nPROP_SIZE);
            sVal2 = String.valueOf(sVal.length() + 4);
            iProp3 = new Integer(JSPFieldData.nPROP_MAXLENGTH);
            sVal3 = String.valueOf(sVal.length());
            iProp4 = new Integer(JSPFieldData.nPROP_HOUR_FORMAT_ID);
            sVal4 = _hmHourFormatCodes.get(sVal);
            // do nothing to sVal
            break;
          case JSPFieldData.nPROP_FILE_PERMISSION:
            // map value...
            sVal = permissionDescMap.get(sVal);
            // do nothing to sVal
            break;
          case JSPFieldData.nPROP_FILE_SIGNATURE_TYPE:
            // map value...
            sVal = getFileSignatureTypeDescMap().get(sVal);
            // do nothing to sVal
            break;
          case JSPFieldData.nPROP_BUTTON_TYPE:
            // map value...
            sVal = getButtonTypeDescMap().get(sVal);
            // do nothing to sVal
            break;
          default:
            // default is text
            // sVal ok
          } // switch
        }
        catch (Exception e) {
          continue;
        }

        alRet.get(JSPFieldData.nNAMES_INDEX).add(sName + JSPFieldData.getPropCodeName(iProp));
        alRet.get(JSPFieldData.nVALUES_INDEX).add(sVal);

        // extra props
        if (iProp2 != null && sVal2 != null) {
          alRet.get(JSPFieldData.nNAMES_INDEX).add(sName + JSPFieldData.getPropCodeName(iProp2));
          alRet.get(JSPFieldData.nVALUES_INDEX).add(sVal2);
        }
        if (iProp3 != null && sVal3 != null) {
          alRet.get(JSPFieldData.nNAMES_INDEX).add(sName + JSPFieldData.getPropCodeName(iProp3));
          alRet.get(JSPFieldData.nVALUES_INDEX).add(sVal3);
        }
        if (iProp4 != null && sVal4 != null) {
          alRet.get(JSPFieldData.nNAMES_INDEX).add(sName + JSPFieldData.getPropCodeName(iProp4));
          alRet.get(JSPFieldData.nVALUES_INDEX).add(sVal4);
        }
      }
    }

    return alRet;
  }


  /**
   *
   * Exports the prop's values as a table row according to the previously set
   * table props. Only single properties are considered for exportation.
   *
   * @return array table prop's values or null if property does not apply to
   * this field
   */
  public String[] exportToTableRow () {

    String[] ret = new String[this._alTableProps.size()];

    Integer iProp = null;
    String stmp = null;

    for (int i=0; i < this._alTableProps.size(); i++) {
      stmp = null;

      iProp = this._alTableProps.get(i);

      try {

        switch (iProp.intValue()) {
        case JSPFieldData.nPROP_ID:
          stmp = String.valueOf(this.getID());
          break;
        case JSPFieldData.nPROP_FIELD_TYPE:
          // in this case get the appropriate field type text from id
          stmp = this.getFieldTypeText();
          break;
        case JSPFieldData.nPROP_DATA_TYPE:
          stmp = this.getProperty(iProp); // stmp is data type text
          break;
        case JSPFieldData.nPROP_ALIGNMENT:
          stmp = this.getProperty(iProp); // stmp is alignment text
          break;
        case JSPFieldData.nPROP_CHART_TEMPLATE:
          stmp = this.getProperty(iProp);
          break;
        default:
          // default is text
          stmp = this.getProperty(iProp);
        } // switch
      }
      catch (Exception e) {
      }

      ret[i] = stmp;
    } // for

    return ret;
  } // exportToTableRow



  public synchronized JPanel makeEditPanel() {

    this._editPanel = new JPanel() {
      private static final long serialVersionUID = 3560402301983401426L;

      public JToolTip createToolTip()
      {
        return new JMultiLineToolTip();
      }
    };
    this._editPanel.setLayout(new BorderLayout());

    
    // simple props' layout
    GridBagLayout sGridbag = new GridBagLayout();
    GridBagConstraints sC = new GridBagConstraints();
    sC.fill = GridBagConstraints.HORIZONTAL;
    // single props' panel 
    JPanel sMainPanel = null;


    // multiple props' layout
    GridBagLayout mGridbag = new GridBagLayout();
    GridBagConstraints mC = new GridBagConstraints();
    mC.fill = GridBagConstraints.HORIZONTAL;
    // multiple props' panel 
    JPanel mMainPanel = null;

    JScrollPane scrollPane = null;
    JPanel scrollPanel = null;

    int nProp = 0;
    Integer iRow = null;
    Integer iProp = null;
    String stmp = null;
    JLabel jLabel = null;
    JComponent jValue = null;
    JComponent jctmp = null;
    JPanel sizer = null;
    PropDependency pd = null;
    PropDependencyItem pdi = null;
    PropDependencyItem[] pdia = null;
    ArrayList<JComponent> altmp = null;
    ArrayList<JLabel> altmp2 = null;
    boolean btmp = false;
    HashMap<JComponent,JLabel> hmtmp = null;

    // first make single props
    for (int i=0; i < this._alEditSingleProps.size(); i++) {

      if (i == 0) {
        // initialize panel
        sMainPanel = new JPanel();
        sMainPanel.setLayout(sGridbag);
      }

      iProp = this._alEditSingleProps.get(i);
      nProp = iProp.intValue();

      // data
      stmp = this.getProperty(iProp);
      if (stmp == null) stmp = ""; //$NON-NLS-1$
      jValue = getEditComponent(iProp);
      switch (nProp) {
      case JSPFieldData.nPROP_DATA_TYPE:
        if (jValue == null) {
          jValue = new JComboBox(this.getDataTypes());
          this.setEditComponent(iProp, jValue);
        }
        if (stmp.equals("")) stmp = JSPFieldData.sCHOOSE; //$NON-NLS-1$
        ((JComboBox)jValue).setSelectedItem(stmp);
        break;
      case JSPFieldData.nPROP_ALIGNMENT:
        if (jValue == null) {
          jValue = new JComboBox(JSPFieldData.getAlignments());
          this.setEditComponent(iProp, jValue);
        }
        
        // retrieve the "correct" value from nPROP_ALIGN_CODE
        stmp = getAlignmentText(getAlignmentIdFromCode(this.getProperty(nPROP_ALIGNMENT_CODE)));
        if (null == stmp || stmp.equals("")) stmp = sCHOOSE; //$NON-NLS-1$
        ((JComboBox)jValue).setSelectedItem(stmp);
        break;
      case JSPFieldData.nPROP_CHART_TEMPLATE:
        if (jValue == null) {
          jValue = new JComboBox(this.getChartTemplates());
          this.setEditComponent(iProp, jValue);
        }
        if (stmp.equals("")) stmp = JSPFieldData.sCHOOSE; //$NON-NLS-1$
        ((JComboBox)jValue).setSelectedItem(stmp);
        break;
      case JSPFieldData.nPROP_DATE_FORMAT:
        if (jValue == null) {
          jValue = new JComboBox(_saDateFormats);
          this.setEditComponent(iProp, jValue);
        }
        if (stmp.equals("")) stmp = JSPFieldData.sCHOOSE; //$NON-NLS-1$
        ((JComboBox)jValue).setSelectedItem(stmp);
        break;
      case JSPFieldData.nPROP_HOUR_FORMAT:
        if (jValue == null) {
          jValue = new JComboBox(_saHourFormats);
          this.setEditComponent(iProp, jValue);
        }
        if (StringUtils.isBlank(stmp)) {
          stmp = JSPFieldData.sHOUR_FORMAT_NONE;
        }
        ((JComboBox) jValue).setSelectedItem(stmp);
        break;
      case JSPFieldData.nPROP_CONNECTOR_SELECT:
        if (jValue == null) {
          jValue = new JComboBox(getConnectors());
          this.setEditComponent(iProp, jValue);
        }
        ((JComboBox)jValue).setSelectedItem(getConnectorItem(stmp));
        break;
      case JSPFieldData.nPROP_FILE_PERMISSION:
        if (jValue == null) {
          jValue = new JComboBox(_saFilePermissionsDesc);
          this.setEditComponent(iProp, jValue);
        }
        if (stmp.equals("")) stmp = _saFilePermissions[0]; //$NON-NLS-1$
        ((JComboBox)jValue).setSelectedItem(permissionMap.get(stmp));
        break;
      case JSPFieldData.nPROP_FILE_SIGNATURE_TYPE:
        if (jValue == null) {
          jValue = new JComboBox(getFileSignatureTypeDesc());
          this.setEditComponent(iProp, jValue);
        }
        if (stmp.equals("")) stmp = getFileSignatureType()[0]; //$NON-NLS-1$
        ((JComboBox)jValue).setSelectedItem(getFileSignatureTypeMap().get(stmp));
        break;
      case JSPFieldData.nPROP_BUTTON_TYPE:
        if (jValue == null) {
          jValue = new JComboBox(getButtonTypeDesc());
          this.setEditComponent(iProp, jValue);
        }
        if (stmp.equals("")) stmp = getButtonType()[0]; //$NON-NLS-1$
        ((JComboBox)jValue).setSelectedItem(stmp);
        break;
      case JSPFieldData.nPROP_DATASOURCE:
      {
        if (jValue == null) {
          JComboBox jcb = new JComboBox(_saDataSources);
          jcb.setEditable(true);
          jcb.addActionListener(new EditableComboListener());
          this.setEditComponent(iProp, jValue = jcb);
        }
        ((JComboBox)jValue).setSelectedItem(stmp);
      }
      break;
      case JSPFieldData.nPROP_PP_PASS_TO_LINK:
      case JSPFieldData.nPROP_USE_LINKS:
      case JSPFieldData.nPROP_OUTPUT_ONLY:
      case JSPFieldData.nPROP_EMPTY_NOT_ALLOWED:
      case JSPFieldData.nPROP_SERVICE_PRINT:
      case JSPFieldData.nPROP_SERVICE_EXPORT:
      case JSPFieldData.nPROP_OPEN_NEW_WINDOW:
      case JSPFieldData.nPROP_PROC_LINK:
      case JSPFieldData.nPROP_ONCHANGE_SUBMIT:
      case JSPFieldData.nPROP_TEXT_VALUE:
      case JSPFieldData.nPROP_CURRDATE_IF_EMPTY:
      case JSPFieldData.nPROP_FILE_SIGN_NEW:
      case JSPFieldData.nPROP_FILE_SIGN_METHOD:
      case JSPFieldData.nPROP_FILE_SIGN_EXISTING:
      case JSPFieldData.nPROP_FILE_UPLOAD_PRESERVE_EXT:
      case JSPFieldData.nPROP_CACHE_HINT:
      case JSPFieldData.nPROP_FILE_IS_IMAGE:
      case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_PROCESSING:
      case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_VALIDATION:
      case JSPFieldData.nPROP_KEEP_SCROLL_ONLOAD:
      case JSPFieldData.nPROP_BUTTON_CONFIRM_ACTION:
        if (jValue == null) {
          jValue = new JCheckBox();
          this.setEditComponent(iProp, jValue);
        }
        ((JCheckBox)jValue).setSelected((new Boolean(stmp)).booleanValue());
        break;
      case JSPFieldData.nPROP_TEXT_AREA:
        if (jValue == null) {
          jValue = new JTextArea(20,40);
          this.setEditComponent(iProp, jValue);
        }
        ((JTextArea)jValue).setText(stmp);
        jValue.setBackground(AlteraAtributosJSP.cBG_COLOR);
        break;
      case JSPFieldData.nPROP_LIST_OF_POPUP_FLOWS:
      {
        if (jValue == null) {
          JComboBox listofSuportFlowsComboBox = new JComboBox(listOfSuportFlows);
          listofSuportFlowsComboBox.setEditable(true);
          listofSuportFlowsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JComboBox suportFlowsComboBox = (JComboBox) e.getSource();
              int idx = suportFlowsComboBox.getSelectedIndex();
              String selectedPopupFlow = String.valueOf(suportFlowsComboBox.getSelectedItem());

              if (selectedPopupFlow != null && !"".equals(selectedPopupFlow)){
                String [][] inputFields = JSPPopupFormFieldData.loadPopupvariables(adapter, selectedPopupFlow);

                for (int i=0; i<inputFields.length; i++){
                  HashMap<Integer, String> prop = new HashMap<Integer, String>();
                  //                  prop.put(nPROP_POPUP_VARIABLES, String.valueOf(inputFields[i][0]));
                  //                  _alPropsList.add (prop);
                }
              }
            }

          });
          this.setEditComponent(iProp, jValue = listofSuportFlowsComboBox);
        }
        if(stmp == null || "".equals(stmp)){
          stmp = adapter.getString("JSPFieldData.form_popup_field.choose");
        }
        ((JComboBox)jValue).setSelectedItem(stmp);
      }
      break;
      case JSPFieldData.nPROP_FORM_TEMPLATE: {
        if (jValue == null) {
          JComboBox jcb = new JComboBox(getSaFormTemplates());
          jcb.setEditable(true);
          jcb.addActionListener(new EditableComboListener());
          this.setEditComponent(iProp, jValue = jcb);
        }
        ((JComboBox) jValue).setSelectedItem(stmp);
      }
        break;
      case JSPFieldData.nPROP_VAR_NAME: {
        if (jValue == null) {
          JComboBox comboBox = new JComboBox();
          Object[] elements = getCatalogue();
          AutoCompleteSupport.install(comboBox, GlazedLists.eventListOf(elements));
          this.setEditComponent(iProp, jValue = comboBox);
        }
        ((JComboBox) jValue).setSelectedItem(stmp);
      }
        break;
      default:
        // DEFAULT IS TEXT...
        if (jValue == null) {
          jValue = new JTextField(stmp,this.nTEXT_SIZE);
          this.setEditComponent(iProp, jValue);
        }
      jValue.setBackground(AlteraAtributosJSP.cBG_COLOR);
      } // switch


      // label
      stmp = JSPFieldData.getPropLabel(iProp);
      jLabel = new JLabel(stmp);
      jLabel.setHorizontalAlignment(JLabel.RIGHT);
      jLabel.setLabelFor(jValue);

      if (hmtmp == null) {
        hmtmp = new HashMap<JComponent, JLabel>();
      }
      hmtmp.put(jValue, jLabel);

      sGridbag.setConstraints(jLabel,sC);
      try {
        	if(null != jValue)
        		sMainPanel.add(jLabel);
		} catch (Exception e) {
			logger.error("jValue == null - 2618" + e);
		}
      
      // separator
      sizer = new JPanel();
      sizer.setSize(5,1);
      sGridbag.setConstraints(sizer,sC);
      if(null != sizer)
      sMainPanel.add(sizer);
      sC.gridwidth = GridBagConstraints.REMAINDER;
      sGridbag.setConstraints(jValue,sC);
      sMainPanel.add(jValue);
      sC.gridwidth = 1;
    } // for single props

    // now handle single props dependencies
    for (int i=0; i < this._alEditSingleProps.size(); i++) {

      iProp = this._alEditSingleProps.get(i);
      nProp = iProp.intValue();

      if (this._hmPropDependencies.containsKey(iProp)) {

        pd = this._hmPropDependencies.get(iProp);

        stmp = this.getProperty(iProp);
        if (stmp == null) stmp = ""; //$NON-NLS-1$
        jValue = getEditComponent(iProp);

        btmp = pd.isOn(stmp, true);

        pdia = pd.getDependencies();
        altmp = new ArrayList<JComponent>();
        altmp2 = new ArrayList<JLabel>();
        for (int j=0; pdia != null && j < pdia.length; j++) {
          pdi = pdia[j];
          jctmp = getEditComponent(new Integer(pdi.getProp()));
          if (pdi.getAction() == PropDependency.nENABLE) {
            jctmp.setEnabled(btmp);
          }
          else {
            jctmp.setEnabled(!btmp);
          }
          altmp.add(jctmp);
          try {
          	if(null != jValue)
          		 jLabel = hmtmp.get(jctmp);
          		 jLabel.setEnabled(jctmp.isEnabled());
          		 altmp2.add(jLabel);
  		} catch (Exception e) {
  			logger.error("jValue == null - 2660" + e);
  		}
         
        }

        switch (nProp) {
        case JSPFieldData.nPROP_DATA_TYPE:
        case JSPFieldData.nPROP_ALIGNMENT:
        case JSPFieldData.nPROP_CHART_TEMPLATE:
        case JSPFieldData.nPROP_DATE_FORMAT:
        case JSPFieldData.nPROP_FILE_SIGNATURE_TYPE:
        case JSPFieldData.nPROP_BUTTON_TYPE:
          // item listener: combobox
          ((JComboBox)jValue).addItemListener(new DependencyComboListener(jValue, pd, altmp, altmp2));
          break;
        case JSPFieldData.nPROP_CONNECTOR_SELECT:
          // item listener: combobox
          ((JComboBox)jValue).addItemListener(new DependencyComboListener(jValue, pd, altmp, altmp2));
          break;
        case JSPFieldData.nPROP_PP_PASS_TO_LINK:
        case JSPFieldData.nPROP_USE_LINKS:
        case JSPFieldData.nPROP_OUTPUT_ONLY:
        case JSPFieldData.nPROP_EMPTY_NOT_ALLOWED:
        case JSPFieldData.nPROP_SERVICE_PRINT:
        case JSPFieldData.nPROP_SERVICE_EXPORT:
        case JSPFieldData.nPROP_OPEN_NEW_WINDOW:
        case JSPFieldData.nPROP_PROC_LINK:
        case JSPFieldData.nPROP_ONCHANGE_SUBMIT:
        case JSPFieldData.nPROP_TEXT_VALUE:
        case JSPFieldData.nPROP_CURRDATE_IF_EMPTY:
        case JSPFieldData.nPROP_FILE_SIGN_NEW:
        case JSPFieldData.nPROP_FILE_SIGN_METHOD:
        case JSPFieldData.nPROP_FILE_SIGN_EXISTING:
        case JSPFieldData.nPROP_FILE_UPLOAD_PRESERVE_EXT:
        case JSPFieldData.nPROP_CACHE_HINT:
        case JSPFieldData.nPROP_FILE_IS_IMAGE:
        case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_PROCESSING:
        case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_VALIDATION:
        case JSPFieldData.nPROP_KEEP_SCROLL_ONLOAD:
        case JSPFieldData.nPROP_BUTTON_CONFIRM_ACTION:
          // item listener: checkbox
          ((JCheckBox)jValue).addItemListener(new DependencyCheckListener(jValue, pd, altmp, altmp2));
          break;
        case JSPFieldData.nPROP_TEXT_AREA:
          //      ((JTextArea)jValue).addDocumentListener(new DependencyTextListener(jValue, pd, altmp, altmp2));
          break;
        case JSPFieldData.nPROP_DATASOURCE:
          // nao sei se eh realmente necessario um listener deste tipo...
          break;
        case JSPFieldData.nPROP_FORM_TEMPLATE:
          // nao sei se eh realmente necessario um listener deste tipo...
          break;
        case JSPFieldData.nPROP_VAR_NAME:
          // nao sei se eh realmente necessario um listener deste tipo...
          break;
        default:
          // action listener: textfields
          ((JTextField)jValue).addActionListener(new DependencyTextListener(jValue, pd, altmp, altmp2));
        }

      }

    }  // for single props dependencies
    hmtmp = null;




    // then make multiple props
    for (int row=0; row < this._alPropsList.size(); row++) {
      iRow = new Integer(row);

      for (int i=0; i < this._alEditMultipleProps.size(); i++) {

        if (row == 0 && i == 0) {
          // initialize panel
          mMainPanel = new JPanel();
          mMainPanel.setLayout(mGridbag);
        }

        iProp = this._alEditMultipleProps.get(i);
        nProp = iProp.intValue();

        // data
        stmp = this.getProperty(iRow, iProp);
        if (stmp == null) stmp = ""; //$NON-NLS-1$
        jValue = getEditComponent(iRow, iProp);
        switch (nProp) {
        case JSPFieldData.nPROP_DATA_TYPE:
          if (jValue == null) {
            jValue = new JComboBox(this.getDataTypes());
            this.setEditComponent(iRow, iProp, jValue);
          }
          if (stmp.equals("")) stmp = JSPFieldData.sCHOOSE; //$NON-NLS-1$
          ((JComboBox)jValue).setSelectedItem(stmp);
          break;
        case JSPFieldData.nPROP_ALIGNMENT:
          if (jValue == null) {
            jValue = new JComboBox(JSPFieldData.getAlignments());
            this.setEditComponent(iRow, iProp, jValue);
          }
          stmp = getAlignmentText(getAlignmentIdFromCode(this.getProperty(iRow, nPROP_ALIGNMENT_CODE)));
          if (null == stmp || stmp.equals("")) stmp = sCHOOSE; //$NON-NLS-1$
          ((JComboBox)jValue).setSelectedItem(stmp);
          break;
        case JSPFieldData.nPROP_CHART_TEMPLATE:
          if (jValue == null) {
            jValue = new JComboBox(this.getChartTemplates());
            this.setEditComponent(iRow, iProp, jValue);
          }
          if (stmp.equals("")) stmp = JSPFieldData.sCHOOSE; //$NON-NLS-1$
          ((JComboBox)jValue).setSelectedItem(stmp);
          break;
        case JSPFieldData.nPROP_DATE_FORMAT:
          if (jValue == null) {
            jValue = new JComboBox(_saDateFormats);
            this.setEditComponent(iRow, iProp, jValue);
          }
          if (stmp.equals("")) stmp = JSPFieldData.sCHOOSE; //$NON-NLS-1$
          ((JComboBox)jValue).setSelectedItem(stmp);
          break;
        case JSPFieldData.nPROP_HOUR_FORMAT:
          if (jValue == null) {
            jValue = new JComboBox(_saHourFormats);
            this.setEditComponent(iProp, jValue);
          }
          if (StringUtils.isBlank(stmp)) {
            stmp = JSPFieldData.sHOUR_FORMAT_NONE;
          }
          ((JComboBox) jValue).setSelectedItem(stmp);
          break;
        case JSPFieldData.nPROP_CONNECTOR_SELECT:
          if (jValue == null) {
            jValue = new JComboBox(getConnectors());
            this.setEditComponent(iRow, iProp, jValue);
          }
          ((JComboBox)jValue).setSelectedItem(getConnectorItem(stmp));
          break;
        case JSPFieldData.nPROP_FILE_PERMISSION:
          if (jValue == null) {
            jValue = new JComboBox(_saFilePermissionsDesc);
            this.setEditComponent(iRow, iProp, jValue);
          }
          if (stmp.equals("")) stmp = _saFilePermissions[0]; //$NON-NLS-1$
          ((JComboBox)jValue).setSelectedItem(permissionMap.get(stmp));
          break;
        case JSPFieldData.nPROP_FILE_SIGNATURE_TYPE:
          if (jValue == null) {
            jValue = new JComboBox(getFileSignatureTypeDesc());
            this.setEditComponent(iRow, iProp, jValue);
          }
          if (stmp.equals("")) stmp = getFileSignatureType()[0]; //$NON-NLS-1$
          ((JComboBox)jValue).setSelectedItem(getFileSignatureTypeMap().get(stmp));
          break;
        case JSPFieldData.nPROP_BUTTON_TYPE:
          if (jValue == null) {
            jValue = new JComboBox(getButtonTypeDesc());
            this.setEditComponent(iRow, iProp, jValue);
          }
          if (stmp.equals("")) stmp = getButtonType()[0]; //$NON-NLS-1$
          ((JComboBox)jValue).setSelectedItem(getButtonTypeMap().get(stmp));
          break;
        case JSPFieldData.nPROP_DATASOURCE:
          if (jValue == null) {
            JComboBox jcb = new JComboBox(_saDataSources);
            jcb.setEditable(true);
            jcb.addActionListener(new EditableComboListener());
            this.setEditComponent(iRow, iProp, jValue = jcb);
          }
          ((JComboBox)jValue).setSelectedItem(stmp);
          break;
        case JSPFieldData.nPROP_FORM_TEMPLATE:
          if (jValue == null) {
            JComboBox jcb = new JComboBox(getSaFormTemplates());
            jcb.setEditable(true);
            jcb.addActionListener(new EditableComboListener());
            this.setEditComponent(iRow, iProp, jValue = jcb);
          }
          ((JComboBox) jValue).setSelectedItem(stmp);
          break;
        case JSPFieldData.nPROP_VAR_NAME: {
          if (jValue == null) {
            JComboBox comboBox = new JComboBox();
            Object[] elements = getCatalogue();
            AutoCompleteSupport.install(comboBox, GlazedLists.eventListOf(elements));
            this.setEditComponent(iRow, iProp, jValue = comboBox);
          }
          ((JComboBox) jValue).setSelectedItem(stmp);
        }
          break;
        case JSPFieldData.nPROP_PP_PASS_TO_LINK:
        case JSPFieldData.nPROP_USE_LINKS:
        case JSPFieldData.nPROP_OUTPUT_ONLY:
        case JSPFieldData.nPROP_EMPTY_NOT_ALLOWED:
        case JSPFieldData.nPROP_SERVICE_PRINT:
        case JSPFieldData.nPROP_SERVICE_EXPORT:
        case JSPFieldData.nPROP_OPEN_NEW_WINDOW:
        case JSPFieldData.nPROP_PROC_LINK:
        case JSPFieldData.nPROP_ONCHANGE_SUBMIT:
        case JSPFieldData.nPROP_TEXT_VALUE:
        case JSPFieldData.nPROP_CURRDATE_IF_EMPTY:
        case JSPFieldData.nPROP_FILE_SIGN_NEW:
        case JSPFieldData.nPROP_FILE_SIGN_METHOD:
        case JSPFieldData.nPROP_FILE_SIGN_EXISTING:
        case JSPFieldData.nPROP_FILE_UPLOAD_PRESERVE_EXT:
        case JSPFieldData.nPROP_CACHE_HINT:
        case JSPFieldData.nPROP_FILE_IS_IMAGE:
        case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_PROCESSING:
        case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_VALIDATION:
        case JSPFieldData.nPROP_KEEP_SCROLL_ONLOAD:
        case JSPFieldData.nPROP_BUTTON_CONFIRM_ACTION:
          if (jValue == null) {
            jValue = new JCheckBox();
            this.setEditComponent(iRow, iProp, jValue);
          }
          ((JCheckBox)jValue).setSelected((new Boolean(stmp)).booleanValue());
          break;
        case JSPFieldData.nPROP_TEXT_AREA:
          if (jValue == null) {
            jValue = new JTextArea(20,40);
            this.setEditComponent(iRow, iProp, jValue);
          }
          ((JTextArea)jValue).setText(stmp);
          jValue.setBackground(AlteraAtributosJSP.cBG_COLOR);
          break;
        default:
          // DEFAULT IS TEXT...
          if (jValue == null) {
            jValue = new JTextField(stmp,this.nTEXT_SIZE);
            this.setEditComponent(iRow, iProp, jValue);
          }
          jValue.setBackground(AlteraAtributosJSP.cBG_COLOR);
        } // switch

        if (row == 0 && i == 0) {
          // header row
          mC.weightx = 1.0;
          mC.gridwidth = 1;
          stmp = JSPFieldData.getPropLabel(JSPFieldData.nPROP_COLUMN);
          jLabel = new JLabel(stmp);
          jLabel.setHorizontalAlignment(JLabel.CENTER);
          jLabel.setSize(1,1);
          mGridbag.setConstraints(jLabel,mC);
          try {
            	if(null != jValue)
            		mMainPanel.add(jLabel);
    		} catch (Exception e) {
    			logger.error("jValue == null" + e);
    		}

          for (int j=0; j < this._alEditMultipleProps.size(); j++) {
            // separator
            sizer = new JPanel();
            sizer.setSize(5,1);
            mGridbag.setConstraints(sizer,mC);
            if(null != sizer)
            mMainPanel.add(sizer);

            stmp = JSPFieldData.getPropLabel(this._alEditMultipleProps.get(j));
            jLabel = new JLabel(stmp);
            jLabel.setHorizontalAlignment(JLabel.CENTER);
            mGridbag.setConstraints(jLabel,mC);
            mMainPanel.add(jLabel);
          } // for j

          // separator
          sizer = new JPanel();
          sizer.setSize(5,1);
          mC.gridwidth = GridBagConstraints.REMAINDER;
          mGridbag.setConstraints(sizer,mC);
          if(null != sizer)
          mMainPanel.add(sizer);

          // separator row
          sizer = new JPanel();
          sizer.setSize(1,3);
          mGridbag.setConstraints(sizer,mC);
          mMainPanel.add(sizer);

          mC.gridwidth = 1;
          mC.weightx = 0.0;
        }

        if (i == 0) {
          mC.gridwidth = 1;
          jLabel = new JLabel(String.valueOf(row+1) + "Âº"); //$NON-NLS-1$
          jLabel.setHorizontalAlignment(JLabel.RIGHT);
          jLabel.setSize(1,1);
          mGridbag.setConstraints(jLabel,mC);
          try {
        	  if(null != jValue)
          		mMainPanel.add(jLabel);
  		} catch (Exception e) {
  			logger.error("jValue == null" + e);
  		}
         
          // separator
          sizer = new JPanel();
          sizer.setSize(5,1);
          mGridbag.setConstraints(sizer,mC);
          mMainPanel.add(sizer);
        }

        mGridbag.setConstraints(jValue,mC);
        try {
        	if(null != jValue)
        	 mMainPanel.add(jValue);
		} catch (Exception e) {
			logger.error("jValue == null" + e);
		}
       

        // separator
        sizer = new JPanel();
        sizer.setSize(5,1);
        if (i == (this._alEditMultipleProps.size()-1)) {
          // last column: separator with buttons
          Dimension dimButton = new Dimension(22,20);
          JButton upBtn = this.makeControlButton("up.gif", //$NON-NLS-1$
              adapter.getString("AlteraAtributosJSP.button.move_line_up"), //$NON-NLS-1$
              new ReorderActionListener(REORDER_UP, row),
              dimButton);

          JButton dnBtn = this.makeControlButton("down.gif", //$NON-NLS-1$
              adapter.getString("AlteraAtributosJSP.button.move_line_down"), //$NON-NLS-1$
              new ReorderActionListener(REORDER_DOWN, row),
              dimButton);
          if(null != sizer){
          sizer.add(upBtn);
          sizer.add(dnBtn);
          upBtn.setEnabled(row != 0);
          dnBtn.setEnabled(row != this._alPropsList.size()-1);
          }
          mC.gridwidth = GridBagConstraints.REMAINDER;
        }
        mGridbag.setConstraints(sizer,mC);
        mMainPanel.add(sizer);
        mC.gridwidth = 1;

      } // for i


    } // for row of multiple props


    // main stuff's layout
    if (sMainPanel != null && mMainPanel != null) {
      JPanel jtmp = new JPanel();
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;
      jtmp.setLayout(gridbag);

      c.gridwidth = GridBagConstraints.REMAINDER;
      gridbag.setConstraints(sMainPanel,c);
      jtmp.add(sMainPanel);

      sizer = new JPanel();
      sizer.setSize(1,10);
      gridbag.setConstraints(sizer,c);
      jtmp.add(sizer);

      gridbag.setConstraints(mMainPanel,c);
      jtmp.add(mMainPanel);

      scrollPanel = new JPanel();
      scrollPanel.add(jtmp, BorderLayout.NORTH);
    }
    else if (sMainPanel != null) {
      scrollPanel = sMainPanel;
    }
    else if (mMainPanel != null) {
      scrollPanel = new JPanel();
      scrollPanel.add(mMainPanel, BorderLayout.NORTH);
    }
    else {
      scrollPanel = new JPanel();
    }


    scrollPane = new JScrollPane(scrollPanel);

    sizer = new JPanel();
    sizer.setSize(100,1);
    this._editPanel.add(sizer, BorderLayout.WEST);
    sizer=new JPanel();
    sizer.setSize(100,1);
    this._editPanel.add(sizer, BorderLayout.EAST);
    sizer=new JPanel();
    sizer.setSize(1,100);
    this._editPanel.add(sizer, BorderLayout.NORTH);

    this._editPanel.add(scrollPane, BorderLayout.CENTER);

    // extra buttons
    if (this.getFieldType().isExtraButtons()) {
      JPanel panel = new JPanel();

      JButton update = new JButton(adapter.getString("JSPFieldData.button.addline")); //$NON-NLS-1$
      update.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          addTableRow();
        }
      });

      panel.add(update, BorderLayout.CENTER);

      update = new JButton(adapter.getString("JSPFieldData.button.removeline")); //$NON-NLS-1$
      update.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          removeTableRow();
        }
      });
      if (this._alPropsList.size() <= 1) {
        update.setEnabled(false);
      }

      panel.add(update, BorderLayout.CENTER);

      this._editPanel.add(panel, BorderLayout.SOUTH);
    }

    return this._editPanel;
  } // getEditPanel

  private String[] getSaFormTemplates() {
    // Form templates
    String[] formTemplates = JSPFieldData._saFormTemplates;
    if (adapter.isRepOn()) {
      String[] repVals = adapter.getDesenho().getFormTemplatesList();
      if (null == repVals)
        repVals = new String[0];
      formTemplates = new String[repVals.length + 1];
      formTemplates[0] = "";
      for (int kk = 0; kk < repVals.length; kk++)
        formTemplates[kk + 1] = repVals[kk];
    }

    JSPFieldData._saFormTemplates = formTemplates;

    return _saFormTemplates;
  }

  private String[] getCatalogue() {
    Object[] catalogue = adapter.getDesenho().getCatalogue().toArray();
    String[] ret = new String[catalogue.length];
    for (int i = 0; i < catalogue.length; i++)
      ret[i] = ((Atributo) catalogue[i]).getNome();
    return ret;
  }

  private JButton makeControlButton(String asImageIconName,
      String asToolTipText,
      ActionListener l,
      Dimension adButtonDimension) {

    ImageIcon ic = new ImageIcon(adapter.getJanela().createImage(asImageIconName, false));
    JButton ret = new JButton(ic);
    ret.setMaximumSize(adButtonDimension);
    ret.setMinimumSize(adButtonDimension);
    ret.setPreferredSize(adButtonDimension);
    ret.setToolTipText(asToolTipText);
    ret.addActionListener(l);

    return ret;
  }


  private void addTableRow() {
    try {this.saveData(false);}catch (Exception e) {}
    this.addListProp();

    this.revalidateParent();
  }

  private void removeTableRow() {
    this.removeListProp();
    try {this.saveData(false);}catch (Exception e) {}

    this.revalidateParent();
  }

  public void saveData()
  throws FieldDataException {
    this.saveData(true);
  }

  @SuppressWarnings("unchecked")
  public void saveData(boolean abValidate)
  throws FieldDataException {

    Integer iRow = null;
    Integer iProp = null;
    String stmp = null;
    JComponent jValue = null;
    boolean bError = false;
    FieldDataException fde = null;

    // first single props
    for (int i=0; i < this._alEditSingleProps.size(); i++) {

      iProp = this._alEditSingleProps.get(i);

      jValue = getEditComponent(iProp);

      if (jValue == null) {
        stmp = null;
      }
      else {
        switch (iProp.intValue()) {
        case JSPFieldData.nPROP_CONNECTOR_SELECT:
          String clazz = ((NameValuePair<String, String>)
              (((JComboBox)jValue).getSelectedItem())).getValue();
          stmp = clazz == null ? "" : clazz;
          break;
        case JSPFieldData.nPROP_DATA_TYPE:
        case JSPFieldData.nPROP_ALIGNMENT:
        case JSPFieldData.nPROP_DATE_FORMAT:
        case JSPFieldData.nPROP_HOUR_FORMAT:
        case JSPFieldData.nPROP_CHART_TEMPLATE:
        case JSPFieldData.nPROP_FILE_PERMISSION:
        case JSPFieldData.nPROP_DATASOURCE:
        case JSPFieldData.nPROP_FORM_TEMPLATE:
        case JSPFieldData.nPROP_FILE_SIGNATURE_TYPE:
        case JSPFieldData.nPROP_BUTTON_TYPE:
        case JSPFieldData.nPROP_LIST_OF_POPUP_FLOWS:
          stmp = (String)(((JComboBox)jValue).getSelectedItem());
          break;
        case JSPFieldData.nPROP_VAR_NAME:
          stmp = (String) (((JComboBox) jValue).getSelectedItem());
          break;
        case JSPFieldData.nPROP_PP_PASS_TO_LINK:
        case JSPFieldData.nPROP_USE_LINKS:
        case JSPFieldData.nPROP_OUTPUT_ONLY:
        case JSPFieldData.nPROP_EMPTY_NOT_ALLOWED:
        case JSPFieldData.nPROP_SERVICE_PRINT:
        case JSPFieldData.nPROP_SERVICE_EXPORT:
        case JSPFieldData.nPROP_OPEN_NEW_WINDOW:
        case JSPFieldData.nPROP_PROC_LINK:
        case JSPFieldData.nPROP_ONCHANGE_SUBMIT:
        case JSPFieldData.nPROP_TEXT_VALUE:
        case JSPFieldData.nPROP_CURRDATE_IF_EMPTY:
        case JSPFieldData.nPROP_FILE_SIGN_NEW:
        case JSPFieldData.nPROP_FILE_SIGN_METHOD:
        case JSPFieldData.nPROP_FILE_SIGN_EXISTING:
        case JSPFieldData.nPROP_FILE_UPLOAD_PRESERVE_EXT:
        case JSPFieldData.nPROP_CACHE_HINT:
        case JSPFieldData.nPROP_FILE_IS_IMAGE:
        case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_PROCESSING:
        case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_VALIDATION:
        case JSPFieldData.nPROP_KEEP_SCROLL_ONLOAD:
        case JSPFieldData.nPROP_BUTTON_CONFIRM_ACTION:
          stmp = String.valueOf(((JCheckBox)jValue).isSelected());
          break;
        case JSPFieldData.nPROP_TEXT_AREA:
        	 try {
             	if(jValue != null)
             		stmp = ((JTextArea)jValue).getText();
     		} catch (Exception e) {
     			logger.error("jValue == null" + e);
     		}
          break;
        default:
          // DEFAULT IS TEXT...
          stmp = ((JTextField)jValue).getText();
        } // switch
      }

      try {
        validateData(iProp,stmp);
      }
      catch (FieldDataException fdexc) {
        if (!bError) {
          // first time
        	if(null != fdexc)
          fde = fdexc;
        }
        else {
          fde.append(fdexc);
        }
        bError = true;

        // property not ok; abort saving
        continue;
      }

      // prop's value ok!
      this.setProperty(iProp, stmp);

    } // for i


    // then multiple props
    for (int row=0; row < this._alPropsList.size(); row++) {

      iRow = new Integer(row);

      for (int i=0; i < this._alEditMultipleProps.size(); i++) {

        iProp = this._alEditMultipleProps.get(i);

        jValue = getEditComponent(iRow, iProp);

        if (jValue == null) {
          stmp = null;
        }
        else {
          switch (iProp.intValue()) {
          case JSPFieldData.nPROP_CONNECTOR_SELECT:
            String clazz = ((NameValuePair<String, String>)
                (((JComboBox)jValue).getSelectedItem())).getValue();
            stmp = clazz == null ? "" : clazz;
            break;
          case JSPFieldData.nPROP_DATA_TYPE:
          case JSPFieldData.nPROP_ALIGNMENT:
          case JSPFieldData.nPROP_CHART_TEMPLATE:
          case JSPFieldData.nPROP_DATE_FORMAT:
          case JSPFieldData.nPROP_HOUR_FORMAT:
          case JSPFieldData.nPROP_FILE_PERMISSION:
          case JSPFieldData.nPROP_DATASOURCE:
          case JSPFieldData.nPROP_FORM_TEMPLATE:
          case JSPFieldData.nPROP_FILE_SIGNATURE_TYPE:
          case JSPFieldData.nPROP_BUTTON_TYPE:
            stmp = (String)(((JComboBox)jValue).getSelectedItem());
            break;
          case JSPFieldData.nPROP_VAR_NAME:
            stmp = (String) (((JComboBox) jValue).getSelectedItem());
            break;
          case JSPFieldData.nPROP_PP_PASS_TO_LINK:
          case JSPFieldData.nPROP_USE_LINKS:
          case JSPFieldData.nPROP_OUTPUT_ONLY:
          case JSPFieldData.nPROP_EMPTY_NOT_ALLOWED:
          case JSPFieldData.nPROP_SERVICE_PRINT:
          case JSPFieldData.nPROP_SERVICE_EXPORT:
          case JSPFieldData.nPROP_OPEN_NEW_WINDOW:
          case JSPFieldData.nPROP_PROC_LINK:
          case JSPFieldData.nPROP_ONCHANGE_SUBMIT:
          case JSPFieldData.nPROP_TEXT_VALUE:
          case JSPFieldData.nPROP_CURRDATE_IF_EMPTY:
          case JSPFieldData.nPROP_FILE_SIGN_NEW:
          case JSPFieldData.nPROP_FILE_SIGN_METHOD:
          case JSPFieldData.nPROP_FILE_SIGN_EXISTING:
          case JSPFieldData.nPROP_FILE_UPLOAD_PRESERVE_EXT:
          case JSPFieldData.nPROP_CACHE_HINT:
          case JSPFieldData.nPROP_FILE_IS_IMAGE:
          case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_PROCESSING:
          case JSPFieldData.nPROP_BUTTON_IGNORE_FORM_VALIDATION:
          case JSPFieldData.nPROP_KEEP_SCROLL_ONLOAD:
          case JSPFieldData.nPROP_BUTTON_CONFIRM_ACTION:
            stmp = String.valueOf(((JCheckBox)jValue).isSelected());
            break;
          case JSPFieldData.nPROP_TEXT_AREA:
        	  try {
                	if(jValue != null)
                		stmp = ((JTextArea)jValue).getText();
        		} catch (Exception e) {
        			logger.error("jValue == null" + e);
        		}
            
            break;
          default:
            // DEFAULT IS TEXT...
            stmp = ((JTextField)jValue).getText();
          } // switch
        }

        try {
          validateData(iRow,iProp,stmp);
        }
        catch (FieldDataException fdexc) {
          if (!bError) {
            // first time
        	  if(null != fdexc)
            fde = fdexc;
          }
          else {
            fde.append(fdexc);
          }
          bError = true;

          // property not ok; abort saving
          continue;
        }

        // prop's value ok!
        this.setProperty(iRow, iProp, stmp);

      } // for i
    } // for row

    if (abValidate && bError) {
      throw fde;
    }
  }

  private void validateData(Integer aiPropID, String asValue)
  throws FieldDataException {
    this.validateData(null,aiPropID,asValue);
  }

  private void validateData(Integer aiRow, Integer aiPropID, String asValue)
  throws FieldDataException {

    Integer itmp = null;
    int ntmp = -1;

    // first check required props
    if (this._alRequiredProps.contains(aiPropID)) {
      if (asValue == null || asValue.equals("") || asValue.trim().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
        if (this.getEditComponent(aiPropID).isEnabled())
          throw new FieldDataException(adapter,aiRow,aiPropID,true);
      }
    }

    // now check prop's value
    if (this._hmPropTypes.containsKey(aiPropID)) {
      itmp = this._hmPropTypes.get(aiPropID);

      switch (itmp.intValue()) {
      case JSPFieldData.nDATA_TYPE:
        if (asValue.equals(JSPFieldData.sCHOOSE)) {
          throw new FieldDataException(adapter,aiRow,aiPropID,false);
        }
        break;
      case JSPFieldData.nPOSITIVE_NUMBER_PERCENT:
        // check if there's an end % if so, remove it and validate
        if (StringUtils.endsWith(asValue, "%")) {
          asValue = asValue.substring(0, asValue.length()-1);
        }
        // now validate in the same way as a positive number
      case JSPFieldData.nPOSITIVE_NUMBER:
        if (asValue != null && !asValue.equals("")) {
          try {
            ntmp = Integer.parseInt(asValue);
          }
          catch (Exception e) {
            ntmp = -1;
          }
          if (ntmp <= 0) {
            throw new FieldDataException(adapter,aiRow,aiPropID,false);
          }
        }
        break;
      case JSPFieldData.nALIGNMENT:
        if (asValue.equals(JSPFieldData.sCHOOSE)) {
          throw new FieldDataException(adapter,aiRow,aiPropID,false);
        }
        break;
      case JSPFieldData.nCHART_TEMPLATES:
        if (asValue.equals(JSPFieldData.sCHOOSE)) {
          throw new FieldDataException(adapter,aiRow,aiPropID,false);
        }
        break;
      case JSPFieldData.nDATE_FORMAT:
      case JSPFieldData.nNOT_CHOSEN:
        if (asValue.equals(JSPFieldData.sCHOOSE)) {
          throw new FieldDataException(adapter,aiRow,aiPropID,false);
        }
        break;
      default:
      }
    }
  }
  
  private NameValuePair<String, String> getConnectorItem(String stmp) {
    NameValuePair<String, String> retObj =
      new NameValuePair<String, String>(sCHOOSE);
    for (NameValuePair<String, String> connector : getConnectors()) {
      String clazz = connector.getValue();
      if (clazz != null && StringUtils.equals(stmp, clazz)) {
        retObj = connector;
        break;
      }
    }
    return retObj;
  }

  class PropDependency {
    public static final int nENABLE = 0;
    public static final int nDISABLE = 1;

    public static final int nUSER_DEFINED = 0;
    public static final int nNOT_EMPTY = 1;
    public static final int nEMPTY = 2;
    public static final int nTRUE = 3;
    public static final int nFALSE = 4;
    public static final int nEMPTY_OR_VALUE = 5;

    private int _nValueAction = -1;
    private int _nValueType = -1;
    private Object _oValue = null;
    private HashMap<Integer,PropDependencyItem> _hmDeps = null;

    public PropDependency(int anProp,
        int anValueAction,
        int anValueType,
        Object aoValue) {
      this._nValueAction = anValueAction;
      this._nValueType = anValueType;
      this._oValue = aoValue;
      this._hmDeps = new HashMap<Integer, PropDependencyItem>();
    }
    public PropDependency(int anProp,
        int anValueAction,
        Object aoValue) {
      this(anProp,
          anValueAction,
          PropDependency.nUSER_DEFINED,
          aoValue);
    }
    public PropDependency(int anProp,
        int anValueAction,
        int anValueType) {

      this(anProp, anValueAction, anValueType, null);
    }


    public void addDependency(PropDependencyItem apdiItem) {
      Integer iProp = new Integer(apdiItem.getProp());

      this._hmDeps.put(iProp,apdiItem);
    }

    public boolean isDependent(int anProp) {
      boolean retObj = false;

      Integer itmp = new Integer(anProp);

      if (this._hmDeps.containsKey(itmp)) {
        retObj = true;
      }

      return retObj;
    }


    public boolean isOn(Object aoValue) {
      return this.isOn(aoValue, false);
    }

    public boolean isOn(Object aoValue, boolean abStringValue) {
      boolean retObj = false;

      boolean bValue = false;
      String sValue = null;
      String sLocalValue = null;

      try {
        switch (this._nValueType) {
        case PropDependency.nEMPTY_OR_VALUE:
        case PropDependency.nUSER_DEFINED:
          // text
          pt.iflow.api.utils.Logger.debug("", this, "", "  ==> aoValue: '"+aoValue+"'; _oValue: '"+_oValue+"'");
          
          if (aoValue == null && this._oValue == null) {
            retObj = true;
          }
          else if(PropDependency.nEMPTY_OR_VALUE == this._nValueType && "".equals(aoValue)) {
            retObj = true;
          }
          else if (aoValue != null && this._oValue != null) {
            sValue = (String)aoValue;
            sLocalValue = (String)this._oValue;
            retObj = sValue.equals(sLocalValue);
          }
          break;
        case PropDependency.nNOT_EMPTY:
          // text
          if (aoValue != null) {
            sValue = (String)aoValue;     
            if (sValue != null && !sValue.equals("")) { //$NON-NLS-1$
              retObj = true;
            }
          }
          break;
        case PropDependency.nEMPTY:
          // text
          if (aoValue == null) {
            retObj = true;
          }
          else {
            sValue = (String)aoValue;     
            if (sValue == null || sValue.equals("")) { //$NON-NLS-1$
              retObj = true;
            }
          }
          break;
        case PropDependency.nTRUE:
          // boolean
          if (aoValue != null) {
            if (abStringValue) {
              sValue = (String)aoValue;
              if (!sValue.equals("")) { //$NON-NLS-1$
                bValue = (new Boolean(sValue)).booleanValue();
              }
            }
            else {
              bValue = ((Boolean)aoValue).booleanValue();
            }
            if (bValue) {
              retObj = true;
            }
          }
          break;
        case PropDependency.nFALSE:
          // boolean
          if (aoValue != null) {
            if (abStringValue) {
              sValue = (String)aoValue;
              if (!sValue.equals("")) { //$NON-NLS-1$
                bValue = (new Boolean(sValue)).booleanValue();
              }
            }
            else {
              bValue = ((Boolean)aoValue).booleanValue();
            }
            if (!bValue) {
              retObj = true;
            }
          }
          break;
        default:
        }
      }
      catch (Exception e) {
        retObj = false;
      }

      if (this._nValueAction == PropDependency.nDISABLE) {
        retObj = !retObj;
      }

      return retObj;
    }

    public PropDependencyItem[] getDependencies() {
      PropDependencyItem[] retObj = new PropDependencyItem[this._hmDeps.size()];

      Iterator<Integer> iter = this._hmDeps.keySet().iterator();
      Integer itmp = null;
      int i=0;
      while (iter != null && iter.hasNext()) {
        itmp = iter.next();
        retObj[i++] = this.getDependencyItem(itmp);
      }

      return retObj;
    }

    public PropDependencyItem getDependencyItem(Integer aiProp) {
      PropDependencyItem retObj = null;

      if (this._hmDeps.containsKey(aiProp)) {
        retObj = this._hmDeps.get(aiProp);
      }

      return retObj;
    }

  }

  class PropDependencyItem {
    private int _nProp = -1; 
    private int _nAction = -1;

    public PropDependencyItem (int anProp, int anAction) {
      this._nProp = anProp;
      this._nAction = anAction;
    }

    public int getProp() {
      return this._nProp;
    }

    public int getAction() {
      return this._nAction;
    }
  }

  class DependencyListener {
    protected JComponent _jc = null;
    protected PropDependency _pd = null;
    protected ArrayList<JComponent> _alDepComp = null;
    protected ArrayList<JLabel> _alDepCompLabels = null;

    public DependencyListener(JComponent ajc,
        PropDependency apd,
        ArrayList<JComponent> aalDependentComponents,
        ArrayList<JLabel> aalDependentComponentLabels) {
      this._jc = ajc;
      this._pd = apd;
      this._alDepComp = aalDependentComponents;
      this._alDepCompLabels = aalDependentComponentLabels;
    }

    protected void updateDependents(Object aoValue) {
      boolean bOn = this._pd.isOn(aoValue);

      JComponent jctmp = null;
      JLabel jltmp = null;
      PropDependencyItem pdi = null;
      PropDependencyItem[] pdia = this._pd.getDependencies();

      for (int j=0; pdia != null && j < pdia.length; j++) {
        pdi = pdia[j];
        jctmp = this._alDepComp.get(j);
        jltmp = this._alDepCompLabels.get(j);
        if (pdi.getAction() == PropDependency.nENABLE) {
          jctmp.setEnabled(bOn);
        }
        else {
          jctmp.setEnabled(!bOn);
        }

        jltmp.setEnabled(jctmp.isEnabled());
      }
    }
  }

  class DependencyComboListener 
  extends DependencyListener 
  implements ItemListener {

    public DependencyComboListener(JComponent ajc,
        PropDependency apd,
        ArrayList<JComponent> aalDependentComponents,
        ArrayList<JLabel> aalDependentComponentLabels) {
      super(ajc, apd, aalDependentComponents, aalDependentComponentLabels);
    }

    public void itemStateChanged(ItemEvent e) {
      String sValue = null;
      if (e.getStateChange() == ItemEvent.SELECTED) {
        sValue = (String)e.getItem();
      }

      updateDependents(sValue);
    }    
  }

  class DependencyCheckListener 
  extends DependencyListener 
  implements ItemListener {

    public DependencyCheckListener(JComponent ajc,
        PropDependency apd,
        ArrayList<JComponent> aalDependentComponents,
        ArrayList<JLabel> aalDependentComponentLabels) {
      super(ajc, apd, aalDependentComponents, aalDependentComponentLabels);
    }

    public void itemStateChanged(ItemEvent e) {
      boolean bChecked = false;
      if (e.getStateChange() == ItemEvent.SELECTED) {
        bChecked = true;    
      }

      updateDependents(new Boolean(bChecked));
    }    
  }

  class DependencyTextListener 
  extends DependencyListener 
  implements ActionListener {

    public DependencyTextListener(JComponent ajc,
        PropDependency apd,
        ArrayList<JComponent> aalDependentComponents,
        ArrayList<JLabel> aalDependentComponentLabels) {
      super(ajc, apd, aalDependentComponents, aalDependentComponentLabels);
    }

    public void actionPerformed(ActionEvent e) {
      String sValue = ((JTextField)_jc).getText();

      updateDependents(sValue);
    }
  }

  private class ReorderActionListener implements ActionListener {
    private int operation;
    private int position;

    public ReorderActionListener(int operation, int position) {
      this.operation = operation;
      this.position = position;
    }

    public void actionPerformed(ActionEvent ev) {
      Integer from = position; // java 5 take care of wrapping primitive types
      Integer to = position;

      switch(operation) {
      case REORDER_UP:
        to = position-1;
        if(to < 0) to = 0;
        break;
      case REORDER_DOWN:
        to = position+1;
        if(to >= JSPFieldData.this._alPropsList.size()) to = JSPFieldData.this._alPropsList.size()-1;
        break;
      }

      if(from == to) return;
      // then multiple props
      for (int i=0; i < JSPFieldData.this._alEditMultipleProps.size(); i++) {
        Integer iProp = JSPFieldData.this._alEditMultipleProps.get(i);

        // swap edit components.
        JComponent item = getEditComponent(to, iProp);
        setEditComponent(to, iProp, getEditComponent(from, iProp));
        setEditComponent(from, iProp, item);
      }

      // for each edit component, update JSPFieldData.this._alPropsList
      try {JSPFieldData.this.saveData(false);}catch (Exception e) {}
      JSPFieldData.this.revalidateParent();
    }
  }


  protected static DataTypeInterface loadDataType(FlowEditorAdapter adapter, String dataTypeClass) {
    DataTypeInterface result = null;
    try {
      Class<?> c = adapter.getRepository().loadClass(dataTypeClass);
      result = (DataTypeInterface) c.newInstance();
    } catch (Throwable e) {
      logger.warn("Could not load Data Type class", e);
    }

    return result;
  }

  public void requestSubFlowCatalogVariables(String subFlowName) {
    String[] fieldsColumnNames = new String[] {
        adapter.getString("AlteraAtributosSubFlow.flowVars"),
        adapter.getString("AlteraAtributosSubFlow.subFlowVars"),
    };
    byte[] bXml = adapter.getRepository().getSubFlow(subFlowName);
    XmlFlow _xmlflow = null;
    try {
      _xmlflow = FlowMarshaller.unmarshal(bXml);
    } catch (JAXBException e) {
      e.printStackTrace();
    }

    XmlCatalogVarsType xmlcv = _xmlflow.getXmlCatalogVars();
    String[][] inputFields;
    if(xmlcv.getXmlAttribute().size() > 0 && xmlcv.getXmlCatalogVarAttribute().size() == 0) {
      inputFields = new String[xmlcv.getXmlAttribute().size()][fieldsColumnNames.length];
      for (int i = 0; i < xmlcv.getXmlAttribute().size(); i++) {
        XmlAttributeType attr = xmlcv.getXmlAttribute().get(i);
        inputFields[i][0] = attr.getName();
        inputFields[i][1] = attr.getDescription();
      }
    } else {
      inputFields = new String[xmlcv.getXmlCatalogVarAttribute().size()][fieldsColumnNames.length];
      for (int i = 0; i < xmlcv.getXmlCatalogVarAttribute().size(); i++) {
        XmlCatalogVarAttributeType attr = xmlcv.getXmlCatalogVarAttribute().get(i);
        inputFields[i][0] = attr.getName();
        inputFields[i][1] = attr.getDataType();
      }
    }
  }

  private static class EditableComboListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      JComboBox jcb = (JComboBox) e.getSource();
      int idx = jcb.getSelectedIndex();
      if(idx < 0) { // user typed this entry
        Object obj = jcb.getSelectedItem();
        final int pos = 0;
        jcb.removeItemAt(pos);
        jcb.insertItemAt(obj, pos);
        jcb.setSelectedIndex(pos);
      }
    }
    
  }

} // class

class ConnectorNameValuePair<N, V> extends NameValuePair<N, V> {

  public ConnectorNameValuePair(NameValuePair<N, V> pair) {
    setName(pair.getName());
    setValue(pair.getValue());
  }
  
  @Override
  public String toString() {
    return getName().toString();
  }
  
}
