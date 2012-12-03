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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.ComboCellEditor;
import pt.iknow.utils.swing.MyColumnEditorModel;

public class AlteraAtributosCriarDocumento extends AlteraAtributos implements AlteraAtributosInterface {

  /** Generated serial version UID. */
  private static final long serialVersionUID = 7651187918085954486L;

  protected static final String TEMPLATE = "template";
  protected static final String VARIABLE = "variable";
  protected static final String FILENAME = "filename";
  
  public AlteraAtributosCriarDocumento(FlowEditorAdapter adapter) {
    super(adapter);
  }

  void customizeTable(List<Atributo> atributos) {
    super.customizeTable(atributos);
    MyColumnEditorModel cm = jTable1.getMyColumnEditorModel();
    String [] dataSources = adapter.getRepository().listPrintTemplates();
    
    for (int x = 0; x < atributos.size(); x++) {
      Atributo at = atributos.get(x);
      if (StringUtils.equals(TEMPLATE, at.getNome())) {
        cm.addEditorForCell(x, 1, new ComboCellEditor(dataSources, true));
        break;
      }
    }
  }
}
