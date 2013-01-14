/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package pt.iknow.floweditor.mozilla;

import org.mozilla.interfaces.nsIConsoleMessage;

/*
 * Interfaces used to communicate with the console
 */
public interface IJavaScriptConsole {
	static final int SHOW_ALL 		= 0;
	static final int SHOW_ERRORS 	= 1;
	static final int SHOW_WARNINGS 	= 2;
	static final int SHOW_MESSAGES 	= 3;
	
	void logConsoleMessage( String message );
	void logConsoleMessage( nsIConsoleMessage message );
	void setShowMode( int mode );

}
