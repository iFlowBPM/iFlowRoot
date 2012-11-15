package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp spacer data (does not contain any relevant 
 * property)
 *
 * @see JSPFieldData
 */
public  class JSPSpacerData extends JSPFieldData {


  // id constructor
  public JSPSpacerData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPSpacerData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  public JSPSpacerData(FlowEditorAdapter adapter, int anID,
		       int anPosition) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
  }


  public JSPSpacerData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_SPACER;

    // add spacer properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
  }


}
