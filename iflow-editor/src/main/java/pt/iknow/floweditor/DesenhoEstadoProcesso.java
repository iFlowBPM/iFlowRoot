package pt.iknow.floweditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iknow.floweditor.Cor.Colors;
import pt.iknow.floweditor.Cor.Fonts;
import pt.iknow.floweditor.objects.BlockState;

public class DesenhoEstadoProcesso extends Desenho {
  private static final long serialVersionUID = 2395541058068114174L;

  private JPanel blockPane;
  
  private boolean showSortedBlocks;

  private Image glow;

  private List<FlowStateHistoryTO> flowStateHistory;
  private List<Linha> touchedLines;
  private List<InstanciaComponente> touchedBlocks;

  public DesenhoEstadoProcesso(Janela janela, JScrollPane scrollPane, byte[] flowData, String altName, int flowVersion,
      List<FlowStateHistoryTO> flowStateHistory) {
    super(janela, scrollPane);

    this.showSortedBlocks = true;
    this.editable = false;
    this.flowVersion = flowVersion;
    this.flowStateHistory = flowStateHistory;
    this.touchedBlocks = new ArrayList<InstanciaComponente>();
    this.touchedLines = new ArrayList<Linha>();
    getPalette().setColor(Colors.Fundo, Cor.BACKGROUND);

    init(flowData);
  }

  private void init(byte[] flowData) {
    newFlow();
    importFlowData(flowData, flowName);
    parseFlow();
    initComponents();
    updateCanvas();
  }

  /**
   * Retrieve component name to paint tab.
   * 
   * @return Component name to paint tab.
   */
  @Override
  public String getName() {
    String suffix = "";
    if (!flowStateHistory.isEmpty()) {
      suffix = " (" + flowStateHistory.get(0).getFlowid() + "/" + flowStateHistory.get(0).getPid() + ")";
    } else { // Should avoid reaching this point...
      suffix = " (no process history)";
    }
    if (flowVersion != -1) {
      suffix += " (v" + this.flowVersion + ")";
    }
    return this.flowName + suffix;
  }

  /**
   * Funcao que desenho o circuito guardado.
   * 
   * @param g
   *          Classe respeitante a graficos.
   */
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Point Vpos = scrollPane.getViewport().getViewPosition();
    Dimension Vdim = scrollPane.getSize();
    int vx1 = Vpos.x;
    int vy1 = Vpos.y;
    int vx2 = vx1 + Vdim.width;
    int vy2 = vy1 + Vdim.height;

