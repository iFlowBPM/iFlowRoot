package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp image data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPImageData extends JSPFieldData {


  // id constructor
  public JSPImageData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPImageData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  public JSPImageData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_IMAGE;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_URL));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ALT_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ALIGNMENT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_WIDTH));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_HEIGHT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));


    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_URL));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_ALIGNMENT));

    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_ALIGNMENT),
			  new Integer(JSPFieldData.nALIGNMENT));

    // add prop dependencies
  }
}
