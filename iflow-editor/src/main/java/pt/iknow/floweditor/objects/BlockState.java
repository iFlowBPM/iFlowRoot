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
package pt.iknow.floweditor.objects;

import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iknow.floweditor.InstanciaComponente;

/**
 * Stores Block information for easier parsing.
 * 
 * @author lcabral
 * @version 13.05.2009
 */
public class BlockState {

  private FlowStateHistoryTO flowStateHistory;
  private InstanciaComponente componente;
  private boolean currentBlock;

  public BlockState(FlowStateHistoryTO flowStateHistory, InstanciaComponente componente, boolean currentBlock) {
    this.flowStateHistory = flowStateHistory;
    this.componente = componente;
    this.currentBlock = currentBlock;
  }

  public FlowStateHistoryTO getFlowStateHistory() {
    return flowStateHistory;
  }

  public void setFlowStateHistory(FlowStateHistoryTO flowStateHistory) {
    this.flowStateHistory = flowStateHistory;
  }

  public InstanciaComponente getComponente() {
    return componente;
  }

  public void setComponente(InstanciaComponente componente) {
    this.componente = componente;
  }

  public boolean isCurrentBlock() {
    return currentBlock;
  }

  public void setCurrentBlock(boolean currentBlock) {
    this.currentBlock = currentBlock;
  }
}
