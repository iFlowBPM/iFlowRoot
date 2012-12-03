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
