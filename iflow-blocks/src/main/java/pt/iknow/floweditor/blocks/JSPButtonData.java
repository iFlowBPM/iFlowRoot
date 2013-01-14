package pt.iknow.floweditor.blocks;

import java.util.Map;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.floweditor.blocks.JSPFieldData.PropDependency;
import pt.iknow.floweditor.blocks.JSPFieldData.PropDependencyItem;

/**
 * Class that contains the jsp button data (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPButtonData extends JSPFieldData {

  // id constructor
  public JSPButtonData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPButtonData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }
  
  public JSPButtonData(JSPFieldData afdData) {
    super(afdData);
  }

  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_BUTTON;

    loadButtonTypes();

    // Permite assinar documentos (escolhe o tipo de assinatura)
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_TYPE));
    // Se assinatura seleccionada, liga as duas abaixo
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_TOOLTIP));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_IMAGE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_SHOW_CONDITION));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_IGNORE_FORM_PROCESSING));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_IGNORE_FORM_VALIDATION));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CONFIRM_ACTION));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CONFIRM_MESSAGE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VARIABLE));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VALUE));

    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VALUE));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_BUTTON_CUSTOM_VARIABLE));

    Map<String, String> buttonTypeMap = getButtonTypeMap();
    PropDependency pd = new PropDependency(JSPFieldData.nPROP_BUTTON_TYPE, PropDependency.nENABLE, PropDependency.nEMPTY_OR_VALUE, buttonTypeMap.get(AlteraAtributosJSP.sCUSTOM_TYPE));
    PropDependencyItem pdi = new PropDependencyItem(JSPFieldData.nPROP_BUTTON_CUSTOM_VARIABLE, PropDependency.nENABLE);
    pd.addDependency(pdi);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_BUTTON_CUSTOM_VALUE, PropDependency.nENABLE);
    pd.addDependency(pdi);
    this._hmPropDependencies.put(new Integer(JSPFieldData.nPROP_BUTTON_TYPE), pd);
  }

}
