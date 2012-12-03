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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import pt.iflow.api.blocks.FormProps;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;

public class AlteraAtributoDetail extends AlteraAtributosJSP {

  private static final long serialVersionUID = -7579180185992578050L;

  public AlteraAtributoDetail(FlowEditorAdapter adapter) {
    super(adapter);
    jButtonAddButton.setEnabled(false);
    jButtonAddButton.setVisible(false);
  }


  public void setDataIn(String title, List<Atributo> atributos) {
    ArrayList<Atributo> novosAtributos = new ArrayList<Atributo>(atributos.size());
    ArrayList<String> botoes = new ArrayList<String>(2);

    Pattern p = Pattern.compile(FormProps.sBUTTON_ATTR_PREFIX+"\\d+_"+FormProps.sBUTTON_ATTR_TYPE); //$NON-NLS-1$

    for (int i=0;i<atributos.size(); i++) {
      Atributo a = atributos.get(i);
      String sName = a.getDescricao();
      String sVal = a.getValor();

      if (p.matcher(sName).matches()) {  // ignore existing buttons
        botoes.add(sVal);
      }
      novosAtributos.add(a);
    }

    // add one button: Print
    if(!botoes.contains(sPRINT_TYPE)) {
      novosAtributos.addAll(getButtonAttributes(sPRINT_TYPE, "0")); //$NON-NLS-1$
    }

    // set required buttons
    hsREQ_BUTTONS.add(sPRINT_TYPE);

    super.setDataIn(title, novosAtributos);
  }

  Collection<Atributo> getButtonAttributes(String buttonType, String buttonPos) {
    Collection<Atributo> novosAtributos = new ArrayList<Atributo>(12);
    String sPrefix = FormProps.sBUTTON_ATTR_PREFIX + buttonPos+"_"; //$NON-NLS-1$
    String key = null;
    key = sPrefix + FormProps.sBUTTON_ATTR_ID;
    novosAtributos.add(adapter.newAtributo(key, buttonPos, key));
    key = sPrefix + FormProps.sBUTTON_ATTR_POSITION;
    novosAtributos.add(adapter.newAtributo(key, buttonPos, key));
    key = sPrefix + FormProps.sBUTTON_ATTR_TYPE;
    novosAtributos.add(adapter.newAtributo(key, buttonType, key));
    key = sPrefix + FormProps.sBUTTON_ATTR_TEXT;
    novosAtributos.add(adapter.newAtributo(key, hmBUTTON_TYPES.get(buttonType), key));
    key = sPrefix + FormProps.sBUTTON_ATTR_TOOLTIP;
    novosAtributos.add(adapter.newAtributo(key, "", key)); //$NON-NLS-1$
    key = sPrefix + FormProps.sBUTTON_ATTR_IMAGE;
    novosAtributos.add(adapter.newAtributo(key, "", key)); //$NON-NLS-1$
    key = sPrefix + FormProps.sBUTTON_ATTR_CUSTOM_VAR;
    novosAtributos.add(adapter.newAtributo(key, "", key)); //$NON-NLS-1$
    key = sPrefix + FormProps.sBUTTON_ATTR_CUSTOM_VALUE;
    novosAtributos.add(adapter.newAtributo(key, "", key)); //$NON-NLS-1$
    key = sPrefix + FormProps.sBUTTON_ATTR_SHOW_COND;
    novosAtributos.add(adapter.newAtributo(key, "", key)); //$NON-NLS-1$
    key = sPrefix + FormProps.sBUTTON_ATTR_IGNORE_FORM_PROCESSING;
    novosAtributos.add(adapter.newAtributo(key, "", key)); //$NON-NLS-1$
    key = sPrefix + FormProps.sBUTTON_ATTR_CONFIRM_ACTION;
    novosAtributos.add(adapter.newAtributo(key, "", key)); //$NON-NLS-1$
    key = sPrefix + FormProps.sBUTTON_ATTR_CONFIRM_ACTION_MESSAGE;
    novosAtributos.add(adapter.newAtributo(key, "", key)); //$NON-NLS-1$
    key = sPrefix + FormProps.sBUTTON_ATTR_IGNORE_FORM_VALIDATION;
    novosAtributos.add(adapter.newAtributo(key, "", key)); //$NON-NLS-1$
    return novosAtributos;
  }

}
