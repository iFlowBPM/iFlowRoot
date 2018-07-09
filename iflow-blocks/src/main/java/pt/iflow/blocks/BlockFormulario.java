package pt.iflow.blocks;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import javax.servlet.http.HttpSession;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.FormOperations;
import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.FormService;
import pt.iflow.api.blocks.FormUtils;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.datatypes.ArrayTableProcessingCapable;
import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iflow.api.datatypes.Link;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.documents.DocumentSessionHelper;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.processdata.ProcessVariableValue;
import pt.iflow.api.processtype.DateDataType;
import pt.iflow.api.processtype.FloatDataType;
import pt.iflow.api.processtype.IntegerDataType;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.processtype.TextDataType;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.NameValuePair;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.utils.XslTransformerFactory;
import pt.iflow.blocks.form.ConSelection;
import pt.iflow.blocks.form.DateCal;
import pt.iflow.blocks.form.FieldInterface;
import pt.iflow.blocks.form.PopupFormField;
import pt.iflow.blocks.form.SQLSelection;
import pt.iflow.blocks.form.Selection;
import pt.iflow.blocks.form.TabDivision;
import pt.iflow.blocks.form.utils.FormButton;
import pt.iflow.blocks.form.utils.FormButtonType;
import pt.iflow.blocks.form.utils.FormCache;
import pt.iflow.connector.document.Document;
import pt.iflow.search.PesquisaProcessoSessionData;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormFile;

/**
 * <p>
 * Title: BlockFormulario
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author iKnow
 */

@SuppressWarnings("unchecked")
public class BlockFormulario extends Block implements FormOperations {
  // Ports
  public Port portIn, portFront;
  private Port blockPopUp = null;

  private static final String sJSP_FORM = "form.jsp";
  private static final String sJSP_OPEN_POPUP = "form.jsp";
  private static final String sJSP_CLOSE_POPUP = "closePopup.jsp";

  protected static final int nTYPE_FORM = 0;
  protected static final int nTYPE_DETALHE = 1;
 

  public final static int nTXT_ATTR_IDX = 0;
  public final static int nLIST_ATTR_IDX = 1;

  public final static String sFORM_NAME = "dados";

  private static final String DEFAULT_PROPS_SEP = ",";

  protected String sJSP = sJSP_FORM;
  protected String sJSPOpenPopup = sJSP_OPEN_POPUP;
  protected String sJSPClosePopup = sJSP_CLOSE_POPUP;
  protected int nTYPE = nTYPE_FORM;

  public BlockFormulario(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = true;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portFront;
    return retObj;
  }

