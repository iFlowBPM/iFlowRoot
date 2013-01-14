package pt.iflow.blocks;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.blocks.msg.Messages;
import pt.iflow.connector.document.Document;


/**
 * <p>Title: BlockDataImport</p>
 * <p>Description: Bloco "biblioteca" para importacao de Dados</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class BlockDataImport extends BlockData {
  // Form fields
  private static final String FIELD_FILE = "file";
  private static final String FIELD_FILE_TYPE = "type";
  
  // values shared beteween iFlow
  private static final String _sSheetNum = "sheet";  //$NON-NLS-1$ 
  private static final String _sInitLine = "initLine";  //$NON-NLS-1$ 
  private static final String _sColType = "colType";  //$NON-NLS-1$ 
  private static final String _sHasHeader = "hasHeader";  //$NON-NLS-1$ 

  private static final String _sColName = "name";  //$NON-NLS-1$ 
  private static final String _sVarName = "var";  //$NON-NLS-1$ 
  private static final String _sModeName = "mode";  //$NON-NLS-1$ 
  private static final String _sFormatName = "format";  //$NON-NLS-1$ 
  
  private static final String _sColNumberVal = "position";  //$NON-NLS-1$ 
  private static final String _sColNameVal = "name";  //$NON-NLS-1$ 

  private static final String _sModeWriteAllways = "write_always";  //$NON-NLS-1$ 
  private static final String _sModeWriteOnce = "write_once";  //$NON-NLS-1$ 

  // Form attributes
  private static final String _sHeader = "header";  //$NON-NLS-1$ 
  private static final String _sSubheader = "subheader";  //$NON-NLS-1$ 
  private static final String _sMessage = "message";  //$NON-NLS-1$ 
  private static final String _sFileLabel = "file";  //$NON-NLS-1$ 
  private static final String _sFileVar = "file_var";  //$NON-NLS-1$ 
  
  private static final String INT_FMT  ="INT";
  private static final String NUM_FMT  ="NUM";
  private static final String DATE_FMT  ="DATE";
  
  // for auto
  protected static final String sCSV_SUFFIX = ".csv";
  protected static final String sEXCEL_SUFFIX = ".xls";
  protected static final String sODS_SUFFIX = ".ods";
  protected static final String sXLSX_SUFFIX = ".xlsx";
  
  protected static final String BUTTON_KEY = "BlockDataImport.BUTTON_KEY";
  protected static final String BUTTON_KEY_IMPORT = "BlockDataImport.IMPORT";
  protected static final String BUTTON_KEY_BACK = "BlockDataImport.BACK";

  public BlockDataImport(int anFlowId,int id, int subflowblockid, String filename) {
    super(BlockData.nIMPORT,anFlowId,id, subflowblockid, filename);
    hasInteraction = true;
  }

  
  

  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    StringBuffer logMsg = new StringBuffer();
    
    String button = procData.getTempData(BUTTON_KEY);
    if(StringUtils.equals(button, BUTTON_KEY_BACK)) {
      outPort = portError;
    }
    procData.setTempData(BUTTON_KEY, null);

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }




  // return error or null if no errors occur
  public String importSpreadSheet(UserInfoInterface userInfo, ProcessData procData, String sType, String sFileName, byte[] data) {
    String retObj = null;
    procData.setTempData(BUTTON_KEY, BUTTON_KEY_IMPORT);
    
    int flowid = procData.getFlowId();
    int pid    = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    Logger.trace(this,"importSpreadSheet",login + " call with flowid="+flowid + ", pid=" + pid +", subpid="+subpid);

    try {

      HashMap<String,String> hmColInfo = new HashMap<String, String>();
      HashMap<String,Integer> hmVarModes = new HashMap<String, Integer>();

      String sName = null;
      String sVar = null;
      String sMode = null;
      String sFmt = null;
      int ntmp = 0;
      int iStartLine = 1;
      int iSheet = 1;
      int iColLabelType = BlockData.nCOLUMN_LABEL;
      boolean bHasHeader = true;

      if(StringUtils.isNotEmpty(this.getAttribute(_sSheetNum)))
        iSheet = Integer.parseInt(this.getAttribute(_sSheetNum));

      if(StringUtils.isNotEmpty(this.getAttribute(_sInitLine)))
        iStartLine = Integer.parseInt(this.getAttribute(_sInitLine));

      if(StringUtils.isNotEmpty(this.getAttribute(_sColType))) {
        if(this.getAttribute(_sColType).equals(_sColNameVal))
          iColLabelType = BlockData.nCOLUMN_LABEL;
        if(this.getAttribute(_sColType).equals(_sColNumberVal))
          iColLabelType = BlockData.nCOLUMN_NUMBER;
      }

      if(StringUtils.isNotEmpty(this.getAttribute(_sHasHeader))) {
        bHasHeader = new Boolean(this.getAttribute(_sHasHeader)).booleanValue();
      }

      Map<String,Format> formatters = new HashMap<String,Format>();
      
      // read formatters
      Format intFmt = null;
      Format numFmt = null;
      Format dateFmt = null;
      
      FlowSetting [] settings = BeanFactory.getFlowSettingsBean().getFlowSettings(flowid);
      for(FlowSetting setting : settings) {
        if(Const.sFLOW_DATE_FORMAT.equals(setting.getName())) {
          String fmt = setting.getValue();
          if(StringUtils.isBlank(fmt)) fmt = Const.sDEF_DATE_FORMAT;
          dateFmt = DateUtility.getBlockDateFormat(userInfo.getOrganization(), fmt);
        } else if(Const.sFLOW_INT_FORMAT.equals(setting.getName())) {
          String fmt = setting.getValue();
          if(StringUtils.isBlank(fmt)) fmt = Const.sDEF_INT_FORMAT;
          intFmt = new DecimalFormat(fmt);
        } else if(Const.sFLOW_FLOAT_FORMAT.equals(setting.getName())) {
          String fmt = setting.getValue();
          if(StringUtils.isBlank(fmt)) fmt = Const.sDEF_FLOAT_FORMAT;
          numFmt = new DecimalFormat(fmt);
        }
      }
      
      // read 

      for (int ii=0; (sName = this.getAttribute(_sColName + ii)) != null; ii++) {
        sVar = this.getAttribute(_sVarName + ii);
        sMode = this.getAttribute(_sModeName + ii);
        sFmt = this.getAttribute(_sFormatName + ii);

        ntmp = BlockData.nIMPORT_MODE_WRITE_ALWAYS;
        if (sMode.equals(_sModeWriteOnce)) {
          ntmp = BlockData.nIMPORT_MODE_WRITE_ONCE;
        }
        
        if(StringUtils.isNotBlank(sFmt)) {
          Format fmt = null;
          if (INT_FMT.equals(sFmt))
            fmt = intFmt;
          else if (NUM_FMT.equals(sFmt))
            fmt = numFmt;
          else if (DATE_FMT.equals(sFmt))
            fmt = dateFmt;
          
          formatters.put(sVar, fmt);
        }

        hmColInfo.put(sName, sVar);
        hmVarModes.put(sVar, new Integer(ntmp));
      }

      if (hmColInfo.size() == 0) {
        throw new Exception("Dados para importação não configurados.");
      }

      // Somewhere get dataadapter parameter
      String adapterId = sType;
      if(StringUtils.equalsIgnoreCase(sType, "auto"))
        adapterId = sFileName.substring(sFileName.lastIndexOf('.')+1);
      DataAdapter adapter = getDataAdapter(adapterId);
      
      boolean loadOk = (null != adapter);
      
      
      
      
      
      if(loadOk)
        loadOk = adapter.loadData(data, new Properties()); // not yet complete...
      if(loadOk)
        retObj = importFromAdapter(this, userInfo, procData, hmColInfo, hmVarModes, adapter, iSheet-1, iStartLine, iColLabelType, bHasHeader, formatters);

      if(!loadOk) {
        retObj = "Ficheiro inválido.";
      }

      // save data in document var
      String sDocumentVar = this.getAttribute(_sFileVar);
      if (StringUtils.isNotBlank(sDocumentVar)) {
        Documents docBean = BeanFactory.getDocumentsBean();
        ProcessListVariable docsVar = procData.getList(sDocumentVar);
        Document doc = new DocumentData(sFileName, data);
        doc = docBean.addDocument(userInfo, procData, doc);
        docsVar.parseAndAddNewItem(String.valueOf(doc.getDocId()));
      }

    }
    catch (Exception e) {
      retObj = "Erro: " + e.getMessage();
    }

    return retObj;
  }


  public Object execute (int op, Object[] aoa) {
    Object retObj = null;
    switch(op) {
    case 1:
    // String importExcel(UserInfoInterface,ProcessData,String,String,byte[])
    retObj = this.importSpreadSheet((UserInfoInterface)aoa[0],
        (ProcessData)aoa[1],
        (String)aoa[2],
        (String)aoa[3],
        (byte[])aoa[4]);
    break;
    case 2:
      retObj = generateForm((UserInfoInterface)aoa[0], (ProcessData)aoa[1], (ServletUtils)aoa[2]);
      break;
    case 3:
      retObj = buttonBackPressed((UserInfoInterface)aoa[0], (ProcessData)aoa[1]);
      break;
    case 4:
      retObj = getAttribute(_sFileVar);
      if(null == retObj) retObj = FIELD_FILE;
      break;
    }
    return retObj;
  }

  public static void main(String [] args) {
    
  }
  
  protected String buttonBackPressed(UserInfoInterface userInfo, ProcessData procData) {
    procData.setTempData(BUTTON_KEY, BUTTON_KEY_BACK);
    return null;
  }
  
  
  /**
   * 
   * Gera um formulário com o seguinte formato:
   * 
   * header<br>
   * subheader<br>
   * textmsg<br>
   * file<br>
   * type<br>
   * botao Avaçar<br>
   * 
   * @param userInfo
   * @param procData
   * @return
   */
  protected String generateForm(UserInfoInterface userInfo, ProcessData procData, ServletUtils response) {
    procData.setTempData(BUTTON_KEY, null);
    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String txt = null;
    ProcessManager pm = BeanFactory.getProcessManagerBean();

    Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));
    
    StringBuffer sbXml = new StringBuffer();
    sbXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    sbXml.append("<form>");
    sbXml.append("<name>import</name>");
    sbXml.append("<action>doimport.jsp</action>");

      // errors
    // FIXME add errors support in proc data?
    if (procData.hasError()) {
      String errMsg = procData.getError();
      if (StringUtils.isNotEmpty(errMsg)) {
        errMsg = Utils.replaceString(errMsg,"<br/>","</text></error><error><text>");//  ugly...
        sbXml.append("<error><text>").append(errMsg).append("</text></error>");
      }
    }

    // Se tiver erro....
    // sbXml.append("<error><text>").append(stmp).append("</text></error>");

    sbXml.append("<blockdivision><columndivision>");

    Properties props = null;
    pt.iflow.blocks.form.FieldInterface field = null;
    
    // cenas: Cabecalho; File, Selection
    txt = getAttribute(_sHeader);
    if(StringUtils.isNotEmpty(txt)) {
      field = new pt.iflow.blocks.form.Header();
      props = new Properties();
      props.setProperty("text", txt);
      props.setProperty("even_field", "false");
      field.setup(userInfo, procData, props, response);
      sbXml.append(field.getXML(props));
    }
    
    txt = getAttribute(_sSubheader);
    if(StringUtils.isNotEmpty(txt)) {
      field = new pt.iflow.blocks.form.SubHeader();
      props = new Properties();
      props.setProperty("text", txt);
      props.setProperty("even_field", "true");
      field.setup(userInfo, procData, props, response);
      sbXml.append(field.getXML(props));
    }
    
    txt = getAttribute(_sMessage);
    if(StringUtils.isNotEmpty(txt)) {
      field = new pt.iflow.blocks.form.TextMessage();
      props = new Properties();
      props.setProperty("text", txt);
      props.setProperty("even_field", "false");
      field.setup(userInfo, procData, props, response);
      sbXml.append(field.getXML(props));
    }
    
    String fileVar = getAttribute(_sFileVar);
    if(StringUtils.isEmpty(fileVar)) {
      fileVar = FIELD_FILE;
    }
    
    txt = getAttribute(_sFileLabel);
    if(null == txt) txt = msg.getString("BlockDataImport.file.default_label");
    field = new pt.iflow.blocks.form.File();
    props = new Properties();
    props.setProperty("text", "");
    props.setProperty("even_field", "true");
    props.setProperty("variable", fileVar);
    props.setProperty("upload_enabled", "true");
    props.setProperty("upload_label", txt);
    props.setProperty("upload_limit", "1");
    props.setProperty("scanner_enabled", "false");
    field.setup(userInfo, procData, props, response);
    sbXml.append(field.getXML(props));
    
    field = new pt.iflow.blocks.form.Selection();  // TODO externalize
    props = new Properties();
    props.setProperty("text", msg.getString("BlockDataImport.file_type.label"));
    props.setProperty("even_field", "false");
    props.setProperty("variable", FIELD_FILE_TYPE);
    props.setProperty("value", "auto");
    props.setProperty("text_value", "false");
    props.setProperty("0_text", msg.getString("BlockDataImport.file_type.value.auto"));
    props.setProperty("0_value", "auto");
    props.setProperty("1_text", msg.getString("BlockDataImport.file_type.value.xls"));
    props.setProperty("1_value", "xls");
    props.setProperty("2_text", msg.getString("BlockDataImport.file_type.value.xlsx"));
    props.setProperty("2_value", "xlsx");
    props.setProperty("3_text", msg.getString("BlockDataImport.file_type.value.ods"));
    props.setProperty("3_value", "ods");
    props.setProperty("4_text", msg.getString("BlockDataImport.file_type.value.csv"));
    props.setProperty("4_value", "csv");
    field.setup(userInfo, procData, props, response);
    sbXml.append(field.getXML(props));
    
    sbXml.append("</columndivision></blockdivision>");

    // Botoes: Cancelar
