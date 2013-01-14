package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Componente_Biblioteca
 *
 *  desc: descricao base de um bloco
 *
 ****************************************************/

import pt.iflow.api.xml.codegen.library.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;

import pt.iknow.utils.StringUtilities;

/****************************
 * Componente_Biblioteca
 */
public class Componente_Biblioteca {
  /* numero de entradas e saidas */
  protected int Num_Entradas;
  protected int Num_Saidas;

  /* nome */
  protected String Nome;
  protected String Descricao;
  protected boolean automatic;
  protected String descrKey;


  /* numero da funcao de desenho */
  protected Image funcao_Desenho;
  protected int Num_funcao_Desenho;
  protected Color color = null;
  
  /* variavel auxiliar */
  protected boolean gravado=false;

  /* variaveis graficos */
  protected int Largura_X;
  protected int Largura_Y;
  protected Point Pontos_Entrada[];
  protected Point Pontos_Saida[];

  /* nomes das entradas e saidas */
  protected ArrayList<String> nomes_entradas;
  protected ArrayList<String> nomes_saidas;
  protected ArrayList<String> desc_nomes_entradas;
  protected ArrayList<String> desc_nomes_saidas;


  /***************/
  protected ArrayList<Atributo> NomesAtributos;
  protected String nomeClasseAlteraAtributos;

  /************************************
   * Constroi comp bib vazio
   */
  public  Componente_Biblioteca() {
  }


  /***********************************************************
   * Cria componente biblioteca com a informacao recebida
   * @param n_entradas numero de entradas
   * @param n_saidas numero de saidas 
   * @param nome nome do componente
   * @param funcao_desenho funcao de desenho
   * @param largura_x comprimento x do componente
   * @param largura_y comprimento y do componente
   * @param p_e posicao das entradas
   * @param p_s posicao das saidas
   * @param nomes_ nomes das variaveis de entrada
   * @param nomes_s nomes das variaveis de saida
   * 
   */
  public  Componente_Biblioteca(int n_entradas,int n_saidas,
      String nome,String funcao_desenho,
      int largura_x,int largura_y,
      Point p_e[],Point p_s[],
      ArrayList<String> nomes_, ArrayList<String> nomes_s, ArrayList<String> nomes_desc, ArrayList<String> nomes_s_desc,
      ArrayList<Atributo> attrs, String nomeClasse, String nomeFile, String description, boolean automatic, Color color, String descrKey) {

    init(n_entradas,n_saidas,nome,
        funcao_desenho,largura_x,largura_y,
        p_e,p_s,nomes_,nomes_s,nomes_desc,
        nomes_s_desc, attrs,nomeClasse, nomeFile, description, automatic,color, descrKey);
  }


  /******************************************
   * Constroi comp bib com algumas variaveis
   * @param n_entradas numero de entradas
   * @param n_saidas numero de saidas
   * @param nome nome do componente
   */
  public  Componente_Biblioteca(int n_entradas,int n_saidas,String nome) {
    Num_funcao_Desenho=1;
    Num_Entradas=n_entradas;
    Num_Saidas=n_saidas;
    Nome=nome;
  }



