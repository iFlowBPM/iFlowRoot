package pt.iflow.api.processdata;

import java.text.Format;
import java.text.ParseException;

import pt.iflow.api.processtype.ProcessDataType;

public interface ProcessVariableValue {
	public String getRawValue();
	public Object getValue();
	public void setValue(Object value);
	public void parse(String source) throws ParseException;
	public void parse(String source, ProcessDataType formatter) throws ParseException;
	public String format();
	public String format(ProcessDataType formatter);
    public String format(Format formatter);
	public boolean equals(ProcessVariableValue value);
}
