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

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.processtype.DataTypeEnum;
import pt.iflow.api.processtype.ProcessDataType;


class InternalValue implements ProcessVariableValue {
  private ProcessDataType _type;

  private String _rawvalue;
  private Object _value;

  InternalValue(ProcessDataType type) {
    this(type, null);
  }

  InternalValue(ProcessDataType type, String rawvalue) {
    try {
      _type = type;
      _rawvalue = rawvalue;
      if (rawvalue != null) {
        _value = _type.convertFrom(_rawvalue);    
      }
    } catch (ParseException e) { 
      _type = type;
    }
    if(_type == null) {
      DataTypeEnum dataTypeEnum = DataTypeEnum.getDataType("Text");
      _type = dataTypeEnum.newDataTypeInstance();
    }
  }

  InternalValue(ProcessDataType type, Object value) {
    this(type);
    setValue(value);
  }

  public void setValue(Object value) {
    _rawvalue =  _type.convertTo(value);
    _value = value;
  }

  public Object getValue() {
    return internalGetValue();
  }

  Object internalGetValue() {
    return _value;
  }

  public String getRawValue() {
    return _rawvalue;
  }

  public String format() {
    Object value = internalGetValue();
    if (_type != null) {
      return _type.format(value);
    } else {
      if (value == null) {
        return null;
      } else {
        return "" + value;
      }
    }
  }

  public String format(ProcessDataType formatter) {
    Object value = internalGetValue();
    if (value == null) {
      return null;
    } else {
      return formatter.format(value);
    }
  }

  public String format(Format formatter) {
    Object value = internalGetValue();
    if (value == null) {
      return null;
    } else if (formatter != null) {
      return formatter.format(value);
    }
    else {
      return format();
    }
  }

  public void parse(String source) throws ParseException {
    try {
      setValue(_type.parse(source));
    } catch(NullPointerException ex) {
      System.err.println("Error: Unable to parse source '" + source + "'.");
      setValue(source);
    }
  }

  public void parse(String source, ProcessDataType formatter) throws ParseException {
    setValue(formatter.parse(source));
  }

  public boolean equals(ProcessVariableValue value) {
    return value != null && 
    StringUtils.equals(getRawValue(), value.getRawValue());
  }

}
