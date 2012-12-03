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

import java.util.Map;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * Parametrização do formulário de detalhe de processo no backoffice do iFlow. 
 * 
 * @author oscar
 *
 */
public class BlockDetailForm extends BlockFormulario {

  public BlockDetailForm(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    nTYPE = nTYPE_DETALHE;
    sJSP = "detail.jsp";// force form JSP
  }
  
  
  public Object execute(int op, Object[]aoa) {
    Object result = null;
    if(op == 2) {
      // override default "generateForm"
      // String generateForm(UserInfo,ProcessData,HashMap)
      result = generateForm((BlockFormulario)this,
          (UserInfoInterface)aoa[0],
          (ProcessData)aoa[1],
          (Map<String,String>)aoa[2], 
          true, // force disabled
          (ServletUtils) aoa[3]);
    } else {
      result = super.execute(op, aoa);
    }
    return result;
  }

}
