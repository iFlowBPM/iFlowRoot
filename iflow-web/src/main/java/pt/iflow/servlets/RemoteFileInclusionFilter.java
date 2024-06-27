package pt.iflow.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class RemoteFileInclusionFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// check for any parameter that points outside the application
		Boolean failedParameter = false;
		Enumeration parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement().toString();
			try {
				URL testURL = new URL(request.getParameter(parameterName));
				if (!StringUtils.equalsIgnoreCase(testURL.getHost(),request.getServerName()))
					failedParameter = true;;
			} catch (MalformedURLException e) {}
		}
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.addHeader("X-Content-Type-Options", "nosniff");
		httpResponse.addHeader("X-XSS-Protection", "1; mode=block");
		httpResponse.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		httpResponse.addHeader("Cache-Control", "no-store");
		httpResponse.addHeader("Cache-Control", "no-cache");		
		httpResponse.addHeader("Pragma", "no-cache");
		httpResponse.addHeader("X-Frame-Options", "SAMEORIGIN");
		
		if(!failedParameter)
			chain.doFilter(request, response);
	}

	public void destroy() {
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
}
