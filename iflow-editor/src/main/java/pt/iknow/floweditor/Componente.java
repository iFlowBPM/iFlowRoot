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
 *  class: Componente
 *
 *  desc: caracteristicas base de um bloco
 *
 ****************************************************/

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

/*******************************************************************************
 * Componente
 */
public class Componente {
  
  public static final int INTERVALO = 5;
  
  /* variavies */
  /* se esta ectivo */
  boolean activo = false;

  /** Se está em estado editável. */
  boolean editable = true;

  /** Cores do componente. */
  private Cor palette;
  
  /* lista de estado com valores de saida */
  // ArrayList lista_estados=null;
  /* lista para os componentes de entrada e saida */
  protected List<List<Conector>> lista_estado_saidas = null;
  protected List<List<Conector>> lista_estado_entradas = null;

  public void destroi() {
    if(isEditable()) {
      for (int i = 0; i < lista_estado_entradas.size(); i++) {
        List<Conector> v = lista_estado_entradas.get(i);
        for (int j = 0; j < v.size(); j++) {
          Conector c = v.get(j);
          c.Comp = null;
        }
        v.clear();
      }
      lista_estado_entradas.clear();
      for (int i = 0; i < lista_estado_saidas.size(); i++) {
        List<Conector> v = lista_estado_saidas.get(i);
        for (int j = 0; j < v.size(); j++) {
          Conector c = v.get(j);
          c.Comp = null;
        }
        v.clear();
      }
      lista_estado_saidas.clear();
    }
  }

  /* identificacao */
  public boolean E_Linha() {
    return false;
  }

  public boolean E_Composto() {
    return false;
  }

  public boolean E_Entrada() {
    return false;
  }

  public boolean E_Saida() {
    return false;
  }

  public boolean E_Gate() {
    return false;
  }

  /* eliminacao */
  public void Elimina() {
  }

  public void Elimina(List<Componente> lista) {
  }

  /* funcoes de desenho */
  public void paint(Graphics g) {
  }

  public void refresh(int delta_x, int delta_y, Desenho p) {
  }

  public void Muda_Activo(boolean a) {
    activo = a;
  }

  public void move(int x, int y) {
  }

  public void Ajusta_Coordenadas() {
  };

  /* estado do componente */
  public boolean inViewPort(int x1, int y1, int x2, int y2) {
    return false;
  }

  /* funcoes de ligacao de components */
  public boolean dentro(int x, int y) {
    return false;
  }

  public int E_Entrada(int x, int y) {
    return 0;
  }

  public int E_Saida(int x, int y) {
    return 0;
  }

  public void Insere_Entrada(Componente c, int num_entrada, int num_saida) {
  }

  public void Insere_Saida(Componente c, int num_entrada, int num_saida) {
  }

  public Point Ponto_Entrada(int x) {
    return null;
  }

  public Point Ponto_Saida(int x) {
    return null;
  }

  public boolean Contido(int x1, int y1, int x2, int y2) {
    return false;
  }

  /*****************************************************************************
   * Funcao que liga este componente a outro
   * 
   * @param c1
   *          primeiro comonente
   * @param n1
   *          numero da entrada de c1
   * @param c2
   *          primeiro comonente
   * @param n2
   *          numero da saida de c2
   * 
   */
  public static void Liga(Componente c1, int n1, Componente c2, int n2) {
    if(c1.isEditable() && c2.isEditable()) {
      if (c1 == c2)
        return;
      /* desligar antigas ligacoes */
      if (c1.E_Linha())
        ((Linha) c1).Desliga();
      if (c2.E_Linha())
        ((Linha) c2).Desliga();

      /* ligar */
      c2.Insere_Entrada(c1, n1, n2);
      c1.Insere_Saida(c2, n1, n2);
    }
  }

  /*****************************************************************************
   * remove informacao sobre uma ligacao de uma saida
   * 
   * @param numero
   *          numero da saida
   * @param comp
   *          componente que esta ligado
   */
  public void Retira_Saida(int numero, Componente comp) {
    if(this.isEditable() && comp.isEditable()) {
      List<Conector> _v = lista_estado_saidas.get(numero);
      /* procurar componente */
      for (int i = 0; i < _v.size(); i++) {
        Conector con = _v.get(i);
        if (con.Comp == comp) {
          _v.remove(i);
          return;
        }
      }
    }
  }

  /*****************************************************************************
   * remove informacao sobre uma ligacao de uma entrada
   * 
   * @param numero
   *          numero da entrada
   * @param comp
   *          componente que esta ligado
   */
  public void Retira_Entrada(int numero, Componente comp) {
    if(this.isEditable() && comp.isEditable()) {
      List<Conector> _v = lista_estado_entradas.get(numero);
      /* procurar componente */
      for (int i = 0; i < _v.size(); i++) {
        Conector con = _v.get(i);
        if (con.Comp == comp) {
          _v.remove(i);
          return;
        }
      }
    }
  }

  /*****************************************************************************
   * mostra/edita atributso
   */
  public void alteraAtributos(Desenho desenho, Janela janela) {
  }
  
  public boolean isEditable() {
    return this.editable;
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
}


