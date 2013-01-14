package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


/**
 * Class that contains the jsp image data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPChartData extends JSPFieldData {


  // id constructor
  public JSPChartData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPChartData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  public JSPChartData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    loadChartTemplates();

    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_CHART;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CHART_TEMPLATE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CHART_TITLE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CHART_DSL));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CHART_DSV));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CHART_WIDTH));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_CHART_HEIGHT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_ALIGNMENT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));


    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CHART_TEMPLATE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CHART_TITLE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CHART_DSL));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CHART_DSV));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CHART_WIDTH));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_CHART_HEIGHT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_ALIGNMENT));

    // set non-string properties types
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_ALIGNMENT),
        new Integer(JSPFieldData.nALIGNMENT));
    this._hmPropTypes.put(new Integer(JSPFieldData.nPROP_CHART_TEMPLATE),
        new Integer(JSPFieldData.nCHART_TEMPLATES));


    // add prop dependencies
  }
}
