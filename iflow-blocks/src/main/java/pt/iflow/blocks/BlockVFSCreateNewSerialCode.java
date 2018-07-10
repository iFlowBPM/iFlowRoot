package pt.iflow.blocks;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.CodeTemplateManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;


public class BlockVFSCreateNewSerialCode extends Block {
  public Port portIn, portTrue, portFalse;

  private final String sATTR_SLIST = "template";
  private final String sATTR_TLIST = "serial";
  
  public BlockVFSCreateNewSerialCode(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
    saveFlowState = false;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portTrue;
    retObj[1] = portFalse;
    return retObj;
  }
  
  public Port getEventPort() {
      return null;
  }
  
  public Port[] getInPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[1];
      retObj[0] = portIn;
      return retObj;
  }

  /**
   * No action in this block
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort;

    String login = userInfo.getUtilizador();

    try {
      String sTemplate = null;
      String sSerial = null;
      ProcessSimpleVariable template = null;
      ProcessSimpleVariable serial = null;
      
      sTemplate = this.getAttribute(sATTR_SLIST);
      sSerial = this.getAttribute(sATTR_TLIST);
      
      if (null != sSerial && null != sTemplate && (!StringUtils.isEmpty(sTemplate) && !StringUtils.isEmpty(sSerial))) {
        template = procData.get(sTemplate);
        serial = procData.get(sSerial);
      }
      
      if (null == sSerial && null == sTemplate && StringUtils.isEmpty(sTemplate) || StringUtils.isEmpty(sSerial)) {
        Logger.warning(login, this, "after", procData.getSignature() + "empty attribute!!");
      }
      else {
        try {
          
          CodeTemplateManager ctm = BeanFactory.getCodeTemplateManagerBean();
         if(null != serial && null != template) serial.setValue(ctm.createNewSerialCode(userInfo, template.getRawValue()));
          Logger.debug(login, this, "after", " Generate Serial Code successfully!");
        } catch (Exception ei) {
          Logger.error(login, this, "after", procData.getSignature() + "caught exception, Generate Serial Code", ei);
        }
      }
      
      if (null != serial && StringUtils.isNotBlank(serial.getRawValue())) {
        outPort = portTrue;
      } else {
        outPort = portFalse;
      }
      
    }
    catch (Exception e) {
      Logger.error(login,this,"after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
     
      outPort = portFalse;
    }
    
    this.addToLog("Using '" + outPort.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Marcar Documentos");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Documentos Marcados");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
