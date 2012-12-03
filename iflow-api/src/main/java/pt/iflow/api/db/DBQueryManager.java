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
package pt.iflow.api.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;

public class DBQueryManager {

  private static Properties _queries = new Properties();
  private static String _queriesFileLocation;
  private static long _lastModification = 0;

  static {
    _queriesFileLocation = System.getProperty("iflow.home") + File.separator + "dbqueries";
    String dbType = Const.DB_TYPE;
    _queriesFileLocation = _queriesFileLocation + File.separator + "queries_" + dbType + ".properties";
    File queriesFile = new File(_queriesFileLocation);
    _lastModification = queriesFile.lastModified();
    reloadQueries();
  }

  private static void reloadQueries() {
    Logger.debug("", "DBQueryManager", "", "LOADING QUERIES FILE " + _queriesFileLocation);
    try {
      _queries.load(new FileInputStream(_queriesFileLocation));
      Logger.adminInfo("DBQueryManager", "reloadQueries", "queries reloaded");
    } catch (IOException e) {
      Logger.adminError("DBQueryManager", "reloadQueries", "Error reading queries.", e);
    }
  }

  private static void checkFile() {
    File queriesFile = new File(_queriesFileLocation);
    if(_lastModification < queriesFile.lastModified()) {
      _lastModification = queriesFile.lastModified();
      reloadQueries();
    }
  }

  public static String getQuery(String queryName){
    checkFile();
    Logger.adminDebug("DBQueryManager","getQuery", "RETURNING QUERY = " + _queries.getProperty(queryName));
    return _queries.getProperty(queryName);
  }

  public static String processQuery(String queryName) {
    return processQuery(queryName,new Object[]{});
  }

  public static String processQuery(String queryName, Object ... arguments) {
    checkFile();
    String query = _queries.getProperty(queryName);
    if(query != null && query.trim().length() > 0) {
      query = MessageFormat.format(query, arguments);
    }
    if(Logger.isAdminDebugEnabled()) {
      Logger.adminDebug("DBQueryManager", "processQuery", "RETURNING QUERY = " + query);
    }
    return query;
  }
  
  public static String toQueryValue(Object obj) {
    if(obj == null) return "null";
    if(obj instanceof Number) {
      return obj.toString();
    }
    if(obj instanceof java.util.Date) {
      return processQuery("SQL.DATETIME", obj);
    }
    return "'"+StringEscapeUtils.escapeSql(obj.toString())+"'";
  }
  
}
