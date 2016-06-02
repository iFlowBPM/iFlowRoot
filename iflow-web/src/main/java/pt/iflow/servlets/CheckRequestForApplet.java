package pt.iflow.servlets;


import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Logger;
import pt.iflow.documents.DocumentServiceServlet;

import com.google.gson.Gson;

public class CheckRequestForApplet extends AppletWebstart {

		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {		
		try{
			
			HttpSession session = DocumentServiceServlet.getSessionFixedForJNLP(req);
			
			//check if session was destroyed
			Cookie[] cookies = req.getCookies();
			Boolean isSessionDestroyed=true;
			for(Cookie cookie: cookies)
				if(StringUtils.equals(cookie.getValue(),session.getId()) && StringUtils.endsWithIgnoreCase(cookie.getName(), "APPLETJSESSIONID"))
					isSessionDestroyed=false;
			if(isSessionDestroyed){
				resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "session was destroyed");
				return;
			}
			
			//get the applet request stored in session
			RequestForApplet rfa = (RequestForApplet)  session.getAttribute(REQUEST_FOR_APPLET);			
			if(rfa==null){
				resp.sendError(HttpServletResponse.SC_NO_CONTENT, "No request found");
				return;
			}
			
			session.removeAttribute(REQUEST_FOR_APPLET);
			Gson gson = new Gson();
			String appletArgument = gson.toJson(rfa.getParameters());
			byte [] ba = appletArgument.getBytes();
		    resp.setHeader("Content-Disposition","attachment;filename=\"" + "appletArgument" +"\";");
		    resp.setContentLength(ba.length);
		    OutputStream out = resp.getOutputStream();
		    out.write(ba);
		    out.flush();
		    out.close();
		    
		} catch(Exception e){
			Logger.error("<unknown>", this, "doGet", "Could not generate applet argument response",e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
		    return;
		}
		
	}	
	
}