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
/* 
 * $Id: SwingPropertyChangeSupport.java,v 1.1 2005/06/18 21:27:14 idk Exp $
 * 
 * Copyright (C) 2005 Sun Microsystems, Inc. All rights
 * reserved. Use is subject to license terms.
 */

package org.jdesktop.swingworker;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;

import javax.swing.SwingUtilities;

/**
 * This subclass of {@code java.beans.PropertyChangeSupport} is almost
 * identical in functionality. The only difference is if constructed with 
 * {@code SwingPropertyChangeSupport(sourceBean, true)} it ensures
 * listeners are only ever notified on the <i>Event Dispatch Thread</i>.
 *
 * @author Igor Kushnirskiy
 * @version $Revision: 1.1 $ $Date: 2005/06/18 21:27:14 $
 */

public final class SwingPropertyChangeSupport extends PropertyChangeSupport {

    /**
     * Constructs a SwingPropertyChangeSupport object.
     *
     * @param sourceBean  The bean to be given as the source for any
     *        events.
     * @throws NullPointerException if {@code sourceBean} is 
     *         {@code null}
     */
    public SwingPropertyChangeSupport(Object sourceBean) {
        this(sourceBean, false);
    }

    /**
     * Constructs a SwingPropertyChangeSupport object.
     * 
     * @param sourceBean the bean to be given as the source for any events
     * @param notifyOnEDT whether to notify listeners on the <i>Event
     *        Dispatch Thread</i> only
     *
     * @throws NullPointerException if {@code sourceBean} is 
     *         {@code null}
     * @since 1.6
     */
    public SwingPropertyChangeSupport(Object sourceBean, boolean notifyOnEDT) {
        super(sourceBean);
        this.notifyOnEDT = notifyOnEDT;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * If {@see #isNotifyOnEDT} is {@code true} and called off the
     * <i>Event Dispatch Thread</i> this implementation uses 
     * {@code SwingUtilities.invokeLater} to send out the notification
     * on the <i>Event Dispatch Thread</i>. This ensures  listeners
     * are only ever notified on the <i>Event Dispatch Thread</i>.
     *
     * @throws NullPointerException if {@code evt} is 
     *         {@code null}
     * @since 1.6
     */
    public void firePropertyChange(final PropertyChangeEvent evt) {
        if (evt == null) {
            throw new NullPointerException();
        }
        if (! isNotifyOnEDT()
            || SwingUtilities.isEventDispatchThread()) {
            super.firePropertyChange(evt);
        } else {
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        firePropertyChange(evt);
                    }
                });
        }
    }

    /**
     * Returns {@code notifyOnEDT} property.
     * 
     * @return {@code notifyOnEDT} property
     * @see #SwingPropertyChangeSupport(Object sourceBean, boolean notifyOnEDT)
     * @since 1.6
     */
    public final boolean isNotifyOnEDT() {
        return notifyOnEDT;
    }

    // Serialization version ID
    static final long serialVersionUID = 7162625831330845068L;

    /**
     * whether to notify listeners on EDT
     * 
     * @serial 
     * @since 1.6
     */ 
    private final boolean notifyOnEDT;
}
