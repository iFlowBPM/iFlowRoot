package pt.iknow.floweditor;

import java.io.File;
import java.util.List;

import javax.swing.JScrollPane;

import pt.iflow.api.transition.FlowStateHistoryTO;

public class DesenhoScrollPane extends JScrollPane {
  private static final long serialVersionUID = 2956791840433440274L;
  
  private Desenho desenho;
  private R2RModule r2rFlow;
  private int r2rType;

  public DesenhoScrollPane(Janela j, String flowId, String flowName) {
    super();
    this.desenho = new Desenho(j, this, flowId, flowName);
    init();
  }
  
  public DesenhoScrollPane(Janela j, File flowFile) {
    super();
    this.desenho = new Desenho(j, this, flowFile);
    init();
  }
  
  public DesenhoScrollPane(Janela j, byte [] flowData, String altName, int flowVersion) {
    this(j, flowData, altName, flowVersion, null);
  }

  public DesenhoScrollPane(Janela j, byte [] flowData, String altName, int flowVersion, List<FlowStateHistoryTO> flowStateHistory) {
    super();
    if (flowStateHistory != null) {
      this.desenho = new DesenhoEstadoProcesso(j, this, flowData, altName, flowVersion, flowStateHistory);
    } else {
      this.desenho = new Desenho(j, this, flowData, altName, flowVersion);
    }
    init();
  }
  
  private void init() {
    setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    setViewportView(desenho);
  }
  
  public Desenho getDesenho() {
    return desenho;
  }
  
  public String getName() {
    return desenho.getName();
  }

  public void setR2rFlow(R2RModule r2rFlow) {
    this.r2rFlow = r2rFlow;
  }

  public R2RModule getR2rFlow() {
    return r2rFlow;
  }

  public void setR2rType(int r2rType) {
    this.r2rType = r2rType;
  }

  public int getR2rType() {
    return r2rType;
  }
}
