package pt.iflow.api.processtype;

import java.text.ParseException;

/**
 * Interface to support XML serialization of processes.
 * 
 * @author ombl
 *
 */
public interface VariableConverter {
  /**
   * Convert a serialized value (rawvalue) to internal representation
   * @param rawvalue serialized value to convert
   * @return internal value
   * @throws ParseException
   */
	public Object convertFrom(String rawvalue) throws ParseException;
	/**
	 * Convert the given internal value to a string to serialize
	 * @param value Object to serialize
	 * @return serialized value
	 */
	public String convertTo(Object value);
}
