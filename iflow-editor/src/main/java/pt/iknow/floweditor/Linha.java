package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Linha
 *
 *  desc: liga blocos
 *
 ****************************************************/

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.iknow.floweditor.Cor.Colors;

/*******************************************************************************
 * Linha
 */
public class Linha extends Componente {
  static int DIST = 4;

  /* pontos de extremidade */
  Point p1, p2;
  /* lista de pontos */
  private ArrayList<Point> _alPoints = new ArrayList<Point>();

  /* variavel de controlo */
  public int escolhido;
  private boolean e[] = null;
  public double intermedio = 0.0;

  /* componentes a que a linha esta ligado */
  public Componente lista[];

  public void destroi() {
    if(this.isEditable()) {
      _alPoints.clear();
      e = null;
      lista = null;
      super.destroi();
    }
  }

  /*****************************************************************************
   * Construtor
   */
  public Linha() {
    Inicializa();
  }

  /*****************************************************************************
   * Construtor onde e indicado os pontos de extremidade
   * 
   * @param _x1
   *          coordenada x do primeiro ponto
   * @param _y1
   *          coordenada y do primeiro ponto
   * @param _x2
   *          coordenada x do ultimo ponto
   * @param _y2
   *          coordenada y do ultimo ponto
   */
  public Linha(int _x1, int _y1, int _x2, int _y2) {
    Inicializa();
    muda(_x1, _y1, _x2, _y2);
  }

  /*****************************************************************************
   * funcao para eliminar este componente (desligar dos outros componentes)
   */
  public void Elimina() {
    Elimina(null);
  }

  public void Elimina(ArrayList<Componente> lista) {
    if(this.isEditable()) {
      intermedio = 0.0;
      escolhido = 1;
      Desliga(lista);
      escolhido = _alPoints.size();
      Desliga(lista);
    }
  }

  /*****************************************************************************
   * inicializa as variaveis do classe
   */
  protected void Inicializa() {
    /* inicialmente nao ha ligacoes */
    lista = new Componente[2];
    lista[0] = null;
    lista[1] = null;

    /* pontos de estremidade */
    p1 = new Point(50, 50);
    p2 = new Point(100, 100);
    _alPoints.add(p1);
    _alPoints.add(p2);
    escolhido = _alPoints.size();

    /* iniciar listas para ligacao aos componentes */
    lista_estado_saidas = new ArrayList<List<Conector>>();
    lista_estado_entradas = new ArrayList<List<Conector>>();
    ArrayList<Conector> _v1 = new ArrayList<Conector>();
    lista_estado_saidas.add(_v1);
    ArrayList<Conector> _v2 = new ArrayList<Conector>();
    lista_estado_entradas.add(_v2);

  }

  public int numberPoints() {
    return _alPoints.size();
  }

  public Point getPoint(int i) {
    return _alPoints.get(i);
  }

  public void setPointList(List<Point> alPoints) {
    if(this.isEditable()) {
      _alPoints.clear();
      _alPoints.addAll(alPoints);
    }
  }

  public void setPoint(int i, Point p) {
    if(this.isEditable()) {
      _alPoints.add(i, p);
    }
  }

  public void removePoint(int i) {
    if(this.isEditable()) {
      _alPoints.remove(i);
    }
  }

  public String toString() {
    StringBuffer sbtmp = new StringBuffer();
    sbtmp.append("linha: ").append(_alPoints); //$NON-NLS-1$
    return sbtmp.toString();
  }

  /*****************************************************************************
   * Funcao que diz que e linha
   */
  public boolean E_Linha() {
    return true;
  }

  /*****************************************************************************
   * Muda a posicao da linha
   * 
   * @param _x1
   *          coordenada x do primeiro ponto
   * @param _y1
   *          coordenada y do primeiro ponto
   * @param _x2
   *          coordenada x do ultimo ponto
   * @param _y2
   *          coordenada y do ultimo ponto
   */
  protected void muda(int _x1, int _y1, int _x2, int _y2) {
    if(this.isEditable()) {
      p1.x = _x1;
      p2.x = _x2;
      p1.y = _y1;
      p2.y = _y2;
    }
  }
  
