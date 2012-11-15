package pt.iknow.utils.swing;

import java.util.EventListener;

/**
 * The listener that's notified when a tab was closed
 * <code>CloseableTabbedPane</code>.
 */
public interface TabClosedListener extends EventListener {
  /**
   * Informs all <code>TabClosedListener</code>s that a tab was closed
   */
  void tabClosed();
}
