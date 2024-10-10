package pt.iflow.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;

public class XSSFilter implements Filter {

	//chars exception list
	private List<String> filterException;
	//paths exception list
	private List<String> filterURLException;
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();
		
		for(String fue: filterURLException)
			if ( requestURI.startsWith(fue)){
				chain.doFilter(request, response);
				return;
			}
		
		chain.doFilter(new XSSFilteredRequest(request), response);
	}

	public void destroy() {
	}

	public void init(FilterConfig fc) throws ServletException {
		Enumeration<?> parameterNames = fc.getInitParameterNames();
		filterException = new ArrayList<String>();
		filterURLException = new ArrayList<String>();
		
		while(parameterNames.hasMoreElements()){
			String aux = parameterNames.nextElement().toString();
			if (StringUtils.startsWith(aux, "pt.iflow.filter_exception"))
				filterException.add(fc.getInitParameter(aux));
			if (StringUtils.startsWith(aux, "pt.iflow.filter_url_exception"))
				filterURLException.add(fc.getInitParameter(aux));
		}			
		fc.getServletContext().setAttribute("pt.iflow.servlets.XSSFilter.exception", filterException);
	}

	class XSSFilteredRequest extends GenericSanitizedRequestWrapper {

		public XSSFilteredRequest(ServletRequest request) {
			super((HttpServletRequest) request);
		}

		public String sanitize(String input) {
			String result = ESAPI.encoder().encodeForHTML(input); 
						
			for(String transformed: filterException)								
				result = StringUtils.replace(result, ESAPI.encoder().encodeForHTML(transformed), transformed);
			
			return result;
		}
	}

}