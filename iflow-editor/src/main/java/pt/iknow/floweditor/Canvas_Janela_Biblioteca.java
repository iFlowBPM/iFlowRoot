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
 *  class: Canvas_Janela_Biblioteca
 *
 *  desc: painel que contem os componentes de uma
 *        biblioteca
 *
 ****************************************************/

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang.StringUtils;

import pt.iknow.floweditor.Cor.Colors;


/*****************************
 * Canvas_Janela_Biblioteca
 */
public class Canvas_Janela_Biblioteca extends JPanel {
  private static final long serialVersionUID = -1573488016453008731L;
  
  private int DELTA_X=40;
  private int DELTA_Y=40;

  Library biblioteca;
  Janela janela;
  JScrollPane sp=null;


  int tamX=0;
  private int tamY=0;
  private int tamYs=0;


  /****/
  Componente_Biblioteca cbEscolhido=null;
  Point p=new Point();

  /******************************
   * Construtor_:
   *		associa a biblioteca
   *		associa eventos
   * @param b biblioteca
   * @param d Desenho (canvas da jenela principal)
   * @param jb janela a qual pertence este 'canvas'
   */
  public Canvas_Janela_Biblioteca(Library b,Janela janela) {
    setBackground(Cor.getInstance().getColor(Colors.Fundo));
    biblioteca=b;
    this.janela = janela;

    /* associar accoes dos botoes  rato */
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        MmouseDown( e.getX(),e.getY());
      }
      public void mouseReleased(MouseEvent e) {
        MmouseUp( e.getX(),e.getY());
      }
    });
    addMouseMotionListener(new MouseMotionAdapter()  {
      public void mouseDragged(MouseEvent e) {
        MmouseDrag( e.getX(),e.getY());
      }
    });

    setBackground(Cor.getInstance().getColor(Colors.Fundo));
    sp = new JScrollPane(this);
    sp.setBackground(Cor.getInstance().getColor(Colors.Fundo));
    sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    sp.getVerticalScrollBar().setUnitIncrement(10); // skip pixels per scroll
    calculaTamanho();
    this.janela.repaint();
  }


  /***************************************
   * calcula o tamanho da area de desenho 
   */   
  private boolean calculaTamanho() {
    boolean ret=false;

    tamX=200; // hint: at least 200 px wide
    tamY=0;
    tamYs=0;

    Iterator<Componente_Biblioteca> it = biblioteca.getAllComponents();
    int itemsToAccount = 0;
    while (it.hasNext()) {
      Componente_Biblioteca cb = it.next();
      if(cb.isAutomatic()) continue; // ignore automatic components
      
      itemsToAccount++;
      if (cb.Largura_X==-1 || cb.Largura_Y==-1) {
        int _x=cb.funcao_Desenho.getWidth(janela);
        int _y=cb.funcao_Desenho.getHeight(janela);
        cb.Largura_X=_x;
        cb.Largura_Y=_y;
        if (cb.Largura_X==-1 || cb.Largura_Y==-1) {
          ret=true;

        }
      }
      
      String descr = cb.Descricao;
      if(StringUtils.isNotBlank(cb.descrKey))
        descr = janela.getBlockMessages().getString(cb.descrKey);
      
      tamX=Math.max(tamX,cb.Largura_X);
      tamX=Math.max(tamX,Janela.FM_Tipo_10.stringWidth(descr));
      tamY=Math.max(tamY,cb.Largura_Y);
    }

    tamX+=DELTA_X;
    tamY+=DELTA_Y;
    tamYs=tamY;
    tamY *= itemsToAccount;

    /* size */
    Dimension newSize=new Dimension(tamX,tamY);
    if (tamY<janela.getSize().height)
      newSize.height=janela.getSize().height;


    /* actualizar tamanho do component */
    if (!(newSize.height==getPreferredSize().height)) {
      setPreferredSize(newSize);
      setSize(newSize);
      setMinimumSize(newSize);
      setMaximumSize(newSize);
    }

    return ret;
  }


  /***********************************************************************
   * funcao que desenha os diversos componentes que estao na biblioteca
   */
  public void paintComponent(Graphics g) {
    try {
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, janela.isAntiAlias()?RenderingHints.VALUE_ANTIALIAS_ON:RenderingHints.VALUE_ANTIALIAS_OFF);
    } catch (Throwable t) {
    }

    boolean toRepaing = calculaTamanho();
    int p_x = 0;
    int p_y = 0;

    /* desenhar fundo */
    g.setColor(Cor.getInstance().getColor(Colors.Fundo));
    g.fillRect(0, 0, Math.max(tamX, 2 * sp.getSize().width), Math.max(tamY, 2 * sp.getSize().height));
    g.setFont(Cor.getInstance().getFont());

    /* desenhar quadrado e componentes */
    g.setColor(Cor.getInstance().getColor(Colors.Componente));

    Iterator<Componente_Biblioteca> it = biblioteca.getAllComponents();
    while (it.hasNext()) {
      Componente_Biblioteca cb = it.next();
      if(cb.isAutomatic()) continue; // ignore automatic components

      g.drawRect(p_x, p_y, sp.getSize().width, tamYs);
      cb.pinta(g, getSize().width / 2 - cb.Largura_X / 2, p_y + (tamYs) / 2 - cb.Largura_Y / 2, Cor.getInstance().getColor(Colors.Componente), Cor.getInstance().getColor(Colors.Fundo), this);
      p_y += tamYs;
    }

    /* desenhar componente arrastado */
    if (cbEscolhido != null) {
      g.setColor(Cor.getInstance().getColor(Colors.Activo));
      cbEscolhido.pinta(g, p.x, p.y, Cor.getInstance().getColor(Colors.Activo), Cor.getInstance().getColor(Colors.CompSelected), this);
    }

    if (toRepaing) {
      repaint();
    }

  }


  /***************************************************************************
   * Evento de carrega com o rato : descobrir se carregou num componente e se
   * tal acontecer coloca-lo na janela principal
   */
  public boolean MmouseDown(int x, int y) {
    cbEscolhido=null;
    Dimension dim = sp.getSize();
    if (x < 0 || x > dim.width)
      return false;
    int aux=y/tamYs;
    
    

    int i = 0;
    Iterator<Componente_Biblioteca> it = biblioteca.getAllComponents();
    while (it.hasNext()) {
      Componente_Biblioteca cb = it.next();
      if(cb.isAutomatic()) continue; // ignore automatic components
      if (aux == i) {
        cbEscolhido=cb;
        break;
      }
      i++;
    }

    p.x=x;
    p.y=y;
    return false;
  }


  /***********************************
   * largar o componente 
   */    
  public boolean MmouseUp( int x,int y) {
    Desenho d = janela.getSelectedDesenho();
    if(null == d) return false;

    /* verificar se largado dentro da janela de flow */
    if (x<0) {
      Point Vpos=sp.getViewport().getViewPosition();
      d.DROP(x-Vpos.x,y-Vpos.y+2,cbEscolhido);
    }
    else
      d.DRAG(x,y,null);
    cbEscolhido=null;
    repaint();
    return false;
  }

  /******************************++
   * arrastar componente
   */
  public boolean MmouseDrag( int x,int y) {
    Desenho d = janela.getSelectedDesenho();
    if(null == d) return false;

    /* janela de flow */
    if (x<0) {
      Point Vpos=sp.getViewport().getViewPosition();
      d.DRAG(x-Vpos.x,y-Vpos.y+2,cbEscolhido);
    }
    else
      d.DRAG(x,y,null);

    p.x=x;
    p.y=y;
    repaint();
    return false;
  }


  /******************************************************************
   * indica quando as imagens estão carregadas
   */
  public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
    repaint();
    return true;
  }

  //Append to the text area and make sure the new text is visible.
  void eventOutput(String eventDescription, MouseWheelEvent e) {
    FlowEditor.log(eventDescription);
  }


}
