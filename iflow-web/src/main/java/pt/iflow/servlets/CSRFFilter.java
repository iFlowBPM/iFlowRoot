package pt.iflow.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

import org.apache.commons.lang.StringUtils;

public class CSRFFilter implements Filter {
	
	private List<String> filterException;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		Boolean validSubmisson = true;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String referer = httpRequest.getHeader("referer");
		String requestURI = httpRequest.getRequestURI();
		try {
			if ( filterException.contains(requestURI))
				validSubmisson = true;
			else if (referer == null)
				validSubmisson = false;			
			else if (StringUtils.equals(request.getServerName(), (new URL(referer)).getHost()))
				validSubmisson = true;
		} catch (MalformedURLException e) {
			validSubmisson = false;
		}

		if (validSubmisson)
			chain.doFilter(request, response);
		else 
			request.getRequestDispatcher("logout.jsp").forward(request, response);
		
	}

	public void destroy() {
	}

	public void init(FilterConfig fc) throws ServletException {
		Enumeration<?> parameterNames = fc.getInitParameterNames();
		filterException = new ArrayList<String>();
		
		while(parameterNames.hasMoreElements()){
			String aux = parameterNames.nextElement().toString();
			if (StringUtils.startsWith(aux, "pt.iflow.filter_exception"))
				filterException.add(fc.getInitParameter(aux));
		}
	}

}
