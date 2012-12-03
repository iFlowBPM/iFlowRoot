/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.flows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlBlock;
import pt.iflow.api.xml.codegen.flow.XmlBlockType;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVars;
import pt.iflow.api.xml.codegen.flow.XmlFlow;
import pt.iflow.api.xml.codegen.flow.XmlPort;
import pt.iflow.blocks.form.PopupFormField;
import pt.iknow.floweditor.blocks.JSPFieldData;

public class SubFlowDataExpander {

  private static final String SUB_FLOW_TYPE = "BlockSubFlow";
  private static final String BOCK_FORM_TYPE = "BlockFormulario";
  private static final String SUB_FLOW_NAME_ATTRIBUTE_START = "M";
  private static final String BLOCK_END_TYPE = "BlockEnd";
  private static final String BLOCK_COPY_TYPE = "BlockCopia";
  private static final String PORT_OUT_COPY = "portOut";
  final static int FLOW = 0;
  final static int SUBFLOW = 1;
  final static int TYPE = 2;
  
  private XmlFlow xmlFlow;
  private Integer flowId;
  private XmlBlock blockEnd;

  public void setXmlFlow(XmlFlow xmlFlow) {
    this.xmlFlow = xmlFlow;
  }

  public XmlFlow getXmlFlow() {
    return xmlFlow;
  }

  public SubFlowDataExpander(XmlFlow xmlFlow, Integer flowId) {
    super();
    this.xmlFlow = xmlFlow;
    this.flowId = flowId;
  }

  public Integer getFlowId() {
    return flowId;
  }

  public void setFlowId(Integer flowId) {
    this.flowId = flowId;
  }

  public SubFlowDataExpander() {
  }

  public Boolean containsSubFlow() {
    for (XmlBlock block : getXmlFlow().getXmlBlock())
      if (block.getType().equals(SUB_FLOW_TYPE)){
        return true;
      }
    return false;
  }

  private SubFlowDataSuportClass getSubFlowDataFromSubFlowBlock(UserInfoInterface userInfo, XmlBlock subFlowBlock) throws MarshalException, ValidationException {
    SubFlowDataSuportClass subFlowData = null;
    for (XmlAttribute xmlAttribute : subFlowBlock.getXmlAttribute())
      if (SUB_FLOW_NAME_ATTRIBUTE_START.equals("" + xmlAttribute.getName().charAt(0))) {
        byte[] sXml = BeanFactory.getFlowHolderBean().readSubFlowData(userInfo, xmlAttribute.getValue());
        XmlFlow xmlSubFlow = FlowMarshaller.unmarshal(sXml);

        subFlowData = new SubFlowDataSuportClass();
        subFlowData.setPrimaryBlock(subFlowBlock);
        subFlowData.setSubFlowName(xmlAttribute.getValue());
        subFlowData.setSubFlowXML(xmlSubFlow);
        subFlowData.setTypeOfSubFlowImplementation(SubFlowDataSuportClass.SUBFLOW_BLOCK);
      }
    return subFlowData;
  }

