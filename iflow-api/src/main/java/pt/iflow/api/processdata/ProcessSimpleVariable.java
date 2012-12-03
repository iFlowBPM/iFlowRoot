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

import java.text.Format;
import java.text.ParseException;

import org.w3c.dom.Element;

import pt.iflow.api.processtype.ProcessDataType;


public class ProcessSimpleVariable extends ProcessVariable implements ProcessVariableValue {
	InternalValue _value;

	ProcessSimpleVariable(ProcessDataType type, Element xmlElement) throws ParseException {
		super(type, xmlElement);
		
		_value = new InternalValue(_type, xmlElement.getChildNodes().item(0).getNodeValue());
	}

	public ProcessSimpleVariable(ProcessDataType type, String name) {
		super(type, name);
	}

	public ProcessSimpleVariable(ProcessSimpleVariable srcVar) {
		super(srcVar._type, srcVar._name);
		setValue(srcVar.getValue());
	}

	private void ensureValue() {
		if (_value == null){
		  _value = new InternalValue(_type);
		}
	}
	
	public String getRawValue() {
		ensureValue();
		return _value.getRawValue();
	}
	
	public Object getValue() {
		ensureValue();
		return _value.getValue();
	}

	public void setValue(Object value) {
		ensureValue();
		_value.setValue(value);
	}
	
	public String format() {
		ensureValue();
		return _value.format();
	}

	public String format(ProcessDataType formatter) {
		ensureValue();
		return _value.format(formatter);
	}

    public String format(Format formatter) {
      ensureValue();
      return _value.format(formatter);
    }

	public void parse(String source) throws ParseException {
		ensureValue();
		_value.parse(source);
	}

	public void parse(String source, ProcessDataType formatter) throws ParseException {
		ensureValue();
		_value.parse(source, formatter);
	}
	
	@Override
	public void clear() {
		setValue(null);
	}

	public String toString() {
		return getName() + "=" + format();
	}

	@Override
	public String toDebugString() {
		return getName() + "[raw:" + getRawValue() + "]=" + getValue();
	}

	public boolean equals(ProcessSimpleVariable var) {
		return equals(var._value);
	}

	public boolean equals(ProcessVariableValue value) {
		return _value.equals(value);
	}
}
