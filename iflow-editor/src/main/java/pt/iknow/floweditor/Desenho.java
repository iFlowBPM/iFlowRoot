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
package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Desenho
 *
 *  desc: desenha o flow, trata de funcionalidades
 *
 ****************************************************/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.transition.FlowStateLogTO;
import pt.iflow.api.utils.FlowInfo;
import pt.iknow.floweditor.Cor.Colors;
import pt.iknow.floweditor.LerFicheiro.Ficheiro;
import pt.iknow.floweditor.messages.Messages;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.swing.RadioOptionPane;

/*******************************************************************************
 * Desenho
 */
public class Desenho extends JPanel implements ActionListener, Scrollable, KeyListener, IDesenho {

  protected static final long serialVersionUID = 123L;

  protected static final int INTERVALO = 5;
  protected static final int borderSize = 50;

  protected boolean editable;
  
  /** Cores do componente. */
  private Cor palette;
  
  // moved from Janela
  protected String flowName = "Untitled"; //$NON-NLS-1$
  protected String flowId = "untitled"; //$NON-NLS-1$
  protected int flowVersion = -1;
  protected String pnumber;

  /* janela a que pertence */
  protected Janela janela = null;
  protected JScrollPane scrollPane = null;

  protected boolean flowChanged = false;

  /* undo */
  protected List<List<Componente>> undo = new ArrayList<List<Componente>>();

  /* biblioteca de trabalho */
  protected LibrarySet _librarySet = new LibrarySet();

  protected List<Componente> activo = new ArrayList<Componente>();

  protected List<InstanciaComponente> componentList;

  protected List<Linha> lineList;

  protected List<Atributo> catalogue = new ArrayList<Atributo>();
  protected Map<String,Integer> catalogueMap = new HashMap<String, Integer>();
  protected Map<String,String> formTemplates = new HashMap<String,String>();

  /* variaveis para manipular componentes */
  protected Point ponto_esc = new Point();

  protected Point ponto_popup = new Point();

  /* componentes auxiliares */
  protected boolean circulo = false;

  protected Point ponto_circ = new Point();

  protected boolean nova_linha = false;

  protected Point ponto_nova_linha = new Point();

  protected Ligacao Rectangulo = new Ligacao(0, 0, 0, 0);

  protected boolean rect_activo = false;

  protected boolean a_mover_rect = false;

  protected boolean a_escolher = false;

  protected int botao_rato = -1;

  protected boolean nova = false;

  /* tamanho de pagina de impressao */
  protected PageFormat pageFormat = new PageFormat();

  /* drag and drop */
  protected Componente_Biblioteca cbEscolhido = null;

  protected Point cbEscolhidoPoint = new Point();

  /* numero de componentes instanciados */
  protected int _blockNumber = 1;

  // Localizacao do ultimo "save"
  protected File lastParent = new File("."); //$NON-NLS-1$
  
  /*****************************************************************************
   * 
   * Construtor
   * 
   ****************************************************************************/

  // default constructor just to initialize "core"
  protected Desenho(Janela j, JScrollPane scrollPane) {
    super();
    this.editable = true;
    this.janela = j;
    this.scrollPane = scrollPane;

    _librarySet = j.getLibrarySet();

    /* tratar dos eventos */
    processActions();

  }

  // reading libraries from the repository
  public Desenho(Janela j, JScrollPane scrollPane, byte [] flowData, String altName, int flowVersion) {
    this(j, scrollPane);

    newFlow();
    importFlowData(flowData, altName);
    this.flowVersion = flowVersion;
    updateCanvas();
  }

  // reading libraries from the repository
  public Desenho(Janela j, JScrollPane scrollPane, File flowFile) {
    this(j, scrollPane);

    newFlow();
    importFlowFile(flowFile);
    updateCanvas();
  }

  // reading libraries from the repository
  public Desenho(Janela j, JScrollPane scrollPane, String flowId, String flowName) {
    this(j, scrollPane);

    newFlow();
    this.flowId = flowId;
    this.flowName = flowName;
    updateCanvas();
  }

  protected Map<Integer, List<FlowStateLogTO>> cachedFlowStateLogs;
  
  protected boolean hasCachedFlowStateLogs(int state) {
    if(cachedFlowStateLogs == null) {
      cachedFlowStateLogs = new HashMap<Integer, List<FlowStateLogTO>>();
    }
    if(!cachedFlowStateLogs.isEmpty() && cachedFlowStateLogs.containsKey(state)) {
      return true;
    }
    return false;
  }
  
  protected List<FlowStateLogTO> getCachedFlowStateLogs(int state) {
    List<FlowStateLogTO> retObj = null;
    if(hasCachedFlowStateLogs(state)) {
      retObj = cachedFlowStateLogs.get(state);
    }
    return retObj;
  }

  protected void cacheFlowStateLogs(int state, List<FlowStateLogTO> flowStateLog) {
    if(!hasCachedFlowStateLogs(state)) {
      cachedFlowStateLogs.put(state, flowStateLog);
    }
  }
  
  protected void clearCachedFlowStateLogs() {
    cachedFlowStateLogs = new HashMap<Integer, List<FlowStateLogTO>>();
  }
  
  public void clearCaches() {
    clearCachedFlowStateLogs();
  }
  
  public void viewStateLogs() {
    if(!this.isEditable()) {
      List<FlowStateLogTO> flowStateLog = new ArrayList<FlowStateLogTO>();
      for (Componente comp : activo) {
        if (comp instanceof InstanciaComponente && comp.activo) {
          int state = ((InstanciaComponente) comp).ID;
          flowStateLog = getCachedFlowStateLogs(state);
          if(flowStateLog == null) {
            flowStateLog = janela.downloadProcessStateLogs(Integer.parseInt(getFlowId()), getPNumber(), state);
            cacheFlowStateLogs(state, flowStateLog);
          }
          break;
        }
      }
      Map<ViewStateLogDialog.PAGES, ImageIcon> images = new HashMap<ViewStateLogDialog.PAGES, ImageIcon>();
      images.put(ViewStateLogDialog.PAGES.FIRST, new ImageIcon(janela.createImage("nav_first.png", false, false)));
      images.put(ViewStateLogDialog.PAGES.NEXT, new ImageIcon(janela.createImage("nav_next.png", false, false)));
      images.put(ViewStateLogDialog.PAGES.PREVIOUS, new ImageIcon(janela.createImage("nav_previous.png", false, false)));
      images.put(ViewStateLogDialog.PAGES.LAST, new ImageIcon(janela.createImage("nav_last.png", false, false)));
      images.put(ViewStateLogDialog.PAGES.DETAILS, new ImageIcon(janela.createImage("menu_icon_process_view.png", false, false)));
      new ViewStateLogDialog(janela, flowStateLog, images);
    }
  }
  
