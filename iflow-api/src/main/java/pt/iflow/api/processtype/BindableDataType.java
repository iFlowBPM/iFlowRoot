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
package pt.iflow.api.processtype;

import pt.iflow.api.processdata.BindDelegate;
import pt.iflow.api.processdata.DynamicBindDelegate;

public class BindableDataType implements ProcessDataType {
  private String _variableName;
  private VariableFormatter formatter;

  public BindableDataType(String variableName) {
    this(variableName, null);
  }

  public BindableDataType(String variableName, VariableFormatter formatter) {
    if(null == variableName) throw new NullPointerException("No method name provided");
    this._variableName = variableName;
    this.formatter = formatter;
  }

  public Object convertFrom(String rawvalue) {
    // always return null. Not serializable.
    return null;
  }

  public String convertTo(Object value) {
    // always return null. Not serializable.
    return null;
  }

  public String format(Object obj) {
    // we are expecting an instance of _class
    if(null == obj) return null;
    if(!(obj instanceof BindDelegate)) throw new IllegalArgumentException("Argument must be an instance of BindDelegate"); 

    Object invokeOutput = ((BindDelegate)obj).invoke(getVariableName());
    if(null == formatter)
      return null == invokeOutput ? null : String.valueOf(invokeOutput);
    return formatter.format(invokeOutput);
  }

  public Object parse(String source) {
    // this is some kind of readonly, no need to parse
    return null;
  }

  public String getVariableName() {
    return this._variableName;
  }
  public String toString() {
    return "Bindable";
  }

  public Class<?> getSupportingClass() {
    DataTypeEnum dtype = DynamicBindDelegate.getDynamicVariableDataType(_variableName);    
    ProcessDataType pdtype = dtype.newDataTypeInstance();
    return pdtype.getSupportingClass();
  }

  public boolean isBindable() {
    return true;
  }

}
