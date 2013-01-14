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
