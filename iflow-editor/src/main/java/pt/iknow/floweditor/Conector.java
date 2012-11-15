package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Conector
 *
 *  desc: guarda informação sobre uma ligacao entre
 *        componentes
 *
 ****************************************************/

/**************************
 * Conector
 */
public class Conector {
    /* componente */
    public Componente Comp=null;
    /* numero da entrada/saida */
    public int Numero;
    
    /********************************
     * Construtor
     * constroi Conector sem ligacao
     */
    public Conector() {
        Comp=null;
        Numero=0;
    }
    
    /***********************************************************
     * Constroi um Conector com o componente e numero recebidos
     * @param c componente
     * @param num numero da entrada/saida
     */
    public Conector(Componente c, int num) {
        Comp=c;
        Numero=num;
    }
    
    /***********************************************
     * Altera os valores guardados
     * @param c componente
     * @param num numero da entrada/saida
     */
    public void Set_Conector(Componente c, int num) {
        Comp=c;
        Numero=num;
    }
    
}
