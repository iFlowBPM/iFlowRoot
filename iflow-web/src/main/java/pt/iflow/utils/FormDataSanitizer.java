package pt.iflow.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import pt.iknow.utils.html.FormData;

public class FormDataSanitizer {

	public static void FormDataParameterSanitize(FormData fd, ServletContext sc){
		Enumeration names = fd.getParameterNames();
		HashMap<String, String[]> resultAux = new HashMap<String, String[]>();
		while(names.hasMoreElements()){
			String name = names.nextElement().toString();			
			String[] valuesOld = fd.getParameterValues(name);
			String[] valuesNew = new String[valuesOld.length];
			
			for(int i=0; i<valuesOld.length; i++){
				String escapedHTML = ESAPI.encoder().encodeForHTML(valuesOld[i]);
				//unescape regular accentuated vowels
				escapedHTML = revertAccents(escapedHTML);
				//unescape the Ç
				escapedHTML = StringUtils.replace(escapedHTML, "&ccedil;", "ç");
				escapedHTML = StringUtils.replace(escapedHTML, "&Ccedil;", "Ç");
				escapedHTML = StringUtils.replace(escapedHTML, "&#x3a;",":");
				escapedHTML = StringUtils.replace(escapedHTML, "&#x40;","@");
				escapedHTML = StringUtils.replace(escapedHTML, "&#x3f;","?");
				
				String escapedSQL = ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.ANSI), escapedHTML);
				
				List<String> filterException = (List<String>)sc.getAttribute("pt.iflow.servlets.XSSFilter.exception");
				if (filterException!=null)
				for(String transformed: filterException)								
					escapedSQL = StringUtils.replace(escapedSQL, ESAPI.encoder().encodeForHTML(transformed), transformed);
				
				
				valuesNew[i] = escapedSQL;
			}
			
			resultAux.put(name, valuesNew);
		}
		
		fd.setParameters(resultAux);
	}

	private static String revertAccents(String escapedHTML) {
		String[] letters = {"a","e","i","o","u","y","A","E","I","O","U","Y"};
		String[] accents = {"grave", "acute", "circ", "tilde", "uml"};
		String result = escapedHTML;
		
		for(String letter: letters)
			for(String accent: accents)
				result = result.replaceAll("&" +letter+ accent +";", StringEscapeUtils.unescapeHtml("&" +letter+ accent +";"));
		
		return result;
	}

}
