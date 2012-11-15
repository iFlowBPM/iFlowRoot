package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp column division data (does not contain any relevant 
 * property)
 *
 * @see JSPFieldData
 */
public  class JSPColumnDivisionData extends JSPFieldData {


  // id constructor
  public JSPColumnDivisionData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPColumnDivisionData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }


  public JSPColumnDivisionData(FlowEditorAdapter adapter, int anID,
      int anPosition) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
  }


  public JSPColumnDivisionData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_COLUMN_DIVISION;

    // add spacer properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_COL_WIDTH_PERCENT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_COL_WIDTH_PERCENT),
        new Integer(JSPFieldData.nPOSITIVE_NUMBER));

  }


}
