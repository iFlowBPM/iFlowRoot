package pt.iflow.flows;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import pt.iflow.api.blocks.Attribute;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.blocks.form.Form;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.core.ProcessCatalogueImpl;
import pt.iflow.api.core.Repository;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.flows.FlowSettings;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.flows.MailStartSettings;
import pt.iflow.api.licensing.LicenseService;
import pt.iflow.api.licensing.LicenseServiceException;
import pt.iflow.api.licensing.LicenseServiceFactory;
import pt.iflow.api.processtype.DataTypeEnum;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.services.types.FlowClassGeneratorConvert;
import pt.iflow.api.services.types.FlowDataConvert;
import pt.iflow.api.services.types.ForkJoinDepConvert;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlAttributeType;
import pt.iflow.api.xml.codegen.flow.XmlBlockType;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttributeType;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarsType;
import pt.iflow.api.xml.codegen.flow.XmlFlow;
import pt.iflow.api.xml.codegen.flow.XmlPortType;
import pt.iflow.api.xml.codegen.flow.XmlTemplateType;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.security.SecurityException;

import com.twolattes.json.Marshaller;

/**
 * Flow representation. This class contains all information related to an
 * instantiated flow.
 * 
 * @author oscar
 * 
 */

public class FlowData implements IFlowData,Serializable {

  private static final String BLOCK_PACKAGE = "pt.iflow.blocks."; //$NON-NLS-1$

  private int _nId;
  private String _sName;
  private String _sFile;
  private Vector<Block> _vFlow;
  private boolean _bOnline;
  private boolean _bDeployed;
  private String _sApplicationId;
  private String _sApplicationName;
  private String _sError = null;
  private String _organizationId;
  private long _lastModified;
  private long _created;
  private HashMap<String, Integer> _hmIndexVars = new HashMap<String, Integer>();
  private int indexPosition = 0;
  private boolean _hasDetail = false;
  private Block _detailForm = null;
  private int _seriesId = NO_SERIES;
  private ProcessCatalogueImpl _catalogue;
  private Map<String, Form> _hmFormTemplates = new HashMap<String, Form>();
  private MailStartSettings _mailStartSettings = null;
  private FlowType flowType;
  private int maxBlockId;
  
  private FlowClassGenerator flowClassFile = null;
  
  private Hashtable<String, Hashtable<Integer, Object[]>> _htSubFlowEndPorts = new Hashtable<String, Hashtable<Integer, Object[]>>();

  private Hashtable<Integer, ForkJoinDep> _htForkJoinDepPath = new Hashtable<Integer, ForkJoinDep>();
  private HashSet<Set<Integer>> _hsAllStates = new HashSet<Set<Integer>>();

  private Hashtable<String, String> _htSubFlows = new Hashtable<String, String>();

  private final int iOFFSET = 1100000;

  private boolean hasSchedules = false;

  public static void reloadClasses(UserInfoInterface userInfo) {
    Repository rep = BeanFactory.getRepBean();
    rep.reloadClassLoaders(userInfo);
  }

  /**
   * default constructor
   */  
  public FlowData() {
	super();
  }

   /**
   * init constructor
   */
   private FlowData(int anFlowId, String asName, String asFileName,
       String organizationId, long created, long lastModified, int seriesId, String typeCode) {
     this._nId = anFlowId;
     this._sName = asName;
     this._sFile = asFileName;
     this._vFlow = new Vector<Block>();
     this._organizationId = organizationId;
     this._created = created;
     this._lastModified = lastModified;
     this._seriesId = seriesId;
     this._catalogue = new ProcessCatalogueImpl();
     this.flowType = FlowType.getFlowType(typeCode);
   }

   protected FlowData(int anFlowId, String asName, String asFileName,
       boolean abOnline, String organizationId, long created,
       long lastModified, int seriesId, String typeCode) {
     this(anFlowId, asName, asFileName, organizationId, created,
         lastModified, seriesId, typeCode);
     this._bOnline = abOnline;
   }
  
   protected FlowData(int anFlowId, String asName, String asFileName,
       boolean abOnline, String organizationId, long created,
       long lastModified, int seriesId, String typeCode, int maxBlockId) {
     this(anFlowId, asName, asFileName, organizationId, created,
         lastModified, seriesId, typeCode);
     this._bOnline = abOnline;
     this.setMaxBlockId(maxBlockId);
   }
   // deploy or not
   protected FlowData(UserInfoInterface userInfo, int anFlowId, String asName,
       String asFileName, long created, long lastModified, int seriesId, String typeCode,
       final XmlFlow aXmlFlow) {
     this(userInfo, anFlowId, asName, asFileName, created, lastModified,
         seriesId, typeCode, aXmlFlow, true);
   }

