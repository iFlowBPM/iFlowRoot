package pt.iknow.floweditor.blocks;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp text data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPTextData extends JSPFieldData {


  // id constructor
  public JSPTextData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPTextData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  
  // full constructor
  public JSPTextData(FlowEditorAdapter adapter, int anID,
		     int anPosition,
		     String asText,
		     String asDataType,
		     String asVarName,
		     int anBoxWidth,
		     int anTextWidth) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
    this.setProperty(JSPFieldData.nPROP_TEXT,asText);
    this.setProperty(JSPFieldData.nPROP_DATA_TYPE,asDataType);
    this.setProperty(JSPFieldData.nPROP_VAR_NAME,asVarName);
    this.setProperty(JSPFieldData.nPROP_SIZE,String.valueOf(anBoxWidth));
    this.setProperty(JSPFieldData.nPROP_MAXLENGTH,
		     String.valueOf(anTextWidth));
  }


  public JSPTextData(JSPFieldData afdData) {
    super(afdData);
  }




  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_TEXT;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DATA_TYPE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_SIZE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_MAXLENGTH));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VALIDATION_EXPR));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VALIDATION_MSG));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT_SUBMIT_ON_BLUR));

    // add required properties
    //this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_DATA_TYPE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_SIZE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_MAXLENGTH));
   

    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_DATA_TYPE),
			  new Integer(JSPFieldData.nDATA_TYPE));
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_SIZE),
			  new Integer(JSPFieldData.nPOSITIVE_NUMBER));
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_MAXLENGTH),
			  new Integer(JSPFieldData.nPOSITIVE_NUMBER));
    
    // now disable data types (if applicable and if data type prop exists)
    DataTypeInterface dti = null;
    
    dti = loadDataType(adapter, "pt.iflow.api.datatypes.CheckBox");
    if(dti != null)
    this._hsDisableDataTypes.add(dti.getID());
    
    dti = loadDataType(adapter, "pt.iflow.api.datatypes.RadioButton");
    if(dti != null)
      this._hsDisableDataTypes.add(dti.getID());
    
    dti = loadDataType(adapter, "pt.iflow.api.datatypes.Link");
    if(dti != null)
      this._hsDisableDataTypes.add(dti.getID());
    
    dti = loadDataType(adapter, "pt.iflow.api.datatypes.FormTableText");
    if(dti != null)
      this._hsDisableDataTypes.add(dti.getID());
    
    // add prop dependencies
    PropDependency pd = new PropDependency(JSPFieldData.nPROP_OUTPUT_ONLY, 
                       PropDependency.nENABLE, 
                       PropDependency.nTRUE);
    PropDependencyItem pdi = null;
    pdi = new PropDependencyItem(JSPFieldData.nPROP_OBLIGATORY_FIELD, PropDependency.nDISABLE);
    pd.addDependency(pdi);
    this._hmPropDependencies.put(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY), pd);
    
    pd = new PropDependency(JSPFieldData.nPROP_OBLIGATORY_FIELD,
            PropDependency.nENABLE,
            PropDependency.nTRUE);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_VALIDATION_EXPR, PropDependency.nENABLE);
    pd.addDependency(pdi);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_VALIDATION_MSG, PropDependency.nENABLE);
    pd.addDependency(pdi);
    this._hmPropDependencies.put(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD), pd);

  }


}
