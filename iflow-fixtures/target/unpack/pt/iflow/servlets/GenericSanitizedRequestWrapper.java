package pt.iflow.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public abstract class GenericSanitizedRequestWrapper extends HttpServletRequestWrapper {

	public GenericSanitizedRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public String getParameter(String paramName) {
		String value = super.getParameter(paramName);
		return sanitize(value);
	}

	public String[] getParameterValues(String paramName) {
		String values[] = super.getParameterValues(paramName);
		for (int index = 0; index < values.length; index++)
			values[index] = sanitize(values[index]);
		return values;
	}
	
	public abstract String sanitize(String input);
}