  public List<SubFlowMapping> expandSubFlow(UserInfoInterface userInfo) throws Exception {
    List<SubFlowMapping> blockMappings = new ArrayList<SubFlowMapping>();

    while (this.containsSubFlow()) {
      List<SubFlowDataSuportClass> subFlowElements = getAllSubFlowsReferencesFromFlow(userInfo);
      if (subFlowElements != null && subFlowElements.size() > 0) {
        for (SubFlowDataSuportClass subFlowData : subFlowElements) {
          XmlFlow subFlow = subFlowData.getSubFlowXML();

          // if subflow has no content bypass it by converting to copy block
          if (subFlow == null && subFlowData.getSubFlowName() == null) {
            subFlowData.getPrimaryBlock().setType(BLOCK_COPY_TYPE);
            subFlowData.getPrimaryBlock().getXmlPort()[1].setName(PORT_OUT_COPY);
            break;
          }

          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "INI - validate Sub Flow");
          validateSubFlow(subFlow, userInfo);
          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "FIM - validate Sub Flow");

          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "INI - add Sub Flow catalog vars to Main flow");
          addSubCatalogVarsToMain(subFlow);
          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "FIM - add Sub Flow catalog vars to Main flow");

          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "INI - assign new block ids to sub flow blocks");
          blockMappings.addAll(assignNewBlockIds(subFlowData, findMaxblockId()));
          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "FIM - assign new block ids to sub flow blocks");

          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "INI - create copy blocks in sub flow");
          buildCopyBlocksInSubFlow(subFlowData, userInfo);
          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "FIM - create copy blocks in sub flow");

          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow",
              "INI - insert PopupReturnBlock property in form with popup property");
          // Colocar atributo "PopupReturnBlock" em todos os blocos de subFluxo
          if (SubFlowDataSuportClass.POPUP_FORM_BLOCK == subFlowData.getTypeOfSubFlowImplementation()) {
            insertPopupReturnBlockInInSubFlowBocksProperties(subFlowData, userInfo);
          }
          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow",
              "FIM - insert PopupReturnBlock property in form with popup property");

          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "INI - Link main flow with sub flow");
          insertSubInMainFlow(subFlowData, userInfo);
          Logger.debug(userInfo.getUtilizador(), this, "expandSubFlow", "INI - Link main flow with sub flow");
        }
      }
    }

    return blockMappings;
  }

  private void validateSubFlow(XmlFlow xmlSubFlow, UserInfoInterface userInfo) throws Exception {
    Boolean hasBlockEnd = Boolean.FALSE;
    for (XmlBlock block : xmlSubFlow.getXmlBlock()) {
      // if (block.getType().equals(SUB_FLOW_TYPE)) {
      // Logger.error(userInfo.getUtilizador(), this, "buildFlow", "exception caught: " + "SubFlow" + xmlSubFlow.getName()
      // + " contains another inner SubFlow!");
      // throw new Exception("SubFlow"+ xmlSubFlow.getName() +" contains another inner SubFlow!");
      // }
      if (block.getType().equals(BLOCK_END_TYPE))
        hasBlockEnd = Boolean.TRUE;
    }
    if (!hasBlockEnd) {
      Logger.error(userInfo.getUtilizador(), this, "buildFlow", "exception caught: " + "SubFlow" + xmlSubFlow.getName()
          + " must contain a BlockEnd!");
      throw new Exception("SubFlow" + xmlSubFlow.getName() + " must contain a BlockEnd!");
    }
  }

  private void addSubCatalogVarsToMain(XmlFlow xmlSubFlow) {
    XmlCatalogVars mainCatalogVars = getXmlFlow().getXmlCatalogVars();
    for (XmlCatalogVarAttribute xmlCatalogVarAttribute : xmlSubFlow.getXmlCatalogVars().getXmlCatalogVarAttribute()) {
      if (!contains(mainCatalogVars, xmlCatalogVarAttribute)) {
        mainCatalogVars.addXmlCatalogVarAttribute(xmlCatalogVarAttribute);
      }
    }
  }

  private boolean contains(XmlCatalogVars mainCatalogVars, XmlCatalogVarAttribute xmlCatalogVarAttribute) {
    for (XmlCatalogVarAttribute mainCatalogVarAttribute : mainCatalogVars.getXmlCatalogVarAttribute())
      if (mainCatalogVarAttribute.getName().equals(xmlCatalogVarAttribute.getName()))
        return Boolean.TRUE;
    return Boolean.FALSE;
  }

  static String[][] retrieveSubflowVarMappings(XmlBlock subFlowBlock) {
    // get the var mapping of subflow
    int size = (subFlowBlock.getXmlAttributeCount() - 1) / 4 - 1; // raio de conta

    String[][] saInVars = new String[size][3];

    // Attributes do block In & Out
    for (int attrNumber = 0; attrNumber < subFlowBlock.getXmlAttributeCount(); attrNumber++) {
      XmlAttribute xmlAttribute = subFlowBlock.getXmlAttribute(attrNumber);
      String name = xmlAttribute.getName();

      int pos = -1;
      try {
        pos = Integer.parseInt(name.substring(8, name.length()));
      } catch (Exception nfe) {
      }

      if (name.charAt(0) == 'I') {
        if (name.substring(1, 8).equals("bigflow")) {
          saInVars[pos][FLOW] = xmlAttribute.getValue();
        } else {
          saInVars[pos][SUBFLOW] = xmlAttribute.getValue();
        }
      } else if (name.charAt(0) == 'T') {
        saInVars[pos][TYPE] = xmlAttribute.getValue();
      }
    }

    // fix for when last positions of saInVars is null
    ArrayList saInVarsTemp = new ArrayList();
    for (int i = 0; i < saInVars.length; i++)
      if (saInVars[i][FLOW] != null)
        saInVarsTemp.add(saInVars[i]);
    saInVars = (String[][]) saInVarsTemp.toArray(new String[saInVarsTemp.size()][3]);

    return saInVars;
  }

  private void buildCopyBlocksInSubFlow(SubFlowDataSuportClass subFlowData, UserInfoInterface userInfo) throws Exception {
    XmlBlock[] rawSubFlow = subFlowData.getSubFlowXML().getXmlBlock();
    // get the var mapping of subflow

    XmlBlockType subFlowBlock = subFlowData.getPrimaryBlock();
    int size = (subFlowBlock .getXmlAttributeCount() - 1) / 4 - 1; // raio de conta

    if (size < 0){
      size = 0; // FIXME correcção temporaria de subfluxo sem variaveis, 
    }

    String[][] saInVars = new String[size][3];
    String[][] saOutVars = new String[size][3];

    // INI - SUB FLOW ACQUISITION OF VARIABLES TO COPY BLOCKS
    if (SubFlowDataSuportClass.SUBFLOW_BLOCK == subFlowData.getTypeOfSubFlowImplementation()){
      // Attributes do block In & Out
      for (int attrNumber = 0; attrNumber < subFlowBlock.getXmlAttributeCount(); attrNumber++) {
        XmlAttribute xmlAttribute = subFlowBlock.getXmlAttribute(attrNumber);
        String name = xmlAttribute.getName();

        int pos = -1;
        try {
          pos = Integer.parseInt(name.substring(8));
        } catch (Exception nfe) {
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
        } else if (name.charAt(0) == 'T') {
          saInVars[pos][TYPE] = xmlAttribute.getValue();
          saOutVars[pos][TYPE] = xmlAttribute.getValue();
        }
      }
    } else if (SubFlowDataSuportClass.POPUP_FORM_BLOCK == subFlowData.getTypeOfSubFlowImplementation()){
      saInVars = processMappingOfPopupVariables(subFlowData.getPrimaryBlock().getXmlAttribute());
    }

    // fix for when last positions of saInVars is null
    ArrayList saInVarsTemp = new ArrayList();
    for (int i = 0; i < saInVars.length; i++)
      if (saInVars[i][FLOW] != null)
        saInVarsTemp.add(saInVars[i]);
    saInVars = (String[][]) saInVarsTemp.toArray(new String[saInVarsTemp.size()][3]);

    //Validate that variables with the same name are mapped one to the other
    for(int in=0; in<saInVars.length; in++){
      for(int out=0; out<saInVars.length; out++){
        if (saInVars[in][FLOW].equals(saInVars[out][SUBFLOW]) && in != out) {
          Logger.error(userInfo.getUtilizador(), this, "buildFlow", "exception caught: In MainFlow - " + this.xmlFlow.getName()
              + ", SubFlow - " + subFlowData.getSubFlowName()
              + " variables with the same name must be mapped one to the other, varM - " + saInVars[in][FLOW] + " varS - "
              + saInVars[in][SUBFLOW]);
          throw new Exception("Variables with the same name must be mapped one to the other!");
        }
      }
    }

    // FIM - SUB FLOW ACQUISITION OF VARIABLES TO COPY BLOCKS 

    // setting ports on start block
    rawSubFlow[0] = processSubFlowStartBlock(subFlowData, userInfo);

    if (SubFlowDataSuportClass.POPUP_FORM_BLOCK == subFlowData.getTypeOfSubFlowImplementation()){
      subFlowData.updatePopUpStartBlockId(rawSubFlow[0], userInfo);
    }

    // setting ports on end block
    blockEnd = processSubFlowEndBlock (subFlowData, userInfo);

    // adding copy variables to xml attributtes
    List<XmlAttribute> startCopyAttr = new ArrayList<XmlAttribute>();
    List<XmlAttribute> endCopyAttr = new ArrayList<XmlAttribute>();
    for (int i = 0; i < saInVars.length; i++) {
      XmlAttribute startOrig = new XmlAttribute();
      startOrig.setName("orig" + i);
      startOrig.setDescription("orig" + i);
      startOrig.setValue(saInVars[i][FLOW]);
      XmlAttribute startDest = new XmlAttribute();
      startDest.setName("dest" + i);
      startDest.setDescription("dest" + i);
      startDest.setValue(saInVars[i][SUBFLOW]);
      startCopyAttr.add(startOrig);
      startCopyAttr.add(startDest);

      XmlAttribute endOrig = new XmlAttribute();
      endOrig.setName("orig" + i);
      endOrig.setDescription("orig" + i);
      endOrig.setValue(saInVars[i][SUBFLOW]);
      XmlAttribute endDest = new XmlAttribute();
      endDest.setName("dest" + i);
      endDest.setDescription("dest" + i);
      endDest.setValue(saInVars[i][FLOW]);
      endCopyAttr.add(endOrig);
      endCopyAttr.add(endDest);
    }
    rawSubFlow[0].setXmlAttribute(startCopyAttr.toArray(new XmlAttribute[startCopyAttr.size()]));
    blockEnd.setXmlAttribute(endCopyAttr.toArray(new XmlAttribute[endCopyAttr.size()]));
  }

  /**
   * Inserts subflow in main by adding subflow's expanded block array to main block array <br>
   * and remaking the connections of the ports <br>
   * <br>
   * 
   * - Assumes that first block in any block array is the startBlock <br>
   * - Assumes that first port of a block points to the block where it came from
   * 
   * @param subFlowData
   * @param userInfo 
   */
  private void insertSubInMainFlow(SubFlowDataSuportClass subFlowData, UserInfoInterface userInfo) {
    XmlBlock subFlowBlock = subFlowData.getPrimaryBlock();
    XmlBlock[] rawSubFlow = subFlowData.getSubFlowXML().getXmlBlock();

    if (SubFlowDataSuportClass.SUBFLOW_BLOCK == subFlowData.getTypeOfSubFlowImplementation()){
      // 1.Connect beginning of subflow
      for (XmlBlock block : xmlFlow.getXmlBlock()){
        if(block.getId()==subFlowBlock.getXmlPort(0).getConnectedBlockId()){ // é bloco que liga ao subFluxo
          for (XmlPort port : block.getXmlPort())
            if (port.getConnectedBlockId() == subFlowBlock.getId())
              port.setConnectedBlockId(rawSubFlow[0].getId());
        }
      }
    }

    if (SubFlowDataSuportClass.SUBFLOW_BLOCK == subFlowData.getTypeOfSubFlowImplementation()){
      // 3.Add blocks to main flow ("Link's" the last block of sub flow to the block conected to the subflowBlock from main flow)
      for (XmlBlock block : xmlFlow.getXmlBlock()){
        if(block.getId()==subFlowBlock.getXmlPort(1).getConnectedBlockId()){
          block.getXmlPort(0).setConnectedBlockId(blockEnd.getId());
          block.getXmlPort(0).setConnectedPortName("portOut");
        }
      }
    }

    ArrayList<XmlBlock> newMainFlow = new ArrayList<XmlBlock>();

    // ADDs MAIN FLOW
    for (XmlBlock block : xmlFlow.getXmlBlock()){
      if(block.getId()!=subFlowBlock.getId() 
          || SubFlowDataSuportClass.POPUP_FORM_BLOCK == subFlowData.getTypeOfSubFlowImplementation()){
        newMainFlow.add(block);
      }
    }

    // ADDs SUB FLOW
    for (XmlBlock block : rawSubFlow){
      newMainFlow.add(block);
    }

    //REPLACES xmlFlow WITH THE MERGE OF PRIMARY FLOW AND SUB FLOW
    xmlFlow.setXmlBlock(newMainFlow.toArray(new XmlBlock[newMainFlow.size()]));
  }

  private String[][] processMappingOfPopupVariables(XmlAttribute[] xmlAttributes) {
    HashMap<String, XmlAttribute> masterFlowVariables = new HashMap<String, XmlAttribute>();
    HashMap<String, XmlAttribute> popupFlowVariables = new HashMap<String, XmlAttribute>();

    for (XmlAttribute xmlAttribute : xmlAttributes){
      String attributeName = xmlAttribute.getName();
      if (attributeName.startsWith("OBJ_") && attributeName.contains("_ROW_")){
        int variableLineNumberInitialIdx = attributeName.indexOf("_ROW_") + "_ROW_".length();
        String variableLineNumber = attributeName.substring(variableLineNumberInitialIdx, attributeName.indexOf("_", variableLineNumberInitialIdx));

        if (attributeName.endsWith(JSPFieldData.MASTER_FLOW_VARIABLES)){
          masterFlowVariables.put(variableLineNumber, xmlAttribute);
        } else if (attributeName.endsWith(JSPFieldData.POPUP_VARIABLES)){
          popupFlowVariables.put(variableLineNumber, xmlAttribute);
        }
      }
    }
    int numberOfMappedVariables = masterFlowVariables.size();

    String[][] masterFlowVariablesArray = new String [numberOfMappedVariables][3];

    Set<String> mappedVariableKeys = masterFlowVariables.keySet();

    Iterator<String> iterParams = mappedVariableKeys.iterator();
    int mappedVariablesIdx = 0;
    while (iterParams.hasNext()) {
      String key = iterParams.next();

      masterFlowVariablesArray[mappedVariablesIdx][FLOW] = masterFlowVariables.get(key).getValue();

      String popupFlowVar = "";
      XmlAttribute popupFlowVariable = popupFlowVariables.get(key);
      if (popupFlowVariable != null){
        popupFlowVar = popupFlowVariable.getValue();
      }
      masterFlowVariablesArray[mappedVariablesIdx][SUBFLOW] = popupFlowVar;
      mappedVariablesIdx++;
    }
    return masterFlowVariablesArray;
  }

  private XmlBlock processSubFlowStartBlock(SubFlowDataSuportClass subFlowData, UserInfoInterface userInfo) {
    XmlBlock[] rawSubFlow = subFlowData.getSubFlowXML().getXmlBlock();
    XmlBlockType subFlowBlock = subFlowData.getPrimaryBlock();

    XmlBlock subFlowBlockStart = rawSubFlow[0];

    subFlowBlockStart.setType(BLOCK_COPY_TYPE);
    XmlPort[] startPorts = new XmlPort[2];

    if (SubFlowDataSuportClass.POPUP_FORM_BLOCK != subFlowData.getTypeOfSubFlowImplementation()) {
      if (subFlowBlock.getXmlPort().length == 2){
      startPorts[0] = subFlowBlock.getXmlPort(0);
      startPorts[1] = rawSubFlow[0].getXmlPort(0);
      } else {
        startPorts[0] = null;
        startPorts[1] = rawSubFlow[0].getXmlPort(0);
      }
    } else {
      startPorts = new XmlPort[1]; // POPUP só terá uma saida.
      startPorts[0] = rawSubFlow[0].getXmlPort(0); // se for do tipo form block nao tem entrada
    }

    subFlowBlockStart.setXmlPort(startPorts);
    subFlowBlockStart.setXmlAttribute(new XmlAttribute[0]);

    return subFlowBlockStart;
  }

  private XmlBlock processSubFlowEndBlock(SubFlowDataSuportClass subFlowData, UserInfoInterface userInfo) {
    XmlBlock[] rawSubFlow = subFlowData.getSubFlowXML().getXmlBlock();
    XmlBlockType subFlowBlock = subFlowData.getPrimaryBlock();

    XmlBlock subFlowBlockEnd = null;
    ArrayList<Integer> endId = new ArrayList<Integer>();

    for (XmlBlock block : rawSubFlow){
      if (block.getType().equals(BLOCK_END_TYPE)){
        subFlowBlockEnd = block;
        endId.add(block.getId());
      }
    }

    subFlowBlockEnd.setType(BLOCK_COPY_TYPE);
    XmlPort[] endPorts = new XmlPort[2];
    endPorts[0] = subFlowBlockEnd.getXmlPort(0);

    XmlPort outPort = new XmlPort();

    if (SubFlowDataSuportClass.POPUP_FORM_BLOCK == subFlowData.getTypeOfSubFlowImplementation()){
      outPort.setConnectedBlockId(subFlowData.getPrimaryBlock().getId()); //Target's main flow form block
      outPort.setName("portOut");
    } else if (SubFlowDataSuportClass.SUBFLOW_BLOCK == subFlowData.getTypeOfSubFlowImplementation()){
      XmlPort primarySubFlowBlockExitPort = subFlowBlock.getXmlPort(1);
      outPort.setName("portOut");
      outPort.setConnectedBlockId(primarySubFlowBlockExitPort.getConnectedBlockId());
      outPort.setConnectedPortName(primarySubFlowBlockExitPort.getConnectedPortName());
    }

    // merges all possible end blocks into one
    for (XmlBlock block : rawSubFlow)
      for (XmlPort port : block.getXmlPort())
        if (endId.contains(port.getConnectedBlockId())) {
          port.setConnectedBlockId(subFlowBlockEnd.getId());
          port.setConnectedPortName("portIn");
        }

    endPorts[1] = outPort;
    subFlowBlockEnd.setXmlPort(endPorts);
    subFlowBlockEnd.setXmlAttribute(new XmlAttribute[0]);

    return subFlowBlockEnd;
  }

  /**
   * Method which runs the blocks of popup flow, including a property with the next block of the main flow
   * 
   * @param subFlowData
   * @param userInfo
   */
  private void insertPopupReturnBlockInInSubFlowBocksProperties(SubFlowDataSuportClass subFlowData, UserInfoInterface userInfo) {
    XmlBlock[] subFlowBlocks = subFlowData.getSubFlowXML().getXmlBlock();

    for (XmlBlock block :subFlowBlocks){
      block.setPopupReturnBlockId(new Integer(subFlowData.getPrimaryBlock().getId()));

      XmlAttribute xmlAttribute = new XmlAttribute();
      xmlAttribute.setName("popupReturnBlockId");
      xmlAttribute.setDescription("popupReturnBlockId");
      xmlAttribute.setValue(String.valueOf(subFlowData.getPrimaryBlock().getId()));
      block.addXmlAttribute(xmlAttribute);
    }
  }

  private List<SubFlowDataSuportClass> getAllSubFlowsReferencesFromFlow(UserInfoInterface userInfo) throws MarshalException, ValidationException {
    String operationUser = userInfo.getUtilizador();
    String methodName = "getFlowSubFlows";

    Logger.debug(operationUser, this, methodName, "INI - Processing search of subFlows.");

    List<SubFlowDataSuportClass> listOfSubFlows = new ArrayList<SubFlowDataSuportClass>();
    for (XmlBlock block : getXmlFlow().getXmlBlock()){
      SubFlowDataSuportClass subFlowData = null;
      if (SUB_FLOW_TYPE.equals(block.getType())){
        Logger.debug(operationUser, this, methodName, "INI - Processing block of SubFlow.");
        subFlowData = getSubFlowDataFromSubFlowBlock(userInfo, block);
        if (subFlowData == null){
          subFlowData = new SubFlowDataSuportClass(SubFlowDataSuportClass.SUBFLOW_BLOCK, null, block, null, null, -1);
          Logger.error(operationUser, this, methodName, "ERROR - while processing sub flow data from SubFlowBlock");
        } else {
          Logger.debug(operationUser, this, methodName, "Info - Flow: "+getXmlFlow().getName()+" will use subFlow "+subFlowData.getSubFlowName());
        }
        // missing ports..
        if (block.getXmlPort().length == 0) {
          XmlPort portIn = new XmlPort();
          XmlPort portSuccess = new XmlPort();
          portSuccess.setName("portSuccess");
          portIn.setName("portIn");
          XmlPort[] ports = { portIn, portSuccess };
          block.setXmlPort(ports);
        } else if (block.getXmlPort().length == 1) {
          XmlPort blankPort = new XmlPort();
          if (block.getXmlPort(0).getName().equals("portIn")) {
            blankPort.setName("portSuccess");
            block.addXmlPort(1, blankPort);
          } else {
            blankPort.setName("portIn");
            block.addXmlPort(0, blankPort);
          }
        }
        Logger.debug(operationUser, this, methodName, "FIM - Processing block of SubFlow.");
      }

      List<SubFlowDataSuportClass> listOfFormSubFlows = null;
      if (BOCK_FORM_TYPE.equals(block.getType())){
        Logger.debug(operationUser, this, methodName, "INI - Processing Form block.");
        listOfFormSubFlows = getSubFlowDataFromFormBlock(userInfo, block);
        Logger.debug(operationUser, this, methodName, "FIM - Processing Form block.");
      }

      if (subFlowData != null){
        listOfSubFlows.add(subFlowData);
      }

      if (listOfFormSubFlows != null && listOfFormSubFlows.size() > 0){
        listOfSubFlows.addAll(listOfFormSubFlows);
      }
    }

    Logger.debug(userInfo.getUtilizador(), this, "getFlowSubFlows", "FIM - Processing search of subFlows.");
    return listOfSubFlows;
  }

  private List<SubFlowDataSuportClass> getSubFlowDataFromFormBlock(UserInfoInterface userInfo, XmlBlock formBlock) throws MarshalException, ValidationException {
    String operationUser = userInfo.getUtilizador();
    String methodName = "getFlowSubFlows";
    Logger.debug(operationUser, this, methodName, "INI - Processing Form block xml attribute.");

    List<SubFlowDataSuportClass> subFlowReferencesInForm = new ArrayList<SubFlowDataSuportClass>();
    List<List<XmlAttribute>> listOfPopupFormfields = getPopupFormFields(userInfo, formBlock);
    if (listOfPopupFormfields != null){
      for(int i=0; i<listOfPopupFormfields.size(); i++){
        SubFlowDataSuportClass popupFlowData = new SubFlowDataSuportClass();

        for (XmlAttribute fieldAtribute : listOfPopupFormfields.get(i)) {
          String fieldAttributeName = fieldAtribute.getName();
          if (fieldAttributeName.startsWith("OBJ_") && fieldAttributeName.endsWith("_ID")){
            String popupFieldId = fieldAttributeName.substring(4, fieldAttributeName.indexOf("_ID"));
            popupFlowData.setFormFieldId(Integer.parseInt(popupFieldId));
          }

          if (fieldAttributeName.startsWith("OBJ_") && fieldAttributeName.endsWith("_suport_flow")){
            popupFlowData.setSubFlowName(fieldAtribute.getValue());
          }

          if (fieldAttributeName.startsWith("OBJ_") && fieldAttributeName.endsWith("_variable")){
            popupFlowData.setPopupVariable(fieldAtribute.getValue());
          }
        }

        byte[] sXml = BeanFactory.getFlowHolderBean().readSubFlowData(userInfo, popupFlowData.getSubFlowName());
        XmlFlow xmlSubFlow = FlowMarshaller.unmarshal(sXml);

        popupFlowData.setPrimaryBlock(formBlock);
        popupFlowData.setSubFlowXML(xmlSubFlow);
        popupFlowData.setTypeOfSubFlowImplementation(SubFlowDataSuportClass.POPUP_FORM_BLOCK);

        subFlowReferencesInForm.add(popupFlowData);
      }
    }

    Logger.debug(operationUser, this, methodName, "FIM - Processing Form block xml attribute.");
    return subFlowReferencesInForm;
  }

  private List <List<XmlAttribute>> getPopupFormFields(UserInfoInterface userInfo, XmlBlock block) {
    List<String> popupFormFieldsPositions = new ArrayList<String>();
    HashMap <String,List<XmlAttribute>> formFieldsList = new HashMap <String,List<XmlAttribute>>();

    List <XmlAttribute> formField = null;
    String currentFieldId = "0";
    for (XmlAttribute formAttribute : block.getXmlAttribute()) {
      String attributteName = formAttribute.getName();

      if (attributteName.startsWith("OBJ_")){
        if (attributteName.endsWith("_ID")){
          if (formField != null){ //existe campo anterior
            formFieldsList.put(currentFieldId, formField);
          }
          formField = new ArrayList <XmlAttribute>();
          currentFieldId = String.valueOf(formAttribute.getValue());
        }

        if (attributteName.endsWith("_type")){
          if ("pt.iflow.blocks.form.PopupFormField".equals(formAttribute.getValue())){
            Logger.info(userInfo.getUtilizador(), this, "getPopupFormFields", "field ["+currentFieldId+"], is a popup");
            popupFormFieldsPositions.add(currentFieldId);
          }
        }

        formField.add(formAttribute);
      }
    }
    if (formField != null){
      formFieldsList.put(currentFieldId, formField);; // inclui o ultimo campo do formulario
    }

    List <List<XmlAttribute>> subFlowsFormFieldsList = null;
    if (popupFormFieldsPositions.size() > 0) {
      subFlowsFormFieldsList = new ArrayList <List<XmlAttribute>>();

      Logger.info(userInfo.getUtilizador(), this, "getPopupFormFields", "Processing fields ["+popupFormFieldsPositions.toString()+"], as popup fields");
      for (String popupFormField : popupFormFieldsPositions){
        subFlowsFormFieldsList.add(formFieldsList.get(popupFormField));
      }
    }
    return subFlowsFormFieldsList;
  }

  /**
   * Recalculates block ids by adding the same value to all<br>
   * <br>
   * 
   * @param rawSubFlow
   *          the array of block where the ids will be updated
   * @param mainFlowMaxBlockId
   *          the value to add to all block ids
   */
  private List<SubFlowMapping> assignNewBlockIds(SubFlowDataSuportClass subflow, Integer mainFlowMaxBlockId) {
    List<SubFlowMapping> mappings = new ArrayList<SubFlowMapping>();

    for (XmlBlock block : subflow.getSubFlowXML().getXmlBlock()) {
      mappings.add(new SubFlowMapping(xmlFlow.getName(), subflow.getSubFlowName() + "_" + subflow.getPrimaryBlock().getId(), block
          .getId(), block.getId() + mainFlowMaxBlockId));
      block.setId(block.getId() + mainFlowMaxBlockId);
      for (XmlPort xmlPort : block.getXmlPort())
        xmlPort.setConnectedBlockId(xmlPort.getConnectedBlockId() + mainFlowMaxBlockId);
    }
    return mappings;
  }

  /**
   * Returns the content of a subFlow from the database<br>
   * <br>
   * 
   * @param subFlowBlock
   *          the subflow block inserted in main flow<br>
   * @param userInfo
   *          user information required for database querys
   */
  private XmlFlow retrieveRawSubFlow(XmlBlock subFlowBlock, UserInfoInterface userInfo) throws MarshalException,
      ValidationException {
    for (XmlAttribute xmlAttribute : subFlowBlock.getXmlAttribute())
      if (SUB_FLOW_NAME_ATTRIBUTE_START.equals("" + xmlAttribute.getName().charAt(0))) {
        byte[] sXml = BeanFactory.getFlowHolderBean().readSubFlowData(userInfo, xmlAttribute.getValue());
        XmlFlow xmlSubFlow = FlowMarshaller.unmarshal(sXml);
        return xmlSubFlow;
      }
    return new XmlFlow();
  }

  /**
   * Returns the biggest block id present in the blocks
   */
  public Integer findMaxblockId() {
    Integer result = 0;
    for (XmlBlock block : xmlFlow.getXmlBlock())
      if (block.getId() > result)
        result = block.getId();

    return result;
  }
}