  /**
   * Verifica se o Conector é um portEvent
   * @return True se for portEvent
   * @
   */
  protected boolean isPortEvent() {
    if( lista_estado_entradas.isEmpty()) {
      return false;
    }
    List<Conector> lst = lista_estado_entradas.get(0);
    if( (lst == null) || lst.isEmpty() ) {
      return false;
    }
    Conector c = lst.get(0);
    if( (c.Comp == null) || !(c.Comp instanceof InstanciaComponente) ) {
      return false;
    }
    Componente_Biblioteca libComponent = ((InstanciaComponente) c.Comp).getComponenteBiblioteca();
    String portName = libComponent.nomes_saidas.get(c.Numero);
    
    return "portEvent".equals(portName);
  }

  /**
   * Funcao para desenhar a linha.
   */
  public void paint(Graphics g) {

    /* escolher cor - consoante o estado */
    boolean isInvalid = (lista == null || lista.length < 2 || lista[0] == null || lista[1] == null);
    if (activo && isInvalid) {
      g.setColor(getPalette().getColor(Colors.Activo));
    } else if(activo) { // A linha nao esta ligada...
      g.setColor(getPalette().getColor(Colors.Activo));
    } else if(isInvalid) { // A linha nao esta ligada...
      g.setColor(getPalette().getColor(Colors.Invalido));
    } else { // Default Color
      g.setColor(getPalette().getColor(Colors.Componente));
    }
    Stroke defaultStroke = ((Graphics2D)g).getStroke();
    if(isPortEvent()) {
      // Make line dotted.
      Graphics2D g2d = (Graphics2D)g;
      float dot[] = {2.0f};
      Stroke dottedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dot, 0.0f);
      g2d.setStroke(dottedStroke);
    }

    Point a = null;
    Point b = null;
    for (int i = 0; i < _alPoints.size() - 1; i++) {
      a = _alPoints.get(i);
      b = _alPoints.get(i + 1);
      g.drawLine(a.x, a.y, b.x, b.y);
      if (i != _alPoints.size() - 2)
        g.fillOval(b.x - 1, b.y - 1, 2, 2);
    }
    // Restore default line.
    ((Graphics2D)g).setStroke(defaultStroke);
    

    /* desenhar ligacoes, se as houver */
    if (lista[0] != null)
      g.fillOval(p1.x - 2, p1.y - 2, 4, 4);
    if (lista[1] != null)
      g.fillOval(p2.x - 2, p2.y - 2, 4, 4);

    /* seta */
    Point P1 = null;
    Point P2 = null;
    
