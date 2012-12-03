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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
  private static final String BUNDLE_NAME = "pt.iflow.applet.applet"; //$NON-NLS-1$

  private static ResourceBundle RESOURCE_BUNDLE;

  static {
    reset();
  }
  
  private Messages() {
  }
  
  public static void reset() {
    RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
  }

  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  /**
   * MessageFormat takes a set of objects, formats them, then returns the formatted strings.
   * 
   * @param asMessage
   *          The Message Name.
   * @param asAttributes
   *          The attributes.
   * @return The formatted strings.
   */
  public static String getString(String asMessage, Object... asAttributes) {
    String sMessage = null;

    if (asMessage != null) {
      if (asAttributes != null) {
        return MessageFormat.format(getString(asMessage), asAttributes);
      } else {
        return asMessage;
      }
    }

    return sMessage;
  }
}
