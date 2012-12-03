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
