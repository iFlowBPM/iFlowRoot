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
 *
 * Created on Feb 24, 2006 by mach
 *
 */

package pt.iknow.floweditor.messages;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

public class Messages {
  private static final String BUNDLE_NAME = "pt.iknow.floweditor.messages.messages"; //$NON-NLS-1$

  private static ResourceBundle RESOURCE_BUNDLE;
  
  static {
    reload();
  }

  private Messages() {
  }

  /**
   * Replaces {n} values by passed attributes
   * @param asMessage
   * @param asAttributes
   * @return parsed string
   */
  public static String getString(String asMessage, String[] asAttributes) {
    String sMessage = null;

    if (asMessage != null) {
      if (asAttributes != null) {     
        return MessageFormat.format(Messages.getString(asMessage), (Object[]) asAttributes);
      }
      else {
        return asMessage;
      }
    }
        
    return sMessage;  
  }
  
  /**
   * Convenience method for single attribute
   * @param asMessage
   * @param asAttribute
   * @return parsed string
   */
  public static String getString(String asMessage, String asAttribute) {
    
    String[] saTmp = { asAttribute };
    return getString(asMessage, saTmp);

  }
  
  /**
   * Convenience method for double attribute
   * @param asMessage
   * @param asAttribute0
   * @param asAttribute1
   * @return parsed string
   */
  public static String getString(String asMessage, String asAttribute0, String asAttribute1 ) {
    
    String[] saTmp = { asAttribute0, asAttribute1};
    return getString(asMessage, saTmp);

  }
  
  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
  
  
  public static void reload() {
    RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
  }
  
  public static Enumeration<String> getKeys() {
    return RESOURCE_BUNDLE.getKeys();
  }
  
  public static Enumeration<String> getSubKeys(String prefix) {
    Enumeration<String> keys = RESOURCE_BUNDLE.getKeys();
    if(null==prefix) return keys;
    Set<String> subKeys = new TreeSet<String>();
    while(keys.hasMoreElements()) {
      String key = keys.nextElement();
      if(key.startsWith(prefix)) subKeys.add(key);
    }
    return Collections.enumeration(subKeys);
  }
}