class SubFlowDataSuportClass {
  public final static int SUBFLOW_BLOCK = 0;
  public final static int POPUP_FORM_BLOCK = 1;

  private int typeOfSubFlowImplementation;
  private String subFlowName = "";
  private XmlBlock primaryBlock;
  private XmlFlow subFlowXML;
  private String popupVariable;
  private int formFieldId = -1;

  public int getTypeOfSubFlowImplementation() {
    return typeOfSubFlowImplementation;
  }

  public void setTypeOfSubFlowImplementation(int typeOfSubFlowImplementation) {
    this.typeOfSubFlowImplementation = typeOfSubFlowImplementation;
  }

  public String getSubFlowName() {
    return subFlowName;
  }

  public void setSubFlowName(String subFlowName) {
    this.subFlowName = subFlowName;
  }

  public XmlBlock getPrimaryBlock() {
    return primaryBlock;
  }

  public void setPrimaryBlock(XmlBlock subFlowBlock) {
    this.primaryBlock = subFlowBlock;
  }

  public XmlFlow getSubFlowXML() {
    return subFlowXML;
  }

  public SubFlowDataSuportClass(int typeOfSubFlowImplementation, String subFlowName, XmlBlock primaryBlock, XmlFlow subFlowXML,
      String popupVariable, int formFieldId) {
    super();
    this.typeOfSubFlowImplementation = typeOfSubFlowImplementation;
    this.subFlowName = subFlowName;
    this.primaryBlock = primaryBlock;
    this.subFlowXML = subFlowXML;
    this.popupVariable = popupVariable;
    this.formFieldId = formFieldId;
  }

