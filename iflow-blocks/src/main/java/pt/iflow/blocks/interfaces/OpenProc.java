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
package pt.iflow.blocks.interfaces;


/**
 * Constants shared between BlockOpenProc and AlteraAtributosOpenProc
 * 
 * @author ombl
 *
 */
public interface OpenProc {

  public static final String PASS_THRU = "passThru"; //$NON-NLS-1$
  
  public static final String JUMP_TO = "jumpTo"; //$NON-NLS-1$
  
  public static final String[] openProcessModes = {PASS_THRU, JUMP_TO};

  
  // check with editor pt.iknow.iflow.iflow.editor.blocks.AlteraAtributosOpenProc
  public static final String[] openProcessTypes = {"Importar Existente", "Abrir Novo"};
  public static final String[] userModes = {"Criador","Perfil","PerfilTexto","Utilizador"};
  public static final String VAR_SEPARATOR=",";

}
