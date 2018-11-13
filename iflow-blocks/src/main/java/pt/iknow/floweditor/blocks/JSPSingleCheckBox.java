package pt.iknow.floweditor.blocks;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp select data (contains both single and multiple 
 * properties)
 *
 * @see JSPFieldData
 */
public  class JSPSingleCheckBox extends JSPFieldData {


  // id constructor
  public JSPSingleCheckBox(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPSingleCheckBox(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  // full constructor
  public JSPSingleCheckBox(FlowEditorAdapter adapter, int anID,
		       int anPosition,
		       String asText,
		       String asVarName) {
    this(adapter, anID);
    this._nPosition = anPosition;
    
    // now set all field properties
    this.setProperty(JSPFieldData.nPROP_TEXT,asText);
    this.setProperty(JSPFieldData.nPROP_VAR_NAME,asVarName);
  }


  public JSPSingleCheckBox(JSPFieldData afdData) {
    super(afdData);
  }




  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_SINGLECHECKBOX;

    // add select single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));    
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY));

    // add static/constant properties
    DataTypeInterface dti = loadDataType(adapter, "pt.iflow.api.datatypes.Text");
    if(dti != null)
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, dti.getDescription());
    else
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, "Text");

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    
    // set non-string properties types
    

  }


}
