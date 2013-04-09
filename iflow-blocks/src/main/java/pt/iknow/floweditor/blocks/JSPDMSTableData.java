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