    List<Conector> lst = lista_estado_saidas.get(0);
    if (lst.size() > 0) {
      Conector c = lst.get(0);
      if (lista[0] == c.Comp) {
        P1 = p1;
        P2 = _alPoints.get(1);

      }
      if (lista[1] == c.Comp) {
        P1 = p2;
        P2 = _alPoints.get(_alPoints.size() - 2);

      }

      if (P1 != null && P2 != null) {
        Point paux1 = new Point(P2.x - (P1.y - P2.y), P2.y + (P1.x - P2.x));

        Point paux2 = new Point(P2.x + (P1.y - P2.y), P2.y - (P1.x - P2.x));

        /* primeiro */
        double dist = Math.sqrt((paux2.x - P1.x) * (paux2.x - P1.x) + (paux2.y - P1.y) * (paux2.y - P1.y));
        double _x = (paux2.x - P1.x);
        if (dist != 0) {
          _x = _x * 5 / dist;
          double _y = (paux2.y - P1.y);
          _y = _y * 5 / dist;
          Point p__ = new Point((int) (P1.x + _x), (int) (P1.y + _y));
          g.drawLine(p__.x, p__.y, P1.x, P1.y);
        }

        /* segundo risco */
        dist = Math.sqrt((paux1.x - P1.x) * (paux1.x - P1.x) + (paux1.y - P1.y) * (paux1.y - P1.y));
        _x = (paux1.x - P1.x);
        if (dist != 0) {
          _x = _x * 5 / dist;
          double _y = (paux1.y - P1.y);
          _y = _y * 5 / dist;
          Point p__ = new Point((int) (P1.x + _x), (int) (P1.y + _y));
          g.drawLine(p__.x, p__.y, P1.x, P1.y);
        }
      }

    }

  }

  /*****************************************************************************
   * Verifica se um dado ponto esta a tocar na linha
   * 
   * @param x
   *          coordenada x
   * @param y
   *          coordenada y
   */
  public boolean dentro(int x, int y) {
    intermedio = 0.0;
    /* inicializar a false */
    e = new boolean[_alPoints.size()];
    for (int i = 0; i < _alPoints.size(); i++)
      e[i] = false;
    escolhido = 0;

    /* verificar se esta a tocar nos pontos */
    for (int i = 0; i < _alPoints.size(); i++) {
      Point a = (Point) _alPoints.get(i);
      if ((x >= a.x - DIST) && (x <= a.x + DIST) && (y >= a.y - DIST) && (y <= a.y + DIST)) {
        e[i] = true;
        if (escolhido == 0)
          escolhido = 1 + i;
      }
    }

    /* se ja escolheu iSAIR */
    if (escolhido != 0)
      return true;

    /* verificar se enta entre 2 pontos */
    for (int i = 0; i < _alPoints.size() - 1; i++) {
      Point a = (Point) _alPoints.get(i);
      Point b = (Point) _alPoints.get(i + 1);

      /* se tem a mesma coordenada x */
      if (a.x == b.x) {
        int max_y = a.y;
        int min_y = b.y;
        if (b.y > max_y) {
          max_y = b.y;
          min_y = a.y;
        }
        if (((x - a.x) <= DIST) && ((x - a.x) >= -DIST) && (max_y >= y - DIST) && (min_y <= y + DIST)) {
          intermedio = 0.5;
          escolhido = i + 1;
          return true;
        }
      } else
      /* diferentes coordenadas x */
      {
        int y_ = a.y + (x - a.x) * (b.y - a.y) / (b.x - a.x);
        int max_x = a.x;
        int min_x = b.x;
        if (a.x < min_x) {
          min_x = a.x;
          max_x = b.x;
        }
        if ((y >= y_ - DIST) && (y <= y_ + DIST) && (x > min_x - DIST) && (x < max_x + DIST)) {
          intermedio = 0.5;
          escolhido = i + 1;
          return true;
        }

        /* second try */
        if (b.y != a.y) {
          int x_ = a.x + (y - a.y) * (b.x - a.x) / (b.y - a.y);

          int max_y = a.y;
          int min_y = b.y;
          if (a.y < min_y) {
            min_y = a.y;
            max_y = b.y;
          }

          if ((x >= x_ - DIST) && (x <= x_ + DIST) && (y > min_y - DIST) && (y < max_y + DIST)) {
            intermedio = 0.5;
            escolhido = i + 1;
            return true;
          }
        }
      }
    }
    return false;
  }

  /*****************************************************************************
   * Mover um ponto da linha o ponto movido tem a ver com o valor da variavel
   * 'escolhido'
   * 
   * @param x
   *          coordenada x
   * @param y
   *          coordenada y
   */
  public void move(int x, int y) {
    if(isEditable()) {
      /* se nao escolheu nenhum ponto ou escolheu entre 2 pontos */
      if (intermedio == 0.5 || escolhido == 0)
        return;
      Point p = (Point) _alPoints.get(escolhido - 1);
      p.x += x;
      p.y += y;
    }
  }

  /*****************************************************************************
   * Funcao que verifica se um dado componente c, esta ligado a linha, caso
   * esteja essa, o valor da variavel 'escolhido' e 1 ou 2
   */
  public void esc(Componente c, Point p) {
    // e=null;
    intermedio = 0.0;

    if (c == lista[0] && c == lista[1]) {
      if (p.x == p1.x && p.y == p1.y)
        escolhido = 1;
      else
        escolhido = _alPoints.size();
    } else if (c == lista[0])
      escolhido = 1;
    else if (c == lista[1])
      escolhido = _alPoints.size();
    else
      escolhido = 0;

  }

  /*****************************************************************************
   * insere um novo componente como entrada
   * 
   * @param c
   *          componente a ligar
   * @param num_entrada
   *          numero da io do componente
   * @param num_saida
   *          numero de io da linha
   */
  public void Insere_Entrada(Componente c, int num_entrada, int num_saida) {
    int a = escolhido;
    if (a > 1)
      a = 2;
    List<Conector> v = lista_estado_entradas.get(num_saida);
    if (v.size() == 1) {
      Conector c_ = v.get(0);
      Componente cc = c_.Comp;
      if (lista[a - 1] == cc)
        lista[a - 1] = null;
      else
        lista[1 - (a - 1)] = null;
      super.Retira_Entrada(num_saida, c_.Comp);
      cc.Retira_Saida(c_.Numero, this);
    }
    Conector con = new Conector();
    v.add(con);
    con.Set_Conector(c, num_entrada);
    lista[a - 1] = c;
    /* coloca o ponto NO sitio certo */
    Set_ponto(c.Ponto_Saida(num_entrada));
  }

  /**
   * ******************************```````````````` insere um novo componente
   * como saida
   * 
   * @param c
   *          componente a ligar
   * @param num_entrada
   *          numero da io do componente
   * @param num_saida
   *          numero de io da linha
   */
  public void Insere_Saida(Componente c, int num_entrada, int num_saida) {
    int a = escolhido;
    if (a > 1)
      a = 2;
    List<Conector> v = lista_estado_saidas.get(num_entrada);

    if (v.size() == 1) {
      Conector c_ = v.get(0);
      Componente cc_ = c_.Comp;
      cc_.Retira_Entrada(c_.Numero, this);
      // Retira_Saida(num_entrada,cc_);
      if (lista[a - 1] == cc_)
        lista[a - 1] = null;
      else
        lista[1 - (a - 1)] = null;
      super.Retira_Saida(num_entrada, cc_);

    }
    Conector con = new Conector();
    v.add(con);

    con.Set_Conector(c, num_saida);
    lista[a - 1] = c;
    /* coloca o ponto NO sitio certo */
    Set_ponto(c.Ponto_Entrada(num_saida));
  }

  /*****************************************************************************
   * Funcao que desliga uma entrada/saida a variavel 'escolhida' contem
   * informacao sobre qual o componente a desligar
   */
  public void Desliga() {
    Desliga(null);
  }

  public void Desliga(ArrayList<Componente> list) {
    if(this.isEditable()) {
      Componente c1 = null, c2 = null;
      Conector co = null;
      int a = escolhido;
      if (intermedio == 0.5)
        return;
      
      /* verifivar qual o escolhido */
      if (a != 1 && a != _alPoints.size())
        return;
      if (a == _alPoints.size())
        escolhido = 2;
      
      /* Entradas */
      
      boolean ja = false;
      
      List<Conector> auxV = lista_estado_entradas.get(0);
      for (int i3 = 0; i3 < auxV.size() && !ja; i3++) {
        
        co = auxV.get(i3);
        c1 = co.Comp;
        
        /* se c1 */
        if (list != null)
          for (int ii = 0; ii < list.size() && !ja; ii++)
            if (c1 == list.get(ii))
              ja = true;
        
      }
      
      if (c1 == lista[escolhido - 1] && c1 != null) {
        /* desligar na linha */
        if (!ja) {
          lista[escolhido - 1] = null;
          Retira_Entrada(0, c1);
          c1.Retira_Saida(co.Numero, this);
          
        }
        
      } else {
        List<Conector> _v = lista_estado_saidas.get(0);
        for (int i = 0; i < _v.size(); i++) {
          co = _v.get(i);
          c2 = co.Comp;
          ja = false;
          if (list != null)
            for (int ii = 0; ii < list.size() && !ja; ii++)
              if (c2 == list.get(ii))
                ja = true;
          if (c2 == lista[escolhido - 1] && c2 != null) {
            if (!ja) {
              Retira_Saida(0, c2);
              lista[escolhido - 1] = null;
              c2.Retira_Entrada(co.Numero, this);
            }
          }
        }
      }
      escolhido = a;
    }
  }

  /*****************************************************************************
   * Coloca um dado ponto da linha NO ponto recebido
   * 
   * @param p
   *          ponto onde vai ficar o ponto da linha escolhido
   */
  protected void Set_ponto(Point p) {
    if(this.isEditable()) {
      if (escolhido == 1) {
        p1.x = p.x;
        p1.y = p.y;
      } else /* p2 */
      {
        p2.x = p.x;
        p2.y = p.y;
      }
    }
  }

  /*****************************************************************************
   * Fazer refresh do componente
   * 
   * @param delta_x
   *          deslocamento x
   * @param delta_y
   *          deslocamento y
   * @param d
   *          canvas onde e desenhado
   */
  public void refresh(int delta_x, int delta_y, Desenho d) {
    /* descobrir qual o rectangulo envolvente */
    int _x1, _y1, _x2, _y2;
    _x1 = _y1 = Integer.MAX_VALUE;
    _x2 = _y2 = Integer.MIN_VALUE;
    Point p;
    for (int i = 0; i < _alPoints.size(); i++) {
      p = (Point) _alPoints.get(i);
      if (p.x > _x2)
        _x2 = p.x;
      if (p.x < _x1)
        _x1 = p.x;
      if (p.y > _y2)
        _y2 = p.y;
      if (p.y < _y1)
        _y1 = p.y;
    }
    /* refrescar o rectangulo + um pouco */
    d.repaint(_x1 - delta_x - 5, _y1 - delta_y - 5, _x2 - _x1 + (2 * delta_x) + 10, _y2 - _y1 + (2 * delta_y) + 30);
  }

  /*****************************************************************************
   * Verifica se um dado ponto e entrada
   */
  public int E_Entrada(int x, int y) {
    if (dentro(x, y))
      return 1;
    else
      return 0;
  }

  /*****************************************************************************
   * Verifica se um dado ponto e entrada
   */
  public int E_Saida(int x, int y) {
    return 0;
    /* nunca e para evitar ligar linhas a linhas */
  }

  /*****************************************************************************
   * Devolve o ponto de entrada
   */
  public Point Ponto_Entrada(int x) {
    if (escolhido == 1)
      return new Point(p1.x, p1.y);
    else
      return new Point(p2.x, p2.y);
  }

  /*****************************************************************************
   * Devolve o ponto de saida
   */
  public Point Ponto_Saida(int x) {
    if (escolhido == 1)
      return new Point(p1.x, p1.y);
    else
      return new Point(p2.x, p2.y);
  }

  /*****************************************************************************
   * Verifica se a linha esta contida NO rectangulo recebido
   */
  public boolean Contido(int x1, int y1, int x2, int y2) {
    Point p;
    boolean a = false;
    e = new boolean[_alPoints.size()];
    for (int i = 0; i < _alPoints.size(); i++)
      e[i] = false;

    /* percorrer pontos */
    for (int i = 0; i < _alPoints.size(); i++) {
      p = (Point) _alPoints.get(i);
      if (p.x <= x2 && p.x >= x1 && p.y <= y2 && p.y >= y1) {
        a = true;
        e[i] = true;
      }
    }
    return a;
  }

  public boolean inViewPort(int vx1, int vy1, int vx2, int vy2) {
    /* percorrer pontos */
    Point p = (Point) _alPoints.get(0);
    int x1 = p.x;
    int x2 = p.x;
    int y1 = p.y;
    int y2 = p.y;
    for (int i = 1; i < _alPoints.size(); i++) {
      p = (Point) _alPoints.get(i);
      if (p.x <= vx2 && p.x >= vx1 && p.y <= vy2 && p.y >= vy1)
        return true;
      x1 = Math.min(x1, p.x);
      x2 = Math.max(x2, p.x);
      y1 = Math.min(y1, p.y);
      y2 = Math.max(y2, p.y);
    }
    Rectangle a = new Rectangle(x1, y1, x2 - x1, y2 - y1);
    return a.intersects(new Rectangle(vx1, vy1, vx2 - vx1, vy2 - vy1));
    // return false;
  }

  /*****************************************************************************
   * Move os pontos da linha que nao estao ligados a componentes (esses sao
   * movidos pelo movimento dos componentes)
   */
  public void Move_Pontos_Desligados(int delta_x, int delta_y) {
    if(this.isEditable()) {
      if (e == null)
        return;
      /* mover ponto 1, se nao ligado */
      if (lista[0] == null && e[0]) {
        p1.x += delta_x;
        p1.y += delta_y;
      }
      /* mover ponto 2 se nao ligado */
      if (lista[1] == null && e[_alPoints.size() - 1]) {
        p2.x += delta_x;
        p2.y += delta_y;
      }
      /* mover resto dos pontos se escolhidos */
      
      for (int i = 1; i < _alPoints.size() - 1; i++)
        if (e[i]) {
          Point p = (Point) _alPoints.get(i);
          p.x += delta_x;
          p.y += delta_y;
        }
    }
  }

  /*****************************************************************************
   * retira uma saida
   */
  public void Retira_Saida(int numero, Componente comp) {
    if(this.isEditable() && comp.isEditable()) {
      /* limpar a variavel lista */
      if (lista[0] == comp)
        lista[0] = null;
      if (lista[1] == comp)
        lista[1] = null;
      /* limpar o resto */
      super.Retira_Saida(numero, comp);
    }
  }

  /*****************************************************************************
   * retira uma entrada
   */
  public void Retira_Entrada(int numero, Componente comp) {
    if(this.isEditable() && comp.isEditable()) {
      if (lista[0] == comp)
        lista[0] = null;
      if (lista[1] == comp)
        lista[1] = null;
      super.Retira_Entrada(numero, comp);
    }
  }

  /*****************************************************************************
   * Insere um ponto de quebra
   */
  public void Insere_Ponto_Quebra(int x, int y) {
    if(this.isEditable()) {
      /* se foi escolhido um ponto NO meio de 2 */
      if (intermedio == 0.5) {
        x = INTERVALO * ((x + INTERVALO / 2) / INTERVALO);
        y = INTERVALO * ((y + INTERVALO / 2) / INTERVALO);
        _alPoints.set(escolhido, new Point(x, y));
        escolhido++;
      }
      intermedio = 0.0;
    }
  }

  /*****************************************************************************
   * elimina um ponto de quebra
   */
  public void Elimina_Ponto_Quebra(int x, int y) {
    if(this.isEditable()) {
      /* se foi escolhido um ponto */
      if (intermedio == 0.0) {
        if (escolhido > 1 && escolhido < _alPoints.size()) {
          _alPoints.remove(escolhido - 1);
          if (e != null)
            e[escolhido - 1] = false;
        }
        if (e != null)
          for (int i = 1, j = 1; i < e.length - 1; i++, j++)
            if (e[i]) {
              _alPoints.remove(j);
              j--;
            }
      }
    }
  }

  /*****************************************************************************
   * retorna o ponto da lista que esta perto do ponto recebido
   */
  public Point Pontos_Interiores(int x, int y) {
    for (int i = 0; i < _alPoints.size(); i++) {
      Point p = (Point) _alPoints.get(i);
      if (x >= p.x - 2 && x <= p.x + 2 && y >= p.y - 2 && y <= p.y + 2)
        return p;
    }
    return null;
  }

  /*****************************************************************************
   * ajusta os pontos da linha para coordenadas certas
   */
  public void Ajusta_Coordenadas() {
    if(this.isEditable()) {
      for (int i = 0; i < _alPoints.size(); i++) {
        Point p = (Point) _alPoints.get(i);
        p.x = INTERVALO * ((p.x + INTERVALO / 2) / INTERVALO);
        p.y = INTERVALO * ((p.y + INTERVALO / 2) / INTERVALO);
      }
    }
  }

  /*******************************************
   * diz se foi escolhido um ponto interior
   */
  public boolean EscPontoInterior() {
    if (escolhido == 1 || escolhido == _alPoints.size())
      return false;
    return true;

  }

  /*****************************/
  static public Conector daIn(Conector in) {
    Linha l = (Linha) in.Comp;
    List<Conector> list = l.lista_estado_entradas.get(0);
    if (list.size() > 0)
      return list.get(0);
    else
      return null;
  }

  /*****************************/
  static public Conector daOut(Conector in) {
    Linha l = (Linha) in.Comp;
    List<Conector> list = l.lista_estado_saidas.get(0);
    if (list.size() > 0)
      return list.get(0);
    else
      return null;
  }
  
  @Override
  public void Elimina(List<Componente> lista) {
    List<Conector> listaEntrada = lista_estado_entradas.get(0);
    List<Conector> listaSaida = lista_estado_saidas.get(0);
    removeConectorReference(listaEntrada);
    removeConectorReference(listaSaida);   
  }

  private void removeConectorReference(List<Conector> listaConectores) {
    // Entradas
    for(Conector con : listaConectores) {
      Componente comp = con.Comp;
      removeComponentReference(comp.lista_estado_entradas);
      removeComponentReference(comp.lista_estado_saidas);
    }
  }

  private void removeComponentReference(List<List<Conector>> lista_conectores) {
    for(List<Conector> newConnList : lista_conectores ) {
      Iterator<Conector> itr = newConnList.iterator();
      while(itr.hasNext()) {
        Conector newCon = itr.next();
        if(newCon.Comp == this) {
          itr.remove();
        }
      }
    }
  }
  
  
}
