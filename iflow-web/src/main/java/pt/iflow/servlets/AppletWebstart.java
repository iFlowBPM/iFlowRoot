/**
 * RepositoryDownload.java
 *
 * Description:
 *
 * History: 01/15/02 - jpms - created.
 * $Id: PublicFiles.java 248 2007-08-01 13:54:31 +0000 (Qua, 01 Ago 2007) uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iflow.servlets;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.iflow.api.presentation.PresentationManager;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.applet.AbstractAppletServletHelper;
import pt.iflow.documents.DocumentServiceServlet;

import com.google.gson.Gson;


public class AppletWebstart extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static final String REQUEST_FOR_APPLET = "REQUEST_FOR_APPLET";
	private Hashtable<String,RequestForApplet> requestPile;
	
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		ServletContext sc = req.getSession().getServletContext();
		if(sc.getAttribute(REQUEST_FOR_APPLET) == null)
			sc.setAttribute(REQUEST_FOR_APPLET, new Hashtable<String,RequestForApplet>());
		super.service(req, resp);		
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try{
			HttpSession session = DocumentServiceServlet.getSessionFixedForJNLP(req);
			UserInfoInterface userInfo = (UserInfoInterface)session.getAttribute(Const.USER_INFO);
			Gson gson = new Gson();			
			String requestURL = req.getRequestURL().toString();
			String documentBaseURL = requestURL.replace("AppletWebstart", "DocumentService");
			String codebaseURL = requestURL.replace("/AppletWebstart", "");
			String checkRequestForAppletURL = requestURL.replace("AppletWebstart", "CheckRequestForApplet");			
			
			AbstractAppletServletHelper helper = AbstractAppletServletHelper.getInstance();
			ArrayList<String> resources = new ArrayList<String>();
			Iterator<String> iter = helper.dependenecies();
			while(iter.hasNext()) {
				String name = iter.next();	            
				resources.add(name);
			}			
			
			Enumeration enp = req.getParameterNames();
			Hashtable htp = new Hashtable();
			htp.put("DOCUMENTBASEURL", documentBaseURL);
			htp.put("CHECKREQUESTFORAPPLET", checkRequestForAppletURL);
			htp.put("JSESSIONID", session.getId());
			while(enp.hasMoreElements()){
				String name = enp.nextElement().toString();
				String value = req.getParameter(name);
				htp.put(name, value);
			}	
			
			//if no aditional arguments just download/start the applet
			if(htp.size()==3){
				String appletArgument = gson.toJson(htp);			
				Hashtable hsSubstLocal = new Hashtable();
				hsSubstLocal.put("appletArgument", appletArgument);
				hsSubstLocal.put("codebaseURL", codebaseURL);
				hsSubstLocal.put("resources", resources);
				String appletWebStartJnlp =  PresentationManager.buildPage(resp, userInfo, hsSubstLocal, "appletWebStart");
				
				byte [] ba = appletWebStartJnlp.getBytes();
			    resp.setHeader("Content-Disposition","attachment;filename=\"" + "appletWebStart.jnlp" +"\";");
			    resp.setContentLength(ba.length);
			    OutputStream out = resp.getOutputStream();
			    out.write(ba);
			    out.flush();
			    out.close();
			}
			//save request for applet
			else{				
				session.setAttribute(REQUEST_FOR_APPLET, new RequestForApplet(htp));
				return;
			}
		} catch(Exception e){
			Logger.error("<unknown>", this, "doGet", "Could not generate applet jnlp file",e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
		    return;
		}
		
	}
	
}