    if (_librarySet != null) {
      /* desenar linhas */
      if (janela.isDrawLines()) {
        if (janela.isDrawAll()) {
          for (Linha linha : lineList) {
            this.paint(g, linha);
          }
        } else {
          for (Linha linha : lineList) {
            if (linha.inViewPort(vx1 - 30, vy1 - 30, vx2 + 60, vy2 + 60)) {
              this.paint(g, linha);
            }
          }
        }
      }
      /* desenhar componentes */
      if (janela.isDrawComponentes()) {
        if (janela.isDrawAll()) {
          for (InstanciaComponente componente : componentList) {
            this.paint(g, componente);
          }
        } else {
          for (InstanciaComponente componente : componentList) {
            if (componente.inViewPort(vx1 - 30, vy1 - 30, vx2 + 60, vy2 + 60)) {
              this.paint(g, componente);
            }
          }
        }
      }
    }
  }

  public JPanel getBlockPane() {
    return blockPane;
  }

  public boolean showSortedBlocks() {
    return showSortedBlocks;
  }

  public void setShowSortedBlocks(boolean showSortedBlocks) {
    this.showSortedBlocks = showSortedBlocks;
  }

  public Image getGlow() {
    return glow;
  }

  public void setGlow(Image glow) {
    this.glow = glow;
  }


  private void paint(Graphics g, Componente componente) {
    if (componente instanceof InstanciaComponente) {
      if (touchedBlocks.contains(componente)) {
        if (isCurrentState(componente)) {
          setComponentePalette(componente, Cor.CURRENT_BLOCK, Cor.CURRENT_BLOCK);
          componente.getPalette().setFont(componente.getPalette().getFont(Fonts.Tipo_8_Bold));
          if (glow != null) {
            Image offsetImg = ((InstanciaComponente) componente).C_B.funcao_Desenho;

            int x = ((InstanciaComponente) componente).Posicao_X + (offsetImg.getWidth(this) / 2) - (glow.getWidth(this) / 2);

            int y = ((InstanciaComponente) componente).Posicao_Y + (offsetImg.getHeight(this) / 2) - (glow.getHeight(this) / 2);

            g.drawImage(glow, x, y, this);
          }
        } else {
          setComponentePalette(componente, Cor.TOUCHED_BLOCK, Cor.TOUCHED_BLOCK);
        }
      } else {
        setComponentePalette(componente, Cor.UNTOUCHED_BLOCK, Cor.UNTOUCHED_BLOCK);
      }
    } else {
      if (touchedLines.contains(componente)) {
        setComponentePalette(componente, Cor.TOUCHED_LINE, Cor.TOUCHED_LINE);
      } else {
        setComponentePalette(componente, Cor.UNTOUCHED_LINE, Cor.UNTOUCHED_LINE);
      }
    }
    componente.paint(g);
  }

  private void setComponentePalette(Componente comp, Color component, Color invalido) {
    //comp.getPalette().setColor(Colors.Activo, Color.blue);
    comp.getPalette().setColor(Colors.Componente, component);
    comp.getPalette().setColor(Colors.Invalido, invalido);
  }

  private boolean isCurrentState(Componente comp) {
    boolean ret = false;
    FlowStateHistoryTO state = flowStateHistory.get(flowStateHistory.size() - 1);
    if (comp instanceof InstanciaComponente && ((InstanciaComponente) comp).ID == state.getState()) {
      ret = true;
    }
    return ret;
  }

  private void parseFlow() {
    for (InstanciaComponente component : componentList) {
      component.setEditable(false);
      for (FlowStateHistoryTO flowState : flowStateHistory) {
        if (component.ID == flowState.getState()) {
          touchedBlocks.add(component);
          if (StringUtils.isNotBlank(flowState.getExitPort())) {
            int index = component.C_B.nomes_saidas.indexOf(flowState.getExitPort());
            if (index > -1 && component.C_B.Pontos_Saida.length > index) {
              for (List<Conector> conectorList : component.lista_estado_saidas) {
                for (Conector conector : conectorList) {
                  if (conector.Comp instanceof Linha) {
                    Point point = new Point(component.C_B.Pontos_Saida[index]);
                    point.setLocation(component.Posicao_X + point.x, component.Posicao_Y + point.y);
                    Point p1 = ((Linha) conector.Comp).p1;
                    Point p2 = ((Linha) conector.Comp).p2;
                    if (p1.equals(point) || p2.equals(point)) {
                      touchedLines.add((Linha) conector.Comp);
                    }
                  }
                }
              }
            }
          }
          break;
        }
      }
    }
  }
  
  private void initComponents() {
    CanvasBlockSorter blockSorter = new CanvasBlockSorter(this, getBlockStates(), getPalette());
    JScrollPane pane = new JScrollPane(blockSorter);
    pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    pane.getHorizontalScrollBar().setUnitIncrement(10);
    blockPane = new JPanel(new BorderLayout());
    blockPane.add(pane, BorderLayout.CENTER);
  }
  
  private List<BlockState> getBlockStates() {
    List<BlockState> blockStates = new ArrayList<BlockState>();
    for (int i = 0, lim = flowStateHistory.size(); i < lim; i++) {
      FlowStateHistoryTO flowState = flowStateHistory.get(i);
      for (InstanciaComponente component : touchedBlocks) {
        if (component.ID == flowState.getState()) {
          blockStates.add(new BlockState(flowState, component, (i == lim - 1)));
        }
      }
    }
    return blockStates;
  }
}