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

import java.net.MalformedURLException;
import java.net.URL;

import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsIScriptError;

import pt.iknow.floweditor.FlowEditor;
import pt.iknow.utils.StringUtilities;

public class JavaScriptConsole implements IJavaScriptConsole {
  // helper to display the source in an editor
  protected int mode = SHOW_ALL;

  /*
   * IJavaScriptConsole Interface
   */
  public void logConsoleMessage(String message) {
    if (mode == SHOW_ALL || mode == SHOW_MESSAGES)
      FlowEditor.log("Message => " + message);
  }

  public void logConsoleMessage(nsIConsoleMessage message) {
    String msg = getMsg(message);
    if (StringUtilities.isNotEmpty(msg))
      FlowEditor.log(msg);
  }

  private String getMsg(nsIConsoleMessage message) {
    // test if it is an nsIScriptError type
    String cat = "Message => ";
    try {
      boolean show = true;

      nsIScriptError scriptError = (nsIScriptError) message.queryInterface(nsIScriptError.NS_ISCRIPTERROR_IID);
      long flags = scriptError.getFlags();
      switch (mode) {
      case IJavaScriptConsole.SHOW_ERRORS:
        show = (flags == nsIScriptError.errorFlag || flags == nsIScriptError.exceptionFlag);
        break;
      case IJavaScriptConsole.SHOW_WARNINGS:
        show = flags == nsIScriptError.warningFlag;
        break;
      case IJavaScriptConsole.SHOW_MESSAGES:
        show = scriptError.getFlags() == nsIScriptError.warningFlag;
        break;
      case IJavaScriptConsole.SHOW_ALL:
      default:
        show = true;
        break;
      }

      if (flags == nsIScriptError.errorFlag || flags == nsIScriptError.exceptionFlag)
        cat = "Error => ";
      else if (flags == nsIScriptError.warningFlag)
        cat = "Warning => ";
      //else
      //  cat = "Message => ";

      if (!show)
        return null;

      String category = scriptError.getCategory();
      StringBuilder sb = new StringBuilder(cat);
      if (category.indexOf("CSS") != -1) {
        sb.append("CSS: ");
      } else if (category.indexOf("javascript") != -1) {
        sb.append("JS: ");
      } else if (category.indexOf("XML") != -1 || category.indexOf("malformed-xml") != -1) {
        sb.append("XML: ");
      } else {
        sb.append("JS: ");
      }

      sb.append(scriptError.getErrorMessage() + ":" + scriptError.getSourceLine());
      String source = scriptError.getSourceName();
      try {
        URL ulr = new URL(source);
        source = ulr.getPath(); // remove host and query part
      } catch (MalformedURLException e) {
      } catch (NullPointerException e) {
      }

      sb.append("\t").append(source);
      sb.append("\t").append(String.valueOf(scriptError.getLineNumber()) + ':' + String.valueOf(scriptError.getColumnNumber()));
      return sb.toString();
    } catch (Exception e) {
      // nothing , the message is not an nsIScriptError type
    }

    return cat+message.getMessage();
  }

  public void setShowMode(int mode) {
    this.mode = mode;
  }
}