//    sbXml.append("<button>");
//    sbXml.append("<name>back</name><text>").append(msg.getString("BlockDataImport.button.back")).append("</text>");
//    sbXml.append("<tooltip>").append(msg.getString("BlockDataImport.button.back_tooltip")).append("</tooltip>");
//    sbXml.append("if (! (/msie|MSIE 6/.test(navigator.userAgent))) { <operation>document.import.backPressed.value='1';try{if(document.import['file_add[0]']){ff=document.import['file_add[0]'];ff.parent.removeChild(ff);}}catch(err){}return true;</operation>");
//    sbXml.append("}else{ <operation>this.backPressed.value='1';try{if(this['file_add[0]']){ff=this['file_add[0]'];ff.parent.removeChild(ff);}}catch(err){}return true;</operation>}");
//    sbXml.append("</button>");

    // Botoes: Avancar
    sbXml.append("<button>");
    sbXml.append("<name>next</name><text>").append(msg.getString("BlockDataImport.button.next")).append("</text>");
    sbXml.append("<tooltip>").append(msg.getString("BlockDataImport.button.next_tooltip")).append("</tooltip>");
    sbXml.append("</button>");

    // Hidden fields
    sbXml.append("<hidden><name>flowid</name><value>").append(flowid).append("</value></hidden>");
    sbXml.append("<hidden><name>pid</name><value>").append(pid).append("</value></hidden>");
    sbXml.append("<hidden><name>subpid</name><value>").append(subpid).append("</value></hidden>");
    sbXml.append("<hidden><name>").append(Const.sMID_ATTRIBUTE).append("</name><value>").append(pm.getModificationId(userInfo, procData.getProcessHeader())).append("</value></hidden>");
    sbXml.append("<hidden><name>backPressed</name><value>0</value></hidden>");


    sbXml.append("</form>");
    
    Logger.debug(userInfo.getUtilizador(),this,"generateForm","[" + flowid + "," + pid + "," + subpid + "] " +"xml=" + sbXml.toString());
    
    String result = null;
    
    try {
      result = BlockFormulario.transformForm(userInfo, procData, "import.xsl", sbXml.toString(), true, false, response); // do not load applet
    } catch (Throwable e) {
      result = "<div style=\"margin-top:30px;font-family:verdana;font-size:11pt;font-color:red\">Ocorreu um erro a gerar a p&aacute;gina.</div>";
      Logger.error(userInfo.getUtilizador(),this,"generateForm","[" + procData.getFlowId() + "," + procData.getPid() + "," + procData.getSubPid() + "] " +"caught exception: " + e.getMessage(), e);
    }
    
    return result;
  }
  
}