   /**
    * Creates a flow
    * 
    * @param aXmlFlow
    *            the flow
    */
   protected FlowData(final UserInfoInterface userInfo, final int anFlowId,
       final String asName, final String asFileName, final long created,
       final long lastModified, final int seriesId, String typeCode,
       final XmlFlow aXmlFlow, final boolean deploy) {

     this(anFlowId, asName, asFileName, userInfo.getOrganization(), created,
         lastModified, seriesId, typeCode);

     
     flowClassFile = new FlowClassGenerator(userInfo, anFlowId);
     
     // block cache to be used in the ForkJoin dependency graph
     Hashtable<Integer, Block> htBlocks = new Hashtable<Integer, Block>();

     try {

       InstantiationResult flowResult = null;
       List<FlowSetting> alSettings = new ArrayList<FlowSetting>();
       if (deploy)
         flowResult = this.instantiateFlow(userInfo, aXmlFlow, anFlowId, alSettings, htBlocks, 0);

       // SAVE FLOW SETTINGS AND PROCESS SETTINGS CATALOGUE VARS
       // first, "static" settings
       FlowSettings flowSettings = BeanFactory.getFlowSettingsBean();
       List<FlowSetting> alDefaultSettings = flowSettings.getDefaultSettings(anFlowId);

       FlowSetting[] currentSettings = flowSettings.getFlowSettings(userInfo, anFlowId);
       Set<String> settingsToKill = new HashSet<String>();
       if (currentSettings != null) {
         for (FlowSetting s : currentSettings)
           settingsToKill.add(s.getName());
       }

       if (alDefaultSettings.size() > 0 || alSettings.size() > 0) {
         FlowSetting[] fsa = new FlowSetting[alDefaultSettings.size() + alSettings.size()];

         // first static props
         for (int i = 0; i < alDefaultSettings.size(); i++) {
           fsa[i] = alDefaultSettings.get(i);
           // add property name to catalogue vars
           this.setFlowSettingVar(fsa[i]);
         }

         // now block properties
         for (int i = alDefaultSettings.size(), j = 0; j < alSettings.size(); i++, j++) {
           fsa[i] = alSettings.get(j);
           // add property name to catalogue vars
           this.setFlowSettingVar(fsa[i]);
         }
         flowSettings.saveFlowSettings(userInfo, fsa, true);

         // found some properties, save them from a painfull death!
         for (FlowSetting setting : fsa)
           settingsToKill.remove(setting.getName());

         // nobody likes you, so i hope you die with cancer
         for (String settingName : settingsToKill)
           flowSettings.removeFlowSetting(userInfo, anFlowId, settingName);

       }
       
       
       // default format settings
       
       Map<DataTypeEnum, String> mapSettings = new HashMap<DataTypeEnum, String>();
       String settingValue;
       FlowSetting setting;
       setting = flowSettings.getFlowSetting(anFlowId, Const.sFLOW_DATE_FORMAT);
       if(null == setting || StringUtils.isEmpty(setting.getValue()))
         settingValue = Const.sDEF_DATE_FORMAT;
       else
         settingValue = setting.getValue();
       mapSettings.put(DataTypeEnum.Date, settingValue);
       mapSettings.put(DataTypeEnum.DateArray, settingValue);
       
       setting = flowSettings.getFlowSetting(anFlowId, Const.sFLOW_INT_FORMAT);
       if(null == setting || StringUtils.isEmpty(setting.getValue()))
         settingValue = Const.sDEF_INT_FORMAT;
       else
         settingValue = setting.getValue();
       mapSettings.put(DataTypeEnum.Integer, settingValue);
       mapSettings.put(DataTypeEnum.IntegerArray, settingValue);

       setting = flowSettings.getFlowSetting(anFlowId, Const.sFLOW_FLOAT_FORMAT);
       if(null == setting || StringUtils.isEmpty(setting.getValue()))
         settingValue = Const.sDEF_FLOAT_FORMAT;
       else
         settingValue = setting.getValue();
       mapSettings.put(DataTypeEnum.Float, settingValue);
       mapSettings.put(DataTypeEnum.FloatArray, settingValue);


       /* CatalogVars */
       XmlCatalogVarsType xmlcv = aXmlFlow.getXmlCatalogVars();

       if (xmlcv != null && xmlcv.getXmlCatalogVarAttribute().size() > 0) {
         for (int i = 0; i < xmlcv.getXmlCatalogVarAttribute().size(); i++) {
           XmlCatalogVarAttributeType attr = xmlcv.getXmlCatalogVarAttribute().get(i);
           DataTypeEnum dataType = DataTypeEnum.getDataType(attr.getDataType());
           String format = attr.getFormat();
           if(StringUtils.isBlank(format)) format = mapSettings.get(dataType);

           this.setCatalogueVar(attr.getName(), attr.getInitVal(), dataType, attr.isIsSearchable(), attr.getPublicName(), format);
         
           flowClassFile.addVar(dataType, attr.getName());
         }
       }

       Logger.debug(userInfo.getUtilizador(), this, "constructor", "Catalogue: " + System.getProperty("line.separator") + _catalogue);

       // end catalog stuff...
       if (!deploy)
         return; // flow not deployed, quit!

       // builds ForkJoin dependency path
       this.buildForkJoinDepPath(userInfo, htBlocks, new HashSet<Integer>(), flowResult.start, null);
       this.saveForkJoinDepPath(userInfo);

       try {
         instantiateDetailBlock(userInfo, flowResult.start, asFileName);
         Logger.info(userInfo.getUtilizador(), this, "constructor", "Process Detail Block instantiated in " + asFileName + ".");
       } catch (Throwable t) {
         Logger.warning(userInfo.getUtilizador(), this, "constructor", "Could not instantiate Process Detail Block", t);
       }

       try {
         loadFormTemplates(userInfo, aXmlFlow);
         Logger.info(userInfo.getUtilizador(), this, "constructor", "FormTemplates instantiated in " + asFileName + ".");
       } catch (Throwable t) {
         Logger.warning(userInfo.getUtilizador(), this, "constructor", "Could not instantiate FormTemplates", t);
       }
       
       instantiateMailStartSettings(userInfo, flowResult.start);

       setDeployed(true);
       
       flowClassFile.writeContent();
       
     } catch (Exception e) {
       this._vFlow = null;
       // Mas que caralho é esta merda!
       String flowError = userInfo.getMessages().getString("FlowData.error.deployFlow", e.getMessage());
       this.setError(flowError);
       Logger.error(userInfo.getUtilizador(), this, "constructor", "Unable to build flow: " + e.getMessage(), e);
     }
   }

