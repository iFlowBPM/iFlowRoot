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
