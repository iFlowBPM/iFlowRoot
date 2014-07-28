package pt.iflow.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class CSRFFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		Boolean validSubmisson = true;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String referer = httpRequest.getHeader("referer");
		String requestURI = httpRequest.getRequestURI();
		try {
			if ( StringUtils.equalsIgnoreCase("/iFlow/login.jsp", requestURI) 
				||	StringUtils.equalsIgnoreCase("/iFlow", requestURI)
				||	StringUtils.equalsIgnoreCase("/iFlow/", requestURI)
				||	StringUtils.equalsIgnoreCase("/iFlow/Admin/login.jsp", requestURI)
				//||	StringUtils.equalsIgnoreCase("/iFlow/main.jsp", requestURI)
				)
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

	public void init(FilterConfig arg0) throws ServletException {
	}

}
