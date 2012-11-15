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
