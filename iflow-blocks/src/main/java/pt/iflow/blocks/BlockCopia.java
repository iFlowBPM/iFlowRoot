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
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.DataSetUtils;
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

public class BlockCopia extends Block {
  public Port portIn, portOut;

  private static final String sDEST_PREFIX = "dest";
  private static final String sORIG_PREFIX = "orig";


  public BlockCopia(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
    saveFlowState = false;
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
    Port[] retObj = new Port[1];
    retObj[0] = portOut;
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
    Port outPort = portOut;

    String login = userInfo.getUtilizador();

    try {
      ////////////////////////////////////
      // Execute the code generated by the block
      ////////////////////////////////////
      String sVarDest = null;
      for (int i=0; (sVarDest = this.getAttribute(sDEST_PREFIX + i)) != null; i++) {
        String sVarOrig = this.getAttribute(sORIG_PREFIX + i);
        boolean isArrayOrig = DataSetUtils.isArrayVar(sVarOrig);
        boolean isArrayDest = DataSetUtils.isArrayVar(sVarDest);
        
        int arrayOrigPos = isArrayOrig ? DataSetUtils.getArrayVarPosition(sVarOrig, procData) : -1;
        int arrayDestPos = isArrayDest ? DataSetUtils.getArrayVarPosition(sVarDest, procData) : -1;
                
        
        if (StringUtils.isEmpty(sVarOrig)) {
        	if (procData.isListVar(sVarDest))
        		procData.clearList(sVarDest);
        	else if (isArrayDest)
        		procData.getList(DataSetUtils.getArrayVarName(sVarDest)).parseAndSetItemValue(arrayDestPos, null);
        	else 
        		procData.clear(sVarDest);
        	
        	Logger.debug(login,this,"after","cleared " + sVarDest);

        	this.addToLog("Cleared " + sVarDest);
        	continue;
        }
        
        if (sVarOrig.indexOf("\"") == -1) {
          // orig value is a var name or a number
          try {
            Double.parseDouble(sVarOrig);
            // value is a number
            
        	if (isArrayDest)
        		procData.getList(DataSetUtils.getArrayVarName(sVarDest)).parseAndSetItemValue(arrayDestPos, sVarOrig);
        	else 
        		procData.parseAndSet(sVarDest, sVarOrig);
            
            Logger.debug(login,this,"after","set " + sVarDest + " with " + sVarOrig);
            this.addToLog("Set " + sVarDest + " with " + sVarOrig);
            continue;
          }
          catch (NumberFormatException nfe) {
          }
        }


        if(procData.isListVar(sVarOrig) && procData.isListVar(sVarDest)) {
        	// Array copy
        	ProcessListVariable orig = procData.getList(sVarOrig);
        	ProcessListVariable dest = procData.getList(sVarDest);
        	dest.setItems(orig.getItems());
        	procData.setList(dest);
    		Logger.debug(login, this, "after", "copied array " + sVarDest + " with array " + sVarOrig);  
    		this.addToLog("Copied array " + sVarDest + " with array " + sVarOrig);
        } else {
        	// var to var copy
        	Object value = null;
        	if (isArrayOrig) {
        	  ProcessListItem li = procData.getListItem(DataSetUtils.getArrayVarName(sVarOrig), arrayOrigPos);
        	  if (li != null)
        	    value = li.getValue(); 
        	}
        	else 
        		value = procData.eval(userInfo, sVarOrig);
        	
        	if (isArrayDest) {
        		String list = DataSetUtils.getArrayVarName(sVarDest);
        		ProcessListVariable listVar = procData.getList(list);
        		if(null == listVar) continue; // ignore this one...
        		listVar.setItemValue(arrayDestPos, value);
        		Logger.debug(login, this, "after", "set list " + list + " item " + arrayDestPos + " with " + value);
        		this.addToLog("Set list " + list + " item " + arrayDestPos + " with " + value);
        	}
        	else {
        		procData.set(sVarDest, value);
        		Logger.debug(login, this, "after", "set " + sVarDest + " with " + value);
        		this.addToLog("Set " + sVarDest + " with " + value);
        	}
        		
        }
      }
    }
    catch (Exception e) {
      Logger.error(login,this,"after",
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
    }

    this.addToLog("Using '" + outPort.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Cópia");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Cópia Efectuada");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