   private InstantiationResult instantiateFlow(UserInfoInterface ui,
       XmlFlow aXmlFlow, int anFlowId, List<FlowSetting> alSettings,
       Hashtable<Integer, Block> htBlocks, int offset) throws Exception {

     Block blockStart = null;
     ArrayList<Block> alBlockEnd = null;
     Hashtable<Integer, Integer> htPortMapIn = new Hashtable<Integer, Integer>();
     Hashtable<Integer, Integer> htPortMapOut = new Hashtable<Integer, Integer>();
     Class<? extends Block> blockClass = null;
     LicenseService licService = LicenseServiceFactory.getLicenseService();

     for (int b = 0; b < aXmlFlow.getXmlBlock().size(); b++) {
       XmlBlockType block = aXmlFlow.getXmlBlock().get(b);
       String blockType = block.getType();
       
       try {
         licService.canInstantiateBlock(ui, blockType);
       } catch (LicenseServiceException e) {
         throw new ForbiddenBlockException(blockType);
       }

// 10/01/2012 - P.Ussman - No longer used due to issue 578 solution  
//       if (blockType.equals("BlockSubFlow")) {
//         InstantiationResult subflowResult = this.instantiateSubFlow(ui,
//             block, anFlowId, alSettings, htBlocks, offset);
//         htPortMapIn.put(new Integer(block.getId()), new Integer(
//             subflowResult.start.getId()));
//         Iterator<Block> it = subflowResult.getEndIterator();
//         while (it.hasNext()) {
//           Block bEnd = it.next();
//           htPortMapOut.put(new Integer(block.getId()), new Integer(
//               bEnd.getId()));
//         }

//         continue;
//       }
       if (blockType.equals("BlockSubFlow"))
    	   continue;
       Integer blockId = new Integer(block.getId() + offset);

       try {
         String stmp = BLOCK_PACKAGE + blockType;
         blockClass = loadBlockClass(ui, stmp);
       } catch (ClassNotFoundException cnfe) {
         throw new Exception("Erro ao criar bloco " + blockType, cnfe);
       }

       Logger.debug(null, this, "constructor", "Processing block "
           + blockClass.getName());
       Constructor<? extends Block> argsConstructor = blockClass
           .getConstructor(int.class, int.class, int.class,
               String.class);
       Block bBlock = argsConstructor.newInstance(anFlowId, blockId, block
           .getId(), aXmlFlow.getName());

       XmlPortType xmlPort = null;
       try {
         // Ports
         for (int portNumber = 0; portNumber < block.getXmlPort().size(); portNumber++) {
           xmlPort = block.getXmlPort().get(portNumber);
           Logger.debug(null, this, "constructor", "Processing port "
               + xmlPort.getName());

           Port port = new Port();
           port.setName(xmlPort.getName());
           port.setConnectedBlockId(xmlPort.getConnectedBlockId()
               + offset);
           port.setConnectedPortName(xmlPort.getConnectedPortName());
           if (xmlPort.getName().equals("portEvent"))
             bBlock.setHasEvent(true);

           Field fPort = blockClass.getField(xmlPort.getName());
           fPort.set(bBlock, port);
         }
       } catch (Exception e) {
         throw new Exception("Erro ao criar porto " + xmlPort.getName()
             + " para o bloco " + block.getType(), e);
       }

       HashMap<String, String> hmtmp = new HashMap<String, String>();
       // Attributes
       for (int attrNumber = 0; attrNumber < block.getXmlAttribute().size(); attrNumber++) {
         XmlAttributeType xmlAttribute = block.getXmlAttribute().get(attrNumber);

         String name = xmlAttribute.getName();
         String value = xmlAttribute.getValue();

         if (value == null)
           value = "";

         if (name.startsWith(IFlowData.sSETTING)
             || name.startsWith(IFlowData.sSETTING_DESC)) {
           hmtmp.put(name, value);
           continue;
         }

         try {
           Logger.debug(null, this, "constructor",
               "Processing attribute " + name + "=" + value);

           Attribute attribute = new Attribute();
           attribute.setName(name);
           attribute.setValue(value);
           bBlock.addAttribute(attribute);
         } catch (Exception e) {
           throw new Exception("Erro ao criar atributo " + name
               + " para o bloco " + blockType, e);
         }
       }

       HashSet<String> hsSettings = new HashSet<String>();
       Iterator<String> iter = hmtmp.keySet().iterator();
       while (iter.hasNext()) {
         String name = (String) iter.next();
         if (!name.startsWith(IFlowData.sSETTING))
           continue;

         String stmp = name.substring(IFlowData.sSETTING.length());
         name = hmtmp.get(name);
         if (hsSettings.contains(name))
           continue;

         try {
           String value = hmtmp.get(IFlowData.sSETTING_DESC + stmp);
           Logger.debug(null, this, "constructor",
               "Processing setting " + name + " (" + value + ")");
           alSettings.add(new FlowSetting(anFlowId, name, value));
           hsSettings.add(name);
         } catch (Exception e) {
           throw new Exception("Erro ao criar propriedade " + name
               + " para o bloco " + blockType, e);
         }
       }

       //Updates PopUp flow conected block id
       bBlock.setBlockRunningInPopup(block.getPopupReturnBlockId());

       // refresh block's cache
       bBlock.refreshCache(ui);

       // Add the block to the vector
       this._vFlow.add(bBlock);

       // keeps a reference to Start Block to build ForkJoin dependency
       // path
       if (bBlock.isStartBlock()) {
         blockStart = bBlock;
       } else if (bBlock.isEndBlock()) {
         if (alBlockEnd == null)
           alBlockEnd = new ArrayList<Block>();
           alBlockEnd.add(bBlock);
       }

       htBlocks.put(blockId, bBlock);
     } // for

     Iterator<Integer> it = htPortMapIn.keySet().iterator();
     while (it.hasNext()) {
       Integer key = (Integer) it.next();
       Integer substId = (Integer) htPortMapIn.get(key);
       Block bBlock = (Block) htBlocks.get(substId); // blockIn

       Port[] inPorts = bBlock.getInPorts(ui);
       for (int ip = 0; inPorts != null && ip < inPorts.length; ip++) {
         if (inPorts[ip] == null)
           continue;
         Integer iConId = new Integer(inPorts[ip].getConnectedBlockId());
         Block btmp = (Block) htBlocks.get(iConId);

         Port[] outPorts = btmp.getOutPorts(ui);
         for (int op = 0; outPorts != null && op < outPorts.length; op++) {
           if (outPorts[op] == null)
             continue;

           int iOutBlockId = outPorts[op].getConnectedBlockId();
           if (iOutBlockId == (key.intValue() + offset)) {
             outPorts[op].setConnectedBlockId(substId.intValue());
           }
         }
       }
     }

     it = htPortMapOut.keySet().iterator();
     while (it.hasNext()) {
       Integer key = (Integer) it.next();
       Integer substId = (Integer) htPortMapOut.get(key);
       Block bBlock = (Block) htBlocks.get(substId); // blockOut

       Port[] outPorts = bBlock.getOutPorts(ui);
       for (int op = 0; outPorts != null && op < outPorts.length; op++) {
         if (outPorts[op] == null)
           continue;
         Integer iConId = new Integer(outPorts[op].getConnectedBlockId());
         Block btmp = (Block) htBlocks.get(iConId);

         Port[] inPorts = btmp.getInPorts(ui);
         for (int ip = 0; inPorts != null && ip < inPorts.length; ip++) {
           if (inPorts[ip] == null)
             continue;

           int iInBlockId = inPorts[ip].getConnectedBlockId();
           if (iInBlockId == (key.intValue() + offset)) {
             inPorts[ip].setConnectedBlockId(substId.intValue());
           }
         }
       }
     }

     // uncomment to check Port Mapping
     if (Const.CHECK_PORT_MAPPING) {

       // should return a warning if any bad block port exist

       Logger.debug("", this, "", "\n******* Checking Port Mapping *******\n");
       it = htBlocks.keySet().iterator();
       while (it.hasNext()) {
         Integer key = (Integer) it.next();
         Block bBlock = (Block) htBlocks.get(key);

         Logger.debug("", this, "", "\nBlockId: " + key + "  block: " + bBlock);
         if (bBlock == null)
           continue;

         Port[] ports = bBlock.getInPorts(ui);
         for (int i = 0; ports != null && i < ports.length; i++) {
           if (ports[i] == null)
             Logger.debug("", this, "", "  null -> b");
           else
             Logger.debug("", this, "", "  "
                 + ports[i].getConnectedPortName() + ": "
                 + ports[i].getConnectedBlockId() + " -> b "
                 + ports[i].getName());
         }
         ports = bBlock.getOutPorts(ui);
         for (int i = 0; ports != null && i < ports.length; i++) {
           if (ports[i] == null)
             Logger.debug("", this, "", "  b -> null");
           else
             Logger.debug("", this, "", " " + ports[i].getName() + " b -> "
                 + ports[i].getConnectedBlockId());
         }
       }
     }
     return new InstantiationResult(blockStart, alBlockEnd);
   }
@Deprecated
   private InstantiationResult instantiateSubFlow(UserInfoInterface ui,
       XmlBlockType xmlblock, int anFlowId, List<FlowSetting> alSettings,
       Hashtable<Integer, Block> htBlocks, int offset) throws Exception {
     Class<?>[] argsClass = new Class[] { int.class, int.class, int.class,
         String.class };
     String subflow = null;
     Attribute attrActiv = null;
     final int FLOW = 0;
     final int SUBFLOW = 1;
     final int TYPE = 2;

     offset += iOFFSET;

     int size = (xmlblock.getXmlAttribute().size() - 1) / 4; // raio de conta

     String[][] saInVars = new String[size][3];
     String[][] saOutVars = new String[size][3];

     // Attributes do block In & Out
     for (int attrNumber = 0; attrNumber < xmlblock.getXmlAttribute().size(); attrNumber++) {
       XmlAttributeType xmlAttribute = xmlblock.getXmlAttribute().get(attrNumber);
       String name = xmlAttribute.getName();

       int pos = -1;
       try {
         pos = Integer.parseInt(name.substring(8, 9));
       } catch (NumberFormatException nfe) {
       }

       if (name.charAt(0) == 'I') {
         if (name.substring(1, 8).equals("bigflow")) {
           saInVars[pos][FLOW] = xmlAttribute.getValue();
         } else {
           saInVars[pos][SUBFLOW] = xmlAttribute.getValue();
         }
       } else if (name.charAt(0) == 'O') {
         if (name.substring(1, 8).equals("bigflow")) {
           saOutVars[pos][FLOW] = xmlAttribute.getValue();
         } else {
           saOutVars[pos][SUBFLOW] = xmlAttribute.getValue();
         }
       } else if (name.charAt(0) == 'M') {
         subflow = xmlAttribute.getValue();
       } else if (name.charAt(0) == 'A') {
         attrActiv = new Attribute(xmlAttribute.getName(), xmlAttribute
             .getValue());
       } else if (name.charAt(0) == 'T') {
         saInVars[pos][TYPE] = xmlAttribute.getValue();
         saOutVars[pos][TYPE] = xmlAttribute.getValue();
       }
     }

     if (!this.hasSubFlow(subflow)) {
       this._htSubFlows.put(subflow, subflow);
     } else {
       throw new Exception("Recursividade de Fluxos encontrada!");
     }
     byte[] sXml = BeanFactory.getFlowHolderBean().readSubFlowData(ui,
         subflow);
     XmlFlow xmlSubFlow = FlowMarshaller.unmarshal(sXml);
     InstantiationResult flowResult = instantiateFlow(ui, xmlSubFlow,
         anFlowId, alSettings, htBlocks, offset);

     if (flowResult == null) {
       throw new Exception(
           "N&atilde;o foi possivel instanciar o subfluxo " + subflow);
     } else if (flowResult.start == null) {
       throw new Exception("O subfluxo " + subflow
           + " n&atilde;o tem BlockStart");
     } else if (flowResult.end == null || flowResult.end.size() == 0) {
       throw new Exception("O subfluxo " + subflow
           + " n&atilde;o tem BlockEnd");
     }

     if (!_htSubFlowEndPorts.containsKey(subflow)) {
       Hashtable<Integer, Object[]> htPorts = new Hashtable<Integer, Object[]>();
       this.buildConnEndPorts(ui, htBlocks, htPorts, flowResult.start);
       _htSubFlowEndPorts.put(subflow, htPorts);
     }

     // remove the start & end blocks
     // replace them by BlockSubFlowIn & BlockSubFlowOut
     htBlocks.remove(new Integer(flowResult.start.getId()));
     this._vFlow.remove(flowResult.start);
     Iterator<Block> it = flowResult.getEndIterator();
     while (it.hasNext()) {
       Block bEnd = it.next();
       htBlocks.remove(new Integer(bEnd.getId()));
       this._vFlow.remove(bEnd);
     }

     // BlockSubFlowIn
     Integer blockIdIn = new Integer(flowResult.start.getId() - 100000);
     Object[] args = new Object[] { new Integer(anFlowId), blockIdIn,
         new Integer(0), new String(subflow) };
     String className = "pt.iflow.blocks.BlockSubFlowIn";
     Class<? extends Block> blockClassIn = loadBlockClass(ui, className);
     Constructor<? extends Block> argsConstructor = blockClassIn
         .getConstructor(argsClass);
     Block bBlockIn = argsConstructor.newInstance(args);

     // attributes
     for (int i = 0; i < saInVars.length; i++) {
       if (StringUtils.isEmpty(saInVars[i][FLOW])
           || StringUtils.isEmpty(saInVars[i][SUBFLOW]))
         continue;
       Attribute attr = new Attribute();
       attr.setName(saInVars[i][FLOW]);
       attr.setValue(saInVars[i][SUBFLOW]);
       bBlockIn.addAttribute(attr);
       attr = new Attribute("Type_" + saInVars[i][FLOW], saInVars[i][TYPE]);
       bBlockIn.addAttribute(attr);
     }

     // BlockSubFlowOut
     Integer blockIdOut = new Integer(flowResult.end.get(0).getId() - 100000);
     args = new Object[] { new Integer(anFlowId), blockIdOut,
         new Integer(0), new String(subflow) };
     className = "pt.iflow.blocks.BlockSubFlowOut";
     Class<? extends Block> blockClassOut = loadBlockClass(ui, className);
     argsConstructor = blockClassOut.getConstructor(argsClass);
     Block bBlockOut = argsConstructor.newInstance(args);

     for (int i = 0; i < saOutVars.length; i++) {
       if (saOutVars[i][FLOW] == null || saOutVars[i][FLOW].equals("")
           || saOutVars[i][SUBFLOW] == null
           || saOutVars[i][SUBFLOW].equals(""))
         continue;
       Attribute attr = new Attribute();
       attr.setName(saOutVars[i][FLOW]);
       attr.setValue(saOutVars[i][SUBFLOW]);
       bBlockOut.addAttribute(attr);
       attr = new Attribute("Type_" + saOutVars[i][FLOW],
           saOutVars[i][TYPE]);
       bBlockOut.addAttribute(attr);
     }
     if (attrActiv != null)
       bBlockOut.addAttribute(attrActiv);

     // portos do BlockSubFlowIn
     Port port = null;
     try {
       XmlPortType xmlPort = xmlblock.getXmlPort().get(0); // portIn -> portIn
       port = new Port();
       port.setName("portIn");
       port.setConnectedBlockId(xmlPort.getConnectedBlockId()
           + (offset - iOFFSET));
       port.setConnectedPortName(xmlPort.getConnectedPortName());
       Field fPort = blockClassIn.getField("portIn");
       fPort.set(bBlockIn, port);

       port = new Port(); // portOutThread
       port.setName("portOutThread");
       port.setConnectedBlockId(bBlockOut.getId());
       port.setConnectedPortName("portInThread");
       fPort = blockClassIn.getField("portOutThread");
       fPort.set(bBlockIn, port);

       port = new Port(); // portOut
       port.setName("portOut");
       fPort = blockClassIn.getField("portOut");
       fPort.set(bBlockIn, port);

       Port[] patmp = flowResult.start.getOutPorts(ui);
       for (int p = 0; patmp != null && p < patmp.length; p++) {
         if (patmp[p] == null)
           continue;

         Integer iBId = new Integer(patmp[p].getConnectedBlockId());
         Block bInnerBlock = (Block) htBlocks.get(iBId);
         if (bInnerBlock == null)
           continue;

         bInnerBlock.getInPorts(ui)[0].setConnectedBlockId(bBlockIn
             .getId());
         bBlockIn.getOutPorts(ui)[0].setConnectedBlockId(patmp[p]
                                                               .getConnectedBlockId());
         bBlockIn.getOutPorts(ui)[0].setConnectedPortName(patmp[p]
                                                                .getConnectedPortName());
       }

//       xmlPort = xmlblock.getXmlPort(2); // portError -> portError
//       port = new Port();
//       port.setName("portError");
//       port.setConnectedBlockId(xmlPort.getConnectedBlockId()
//           + (offset - iOFFSET));
//       port.setConnectedPortName(xmlPort.getConnectedPortName());
//       fPort = blockClassIn.getField("portError");
//       fPort.set(bBlockIn, port);
     } catch (Exception e) {
       e.printStackTrace();
       throw new Exception("Erro ao criar porto " + port.getName()
           + " para o bloco SubFlowIn");
     }

     // portos do BlockSubFlowOut
     try {
       port = new Port(); // portIn
       port.setName("portIn");
       Field fPort = blockClassOut.getField("portIn");
       fPort.set(bBlockOut, port);

       Hashtable<Integer, Object[]> htPorts = _htSubFlowEndPorts
       .get(subflow);
       Iterator<Integer> portIt = htPorts.keySet().iterator();
       while (portIt.hasNext()) {
         Integer itmp = portIt.next();
         Object[] otmp = (Object[]) htPorts.get(itmp);
         Port outPort = (Port) otmp[0];

         outPort.setConnectedBlockId(bBlockOut.getId());
         outPort.setConnectedPortName(port.getName());

         bBlockOut.getInPorts(ui)[0]
                                  .setConnectedBlockId(itmp.intValue());
         bBlockOut.getInPorts(ui)[0].setConnectedPortName(outPort
             .getName());
       }

       port = new Port(); // portInThread
       port.setName("portInThread");
       port.setConnectedBlockId(bBlockIn.getId());
       port.setConnectedPortName("portOutThread");
       fPort = blockClassOut.getField("portInThread");
       fPort.set(bBlockOut, port);

       XmlPortType xmlPort = xmlblock.getXmlPort().get(1); // portSuccess -> portOut
       port = new Port();
       port.setName("portOut");
       port.setConnectedBlockId(xmlPort.getConnectedBlockId()
           + (offset - iOFFSET));
       port.setConnectedPortName(xmlPort.getConnectedPortName());
       fPort = blockClassOut.getField("portOut");
       fPort.set(bBlockOut, port);

//       xmlPort = xmlblock.getXmlPort(2); // portError
//       port = new Port();
//       port.setName("portError");
//       port.setConnectedBlockId(xmlPort.getConnectedBlockId()
//           + (offset - iOFFSET));
//       port.setConnectedPortName(xmlPort.getConnectedPortName());
//       fPort = blockClassOut.getField("portError");
//       fPort.set(bBlockOut, port);
     } catch (Exception e) {
       e.printStackTrace();
       throw new Exception("Erro ao criar porto " + port.getName()
           + " para o bloco SubFlowOut");
     }

     this._vFlow.add(bBlockIn);
     this._vFlow.add(bBlockOut);

     htBlocks.put(blockIdIn, bBlockIn);
     htBlocks.put(blockIdOut, bBlockOut);

     ArrayList<Block> altmp = new ArrayList<Block>();
     altmp.add(bBlockOut);

     return new InstantiationResult(bBlockIn, altmp);
   }

