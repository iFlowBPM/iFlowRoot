package pt.iknow.floweditor.blocks;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp password data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPPasswordData extends JSPFieldData {


  // id constructor
  public JSPPasswordData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }
  
  // simple constructor
  public JSPPasswordData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }
  
  
  // full constructor
  public JSPPasswordData(FlowEditorAdapter adapter, int anID,
			 int anPosition,
			 String asText,
			 String asVarName,
			 int anBoxWidth,
			 int anPasswordWidth) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
    this.setProperty(JSPFieldData.nPROP_TEXT,asText);
    this.setProperty(JSPFieldData.nPROP_VAR_NAME,asVarName);
    this.setProperty(JSPFieldData.nPROP_SIZE,String.valueOf(anBoxWidth));
    this.setProperty(JSPFieldData.nPROP_MAXLENGTH,
		     String.valueOf(anPasswordWidth));
  }


  public JSPPasswordData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_PASSWORD;

    // add password single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_SIZE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_MAXLENGTH));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VALIDATION_EXPR));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VALIDATION_MSG));
    

    // add static/constant properties
    DataTypeInterface dti = loadDataType(adapter, "pt.iflow.api.datatypes.Text");
    if(dti != null)
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, dti.getDescription());
    else
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, "Text");


    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_SIZE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_MAXLENGTH));

    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_SIZE),
			  new Integer(JSPFieldData.nPOSITIVE_NUMBER));
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_MAXLENGTH),
			  new Integer(JSPFieldData.nPOSITIVE_NUMBER));

    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_COLS),
              new Integer(JSPFieldData.nPOSITIVE_NUMBER));
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_ROWS),
              new Integer(JSPFieldData.nPOSITIVE_NUMBER));

    // add prop dependencies
    PropDependency pd = new PropDependency(JSPFieldData.nPROP_OBLIGATORY_FIELD,
            PropDependency.nDISABLE,
            PropDependency.nTRUE);
    PropDependencyItem pdi = null;
    pdi = new PropDependencyItem(JSPFieldData.nPROP_VALIDATION_EXPR, PropDependency.nDISABLE);
    pd.addDependency(pdi);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_VALIDATION_MSG, PropDependency.nDISABLE);
    pd.addDependency(pdi);
    this._hmPropDependencies.put(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD), pd);
    
  }


}
