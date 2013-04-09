package pt.iflow.applet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MultiActionListener implements ActionListener {
  
  private static Log log = LogFactory.getLog(MultiActionListener.class);
  
  private ArrayList<ActionListener> actionsList;
  
  public MultiActionListener(ActionListener ... actionListeners ) {
    actionsList = new ArrayList<ActionListener>();
    if(null != actionListeners)
      actionsList.addAll(Arrays.asList(actionListeners));
  }
  
  /**
   * Appends the listener element to the list.
   * @param actionListener The observer.
   */
  public void addListener(ActionListener actionListener) {
    actionsList.add(actionListener);
  }
  
  /**
   * Invoked when an action occurs.
   * @see java.awt.event.ActionListener
   */
  public void actionPerformed(ActionEvent e) {
    try {
      for(ActionListener actionListener: actionsList) {
        actionListener.actionPerformed(e);
      }
    } catch (InterruptedListenerException ex) {
      log.info("Listener Interrupted."); //$NON-NLS-1$
    }
  }

}