   private void buildConnEndPorts(UserInfoInterface ui,
       Hashtable<Integer, Block> htBlocks,
       Hashtable<Integer, Object[]> htPorts, Block btmp) {
     Port[] ptmp = btmp.getOutPorts(ui);
     for (int p = 0; ptmp != null && p < ptmp.length; p++) {
       if (ptmp[p] == null)
         continue;
       Integer connId = new Integer(ptmp[p].getConnectedBlockId());
       Block bEndtmp = (Block) htBlocks.get(connId);
       if (bEndtmp == null)
         continue;
       if (bEndtmp.isEndBlock()) {
         Object[] otmp = new Object[2];
         otmp[0] = ptmp[p];
         otmp[1] = bEndtmp;
         htPorts.put(new Integer(btmp.getId()), otmp);
       }
       this.buildConnEndPorts(ui, htBlocks, htPorts, bEndtmp);
     }
   }

   private void setCatalogueVar(String sVar, String sValue, DataTypeEnum sType, boolean isSearchable, String sPublicName, String sDefaultFormat) {
     if (this._catalogue.hasVar(sVar))
       return;
     
     this._catalogue.importDataType(sVar, sValue, sType, isSearchable, sPublicName, sDefaultFormat);
     if (isSearchable && indexPosition < Const.INDEX_COLUMN_COUNT)
       this._hmIndexVars.put(sVar, indexPosition++);
   }

