package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp sub header data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPSubHeaderData extends JSPFieldData {


  // id constructor
  public JSPSubHeaderData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPSubHeaderData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }
  
  
  // full constructor
  public JSPSubHeaderData(FlowEditorAdapter adapter, int anID,
		       int anPosition,
		       String asText) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
    this.setProperty(JSPFieldData.nPROP_TEXT,asText);
  }


  public JSPSubHeaderData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_SUBHEADER;

    nTEXT_SIZE = 25;

    // add header single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
  }


}