  /*************************************************
   * funcao que inicializa as variaveis da classe
   *
   * @param n_entradas numero de entradas
   * @param n_saidas numero de saidas
   * @param nome nome do componente
   * @param num_funcao_desenho funcao de desenho
   * @param saidas array com funcoes para calcular saidas
   * @param largura_x comprimento x do componente
   * @param largura_y comprimento y do componente
   * @param p_e posicao das entradas
   * @param p_s posicao das saidas
   * @param nomes_ nomes das variaveis de entrada
   * @param nomes_s nomes das variaveis de saida
   * @param PinInfo vector com informacao sobre os pins
   */
  private void init(int n_entradas,int n_saidas, String nome,
      String funcao_desenho, int largura_x,int largura_y,
      Point p_e[],Point p_s[], ArrayList<String> nomes_, ArrayList<String> nomes_s, ArrayList<String> nomes_desc,
      ArrayList<String> nomes_s_desc, ArrayList<Atributo> attrs, String nomeClasse, String nomeFile, String description,
      boolean automatic, Color color, String descrKey) {
    this.color = color;
    /* atributos */
    nomeClasseAlteraAtributos=nomeClasse;
    // nomeFicheiro - Deprecated!
    NomesAtributos = new ArrayList<Atributo>(attrs);

    /* informcao grafica */
    Pontos_Entrada=p_e;
    Pontos_Saida=p_s;
    Largura_X=largura_x;

    /* entradas / saidas */
    Num_Entradas=n_entradas;
    Num_Saidas=n_saidas;
    Nome=nome;
    Descricao = description;
    this.descrKey = descrKey;
    
    // O bloco start e catalogo sao sempre automaticos, mesmo que nao tenha sido indicado no descritor
    this.automatic = (automatic || StringUtilities.isEqual(Nome, "BlockStart"));


    /* copiar nomes */
    nomes_entradas = new ArrayList<String>();
    desc_nomes_entradas = new ArrayList<String>();
    for (int i=0;i<n_entradas;i++) {
      String st=nomes_.get(i);
      nomes_entradas.add(st);
      desc_nomes_entradas.add(nomes_desc.get(i));
    }
    nomes_saidas = new ArrayList<String>();
    desc_nomes_saidas = new ArrayList<String>();
    for (int i=0;i<n_saidas;i++) {
      String st=nomes_s.get(i);
      nomes_saidas.add(st);
      desc_nomes_saidas.add(nomes_s_desc.get(i));
    }



    /* largura Y */
    Largura_Y=largura_y;
    //this.funcao_Desenho=funcao_desenho;

    /* desenho */
    try{
      if (funcao_desenho.endsWith(";")) //$NON-NLS-1$
        Num_funcao_Desenho=Integer.parseInt(
            funcao_desenho.substring(0,
                funcao_desenho.length()-1));
      else
        Num_funcao_Desenho=Integer.parseInt(funcao_desenho);

      this.funcao_Desenho=null;

    }
    catch(Exception e) {
      try{
        if (funcao_desenho.endsWith(";")) //$NON-NLS-1$
          this.funcao_Desenho = Janela.getInstance().createImage(funcao_desenho.substring(0,funcao_desenho.length()-1), true);
        else
          this.funcao_Desenho=Janela.getInstance().createImage(funcao_desenho, true, color);
        this.Largura_X=funcao_Desenho.getWidth(Desenha_Componente.ppanel);
        this.Largura_Y=funcao_Desenho.getHeight(Desenha_Componente.ppanel);
      }
      catch (Exception e2) {
        Num_funcao_Desenho=1;
        funcao_Desenho=null;
      }
    }
  }




  /********************************************
   * funcao que desenha o componente
   * @param g instancia da classe Graphics
   * @param Posicao_X posicao x inicial
   * @param Posicao_Y posicao y inicial
   * @param c cor do componente
   */
  public void pinta(Graphics g,int Posicao_X,int Posicao_Y,java.awt.Color c, java.awt.Color activo) {
    Desenha_Componente.Desenho_Componente(Num_funcao_Desenho,funcao_Desenho, g,Posicao_X,Posicao_Y,this,c, activo);
  }
  public void pinta(Graphics g,int Posicao_X,int Posicao_Y,java.awt.Color c, java.awt.Color activo, JPanel jp) {
    Desenha_Componente.Desenho_Componente(Num_funcao_Desenho,funcao_Desenho, g,Posicao_X,Posicao_Y,this,c, activo,jp);
  }


  /*********************************************************
   * Cria um componente cujo este e o Componente_Biblioteca
   * @return componente criado
   */
  public InstanciaComponente cria(Desenho desenho) {
    int num = desenho.incBlockNumber();
    String nome=Mesg.Comp+num;
    /* componente simples */
    return new InstanciaComponente(this,nome,num);
  }


  public boolean E_Gate() { return true; }
  public boolean E_Composto() { return false; }
  public boolean E_Complexo() { return false; }
  public boolean E_Sequencial() { return false; }

  public int Posicao_Entrada(String nomeSinal) {
    for (int i=0;i<nomes_entradas.size();i++) {
      String nome=(String)nomes_entradas.get(i);
      if (nome.equals(nomeSinal))
        return i;
    }
    return -1;
  }
  public int Posicao_Saida(String nomeSinal) {
    for (int i=0;i<nomes_saidas.size();i++) {
      String nome=(String)nomes_saidas.get(i);
      if (nome.equals(nomeSinal))
        return i;
    }
    return -1;
  }

  /***************************/
  public String toString() {
    return Nome;
  }

  public boolean isAutomatic() {
    return this.automatic;
  }
  
  public boolean isStart() {
    return StringUtilities.isEqual(Nome, "BlockStart");
  }
  
  public String getClassAlteraAtributos() {
    return this.nomeClasseAlteraAtributos;
  }
}