   private void setFlowSettingVar(FlowSetting settingVar) {
     if (this._catalogue.hasVar(settingVar.getName()))
       return;
     this._catalogue.importFlowSetting(settingVar.getFlowId(), settingVar.getName(), settingVar.getDescription());
   }

   public ProcessCatalogue getCatalogue() {
     return this._catalogue;
   }

   private void instantiateMailStartSettings(UserInfoInterface ui, Block startBlock) {
     _mailStartSettings = MailStartSettings.parse(startBlock);
   }

   
   private Block instantiateDetailBlock(UserInfoInterface ui, Block startBlock, String flowName) throws Exception {
     // Integer blockId = startBlock.getId();
     _detailForm = null;

     String buildDetailForm = startBlock.getAttribute(sDETAIL_FORM);
     if (StringUtils.equalsIgnoreCase("no", buildDetailForm)) {
       this._hasDetail = false;
       return null;
     } else {
       this._hasDetail = true;
       // if not "form" or sDETAIL_FORM_EXISTINGBLOCKFORM, assumes "default"
       if (!(StringUtils.equalsIgnoreCase("form", buildDetailForm) || 
           StringUtils.equalsIgnoreCase(sDETAIL_FORM_EXISTINGBLOCKFORM, buildDetailForm)))
         return null;
     }

     Class<? extends Block> blockClass = null;
     try {
       // force block to be a BlockDetailForm and not a BlockFormulario
       String className = BLOCK_PACKAGE + "BlockDetailForm";// startBlock.getAttribute(sDETAIL_CLASS);
       if (null == className)
         return null;
       blockClass = loadBlockClass(ui, className);
     } catch (ClassNotFoundException cnfe) {
       throw new Exception("Erro ao criar bloco de detalhe de processo",
           cnfe);
     }

     Logger.debug(null, this, "constructor", "Processing block "
         + blockClass.getName());
     Constructor<? extends Block> argsConstructor = blockClass
         .getConstructor(int.class, int.class, int.class, String.class);
     Block bBlock = argsConstructor.newInstance(startBlock.getFlowId(),
         startBlock.getId(), startBlock.getSubFlowBlockId(), flowName);
     
     if (StringUtils.equalsIgnoreCase("form", buildDetailForm)) {
       HashMap<String, String> hmAttrs = startBlock.getAttributeMap();
       // Attributes
       for (String name : hmAttrs.keySet()) {
         String value = startBlock.getAttribute(name);
         if (!name.startsWith(IFlowData.sDETAIL)) {
           continue;
         }

         Attribute attribute = new Attribute();
         attribute.setName(name.substring(IFlowData.sDETAIL.length()));
         attribute.setValue(value);
         bBlock.addAttribute(attribute);
       }
     }
     else if (StringUtils.equalsIgnoreCase(sDETAIL_FORM_EXISTINGBLOCKFORM, buildDetailForm)) {
       int formBlockId = Integer.parseInt(startBlock.getAttribute(sDETAIL_FORM_BID));
       Block bf = this.getBlock(formBlockId);
       HashMap<String, String> hmAttrs = bf.getAttributeMap();
       // Attributes
       for (String name : hmAttrs.keySet()) {
         String value = bf.getAttribute(name);
         bBlock.addAttribute(new Attribute(name, value));
       }    
       
       bBlock.addAttribute(new Attribute(FormProps.READ_ONLY, "true"));
     }
     
     // clear cache
     bBlock.refreshCache(ui);

     _detailForm = bBlock;
     return bBlock;
   }

   private void saveForkJoinDepPath(UserInfoInterface userInfo) {

     String login = userInfo.getUtilizador();
     DataSource ds = Utils.getDataSource();
     Connection db = null;
     Statement st = null;

     try {
       db = ds.getConnection();
       db.setAutoCommit(false);
       st = db.createStatement();

       // clean former deploys
       st.executeUpdate("delete from forkjoin_state_dep where flowid="
           + this._nId);
       st.executeUpdate("delete from forkjoin_hierarchy where flowid="
           + this._nId);
       st.executeUpdate("delete from forkjoin_blocks    where flowid="
           + this._nId);

       // fill the block table
       Enumeration<ForkJoinDep> enumer = _htForkJoinDepPath.elements();
       while (enumer.hasMoreElements()) {
         ForkJoinDep fjp = enumer.nextElement();
         st
         .executeUpdate("insert into forkjoin_blocks (flowid, blockid, type) "
             + "values ("
             + this._nId
             + ","
             + fjp.getBlockId() + "," + fjp.getType() + ")");
       }

       // now fill the 2 dependency tables
       enumer = _htForkJoinDepPath.elements();
       while (enumer.hasMoreElements()) {
         ForkJoinDep fjp = enumer.nextElement();

         // build fork/join hierarchy
         Enumeration<ForkJoinDep> enumJFStates = fjp.elementsJFStates();
         while (enumJFStates.hasMoreElements()) {
           ForkJoinDep fjpSon = enumJFStates.nextElement();
           st.executeUpdate("insert into forkjoin_hierarchy (flowid, "
               + "parentblockid, blockid) values (" + this._nId
               + "," + fjp.getBlockId() + ","
               + fjpSon.getBlockId() + ")");
         }
         // build fork/join state dependencies
         Iterator<Integer> itStates = fjp.iteratorStates();
         while (itStates.hasNext()) {
           Integer iState = itStates.next();
           st.executeUpdate("insert into forkjoin_state_dep (flowid, "
               + "parentblockid, blockid) values (" + this._nId
               + "," + fjp.getBlockId() + "," + iState + ")");
         }
       }
       db.commit();
     } catch (SQLException sqle) {
       Logger.error(login, this, "saveForkJoinDepPath",
           "caught sql exception: " + sqle.getMessage());
       sqle.printStackTrace();
       try {
         db.rollback();
       } catch (Exception e) {
       }
     } catch (Exception e) {
       Logger.error(login, this, "saveForkJoinDepPath",
           "caught exception: " + e.getMessage());
       e.printStackTrace();
       try {
         db.rollback();
       } catch (Exception el) {
       }
     } finally {
       DatabaseInterface.closeResources(db,st);
     }
   }

