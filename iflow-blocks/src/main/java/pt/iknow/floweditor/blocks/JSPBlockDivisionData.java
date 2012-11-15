package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp block division data (does not contain any relevant 
 * property)
 *
 * @see JSPFieldData
 */
public  class JSPBlockDivisionData extends JSPFieldData {


  // id constructor
  public JSPBlockDivisionData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPBlockDivisionData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }


  public JSPBlockDivisionData(FlowEditorAdapter adapter, int anID,
      int anPosition) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
  }


  public JSPBlockDivisionData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_BLOCK_DIVISION;

    // add spacer properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
  }


}
