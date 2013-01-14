package pt.iknow.floweditor.blocks;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp text label data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPSQLLabelData extends JSPFieldData {


  // id constructor
  public JSPSQLLabelData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPSQLLabelData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  public JSPSQLLabelData(JSPFieldData afdData) {
    super(afdData);
  }

  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_SQL_LABEL;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_QUERY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DATASOURCE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_EMPTY_NOT_ALLOWED));

    
    // add static/constant properties
    DataTypeInterface dti = loadDataType(adapter,"pt.iflow.api.datatypes.Text");
    if(dti != null)
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, dti.getDescription());
    else
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, "Text");

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_QUERY));
  }


}
