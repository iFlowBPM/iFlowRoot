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
 *  class: Instancia_Componente
 *
 *  desc: bloco simples
 *
 ****************************************************/

import java.awt.Graphics;
import java.awt.Point;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iknow.floweditor.Cor.Colors;
import pt.iknow.floweditor.blocks.AbstractAlteraAtributos;
import pt.iknow.floweditor.blocks.AlteraAtributosInterface;
import pt.iknow.floweditor.messages.Messages;
import pt.iknow.utils.StringUtilities;

/*******************************************************************************
 * Instancia_Componente
 */
public class InstanciaComponente extends Componente implements Comparable<InstanciaComponente> {

  /* componente de biblioteca */
  Componente_Biblioteca C_B;

  /* posicao grafica */
  int Posicao_X;
  int Posicao_Y;
  int nivel = 0;

  /* nome */
  public int ID;
  public String Nome;

  /* atributos */
  private ArrayList<Atributo> atributos = new ArrayList<Atributo>();
  private Map<String,Atributo> attrMap = new HashMap<String,Atributo>();

  public InstanciaComponente clone(Desenho desenho) {
    InstanciaComponente retObj = C_B.cria(desenho);
    if(!StringUtilities.isEqual(Mesg.Comp+ID, Nome)) // is not autogen name
      retObj.Nome = Nome.replace("(" + ID + ")", "(" + retObj.ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    retObj.atributos = new ArrayList<Atributo>();
    for (Atributo item : this.atributos) {
      Atributo attr = item.cloneAtributo();
      retObj.atributos.add(attr);
    }

    return retObj;

  }

  public void destroi() {
    if(this.isEditable()) {
      Nome = null;
      // Pontos_Entradas.removeAllElements();
      // Pontos_Saidas.removeAllElements();
      super.destroi();
    }
  }

  /*****************************************************************************
   * Construtor vazio
   */
  public InstanciaComponente() {
  }

  /*****************************************************************************
   * Inicia apenas os pontos de entrada/saida
   */
  public InstanciaComponente(Componente_Biblioteca C_B) {
    Inicia_Pontos(C_B);
  }

  /*****************************************************************************
   * Inicia o componente por completo
   */
  public InstanciaComponente(Componente_Biblioteca cb, String nome, int id) {
    C_B = cb;
    Nome = nome;
    this.ID = id;

    /* iniciar vectores */
    lista_estado_saidas = new ArrayList<List<Conector>>();
    lista_estado_entradas = new ArrayList<List<Conector>>();

    /* iniciar lista de entradas */
    for (int i = 1; i <= C_B.Num_Entradas; i++)
      lista_estado_entradas.add(new ArrayList<Conector>());
    /* inciar lista de saidas e estados das saidas */
    for (int i = 1; i <= C_B.Num_Saidas; i++) {
      ArrayList<Conector> _v = new ArrayList<Conector>();
      lista_estado_saidas.add(_v);
    }
    /* posicao */
    Posicao_X = 100;
    Posicao_Y = 100;
    /* pontos */
    Inicia_Pontos(C_B);
  }

  /*****************************************************************************
   * Inicia as variaveis relativas as posicao dos pontos de entradas e saidas
   */
  protected void Inicia_Pontos(Componente_Biblioteca C_B) {
    /* atributos */
    for (int aux = 0; aux < C_B.NomesAtributos.size(); aux++) {

      Atributo attr = new AtributoImpl(C_B.NomesAtributos.get(aux));
      atributos.add(attr);
      attrMap.put(attr.getNome(), attr);
    }
  }

  public Atributo getAtributo(String name) {
    return attrMap.get(name);
  }

  public void addAtributo(Atributo atr) {
    this.atributos.add(atr);
    attrMap.put(atr.getNome(), atr);
  }

  public List<Atributo> getAtributos() {
    return this.atributos;
  }
  
  /*****************************************************************************
   * Funcao para desenhar o componente
   */
  public void paint(Graphics g) {
    /* desenhar componente */
    if (activo)
      C_B.pinta(g, Posicao_X, Posicao_Y, getPalette().getColor(Colors.Activo), getPalette().getColor(Colors.CompSelected));
    else
      C_B.pinta(g, Posicao_X, Posicao_Y, getPalette().getColor(Colors.Componente), getPalette().getColor(Colors.Fundo));

    /* escrever nome */
    if (!activo)
      g.setColor(getPalette().getColor(Colors.Componente));
    else
      g.setColor(getPalette().getColor(Colors.Activo));
    g.setFont(getPalette().getFont());
    int px = (Posicao_X + C_B.Largura_X / 2) - Janela.FM_Tipo_8.stringWidth(Nome) / 2;
    g.drawString(Nome, px, Posicao_Y - 5);
    // g.drawOval(px-2,Posicao_Y-10-2,4,4);

    // g.drawString(cb.Nome,px,y1+5+Desenho.FM_Tipo_10.getHeight()/2+y2);

    /* desenahr cruz sobre entrada/saida nao ligada */
    g.setColor(getPalette().getColor(Colors.Cross));
    for (int i = 0; i < lista_estado_entradas.size(); i++) {
      boolean ja = false;
      List<Conector> l = lista_estado_entradas.get(i);
      for (int k = 0; k < l.size() && !ja; k++) {
        Conector c = l.get(k);
        Conector c_in = Linha.daIn(c);
        if (c_in != null)
          if (c_in.Comp != null)
            ja = true;
      }
      if (!ja) {
        Point p = (Point) C_B.Pontos_Entrada[i];
        g.drawLine(Posicao_X + p.x - 2, Posicao_Y + p.y - 2, Posicao_X + p.x + 2, Posicao_Y + p.y + 2);
        g.drawLine(Posicao_X + p.x + 2, Posicao_Y + p.y - 2, Posicao_X + p.x - 2, Posicao_Y + p.y + 2);
      }
    }
    for (int i = 0; i < lista_estado_saidas.size(); i++) {
      boolean ja = false;
      List<Conector> l = lista_estado_saidas.get(i);
      for (int k = 0; k < l.size() && !ja; k++) {
        Conector c = l.get(k);
        Conector c_in = Linha.daOut(c);
        if (c_in != null)
          if (c_in.Comp != null)
            ja = true;
      }
      if (!ja) {
        Point p = (Point) C_B.Pontos_Saida[i];
        g.drawLine(Posicao_X + p.x - 2, Posicao_Y + p.y - 2, Posicao_X + p.x + 2, Posicao_Y + p.y + 2);
        g.drawLine(Posicao_X + p.x + 2, Posicao_Y + p.y - 2, Posicao_X + p.x - 2, Posicao_Y + p.y + 2);
      }
    }

  }

  /*****************************************************************************
   * Funcao que verifica se um ponto esta dentro ou nao do componente
   */
  public boolean dentro(int x, int y) {
    if ((x >= Posicao_X) && (x <= Posicao_X + C_B.Largura_X))
      if ((y >= Posicao_Y) && (y <= Posicao_Y + C_B.Largura_Y))
        return true;
    return false;
  }

  /*****************************************************************************
   * Verifica se um dado ponto esta perto de um outro ponto, que pode ser uma
   * entrada ou saida
   * 
   * @param x
   *          posicao x do ponto a verificar
   * @param y
   *          posicao y do ponto a verificar
   * @param x1
   *          posicao x do ponto de entrada/saida
   * @param y1
   *          posicao y do ponto de entrada/saida
   */
  private boolean dentro_ponto(int x, int y, int x1, int y1) {
    if ((x >= x1 - 5) && (x <= x1 + 5))
      if ((y >= y1 - 5) && (y <= y1 + 5))
        return true;
    return false;
  }

  /*****************************************************************************
   * Mover o componente e as linhas a ele ligadas
   * 
   * @param x
   *          intervalo x de deslocacao
   * @param y
   *          intervalo y de deslocacao
   */
  public void move(int x, int y) {
    if(this.isEditable()) {
      Componente aux;
      int i;
      
      /* mover entradas */
      for (i = 1; i <= lista_estado_entradas.size(); i++) {
        List<Conector> v = lista_estado_entradas.get(i - 1);
        for (int j = 0; j < v.size(); j++) {
          aux = v.get(j).Comp;
          if (aux != null) {
            ((Linha) aux).esc(this, Ponto_Entrada(i - 1));
            aux.move(x, y);
          }
        }
      }
      
      /* mover saidas */
      for (i = 1; i <= lista_estado_saidas.size(); i++) {
        List<Conector> v = lista_estado_saidas.get(i - 1);
        for (int j = 0; j < v.size(); j++) {
          aux = v.get(j).Comp;
          if (aux != null) {
            ((Linha) aux).esc(this, Ponto_Saida(i - 1));
            aux.move(x, y);
          }
        }
      }
      
      /* modificar a posicao */
      Posicao_X += x;
      Posicao_Y += y;
    }
  }

  /*****************************************************************************
   * verificar se um ponto esta perto de uma entrada Devolve qual o ponto de
   * entrada
   * 
   * @return numero da entrada
   */
  public int E_Entrada(int x, int y) {
    /* percorrer entradas */
    for (int i = 0; i < C_B.Num_Entradas; i++) {
      Point p = C_B.Pontos_Entrada[i];
      if (dentro_ponto(x, y, Posicao_X + p.x, Posicao_Y + p.y))
        return (i + 1);
    }
    return 0;
  }

  /*****************************************************************************
   * verificar se um ponto esta perto de uma saida. Devolve qual o ponto de
   * saida
   * 
   * @return numero da saida
   */
  public int E_Saida(int x, int y) {
    /* percorrer saidas */
    for (int i = 0; i < C_B.Num_Saidas; i++) {
      Point p = C_B.Pontos_Saida[i];
      if (dentro_ponto(x, y, Posicao_X + p.x, Posicao_Y + p.y))
        return (i + 1);
    }
    return 0;
  }

  /*****************************************************************************
   * Devolve o ponto de entrada numero x
   */
  public Point Ponto_Entrada(int x) {
    if (x >= C_B.Num_Entradas)
      return null;
    /* tirar ponto e devolve-lo */
    Point p = new Point(C_B.Pontos_Entrada[x]);
    p.x += Posicao_X;
    p.y += Posicao_Y;
    return p;
  }

  /*****************************************************************************
   * Devolve o ponto de saida numero x
   */
  public Point Ponto_Saida(int x) {
    if (x >= C_B.Num_Saidas)
      return null;
    /* tirar ponto e devolve-lo */
    Point p = new Point(C_B.Pontos_Saida[x]);
    p.x += Posicao_X;
    p.y += Posicao_Y;
    return p;
  }

  /*****************************************************************************
   * Funcao para eliminar o componente - desligar todas as ligacoes
   */
  public void Elimina() {
    Elimina(null);
  }

  public void Elimina(ArrayList<Componente> lista) {
    if(this.isEditable()) {
      int i;
      Componente aux;
      
      /* lista de entradas */
      for (i = 1; i <= lista_estado_entradas.size(); i++) {
        List<Conector> v = lista_estado_entradas.get(i - 1);
        int valor = 0;
        while (valor < v.size()) {
          aux = v.get(valor).Comp;
          
          boolean ja = false;
          if (lista != null)
            for (int ii = 0; ii < lista.size() && !ja; ii++)
              if (aux == lista.get(ii))
                ja = true;
          if (aux != null && !ja) {
            ((Linha) aux).esc(this, Ponto_Entrada(i - 1));
            ((Linha) aux).Desliga(null);
          }
          valor++;
        }
      }
      /* lista de saidas */
      for (i = 1; i <= lista_estado_saidas.size(); i++) {
        List<Conector> v = lista_estado_saidas.get(i - 1);
        int valor = 0;
        while (valor < v.size()) {
          aux = v.get(valor).Comp;
          
          boolean ja = false;
          if (lista != null)
            for (int ii = 0; ii < lista.size() && !ja; ii++)
              if (aux == lista.get(ii))
                ja = true;
          
          if (aux != null && !ja) {
            ((Linha) aux).esc(this, Ponto_Entrada(i - 1));
            ((Linha) aux).Desliga(null);
          }
          valor++;
        }
      }
    }
  }

  /*****************************************************************************
   * Refrescar o componente
   */
  public void refresh(int delta_x, int delta_y, Desenho p) {
    /* refrescar o proprio componente */
    int aux = Janela.FM_Tipo_8.stringWidth(Nome) / 2;
    String descr = C_B.Descricao;
    if(StringUtils.isNotBlank(C_B.descrKey))
      descr = p.janela.getBlockMessages().getString(C_B.descrKey);
    
    aux = Math.max(aux, Janela.FM_Tipo_8.stringWidth(descr) / 2);

    int p1x = Posicao_X - (delta_x + 60 - C_B.Largura_X / 2 + aux);
    int p1y = Posicao_Y - delta_y - 20 - Janela.FM_Tipo_8.getHeight() / 2;
    int dp2x = 2 * (delta_x + 80 + aux);
    int dp2y = (2 * delta_y) + C_B.Largura_Y + 40 + Janela.FM_Tipo_8.getHeight();

    p.repaint(p1x, p1y, dp2x, dp2y);

    int i;
    Componente c_aux;

    /* refrescar os componentes ligados */
    for (i = 1; i <= lista_estado_entradas.size(); i++) {
      List<Conector> v = lista_estado_entradas.get(i - 1);
      for (int j = 0; j < v.size(); j++) {
        c_aux = v.get(j).Comp;
        if (c_aux != null)
          c_aux.refresh(delta_x, delta_y, p);
      }
    }
    for (i = 1; i <= lista_estado_saidas.size(); i++) {
      List<Conector> v = lista_estado_saidas.get(i - 1);
      for (int j = 0; j < v.size(); j++) {
        c_aux = v.get(j).Comp;
        if (c_aux != null)
          c_aux.refresh(delta_x, delta_y, p);
      }
    }
  }

  /*****************************************************************************
   * Inserir uma nova entrada
   */
  public void Insere_Entrada(Componente c, int num_entrada, int num_saida) {

    List<Conector> v = lista_estado_entradas.get(num_saida);
    Conector con = new Conector();
    con.Set_Conector(c, num_entrada);
    v.add(con);
  }

  /*****************************************************************************
   * Inserir uma nova saida
   */
  public void Insere_Saida(Componente c, int num_entrada, int num_saida) {
    List<Conector> v = lista_estado_saidas.get(num_entrada);
    Conector con = new Conector();
    con.Set_Conector(c, num_saida);
    v.add(con);
  }

  /*****************************************************************************
   * Funcao que verifica se o componente esta contido NO rectangulo recebido
   */
  public boolean Contido(int x1, int y1, int x2, int y2) {
    if (x1 < Posicao_X && x2 > (Posicao_X + C_B.Largura_X) && y1 < Posicao_Y && y2 > (Posicao_Y + C_B.Largura_Y))
      return true;
    return false;
  }

  public boolean inViewPort(int x1, int y1, int x2, int y2) {
    if (Posicao_X >= x2)
      return false;
    if (Posicao_X + C_B.Largura_X <= x1)
      return false;
    if (Posicao_Y >= y2)
      return false;
    if (Posicao_Y + C_B.Largura_Y <= y1)
      return false;
    return true;
  }

  /*****************************************************************************
   * Ajustar coordenadas do componente para ficar em esquadria
   */
  public void Ajusta_Coordenadas() {
    /* descobrir novas posicoes + proximas */
    int x = (Posicao_X + INTERVALO / 2) / INTERVALO;
    int x1 = INTERVALO * x;
    int y = (Posicao_Y + INTERVALO / 2) / INTERVALO;
    int y1 = INTERVALO * y;
    this.move(x1 - Posicao_X, y1 - Posicao_Y);
  }

  /*****************************************************************************
   * Mudar nome do componente, so permite nomes com 15 ou menos caracteres
   */
  public void Mudar_Nome(String nome) {
    if(this.isEditable()) {      
      Nome = nome;
    }
  }

  /*****************************************************************************
   * Informacao sobre componente simplex
   */
  public boolean E_Gate() {
    return true;
  }

  /*****************************************************************************
   * mostra/edita atributos
   */
  public void alteraAtributos(final Desenho desenho, final Janela janela) {
    if(this.isEditable()) {
      try {
        if (C_B.nomeClasseAlteraAtributos == null && (atributos == null || atributos.size() == 0)) {
          return;
        }
        
        Class<AbstractAlteraAtributos> dialogClass = null;
        AbstractAlteraAtributos dialog = null;
        if (C_B.nomeClasseAlteraAtributos == null) {
          dialogClass = janela.loadGUIClass("pt.iknow.floweditor.blocks.AlteraAtributos"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
          dialogClass = janela.loadGUIClass(C_B.nomeClasseAlteraAtributos);
        }
        
        
        // first try a "specialized" constructor
        FlowEditorAdapter adapter = new FlowEditorAdapterImpl(janela, desenho, C_B.descrKey);
        
        try {
          Constructor<AbstractAlteraAtributos> argsConstructor = dialogClass.getConstructor(FlowEditorAdapter.class);
          dialog = argsConstructor.newInstance(adapter);
        } catch (NoSuchMethodException e) {
          // Dont warn about this, it is not important
          // FlowEditor.log(e);
        } catch (Exception e) {
          FlowEditor.log("Erro ao instanciar interface. Tentar generico...", e);
        }
        
//        System.out.println("antes de");
        dialog.setDataIn(Messages.getString("Instancia_Componente.title"), atributos); //$NON-NLS-1$
//        System.out.println("depois de");
        
        if (dialog.getExitStatus() == AlteraAtributosInterface.EXIT_STATUS_OK) {
          String[][] newAttr = dialog.getNewAttributes();
          Atributo attr = null;
          
          if (C_B.nomeClasseAlteraAtributos != null) {
            atributos = new ArrayList<Atributo>();
          }
          
          for (int i = 0; i < newAttr.length; i++) {
            if (C_B.nomeClasseAlteraAtributos == null) {
              attr = atributos.get(i);
              attr.setAtributo(newAttr[i][1]);
            } else {
              String desc = newAttr[i][0];
              if (newAttr[i].length == 3 && newAttr[i][2] != null && !newAttr[i][2].equals("")) { //$NON-NLS-1$
                desc = newAttr[i][2];
              }
              attr = new AtributoImpl(newAttr[i][0], newAttr[i][1], desc, null);
              atributos.add(attr);
            }
          }
          // Notificar desenho de alteracao do estado.
          desenho.setFlowChanged(true);
        }
      } catch (Exception e) {
        FlowEditor.log("error", e);
      }
    }
  }

  public String toString() {
    return ID + " - " + Nome + " (" + C_B.Descricao + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  public int compareTo(InstanciaComponente ic) {
    return ID - ic.ID;
  }

  public boolean isStart() {
    return C_B.isStart();
  }

  public Componente_Biblioteca getComponenteBiblioteca() {
    return C_B;
  }
}
