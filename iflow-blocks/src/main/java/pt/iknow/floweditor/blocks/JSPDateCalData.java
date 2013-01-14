package pt.iknow.floweditor.blocks;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp date data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPDateCalData extends JSPFieldData {


  // id constructor
  public JSPDateCalData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPDateCalData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }


  public JSPDateCalData(JSPFieldData afdData) {
    super(afdData);
  }

  private String getDescription() {
    String description = "Date";
    try {
      Class<?> c = Class.forName("pt.iflow.api.datatypes.Date", true, getClass().getClassLoader());
      DataTypeInterface dti = (DataTypeInterface) c.newInstance();
      description = dti.getDescription();
    } catch (Exception e) {
    }
    return description;
  }

  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_DATECAL;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DATE_FORMAT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_HOUR_FORMAT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CURRDATE_IF_EMPTY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OUTPUT_ONLY));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ONCHANGE_SUBMIT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD));

    // add static/constant properties
    this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, getDescription());

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_DATE_FORMAT));

    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_DATE_FORMAT),
        new Integer(JSPFieldData.nDATE_FORMAT));

  }


}