   private void buildForkJoinDepPathImpl(UserInfoInterface userInfo,
       Hashtable<Integer, Block> htBlocks, Set<Integer> alStates,
       Block block, ForkJoinDep lastFJD) {

     if (block == null || block.isEndBlock())
       return;
     Integer blockId = new Integer(block.getId());

     if (alStates.contains(blockId))
       return;
     if (_hsAllStates.contains(blockId))
       return;

     if (lastFJD != null && lastFJD.hasDependency(blockId))
       return;

     Port[] outports = block.getOutPorts(userInfo);
     Integer nextBlockId = null;
     Block nextBlock = null;

     if (block.getClass().getName().indexOf("BlockSincronizacao") != -1
         || block.getClass().getName().indexOf("JuncaoExclusiva") != -1) {

       if (_htForkJoinDepPath.containsKey(blockId)) {
         if (lastFJD == null || !lastFJD.hasDependency(blockId)) {
           ForkJoinDep fjd = _htForkJoinDepPath.get(blockId);
           if (lastFJD != null)
             fjd.addJFState(lastFJD);
           fjd.addStates(alStates);
           _hsAllStates.add(alStates);
         }
         // other branch already passed here, so do not continue
         // recursion
       } else {
         ForkJoinDep fjd = new ForkJoinDep(blockId, ForkJoinDep.JOIN,
             alStates);
         if (lastFJD != null)
           fjd.addJFState(lastFJD);
         _htForkJoinDepPath.put(blockId, fjd);

         for (int i = 0; outports != null && i < outports.length; i++) {
           if (outports[i] == null)
             continue;
           nextBlockId = new Integer(outports[i].getConnectedBlockId());
           nextBlock = htBlocks.get(nextBlockId);
           this.buildForkJoinDepPathImpl(userInfo, htBlocks,
               new HashSet<Integer>(), nextBlock, fjd);
         }
       }

     } else if (block.getClass().getName().indexOf("BlockBifurcacao") != -1) {

       ForkJoinDep fjd = new ForkJoinDep(blockId, ForkJoinDep.FORK,
           alStates);
       if (lastFJD != null)
         fjd.addJFState(lastFJD);
       _htForkJoinDepPath.put(blockId, fjd);

       for (int i = 0; outports != null && i < outports.length; i++) {
         if (outports[i] == null)
           continue;
         nextBlockId = new Integer(outports[i].getConnectedBlockId());
         nextBlock = htBlocks.get(nextBlockId);
         this.buildForkJoinDepPathImpl(userInfo, htBlocks,
             new HashSet<Integer>(), nextBlock, fjd);
       }

     } else {

       if (!block.isStartBlock())
         alStates.add(blockId);

       for (int i = 0; outports != null && i < outports.length; i++) {
         if (outports[i] == null)
           continue;
         nextBlockId = new Integer(outports[i].getConnectedBlockId());
         nextBlock = htBlocks.get(nextBlockId);
         this.buildForkJoinDepPathImpl(userInfo, htBlocks,
             new HashSet<Integer>(alStates), nextBlock, lastFJD);
       }
     }
   }

   // TODO finish
   private void buildForkJoinDepPath(UserInfoInterface userInfo,
       Hashtable<Integer, Block> htBlocks, Set<Integer> alStates,
       Block block, ForkJoinDep lastFJD) {

     // So vale a pena gastar CPU a percorrer grafos se existirem blocos
     // bifurcação, sincronização ou junção
     boolean blockFound = false;
     for (Block b : htBlocks.values()) {
       String name = b.getClass().getName();
       // TODO encontrar uma forma mais elegante de efectuar este teste.
       if (name.contains("BlockSincronizacao")
           || name.contains("JuncaoExclusiva")
           || name.contains("BlockBifurcacao")) {
         blockFound = true;
         break;
       }
     }

     if (!blockFound)
       return;

     buildForkJoinDepPathImpl(userInfo, htBlocks, alStates, block, lastFJD);
   }

   /**
    * Checks if flow is guest accessible.
    * 
    * @param userInfo
    *            User information.
    * @return True if the flow is constructed in a way that allows unregistered
    *         users to access it, false otherwise.
    */
   public boolean isGuestCompatible(UserInfoInterface userInfo) {
     if (Logger.isDebugEnabled()) {
       Logger.debug(userInfo.getUtilizador(), this,
           "isGuestCompatible(UserInfoInterface)", "start");
     }

     FlowSetting procLocation = BeanFactory.getFlowSettingsBean().getFlowSetting(
         this.getId(), Const.sPROCESS_LOCATION);
     if (StringUtilities.isEqual(procLocation.getValue(), Const.sPROCESS_IN_DB)) {
       if (Logger.isDebugEnabled()) {
         Logger.debug(userInfo.getUtilizador(), this,
             "isGuestCompatible(UserInfoInterface)", 
         "process location set  to db... returning 'false'.");
       }
       return false;
     }        

     Vector<Block> blockVector = this.getFlow();
     // search for start block
     Block startBlock = null;
     for (Block currentBlock : blockVector) {
       if (currentBlock.isStartBlock()) {
         startBlock = currentBlock;
         break;
       }
     }
     boolean result = searchDeep(userInfo, startBlock, new HashSet<Integer>());

     if (Logger.isDebugEnabled()) {
       Logger.debug(userInfo.getUtilizador(), this,
           "isGuestCompatible(UserInfoInterface)", "end : returning '"
           + result + "'.");
     }

     return result;
   }

   private boolean searchDeep(UserInfoInterface userInfo, Block currentBlock, HashSet<Integer> processedBlocks) {
     if (currentBlock == null) {
       return false;
     }

     if (Logger.isDebugEnabled()) {
       Logger.debug(userInfo.getUtilizador(), this,
           "searchDeep(UserInfoInterface,Block)", "start : block id="
           + currentBlock.getId() + " (isForwardBlock?"
           + currentBlock.isForwardBlock() + ", isEndBlock?"
           + currentBlock.isEndBlock()
           + ", isProcInDBRequired?"
           + currentBlock.isProcInDBRequired() + ", "
           + currentBlock.getClass().getName() + ").");
     }

     if (currentBlock.isForwardBlock() || currentBlock.isEndBlock()) {
       if (Logger.isDebugEnabled()) {
         Logger.debug(userInfo.getUtilizador(), this,
             "searchDeep(UserInfoInterface,Block)",
             "end : block id=" + currentBlock.getId()
             + ". Found "
             + (currentBlock.isForwardBlock() ? "forward" : "end") 
             + " block, returning 'true'.");
       }
       return true;
     } else if (currentBlock.isProcInDBRequired()) {
       if (Logger.isDebugEnabled()) {
         Logger
         .debug(
             userInfo.getUtilizador(),
             this,
             "searchDeep(UserInfoInterface,Block)",
             "end : block id="
             + currentBlock.getId()
             + ". Unable to proceed with search from this block on: "
             + "isProcInDBRequired, returning 'false'.");
       }
       return false;
     }
     List<Block> queue = getChildBlocks(currentBlock);
     for (Block block : queue) {
              
       if (processedBlocks.contains(block.getId())) {
         Logger.info(userInfo.getUtilizador(), this, "searchDeep", 
             "Already processed child block " + block.getId() + " for block " + currentBlock.getId());
         continue;
       }
       processedBlocks.add(block.getId());
       
       try {
         if (!searchDeep(userInfo, block, processedBlocks)) {
           if (Logger.isDebugEnabled()) {
             Logger
             .debug(
                 userInfo.getUtilizador(),
                 this,
                 "searchDeep(UserInfoInterface,Block)",
                 "end : block id="
                 + currentBlock.getId()
                 + ". Interrupted deep search algorithm, returning 'false'.");
           }
           return false;
         }
       } catch (StackOverflowError ex) {
         Logger.warning(userInfo.getUtilizador(), this, "searchDeep", "Stack overflow, returning 'false'!");
         return false;
       }
     }

     if (Logger.isDebugEnabled()) {
       Logger
       .debug(
           userInfo.getUtilizador(),
           this,
           "searchDeep(UserInfoInterface,Block)",
           "end : block id="
           + currentBlock.getId()
           + ". Finished deep search algorithm, returning 'true'.");
     }
     return true;
   }

   private List<Block> getChildBlocks(Block block) {
     List<Block> result = new ArrayList<Block>();
     Port[] ports = block.getOutPorts(null);
     for (Port port : ports) {
    	 if(port!=null)
    		 result.add(getBlock(port.getConnectedBlockId()));
     }
     return result;
   }

