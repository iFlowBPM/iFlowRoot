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
 * $Id: AccumulativeRunnable.java,v 1.3 2008/07/25 19:32:29 idk Exp $
 * 
 * Copyright (C) 2005 Sun Microsystems, Inc. All rights
 * reserved. Use is subject to license terms.
 */

package org.jdesktop.swingworker;

import java.util.*;
import javax.swing.SwingUtilities;

/**
 * An abstract class to be used in the cases where we need {@code Runnable}
 * to perform  some actions on an appendable set of data.
 * The set of data might be appended after the {@code Runnable} is
 * sent for the execution. Usually such {@code Runnables} are sent to
 * the EDT.
 *
 * <p>
 * Usage example:
 * 
 * <p>
 * Say we want to implement JLabel.setText(String text) which sends
 * {@code text} string to the JLabel.setTextImpl(String text) on the EDT. 
 * In the event JLabel.setText is called rapidly many times off the EDT
 * we will get many updates on the EDT but only the last one is important.
 * (Every next updates overrides the previous one.)
 * We might want to implement this {@code setText} in a way that only
 * the last update is delivered.
 * <p>
 * Here is how one can do this using {@code AccumulativeRunnable}:
 * <pre>
 * AccumulativeRunnable<String> doSetTextImpl = 
 * new  AccumulativeRunnable<String>() {
 *     @Override 
 *     protected void run(List&lt;String&gt; args) {
 *         //set to the last string being passed
 *         setTextImpl(args.get(args.size() - 1);
 *     }
 * }
 * void setText(String text) {
 *     //add text and send for the execution if needed.
 *     doSetTextImpl.add(text);
 * }
 * </pre>
 *
 * <p>
 * Say we want want to implement addDirtyRegion(Rectangle rect)
 * which sends this region to the 
 * handleDirtyRegions(List<Rect> regions) on the EDT.
 * addDirtyRegions better be accumulated before handling on the EDT.
 * 
 * <p>
 * Here is how it can be implemented using AccumulativeRunnable:
 * <pre>
 * AccumulativeRunnable<Rectangle> doHandleDirtyRegions = 
 *     new AccumulativeRunnable<Rectangle>() {
 *         @Override 
 *         protected void run(List&lt;Rectangle&gt; args) {
 *             handleDirtyRegions(args);
 *         }
 *     };
 *  void addDirtyRegion(Rectangle rect) {
 *      doHandleDirtyRegions.add(rect);
 *  }
 * </pre>
 *
 * @author Igor Kushnirskiy
 * @version $Revision: 1.3 $ $Date: 2008/07/25 19:32:29 $
 *
 * @param <T> the type this {@code Runnable} accumulates
 * 
 */
abstract class AccumulativeRunnable<T> implements Runnable {
    private List<T> arguments = null;
    
    /**
     * Equivalent to {@code Runnable.run} method with the
     * accumulated arguments to process.
     *
     * @param args accumulated arguments to process.
     */
    protected abstract void run(List<T> args);
    
    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation calls {@code run(List<T> args)} method
     * with the list of accumulated arguments.
     */
    public final void run() {
        run(flush());
    }
    
    /**
     * prepends or appends arguments and sends this {@code Runnable} for the 
     * execution if needed.
     * <p>
     * This implementation uses {@see #submit} to send this 
     * {@code Runnable} for execution.
     * @param isPrepend prepend or append 
     * @param args the arguments to add  
     */
    public final synchronized void add(boolean isPrepend, T... args) {
        boolean isSubmitted = true;
        if (arguments == null) {
            isSubmitted = false;
            arguments = new ArrayList<T>();
        }
        if (isPrepend) { 
            arguments.addAll(0, Arrays.asList(args)); 
        } else { 
            Collections.addAll(arguments, args); 
        } 
        if (!isSubmitted) {
            submit();
        }
    }

    /**
     * appends arguments and sends this {@code Runnable} for the
     * execution if needed.
     * <p>
     * This implementation uses {@see #submit} to send this 
     * {@code Runnable} for execution. 
     * @param args the arguments to accumulate
     */
    public final void add(T... args) {
        add(false, args);
    }
    
    /**
     * Sends this {@code Runnable} for the execution
     *
     * <p>
     * This method is to be executed only from {@code add} method.
     *
     * <p>
     * This implementation uses {@code SwingWorker.invokeLater}.
     */
    protected void submit() {
        SwingUtilities.invokeLater(this);
    }
        
    /**
     * Returns accumulated arguments and flashes the arguments storage.
     *
     * @return accumulated arguments
     */
    private final synchronized List<T> flush() {
        List<T> list = arguments;
        arguments = null;
        return list;
    }
}

