package pt.iflow.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

public class MySQLInjectionFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new MySQLInjectionFilteredRequest(request), response);
	}

	public void destroy() {
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

	class MySQLInjectionFilteredRequest extends GenericSanitizedRequestWrapper {

		public MySQLInjectionFilteredRequest(ServletRequest request) {
			super((HttpServletRequest) request);
		}

		public String sanitize(String input) {
			return ESAPI.encoder().encodeForSQL(
					new MySQLCodec(MySQLCodec.Mode.ANSI), input);
		}
	}

}