   private Block getBlock(int blockId) {
     Block result = null;
     Vector<Block> blockVector = this.getFlow();
     for (Block currentBlock : blockVector) {
       if (currentBlock.getId() == blockId) {
         result = currentBlock;
         break;
       }
     }
     return result;
   }

   public int getId() {
     return this._nId;
   }

   public String getName() {
     return new String(this._sName);
   }

   public String getFileName() {
     return new String(this._sFile);
   }

   public boolean isOnline() {
     return this._bOnline;
   }

   protected void setOnline(boolean abOnline) {
     this._bOnline = abOnline;
   }

   public boolean isDeployed() {
     return this._bDeployed;
   }

   public boolean hasSubFlow(String subflow) {
     return this._htSubFlows.containsKey(subflow);
   }

   private void setDeployed(boolean abDeployed) {
     this._bDeployed = abDeployed;
   }
   
   public String getApplicationId() {
     return _sApplicationId;
   }

   public void setApplicationId(String applicationId) {
     _sApplicationId = applicationId;
   }

   public String getApplicationName() {
     return _sApplicationName;
   }

   public void setApplicationName(String applicationName) {
     _sApplicationName = applicationName;
   }

   public boolean hasError() {
     if (this._sError != null && !this._sError.equals("")) {
       return true;
     }
     return false;
   }

   public String getError() {
     return this._sError;
   }

   protected void setError(String asError) {
     this._sError = asError;
   }

   /**
    * Retrieves the blocks pertaining to this flow.
    * 
    * @return Block vector
    */
   public Vector<Block> getFlow() {
     return this._vFlow;
   }

   public Map<String, Integer> getIndexVars() {
     return this._hmIndexVars;
   }

   public String[] getIndexVarStrings() {
     String[] vars = new String[Const.INDEX_COLUMN_COUNT];
     for (String name : this._hmIndexVars.keySet()) {
       int pos = this._hmIndexVars.get(name);
       vars[pos] = name;
     }
     return vars;
   }
   	public static FlowDataConvert[] toFlowDataConvertArray (FlowData[] fd) {
   		FlowDataConvert[] fdc = new FlowDataConvert [fd.length];
   		for (int i = 0; i < fdc.length; i++) {
   			fdc[i] = fd[i].toFlowDataConvert();
   		}
   		return fdc;
   	}
   	
	public FlowDataConvert toFlowDataConvert() {
		FlowDataConvert fdc = new FlowDataConvert();
		fdc.setBLOCK_PACKAGE(BLOCK_PACKAGE);
		fdc.setiOFFSET(this.iOFFSET);
		
		fdc.setIndexPosition(this.indexPosition); //Probably deletable
		fdc.setFlowType(this.flowType.getCode());
		fdc.setMaxBlockId(this.maxBlockId);
		
		fdc.set_nId(this._nId);
		fdc.set_sName(this._sName);
		fdc.set_sFile(this._sFile);
		//_vFlow has blocks, AXIS has problems with custom type Block, perphaps all blocks types needs mapping in wsdd
		//fdc.set_vFlow(this._vFlow);
		fdc.set_bOnline(this._bOnline);
		fdc.set_bDeployed(this._bDeployed);
		fdc.set_sApplicationId(this._sApplicationId);
		fdc.set_sApplicationName(this._sApplicationName);
		fdc.set_sError(this._sError);
		fdc.set_organizationId(this._organizationId);
		fdc.set_lastModified(this._lastModified);
		fdc.set_created(this._created);
		fdc.set_hmIndexVars(this._hmIndexVars);
		fdc.set_hasDetail(this._hasDetail);
		//_detailForm has blocks, AXIS has problems with custom type Block, perphaps all blocks types needs mapping in wsdd
		//fdc.set_detailForm(this._detailForm);
		fdc.set_seriesId(this._seriesId);
		//fdc.set_catalogue(this._catalogue.toProcessCatalogueConvert());
		fdc.set_hmFormTemplates(this._hmFormTemplates);
		/*if (this._mailStartSettings != null) {
			fdc.set_mailStartSettings(this._mailStartSettings.toMailStartSettingsConvert());
		}*/
		if (this.flowClassFile != null) {
			try {
			fdc.setFlowClassFile(this.flowClassFile.toFlowClassGeneratorConvert());
			} catch (Exception e) {
				Logger.warning(null, this, "toFlowDataConvert", "flowClassFile could not assigned");
			}
		}

		fdc.set_htSubFlowEndPorts(this._htSubFlowEndPorts);
		fdc.set_htForkJoinDepPath(toHtForkJoinDepConvert(this._htForkJoinDepPath));
		fdc.set_hsAllStates(this._hsAllStates);
		fdc.set_htSubFlows(this._htSubFlows);
		return fdc;
	}
   
	 static Hashtable<Integer, ForkJoinDepConvert> toHtForkJoinDepConvert (Hashtable<Integer, ForkJoinDep> htfjd) {
		 Enumeration e = htfjd.keys();
    	 Hashtable<Integer, ForkJoinDepConvert> ht = new Hashtable<Integer, ForkJoinDepConvert>();
    	 while(e.hasMoreElements()) {
    		 Integer iKey = (Integer)e.nextElement();
    		 ForkJoinDepConvert fjdc = htfjd.get(iKey).toForkJoinDepConvert();
    		 ht.put(iKey, fjdc);
    	 }
    	 return ht;
     }
	
   /**
    * 
    * @author ptgm
    * 
    *         ForkJoinDep Fork & Join Dependency Path
    */
   public class ForkJoinDep {

     public static final int FORK = 0;
     public static final int JOIN = 1;

     private Integer _blockId = null;
     private int _type = -1;

     // list of states that are behind this block until next fork/join
     private Set<Integer> _hsStates = new HashSet<Integer>();

     // list of fork/join states that are behind with their own dependencies
     private Hashtable<Integer, ForkJoinDep> _htJFStates = new Hashtable<Integer, ForkJoinDep>();

     public ForkJoinDep(Integer blockId, int type, Set<Integer> hsStates) {
       _blockId = blockId;
       _type = type;
       _hsStates = hsStates;
       _htJFStates = new Hashtable<Integer, ForkJoinDep>();
     }

     public boolean hasDependency(Integer blockId) {
       if (_htJFStates.containsKey(blockId))
         return true;
       Enumeration<ForkJoinDep> enumJF = this.elementsJFStates();
       while (enumJF.hasMoreElements()) {
         ForkJoinDep fjd = enumJF.nextElement();
         if (fjd.hasDependency(blockId))
           return true;
       }
       return false;
     }

     public Integer getBlockId() {
       return _blockId;
     }

     public int getType() {
       return _type;
     }

     public void addStates(Set<Integer> moreStates) {
       _hsStates.addAll(moreStates);
     }

     public Iterator<Integer> iteratorStates() {
       return _hsStates.iterator();
     }

     public void addJFState(ForkJoinDep btp) {
       _htJFStates.put(btp.getBlockId(), btp);
     }

     public ForkJoinDep getJFState(Integer blockId) {
       return (ForkJoinDep) _htJFStates.get(blockId);
     }

     public Enumeration<ForkJoinDep> elementsJFStates() {
       return _htJFStates.elements();
     }

     public String toString() {
       StringBuffer sbtmp = new StringBuffer();

       sbtmp.append("\nBlockID: ").append(_blockId);
       if (_type == FORK)
         sbtmp.append(" FORK");
       else
         sbtmp.append(" JOIN");

       sbtmp.append("\nState List: ");
       Iterator<Integer> it = this.iteratorStates();
       boolean first = true;
       while (it.hasNext()) {
         if (!first)
           sbtmp.append(", ");
         sbtmp.append(it.next());
         first = false;
       }

       sbtmp.append("\nFORK JOIN State List: ");
       Enumeration<ForkJoinDep> enumer = this.elementsJFStates();
       first = true;
       while (enumer.hasMoreElements()) {
         if (!first)
           sbtmp.append(", ");
         ForkJoinDep fjp = enumer.nextElement();
         sbtmp.append(fjp.getBlockId());
         first = false;
       }
       return sbtmp.toString();
     }
   
