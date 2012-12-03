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
 * <p>Title: </p>
 * <p>Description: Class that contains the jsp file data (only contains single properties) </p>
 * <p>   @see JSPFieldData
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: iKnow </p>
 * @author Campa
 * @version 1.0
 */

public  class JSPFileData extends JSPFieldData {


  // id constructor
  public JSPFileData(FlowEditorAdapter adapter, int anID) {
    super(adapter, anID);
  }

  // simple constructor
  public JSPFileData(FlowEditorAdapter adapter) {
    this(adapter, -1);
  }

  public JSPFileData(JSPFieldData afdData) {
    super(afdData);
  }


  protected void init() {
    loadSignatureTypes();

    this._nFieldType = JSPFieldTypeEnum.FIELD_TYPE_FILE;

    // add text single properties
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_LABEL));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_TEXT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));

    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_LINK_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_LINK_LABEL));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_LINK_TEXT));

    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_EDIT_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_EDIT_LABEL));

    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_REMOVE_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_REMOVE_LABEL));

    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_LABEL));
//  this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_RENAME));
//  this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_PRESERVE_EXT));
//  this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_EXTENSIONS));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_LIMIT));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_COND));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_UPLOAD_VALID_EXTENSIONS));
    
    // Permite assinar documentos (escolhe o tipo de assinatura)
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_SIGNATURE_TYPE));
    // Se assinatura seleccionada, liga as duas abaixo
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_SIGN_NEW));
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_SIGN_EXISTING));
    // Permite usar scanner
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_SCANNER_COND));
    
    // TextBox utilizadores a encriptar
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_ENCRYPT_USERS));

    // this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_PERMISSION));
    
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_DISABLE_COND));
    
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_OBLIGATORY_FIELD));
    
    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_SIGN_METHOD));

    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_IS_IMAGE));

    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_IS_IMAGE_WIDTH));

    this._alEditSingleProps.add(new Integer(JSPFieldData.nPROP_FILE_IS_IMAGE_HEIGHT));

    // add static/constant properties
    DataTypeInterface dti = loadDataType(adapter, "pt.iflow.api.datatypes.Text");
    if(dti != null)
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, dti.getDescription());
    else
      this.setStaticProperty(JSPFieldData.nPROP_DATA_TYPE, "Text");
    
    // add required properties
    this._alRequiredProps.add(new Integer(JSPFieldData.nPROP_VAR_NAME));

    final String noSignatureValue = getFileSignatureTypeMap().get("NONE");
    PropDependency pd = new PropDependency(JSPFieldData.nPROP_FILE_SIGNATURE_TYPE, PropDependency.nDISABLE, PropDependency.nEMPTY_OR_VALUE, noSignatureValue);
    PropDependencyItem pdi = new PropDependencyItem(JSPFieldData.nPROP_FILE_SIGN_NEW, PropDependency.nENABLE);
    pd.addDependency(pdi);
    pdi = new PropDependencyItem(JSPFieldData.nPROP_FILE_SIGN_EXISTING, PropDependency.nENABLE);
    pd.addDependency(pdi);
    this._hmPropDependencies.put(new Integer(JSPFieldData.nPROP_FILE_SIGNATURE_TYPE), pd);


  }
}
