/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
