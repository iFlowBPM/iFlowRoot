package pt.iflow.blocks;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.CodeTemplateManager;
import pt.iflow.api.documents.CodeTemplate;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockVFSIdentifyCodes extends Block {
  public Port portIn, portTrue, portFalse;

  private final String sATTR_SERIAL = "serialList";
  private final String sATTR_TEMPLATELIST = "templateList";

  public BlockVFSIdentifyCodes(int anFlowId, int id, int subflowblockid, String filename) {
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

    String login = userInfo.getUtilizador();

    try {
      String serials = this.getAttribute(sATTR_SERIAL);
      serials = procData.transform(userInfo, serials);
      ProcessListVariable templateList = procData.getList(this.getAttribute(sATTR_TEMPLATELIST));

      if (StringUtils.isBlank(serials)) {
        Logger.error(login, this, "after", procData.getSignature() + "No serials in input - ");
        outPort = portFalse;
      } else {
        CodeTemplateManager ctm = BeanFactory.getCodeTemplateManagerBean();
        List<CodeTemplate> codeTemplateList = ctm.listCodeTemplates(userInfo, procData);
        String[] serialList = serials.split("-");

        for (int i = 0; i < serialList.length; i++)
          for (CodeTemplate codeTemplate : codeTemplateList) {
            String pattern = codeTemplate.getTemplate();
            pattern = StringUtils.replace(pattern, "X", "[a-zA-Z]{1}?");
            pattern = StringUtils.replace(pattern, "x", "[a-zA-Z]{1}?");
            pattern = StringUtils.replace(pattern, "0", "\\d{1}?");
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(serialList[i]);

            if (m.matches()) {
              templateList.setItemValue(i, codeTemplate.getName());
              break;
            }
            else
              templateList.setItemValue(i, "");
          }
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portFalse;
    }

    this.addToLog("Using '" + outPort.getName() + "';");
    this.saveLogs(userInfo, procData, this);

    return outPort;
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
