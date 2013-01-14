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

import org.mozilla.interfaces.nsIConsoleListener;
import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;

public class JavaScriptConsoleListener implements nsIConsoleListener {

	protected IJavaScriptConsole jsConsole = null;
	
	public JavaScriptConsoleListener( IJavaScriptConsole jsConsole) {
		this.jsConsole = jsConsole;
	}

	public void observe(final nsIConsoleMessage consoleMessage) {
		//ignore if jsConsole is not available
		if( jsConsole == null )
			return;
		
		jsConsole.logConsoleMessage( consoleMessage );
	}

	public nsISupports queryInterface(String id) {
		return Mozilla.queryInterface(this, id);
	}

}
