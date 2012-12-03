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
 * Class that contains the jsp table data (only contains multiple properties) 
 *
 * @see JSPFieldData
 */
public  class JSPTableData extends JSPFieldData {


  // id constructor
  public JSPTableData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPTableData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  // full constructor
  public JSPTableData(FlowEditorAdapter adapter, int anID,
		      int anPosition) {
    this(adapter, anID);
    this._nPosition = anPosition;
  }
  

  public JSPTableData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_TABLE;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_SERVICE_PRINT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_SERVICE_EXPORT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ROW_CONTROL_LIST));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_TABLE_HEADER));

    // add table multiple properties
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_MACRO_TITLE));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_TITLE));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_DATA_TYPE));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_ALIGNMENT));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_EXTRA_PROPS));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY));


    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TITLE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_DATA_TYPE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_ALIGNMENT));
    
    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_DATA_TYPE),
			  new Integer(JSPFieldData.nDATA_TYPE));
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_ALIGNMENT),
			  new Integer(JSPFieldData.nDATA_TYPE));

    // now disable data types (if applicable and if data type prop exists)
  }


}
