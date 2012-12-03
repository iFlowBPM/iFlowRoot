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


public class ProcessListItem implements ProcessVariableValue {
	
	public static final int NO_POSITION = -1;
	
	int _position;
	InternalValue _value;

	public ProcessListItem(ProcessDataType type) throws ParseException {
		this(NO_POSITION, new InternalValue(type));
	}
	
	public ProcessListItem(InternalValue value) {
		this(NO_POSITION, value);
	}
	
	public ProcessListItem(int position, InternalValue value) {
		setPosition(position);
		_value = value;
	}
	
	ProcessListItem(ProcessDataType type, Element xmlElement) throws ParseException {
		
		_position = Integer.parseInt(xmlElement.getAttribute(ProcessXml.ATTR_POSITION));
		_value = new InternalValue(type, xmlElement.getChildNodes().item(0).getNodeValue());
	}

	public int getPosition() {
		return _position;
	}

	void setPosition(int position) { 
		_position = position;
	}
	
	public String getRawValue() {
		return _value.getRawValue();
	}
	
	public Object getValue() {
		return _value.getValue();
	}

	public void setValue(Object value) {
		_value.setValue(value);
	}
	
	public String format() {
		return _value.format();
	}

    public String format(ProcessDataType formatter) {
      return _value.format(formatter);
    }

    public String format(Format formatter) {
      return _value.format(formatter);
    }

	public void parse(String source) throws ParseException {
		_value.parse(source);
	}

	public void parse(String source, ProcessDataType formatter) throws ParseException {
		_value.parse(source, formatter);
	}
	
	public void clear() {
		setValue(null);
	}
	
	public String toString() {
		return "@" + getPosition() + "=" + format();
	}
		
	public String toDebugString() {
		return "@" + getPosition() + "[raw:" + getRawValue() + "]=" + getValue();
	}

	public boolean equals(ProcessVariableValue value) {
		return _value.equals(value);
	}

}
