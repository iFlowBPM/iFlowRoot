package pt.iknow.floweditor.blocks;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp fixed table data
 *
 * @see JSPFieldData
 */
public  class JSPFixedTableData extends JSPFieldData {


  // id constructor
  public JSPFixedTableData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPFixedTableData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  // full constructor
  public JSPFixedTableData(FlowEditorAdapter adapter, int anID,
			   int anPosition,
			   String asText) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
    this.setProperty(JSPFieldData.nPROP_TEXT_AREA,asText);
  }


  public JSPFixedTableData(JSPFieldData afdData) {
    super(afdData);
  }




  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_FIXED_TABLE;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT_AREA));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));


    // add static/constant properties
    DataTypeInterface dti = loadDataType(adapter, "pt.iflow.api.datatypes.Text");
    if(dti != null)
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, dti.getDescription());
    else
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, "Text");
    
    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT_AREA));
  }


}