  public SubFlowDataSuportClass() {
    // TODO Auto-generated constructor stub
  }

  public void setSubFlowXML(XmlFlow subFlowXML) {
    this.subFlowXML = subFlowXML;
  }

  public void setPopupVariable(String popupVariable) {
    this.popupVariable = popupVariable;
  }

  public String getPopupVariable() {
    return popupVariable;
  }

  public int getFormFieldId() {
    return formFieldId;
  }

  public void setFormFieldId(int formFieldId) {
    this.formFieldId = formFieldId;
  }

  public void updatePopUpStartBlockId(XmlBlock popupFlowStartupBlock, UserInfoInterface userInfo) {
    XmlAttribute popupFlowStartBlockIdXMLAttribute = new XmlAttribute();

    StringBuffer attributeName = new StringBuffer("OBJ_").append(formFieldId).append("_").append(PopupFormField.POPUP_FLOW_START_BLOCK_ID);
    popupFlowStartBlockIdXMLAttribute.setName(attributeName.toString());
    popupFlowStartBlockIdXMLAttribute.setDescription(PopupFormField.POPUP_FLOW_START_BLOCK_ID);
    popupFlowStartBlockIdXMLAttribute.setValue(String.valueOf(popupFlowStartupBlock.getId()));

    primaryBlock.addXmlAttribute(popupFlowStartBlockIdXMLAttribute);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SubFlowDataSuportClass [");
    if (popupVariable != null){
      builder.append("popupVariable={").append(popupVariable).append("}, ");
    }
    if (subFlowName != null){
      builder.append("subFlowName=").append(subFlowName).append(", ");
    }
    if (primaryBlock != null){
      builder.append("primaryBlock={");
      builder.append("id=").append(String.valueOf(primaryBlock.getId())).append(", ");
      builder.append("tipo=").append(String.valueOf(primaryBlock.getType()));
      builder.append("}, ");
    }
    if (SUBFLOW_BLOCK == typeOfSubFlowImplementation){
      builder.append("typeOfSubFlowImplementation=").append("SUBFLOW_BLOCK").append("]");
    } else if (POPUP_FORM_BLOCK== typeOfSubFlowImplementation){
      builder.append("typeOfSubFlowImplementation=").append("POPUP - FORM BLOCK").append("]");
    }
    return builder.toString();
  }
}
