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
