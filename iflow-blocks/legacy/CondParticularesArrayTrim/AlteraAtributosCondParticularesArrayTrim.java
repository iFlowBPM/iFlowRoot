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
package pt.iknow.floweditor.blocks;

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para editar e criar validações </p></p>
 * <p>  condição | mensagem de erro
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow </p>
 * @author João Valentim
 * @version 1.0
 */

import javax.swing.JFrame;

import pt.iknow.floweditor.messages.Messages;

public class AlteraAtributosCondParticularesArrayTrim extends AlteraAtributosArrayTrim implements
AlteraAtributosInterface {

  private static final long serialVersionUID = 3725359039127895414L;

  //  private static final String[] columnNames = { "VarsTeste", "VarsTrim", "VarControlo" };
  //
  //  protected String[] getColumnNames() {
  //    return columnNames;
  //  }

  public AlteraAtributosCondParticularesArrayTrim(JFrame janela) {
    super(janela, Messages.getString("AlteraAtributosCondParticularesArrayTrim.title"), true);
  }
}
