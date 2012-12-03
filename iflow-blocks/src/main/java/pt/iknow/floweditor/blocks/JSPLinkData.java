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
