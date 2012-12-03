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

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp external connector data.
 *
 * @see JSPFieldData
 */
public  class JSPConnectorData extends JSPFieldData {


  // id constructor
  public JSPConnectorData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPConnectorData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }


  public JSPConnectorData(JSPFieldData afdData) {
    super(afdData);
  }




  // XXX removed "Class" info
  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_CONNECTOR;

    // add select single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CONNECTOR_SELECT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DEFAULT_VALUE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DEFAULT_TEXT));
//    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CLASSE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ARGS));
//    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VALUE_KEY));
//    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT_KEY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ONCHANGE_SUBMIT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT_VALUE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
//    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CLASSE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CONNECTOR_SELECT));
//    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VALUE_KEY));
//    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT_KEY));


    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_CONNECTOR_SELECT),
        new Integer(JSPFieldData.nNOT_CHOSEN));
  }


}
