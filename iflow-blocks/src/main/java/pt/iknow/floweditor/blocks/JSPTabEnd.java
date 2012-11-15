package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp block division data (does not contain any relevant 
 * property)
 *
 * @see JSPFieldData
 */
public  class JSPTabEnd extends JSPFieldData {


  // id constructor
  public JSPTabEnd(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPTabEnd(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  public JSPTabEnd(FlowEditorAdapter adapter, int anID,
			  int anPosition) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
  }


  public JSPTabEnd(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_TAB_END;

    // add spacer properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
  }


}
