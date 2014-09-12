package pt.iflow.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

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

}
