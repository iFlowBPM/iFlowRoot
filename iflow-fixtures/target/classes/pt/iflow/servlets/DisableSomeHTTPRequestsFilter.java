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

public class DisableSomeHTTPRequestsFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		if (StringUtils.equalsIgnoreCase("TRACE", httpRequest.getMethod()))
			request.getRequestDispatcher("logout.jsp").forward(request, response);
		else
			chain.doFilter(request, response);					
	}

	public void destroy() {
	}

	public void init(FilterConfig fc) throws ServletException {}

}
