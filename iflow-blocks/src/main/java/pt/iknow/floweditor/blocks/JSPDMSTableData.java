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

public class JSPDMSTableData extends JSPFieldData {

	// id constructor
	public JSPDMSTableData(FlowEditorAdapter adapter, int anID) {
		super(adapter, anID);
	}

	// simple constructor
	public JSPDMSTableData(FlowEditorAdapter adapter) {
		this(adapter, -1);
	}

	// full constructor
	public JSPDMSTableData(FlowEditorAdapter adapter, int anID, int anPosition) {
		this(adapter, anID);
		this._nPosition = anPosition;
	}

	public JSPDMSTableData(JSPFieldData afdData) {
		super(afdData);
	}

	protected void init() {
		this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_DMS_TABLE;

		// add text single properties
//		this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_SERVICE_PRINT));
//		this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_SERVICE_EXPORT));
//		this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ROW_CONTROL_LIST));
		this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
//		this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_TABLE_HEADER));
		this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DMS_TITLES));
        this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DMS_VARS));
        this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DMS_ALIGN));
        this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DMS_FOLDER));

		// add required properties
        this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_DMS_TITLES));
        this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_DMS_VARS));
		this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_DMS_FOLDER));
	}
}
