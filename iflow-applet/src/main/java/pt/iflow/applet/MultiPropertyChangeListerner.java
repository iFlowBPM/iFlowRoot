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
