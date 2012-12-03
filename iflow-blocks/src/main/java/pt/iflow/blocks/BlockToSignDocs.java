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

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;


public class BlockToSignDocs extends Block {
  public Port portIn, portTrue, portFalse;

  private final String sATTR_DOCS = "DocsToSign";
  private final String sATTR_TOSIGN = "CheckBoxToSign";
  
  public BlockToSignDocs(int anFlowId,int id, int subflowblockid, String filename) {
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
      String sDocs = null;
      String sValues = null;
      boolean bEvalResult = false;
      ProcessListVariable doclist = null;
      ProcessListVariable valueslist = null;
      
      sDocs = this.getAttribute(sATTR_DOCS);
      sValues = this.getAttribute(sATTR_TOSIGN);
      
      if(!StringUtils.isEmpty(sDocs) && !StringUtils.isEmpty(sValues)){
          doclist = procData.getList(sDocs);
          valueslist = procData.getList(sValues);
      }
      
      if (StringUtils.isEmpty(sDocs) || StringUtils.isEmpty(sValues) || doclist.size() != valueslist.size()) {
        Logger.warning(login,this,"after", procData.getSignature() + "empty attribute!!");
        bEvalResult = false;
      }
      else {
        try {
          
          Documents docBean = BeanFactory.getDocumentsBean();       
          bEvalResult = docBean.markDocsToSign(userInfo, doclist, valueslist);
                    
          Logger.debug(login,this,"after", " Documents mark to Sign successfully!");
        } catch (Exception ei) {
          bEvalResult = false;
          Logger.error(login,this,"after", procData.getSignature() + "caught exception, mark documents to sign", ei);
        }
      }
      
      if (bEvalResult) {
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
