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
 * Class that contains the jsp text label data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPExpressionLabelData extends JSPFieldData {


  // id constructor
  public JSPExpressionLabelData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPExpressionLabelData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  // full constructor
  public JSPExpressionLabelData(FlowEditorAdapter adapter, int anID,
			  int anPosition,
			  String asText,
			  String asDataType,
			  String asVarName) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
    this.setProperty(JSPFieldData.nPROP_TEXT,asText);
    this.setProperty(JSPFieldData.nPROP_DATA_TYPE,asDataType);
    this.setProperty(JSPFieldData.nPROP_VAR_NAME,asVarName);
  }


  public JSPExpressionLabelData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_EXPRESSION_LABEL;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DATA_TYPE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_EXPRESSION));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_EMPTY_NOT_ALLOWED));

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_DATA_TYPE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_EXPRESSION));

    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_DATA_TYPE),
              new Integer(JSPFieldData.nDATA_TYPE));


    // now disable data types (if applicable and if data type prop exists)
    DataTypeInterface dti = null;
    
    dti = loadDataType(adapter, "pt.iflow.api.datatypes.CheckBox");
    if(dti != null)
    this._hsDisableDataTypes.add(dti.getID());
    
    dti = loadDataType(adapter, "pt.iflow.api.datatypes.RadioButton");
    if(dti != null)
      this._hsDisableDataTypes.add(dti.getID());
    
    dti = loadDataType(adapter, "pt.iflow.api.datatypes.Link");
    if(dti != null)
      this._hsDisableDataTypes.add(dti.getID());
    
    dti = loadDataType(adapter, "pt.iflow.api.datatypes.FormTableText");
    if(dti != null)
      this._hsDisableDataTypes.add(dti.getID());

  }


}
