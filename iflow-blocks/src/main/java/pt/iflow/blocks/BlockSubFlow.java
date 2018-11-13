package pt.iflow.blocks;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.forkjoin.ForkManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVars;
import pt.iflow.api.xml.codegen.flow.XmlFlow;

@Deprecated
public class BlockSubFlow extends Block {

  public Port portIn, portOut, portOutThread, portError;

  private final String sPREFIX = "Type_";

  public BlockSubFlow(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    bProcInDBRequired = true;
    saveFlowState = false;
    canRunInPopupBlock = false;
    throw new RuntimeException("Not migrated.");
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[3];
    retObj[0] = portOut;
    retObj[1] = portOutThread;
    retObj[2] = portError;
    return retObj;
  }

  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public Port after(UserInfoInterface userInfo, ProcessData procData) {
	  return portOut;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Entrada SubFluxo " + this.getSubFlowFilename());
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Entrada SubFluxo " + this.getSubFlowFilename() + " Conclu&iacute;da.");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return Block.getDefaultUrl(userInfo, procData);
  }
}
