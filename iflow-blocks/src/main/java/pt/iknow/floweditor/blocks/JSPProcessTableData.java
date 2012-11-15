package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp table data (only contains multiple properties) 
 *
 * @see JSPFieldData
 */
public  class JSPProcessTableData extends JSPFieldData {


  // id constructor
  public JSPProcessTableData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPProcessTableData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  // full constructor
  public JSPProcessTableData(FlowEditorAdapter adapter, int anID,
		      int anPosition) {
    this(adapter, anID);
    this._nPosition = anPosition;
  }
  

  public JSPProcessTableData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_PROCESS_TABLE;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_PP_RESULT_ARRAY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TABLE_SIZE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_PP_PREFETCH));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_USE_LINKS));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_LINK_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_LINK_LABEL));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_SERVICE_PRINT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_SERVICE_EXPORT));
    
    
    // add table multiple properties
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_TITLE));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_DATA_TYPE));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_ALIGNMENT));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_EXTRA_PROPS));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_PP_PASS_TO_LINK));


    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_PP_RESULT_ARRAY));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TITLE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_DATA_TYPE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_ALIGNMENT));
    
    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_ALIGNMENT),
			  new Integer(JSPFieldData.nDATA_TYPE));

  }


}
