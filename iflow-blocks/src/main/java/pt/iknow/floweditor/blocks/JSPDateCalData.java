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

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp date data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPDateCalData extends JSPFieldData {


  // id constructor
  public JSPDateCalData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPDateCalData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }


  public JSPDateCalData(JSPFieldData afdData) {
    super(afdData);
  }

  private String getDescription() {
    String description = "Date";
    try {
      Class<?> c = Class.forName("pt.iflow.api.datatypes.Date", true, getClass().getClassLoader());
      DataTypeInterface dti = (DataTypeInterface) c.newInstance();
      description = dti.getDescription();
    } catch (Exception e) {
    }
    return description;
  }

  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_DATECAL;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DATE_FORMAT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_HOUR_FORMAT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CURRDATE_IF_EMPTY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ONCHANGE_SUBMIT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD));

    // add static/constant properties
    this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, getDescription());

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_DATE_FORMAT));

    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_DATE_FORMAT),
        new Integer(JSPFieldData.nDATE_FORMAT));

  }


}
