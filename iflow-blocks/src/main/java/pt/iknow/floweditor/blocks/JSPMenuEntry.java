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

import java.util.Map;

import pt.iknow.floweditor.FlowEditorAdapter;

/**
 * Class that contains the jsp button data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPMenuEntry extends JSPFieldData {

  // id constructor
  public JSPMenuEntry(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPMenuEntry(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }
  
  public JSPMenuEntry(JSPFieldData afdData) {
    super(afdData);
  }

  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_MENU;

    loadButtonTypes();

    // Permite assinar documentos (escolhe o tipo de assinatura)
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_TYPE));
    // Se assinatura seleccionada, liga as duas abaixo
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_TOOLTIP));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_IMAGE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_SHOW_CONDITION));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_IGNORE_FORM_PROCESSING));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_IGNORE_FORM_VALIDATION));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CONFIRM_ACTION));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CONFIRM_MESSAGE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VARIABLE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VALUE));

    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VALUE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VARIABLE));

    Map<String, String> buttonTypeMap = getButtonTypeMap();
    PropDependency pd = new PropDependency(JSPFieldData.nPROP_BUTTON_TYPE, PropDependency.nENABLE, PropDependency.nEMPTY_OR_VALUE, buttonTypeMap.get(AlteraAtributosJSP.sCUSTOM_TYPE));
    PropDependencyItem pdi = new PropDependencyItem(JSPFieldData.nPROP_BUTTON_CUSTOM_VARIABLE, PropDependency.nENABLE);
    pd.addDependency(pdi);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_BUTTON_CUSTOM_VALUE, PropDependency.nENABLE);
    pd.addDependency(pdi);
    this._hmPropDependencies.put(new Integer(JSPFieldData.nPROP_BUTTON_TYPE), pd);
  }

}
