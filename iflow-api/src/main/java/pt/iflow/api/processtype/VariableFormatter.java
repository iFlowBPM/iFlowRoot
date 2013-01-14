package pt.iflow.api.processtype;

import java.text.ParseException;

/**
 * User interface presentation and parse support
 * @author ombl
 *
 */
public interface VariableFormatter {
  /**
   * Parse a value from user input
   * @param source User input
   * @return internal value
   * @throws ParseException
   */
	public Object parse(String source) throws ParseException;
	
	/**
	 * Convert internal value to presentation value
	 * @param obj
	 * @return
	 */
	public String format(Object obj);
}
