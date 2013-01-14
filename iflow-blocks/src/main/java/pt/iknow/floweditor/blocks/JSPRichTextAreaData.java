package pt.iknow.floweditor.blocks;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;

/**
 * Class that contains the jsp text area data [for rich text] (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPRichTextAreaData extends JSPFieldData {
  // id constructor
  public JSPRichTextAreaData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPRichTextAreaData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }


  // full constructor
  public JSPRichTextAreaData(FlowEditorAdapter adapter, int anID, int anPosition, String asText, String asVarName, int anCols, int anRows) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
    this.setProperty(JSPFieldData.nPROP_TEXT,asText);
    this.setProperty(JSPFieldData.nPROP_VAR_NAME,asVarName);

  }

  public JSPRichTextAreaData(JSPFieldData afdData) {
    super(afdData);
  }

  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_RICH_TEXT_AREA;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    // INI - DIMENSIONS
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_WIDTH));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_HEIGHT));
    // FIM - DIMENSIONS
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY));

    // add static/constant properties
    DataTypeInterface dti = loadDataType(adapter, "pt.iflow.api.datatypes.Text");
    if(dti != null) {
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, dti.getDescription());
    } else {
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, "Text");
    }

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_COLS));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_ROWS));

    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_COLS), new Integer(JSPFieldData.nPOSITIVE_NUMBER_PERCENT));
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_ROWS), new Integer(JSPFieldData.nPOSITIVE_NUMBER));

    // add prop dependencies

  }
}