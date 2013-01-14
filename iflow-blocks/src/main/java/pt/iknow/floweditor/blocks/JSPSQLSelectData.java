package pt.iknow.floweditor.blocks;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp sql select data.
 *
 * @see JSPFieldData
 */
public  class JSPSQLSelectData extends JSPFieldData {


  // id constructor
  public JSPSQLSelectData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPSQLSelectData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  public JSPSQLSelectData(JSPFieldData afdData) {
    super(afdData);
  }




  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_SQL_SELECT;

    
    // add select single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DEFAULT_VALUE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DEFAULT_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_QUERY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DATASOURCE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ONCHANGE_SUBMIT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT_VALUE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CACHE_HINT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));


    // add static/constant properties
    DataTypeInterface dti = loadDataType(adapter, "pt.iflow.api.datatypes.Text");
    if(dti != null)
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, dti.getDescription());
    else
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, "Text");

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_QUERY));
   

    // set non-string properties types
  }


}
