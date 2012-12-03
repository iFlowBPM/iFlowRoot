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
