package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;

/**
 * Class that contains the jsp form template (only contains single properties)
 * 
 * @see JSPFieldData
 */
public class JSPDocumentPreview extends JSPFieldData {

  // id constructor
  public JSPDocumentPreview(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPDocumentPreview(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  // full constructor
  public JSPDocumentPreview(FlowEditorAdapter adapter, int anID, int anPosition, String asText) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
    this.setProperty(JSPFieldData.nPROP_TEXT, asText);
  }

  public JSPDocumentPreview(JSPFieldData afdData) {
    super(afdData);
  }

  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_DOCUMENT_PREVIEW;

    nTEXT_SIZE = 25;

    // add header single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
  }

  public String[] exportToTableRow() {
    String[] ret = new String[this._alTableProps.size()];
    ret[3] = this.getProperty(JSPFieldData.nPROP_VAR_NAME);
    ret[1] = this.getFieldTypeText();
    return ret;
  }
}