     public ForkJoinDepConvert toForkJoinDepConvert () {
    	 ForkJoinDepConvert fjdc = new ForkJoinDepConvert();
    	 fjdc.setFORK(FORK);
    	 fjdc.setJOIN(JOIN);
    	 fjdc.set_blockId(_blockId);
    	 fjdc.set_type(_type);
    	 fjdc.set_hsStates(_hsStates);
    	 fjdc.set_htJFStates(toHtForkJoinDepConvert(this._htJFStates));
    	 return fjdc;
     }
   }

   static class ForbiddenBlockException extends SecurityException {

     /**
      *
      */
     private static final long serialVersionUID = -551535203284838499L;
     String msg;

     ForbiddenBlockException(String block) {
       super();
       this.msg = MessageFormat.format(
           "A licença não permite blocos do tipo {0}.",
           new Object[] { block });
       ;
     }

     public String getMessage() {
       return msg;
     }
   }

   public String getOrganizationId() {
     return _organizationId;
   }

   protected void setOrganizationId(String organizationId) {
     _organizationId = organizationId;
   }

   public boolean runMaximized() {

     boolean retValue = false;

     FlowSetting setting = BeanFactory.getFlowSettingsBean().getFlowSetting(
         this.getId(), Const.sRUN_MAXIMIZED);

     if (null != setting && !StringUtils.isEmpty(setting.getValue())
         && setting.getValue().equals(Const.sRUN_MAXIMIZED_YES)) {
       retValue = true;
     }

     return retValue;
   }

   public long getLastModified() {
     return this._lastModified;
   }

   public long getCreated() {
     return this._created;
   }

   public boolean hasDetail() {
     return this._hasDetail;
   }

   public Block getDetailForm() {
     return this._detailForm;
   }

   public int getSeriesId() {
     return this._seriesId;
   }

   public void setSeriesId(int seriesId) {
     this._seriesId = seriesId;
   }

   private static class InstantiationResult {
     private Block start = null;
     private List<Block> end = null;

     private InstantiationResult(Block start, List<Block> end) {
       this.start = start;
       this.end = end;
     }

     private Iterator<Block> getEndIterator() {
       if (null == end)
         return null;
       return end.iterator();
     }
   }

   @SuppressWarnings("unchecked")
   private Class<? extends Block> loadBlockClass(UserInfoInterface ui,
       String className) throws ClassNotFoundException {
     Class<? extends Block> clazz = null;
     try {
       clazz = (Class<? extends Block>) BeanFactory.getRepBean()
       .loadClass(ui, className);
     } catch (ClassNotFoundException e) {
       clazz = (Class<? extends Block>) Class.forName(className);
     }
     return clazz;
   }

   // template loading and retrieving


   private void loadFormTemplates(UserInfoInterface userInfo, XmlFlow aXmlFlow) {
     List<XmlTemplateType> templates = aXmlFlow.getXmlFormTemplate();
     if(null == templates || templates.size() == 0) return;
     Marshaller<Form> marshaller =  Marshaller.create(Form.class);

     for(XmlTemplateType xmlFormTemplate : templates) {
       String templateName = xmlFormTemplate.getName();
       try {
         Form template = marshaller.unmarshall(new JSONObject(xmlFormTemplate.getValue()));
         _hmFormTemplates.put(templateName, template);
       } catch (JSONException e) {
         Logger.warning(userInfo.getUtilizador(), this, "loadFormTemplates", "Could not unmarshall form template: "+templateName, e);
       }
     }

   }

   public Form getFormTemplate(String name) {
     return _hmFormTemplates.get(name);
   }

  public MailStartSettings getMailSettings() {
    return _mailStartSettings;
  }

  public FlowType getFlowType() {
    return flowType;
  }
 
  public void setFlowType(FlowType flowType) {
    this.flowType = flowType;
  }
 
  public void setFlowType(String typeCode) {
    this.flowType = FlowType.getFlowType(typeCode);
  }

  public boolean isVisibleInMenu() {
    boolean retValue = true;
    FlowSetting setting = BeanFactory.getFlowSettingsBean().getFlowSetting(this.getId(), Const.sFLOW_MENU_ACCESSIBLE);

    if (null == setting || StringUtils.isEmpty(setting.getValue()) || setting.getValue().equals(Const.sFLOW_MENU_ACCESSIBLE_YES)) {
      retValue = true;
    } else {
      retValue = false;
    }
    return retValue;
  }

  public boolean hasSchedules() {
    return hasSchedules;
  }

  public void setHasSchedules(boolean hasSchedules) {
    this.hasSchedules = hasSchedules;
  }

  public void setMaxBlockId(int maxBlockId) {
    this.maxBlockId = maxBlockId;
  }

  public int getMaxBlockId() {
    return maxBlockId;
  }
}

class FlowClassGenerator {
  
  private UserInfoInterface userInfo;
  private StringBuilder header;
  private StringBuilder footer;
  private StringBuilder content;
  private String linesep = System.getProperty("line.separator");
  
  public FlowClassGenerator(UserInfoInterface userInfo, int flowid) {    
    if (!canGenerate()) {
      return;
    }

    this.userInfo = userInfo;
    
    header = new StringBuilder();

    header.append("//your package here.. perhaps package flow").append(flowid).append(";").append(linesep).append(linesep);    
    header.append("import pt.iflow.api.processdata.ProcessData;").append(linesep);
    header.append("import pt.iflow.api.utils.UserInfoInterface;").append(linesep).append(linesep);    		
    header.append("//your imports here.. ").append(linesep).append(linesep);
    header.append("public abstract class Flow").append(flowid).append(" {").append(linesep).append(linesep);
    header.append("  protected UserInfoInterface userInfo;").append(linesep);
    header.append("  protected ProcessData procData;").append(linesep);

    content = new StringBuilder();

    footer = new StringBuilder();
    
    
    footer.append(linesep);
    footer.append("  // metodos que podem estar implementados na beanshell").append(linesep);
    footer.append("  public Object eval(String s) {").append(linesep);
    footer.append("    return null;").append(linesep);
    footer.append("  }").append(linesep);
    footer.append(linesep);
    footer.append("  // implementar este método").append(linesep);
    footer.append("  public abstract void execute() throws Exception;").append(linesep);

    footer.append("}").append(linesep);
  }
  
  public void addVar(DataTypeEnum type, String varname) {
    if (!canGenerate()) {
      return;
    }

    ProcessDataType pdt = type.newDataTypeInstance();
    if (pdt == null)
      return;
    String supportingClass = pdt.getSupportingClass().getName();
    if (StringUtils.equals(supportingClass, "java.lang.String"))
      supportingClass = "String";
    
    content.append("  protected ").append(supportingClass).append((type.isList()?"[]":"")).append(" _").append(varname).append(";").append(linesep);
  }
  
  public String getContent() {
    if (!canGenerate()) {
      return null;
    }
    return header.toString() + content.toString() + footer.toString();
  }

  public void writeContent() {
    if (!canGenerate()) {
      return;
    }
    Logger.debug(userInfo.getUtilizador(), this, "writeContent", "Flow beanshell helper class file: " + linesep + linesep + getContent() + linesep);
  }

  private boolean canGenerate() {
    return Const.nMODE == Const.nDEVELOPMENT;
  }
  
  public FlowClassGeneratorConvert toFlowClassGeneratorConvert () throws Exception {
	  FlowClassGeneratorConvert fcgc = new FlowClassGeneratorConvert();
	  fcgc.setHeader(this.header.toString());
	  fcgc.setFooter(this.footer.toString());
	  fcgc.setContent(this.content.toString());
	  fcgc.setLinesep(this.linesep);
	  return fcgc;
  }
}