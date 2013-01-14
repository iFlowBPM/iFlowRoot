package pt.iflow.applet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MultiPropertyChangeListerner implements PropertyChangeListener {

  private static Log log = LogFactory.getLog(MultiPropertyChangeListerner.class);

  private ArrayList<PropertyChangeListener> propertyChangeListenerList;

  public MultiPropertyChangeListerner(PropertyChangeListener ... changeListeners) {
    propertyChangeListenerList = new ArrayList<PropertyChangeListener>();
    if(null != changeListeners) {
      propertyChangeListenerList.addAll(Arrays.asList(changeListeners));
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener actionListener) {
    propertyChangeListenerList.add(actionListener);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    try {
      for (PropertyChangeListener propertyChangeListener : propertyChangeListenerList) {
        propertyChangeListener.propertyChange(evt);
      }
    } catch (InterruptedListenerException ex) {
      log.info("Listener Interrupted."); //$NON-NLS-1$
    }
  }

}
