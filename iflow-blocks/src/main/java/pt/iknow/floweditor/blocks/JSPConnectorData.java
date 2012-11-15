package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp external connector data.
 *
 * @see JSPFieldData
 */
public  class JSPConnectorData extends JSPFieldData {


  // id constructor
  public JSPConnectorData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPConnectorData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }


  public JSPConnectorData(JSPFieldData afdData) {
    super(afdData);
  }




  // XXX removed "Class" info
  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_CONNECTOR;

    // add select single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CONNECTOR_SELECT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DEFAULT_VALUE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DEFAULT_TEXT));
//    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CLASSE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ARGS));
//    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VALUE_KEY));
//    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT_KEY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ONCHANGE_SUBMIT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT_VALUE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
//    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CLASSE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CONNECTOR_SELECT));
//    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VALUE_KEY));
//    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT_KEY));


    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_CONNECTOR_SELECT),
        new Integer(JSPFieldData.nNOT_CHOSEN));
  }


}
