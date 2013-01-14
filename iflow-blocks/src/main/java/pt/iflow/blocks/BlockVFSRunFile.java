package pt.iflow.blocks;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.CodeTemplateManager;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.documents.CodeTemplate;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;

public class BlockVFSRunFile extends Block {
  public Port portIn, portTrue, portFalse;

  private final String sATTR_FILE = "File";

  public BlockVFSRunFile(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
    saveFlowState = false;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portTrue;
    retObj[1] = portFalse;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  /**
   * No action in this block
   * 
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
    Port outPort = portTrue;
    Boolean a = Pattern.matches("[0-9]{1}?[0-9]{1}?[0-9]{1}?[0-9]{1}?[0-9]{1}?[0-9]{1}?", "123456");

    String sFileVar = this.getAttribute(sATTR_FILE);
    ProcessListVariable variable = procData.getList(sFileVar);
    Documents docBean = BeanFactory.getDocumentsBean();
    CodeTemplateManager ctm = BeanFactory.getCodeTemplateManagerBean();
    List<CodeTemplate> codeTemplateList = ctm.listCodeTemplates(userInfo, procData);

    if (variable == null) {
      Logger.error(userInfo.getUtilizador(), this, "after", "Unable to process data from file: unknown variable (found: variable="
          + sFileVar + ")");
      return portFalse;
    }

    for (ProcessListItem item : variable.getItems()) {
      Long docId = (Long) item.getValue();
      Document doc = docBean.getDocument(userInfo, procData, docId.intValue());
      int fid;
      String[] serialList = { "123456" };// doc.getSerials().split("-");
      try {
        for (int i = 0; i < serialList.length; i++)
          for (CodeTemplate codeTemplate : codeTemplateList) {
            String pattern = codeTemplate.getTemplate();
            pattern = StringUtils.replace(pattern, "X", "[a-zA-Z]{1}?");
            pattern = StringUtils.replace(pattern, "x", "[a-zA-Z]{1}?");
            pattern = StringUtils.replace(pattern, "0", "[0-9]{1}?");
            String code = serialList[i];

            if (Pattern.matches(pattern, code))
              launchProcess(codeTemplate.getCallback(), sFileVar, userInfo, procData);
          }
      } catch (Exception e) {
        return portFalse;
      }
    }
    return outPort;
  }

  private void launchProcess(String callback, String sFileVar, UserInfoInterface userInfo, ProcessData procData) throws Exception,
      Exception {

    UserInfoInterface newUserInfo = BeanFactory.getUserInfoFactory().newUserInfoDelegate(this, userInfo.getUtilizador());
    ProcessManager pm = BeanFactory.getProcessManagerBean();
    Flow flow = BeanFactory.getFlowBean();
    ProcessData newProcData = pm.createProcess(newUserInfo, Integer.parseInt(callback));
    this.saveDataSet(newUserInfo, newProcData);
    flow.nextBlock(newUserInfo, newProcData);
    Block block = flow.getBlock(newUserInfo, newProcData);

    int newPid = newProcData.getPid();
    int newSubPid = newProcData.getSubPid();

    if (procData.isListVar(sFileVar)) {
      newProcData.setList(procData.getList(sFileVar));
    } else {
      newProcData.set(procData.get(sFileVar));
    }

    this.saveDataSet(newUserInfo, newProcData);

    Logger.info(userInfo.getUtilizador(), this, "after", procData.getSignature() + "created process " + newProcData.getSignature()
        + " stopped execution.");

    // now schedule process in user

    String url = Block.getDefaultUrl(newUserInfo, newProcData);

    Activity activity = new Activity(newUserInfo.getUtilizador(), Integer.parseInt(callback), newPid, newSubPid, 0, 0, block
        .getDescription(newUserInfo, newProcData), url);
    activity.mid = newProcData.getMid();
    pm.updateActivity(newUserInfo, activity);
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Marcar Documentos");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Documentos Marcados");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
