package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp separator data (does not contain any relevant 
 * property)
 *
 * @see JSPFieldData
 */
public  class JSPSeparatorData extends JSPFieldData {


  // id constructor
  public JSPSeparatorData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPSeparatorData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  public JSPSeparatorData(FlowEditorAdapter adapter, int anID,
			  int anPosition) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
  }


  public JSPSeparatorData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_SEPARATOR;

    // add spacer properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
  }


}
