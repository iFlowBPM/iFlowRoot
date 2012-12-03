/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iknow.floweditor.blocks;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import pt.iflow.api.datatypes.DataTypeInterface;
import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVars;
import pt.iknow.floweditor.FlowEditorAdapter;

/**
 * Class that contains the jsp text area data [for subFlowFieldData] (only contains single properties)
 *
 * @see JSPFieldData
 */
public  class JSPPopupFormFieldData extends JSPFieldData {
  // id constructor
  public JSPPopupFormFieldData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPPopupFormFieldData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  // full constructor
  public JSPPopupFormFieldData(FlowEditorAdapter adapter, int anID, int anPosition, String asText, String asVarName, int anCols, int anRows) {
    this(adapter, anID);
    this._nPosition = anPosition;

    // now set all field properties
    this.setProperty(JSPFieldData.nPROP_TEXT,asText);
    this.setProperty(JSPFieldData.nPROP_VAR_NAME,asVarName);
  }

  public JSPPopupFormFieldData(JSPFieldData afdData) {
    super(afdData);
  }

  protected void init() {
    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_SUB_FLOW_FORM_FIELD;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    //this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_LIST_OF_POPUP_FLOWS));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));

    // INI - DIMENSIONS
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_WIDTH));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_HEIGHT));
    // FIM - DIMENSIONS

    // add select multiple properties

    // TODO HM Variaveis no fluxo
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_POPUP_CALLER_VARIABLE));
    this._alEditMultipleProps.add(new Integer(JSPFieldData.nPROP_POPUP_VARIABLES));

    // add static/constant properties
    DataTypeInterface dti = loadDataType(adapter, "pt.iflow.api.datatypes.Text");
    if(dti != null) {
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, dti.getDescription());
    } else {
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, "Text");
    }

    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_LIST_OF_POPUP_FLOWS));

    // add prop dependencies

  }

  public static String[][] loadPopupvariables(FlowEditorAdapter flowEditorAdapter, String selectedPopupFlow) {
    pt.iflow.api.xml.codegen.flow.XmlFlow _xmlflow = null;
    String[][] inputFields = new String [0][2];
    try {
      byte[] subflow = flowEditorAdapter.getRepository().getSubFlow(selectedPopupFlow);
      _xmlflow = FlowMarshaller.unmarshal(subflow);
      XmlCatalogVars xmlcv = _xmlflow.getXmlCatalogVars();

      if(xmlcv.getXmlAttributeCount() > 0 && xmlcv.getXmlCatalogVarAttributeCount() == 0) {
        inputFields = new String[xmlcv.getXmlAttributeCount()][2];
        for (int i = 0; i < xmlcv.getXmlAttributeCount(); i++) {
          XmlAttribute attr = xmlcv.getXmlAttribute(i);
          inputFields[i][0] = attr.getName();
          inputFields[i][1] = attr.getDescription();
        }
      } else {
        inputFields = new String[xmlcv.getXmlCatalogVarAttributeCount()][2];
        for (int i = 0; i < xmlcv.getXmlCatalogVarAttributeCount(); i++) {
          XmlCatalogVarAttribute attr = xmlcv.getXmlCatalogVarAttribute(i);
          inputFields[i][0] = attr.getName();
          inputFields[i][1] = attr.getFormat();
        }
      }
    } catch (ValidationException ve) {
    } catch (MarshalException me) {
    }
    return inputFields;
  }
}
