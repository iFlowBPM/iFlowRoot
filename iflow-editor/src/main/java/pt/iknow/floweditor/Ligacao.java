package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Ligacao
 *
 *  desc: guarda dois pontos
 *
 ****************************************************/


public class Ligacao {
    /* valore a guardar */
    public int c1,n1,c2,n2;
    
    
    /********************************************************
     * Constroi uma Ligacao com os valores recebidos
     * @param cc1 valor da posiaco relativa do 1o componente
     * @param nn1 valor da sua entrada/saida
     * @param cc2 valor da posiaco relativa do 2o componente
     * @param nn2 valor da sua saida/entrada
     */
    public Ligacao(int cc1,int nn1,int cc2, int nn2) {
        c1=cc1;
        n1=nn1;
        c2=cc2;
        n2=nn2;
    }
}
