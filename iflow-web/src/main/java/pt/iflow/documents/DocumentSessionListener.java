package pt.iflow.documents;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import pt.iflow.api.documents.DocumentSessionHelper;

public class DocumentSessionListener implements HttpSessionListener {

  public void sessionCreated(HttpSessionEvent arg0) {
  }

  public void sessionDestroyed(HttpSessionEvent arg0) {
    // cleanup any documents associated with the session being destroyed
    arg0.getSession().removeAttribute(DocumentSessionHelper.SESSION_ATTRIBUTE);
  }

}
