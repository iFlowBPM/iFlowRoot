package pt.iflow.servlets;

import java.io.Serializable;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import pt.iflow.api.utils.Logger;

public class SimpleSessionHelper implements HttpSessionBindingListener, Serializable {
  private static final long serialVersionUID = 1L;

  private static long inSession = 0L;
  
  public SimpleSessionHelper () {
  }
  
  public void valueBound(HttpSessionBindingEvent arg0) {
    // Called when goes to session
    ++inSession;
    Logger.trace(this, "valueBound", "New session created. Session count: "+inSession);
  }

  public void valueUnbound(HttpSessionBindingEvent arg0) {
    // Called when session is destroyed
    --inSession;
    Logger.trace(this, "valueUnbound", "Session destroyed. Session count: "+inSession);
  }

}
