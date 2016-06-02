package pt.iflow.documents;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import pt.iflow.api.documents.DocumentSessionHelper;

public class DocumentSessionListener implements HttpSessionListener {

	public static final String ACTIVE_SESSIONS = "ACTIVE_SESSIONS";
	
	public void init(ServletConfig config) {		
	}

	public void sessionCreated(HttpSessionEvent arg0) {
		// store created sessions in global context
		HttpSession session = arg0.getSession();
		ServletContext context = session.getServletContext();
		Map activeSessions = (Map) context.getAttribute(ACTIVE_SESSIONS);
		if(activeSessions==null){
			activeSessions =  new HashMap();
			context.setAttribute(ACTIVE_SESSIONS, activeSessions);
		}			
		activeSessions.put(session.getId(), session);
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		// cleanup any documents associated with the session being destroyed
		arg0.getSession().removeAttribute(
				DocumentSessionHelper.SESSION_ATTRIBUTE);

		// remove stored session when it's destroyed
		HttpSession session = arg0.getSession();
		ServletContext context = session.getServletContext();
		Map activeSessions = (Map) context.getAttribute(ACTIVE_SESSIONS);
		activeSessions.remove(session.getId());
	}

}
