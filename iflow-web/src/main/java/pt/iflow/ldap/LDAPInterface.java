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
package pt.iflow.ldap;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.utils.Logger;
import pt.iknow.utils.ldap.LDAPDirectory;


public class LDAPInterface {

  private static LDAPDirectory _directory = null;
  
  private static String _server;
  private static String _searchBase;
  private static String _bindDn;
  private static String _bindPasswd;

  public static void init(Properties parameters) {
    _server = parameters.getProperty("SERVER_NAME");
    _searchBase = parameters.getProperty("SEARCH_BASE");
    _bindDn = parameters.getProperty("BIND_DN");
    _bindPasswd = parameters.getProperty("BIND_PASSWD");
    Logger.debug(null,"LDAPInterface","init","Server = " + _server + " Base = " + _searchBase+" Bind DN = "+_bindDn);
  }
  
  public static LDAPDirectory getDirectory() {
    if(_directory == null) {
      // get directory interface
      _directory = new LDAPDirectory(_server, _searchBase, _bindDn, _bindPasswd);
      Logger.debug(null,"LDAPInterface","getDirectory","Server = " + _server + " Base = " + _searchBase+" Bind DN = "+_bindDn);
    }
    
    return _directory;
  }
  
  public static String getDistinguishedName(String filter) {
    return getDN(filter);
  }
  
  public static String getDN(String filter) {
    Collection<Map<String,String>> users = searchDeep(filter);
    String result=null;
    if (users.isEmpty() || users.size() == 0 || users.size() > 1) {
      Logger.debug(null,"LDAPInterface","getDN","EMPTY USER LIST");
    }
    else {
      Map<String,String> user =  users.iterator().next();
      result = (String) user.get(LDAPDirectory.DN);
    }
    return result;
  }
  
  
  public static Map<String,String> getByDN(String dn) {
    Logger.debug(null,"LDAPInterface","searchDN","Performing LDAP search by dn " + dn);
    return getDirectory().getByDNPrefix(dn);
  }
  
  public static Collection<Map<String,String>> searchDeep(String filter) {
	  return searchDeep(null, filter);
  }
  
  public static Collection<Map<String,String>> searchDeep(String startFrom, String filter) {
    Logger.debug(null,"LDAPInterface","searchDeep","Performing LDAP search starting from " + startFrom + ": " + filter);
    return getDirectory().search(startFrom, filter, true);
  }
  
  public static Collection<Map<String,String>> searchFlat(String filter) {
	  return searchFlat(null, filter);
  }
  
  public static Collection<Map<String,String>> searchFlat(String startFrom, String filter) {
    Logger.debug(null,"LDAPInterface","searchFlat","Performing LDAP search starting from " + startFrom + ": " + filter);
    return getDirectory().search(startFrom, filter, false);
  }
  
  public static boolean checkBindPassword(String bindDn, String password) {
    return getDirectory().checkBindPassword(bindDn, password);
  }
  
  public static void disconnect() {
    if(_directory != null) {
      _directory = null;
    }
  }
  
  public static String getBaseDN() {
    return _searchBase;
  }
  
}
