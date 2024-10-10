package pt.iflow.servlets;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

public class SynchronizerTokenController {
	
	public static final String SYNCRONIZER_TOKEN_MAP = "SYNCRONIZER_TOKEN_MAP";
	
	public static void register(ServletContext sc, String username){
		Random rd =  new Random((new Date()).getTime());
		String newToken = "" + rd.nextLong();
		
		HashMap<String,String> tokenMap = (HashMap<String, String>) sc.getAttribute(SYNCRONIZER_TOKEN_MAP);
		if(tokenMap==null)
			tokenMap = new HashMap<String, String>();
		
		tokenMap.put(username, newToken);
		sc.setAttribute(SYNCRONIZER_TOKEN_MAP, tokenMap);
	}
	
	public static Boolean validate(ServletContext sc, String ui, String token){
		HashMap<String,String> tokenMap = (HashMap<String, String>) sc.getAttribute(SYNCRONIZER_TOKEN_MAP);
		
		if(tokenMap==null)
			return false;
		
		if(StringUtils.equals(token, tokenMap.get(ui)))
			return true;
		
		return false;
	}
	
	public static String checkToken(ServletContext sc, String ui){
		HashMap<String,String> tokenMap = (HashMap<String, String>) sc.getAttribute(SYNCRONIZER_TOKEN_MAP);
		
		if(tokenMap==null)
			return null;
		
		return tokenMap.get(ui);
	}
}
