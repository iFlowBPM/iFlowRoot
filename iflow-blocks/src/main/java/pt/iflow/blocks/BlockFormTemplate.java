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

public class BlockFormTemplate extends Block {

    public BlockFormTemplate(int anFlowId, int id, int subflowblockid, String filename) {
        super(anFlowId, id, subflowblockid, filename);
    }

  @Override
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return false;
    }

  @Override
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public Port getEventPort() {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public Port[] getInPorts(UserInfoInterface userInfo) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
    public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

  @Override
    public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
    }

}
