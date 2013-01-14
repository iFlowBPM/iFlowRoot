package pt.iknow.floweditor.blocks;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp link data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPLinkData extends JSPFieldData {


  // id constructor
  public JSPLinkData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPLinkData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  public JSPLinkData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_LINK;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CSS_CLASS));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ONCLICK));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ONMOUSE_OVER_STATUS));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ALIGNMENT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CONTROL_ON_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_PROC_LINK));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_URL));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OPEN_NEW_WINDOW));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_WINDOW_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_VALUE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));


    // add static/constant properties
    DataTypeInterface dti = loadDataType(adapter, "pt.iflow.api.datatypes.Text");
    if(dti != null)
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, dti.getDescription());
    else
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, "Text");

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));



    // add prop dependencies
    PropDependency pd = new PropDependency(JSPFieldData.nPROP_PROC_LINK, 
					   PropDependency.nENABLE, 
					   PropDependency.nTRUE);
    PropDependencyItem pdi = new PropDependencyItem(JSPFieldData.nPROP_URL, PropDependency.nDISABLE);
    pd.addDependency(pdi);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_VAR_NAME, PropDependency.nENABLE);
    pd.addDependency(pdi);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_VAR_VALUE, PropDependency.nENABLE);
    pd.addDependency(pdi);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_OPEN_NEW_WINDOW, PropDependency.nDISABLE);
    pd.addDependency(pdi);
    // next dependency item should not be necessary, since nPROP_WINDOW_NAME depends 
    // on nPROP_OPEN_NEW_WINDOW: TODO
    pdi = new PropDependencyItem(JSPFieldData.nPROP_WINDOW_NAME, PropDependency.nDISABLE);
    pd.addDependency(pdi);
    this._hmPropDependencies.put(new Integer(JSPFieldData.nPROP_PROC_LINK), pd);



    pd = new PropDependency(JSPFieldData.nPROP_OPEN_NEW_WINDOW, 
			    PropDependency.nENABLE, 
			    PropDependency.nTRUE);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_WINDOW_NAME, PropDependency.nENABLE);
    pd.addDependency(pdi);
    this._hmPropDependencies.put(new Integer(JSPFieldData.nPROP_OPEN_NEW_WINDOW), pd);

   
  }
}
