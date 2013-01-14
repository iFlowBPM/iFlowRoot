package pt.iflow.blocks;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.CodeTemplateManager;
import pt.iflow.api.documents.CodeTemplate;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.StringUtilities;

/**
 * <p>
 * Title: BlockAddCodeTemplate
 * </p>
 * <p>
 * Description: This block is used to get a list of code templates in the internal template table
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Infosistema
 * </p>
 * 
 * @author
 */

public class BlockListCodeTemplate extends Block {
  public Port portIn, portSuccess, portError;

  private static final String TEMPLATE = "Template";
  private static final String NAME = "Name";
  private static final String DESCRIPTION = "Description";
  private static final String CALLBACK = "Callback";
  private static final String METATAG = "Metatag";

  public BlockListCodeTemplate(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();

    try {
      ProcessListVariable sTemplateVar = procData.getList(this.getAttribute(TEMPLATE));
      ProcessListVariable sNameVar = procData.getList(this.getAttribute(NAME));
      ProcessListVariable sDescVar = procData.getList(this.getAttribute(DESCRIPTION));
      ProcessListVariable sCallbackVar = procData.getList(this.getAttribute(CALLBACK));
      ProcessListVariable sMetatagVar = procData.getList(this.getAttribute(METATAG));

      sTemplateVar.clear();
      sNameVar.clear();
      sDescVar.clear();
      sCallbackVar.clear();
      sMetatagVar.clear();

      CodeTemplateManager ap = BeanFactory.getCodeTemplateManagerBean();
      List<CodeTemplate> codeTempList = ap.listCodeTemplates(userInfo, procData);

      for (int i = 0; i < codeTempList.size(); i++) {
        sTemplateVar.setItemValue(i, codeTempList.get(i).getTemplate());
        sNameVar.setItemValue(i, codeTempList.get(i).getName());
        sDescVar.setItemValue(i, codeTempList.get(i).getDescription());
        sCallbackVar.setItemValue(i, codeTempList.get(i).getCallback());
        sMetatagVar.setItemValue(i, codeTempList.get(i).getFlag());
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portError;
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  private Hashtable<String, String> getFields() {
    Hashtable<String, String> fields = new Hashtable<String, String>();
    Iterator<String> itera = this.getAttributeMap().keySet().iterator();
    while (itera.hasNext()) {
      final String sAttrName = itera.next();

      if (StringUtilities.isEmpty(sAttrName) || sAttrName.equals(TEMPLATE) || sAttrName.equals(NAME)
          || sAttrName.equals(DESCRIPTION) || sAttrName.equals(CALLBACK) || sAttrName.equals(sDESCRIPTION)
          || sAttrName.equals(sRESULT))
        continue;

      String sAttrValue = this.getAttribute(sAttrName);

      if (StringUtilities.isEmpty(sAttrValue))
        continue;
      else
        sAttrValue = sAttrValue.trim();

      fields.put(sAttrName, sAttrValue);
    }

    return fields;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData process) {
    return this.getDesc(userInfo, process, true, "Add Code Template");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Added Code Template");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
