package pt.iflow.applet;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MultiDocumentListener implements DocumentListener {

  private static Log log = LogFactory.getLog(MultiDocumentListener.class);
  
  private ArrayList<DocumentListener> actionsList;

  public MultiDocumentListener(DocumentListener ... documentListeners ) {
    actionsList = new ArrayList<DocumentListener>();
    if(documentListeners != null)
      actionsList.addAll(Arrays.asList(documentListeners));
  }

  /**
   * Appends the listener element to the list.
   * @param actionListener The observer.
   */
  public void addDocumentListener(DocumentListener actionListener) {
    actionsList.add(actionListener);
  }

  /**
   * Gives notification that an attribute or set of attributes changed.
   * @see javax.swing.event.DocumentListener
   */
  public void changedUpdate(DocumentEvent e) {
    for(DocumentListener actionListener: actionsList) {
      actionListener.changedUpdate(e);
    }
  }

  /**
   * Gives notification that there was an insert into the document.
   * The range given by the DocumentEvent bounds the freshly inserted region.
   * @see javax.swing.event.DocumentListener
   */
  public void insertUpdate(DocumentEvent e) {
    for(DocumentListener actionListener: actionsList) {
      actionListener.insertUpdate(e);
    }
  }

  /**
   * Gives notification that a portion of the document has been removed.
   * The range is given in terms of what the view last saw (that is, before updating sticky positions).
   * @see javax.swing.event.DocumentListener
   */
  public void removeUpdate(DocumentEvent e) {
    try {
      for(DocumentListener actionListener: actionsList) {
        actionListener.removeUpdate(e);
      }
    } catch (InterruptedListenerException ex) {
      log.info("Listener Interrupted."); //$NON-NLS-1$
    }
  }

}
