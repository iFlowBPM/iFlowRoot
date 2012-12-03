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

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockEvento</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author ptgm@iknow
 * @version 1.0
 */

public class BlockEvento extends Block {
	public Port portIn, portFront;

	public BlockEvento(int anFlowId,int id, int subflowblockid, String filename) {
		super(anFlowId,id, subflowblockid, filename);
		isEvent = true;
		isCodeGenerator = true;
		hasInteraction = false;
		canRunInPopupBlock = false;
	}

	public Port[] getOutPorts (UserInfoInterface userInfo) {
		Port[] retObj = new Port[1];
		retObj[0] = portFront;
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
   * Update activities and assign tasks to event user
   * 
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return Block.getDefaultUrl(userInfo, procData);
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
      return portFront;
	}

	public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
		return this.getDesc(userInfo, procData, true, "Evento");
	}

	public String getResult (UserInfoInterface userInfo, ProcessData procData) {
		return this.getDesc(userInfo, procData, false, "Evento Efectuado");
	}

	public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
		return "";
	}
}
