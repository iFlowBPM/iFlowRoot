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
package pt.iflow.api.processdata;


import java.text.ParseException;

import pt.iflow.api.processtype.ProcessDataType;

public class BindableProcessVariable extends ProcessSimpleVariable implements ProcessVariableValue {

  public BindableProcessVariable(ProcessDataType type, String name) {
    super(type, name);
    if(!type.isBindable()) throw new IllegalArgumentException("Type must be bindable.");
    this._value = new InternalBindableValue(type, name, null);
  }

  /**
   * Create a variable binded to the process and user
   * 
   * @param type
   * @param name
   * @param process
   * @param userInfo
   */
  public BindableProcessVariable(ProcessDataType type, String name, BindDelegate delegate) {
    this(type, name);
    this._value = new InternalBindableValue(type, name, delegate);
  }

  @Override
  public void clear() {
    this._value = new InternalBindableValue(getType(), _name, null);
  }

  @Override
  public void parse(String source, ProcessDataType formatter) throws ParseException {
    // do nothing
  }


  @Override
  public void parse(String source) throws ParseException {
    throw new UnsupportedOperationException(); // TODO para já manter as excepcoes para saber se sao chamadas erroneamente
  }


  @Override
  public void setValue(Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getValue() {
//    return format();
    return _value.getValue();
  }

  
  @Override
  protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("This is a special variable and sould not be cloned.");
  }

}

class InternalBindableValue extends InternalValue {

  String name;
  
  InternalBindableValue(ProcessDataType type, String name, BindDelegate delegate) {
    super(type, delegate);
    this.name = name;
  }

  @Override
  public Object getValue() {
    Object delegate = internalGetValue();
    if(delegate == null) return null;
    if(!(delegate instanceof BindDelegate)) throw new IllegalArgumentException("Argument must be an instance of BindDelegate"); 
    
    return ((BindDelegate)delegate).invoke(name);
  }
  
}