  /*****************************************************************************
   * Funcao que associa as accoes de rato/teclado/menus a janela associada
   */
  protected void processActions() {
    /* associar accoes de teclado */
    addKeyListener(this);

    /* associar accoes dos botoes rato */
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {

        /* correct position */
        int x = e.getX();
        int y = e.getY();
        x = INTERVALO * ((int) (x + INTERVALO / 2) / INTERVALO);
        y = INTERVALO * ((int) (y + INTERVALO / 2) / INTERVALO);

        if (e.getModifiers() == 4)
          botao_rato = 2;
        else
          botao_rato = 1;

        /* double click */
        if (e.getClickCount() == 2) {
          if (!isEditable()) {
            viewStateLogs();
          } else {
            if (!rect_activo) {
              MmouseDown(x, y);
            }
            
            MmouseDClick(x, y);
            
            if (!rect_activo) {
              MmouseUp(x, y);
            }
          }
        } else {
          MmouseDown(x, y);
        }
      }

      public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (e.getModifiers() == 4) {
          Mpopmenu(x, y);
          return;
        }
        MmouseUp(x, y);

        /* verificar se existe coordendada <0 */
        verificaCoordenadasNegativas(activo);

      }

    });
    /* associar accao de movimento de rato */
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
      }

      public void mouseDragged(MouseEvent e) {
        processMouseMovement(e.getX(), e.getY());
      }

    });

  }

  /*****************************************************************************
   * Funcao que desenho o circuito guardado
   * 
   * @param g
   *          classe respeitante a graficos
   */
  public void paintComponent(Graphics g) {
    try {
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, janela.isAntiAlias() ? RenderingHints.VALUE_ANTIALIAS_ON
          : RenderingHints.VALUE_ANTIALIAS_OFF);
    } catch (Throwable t) {
      FlowEditor.log("error", t);
    }

    Point Vpos = scrollPane.getViewport().getViewPosition();
    Dimension Vdim = scrollPane.getSize();
    int vx1 = Vpos.x;
    int vy1 = Vpos.y;
    int vx2 = vx1 + Vdim.width;
    int vy2 = vy1 + Vdim.height;

    int c1 = 0, c2 = 0;

    /* saber as dimensoes do desenho */
    Dimension dim = getSize();

    setBackground(getPalette().getColor(Colors.Fundo));
    g.setColor(getPalette().getColor(Colors.Fundo));
    g.fillRect(0, 0, dim.width, dim.height);

    /* desenhar rectangulo */
    if (rect_activo) {
      int x2, x3, y2, y3;
      if (Rectangulo.c1 > Rectangulo.c2) {
        x3 = Rectangulo.c2;
        x2 = Rectangulo.c1;
      } else {
        x3 = Rectangulo.c1;
        x2 = Rectangulo.c2;
      }
      if (Rectangulo.n1 > Rectangulo.n2) {
        y3 = Rectangulo.n2;
        y2 = Rectangulo.n1;
      } else {
        y3 = Rectangulo.n1;
        y2 = Rectangulo.n2;
      }
      g.setColor(getPalette().getColor(Colors.BackSelected));
      g.fillRect(x3, y3, x2 - x3, y2 - y3);
    }

    /* desenhar pontos */
    if (_librarySet != null) {
      /* desenar linhas */
      if (janela.isDrawLines()) {
        if (janela.isDrawAll()) {
          for (int i = 0; i < lineList.size(); i++) {
            (lineList.get(i)).paint(g);
          }
        } else {
          for (int i = 0; i < lineList.size(); i++) {
            Componente c = lineList.get(i);
            if (c.inViewPort(vx1 - 30, vy1 - 30, vx2 + 60, vy2 + 60)) {
              c.paint(g);
            } else {
              c1++;
            }
          }
        }
      } else {
        c1 = lineList.size();
      }

      /* desenhar componentes */
      if (janela.isDrawComponentes()) {
        if (janela.isDrawAll()) {
          for (int i = 0; i < componentList.size(); i++)
            (componentList.get(i)).paint(g);
        } else {
          for (int i = 0; i < componentList.size(); i++) {
            Componente c = componentList.get(i);
            if (c.inViewPort(vx1 - 30, vy1 - 30, vx2 + 60, vy2 + 60)) {
              c.paint(g);
            } else {
              c2++;
            }
          }
        }
      } else {
        c2 = componentList.size();
      }
    }

    /* desenhar circulo */
    if (circulo) {
      g.setColor(getPalette().getColor(Colors.Circulo));
      g.drawOval(ponto_circ.x - 3, ponto_circ.y - 3, 6, 6);
    }

    /* desenhar rectangulo */
    if (rect_activo) {
      int x2, x3, y2, y3;
      if (Rectangulo.c1 > Rectangulo.c2) {
        x3 = Rectangulo.c2;
        x2 = Rectangulo.c1;
      } else {
        x3 = Rectangulo.c1;
        x2 = Rectangulo.c2;
      }
      if (Rectangulo.n1 > Rectangulo.n2) {
        y3 = Rectangulo.n2;
        y2 = Rectangulo.n1;
      } else {
        y3 = Rectangulo.n1;
        y2 = Rectangulo.n2;
      }
      g.setColor(getPalette().getColor(Colors.Rectangulo));
      g.drawRect(x3, y3, x2 - x3, y2 - y3);
    }

    /** ** */
    if (cbEscolhido != null) {
      g.setColor(getPalette().getColor(Colors.Activo));
      cbEscolhido.pinta(g, cbEscolhidoPoint.x, cbEscolhidoPoint.y, getPalette().getColor(Colors.Activo), getPalette().getColor(Colors.CompSelected));

    }
  }

  /*****************************************************************************
   * Funcao que trata de accoes de teclado
   * 
   * @param x
   *          valor correspondente a tecla
   */
  public void keyDown(int x) {
    /* ESC : redesenhar */
    if (x == KeyEvent.VK_ESCAPE) // 27)
      repaint();

    System.out.println(x);

    // if (x==17)
    // nova=true;
    /* DEL : Eliminar componentes activos */
    if (activo.size() > 0 && x == KeyEvent.VK_DELETE) // 127)
    {
      for (int i = activo.size() - 1; i >= 0; i--)
        removeComponente(((Componente) activo.get(i)));
      undo.add(activo);
      activo = new ArrayList<Componente>();
      repaint();

    }

  }

  public void keyPressed(java.awt.event.KeyEvent keyEvent) {
    System.out.println(keyEvent);
    keyDown(keyEvent.getKeyCode());
  }

  public void keyReleased(java.awt.event.KeyEvent keyEvent) {
  }

  public void keyTyped(java.awt.event.KeyEvent keyEvent) {
  }
  /*****************************************************************************
   * Seleciona um componente de biblioteca escolhido na janela de biblioteca
   * 
   * @param cb
   *          componente de biblioteca
   */
  public InstanciaComponente setComponent(Componente_Biblioteca cb, int x, int y) {
    /* limpar componentes activos */
    for (int aux2 = 0; aux2 < activo.size(); aux2++)
      ((Componente) activo.get(aux2)).Muda_Activo(false);
    activo.clear();
    rect_activo = false;

    /* criar componente */
    InstanciaComponente ic = cb.cria(this);

    /* alterar variaveis da janela */
    componentList.add(ic);
    ponto_esc.x = 0;

    ponto_esc.y = 0;
    ic.Ajusta_Coordenadas();

    ic.Posicao_X = x;
    ic.Posicao_Y = y;

    setFlowChanged(true);

    repaint();
    return ic;
  }

  /*****************************************************************************
   * Trata da accaoe de carregar NO botao esquerdo do rato
   * 
   * @param x
   *          coordenada x do rato
   * @param y
   *          coordenada y do rato
   */
  public boolean MmouseDown(int x, int y) {
    /* se nao esta escolhido nenhum comp. */
    /* teste [jpp] */
    if (!rect_activo && activo.size() >= 0) {
      for (int i = 0; i < activo.size(); i++) {
        ((Componente) activo.get(i)).Muda_Activo(false);
      }
      activo.clear();
      repaint();

    }

    if ((rect_activo && !a_escolher && !a_mover_rect) || (activo.size() == 0 && !a_escolher && !a_mover_rect))
      return selectObject(x, y);

    return false;
  }

  public boolean MmouseUp(int x, int y) {
    /* se nao esta escolhido nenhum comp. */
    return LibertaObjecto(x, y);
  }

  /*****************************************************************************
   * Funcao que escolhe qual o objecto escolhido
   * 
   * @param x
   *          coordenada x do rato
   * @param y
   *          coordenada y do rato
   */
  public boolean selectObject(int x, int y) {
    ponto_esc.x = x;
    ponto_esc.y = y;
    nova_linha = false;
    int x1, y1, x2, y2;

    /* verificar se carregou NO rectangulo */
    if (rect_activo) {
      a_mover_rect = false;
      x1 = Math.min(Rectangulo.c1, Rectangulo.c2);
      x2 = Math.max(Rectangulo.c1, Rectangulo.c2);
      y1 = Math.min(Rectangulo.n1, Rectangulo.n2);
      y2 = Math.max(Rectangulo.n1, Rectangulo.n2);
      /* se carregou dentro do rectangulo, entao e para move-lo */
      if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
        a_mover_rect = true;
        return false;
      } else {
        rect_activo = false;
        repaint(x1 - 1, y1 - 1, 2 + x2 - x1, 2 + y2 - y1);

      }
    }

    /* limpar lista de objectos seleccionados */
    for (int i = 0; i < activo.size();) {
      activo.get(0).Muda_Activo(false);
      activo.remove(0);
    }

    /* verificar se carregou num dos componentes */
    Linha activo_ = null;
    if (!nova)
      activo = getComponents(_librarySet, x, y);
    if (activo.size() != 0) {
      rect_activo = false;
      return false;
    }

    if (_librarySet != null) {
      /* Verificar se carregou numa entrada/saida */
      activo_ = new Linha(x, y, x, y);
      ponto_nova_linha.x = x;
      ponto_nova_linha.y = y;
      /* verificar se liga */
      if (isConnected(activo_, x, y)) {
        activo_.dentro(x, y);
        lineList.add(activo_);
        ponto_esc.x = x;
        ponto_esc.y = y;
        activo.add(activo_);
        activo_.Muda_Activo(true);
        nova_linha = true;
        rect_activo = false;
        return false;
      } else {
        /* verificar se liga a uma linha */
        for (int i = 0; i < lineList.size(); i++) {
          Linha l = lineList.get(i);
          Point p;
          /* verificar se liga a algum ponto desta linha */
          if ((p = l.Pontos_Interiores(x, y)) != null) {
            List<Conector> __l = l.lista_estado_entradas.get(0);
            // Conector
            Conector con = __l.get(0);
            Componente c = con.Comp;
            /* se a linha esta ligada a agum componente */
            if (c != null) {
              activo_ = new Linha(p.x, p.y, p.x, p.y);
              activo_.escolhido = activo_.numberPoints();
              activo_.Insere_Entrada(c, con.Numero, 0);
              activo_.muda(p.x, p.y, p.x, p.y);
              List<Point> v = new ArrayList<Point>();
              /* adicionar pontos */
              if (l.lista[0] == c) {
                for (int j = 0; j < l.numberPoints(); j++) {
                  Point p1 = l.getPoint(j);
                  v.add(new Point(p1));
                  if (p == p1)
                    break;
                }
                v.add(activo_.p2);
                activo_.p1 = activo_.getPoint(0);
              } else {
                v.add(activo_.p1);
                for (int j = l.numberPoints() - 1; j > 0; j--) {
                  Point p1 = l.getPoint(j);
                  v.set(1, new Point(p1));
                  if (p1 == p)
                    break;
                }
                activo_.p2 = activo_.getPoint(activo_.numberPoints() - 1);
              }
              activo_.setPointList(v);
              /* adicionar linha a lista */
              lineList.add(activo_);
              ponto_esc.x = p.x;
              ponto_esc.y = p.y;
              activo_.Muda_Activo(true);
              activo.add(activo_);
              nova_linha = true;
              rect_activo = false;
              c.Insere_Saida(activo_, con.Numero, 0);
              activo_.escolhido = 1;
              return false;
            }
          }
        }
      }
    }

    /* na carregou a nada => comecar a escolher uma dada area */
    a_escolher = true;
    Rectangulo.c1 = x;
    Rectangulo.c2 = x;
    Rectangulo.n1 = y;
    Rectangulo.n2 = y;
    rect_activo = true;
    repaint();
    return false;
  }

  /*****************************************************************************
   * Funcao que trata do movimento do rato
   * 
   * @param x
   *          coordenada x do rato
   * @param y
   *          coordenada y do rato
   */
  public boolean processMouseMovement(int x, int y) {
    int x1, y1, x2, y2;
    int t1, t2;

    /*
     * iSAIR se o botao nao e o esquerdo ou se nao esta escolhido nenhum objecto
     */
    if (botao_rato != 1 || (activo.size() == 0 && rect_activo == false))
      return false;

    /* descobrir maior e menor x e y */
    x1 = Math.min(x, ponto_esc.x);
    t1 = Math.max(x, ponto_esc.x) - x1;
    y1 = Math.min(y, ponto_esc.y);
    t2 = Math.max(y, ponto_esc.y) - y1;

    /* "PULAR" */
    if (t1 < INTERVALO && t2 < INTERVALO)
      return false;

    /* se o utilizador estiver a seleccionar uma area */
    if (a_escolher) {
      int x3, y3;
      /* actualizar o rectangulo */
      x2 = Math.min(Math.min(Rectangulo.c1, x), Rectangulo.c2);
      x3 = Math.max(Math.max(Rectangulo.c1, x), Rectangulo.c2);
      y2 = Math.min(Math.min(Rectangulo.n1, y), Rectangulo.n2);
      y3 = Math.max(Math.max(Rectangulo.n1, y), Rectangulo.n2);
      Rectangulo.c2 = x;
      Rectangulo.n2 = y;
      repaint(x2 - 5, y2 - 5, x3 - x2 + 10, y3 - y2 + 10);
      x3 = Math.min(Rectangulo.c1, Rectangulo.c2);
      x2 = Math.max(Rectangulo.c1, Rectangulo.c2);
      y3 = Math.min(Rectangulo.n1, Rectangulo.n2);
      y2 = Math.max(Rectangulo.n1, Rectangulo.n2);
      // Library b=Biblioteca_activa;

      /* limpar lista de activos */
      for (int i = 0; i < activo.size();) {
        activo.remove(0).Muda_Activo(false);
      }

      /* percorrer lista de componentes */
      for (int i = 0; i < componentList.size(); i++) {
        Componente t = componentList.get(i);
        /* verificar se o componente esta dentro da area */
        if (t.Contido(x3, y3, x2, y2)) {
          activo.add(t);
          t.Muda_Activo(true);
        }
      }
      /* verificar nas linhas */
      for (int i = 0; i < lineList.size(); i++) {
        Componente t = lineList.get(i);
        if (t.Contido(x3, y3, x2, y2)) {
          activo.add(t);
          t.Muda_Activo(true);
        }
      }
      return false;
    }

    /* se estiver a mover o rectangulo */
    if (a_mover_rect) {
      int x3, y3;
      /* actualizar posicao rectangulo */
      Rectangulo.c1 += x - ponto_esc.x;
      Rectangulo.n1 += y - ponto_esc.y;
      Rectangulo.c2 += x - ponto_esc.x;
      Rectangulo.n2 += y - ponto_esc.y;

      /* refresh */
      x3 = Math.min(Rectangulo.c1, Rectangulo.c2);
      x2 = Math.max(Rectangulo.c1, Rectangulo.c2);
      y3 = Math.min(Rectangulo.n1, Rectangulo.n2);
      y2 = Math.max(Rectangulo.n1, Rectangulo.n2);

      repaint(x3 - t1 - 15, y3 - t2 - 15, x2 - x3 + 2 * t1 + 30, y2 - y3 + 2 * t2 + 40);
    }

    /* se estiver algum objecto activo => move-lo */
    if ((activo.size() != 0 && !rect_activo) || a_mover_rect) {

      /* percorrer lista */
      for (int o = 0; o < activo.size(); o++) {
        Componente comp = activo.get(o);
        if (!rect_activo) {
          comp.move(x - ponto_esc.x, y - ponto_esc.y);
          setFlowChanged(true);
          comp.refresh(t1, t2, this);
          if (isInputOrOutput(x, y))
            circulo = true;
          else
            circulo = false;
        } else {
          if (comp.E_Linha()) {
            setFlowChanged(true);
            ((Linha) comp).Move_Pontos_Desligados(x - ponto_esc.x, y - ponto_esc.y);
            comp.refresh(t1, t2, this);
          } else {
            setFlowChanged(true);
            comp.move(x - ponto_esc.x, y - ponto_esc.y);
            comp.refresh(t1, t2, this);
          }
        }
      }
    }

    // do scroll
    doAutoScroll(x, y);

    /* guardar ultimos valores */
    ponto_esc.x = x;
    ponto_esc.y = y;
    return false;
  }

  /*****************************************************************************
   * Funcao que trata da accao de 2o click NO objecto activo
   * 
   * @param x
   *          coordenada x do rato
   * @param y
   *          coordenada y do rato
   */
  public boolean LibertaObjecto(int x, int y) {
    updateCanvasSize();
    ponto_esc.x = x;
    ponto_esc.y = y;
    circulo = false;

    /* se e uma linha nova */
    if (nova_linha && activo.size() > 0) {
      /* elimina linha se tiver menos de comprimento 10 */
      if (Math.abs(x - ponto_nova_linha.x) + Math.abs(y - ponto_nova_linha.y) < 10) {
        Componente c = activo.get(activo.size() - 1);
        c.Muda_Activo(false);
        removeComponente(c);
        activo.remove(activo.size() - 1);
        repaint();
        return false;
      }
      setFlowChanged(true);
    }

    /* ajustar pontos */
    for (int o = 0; o < activo.size(); o++)
      activo.get(o).Ajusta_Coordenadas();
    repaint();

    /* se estava a mover o rectangulo, ja nao esta */
    if (a_mover_rect) {
      nova_linha = false;
      a_mover_rect = false;
      return false;
    }
    /* se estava a escolher o rectangulo, ja nao esta */
    if (a_escolher) {
      a_escolher = false;
      nova_linha = false;

      if (rect_activo && activo.size() == 0)
        rect_activo = false;
      return false;
    }

    /* se estiver algum componente activo */
    if (activo.size() > 0) {
      /* se o comp activo for linha desfazer a ligacao */
      if ((activo.size() == 1) && !rect_activo && (activo.get(0).E_Linha())) {
        setFlowChanged(true);
        Linha l = (Linha) activo.get(0);
        if (isConnected(l, x, y)) {
          /* acrescentar 2 pontos interemedios */
          if (l.numberPoints() == 2) {
            l.Ajusta_Coordenadas();
          }
          circulo = false;
          activo.clear();
          // rect_activo=false;
          l.Muda_Activo(false);
          nova_linha = false;
          return false;
        }
        l.Desliga();
      }
    }

    a_mover_rect = false;

    if (a_escolher)
      a_escolher = false;

    return false;
  }

  /*****************************************************************************
   * Elimina um componente da lista
   * 
   * @param comp
   *          componente a elimina
   */
  protected void removeComponente(Componente comp) {
    if(this.isEditable() && comp.isEditable()) {
      if (!(comp instanceof Linha) && ((InstanciaComponente) comp).C_B.isAutomatic()) {
        new Erro(Mesg.ErroDeleteStart, janela);
        return;
      }
      
      /* eliminar ligacoes do componente */
      comp.Elimina(activo);
      /* retirar o componente das listas */
      int pos = componentList.indexOf(comp);
      if (pos != -1) {
        componentList.remove(pos);
      }
      pos = lineList.indexOf(comp);
      if (pos != -1) {
        lineList.remove(pos);
      }
      comp.refresh(5, 5, this);
      setFlowChanged(true);
      updateCanvasSize();
    }
  }

  /**
   * ***********************************************+ Verifica se um componente
   * (linha) esta ligado a um outro componente da lista
   * 
   * @param activo
   *          componente a verificar ligacoes
   * @param x
   *          coordenada x do rato
   * @param y
   *          coordenada y do rato
   */
  public boolean isConnected(Componente activo, int x, int y) {
    int j1, j2;
    Linha linha = (Linha) activo;
    if (_librarySet == null || linha.EscPontoInterior())
      return false;

    /* percorrer lista de componentes */
    for (int i = 0; i < componentList.size(); i++) {
      Componente ic;
      ic = componentList.get(i);
      if (ic != activo) {
        /* verificar se e saida */
        j2 = ic.E_Saida(x, y);
        if (j2 > 0)
          Componente.Liga(ic, j2 - 1, activo, 0);
        /* verificar se e uma entrada */
        j1 = ic.E_Entrada(x, y);
        if (j1 > 0)
          Componente.Liga(activo, 0, ic, j1 - 1);
        /* iSAIR se ja encontrou o componente */
        if (j1 != 0 || j2 != 0) {
          repaint();
          if (nova_linha && linha.numberPoints() == 4) {
            Point p3 = linha.getPoint(1);
            Point p4 = linha.getPoint(0);
            p3.y = p4.y;
          }
          return true;
        }
      }
    }
    /* verificar se liga a um ponto de quebra de uma linha */
    for (int i = 0; i < lineList.size(); i++) {
      Linha l = (Linha) lineList.get(i);
      if (l != activo) {
        /* testar os pontos interiores da linha */
        Point p = l.Pontos_Interiores(x, y);
        if (p != null)
          if (p != l.p1 && p != l.p2) {
            List<Conector> __l = l.lista_estado_entradas.get(0);
            // Conector
            Conector c = __l.get(0);

            /*
             * so liga se a linha em questao esta ligada a um componente
             */
            if (c.Comp != null) {
              /*
               * verificar orientacao da linha e inserir os pontos
               * correspondentes
               */
              if (l.lista[0] == c.Comp) {
                for (int j = 0; j < l.numberPoints(); j++) {
                  Point p1 = l.getPoint(j);
                  if (p == p1)
                    break;
                  if (linha.escolhido == 1)
                    linha.setPoint(j, new Point(p1));
                  else
                    linha.setPoint(linha.numberPoints() - j - 1, new Point(p1));
                }
              } else {
                for (int j = l.numberPoints() - 1, t = 0; j > 0; j--, t++) {
                  Point p1 = l.getPoint(j);
                  if (p1 == p)
                    break;
                  if (linha.escolhido == 1)
                    linha.setPoint(t, new Point(p1));
                  else
                    linha.setPoint(linha.numberPoints() - t - 1, new Point(p1));
                }
              }
            }
            /* actualizar os pontos extremidades */
            linha.p1 = linha.getPoint(0);
            linha.p2 = linha.getPoint(linha.numberPoints() - 1);
            /* criar ligacoes */
            c.Comp.Insere_Saida(linha, c.Numero, 0);
            linha.Insere_Entrada(c.Comp, c.Numero, 0);
            return true;
          }
      }
    }
    return false;
  }

  /*****************************************************************************
   * Verifica se um ponto corresponde a uma entrada ou uma saida
   */
  public boolean isInputOrOutput(int x, int y) {

    if (_librarySet == null)
      return false;
    /* percorrer lista de componentes */
    for (int i = 0; i < componentList.size(); i++) {
      Componente ic;
      ic = (Componente) componentList.get(i);
      if (activo.size() == 1) {
        if (ic != ((Componente) activo.get(0))) {
          int aux;
          /* verificar se e saida */
          if ((aux = ic.E_Saida(x, y)) > 0) {
            Point p = ic.Ponto_Saida(aux - 1);
            ponto_circ.x = p.x;
            ponto_circ.y = p.y;
            return true;
          }
          /* verificar se e uma entrada */
          if ((aux = ic.E_Entrada(x, y)) > 0) {
            Point p = ic.Ponto_Entrada(aux - 1);
            ponto_circ.x = p.x;
            ponto_circ.y = p.y;
            return true;
          }
        }
      }
    }
    return false;
  }

  /*****************************************************************************
   * Devolve o componente que esta nesse ponto NO caso de linhas devolver todas
   */
  public ArrayList<Componente> getComponents(LibrarySet b_a, int x, int y) {
    ArrayList<Componente> acc = new ArrayList<Componente>();
    if (b_a != null) {
      /* verificar na lista de instancias */
      for (int i = 0; i < componentList.size(); i++) {
        Componente ic = componentList.get(i);
        if (ic.dentro(x, y)) {
          ponto_esc.x = x;
          ponto_esc.y = y;
          ic.Muda_Activo(true);
          acc.add(ic);
          return acc;
        }
      }
      /* procurar na lista de linhas */
      Componente comp = null;
      boolean esc = false;
      double iii = 0.0;
      for (int i = 0; i < lineList.size(); i++) {
        Linha ic = lineList.get(i);
        boolean dentro = ic.dentro(x, y);
        List<Conector> __l = ic.lista_estado_entradas.get(0);
        Conector con = null;
        if (__l.size() > 0) {
          con = __l.get(0);
        }
        Componente _comp = null;
        if (con != null)
          _comp = con.Comp;
        if ((esc == false || comp == _comp) && dentro) {
          ponto_esc.x = x;
          ponto_esc.y = y;
          ic.Muda_Activo(true);
          acc.add(ic);
          esc = true;
          return acc;
        } else {
          if (iii == 0.5 && ic.intermedio == 0.0 && dentro) {
            for (int j = 0; j < acc.size(); j++) {
              Linha l = (Linha) acc.get(j);
              l.Muda_Activo(false);
            }
            acc.clear();
            acc.add(ic);
            ic.Muda_Activo(true);
            comp = con.Comp;
            iii = 0.0;
            return acc;
          }
        }
      }
    }
    return acc;
  }

  /*****************************************************************************
   * Fazer duplo-clique nos componentes selecionados
   */
  public boolean MmouseDClick(int x, int y) {
    /* percorrer os componentes activos e fazer double-click */
    for (int i = 0; i < activo.size(); i++) {
      Componente ic = (Componente) activo.get(i);
      ic.alteraAtributos(this, janela);
    }
    repaint();

    return false;
  }

  /*****************************************************************************
   * Funcao que verifica qual o popup menu que e mostrado
   */
  public void Mpopmenu(int x, int y) {
    if(!this.isEditable()) {
      y = y + 60;
    }
    JPopupMenu i;

    if (activo.size() != 0) {

      Componente c = (Componente) activo.get(0);
      /* seleccionar */
      if (activo.size() == 1 && c.E_Linha()) {
        i = janela.getLinePopup(this);
      } else {
        boolean b = true;
        for (int j = 0; j < activo.size(); j++) {
          if (!((Componente) activo.get(j)).E_Linha()) {
            b = false;
          }
        }
        if (!b) {
          i = janela.getBlockPopup(this);
        } else {
          i = janela.getLinePopup(this);
        }
      }
    } else {
      i = janela.getCanvasPopup(this);
    }
    ponto_popup.x = x;
    ponto_popup.y = y;
    ponto_esc.x = x;
    ponto_esc.y = y;

    /* mostrar popup-menu */
    Point Vpos = scrollPane.getViewport().getViewPosition();
    int vx1 = x - Vpos.x;
    int vy1 = y - Vpos.y;

    if(i != null) {
      i.show(janela, vx1, vy1);
    }
  }

  public void copyTo(Desenho d) {
    d.duplicateComponents(this, activo, Rectangulo, rect_activo, ponto_esc, 0);
    rect_activo = false; // reset...
    a_mover_rect = false;
    janela.setSelectedDesenho(d);
  }

  /*****************************************************************************
   * funcao que dublica a lista de componentes na lista de activos
   */
  public void duplica() {
    if(this.isEditable()) {
      duplicateComponents(this, activo, Rectangulo, rect_activo, ponto_esc, 50);
    }
  }

  protected void duplicateComponents(Desenho from, List<Componente> activo, Ligacao rectangulo, boolean rect_activo, Point ponto_esc, int incr) {
    /* percorrer lista de seleccionados */
    List<Componente> novo = new ArrayList<Componente>();
    List<Componente> toKill = new ArrayList<Componente>();
    Rectangulo.c1 = rectangulo.c1 + incr;
    Rectangulo.c2 = rectangulo.c2 + incr;
    Rectangulo.n1 = rectangulo.n1 + incr;
    Rectangulo.n2 = rectangulo.n2 + incr;
    this.rect_activo = rect_activo;
    this.ponto_esc.x = ponto_esc.x + incr;
    this.ponto_esc.y = ponto_esc.y + incr;


    for (int i = 0; i < activo.size(); i++) {
      setFlowChanged(true);
      Componente activo_ = activo.get(i);
      if (activo_.E_Linha()) {
        Linha antigo = (Linha) activo_;
        Linha l = new Linha();

        if(antigo.lista == null || antigo.lista.length < 2 || antigo.lista[0] == null || antigo.lista[1] == null) {
          toKill.add(l);
        }


        Componente c1 = antigo.lista[0];
        Componente c2 = antigo.lista[1];
        // If line end is outside selection, ignore line
        if(!activo.contains(c1) || !activo.contains(c2)) {
          toKill.add(l);
        }

        if(!toKill.contains(l)) {
          lineList.add(l);
          l.activo = true;
        }

        novo.add(l);
        /* colocar todos os pontos iguais com delta 50 */
        l.p1.x = antigo.p1.x + incr;
        l.p1.y = antigo.p1.y + incr;
        l.p2.x = antigo.p2.x + incr;
        l.p2.y = antigo.p2.y + incr;

        for (int j = 1; j < antigo.numberPoints() - 1; j++) {
          Point p = antigo.getPoint(j);
          l.setPoint(j, new Point(p.x + incr, p.y + incr));
        }
        int x3 = Math.min(Rectangulo.c1, Rectangulo.c2);
        int x2 = Math.max(Rectangulo.c1, Rectangulo.c2);
        int y3 = Math.min(Rectangulo.n1, Rectangulo.n2);
        int y2 = Math.max(Rectangulo.n1, Rectangulo.n2);
        l.Contido(x3, y3, x2, y2);

      } else {
        /* duplicar usando a informacao de biblioteca */
        InstanciaComponente ic;
        // start é caso especial...
        if(((InstanciaComponente) activo_).isStart()) {
          ic=((InstanciaComponente) activo_);
          InstanciaComponente icStart = getBlockStart();
          if(ic != icStart) { // se for o mesmo bloco, ignora...

            String[] options = {
                Messages.getString("Desenho.duplicateStart.replaceOpt"),
                Messages.getString("Desenho.duplicateStart.appendOpt"),
                Messages.getString("Desenho.duplicateStart.ignoreOpt"),
            };

            RadioOptionPane opt = new RadioOptionPane(this, 
                Messages.getString("Desenho.duplicateStart.title"),
                Messages.getString("Desenho.duplicateStart.msg"), 
                options 
            );
            opt.setDefaultOption(2);
            opt.setButtonText(Messages.getString("Desenho.duplicateStart.button"));
            int result = opt.open();
            System.out.println("Tha result is.... "+result);
            novo.add(icStart);
            if(result == 0 || result == 1) {
              if(result == 0) icStart.getAtributos().clear();
              for(Atributo a : ic.getAtributos()) {
                Atributo b = icStart.getAtributo(a.getNome());
                if(b == null) icStart.addAtributo(a.cloneAtributo());
              }

              // catálogo...
              if(result == 0) catalogue.clear();
              Set<String> mCat = new HashSet<String>(catalogue.size());
              for(Atributo a : catalogue)
                mCat.add(a.getNome());

              for(Atributo a : from.getCatalogue()) {
                if(mCat.contains(a.getNome())) continue;
                catalogue.add(a.cloneAtributo());
              }


            }
            ic=icStart;
          } else {
            JOptionPane.showMessageDialog(this, Messages.getString("Desenho.duplicateStart.errorMsg"), Messages.getString("Desenho.duplicateStart.errorTitle"), JOptionPane.ERROR_MESSAGE);
          }
        } else {
          ic = ((InstanciaComponente) activo_).clone(this);
          componentList.add(ic);
          novo.add(ic);
          /* actualizar posicao */
          ic.Posicao_X = ((InstanciaComponente) activo_).Posicao_X + incr;
          ic.Posicao_Y = ((InstanciaComponente) activo_).Posicao_Y + incr;
        }

        ic.activo = true;
      }
      activo_.activo = false;
    }

    if (rect_activo)
      a_mover_rect = true;

    /* fazer as ligacoes entre os componentes */
    for (int aux = 0; aux < activo.size(); aux++) {
      Componente comp = activo.get(aux);
      if (comp.E_Linha()) {
        Linha l = (Linha) comp;
        for (int i = 0; i < 2; i++)
          /* se estiver ligado */
          if (l.lista[i] != null) {
            int posicao = activo.indexOf(l.lista[i]);
            if (posicao >= 0) {
              if (i == 0)
                ((Linha) novo.get(aux)).escolhido = 1;
              else
                ((Linha) novo.get(aux)).escolhido = l.numberPoints();

              List<Conector> __l = l.lista_estado_entradas.get(0);
              Conector v = __l.get(0);

              if (v.Comp == l.lista[i])
                Componente.Liga(novo.get(posicao), v.Numero, novo.get(aux), 0);
              else {
                List<Conector> cc = l.lista_estado_saidas.get(0);

                for (int a2 = 0; a2 < cc.size(); a2++) {
                  v = cc.get(a2);
                  if (v.Comp == l.lista[i])
                    Componente.Liga(novo.get(aux), 0, novo.get(posicao), v.Numero);
                }
              }
            }
          }
      }
    }

    activo.clear(); // remove other container active state
    this.activo.clear();
    this.activo.addAll(novo);
    this.activo.removeAll(toKill);

    botao_rato = 1;
    updateCanvasSize();
    repaint();
  }

  /*****************************************************************************
   * funcao que trata de accoes de popup menu
   */
  public void processPopUpMenuActions(String accao, Library b) {
    if (accao.equals(Mesg.MenuUndo))
      undo();
    else if (accao.equals(Mesg.MenuNovaLinha) || accao.equals(Mesg.MenuLinha)) {
      Linha linha = new Linha();
      linha.muda(ponto_popup.x, ponto_popup.y, ponto_popup.x + 50, ponto_popup.y + 50);
      lineList.add(linha);
      setFlowChanged(true);
    } else if (accao.equals(Mesg.SearchBlock)) {
      searchBlockByName();
    } else if (accao.equals(Mesg.SearchBlockWithVar)) {
      searchBlockByContents();
    } else {
      /* novo componente */
      Componente_Biblioteca cb = b.getComponent(accao);
      if (cb != null) {
        if(editable) {
          InstanciaComponente ic = setComponent(cb, ponto_esc.x, ponto_esc.y);
          activo.add(ic);
          botao_rato = 1;
          a_escolher = false;
        }
      }
    }
    repaint();
  }

  public void verifyFlow() {
    Validate.VerifyFlow(componentList, janela);
  }

  public void undo() {
    if(this.isEditable()) {
      if (undo.size() > 0) {
        List<Componente> ultimo = undo.get(undo.size() - 1);
        undo.remove(undo.size() - 1);
        for (int i = 0; i < ultimo.size(); i++) {
          Componente c = ultimo.get(i);
          c.Muda_Activo(false);
          if (c.E_Linha())
            lineList.add((Linha) c);
          else {
            componentList.add((InstanciaComponente) c);
          }
        }
      }
      updateCanvasSize();
      repaint();
    }
  }

  public void insertBreakPoint() {
    if (this.isEditable()) {
      /* inserir ponto de quebra da linha */
      for (int i = 0; i < activo.size(); i++) {
        Componente c = (Componente) activo.get(i);
        if (c.E_Linha()) {
          setFlowChanged(true);
          Linha l = (Linha) c;
          l.Insere_Ponto_Quebra(ponto_popup.x, ponto_popup.y);
        }
      }
      botao_rato = 1;
      repaint();
    }
  }

  public void removeBreakPoint() {
    if(this.isEditable()) {
      /* retirar ponto de quebra da linha */
      for (int i = 0; i < activo.size(); i++) {
        Componente c = (Componente) activo.get(i);
        
        if (c.E_Linha()) {
          setFlowChanged(true);
          Linha l = (Linha) c;
          l.Elimina_Ponto_Quebra(ponto_popup.x, ponto_popup.y);
        }
        c.Muda_Activo(false);
      }
      activo.clear();
      rect_activo = false;
      repaint();
    }
  }

  public void renameBlock() {
    if(this.isEditable()) {
      /* mudar o nome */
      for (int i = 0; i < activo.size(); i++) {
        Componente comp = (Componente) activo.get(i);
        if (!comp.E_Linha()) {
          setFlowChanged(true);
          InstanciaComponente ic = (InstanciaComponente) comp;
          
          String sNome = ic.Nome;
          int nId = ic.ID;
          String stmp = "(" + nId + ")"; //$NON-NLS-1$ //$NON-NLS-2$
          int ntmp = sNome.indexOf(stmp);
          if (ntmp > -1) {
            sNome = sNome.substring(0, ntmp);
          }
          AlteraNomeBloco mn = new AlteraNomeBloco(janela, sNome);
          if(mn.openDialog() != AlteraNomeBloco.CANCEL) {
            String NovoNome = mn.getNome();
            
            if (StringUtilities.isEmpty(NovoNome)) {
              NovoNome = "Block" + nId; //$NON-NLS-1$
            } else {
              ntmp = NovoNome.indexOf(stmp);
              if (ntmp == -1) {
                NovoNome += stmp;
              }
            }
            
            ic.Mudar_Nome(NovoNome);
            ic.refresh(10, 10, this);
            setFlowChanged(true);
          }
          if (!rect_activo)
            comp.Muda_Activo(false);
          else
            a_mover_rect = false;
        }
      }
      if (!rect_activo)
        activo.clear();
    }
  }

  public void removeBlock() {
    if(this.isEditable()) {
      /* eliminar os componentes */
      for (int i = 0; i < activo.size(); i++)
        removeComponente((Componente) activo.get(i));
      if (activo.size() > 0) {
        setFlowChanged(true);
        undo.add(activo);
        activo = new ArrayList<Componente>();
      }
      repaint();
    }
  }

  public void changeBlockAttributes() {
    if(this.isEditable()) {
      /* altera atributos */
      for (int i = 0; i < activo.size(); i++)
        activo.get(i).alteraAtributos(this, janela);
      repaint();
    }
  }

  public String getFlowName() {
    return this.flowName;
  }

  public String getFlowId() {
    return this.flowId;
  }
  
  public void setFlowId(String flowid) {
    this.flowId = flowid;
  }
  
  public String getPNumber() {
    return this.pnumber;
  }
  
  public void setPNumber(String pnumber) {
    this.pnumber = pnumber;
  }

  public byte [] getFlowData() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      GravarFicheiro.saveFlow(this, this.flowId, this.flowName, baos);
    } catch (Exception e) {
      FlowEditor.log("error", e);
      new Erro(Messages.getString("Desenho.error.serialization", this.flowId, e.getLocalizedMessage()), this.janela); //$NON-NLS-1$
      return null;
    }
    return baos.toByteArray();
  }

  /**
   * Return component name to paint tab.
   */
  public String getName() {
    String vTag = "";
    if(flowVersion != -1) vTag = " (v"+this.flowVersion+")";
    return this.flowName+vTag;
  }

  public void uploadFlow() {
    UploadFlowDialog.openDialog(this, true);
  }

  public void uploadSubFlow() {
    UploadFlowDialog.openDialog(this, false);
  }

  public void renameFlow() {
    AlteraNomeFluxo mn = new AlteraNomeFluxo(janela, this.flowId, this.flowName);
    if(mn.openDialog() != AlteraNomeFluxo.CANCEL) {
      String newId = mn.getId();
      boolean modified = false;
      if(StringUtilities.isNotEmpty(newId) && !this.flowId.equals(newId)) {
        RepositoryClient rep = janela.getRepository();
        boolean fileExists = false;
        if(rep.checkConnection()) {
          // test if file exists in repository
          if (rep.hasExtendedAPI()) {
            FlowInfo finfo = rep.getFlowInfo(newId);
            fileExists = (finfo != null);
          } else {
            String[] flows = rep.listFlows(); // hard way: brute force
            for (String flow : flows) {
              if (StringUtilities.isEqual(flow, newId)) {
                fileExists = true;
                break;
              }
            }
          }
        }

        if (fileExists) {
          FlowEditor.log("Flow exists. Please confirm."); //$NON-NLS-1$
          Object [] options = new Object[]{ Mesg.Sim, Mesg.Nao };
          int opt = JOptionPane.showOptionDialog(janela, Messages.getString("Desenho.confirm.flowExists"),  //$NON-NLS-1$
              Messages.getString("Desenho.title_warning"), JOptionPane.YES_NO_OPTION,  //$NON-NLS-1$
              JOptionPane.WARNING_MESSAGE, (Icon) null, options, options[1]);
          if (opt != JOptionPane.OK_OPTION) {
            return;
          }
        }
        this.flowId = newId;
        modified = true;
      }

      String newName = mn.getNome();
      if(StringUtilities.isNotEmpty(newName) && !this.flowName.equals(newName)) {
        this.flowName = mn.getNome();
        modified = true;
      }

      if(modified) {
        setFlowChanged(modified);
        updateTitle();
      }
    }
  }

  protected void checkAutomaticComponents() {
    // gets all block names
    HashSet<String> hsBlocks = new HashSet<String>();
    Iterator<InstanciaComponente> itComponentes = componentList.iterator();
    while (itComponentes.hasNext()) {
      InstanciaComponente ic = itComponentes.next();
      hsBlocks.add(ic.C_B.Nome);
    }

    // Add missing components
    ArrayList<Componente> aux = new ArrayList<Componente>();
    int incr = 50;
    String [] libs = _librarySet.getLibraryKeys();
    for (String libName : libs) {
      Iterator<Componente_Biblioteca> it = _librarySet.getLibrary(libName).getAllComponents();
      while (it.hasNext()) {
        Componente_Biblioteca cb = it.next();
        if (cb.isAutomatic() && !hsBlocks.contains(cb.Nome)) {
          InstanciaComponente comp = setComponent(cb, incr, incr);
          incr += 50;
          aux.add(comp);
        }
      }
    }

    verificaCoordenadasNegativas(aux);
    setFlowChanged(false); // reset 'alterou' flag
  }

  protected void importFlowFile(File fileName) {
    this.janela.aEspera();
    /* ler ficheiro de entrada */
    try {
      this.importFlow(LerFicheiro.readFlow(this, fileName));
    } catch (Exception e) {
      throw new InvalidFlowException(Messages.getString("Desenho.error.invalidFlowData", fileName.getName()),e); //$NON-NLS-1$
    }
    this.janela.normal();
  }

  protected void importFlowData(byte [] data, String altName) {
    this.janela.aEspera();
    /* ler ficheiro de entrada */
    try {
      FlowInfo finfo = janela.getRepository().getFlowInfo(altName);
      this.importFlow(LerFicheiro.readFlow(this, data, altName, finfo));
    } catch (Exception e) {
      throw new InvalidFlowException(Messages.getString("Desenho.error.invalidFlowData", "iFlow"),e); //$NON-NLS-1$ //$NON-NLS-2$
    }
    this.janela.normal();
  }

  protected void importFlow(Ficheiro ficheiro) {
    this.componentList = ficheiro.componentes;
    this.lineList = prepararLinhas(ficheiro.linhas);
    this.flowId = ficheiro.name;//+"."+Mesg.FileExtension;
    this.flowName = ficheiro.description;
    this.setBlockNumber(ficheiro.nextBlockNum);

    this.checkAutomaticComponents();
    this.setFlowChanged(false);
    this.updateTitle();
    this.janela.repaint();
  }

  protected List<Linha> prepararLinhas(ArrayList<Linha> linhas) {
    List<Linha> retObj = new ArrayList<Linha>();
    for (Linha linha : linhas) {
      boolean insere = true;
      for (int i = 0; i < retObj.size(); i++) {
        Linha item = retObj.get(i);
        if (linha.p2.equals(item.p2)) {
          retObj.set(i, linha);
          insere = false;
          break;
        }
      }
      if (insere) {
        retObj.add(linha);
      }
    }
    return retObj;
  }
  
  /*****************************************************************************
   * Funcao que grava o Ficheiro actual
   */
  public void exportFlowToFile(File flowFile) {
    janela.aEspera();

    try {
      OutputStream out = new BufferedOutputStream(new FileOutputStream(flowFile)); 
      GravarFicheiro.saveFlow(this, this.flowId, this.flowName, out);
      out.close();
      setFlowChanged(false);
    } catch(Exception e) {
      FlowEditor.log("error", e);
      new Erro(Mesg.ErroGravarFicheiro, janela);
    }
    janela.normal();
    updateTitle();
  }

  /*****************************************************************************
   * Funcao que trata das accoes da janela
   */
  public void actionPerformed(ActionEvent evt) {
  }

  /*****************************************************************************
   * funcao que cria novo projecto
   */
  protected void newFlow() {

    componentList = new ArrayList<InstanciaComponente>();
    lineList = new ArrayList<Linha>();

    janela.normal();

    this.checkAutomaticComponents();
    updateCanvas();
  }

  /*****************************************************************************
   * adiciona os componentes a janela - desenho, componentes de biblioteca
   * menus, toolbar
   */
  protected void updateCanvas() {
    /* config */
    updateTitle();
    setFlowChanged(false);
    revalidate();
  }

  /*****************************************************************************
   * Alterar variavel que diz se o circuito foi alterado (altera o nome do
   * circuito)
   */
  public void setFlowChanged(boolean val) {
    if(this.isEditable()) {
      flowChanged = val;
      updateTitle();
      if (val == false)
        updateCanvasSize();
    }
  }

  /*****************************************************************************
   * acerta nome da janela
   */
  protected void updateTitle() {
    if(this.isEditable()) {
      String name = this.getName();
      String flowDesc = name + " [" + this.flowId + "]"; //$NON-NLS-1$ //$NON-NLS-2$
      flowDesc += (flowChanged?" * ":"   ");//$NON-NLS-1$ //$NON-NLS-2$
      
      String tabTitle = (flowChanged?"*":"")+name; //$NON-NLS-1$ //$NON-NLS-2$
      janela.setTabTitle(scrollPane, tabTitle, flowDesc);
    }
  }

  /*****************************************************************************
   * Da o valor da variavel que diz se o circuito foi alterado
   */
  public boolean isFlowChanged() {
    return flowChanged;
  }

  /*****************************************************************************
   * actualiza o tamanho do desenho
   */
  public void updateCanvasSize() {
    updateTitle();

    /* calcular tamanho do desenho */
    Dimension dim = janela.getSize();
    dim.width = dim.width - janela.getLibraryWidth();
    dim.height = dim.height;

    Dimension dim2 = DimensaoDesenho();
    Dimension dim3 = getSize();

    dim2.width = Math.max(dim2.width + 50, dim.width - 15);

    dim2.height = Math.max(dim2.height + 50, dim.height - 50);

    /* modificar tamanho do canvas */
    if (dim2.width != dim3.width || dim2.height != dim3.height) {
      setPreferredSize(dim2);
      setSize(dim2.width, dim2.height);
      setMinimumSize(dim2);

    }
  }

  /*****************************************************************************
   * calcula dimensao do desenho
   */
  protected Dimension DimensaoDesenho() {
    /* calcular tamanho do desenho */
    Dimension dim2 = new Dimension();

    for (int i = 0; i < componentList.size(); i++) {
      InstanciaComponente c = (InstanciaComponente) componentList.get(i);

      if (dim2.width < c.Posicao_X + c.C_B.Largura_X + 20)
        dim2.width = c.Posicao_X + c.C_B.Largura_X + 20;
      if (dim2.height < c.Posicao_Y + c.C_B.Largura_Y + 20)
        dim2.height = c.Posicao_Y + c.C_B.Largura_Y + 20;
    }
    for (int i = 0; i < lineList.size(); i++) {
      Linha linha = (Linha) lineList.get(i);
      for (int aux = 0; aux < linha.numberPoints(); aux++) {
        Point p = linha.getPoint(aux);
        if (dim2.width < p.x)
          dim2.width = p.x;
        if (dim2.height < p.y)
          dim2.height = p.y;
      }
    }
    return dim2;
  }

  /*****************************************************************************
   * imprime
   */
  public void Printing(String accao) {
    /* altera o actual formato das paginas */
    if (accao.equals(Mesg.MenuPageFormat)) {
      PrinterJob job = PrinterJob.getPrinterJob();
      pageFormat = job.pageDialog(job.defaultPage());
    } else

      /* imprime com o actual formato de pagina */
      if (accao.equals(Mesg.MenuPrint)) {
        JComponentVista vista;
        PrinterJob job = PrinterJob.getPrinterJob();

        /* criar informacao auxiliar para imprimir */
        vista = new JComponentVista(this, pageFormat, DimensaoDesenho());
        vista.SET();
        job.setPageable(vista);
        Color fundoBU = getPalette().getColor(Colors.Fundo);
        Color componenteBU = getPalette().getColor(Colors.Componente);
        try {
          /* imprimir */
          if (job.printDialog()) {
            /* cores para imprimir */
            getPalette().setColor(Colors.Fundo, Color.white);
            getPalette().setColor(Colors.Componente, Color.black);
            job.print();
          }
        } catch (PrinterException e) {
          new Erro(e.getMessage(), janela);
        } finally {
          getPalette().setColor(Colors.Fundo, fundoBU);
          getPalette().setColor(Colors.Componente, componenteBU);
        }
      }
  }


  /** ********************************** */
  void searchComponenteBibilioteca() {
    EncontraComponente aa = new EncontraComponente(janela, true, _librarySet);
    if (aa.compBib != null) {
      Point Vpos = scrollPane.getViewport().getViewPosition();

      setComponent(aa.compBib, 100 + Vpos.x, 100 + Vpos.y);
    }

  }

  void searchBlockByName() {
    doSearchBlock(new SearchBlockByName());
  }

  void searchBlockByContents() {
    doSearchBlock(new SearchBlockByContents());
  }

  void doSearchBlock(BlockSearchInterface filter) {
    BlockFinder bf = new BlockFinder(janela, filter);
    bf.setVisible(true);
  }

  protected void gotoComponente(InstanciaComponente ic) {
    if (null == ic)
      return;
    Point icPos = new Point(ic.Posicao_X, ic.Posicao_Y);
    gotoPoint(icPos);

    // "bem vindos ah code replication 2007"
    for (int i = 0; i < activo.size();) {
      activo.remove(0).Muda_Activo(false);
    }

    activo.add(ic);
    ic.Muda_Activo(true);
    a_escolher = false;
    rect_activo = true;
    ic.activo = true;
    repaint();
  }

  /*****************************************************************************
   * Retira pontos de quebra das linhas
   */
  static public void retiraPontosQuebra(ArrayList<Linha> linhas) {
    for (int i = 0; i < linhas.size(); i++) {
      Linha l = linhas.get(i);
      while (l.numberPoints() > 2)
        l.removePoint(1);
    }
  }

  static public void inserePontosQuebra(ArrayList<Linha> linhas) {
    retiraPontosQuebra(linhas);
    for (int i = 0; i < linhas.size(); i++) {
      Linha l = (Linha) linhas.get(i);

      Point p1 = l.getPoint(0);
      Point p2 = l.getPoint(1);
      Point p3 = new Point((p1.x + p2.x) / 2, p1.y);
      Point p4 = new Point((p1.x + p2.x) / 2, p2.y);

      l.setPoint(1, p3);
      l.setPoint(2, p4);
    }
  }

  /*****************************************************************************
   * DRAG AND DROP
   */

  /*****************************************************************************
   * arrasta componente
   */
  void DRAG(int x, int y, Componente_Biblioteca cb) {
    if(this.isEditable()) {
      if (cb == null && cb == cbEscolhido)
        return;
      cbEscolhido = cb;
      
      /* acerta posicao */
      Point Vpos = scrollPane.getViewport().getViewPosition();
      Dimension Vdim = scrollPane.getSize();
      x = x + Vdim.width + Vpos.x;
      y = y + Vpos.y;
      cbEscolhidoPoint.x = x;
      cbEscolhidoPoint.y = y;
      repaint();
    }
  }

  /*****************************************************************************
   * liberta componente
   */
  void DROP(int x, int y, Componente_Biblioteca cb) {
    if(this.isEditable()) {
      if ((cb == null) && (cb == cbEscolhido))
        return;
  
      /* acerta posicao */
      Point Vpos = scrollPane.getViewport().getViewPosition();
      Dimension Vdim = scrollPane.getSize();
      x = x + Vpos.x + Vdim.width;
      y = y + Vpos.y;
      cbEscolhidoPoint.x = x;
      cbEscolhidoPoint.y = y;
      cbEscolhido = null;
  
      /* cria componente e coloca-o NO flow */
      InstanciaComponente comp = setComponent(cb, x, y);
      updateCanvasSize();
      ArrayList<Componente> aux = new ArrayList<Componente>();
      aux.add(comp);
      verificaCoordenadasNegativas(aux);
    }
  }

  /*****************************************************************************
   * verifica se exite algum componente com coordenas negativas.em caso positivo
   * desloca todos os componentes
   */
  void verificaCoordenadasNegativas(List<Componente> activos) {
    /* verificar se existe coordendada <0 */
    int DX = 0;
    int DY = 0;

    /* verifica apenas os seleccionados */
    for (int j = 0; j < activos.size(); j++) {

      Componente comp = (Componente) activos.get(j);
      if (comp instanceof Linha) {
        Linha l = (Linha) comp;
        for (int k = 0; k < l.numberPoints(); k++) {
          Point pt = l.getPoint(k);
          DX = Math.min(pt.x, DX);
          DY = Math.min(pt.y, DY);
        }
      } else {
        InstanciaComponente ic = (InstanciaComponente) comp;
        DX = Math.min(ic.Posicao_X, DX);
        DY = Math.min(ic.Posicao_Y, DY);
      }
    }

    /*
     * verifica se existem coordenadas negativas e desloca TODOS os componentes
     */
    if (DX < 0 || DY < 0) {
      for (int j = 0; j < componentList.size(); j++) {
        InstanciaComponente comp = (InstanciaComponente) componentList.get(j);
        comp.Posicao_X -= DX;
        comp.Posicao_Y -= DY;
      }
      for (int j = 0; j < lineList.size(); j++) {
        Linha l = (Linha) lineList.get(j);
        for(int n = 0; n < l.numberPoints(); n++) {
          Point pt = l.getPoint(n);
          pt.x -= DX;
          pt.y -= DY;
        }
      }
      if (rect_activo) {
        Rectangulo.c1 -= DX;
        Rectangulo.n1 -= DY;
        Rectangulo.c2 -= DX;
        Rectangulo.n2 -= DY;
      }

      /* actualiza janela */
      Point Vpos = scrollPane.getViewport().getViewPosition();
      FlowEditor.log("Vpos=" + Vpos.x + "," + Vpos.y); //$NON-NLS-1$ //$NON-NLS-2$
      FlowEditor.log("Dxy=" + DX + "," + DY); //$NON-NLS-1$ //$NON-NLS-2$
      scrollPane.getViewport().setViewPosition(new Point(Vpos.x - DX, Vpos.y - DY));

      updateCanvasSize();
      repaint();
    }
  }

  /**
   * "Move" objects to create a greater scroll area. All arguments are
   * increments
   * 
   * @param t
   * @param l
   * @param b
   * @param r
   */
  protected void scrollObjects(int t, int l, int b, int r) {
    Dimension dim = getSize();

    dim.height += b;
    dim.width += r;

    if (t != 0 || l != 0) {
      // Move all objects
      for (InstanciaComponente componente : componentList) {
        componente.Posicao_X += t;
        componente.Posicao_Y += l;
      }

      dim.height += t;
      dim.width += l;
    }

    setSize(dim);
  }

  protected class SearchBlockByName implements BlockSearchInterface {
    public InstanciaComponente[] search(String name) {
      if (StringUtilities.isEmpty(name)) {
        InstanciaComponente[] comps = componentList.toArray(new InstanciaComponente[componentList.size()]);
        Arrays.sort(comps);
        return comps;
      }

      TreeSet<InstanciaComponente> componentes = new TreeSet<InstanciaComponente>();
      name = name.toLowerCase();

      for (InstanciaComponente componente : componentList) {
        String blockName = componente.Nome.toLowerCase();
        String blockType = componente.C_B.Nome.toLowerCase();
        if (blockName.contains(name) || blockType.contains(name))
          componentes.add(componente);
      }

      return componentes.toArray(new InstanciaComponente[componentes.size()]);
    }

    public void gotoBlock (InstanciaComponente ic) {
      gotoComponente(ic);
    }
  }

  protected class SearchBlockByContents implements BlockSearchInterface {
    public InstanciaComponente[] search(String name) {
      if (StringUtilities.isEmpty(name)) {
        InstanciaComponente[] comps = componentList.toArray(new InstanciaComponente[componentList.size()]);
        Arrays.sort(comps);
        return comps;
      }

      TreeSet<InstanciaComponente> componentes = new TreeSet<InstanciaComponente>();
      name = name.toLowerCase();

      for (InstanciaComponente componente : componentList) {

        for (Atributo atributo : componente.getAtributos()) {
          String nome = atributo.getNome().toLowerCase();
          String valor = atributo.getValor().toLowerCase();
          if (nome.contains(name) || valor.contains(name)) {
            componentes.add(componente);
            break;
          }
        }
      }

      return componentes.toArray(new InstanciaComponente[componentes.size()]);
    }
    public void gotoBlock (InstanciaComponente ic) {
      gotoComponente(ic);
    }
  }

  protected void doAutoScroll(int x, int y) {
    JViewport viewport = scrollPane.getViewport();
    if (viewport == null)
      return;
    Point viewPos = viewport.getViewPosition();
    Dimension viewSize = viewport.getSize();
    Dimension canvasSize = getSize();

    // FlowEditor.log("Cursor location = [x="+x+",y="+y+"] View location =
    // "+viewPos+" Canvas size = "+canvasSize+" View size = "+viewSize);

    // cursor location is relative to canvas size. Must get viewport relative
    // position.
    int relPosX = Math.abs(x - viewPos.x);
    int relPosY = Math.abs(y - viewPos.y);
    // FlowEditor.log("Relative position = "+relPosX+","+relPosY);

    // border size is 20
    int newX = viewPos.x;
    int newY = viewPos.y;

    boolean xdone = false;
    boolean ydone = false;

    // test if close to left border
    if (relPosX < borderSize) {
      int t = Math.min(borderSize, borderSize - relPosX);
      newX = Math.max(viewPos.x - t, 0); // scroll faster if closer to the
      // border
      // FlowEditor.log(newX);
      xdone = true;
    }

    // test top border
    if (relPosY < borderSize) {
      int t = Math.min(borderSize, borderSize - relPosY);
      newY = Math.max(viewPos.y - t, 0); // scroll faster if closer to the
      // border
      // FlowEditor.log(newY);
      ydone = true;
    }

    // test right border (if left unchanged)
    if (!xdone && relPosX > viewSize.width - borderSize) {
      int t = Math.min(borderSize, relPosX - (viewSize.width - borderSize));
      // FlowEditor.log(t);
      newX = viewPos.x + t;
      xdone = true;

      // check if we have gone too far
      newX = Math.min(canvasSize.width - viewSize.width, newX);
    }

    // test bottom border (if left unchanged)
    if (!ydone && relPosY > viewSize.height - borderSize) {
      int t = Math.min(borderSize, relPosY - (viewSize.height - borderSize));
      // FlowEditor.log(t);
      newY = viewPos.y + t;
      ydone = true;

      // check if we have gone too far
      newY = Math.min(canvasSize.height - viewSize.height, newY);
    }

    // FlowEditor.log("new [x="+newX+",y="+newY+"]");

    if (viewPos.x != newX || viewPos.y != newY) { // value changed, scroll!
      // Move
      viewport.setViewPosition(new Point(newX, newY));
    }
  }

  // Scrollable method implementation

  public Dimension getPreferredScrollableViewportSize() {
    return scrollPane.getPreferredSize();
  }

  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    return 1;
  }

  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    return 15;
  }

  public List<InstanciaComponente> getComponentList() {
    return componentList;
  }
  public Collection<Atributo> getCatalogue() {
    return Collections.unmodifiableList(catalogue);
  }
  public void newCatalog() {
    catalogue.clear();
    catalogueMap.clear();
  }

  public boolean addCatalogVariable(String nome, String valor, boolean isSearchable, String publicName, String tipo, String format) {
    Atributo newAttr = new AtributoImpl();
    newAttr.setNome(nome);
    newAttr.setInitValue(valor);
    newAttr.setSearchable(isSearchable);
    newAttr.setPublicName(publicName);
    newAttr.setDataType(tipo);
    newAttr.setFormat(format);

    return addCatalogVariable(newAttr);
  }
  
  public boolean addCatalogVariable(Atributo variable) {
    return addCatalogVariable(variable, false);
  }
  /**
   * Add variable to catalog
   * 
   * @param variable variable to add
   * @param check check if variable exists
   * @return true if variable added/updated false otherwise or if check is true and variable exists
   */
  public boolean addCatalogVariable(Atributo variable, boolean check) {
    if(null == variable || StringUtilities.isEmpty(variable.getNome())) return false;

    // Isto tudo para manter a ordem. Vale a pena?
    if(catalogueMap.containsKey(variable.getNome())) {
      if(check) return false;
      catalogue.set(catalogueMap.get(variable.getNome()), variable);
    } else {
      catalogue.add(variable);
      catalogueMap.put(variable.getNome(), catalogue.indexOf(variable));
    }
    return true;
  }

  public void removeCatalogVariable(String variableName) {
    if(StringUtilities.isEmpty(variableName)) return;
    if(catalogueMap.containsKey(variableName)) {
      Integer position = catalogueMap.remove(variableName);
      catalogue.remove(position);
    }
  }

  public LibrarySet getLibrarySet() {
    return _librarySet;
  }

  public int getBlockNumber() {
    return this._blockNumber;
  }

  protected void setBlockNumber(int num) {
    this._blockNumber = num;
  }

  public int incBlockNumber() {
    return _blockNumber++;
  }

  public void gotoLineStart() {
    for(Componente c : activo) {
      if (c.E_Linha()) {
        Linha l = (Linha) c;
        gotoPoint(l.p2);
        break;
      }
    }
    repaint();
  }

  public void gotoLineEnd() {
    for(Componente c : activo) {
      if (c.E_Linha()) {
        Linha l = (Linha) c;
        gotoPoint(l.p1);
        break;
      }
    }
    repaint();
  }

  protected void gotoPoint(Point p) {
    if(null == p) return;
    p = new Point(p);// backup point...
    JViewport viewport = scrollPane.getViewport();
    Rectangle compDim = this.getBounds();
    Dimension dim = viewport.getSize();


    int halfWidth = dim.width / 2;
    int halfHeight = dim.height / 2;

    p.translate(-halfWidth, -halfHeight);

    if (!compDim.contains(p)) {
      if (p.x > compDim.width)
        p.x = compDim.width - dim.width;
      if (p.y > compDim.height)
        p.y = compDim.height - dim.height;
      if (p.x < 0)
        p.x = 0;
      if (p.y < 0)
        p.y = 0;
    }

    viewport.setViewPosition(p);
  }

  public File getLastParent() {
    return lastParent;
  }

  public void setLastParent(File lastParent) {
    this.lastParent = lastParent;
  }


  public InstanciaComponente getBlockStart() {
    for(InstanciaComponente i : componentList)
      if(i.isStart()) return i;
    return null;
  }

  public Map<String,String> getFormTemplates() {
    return Collections.unmodifiableMap(formTemplates);
  }

  /**
   * Set/unset a form template.
   * Remove a template if atributos parameter is null or empty.
   * 
   * @param name Template name
   * @param form Template attributes
   * @return Previous template
   */
  public String setFormTemplate(String name, String form) {
    // TODO notificar mudanca de estado
    // desenho.setFlowChanged(true);
    if(null == name) return null;
    if(StringUtils.isBlank(form)) return formTemplates.remove(name); 
    return formTemplates.put(name, form);
  }

  /**
   * Set/unset a form template.
   * Remove a template if atributos parameter is null or empty.
   * 
   * @param name Template name
   * @param atributos Template attributes
   * @return Previous template
   */
  public String getFormTemplate(String name) {
    if(null == name) return null;
    return formTemplates.get(name);
  }

  void templateManager() {
    TemplateManager tm = new TemplateManager(janela, this);
    tm.setVisible(true);
  }
  
  public boolean isEditable() {
    return editable;
  }
  
  public void setEditable(boolean isEditable) {
    this.editable = isEditable;
  }
  
  public Cor getPalette() {
    if(this.palette == null) {
      this.palette = new Cor();
    }
    return this.palette;
  }
  
  public void setPalette(Cor newPalette) {
    this.palette = newPalette;
  }

  public String[] getFormTemplatesList() {
    ArrayList<String> resultAux = new ArrayList<String>();

    for (InstanciaComponente comp : componentList) {
      if (comp.C_B.Nome.equals("BlockFormTemplate"))
        resultAux.add(comp.Nome);
    }
    String[] result = resultAux.toArray(new String[resultAux.size()]);
    return result;
  }
}
