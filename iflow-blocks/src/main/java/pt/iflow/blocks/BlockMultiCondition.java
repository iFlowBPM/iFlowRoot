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
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockValidacoes</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockMultiCondition extends Block {
  public int NUMBER_OF_CONDITION_PORTS = 4;
  public String CONDITION_ATRIBUTE_PREFIX = "condition";
  public Port portIn, portTrueC1, portTrueC2, portTrueC3, portTrueC4, portFalse;

  public BlockMultiCondition(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
    saveFlowState = false;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[NUMBER_OF_CONDITION_PORTS + 1];

    retObj[0] = portTrueC1;
    retObj[1] = portTrueC2;
    retObj[2] = portTrueC3;
    retObj[3] = portTrueC4;

    retObj[NUMBER_OF_CONDITION_PORTS] = portFalse;
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
    Port outPort = portFalse;

    String login = userInfo.getUtilizador();

    try {
      String[] sConditions = new String [NUMBER_OF_CONDITION_PORTS];
      boolean bEvalResult = false;

      Port[] conditionPorts = new Port[NUMBER_OF_CONDITION_PORTS];
      conditionPorts[0] = portTrueC1;
      conditionPorts[1] = portTrueC2;
      conditionPorts[2] = portTrueC3;
      conditionPorts[3] = portTrueC4;

      for (int i=0; i< NUMBER_OF_CONDITION_PORTS; i++){
        sConditions [i] = this.getAttribute(CONDITION_ATRIBUTE_PREFIX+i);
      }

      for (int i=0; i<NUMBER_OF_CONDITION_PORTS; i++){
        if (bEvalResult == false){
          if (StringUtils.isEmpty(sConditions [i])) {
            Logger.warning(login,this,"after", 
                procData.getSignature() + " Condition ["+(i+1)+"] is empty!! Assuming false");
            bEvalResult = false;
          } else {
            try {
              bEvalResult = procData.query(userInfo, sConditions [i]);
              Logger.debug(login,this,"after", sConditions [i] + " evaluated " + bEvalResult);
            } catch (Exception ei) {
              bEvalResult = false;
              Logger.error(login,this,"after", 
                  procData.getSignature() + "caught exception evaluation condition (assuming false): " + sConditions [i], ei);
            }

            if (bEvalResult) {
              Logger.info(login,this,"after", 
                  procData.getSignature() + "Condition ["+i+"] - ["+sConditions [i]+"], evaluated as true");
              outPort = conditionPorts[i];
            }
          }
          this.addToLog("Evaluated '" + sConditions[i] + "' as '" + bEvalResult + "'");
        }
      }

      this.addToLog("Evaluated all conditions using " + outPort.getName());
    } catch (Exception e) {
      Logger.error(login,this,"after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portFalse;
    }

    this.addToLog("Using '" + outPort.getName() + "';");
    this.saveLogs(userInfo, procData, this);

    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Multiple condition");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "all conditions verified");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