  public Port getEventPort() {
    return portEvent;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  /**
   * 
   * Block's initialization code.
   * 
   * @param dataSet
   *          data
   * @return next page (block's page)
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    // Variables
    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();
    String login = userInfo.getUtilizador();

    String description = this.getDescription(userInfo, procData);
    String url = this.getUrl(userInfo, procData);

    Logger.trace(this, "before", login + " call with subpid=" + subpid + ",pid=" + pid + ",flowid=" + flowid);

    String nextPage = url;

    ProcessManager pm = BeanFactory.getProcessManagerBean();
    Activity activity = null;
    String stmp = null;

    try {
      // Get the ProcessManager EJB

      activity = new Activity(login, flowid, pid, subpid, 0, 0, description, Block.getDefaultUrl(userInfo, procData), 1);
      //activity.setRead();
      activity.mid = procData.getMid();
      pm.updateActivity(userInfo, activity);

    } catch (Exception e) {
      Logger.error(login, this, "before", "Caught an unexpected exception scheduling activities: "
    		  , e);
    }

    stmp = procData.getAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR);
    if (StringUtils.isEmpty(stmp)) {
      stmp = procData.getCreator();
      if (!login.equals(stmp)) {
        procData.setAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR, "true");
      } else {
        if (pm != null) {
          //if process is only in memory this validation makes no sense, so we skip it and optimize processing times
          if(pid!=-10){
			  ListIterator it = pm.getProcessActivities(userInfo, flowid, pid, subpid);
		      while (it != null && it.hasNext()) {
		        activity = (Activity) it.next();
		        if (!login.equals(activity.userid)) {
		          // Another user has activity scheduled
		          procData.setAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR, "true");
		          break;
		        }
		      }  
          }          
        }
      }
    }

    this.init();

    return nextPage;
  }

  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executa a acção do bloco.
   * 
   * @param dataSet
   * @return
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    procData.clearError();
    if (blockPopUp != null){
      Port returnPort = new Port();
      returnPort.setConnectedBlockId(blockPopUp.getConnectedBlockId());
      returnPort.setName(blockPopUp.getName());
      returnPort.setConnectedPortName(blockPopUp.getConnectedPortName());

      blockPopUp = null;

      return returnPort;
    }
    return portFront;
  }

  @Override
  public void onEventFired(UserInfoInterface userInfo, ProcessData procData) {
    procData.clearError();
  }

  /**
   * clean/remake block's cache
   */
  public void refreshCache(final UserInfoInterface userInfo) {
    FormCache.refreshCache(this);
  }

  protected void init() {
    FormCache.init(this);
  }


  protected List<FormButton> makeDefaultButtons() {
    return FormCache.getDefaultButtons();
  }

  public String generateForm(final UserInfoInterface userInfo, final ProcessData procData,
      final Map<String, String> ahmHiddenFields, final ServletUtils response) {
    return generateForm(this, userInfo, procData, ahmHiddenFields, false, response);
  }

  public static String generateForm(final BlockFormulario block, final UserInfoInterface userInfo, final ProcessData procData,
      final Map<String, String> ahmHiddenFields, final boolean abForceDisabled, final ServletUtils response,
      final String jspOverride, final List<FormButton> buttonsOverride) {
    return generateForm(block, userInfo, procData, ahmHiddenFields, abForceDisabled, FormService.NONE, -1, null, response,
        jspOverride, buttonsOverride, false);
  }

  protected static String generateForm(final BlockFormulario abBlock, final UserInfoInterface userInfo, final ProcessData procData,
      final Map<String, String> ahmHiddenFields, final boolean abForceDisabled, final ServletUtils response) {
    return generateForm(abBlock, userInfo, procData, ahmHiddenFields, abForceDisabled, FormService.NONE, -1, response);
  }

  protected static String generateForm(final BlockFormulario abBlock, final UserInfoInterface userInfo, final ProcessData procData,
      final Map<String, String> ahmHiddenFields, final boolean abForceDisabled, final ProcessData procData2,
      final ServletUtils response) {
    return generateForm(abBlock, userInfo, procData, ahmHiddenFields, abForceDisabled, FormService.NONE, -1, procData2, response,
        null, null, false);
  }

  public static String generateForm(final BlockFormulario abBlock, final UserInfoInterface userInfo, final ProcessData procData,
      final Map<String, String> ahmHiddenFields, final boolean abForceDisabled, final FormService anService, final int anTargetField,
      final ServletUtils response) {
    return generateForm(abBlock, userInfo, procData, ahmHiddenFields, abForceDisabled, anService, anTargetField, null, response,
        null, null, false);
  }

  public static String generateForm(final BlockFormulario abBlock, final UserInfoInterface userInfo, final ProcessData procData,
	      final Map<String, String> ahmHiddenFields, final boolean abForceDisabled, final FormService anService, final int anTargetField,
	      final ServletUtils response, boolean showButton) {
	    return generateForm(abBlock, userInfo, procData, ahmHiddenFields, abForceDisabled, anService, anTargetField, null, response,
	        null, null, showButton);
	  }
  
  private static String generateForm(final BlockFormulario abBlock, final UserInfoInterface userInfo, final ProcessData procData,
      final Map<String, String> ahmHiddenFields, final boolean abForceDisabled, final FormService anService, final int anTargetField,
      final ProcessData procData2, final ServletUtils response, final String jspOverride,
      final List<FormButton> buttonsOverride, boolean showButton) { // needed to encode public files
    long start = System.currentTimeMillis();
    String retObj = generateFormProfiled(abBlock, userInfo, procData, ahmHiddenFields, abForceDisabled, anService, anTargetField,
        procData2, response, jspOverride, buttonsOverride, showButton);
    long end = System.currentTimeMillis();
    Logger.trace("BlockFormulario", "generateForm", "Form generation took " + (end - start) + " ms");
    return retObj;
  }

  private static String generateFormProfiled(
      final BlockFormulario abBlock,
      final UserInfoInterface userInfo,
      final ProcessData procData,
      final Map<String,String> ahmHiddenFields,
      final boolean abForceDisabled,
      final FormService anService,
      final int anTargetField,
      final ProcessData procData2,
      final ServletUtils response,
      final String jspOverride,
      final List<FormButton> buttonsOverride,
      final boolean showButton ) {  // needed to encode public files

    // Cleanup pending documents
    if(response != null) {
      HttpSession session = response.getSession();
      if(null != session) {
        session.removeAttribute(DocumentSessionHelper.SESSION_ATTRIBUTE);
      }
    }
    
    
    String retObj = null;
    final int flowid = procData.getFlowId();
    final int pid = procData.getPid();
    final int subpid = procData.getSubPid();
    int level = 0;

    
    final String sLogin = userInfo.getUtilizador();
    try {

      // 2nd dataset is to be used in detalhe processo and will contain main process data
      // while 1st dataset will contain detailed process data

      abBlock.init();


      StringBuffer sbXml = new StringBuffer();
      String sFormName = sFORM_NAME;
      String sXslName = abBlock.getAttribute(FormProps.sSTYLESHEET);
      String sPrintStyleSheet = abBlock.getAttribute(FormProps.sPRINT_STYLESHEET);
      boolean bPrint = StringUtils.isNotEmpty(sPrintStyleSheet);

      String sXsl = sXslName;
      boolean noPrint = true;
      if (bPrint && anService == FormService.PRINT && anTargetField == -1) {
        noPrint = false;
        // use selected stylesheet only when printing
        // and when printing whole page (no target field)
        // otherwise use block's stylesheet
        sXsl = sPrintStyleSheet;
      }
      
      boolean bForceDisabled = abForceDisabled;
      if (abBlock.getAttribute(FormProps.READ_ONLY) != null && !bForceDisabled) {
        bForceDisabled = Boolean.valueOf(String.valueOf(procData.eval(userInfo, abBlock.getAttribute(FormProps.READ_ONLY))));
      }

      Logger.debug(sLogin,abBlock,"generateForm",procData.getSignature() +"block xsl name=" + sXslName);
      Logger.debug(sLogin,abBlock,"generateForm",procData.getSignature() +"block print xsl name=" + sPrintStyleSheet);

      String sJSP = abBlock.getJSP(procData);
      if(StringUtils.isNotEmpty(jspOverride)) sJSP = jspOverride;


      int fieldNumber = -1;
      ArrayList<FieldInterface> alFields = FormCache.getFields(abBlock);
      HashMap<Integer,Properties>[] hma = FormCache.getFieldAttributes(abBlock);
      HashMap hmListAttrs = hma[nLIST_ATTR_IDX];
      List<FormButton> alButtons = null;
      FieldInterface fi = null;
      HashMap hmListValues = null;
      HashMap<String,String> hmHiddenFields = new HashMap<String,String>();
      // IE button click fix
      hmHiddenFields.put(FormProps.sBUTTON_CLICKED, "");
      
      HashSet<String> hsLinkVars = new HashSet<String>();

      String[] satmp = null;
      String stmp = null;
      String stmp2 = null;
      String stmp3 = null;
      String stmp4 = null;
      boolean btmp = false;
      ArrayList altmp = null;
      HashSet hstmp = null;
      int ntmp = 0;
      int nFieldCounter = 0;
      boolean nextIsVisible = false; // usado nos tabs
      Stack<NameValuePair<Boolean, TabContainerWrapper>> tabStack = new Stack<NameValuePair<Boolean, TabContainerWrapper>>();
      Stack<NameValuePair<Boolean, MenuContainerWrapper>> menuStack = new Stack<NameValuePair<Boolean, MenuContainerWrapper>>();
      
      if(null != buttonsOverride && !buttonsOverride.isEmpty()) {
        alButtons = buttonsOverride;
      } else {
        alButtons = FormCache.getButtons(abBlock);
        if (alButtons == null || alButtons.size() == 0) {
          // no buttons defined... use default buttons
          alButtons = abBlock.makeDefaultButtons();
        }
      }
      boolean inFrameworkDetail = false;
      if (procData.getTempData(FormProps.FRAMEWORK_DETAIL) != null) {
        inFrameworkDetail = Boolean.valueOf(String.valueOf(procData.eval(userInfo, procData.getTempData(FormProps.FRAMEWORK_DETAIL))));
      }
      procData.setTempData(FormProps.FRAMEWORK_DETAIL, null);
      boolean disableButtons = false;
      if (abBlock.getAttribute(FormProps.DISABLE_BUTTONS) != null) {
        disableButtons = Boolean.valueOf(String.valueOf(procData.eval(userInfo, abBlock.getAttribute(FormProps.DISABLE_BUTTONS))));
      }
      if (inFrameworkDetail) {
    	List<FormButton> alButtons2 = new ArrayList<FormButton>();	  
        for(int i = 0 ; i < alButtons.size(); i++){
        	FormButton fb = alButtons.get(i);
            if (fb.getType() == FormButtonType.PRINT) {
            	alButtons2.add(fb);
                break;
              }
        }
        alButtons = alButtons2;
      }
      if (disableButtons) {
        alButtons = new ArrayList<FormButton>();
      }

      sbXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      sbXml.append("<?xml-stylesheet href=\"").append(sXsl).append("\" type=\"text/xsl\"?>");
      sbXml.append("<form>");
      sbXml.append("<name>").append(sFormName).append("</name>");
      sbXml.append("<action>").append(sJSP).append("</action>");

      if (ahmHiddenFields != null && ahmHiddenFields.size() > 0) {
        // copy hidden fields to local hashmap
        Iterator <String> hiddenIter = ahmHiddenFields.keySet().iterator();
        while (hiddenIter.hasNext()) {
          stmp = hiddenIter.next();
          if(!stmp.startsWith("_tabholder_selected")){
	          stmp2 = ahmHiddenFields.get(stmp);
	          hmHiddenFields.put(stmp,stmp2);
          }
        }
      }

      if (abBlock.nTYPE == nTYPE_FORM) {
        // errors
        if (procData.hasError()) {
          String error = procData.getError();
          if (StringUtils.isNotEmpty(error)) {
            stmp = Utils.replaceString(error,"<br/>","</text></error><error><text>");
            sbXml.append("<error><title>" + userInfo.getMessages().getString("form.error_msg_title") + "</title><text>");
            sbXml.append(StringEscapeUtils.escapeXml(error));
            sbXml.append("</text></error>");

          }
        }
      }

      sbXml.append("<blockdivision><columndivision>");

      // form fields
      fieldNumber=0;
      int nFieldEnd = alFields.size();
      if (anService != FormService.NONE && anTargetField > -1) {
        fieldNumber = anTargetField;
        nFieldEnd = fieldNumber + 1; // allow one iteration
      }
//Start field for
      for (; fieldNumber < nFieldEnd; fieldNumber++) {
        fi = (FieldInterface)alFields.get(fieldNumber);

        if (fi == null) {
          continue;
        }

        Logger.debug(userInfo.getUtilizador(),abBlock,"generateForm",
            procData.getSignature() + "Processing field " + fieldNumber + " of type " + fi.getClass().getName());

        
        // update field counter
        nFieldCounter++;

        Properties props = abBlock.getFieldProperties(userInfo, fieldNumber);
        
        props.setProperty(FormProps.JSP, sJSP);
        props.setProperty(FormProps.FORM_NAME, sFormName);
        props.setProperty(FormProps.FLOWID,"" + procData.getFlowId());
        props.setProperty(FormProps.PID,"" + procData.getPid());
        props.setProperty(FormProps.SUBPID,"" + procData.getSubPid());
        
        // now build lists for list/query attributes and append them to props
        hmListValues = (HashMap) hmListAttrs.get(fieldNumber);
        Iterator iter = hmListValues.keySet().iterator();
        while (iter.hasNext()) {
          stmp = (String)iter.next();

          if (stmp.endsWith("_variable")) {
            // process later variables
            continue;
          }

          stmp2 = props.getProperty(stmp);

          if (Utils.isListVar(stmp2)) {
            props.remove(stmp);
          }
          else {
            // should not be here....
            continue;
          }

          altmp = (ArrayList)hmListValues.get(stmp);
          satmp = (String[])altmp.get(0);
          hstmp = (HashSet)altmp.get(1);

          ntmp = 0;
          stmp3 = null;
          try {
            stmp3 = stmp.substring(0,stmp.indexOf(FormProps.sJUNCTION));
            ntmp = Integer.parseInt(stmp3) + ntmp;
            stmp3 = String.valueOf(ntmp);
          }
          catch (Exception en) {
            ntmp = 0;
            stmp3 = null;
          }

          for (int i=0; i < satmp.length; i++) {

            if (hstmp != null && hstmp.contains(String.valueOf(i))) {
              altmp = FlowSetting.getQueryValues(userInfo, satmp[i],procData);
            }
            else {
              altmp = new ArrayList();
              altmp.add(satmp[i]);
              // now reset offset counter
            }

            for (int ii=0; ii < altmp.size(); ii++,ntmp++) {
              if (stmp3 != null) {
                stmp4 = ntmp + stmp.substring(stmp.indexOf(FormProps.sJUNCTION));
              }
              else {
                stmp4 = ntmp + FormProps.sJUNCTION + stmp;
              }

              props.setProperty(stmp4,(String)altmp.get(ii));
            }
          }
          altmp = null;
          satmp = null;
          hstmp = null;
        } // while

        if (bForceDisabled) props.setProperty(FormProps.sOUTPUT_ONLY,"true");
        if (anService == FormService.NONE) props.setProperty("services_enabled","true");

        {
          // FIXME Nao sei onde colocar este pedaco de codigo....
          // TODO TAB Container disable w/o adding 2 end
          String sFieldClass = fi.getClass().getName();
          NameValuePair<Boolean, TabContainerWrapper> myAttr = new NameValuePair<Boolean, TabContainerWrapper>(!abBlock.checkDisabledField(userInfo, procData, props));
          if (sFieldClass.endsWith("form.TabDivision")) {
            nextIsVisible = true;
            tabStack.push(myAttr.setValue(new TabDivisionWrapper()));
          } else if (sFieldClass.endsWith("form.TabBegin")) {
            if(tabStack.isEmpty()) {
              continue; // Ignore this field
            }
            props.setProperty("display", nextIsVisible?"":"none");
            nextIsVisible = false;
            
            NameValuePair<Boolean, TabContainerWrapper> attr = tabStack.peek();
            if (!attr.getName()) {
              continue;
            }
            TabContainerWrapper wrapper = attr.getValue();
            if(!wrapper.isHolder()) {
              tabStack.pop();
              props.setProperty("close_previous", wrapper.close());
            }
            tabStack.push(myAttr.setValue(new TabWrapper()));
          } else if (sFieldClass.endsWith("form.TabEnd")) {
            if(tabStack.isEmpty()) {
              continue; // Ignore this field
            }
            NameValuePair<Boolean, TabContainerWrapper> attr = tabStack.pop();
            TabContainerWrapper wrapper = attr.getValue();
            if (!attr.getName()) {
              continue;
            }
            if(wrapper.isHolder()) {
              tabStack.push(myAttr.setValue(wrapper)); // keep it
            }
          } else if (sFieldClass.endsWith("form.TabEndDivision")) {
            if(tabStack.isEmpty()) {
              continue; // Ignore this field
            }
            TabContainerWrapper wrapper = (TabContainerWrapper) tabStack.pop().getValue();
            if(!wrapper.isHolder()) {  // close a previous tab
              props.setProperty("close_previous", wrapper.close());
            }
          }
        }
        
        if (!tabStack.isEmpty()) {
          NameValuePair<Boolean, TabContainerWrapper> tabattr = tabStack.peek();
          if (!tabattr.getName()) {
            Logger.debug(sLogin,abBlock,"generateForm",
                procData.getSignature() +"field " + fieldNumber + " (type: " + fi.getClass().getName() + ") is inside disabled tab");
            continue;
          }
        }
        
        
        
        //MENU_DIVISION
        String sFieldClass = fi.getClass().getName();
        NameValuePair<Boolean, MenuContainerWrapper> myAttr = new NameValuePair<Boolean, MenuContainerWrapper>(!abBlock.checkDisabledField(userInfo, procData, props)); 
        
        if (sFieldClass.endsWith("form.MenuDivision")) {
              nextIsVisible = true;
              level = level + 1;

              props.setProperty("first",""+level);
              menuStack.push(myAttr.setValue(new MenuDivisionWrapper()));

        } else if (sFieldClass.endsWith("form.MenuEntry")) {
              if(menuStack.isEmpty()) {
                continue; // Ignore this field
              }
              props.setProperty("display", nextIsVisible?"":"none");
              nextIsVisible = false;          
              NameValuePair<Boolean, MenuContainerWrapper> attr = menuStack.peek();
              if (!attr.getName()) {
                continue;
              }
              MenuContainerWrapper wrapper = attr.getValue();
              if(!wrapper.isHolder()) {
                menuStack.pop();
                props.setProperty("close_previous", wrapper.close());
              }
              menuStack.push(myAttr.setValue(new MenuWrapper()));
        
        } else if (sFieldClass.endsWith("form.MenuEntry")) {
              if(menuStack.isEmpty()) {
                continue; // Ignore this field
              }
              NameValuePair<Boolean, MenuContainerWrapper> attr = menuStack.pop();
              MenuContainerWrapper wrapper = attr.getValue();
              if (!attr.getName()) {
                continue;
              }
              if(wrapper.isHolder()) {
                  menuStack.push(myAttr.setValue(wrapper)); // keep it
              }
              
        } else if (sFieldClass.endsWith("form.MenuEndDivision")) {
              if(menuStack.isEmpty()) {
                continue; // Ignore this field
              }
              level = level - 1;
              props.setProperty("first",""+level);
        }
      
          if (!menuStack.isEmpty()) {
            NameValuePair<Boolean, MenuContainerWrapper> menuattr = menuStack.peek();
            if (!menuattr.getName()) {
              Logger.debug(sLogin,abBlock,"generateForm",
                  procData.getSignature() +"field " + fieldNumber + " (type: " + fi.getClass().getName() + ") is inside disabled menu");
              continue;
            }
          }
        //END MENU_DIVISION
        
        
        
        
        
        // check if field is disabled
        if (abBlock.checkDisabledField(userInfo, procData, props)) {
          Logger.debug(sLogin,abBlock,"generateForm",
              procData.getSignature() +"field " + fieldNumber + " (type: " + fi.getClass().getName() + ") is disabled");
          continue;          
        }

        props.setProperty(FormProps.OUTPUT_FIELD, fi.isOutputField() ? "true" : "false");

        props.put(FormProps.sOBLIGATORY_PROP, 
            String.valueOf(FormUtils.checkRequiredField(userInfo, procData, props)));
        Logger.debug(sLogin,abBlock,"generateForm",
            procData.getSignature() +"field " + fieldNumber + " (type: " + fi.getClass().getName() + ") is obligatory ? " + props.getProperty(FormProps.sOBLIGATORY_PROP));

        // Inicializa o field interface
        fi.setup(userInfo, procData, props, response);

        Logger.debug(sLogin,abBlock,"generateForm",
            procData.getSignature() +"field " + fieldNumber + " setup (type: " + fi.getClass().getName() + ")");               
        
        
        // now set "value" field (if applicable) as well as form suffix
        // first single properties
        String varName = props.getProperty(FormProps.sVARIABLE);
        if (StringUtils.isNotEmpty(varName)) {

          fi.initVariable(userInfo, procData, varName, props);
          
          String fieldContent = null;

          ProcessVariableValue value = null;
          
          Collection<String> lVarNames = procData.getSimpleVariableNames();
          //TODO verificar se é uma lista
          if (lVarNames.contains(varName)) {
            value = procData.get(varName);
          } else {
            try {
              Object o = procData.eval(userInfo, varName);
              if (o == null) o = "";
              //IntegerDataType
              if (o instanceof Integer || o instanceof Long) {
                value = new ProcessSimpleVariable(new IntegerDataType(), varName);
              }
              //FloatDataType
              else if (o instanceof Double || o instanceof Float){
                value = new ProcessSimpleVariable(new FloatDataType(), varName);
              }
              //DateDataType
              else if (o instanceof Date) {
                value = new ProcessSimpleVariable(new DateDataType(), varName);
              }
              //TextDataType
              else {
                value = new ProcessSimpleVariable(new TextDataType(), varName);
                o = String.valueOf(o);
              }
              value.setValue(o);
            } catch (Exception e) {
              Logger.error(sLogin,abBlock,"generateForm", procData.getSignature() +"Error evaluating: " + varName);
            }
          }
          
          DataTypeInterface dtiSimple = abBlock.getDataType(userInfo, procData, varName, props, fi);
          if (dtiSimple != null) {

            Map<String,Object> deps = new HashMap<String, Object>();
            deps.put(FormProps.HIDDEN_FIELDS, hmHiddenFields);
            
            String extraProp = FormProps.sEXTRA_PROPS;
            dtiSimple.init(userInfo, procData, parseExtraProps(extraProp, props), deps);
            
            fieldContent = dtiSimple.format(userInfo, procData, anService, fieldNumber, varName, value, props, response);
            Logger.debug(sLogin,abBlock,"generateForm",
                procData.getSignature() +"format(" + varName + ")=" + fieldContent);
          }

          // HTML PARSING
          fieldContent = StringEscapeUtils.escapeXml(fieldContent); //TODO FIX validar correcção FormUtils.escapeAmp(fieldContent);

          props.setProperty("value",fieldContent==null?"":fieldContent);
        }
        
        handleRowControlList(userInfo, abBlock, procData, props, fieldNumber);
        
        Set<String> hsIgnoreColumns = computeIgnoreColumns(userInfo, abBlock, procData, fi.isArrayTable(), props);
        HashMap redBlueControlArrays = new HashMap(); // Gosto muito do red-blue porque é um nome bonito e totalmente inutil como stmp2

        Map<String,List<ProcessVariableValue>> valueMap = preProcessMultipleVars(userInfo, 
            abBlock, 
            procData, 
            fieldNumber, 
            fi.isArrayTable(), 
            props, 
            hsIgnoreColumns, 
            redBlueControlArrays, 
            hmListValues, 
            hmHiddenFields);
        

        // now generate data to display
        for (int i=0; true; i++) {

          String listVarName = props.getProperty(i + "_" + FormProps.sVARIABLE); 
          stmp = listVarName;
          if (listVarName == null) break;

          if (fi.isArrayTable() &&
              hsIgnoreColumns.contains(listVarName)) {
            // ignore this column
            continue;
          }

          String fieldContent = null;
          
          
          if (bForceDisabled) props.setProperty(i + "_" + FormProps.sOUTPUT_ONLY, "true");

          // XXX A martelada comeca com pancadinhas simples e suaves...
          ArrayList redBlueList = (ArrayList) redBlueControlArrays.get(listVarName+"_bg");
          ArrayList showList = (ArrayList) redBlueControlArrays.get(listVarName+"_show");
          Logger.debug(sLogin,abBlock,"generateForm","Show List: "+showList);

          
          List<ProcessVariableValue> values = valueMap.get(listVarName);
          
          
          // LEGACY CHECK
          // ORIG: stmp = Utils.unlistVarName(stmp); // variable name without list format
          if (!StringUtils.equals(listVarName, Utils.unlistVarName(listVarName))) {
            // XXX
            throw new Error("OOPPSSSS... BLOCK FORM LEGACY CHECK FOR LISTED VAR NAME!!!");
          }
          // LEGACY CHECK END
          
          DataTypeInterface dtiMultiple = abBlock.getDataType(userInfo, procData, listVarName, i, props, fi);
          if (dtiMultiple != null) {

            Map<String,String> extraProps = parseExtraProps(i + "_" + FormProps.sEXTRA_PROPS, props);
                            
            Map<String,Object> deps = new HashMap<String, Object>();            
            deps.put(FormProps.HIDDEN_FIELDS, hmHiddenFields);
            deps.put(FormProps.VALUE_MAP ,valueMap);
            
            dtiMultiple.init(userInfo, procData, extraProps, deps);            

            for (int row=0; row < values.size(); row++) {
              
              ProcessVariableValue rowValue = values.get(row);
              
              if(rowValue != null && rowValue.getValue() != null && rowValue.getValue().getClass().equals(String.class))
                rowValue.setValue(StringEscapeUtils.escapeXml(rowValue.getValue().toString()));
              
              fieldContent = dtiMultiple.formatRow(userInfo, procData, anService, fieldNumber, i, listVarName, row, rowValue, props, response);
              Logger.debug(sLogin,abBlock,"generateForm",
                  procData.getSignature() +
                  "List formatRow(" + listVarName + ")[" + row + "]=" + fieldContent);
              
              if(rowValue != null && rowValue.getValue() != null && rowValue.getValue().getClass().equals(String.class))
                rowValue.setValue(StringEscapeUtils.unescapeXml(rowValue.getValue().toString()));

              // XXX Mais dificil ainda, integrar o red-blue com o shaked, twisted and stirred code
              try {
                if(redBlueList != null) {
                  String redBlue = (String) redBlueList.get(row);
                  if(null != redBlue)
                    props.setProperty(i + "_" + row + "_value_control",redBlue);
                }
              } catch(Throwable t) {}

              // XXX Depois a martelada continua até alguém se aleijar. Mas a sério!
              try {
                if(showList != null) {
                  Boolean toShow = (Boolean) showList.get(row);
                  Logger.debug(sLogin,abBlock,"generateForm","Show List["+row+"]: "+toShow);
                  if(!toShow.booleanValue()) fieldContent = ""; // .oO( BRUTAL!!! )
                }
              } catch(Throwable t) {}


              props.setProperty(i + "_" + row + "_value", fieldContent == null ? "" : fieldContent);
            }
          } // if stmp3 != null && ...
        } // for (int i=0; true; i++...


        // pair var-value handled
        // now handle other field special stuff
        stmp = fi.getClass().getName();
        if (stmp.endsWith("form.Link")) {
          // link field

          // control
          btmp = true;
          stmp = props.getProperty("control_on_cond");
          if (StringUtils.isNotEmpty(stmp)) {
            btmp = false;
            try {
              btmp = procData.query(userInfo, stmp);
            }
            catch (Exception ei) {
              ProcessData ns = abBlock.nTYPE == nTYPE_DETALHE && procData2 != null ? procData2 : null;;

              if (ns != null) {
                // try in main process dataset
                try {
                  btmp = ns.query(userInfo, stmp);
                }
                catch (Exception ei2) {
                  btmp = false;
                  Logger.error(sLogin,abBlock,"generateForm","[" + flowid + "," + pid + "," + subpid + "] " +
                      "caught exception evaluation beanshell condition for "
                      + "main and detailed datasets <cond>"
                      + stmp + "</cond> (assuming false): "
                      + ei.getMessage());
                }
              }
              else {
                btmp = false;
                Logger.error(sLogin,abBlock,"generateForm","[" + flowid + "," + pid + "," + subpid + "] " +
                    "caught exception evaluation beanshell condition <cond>"
                    + stmp + "</cond> (assuming false): "
                    + ei.getMessage());
              }
            }
          }

          if (!btmp) {
            // control var not on ON value... disable link
            props.setProperty(FormProps.sOUTPUT_ONLY, "true");
          }

          stmp3 = null; // url var
          stmp = props.getProperty("proc_link");
          if (StringUtils.equalsIgnoreCase(stmp, "true")) {
            // disabled new window
            props.setProperty("open_new_window", "false");

            stmp = props.getProperty("variable");
            if (StringUtils.isNotEmpty(stmp)) {

              if (!hsLinkVars.contains(stmp) &&
                  hmHiddenFields != null && hmHiddenFields.containsKey(stmp)) {
                Logger.warning(sLogin,abBlock,"generateForm","[" + flowid + "," + pid + "," + subpid + "] " +"link variable already exists as hidden field.");
              }
              else {
                hsLinkVars.add(stmp); // add var to link var list

                if (hmHiddenFields == null) hmHiddenFields = new HashMap<String, String>();
                hmHiddenFields.put(stmp,"");

                stmp2 = props.getProperty("var_value");
                if (stmp2 == null) {
                  stmp2 = "";
                }
                else {
                  stmp2 = procData.transform(userInfo, stmp2);
                  if (stmp2 == null) stmp2 = "";
                }                
                String keepScrollOnLoad = props.getProperty(FormProps.KEEP_SCROLL_ONLOAD);
                if(keepScrollOnLoad==null) keepScrollOnLoad="";
                
                stmp3 = "javascript:disableForm(" + keepScrollOnLoad + ");document."
                  + BlockFormulario.sFORM_NAME
                  + "." + stmp + ".value='" + stmp2 + "';document."
                  + BlockFormulario.sFORM_NAME
                  + ".op.value='3';document."
                  + BlockFormulario.sFORM_NAME
                  + ".submit();";
              }
            }
          }
          else {
            stmp3 = props.getProperty("url");
            if (StringUtils.isEmpty(stmp3)) {
              stmp3 = null;
            }
            else {
              if (stmp3.indexOf("\"") > -1) {
                // try to string parse
                stmp4 = procData.transform(userInfo, stmp3);
                if (StringUtils.isNotEmpty(stmp4)) {
                  stmp3 = stmp4;
                }
                stmp4 = null;
              }
            }
          }
          if (stmp3 != null) {
            props.setProperty("href", stmp3);
          }
          else {
            Logger.error(sLogin,abBlock,"generateForm","[" + flowid + "," + pid + "," + subpid + "] " +"no url property");
          }
        }
        else if (stmp.endsWith("form.DateCal")) {
          props.setProperty("formname", BlockFormulario.sFORM_NAME);
        }
        else if (stmp.endsWith("form.ArrayTable") || stmp.endsWith("form.DMSTable")) {
          String disableHeader = props.getProperty(FormProps.DISABLE_TABLE_HEADER);
          if (StringUtils.isBlank(disableHeader)) {
            disableHeader = "false";
          }
          disableHeader = procData.transform(userInfo, disableHeader);
          props.setProperty(FormProps.DISABLE_TABLE_HEADER, disableHeader);
        }
        else if (stmp.endsWith("form.TextBox")) {
          String submitOnBlur = props.getProperty(FormProps.TEXT_SUBMIT_ON_BLUR);
          if (StringUtils.isBlank(submitOnBlur)) {
            submitOnBlur = "false";
          }
          submitOnBlur = procData.transform(userInfo, submitOnBlur);
          props.setProperty(FormProps.TEXT_SUBMIT_ON_BLUR, submitOnBlur);
        }
        else if (stmp.endsWith("form.File")) {
          String onclick = "javascript:if (this.checked) { return confirm('"
            + userInfo.getMessages().getString("BlockFormulario.file.delete.confirmation")
            + "'); }";
          props.setProperty(FormProps.ONCLICK, onclick);
          props.setProperty(FormProps.FILE_UPLOAD_RENAME,
              getParsedProperty(userInfo, procData, props.getProperty(FormProps.FILE_UPLOAD_RENAME)));
          props.setProperty(FormProps.FILE_UPLOAD_EXTENSIONS,
              getParsedProperty(userInfo, procData, props.getProperty(FormProps.FILE_UPLOAD_EXTENSIONS)));
          props.setProperty(FormProps.FILE_UPLOAD_PRESERVE_EXT,
              getParsedProperty(userInfo, procData, props.getProperty(FormProps.FILE_UPLOAD_PRESERVE_EXT)));
        }

        props.setProperty(FormProps.sONCHANGE_SUBMIT, 
            FormUtils.getOnChangeSubmit(props.getProperty(FormProps.sONCHANGE_SUBMIT), BlockFormulario.sFORM_NAME));

        // set odd property (to enable alternate bgcolors)
        if (nFieldCounter % 2 == 0) props.setProperty("even_field", "true");  // FIXME isto devia ficar la em cima antes do init
        else props.setProperty("even_field", "false");
        
        // last selected tab
        if (fi instanceof TabDivision && ahmHiddenFields.get("_tabholder_selected" + props.getProperty("fieldid"))!=null)
        	props.setProperty("_tabholder_selected", ahmHiddenFields.get("_tabholder_selected" + props.getProperty("fieldid")));

        // now get xml from field object
        stmp = fi.getXML(props);
        if (stmp != null) {
          sbXml.append(stmp);
        }
        else {
          Logger.warning(sLogin,abBlock,"generateForm",
              procData.getSignature() + "null generated xml for field "
              + fieldNumber + " (" + fi.getClass().getName() + ")");
        }

      } //End field for


      // Close all tabs...
      while(!tabStack.isEmpty()) {
        TabContainerWrapper wrapper = (TabContainerWrapper) tabStack.pop().getValue();
        sbXml.append(wrapper.close());
      }


      sbXml.append("</columndivision></blockdivision>");


      if (anService == FormService.NONE || showButton) {
        
        // buttons
        for (FormButton formButton : alButtons) {
          
          if (formButton == null) continue;

          // IE button fix
          String sButtonFix = "document.getElementById('" + FormProps.sBUTTON_CLICKED + "').value='" + formButton.getId() + "'; ";
          
          FormButtonType type = formButton.getType();
          
          boolean useIt = true;
          String text = formButton.getText(userInfo);
          String operation = "";
          String buttonFormName = BlockFormulario.getButtonFormId(formButton);

          String showCond = formButton.getAttribute(FormButton.ATTR_SHOW_COND);
          if (StringUtils.isNotEmpty(showCond)) {
            useIt = false;
            try {
              useIt = procData.query(userInfo, showCond);
            }
            catch (Exception ei) {
              if (abBlock.nTYPE == nTYPE_DETALHE && procData2 != null) {
                // try in main process
                try {
                  useIt = procData2.query(userInfo, showCond);
                }
                catch (Exception ei2) {
                  useIt = false;
                  Logger.error(sLogin,abBlock,"generateForm",
                      procData.getSignature() +
                      "caught exception evaluation beanshell condition for "
                      + "main and detailed processes <cond>"
                      + showCond + "</cond> (assuming false): "
                      + ei.getMessage());
                }
              }
              else {
                useIt = false;
                Logger.error(sLogin,abBlock,"generateForm",
                    procData.getSignature() +
                    "caught exception evaluation beanshell condition <cond>"
                    + showCond + "</cond> (assuming false): "
                    + ei.getMessage());
              }
            }
          }

          
          // OP's
          // 0 - entering page/reload
          // 1 - unused
          // 2 - save
          // 3 - next
          // 4 - cancel
          // 5 - service print
          // 6 - service print field
          // 7 - service export field
          // 8 - only process form
          // 9 - return to parent
          switch (formButton.getType()) {
          case CANCEL:
            String procNotInCreator = procData.getAppData(DataSetVariables.PROCESS_NOT_IN_CREATOR);
            if (StringUtils.isEmpty(procNotInCreator) && procData.isInDB()) {
              operation = "if (confirm('Deseja cancelar/fechar o processo?')) { disableForm(); document." +
                sFormName + ".op.value=4; } else { return false; }";
            }
            else {
              useIt = false;
            }
            break;
          case RESET:
            operation = "disableForm(); document." + sFormName + ".op.value=0;";
            break;
          case SAVE:
            if (userInfo.isGuest()) {
              Logger.info(sLogin,abBlock,"generateForm",
              "Guest user: ignoring save button...");
              useIt = false;
            }
            else {
              operation = "disableForm(); document." + sFormName + ".op.value=2;";
            }
            break;
          case PRINT:
            useIt = bPrint;
            operation = sButtonFix + "PrintService(null);";
            break;
          case NEXT:
            operation = "if (CheckEmptyFields()) { disableForm(); document." + sFormName + ".op.value='3'; " + sButtonFix
                + "} else { return false; }";
            break;
          case RETURN_PARENT:
            ProcessData pdLocal = abBlock.nTYPE == nTYPE_DETALHE && procData2 != null ? procData2 : procData;
            String switchRet = pdLocal.getTempData(Const.sSWITCH_PROC_RETURN_PARENT);
            useIt = StringUtils.equals(switchRet, "true");
            if (useIt) {
              String showCond2 = formButton.getAttribute(FormButton.ATTR_SHOW_COND);
              if (StringUtils.isNotEmpty(showCond2)) {
                useIt = false;
                ProcessData ns = pdLocal==procData?procData:procData2;
                try {
                  useIt = ns.query(userInfo, stmp2);
                }
                catch (Exception ei) {
                  btmp = false;
                  Logger.error(sLogin,abBlock,"generateForm",
                      procData.getSignature() + 
                      "caught exception evaluation beanshell condition <cond>"
                      + showCond2 + "</cond> (assuming false): "
                      + ei.getMessage());
                }
              }

              operation = "document." + sFormName + ".op.value=9;";
            }
            break;
          case CUSTOM:            
            if (useIt) {
              String image = formButton.getAttribute(FormButton.ATTR_IMAGE);
              if (StringUtils.isEmpty(image) && StringUtils.isEmpty(text)) {
                Logger.warning(sLogin,abBlock, "generateForm",
                    procData.getSignature() +
                    "No text nor image defined for custom button " + formButton.getId() + ".");
              }
              else {                
                operation = "if (CheckEmptyFields()) { disableForm(); document." + sFormName + ".op.value='3'; " + sButtonFix
                    + "} else { return false; }";

                String customVar = formButton.getAttribute(FormButton.ATTR_CUSTOM_VAR);
                if (StringUtils.isNotEmpty(customVar)) {
                  String customValue = formButton.getAttribute(FormButton.ATTR_CUSTOM_VALUE);
                  if (StringUtils.isNotEmpty(customValue)) {
                    operation += "document." + sFormName + "." + customVar + 
                      ".value='" + customValue + "';";
                    // add var to hidden field list
                    hmHiddenFields.put(customVar, "");
                  }
                }
              }
            }            
            break;
            default:
              Logger.warning(sLogin,abBlock, "generateForm",
                  procData.getSignature() +
                  "button type " + type.name() + " not supported!!");              
          }
      
          if (formButton.confirmMessage()) {
            String cnfMsg = formButton.getAttribute(FormButton.ATTR_CONFIRM_ACTION_MESSAGE);
            try {
              cnfMsg = procData.transform(userInfo, cnfMsg);
            } catch (EvalException ex) {
              // maintain as was
            }
            if (StringUtils.isBlank(cnfMsg)) {
              cnfMsg = userInfo.getMessages().getString("blockmsg.default.confirmMessage");
            }
            operation = "if (confirm('" + cnfMsg + "')) { " + operation + " } else { return false; }";
          }
          
          if (useIt) {
            sbXml.append("<button>");
            sbXml.append("<id>").append(formButton.getId()).append("</id>");
            sbXml.append("<name>").append(buttonFormName).append("</name>");
            sbXml.append("<text>").append(text).append("</text>");
            sbXml.append("<operation>").append(operation).append("</operation>");

            // TOOLTIP
            String tooltip = formButton.getAttribute(FormButton.ATTR_TOOLTIP);
            tooltip = StringUtils.isNotEmpty(tooltip) ? tooltip : text; 
            sbXml.append("<tooltip>").append(tooltip).append("</tooltip>");

            // IMAGE
            String image = formButton.getAttribute(FormButton.ATTR_IMAGE);
            if (StringUtils.isNotEmpty(image)) {
              if (image.indexOf("http://") == -1) {
//                if (Const.APP_PORT == -1) {
//                  image = Const.APP_PROTOCOL + "://" + Const.APP_HOST + stmp3;
//                }
//                else {
//                  image = Const.APP_PROTOCOL + "://" + Const.APP_HOST + ":" + Const.APP_PORT + stmp3;
//                }
            	  image = "../" + image;
              }
              sbXml.append("<buttonimage><src>").append(image).append("</src>");
              sbXml.append("<alt>").append(tooltip).append("</alt>");
              sbXml.append("</buttonimage>");
            }
            sbXml.append("</button>");

          }
        } // for buttons
      } // if (anService == FormService.NONE)


      // hidden fields
      if (hmHiddenFields != null && hmHiddenFields.size() > 0) {
        Iterator<String> iter = hmHiddenFields.keySet().iterator();
        while (iter.hasNext()) {
          stmp = iter.next();
          stmp2 = hmHiddenFields.get(stmp);
          String hiddenValue = stmp2 == null ? "" : StringEscapeUtils.escapeXml(stmp2);
          sbXml.append("<hidden>");
          sbXml.append("<name>").append(stmp).append("</name>");
          sbXml.append("<value>").append(hiddenValue).append("</value>");
          sbXml.append("</hidden>");
        }
      }

      sbXml.append("<loadingLabel>");
      sbXml.append(userInfo.getMessages().getString("formloading.label"));
      sbXml.append("</loadingLabel>");
      

      sbXml.append("</form>");
      // Logger.debug(sLogin,abBlock,"generateForm","[" + flowid + "," + pid + "," + subpid + "] " +"xml=" + sbXml.toString());


      if (anService == FormService.EXPORT) {
        // return xml
        retObj = sbXml.toString();
      }
      else {
        // now the transformation

        String xml = sbXml.toString();
        retObj = transformForm(userInfo, procData, sXsl, xml, noPrint, Const.bUSE_SCANNER, response);  // load upload applet if required

        retObj = StringEscapeUtils.unescapeHtml(retObj);

        Logger.trace("BlockFormulario", "generateForm", "Form xml: "+xml.length()+" chars; Generated html: "+retObj.length()+" chars");

      }
    }
    catch (Throwable t) {
      retObj = "<div style=\"margin-top:30px;font-family:verdana;font-size:11pt;font-color:red\">Ocorreu um erro a gerar a p&aacute;gina.</div>";
      Logger.error(sLogin,abBlock,"generateForm",procData.getSignature() +"caught exception: " + t.getMessage(), t);
    }
    return retObj;
  }

  
  private static String getButtonFormId(FormButton formButton) {
    FormButtonType type = formButton.getType();  
    String ret = type.getCode();
    if (type == FormButtonType.CUSTOM)
      ret = ret + "_" + formButton.getId();
    
    return ret;
  }

  private Properties getFieldProperties(UserInfoInterface userInfo, int field) {

    HashMap<Integer,Properties>[] hma = FormCache.getFieldAttributes(this);
    HashMap<Integer,Properties> hmAttrs = hma[nTXT_ATTR_IDX];
    
    Properties props = (Properties)hmAttrs.get(field);
    Properties retObj = new Properties();
    retObj.setProperty(Block.sORG_ID_PROP, userInfo.getOrganization());
    // clone property object to be able to safelly set new local properties
    Enumeration<String> enumer = (Enumeration<String>)props.propertyNames();
    while (enumer.hasMoreElements()) {
      String propName = enumer.nextElement();
      retObj.setProperty(propName,props.getProperty(propName));
    }

    // set field id
    retObj.setProperty("fieldid",String.valueOf(field));
    
    return retObj;
  }
  
  
  private DataTypeInterface getDataType(UserInfoInterface userInfo, ProcessData procData, String varName, Properties props, FieldInterface fi) throws Exception {
    return getDataType(userInfo, procData, varName, -1, props, fi);
  }
  
  private DataTypeInterface getDataType(UserInfoInterface userInfo, ProcessData procData, String varName, int index, Properties props, FieldInterface fi) throws Exception {
    DataTypeInterface dti = null;
    
    String propPrefix = index > -1 ? index + "_" : "";
    String propName = propPrefix + FormProps.sDATATYPE;
    String datatypeName = props.getProperty(propName);

    
    if (StringUtils.isEmpty(datatypeName) && fi != null) {
      if (fi instanceof DateCal) {
        // Force datatype to date...        
        datatypeName = pt.iflow.api.datatypes.Date.class.getName();
        props.setProperty(propName, datatypeName);
        Logger.debug(userInfo.getUtilizador(),this,"getDataType",
            procData.getSignature() + "Forced date datatype for DateCal var " + varName);
      }
      else if (fi instanceof pt.iflow.blocks.form.Selection 
          || fi instanceof pt.iflow.blocks.form.TextArea) {      
        // FIXME - editor sometimes removes datatype
        datatypeName = pt.iflow.api.datatypes.Text.class.getName();    
        props.setProperty(propName, datatypeName);
        Logger.debug(userInfo.getUtilizador(),this,"getDataType",
            procData.getSignature() + "Forced text datatype for "
            + fi.getClass().getName() + " var " + varName);
      }
      else if (fi instanceof pt.iflow.blocks.form.File) {
        datatypeName = pt.iflow.api.datatypes.Text.class.getName();    
        props.setProperty(propName, datatypeName);
        Logger.debug(userInfo.getUtilizador(),this,"getDataType",
            procData.getSignature() + "Forced text datatype for "
            + fi.getClass().getName() + " var " + varName);        
      }
    }
    
    if (StringUtils.isNotEmpty(datatypeName)) {

      Logger.debug(userInfo.getUtilizador(),this,"getDataType",
          procData.getSignature() + "Datatype: " + datatypeName + " for var " + varName);

      Class<? extends DataTypeInterface> cClass = 
        (Class<? extends DataTypeInterface>)Class.forName(datatypeName);

      dti = cClass.newInstance();
      dti.setLocale(userInfo.getUserSettings().getLocale());

    }
    else {
      Logger.debug(userInfo.getUtilizador(),this,"getDataType",
          procData.getSignature() + "Empty datatype for var " + varName);
    }

    return dti;
  }
  
  private static String getParsedProperty(UserInfoInterface userInfo, ProcessData procData, String value) throws EvalException {
    if (StringUtils.isBlank(value)) {
      return "";
    }
    String retObj = value;
    retObj = procData.transform(userInfo, retObj);
    if (StringUtils.isBlank(retObj)) {
      retObj = value;
    }
    return retObj;
  }
  
  // returns map with varname and list of values
  private static Set<String> computeIgnoreColumns(UserInfoInterface userInfo, 
      BlockFormulario block, 
      ProcessData procData,
      boolean isArrayTable,
      Properties props) {


    Set<String> retObj = new HashSet<String>();
    if (!isArrayTable)
      return retObj; 
    
      String login = userInfo.getUtilizador();
    
    for (int i=0; true; i++) {

      String sPropPrefix = i + "_";

      String sVar = props.getProperty(sPropPrefix + FormProps.sVARIABLE);
      if (sVar == null) break;

      String sTitle = props.getProperty(sPropPrefix + FormProps.sTITLE);

      Map<String,String> hmExtraProps = parseExtraProps(sPropPrefix + FormProps.sEXTRA_PROPS, props);
      boolean disableColuna = false;

      if (hmExtraProps != null) {
        // title
        String tep = (String)hmExtraProps.get(FormProps.sROW_TITLE_EXTRAPROP);
        if (tep != null && tep.toLowerCase(Locale.ENGLISH).equals(FormProps.sTITLE_EXTRAPROP_VAR)) {
          // fetch var's value
          sTitle = procData.getFormatted(sTitle);
        }

        String epdisable = (String) hmExtraProps.get(FormProps.sEXTRA_PROP_DISABLE_CONDITION);
        if(StringUtils.isNotEmpty(epdisable)) {
          try {
            disableColuna = procData.query(userInfo, epdisable);
          }
          catch (Exception ei) {
            disableColuna = false;
            Logger.debug(login,block,"computeIgnoreColumns", 
                "Condicao Disable da coluna caught exception evaluation beanshell (assuming false): " + epdisable, ei);
          }
        }
      }

      if (disableColuna || StringUtils.isEmpty(sTitle)) {
        // IGNORE THIS COLUMN!!!
        // empty columns must have &nbsp;
        retObj.add(sVar);
        props.setProperty((sPropPrefix + "IGNORE"), "true");
        continue;
      }
    }
    
    return retObj;
  }
  
  // returns map with varname and list of values
  private static Map<String, List<ProcessVariableValue>> preProcessMultipleVars(UserInfoInterface userInfo, 
      BlockFormulario block, 
      ProcessData procData,
      int fieldNumber,
      boolean isArrayTable,
      Properties props, 
      Set<String> hsIgnoreColumns, 
      HashMap redBlueControlArrays, 
      HashMap hmListValues,
      HashMap hmHiddenFields) {

    String login = userInfo.getUtilizador();
    
    int nMaxRow = 0;
    
    Map<String,List<ProcessVariableValue>> retObj = new HashMap<String, List<ProcessVariableValue>>();
    
    for (int i=0; true; i++) {

      String sPropPrefix = i + "_";

      String sVar = props.getProperty(sPropPrefix + FormProps.sVARIABLE);
      if (sVar == null) break;

      // first handle macroheader and header
      String sMacroTitle = props.getProperty(sPropPrefix + FormProps.sMACROTITLE);
      String sTitle = props.getProperty(sPropPrefix + FormProps.sTITLE);

      Map<String,String> hmExtraProps = parseExtraProps(sPropPrefix + FormProps.sEXTRA_PROPS, props);

      if (hmExtraProps != null) {
        // macro title
        String mtep = (String)hmExtraProps.get(FormProps.sROW_MACROTITLE_EXTRAPROP);
        if (mtep != null && mtep.toLowerCase(Locale.ENGLISH).equals(FormProps.sTITLE_EXTRAPROP_VAR)) {
          // fetch var's value
          sMacroTitle = procData.getFormatted(sMacroTitle);
          // update props entry
          props.setProperty((sPropPrefix + FormProps.sMACROTITLE), sMacroTitle);
        }
        else {
          // leave as is
        }

        // title
        String tep = (String)hmExtraProps.get(FormProps.sROW_TITLE_EXTRAPROP);
        if (tep != null && tep.toLowerCase(Locale.ENGLISH).equals(FormProps.sTITLE_EXTRAPROP_VAR)) {
          // fetch var's value
          sTitle = procData.getFormatted(sTitle);
          // update props entry
          props.setProperty((sPropPrefix + FormProps.sTITLE), sTitle);
        }
        else {
          // leave as is
        }

        // process bgcolor extra prop
        String bgcep = (String)hmExtraProps.get(FormProps.sROW_BGCOLOR_EXTRAPROP);
        if(StringUtils.isNotEmpty(bgcep)) {
          props.setProperty((sPropPrefix + FormProps.sROW_BGCOLOR_EXTRAPROP), bgcep);
        }

        // process red-blue properties
        String ctrlBg = hmExtraProps.get("ctrl_bg");
        if(StringUtils.isNotEmpty(ctrlBg)) {
          redBlueControlArrays.put(sPropPrefix + "ctrl_bg", ctrlBg);
        }
        String ctrlShow = (String) hmExtraProps.get("ctrl_show");
        if(StringUtils.isNotEmpty(ctrlShow)) {
          redBlueControlArrays.put(sPropPrefix + "ctrl_show", ctrlShow);
        }
      }

      String varProp = sPropPrefix + FormProps.sVARIABLE;

      int currSize = 0;
      if (hmListValues.containsKey(varProp)) {
        // "static" list
        List<ProcessVariableValue> procValues = new ArrayList<ProcessVariableValue>();
        ArrayList altmp = (ArrayList)hmListValues.get(varProp);
        String[] satmp = (String[])altmp.get(0);
        HashSet hstmp = (HashSet)altmp.get(1);

        for (int ii=0; ii < satmp.length; ii++) {
          if (hstmp != null && hstmp.contains(String.valueOf(ii))) {
            ArrayList al = FlowSetting.getQueryValues(userInfo, satmp[ii],procData);
            for (int qv=0; al != null && qv < al.size(); qv++) {
              procValues.add(new TextProcessVariableValue((String)al.get(qv)));
            }
          }
          else {
            procValues.add(new TextProcessVariableValue(satmp[ii]));
          }
        }
        retObj.put(varProp, procValues);
        currSize = altmp.size();
      }
      else {
        // proc variable
        String var = props.getProperty(varProp);
        currSize = procData.getList(var) != null ? procData.getList(var).size() : 0;
        if (currSize < 0) {
          currSize = 0;
        }
      }

      if (currSize > nMaxRow) nMaxRow = currSize;
    }

    // add maxrow to hidden fields
    hmHiddenFields.put(fieldNumber + FormProps.sMAX_ROW, String.valueOf(nMaxRow));
    // add maxrow to properties in order to allow fields to display lists properly
    props.setProperty(FormProps.sMAX_ROW, String.valueOf(nMaxRow));
    props.setProperty(fieldNumber + FormProps.sMAX_ROW, String.valueOf(nMaxRow));

    
    
    // now fill data arrays:
    //   1.insert new values;
    //   2.rename old values to prop name;
    //   3.fill arrays with empty strings in short lists
    for (int i=0; true; i++) {
      String varProp = i + "_" + FormProps.sVARIABLE; // smtp
      String varname = props.getProperty(varProp); // stmp2
      
      if (varname == null) break;
      
      if (isArrayTable) {
        if (hsIgnoreColumns.contains(varname)) {
          // ignore this column
          continue;
        }
        try {
          DataTypeInterface dti = block.getDataType(userInfo, procData, varname, i, props, null);
          if (dti != null && dti instanceof pt.iflow.api.datatypes.Link) {
            Logger.debug(login, block, "preProcessMultipleVars", 
                procData.getSignature() + varname + " is link.. assuming null values");
            
            List<ProcessVariableValue> values = new ArrayList<ProcessVariableValue>();
            for (int ii=0; ii < nMaxRow; ii++) {
              values.add(null);
            }
            retObj.put(varname, values);

            continue;
          }
        } catch (Exception e) {
          Logger.warning(login, block, "preProcessMultipleVars", 
              procData.getSignature() + "exception getting datatype... ignoring var " + varname);
        }
      }

      if (retObj.containsKey(varProp)) {
        // values from static lists
        List<ProcessVariableValue> values = retObj.get(varProp);
        while (values.size() < nMaxRow) {
          values.add(new TextProcessVariableValue(""));
        }
        retObj.remove(varProp); // remove from varProp to insert as varname
        retObj.put(varname, values);
      }
      else {
        // values from process
        List<ProcessVariableValue> values = new ArrayList<ProcessVariableValue>();
        String redBlueVar = (String) redBlueControlArrays.get(i+"_ctrl_bg");
        String showVar = (String) redBlueControlArrays.get(i+"_ctrl_show");
        ArrayList<String> redBlueCtrl = new ArrayList<String>();
        ArrayList<Boolean> showCtrl = new ArrayList<Boolean>();
        ProcessListVariable listVar = procData.getList(varname);
        for (int ii=0; ii < nMaxRow; ii++) {
          ProcessVariableValue value = null;
          if (listVar != null && ii < listVar.size()) {
            value = listVar.getItem(ii);
          }
           try {
        	  if(value != null){
        	  values.add(value);
        	  }else{
        		  values.add(null);
        	  }
			
          	} catch (Exception e) {
          		Logger.error(login,block,"", "values.add - 1637 ");
          }{
        	  
          }
          
          String formattedValue = "";
          formattedValue = value != null ? value.format() : "";
               
          // Tratar das cores e o resto
          double redBlueRes = Double.NaN;
          if(value != null && StringUtils.isNotEmpty(formattedValue) && StringUtils.isNotEmpty(redBlueVar)) {
            Logger.debug(login,block,"preProcessMultipleVars", "Temos redBlue");

            String redBlueVal ="";
            redBlueVal = procData.getListItemFormatted(redBlueVar,ii);
            if(redBlueVal != "") {
              try {
                Double aVal = new Double(formattedValue);
                Double rbVal = new Double(redBlueVal);
                // é numero
                
                
                redBlueRes = aVal.compareTo(rbVal);
              } catch (Throwable t) {
                try {
                  redBlueRes = formattedValue.compareTo(redBlueVal);  
                } catch(Throwable tt) {
                  redBlueRes = Double.NaN;
                }
              }
            }
          }
          
       
          if(Double.isInfinite(redBlueRes) || Double.isNaN(redBlueRes)) 
            redBlueCtrl.add(null); // ignore
          else if( redBlueRes > 0)
            redBlueCtrl.add("gt");
          else if (redBlueRes == 0)
            redBlueCtrl.add("eq");
          else
            redBlueCtrl.add("lt");

          Boolean showVal = Boolean.TRUE;
          if(null != showVar) {
            double vv = Double.parseDouble(procData.getListItemFormatted(showVar, ii));
            Logger.debug(login,block,"preProcessMultipleVars", "showVar: '"+showVar+"' vv: "+vv);
            if(vv == 0) showVal = Boolean.FALSE;
          }
          showCtrl.add(showVal);

        }
        if(null != redBlueVar) redBlueControlArrays.put(varname+"_bg", redBlueCtrl);
        if(null != showVar)    redBlueControlArrays.put(varname+"_show", showCtrl);

        retObj.put(varname,values);
      }
    }
    
    return retObj;
  }
  
  private static void handleRowControlList(UserInfoInterface userInfo, BlockFormulario block, ProcessData procData, Properties props, int fieldNumber) {
    String login = userInfo.getUtilizador();
    if (props.getProperty(FormProps.sROW_CONTROL_LIST) != null) {
      String sRowControlList = (String)props.remove(FormProps.sROW_CONTROL_LIST);
      if (StringUtils.isNotEmpty(sRowControlList)) {
        if (procData.isListVar(sRowControlList)) {
          if (procData.getList(sRowControlList) == null) {
            Logger.warning(login,block,"handleRowControlList",
                procData.getSignature() + "ROWCONTROLLIST VAR " + sRowControlList + " IS NULL (perhaps not defined in catalogue)!");                  
          }
          else {
            for (int rcl=0; rcl < procData.getList(sRowControlList).size(); rcl++) {

              String ctl = procData.getListItemFormatted(sRowControlList, rcl);
              if (StringUtils.isEmpty(ctl)) continue;

              StringBuilder sb = new StringBuilder();

              if (StringUtils.equals(ctl, "separator")) {
                sb.append("<separator>true</separator>");
                // force output only
                props.setProperty("row_"+rcl + "_" + FormProps.sOUTPUT_ONLY, "true");
              }
              else {
                // try parse as props
                Map<String, String> extraProps = parseExtraProps(ctl, DEFAULT_PROPS_SEP);
                if (extraProps != null) {
                  if (extraProps.containsKey(FormProps.SUB_HEADER)) {
                    props.setProperty("row_"+rcl + "_" + FormProps.SUB_HEADER, "true");
                    for (int i=0; true; i++) {
                      String shcol = FormProps.SUB_HEADER_COL_PREFIX + i; 
                      String shcolText = extraProps.get(shcol);
                      if (StringUtils.isEmpty(shcolText))
                        break;
                      
                      props.setProperty("row_"+rcl + "_" + shcol, shcolText);
                    }
                  }
                  Iterator<String> keys = extraProps.keySet().iterator(); 
                  while (keys.hasNext()) {
                    String key = keys.next();

                    if (StringUtils.equals(key, FormProps.SUB_HEADER) ||
                        StringUtils.startsWith(key, FormProps.SUB_HEADER_COL_PREFIX)) {
                      // already processed
                      continue;
                    }
                    
                    String val = extraProps.get(key);
                    sb.append("<").append(key).append(">").append(val).append("</").append(key).append(">");

                    if (StringUtils.equals(key, FormProps.sOUTPUT_ONLY)) {
                      props.setProperty("row_"+rcl + "_" + FormProps.sOUTPUT_ONLY, val);                      
                    }
                    else if (StringUtils.equals(key, FormProps.READ_ONLY)) {
                      props.setProperty("row_"+rcl + "_" + FormProps.READ_ONLY, val);                      
                    }
                    else if (StringUtils.equals(key, FormProps.DISABLED)) {
                      props.setProperty("row_"+rcl + "_" + FormProps.DISABLED, val);                      
                    }
                  }
                }
              }
              
              props.setProperty(rcl + "_" + FormProps.sROW_CONTROL_LIST, sb.toString());
            }
          }
        } else {
          Logger.warning(login,block,"handleRowControlList",
              procData.getSignature() +
              "ROW_CONTROL_LIST attribute (" + sRowControlList + 
              ") is not an array in procdata for field " + fieldNumber + "... ignoring");
        }
      }
    }
  }
  
  private boolean checkDisabledField(UserInfoInterface userInfo, ProcessData procData, Properties props) {
    boolean disableField = false;
    String disableCond = props.getProperty(FormProps.sDISABLE_COND);
    if (StringUtils.isNotEmpty(disableCond)) {
      try {
        disableField = procData.query(userInfo, disableCond);
      }
      catch (Exception eeval) {
        Logger.warning(userInfo.getUtilizador(), this, "checkDisabledField", 
            procData.getSignature() + "Error evaluating disable condition " + disableCond, eeval);
      }
    }
    return disableField;
  }

  protected static String transformForm(final UserInfoInterface userInfo, final ProcessData procData, final String sXsl,
      final String xml, final boolean noPrint, final boolean useScanner, final ServletUtils response) throws IOException,
      TransformerException {
    String sLogin = userInfo.getUtilizador();
    String retObj = null;

    Transformer transformer = XslTransformerFactory.getTransformer(userInfo, sXsl);
    if (noPrint && null == transformer) {
      Logger.debug(sLogin, "BlockFormulario", "transformForm", "Stylesheet not found. Trying flow stylesheet...");
      // some default XSL processing...
      String sDefaultXslName = "";
      FlowSetting defXSLSetting = BeanFactory.getFlowSettingsBean().getFlowSetting(procData.getFlowId(), Const.sDEFAULT_STYLESHEET);
      if (null != defXSLSetting)
        sDefaultXslName = defXSLSetting.getValue();

      Logger.debug(sLogin, "BlockFormulario", "transformForm", "[" + procData.getFlowId() + "," + procData.getPid() + ","
          + procData.getSubPid() + "] " + "flow default xsl name=" + sDefaultXslName);
      transformer = XslTransformerFactory.getTransformer(userInfo, sDefaultXslName);
    }

    if (noPrint && null == transformer) {
      Logger.debug(sLogin, "BlockFormulario", "transformForm", "Flow stylesheet not found. Fail back to system default...");
      transformer = XslTransformerFactory.getDefaultTransformer(userInfo);
    }

    if (null == transformer) {
      retObj = "StyleSheet inv&aacute;lida.";
    } else {
      String sUrl = Const.APP_PROTOCOL + "://" + Const.APP_HOST + ":" + Const.APP_PORT + Const.APP_URL_PREFIX;

      Locale userLocale = userInfo.getUserSettings().getLocale();
      transformer.setParameter("lang_string", userLocale.getLanguage());
      transformer.setParameter("country_string", userLocale.getCountry());
      transformer.setParameter("locale_string", userInfo.getUserSettings().getLangString());
      transformer.setParameter("url_prefix", Const.APP_URL_PREFIX);
      transformer.setParameter("full_url_prefix", sUrl);
      transformer.setParameter("use_scanner", "" + useScanner + "");
      transformer.setParameter("response", response);
      transformer.setParameter("theme", BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName());      
      
      long start = Runtime.getRuntime().freeMemory();
      Logger.debug(sLogin, "BlockFormulario", "transformForm", "MEMORIA: TO STRING=" + (start - Runtime.getRuntime().freeMemory()));
      start = Runtime.getRuntime().freeMemory();
      byte[] xmlBytes = xml.getBytes("UTF-8");
      Logger.debug(sLogin, "BlockFormulario", "transformForm", "MEMORIA: TO BYTE[]=" + (start - Runtime.getRuntime().freeMemory()));
      start = Runtime.getRuntime().freeMemory();
      InputStream isInStream = new ByteArrayInputStream(xmlBytes);
      Logger.debug(sLogin, "BlockFormulario", "transformForm", "MEMORIA: TO ByteArrayOutputStream="
          + (start - Runtime.getRuntime().freeMemory()));
      Logger.debug(sLogin, "BlockFormulario", "transformForm", "MEMORIA: XIXA=" + xmlBytes.length);

      ByteArrayOutputStream osOutStream = new ByteArrayOutputStream();
      transformer.transform(new StreamSource(isInStream), new StreamResult(osOutStream));
      retObj = osOutStream.toString("UTF-8");

      dumpGeneratedForm(xmlBytes, osOutStream.toByteArray());
    }

    return retObj;
  }

  private boolean isFieldDisabled(final UserInfoInterface userInfo, final String disableExpr, final ProcessData process) {
    if (StringUtils.isEmpty(disableExpr) || null == process)
      return false;
    boolean result = false;
    try {
      // process.resetEvaluator();
      result = process.query(userInfo, disableExpr);
    } catch (Exception e) {
      Logger.warning(null, this, "isFieldDisabled", "Condicao Disable caught exception evaluation beanshell (assuming false): ", e);
      result = false;
    }

    // disable/hide field... DO NOT PROCESS
    return result;
  }

  /**
   * Dump generated form to xml and HTML files
   * 
   * @param xml
   *          xml bytes used to build the html form
   * @param html
   *          the generated HTML form
   */
  protected static void dumpGeneratedForm(final byte[] xml, final byte[] html) {
    if (StringUtils.isEmpty(Const.DEBUG_FORM)
        || !ArrayUtils.contains(new String[] { "true", "yes" }, Const.DEBUG_FORM.toLowerCase()))
      return;
    OutputStream tmpOut = null;
    //String iflowHome = System.getProperty("iflow.home");
    String iflowHome = Const.sIFLOW_HOME;
    try {
      // This will output the generated form and HTML
      tmpOut = new FileOutputStream(new File(iflowHome, "form.xml"));
      tmpOut.write(xml);
      tmpOut.close();
      
      tmpOut = new FileOutputStream(new File(iflowHome, "form.html"));
      tmpOut.write(html);
      tmpOut.close();
     
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
     
    	if (tmpOut != null) {
        try {
          tmpOut.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public ProcessData processForm(final UserInfoInterface userInfo, final ProcessData pdProcData, final FormData afdFormData, final ServletUtils servletContext, final boolean ignoreValidation) {

    long start = System.currentTimeMillis();
    ProcessData retObj = processFormProfiled(userInfo, pdProcData, afdFormData, servletContext, ignoreValidation);
    long end = System.currentTimeMillis();
    Logger.trace("BlockFormulario", "processForm", "Form processing took " + (end - start) + " ms");

    return retObj;
  }

  protected ProcessData processFormProfiled(final UserInfoInterface userInfo, final ProcessData pdProcData,
      final FormData afdFormData, final ServletUtils servletContext, final boolean aIgnoreValidation) {

    // Save pending documents
    if(servletContext != null) {
      HttpSession session = servletContext.getSession();
      if(null != session) {
        DocumentSessionHelper helper = (DocumentSessionHelper) session.getAttribute(DocumentSessionHelper.SESSION_ATTRIBUTE);
        if(null != helper)
          helper.updateProcessData(userInfo, pdProcData);
      }
    }

    final ProcessData retObj = pdProcData;
    final String sLogin = userInfo.getUtilizador();

    final ArrayList<FieldInterface> alFields = FormCache.getFields(this);
    FieldInterface fi = null;
    boolean btmp = false;
    StringBuffer sbError = null;
    final ArrayList<String> alErrors = new ArrayList<String>();
    StringBuilder logBuffer = null;;

    final Documents docBean = BeanFactory.getDocumentsBean();

    FormButtonControl fbControl = resetFormButtons(userInfo, retObj, afdFormData);

    boolean ignoreValidation = aIgnoreValidation || fbControl.ignoreValidation;

    HashSet<String> hsDisabledFields = new HashSet<String>();
    boolean bLinkPressed = false;

    HashMap<Integer, Properties> fieldProps = new HashMap<Integer, Properties>();

    retObj.clearError();

    for (int field = 0; nTYPE == nTYPE_FORM && field < alFields.size(); field++) {
      // first of all, process ALL links (to be able to cleanup other link's related vars).. 
      // if a link is clicked, process only link's attributes.. do NOT process entire form!

      fi = alFields.get(field);

      if (fi == null) {
        Logger.warning(sLogin, this, "processForm", 
            retObj.getSignature() + "Field '" + String.valueOf(field) + "' is null... Ignoring...");
        continue;
      }

      Logger.debug(userInfo.getUtilizador(),this,"processForm",
          retObj.getSignature() + "Link processing: field " + field + " of type " + fi.getClass().getName());

      try {
        Properties props = getFieldProperties(userInfo, field);
        fieldProps.put(field, props);

        props.setProperty(FormProps.OUTPUT_FIELD, fi.isOutputField() ? "true" : "false");

        if (fi instanceof pt.iflow.blocks.form.Button || fi instanceof pt.iflow.blocks.form.MenuEntry) {
          String buttonId = "btid"+field;
          if (StringUtils.equals(afdFormData.getParameter(FormProps.sBUTTON_CLICKED), String.valueOf(buttonId))) {

            fbControl.ignoreValidation = Boolean.valueOf(props.getProperty("button_ignore_from_validation"));
            fbControl.ignoreProcessing = Boolean.valueOf(props.getProperty("button_ignore_from_processing"));

            ignoreValidation = aIgnoreValidation || fbControl.ignoreValidation;
          }
           
          String buttonType = props.getProperty("button_type");
          if ("_custom".equals(buttonType)){
            if (StringUtils.equals(afdFormData.getParameter(FormProps.sBUTTON_CLICKED), String.valueOf(buttonId))) {
              String buttonVariable = props.getProperty("button_custom_variable");
              String buttonValue = props.getProperty("button_custom_value");

              try {
                retObj.parseAndSet(buttonVariable, buttonValue);
                this.addToLog("Field button " + buttonId + " set '" + buttonVariable + "' with '" + buttonValue + "';");
              } catch (ParseException e) {
                Logger.error(userInfo.getUtilizador(), this, "resetFormButtons",  retObj.getSignature() + "caught exception in button with var " + buttonVariable + " and value " + buttonValue + ": ", e);
              }
            }
          }
        }
       

        if (fi instanceof pt.iflow.blocks.form.PopupFormField) {
          String buttonId = PopupFormField.POP_UP_ID_PREFIX + field;
          String popupStartBlockId = props.getProperty(PopupFormField.POPUP_FLOW_START_BLOCK_ID);

          if (StringUtils.equals(afdFormData.getParameter(FormProps.sBUTTON_CLICKED), String.valueOf(buttonId))) {
            Port outPort = new Port();
            try {
              outPort.setConnectedPortName(portFront.getConnectedPortName());
              outPort.setName(portFront.getName());
              outPort.setConnectedBlockId(Integer.parseInt(popupStartBlockId));

              this.blockPopUp = outPort;
            } catch (Exception e) {
              Logger.error(userInfo.getUtilizador(), this, "processFormProfiled", "Error while processing popup, resuming primary flow");
            }
          }
        }

        // check if field is disabled
        String disCond = props.getProperty(FormProps.sDISABLE_COND);
        btmp = isFieldDisabled(userInfo, disCond, retObj);
        if (btmp) {
          hsDisabledFields.add(String.valueOf(field));
          Logger.debug(sLogin, this, "processForm", "Link single field " + String.valueOf(field)
              + " ignored since disable condition evaluates true");
          continue;
        }

        // first single props
        String varName = props.getProperty(FormProps.sVARIABLE);
        if (StringUtils.isNotEmpty(varName)) {

          if (FormUtils.checkOutputField(props)) {
            // output/disabled field... DO NOT PROCESS
            hsDisabledFields.add(String.valueOf(field));
            Logger.debug(sLogin, this, "processForm", "Link single field " + String.valueOf(field)
                + "ignored since is output only");
            continue;
          }

          if (!(fi instanceof pt.iflow.blocks.form.Link)) {
            // single prop and not link field type: PROCESS LATER...now only links matter
            continue;
          }

          DataTypeInterface dtiSimple = getDataType(userInfo, retObj, varName, props, fi);
          if (dtiSimple != null) {

            Map<String,Object> deps = new HashMap<String, Object>();

            String extraProp = FormProps.sEXTRA_PROPS;
            dtiSimple.init(userInfo, pdProcData, parseExtraProps(extraProp, props), deps);

            logBuffer = new StringBuilder();
            dtiSimple.parseAndSet(userInfo, retObj, varName, afdFormData, props, ignoreValidation, logBuffer);
            this.addToLog(logBuffer.toString());

            if (!bLinkPressed) {
              // no link yet pressed

              bLinkPressed = StringUtils.equals(retObj.getFormatted(varName), "1");

              if (bLinkPressed) {
                Logger.debug(sLogin, this, "processForm", "link pressed for var " + varName);
              }    
            }
          }
        }

        // now multiple props 

        // // find maximum number of rows to process multiple items
        // XXX deixar de usar max row: agora as listas tem tamanho fixo, pelo que se pode
        // ir pelo seu tamanho
        int nMaxRow = 0;
        String maxRowField = field + FormProps.sMAX_ROW;
        if (afdFormData.hasParameter(maxRowField)) {
          try {
            nMaxRow = Integer.parseInt(afdFormData.getParameter(maxRowField));
          } catch (Exception ei) {
          }
        }

        for (int i = 0; true; i++) {

          String listVarName = props.getProperty(i + "_" + FormProps.sVARIABLE); 
          if (listVarName == null) break;

          String extraProp = i + "_" + FormProps.sEXTRA_PROPS;
          Map<String,String> extraProps = parseExtraProps(extraProp, props);


          if (FormUtils.checkOutputField(props, i)) {
            // output/disabled field.. do not process
            hsDisabledFields.add(String.valueOf(field) + "_" + i); // Is this a "table" field??
            Logger.debug(sLogin, this, "processForm", "Link multiple field " + String.valueOf(field) + "_" + i
                + " ignored since is output only");
            continue;
          }

          // Disable condition
          {
            if (extraProps != null) {
              String disableExpr = extraProps.get(FormProps.sEXTRA_PROP_DISABLE_CONDITION);
              boolean disable = isFieldDisabled(userInfo, disableExpr, retObj);
              if (disable) {
                hsDisabledFields.add(String.valueOf(field) + "_" + i);
                Logger.debug(sLogin, this, "processForm", "Link multiple field " + String.valueOf(field) + "_" + i
                    + " ignored since disable condition evaluates true");
                continue;
              }

            }
          }

          // LEGACY CHECK
          // ORIG: stmp = Utils.unlistVarName(stmp); // variable name without list format
          if (!StringUtils.equals(listVarName, Utils.unlistVarName(listVarName))) {
            // XXX
            throw new Error("OOPPSSSS... BLOCK FORM LEGACY CHECK FOR LISTED VAR NAME!!!");
          }
          // LEGACY CHECK END

          // DATATYPE
          DataTypeInterface dtiMultiple = getDataType(userInfo, retObj, listVarName, i, props, fi);
          if (dtiMultiple != null) {

            if (!(dtiMultiple instanceof pt.iflow.api.datatypes.Link)) {
              // ONLY PROCESS LINKS
              continue;
            }

            Map<String,Object> deps = new HashMap<String, Object>();


            dtiMultiple.init(userInfo, retObj, extraProps, deps);


            logBuffer = new StringBuilder();
            dtiMultiple.parseAndSetList(userInfo, retObj, i, listVarName, nMaxRow, afdFormData, props, ignoreValidation, logBuffer);
            this.addToLog(logBuffer.toString());

            if (!bLinkPressed) {
              // no link yet pressed

              bLinkPressed = StringUtils.equals(retObj.getFormatted(listVarName), "1");

              if (bLinkPressed) {
                if (Logger.isDebugEnabled()) {
                  Logger.debug(sLogin, this, "processForm", "link pressed for var " + listVarName);
                }
              }
            }
          }
        }
      } catch (Exception e) {
        Logger.error(sLogin, this, "processForm", "caught exception processing links in field ", e);
        continue;
      }
    }

    // if a link is pressed, do not process remaining vars
    if (bLinkPressed) {
      this.addToLog("Link Clicked... Exit Form Processing;");
      this.saveLogs(userInfo, pdProcData, this);
      Logger.info(sLogin, this, "processForm", 
          retObj.getSignature() + "link pressed... early exit");
      return retObj;
    }

    if (fbControl.ignoreProcessing) {
      this.addToLog("Button clicked with ignore form processing flag... Exit Form Processing;");
      this.saveLogs(userInfo, pdProcData, this);
      Logger.info(sLogin, this, "processForm", 
          retObj.getSignature() + "Button clicked with ignore form processing flag... early exit");
      return retObj;      
    }

    Stack<NameValuePair<Boolean, TabContainerWrapper>> tabStack = new Stack<NameValuePair<Boolean, TabContainerWrapper>>();
//    Stack<NameValuePair<Boolean, MenuContainerWrapper>> menuStack = new Stack<NameValuePair<Boolean, MenuContainerWrapper>>();
    // now process other non-link fields.
    for (int field = 0; nTYPE == nTYPE_FORM && field < alFields.size(); field++) {

      fi = alFields.get(field);

      if (fi == null) {
        continue;
      }

      Logger.debug(userInfo.getUtilizador(),this,"processForm",
          retObj.getSignature() + "Processing field " + field + " of type " + fi.getClass().getName());

      try {

        Properties props = fieldProps.get(field);

        String disCond = props.getProperty(FormProps.sDISABLE_COND);
        Boolean ignoreField = isFieldDisabled(userInfo, disCond, retObj);
        hsDisabledFields.add(String.valueOf(field));
        NameValuePair myAttr = new NameValuePair<Boolean, TabContainerWrapper>(ignoreField);

        String sFieldClass = fi.getClass().getName();
        if (sFieldClass.equals("pt.iflow.blocks.form.TabDivision")) {
          tabStack.push(myAttr.setValue(new TabDivisionWrapper()));
        } else if (sFieldClass.equals("pt.iflow.blocks.form.TabBegin")) {
          TabContainerWrapper wrapper = tabStack.peek().getValue();
          if(!wrapper.isHolder()) {
            tabStack.pop();
          }
          tabStack.push(myAttr.setValue(new TabWrapper()));
        } else if (sFieldClass.equals("pt.iflow.blocks.form.TabEnd")) {
          if(!tabStack.isEmpty()) {
            TabContainerWrapper wrapper = tabStack.pop().getValue();
            if(wrapper.isHolder()) {
              tabStack.push(myAttr.setValue(wrapper));
            }
          }
        } else if (sFieldClass.equals("pt.iflow.blocks.form.TabEndDivision")) {
          if(!tabStack.isEmpty()) {
            tabStack.pop();
          }
        } else if (!tabStack.isEmpty() && !ignoreField) {
          NameValuePair<Boolean, TabContainerWrapper> peekAttr = tabStack.peek();
          ignoreField = peekAttr.getName();
        }

              
        
        // check if field is disabled
        if (ignoreField) {
          Logger.debug(sLogin, this, "processForm", "Field " + String.valueOf(field) + " marked as disabled. Ignoring....");
          continue;
        }

        // first of all, I need to know how's process table doing....
        // TODO MOVE IMPLEMENTATION FOR FIELD
        if ("pt.iflow.blocks.form.ProcessTable".equals(fi.getClass().getName())) {
          // FIXME ISTO EH UM NOJO!!
          String hasLink = props.getProperty("use_links");
          String linkLabel = props.getProperty("link_label");
          Logger.debug(sLogin, this, "processForm", "Field " + String.valueOf(field) + " hasLink=" + hasLink + "; linkLabel="
              + linkLabel);

          if (!("true".equals(hasLink) || "yes".equals(hasLink)))
            continue; // ignore this field

          Logger.debug(sLogin, this, "processForm", "Field '" + String.valueOf(field) + "' hasLink and is gonna be processed.");

          // TODO ver se se vai imprimir

          String partL = linkLabel + "_" + field;
          String valueL = afdFormData.getParameter(partL);
          retObj.parseAndSet(linkLabel, valueL);
          this.addToLog("Field '" + String.valueOf(field) + "' partL=" + partL + "; valueL=" + valueL + ";");
          Logger.debug(sLogin, this, "processForm", "Field '" + String.valueOf(field) + "' partL=" + partL + "; valueL=" + valueL);

          String varFID = linkLabel + "_flowid";
          String partFID = linkLabel + "_" + field + "_flowid";
          String valueFID = afdFormData.getParameter(partFID);
          retObj.parseAndSet(varFID, valueFID);
          this.addToLog("Field '" + String.valueOf(field) + "' varFID=" + varFID + "; valueFID=" + valueFID + ";");
          Logger.debug(sLogin, this, "processForm", "Field '" + String.valueOf(field) + "' varFID=" + varFID + "; valueFID="
              + valueFID);

          String varPID = linkLabel + "_pid";
          String partPID = linkLabel + "_" + field + "_pid";
          String valuePID = afdFormData.getParameter(partPID);
          retObj.parseAndSet(varPID, valuePID);
          this.addToLog("Field '" + String.valueOf(field) + "' varPID=" + varPID + "; valuePID=" + valuePID + ";");
          Logger.debug(sLogin, this, "processForm", "Field '" + String.valueOf(field) + "' varPID=" + varPID + "; valuePID="
              + valuePID);

          String varSID = linkLabel + "_subpid";
          String partSID = linkLabel + "_" + field + "_subpid";
          String valueSID = afdFormData.getParameter(partSID);
          retObj.parseAndSet(varSID, valueSID);
          this.addToLog("Field '" + String.valueOf(field) + "' varSID=" + varSID + "; valueSID=" + valueSID + ";");
          Logger.debug(sLogin, this, "processForm", "Field '" + String.valueOf(field) + "' varSID=" + varSID + "; valueSID="
              + valueSID);

          String sVarName = "";
          for (int i = 0; (sVarName = props.getProperty(i + "_" + FormProps.sVARIABLE)) != null; i++) {
            String pl = props.getProperty(i + "_pp_pass_link");
            if (null != pl && "true".equals(pl)) {
              String var = linkLabel + "_" + sVarName;
              String part = linkLabel + "_" + field + "_" + sVarName;
              String value = afdFormData.getParameter(part);
              retObj.parseAndSet(var, value);
              this.addToLog("Field '" + String.valueOf(field) + "' var=" + var + "; value=" + value + ";");
              Logger.debug(sLogin, this, "processForm", "Field '" + String.valueOf(field) + "' var=" + var + "; value=" + value);
            }
          }

          Logger.debug(sLogin, this, "processForm", "Field '" + String.valueOf(field) + "' Done!");

          // Do not process anything more for this field
          continue;
        }

        // next try fixed table vars
        // TODO MOVE IMPLEMENTATION TO FIELD
        if (fi instanceof pt.iflow.blocks.form.FixedTable) {

          String sTextArea = props.getProperty(FormProps.sTEXT_AREA);

          BufferedReader br = new BufferedReader(new StringReader(sTextArea));

          String sLine = null;
          while ((sLine = br.readLine()) != null) {
            sLine = sLine.trim();
            if (StringUtils.isEmpty(sLine))
              continue;
            if (sLine.startsWith("#"))
              continue;

            String sProp = sLine.substring(0, sLine.lastIndexOf("="));

            Map<String, String> hmFixedProps = pt.iflow.blocks.form.FixedTable.parseFixedProps(sProp);

            String sType = hmFixedProps.get(pt.iflow.blocks.form.FixedTable.PROP_TYPE);

            if (pt.iflow.blocks.form.FixedTable.TYPE_TEXT.equals(sType))
              continue;

            String sVar = (String) hmFixedProps.get(pt.iflow.blocks.form.FixedTable.PROP_ID);

            String sValue = afdFormData.getParameter(sVar);

            if (sValue == null
                && pt.iflow.blocks.form.FixedTable.PERM_READONLY.equals((String) hmFixedProps
                    .get(pt.iflow.blocks.form.FixedTable.PROP_PERMS))) {
              sValue = afdFormData.getParameter(sVar + "_HIDDEN");
            }

            try {
              int idx1 = sVar.indexOf("[");
              int idx2 = sVar.indexOf("]");
              if (idx1 >=0 && idx2 > idx1) {
                String auxVName = sVar.substring(0, idx1);
                int idx = Integer.valueOf(sVar.substring(idx1+1, idx2));
                retObj.parseAndSetListItem(auxVName, sValue, idx);
              }else
                retObj.parseAndSet(sVar, sValue);

              retObj.parseAndSet(sVar, sValue);
              this.addToLog("Set '" + sVar + "' with '" + sValue + "';");
            } catch (Exception ei) {
            }
          }
        }

        // now try single properties
        String varName = props.getProperty(FormProps.sVARIABLE);
        if (StringUtils.isNotEmpty(varName)) {

          if (FormUtils.checkOutputField(props)) {
            // output/disabled field... DO NOT PROCESS
            Logger.debug(sLogin, this, "processForm", 
                pdProcData.getSignature() + "Single var " + varName + " is output only or disabled.." + " continuing to next one");
            continue;
          }

          if (Logger.isDebugEnabled()) {
            Logger.debug(sLogin, this, "processForm", 
                pdProcData.getSignature() + "processing simple var " + varName);
          }

          // process first special fields
          if (fi instanceof pt.iflow.blocks.form.File) {

            // TODO: move implementation to field File

            // FILE

            Logger.debug(sLogin, this, "processForm", 
                retObj.getSignature() + "processing file var: " + varName);

            ProcessListVariable docsVar = retObj.getList(varName);
            ListIterator<ProcessListItem> docs = docsVar != null ? docsVar.getItemIterator() : null;
            List<ProcessListItem> removedDocs = new ArrayList<ProcessListItem>();
            // Check if must remove files....
            while (docs != null && docs.hasNext()) {
              ProcessListItem docItem = docs.next();

              int nDocId = -1;

              if(docItem != null){
                nDocId = Integer.parseInt(docItem.format());
              }
              String sRemoval = varName + "_rem_[" + nDocId + "]";
              sRemoval = afdFormData.getParameter(sRemoval);

              if (nDocId > -1 && sRemoval != null && sRemoval.equals("true")) {

                // TODO validating flow+pid+subpid for document removal ???

                if (docBean.removeDocument(userInfo, pdProcData, nDocId)) {
                  // remove document & update process
                  // procurar a posicao no array....
                  //docItem.clear();
                  //docsVar.setItem(docItem);
                  // reference file to remove post-processing
                  removedDocs.add(docItem);
                } else {
                  Logger.error(sLogin, this, "processForm", 
                      retObj.getSignature() + "error removing file for var " + varName);
                  retObj.setError("Ocorreu um erro ao remover o ficheiro");
                }
              } else {
                // Fazer updates...
                FormFile ffFile = afdFormData.getFileParameter(varName + "_upd_[" + nDocId + "]"); // updates

                if (ffFile != null && ffFile.isValid()) {
                  // quick and dirty hack: fix file name for IE
                  String fileName = ffFile.getFileName().replace('\\', '/').replaceAll("[^/]*/", "");
                  // process file name
                  if(extensionAccepted(userInfo, pdProcData, props, fileName)) {
                    fileName = getFileName(userInfo, pdProcData, props, fileName);

                    Document doc = new DocumentData(nDocId, fileName, ffFile.getData(), new java.util.Date());
                    doc = docBean.updateDocument(userInfo, pdProcData, doc);
                    Logger.info(sLogin, this, "processForm", 
                        retObj.getSignature() + "file (" + doc.getFileName() + 
                        ") for var " + varName + " updated.");
                  }
                }
              }
            }

            // files must be explicitly removed, hence:
            for (ProcessListItem docItem : removedDocs) {
              if (docItem.getValue() == null) {
                docsVar.removeItem(docItem);
              }
            }

            // Map<String,FormFile> fileParams = afdFormData.getFileParameters();
            for (int i = 0; true; i++) {
              // upload/update document

              // NEW FILE TO UPLOAD
              FormFile ffFile = afdFormData.getFileParameter(varName + "_add_[" + i + "]"); // new files
              String fileID = "";

              if (null != ffFile && ffFile.isValid()) {
                // quick and dirty hack: fix file name for IE
                String fileName = ffFile.getFileName().replace('\\', '/').replaceAll("[^/]*/", "");
                if(extensionAccepted(userInfo, pdProcData, props, fileName)) {
                  fileName = getFileName(userInfo, pdProcData, props, fileName);

                  Document doc = new DocumentData(fileName, ffFile.getData());
                  doc = docBean.addDocument(userInfo, pdProcData, doc);
                  Logger.info(sLogin, this, "processForm", 
                      retObj.getSignature() + "file (" + doc.getFileName() + 
                      ") for var " + varName + " added.");

                  // now update process
                  docsVar.parseAndAddNewItem(String.valueOf(doc.getDocId()));
                }
              } else if (null == ffFile) { // NEW FILE ALREADY UPLOADED
                fileID = afdFormData.getParameter(varName + "_new_[" + i + "]");

                if (null == fileID) {
                  ffFile = null;
                  fileID = null;
                  break;
                }

                int fileId = -1;

                try {
                  fileId = Integer.parseInt(fileID);
                } catch (Exception e) {
                  Logger.warning(userInfo.getUtilizador(), this, "processForm", "Invalid file id", e);
                }

                if(fileId >= 0) {
                  try {
                    // read data from doc table
                    Document doc = docBean.getDocumentInfo(userInfo, pdProcData, fileId);
                    if(extensionAccepted(userInfo, pdProcData, props, doc.getFileName())) {
                      doc.setFileName(getFileName(userInfo, pdProcData, props, doc.getFileName()));
                      docBean.updateDocumentInfo(userInfo, pdProcData, doc);

                      // now update process
                      docsVar.parseAndAddNewItem(fileID);
                    } else {
                      throw new Exception("Extension not accepted: "+ doc.getFileName());
                    }
                  } catch (Exception e) {
                    Logger.warning(userInfo.getUtilizador(), this, "processForm", "Error updating file info", e);
                    docBean.removeDocument(userInfo, pdProcData, fileId);
                  }
                }

              }

            }

            // validation
            if (!ignoreValidation
                && FormUtils.checkRequiredField(userInfo, retObj, props)
                && (docsVar == null || docsVar.size() == 0)) {
              alErrors.add(FormUtils.formatParsingError(props, varName, userInfo.getMessages().getString("Datatype.required_field")));
            }
          } else {

            // DATATYPE
            DataTypeInterface dtiSimple = getDataType(userInfo, retObj, varName, props, fi);
            if (dtiSimple != null) {

              Map<String,Object> deps = new HashMap<String, Object>();

              String extraProp = FormProps.sEXTRA_PROPS;
              dtiSimple.init(userInfo, pdProcData, parseExtraProps(extraProp, props), deps);

              if (dtiSimple instanceof pt.iflow.api.datatypes.Link) {
                // ALREADY PROCESSED
                continue;
              } 

            }

            logBuffer = new StringBuilder();
            String parseResult = null;
            int idx1 = varName.indexOf("[");
            int idx2 = varName.indexOf("]");
            if (dtiSimple != null) {
              if (idx1 >=0 && idx2 > idx1) {
                String auxVName = varName.substring(0, idx1);
                int idx = Integer.valueOf(varName.substring(idx1+1, idx2));
                parseResult = dtiSimple.parseAndSetList(userInfo, retObj, idx, auxVName, 1, afdFormData, props, ignoreValidation, logBuffer);
              }else
                parseResult = dtiSimple.parseAndSet(userInfo, retObj, varName, afdFormData, props, ignoreValidation, logBuffer);
            }
            else {
              Logger.debug(sLogin, this, "processForm", 
                  pdProcData.getSignature() + "var " + varName + " has no datatype associated.. using form value");
              String formValue = afdFormData.hasParameter(varName) ? afdFormData.getParameter(varName) : null; 
              retObj.parseAndSet(varName, formValue); 
              logBuffer.append("Set '" + varName + "' with '" + formValue + "'");
            }

            if (parseResult != null) {
              // format to a more friendly message
              alErrors.add(FormUtils.formatParsingError(props, varName, parseResult));
            }
            else {
              this.addToLog(logBuffer.toString());
              Logger.debug(sLogin, this, "processForm", 
                  pdProcData.getSignature() + logBuffer.toString());

              // fields' special cases
              // TODO / FIXME : MOVE TO FIELD IMPLEMENTATION ..
              if (fi instanceof Selection || fi instanceof SQLSelection || fi instanceof ConSelection) {
                // set also <var><sLIST_TEXT_SUFFIX> var with displayed text
                String value = retObj.getFormatted(varName);
                // ignore obligatory validation for ConSelection
                if (!(fi instanceof ConSelection)
                    && !ignoreValidation
                    && FormUtils.checkRequiredField(userInfo, retObj, props)
                    && StringUtils.equals(props.getProperty(FormProps.sDEFAULT_VALUE), value)) {
                  alErrors.add(FormUtils.formatParsingError(props, varName, userInfo.getMessages().getString("Datatype.required_field")));
                } else {

                  fi.setup(userInfo, retObj, props, servletContext);

                  String propKey = "text_" + value;
                  String addonvalue = props.getProperty(propKey);
                  String addonfull = varName + FormProps.sLIST_TEXT_SUFFIX;
                  retObj.parseAndSet(addonfull, addonvalue);
                  this.addToLog("Set selection text var '" + addonfull + "' as '" + addonvalue + "';");
                  Logger.debug(sLogin, this, "processForm", 
                      pdProcData.getSignature() + "setting selection text var: " + addonfull + "=" + addonvalue);

                }
              }
            }
          }
        }

        // now try multiple values
        handleRowControlList(userInfo, this, retObj, props, field);        
        Set<String> hsIgnoreColumns = computeIgnoreColumns(userInfo, this, retObj, fi.isArrayTable(), props);

        // find maximum number of rows to process multiple items
        int nMaxRow = 0;
        String maxRowField = field + FormProps.sMAX_ROW;
        if (afdFormData.hasParameter(maxRowField)) {
          try {
            nMaxRow = Integer.parseInt(afdFormData.getParameter(maxRowField));
          } catch (Exception ei) {
          }
        }
        for (int i = 0; true; i++) {

          String listVarName = props.getProperty(i + "_" + FormProps.sVARIABLE);
          if (listVarName == null)
            break;

          //          if (hsDisabledFields.contains(String.valueOf(field) + "_" + i)) {
          //            Logger.debug(sLogin, this, "processForm", 
          //                retObj.getSignature() + "Multiple field " + String.valueOf(field) + "_" + i
          //                + " marked as disabled. Ignoring....");
          //            continue;
          //          }

          if (FormUtils.checkOutputField(props, i)) {
            // output/disabled field.. do not process
            Logger.debug(sLogin, this, "processForm", 
                retObj.getSignature() + "Multiple var " + i + "_variable=" + listVarName + " is output only or disabled.."
                + " continuing to next one");
            continue;
          }

          if (hsIgnoreColumns.contains(listVarName)) {
            Logger.debug(sLogin, this, "processForm", 
                retObj.getSignature() + "ignoring column for " + listVarName);
            continue;
          }

          if (Logger.isDebugEnabled()) {
            Logger.debug(sLogin, this, "processForm", 
                pdProcData.getSignature() + "processing list var " + listVarName);
          }

          props.setProperty("varIndex", String.valueOf(i));

          // DATATYPE
          DataTypeInterface dtiMultiple = getDataType(userInfo, retObj, listVarName, i, props, fi);
          if (dtiMultiple != null) {

            if (dtiMultiple instanceof Link) {
              // ALREADY PROCESSED
              continue;
            }
            if (fi.isArrayTable() && !(dtiMultiple instanceof ArrayTableProcessingCapable)) {
              Logger.warning(sLogin, this, "processForm", 
                  retObj.getSignature() + "multiple var " + i + "_variable=" + listVarName + 
                  " of datatype " + dtiMultiple.getClass().getName()
                  + " is not capable of processing array table fields.. continuing to next field");
              continue;
            }

            Map<String,Object> deps = new HashMap<String, Object>();

            String extraProp = i + "_" + FormProps.sEXTRA_PROPS;
            dtiMultiple.init(userInfo, retObj, parseExtraProps(extraProp, props), deps);

            logBuffer = new StringBuilder();
            dtiMultiple.formPreProcess(userInfo, retObj, listVarName, props, logBuffer);
            this.addToLog(logBuffer.toString());
          }

          if (dtiMultiple != null) {
            logBuffer = new StringBuilder();
            String parseResult = dtiMultiple.parseAndSetList(userInfo, retObj, i, listVarName, nMaxRow, afdFormData, props, ignoreValidation, logBuffer);
            if (parseResult != null) {
              // format to a more friendly message
              alErrors.add(FormUtils.formatParsingError(props, listVarName, parseResult));
            }
            else {
              this.addToLog(logBuffer.toString());
            }
          }
          else {
            Logger.debug(sLogin, this, "processForm", 
                retObj.getSignature() + "setting list var " + listVarName + " but no datatype defined!!");
            ProcessListVariable listVar = retObj.getList(listVarName); 
            for (int item=0; item < nMaxRow; item++) {
              if (FormUtils.checkOutputField(props, i, item)) {
                Logger.debug(sLogin, this, "processForm", 
                    retObj.getSignature() + "Row " + item + " of list var '" + listVarName + " is output only... continuing to next row.");
                continue;
              }
              String formName = FormUtils.getListKey(listVarName, item);
              String itemValue = afdFormData.hasParameter(formName) ? 
                  afdFormData.getParameter(formName) : null; 
                  listVar.parseAndSetItemValue(item, itemValue);
                  this.addToLog("Set list var '" + listVarName + "[" + item + "]' with '" + itemValue + "';");
            }
          }

        } // for vars
      } catch (Exception e) {
        Logger.error(sLogin, this, "processForm", retObj.getSignature() + "caught exception in field " + fi.getDescription() + ": ", e);
        alErrors.add("Erro ao processar campo " + fi.getDescription());

        continue;
      }

    } // for fields

    // check validation expressions
    for (int field = 0; nTYPE == nTYPE_FORM && field < alFields.size(); field++) {

      fi = alFields.get(field);

      if (fi == null) {
        continue;
      }

      if (hsDisabledFields.contains(String.valueOf(field))) {
        continue;
      }

      try {

        Properties props = fieldProps.get(field);

        String varName = props.getProperty(FormProps.sVARIABLE);
        if (StringUtils.isNotEmpty(varName)) {

          if (FormUtils.checkOutputField(props)) {
            continue;
          }

          if (StringUtils.isNotEmpty(props.getProperty(FormProps.sVALIDATION_EXPR)) && 
              FormUtils.checkRequiredField(userInfo, retObj, props)) {

            String valExpress = props.getProperty(FormProps.sVALIDATION_EXPR);
            boolean beval = false;

            try {
              beval = retObj.query(userInfo, valExpress);
            } catch (Exception ei) {
              beval = true;
              Logger.error(sLogin, this, "processForm", 
                  pdProcData.getSignature()
                  + "caught exception evaluation beanshell: " + valExpress + " :assuming true", ei);
            }
            if (beval) {
              String msg = FormUtils.getTransformedText(userInfo, pdProcData, props.getProperty(FormProps.sVALIDATION_MSG));            
              alErrors.add(FormUtils.formatParsingError(props, varName, msg));
            }
          }
        }
      } catch (Exception e) {
      }
    }

    if (alErrors.size() > 0) {
      sbError = new StringBuffer();
      for (int i = 0; i < alErrors.size(); i++) {
        if (i > 0) {
          sbError.append("<br/>");
        }
        sbError.append((String) alErrors.get(i));
      }
      retObj.setError(sbError.toString());
      this.addToLog("Found errors: ;   " + sbError.toString().replace("<br/>", ";   ") + ";");
    } else {
      // remove error
      retObj.clearError();
    }

    this.addToLog("Standard Exit;");
    this.saveLogs(userInfo, pdProcData, this);

    return retObj;
  }

  private FormButtonControl resetFormButtons(UserInfoInterface userInfo, ProcessData procData, FormData formData) {
    String user = userInfo.getUtilizador();

    FormButtonControl ret = new FormButtonControl();

    // Tradicional buttons
    List<FormButton> alButtons = FormCache.getButtons(this);
    if (alButtons != null && alButtons.size() > 0) {
      for (int i = 0; alButtons != null && i < alButtons.size(); i++) {
        FormButton formButton = alButtons.get(i);

        if (formButton == null){
          continue;
        }

        FormButtonType type = formButton.getType();

        if (StringUtils.equals(formData.getParameter(FormProps.sBUTTON_CLICKED), String.valueOf(formButton.getId()))) {
          // clicked button
          ret.ignoreProcessing = formButton.ignoreFormProcessing();
          ret.ignoreValidation = formButton.ignoreFormValidation();
        }

        if (type == FormButtonType.CUSTOM) {
          String customVar = formButton.getAttribute(FormButton.ATTR_CUSTOM_VAR);
          String value = formData.getParameter(customVar);

          try {
            procData.parseAndSet(customVar, value);
            this.addToLog("Button " + formButton.getId() + " set '" + customVar + "' with '" + value + "';");
          } catch (ParseException e) {
            Logger.error(user, this, "resetFormButtons", 
                procData.getSignature() + "caught exception in button with var " + customVar
                + " and value " + value + ": ", e);
            continue;
          }
        }
      }
    }
    return ret;
  }

  public boolean hasError(ProcessData procData) {
    return procData.hasError();
  }

  public boolean setError(ProcessData procData, String asError) {
    procData.setError(asError);
    return true;
  }

  public String getError(ProcessData procData) {
    return procData.getError();
  }

  public String exportFieldToSpreadSheet(UserInfoInterface userInfo, ProcessData procData, String asField, OutputStream apsOut,
      ServletUtils response) {

    String retObj = null;

    int nField = -1;

    try {
      nField = Integer.parseInt(asField);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "exportFieldToExcel", procData.getSignature()
          + "caught exception parsing field id=" + asField);
      return "Campo a exportar em falta.";
    }

    String sXml = generateForm(this, userInfo, procData, null, true, FormService.EXPORT, nField, response);

    retObj = BlockData.exportToSpreadSheet(this, userInfo, this.getDescription(userInfo, procData), "row", "col", "value", sXml,
        apsOut);

    return retObj;
  }

  public String exportProcTableToSpreadSheet(UserInfoInterface userInfo, ProcessData procData, PesquisaProcessoSessionData ppsd,
      OutputStream apsOut) {
    String result = null;
    try {

      result = BlockData.exportToSpreadSheet(this, userInfo, this.getDescription(userInfo, procData), ppsd.exportAllData(), apsOut);
    } catch (Throwable t) {
      t.printStackTrace();
      result = t.getMessage();
    }
    return result;
  }

  public String print(UserInfoInterface userInfo, ProcessData procData, String asField, ServletUtils response) {

    String retObj = null;

    int nField = -1;

    try {
      nField = Integer.parseInt(asField);
    } catch (Exception e) {
    }

    retObj = generateForm(this, userInfo, procData, null, true, FormService.PRINT, nField, response);

    return retObj;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Formulário");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Formulário Processado");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    int flowid = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();

    return "Form/" + getJSP(procData) + "?flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid;
  }

  public String getFormName() {
    return sFORM_NAME;
  }

  public String getPrintStylesheet() {
    return this.getAttribute(FormProps.sPRINT_STYLESHEET);
  }

  public Object execute(int op, Object[] aoa) {
    Object retObj = null;

    switch (op) {
    case OP_REFRESH_CACHE:
      // void refreshCache()
      this.refreshCache(null); // dont really need userinfo
      break;
    case OP_GENERATE_FORM:
      // String generateForm(UserInfo,ProcessData,HashMap)
      retObj = this.generateForm((UserInfoInterface) aoa[0], (ProcessData) aoa[1], (Map<String, String>) aoa[2],
          (ServletUtils) aoa[3]);
      break;
    case OP_PROCESS_FORM:
      // DataSet processForm(UserInfo,ProcessData,HashMap)
      retObj = this.processForm((UserInfoInterface) aoa[0], (ProcessData) aoa[1], (FormData) aoa[2], (ServletUtils)aoa[3], (Boolean)aoa[4]);
      break;
    case OP_HAS_ERROR:
      // boolean hasError(ProcessData)
      retObj = new Boolean(this.hasError((ProcessData) aoa[0]));
      break;
    case OP_SET_ERROR:
      // boolean setError(ProcessData,String)
      retObj = new Boolean(this.setError((ProcessData) aoa[0], (String) aoa[1]));
      break;
    case OP_GET_ERROR:
      // String getError(ProcessData)
      retObj = this.getError((ProcessData) aoa[0]);
      break;
    case OP_GET_FORM_NAME:
      // String: var sFORM_NAME
      retObj = this.getFormName();
      break;
    case OP_EXPORT_SPREADSHEET:
      // String exportFieldToExcel (service)
      retObj = this.exportFieldToSpreadSheet((UserInfoInterface) aoa[0], (ProcessData) aoa[1], (String) aoa[2],
          (OutputStream) aoa[3], new ServletUtils()); // "ignorable" servlet utils (no servlet references)
      break;
    case OP_PRINT:
      // String print (service)
      retObj = this.print((UserInfoInterface) aoa[0], (ProcessData) aoa[1], (String) aoa[2], (ServletUtils) aoa[3]);
      break;
    case OP_GET_PRINT_STYLESHEET:
      // String attribute sPRINT_STYLESHEET
      retObj = this.getPrintStylesheet();
      break;
    case OP_EXPORT_PROCTABLE_STYLESHEET:
      // String exportProcTableToExcel (service)
      retObj = this.exportProcTableToSpreadSheet((UserInfoInterface) aoa[0], (ProcessData) aoa[1],
          (PesquisaProcessoSessionData) aoa[2], (OutputStream) aoa[3]);
      break;
    case OP_GET_AUTOPRINT:
      // String auto print
      retObj = this.autoPrint();
      break;
    case OP_GET_PROCESS_SEARCH:
      // String get pesquisa processo
      retObj = this.getPesquisaProcessoFIDs();
      break;
    case OP_AUTO_SUBMIT:
      // Boolean test if can move forward
      retObj = this.isForwardOnSubmit();
      break;
    default:
    }

    return retObj;
  }

  private static Map<String, String> parseExtraProps(String extraProp, Properties props) {
    return parseExtraProps(extraProp, props, DEFAULT_PROPS_SEP);
  }

  private static Map<String, String> parseExtraProps(String extraProp, Properties props, String asSep) {
    if (props != null) {
      String extraProps = props.getProperty(extraProp);  
      return parseExtraProps(extraProps, asSep);
    }
    return null;
  }

  private static Map<String, String> parseExtraProps(String extraProps, String asSep) {
    HashMap<String, String> retObj = null;

      if (StringUtils.isNotEmpty(extraProps)) {

        retObj = new HashMap<String, String>();

        List<String> al = Utils.tokenize(extraProps, asSep);

        for (int jj = 0; jj < al.size(); jj++) {
          String sProp = al.get(jj);
          if (sProp == null)
            continue;
          sProp = sProp.trim();
          int ntmp = sProp.indexOf("=");
          if (ntmp == -1) {
            retObj.put(sProp, "");
          } else {
            String sName = sProp.substring(0, ntmp);
            String sValue = sProp.substring(ntmp + 1);
            sValue = FormUtils.decodeExtraPropValue(sValue);
            
            if (sName != null)
              sName = sName.trim();
            if (sValue != null)
              sValue = sValue.trim();

            retObj.put(sName, sValue);
          }
        }
      }

    return retObj;
  }

  public String autoPrint() {
    ArrayList<FieldInterface> alFields = FormCache.getFields(this);

    int field = 0;
    int nFieldEnd = alFields.size();

    for (; field < nFieldEnd; field++) {
      FieldInterface fi = alFields.get(field);

      String className = fi.getClass().getName();
      if ("pt.iflow.blocks.form.ProcessTable".equals(className))
        return ""; // delay print
    }

    return "onLoad=\"if(window.print) window.print;\"";
  }

  public String getPesquisaProcessoFIDs() {
    ArrayList<FieldInterface> alFields = FormCache.getFields(this);

    int field = 0;
    int nFieldEnd = alFields.size();
    StringBuffer sb = new StringBuffer();

    for (; field < nFieldEnd; field++) {
      FieldInterface fi = alFields.get(field);

      String className = fi.getClass().getName();
      if ("pt.iflow.blocks.form.ProcessTable".equals(className)) {
        sb.append(field).append(",");
      }
    }

    return sb.toString();
  }

  public Boolean isForwardOnSubmit() {
    String forward = getAttribute(FormProps.FORWARD_ON_SUBMIT);
    return new Boolean(StringUtils.equals("true", forward));
  }
  
  private static abstract class TabContainerWrapper {
    public abstract String close();

    public abstract boolean isHolder();
  }

  private static class TabDivisionWrapper extends TabContainerWrapper {
    
    public boolean isHolder() {
      return true;
    }

    public String close() {
      return "</tabdivision>";
    }
  }

  private static class TabWrapper extends TabContainerWrapper {

    public boolean isHolder() {
      return false;
    }

    public String close() {
      return "</columndivision></blockdivision></tab>";
    }
  }

  private static abstract class MenuContainerWrapper {
        public abstract String close();
        public abstract boolean isHolder();
      }

      private static class MenuDivisionWrapper extends MenuContainerWrapper {
        public String close() {
            return "</menuentry>";
          }
        public boolean isHolder() {
          return true;
        }


      }

      private static class MenuWrapper extends MenuContainerWrapper {

        public boolean isHolder() {
          return false;
        }

        public String close() {
          return "</menuentry>";
        }
      }
  
  private static class TextProcessVariableValue implements ProcessVariableValue {

    String value;

    public TextProcessVariableValue(String value) {
      this.value = value;
    }

    public boolean equals(ProcessVariableValue value) {
      return StringUtils.equals(this.value, value.format());
    }

    public String format() {
      return value;
    }

    public String format(ProcessDataType formatter) {
      return format();
    }

    public String format(Format formatter) {
      return format();
    }

    public String getRawValue() {
      return value;
    }

    public Object getValue() {
      return value;
    }

    public void parse(String source) throws ParseException {
      value = source;
    }

    public void parse(String source, ProcessDataType formatter) throws ParseException {
      parse(source);
    }

    public void setValue(Object value) {
      this.value = String.valueOf(value);
    }
  }

  private class FormButtonControl {
    public boolean ignoreProcessing = false;
    public boolean ignoreValidation = false;
    @Override
    public String toString() {      
      return "IGNORE PROCESSING: " + ignoreProcessing + "; IGNORE VALIDATION: " + ignoreValidation;
    }    
  }
  
  private boolean extensionAccepted(UserInfoInterface userInfo, ProcessData procData, Properties props, String name) {
    if(name == null) return false;
    String allowedExtensions = props.getProperty("file_upload_extensions");
    if(StringUtils.isBlank(allowedExtensions)) return true;
    String ext = "";
    int pos=name.lastIndexOf('.');
    if(pos > 0) ext = name.substring(pos);
    String extensions = allowedExtensions;
    try {
      extensions = (String) procData.eval(userInfo, allowedExtensions);
    } catch(Throwable t) {
      Logger.warning(userInfo.getUtilizador(), this, "extensionAccepted", "Error evaluating allowed extensions", t);
    }
    try {
      String [] exts = extensions.split(",");
      for(String e : exts) {
        e = e.trim();
        if(e.equals(ext)) return true;
      }
    } catch(Exception e) {
      Logger.warning(userInfo.getUtilizador(), this, "extensionAccepted", "Error comparing allowed extensions", e);
    }
    return false;
  }

  private String getFileName(UserInfoInterface userInfo, ProcessData procData, Properties props, String name) {
    if(name == null) return null;
    String sKeepExtensions = props.getProperty("file_upload_preserve_ext");
    String nameTemplate = props.getProperty("file_upload_rename");
    if(StringUtils.isBlank(nameTemplate)) return name;
    String ext = "";
    int pos=name.lastIndexOf('.');
    if(pos > 0) ext = name.substring(pos);
    
    String newName = nameTemplate;
    try {
      newName = (String) procData.eval(userInfo, nameTemplate);
    } catch(Throwable t) {
      Logger.warning(userInfo.getUtilizador(), this, "getFileName", "Error evaluating file name", t);
    }
    try {
      boolean keepExtensions = ((Boolean)procData.eval(userInfo, sKeepExtensions)).booleanValue();
      if(keepExtensions && !ext.equals("")) newName += ext;
    } catch(Throwable t) {
      Logger.warning(userInfo.getUtilizador(), this, "getFileName", "Error evaluating extension keep", t);
    }
    return newName;
  }

  private String getJSP(ProcessData procData) {
    if (procData.MustOpenPopup()) {
      procData.setPopupOpened(true);
      return sJSP_OPEN_POPUP;
    } else if (procData.MustClosePopup()) {
      procData.setPopupOpened(false);
      return sJSP_CLOSE_POPUP;
    } else {
      return sJSP;
    }
  }
}

