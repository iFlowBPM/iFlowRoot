package pt.iflow.utils;

import java.util.Enumeration;
import java.util.HashMap;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import pt.iknow.utils.html.FormData;

public class FormDataSanitizer {

	public static void FormDataParameterSanitize(FormData fd){
		Enumeration names = fd.getParameterNames();
		HashMap<String, String[]> resultAux = new HashMap<String, String[]>();
		while(names.hasMoreElements()){
			String name = names.nextElement().toString();			
			String[] valuesOld = fd.getParameterValues(name);
			String[] valuesNew = new String[valuesOld.length];
			
			for(int i=0; i<valuesOld.length; i++){
				String escapedHTML = ESAPI.encoder().encodeForHTML(valuesOld[i]);
				String escapedSQL = ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.ANSI), escapedHTML);
				valuesNew[i] = escapedSQL;
			}
			
			resultAux.put(name, valuesNew);
		}
		
		fd.setParameters(resultAux);
	}

}
