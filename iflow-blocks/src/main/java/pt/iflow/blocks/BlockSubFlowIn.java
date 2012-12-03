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
public class BlockSubFlowIn extends Block {

  public Port portIn, portOut, portOutThread, portError;

  private final String sPREFIX = "Type_";

  public BlockSubFlowIn(int anFlowId, int id, int subflowblockid, String filename) {
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
    String userid = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();
    Port retObj = null;
    
    if (!"yes".equals(procData.getTempData("thread"))) {

      ProcessData pdPaiThread = new ProcessData(procData);
      pdPaiThread.setTempData("thread", "yes");
      procData.clearData();
      if (ForkManager.registerSubProc(userInfo, pdPaiThread, this)) {
        try {
        	// FIXME !!! no more old catalogues.. and dont use datasets...!!!!
        	
          HashMap<String,String> hmVars = new HashMap<String, String>();
          HashMap<String,String> hmValues = new HashMap<String, String>();
          byte [] sXml = BeanFactory.getFlowHolderBean().readSubFlowData(userInfo, this.getSubFlowFilename());
          XmlFlow xmlSubFlow = FlowMarshaller.unmarshal(sXml);
          XmlCatalogVars xmlCatalog = xmlSubFlow.getXmlCatalogVars();
          if(xmlCatalog.getXmlAttributeCount() > 0 && xmlCatalog.getXmlCatalogVarAttributeCount() == 0) {
            for (int i = 0; i < xmlCatalog.getXmlAttributeCount(); i++) {
              XmlAttribute attr = xmlCatalog.getXmlAttribute(i);
              hmVars.put(attr.getName(), attr.getDescription());
              hmValues.put(attr.getName(), attr.getValue());
            }
          } else {
            for (int i = 0; i < xmlCatalog.getXmlCatalogVarAttributeCount(); i++) {
              XmlCatalogVarAttribute attr = xmlCatalog.getXmlCatalogVarAttribute(i);
              hmVars.put(attr.getName(), attr.getDataType());
              hmValues.put(attr.getName(), attr.getInitVal());
            }
          }

          ProcessData dsProc = pdPaiThread;
          // init catalog
          ProcessData dsSub = procData;
          // O codigo anterior substitui o catalogo todo. vamos fazer o mesmo... :-(
          // dsSub.setCatalogueVars(hmVars, hmValues, new HashMap<String, String>());
          // Não fazemos nada e assumimos que é o mesmo catalogo uma vez que pertencem ao mesmo processo.

          HashMap<String,String> hmAttr = this.getAttributeMap();
          Iterator<String> it = hmAttr.keySet().iterator();
          while (it.hasNext()) {
            String var = it.next();
            Logger.debug(userInfo.getUtilizador(), this, "after", "var["+var+"]");
            if (StringUtils.isEmpty(var) || var.startsWith(sPREFIX)) continue;

            String subFlowVar = hmAttr.get(var);
            // type is not needed any more
            // String type = hmAttr.get(sPREFIX + var);
            if(dsProc.isListVar(subFlowVar)) {
              dsSub.setList(dsProc.getList(subFlowVar));
              Logger.debug(userInfo.getUtilizador(), this, "after", "  var: " + var + "   subFlowVar: " + subFlowVar + "  type array: " + dsProc.getVariableDataType(subFlowVar));
            } else {
              dsSub.set(dsProc.get(subFlowVar));
              Logger.debug(userInfo.getUtilizador(), this, "after", "  var: " + var + "   subFlowVar: " + subFlowVar + "  type: " + dsProc.getVariableDataType(subFlowVar));
            }
          }

          // create the activity for the subflow path
          ProcessManager pm = BeanFactory.getProcessManagerBean();

          Activity a = new Activity(userid, procData.getFlowId(), 
              procData.getPid(), procData.getSubPid(), 0, 0, 
              this.getDescription(userInfo, procData),
              Block.getDefaultUrl(userInfo, procData));
          a.profilename = userid;
          a.mid = procData.getMid();
          pm.createActivity(userInfo, a);


          retObj = portOut;
        }
        catch (Exception e) {
          e.printStackTrace();
          Logger.error(userid, this, "after", 
              procData.getSignature() + "caught exception: " + e.getMessage());
          retObj = portError;
        }

      } else {
        retObj = portError;
      }

    } else {
      procData.setTempData("thread","");
      retObj = portOutThread;
    }

    logMsg.append("Using '" + retObj.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return retObj;
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
